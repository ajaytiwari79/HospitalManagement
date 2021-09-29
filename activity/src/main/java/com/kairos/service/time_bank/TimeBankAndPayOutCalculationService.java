package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.pay_out.PayOutDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_bank.*;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;

@Service
public class TimeBankAndPayOutCalculationService {

    @Inject private TimeBankCalculationService timeBankCalculationService;
    @Inject private PayOutRepository payOutRepository;
    @Inject private PayOutCalculationService payOutCalculationService;
    @Inject private ExecutorService executorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankAndPayOutCalculationService.class);



    public TimeBankAndPayoutDTO getTimeBankAdvanceView(TimebankFilterDTO timebankFilterDTO,String sortingOrder,Set<LocalDate> dateSet, Set<DayOfWeek> dayOfWeekSet,List<DateTimeInterval> intervals, Long unitId, double totalTimeBankBeforeStartDate, Date startDate, Date endDate, String query, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, List<EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOS, List<TimeTypeDTO> timeTypeDTOS, Map<DateTimeInterval, List<PayOutTransaction>> payoutTransactionIntervalMap, List<PayOutPerShift> payOutPerShifts, boolean includeTimeTypeCalculation) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = employmentWithCtaDetailsDTOS.get(0);
        TimeBankDTO timeBankDTO = new TimeBankDTO(startDate, asDate(DateUtils.asLocalDate(endDate)), employmentWithCtaDetailsDTO, employmentWithCtaDetailsDTO.getStaffId(), employmentWithCtaDetailsDTO.getId(), employmentWithCtaDetailsDTO.getTotalWeeklyMinutes(), employmentWithCtaDetailsDTO.getWorkingDaysInWeek());
        DateTimeInterval interval = new DateTimeInterval(startDate.getTime(), endDate.getTime());
        Map<DateTimeInterval, List<DailyTimeBankEntry>> timeBanksIntervalMap = getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        Object[] objects = getShiftsIntervalMap(intervals, shifts, payOutPerShifts);
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> shiftsintervalMap = (Map<DateTimeInterval, List<ShiftWithActivityDTO>>) objects[0];
        Map<DateTimeInterval, List<PayOutPerShift>> payOutsintervalMap = (Map<DateTimeInterval, List<PayOutPerShift>>) objects[1];
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = timeBankCalculationService.getTimeBankIntervals(timebankFilterDTO,unitId, startDate, endDate, totalTimeBankBeforeStartDate, query, intervals, shiftsintervalMap, timeBanksIntervalMap, timeTypeDTOS, employmentWithCtaDetailsDTOS, payoutTransactionIntervalMap, payOutsintervalMap,includeTimeTypeCalculation);
        if(isNull(sortingOrder) || sortingOrder.equals("DSC")) {
            Collections.reverse(timeBankIntervalDTOS);
        }
        timeBankDTO.setTimeIntervals(timeBankIntervalDTOS);
        List<CTADistributionDTO> scheduledCTADistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistribution().getScheduledCTADistributions().stream()).collect(Collectors.toList());
        getTotalTimebankDetails(timebankFilterDTO,shifts, timeTypeDTOS, employmentWithCtaDetailsDTO, timeBankDTO, interval, timeBankIntervalDTOS, scheduledCTADistributions,includeTimeTypeCalculation);
        List<PayOutPerShift> payOutPerShiftBeforestartDate = payOutRepository.findAllByEmploymentAndBeforeDate(dateSet,dayOfWeekSet,employmentWithCtaDetailsDTO.getId(), startDate);
        long payoutMinutesBefore = isCollectionNotEmpty(payOutPerShiftBeforestartDate) ? payOutPerShiftBeforestartDate.stream().mapToLong(PayOutPerShift::getTotalPayOutMinutes).sum() : 0;
        PayOutDTO payOutDTO = payOutCalculationService.getAdvanceViewPayout(timebankFilterDTO,sortingOrder,intervals, payOutPerShifts, payoutMinutesBefore, payoutTransactionIntervalMap, employmentWithCtaDetailsDTOS, query);
        return new TimeBankAndPayoutDTO(timeBankDTO, payOutDTO);
    }


    private void getTotalTimebankDetails(TimebankFilterDTO timebankFilterDTO,List<ShiftWithActivityDTO> shifts, List<TimeTypeDTO> timeTypeDTOS, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, TimeBankDTO timeBankDTO, DateTimeInterval interval, List<TimeBankIntervalDTO> timeBankIntervalDTOS, List<CTADistributionDTO> scheduledCTADistributions, boolean includeTimeTypeCalculation) {
        Map<String, Double> ctaDistributionMap = scheduledCTADistributions.stream().collect(Collectors.groupingBy(CTADistributionDTO::getName, Collectors.summingDouble(CTADistributionDTO::getMinutes)));
        scheduledCTADistributions = getCTADistributionsOfTimebank(ctaDistributionMap, employmentWithCtaDetailsDTO);
        List<CTADistributionDTO> ctaBonusDistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistribution().getCtaRuletemplateBonus().getCtaDistributions().stream()).collect(Collectors.toList());
        Map<String, Double> ctaBonusDistributionMap = ctaBonusDistributions.stream().collect(Collectors.groupingBy(CTADistributionDTO::getName, Collectors.summingDouble(CTADistributionDTO::getMinutes)));
        double[] calculatedTimebankValues = getSumOfTimebankIntervalValues(timeBankIntervalDTOS);
        double totalContractedMin = calculatedTimebankValues[0];
        double totalScheduledMin = calculatedTimebankValues[1];
        double totalTimeBankAfterCtaMin = calculatedTimebankValues[2];
        double totalTimeBankBeforeCtaMin = calculatedTimebankValues[3];
        double totalTimeBankDiff = calculatedTimebankValues[4];
        double totalTimeBank = calculatedTimebankValues[5];
        double requestPayOut = calculatedTimebankValues[6];
        double paidPayOut = calculatedTimebankValues[7];
        double approvePayOut = calculatedTimebankValues[8];
        double totalPlannedMinutes = calculatedTimebankValues[9];
        double plannedMinutesOfTimebank = calculatedTimebankValues[10];
        double protectedDaysOffMinutes = (long)calculatedTimebankValues[11];
        timeBankDTO.setApprovePayOut(approvePayOut);
        timeBankDTO.setPaidoutChange(paidPayOut);
        timeBankDTO.setRequestPayOut(requestPayOut);
        timeBankDTO.setTimeBankDistribution(new TimeBankCTADistributionDTO(scheduledCTADistributions, getCTABonusDistributions(ctaBonusDistributionMap, employmentWithCtaDetailsDTO.getCtaRuleTemplates()), plannedMinutesOfTimebank));
        timeBankDTO.setWorkingTimeType(isNotNull(timeTypeDTOS) && includeTimeTypeCalculation ? getWorkingTimeType(timebankFilterDTO,interval, shifts, timeTypeDTOS) : null);
        timeBankDTO.setTotalContractedMin(totalContractedMin);
        timeBankDTO.setTotalTimeBankMin(totalTimeBank - approvePayOut);
        timeBankDTO.setTotalTimeBankAfterCtaMin(totalTimeBankAfterCtaMin - approvePayOut);
        timeBankDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBeforeCtaMin);
        timeBankDTO.setTotalScheduledMin(totalScheduledMin);
        timeBankDTO.setTotalTimeBankDiff(totalTimeBankDiff);
        timeBankDTO.setTotalPlannedMinutes(totalPlannedMinutes);
        timeBankDTO.setProtectedDaysOffMinutes(protectedDaysOffMinutes);
    }

    private List<CTADistributionDTO> getCTADistributionsOfTimebank(Map<String, Double> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        employmentWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            if (cta.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && cta.getCalculationFor().equals(SCHEDULED_HOURS)) {
                scheduledCTADistributions.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getName(), 0d),0));
            }
        });
        return scheduledCTADistributions;
    }
    public double[] getSumOfTimebankIntervalValues(List<TimeBankIntervalDTO> timeBankIntervalDTOS) {
        double totalContractedMin = 0l;
        double totalScheduledMin = 0l;
        double totalTimeBankAfterCtaMin = 0l;
        double totalTimeBankBeforeCtaMin = 0l;
        double totalTimeBankDiff = 0l;
        double totalTimeBank = 0l;
        double approvePayOut = 0l;
        double requestPayOut = 0l;
        double paidPayOut = 0l;
        double totalPlannedMinutes = 0l;
        double plannedMinutesOfTimebank = 0l;
        double protectedDaysOffMinutes = 0l;
        for (TimeBankIntervalDTO timeBankIntervalDTO : timeBankIntervalDTOS) {
            totalContractedMin += timeBankIntervalDTO.getTotalContractedMin();
            totalScheduledMin += timeBankIntervalDTO.getTotalScheduledMin();
            totalTimeBankDiff += timeBankIntervalDTO.getTotalTimeBankDiff();
            totalTimeBank += timeBankIntervalDTO.getTotalTimeBankMin();
            requestPayOut += timeBankIntervalDTO.getRequestPayOut();
            paidPayOut += timeBankIntervalDTO.getPaidoutChange();
            approvePayOut += timeBankIntervalDTO.getApprovePayOut();
            totalPlannedMinutes += timeBankIntervalDTO.getTotalPlannedMinutes();
            plannedMinutesOfTimebank += timeBankIntervalDTO.getTimeBankDistribution().getPlannedMinutesOfTimebank();
            protectedDaysOffMinutes += timeBankIntervalDTO.getProtectedDaysOffMinutes();
        }
        if (!timeBankIntervalDTOS.isEmpty()) {
            totalTimeBankBeforeCtaMin = timeBankIntervalDTOS.get(timeBankIntervalDTOS.size() - 1).getTotalTimeBankBeforeCtaMin();
            totalTimeBankAfterCtaMin = totalTimeBankBeforeCtaMin + totalTimeBankDiff;
        }
        return new double[]{totalContractedMin, totalScheduledMin, totalTimeBankAfterCtaMin, totalTimeBankBeforeCtaMin, totalTimeBankDiff, totalTimeBank, requestPayOut, paidPayOut, approvePayOut, totalPlannedMinutes, plannedMinutesOfTimebank,protectedDaysOffMinutes};
    }

    public CTARuletemplateBonus getCTABonusDistributions(Map<String, Double> ctaDistributionMap, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS) {
        List<CTADistributionDTO> ctaBonusDistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : ctaRuleTemplateDTOS) {
            if (ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && newHashSet(CONDITIONAL_BONUS,BONUS_HOURS,FUNCTIONS,UNUSED_DAYOFF_LEAVES).contains(ctaRuleTemplate.getCalculationFor())) {
                CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0d),0);
                ctaBonusDistributions.add(ctaDistributionDTO);
                ctaBonusMinutes += ctaDistributionDTO.getMinutes();
            }
        }
        return new CTARuletemplateBonus(ctaBonusDistributions, ctaBonusMinutes);
    }

    private Object[] getShiftsIntervalMap(List<DateTimeInterval> intervals, List<ShiftWithActivityDTO> shifts, List<PayOutPerShift> payOutPerShifts) {
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> shiftsintervalMap = new HashMap<>(intervals.size());
        Map<DateTimeInterval, List<PayOutPerShift>> payOutsintervalMap = new HashMap<>(intervals.size());
        intervals.forEach(interval -> {
            Object[] objects = getShiftsByDate(interval, shifts, payOutPerShifts);
            shiftsintervalMap.put(interval, (List<ShiftWithActivityDTO>) objects[0]);
            payOutsintervalMap.put(interval, (List<PayOutPerShift>) objects[1]);
        });
        return new Object[]{shiftsintervalMap, payOutsintervalMap};
    }

    public ScheduleTimeByTimeTypeDTO getWorkingTimeType(TimebankFilterDTO timebankFilterDTO, DateTimeInterval interval, List<ShiftWithActivityDTO> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
        if (isCollectionNotEmpty(timeTypeDTOS)) {
            List<ScheduleTimeByTimeTypeDTO> parentTimeTypes = new ArrayList<>();
            timeTypeDTOS.forEach(timeType -> {
                double totalScheduledMin = 0;
                if (timeType.getTimeTypes().equals(TimeTypes.WORKING_TYPE.toValue()) && timeType.getUpperLevelTimeTypeId() == null) {
                    ScheduleTimeByTimeTypeDTO parentTimeType = new ScheduleTimeByTimeTypeDTO(0);
                    List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timebankFilterDTO,timeType.getId(), interval, shifts, timeTypeDTOS);
                    parentTimeType.setChildren(children);
                    parentTimeType.setName(timeType.getLabel());
                    parentTimeType.setTimeTypeId(timeType.getId());
                    totalScheduledMin = updateTotalScheduledMinByTimeType(timebankFilterDTO,interval, shifts, timeType, totalScheduledMin, children);
                    parentTimeType.setTotalMin(totalScheduledMin);
                    parentTimeType.setTotalMin(children.stream().mapToDouble(ScheduleTimeByTimeTypeDTO::getTotalMin).sum());
                    parentTimeTypes.add(parentTimeType);
                }
            });
            scheduleTimeByTimeTypeDTO.setTotalMin(parentTimeTypes.stream().mapToDouble(ScheduleTimeByTimeTypeDTO::getTotalMin).sum());
            scheduleTimeByTimeTypeDTO.setChildren(parentTimeTypes);
        }
        return scheduleTimeByTimeTypeDTO;
    }

    private double updateTotalScheduledMinByTimeType(TimebankFilterDTO timebankFilterDTO,DateTimeInterval interval, List<ShiftWithActivityDTO> shifts, TimeTypeDTO timeType, Double totalScheduledMin, List<ScheduleTimeByTimeTypeDTO> children) {
        if (!children.isEmpty()) {
            totalScheduledMin += children.stream().mapToDouble(ScheduleTimeByTimeTypeDTO::getTotalMin).sum();
        }
        if (isCollectionNotEmpty(shifts)) {
            for (ShiftWithActivityDTO shift : shifts) {
                for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                    if (timeType.getId().equals(shiftActivity.getActivity().getActivityBalanceSettings().getTimeTypeId()) && interval.contains(shift.getStartDate().getTime())) {
                        if(isNotNull(timebankFilterDTO) && !timebankFilterDTO.isShowTime()){
                            double cost = timeBankCalculationService.getCostByByMinutes(timebankFilterDTO.getEmployment().getEmploymentLines(),shiftActivity.getScheduledMinutes(),asLocalDate(shiftActivity.getStartDate())).intValue();
                            totalScheduledMin += cost;
                        }else {
                            totalScheduledMin += shiftActivity.getScheduledMinutes();
                        }
                    }
                }

            }
        }
        return totalScheduledMin;
    }

    private List<ScheduleTimeByTimeTypeDTO> getTimeTypeDTOS(TimebankFilterDTO timebankFilterDTO,BigInteger timeTypeId, DateTimeInterval interval, List<ShiftWithActivityDTO> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        List<ScheduleTimeByTimeTypeDTO> scheduleTimeByTimeTypeDTOS = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if (timeType.getUpperLevelTimeTypeId() != null && timeType.getUpperLevelTimeTypeId().equals(timeTypeId)) {
                ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
                scheduleTimeByTimeTypeDTO.setTimeTypeId(timeType.getId());
                scheduleTimeByTimeTypeDTO.setName(timeType.getLabel());
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timebankFilterDTO,timeType.getId(), interval, shifts, timeTypeDTOS);
                scheduleTimeByTimeTypeDTO.setChildren(children);
                if (!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToDouble(ScheduleTimeByTimeTypeDTO::getTotalMin).sum();
                }
                updateScheduledMinutesByTimeType(timebankFilterDTO,interval, shifts, timeType, totalScheduledMin, scheduleTimeByTimeTypeDTO);
                scheduleTimeByTimeTypeDTOS.add(scheduleTimeByTimeTypeDTO);
            }
        });
        return scheduleTimeByTimeTypeDTOS;
    }


    private void updateScheduledMinutesByTimeType(TimebankFilterDTO timebankFilterDTO,DateTimeInterval interval, List<ShiftWithActivityDTO> shifts, TimeTypeDTO timeType, int totalScheduledMin, ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO) {
        if (isCollectionNotEmpty(shifts)) {
            for (ShiftWithActivityDTO shift : shifts) {
                for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                    if (timeType.getId().equals(shiftActivity.getActivity().getActivityBalanceSettings().getTimeTypeId()) && interval.contains(shift.getStartDate().getTime())) {
                        if(isNotNull(timebankFilterDTO) && !timebankFilterDTO.isShowTime()){
                            double cost = timeBankCalculationService.getCostByByMinutes(timebankFilterDTO.getEmployment().getEmploymentLines(),shiftActivity.getScheduledMinutes(),asLocalDate(shiftActivity.getStartDate())).intValue();
                            totalScheduledMin += cost;
                        }else {
                            totalScheduledMin += shiftActivity.getScheduledMinutes();
                        }
                    }
                }
            }
            scheduleTimeByTimeTypeDTO.setTotalMin(totalScheduledMin);
        }
    }

    private Object[] getShiftsByDate(DateTimeInterval interval, List<ShiftWithActivityDTO> shifts, List<PayOutPerShift> payOutPerShifts) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>();
        Map<BigInteger, PayOutPerShift> payOutPerShiftMap = new HashMap<>();
        List<PayOutPerShift> intervalPayOutPerShifts = new ArrayList<>();
        for (PayOutPerShift payOutPerShift : payOutPerShifts) {
            if(!BigInteger.valueOf(-1l).equals(payOutPerShift.getShiftId())){
                payOutPerShiftMap.put(payOutPerShift.getShiftId(),payOutPerShift);
            }else if(interval.contains(asDate(payOutPerShift.getDate()).getTime())){
                intervalPayOutPerShifts.add(payOutPerShift);
            }
        }
        shifts.forEach(shift -> {
            if (interval.contains(shift.getStartDate().getTime()) || interval.contains(shift.getEndDate().getTime())) {
                shiftWithActivityDTOS.add(shift);
                if (payOutPerShiftMap.containsKey(shift.getId())) {
                    intervalPayOutPerShifts.add(payOutPerShiftMap.get(shift.getId()));
                }
            }
        });
        return new Object[]{shiftWithActivityDTOS, intervalPayOutPerShifts};
    }

    public Map<DateTimeInterval, List<DailyTimeBankEntry>> getTimebankIntervalsMap(List<DateTimeInterval> intervals, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        Map<DateTimeInterval, List<DailyTimeBankEntry>> timeBanksIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> timeBanksIntervalMap.put(i, getTimeBanksByInterval(i, dailyTimeBankEntries)));
        return timeBanksIntervalMap;
    }

    private List<DailyTimeBankEntry> getTimeBanksByInterval(DateTimeInterval interval, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        List<DailyTimeBankEntry> dailyTimeBanks1Entry = new ArrayList<>();
        dailyTimeBankEntries.forEach(tb -> {
            if (interval.contains(asDate(tb.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate()))) {
                dailyTimeBanks1Entry.add(tb);
            }
        });
        return dailyTimeBanks1Entry;
    }

}
