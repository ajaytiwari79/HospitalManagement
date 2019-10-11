package com.kairos.service.time_bank;

import com.google.common.collect.Lists;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta.CompensationTableInterval;
import com.kairos.dto.activity.pay_out.PayOutDTO;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.time_bank.*;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.ScheduledActivitiesDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.ACTIVITY_END_DATE_LESS_THAN_START_DATE;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.PAID_OUT;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;
import static com.kairos.enums.phase.PhaseDefaultName.PAYROLL;
import static com.kairos.enums.phase.PhaseDefaultName.REALTIME;
import static com.kairos.enums.phase.PhaseDefaultName.*;
import static com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService.getCutoffInterval;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.*;

/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018
 *
 * */
@Service
public class TimeBankCalculationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankCalculationService.class);

    @Inject
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PayOutRepository payOutRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private PayOutTransactionMongoRepository payOutTransactionMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;
    @Inject
    private TimeBankService timeBankService;


    public DailyTimeBankEntry calculateDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, DailyTimeBankEntry dailyTimeBankEntry, DateTimeInterval planningPeriodInterval, List<DayTypeDTO> dayTypeDTOS, boolean validatedByPlanner) {
        boolean anyShiftPublish = false;
        int contractualMinutes = getContractualMinutesByDate(planningPeriodInterval, dateTimeInterval.getStartLocalDate(), staffAdditionalInfoDTO.getEmployment().getEmploymentLines());
        if (isCollectionNotEmpty(shifts)) {
            Map<Long, DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            CalculatePlannedHoursAndScheduledHours calculatePlannedHoursAndScheduledHours = new CalculatePlannedHoursAndScheduledHours(staffAdditionalInfoDTO, dateTimeInterval, shifts, validatedByPlanner, anyShiftPublish, dayTypeDTOMap).calculate();
            anyShiftPublish = calculatePlannedHoursAndScheduledHours.isAnyShiftPublish();
            int totalDailyPlannedMinutes = calculatePlannedHoursAndScheduledHours.getTotalDailyPlannedMinutes();
            int scheduledMinutesOfTimeBank = calculatePlannedHoursAndScheduledHours.getScheduledMinutesOfTimeBank();
            int totalPublishedDailyPlannedMinutes = calculatePlannedHoursAndScheduledHours.getTotalPublishedDailyPlannedMinutes();
            Map<BigInteger, Integer> ctaTimeBankMinMap = calculatePlannedHoursAndScheduledHours.ctaTimeBankMinMap;
            dailyTimeBankEntry = updateDailyTimeBankEntry(staffAdditionalInfoDTO.getEmployment(), dateTimeInterval, dailyTimeBankEntry, anyShiftPublish, contractualMinutes, totalDailyPlannedMinutes, scheduledMinutesOfTimeBank, totalPublishedDailyPlannedMinutes, ctaTimeBankMinMap);
        } else if (isNotNull(dailyTimeBankEntry)) {
            resetDailyTimebankEntry(dailyTimeBankEntry, contractualMinutes);
        }
        if (isNotNull(dailyTimeBankEntry)) {
            updatePublishedBalances(dailyTimeBankEntry, staffAdditionalInfoDTO.getEmployment().getEmploymentLines(), staffAdditionalInfoDTO.getUnitId(), dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes());
        }
        return dailyTimeBankEntry;
    }


    private DailyTimeBankEntry updateDailyTimeBankEntry(StaffEmploymentDetails staffEmploymentDetails, DateTimeInterval dateTimeInterval, DailyTimeBankEntry dailyTimeBankEntry, boolean anyShiftPublish, int contractualMinutes, int totalDailyPlannedMinutes, int scheduledMinutesOfTimeBank, int totalPublishedDailyPlannedMinutes, Map<BigInteger, Integer> ctaTimeBankMinMap) {
        dailyTimeBankEntry = isNullOrElse(dailyTimeBankEntry, new DailyTimeBankEntry(staffEmploymentDetails.getId(), staffEmploymentDetails.getStaffId(), dateTimeInterval.getStartLocalDate()));
        int deltaTimeBankMinutes = totalDailyPlannedMinutes - contractualMinutes;
        int timeBankMinWithoutCta = scheduledMinutesOfTimeBank - contractualMinutes;
        dailyTimeBankEntry.setStaffId(staffEmploymentDetails.getStaffId());
        dailyTimeBankEntry.setTimeBankMinutesWithoutCta(timeBankMinWithoutCta);
        dailyTimeBankEntry.setPlannedMinutesOfTimebank(totalDailyPlannedMinutes);
        int deltaAccumulatedTimebankMinutes = anyShiftPublish ? (totalPublishedDailyPlannedMinutes - contractualMinutes) : 0;
        dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(deltaAccumulatedTimebankMinutes + dailyTimeBankEntry.getProtectedDaysOffMinutes());
        dailyTimeBankEntry.setCtaBonusMinutesOfTimeBank(ctaTimeBankMinMap.values().stream().mapToInt(ctaBonus -> ctaBonus).sum());
        dailyTimeBankEntry.setPublishedSomeActivities(anyShiftPublish);
        dailyTimeBankEntry.setContractualMinutes(contractualMinutes);
        dailyTimeBankEntry.setScheduledMinutesOfTimeBank(scheduledMinutesOfTimeBank);
        dailyTimeBankEntry.setDeltaTimeBankMinutes(deltaTimeBankMinutes);
        dailyTimeBankEntry.setTimeBankCTADistributionList(getCTADistributionsOfTimebank(staffEmploymentDetails.getCtaRuleTemplates(), ctaTimeBankMinMap));
        dailyTimeBankEntry.setDraftDailyTimeBankEntry(null);
        return dailyTimeBankEntry;
    }

    public void resetDailyTimebankEntry(DailyTimeBankEntry dailyTimeBankEntry, int contractualMinutes) {
        dailyTimeBankEntry.setTimeBankMinutesWithoutCta(0);
        dailyTimeBankEntry.setPlannedMinutesOfTimebank(0);
        dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(dailyTimeBankEntry.getProtectedDaysOffMinutes());
        dailyTimeBankEntry.setCtaBonusMinutesOfTimeBank(0);
        dailyTimeBankEntry.setContractualMinutes(contractualMinutes);
        dailyTimeBankEntry.setScheduledMinutesOfTimeBank(0);
        dailyTimeBankEntry.setDeltaTimeBankMinutes(-contractualMinutes);
        dailyTimeBankEntry.setPublishedSomeActivities(false);
        dailyTimeBankEntry.setTimeBankCTADistributionList(new ArrayList<>());
        dailyTimeBankEntry.setDraftDailyTimeBankEntry(null);
    }

    private Double calculateBonusAndUpdateShiftActivity(DateTimeInterval dateTimeInterval, Map<BigInteger, Integer> ctaTimeBankMinMap, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivity, DateTimeInterval shiftInterval) {
        Double ctaBonusMinutes = 0.0;
        if (isNotNull(shiftInterval)) {
            ctaBonusMinutes = calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval);
        }
        return ctaBonusMinutes;
    }

    public int getFunctionalBonusCompensation(StaffEmploymentDetails staffEmploymentDetails, CTARuleTemplateDTO ctaRuleTemplateDTO, DateTimeInterval dateTimeInterval) {
        int value = 0;
        Long functionId = null;
        if (isNull(staffEmploymentDetails.getFunctionId())) {
            Optional<FunctionDTO> appliedFunctionDTO = staffEmploymentDetails.getAppliedFunctions().stream().filter(function -> function.getAppliedDates().contains(dateTimeInterval.getStartLocalDate())).findFirst();
            functionId = appliedFunctionDTO.isPresent() ? appliedFunctionDTO.get().getId() : null;
        }
        if (ctaRuleTemplateDTO.getStaffFunctions().contains(isNotNull(staffEmploymentDetails.getFunctionId()) ? staffEmploymentDetails.getFunctionId() : functionId)) {
            value = getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate()).equals(new BigDecimal(0)) ? new BigDecimal(ctaRuleTemplateDTO.getCalculateValueAgainst().getFixedValue().getAmount()).divide(staffEmploymentDetails.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(60)).intValue() : 0;
        }
        return value;
    }

    public Double calculateCTARuleTemplateBonus(CTARuleTemplateDTO ctaRuleTemplateDTO, DateTimeInterval dateTimeInterval, DateTimeInterval shiftDateTimeInterval) {
        Double ctaTimeBankMin = 0.0;
        if (isNotNull(shiftDateTimeInterval)) {
            Interval shiftInterval = new Interval(shiftDateTimeInterval.getStartDate().getTime(), shiftDateTimeInterval.getEndDate().getTime());
            LOGGER.debug("rule template : {} shiftInterval {}", ctaRuleTemplateDTO.getId(), shiftInterval);
            for (CompensationTableInterval ctaInterval : ctaRuleTemplateDTO.getCompensationTable().getCompensationTableInterval()) {
                List<Interval> intervalOfCTAs = getCTAInterval(ctaInterval, new DateTime(dateTimeInterval.getStartDate()));
                LOGGER.debug("rule template : {} interval size {}", ctaRuleTemplateDTO.getId(), intervalOfCTAs);
                for (Interval intervalOfCTA : intervalOfCTAs) {
                    if (intervalOfCTA.overlaps(shiftInterval)) {
                        int overlapTimeInMin = (int) intervalOfCTA.overlap(shiftInterval).toDuration().getStandardMinutes();
                        if (ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.MINUTES)) {
                            ctaTimeBankMin += ((double) overlapTimeInMin / ctaRuleTemplateDTO.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                            break;
                        } else if (ctaInterval.getCompensationMeasurementType().equals(CompensationMeasurementType.PERCENT)) {
                            ctaTimeBankMin += ((double) Math.round((double) overlapTimeInMin / ctaRuleTemplateDTO.getCompensationTable().getGranularityLevel()) / 100) * ctaInterval.getValue();
                            break;
                        }

                    }
                }
            }
        }
        return ctaTimeBankMin;
    }

    public int getContractualMinutesByDate(DateTimeInterval planningPeriodInterval, java.time.LocalDate localDate, List<EmploymentLinesDTO> employmentLines) {
        Date date = asDate(localDate);
        int contractualMinutes = 0;
        if (CollectionUtils.isNotEmpty(employmentLines)) {
            if (planningPeriodInterval.contains(date) || planningPeriodInterval.getEndLocalDate().equals(localDate)) {
                for (EmploymentLinesDTO employmentLine : employmentLines) {
                    DateTimeInterval positionInterval = employmentLine.getInterval();
                    if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(date) || employmentLine.getEndDate().equals(localDate)))) {
                        contractualMinutes = localDate.getDayOfWeek().getValue() <= employmentLine.getWorkingDaysInWeek() ? employmentLine.getTotalWeeklyMinutes() / employmentLine.getWorkingDaysInWeek() : 0;
                        break;
                    }
                }
            }
        }
        return contractualMinutes;
    }

    public void calculateScheduledAndDurationInMinutes(ShiftActivity shiftActivity, Activity activity, StaffEmploymentDetails staffEmploymentDetails) {
        if (shiftActivity.getStartDate().after(shiftActivity.getEndDate())) {
            exceptionService.invalidRequestException(ACTIVITY_END_DATE_LESS_THAN_START_DATE, activity.getName());
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
            case CommonConstants.FULL_DAY_CALCULATION:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullDayCalculationType())) ? staffEmploymentDetails.getFullTimeWeeklyMinutes() : staffEmploymentDetails.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = new Double(staffEmploymentDetails.getTotalWeeklyMinutes() * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case CommonConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullWeekCalculationType())) ? staffEmploymentDetails.getFullTimeWeeklyMinutes() : staffEmploymentDetails.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            default:
                break;
        }
        if (TimeTypes.WORKING_TYPE.toString().equals(shiftActivity.getTimeType())) {
            shiftActivity.setDurationMinutes(duration);
            shiftActivity.setScheduledMinutes(scheduledMinutes);
        }
    }

    public boolean validateCTARuleTemplate(Map<Long, DayTypeDTO> dayTypeDTOMap, CTARuleTemplateDTO ctaRuleTemplateDTO, StaffEmploymentDetails staffEmploymentDetails, BigInteger shiftPhaseId, BigInteger activityId, BigInteger timeTypeId, Date shiftDate, List<PlannedTime> plannedTimes) {
        return ctaRuleTemplateDTO.isRuleTemplateValid(staffEmploymentDetails.getEmploymentType().getId(), shiftPhaseId, activityId, timeTypeId, plannedTimes) && isDayTypeValid(shiftDate, ctaRuleTemplateDTO, dayTypeDTOMap);
    }

    private boolean isDayTypeValid(Date shiftDate, CTARuleTemplateDTO ruleTemplateDTO, Map<Long, DayTypeDTO> dayTypeDTOMap) {
        List<DayTypeDTO> dayTypeDTOS = ruleTemplateDTO.getDayTypeIds().stream().map(daytypeId -> dayTypeDTOMap.get(daytypeId)).collect(Collectors.toList());
        boolean valid = false;
        for (DayTypeDTO dayTypeDTO : dayTypeDTOS) {
            if (dayTypeDTO.isHolidayType()) {
                for (CountryHolidayCalenderDTO countryHolidayCalenderDTO : dayTypeDTO.getCountryHolidayCalenderData()) {
                    DateTimeInterval dateTimeInterval;
                    if (dayTypeDTO.isAllowTimeSettings()) {
                        LocalTime holidayEndTime = countryHolidayCalenderDTO.getEndTime().get(ChronoField.MINUTE_OF_DAY) == 0 ? LocalTime.MAX : countryHolidayCalenderDTO.getEndTime();
                        dateTimeInterval = new DateTimeInterval(asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getStartTime()), asDate(countryHolidayCalenderDTO.getHolidayDate(), holidayEndTime));
                    } else {
                        dateTimeInterval = new DateTimeInterval(asDate(countryHolidayCalenderDTO.getHolidayDate()), asDate(countryHolidayCalenderDTO.getHolidayDate().plusDays(1)));
                    }
                    valid = dateTimeInterval.contains(shiftDate);
                    if (valid) {
                        break;
                    }
                }
            } else {
                valid = ruleTemplateDTO.getDays() != null && ruleTemplateDTO.getDays().contains(asLocalDate(shiftDate).getDayOfWeek());
            }
            if (valid) {
                break;
            }
        }
        return valid;
    }

    private List<Interval> getCTAInterval(CompensationTableInterval interval, DateTime startDate) {
        List<Interval> ctaIntervals = new ArrayList<>(2);
        if (interval.getFrom().isAfter(interval.getTo())) {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay().plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay().plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.withTimeAtStartOfDay().plusDays(1)));
        } else if (interval.getFrom().equals(interval.getTo())) {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay(), startDate.withTimeAtStartOfDay().plusDays(1)));
        } else {
            ctaIntervals.add(new Interval(startDate.withTimeAtStartOfDay().plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.withTimeAtStartOfDay().plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
        }
        return ctaIntervals;
    }

    private List<TimeBankCTADistribution> getCTADistributionsOfTimebank(List<CTARuleTemplateDTO> ctaRuleTemplateCalulatedTimeBankDTOS, Map<BigInteger, Integer> ctaTimeBankMinMap) {
        List<TimeBankCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalulatedTimeBankDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateCalulatedTimeBankDTOS) {
            if (ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
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
            if (interval.contains(shift.getStartDate().getTime()) || interval.contains(shift.getEndDate().getTime())) {
                shiftWithActivityDTOS.add(shift);
                if (payOutPerShiftMap.containsKey(shift.getId())) {
                    intervalPayOutPerShifts.add(payOutPerShiftMap.get(shift.getId()));
                }
            }
        });
        return new Object[]{shiftWithActivityDTOS, intervalPayOutPerShifts};
    }

    public TimeBankAndPayoutDTO getTimeBankAdvanceView(List<Interval> intervals, Long unitId, long totalTimeBankBeforeStartDate, Date startDate, Date endDate, String query, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, List<TimeTypeDTO> timeTypeDTOS, Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap) {
        TimeBankDTO timeBankDTO = new TimeBankDTO(startDate, asDate(DateUtils.asLocalDate(endDate).minusDays(1)), employmentWithCtaDetailsDTO, employmentWithCtaDetailsDTO.getStaffId(), employmentWithCtaDetailsDTO.getId(), employmentWithCtaDetailsDTO.getTotalWeeklyMinutes(), employmentWithCtaDetailsDTO.getWorkingDaysInWeek());
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        List<PayOutPerShift> payOutPerShifts = payOutRepository.findAllByEmploymentAndDate(employmentWithCtaDetailsDTO.getId(), startDate, endDate);
        Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap = getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        Object[] objects = getShiftsIntervalMap(intervals, shifts, payOutPerShifts);
        Map<Interval, List<ShiftWithActivityDTO>> shiftsintervalMap = (Map<Interval, List<ShiftWithActivityDTO>>) objects[0];
        Map<Interval, List<PayOutPerShift>> payOutsintervalMap = (Map<Interval, List<PayOutPerShift>>) objects[1];
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = getTimeBankIntervals(unitId, startDate, endDate, totalTimeBankBeforeStartDate, query, intervals, shiftsintervalMap, timeBanksIntervalMap, timeTypeDTOS, employmentWithCtaDetailsDTO, payoutTransactionIntervalMap, payOutsintervalMap);
        Lists.reverse(timeBankIntervalDTOS);
        timeBankDTO.setTimeIntervals(timeBankIntervalDTOS);
        List<CTADistributionDTO> scheduledCTADistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistribution().getScheduledCTADistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = scheduledCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getName(), Collectors.summingInt(tb -> tb.getMinutes())));
        scheduledCTADistributions = getCTADistributionsOfTimebank(ctaDistributionMap, employmentWithCtaDetailsDTO);
        List<CTADistributionDTO> ctaBonusDistributions = timeBankIntervalDTOS.stream().flatMap(ti -> ti.getTimeBankDistribution().getCtaRuletemplateBonus().getCtaDistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaBonusDistributionMap = ctaBonusDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getName(), Collectors.summingInt(tb -> tb.getMinutes())));
        long  protectedDaysOffMinutes=dailyTimeBankEntries.stream().collect(Collectors.summingLong(dailyTimeBankEntry -> dailyTimeBankEntry.getProtectedDaysOffMinutes()));
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
        timeBankDTO.setTimeBankDistribution(new TimeBankCTADistributionDTO(scheduledCTADistributions, getCTABonusDistributions(ctaBonusDistributionMap, employmentWithCtaDetailsDTO.getCtaRuleTemplates()), plannedMinutesOfTimebank));
        timeBankDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
        timeBankDTO.setTotalContractedMin(totalContractedMin);
        timeBankDTO.setTotalTimeBankMin(totalTimeBank - approvePayOut);
        timeBankDTO.setTotalTimeBankAfterCtaMin(totalTimeBankAfterCtaMin - approvePayOut);
        timeBankDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBeforeCtaMin);
        timeBankDTO.setTotalScheduledMin(totalScheduledMin);
        timeBankDTO.setTotalTimeBankDiff(totalTimeBankDiff);
        timeBankDTO.setTotalPlannedMinutes(totalPlannedMinutes);
        timeBankDTO.setProtectedDaysOffMinutes(protectedDaysOffMinutes);
        List<PayOutPerShift> payOutPerShiftBeforestartDate = payOutRepository.findAllByEmploymentAndBeforeDate(employmentWithCtaDetailsDTO.getId(), startDate);
        long payoutMinutesBefore = isCollectionNotEmpty(payOutPerShiftBeforestartDate) ? payOutPerShiftBeforestartDate.stream().mapToLong(payout -> payout.getTotalPayOutMinutes()).sum() : 0;
        PayOutDTO payOut = payOutCalculationService.getAdvanceViewPayout(intervals, payOutPerShifts, payoutMinutesBefore, payoutTransactionIntervalMap, employmentWithCtaDetailsDTO, query);
        return new TimeBankAndPayoutDTO(timeBankDTO, payOut);
    }

    private List<CTADistributionDTO> getCTADistributionsOfTimebank(Map<String, Integer> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        employmentWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            if (cta.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && cta.getCalculationFor().equals(SCHEDULED_HOURS)) {
                scheduledCTADistributions.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getName(), 0)));

            }
        });
        return scheduledCTADistributions;
    }

    private CTARuletemplateBonus getCTABonusDistributions(Map<String, Integer> ctaDistributionMap, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS) {
        List<CTADistributionDTO> ctaBonusDistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : ctaRuleTemplateDTOS) {
            if (ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && (ctaRuleTemplate.getCalculationFor().equals(BONUS_HOURS) || ctaRuleTemplate.getCalculationFor().equals(FUNCTIONS))) {
                CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0));
                ctaBonusDistributions.add(ctaDistributionDTO);
                ctaBonusMinutes += ctaDistributionDTO.getMinutes();
            }
        }
        return new CTARuletemplateBonus(ctaBonusDistributions, ctaBonusMinutes);
    }

    public Map<Interval, List<PayOutTransaction>> getPayoutTrasactionIntervalsMap(List<Interval> intervals, Date startDate, Date endDate, Long employmentId) {
        List<PayOutTransaction> payOutTransactions = payOutTransactionMongoRepository.findAllByEmploymentIdAndDate(employmentId, startDate, endDate);
        Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(interval -> payoutTransactionAndIntervalMap.put(interval, getPayoutTransactionsByInterval(interval, payOutTransactions)));
        return payoutTransactionAndIntervalMap;
    }

    private List<PayOutTransaction> getPayoutTransactionsByInterval(Interval interval, List<PayOutTransaction> payOutTransactions) {
        List<PayOutTransaction> payOutTransactionList = new ArrayList<>();
        payOutTransactions.forEach(payOutTransaction -> {
            if (interval.contains(asDate(payOutTransaction.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(payOutTransaction.getDate()))) {
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
            plannedMinutesOfTimebank += timeBankIntervalDTO.getTimeBankDistribution().getPlannedMinutesOfTimebank();
        }
        if (!timeBankIntervalDTOS.isEmpty()) {
            totalTimeBankBeforeCtaMin = timeBankIntervalDTOS.get(timeBankIntervalDTOS.size() - 1).getTotalTimeBankBeforeCtaMin();
            totalTimeBankAfterCtaMin = totalTimeBankBeforeCtaMin + totalTimeBankDiff;
        }
        return new long[]{totalContractedMin, totalScheduledMin, totalTimeBankAfterCtaMin, totalTimeBankBeforeCtaMin, totalTimeBankDiff, totalTimeBank, requestPayOut, paidPayOut, approvePayOut, totalPlannedMinutes, plannedMinutesOfTimebank};

    }

    public TimeBankDTO getTimeBankOverview(Long unitId, Long employmentId, Date startDate, Date endDate, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<Interval> intervals = getAllIntervalsBetweenDates(startDate, endDate, WEEKLY);
        Map<Interval, List<DailyTimeBankEntry>> intervalTimeBankMap = getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap = getPayoutTrasactionIntervalsMap(intervals, startDate, endDate, employmentId);
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setEmploymentId(employmentId);
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
        Interval interval = getIntervalValidIntervalForTimebank(employmentWithCtaDetailsDTO, new Interval(startDate.getTime(), endDate.getTime()), planningPeriodInterval);
        List<TimeBankIntervalDTO> weeklyTimeBankIntervals = new ArrayList<>();
        if (isNotNull(interval)) {
            weeklyTimeBankIntervals = getTimeBankIntervals(unitId, startDate, endDate, 0, WEEKLY, intervals, new HashMap<>(), intervalTimeBankMap, null, employmentWithCtaDetailsDTO, payoutTransactionIntervalMap, new HashMap<>());
            weeklyTimeBankIntervals = weeklyTimeBankIntervals.stream().filter(timeBankIntervalDTO -> interval.contains(timeBankIntervalDTO.getStartDate().getTime()) || interval.contains(timeBankIntervalDTO.getEndDate().getTime())).collect(Collectors.toList());
        }
        timeBankDTO.setWeeklyIntervalsTimeBank(weeklyTimeBankIntervals);
        long[] calculatedTimebankValues = getSumOfTimebankIntervalValues(weeklyTimeBankIntervals);
        timeBankDTO.setTotalContractedMin(calculatedTimebankValues[0]);
        timeBankDTO.setTotalScheduledMin(calculatedTimebankValues[1]);
        timeBankDTO.setTotalTimeBankMin(calculatedTimebankValues[4]);
        timeBankDTO.setTotalPlannedMinutes(calculatedTimebankValues[10]);
        intervals = getAllIntervalsBetweenDates(startDate, endDate, MONTHLY);
        intervalTimeBankMap = getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        payoutTransactionIntervalMap = getPayoutTrasactionIntervalsMap(intervals, startDate, endDate, employmentId);
        List<TimeBankIntervalDTO> monthlyTimeBankIntervals = new ArrayList<>();
        if (isNotNull(interval)) {
            monthlyTimeBankIntervals = getTimeBankIntervals(unitId, startDate, endDate, 0, MONTHLY, intervals, new HashMap<>(), intervalTimeBankMap, null, employmentWithCtaDetailsDTO, payoutTransactionIntervalMap, new HashMap<>());
            monthlyTimeBankIntervals = monthlyTimeBankIntervals.stream().filter(timeBankIntervalDTO -> interval.contains(timeBankIntervalDTO.getStartDate().getTime()) || interval.contains(timeBankIntervalDTO.getEndDate().getTime())).collect(Collectors.toList());
        }
        timeBankDTO.setMonthlyIntervalsTimeBank(monthlyTimeBankIntervals);
        timeBankDTO.setHourlyCost(employmentWithCtaDetailsDTO.getHourlyCost());
        return timeBankDTO;
    }

    public TimeBankVisualViewDTO getVisualViewTimeBank(DateTimeInterval interval, DailyTimeBankEntry dailyTimeBankEntry, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, Map<String, List<TimeType>> presenceAbsenceTimeTypeMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = getScheduledActivities(shifts);
        List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaName(), Collectors.summingInt(tb -> tb.getMinutes())));
        List<CTADistributionDTO> timeBankDistributionsDto = getDistributionOfTimeBank(ctaDistributionMap, employmentWithCtaDetailsDTO, 0).getCtaRuletemplateBonus().getCtaDistributions();
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
            if (Optional.ofNullable(dailyTimeBankEntry).isPresent()) {
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

    public int calculateDeltaTimeBankForInterval(DateTimeInterval planningPeriodInterval, Interval interval, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, boolean isByOverView, List<DailyTimeBankEntry> dailyTimeBankEntries, boolean calculateContractual) {
        Set<LocalDate> dailyTimeBanksDates = new HashSet<>();
        if (!calculateContractual) {
            dailyTimeBanksDates = dailyTimeBankEntries.stream().map(d -> DateUtils.toJodaDateTime(d.getDate()).toLocalDate()).collect(Collectors.toSet());
        }
        interval = getIntervalValidIntervalForTimebank(employmentWithCtaDetailsDTO, interval, planningPeriodInterval);
        //It can be contractual or Delta Timebank minutes it calculate on the basis of calculateContractual param
        int contractualOrDeltaMinutes = 0;
        if (interval != null) {
            DateTime startDate = interval.getStart();
            while (startDate.isBefore(interval.getEnd())) {
                if (calculateContractual || !dailyTimeBanksDates.contains(startDate.toLocalDate())) {
                    boolean vaild = (employmentWithCtaDetailsDTO.getWorkingDaysInWeek() == 7) || (startDate.getDayOfWeek() != DateTimeConstants.SATURDAY && startDate.getDayOfWeek() != DateTimeConstants.SUNDAY);
                    if (vaild) {
                        contractualOrDeltaMinutes += getContractualMinutesByDate(planningPeriodInterval, DateUtils.asLocalDate(startDate), employmentWithCtaDetailsDTO.getEmploymentLines());
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        return contractualOrDeltaMinutes;
    }

    public Interval getIntervalValidIntervalForTimebank(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, Interval interval, DateTimeInterval planningPeriodInterval) {
        Interval updatedInterval = null;
        DateTime employmentStartTime = toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate());
        if (interval.contains(employmentStartTime) || interval.getStart().isAfter(employmentStartTime)) {
            DateTime employmentEndTime = toJodaDateTime(isNull(employmentWithCtaDetailsDTO.getEndDate()) ? planningPeriodInterval.getEndLocalDate() : employmentWithCtaDetailsDTO.getEndDate().isBefore(planningPeriodInterval.getEndLocalDate()) ? employmentWithCtaDetailsDTO.getEndDate() : planningPeriodInterval.getEndLocalDate());
            Interval employmentInterval = new Interval(employmentStartTime, employmentEndTime);
            if (interval.overlaps(employmentInterval)) {
                updatedInterval = interval.overlap(employmentInterval);
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

    private List<TimeBankIntervalDTO> getTimeBankIntervals(Long unitId, Date startDate, Date endDate, long totalTimeBankBefore, String query, List<Interval> intervals, Map<Interval, List<ShiftWithActivityDTO>> shiftsintervalMap, Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap, List<TimeTypeDTO> timeTypeDTOS, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap, Map<Interval, List<PayOutPerShift>> payOutsintervalMap) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        List<PeriodDTO> planningPeriods = planningPeriodService.findAllPeriodsByStartDateAndLastDate(unitId, DateUtils.asLocalDate(startDate), DateUtils.asLocalDate(endDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
        for (Interval interval : intervals) {
            List<PayOutPerShift> payOutPerShifts = payOutsintervalMap.get(interval);
            List<ShiftWithActivityDTO> shifts = shiftsintervalMap.get(interval);
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBanksIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionIntervalMap.get(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(interval.getStart().toDate(), query.equals(DAILY) ? interval.getStart().toDate() : interval.getEnd().minusDays(1).toDate(), getPhaseNameByPeriods(planningPeriods, interval.getStart()));
            int timeBankOfInterval = calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
            int contractualMin = calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, false, dailyTimeBankEntries, true);
            timeBankIntervalDTO.setTotalContractedMin(contractualMin);
            Long approvePayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p -> (long) p.getMinutes()).sum();
            Long requestPayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.REQUESTED)).mapToLong(p -> (long) p.getMinutes()).sum();
            Long paidPayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.PAIDOUT)).mapToLong(p -> (long) p.getMinutes()).sum();
            timeBankIntervalDTO.setApprovePayOut(approvePayOut);
            timeBankIntervalDTO.setRequestPayOut(requestPayOut);
            timeBankIntervalDTO.setPaidoutChange(paidPayOut);
            timeBankIntervalDTO.setHeaderName(getHeaderName(query, interval));
            if (isCollectionNotEmpty(dailyTimeBankEntries)) {
                long  protectedDaysOffMinutes=dailyTimeBankEntries.stream().collect(Collectors.summingLong(dailyTimeBankEntry -> dailyTimeBankEntry.getProtectedDaysOffMinutes()));
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
                timeBankIntervalDTO.setProtectedDaysOffMinutes(protectedDaysOffMinutes);
                timeBankIntervalDTO.setTotalScheduledMin(scheduledMinutesOfTimebank + scheduledMinutesOfPayout);
                List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
                Map<String, Integer> ctaDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaName(), Collectors.summingInt(tb -> tb.getMinutes())));
                timeBankIntervalDTO.setTimeBankDistribution(getDistributionOfTimeBank(ctaDistributionMap, employmentWithCtaDetailsDTO, plannedMinutesOfTimebank));
                timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            } else {
                totalTimeBankBefore -= timeBankOfInterval;
                updateTimebankIntervalWithDefaultValue(totalTimeBankBefore, query, timeTypeDTOS, employmentWithCtaDetailsDTO, interval, shifts, timeBankIntervalDTO, timeBankOfInterval, approvePayOut);
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            }
        }
        return timeBankIntervalDTOS;
    }

    private void updateTimebankIntervalWithDefaultValue(long totalTimeBankBefore, String query, List<TimeTypeDTO> timeTypeDTOS, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, Interval interval, List<ShiftWithActivityDTO> shifts, TimeBankIntervalDTO timeBankIntervalDTO, int timeBankOfInterval, Long approvePayOut) {
        timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore - approvePayOut);
        timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore + timeBankOfInterval);
        timeBankIntervalDTO.setTotalTimeBankMin(-timeBankOfInterval + approvePayOut);
        timeBankIntervalDTO.setTotalTimeBankDiff(-timeBankOfInterval + approvePayOut);
        timeBankIntervalDTO.setTitle(getTitle(query, interval));
        timeBankIntervalDTO.setTimeBankDistribution(getDistributionOfTimeBank(new HashMap<>(), employmentWithCtaDetailsDTO, 0));
        timeBankIntervalDTO.setWorkingTimeType(getWorkingTimeType(interval, shifts, timeTypeDTOS));
    }

    private String getPhaseNameByPeriods(List<PeriodDTO> planningPeriods, DateTime startDate) {
        String phaseName = "";
        java.time.LocalDate startLocalDate = DateUtils.asLocalDate(startDate);
        for (PeriodDTO planningPeriod : planningPeriods) {
            if (planningPeriod.getStartDate().isEqual(startLocalDate) || planningPeriod.getEndDate().isEqual(startLocalDate) || (planningPeriod.getStartDate().isBefore(startLocalDate) && planningPeriod.getEndDate().isAfter(startLocalDate))) {
                phaseName = planningPeriod.getCurrentPhaseName();
                break;
            }
        }
        return phaseName;
    }

    private String getHeaderName(String query, Interval interval) {
        if (isNotNull(query)) {
            if (query.equals(DAILY)) {
                return DayOfWeek.of(interval.getStart().getDayOfWeek()).toString();
            } else {
                return getTitle(query, interval);
            }
        }
        return null;
    }

    public TimeBankCTADistributionDTO getDistributionOfTimeBank(Map<String, Integer> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, long plannedMinutesOfTimebank) {
        List<CTADistributionDTO> timeBankCTADistributionDTOS = new ArrayList<>();
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : employmentWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if (ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
                if (ctaRuleTemplate.getCalculationFor().equals(BONUS_HOURS) || ctaRuleTemplate.getCalculationFor().equals(FUNCTIONS)) {
                    CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0));
                    timeBankCTADistributionDTOS.add(ctaDistributionDTO);
                    ctaBonusMinutes += ctaDistributionDTO.getMinutes();
                } else if (ctaRuleTemplate.getCalculationFor().equals(SCHEDULED_HOURS)) {
                    scheduledCTADistributions.add(new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0)));
                }
            }
        }
        return new TimeBankCTADistributionDTO(scheduledCTADistributions, new CTARuletemplateBonus(timeBankCTADistributionDTOS, ctaBonusMinutes), plannedMinutesOfTimebank);
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
            default:
                break;
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
        if (isCollectionNotEmpty(timeTypeDTOS)) {
            List<ScheduleTimeByTimeTypeDTO> parentTimeTypes = new ArrayList<>();
            timeTypeDTOS.forEach(timeType -> {
                int totalScheduledMin = 0;
                if (timeType.getTimeTypes().equals(TimeTypes.WORKING_TYPE.toValue()) && timeType.getUpperLevelTimeTypeId() == null) {
                    ScheduleTimeByTimeTypeDTO parentTimeType = new ScheduleTimeByTimeTypeDTO(0);
                    List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                    parentTimeType.setChildren(children);
                    parentTimeType.setName(timeType.getLabel());
                    parentTimeType.setTimeTypeId(timeType.getId());
                    if (!children.isEmpty()) {
                        totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                    }
                    if (shifts != null && !shifts.isEmpty()) {
                        for (ShiftWithActivityDTO shift : shifts) {
                            for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                                if (timeType.getId().equals(shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.contains(shift.getStartDate().getTime())) {
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
        }
        return scheduleTimeByTimeTypeDTO;
    }

    private List<ScheduleTimeByTimeTypeDTO> getTimeTypeDTOS(BigInteger timeTypeId, Interval interval, List<ShiftWithActivityDTO> shifts, List<TimeTypeDTO> timeTypeDTOS) {
        List<ScheduleTimeByTimeTypeDTO> scheduleTimeByTimeTypeDTOS = new ArrayList<>();
        timeTypeDTOS.forEach(timeType -> {
            int totalScheduledMin = 0;
            if (timeType.getUpperLevelTimeTypeId() != null && timeType.getUpperLevelTimeTypeId().equals(timeTypeId)) {
                ScheduleTimeByTimeTypeDTO scheduleTimeByTimeTypeDTO = new ScheduleTimeByTimeTypeDTO(0);
                scheduleTimeByTimeTypeDTO.setTimeTypeId(timeType.getId());
                scheduleTimeByTimeTypeDTO.setName(timeType.getLabel());
                List<ScheduleTimeByTimeTypeDTO> children = getTimeTypeDTOS(timeType.getId(), interval, shifts, timeTypeDTOS);
                scheduleTimeByTimeTypeDTO.setChildren(children);
                if (!children.isEmpty()) {
                    totalScheduledMin += children.stream().mapToInt(c -> c.getTotalMin()).sum();
                }
                if (isCollectionNotEmpty(shifts)) {
                    for (ShiftWithActivityDTO shift : shifts) {
                        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
                            if (timeType.getId().equals(shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && interval.contains(shift.getStartDate().getTime())) {
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
            if (interval.contains(asDate(tb.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate()))) {
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
        DateTime endDateTime = new DateTime(endDate).withMillisOfSecond(1);
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
                default:
                    break;
                //case "ByPeriod": return getActualTimeBankByPeriod(startDate,endDate,shifts);
            }
            intervals.add(new Interval(startDateTime, nextEndDay.isAfter(endDateTime) ? endDateTime : nextEndDay));
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
            default:
                break;
        }
        return quaterDateTime;
    }

    //Calculating Time Bank Against Open Shift
    public int[] calculateDailyTimeBankForOpenShift(OpenShift shift, Activity activity, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        int totalTimebank = 0;
        int plannedTimeMin = 0;
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        Interval shiftInterval = new Interval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        int contractualMin = startDate.getDayOfWeek() <= employmentWithCtaDetailsDTO.getWorkingDaysInWeek() ? employmentWithCtaDetailsDTO.getTotalWeeklyMinutes() / employmentWithCtaDetailsDTO.getWorkingDaysInWeek() : 0;
        for (CTARuleTemplateDTO ruleTemplate : employmentWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if (ruleTemplate.getPlannedTimeWithFactor().getAccountType() == null) continue;
            if (ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
                int ctaTimeBankMin = 0;
                if ((ruleTemplate.getActivityIds().contains(shift.getActivityId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(activity.getBalanceSettingsActivityTab().getTimeTypeId())))) {
                    if (((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(asLocalDate(shiftInterval.getStart()).getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(DateUtils.toLocalDate(shiftInterval.getStart()))))) {
                        if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS)) {
                            plannedTimeMin += calculateScheduleAndDurationHourForOpenShift(shift, activity, employmentWithCtaDetailsDTO);
                            totalTimebank += plannedTimeMin;
                        }
                        if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                            for (CompensationTableInterval ctaIntervalDTO : ruleTemplate.getCompensationTable().getCompensationTableInterval()) {
                                List<Interval> ctaIntervals = getCTAInterval(ctaIntervalDTO, startDate);
                                for (Interval ctaInterval : ctaIntervals) {
                                    if (ctaInterval.overlaps(shiftInterval)) {
                                        int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).toDuration().getStandardMinutes();
                                        if (CompensationMeasurementType.MINUTES.equals(ctaIntervalDTO.getCompensationMeasurementType())) {
                                            ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) * ctaIntervalDTO.getValue();
                                            totalTimebank += ctaTimeBankMin;
                                            plannedTimeMin += ctaTimeBankMin;
                                            break;
                                        } else if (CompensationMeasurementType.PERCENT.equals(ctaIntervalDTO.getCompensationMeasurementType())) {
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
        totalTimebank = startDate.getDayOfWeek() <= employmentWithCtaDetailsDTO.getWorkingDaysInWeek() ? totalTimebank - contractualMin : totalTimebank;
        return new int[]{plannedTimeMin, totalTimebank};
    }

    //Calculating schedule Minutes for Open Shift
    private int calculateScheduleAndDurationHourForOpenShift(OpenShift openShift, Activity activity, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
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
            case CommonConstants.FULL_DAY_CALCULATION:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullDayCalculationType())) ? employmentWithCtaDetailsDTO.getFullTimeWeeklyMinutes() : employmentWithCtaDetailsDTO.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = new Double(employmentWithCtaDetailsDTO.getTotalWeeklyMinutes() * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case CommonConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getTimeCalculationActivityTab().getFullWeekCalculationType())) ? employmentWithCtaDetailsDTO.getFullTimeWeeklyMinutes() : employmentWithCtaDetailsDTO.getTotalWeeklyMinutes();
                duration = new Double(weeklyMinutes * activity.getTimeCalculationActivityTab().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            default:
                break;
        }
        return scheduledMinutes;
    }

    public TreeMap<java.time.LocalDate, TimeBankIntervalDTO> getAccumulatedTimebankDTO(java.time.LocalDate firstRequestPhasePlanningPeriodEndDate, DateTimeInterval planningPeriodInterval, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, java.time.LocalDate startDate, java.time.LocalDate endDate, long actualTimebankMinutes, UserAccessRoleDTO userAccessRoleDTO, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS) {
        long expectedTimebankMinutes = actualTimebankMinutes;
        java.time.LocalDate employmentStartDate = employmentWithCtaDetailsDTO.getStartDate();
        Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(k -> k.getDate(), v -> v));
        TreeMap<java.time.LocalDate, TimeBankIntervalDTO> localDateTimeBankByDateDTOMap = new TreeMap<>();
        endDate = isNull(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : endDate.isBefore(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : employmentWithCtaDetailsDTO.getEndDate();
        Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap = getDatePhaseDefaultName(employmentStartDate, endDate, employmentWithCtaDetailsDTO.getUnitId());
        Set<PhaseDefaultName> validPhaseForActualTimeBank = newHashSet(PUZZLE, CONSTRUCTION);
        while (employmentStartDate.isBefore(endDate) || employmentStartDate.equals(endDate)) {
            int totalTimeBankMinutes;
            long publishedBalancesMinutes = 0;
            DailyTimeBankEntry dailyTimeBankEntry;
            Map<String, Integer> ctaRuletemplateNameAndMinutesMap = new HashMap<>();
            if (dateDailyTimeBankEntryMap.containsKey(employmentStartDate)) {
                dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                totalTimeBankMinutes = getDeltaTimebankByUserAccessRole(userAccessRoleDTO, dailyTimeBankEntry);
                publishedBalancesMinutes = dailyTimeBankEntry.getPublishedBalances().values().stream().mapToLong(value -> value).sum();
                ctaRuletemplateNameAndMinutesMap = dailyTimeBankEntry.getTimeBankCTADistributionList().stream().collect(Collectors.toMap(k -> k.getCtaName(), v -> v.getMinutes()));
            } else {
                totalTimeBankMinutes = -getContractualMinutesByDate(planningPeriodInterval, employmentStartDate, employmentWithCtaDetailsDTO.getEmploymentLines());
            }
            if (!employmentStartDate.isAfter(firstRequestPhasePlanningPeriodEndDate) || validPhaseForActualTimeBank.contains(datePhaseDefaultNameMap.get(employmentStartDate))) {
                expectedTimebankMinutes += totalTimeBankMinutes;
            }
            CTARuletemplateBonus ctaRuletemplateBonus = getCTABonusDistributions(ctaRuletemplateNameAndMinutesMap, ctaRuleTemplateDTOS);
            TimeBankCTADistributionDTO timeBankCTADistributionDTO = new TimeBankCTADistributionDTO(newArrayList(), ctaRuletemplateBonus, 0);
            if (employmentStartDate.isAfter(startDate) || startDate.equals(employmentStartDate)) {
                localDateTimeBankByDateDTOMap.put(employmentStartDate, new TimeBankIntervalDTO(totalTimeBankMinutes, 0, expectedTimebankMinutes, publishedBalancesMinutes, timeBankCTADistributionDTO));
            }
            employmentStartDate = employmentStartDate.plusDays(1);
        }
        return localDateTimeBankByDateDTOMap;
    }

    private int getDeltaTimebankByUserAccessRole(UserAccessRoleDTO userAccessRoleDTO, DailyTimeBankEntry dailyTimeBankEntry) {
        int deltaTimebankMinutes;
        if (userAccessRoleDTO.getManagement() && isNotNull(dailyTimeBankEntry.getDraftDailyTimeBankEntry())) {
            deltaTimebankMinutes = dailyTimeBankEntry.getDraftDailyTimeBankEntry().getDeltaTimeBankMinutes();
        } else {
            deltaTimebankMinutes = dailyTimeBankEntry.getDeltaTimeBankMinutes();
        }
        return deltaTimebankMinutes;
    }

    public BigDecimal getHourlyCostByDate(List<EmploymentLinesDTO> employmentLines, java.time.LocalDate localDate) {
        BigDecimal hourlyCost = new BigDecimal(0);
        for (EmploymentLinesDTO employmentLine : employmentLines) {
            DateTimeInterval positionInterval = employmentLine.getInterval();
            if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(asDate(localDate)) || employmentLine.getEndDate().equals(localDate)))) {
                hourlyCost = employmentLine.getHourlyCost();
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
        if (isCollectionNotEmpty(payOutPerShifts)) {
            for (PayOutPerShift payOutPerShift : payOutPerShifts) {
                scheduledMinutesOfPayout += payOutPerShift.getScheduledMinutes();
                plannedMinutesOfPayout += payOutPerShift.getCtaBonusMinutesOfPayOut() + payOutPerShift.getScheduledMinutes();
            }
        }
        return new long[]{plannedMinutesOfPayout, scheduledMinutesOfPayout};
    }

    private Map<java.time.LocalDate, PhaseDefaultName> getDatePhaseDefaultName(java.time.LocalDate startDate, java.time.LocalDate endDate, Long unitId) {
        LocalDateTime startDateTime = startDate.atTime(LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MIDNIGHT);
        Set<LocalDateTime> localDateTimes = new HashSet<>();
        while (startDateTime.isBefore(endDateTime) || startDateTime.equals(endDateTime)) {
            localDateTimes.add(startDateTime);
            startDateTime = startDateTime.plusDays(1);
        }
        return phaseService.getPhasesByDates(unitId, localDateTimes).entrySet().stream().collect(Collectors.toMap(k -> asLocalDate(k.getKey()), v -> v.getValue().getPhaseEnum()));
    }

    private Map<java.time.LocalDate, Boolean> getDateWisePublishPlanningPeriod(Long employmentTypeId, java.time.LocalDate startDate, java.time.LocalDate endDate, Long unitId) {
        List<PlanningPeriodDTO> planningPeriodDTOS = planningPeriodService.findAllPlanningPeriodBetweenDatesAndUnitId(unitId, asDate(startDate), asDate(endDate));
        Map<java.time.LocalDate, Boolean> dateAndPublishPlanningPeriod = new HashMap<>();
        boolean publish;
        for (PlanningPeriodDTO planningPeriodDTO : planningPeriodDTOS) {
            publish = planningPeriodDTO.getPublishEmploymentIds().contains(employmentTypeId);
            startDate = planningPeriodDTO.getStartDate();
            endDate = planningPeriodDTO.getEndDate();
            while (!startDate.isAfter(endDate)) {
                dateAndPublishPlanningPeriod.put(startDate, publish);
                startDate = startDate.plusDays(1);
            }
        }
        return dateAndPublishPlanningPeriod;
    }


    public DailyTimeBankEntry updatePublishedBalances(DailyTimeBankEntry dailyTimeBankEntry, List<EmploymentLinesDTO> employmentLines, Long unitId, int deltaAccumulatedTimebankMinutes) {
        DailyTimeBankEntry todayDailyTimeBankEntry = timeBankRepository.findByEmploymentAndDate(dailyTimeBankEntry.getEmploymentId(), java.time.LocalDate.now());
        if (isNull(todayDailyTimeBankEntry)) {
            DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
            int contractualMinutes = getContractualMinutesByDate(planningPeriodInterval, java.time.LocalDate.now(), employmentLines);
            todayDailyTimeBankEntry = new DailyTimeBankEntry(dailyTimeBankEntry.getEmploymentId(), dailyTimeBankEntry.getStaffId(), java.time.LocalDate.now());
            todayDailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(-contractualMinutes);
            todayDailyTimeBankEntry.setContractualMinutes(contractualMinutes);
            todayDailyTimeBankEntry.setDeltaTimeBankMinutes(-contractualMinutes);
        }
        todayDailyTimeBankEntry.getPublishedBalances().put(dailyTimeBankEntry.getDate(), deltaAccumulatedTimebankMinutes);
        return timeBankRepository.save(todayDailyTimeBankEntry);
    }

    public Long calculateActualTimebank(DateTimeInterval dateTimeInterval, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, java.time.LocalDate endDate, java.time.LocalDate employmentStartDate) {
        Map<java.time.LocalDate, Boolean> publishPlanningPeriodDateMap = getDateWisePublishPlanningPeriod(employmentWithCtaDetailsDTO.getEmploymentTypeId(), employmentStartDate, endDate, employmentWithCtaDetailsDTO.getUnitId());
        Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(k -> k.getDate(), v -> v));
        Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap = getDatePhaseDefaultName(employmentStartDate, endDate, employmentWithCtaDetailsDTO.getUnitId());
        long actualTimebank = employmentWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
        endDate = isNull(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : endDate.isBefore(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : employmentWithCtaDetailsDTO.getEndDate();
        Set<PhaseDefaultName> validPhaseForActualTimeBank = newHashSet(REALTIME, TIME_ATTENDANCE, PAYROLL);
        while (employmentStartDate.isBefore(endDate) || employmentStartDate.equals(endDate)) {
            int deltaTimeBankMinutes = (-getContractualMinutesByDate(dateTimeInterval, employmentStartDate, employmentWithCtaDetailsDTO.getEmploymentLines()));
            if (dateDailyTimeBankEntryMap.containsKey(employmentStartDate) && dateDailyTimeBankEntryMap.get(employmentStartDate).isPublishedSomeActivities()) {
                DailyTimeBankEntry dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                deltaTimeBankMinutes = dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes();
                actualTimebank += deltaTimeBankMinutes;
                LOGGER.debug("delta timebank {}", dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes());
                LOGGER.debug("actual timebank {} till date {} phase {}", actualTimebank, employmentStartDate, datePhaseDefaultNameMap.get(employmentStartDate));
            } else if (validPhaseForActualTimeBank.contains(datePhaseDefaultNameMap.get(employmentStartDate)) || publishPlanningPeriodDateMap.get(employmentStartDate)) {
                actualTimebank += deltaTimeBankMinutes;
                LOGGER.debug("delta timebank {}", deltaTimeBankMinutes);
                LOGGER.debug("actual timebank {} till date {} phase {}", actualTimebank, employmentStartDate, datePhaseDefaultNameMap.get(employmentStartDate));
            }
            employmentStartDate = employmentStartDate.plusDays(1);
        }
        return actualTimebank;
    }

    @Getter
    @Setter
    public class CalculatePlannedHoursAndScheduledHours {
        private StaffAdditionalInfoDTO staffAdditionalInfoDTO;
        private DateTimeInterval dateTimeInterval;
        private List<ShiftWithActivityDTO> shifts;
        private boolean validatedByPlanner;
        private boolean anyShiftPublish;
        private int totalDailyPlannedMinutes;
        private int scheduledMinutesOfTimeBank;
        private int totalPublishedDailyPlannedMinutes;
        private Map<BigInteger, Integer> ctaTimeBankMinMap;
        private Map<Long, DayTypeDTO> dayTypeDTOMap;

        public CalculatePlannedHoursAndScheduledHours(StaffAdditionalInfoDTO staffAdditionalInfoDTO, DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, boolean validatedByPlanner, boolean anyShiftPublish, Map<Long, DayTypeDTO> dayTypeDTOMap) {
            this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
            this.dateTimeInterval = dateTimeInterval;
            this.shifts = shifts;
            this.validatedByPlanner = validatedByPlanner;
            this.anyShiftPublish = anyShiftPublish;
            this.dayTypeDTOMap = dayTypeDTOMap;
            this.totalDailyPlannedMinutes = 0;
            this.scheduledMinutesOfTimeBank = 0;
            this.totalPublishedDailyPlannedMinutes = 0;
            this.ctaTimeBankMinMap = new HashMap<>();
        }

        public CalculatePlannedHoursAndScheduledHours(StaffAdditionalInfoDTO staffAdditionalInfoDTO, DateTimeInterval dateTimeInterval) {
            this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
            this.dateTimeInterval = dateTimeInterval;
        }

        public CalculatePlannedHoursAndScheduledHours() {
        }

        public CalculatePlannedHoursAndScheduledHours calculate() {
            boolean ruleTemplateValid = false;
            for (CTARuleTemplateDTO ruleTemplate : staffAdditionalInfoDTO.getEmployment().getCtaRuleTemplates()) {
                for (ShiftWithActivityDTO shift : shifts) {
                    List<ShiftActivityDTO> shiftActivities = getShiftActivityByBreak(shift.getActivities(), shift.getBreakActivities());
                    for (ShiftActivityDTO shiftActivity : shiftActivities) {
                        ShiftActivityDTO shiftActivityDTO = getShiftActivityDTO(shift, shiftActivity);
                        ruleTemplateValid = validateCTARuleTemplate(dayTypeDTOMap, ruleTemplate, staffAdditionalInfoDTO.getEmployment(), shift.getPhaseId(), shiftActivity.getActivity().getId(), shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId(), shiftActivity.getStartDate(), shiftActivity.getPlannedTimes()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT);
                        LOGGER.debug("rule template : {} valid {}", ruleTemplate.getId(), ruleTemplateValid);
                        if (ruleTemplateValid) {
                            int ctaBonusAndScheduledMinutes = 0;
                            if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && dateTimeInterval.contains(shiftActivity.getStartDate().getTime())) {
                                scheduledMinutesOfTimeBank += shiftActivity.getScheduledMinutes();
                                ctaBonusAndScheduledMinutes = shiftActivity.getScheduledMinutes();
                                shiftActivityDTO.setScheduledMinutesOfTimebank(shiftActivity.getScheduledMinutes() + shiftActivityDTO.getScheduledMinutesOfTimebank());
                            } else if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                                ctaBonusAndScheduledMinutes = getAndUpdateCtaBonusMinutes(dateTimeInterval, ctaTimeBankMinMap, ruleTemplate, shiftActivity);
                                Optional<TimeBankDistributionDTO> optionalTimeBankDistributionDTO = shiftActivityDTO.getTimeBankCTADistributions().stream().filter(distributionDTO -> distributionDTO.getCtaRuleTemplateId().equals(ruleTemplate.getId())).findAny();
                                if (optionalTimeBankDistributionDTO.isPresent()) {
                                    optionalTimeBankDistributionDTO.get().setMinutes(optionalTimeBankDistributionDTO.get().getMinutes() + ctaBonusAndScheduledMinutes);
                                } else {
                                    TimeBankDistributionDTO timeBankDistributionDTO = new TimeBankDistributionDTO(ruleTemplate.getName(), ruleTemplate.getId(), DateUtils.asLocalDate(new Date()), ctaBonusAndScheduledMinutes);
                                    shiftActivityDTO.getTimeBankCTADistributions().add(timeBankDistributionDTO);
                                }
                                shiftActivityDTO.setTimeBankCtaBonusMinutes(ctaBonusAndScheduledMinutes + shiftActivityDTO.getTimeBankCtaBonusMinutes());
                                LOGGER.debug("rule template : {} minutes {}", ruleTemplate.getId(), ctaBonusAndScheduledMinutes);
                            }
                            shiftActivityDTO.setPlannedMinutesOfTimebank(ctaBonusAndScheduledMinutes + shiftActivityDTO.getPlannedMinutesOfTimebank());
                            totalDailyPlannedMinutes += ctaBonusAndScheduledMinutes;
                            ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaBonusAndScheduledMinutes);
                            if ((validatedByPlanner || shiftActivity.getStatus().contains(ShiftStatus.PUBLISH)) && !shift.isDraft()) {
                                totalPublishedDailyPlannedMinutes += ctaBonusAndScheduledMinutes;
                                anyShiftPublish = true;
                            }
                            //updateChildActivitiesTimebankCtaBonus(shiftActivity.getChildActivities(), ruleTemplate, dateTimeInterval);
                        }
                    }
                }
                if (ruleTemplate.getCalculationFor().equals(FUNCTIONS) && ruleTemplateValid) {
                    int value = getFunctionalBonusCompensation(staffAdditionalInfoDTO.getEmployment(), ruleTemplate, dateTimeInterval);
                    ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + value);
                    totalDailyPlannedMinutes += value;
                }
            }
            return this;
        }


        public ShiftActivityDTO getShiftActivityDTO(ShiftWithActivityDTO shift, ShiftActivityDTO shiftActivity) {
            ShiftActivityDTO shiftActivityDTO = shiftActivity;
            try {
                return shift.getActivities().stream().filter(shiftActivityDTO1 -> shiftActivityDTO1.getId().equals(shiftActivity.getId()) || (isCollectionNotEmpty(shift.getBreakActivities()) && shift.getBreakActivities().get(0).getId().equals(shiftActivityDTO1.getId()))).findAny().get();
            } catch (NullPointerException | NoSuchElementException e) {
                shiftActivityDTO = shiftActivity;
            }
            return shiftActivityDTO;
        }

        public List<ShiftActivityDTO> getShiftActivityByBreak(List<ShiftActivityDTO> shiftActivities, List<ShiftActivityDTO> breakActivities) {
            List<ShiftActivityDTO> updatedShiftActivities = new ArrayList<>();
            if (isCollectionNotEmpty(breakActivities) && !(breakActivities.get(0)).isBreakNotHeld()) {
                List<ActivityDTO> activityDTOS = activityMongoRepository.findByDeletedFalseAndIdsIn(newArrayList(breakActivities.get(0).getActivityId()));
                for (ShiftActivityDTO shiftActivity : shiftActivities) {
                    for (ShiftActivityDTO breakActivity : breakActivities) {
                        if (shiftActivity.getInterval().overlaps(breakActivity.getInterval()) && shiftActivity.getInterval().overlap(breakActivity.getInterval()).getMinutes() == breakActivity.getInterval().getMinutes()) {
                            List<DateTimeInterval> dateTimeIntervals = shiftActivity.getInterval().minusInterval(breakActivity.getInterval());
                            for (DateTimeInterval timeInterval : dateTimeIntervals) {
                                ShiftActivityDTO updatedShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(shiftActivity, ShiftActivityDTO.class);
                                updatedShiftActivity.setStartDate(timeInterval.getStartDate());
                                updatedShiftActivity.setEndDate(timeInterval.getEndDate());
                                List<PlannedTime> plannedTimes = new ArrayList<>();
                                for (PlannedTime plannedTime : updatedShiftActivity.getPlannedTimes()) {
                                    if (plannedTime.getInterval().overlaps(timeInterval)) {
                                        DateTimeInterval plannedTimeInterval = plannedTime.getInterval().overlap(timeInterval);
                                        plannedTime.setStartDate(plannedTimeInterval.getStartDate());
                                        plannedTime.setEndDate(plannedTimeInterval.getEndDate());
                                        plannedTimes.add(plannedTime);
                                    }
                                }
                                updatedShiftActivity.setActivity(shiftActivity.getActivity());
                                updatedShiftActivity.setPlannedTimes(plannedTimes);
                                updatedShiftActivities.add(updatedShiftActivity);
                            }
                            List<PlannedTime> plannedTimes = new ArrayList<>();
                            for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
                                if (breakActivity.getInterval().overlaps(plannedTime.getInterval())) {
                                    DateTimeInterval dateTimeInterval = breakActivity.getInterval().overlap(plannedTime.getInterval());
                                    PlannedTime breakPlannedTime = ObjectMapperUtils.copyPropertiesByMapper(plannedTime, PlannedTime.class);
                                    breakPlannedTime.setStartDate(dateTimeInterval.getStartDate());
                                    breakPlannedTime.setEndDate(dateTimeInterval.getEndDate());
                                    plannedTimes.add(breakPlannedTime);
                                }
                            }
                            breakActivity.setActivity(activityDTOS.get(0));
                            breakActivity.setPlannedTimes(plannedTimes);
                            updatedShiftActivities.add(breakActivity);
                        }
                    }
                }
            } else {
                updatedShiftActivities = shiftActivities;
            }
            Collections.sort(updatedShiftActivities);
            return updatedShiftActivities;
        }

        public int getAndUpdateCtaBonusMinutes(DateTimeInterval dateTimeInterval, Map<BigInteger, Integer> ctaTimeBankMinMap, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivity) {
            Double ctaBonusAndScheduledMinutes = 0.0;
            for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
                if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId())) {
                    DateTimeInterval shiftInterval = dateTimeInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                    ctaBonusAndScheduledMinutes += calculateBonusAndUpdateShiftActivity(dateTimeInterval, ctaTimeBankMinMap, ruleTemplate, shiftActivity, shiftInterval);
                    if (asLocalDate(plannedTime.getStartDate()).isBefore(asLocalDate(plannedTime.getEndDate()))) {
                        DateTimeInterval nextDayInterval = new DateTimeInterval(getStartOfDay(plannedTime.getEndDate()), getEndOfDay(plannedTime.getEndDate()));
                        shiftInterval = nextDayInterval.overlap(new DateTimeInterval(getStartOfDay(plannedTime.getEndDate()), plannedTime.getEndDate()));
                        ctaBonusAndScheduledMinutes += calculateBonusAndUpdateShiftActivity(nextDayInterval, ctaTimeBankMinMap, ruleTemplate, shiftActivity, shiftInterval);
                    }
                }
            }
            return (int) Math.round(ctaBonusAndScheduledMinutes);
        }

        private void updateChildActivitiesTimebankCtaBonus(List<ShiftActivityDTO> childActivitiesDTOS, CTARuleTemplateDTO ruleTemplate, DateTimeInterval dateTimeInterval) {
            childActivitiesDTOS.sort(Comparator.comparing(ShiftActivityDTO::getStartDate));
            int ctaBonusAndScheduledMinutes = 0;
            for (ShiftActivityDTO childActivityDTO : childActivitiesDTOS) {
                if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && dateTimeInterval.contains(childActivityDTO.getStartDate().getTime())) {
                    ctaBonusAndScheduledMinutes = childActivityDTO.getScheduledMinutes();
                } else if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                    DateTimeInterval shiftInterval = dateTimeInterval.overlap(new DateTimeInterval(childActivityDTO.getStartDate(), childActivityDTO.getEndDate()));
                    ctaBonusAndScheduledMinutes = calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval).intValue();
                    if (asLocalDate(childActivityDTO.getStartDate()).isBefore(asLocalDate(childActivityDTO.getEndDate()))) {
                        DateTimeInterval nextDayInterval = new DateTimeInterval(getStartOfDay(childActivityDTO.getEndDate()), getEndOfDay(childActivityDTO.getEndDate()));
                        shiftInterval = nextDayInterval.overlap(new DateTimeInterval(getStartOfDay(childActivityDTO.getEndDate()), childActivityDTO.getEndDate()));
                        ctaBonusAndScheduledMinutes += calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval);
                    }
                    LOGGER.debug("rule template : {} minutes {}", ruleTemplate.getId(), ctaBonusAndScheduledMinutes);
                }
                childActivityDTO.getTimeBankCTADistributions().add(new TimeBankDistributionDTO(ruleTemplate.getName(), ruleTemplate.getId(), DateUtils.asLocalDate(childActivityDTO.getStartDate()), ctaBonusAndScheduledMinutes));
            }
        }
    }


    public boolean updateTimeBankAgainstProtectedDaysOffSetting() {
        List<DailyTimeBankEntry> dailyTimeBankEntriesToSave = new ArrayList<>();
        List<PayOutPerShift> payOutOfStaffs = new ArrayList<>();
        List<StaffEmploymentDetails> staffEmploymentDetails = userIntegrationService.getStaffsMainEmployment();
        Set<Long> unitIds = staffEmploymentDetails.stream().map(staffEmploymentDetail -> staffEmploymentDetail.getUnitId()).collect(Collectors.toSet());
        List<ProtectedDaysOffSettingDTO> protectedDaysOffSettingOfUnit = protectedDaysOffService.getAllProtectedDaysOffByUnitIds(new ArrayList<>(unitIds));
        Map<Long, ProtectedDaysOffSettingDTO> unitIdAndProtectedDaysOffSettingDTOMap = protectedDaysOffSettingOfUnit.stream().collect(Collectors.toMap(k -> k.getUnitId(), v -> v));
        Map<Long, List<StaffEmploymentDetails>> unitAndStaffEmploymentDetailsMap = staffEmploymentDetails.stream().collect(groupingBy(StaffEmploymentDetails::getUnitId));
        Set<Long> employmentIds = staffEmploymentDetails.stream().map(staffEmploymentDetail -> staffEmploymentDetail.getId()).collect(toSet());
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankService.findAllByEmploymentIdsAndBetweenDate(employmentIds, getDate(), getDate());
        Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getEmploymentId(), v -> v));
        List<Activity> activities = activityMongoRepository.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.PROTECTED_DAYS_OFF, unitIds);
        Set<BigInteger> activityIds = activities.stream().map(activity -> activity.getId()).collect(Collectors.toSet());
        Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        Map<Long, Activity> unitIdAndActivityMap = activities.stream().collect(Collectors.toMap(k -> k.getUnitId(), v -> v));
        Map[] activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap = getActivityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap(employmentIds, activityIds, activityMap);
        Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap = activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap[0];
        Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap = activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap[1];
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(new ArrayList<>(employmentIds), getDate(), getDate());
        Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap = ctaResponseDTOS.stream().collect(Collectors.toMap(k -> k.getEmploymentId(), v -> v));
        for (Long unitId : unitIds) {
            getDailyTimeBankEntryAndPaidOutPerUnit(dailyTimeBankEntriesToSave, payOutOfStaffs, unitIdAndProtectedDaysOffSettingDTOMap, unitAndStaffEmploymentDetailsMap, employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId);
        }
        if (isCollectionNotEmpty(dailyTimeBankEntriesToSave)) {
            timeBankRepository.saveEntities(dailyTimeBankEntriesToSave);
        }
        if (isCollectionNotEmpty(payOutOfStaffs)) {
            payOutService.savePayout(payOutOfStaffs);
        }
        return true;
    }

    private Object[] getDailyTimeBankEntryAndPaidOutPerUnit(List<DailyTimeBankEntry> dailyTimeBankEntriesToSave, List<PayOutPerShift> payOutOfStaffs,  Map<Long, ProtectedDaysOffSettingDTO> unitIdAndProtectedDaysOffSettingDTOMap, Map<Long, List<StaffEmploymentDetails>> unitAndStaffEmploymentDetailsMap, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId) {
        Object[] dailyTimeBankAndPayoutByOnceInAYear = new Object[]{};
        try {
            ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = unitIdAndProtectedDaysOffSettingDTOMap.get(unitId);
            Activity activity= unitIdAndActivityMap.get(unitId);
            if (isNotNull(protectedDaysOffSettingDTO) && isNotNull(activity)) {
                List<StaffEmploymentDetails> staffEmploymentDetailsList = unitAndStaffEmploymentDetailsMap.get(unitId);
                for (StaffEmploymentDetails employmentDetails : staffEmploymentDetailsList) {
                    dailyTimeBankAndPayoutByOnceInAYear = setDailyTimeBankAndPayoutPerStaff(dailyTimeBankEntriesToSave, dailyTimeBankAndPayoutByOnceInAYear, employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId, protectedDaysOffSettingDTO, activity, employmentDetails);
                }
                if (isNotNull(dailyTimeBankAndPayoutByOnceInAYear[0])) {
                    dailyTimeBankEntriesToSave.add((DailyTimeBankEntry) dailyTimeBankAndPayoutByOnceInAYear[0]);
                }
                if (isNotNull(dailyTimeBankAndPayoutByOnceInAYear[1])) {
                    payOutOfStaffs.add((PayOutPerShift) dailyTimeBankAndPayoutByOnceInAYear[1]);
                }
            }
        } catch (Exception e) {
            LOGGER.error("error while add protected days off time bank in unit  {} ,\n {}  ", unitId, e);
            e.printStackTrace();
        }
        return dailyTimeBankAndPayoutByOnceInAYear;
    }

    private Object[] setDailyTimeBankAndPayoutPerStaff(List<DailyTimeBankEntry> dailyTimeBankEntriesToSave, Object[] dailyTimeBankAndPayoutByOnceInAYear, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO, Activity activity, StaffEmploymentDetails employmentDetails) {
        List<ProtectedDaysOffSetting> protectedDaysOffSettings;
        try {
            protectedDaysOffSettings = employmentDetails.getProtectedDaysOffSettings();
            protectedDaysOffSettings=protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.getPublicHolidayDate().isAfter(employmentDetails.getStartDate()) || (isNull(employmentDetails.getEndDate())|| !protectedDaysOffSetting.getPublicHolidayDate().isAfter(employmentDetails.getEndDate()))).collect(Collectors.toList());
            employmentDetails.setProtectedDaysOffSettings(protectedDaysOffSettings);
            switch (protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings()) {
                case UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR:
                    DateTimeInterval dateTimeInterval=getCutoffInterval(activity.getRulesActivityTab().getCutOffStartFrom(), activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue(), asDate(getLocalDate()), getLocalDate());
                    if (dateTimeInterval.getStartLocalDate().equals(getLocalDate())) {
                        long count = employmentDetails.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && dateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).count();
                        DailyTimeBankEntry dailyTimeBankEntry = isNullOrElse(employmentIdAndDailyTimeBankEntryMap.get(employmentDetails.getId()), new DailyTimeBankEntry(employmentDetails.getId(), employmentDetails.getStaffId(), dateTimeInterval.getStartLocalDate()));
                        int contractualMinutes = getContractualMinutesByDate(dateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
                        resetDailyTimebankEntry(dailyTimeBankEntry, contractualMinutes);
                        dailyTimeBankEntry.setProtectedDaysOffMinutes(dailyTimeBankEntry.getProtectedDaysOffMinutes() + (int) (count * contractualMinutes));
                        dailyTimeBankEntriesToSave.add(dailyTimeBankEntry);
                    }
                    break;
                case ONCE_IN_A_YEAR:
                    dailyTimeBankAndPayoutByOnceInAYear = getDailyTimeBankAndPayoutByOnceInAYear(employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId, employmentDetails);
                    break;
                case ACTIVITY_CUT_OFF_INTERVAL:
                    dailyTimeBankAndPayoutByOnceInAYear = getDailyTimeBankEntryAndPayoutByCutOffInterval(employmentIdAndCtaResponseDTOMap, employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, unitId, employmentDetails);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("error while add protected days off time bank in staff  {} ,\n {}  ", employmentDetails.getStaffId(), e);
        }
        return dailyTimeBankAndPayoutByOnceInAYear;
    }


    private Object[] getDailyTimeBankEntryAndPayoutByCutOffInterval(Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Long unitId, StaffEmploymentDetails employmentDetails) {
        int[] scheduledAndApproveActivityCount;
        Activity activity = unitIdAndActivityMap.get(unitId);
        DateTimeInterval activityDateTimeInterval = activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
        List<ProtectedDaysOffSetting> protectedDaysOffSettings = new ArrayList<>();
        for (ProtectedDaysOffSetting protectedDaysOffSetting : employmentDetails.getProtectedDaysOffSettings()) {
            DateTimeInterval dateTimeInterval = getCutoffInterval(protectedDaysOffSetting.getPublicHolidayDate(), activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue(), asDate(getLocalDate().minusDays(1)), protectedDaysOffSetting.getPublicHolidayDate().plusDays(1));
            if (isNotNull(dateTimeInterval) && dateTimeInterval.getEndLocalDate().isEqual(getLocalDate()) && protectedDaysOffSetting.isProtectedDaysOff() && protectedDaysOffSetting.getPublicHolidayDate().isBefore(getLocalDate())) {
                protectedDaysOffSettings.add(protectedDaysOffSetting);
            }
        }
        int count = protectedDaysOffSettings.size();
        protectedDaysOffSettings.sort((protectedDaysOffSetting, t1) -> protectedDaysOffSetting.getPublicHolidayDate().compareTo(t1.getPublicHolidayDate()));
        DateTimeInterval protectedDaysDateTimeInterval = new DateTimeInterval(protectedDaysOffSettings.get(0).getPublicHolidayDate(), getLocalDate());
        scheduledAndApproveActivityCount = workTimeAgreementBalancesCalculationService.getShiftsActivityCountByInterval(activityDateTimeInterval, isNotNull(employmentIdAndShiftMap.get(employmentDetails.getId())) ? employmentIdAndShiftMap.get(employmentDetails.getId()) : new ArrayList<>(), newHashSet(unitIdAndActivityMap.get(unitId).getId()));
        count = count - scheduledAndApproveActivityCount[0];
        DailyTimeBankEntry dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, protectedDaysDateTimeInterval, count);
        PayOutPerShift payOutPerShift = getPayoutData(employmentIdAndCtaResponseDTOMap, employmentDetails, protectedDaysDateTimeInterval, count);
        return new Object[]{dailyTimeBankEntry, payOutPerShift};
    }

    private Object[] getDailyTimeBankAndPayoutByOnceInAYear(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId, StaffEmploymentDetails employmentDetails) {
        int[] scheduledAndApproveActivityCount;
        DateTimeInterval activityDateTimeInterval = activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
        int count = (int) employmentDetails.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && activityDateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).count();
        scheduledAndApproveActivityCount = workTimeAgreementBalancesCalculationService.getShiftsActivityCountByInterval(activityDateTimeInterval, isNotNull(employmentIdAndShiftMap.get(employmentDetails.getId())) ? employmentIdAndShiftMap.get(employmentDetails.getId()) : new ArrayList<>(), newHashSet(unitIdAndActivityMap.get(unitId).getId()));
        count = count - scheduledAndApproveActivityCount[0];
        DailyTimeBankEntry dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, activityDateTimeInterval, count);
        PayOutPerShift payOutPerShift = getPayoutData(employmentIdAndCtaResponseDTOMap, employmentDetails, activityDateTimeInterval, count);
        return new Object[]{dailyTimeBankEntry, payOutPerShift};
    }

    private DailyTimeBankEntry getDailyTimeBankEntry(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, StaffEmploymentDetails employmentDetails, DateTimeInterval activityDateTimeInterval, int count) {
        DailyTimeBankEntry dailyTimeBankEntry = employmentIdAndDailyTimeBankEntryMap.get(employmentDetails.getId());
        if(count>0) {
            CTAResponseDTO ctaResponseDTO = employmentIdAndCtaResponseDTOMap.get(employmentDetails.getId());
            int contractualMinutes = getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
            int value = 0;
            for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
                if (UNUSED_DAYOFF_LEAVES.equals(ruleTemplate.getCalculationFor()) && TIMEBANK_ACCOUNT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                    if (isNull(dailyTimeBankEntry)) {
                        dailyTimeBankEntry = new DailyTimeBankEntry(employmentDetails.getId(), employmentDetails.getStaffId(), getLocalDate());
                        resetDailyTimebankEntry(dailyTimeBankEntry, contractualMinutes);
                    }
                    int bonusByRuletemplate = getBonusOfUnusedDaysOff(activityDateTimeInterval, employmentDetails, contractualMinutes, ruleTemplate);
                    value += (bonusByRuletemplate * count);
                    dailyTimeBankEntry.getTimeBankCTADistributionList().add(new TimeBankCTADistribution(ruleTemplate.getName(), value, ruleTemplate.getId()));
                }
            }
            if (isNotNull(dailyTimeBankEntry)) {
                dailyTimeBankEntry.setPlannedMinutesOfTimebank(dailyTimeBankEntry.getPlannedMinutesOfTimebank() + value);
                dailyTimeBankEntry.setDeltaTimeBankMinutes(dailyTimeBankEntry.getDeltaTimeBankMinutes() + value);
                dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes() + value);
            }
        }
        return dailyTimeBankEntry;
    }

    private PayOutPerShift getPayoutData(Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, StaffEmploymentDetails employmentDetails, DateTimeInterval activityDateTimeInterval, int count) {
        PayOutPerShift payOutPerShift = null;
        if(count>0) {
            CTAResponseDTO ctaResponseDTO = employmentIdAndCtaResponseDTOMap.get(employmentDetails.getId());
            int contractualMinutes = getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
            int value = 0;
            for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
                if (UNUSED_DAYOFF_LEAVES.equals(ruleTemplate.getCalculationFor()) && PAID_OUT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                    if (isNull(payOutPerShift)) {
                        payOutPerShift = new PayOutPerShift(new BigInteger("-1"), employmentDetails.getId(), employmentDetails.getStaffId(), getLocalDate(), employmentDetails.getUnitId());
                    }
                    int bonusByRuletemplate = getBonusOfUnusedDaysOff(activityDateTimeInterval, employmentDetails, contractualMinutes, ruleTemplate);
                    value += (bonusByRuletemplate * count);

                    payOutPerShift.getPayOutPerShiftCTADistributions().add(new PayOutPerShiftCTADistribution(ruleTemplate.getName(), value, ruleTemplate.getId()));
                }
            }
            if (isNotNull(payOutPerShift)) {
                payOutPerShift.setCtaBonusMinutesOfPayOut(value);
                payOutPerShift.setScheduledMinutes(0);
                payOutPerShift.setTotalPayOutMinutes(value);
            }
        }
        return payOutPerShift;
    }

    private Map[] getActivityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap(Set<Long> employmentIds, Set<BigInteger> activityIds, Map<BigInteger, Activity> activityWrapperMap) {
        Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap = new HashMap<>();
        for (BigInteger activityId : activityWrapperMap.keySet()) {
            Activity activityWrapper = activityWrapperMap.get(activityId);
            activityIdDateTimeIntervalMap.putIfAbsent(activityId, getCutoffInterval(activityWrapper.getRulesActivityTab().getCutOffStartFrom(), activityWrapper.getRulesActivityTab().getCutOffIntervalUnit(), activityWrapper.getRulesActivityTab().getCutOffdayValue(), asDate(getLocalDate().minusDays(1)), getLocalDate()));
        }
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>(activityIdDateTimeIntervalMap.values());
        dateTimeIntervals.sort((dateTimeInterval, t1) -> dateTimeInterval.getStartLocalDate().compareTo(t1.getStartLocalDate()));
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIds, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), activityIds);
        Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap = shiftWithActivityDTOS.stream().collect(groupingBy(ShiftWithActivityDTO::getEmploymentId));
        return new Map[]{activityIdDateTimeIntervalMap, employmentIdAndShiftMap};
    }



    private int getBonusOfUnusedDaysOff(DateTimeInterval dateTimeInterval, StaffEmploymentDetails staffEmploymentDetails, int contractualMinutes, CTARuleTemplateDTO ruleTemplate) {
        int value = 0;
        if (ruleTemplate.getCalculationFor().equals(UNUSED_DAYOFF_LEAVES)) {
            if (CompensationMeasurementType.FIXED_VALUE.equals(ruleTemplate.getCompensationTable().getUnusedDaysOffType())) {
                value += getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate()).equals(new BigDecimal(0)) ? new BigDecimal(ruleTemplate.getCalculateValueAgainst().getFixedValue().getAmount()).divide(staffEmploymentDetails.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(60)).intValue() : 0;
            } else if (CompensationMeasurementType.PERCENT.equals(ruleTemplate.getCompensationTable().getUnusedDaysOffType())) {
                value += contractualMinutes * ruleTemplate.getCompensationTable().getUnusedDaysOffvalue() / 100;
            }
        }
        return value;
    }

}
