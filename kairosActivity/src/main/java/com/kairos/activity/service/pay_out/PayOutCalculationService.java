package com.kairos.activity.service.pay_out;

import com.google.common.collect.Lists;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.enums.TimeTypes;
import com.kairos.activity.persistence.model.pay_out.DailyPayOutEntry;
import com.kairos.activity.persistence.model.pay_out.PayOutCTADistribution;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.response.dto.pay_out.*;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.pay_out.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
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
public class PayOutCalculationService {


    /*
    * It is for SelfRostering Tab It calculate PayOut for UpcomingDays
    * on the basis of currentCta
    * */
    public List<CalculatedPayOutByDateDTO> getPayOutByDates(UnitPositionWithCtaDetailsDTO ctaDto, List<ShiftQueryResultWithActivity> shifts, int days) {
        shifts = getFutureShifts();
        Map<String, List<ShiftQueryResultWithActivity>> shiftQueryResultMap = getMapOfShiftByInterval(shifts, 1);
        List<CalculatedPayOutByDateDTO> calculatedPayOutByDateDTOS = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            DateTime dateTime = new DateTime().withTimeAtStartOfDay().plusDays(i);
            Interval interval = new Interval(dateTime, dateTime.plusDays(1));
            List<ShiftQueryResultWithActivity> shiftQueryResults = shiftQueryResultMap.get(interval.toString());
            int totalPayOut = getPayOutByInterval(ctaDto, interval, shiftQueryResults, null).getTotalPayOutMin();
            calculatedPayOutByDateDTOS.add(new CalculatedPayOutByDateDTO(interval.getStart().toLocalDate(), totalPayOut));
        }
        return calculatedPayOutByDateDTOS;
    }

    public DailyPayOutEntry getPayOutByInterval(UnitPositionWithCtaDetailsDTO ctaDto, Interval interval, List<ShiftQueryResultWithActivity> shifts, DailyPayOutEntry dailyPayOutEntry) {
        if (shifts != null && !shifts.isEmpty()) {
            calculateDailyPayOut(interval, ctaDto, shifts, dailyPayOutEntry);
        } else {
            int dailyContractualMinutes = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? -ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek() : 0;
            dailyPayOutEntry.setTotalPayOutMin(dailyContractualMinutes != 0 ? -dailyContractualMinutes : 0);
            dailyPayOutEntry.setContractualMin(dailyContractualMinutes);
            dailyPayOutEntry.setScheduledMin(0);
            dailyPayOutEntry.setPayOutMinWithoutCta(0);
            dailyPayOutEntry.setPayOutMinWithCta(0);
            dailyPayOutEntry.setStaffId(ctaDto.getStaffId());
            dailyPayOutEntry.setPayOutCTADistributionList(getDistribution(ctaDto));
        }
        return dailyPayOutEntry;
    }

    public List<PayOutCTADistribution> getDistribution(UnitPositionWithCtaDetailsDTO ctaDto) {
        List<PayOutCTADistribution> payOutCTADistributions = new ArrayList<>(ctaDto.getCtaRuleTemplates().size());
        ctaDto.getCtaRuleTemplates().forEach(rt -> {
            payOutCTADistributions.add(new PayOutCTADistribution(rt.getName(), 0, rt.getId(), rt.getPayrollSystem(), rt.getPayrollType()));
        });
        return payOutCTADistributions;
    }

    //TODO complete review by Sachin and need Test cases
    public DailyPayOutEntry calculateDailyPayOut(Interval interval, UnitPositionWithCtaDetailsDTO ctaDto, List<ShiftQueryResultWithActivity> shifts, DailyPayOutEntry dailyPayOutEntry) {
        int totalDailyPayOut = 0;
        int dailyScheduledMin = 0;
        int payOutMinWithoutCta = 0;
        int contractualMin = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek() : 0;
        Map<Long, Integer> ctaPayOutMinMap = new HashMap<>();
        for (ShiftQueryResultWithActivity shift : shifts) {
            Interval shiftInterval = new Interval(new DateTime(shift.getStartDate().getTime()).withZone(ctaDto.getUnitDateTimeZone()), new DateTime(shift.getEndDate().getTime()).withZone(ctaDto.getUnitDateTimeZone()));
            shiftInterval = interval.overlap(shiftInterval);
            totalDailyPayOut += dailyScheduledMin;
            for (CTARuleTemplateCalulatedPayOutDTO ruleTemplate : ctaDto.getCtaRuleTemplates()) {
                if(ruleTemplate.getAccountType()==null) continue;
                if(ruleTemplate.getAccountType().equals(PAIDOUT_ACCOUNT)){
                    int ctaPayOutMin = 0;
                    if ((ruleTemplate.getActivityIds().contains(shift.getActivity().getId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))) && ((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(DateUtils.toLocalDate(shiftInterval.getStart()))))) {
                        if(ruleTemplate.isCalculateScheduledHours()) {
                            dailyScheduledMin += shift.getScheduledMinutes();
                        }else {
                            for (CTAIntervalDTO ctaIntervalDTO : ruleTemplate.getCtaIntervalDTOS()) {
                                Interval ctaInterval = getCTAInterval(ctaIntervalDTO,interval);
                                if (ctaInterval.overlaps(shiftInterval)) {
                                    int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).toDuration().getStandardMinutes();
                                    if (ctaIntervalDTO.getCompensationType().equals(AppConstants.MINUTES)) {
                                        ctaPayOutMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * ctaIntervalDTO.getCompensationValue();
                                        totalDailyPayOut += ctaPayOutMin;
                                        break;
                                    } else if (ctaIntervalDTO.getCompensationType().equals(AppConstants.PERCENT)) {
                                        ctaPayOutMin += (int)(((double)Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity())/100) * ctaIntervalDTO.getCompensationValue());
                                        totalDailyPayOut += ctaPayOutMin;
                                        break;
                                    }

                                }
                            }
                        }
                    }
                    ctaPayOutMinMap.put(ruleTemplate.getId(), ctaPayOutMinMap.containsKey(ruleTemplate.getId()) ? ctaPayOutMinMap.get(ruleTemplate.getId()) + ctaPayOutMin : ctaPayOutMin);
                }
            }
        }
        totalDailyPayOut = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? totalDailyPayOut - contractualMin : totalDailyPayOut;
        payOutMinWithoutCta = dailyScheduledMin-contractualMin;
        dailyPayOutEntry.setStaffId(ctaDto.getStaffId());
        dailyPayOutEntry.setPayOutMinWithoutCta(payOutMinWithoutCta);
        dailyPayOutEntry.setPayOutMinWithCta(totalDailyPayOut - payOutMinWithoutCta);
        dailyPayOutEntry.setContractualMin(contractualMin);
        dailyPayOutEntry.setScheduledMin(dailyScheduledMin);
        dailyPayOutEntry.setTotalPayOutMin(totalDailyPayOut);
        dailyPayOutEntry.setPayOutCTADistributionList(getBlankPayOutDistribution(ctaDto.getCtaRuleTemplates(), ctaPayOutMinMap));
        return dailyPayOutEntry;
    }

    public Interval getCTAInterval(CTAIntervalDTO ctaIntervalDTO,Interval interval){
        int ctaStart = ctaIntervalDTO.getStartTime();
        int ctaEnd = ctaIntervalDTO.getStartTime()>ctaIntervalDTO.getEndTime()? 1440+ctaIntervalDTO.getEndTime() : ctaIntervalDTO.getEndTime();
        return new Interval(interval.getStart().withTimeAtStartOfDay().plusMinutes(ctaStart), interval.getStart().plusMinutes(ctaEnd));
    }

    private List<PayOutCTADistribution> getBlankPayOutDistribution(List<CTARuleTemplateCalulatedPayOutDTO> ctaRuleTemplateCalulatedPayOutDTOS, Map<Long, Integer> ctaPayOutMinMap) {
        List<PayOutCTADistribution> payOutCTADistributions = new ArrayList<>(ctaRuleTemplateCalulatedPayOutDTOS.size());
        for (CTARuleTemplateCalulatedPayOutDTO ruleTemplate : ctaRuleTemplateCalulatedPayOutDTOS) {
            payOutCTADistributions.add(new PayOutCTADistribution(ruleTemplate.getName(), ctaPayOutMinMap.containsKey(ruleTemplate.getId())?ctaPayOutMinMap.get(ruleTemplate.getId()):0, ruleTemplate.getId(), ruleTemplate.getPayrollSystem(), ruleTemplate.getPayrollType()));
        }
        return payOutCTADistributions;
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

    public PayOutDTO getAdvanceViewPayOut(int totalPayOutBeforeStartDate, Date startDate, Date endDate, String query, List<ShiftQueryResultWithActivity> shifts, List<DailyPayOutEntry> dailyPayOutEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, List<TimeTypeDTO> timeTypeDTOS) {
        PayOutDTO payOutDTO = new PayOutDTO();
        payOutDTO.setCostTimeAgreement(unitPositionWithCtaDetailsDTO);
        payOutDTO.setStartDate(startDate);
        payOutDTO.setEndDate(endDate);
        List<Interval> intervals = getAllIntervalsBetweenDates(startDate, endDate, query);
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        Map<Interval, List<DailyPayOutEntry>> payOutsIntervalMap = getPayOutIntervalsMap(intervals, dailyPayOutEntries);
        Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap = getShiftsIntervalMap(intervals, shifts);
        payOutDTO.setStaffId(unitPositionWithCtaDetailsDTO.getStaffId());
        payOutDTO.setWorkingDaysInWeek(unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek());
        payOutDTO.setUnitPositionId(unitPositionWithCtaDetailsDTO.getUnitPositionId());
        payOutDTO.setTotalWeeklyMin(unitPositionWithCtaDetailsDTO.getContractedMinByWeek());
        List<PayOutIntervalDTO> payOutIntervalDTOS = getTimeIntervals(totalPayOutBeforeStartDate, query, intervals, shiftsintervalMap, payOutsIntervalMap, timeTypeDTOS, unitPositionWithCtaDetailsDTO);
        payOutDTO.setTimeIntervals(payOutIntervalDTOS);
        List<PayOutCTADistributionDTO> payOutCTADistributions = payOutIntervalDTOS.stream().flatMap(ti -> ti.getPayOutDistributions().stream()).collect(Collectors.toList());
        payOutCTADistributions = payOutCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getId(), Collectors.summingInt(tb -> tb.getMinutes()))).entrySet().stream().map(t -> new PayOutCTADistributionDTO(t.getKey(), t.getValue())).collect(Collectors.toList());
        payOutCTADistributions = getDistributionOfPayOut(payOutCTADistributions, unitPositionWithCtaDetailsDTO);
        payOutDTO.setPayOutDistributions(payOutCTADistributions);
        payOutDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
        payOutDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
        int contractualMin = calculatePayOutForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyPayOutEntries,true);
        payOutDTO.setTotalContractedMin(contractualMin);
        int calculatePayOutForInterval = calculatePayOutForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyPayOutEntries,false);
        payOutDTO.setTotalPayOutMin(-calculatePayOutForInterval);
        if (dailyPayOutEntries != null && !dailyPayOutEntries.isEmpty()) {
            payOutDTO.getCostTimeAgreement().setMinutesFromCta(dailyPayOutEntries.stream().mapToInt(t -> t.getPayOutMinWithCta()).sum());
            int calculatedPayOut = dailyPayOutEntries.stream().mapToInt(ti -> ti.getTotalPayOutMin()).sum();
            int totalPayOut = calculatedPayOut - calculatePayOutForInterval;
            payOutDTO.setTotalPayOutAfterCtaMin(totalPayOut + totalPayOutBeforeStartDate);
            payOutDTO.setTotalPayOutBeforeCtaMin(totalPayOutBeforeStartDate);
            int scheduledMinutes = dailyPayOutEntries.stream().mapToInt(ti -> ti.getScheduledMin()).sum();
            payOutDTO.setTotalScheduledMin(scheduledMinutes);
            payOutDTO.setTotalPayOutMin(totalPayOut);
            payOutDTO.setTotalPayOutDiff(totalPayOut);
            payOutDTO.getCostTimeAgreement().setMinutesFromCta(dailyPayOutEntries.stream().mapToInt(ti -> ti.getPayOutMinWithCta()).sum());

        }
        return payOutDTO;
    }

    public PayOutDTO getOverviewPayOut(Long unitEmployementPositionId, DateTime startDate, DateTime lastDateTimeOfYear, List<DailyPayOutEntry> dailyPayOutEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<Interval> weeklyIntervals = getWeeklyIntervals(startDate, lastDateTimeOfYear);
        List<Interval> monthlyIntervals = getMonthlyIntervals(startDate, lastDateTimeOfYear);
        Map<Interval, List<DailyPayOutEntry>> weeklyIntervalPayOutMap = getPayOutIntervalsMap(weeklyIntervals, dailyPayOutEntries);
        Map<Interval, List<DailyPayOutEntry>> monthlyIntervalPayOutMap = getPayOutIntervalsMap(monthlyIntervals, dailyPayOutEntries);
        PayOutDTO payOutDTO = new PayOutDTO();
        payOutDTO.setUnitPositionId(unitEmployementPositionId);
        //int contractualminutes = dailyPayOutEntries.stream().mapToInt(t -> t.getContractualMin()).sum();
        int scheduledMinutes = dailyPayOutEntries.stream().mapToInt(t -> t.getScheduledMin()).sum();
        int totalPayOutminutes = dailyPayOutEntries.stream().mapToInt(t -> t.getTotalPayOutMin()).sum();
        int payOutMinutesAfterCta = dailyPayOutEntries.stream().mapToInt(t -> t.getPayOutMinWithCta()).sum();

        payOutDTO.setTotalScheduledMin(scheduledMinutes + payOutMinutesAfterCta);
        lastDateTimeOfYear = lastDateTimeOfYear.isAfter(new DateTime().withTimeAtStartOfDay()) ? new DateTime().withTimeAtStartOfDay() : lastDateTimeOfYear;
        startDate = startDate.isBefore(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate())) ? DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()) : startDate;
        int calculatePayOutForInterval = calculatePayOutForInterval(new Interval(startDate, lastDateTimeOfYear), unitPositionWithCtaDetailsDTO,true,dailyPayOutEntries,false);
        payOutDTO.setTotalPayOutMin(totalPayOutminutes - calculatePayOutForInterval);
        payOutDTO.setWeeklyIntervalsPayOut(getPayOutByIntervals(weeklyIntervals, weeklyIntervalPayOutMap, AppConstants.WEEKLY, unitPositionWithCtaDetailsDTO));
        int contractualMin = calculatePayOutForInterval(new Interval(startDate, lastDateTimeOfYear), unitPositionWithCtaDetailsDTO,true,dailyPayOutEntries,true);
        payOutDTO.setTotalContractedMin(contractualMin);
        payOutDTO.setMonthlyIntervalsPayOut(getPayOutByIntervals(monthlyIntervals, monthlyIntervalPayOutMap, AppConstants.MONTHLY, unitPositionWithCtaDetailsDTO));
        return payOutDTO;
    }

    public int calculatePayOutForInterval(Interval interval, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, boolean isByOverView, List<DailyPayOutEntry> dailyPayOutEntries, boolean calculateContractual) {
        List<LocalDate> dailyPayOutsDates = new ArrayList<>();
        if(!calculateContractual){
            dailyPayOutsDates = dailyPayOutEntries.stream().map(d->DateUtils.toJodaDateTime(d.getDate()).toLocalDate()).collect(Collectors.toList());
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
                    if(calculateContractual || !dailyPayOutsDates.contains(interval.getStart().toLocalDate())) {
                        count++;
                    }
                    interval = interval.withStart(interval.getStart().plusDays(1));
                }
                contractualMinutes = count * (unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek());
            } else {
                DateTime startDate = interval.getStart();
                while (startDate.isBefore(interval.getEnd())) {
                    if ((calculateContractual || !dailyPayOutsDates.contains(startDate.toLocalDate())) && startDate.getDayOfWeek() != DateTimeConstants.SATURDAY && startDate.getDayOfWeek() != DateTimeConstants.SUNDAY) {
                        count++;
                    }
                    startDate = startDate.plusDays(1);
                }
                contractualMinutes = count * (unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek());
            }
        }
        return contractualMinutes;
    }


    public List<PayOutIntervalDTO> getPayOutByIntervals(List<Interval> intervals, Map<Interval, List<DailyPayOutEntry>> payOutIntervalMap, String basedUpon, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<PayOutIntervalDTO> payOutIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<DailyPayOutEntry> dailyPayOutEntries = payOutIntervalMap.get(interval);
            int weekCount = getWeekCount(interval);
            PayOutIntervalDTO payOutIntervalDTO = basedUpon.equals(AppConstants.WEEKLY)
                    ? new PayOutIntervalDTO(AppConstants.WEEK + " " + weekCount)
                    : new PayOutIntervalDTO(Month.of(interval.getEnd().getMonthOfYear()).toString().toUpperCase());
            if (interval.getStart().toLocalDate().isBefore(new DateTime().toLocalDate())) {
                int calculatePayOutForInterval = calculatePayOutForInterval(interval, unitPositionWithCtaDetailsDTO,true,dailyPayOutEntries,false);
                payOutIntervalDTO.setTotalPayOutMin(-calculatePayOutForInterval);
                if (dailyPayOutEntries != null && !dailyPayOutEntries.isEmpty()) {
                    int totalPayOut = dailyPayOutEntries.stream().mapToInt(dailyPayOutEntry -> dailyPayOutEntry.getTotalPayOutMin()).sum();
                    int scheduledMinutes = dailyPayOutEntries.stream().mapToInt(t -> t.getScheduledMin()).sum();
                    int payOutMinutesAfterCta = dailyPayOutEntries.stream().mapToInt(t -> t.getPayOutMinWithCta()).sum();
                    payOutIntervalDTO.setTotalPayOutDiff(scheduledMinutes + payOutMinutesAfterCta);
                    payOutIntervalDTO.setTotalPayOutMin(totalPayOut - calculatePayOutForInterval);
                }

            }
            payOutIntervalDTOS.add(payOutIntervalDTO);
        }
        return payOutIntervalDTOS;
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


    public List<PayOutIntervalDTO> getTimeIntervals(int totalPayOutBefore, String query, List<Interval> intervals, Map<Interval, List<ShiftQueryResultWithActivity>> shiftsintervalMap, Map<Interval, List<DailyPayOutEntry>> payOutsIntervalMap, List<TimeTypeDTO> timeTypeDTOS, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<PayOutIntervalDTO> payOutIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<ShiftQueryResultWithActivity> shifts = shiftsintervalMap.get(interval);
            List<DailyPayOutEntry> dailyPayOutEntries = payOutsIntervalMap.get(interval);
            PayOutIntervalDTO payOutIntervalDTO = new PayOutIntervalDTO(interval.getStart().toDate(), interval.getEnd().toDate());
            int payOutOfInterval = calculatePayOutForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyPayOutEntries,false);
            int contractualMin = calculatePayOutForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyPayOutEntries,true);
            payOutIntervalDTO.setTotalContractualMin(contractualMin);
            if (dailyPayOutEntries != null && !dailyPayOutEntries.isEmpty()) {
                payOutIntervalDTO.setTitle(getTitle(query, interval));
                int calculatedPayOut = dailyPayOutEntries.stream().mapToInt(ti -> ti.getTotalPayOutMin()).sum();
                int totalPayOut = calculatedPayOut - payOutOfInterval;
                payOutIntervalDTO.setTotalPayOutAfterCtaMin(totalPayOutBefore + totalPayOut);
                payOutIntervalDTO.setTotalPayOutBeforeCtaMin(totalPayOutBefore);
                payOutIntervalDTO.setTotalPayOutMin(totalPayOut);
                payOutIntervalDTO.setTotalPayOutDiff(totalPayOut);
                totalPayOutBefore += totalPayOut;
                int scheduledMinutes = dailyPayOutEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).mapToInt(tb -> tb.getScheduledMin()).sum();
                payOutIntervalDTO.setTotalScheduledMin(scheduledMinutes);
                List<PayOutCTADistribution> payOutDistributions = dailyPayOutEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(DateUtils.asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getPayOutCTADistributionList().stream()).collect(Collectors.toList());
                List<PayOutCTADistributionDTO> payOutCTADistributionDTOS = payOutDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaRuleTemplateId(), Collectors.summingInt(tb -> tb.getMinutes()))).entrySet().stream().map(t -> new PayOutCTADistributionDTO(t.getKey(), t.getValue())).collect(Collectors.toList());
                List<PayOutCTADistributionDTO> payOutDistributionsDto = getDistributionOfPayOut(payOutCTADistributionDTOS, unitPositionWithCtaDetailsDTO);
                payOutIntervalDTO.setPayOutDistributions(payOutDistributionsDto);
                payOutIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                payOutIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                payOutIntervalDTOS.add(payOutIntervalDTO);

            } else {
                totalPayOutBefore -= payOutOfInterval;
                payOutIntervalDTO.setTotalPayOutAfterCtaMin(totalPayOutBefore);
                payOutIntervalDTO.setTotalPayOutBeforeCtaMin(totalPayOutBefore + payOutOfInterval);
                payOutIntervalDTO.setTotalPayOutMin(-payOutOfInterval);
                payOutIntervalDTO.setTotalPayOutDiff(-payOutOfInterval);
                payOutIntervalDTO.setTitle(getTitle(query, interval));
                payOutIntervalDTO.setPayOutDistributions(getBlankPayOutDistribution(unitPositionWithCtaDetailsDTO));
                payOutIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                payOutIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                payOutIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                payOutIntervalDTO.setNonWorkingTimeType(getNonWorkingTimeType(interval, shifts, timeTypeDTOS));
                payOutIntervalDTOS.add(payOutIntervalDTO);
            }
        }
        return Lists.reverse(payOutIntervalDTOS);
    }

    public List<PayOutCTADistributionDTO> getDistributionOfPayOut(List<PayOutCTADistributionDTO> payOutCTADistributionDTOS, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        payOutCTADistributionDTOS.forEach(payOutCTADistributionDTO -> {
            unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
                if (payOutCTADistributionDTO.getId().equals(cta.getId())) {
                    payOutCTADistributionDTO.setName(cta.getName());
                }
            });
        });
        return payOutCTADistributionDTOS;
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
            //case "ByPeriod": return getActualPayOutByPeriod(startDate,endDate,shifts);
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

    private List<PayOutCTADistributionDTO> getBlankPayOutDistribution(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<PayOutCTADistributionDTO> payOutCTADistributionDTOS = new ArrayList<>();
        unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            payOutCTADistributionDTOS.add(new PayOutCTADistributionDTO(cta.getId(), cta.getName(), 0));
        });
        return payOutCTADistributionDTOS;
    }

    private Map<Interval, List<DailyPayOutEntry>> getPayOutIntervalsMap(List<Interval> intervals, List<DailyPayOutEntry> dailyPayOutEntries) {
        Map<Interval, List<DailyPayOutEntry>> payOutsIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> {
            payOutsIntervalMap.put(i, getPayOutsByInterval(i, dailyPayOutEntries));
        });
        return payOutsIntervalMap;
    }

    private List<DailyPayOutEntry> getPayOutsByInterval(Interval interval, List<DailyPayOutEntry> dailyPayOutEntries) {
        List<DailyPayOutEntry> dailyPayOuts1Entry = new ArrayList<>();
        dailyPayOutEntries.forEach(tb -> {
            if (interval.contains(DateUtils.asDate(tb.getDate()).getTime()) || interval.getStart().equals(new DateTime(DateUtils.asDate(tb.getDate())))) {
                dailyPayOuts1Entry.add(tb);
            }
        });
        return dailyPayOuts1Entry;
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
                //case "ByPeriod": return getActualPayOutByPeriod(startDate,endDate,shifts);
            }
            intervals.add(new Interval(startDateTime, nextEndDay));
            startDateTime = nextEndDay;
        }
        if (!startDateTime.equals(endDateTime) && startDateTime.isBefore(endDateTime)) {
            intervals.add(new Interval(startDateTime, endDateTime));
        }
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

}
