package com.kairos.activity.util.time_bank;

import com.google.common.collect.Lists;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.enums.TimeTypes;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBank;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.response.dto.time_bank.*;
import com.kairos.activity.persistence.model.time_bank.TimeBankDistribution;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Service
public class TimeBankCalculationService {


    /*
    * It is for SelfRostering Tab It calculate timebank for UpcomingDays
    * on the basis of currentCta
    * */
    public List<CalculatedTimeBankByDateDTO> getTimeBankByDates(TimebankWrapper ctaDto, List<ShiftQueryResultWithActivity> shifts, int days) {
        shifts = getFutureShifts();
        Map<String, List<ShiftQueryResultWithActivity>> shiftQueryResultMap = getMapOfShiftByInterval(shifts, 1);
        List<CalculatedTimeBankByDateDTO> calculatedTimeBankByDateDTOS = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            DateTime dateTime = new DateTime().withTimeAtStartOfDay().plusDays(i);
            Interval interval = new Interval(dateTime, dateTime.plusDays(1));
            List<ShiftQueryResultWithActivity> shiftQueryResults = shiftQueryResultMap.get(interval.toString());
            int totalTimeBank = getTimeBankByInterval(ctaDto, interval, shiftQueryResults, null).getTotalTimeBankMin();
            calculatedTimeBankByDateDTOS.add(new CalculatedTimeBankByDateDTO(interval.getStart().toLocalDate(), totalTimeBank));
        }
        return calculatedTimeBankByDateDTOS;
    }

    public DailyTimeBank getTimeBankByInterval(TimebankWrapper ctaDto, Interval interval, List<ShiftQueryResultWithActivity> shifts, DailyTimeBank dailyTimeBank) {
        if (shifts != null && !shifts.isEmpty()) {
            dailyTimeBank = calculateDailyTimebank(interval, ctaDto, shifts, dailyTimeBank);
        } else {
            int dailyContractualMinutes = interval.getStart().getDayOfWeek()<= ctaDto.getWorkingDaysPerWeek() ? -ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek():0;
            dailyTimeBank.setTotalTimeBankMin(dailyContractualMinutes!=0?-dailyContractualMinutes:0);
            dailyTimeBank.setContractualMin(dailyContractualMinutes);
            dailyTimeBank.setScheduledMin(0);
            dailyTimeBank.setTimeBankMinBeforeCta(0);
            dailyTimeBank.setTimeBankMinAfterCta(0);
            dailyTimeBank.setUnitWorkingDaysInWeek(ctaDto.getWorkingDaysPerWeek());
            dailyTimeBank.setStaffId(ctaDto.getStaffId());
            dailyTimeBank.setTotalContractualMinInWeek(ctaDto.getContractedMinByWeek());
            dailyTimeBank.setTimeBankDistributionList(getDistribution(ctaDto));
        }
        return dailyTimeBank;
    }


    public List<TimeBankDistribution> getDistribution(TimebankWrapper ctaDto) {
        List<TimeBankDistribution> timeBankDistributions = new ArrayList<>(ctaDto.getCtaRuleTemplates().size());
        ctaDto.getCtaRuleTemplates().forEach(rt -> {
            timeBankDistributions.add(new TimeBankDistribution(rt.getName(), 0, rt.getId()));
        });
        return timeBankDistributions;
    }
    //TODO complete review by Sachin and need Test cases
    public DailyTimeBank calculateDailyTimebank(Interval interval, TimebankWrapper ctaDto, List<ShiftQueryResultWithActivity> shifts, DailyTimeBank dailyTimeBank) {
        int totalDailyTimebank = 0;
        int dailyScheduledMin = 0;
        int timeBankMinBeforeCta = 0;
        int contractualMin = interval.getStart().getDayOfWeek()<=ctaDto.getWorkingDaysPerWeek() ?ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek():0;
        Map<Long, Integer> ctaTimeBankMinMap = new HashMap<>();
        for (ShiftQueryResultWithActivity shift : shifts) {
            Interval shiftInterval = new Interval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
            shiftInterval = interval.overlap(shiftInterval);
            dailyScheduledMin += shiftInterval.toDuration().getStandardMinutes();
            timeBankMinBeforeCta += shift.getActivity() != null ? (int) shiftInterval.toDuration().getStandardMinutes() : 0;
            totalDailyTimebank += shiftInterval.toDuration().getStandardMinutes();
            for (CTARuleTemplateDTO ruleTemplate : ctaDto.getCtaRuleTemplates()) {
                int ctaTimeBankMin = 0;
                if ((ruleTemplate.getActivityIds().contains(shift.getActivity().getId()) || (ruleTemplate.getTimeTypeId()!=null && ruleTemplate.getTimeTypeId().equals(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))) && ((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(DateUtils.toLocalDate(shiftInterval.getStart()))))) {
                    for (CTAIntervalDTO ctaIntervalDTO : ruleTemplate.getCtaIntervalDTOS()) {
                        int ctaStart = ctaIntervalDTO.getEndTime()<ctaIntervalDTO.getStartTime()?0:ctaIntervalDTO.getStartTime();
                        int ctaEnd = ctaIntervalDTO.getEndTime()==0?1440:ctaIntervalDTO.getEndTime();
                        Interval ctaInterval = new Interval(interval.getStart().withTimeAtStartOfDay().plusMinutes(ctaStart),interval.getStart().plusMinutes(ctaEnd));
                        if (ctaInterval.overlaps(shiftInterval)) {
                            int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).toDuration().getStandardMinutes();
                            if (ctaIntervalDTO.getCompensationType().equals(AppConstants.MINUTES)) {
                                ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * ctaIntervalDTO.getCompensationValue();
                                totalDailyTimebank += ctaTimeBankMin;
                                break;
                            } else if (ctaIntervalDTO.getCompensationType().equals(AppConstants.PERCENTAGE)) {
                                ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * (60 / 100) * ctaIntervalDTO.getCompensationValue();
                                totalDailyTimebank += ctaTimeBankMin;
                                break;
                            }

                        }
                    }
                }
                ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId()) ? ctaTimeBankMinMap.get(ruleTemplate.getId()) + ctaTimeBankMin : ctaTimeBankMin);
            }
        }
        totalDailyTimebank = interval.getStart().getDayOfWeek()<=ctaDto.getWorkingDaysPerWeek() ? totalDailyTimebank - contractualMin:totalDailyTimebank;
        timeBankMinBeforeCta = interval.getStart().getDayOfWeek()<=ctaDto.getWorkingDaysPerWeek() ? timeBankMinBeforeCta - contractualMin:totalDailyTimebank;
        dailyTimeBank.setUnitWorkingDaysInWeek(ctaDto.getWorkingDaysPerWeek());
        dailyTimeBank.setStaffId(ctaDto.getStaffId());
        dailyTimeBank.setTotalContractualMinInWeek(ctaDto.getContractedMinByWeek());
        dailyTimeBank.setTimeBankMinBeforeCta(timeBankMinBeforeCta);
        dailyTimeBank.setTimeBankMinAfterCta(totalDailyTimebank - timeBankMinBeforeCta);
        dailyTimeBank.setContractualMin(contractualMin);
        dailyTimeBank.setScheduledMin(dailyScheduledMin);
        dailyTimeBank.setTotalTimeBankMin(totalDailyTimebank);
        dailyTimeBank.setTimeBankDistributionList(getBlankTimeBankDistribution(ctaDto.getCtaRuleTemplates(), ctaTimeBankMinMap));
        return dailyTimeBank;
    }

    private List<TimeBankDistribution> getBlankTimeBankDistribution(List<CTARuleTemplateDTO> ctaRuleTemplateDTOS, Map<Long, Integer> ctaTimeBankMinMap) {
        List<TimeBankDistribution> timeBankDistributions = new ArrayList<>(ctaRuleTemplateDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateDTOS) {
            timeBankDistributions.add(new TimeBankDistribution(ruleTemplate.getName(), ctaTimeBankMinMap.get(ruleTemplate.getId()), ruleTemplate.getId()));
        }
        return timeBankDistributions;
    }

    public Map<String, List<ShiftQueryResultWithActivity>> getMapOfShiftByInterval(List<ShiftQueryResultWithActivity> shifts, int intervalValue) {
        Map<String, List<ShiftQueryResultWithActivity>> shiftQueryResultMap = new HashMap<>();
        for (int i = 0; i < shifts.size() - 1; i++) {
            DateTime startDateTime = new DateTime().plusDays(i).withTimeAtStartOfDay();
            Interval interval = new Interval(startDateTime, startDateTime.plusDays(intervalValue));
            shiftQueryResultMap.put(interval.toString(), getShiftsByDate(interval, shifts));
        }
        return shiftQueryResultMap;
    }


    private List<ShiftQueryResultWithActivity> getShiftsByDate(Interval interval, List<ShiftQueryResultWithActivity> shifts) {
        List<ShiftQueryResultWithActivity> shifts1 = new ArrayList<>();
        shifts.forEach(s -> {
            if (interval.contains(s.getStartDate().getTime()) || interval.contains(s.getEndDate().getTime())) {
                shifts1.add(s);
            }
        });
        return shifts1;
    }

    private List<ShiftQueryResultWithActivity> getFutureShifts() {
        List<ShiftQueryResultWithActivity> shifts = new ArrayList<>(30);
        IntStream.range(0, 29).forEachOrdered(i -> {
            ShiftQueryResultWithActivity shift = new ShiftQueryResultWithActivity();
            shift.setId(new BigInteger("" + i));
            shift.setActivityId(new BigInteger("123"));
            shift.setStartDate(new DateTime().plusDays(i).toDate());
            shift.setEndDate(new DateTime().plusDays(i).plusHours(9).toDate());
            shifts.add(shift);
        });
        return shifts;
    }

    public TimeBankDTO getAdvanceViewTimeBank(int totalTimeBankBeforeStartDate, Date startDate, Date endDate, String query, List<ShiftQueryResultWithActivity> shifts, List<DailyTimeBank> dailyTimeBanks, TimebankWrapper timebankWrapper, List<TimeTypeDTO> timeTypeDTOS) {
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setCostTimeAgreement(timebankWrapper);
        timeBankDTO.setStartDate(startDate);
        timeBankDTO.setEndDate(endDate);
        List<Interval> intervals = getAllIntervalsBetweenDates(startDate, endDate, query);
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        Map<Interval, List<DailyTimeBank>> timeBanksIntervalMap = getTimebankIntervalsMap(intervals, dailyTimeBanks);
        Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap = getShiftsIntervalMap(intervals, shifts);
        timeBankDTO.setStaffId(timebankWrapper.getStaffId());//dailyTimeBanks.get(0).getStaffId());
        timeBankDTO.setWorkingDaysInWeek(timebankWrapper.getWorkingDaysPerWeek());//dailyTimeBanks.get(0).getUnitWorkingDaysInWeek());
        timeBankDTO.setUnitPositionId(timebankWrapper.getUnitPositionId());
        timeBankDTO.setTotalWeeklyMin(timebankWrapper.getContractedMinByWeek());
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = getTimeIntervals(totalTimeBankBeforeStartDate,query, intervals, shiftsintervalMap, timeBanksIntervalMap, timeTypeDTOS, timebankWrapper);
        timeBankDTO.setTimeIntervals(timeBankIntervalDTOS);
        List<TimeBankDistributionDTO> timeBankDistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistributions().stream()).collect(Collectors.toList());
        timeBankDistributions = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getId(), Collectors.summingInt(tb -> tb.getMinutes()))).entrySet().stream().map(t -> new TimeBankDistributionDTO(t.getKey(), t.getValue())).collect(Collectors.toList());
        timeBankDistributions = getDistributionOfTimeBank(timeBankDistributions, timebankWrapper);
        timeBankDTO.setTimeBankDistributions(timeBankDistributions);
        timeBankDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
        timeBankDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
        if (dailyTimeBanks != null && !dailyTimeBanks.isEmpty()) {
            timeBankDTO.getCostTimeAgreement().setMinutesFromCta(dailyTimeBanks.stream().mapToInt(t->t.getTimeBankMinAfterCta()).sum());
            timeBankDTO.setTotalContractedMin(dailyTimeBanks.stream().mapToInt(ti -> ti.getContractualMin()).sum());
            int totalTimeBank = dailyTimeBanks.stream().mapToInt(ti -> ti.getTotalTimeBankMin()).sum();
            timeBankDTO.setTotalTimeBankAfterCtaMin(totalTimeBank+totalTimeBankBeforeStartDate);
            timeBankDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBeforeStartDate);
            timeBankDTO.setTotalScheduledMin(dailyTimeBanks.stream().mapToInt(ti -> ti.getScheduledMin()).sum());
            timeBankDTO.setTotalTimeBankMin(totalTimeBank);
            timeBankDTO.setTotalTimeBankDiff(totalTimeBank);
            timeBankDTO.getCostTimeAgreement().setMinutesFromCta(dailyTimeBanks.stream().mapToInt(ti -> ti.getTimeBankMinAfterCta()).sum());

        }
        return timeBankDTO;
    }

    public TimeBankDTO getOverviewTimeBank(Long unitEmployementPositionId, DateTime startDate, DateTime lastDateTimeOfYear, List<DailyTimeBank> dailyTimeBanks) {
        /*Interval intervalOfLastDecemberByYear = new Interval(startDate, startDate.plusMonths(1));
        int timeBankOfLastDecember = getTotalTimeBanksByInterval(intervalOfLastDecemberByYear, dailyTimeBanks);
        startDate = startDate.plusMonths(1);
        Interval intervalOfLastWeekBeforeYear = new Interval(startDate.minusDays(1).withDayOfWeek(DateTimeConstants.MONDAY), startDate);
        int timeBanksOfLastWeekOfDecember = getTotalTimeBanksByInterval(intervalOfLastWeekBeforeYear, dailyTimeBanks);*/
        List<Interval> weeklyIntervals = getWeeklyIntervals(startDate, lastDateTimeOfYear);
        List<Interval> monthlyIntervals = getMonthlyIntervals(startDate, lastDateTimeOfYear);
        Map<Interval, List<DailyTimeBank>> weeklyIntervalTimeBankMap = getTimebankIntervalsMap(weeklyIntervals, dailyTimeBanks);
        Map<Interval, List<DailyTimeBank>> monthlyIntervalTimeBankMap = getTimebankIntervalsMap(monthlyIntervals, dailyTimeBanks);
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setUnitPositionId(unitEmployementPositionId);
        int contractualminutes = dailyTimeBanks.stream().mapToInt(t -> t.getContractualMin()).sum();
        int scheduledMinutes = dailyTimeBanks.stream().mapToInt(t -> t.getScheduledMin()).sum();
        int totalTimeBankminutes = dailyTimeBanks.stream().mapToInt(t -> t.getTotalTimeBankMin()).sum();
        int timebankMinutesAfterCta = dailyTimeBanks.stream().mapToInt(t->t.getTimeBankMinAfterCta()).sum();
        timeBankDTO.setTotalContractedMin(contractualminutes);
        timeBankDTO.setTotalScheduledMin(scheduledMinutes+timebankMinutesAfterCta);
        timeBankDTO.setTotalTimeBankMin(totalTimeBankminutes);
        //  int lastWeekTimeBankBeforeYear = timeBanksOfLastWeekOfDecember.stream()
        timeBankDTO.setWeeklyIntervalsTimeBank(getTimeBankByIntervals(weeklyIntervals, weeklyIntervalTimeBankMap, AppConstants.WEEKLY));
        timeBankDTO.setMonthlyIntervalsTimeBank(getTimeBankByIntervals(monthlyIntervals, monthlyIntervalTimeBankMap, AppConstants.MONTHLY));
        return timeBankDTO;
    }

    public List<TimeBankIntervalDTO> getTimeBankByIntervals(List<Interval> intervals, Map<Interval, List<DailyTimeBank>> timeBankIntervalMap, String basedUpon) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<DailyTimeBank> dailyTimeBanks = timeBankIntervalMap.get(interval);
            int weekCount = getWeekCount(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = basedUpon.equals(AppConstants.WEEKLY)
                    ? new TimeBankIntervalDTO(AppConstants.WEEK + " " + weekCount)
                    : new TimeBankIntervalDTO(Month.of(interval.getEnd().getMonthOfYear()).toString().toUpperCase());
            int totalTimeBank = 0;
            if (dailyTimeBanks != null && !dailyTimeBanks.isEmpty()) {
                totalTimeBank = dailyTimeBanks.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum();
                timeBankIntervalDTO.setTotalTimeBankMin(totalTimeBank);
                int scheduledMinutes = dailyTimeBanks.stream().mapToInt(t -> t.getScheduledMin()).sum();
                int timebankMinutesAfterCta = dailyTimeBanks.stream().mapToInt(t->t.getTimeBankMinAfterCta()).sum();
                timeBankIntervalDTO.setTotalTimeBankDiff(scheduledMinutes+timebankMinutesAfterCta);
               // timeBankIntervalDTO.setTotalTimeBankDiff(timeBankBefore<0?totalTimeBank+timeBankBefore:totalTimeBank-timeBankBefore);
            }
            //timeBankBefore = totalTimeBank;
            timeBankIntervalDTOS.add(timeBankIntervalDTO);
        }
        return timeBankIntervalDTOS;
    }

    //This method because of weekOfTheWeek function depends how many day in current Week
    private int getWeekCount(Interval interval){
        if(interval.getEnd().getWeekOfWeekyear()==1 && interval.getEnd().getMonthOfYear()==12){
            return interval.getStart().minusDays(1).getWeekOfWeekyear()+1;
        }else {
            return interval.getEnd().getWeekOfWeekyear();
        }
    }

    public List<Interval> getMonthlyIntervals(DateTime startDate, DateTime lastDateTimeOfYear) {
        List<Interval> intervals = new ArrayList<>(12);
        DateTime endDate = startDate.dayOfMonth().withMaximumValue();
        while (true) {
            intervals.add(new Interval(startDate, endDate));
            startDate = endDate.plusDays(1);
            endDate = startDate.dayOfMonth().withMaximumValue();
            if (endDate.equals(lastDateTimeOfYear)) {
                intervals.add(new Interval(startDate, lastDateTimeOfYear));
                break;
            }
        }
        return intervals;
    }


    public List<Interval> getWeeklyIntervals(DateTime startDate, DateTime lastDateTimeOfYear) {
        List<Interval> intervals = new ArrayList<>(60);
        DateTime endDate = startDate.getDayOfWeek()==7?startDate.plusWeeks(1):startDate.withDayOfWeek(DateTimeConstants.SUNDAY);
        while (true) {
            if (endDate.getYear() != startDate.getYear()) {
                intervals.add(new Interval(startDate, lastDateTimeOfYear));
                break;
            }
            intervals.add(new Interval(startDate, endDate));
            startDate = endDate;
            if (lastDateTimeOfYear.equals(endDate)) {
                break;
            }
            endDate = startDate.plusWeeks(1);
        }
        return intervals;
    }


    public List<TimeBankIntervalDTO> getTimeIntervals(int totalTimeBankBefore, String query, List<Interval> intervals, Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap, Map<Interval, List<DailyTimeBank>> timeBanksIntervalMap, List<TimeTypeDTO> timeTypeDTOS, TimebankWrapper timebankWrapper) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<ShiftQueryResultWithActivity> shifts = shiftsintervalMap.get(interval);
            List<DailyTimeBank> dailyTimeBanks = timeBanksIntervalMap.get(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(interval.getStart().toDate(), interval.getEnd().toDate());
            if (dailyTimeBanks != null && !dailyTimeBanks.isEmpty()) {
                timeBankIntervalDTO.setTitle(getTitle(query, interval));
                int totalTimeBank = dailyTimeBanks.stream().mapToInt(ti -> ti.getTotalTimeBankMin()).sum();
                timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore+totalTimeBank);
                timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore);
                timeBankIntervalDTO.setTotalTimeBankMin(totalTimeBank);
                timeBankIntervalDTO.setTotalTimeBankDiff(totalTimeBank);
                totalTimeBankBefore+=totalTimeBank;
                /*timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(dailyTimeBanks.stream().filter(tb -> interval.contains(DateUtils.asDate(tb.getDate()).getTime())).mapToInt(tb -> tb.getTimeBankMinBeforeCta()).sum());*/
                timeBankIntervalDTO.setTotalScheduledMin(dailyTimeBanks.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).mapToInt(tb -> tb.getScheduledMin()).sum());
                timeBankIntervalDTO.setTotalContractualMin(dailyTimeBanks.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).mapToInt(tb -> tb.getContractualMin()).sum());
                /*timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(dailyTimeBanks.stream().filter(tb -> interval.contains(DateUtils.asDate(tb.getDate()).getTime())).mapToInt(tb -> tb.getTimeBankMinAfterCta()).sum());*/
                List<TimeBankDistribution> timeBankDistributions = dailyTimeBanks.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankDistributionList().stream()).collect(Collectors.toList());
                List<TimeBankDistributionDTO> timeBankDistributionDTOS = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaRuleTemplateId(), Collectors.summingInt(tb -> tb.getMinutes()))).entrySet().stream().map(t -> new TimeBankDistributionDTO(t.getKey(), t.getValue())).collect(Collectors.toList());
                List<TimeBankDistributionDTO> timeBankDistributionsDto = getDistributionOfTimeBank(timeBankDistributionDTOS, timebankWrapper);
                timeBankIntervalDTO.setTimeBankDistributions(timeBankDistributionsDto);
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                //timeBankIntervalDTO.setMinutesFromCta(21);
                timeBankIntervalDTOS.add(timeBankIntervalDTO);

            } else {
                timeBankIntervalDTO.setTotalTimeBankDiff(totalTimeBankBefore);
                timeBankIntervalDTO.setTitle(getTitle(query, interval));
                timeBankIntervalDTO.setTimeBankDistributions(getBlankTimeBankDistribution(timebankWrapper));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                //timeBankIntervalDTO.setMinutesFromCta(21);
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            }
        }
        return Lists.reverse(timeBankIntervalDTOS);
    }

    public List<TimeBankDistributionDTO> getDistributionOfTimeBank(List<TimeBankDistributionDTO> timeBankDistributionDTOS, TimebankWrapper timebankWrapper) {
        timeBankDistributionDTOS.forEach(timeBankDistributionDTO -> {
            timebankWrapper.getCtaRuleTemplates().forEach(cta -> {
                if (timeBankDistributionDTO.getId().equals(cta.getId())) {
                    timeBankDistributionDTO.setName(cta.getName());
                }
            });
        });
        return timeBankDistributionDTOS;
    }

    public String getTitle(String query, Interval interval) {
        switch (query) {
            case AppConstants.DAILY:
                return interval.getStart().toLocalDate().toString();
            case AppConstants.WEEKLY:
                return StringUtils.capitalize(AppConstants.WEEKLY)+" " + interval.getStart().getWeekOfWeekyear();
            case AppConstants.MONTHLY:
                return interval.getStart().monthOfYear().getAsText();
            case AppConstants.ANNUALLY:
                return StringUtils.capitalize(AppConstants.YEAR)+" "+ interval.getStart().getYear();
            case AppConstants.QUATERLY:
                return StringUtils.capitalize(AppConstants.QUARTER)+" "+(interval.getStart().dayOfMonth().withMinimumValue().equals(interval.getStart())?interval.getStart().getMonthOfYear()/3:(interval.getStart().getMonthOfYear()/3)+1);
            //case "ByPeriod": return getActualTimeBankByPeriod(startDate,endDate,shifts);
        }
        return "";
    }

    public ScheduleTimeByTimeTypeDTO getWorkingTimeType(Interval interval, List<ShiftQueryResultWithActivity> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
        List<ScheduleTimeByTimeTypeDTO> parentTimeTypes = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if (timeType.getTimeTypes().equals(TimeTypes.WORKING_TYPE.toValue()) && timeType.getUpperLevelTimeTypeId() == null) {
                ScheduleTimeByTimeTypeDTO parentTimeType = new ScheduleTimeByTimeTypeDTO(0);
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                parentTimeType.setChildren(children);
                parentTimeType.setLabel(timeType.getLabel());
                parentTimeType.setTimeTypeId(timeType.getId());
                if (!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                }
                if (shifts != null && !shifts.isEmpty()) {
                    for (ShiftQueryResultWithActivity shift : shifts) {
                        if (timeType.getId().equals(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.overlaps(shift.getInterval())) {
                            totalScheduledMin += interval.overlap(shift.getInterval()).toDuration().getStandardMinutes();
                        }
                    }
                }
                parentTimeType.setTotalMin(totalScheduledMin);
                parentTimeType.setTotalMin(children.stream().mapToInt(c -> c.getTotalMin()).sum());
                parentTimeTypes.add(parentTimeType);
            }
        });
        scheduleTimeByTimeTypeDTO.setChildren(parentTimeTypes);
        return scheduleTimeByTimeTypeDTO;
    }

    public ScheduleTimeByTimeTypeDTO getNonWorkingTimeType(Interval interval, List<ShiftQueryResultWithActivity> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
        List<ScheduleTimeByTimeTypeDTO> parentTimeTypes = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if (timeType.getTimeTypes().equals(TimeTypes.NON_WORKING_TYPE.toValue()) && timeType.getUpperLevelTimeTypeId() == null) {
                ScheduleTimeByTimeTypeDTO parentTimeType = new ScheduleTimeByTimeTypeDTO(0);
                parentTimeType.setTimeTypeId(timeType.getId());
                parentTimeType.setLabel(timeType.getLabel());
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                parentTimeType.setChildren(children);
                if (!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                }
                if (shifts != null && !shifts.isEmpty()) {
                    for (ShiftQueryResultWithActivity shift : shifts) {
                        if (timeType.getId().equals(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.overlaps(shift.getInterval())) {
                            totalScheduledMin += interval.overlap(shift.getInterval()).toDuration().getStandardMinutes();
                        }
                    }
                }
                parentTimeType.setTotalMin(totalScheduledMin);
                parentTimeTypes.add(parentTimeType);
            }
        });
        scheduleTimeByTimeTypeDTO.setTotalMin(parentTimeTypes.stream().mapToInt(ptt -> ptt.getTotalMin()).sum());
        scheduleTimeByTimeTypeDTO.setChildren(parentTimeTypes);
        return scheduleTimeByTimeTypeDTO;
    }


    public List<ScheduleTimeByTimeTypeDTO> getTimeTypeDTOS(BigInteger timeTypeId, Interval interval, List<ShiftQueryResultWithActivity> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        List<ScheduleTimeByTimeTypeDTO> scheduleTimeByTimeTypeDTOS = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if (timeType.getUpperLevelTimeTypeId() != null && timeType.getUpperLevelTimeTypeId().equals(timeTypeId)) {
                ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
                scheduleTimeByTimeTypeDTO.setTimeTypeId(timeType.getId());
                scheduleTimeByTimeTypeDTO.setLabel(timeType.getLabel());
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                scheduleTimeByTimeTypeDTO.setChildren(children);
                if (!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                }

                if (shifts != null && !shifts.isEmpty()) {
                    for (ShiftQueryResultWithActivity shift : shifts) {
                        if (timeType.getId().equals(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.overlaps(shift.getInterval())) {
                            totalScheduledMin += interval.overlap(shift.getInterval()).toDuration().getStandardMinutes();
                        }
                    }
                    scheduleTimeByTimeTypeDTO.setTotalMin(totalScheduledMin);
                }
                scheduleTimeByTimeTypeDTOS.add(scheduleTimeByTimeTypeDTO);
            }
        });
        return scheduleTimeByTimeTypeDTOS;
    }


    public List<TimeTypeIntervalDTO> getTimeTypeInterval(Interval interval, List<ShiftQueryResultWithActivity> shifts) {
        List<TimeTypeIntervalDTO> timeTypeIntervalDTOS = new ArrayList<>();

        return timeTypeIntervalDTOS;
    }

    private List<TimeBankIntervalDTO> calculateTimeBankByInterval(Map<Interval, List<DailyTimeBank>> timeBanksIntervalMap, List<Interval> intervals) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        intervals.forEach(i -> {
            List<DailyTimeBank> dailyTimeBanks = timeBanksIntervalMap.get(i);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(i.getStart().toDate(), i.getEnd().toDate());
            timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(0);//dailyTimeBanks.stream().mapToInt(t->t.getTimeBankMinAfterCta()).sum());
            timeBankIntervalDTO.setTotalContractualMin(0);//dailyTimeBanks.stream().mapToInt(t->t.getContractualMin()).sum());
            timeBankIntervalDTO.setTotalScheduledMin(0);//dailyTimeBanks.stream().mapToInt(t->t.getScheduledMin()).sum());
            timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(0);//dailyTimeBanks.stream().mapToInt(t->t.getTimeBankMinBeforeCta()).sum());
            timeBankIntervalDTO.setTotalTimeBankMin(0);//dailyTimeBanks.stream().mapToInt(t->t.getTotalTimeBankMin()).sum());
            //timeBankIntervalDTO.setTimeBankDistributions(getBlankTimeBankDistribution(dailyTimeBanks, null));
            List<TimeBankDistribution> timeBankDistributions = dailyTimeBanks.stream().flatMap(tb -> tb.getTimeBankDistributionList().stream()).collect(Collectors.toList());
            timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaRuleTemplateId(), Collectors.summarizingInt(tb -> tb.getMinutes())));
            timeBankIntervalDTOS.add(timeBankIntervalDTO);
        });

        return timeBankIntervalDTOS;
    }

    private List<TimeBankDistributionDTO> getBlankTimeBankDistribution(TimebankWrapper timebankWrapper) {
        List<TimeBankDistributionDTO> timeBankDistributionDTOS = new ArrayList<>();
        timebankWrapper.getCtaRuleTemplates().forEach(cta -> {
            timeBankDistributionDTOS.add(new TimeBankDistributionDTO(cta.getId(), cta.getName(), 0));
        });
        return timeBankDistributionDTOS;
    }

    private Map<Interval, List<DailyTimeBank>> getTimebankIntervalsMap(List<Interval> intervals, List<DailyTimeBank> dailyTimeBanks) {
        Map<Interval, List<DailyTimeBank>> timeBanksIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> {
            timeBanksIntervalMap.put(i, getTimeBanksByInterval(i, dailyTimeBanks));
        });
        return timeBanksIntervalMap;
    }

    private List<DailyTimeBank> getTimeBanksByInterval(Interval interval, List<DailyTimeBank> dailyTimeBanks) {
        List<DailyTimeBank> dailyTimeBanks1 = new ArrayList<>();
        dailyTimeBanks.forEach(tb -> {
            if (interval.contains(DateUtils.asDate(tb.getDate()).getTime()) || interval.getStart().equals(new DateTime(DateUtils.asDate(tb.getDate())))) {
                dailyTimeBanks1.add(tb);
            }
        });
        return dailyTimeBanks1;
    }

    private int getTotalTimeBanksByInterval(Interval interval, List<DailyTimeBank> dailyTimeBanks) {
        int totalTimeBank = 0;
        totalTimeBank = dailyTimeBanks.stream().filter(dailyTimeBank -> (interval.contains(DateUtils.asDate(dailyTimeBank.getDate()).getTime()))).mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum();
        return totalTimeBank;
    }

    private Map<Interval, List<ShiftQueryResultWithActivity>> getShiftsIntervalMap(List<Interval> intervals, List<ShiftQueryResultWithActivity> shifts) {
        Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> {
            shiftsintervalMap.put(i, getShiftsByDate(i, shifts));
        });
        return shiftsintervalMap;
    }


    public List<Interval> getAllIntervalsBetweenDates(Date startDate, Date endDate, String field) {
        DateTime startDateTime = new DateTime(startDate);
        DateTime endDateTime = new DateTime(endDate);
        List<Interval> intervals = new ArrayList<>();
        DateTime nextEndDay = startDateTime;
        while (nextEndDay.isBefore(endDateTime)) {
            switch (field) {
                case AppConstants.DAILY:
                    nextEndDay = startDateTime.plusDays(1);
                    break;
                case AppConstants.WEEKLY:
                    nextEndDay = startDateTime.getDayOfWeek()==7?startDateTime.plusWeeks(1):startDateTime.withDayOfWeek(DateTimeConstants.SUNDAY);
                    break;
                case AppConstants.MONTHLY:
                    nextEndDay = startDateTime.dayOfMonth().withMaximumValue().plusDays(1);
                    break;
                case AppConstants.ANNUALLY:
                    nextEndDay = startDateTime.dayOfYear().withMaximumValue().plusDays(1);
                    break;
                case AppConstants.QUATERLY:
                    nextEndDay = getQuaterByDate(startDateTime);
                    break;
                //case "ByPeriod": return getActualTimeBankByPeriod(startDate,endDate,shifts);
            }
            intervals.add(new Interval(startDateTime, nextEndDay));
            startDateTime = nextEndDay;
        }
        if(!startDateTime.equals(endDateTime) && startDateTime.isBefore(endDateTime)) {
            intervals.add(new Interval(startDateTime, endDateTime));
        }
        return intervals;
    }

    private DateTime getQuaterByDate(DateTime dateTime){
        int quater = (int)Math.ceil((double) dateTime.getMonthOfYear()/3);
        DateTime quaterDateTime = null;
        switch (quater){
            case 1:quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(3).dayOfMonth().withMaximumValue().plusDays(1);
                break;
            case 2:quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(6).dayOfMonth().withMaximumValue().plusDays(1);
                break;
            case 3:quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(9).dayOfMonth().withMaximumValue().plusDays(1);
                break;
            case 4:quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(12).dayOfMonth().withMaximumValue().plusDays(1);
                break;
        }
        return quaterDateTime;
    }


    private List<ShiftQueryResultWithActivity> getShifts() {
        List<ShiftQueryResultWithActivity> shifts = new ArrayList<>(3);
        ShiftQueryResultWithActivity shiftQueryResultWithActivity = new ShiftQueryResultWithActivity();
        shiftQueryResultWithActivity.setStartDate(new DateTime().withTimeAtStartOfDay().plusHours(7).toDate());
        shiftQueryResultWithActivity.setEndDate(new DateTime().withTimeAtStartOfDay().plusHours(18).toDate());
        shiftQueryResultWithActivity.setActivityId(new BigInteger("123"));
        shiftQueryResultWithActivity.setActivity(new Activity());
        //shiftQueryResultWithActivity.getActivity().setIncludeTimebank(true);
        shiftQueryResultWithActivity.getActivity().setBalanceSettingsActivityTab(new BalanceSettingsActivityTab());
        shifts.add(shiftQueryResultWithActivity);
        shiftQueryResultWithActivity = new ShiftQueryResultWithActivity();
        shiftQueryResultWithActivity.setStartDate(new DateTime().withTimeAtStartOfDay().plusHours(7).toDate());
        shiftQueryResultWithActivity.setEndDate(new DateTime().withTimeAtStartOfDay().plusHours(18).toDate());
        shiftQueryResultWithActivity.setActivityId(new BigInteger("123"));
        shiftQueryResultWithActivity.setActivity(new Activity());
        // shiftQueryResultWithActivity.getActivity().setIncludeTimebank(true);
        shiftQueryResultWithActivity.getActivity().setBalanceSettingsActivityTab(new BalanceSettingsActivityTab());
        shifts.add(shiftQueryResultWithActivity);
        shiftQueryResultWithActivity = new ShiftQueryResultWithActivity();
        shiftQueryResultWithActivity.setStartDate(new DateTime().withTimeAtStartOfDay().plusHours(18).toDate());
        shiftQueryResultWithActivity.setEndDate(new DateTime().withTimeAtStartOfDay().plusHours(27).toDate());
        shiftQueryResultWithActivity.setActivityId(new BigInteger("123"));
        shiftQueryResultWithActivity.setActivity(new Activity());
        shiftQueryResultWithActivity.getActivity().setBalanceSettingsActivityTab(new BalanceSettingsActivityTab());
        //shiftQueryResultWithActivity.getActivity().setIncludeTimebank(true);
        shifts.add(shiftQueryResultWithActivity);
        return shifts;

    }
