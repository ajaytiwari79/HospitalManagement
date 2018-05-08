package com.kairos.activity.util.time_bank;

import com.google.common.collect.Lists;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.enums.TimeTypes;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import com.kairos.activity.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.time_bank.*;
import com.kairos.activity.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.activity.constants.AppConstants.*;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Service
public class TimeBankCalculationService {


    private static final Logger log = LoggerFactory.getLogger(TimeBankCalculationService.class);

    /*
    * It is for SelfRostering Tab It calculate timebank for UpcomingDays
    * on the basis of currentCta
    * */
    public List<CalculatedTimeBankByDateDTO> getTimeBankByDates(UnitPositionWithCtaDetailsDTO ctaDto, List<ShiftQueryResultWithActivity> shifts, int days) {
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

    public DailyTimeBankEntry getTimeBankByInterval(UnitPositionWithCtaDetailsDTO ctaDto, Interval interval, List<ShiftQueryResultWithActivity> shifts, DailyTimeBankEntry dailyTimeBankEntry) {
        if (shifts != null && !shifts.isEmpty()) {
            calculateDailyTimebank(interval, ctaDto, shifts, dailyTimeBankEntry);
        } else {
            int dailyContractualMinutes = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? -ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek() : 0;
            dailyTimeBankEntry.setTotalTimeBankMin(dailyContractualMinutes != 0 ? -dailyContractualMinutes : 0);
            dailyTimeBankEntry.setContractualMin(dailyContractualMinutes);
            dailyTimeBankEntry.setScheduledMin(0);
            dailyTimeBankEntry.setTimeBankMinWithoutCta(0);
            dailyTimeBankEntry.setTimeBankMinWithCta(0);
            dailyTimeBankEntry.setStaffId(ctaDto.getStaffId());
            dailyTimeBankEntry.setTimeBankCTADistributionList(getDistribution(ctaDto));
        }
        return dailyTimeBankEntry;
    }



    public void calculateScheduleAndDurationHour(Shift shift, Activity activity, StaffUnitPositionDetails unitPosition){
        int scheduledMinutes = 0;
        int duration = 0;
        switch (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) {
            case ENTERED_MANUALLY:
                duration = shift.getDurationMinutes();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case FIXED_TIME:
                duration = activity.getTimeCalculationActivityTab().getFixedTimeValue().intValue();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case ENTERED_TIMES:
                duration = (int) new Interval(shift.getStartDate().getTime(), shift.getEndDate().getTime()).toDuration().getStandardMinutes();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;

            case AppConstants.FULL_DAY_CALCULATION:
                duration = new Double((unitPosition.getFullTimeWeeklyMinutes() / unitPosition.getWorkingDaysInWeek()) * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = new Double(unitPosition.getTotalWeeklyMinutes() * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.FULL_WEEK:
                duration = new Double((unitPosition.getFullTimeWeeklyMinutes() / unitPosition.getWorkingDaysInWeek()) * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
        }
        shift.setDurationMinutes(duration);
        shift.setScheduledMinutes(scheduledMinutes);
    }

    private void calculateEnteredManually(Shift shift, Activity activity) {
        int duration = (int) new Interval(shift.getStartDate().getTime(), shift.getEndDate().getTime()).toDuration().getStandardMinutes();
        shift.setDurationMinutes(duration);
        shift.setScheduledMinutes(new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue());
    }


    public List<TimeBankCTADistribution> getDistribution(UnitPositionWithCtaDetailsDTO ctaDto) {
        List<TimeBankCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaDto.getCtaRuleTemplates().size());
        ctaDto.getCtaRuleTemplates().forEach(rt -> {
            timeBankCTADistributions.add(new TimeBankCTADistribution(rt.getName(), 0, rt.getId()));
        });
        return timeBankCTADistributions;
    }

    //TODO complete review by Sachin and need Test cases
    public DailyTimeBankEntry calculateDailyTimebank(Interval interval, UnitPositionWithCtaDetailsDTO ctaDto, List<ShiftQueryResultWithActivity> shifts, DailyTimeBankEntry dailyTimeBankEntry) {
        int totalDailyTimebank = 0;
        int dailyScheduledMin = 0;
        int timeBankMinWithoutCta = 0;
        int contractualMin = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek() : 0;
        Map<Long, Integer> ctaTimeBankMinMap = new HashMap<>();
        for (ShiftQueryResultWithActivity shift : shifts) {
            Interval shiftInterval = new Interval(new DateTime(shift.getStartDate().getTime()), new DateTime(shift.getEndDate().getTime()));
            if (interval.overlaps(shiftInterval)) {
                shiftInterval = interval.overlap(shiftInterval);
                //totalDailyTimebank += dailyScheduledMin;
                for (CTARuleTemplateCalulatedTimeBankDTO ruleTemplate : ctaDto.getCtaRuleTemplates()) {
                    if (ruleTemplate.getAccountType() == null) continue;
                    if (ruleTemplate.getAccountType().equals(TIMEBANK_ACCOUNT)) {
                        int ctaTimeBankMin = 0;
                        if ((ruleTemplate.getActivityIds().contains(shift.getActivity().getId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId())))) {
                            if (((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(DateUtils.toLocalDate(shiftInterval.getStart()))))) {
                                if (ruleTemplate.isCalculateScheduledHours()) {
                                    dailyScheduledMin += shift.getScheduledMinutes();
                                    totalDailyTimebank+=dailyScheduledMin;
                                } else {
                                    for (CTAIntervalDTO ctaIntervalDTO : ruleTemplate.getCtaIntervalDTOS()) {
                                        Interval ctaInterval = getCTAInterval(ctaIntervalDTO, interval);
                                        if (ctaInterval.overlaps(shiftInterval)) {
                                            int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).toDuration().getStandardMinutes();
                                            if (ctaIntervalDTO.getCompensationType().equals(AppConstants.MINUTES)) {
                                                ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * ctaIntervalDTO.getCompensationValue();
                                                totalDailyTimebank += ctaTimeBankMin;
                                                break;
                                            } else if (ctaIntervalDTO.getCompensationType().equals(AppConstants.PERCENT)) {
                                                ctaTimeBankMin += (int) (((double) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) / 100) * ctaIntervalDTO.getCompensationValue());
                                                totalDailyTimebank += ctaTimeBankMin;
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
                        }
                        ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId()) ? ctaTimeBankMinMap.get(ruleTemplate.getId()) + ctaTimeBankMin : ctaTimeBankMin);
                    }
                }
            }
        }
        totalDailyTimebank = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? totalDailyTimebank - contractualMin : totalDailyTimebank;
        timeBankMinWithoutCta = dailyScheduledMin-contractualMin;
        dailyTimeBankEntry.setStaffId(ctaDto.getStaffId());
        dailyTimeBankEntry.setTimeBankMinWithoutCta(timeBankMinWithoutCta);
        dailyTimeBankEntry.setTimeBankMinWithCta(ctaTimeBankMinMap.entrySet().stream().mapToInt(c->c.getValue()).sum());
        dailyTimeBankEntry.setContractualMin(contractualMin);
        dailyTimeBankEntry.setScheduledMin(dailyScheduledMin);
        dailyTimeBankEntry.setTotalTimeBankMin(totalDailyTimebank);
        dailyTimeBankEntry.setTimeBankCTADistributionList(getBlankTimeBankDistribution(ctaDto.getCtaRuleTemplates(), ctaTimeBankMinMap));
        return dailyTimeBankEntry;
    }

    public Interval getCTAInterval(CTAIntervalDTO ctaIntervalDTO,Interval interval){
        int ctaStart = ctaIntervalDTO.getStartTime();
        int ctaEnd = ctaIntervalDTO.getStartTime()>ctaIntervalDTO.getEndTime() || ctaIntervalDTO.getEndTime()==0? 1440+ctaIntervalDTO.getEndTime() : ctaIntervalDTO.getEndTime();
        return new Interval(interval.getStart().withTimeAtStartOfDay().plusMinutes(ctaStart), interval.getStart().plusMinutes(ctaEnd));
    }

    private List<TimeBankCTADistribution> getBlankTimeBankDistribution(List<CTARuleTemplateCalulatedTimeBankDTO> ctaRuleTemplateCalulatedTimeBankDTOS, Map<Long, Integer> ctaTimeBankMinMap) {
        List<TimeBankCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalulatedTimeBankDTOS.size());
        for (CTARuleTemplateCalulatedTimeBankDTO ruleTemplate : ctaRuleTemplateCalulatedTimeBankDTOS) {
            timeBankCTADistributions.add(new TimeBankCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId())?ctaTimeBankMinMap.get(ruleTemplate.getId()):0, ruleTemplate.getId()));
        }
        return timeBankCTADistributions;
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

    public TimeBankDTO getAdvanceViewTimeBank(int totalTimeBankBeforeStartDate, Date startDate, Date endDate, String query, List<ShiftQueryResultWithActivity> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, List<TimeTypeDTO> timeTypeDTOS) {
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setCostTimeAgreement(unitPositionWithCtaDetailsDTO);
        timeBankDTO.setStartDate(startDate);
        timeBankDTO.setEndDate(endDate);
        List<Interval> intervals = getAllIntervalsBetweenDates(startDate, endDate, query);
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap = getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap = getShiftsIntervalMap(intervals, shifts);
        timeBankDTO.setStaffId(unitPositionWithCtaDetailsDTO.getStaffId());
        timeBankDTO.setWorkingDaysInWeek(unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek());
        timeBankDTO.setUnitPositionId(unitPositionWithCtaDetailsDTO.getUnitPositionId());
        timeBankDTO.setTotalWeeklyMin(unitPositionWithCtaDetailsDTO.getContractedMinByWeek());
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = getTimeIntervals(totalTimeBankBeforeStartDate, query, intervals, shiftsintervalMap, timeBanksIntervalMap, timeTypeDTOS, unitPositionWithCtaDetailsDTO);
        timeBankDTO.setTimeIntervals(timeBankIntervalDTOS);
        List<TimeBankCTADistributionDTO> timeBankCTADistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistributions().stream()).collect(Collectors.toList());
        timeBankCTADistributions = timeBankCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getId(), Collectors.summingInt(tb -> tb.getMinutes()))).entrySet().stream().map(t -> new TimeBankCTADistributionDTO(t.getKey(), t.getValue())).collect(Collectors.toList());
        timeBankCTADistributions = getDistributionOfTimeBank(timeBankCTADistributions, unitPositionWithCtaDetailsDTO);
        timeBankDTO.setTimeBankDistributions(timeBankCTADistributions);
        timeBankDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
        timeBankDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
        int contractualMin = calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyTimeBankEntries,true);
        timeBankDTO.setTotalContractedMin(contractualMin);
        int calculateTimeBankForInterval = calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyTimeBankEntries,false);
        timeBankDTO.setTotalTimeBankMin(-calculateTimeBankForInterval);
        //timeBankDTO.setTotalTimeBankAfterCtaMin(timeBankIntervalDTOS.stream().mapToInt(t->t.getTotalTimeBankAfterCtaMin()).sum());
        timeBankDTO.setTotalTimeBankDiff(timeBankIntervalDTOS.stream().mapToInt(t->t.getTotalTimeBankDiff()).sum());
        timeBankDTO.setTotalTimeBankMin(timeBankIntervalDTOS.stream().mapToInt(t->t.getTotalTimeBankMin()).sum());
        //timeBankDTO.setTotalTimeBankBeforeCtaMin(timeBankIntervalDTOS.stream().mapToInt(t->t.getTotalTimeBankBeforeCtaMin()).sum());
        timeBankDTO.setMinutesFromCta(timeBankIntervalDTOS.stream().mapToInt(t -> t.getMinutesFromCta()).sum());
        timeBankDTO.setTotalScheduledMin(timeBankIntervalDTOS.stream().mapToInt(t->t.getTotalScheduledMin()).sum());
        timeBankDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBeforeStartDate);
        timeBankDTO.setTotalTimeBankAfterCtaMin(timeBankDTO.getTotalTimeBankMin() + totalTimeBankBeforeStartDate);
        /*if (dailyTimeBankEntries != null && !dailyTimeBankEntries.isEmpty()) {
            timeBankDTO.setMinutesFromCta(dailyTimeBankEntries.stream().mapToInt(t -> t.getTimeBankMinWithCta()).sum());
            int calculatedTimeBank = dailyTimeBankEntries.stream().mapToInt(ti -> ti.getTotalTimeBankMin()).sum();
            int totalTimeBank = calculatedTimeBank - calculateTimeBankForInterval;


            int scheduledMinutes = dailyTimeBankEntries.stream().mapToInt(ti -> ti.getScheduledMin()).sum();
            timeBankDTO.setTotalScheduledMin(scheduledMinutes);
            timeBankDTO.setTotalTimeBankMin(totalTimeBank);
            timeBankDTO.setTotalTimeBankDiff(totalTimeBank);
            timeBankDTO.getCostTimeAgreement().setMinutesFromCta(dailyTimeBankEntries.stream().mapToInt(ti -> ti.getTimeBankMinWithCta()).sum());

        }*/
        return timeBankDTO;
    }

    public TimeBankDTO getOverviewTimeBank(Long unitEmployementPositionId, DateTime startDate, DateTime lastDateTimeOfYear, List<DailyTimeBankEntry> dailyTimeBankEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<Interval> weeklyIntervals = getWeeklyIntervals(startDate, lastDateTimeOfYear);
        List<Interval> monthlyIntervals = getMonthlyIntervals(startDate, lastDateTimeOfYear);
        Map<Interval, List<DailyTimeBankEntry>> weeklyIntervalTimeBankMap = getTimebankIntervalsMap(weeklyIntervals, dailyTimeBankEntries);
        Map<Interval, List<DailyTimeBankEntry>> monthlyIntervalTimeBankMap = getTimebankIntervalsMap(monthlyIntervals, dailyTimeBankEntries);
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setUnitPositionId(unitEmployementPositionId);
        //int contractualminutes = dailyTimeBankEntries.stream().mapToInt(t -> t.getContractualMin()).sum();
        int scheduledMinutes = dailyTimeBankEntries.stream().mapToInt(t -> t.getScheduledMin()).sum();

        int timebankMinutesAfterCta = dailyTimeBankEntries.stream().mapToInt(t -> t.getTimeBankMinWithCta()).sum();
        int plannedHours = scheduledMinutes + timebankMinutesAfterCta;
        timeBankDTO.setTotalScheduledMin(plannedHours);
        lastDateTimeOfYear = lastDateTimeOfYear.isAfter(new DateTime().withTimeAtStartOfDay()) ? new DateTime().withTimeAtStartOfDay() : lastDateTimeOfYear;
        startDate = startDate.isBefore(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate())) ? DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()) : startDate;
        int contractualMin = calculateTimeBankForInterval(new Interval(startDate, lastDateTimeOfYear), unitPositionWithCtaDetailsDTO,true,dailyTimeBankEntries,true);
        timeBankDTO.setTotalTimeBankMin(plannedHours - contractualMin);
        timeBankDTO.setWeeklyIntervalsTimeBank(getTimeBankByIntervals(weeklyIntervals, weeklyIntervalTimeBankMap, AppConstants.WEEKLY, unitPositionWithCtaDetailsDTO));
        timeBankDTO.setTotalContractedMin(contractualMin);
        timeBankDTO.setMonthlyIntervalsTimeBank(getTimeBankByIntervals(monthlyIntervals, monthlyIntervalTimeBankMap, AppConstants.MONTHLY, unitPositionWithCtaDetailsDTO));
        return timeBankDTO;
    }

    public int calculateTimeBankForInterval(Interval interval, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO,boolean isByOverView,List<DailyTimeBankEntry> dailyTimeBankEntries,boolean calculateContractual) {
        List<LocalDate> dailyTimeBanksDates = new ArrayList<>();
        if(!calculateContractual){
            dailyTimeBanksDates = dailyTimeBankEntries.stream().map(d->DateUtils.toJodaDateTime(d.getDate()).toLocalDate()).collect(Collectors.toList());
        }
        if(isByOverView) {
            interval = getIntervalByDateForOverview(unitPositionWithCtaDetailsDTO, interval);
        }else {
            interval = getIntervalByDateForAdvanceView(unitPositionWithCtaDetailsDTO, interval);
        }
        int contractualMinutes = 0;
        int count = 0;
        if(interval!=null) {
            if (unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() == 7) {
                while (interval.getStart().isBefore(interval.getEnd())) {
                    if(calculateContractual || !dailyTimeBanksDates.contains(interval.getStart().toLocalDate())) {
                        count++;
                    }
                    interval = interval.withStart(interval.getStart().plusDays(1));
                }
                contractualMinutes = count * (unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek());
            } else {
                DateTime startDate = interval.getStart();
                while (startDate.isBefore(interval.getEnd())) {
                    if ((calculateContractual || !dailyTimeBanksDates.contains(startDate.toLocalDate())) && startDate.getDayOfWeek() != DateTimeConstants.SATURDAY && startDate.getDayOfWeek() != DateTimeConstants.SUNDAY) {
                        count++;
                    }
                    startDate = startDate.plusDays(1);
                }
                contractualMinutes = count * (unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek());
            }
        }
        return contractualMinutes;
    }


    public List<TimeBankIntervalDTO> getTimeBankByIntervals(List<Interval> intervals, Map<Interval, List<DailyTimeBankEntry>> timeBankIntervalMap, String basedUpon, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankIntervalMap.get(interval);
            int weekCount = getWeekCount(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = basedUpon.equals(AppConstants.WEEKLY)
                    ? new TimeBankIntervalDTO(AppConstants.WEEK + " " + weekCount)
                    : new TimeBankIntervalDTO(Month.of(interval.getEnd().getMonthOfYear()).toString().toUpperCase());
            if (interval.getStart().toLocalDate().isBefore(new DateTime().toLocalDate())) {
                int calculateTimeBankForInterval = calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,true,dailyTimeBankEntries,true);
                timeBankIntervalDTO.setTotalTimeBankMin(-calculateTimeBankForInterval);
                if (dailyTimeBankEntries != null && !dailyTimeBankEntries.isEmpty()) {
                    int scheduledMinutes = dailyTimeBankEntries.stream().mapToInt(t -> t.getScheduledMin()).sum();
                    int timebankMinutesAfterCta = dailyTimeBankEntries.stream().mapToInt(t -> t.getTimeBankMinWithCta()).sum();
                    int plannedHours = scheduledMinutes + timebankMinutesAfterCta;
                    timeBankIntervalDTO.setTotalTimeBankDiff(plannedHours);
                    timeBankIntervalDTO.setTotalTimeBankMin(plannedHours - calculateTimeBankForInterval);
                }

            }
            timeBankIntervalDTOS.add(timeBankIntervalDTO);
        }
        return timeBankIntervalDTOS;
    }


    private Interval getIntervalByDateForOverview(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Interval interval) {
        DateTime startDate = interval.getStart();
        DateTime endDate = interval.getEnd();
        Interval updatedInterval = null;
        DateTime unitPositionStartDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate());
        DateTime unitPositionEndDate = null;
        if(unitPositionWithCtaDetailsDTO.getUnitPositionEndDate()!=null){
            unitPositionEndDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionEndDate());
        }
        if (startDate.toLocalDate().isBefore(new DateTime().toLocalDate()) && (startDate.isAfter(unitPositionStartDate) || endDate.isAfter(unitPositionStartDate))) {
            if (startDate.isBefore(unitPositionStartDate)) {
                startDate = unitPositionStartDate;
            }
            if (unitPositionEndDate!=null && endDate.toLocalDate().isAfter(unitPositionEndDate.toLocalDate())) {
                endDate = unitPositionEndDate;
            }
            if (endDate.toLocalDate().isAfter(new DateTime().toLocalDate())) {
                endDate = new DateTime().withTimeAtStartOfDay();
            }
            if(startDate.isBefore(endDate)) {
                updatedInterval = new Interval(startDate, endDate);
            }
        }
        return updatedInterval;
    }

    private Interval getIntervalByDateForAdvanceView(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Interval interval) {
        Interval updatedInterval = null;
        DateTime unitPositionStartTime = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate());
        if(interval.contains(unitPositionStartTime) || interval.getStart().isAfter(unitPositionStartTime)){
            DateTime unitPositionEndTime = unitPositionWithCtaDetailsDTO.getUnitPositionEndDate()!=null?DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionEndDate()):null;
            Interval unitPositionInterval = new Interval(unitPositionStartTime,unitPositionEndTime==null?interval.getEnd():unitPositionEndTime);
            if(interval.overlaps(unitPositionInterval)){
                updatedInterval = interval.overlap(unitPositionInterval);
            }
        }
        return updatedInterval;
    }

    //This method because of weekOfTheWeek function depends how many day in current Week
    private int getWeekCount(Interval interval) {
        if (interval.getEnd().getWeekOfWeekyear() == 1 && interval.getEnd().getMonthOfYear() == 12) {
            return interval.getStart().minusDays(1).getWeekOfWeekyear() + 1;
        } else {
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
        DateTime endDate = startDate.getDayOfWeek() == 7 ? startDate.plusWeeks(1) : startDate.withDayOfWeek(DateTimeConstants.SUNDAY);
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


    public List<TimeBankIntervalDTO> getTimeIntervals(int totalTimeBankBefore, String query, List<Interval> intervals, Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap, Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap, List<TimeTypeDTO> timeTypeDTOS, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<ShiftQueryResultWithActivity> shifts = shiftsintervalMap.get(interval);
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBanksIntervalMap.get(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(interval.getStart().toDate(), interval.getEnd().toDate());
            int timeBankOfInterval = calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyTimeBankEntries,false);
            int contractualMin = calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyTimeBankEntries,true);
            timeBankIntervalDTO.setTotalContractualMin(contractualMin);
            if (dailyTimeBankEntries != null && !dailyTimeBankEntries.isEmpty()) {
                timeBankIntervalDTO.setTitle(getTitle(query, interval));
                int calculatedTimeBank = dailyTimeBankEntries.stream().mapToInt(ti -> ti.getTotalTimeBankMin()).sum();
                int totalTimeBank = calculatedTimeBank - timeBankOfInterval;
                timeBankIntervalDTO.setMinutesFromCta(dailyTimeBankEntries.stream().mapToInt(t -> t.getTimeBankMinWithCta()).sum());
                timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore + totalTimeBank);
                timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore);
                timeBankIntervalDTO.setTotalTimeBankMin(totalTimeBank);
                timeBankIntervalDTO.setTotalTimeBankDiff(totalTimeBank);
                totalTimeBankBefore += totalTimeBank;
                int scheduledMinutes = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).mapToInt(tb -> tb.getScheduledMin()).sum();
                timeBankIntervalDTO.setTotalScheduledMin(scheduledMinutes);
                List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
                List<TimeBankCTADistributionDTO> timeBankCTADistributionDTOS = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaRuleTemplateId(), Collectors.summingInt(tb -> tb.getMinutes()))).entrySet().stream().map(t -> new TimeBankCTADistributionDTO(t.getKey(), t.getValue())).collect(Collectors.toList());
                List<TimeBankCTADistributionDTO> timeBankDistributionsDto = getDistributionOfTimeBank(timeBankCTADistributionDTOS, unitPositionWithCtaDetailsDTO);
                timeBankIntervalDTO.setTimeBankDistributions(timeBankDistributionsDto);
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTOS.add(timeBankIntervalDTO);

            } else {
                totalTimeBankBefore -= timeBankOfInterval;
                timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore);
                timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore + timeBankOfInterval);
                timeBankIntervalDTO.setTotalTimeBankMin(-timeBankOfInterval);
                timeBankIntervalDTO.setTotalTimeBankDiff(-timeBankOfInterval);
                timeBankIntervalDTO.setTitle(getTitle(query, interval));
                timeBankIntervalDTO.setTimeBankDistributions(getBlankTimeBankDistribution(unitPositionWithCtaDetailsDTO));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            }
        }
        return Lists.reverse(timeBankIntervalDTOS);
    }

    public List<TimeBankCTADistributionDTO> getDistributionOfTimeBank(List<TimeBankCTADistributionDTO> timeBankCTADistributionDTOS, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        timeBankCTADistributionDTOS.forEach(timeBankCTADistributionDTO -> {
            unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
                if (timeBankCTADistributionDTO.getId().equals(cta.getId())) {
                    timeBankCTADistributionDTO.setName(cta.getName());
                }
            });
        });
        return timeBankCTADistributionDTOS;
    }

    public String getTitle(String query, Interval interval) {
        switch (query) {
            case DAILY:
                return interval.getStart().toLocalDate().toString();
            case WEEKLY:
                return StringUtils.capitalize(AppConstants.WEEKLY) + " " + interval.getStart().getWeekOfWeekyear();
            case MONTHLY:
                return interval.getStart().monthOfYear().getAsText();
            case ANNUALLY:
                return StringUtils.capitalize(AppConstants.YEAR) + " " + interval.getStart().getYear();
            case QUATERLY:
                return StringUtils.capitalize(AppConstants.QUARTER) + " " + (interval.getStart().dayOfMonth().withMinimumValue().equals(interval.getStart()) ? interval.getStart().getMonthOfYear() / 3 : (interval.getStart().getMonthOfYear() / 3) + 1);
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

/*
    public List<TimeTypeIntervalDTO> getTimeTypeInterval(Interval interval, List<ShiftQueryResultWithActivity> shifts) {
        List<TimeTypeIntervalDTO> timeTypeIntervalDTOS = new ArrayList<>();

        return timeTypeIntervalDTOS;
    }*/

    private List<TimeBankIntervalDTO> calculateTimeBankByInterval(Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap, List<Interval> intervals) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        intervals.forEach(i -> {
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBanksIntervalMap.get(i);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(i.getStart().toDate(), i.getEnd().toDate());
            timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(0);//dailyTimeBankEntries.stream().mapToInt(t->t.getTimeBankMinWithCta()).sum());
            timeBankIntervalDTO.setTotalContractualMin(0);//dailyTimeBankEntries.stream().mapToInt(t->t.getContractualMin()).sum());
            timeBankIntervalDTO.setTotalScheduledMin(0);//dailyTimeBankEntries.stream().mapToInt(t->t.getScheduledMin()).sum());
            timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(0);//dailyTimeBankEntries.stream().mapToInt(t->t.getTimeBankMinWithoutCta()).sum());
            timeBankIntervalDTO.setTotalTimeBankMin(0);//dailyTimeBankEntries.stream().mapToInt(t->t.getTotalTimeBankMin()).sum());
            //timeBankIntervalDTO.setTimeBankDistributions(getBlankTimeBankDistribution(dailyTimeBankEntries, null));
            List<TimeBankCTADistribution> timeBankCTADistributions = dailyTimeBankEntries.stream().flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
            timeBankCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaRuleTemplateId(), Collectors.summarizingInt(tb -> tb.getMinutes())));
            timeBankIntervalDTOS.add(timeBankIntervalDTO);
        });

        return timeBankIntervalDTOS;
    }

    private List<TimeBankCTADistributionDTO> getBlankTimeBankDistribution(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<TimeBankCTADistributionDTO> timeBankCTADistributionDTOS = new ArrayList<>();
        unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            timeBankCTADistributionDTOS.add(new TimeBankCTADistributionDTO(cta.getId(), cta.getName(), 0));
        });
        return timeBankCTADistributionDTOS;
    }

    private Map<Interval, List<DailyTimeBankEntry>> getTimebankIntervalsMap(List<Interval> intervals, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> {
            timeBanksIntervalMap.put(i, getTimeBanksByInterval(i, dailyTimeBankEntries));
        });
        return timeBanksIntervalMap;
    }

    private List<DailyTimeBankEntry> getTimeBanksByInterval(Interval interval, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        List<DailyTimeBankEntry> dailyTimeBanks1Entry = new ArrayList<>();
        dailyTimeBankEntries.forEach(tb -> {
            if (interval.contains(DateUtils.asDate(tb.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate()))) {
                dailyTimeBanks1Entry.add(tb);
            }
        });
        return dailyTimeBanks1Entry;
    }

    private int getTotalTimeBanksByInterval(Interval interval, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        int totalTimeBank = 0;
        totalTimeBank = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> (interval.contains(DateUtils.asDate(dailyTimeBankEntry.getDate()).getTime()))).mapToInt(dailyTimeBankEntry -> dailyTimeBankEntry.getTotalTimeBankMin()).sum();
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
                case DAILY:
                    nextEndDay = startDateTime.plusDays(1);
                    break;
                case WEEKLY:
                    nextEndDay = startDateTime.getDayOfWeek() == 7 ? startDateTime.plusWeeks(1) : startDateTime.withDayOfWeek(DateTimeConstants.SUNDAY);
                    break;
                case MONTHLY:
                    nextEndDay = startDateTime.dayOfMonth().withMaximumValue().plusDays(1);
                    break;
                case ANNUALLY:
                    nextEndDay = startDateTime.dayOfYear().withMaximumValue().plusDays(1);
                    break;
                case QUATERLY:
                    nextEndDay = getQuaterByDate(startDateTime);
                    break;
                //case "ByPeriod": return getActualTimeBankByPeriod(startDate,endDate,shifts);
            }
            if(nextEndDay.isAfter(endDateTime)){
                intervals.add(new Interval(startDateTime, endDateTime));
                break;
            }
            intervals.add(new Interval(startDateTime, nextEndDay));
            startDateTime = nextEndDay;
        }
        /*if (!startDateTime.equals(endDateTime) && startDateTime.isBefore(endDateTime)) {
            intervals.add(new Interval(startDateTime, endDateTime));
        }*/
        return intervals;
    }

    private DateTime getQuaterByDate(DateTime dateTime) {
        int quater = (int) Math.ceil((double) dateTime.getMonthOfYear() / 3);
        DateTime quaterDateTime = null;
        switch (quater) {
            case 1:
                quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(3).dayOfMonth().withMaximumValue().plusDays(1);
                break;
            case 2:
                quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(6).dayOfMonth().withMaximumValue().plusDays(1);
                break;
            case 3:
                quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(9).dayOfMonth().withMaximumValue().plusDays(1);
                break;
            case 4:
                quaterDateTime = dateTime.withTimeAtStartOfDay().withMonthOfYear(12).dayOfMonth().withMaximumValue().plusDays(1);
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
        shiftQueryResultWithActivity.getActivity().setBalanceSettingsActivityTab(new BalanceSettingsActivityTab());
        shifts.add(shiftQueryResultWithActivity);
        shiftQueryResultWithActivity = new ShiftQueryResultWithActivity();
        shiftQueryResultWithActivity.setStartDate(new DateTime().withTimeAtStartOfDay().plusHours(7).toDate());
        shiftQueryResultWithActivity.setEndDate(new DateTime().withTimeAtStartOfDay().plusHours(18).toDate());
        shiftQueryResultWithActivity.setActivityId(new BigInteger("123"));
        shiftQueryResultWithActivity.setActivity(new Activity());
        shiftQueryResultWithActivity.getActivity().setBalanceSettingsActivityTab(new BalanceSettingsActivityTab());
        shifts.add(shiftQueryResultWithActivity);
        shiftQueryResultWithActivity = new ShiftQueryResultWithActivity();
        shiftQueryResultWithActivity.setStartDate(new DateTime().withTimeAtStartOfDay().plusHours(18).toDate());
        shiftQueryResultWithActivity.setEndDate(new DateTime().withTimeAtStartOfDay().plusHours(27).toDate());
        shiftQueryResultWithActivity.setActivityId(new BigInteger("123"));
        shiftQueryResultWithActivity.setActivity(new Activity());
        shiftQueryResultWithActivity.getActivity().setBalanceSettingsActivityTab(new BalanceSettingsActivityTab());
        shifts.add(shiftQueryResultWithActivity);
        return shifts;

    }


}
