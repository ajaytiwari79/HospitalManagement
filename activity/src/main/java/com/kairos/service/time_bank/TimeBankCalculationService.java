package com.kairos.service.time_bank;

import com.google.common.collect.Lists;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta.CompensationTableInterval;
import com.kairos.dto.activity.pay_out.PayOutDTO;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.time_bank.*;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.ScheduledActivitiesDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.toMap;
/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018
 *
 * */
@Component
public class TimeBankCalculationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankCalculationService.class);

    @Inject
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PayOutRepository payOutRepository;

    public DailyTimeBankEntry getTimeBankByInterval(StaffUnitPositionDetails unitPosition, DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, DailyTimeBankEntry dailyTimeBankEntry, Set<DateTimeInterval> planningPeriodIntervals, List<DayTypeDTO> dayTypeDTOS,boolean validatedByPlanner) {
        boolean anyShiftPublish = false;
        if(isCollectionNotEmpty(shifts)) {
            dailyTimeBankEntry = isNullOrElse(dailyTimeBankEntry, new DailyTimeBankEntry(unitPosition.getId(), unitPosition.getStaffId(), dateTimeInterval.getStartLocalDate()));
            int totalDailyPlannedMinutes = 0;
            int scheduledMinutesOfTimeBank = 0;
            int totalPublishedDailyPlannedMinutes = 0;
            int contractualMinutes = getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals, DateUtils.asLocalDate(shifts.get(0).getStartDate()), unitPosition.getPositionLines());
            Map<BigInteger, Integer> ctaTimeBankMinMap = new HashMap<>();
            Map<Long, DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            boolean ruleTemplateValid = false;
            for (CTARuleTemplateDTO ruleTemplate : unitPosition.getCtaRuleTemplates()) {
                for (ShiftWithActivityDTO shift : shifts) {
                    for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                            ruleTemplateValid = validateCTARuleTemplate(dayTypeDTOMap, ruleTemplate, unitPosition, shift.getPhaseId(), shiftActivity.getActivity().getId(), shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId(), shiftActivity.getStartDate(), shiftActivity.getPlannedTimeId()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT);
                            if(ruleTemplateValid) {
                                int ctaBonusAndScheduledMinutes = 0;
                                if(ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && dateTimeInterval.contains(shiftActivity.getStartDate().getTime())) {
                                    scheduledMinutesOfTimeBank += shiftActivity.getScheduledMinutes();
                                    ctaBonusAndScheduledMinutes = shiftActivity.getScheduledMinutes();
                                    shiftActivity.setScheduledMinutesOfTimebank(shiftActivity.getScheduledMinutes()+shiftActivity.getScheduledMinutesOfTimebank());
                                } else if(ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                                    ctaBonusAndScheduledMinutes = getAndUpdateCtaBonusMinutes(dateTimeInterval, ctaTimeBankMinMap, ruleTemplate, shiftActivity);
                                }
                                shiftActivity.setPlannedMinutesOfTimebank(ctaBonusAndScheduledMinutes);
                                totalDailyPlannedMinutes += ctaBonusAndScheduledMinutes;
                                ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaBonusAndScheduledMinutes);
                                if(shiftActivity.getStatus().contains(ShiftStatus.PUBLISH) || validatedByPlanner) {
                                    totalPublishedDailyPlannedMinutes += ctaBonusAndScheduledMinutes;
                                    anyShiftPublish = true;
                                }
                            }

                    }
                }
                if(ruleTemplate.getCalculationFor().equals(FUNCTIONS) && ruleTemplateValid) {
                    int value = getFunctionalBonusCompensation(unitPosition, ruleTemplate, dateTimeInterval);
                    ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + value);
                    totalDailyPlannedMinutes += value;
                }
            }
            int deltaTimeBankMinutes = totalDailyPlannedMinutes - contractualMinutes;
            int timeBankMinWithoutCta = scheduledMinutesOfTimeBank - contractualMinutes;
            dailyTimeBankEntry.setStaffId(unitPosition.getStaffId());
            dailyTimeBankEntry.setTimeBankMinutesWithoutCta(timeBankMinWithoutCta);
            dailyTimeBankEntry.setPlannedMinutesOfTimebank(totalDailyPlannedMinutes);
            dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(anyShiftPublish ? (totalPublishedDailyPlannedMinutes - contractualMinutes) : 0);
            dailyTimeBankEntry.setCtaBonusMinutesOfTimeBank(ctaTimeBankMinMap.values().stream().mapToInt(ctaBonus -> ctaBonus).sum());
            dailyTimeBankEntry.setContractualMinutes(contractualMinutes);
            dailyTimeBankEntry.setScheduledMinutesOfTimeBank(scheduledMinutesOfTimeBank);
            dailyTimeBankEntry.setDeltaTimeBankMinutes(deltaTimeBankMinutes);
            dailyTimeBankEntry.setTimeBankCTADistributionList(getScheduledCTADistributions(unitPosition.getCtaRuleTemplates(), ctaTimeBankMinMap));
        } else if (isNotNull(dailyTimeBankEntry)) {
                dailyTimeBankEntry.setDeleted(true);
            }
        return dailyTimeBankEntry;
    }

    private int getAndUpdateCtaBonusMinutes(DateTimeInterval dateTimeInterval, Map<BigInteger, Integer> ctaTimeBankMinMap, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivity) {
        DateTimeInterval shiftInterval = dateTimeInterval.overlap(new DateTimeInterval(shiftActivity.getStartDate(),shiftActivity.getEndDate()));
        int ctaBonusAndScheduledMinutes = 0;
        ctaBonusAndScheduledMinutes = calculateBonusAndUpdateShiftActivity(dateTimeInterval, ctaTimeBankMinMap, ruleTemplate, shiftActivity, shiftInterval);
        if(asLocalDate(shiftActivity.getStartDate()).isBefore(asLocalDate(shiftActivity.getEndDate()))){
            DateTimeInterval nextDayInterval = new DateTimeInterval(getStartOfDay(shiftActivity.getEndDate()),getEndOfDay(shiftActivity.getEndDate()));
            shiftInterval = nextDayInterval.overlap(new DateTimeInterval(getStartOfDay(shiftActivity.getEndDate()),shiftActivity.getEndDate()));
            ctaBonusAndScheduledMinutes += calculateBonusAndUpdateShiftActivity(nextDayInterval, ctaTimeBankMinMap, ruleTemplate, shiftActivity, shiftInterval);
        }
        return ctaBonusAndScheduledMinutes;
    }

    private int calculateBonusAndUpdateShiftActivity(DateTimeInterval dateTimeInterval, Map<BigInteger, Integer> ctaTimeBankMinMap, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivity, DateTimeInterval shiftInterval) {
        int ctaBonusMinutes = 0;
        if (isNotNull(shiftInterval)) {
            ctaBonusMinutes = calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval);
            shiftActivity.setTimeBankCtaBonusMinutes(shiftActivity.getTimeBankCtaBonusMinutes() + ctaBonusMinutes);
            Optional<TimeBankDistributionDTO> timeBankDistributionDTOOptional = shiftActivity.getTimeBankCTADistributions().stream().filter(timeBankDistributionDTO -> timeBankDistributionDTO.getCtaRuleTemplateId().equals(ruleTemplate.getId())).findFirst();
            if (timeBankDistributionDTOOptional.isPresent()) {
                timeBankDistributionDTOOptional.get().setMinutes(ctaBonusMinutes + timeBankDistributionDTOOptional.get().getMinutes());
            } else {
                shiftActivity.getTimeBankCTADistributions().add(new TimeBankDistributionDTO(ruleTemplate.getName(), ruleTemplate.getId(), DateUtils.asLocalDate(new Date()), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaBonusMinutes));
            }
        }
        return ctaBonusMinutes;
    }

    public int getFunctionalBonusCompensation(StaffUnitPositionDetails unitPosition, CTARuleTemplateDTO ruleTemplate, DateTimeInterval interval) {
        int value = 0;
        Long functionId = null;
        if(isNull(unitPosition.getFunctionId())) {
            Optional<FunctionDTO> appliedFunctionDTO = unitPosition.getAppliedFunctions().stream().filter(function -> function.getAppliedDates().contains(interval.getStartLocalDate())).findFirst();
            functionId = appliedFunctionDTO.isPresent() ? appliedFunctionDTO.get().getId() : null;
        }
        if(ruleTemplate.getStaffFunctions().contains(isNotNull(unitPosition.getFunctionId()) ? unitPosition.getFunctionId() : functionId)) {
            value = !getHourlyCostByDate(unitPosition.getPositionLines(), interval.getStartLocalDate()).equals(new BigDecimal(0)) ? new BigDecimal(ruleTemplate.getCalculateValueAgainst().getFixedValue().getAmount()).divide(unitPosition.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(60)).intValue() : 0;
        }
        return value;
    }

    public int calculateCTARuleTemplateBonus(CTARuleTemplateDTO ruleTemplate, DateTimeInterval dateTimeInterval, DateTimeInterval shiftDateTimeInterval) {
        int ctaTimeBankMin = 0;
        Interval shiftInterval = new Interval(shiftDateTimeInterval.getStartDate().getTime(), shiftDateTimeInterval.getEndDate().getTime());
        for (CompensationTableInterval ctaInterval : ruleTemplate.getCompensationTable().getCompensationTableInterval()) {
            List<Interval> intervalOfCTAs = getCTAInterval(ctaInterval, new DateTime(dateTimeInterval.getStartDate()));
            for (Interval intervalOfCTA : intervalOfCTAs) {
                if(intervalOfCTA.overlaps(shiftInterval)) {
                    int overlapTimeInMin = (int) intervalOfCTA.overlap(shiftInterval).toDuration().getStandardMinutes();
                    if(ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.MINUTES)) {
                        ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                        break;
                    } else if(ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.PERCENT)) {
                        ctaTimeBankMin += (int) (((double) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) / 100) * ctaInterval.getValue());
                        break;
                    }

                }
            }
        }
        return ctaTimeBankMin;
    }

    public int getContractualAndTimeBankByPlanningPeriod(Set<DateTimeInterval> planningPeriodIntervals, java.time.LocalDate localDate, List<com.kairos.dto.user.employment.UnitPositionLinesDTO> positionLines) {
        Date date = asDate(localDate);
        int contractualMinutes = 0;
        if(CollectionUtils.isNotEmpty(positionLines)) {
            boolean valid = false;
            for (DateTimeInterval planningPeriodInterval : planningPeriodIntervals) {
                if(planningPeriodInterval.contains(date) || planningPeriodInterval.getEndLocalDate().equals(localDate)) {
                    valid = true;
                    break;
                }
            }
            if(valid) {
                for (com.kairos.dto.user.employment.UnitPositionLinesDTO positionLine : positionLines) {
                    DateTimeInterval positionInterval = positionLine.getInterval();
                    if((positionInterval == null && (positionLine.getStartDate().equals(localDate) || positionLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(date) || positionLine.getEndDate().equals(localDate)))) {
                        contractualMinutes = localDate.getDayOfWeek().getValue() <= positionLine.getWorkingDaysInWeek() ? positionLine.getTotalWeeklyMinutes() / positionLine.getWorkingDaysInWeek() : 0;
                        break;
                    }
                }
            }
        }
        return contractualMinutes;
    }

    public void calculateScheduledAndDurationMinutes(ShiftActivity shiftActivity, Activity activity, StaffUnitPositionDetails unitPosition) {
        if(shiftActivity.getStartDate().after(shiftActivity.getEndDate())) {
            exceptionService.invalidRequestException("activity.end_date.less_than.start_date", shiftActivity.getActivityName());
        }
        int scheduledMinutes = 0;
        int duration = 0;
        int weeklyMinutes;
        switch (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) {
            case ENTERED_MANUALLY:
                duration = shiftActivity.getDurationMinutes();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case FIXED_TIME:
                duration = activity.getTimeCalculationActivityTab().getFixedTimeValue().intValue();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case ENTERED_TIMES:
                duration = (int) new Interval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime()).toDuration().getStandardMinutes();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case AppConstants.FULL_DAY_CALCULATION:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullDayCalculationType())) ? unitPosition.getFullTimeWeeklyMinutes() : unitPosition.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = new Double(unitPosition.getTotalWeeklyMinutes() * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullWeekCalculationType())) ? unitPosition.getFullTimeWeeklyMinutes() : unitPosition.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
        }
        shiftActivity.setDurationMinutes(duration);
        if(TimeTypes.WORKING_TYPE.toString().equals(shiftActivity.getTimeType())) {
            shiftActivity.setScheduledMinutes(scheduledMinutes);
        }
    }

    public boolean validateCTARuleTemplate(Map<Long, DayTypeDTO> dayTypeDTOMap, CTARuleTemplateDTO ruleTemplate, StaffUnitPositionDetails unitPositionDetails, BigInteger shiftPhaseId, BigInteger activityId, BigInteger timeTypeId, Date shiftDate, BigInteger plannedTimeId) {
        return ruleTemplate.isRuleTemplateValid(unitPositionDetails.getEmploymentType().getId(), shiftPhaseId, activityId, timeTypeId, plannedTimeId) && isDayTypeValid(shiftDate, ruleTemplate, dayTypeDTOMap);
    }

    private boolean isDayTypeValid(Date shiftDate, CTARuleTemplateDTO ruleTemplateDTO, Map<Long, DayTypeDTO> dayTypeDTOMap) {
        List<DayTypeDTO> dayTypeDTOS = ruleTemplateDTO.getDayTypeIds().stream().map(daytypeId -> dayTypeDTOMap.get(daytypeId)).collect(Collectors.toList());
        boolean valid = false;
        for (DayTypeDTO dayTypeDTO : dayTypeDTOS) {
            if(dayTypeDTO.isHolidayType()) {
                for (CountryHolidayCalenderDTO countryHolidayCalenderDTO : dayTypeDTO.getCountryHolidayCalenderData()) {
                    DateTimeInterval dateTimeInterval;
                    if(dayTypeDTO.isAllowTimeSettings()) {
                        LocalTime holidayEndTime = countryHolidayCalenderDTO.getEndTime().get(ChronoField.MINUTE_OF_DAY) == 0 ? LocalTime.MAX : countryHolidayCalenderDTO.getEndTime();
                        dateTimeInterval = new DateTimeInterval(asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getStartTime()), asDate(countryHolidayCalenderDTO.getHolidayDate(), holidayEndTime));
                    } else {
                        dateTimeInterval = new DateTimeInterval(asDate(countryHolidayCalenderDTO.getHolidayDate()), asDate(countryHolidayCalenderDTO.getHolidayDate().plusDays(1)));
                    }
                    valid = dateTimeInterval.contains(shiftDate);
                    if(valid) {
                        break;
                    }
                }
            } else {
                valid = ruleTemplateDTO.getDays() != null && ruleTemplateDTO.getDays().contains(asLocalDate(shiftDate).getDayOfWeek());
            }
            if(valid) {
                break;
            }
        }
        return valid;
    }

    private List<Interval> getCTAInterval(CompensationTableInterval interval, DateTime startDate) {
        List<Interval> ctaIntervals = new ArrayList<>(2);
        if(interval.getFrom().isAfter(interval.getTo())) {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay().plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay().plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.withTimeAtStartOfDay().plusDays(1)));
        } else if(interval.getFrom().equals(interval.getTo())) {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay().plusDays(1)));
        } else {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay().plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.withTimeAtStartOfDay().plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
        }
        return ctaIntervals;
    }

    private List<TimeBankCTADistribution> getScheduledCTADistributions(List<CTARuleTemplateDTO> ctaRuleTemplateCalulatedTimeBankDTOS, Map<BigInteger, Integer> ctaTimeBankMinMap) {
        List<TimeBankCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalulatedTimeBankDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateCalulatedTimeBankDTOS) {
            if(ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
                timeBankCTADistributions.add(new TimeBankCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0), ruleTemplate.getId()));
            }
        }
        return timeBankCTADistributions;
    }

    private Object[] getShiftsByDate(Interval interval, List<ShiftWithActivityDTO> shifts, List<PayOutPerShift> payOutPerShifts) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>();
        Map<BigInteger, PayOutPerShift> payOutPerShiftMap = payOutPerShifts.stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
        List<PayOutPerShift> intervalPayOutPerShifts = new ArrayList<>();
        shifts.forEach(shift -> {
            if(interval.contains(shift.getStartDate().getTime()) || interval.contains(shift.getEndDate().getTime())) {
                shiftWithActivityDTOS.add(shift);
                if(payOutPerShiftMap.containsKey(shift.getId())) {
                    intervalPayOutPerShifts.add(payOutPerShiftMap.get(shift.getId()));
                }
            }
        });
        return new Object[]{shiftWithActivityDTOS, intervalPayOutPerShifts};
    }

    public TimeBankAndPayoutDTO getTimeBankAdvanceView(List<Interval> intervals, Long unitId, long totalTimeBankBeforeStartDate, Date startDate, Date endDate, String query, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, List<TimeTypeDTO> timeTypeDTOS, Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap) {
        TimeBankDTO timeBankDTO = new TimeBankDTO(startDate, asDate(DateUtils.asLocalDate(endDate).minusDays(1)), unitPositionWithCtaDetailsDTO, unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getId(), unitPositionWithCtaDetailsDTO.getTotalWeeklyMinutes(), unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek());
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        List<PayOutPerShift> payOutPerShifts = payOutRepository.findAllByUnitPositionAndDate(unitPositionWithCtaDetailsDTO.getId(), startDate, endDate);
        Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap = getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        Object[] objects = getShiftsIntervalMap(intervals, shifts, payOutPerShifts);
        Map<Interval, List<ShiftWithActivityDTO>> shiftsintervalMap = (Map<Interval, List<ShiftWithActivityDTO>>) objects[0];
        Map<Interval, List<PayOutPerShift>> payOutsintervalMap = (Map<Interval, List<PayOutPerShift>>) objects[1];
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = getTimeBankIntervals(unitId, startDate, endDate, totalTimeBankBeforeStartDate, query, intervals, shiftsintervalMap, timeBanksIntervalMap, timeTypeDTOS, unitPositionWithCtaDetailsDTO, payoutTransactionIntervalMap, payOutsintervalMap);
        timeBankDTO.setTimeIntervals(timeBankIntervalDTOS);
        List<CTADistributionDTO> scheduledCTADistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistribution().getScheduledCTADistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = scheduledCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getName(), Collectors.summingInt(tb -> tb.getMinutes())));
        scheduledCTADistributions = getScheduledCTADistributions(ctaDistributionMap, unitPositionWithCtaDetailsDTO);
        List<CTADistributionDTO> ctaBonusDistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistribution().getCtaRuletemplateBonus().getCtaDistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaBonusDistributionMap = ctaBonusDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getName(), Collectors.summingInt(tb -> tb.getMinutes())));
        long[] calculatedTimebankValues = getSumOfTimebankIntervalValues(timeBankIntervalDTOS);
        long totalContractedMin = calculatedTimebankValues[0];
        long totalScheduledMin = calculatedTimebankValues[1];
        long totalTimeBankAfterCtaMin = calculatedTimebankValues[2];
        long totalTimeBankBeforeCtaMin = calculatedTimebankValues[3];
        long totalTimeBankDiff = calculatedTimebankValues[4];
        long totalTimeBank = calculatedTimebankValues[5];
        long requestPayOut = calculatedTimebankValues[6];
        long paidPayOut = calculatedTimebankValues[7];
        long approvePayOut = calculatedTimebankValues[8];
        long totalPlannedMinutes = calculatedTimebankValues[9];
        long plannedMinutesOfTimebank = calculatedTimebankValues[10];
        timeBankDTO.setApprovePayOut(approvePayOut);
        timeBankDTO.setPaidoutChange(paidPayOut);
        timeBankDTO.setRequestPayOut(requestPayOut);
        timeBankDTO.setTimeBankDistribution(new TimeBankCTADistributionDTO(scheduledCTADistributions, getCTABonusDistributions(ctaBonusDistributionMap, unitPositionWithCtaDetailsDTO),plannedMinutesOfTimebank));
        timeBankDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
        timeBankDTO.setTotalContractedMin(totalContractedMin);
        timeBankDTO.setTotalTimeBankMin(totalTimeBank - approvePayOut);
        timeBankDTO.setTotalTimeBankAfterCtaMin(totalTimeBankAfterCtaMin - approvePayOut);
        timeBankDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBeforeCtaMin);
        timeBankDTO.setTotalScheduledMin(totalScheduledMin);
        timeBankDTO.setTotalTimeBankDiff(totalTimeBankDiff);
        timeBankDTO.setTotalPlannedMinutes(totalPlannedMinutes);
        List<PayOutPerShift> payOutPerShiftBeforestartDate = payOutRepository.findAllByUnitPositionAndBeforeDate(unitPositionWithCtaDetailsDTO.getId(), startDate);
        long payoutMinutesBefore = isCollectionNotEmpty(payOutPerShiftBeforestartDate) ? payOutPerShiftBeforestartDate.stream().mapToLong(payout -> payout.getTotalPayOutMinutes()).sum() : 0;
        PayOutDTO payOut = payOutCalculationService.getAdvanceViewPayout(intervals, payOutPerShifts, payoutMinutesBefore, payoutTransactionIntervalMap, unitPositionWithCtaDetailsDTO, query);
        return new TimeBankAndPayoutDTO(timeBankDTO, payOut);
    }

    private List<CTADistributionDTO> getScheduledCTADistributions(Map<String, Integer> ctaDistributionMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            if(cta.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && cta.getCalculationFor().equals(SCHEDULED_HOURS)) {
                scheduledCTADistributions.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getName(), 0)));

            }
        });
        return scheduledCTADistributions;
    }

    private CTARuletemplateBonus getCTABonusDistributions(Map<String, Integer> ctaDistributionMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<CTADistributionDTO> ctaBonusDistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : unitPositionWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if(ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && (ctaRuleTemplate.getCalculationFor().equals(BONUS_HOURS) || ctaRuleTemplate.getCalculationFor().equals(FUNCTIONS))) {
                CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0));
                ctaBonusDistributions.add(ctaDistributionDTO);
                ctaBonusMinutes += ctaDistributionDTO.getMinutes();
            }
        }
        return new CTARuletemplateBonus(ctaBonusDistributions, ctaBonusMinutes);
    }

    public Map<Interval, List<PayOutTransaction>> getPayoutTrasactionIntervalsMap(List<Interval> intervals, List<PayOutTransaction> payOuts) {
        Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(interval -> payoutTransactionAndIntervalMap.put(interval, getPayoutTransactionsByInterval(interval, payOuts)));
        return payoutTransactionAndIntervalMap;
    }

    private List<PayOutTransaction> getPayoutTransactionsByInterval(Interval interval, List<PayOutTransaction> payOutTransactions) {
        List<PayOutTransaction> payOutTransactionList = new ArrayList<>();
        payOutTransactions.forEach(payOutTransaction -> {
            if(interval.contains(asDate(payOutTransaction.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(payOutTransaction.getDate()))) {
                payOutTransactionList.add(payOutTransaction);
            }
        });
        return payOutTransactionList;
    }

    private long[] getSumOfTimebankIntervalValues(List<TimeBankIntervalDTO> timeBankIntervalDTOS) {
        long totalContractedMin = 0l;
        long totalScheduledMin = 0l;
        long totalTimeBankAfterCtaMin = 0l;
        long totalTimeBankBeforeCtaMin = 0l;
        long totalTimeBankDiff = 0l;
        long totalTimeBank = 0l;
        long approvePayOut = 0l;
        long requestPayOut = 0l;
        long paidPayOut = 0l;
        long totalPlannedMinutes = 0l;
        long plannedMinutesOfTimebank = 0l;
        for (TimeBankIntervalDTO timeBankIntervalDTO : timeBankIntervalDTOS) {
            totalContractedMin += timeBankIntervalDTO.getTotalContractedMin();
            totalScheduledMin += timeBankIntervalDTO.getTotalScheduledMin();
            totalTimeBankDiff += timeBankIntervalDTO.getTotalTimeBankDiff();
            totalTimeBank += timeBankIntervalDTO.getTotalTimeBankMin();
            requestPayOut += timeBankIntervalDTO.getRequestPayOut();
            paidPayOut += timeBankIntervalDTO.getPaidoutChange();
            approvePayOut += timeBankIntervalDTO.getApprovePayOut();
            totalPlannedMinutes += timeBankIntervalDTO.getTotalPlannedMinutes();
            plannedMinutesOfTimebank+=timeBankIntervalDTO.getTimeBankDistribution().getPlannedMinutesOfTimebank();
        }
        if(!timeBankIntervalDTOS.isEmpty()) {
            totalTimeBankBeforeCtaMin = timeBankIntervalDTOS.get(timeBankIntervalDTOS.size() - 1).getTotalTimeBankBeforeCtaMin();
            totalTimeBankAfterCtaMin = totalTimeBankBeforeCtaMin + totalTimeBankDiff;
        }
        return new long[]{totalContractedMin, totalScheduledMin, totalTimeBankAfterCtaMin, totalTimeBankBeforeCtaMin, totalTimeBankDiff, totalTimeBank, requestPayOut, paidPayOut, approvePayOut, totalPlannedMinutes,plannedMinutesOfTimebank};

    }

    public TimeBankDTO getTimeBankOverview(Long unitId, Long unitEmployementPositionId, DateTime startDate, DateTime lastDateTimeOfYear, List<DailyTimeBankEntry> dailyTimeBankEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<Interval> weeklyIntervals = getWeeklyIntervals(startDate, lastDateTimeOfYear);
        List<Interval> monthlyIntervals = getMonthlyIntervals(startDate, lastDateTimeOfYear);
        Map<Interval, List<DailyTimeBankEntry>> weeklyIntervalTimeBankMap = getTimebankIntervalsMap(weeklyIntervals, dailyTimeBankEntries);
        Map<Interval, List<DailyTimeBankEntry>> monthlyIntervalTimeBankMap = getTimebankIntervalsMap(monthlyIntervals, dailyTimeBankEntries);
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setUnitPositionId(unitEmployementPositionId);
        List<TimeBankIntervalDTO> weeklyTimeBankIntervals = getTimeBankByIntervals(unitId, weeklyIntervals, weeklyIntervalTimeBankMap, WEEKLY, unitPositionWithCtaDetailsDTO);
        timeBankDTO.setTotalTimeBankMin(weeklyTimeBankIntervals.stream().mapToLong(t -> t.getTotalTimeBankMin()).sum());
        timeBankDTO.setWeeklyIntervalsTimeBank(weeklyTimeBankIntervals);
        Set<DateTimeInterval> planningPeriodIntervals = getPlanningPeriodIntervals(unitId, startDate.toDate(), lastDateTimeOfYear.toDate());
        int contractualMin = calculateTimeBankForInterval(planningPeriodIntervals, new Interval(startDate, lastDateTimeOfYear), unitPositionWithCtaDetailsDTO, true, dailyTimeBankEntries, true);
        timeBankDTO.setTotalScheduledMin(weeklyTimeBankIntervals.stream().mapToLong(t -> t.getTotalTimeBankDiff()).sum());
        timeBankDTO.setTotalContractedMin(contractualMin);
        timeBankDTO.setMonthlyIntervalsTimeBank(getTimeBankByIntervals(unitId, monthlyIntervals, monthlyIntervalTimeBankMap, MONTHLY, unitPositionWithCtaDetailsDTO));
        timeBankDTO.setHourlyCost(unitPositionWithCtaDetailsDTO.getHourlyCost());
        return timeBankDTO;
    }

    public TimeBankVisualViewDTO getVisualViewTimeBank(DateTimeInterval interval, DailyTimeBankEntry dailyTimeBankEntry, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, Map<String, List<TimeType>> presenceAbsenceTimeTypeMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = getScheduledActivities(shifts);
        List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaName(), Collectors.summingInt(tb -> tb.getMinutes())));
        List<CTADistributionDTO> timeBankDistributionsDto = getDistributionOfTimeBank(ctaDistributionMap, unitPositionWithCtaDetailsDTO,0).getCtaRuletemplateBonus().getCtaDistributions();
        long presenceScheduledMin = getScheduledMinOfActivityByTimeType(presenceAbsenceTimeTypeMap.get("Presence"), shifts);
        long absenceScheduledMin = getScheduledMinOfActivityByTimeType(presenceAbsenceTimeTypeMap.get("Absence"), shifts);
        long totalTimeBankChange = dailyTimeBankEntries.stream().mapToLong(t -> t.getDeltaTimeBankMinutes()).sum();
        //Todo pradeep please fix when accumulated timebank fucnationality is done
        long accumulatedTimeBankBefore = 0;
        long totalTimeBank = accumulatedTimeBankBefore + totalTimeBankChange;
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = getVisualViewTimebankInterval(dailyTimeBankEntries, interval);
        return new TimeBankVisualViewDTO(totalTimeBank, presenceScheduledMin, absenceScheduledMin, totalTimeBankChange, timeBankIntervalDTOS, scheduledActivitiesDTOS, timeBankDistributionsDto);
    }

    private List<TimeBankIntervalDTO> getVisualViewTimebankInterval(List<DailyTimeBankEntry> dailyTimeBankEntries, DateTimeInterval interval) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>((int) interval.getDays());
        Map<java.time.LocalDate, DailyTimeBankEntry> dailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getDate(), v -> v));
        boolean byMonth = interval.getDays() > 7;
        for (int i = 0; i <= interval.getDays(); i++) {
            java.time.LocalDate localDate = interval.getStartLocalDate().plusDays(i);
            DailyTimeBankEntry dailyTimeBankEntry = dailyTimeBankEntryMap.get(localDate);
            String title = byMonth ? localDate.getDayOfMonth() + " " + localDate.getMonth() : localDate.getDayOfWeek().toString();
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(0, 0, title);
            if(Optional.ofNullable(dailyTimeBankEntry).isPresent()) {
                long scheduledMin = dailyTimeBankEntry.getScheduledMinutesOfTimeBank() + dailyTimeBankEntry.getCtaBonusMinutesOfTimeBank();
                long totalTimeBankChange = dailyTimeBankEntry.getDeltaTimeBankMinutes() < 0 ? 0 : dailyTimeBankEntry.getDeltaTimeBankMinutes();
                timeBankIntervalDTO = new TimeBankIntervalDTO(scheduledMin, totalTimeBankChange, title);
            }
            timeBankIntervalDTOS.add(timeBankIntervalDTO);
        }
        return timeBankIntervalDTOS;
    }

    private long getScheduledMinOfActivityByTimeType(List<TimeType> timeTypes, List<ShiftWithActivityDTO> shifts) {
        Map<BigInteger, TimeType> timeTypeMap = timeTypes.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        long scheduledMin = shifts.stream().flatMap(s -> s.getActivities().stream()).filter(s -> timeTypeMap.containsKey(s.getActivity().getBalanceSettingsActivityTab().getTimeTypeId())).mapToLong(s -> s.getScheduledMinutes()).sum();
        return scheduledMin;
    }

    private List<ScheduledActivitiesDTO> getScheduledActivities(List<ShiftWithActivityDTO> shifts) {
        Map<String, Long> activityScheduledMin = shifts.stream().flatMap(s -> s.getActivities().stream()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(activity -> activity.getActivity().getId() + "-" + activity.getActivity().getName(), Collectors.summingLong(s -> s.getScheduledMinutes())));
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = new ArrayList<>(activityScheduledMin.size());
        activityScheduledMin.forEach((activity, mintues) -> {
            String[] idNameArray = activity.split("-");
            scheduledActivitiesDTOS.add(new ScheduledActivitiesDTO(new BigInteger(idNameArray[0]), idNameArray[1], mintues));
        });
        return scheduledActivitiesDTOS;
    }

    public int calculateTimeBankForInterval(Set<DateTimeInterval> planningPeriodIntervals, Interval interval, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, boolean isByOverView, List<DailyTimeBankEntry> dailyTimeBankEntries, boolean calculateContractual) {
        Set<LocalDate> dailyTimeBanksDates = new HashSet<>();
        if(!calculateContractual) {
            dailyTimeBanksDates = dailyTimeBankEntries.stream().map(d -> DateUtils.toJodaDateTime(d.getDate()).toLocalDate()).collect(Collectors.toSet());
        }
        if(isByOverView) {
            interval = getIntervalByDateForOverview(unitPositionWithCtaDetailsDTO, interval);
        } else {
            interval = getIntervalByDateForAdvanceView(unitPositionWithCtaDetailsDTO, interval);
        }
        int contractualMinutes = 0;
        if(interval != null) {
            DateTime startDate = interval.getStart();
            while (startDate.isBefore(interval.getEnd())) {
                if(calculateContractual || !dailyTimeBanksDates.contains(startDate.toLocalDate())) {
                    boolean vaild = (unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() == 7) || (startDate.getDayOfWeek() != DateTimeConstants.SATURDAY && startDate.getDayOfWeek() != DateTimeConstants.SUNDAY);
                    if(vaild) {
                        contractualMinutes += getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals, DateUtils.asLocalDate(startDate), unitPositionWithCtaDetailsDTO.getPositionLines());
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        return contractualMinutes;
    }

    public Set<DateTimeInterval> getPlanningPeriodIntervals(Long unitId, Date startDate, Date endDate) {
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdAndBetweenDates(unitId, startDate, endDate);
        Set<DateTimeInterval> dateTimeIntervals = planningPeriods.stream().map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
        return dateTimeIntervals;
    }

    public Object[] getPlanningPeriodIntervalsWithPlanningPeriods(Long unitId, Date startDate, Date endDate) {
        List<PeriodDTO> planningPeriods = planningPeriodMongoRepository.findAllPeriodsByStartDateAndLastDate(unitId, DateUtils.asLocalDate(startDate), DateUtils.asLocalDate(endDate));
        Set<DateTimeInterval> dateTimeIntervals = planningPeriods.stream().map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
        return new Object[]{dateTimeIntervals, planningPeriods};
    }

    private List<TimeBankIntervalDTO> getTimeBankByIntervals(Long unitId, List<Interval> intervals, Map<Interval, List<DailyTimeBankEntry>> timeBankIntervalMap, String basedUpon, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankIntervalMap.get(interval);
            int weekCount = getWeekCount(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = basedUpon.equals(WEEKLY) ? new TimeBankIntervalDTO(AppConstants.WEEK + " " + weekCount) : new TimeBankIntervalDTO(Month.of(interval.getEnd().getMonthOfYear()).toString().toUpperCase());
            if(interval.getStart().toLocalDate().isBefore(new DateTime().toLocalDate())) {
                Set<DateTimeInterval> planningPeriodIntervals = getPlanningPeriodIntervals(unitId, interval.getStart().toDate(), interval.getEnd().toDate());
                int calculateTimeBankForInterval = calculateTimeBankForInterval(planningPeriodIntervals, interval, unitPositionWithCtaDetailsDTO, true, dailyTimeBankEntries, false);
                timeBankIntervalDTO.setTotalTimeBankMin(-calculateTimeBankForInterval);
                if(dailyTimeBankEntries != null && !dailyTimeBankEntries.isEmpty()) {
                    int totalTimeBank = dailyTimeBankEntries.stream().mapToInt(dailyTimeBankEntry -> dailyTimeBankEntry.getDeltaTimeBankMinutes()).sum();
                    int scheduledMinutes = dailyTimeBankEntries.stream().mapToInt(t -> t.getScheduledMinutesOfTimeBank()).sum();
                    int timebankMinutesAfterCta = dailyTimeBankEntries.stream().mapToInt(t -> t.getCtaBonusMinutesOfTimeBank()).sum();
                    timeBankIntervalDTO.setTotalTimeBankDiff(scheduledMinutes + timebankMinutesAfterCta);
                    timeBankIntervalDTO.setTotalTimeBankMin(totalTimeBank - calculateTimeBankForInterval);
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
        DateTime unitPositionStartDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate());
        DateTime unitPositionEndDate = null;
        if(unitPositionWithCtaDetailsDTO.getEndDate() != null) {
            unitPositionEndDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getEndDate());
        }
        if(startDate.toLocalDate().isBefore(new DateTime().toLocalDate()) && (startDate.isAfter(unitPositionStartDate) || endDate.isAfter(unitPositionStartDate))) {
            if(startDate.isBefore(unitPositionStartDate)) {
                startDate = unitPositionStartDate;
            }
            if(unitPositionEndDate != null && endDate.toLocalDate().isAfter(unitPositionEndDate.toLocalDate())) {
                endDate = unitPositionEndDate;
            }
            if(endDate.toLocalDate().isAfter(new DateTime().toLocalDate())) {
                endDate = new DateTime().withTimeAtStartOfDay();
            }
            if(startDate.isBefore(endDate)) {
                updatedInterval = new Interval(startDate, endDate);
            }
        }
        return updatedInterval;
    }

    public Interval getIntervalByDateForAdvanceView(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Interval interval) {
        Interval updatedInterval = null;
        DateTime unitPositionStartTime = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate());
        if(interval.contains(unitPositionStartTime) || interval.getStart().isAfter(unitPositionStartTime)) {
            DateTime unitPositionEndTime = unitPositionWithCtaDetailsDTO.getEndDate() != null ? DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getEndDate()) : null;
            Interval unitPositionInterval = new Interval(unitPositionStartTime, unitPositionEndTime == null ? interval.getEnd() : unitPositionEndTime);
            if(interval.overlaps(unitPositionInterval)) {
                updatedInterval = interval.overlap(unitPositionInterval);
            }
        }
        return updatedInterval;
    }

    //This method because of weekOfTheWeek function depends how many day in current Week
    private int getWeekCount(Interval interval) {
        if(interval.getEnd().getWeekOfWeekyear() == 1 && interval.getEnd().getMonthOfYear() == 12) {
            return interval.getStart().minusDays(1).getWeekOfWeekyear() + 1;
        } else {
            return interval.getEnd().getWeekOfWeekyear();
        }
    }

    private List<Interval> getMonthlyIntervals(DateTime startDate, DateTime lastDateTimeOfYear) {
        List<Interval> intervals = new ArrayList<>(12);
        DateTime endDate = startDate.dayOfMonth().withMaximumValue();
        while (true) {
            intervals.add(new Interval(startDate, endDate));
            startDate = endDate.plusDays(1);
            endDate = startDate.dayOfMonth().withMaximumValue();
            if(endDate.equals(lastDateTimeOfYear)) {
                intervals.add(new Interval(startDate, lastDateTimeOfYear));
                break;
            }
        }
        return intervals;
    }

    private List<Interval> getWeeklyIntervals(DateTime startDate, DateTime lastDateTimeOfYear) {
        List<Interval> intervals = new ArrayList<>(60);
        DateTime endDate = startDate.getDayOfWeek() == 7 ? startDate.plusWeeks(1) : startDate.withDayOfWeek(DateTimeConstants.SUNDAY);
        while (true) {
            if(endDate.getYear() != startDate.getYear()) {
                intervals.add(new Interval(startDate, lastDateTimeOfYear));
                break;
            }
            intervals.add(new Interval(startDate, endDate));
            startDate = endDate;
            if(lastDateTimeOfYear.equals(endDate)) {
                break;
            }
            endDate = startDate.plusWeeks(1);
        }
        return intervals;
    }

    private List<TimeBankIntervalDTO> getTimeBankIntervals(Long unitId, Date startDate, Date endDate, long totalTimeBankBefore, String query, List<Interval> intervals, Map<Interval, List<ShiftWithActivityDTO>> shiftsintervalMap, Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap, List<TimeTypeDTO> timeTypeDTOS, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap, Map<Interval, List<PayOutPerShift>> payOutsintervalMap) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        Object[] planningPeriodIntervalsWithPlanningPeriods = getPlanningPeriodIntervalsWithPlanningPeriods(unitId, startDate, endDate);
        Set<DateTimeInterval> planningPeriodIntervals = (Set<DateTimeInterval>) planningPeriodIntervalsWithPlanningPeriods[0];
        List<PeriodDTO> planningPeriods = (List<PeriodDTO>) planningPeriodIntervalsWithPlanningPeriods[1];
        for (Interval interval : intervals) {
            List<PayOutPerShift> payOutPerShifts = payOutsintervalMap.get(interval);
            List<ShiftWithActivityDTO> shifts = shiftsintervalMap.get(interval);
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBanksIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionIntervalMap.get(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(interval.getStart().toDate(), query.equals(DAILY) ? interval.getStart().toDate() : interval.getEnd().minusDays(1).toDate(), getPhaseNameByPeriods(planningPeriods, interval.getStart()));
            int timeBankOfInterval = calculateTimeBankForInterval(planningPeriodIntervals, interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
            int contractualMin = calculateTimeBankForInterval(planningPeriodIntervals, interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBankEntries, true);
            timeBankIntervalDTO.setTotalContractedMin(contractualMin);
            Long approvePayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p -> (long) p.getMinutes()).sum();
            Long requestPayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.REQUESTED)).mapToLong(p -> (long) p.getMinutes()).sum();
            Long paidPayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.PAIDOUT)).mapToLong(p -> (long) p.getMinutes()).sum();
            timeBankIntervalDTO.setApprovePayOut(approvePayOut);
            timeBankIntervalDTO.setRequestPayOut(requestPayOut);
            timeBankIntervalDTO.setPaidoutChange(paidPayOut);
            timeBankIntervalDTO.setHeaderName(getHeaderName(query, interval));
            if(isCollectionNotEmpty(dailyTimeBankEntries)) {
                timeBankIntervalDTO.setTitle(getTitle(query, interval));
                long[] calculatedTimebankValues = getSumOfTimebankValues(dailyTimeBankEntries);
                long calculatedTimeBank = calculatedTimebankValues[0];
                long totalTimeBank = calculatedTimeBank - timeBankOfInterval;
                long plannedMinutesOfTimebank = calculatedTimebankValues[1];
                long scheduledMinutesOfTimebank = calculatedTimebankValues[2];
                long[] calculatedPayoutValues = getSumOfPayoutValues(payOutPerShifts);
                long plannedMinutesOfPayout = calculatedPayoutValues[0];
                long scheduledMinutesOfPayout = calculatedPayoutValues[1];
                timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore);
                totalTimeBankBefore += totalTimeBank;
                timeBankIntervalDTO.setTotalPlannedMinutes(plannedMinutesOfTimebank + plannedMinutesOfPayout);
                timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore - approvePayOut);
                timeBankIntervalDTO.setTotalTimeBankMin(totalTimeBank - approvePayOut);
                timeBankIntervalDTO.setTotalTimeBankDiff(totalTimeBank - approvePayOut);
                timeBankIntervalDTO.setTotalScheduledMin(scheduledMinutesOfTimebank + scheduledMinutesOfPayout);
                List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
                Map<String, Integer> ctaDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaName(), Collectors.summingInt(tb -> tb.getMinutes())));
                timeBankIntervalDTO.setTimeBankDistribution(getDistributionOfTimeBank(ctaDistributionMap, unitPositionWithCtaDetailsDTO,plannedMinutesOfTimebank));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            } else {
                totalTimeBankBefore -= timeBankOfInterval;
                timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore - approvePayOut);
                timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore + timeBankOfInterval);
                timeBankIntervalDTO.setTotalTimeBankMin(-timeBankOfInterval + approvePayOut);
                timeBankIntervalDTO.setTotalTimeBankDiff(-timeBankOfInterval + approvePayOut);
                timeBankIntervalDTO.setTitle(getTitle(query, interval));
                timeBankIntervalDTO.setTimeBankDistribution(getDistributionOfTimeBank(new HashMap<>(), unitPositionWithCtaDetailsDTO,0));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            }
        }
        return Lists.reverse(timeBankIntervalDTOS);
    }

    private String getPhaseNameByPeriods(List<PeriodDTO> planningPeriods, DateTime startDate) {
        String phaseName = "";
        java.time.LocalDate startLocalDate = DateUtils.asLocalDate(startDate);
        for (PeriodDTO planningPeriod : planningPeriods) {
            if(planningPeriod.getStartDate().isEqual(startLocalDate) || planningPeriod.getEndDate().isEqual(startLocalDate) || (planningPeriod.getStartDate().isBefore(startLocalDate) && planningPeriod.getEndDate().isAfter(startLocalDate))) {
                phaseName = planningPeriod.getCurrentPhaseName();
                break;
            }
        }
        return phaseName;
    }

    private String getHeaderName(String query, Interval interval) {
        if(query.equals(DAILY)) {
            return DayOfWeek.of(interval.getStart().getDayOfWeek()).toString();
        } else {
            return getTitle(query, interval);
        }
    }

    public TimeBankCTADistributionDTO getDistributionOfTimeBank(Map<String, Integer> ctaDistributionMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO,long plannedMinutesOfTimebank) {
        List<CTADistributionDTO> timeBankCTADistributionDTOS = new ArrayList<>();
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : unitPositionWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if(ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
                if(ctaRuleTemplate.getCalculationFor().equals(BONUS_HOURS) || ctaRuleTemplate.getCalculationFor().equals(FUNCTIONS)) {
                    CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0));
                    timeBankCTADistributionDTOS.add(ctaDistributionDTO);
                    ctaBonusMinutes += ctaDistributionDTO.getMinutes();
                } else if(ctaRuleTemplate.getCalculationFor().equals(SCHEDULED_HOURS)) {
                    scheduledCTADistributions.add(new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0)));
                }
            }
        }
        return new TimeBankCTADistributionDTO(scheduledCTADistributions, new CTARuletemplateBonus(timeBankCTADistributionDTOS, ctaBonusMinutes),plannedMinutesOfTimebank);
    }

    private String getTitle(String query, Interval interval) {
        switch (query) {
            case DAILY:
                return interval.getStart().toLocalDate().toString();
            case WEEKLY:
                return StringUtils.capitalize(WEEKLY) + " " + interval.getStart().getWeekOfWeekyear();
            case MONTHLY:
                return interval.getStart().monthOfYear().getAsText();
            case ANNUALLY:
                return StringUtils.capitalize(AppConstants.YEAR) + " " + interval.getStart().getYear();
            case QUATERLY:
                return StringUtils.capitalize(AppConstants.QUARTER) + " " + getQuaterNumberByDate(interval.getStart());//(interval.getStart().dayOfMonth().withMinimumValue().equals(interval.getStart()) ? interval.getStart().getMonthOfYear() / 3 : (interval.getStart().getMonthOfYear() / 3) + 1);
            //case "ByPeriod": return getActualTimeBankByPeriod(startDate,endDate,shifts);
        }
        return "";
    }

    private int getQuaterNumberByDate(DateTime dateTime) {
        int quater = (int) Math.ceil((double) dateTime.getMonthOfYear() / 3);
        return quater;
    }

    private ScheduleTimeByTimeTypeDTO getWorkingTimeType(Interval interval, List<ShiftWithActivityDTO> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
        List<ScheduleTimeByTimeTypeDTO> parentTimeTypes = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if(timeType.getTimeTypes().equals(TimeTypes.WORKING_TYPE.toValue()) && timeType.getUpperLevelTimeTypeId() == null) {
                ScheduleTimeByTimeTypeDTO parentTimeType = new ScheduleTimeByTimeTypeDTO(0);
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                parentTimeType.setChildren(children);
                parentTimeType.setName(timeType.getLabel());
                parentTimeType.setTimeTypeId(timeType.getId());
                if(!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                }
                if(shifts != null && !shifts.isEmpty()) {
                    for (ShiftWithActivityDTO shift : shifts) {
                        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                            if(timeType.getId().equals(shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.contains(shift.getStartDate().getTime())) {
                                totalScheduledMin += shiftActivity.getScheduledMinutes();
                            }
                        }

                    }
                }
                parentTimeType.setTotalMin(totalScheduledMin);
                parentTimeType.setTotalMin(children.stream().mapToInt(c -> c.getTotalMin()).sum());
                parentTimeTypes.add(parentTimeType);
            }
        });
        scheduleTimeByTimeTypeDTO.setTotalMin(parentTimeTypes.stream().mapToInt(t -> t.getTotalMin()).sum());
        scheduleTimeByTimeTypeDTO.setChildren(parentTimeTypes);
        return scheduleTimeByTimeTypeDTO;
    }

    private List<ScheduleTimeByTimeTypeDTO> getTimeTypeDTOS(BigInteger timeTypeId, Interval interval, List<ShiftWithActivityDTO> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        List<ScheduleTimeByTimeTypeDTO> scheduleTimeByTimeTypeDTOS = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if(timeType.getUpperLevelTimeTypeId() != null && timeType.getUpperLevelTimeTypeId().equals(timeTypeId)) {
                ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
                scheduleTimeByTimeTypeDTO.setTimeTypeId(timeType.getId());
                scheduleTimeByTimeTypeDTO.setName(timeType.getLabel());
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                scheduleTimeByTimeTypeDTO.setChildren(children);
                if(!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                }
                if(isCollectionNotEmpty(shifts)) {
                    for (ShiftWithActivityDTO shift : shifts) {
                        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                            if(timeType.getId().equals(shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.contains(shift.getStartDate().getTime())) {
                                totalScheduledMin += shiftActivity.getScheduledMinutes();
                            }
                        }
                    }
                    scheduleTimeByTimeTypeDTO.setTotalMin(totalScheduledMin);
                }
                scheduleTimeByTimeTypeDTOS.add(scheduleTimeByTimeTypeDTO);
            }
        });
        return scheduleTimeByTimeTypeDTOS;
    }

    private Map<Interval, List<DailyTimeBankEntry>> getTimebankIntervalsMap(List<Interval> intervals, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> timeBanksIntervalMap.put(i, getTimeBanksByInterval(i, dailyTimeBankEntries)));
        return timeBanksIntervalMap;
    }

    private List<DailyTimeBankEntry> getTimeBanksByInterval(Interval interval, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        List<DailyTimeBankEntry> dailyTimeBanks1Entry = new ArrayList<>();
        dailyTimeBankEntries.forEach(tb -> {
            if(interval.contains(asDate(tb.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate()))) {
                dailyTimeBanks1Entry.add(tb);
            }
        });
        return dailyTimeBanks1Entry;
    }

    private Object[] getShiftsIntervalMap(List<Interval> intervals, List<ShiftWithActivityDTO> shifts, List<PayOutPerShift> payOutPerShifts) {
        Map<Interval, List<ShiftWithActivityDTO>> shiftsintervalMap = new HashMap<>(intervals.size());
        Map<Interval, List<PayOutPerShift>> payOutsintervalMap = new HashMap<>(intervals.size());
        intervals.forEach(interval -> {
            Object[] objects = getShiftsByDate(interval, shifts, payOutPerShifts);
            shiftsintervalMap.put(interval, (List<ShiftWithActivityDTO>) objects[0]);
            payOutsintervalMap.put(interval, (List<PayOutPerShift>) objects[1]);
        });
        return new Object[]{shiftsintervalMap, payOutsintervalMap};
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
                    nextEndDay = startDateTime.getDayOfWeek() == 1 ? startDateTime.plusWeeks(1) : startDateTime.withDayOfWeek(DateTimeConstants.MONDAY).plusWeeks(1);
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
            intervals.add(new Interval(startDateTime, nextEndDay.isAfter(endDateTime) ? endDateTime : nextEndDay));
            startDateTime = nextEndDay;
        }
        if(!startDateTime.equals(endDateTime) && startDateTime.isBefore(endDateTime)) {
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

    //Calculating Time Bank Against Open Shift
    public int[] calculateDailyTimeBankForOpenShift(OpenShift shift, Activity activity, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        int totalTimebank = 0;
        int plannedTimeMin = 0;
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        Interval shiftInterval = new Interval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() ? unitPositionWithCtaDetailsDTO.getTotalWeeklyMinutes() / unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() : 0;
        for (CTARuleTemplateDTO ruleTemplate : unitPositionWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if(ruleTemplate.getPlannedTimeWithFactor().getAccountType() == null) continue;
            if(ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
                int ctaTimeBankMin = 0;
                if((ruleTemplate.getActivityIds().contains(shift.getActivityId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(activity.getBalanceSettingsActivityTab().getTimeTypeId())))) {
                    if(((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(DateUtils.toLocalDate(shiftInterval.getStart()))))) {
                        if(ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS)) {
                            plannedTimeMin += calculateScheduleAndDurationHourForOpenShift(shift, activity, unitPositionWithCtaDetailsDTO);
                            totalTimebank += plannedTimeMin;
                        }
                        if(ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                            for (CompensationTableInterval ctaIntervalDTO : ruleTemplate.getCompensationTable().getCompensationTableInterval()) {
                                List<Interval> ctaIntervals = getCTAInterval(ctaIntervalDTO, startDate);
                                for (Interval ctaInterval : ctaIntervals) {
                                    if(ctaInterval.overlaps(shiftInterval)) {
                                        int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).toDuration().getStandardMinutes();
                                        if(ctaIntervalDTO.getCompensationMeasurementType().equals(AppConstants.MINUTES)) {
                                            ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) * ctaIntervalDTO.getValue();
                                            totalTimebank += ctaTimeBankMin;
                                            plannedTimeMin += ctaTimeBankMin;
                                            break;
                                        } else if(ctaIntervalDTO.getCompensationMeasurementType().equals(AppConstants.PERCENT)) {
                                            ctaTimeBankMin += (int) (((double) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) / 100) * ctaIntervalDTO.getValue());
                                            totalTimebank += ctaTimeBankMin;
                                            plannedTimeMin += ctaTimeBankMin;
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        totalTimebank = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() ? totalTimebank - contractualMin : totalTimebank;
        return new int[]{plannedTimeMin, totalTimebank};
    }

    //Calculating schedule Minutes for Open Shift
    private int calculateScheduleAndDurationHourForOpenShift(OpenShift openShift, Activity activity, UnitPositionWithCtaDetailsDTO unitPosition) {
        int scheduledMinutes = 0;
        int duration;
        int weeklyMinutes;
        switch (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) {
            case ENTERED_MANUALLY:
                duration = (int) MINUTES.between(DateUtils.asLocalTime(openShift.getStartDate()), DateUtils.asLocalTime(openShift.getStartDate()));
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case FIXED_TIME:
                duration = activity.getTimeCalculationActivityTab().getFixedTimeValue().intValue();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case ENTERED_TIMES:
                duration = (int) new Interval(openShift.getStartDate().getTime(), openShift.getEndDate().getTime()).toDuration().getStandardMinutes();
                scheduledMinutes = new Double(duration * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                break;
            case AppConstants.FULL_DAY_CALCULATION:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullDayCalculationType())) ? unitPosition.getFullTimeWeeklyMinutes() : unitPosition.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = new Double(unitPosition.getTotalWeeklyMinutes() * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullWeekCalculationType())) ? unitPosition.getFullTimeWeeklyMinutes() : unitPosition.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
        }
        return scheduledMinutes;
    }

    public Map<java.time.LocalDate, TimeBankByDateDTO> getAccumulatedTimebankDTO(Set<DateTimeInterval> planningPeriodIntervals, List<DailyTimeBankEntry> dailyTimeBankEntries, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        long accumulatedTimebank = 0;
        java.time.LocalDate unitPositionStartDate = unitPositionWithCtaDetailsDTO.getStartDate();
        Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(k -> k.getDate(), v -> v));
        Map<java.time.LocalDate, TimeBankByDateDTO> localDateTimeBankByDateDTOMap = new HashMap<>();
        endDate = isNull(unitPositionWithCtaDetailsDTO.getEndDate()) ? endDate : endDate.isBefore(unitPositionWithCtaDetailsDTO.getEndDate()) ? endDate : unitPositionWithCtaDetailsDTO.getEndDate();
        while (unitPositionStartDate.isBefore(endDate) || unitPositionStartDate.equals(endDate)) {
            TimeBankByDateDTO timeBankByDateDTO = new TimeBankByDateDTO();
            int totalTimeBankMinutes;
            if(dateDailyTimeBankEntryMap.containsKey(unitPositionStartDate)) {
                DailyTimeBankEntry dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(unitPositionStartDate);
                totalTimeBankMinutes = dailyTimeBankEntry.getDeltaTimeBankMinutes();
                accumulatedTimebank += dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes();
            } else {
                totalTimeBankMinutes = (-getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals, unitPositionStartDate, unitPositionWithCtaDetailsDTO.getPositionLines()));
            }
            if(unitPositionStartDate.equals(unitPositionWithCtaDetailsDTO.getAccumulatedTimebankDate())) {
                accumulatedTimebank = unitPositionWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
            }
            timeBankByDateDTO.setAccumulatedTimebankMinutes(accumulatedTimebank);
            timeBankByDateDTO.setTimeBankChangeMinutes(totalTimeBankMinutes);
            if(unitPositionStartDate.isAfter(startDate) || startDate.equals(unitPositionStartDate)) {
                localDateTimeBankByDateDTOMap.put(unitPositionStartDate, timeBankByDateDTO);
            }
            unitPositionStartDate = unitPositionStartDate.plusDays(1);
        }
        return localDateTimeBankByDateDTOMap;
    }

    private BigDecimal getHourlyCostByDate(List<com.kairos.dto.user.employment.UnitPositionLinesDTO> positionLines, java.time.LocalDate localDate) {
        BigDecimal hourlyCost = new BigDecimal(0);
        for (com.kairos.dto.user.employment.UnitPositionLinesDTO positionLine : positionLines) {
            DateTimeInterval positionInterval = positionLine.getInterval();
            if((positionInterval == null && (positionLine.getStartDate().equals(localDate) || positionLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(asDate(localDate)) || positionLine.getEndDate().equals(localDate)))) {
                hourlyCost = positionLine.getHourlyCost();
                break;
            }
        }
        return hourlyCost;
    }

    private long[] getSumOfTimebankValues(List<DailyTimeBankEntry> dailyTimeBankEntries) {
        long calculatedTimeBank = 0l;
        long plannedMinutesOfTimebank = 0l;
        long scheduledMinutes = 0l;
        for (DailyTimeBankEntry dailyTimeBankEntry : dailyTimeBankEntries) {
            calculatedTimeBank += dailyTimeBankEntry.getDeltaTimeBankMinutes();
            plannedMinutesOfTimebank += dailyTimeBankEntry.getPlannedMinutesOfTimebank();
            scheduledMinutes += dailyTimeBankEntry.getScheduledMinutesOfTimeBank();
        }
        return new long[]{calculatedTimeBank, plannedMinutesOfTimebank, scheduledMinutes};

    }

    private long[] getSumOfPayoutValues(List<PayOutPerShift> payOutPerShifts) {
        long plannedMinutesOfPayout = 0l;
        long scheduledMinutesOfPayout = 0l;
        for (PayOutPerShift payOutPerShift : payOutPerShifts) {
            scheduledMinutesOfPayout += payOutPerShift.getScheduledMinutes();
            plannedMinutesOfPayout += payOutPerShift.getCtaBonusMinutesOfPayOut() + payOutPerShift.getScheduledMinutes();
        }
        return new long[]{plannedMinutesOfPayout, scheduledMinutesOfPayout};

    }
}