/*
    private List<Interval> getIntervalsByDay(DateTime startDate,DateTime endDate){
        List<Interval> intervals = new ArrayList<>();
        while (true){

            DateTime nextDay = startDate.plusDays(1);
            if(!nextDay.isBefore(endDate)){
                nextDay = endDate;
                intervals.add(new Interval(startDate,nextDay));
                break;
            }
            intervals.add(new Interval(startDate,nextDay));
            startDate = nextDay;
        }
        return intervals;
    }

    private List<Interval> getIntervalsByWeek(DateTime startDate,DateTime endDate){
        List<Interval> intervals = new ArrayList<>();
        while (true){
            DateTime lastDayOfWeek = startDate.dayOfWeek().withMaximumValue().plusDays(1);
            if(!lastDayOfWeek.isBefore(endDate)){
                lastDayOfWeek = endDate;
                intervals.add(new Interval(startDate,lastDayOfWeek));
                break;
            }
            intervals.add(new Interval(startDate,lastDayOfWeek));
            startDate = lastDayOfWeek;
        }
        return intervals;
    }

    private List<Interval> getIntervalsByMonth(DateTime startDate,DateTime endDate){
        List<Interval> intervals = new ArrayList<>();
        while (true){
            DateTime lastDayOfMonth = startDate.dayOfMonth().withMaximumValue().plusDays(1);
            if(!lastDayOfMonth.isBefore(endDate)){
                lastDayOfMonth = endDate;
                intervals.add(new Interval(startDate,lastDayOfMonth));
                break;
            }
            intervals.add(new Interval(startDate,lastDayOfMonth));
            startDate = lastDayOfMonth;
        }
        return intervals;
    }

    private List<Interval> getIntervalsByYear(DateTime startDate,DateTime endDate){
        List<Interval> intervals = new ArrayList<>();
        while (true){
            DateTime lastDayOfYear = startDate.dayOfYear().withMaximumValue().plusDays(1);
            if(!lastDayOfYear.isBefore(endDate)){
                lastDayOfYear = endDate;
                intervals.add(new Interval(startDate,lastDayOfYear));
                break;
            }
            intervals.add(new Interval(startDate,lastDayOfYear));
            startDate = lastDayOfYear;
        }
        return intervals;
    }*/


}
