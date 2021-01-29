package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
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
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftDataHelper;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.ACTIVITY_END_DATE_LESS_THAN_START_DATE;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_ORGANIZATION_PHASES;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;
import static com.kairos.enums.phase.PhaseDefaultName.PAYROLL;
import static com.kairos.enums.phase.PhaseDefaultName.REALTIME;
import static com.kairos.enums.phase.PhaseDefaultName.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.*;

/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018
 *
 * */
@Getter
@Service
public class TimeBankCalculationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankCalculationService.class);
    @Inject public PayOutCalculationService payOutCalculationService;
    @Inject public PlanningPeriodService planningPeriodService;
    @Inject public ExceptionService exceptionService;
    @Inject public PayOutRepository payOutRepository;
    @Inject public PhaseService phaseService;
    @Inject public TimeBankRepository timeBankRepository;
    @Inject public PayOutTransactionMongoRepository payOutTransactionMongoRepository;
    @Inject public ActivityMongoRepository activityMongoRepository;
    @Inject public ShiftMongoRepository shiftMongoRepository;
    @Inject public WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;
    @Inject public PayOutService payOutService;
    @Inject public UserIntegrationService userIntegrationService;
    @Inject public CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject public ProtectedDaysOffService protectedDaysOffService;
    @Inject public TimeBankService timeBankService;
    @Inject private TimeBankAndPayOutCalculationService timeBankAndPayOutCalculationService;

    public DailyTimeBankEntry calculateDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, DailyTimeBankEntry dailyTimeBankEntry, DateTimeInterval planningPeriodInterval, List<DayTypeDTO> dayTypeDTOS, boolean validatedByPlanner) {
        boolean anyShiftPublish = false;
        int contractualMinutes = getContractualMinutesByDate(planningPeriodInterval, dateTimeInterval.getStartLocalDate(), staffAdditionalInfoDTO.getEmployment().getEmploymentLines());
        if (isCollectionNotEmpty(shifts)) {
            Map<BigInteger, DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
            CalculatePlannedHoursAndScheduledHours calculatePlannedHoursAndScheduledHours = new CalculatePlannedHoursAndScheduledHours(staffAdditionalInfoDTO, dateTimeInterval, shifts, validatedByPlanner, anyShiftPublish, dayTypeDTOMap,this).calculate();
            anyShiftPublish = calculatePlannedHoursAndScheduledHours.isAnyShiftPublish() || validatedByPlanner;
            int totalDailyPlannedMinutes = calculatePlannedHoursAndScheduledHours.getTotalDailyPlannedMinutes();
            int scheduledMinutesOfTimeBank = calculatePlannedHoursAndScheduledHours.getScheduledMinutesOfTimeBank();
            int totalPublishedDailyPlannedMinutes = calculatePlannedHoursAndScheduledHours.getTotalPublishedDailyPlannedMinutes();
            Map<BigInteger, Integer> ctaTimeBankMinMap = calculatePlannedHoursAndScheduledHours.getCtaTimeBankMinMap();
            dailyTimeBankEntry = updateDailyTimeBankEntry(staffAdditionalInfoDTO.getEmployment(), dateTimeInterval, dailyTimeBankEntry, anyShiftPublish, contractualMinutes, totalDailyPlannedMinutes, scheduledMinutesOfTimeBank, totalPublishedDailyPlannedMinutes, ctaTimeBankMinMap);
        } else if (isNotNull(dailyTimeBankEntry)) {
            resetDailyTimebankEntry(dailyTimeBankEntry, contractualMinutes);
        }
        return dailyTimeBankEntry;
    }

    private DailyTimeBankEntry updateDailyTimeBankEntry(StaffEmploymentDetails staffEmploymentDetails, DateTimeInterval dateTimeInterval, DailyTimeBankEntry dailyTimeBankEntry, boolean anyShiftPublish, int contractualMinutes, int totalDailyPlannedMinutes, int scheduledMinutesOfTimeBank, int totalPublishedDailyPlannedMinutes, Map<BigInteger, Integer> ctaTimeBankMinMap) {
        dailyTimeBankEntry = isNullOrElse(dailyTimeBankEntry, new DailyTimeBankEntry(staffEmploymentDetails.getId(), staffEmploymentDetails.getStaffId(), dateTimeInterval.getStartLocalDate()));
        int timeBankMinWithoutCta = scheduledMinutesOfTimeBank - contractualMinutes;
        dailyTimeBankEntry.setStaffId(staffEmploymentDetails.getStaffId());
        dailyTimeBankEntry.setTimeBankMinutesWithoutCta(timeBankMinWithoutCta);
        int deltaAccumulatedTimebankMinutes = anyShiftPublish ? (totalPublishedDailyPlannedMinutes - contractualMinutes) : MINIMUM_VALUE;
        List<TimeBankCTADistribution> protectedDaysOffTimeBankCTADistributions = getProtectedDaysOffTimeBankCTADistributions(dailyTimeBankEntry);
        int bonusOfProtectedDaysOff = protectedDaysOffTimeBankCTADistributions.stream().mapToInt(timeBankCTADistribution -> timeBankCTADistribution.getMinutes()).sum();
        dailyTimeBankEntry.setPlannedMinutesOfTimebank(totalDailyPlannedMinutes + bonusOfProtectedDaysOff);
        dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(deltaAccumulatedTimebankMinutes + bonusOfProtectedDaysOff);
        dailyTimeBankEntry.setCtaBonusMinutesOfTimeBank(ctaTimeBankMinMap.values().stream().mapToInt(ctaBonus -> ctaBonus).sum() + bonusOfProtectedDaysOff);
        dailyTimeBankEntry.setPublishedSomeActivities(dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes() != MINIMUM_VALUE);
        dailyTimeBankEntry.setContractualMinutes(contractualMinutes);
        dailyTimeBankEntry.setScheduledMinutesOfTimeBank(scheduledMinutesOfTimeBank);
        int deltaTimeBankMinutes = dailyTimeBankEntry.getPlannedMinutesOfTimebank() - contractualMinutes;
        dailyTimeBankEntry.setDeltaTimeBankMinutes(deltaTimeBankMinutes);
        protectedDaysOffTimeBankCTADistributions.addAll(getCTADistributionsOfTimebank(staffEmploymentDetails.getCtaRuleTemplates(), ctaTimeBankMinMap));
        dailyTimeBankEntry.setTimeBankCTADistributionList(protectedDaysOffTimeBankCTADistributions);
        dailyTimeBankEntry.setDraftDailyTimeBankEntry(null);
        return dailyTimeBankEntry;
    }

    public void resetDailyTimebankEntry(DailyTimeBankEntry dailyTimeBankEntry, int contractualMinutes) {
        List<TimeBankCTADistribution> timeBankCTADistributionList = getProtectedDaysOffTimeBankCTADistributions(dailyTimeBankEntry);
        dailyTimeBankEntry.setTimeBankMinutesWithoutCta(MINIMUM_VALUE);
        int bonusOfProtectedDaysOff = timeBankCTADistributionList.stream().mapToInt(timeBankCTADistribution -> timeBankCTADistribution.getMinutes()).sum();
        dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(bonusOfProtectedDaysOff);
        dailyTimeBankEntry.setCtaBonusMinutesOfTimeBank(bonusOfProtectedDaysOff);
        dailyTimeBankEntry.setPlannedMinutesOfTimebank(bonusOfProtectedDaysOff);
        dailyTimeBankEntry.setPublishedSomeActivities(false);
        dailyTimeBankEntry.setContractualMinutes(contractualMinutes);
        dailyTimeBankEntry.setScheduledMinutesOfTimeBank(MINIMUM_VALUE);
        dailyTimeBankEntry.setDeltaTimeBankMinutes(-contractualMinutes);
        dailyTimeBankEntry.setTimeBankCTADistributionList(isCollectionNotEmpty(timeBankCTADistributionList) ?timeBankCTADistributionList :new ArrayList<>());
        dailyTimeBankEntry.setDraftDailyTimeBankEntry(null);
        dailyTimeBankEntry.setTimeBankOffMinutes(0);
    }

    private List<TimeBankCTADistribution> getProtectedDaysOffTimeBankCTADistributions(DailyTimeBankEntry dailyTimeBankEntry) {
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(dailyTimeBankEntry.getEmploymentId(), asDate(java.time.LocalDate.now()));
        List<TimeBankCTADistribution> timeBankCTADistributionList=new ArrayList<>();
        if(isNull(ctaResponseDTO)){
            return timeBankCTADistributionList;
        }
        Set<BigInteger> unusedCtaRuleTemplateId= ctaResponseDTO.getRuleTemplates().stream().filter(ctaRuleTemplateDTO -> UNUSED_DAYOFF_LEAVES.equals(ctaRuleTemplateDTO.getCalculationFor())).map(CTARuleTemplateDTO::getId).collect(toSet());

        if(isCollectionNotEmpty(dailyTimeBankEntry.getTimeBankCTADistributionList())){
            for (TimeBankCTADistribution timeBankCTADistribution : dailyTimeBankEntry.getTimeBankCTADistributionList()) {
                if(unusedCtaRuleTemplateId.contains(timeBankCTADistribution.getCtaRuleTemplateId())){
                    timeBankCTADistributionList.add(timeBankCTADistribution);
                }
            }
        }
        return timeBankCTADistributionList;
    }

    public Double calculateBonusAndUpdateShiftActivity(DateTimeInterval dateTimeInterval,  CTARuleTemplateDTO ruleTemplate, DateTimeInterval shiftInterval, StaffEmploymentDetails staffEmploymentDetails) {
        Double ctaBonusMinutes = 0.0;
        if (isNotNull(shiftInterval)) {
            ctaBonusMinutes = calculateCTARuleTemplateBonus(ruleTemplate, dateTimeInterval, shiftInterval, staffEmploymentDetails);
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
            BigDecimal hourlyCostByDate = getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate());
            BigDecimal functionValue = BigDecimal.valueOf(ctaRuleTemplateDTO.getCalculateValueAgainst().getFixedValue().getAmount());
            value = !hourlyCostByDate.equals(BigDecimal.valueOf(0)) ? functionValue.divide(staffEmploymentDetails.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)).intValue() : 0;
        }
        return value;
    }

    public Double calculateCTARuleTemplateBonus(CTARuleTemplateDTO ctaRuleTemplateDTO, DateTimeInterval dateTimeInterval, DateTimeInterval shiftDateTimeInterval, StaffEmploymentDetails staffEmploymentDetails) {
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
                        } else if (CompensationMeasurementType.FIXED_VALUE.equals(ctaInterval.getCompensationMeasurementType())) {
                            double value = ((double) overlapTimeInMin / ctaRuleTemplateDTO.getCompensationTable().getGranularityLevel()) * ctaInterval.getValue();
                            ctaTimeBankMin += (double) (!getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate()).equals(BigDecimal.valueOf(0)) && staffEmploymentDetails.getHourlyCost().equals(0)? BigDecimal.valueOf(value).divide(staffEmploymentDetails.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)).intValue() : 0);
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

    public void calculateScheduledAndDurationInMinutes(ShiftActivity shiftActivity, Activity activity, StaffEmploymentDetails staffEmploymentDetails,boolean calculateTimeBankOff) {
        if (shiftActivity.getStartDate().after(shiftActivity.getEndDate())) {
            exceptionService.invalidRequestException(ACTIVITY_END_DATE_LESS_THAN_START_DATE, activity.getName());
        }
        int scheduledMinutes = 0;
        int duration = 0;
        int weeklyMinutes;
        switch (activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime()) {
            case ENTERED_MANUALLY:
                duration = shiftActivity.getDurationMinutes();
                scheduledMinutes = Double.valueOf(duration * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                break;
            case FIXED_TIME:
                duration = activity.getActivityTimeCalculationSettings().getFixedTimeValue().intValue();
                scheduledMinutes = Double.valueOf(duration * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                break;
            case ENTERED_TIMES:
                duration = (int) new Interval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime()).toDuration().getStandardMinutes();
                scheduledMinutes = Double.valueOf(duration * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                break;
            case CommonConstants.FULL_DAY_CALCULATION:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getActivityTimeCalculationSettings().getFullDayCalculationType())) ? staffEmploymentDetails.getFullTimeWeeklyMinutes() : staffEmploymentDetails.getTotalWeeklyMinutes();
                duration = Double.valueOf(weeklyMinutes * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = Double.valueOf(staffEmploymentDetails.getTotalWeeklyMinutes() * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case CommonConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getActivityTimeCalculationSettings().getFullWeekCalculationType())) ? staffEmploymentDetails.getFullTimeWeeklyMinutes() : staffEmploymentDetails.getTotalWeeklyMinutes();
                duration = Double.valueOf(weeklyMinutes * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            default:
                break;
        }
        if (TimeTypes.WORKING_TYPE.toString().equals(shiftActivity.getTimeType()) || calculateTimeBankOff) {
            shiftActivity.setDurationMinutes(duration);
            shiftActivity.setScheduledMinutes(scheduledMinutes);
        }
    }

    public void calculateScheduledAndDurationInMinutes(ShiftActivityDTO shiftActivity, ActivityDTO activity, StaffEmploymentDetails staffEmploymentDetails, boolean calculateTimeBankOff) {
        if(isNull(activity)){
            activity = shiftActivity.getActivity();
        }
        if (shiftActivity.getStartDate().after(shiftActivity.getEndDate())) {
            exceptionService.invalidRequestException(ACTIVITY_END_DATE_LESS_THAN_START_DATE, activity.getName());
        }
        int scheduledMinutes = 0;
        int duration = 0;
        int weeklyMinutes;
        switch (activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime()) {
            case ENTERED_MANUALLY:
                duration = shiftActivity.getDurationMinutes();
                scheduledMinutes = Double.valueOf(duration * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                break;
            case FIXED_TIME:
                duration = activity.getActivityTimeCalculationSettings().getFixedTimeValue().intValue();
                scheduledMinutes = Double.valueOf(duration * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                break;
            case ENTERED_TIMES:
                duration = (int) new Interval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime()).toDuration().getStandardMinutes();
                scheduledMinutes = Double.valueOf(duration * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                break;
            case CommonConstants.FULL_DAY_CALCULATION:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getActivityTimeCalculationSettings().getFullDayCalculationType())) ? staffEmploymentDetails.getFullTimeWeeklyMinutes() : staffEmploymentDetails.getTotalWeeklyMinutes();
                duration = Double.valueOf(weeklyMinutes * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case AppConstants.WEEKLY_HOURS:
                duration = Double.valueOf(staffEmploymentDetails.getTotalWeeklyMinutes() * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            case CommonConstants.FULL_WEEK:
                weeklyMinutes = (TimeCalaculationType.FULL_TIME_WEEKLY_HOURS_TYPE.equals(activity.getActivityTimeCalculationSettings().getFullWeekCalculationType())) ? staffEmploymentDetails.getFullTimeWeeklyMinutes() : staffEmploymentDetails.getTotalWeeklyMinutes();
                duration = Double.valueOf(weeklyMinutes * activity.getActivityTimeCalculationSettings().getMultiplyWithValue()).intValue();
                scheduledMinutes = duration;
                break;
            default:
                break;
        }
        if (TimeTypes.WORKING_TYPE.toString().equals(shiftActivity.getTimeType()) || calculateTimeBankOff) {
            shiftActivity.setDurationMinutes(duration);
            shiftActivity.setScheduledMinutes(scheduledMinutes);
        }
    }

    public boolean validateCTARuleTemplate(CTARuleTemplateDTO ctaRuleTemplateDTO, StaffEmploymentDetails staffEmploymentDetails, BigInteger shiftPhaseId, Set<BigInteger> activityIds, Set<BigInteger> timeTypeIds, List<PlannedTime> plannedTimes) {
        return ctaRuleTemplateDTO.isRuleTemplateValid(staffEmploymentDetails.getEmploymentType().getId(), shiftPhaseId, activityIds, timeTypeIds, plannedTimes);
    }

    public boolean isDayTypeValid(Date shiftDate, CTARuleTemplateDTO ruleTemplateDTO, Map<BigInteger, DayTypeDTO> dayTypeDTOMap) {
        List<DayTypeDTO> dayTypeDTOS = ruleTemplateDTO.getDayTypeIds().stream().map(dayTypeDTOMap::get).collect(Collectors.toList());
        boolean valid = false;
        for (DayTypeDTO dayTypeDTO : dayTypeDTOS) {
            if (dayTypeDTO.isHolidayType()) {
                valid = isPublicHolidayValid(shiftDate, valid, dayTypeDTO);
            } else {
                valid = ruleTemplateDTO.getDays() != null && ruleTemplateDTO.getDays().contains(asLocalDate(shiftDate).getDayOfWeek());
            }
            if (valid) {
                break;
            }
        }
        return valid;
    }

    public static boolean isPublicHolidayValid(Date shiftDate, boolean valid, DayTypeDTO dayTypeDTO) {
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
            if (ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT) && !ruleTemplate.getCalculationFor().equals(UNUSED_DAYOFF_LEAVES)) {
                timeBankCTADistributions.add(new TimeBankCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0), ruleTemplate.getId()));
            }
        }
        return timeBankCTADistributions;
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

    public TimeBankDTO getTimeBankOverview(Long unitId, Long employmentId, Date startDate, Date endDate, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<Interval> intervals = getAllIntervalsBetweenDates(startDate, endDate, WEEKLY);
        Map<Interval, List<DailyTimeBankEntry>> intervalTimeBankMap = timeBankAndPayOutCalculationService.getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap = getPayoutTrasactionIntervalsMap(intervals, startDate, endDate, employmentId);
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setEmploymentId(employmentId);
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
        Interval interval = getIntervalValidIntervalForTimebank(employmentWithCtaDetailsDTO, new Interval(startDate.getTime(), endDate.getTime()), planningPeriodInterval);
        List<TimeBankIntervalDTO> weeklyTimeBankIntervals = new ArrayList<>();
        if (isNotNull(interval)) {
            weeklyTimeBankIntervals = getTimeBankIntervals(unitId, startDate, endDate, 0, WEEKLY, intervals, new HashMap<>(), intervalTimeBankMap, null, newArrayList(employmentWithCtaDetailsDTO), payoutTransactionIntervalMap, new HashMap<>());
            weeklyTimeBankIntervals = weeklyTimeBankIntervals.stream().filter(timeBankIntervalDTO -> interval.contains(timeBankIntervalDTO.getStartDate().getTime()) || interval.contains(timeBankIntervalDTO.getEndDate().getTime())).collect(Collectors.toList());
        }
        timeBankDTO.setWeeklyIntervalsTimeBank(weeklyTimeBankIntervals);
        long[] calculatedTimebankValues = timeBankAndPayOutCalculationService.getSumOfTimebankIntervalValues(weeklyTimeBankIntervals);
        timeBankDTO.setTotalContractedMin(calculatedTimebankValues[0]);
        timeBankDTO.setTotalScheduledMin(calculatedTimebankValues[1]);
        timeBankDTO.setTotalTimeBankMin(calculatedTimebankValues[4]);
        timeBankDTO.setTotalPlannedMinutes(calculatedTimebankValues[10]);
        intervals = getAllIntervalsBetweenDates(startDate, endDate, MONTHLY);
        intervalTimeBankMap = timeBankAndPayOutCalculationService.getTimebankIntervalsMap(intervals, dailyTimeBankEntries);
        payoutTransactionIntervalMap = getPayoutTrasactionIntervalsMap(intervals, startDate, endDate, employmentId);
        List<TimeBankIntervalDTO> monthlyTimeBankIntervals = new ArrayList<>();
        if (isNotNull(interval)) {
            monthlyTimeBankIntervals = getTimeBankIntervals(unitId, startDate, endDate, 0, MONTHLY, intervals, new HashMap<>(), intervalTimeBankMap, null, newArrayList(employmentWithCtaDetailsDTO), payoutTransactionIntervalMap, new HashMap<>());
            monthlyTimeBankIntervals = monthlyTimeBankIntervals.stream().filter(timeBankIntervalDTO -> interval.contains(timeBankIntervalDTO.getStartDate().getTime()) || interval.contains(timeBankIntervalDTO.getEndDate().getTime())).collect(Collectors.toList());
        }
        timeBankDTO.setMonthlyIntervalsTimeBank(monthlyTimeBankIntervals);
        timeBankDTO.setHourlyCost(employmentWithCtaDetailsDTO.getHourlyCost());
        return timeBankDTO;
    }

    public TimeBankVisualViewDTO getVisualViewTimeBank(DateTimeInterval interval, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, Map<String, List<TimeType>> presenceAbsenceTimeTypeMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = getScheduledActivities(shifts);
        List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(TimeBankCTADistribution::getCtaName, Collectors.summingInt(TimeBankCTADistribution::getMinutes)));
        List<CTADistributionDTO> timeBankDistributionsDto = getDistributionOfTimeBank(ctaDistributionMap, employmentWithCtaDetailsDTO.getCtaRuleTemplates(), 0,new HashMap<>()).getCtaRuletemplateBonus().getCtaDistributions();
        long presenceScheduledMin = getScheduledMinOfActivityByTimeType(presenceAbsenceTimeTypeMap.get("Presence"), shifts);
        long absenceScheduledMin = getScheduledMinOfActivityByTimeType(presenceAbsenceTimeTypeMap.get("Absence"), shifts);
        long totalTimeBankChange = dailyTimeBankEntries.stream().mapToLong(DailyTimeBankEntry::getDeltaTimeBankMinutes).sum();
        //Todo pradeep please fix when accumulated timebank fucnationality is done
        long accumulatedTimeBankBefore = 0;
        long totalTimeBank = accumulatedTimeBankBefore + totalTimeBankChange;
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = getVisualViewTimebankInterval(dailyTimeBankEntries, interval);
        return new TimeBankVisualViewDTO(totalTimeBank, presenceScheduledMin, absenceScheduledMin, totalTimeBankChange, timeBankIntervalDTOS, scheduledActivitiesDTOS, timeBankDistributionsDto);
    }

    private List<TimeBankIntervalDTO> getVisualViewTimebankInterval(List<DailyTimeBankEntry> dailyTimeBankEntries, DateTimeInterval interval) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>((int) interval.getDays());
        Map<java.time.LocalDate, DailyTimeBankEntry> dailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(DailyTimeBankEntry::getDate, v -> v));
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
        Map<BigInteger, TimeType> timeTypeMap = timeTypes.stream().collect(Collectors.toMap(MongoBaseEntity::getId, v -> v));
        return shifts.stream().flatMap(s -> s.getActivities().stream()).filter(s -> timeTypeMap.containsKey(s.getActivity().getActivityBalanceSettings().getTimeTypeId())).mapToLong(ShiftActivityDTO::getScheduledMinutes).sum();
    }

    private List<ScheduledActivitiesDTO> getScheduledActivities(List<ShiftWithActivityDTO> shifts) {
        Map<String, Long> activityScheduledMin = shifts.stream().flatMap(s -> s.getActivities().stream()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(activity -> activity.getActivity().getId() + "-" + activity.getActivity().getName(), Collectors.summingLong(ShiftActivityDTO::getScheduledMinutes)));
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = new ArrayList<>(activityScheduledMin.size());
        activityScheduledMin.forEach((activity, mintues) -> {
            String[] idNameArray = activity.split("-");
            scheduledActivitiesDTOS.add(new ScheduledActivitiesDTO(new BigInteger(idNameArray[0]), idNameArray[1], mintues));
        });
        return scheduledActivitiesDTOS;
    }


    public Object[] calculateDeltaTimeBankForInterval(DateTimeInterval planningPeriodInterval, Interval interval, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO,Set<DayOfWeek> dayOfWeeks,List<DailyTimeBankEntry> dailyTimeBankEntries, boolean calculateContractual) {
        Map<String,DailyTimeBankEntry> dailyTimeBanksDatesMap = new HashMap<>();
        if (!calculateContractual) {
            dailyTimeBanksDatesMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(d -> toJodaDateTime(d.getDate()).toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId(),v->v));
        }
        interval = getIntervalValidIntervalForTimebank(employmentWithCtaDetailsDTO, interval, planningPeriodInterval);
        //It can be contractual or Delta Timebank minutes it calculate on the basis of calculateContractual param
        BigDecimal cost = BigDecimal.valueOf(0);
        int contractualOrDeltaMinutes = 0;
        if (interval != null) {
            DateTime startDate = interval.getStart();
            while (!startDate.isAfter(interval.getEnd())) {
                if(isCollectionEmpty(dayOfWeeks) || dayOfWeeks.contains(asLocalDate(startDate.toDate()).getDayOfWeek())){
                    if (calculateContractual || !dailyTimeBanksDatesMap.containsKey(startDate.toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId())) {
                        boolean vaild = (employmentWithCtaDetailsDTO.getWorkingDaysInWeek() == 7) || (startDate.getDayOfWeek() != DateTimeConstants.SATURDAY && startDate.getDayOfWeek() != DateTimeConstants.SUNDAY);
                        if (vaild) {
                            int contractualMin = getContractualMinutesByDate(planningPeriodInterval, DateUtils.asLocalDate(startDate), employmentWithCtaDetailsDTO.getEmploymentLines());
                            if(!calculateContractual) {
                                contractualOrDeltaMinutes -= contractualMin;
                            }else {
                                contractualOrDeltaMinutes += contractualMin;
                            }
                            cost = cost.add(getCostByByMinutes(employmentWithCtaDetailsDTO.getEmploymentLines(), contractualMin, asLocalDate(startDate)));
                        }
                    }else if(!calculateContractual && dailyTimeBanksDatesMap.containsKey(startDate.toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId())){
                        int contractualMin =  dailyTimeBanksDatesMap.get(startDate.toLocalDate()+"-"+employmentWithCtaDetailsDTO.getId()).getDeltaTimeBankMinutes();
                        contractualOrDeltaMinutes += contractualMin;
                        cost = cost.add(getCostByByMinutes(employmentWithCtaDetailsDTO.getEmploymentLines(),contractualMin,asLocalDate(startDate)));
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        return new Object[]{contractualOrDeltaMinutes,cost};
    }

    public Interval getIntervalValidIntervalForTimebank(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, Interval interval, DateTimeInterval planningPeriodInterval) {
        DateTime employmentStartTime = toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate());
        DateTime employmentEndTime = toJodaDateTime(isNull(employmentWithCtaDetailsDTO.getEndDate()) ? planningPeriodInterval.getEndLocalDate() : employmentWithCtaDetailsDTO.getEndDate().isBefore(planningPeriodInterval.getEndLocalDate()) ? employmentWithCtaDetailsDTO.getEndDate() : planningPeriodInterval.getEndLocalDate());
        Interval employmentInterval = new Interval(employmentStartTime, employmentEndTime);
        return interval.overlap(employmentInterval);
    }


    public List<TimeBankIntervalDTO> getTimeBankIntervals(Long unitId, Date startDate, Date endDate, long totalTimeBankBefore, String query, List<Interval> intervals, Map<Interval, List<ShiftWithActivityDTO>> shiftsintervalMap, Map<Interval, List<DailyTimeBankEntry>> timeBanksIntervalMap, List<TimeTypeDTO> timeTypeDTOS, List<EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOS, Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap, Map<Interval, List<PayOutPerShift>> payOutsintervalMap) {
        List<TimeBankIntervalDTO> timeBankIntervalDTOS = new ArrayList<>(intervals.size());
        List<PeriodDTO> planningPeriods = planningPeriodService.findAllPeriodsByStartDateAndLastDate(unitId, DateUtils.asLocalDate(startDate), DateUtils.asLocalDate(endDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
        Map<Long,List<EmploymentLinesDTO>> employmentWithCtaDetailsDTOMap = employmentWithCtaDetailsDTOS.stream().filter(distinctByKey(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getId())).collect(Collectors.toMap(k->k.getId(),v->v.getEmploymentLines()));
        int sequence = 1;
        for (Interval interval : intervals) {
            List<PayOutPerShift> payOutPerShifts = payOutsintervalMap.get(interval);
            List<ShiftWithActivityDTO> shifts = shiftsintervalMap.get(interval);
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBanksIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionIntervalMap.get(interval);
            TimeBankIntervalDTO timeBankIntervalDTO = new TimeBankIntervalDTO(interval.getStart().toDate(), query.equals(DAILY) ? interval.getStart().toDate() : interval.getEnd().minusDays(1).toDate(), getPhaseNameByPeriods(planningPeriods, interval.getStart()));
            Object[] timeBankAndCostDetail = getTimebankAndContractualAndTimebankCost(employmentWithCtaDetailsDTOS,dailyTimeBankEntries,planningPeriodInterval,interval);
            long timeBankOfInterval = (Long) timeBankAndCostDetail[0];
            long contractualMin = (Long)timeBankAndCostDetail[1];
            BigDecimal totalTimebankCost = (BigDecimal) timeBankAndCostDetail[2];
            BigDecimal totalContractualCost = (BigDecimal) timeBankAndCostDetail[3];
            timeBankIntervalDTO.setTotalContractedMin(contractualMin);
            Long approvePayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p -> (long) p.getMinutes()).sum();
            Long requestPayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.REQUESTED)).mapToLong(p -> (long) p.getMinutes()).sum();
            Long paidPayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.PAIDOUT)).mapToLong(p -> (long) p.getMinutes()).sum();
            timeBankIntervalDTO.setApprovePayOut(approvePayOut);
            timeBankIntervalDTO.setSequence(sequence++);
            timeBankIntervalDTO.setRequestPayOut(requestPayOut);
            timeBankIntervalDTO.setPaidoutChange(paidPayOut);
            timeBankIntervalDTO.setTotalContractedCost(totalContractualCost.floatValue());
            timeBankIntervalDTO.setTotalTimeBankDiffCost(totalTimebankCost.floatValue());
            timeBankIntervalDTO.setHeaderName(getHeaderName(query, interval));
            List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = employmentWithCtaDetailsDTOS.stream().flatMap(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getCtaRuleTemplates().stream()).filter(distinctByKey(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getName())).collect(toList());
            if (isCollectionNotEmpty(dailyTimeBankEntries)) {
                totalTimeBankBefore = getTotalTimeBankDetailsByInterval(totalTimeBankBefore, query, timeTypeDTOS, interval, payOutPerShifts, shifts, dailyTimeBankEntries, timeBankIntervalDTO, timeBankOfInterval, approvePayOut, ctaRuleTemplateDTOS,employmentWithCtaDetailsDTOMap);
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            } else {
                totalTimeBankBefore -= timeBankOfInterval;
                updateTimebankIntervalWithDefaultValue(totalTimeBankBefore, query, timeTypeDTOS, ctaRuleTemplateDTOS, interval, shifts, timeBankIntervalDTO, timeBankOfInterval, approvePayOut);
                timeBankIntervalDTOS.add(timeBankIntervalDTO);
            }
        }
        return timeBankIntervalDTOS;
    }

    private Object[] getTimebankAndContractualAndTimebankCost(List<EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOS,List<DailyTimeBankEntry> dailyTimeBankEntries,DateTimeInterval planningPeriodInterval,Interval interval){
        long totalTimebank = 0;
        long totalContractual = 0;
        BigDecimal totalTimebankCost = BigDecimal.valueOf(0);
        BigDecimal totalContractualCost = BigDecimal.valueOf(0);
        Map<Long,List<DailyTimeBankEntry>> dailyTimeBankMap = dailyTimeBankEntries.stream().collect(groupingBy(dailyTimeBankEntry -> dailyTimeBankEntry.getEmploymentId()));
        for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : employmentWithCtaDetailsDTOS) {
            List<DailyTimeBankEntry> employmentDailyTimeBankEntries = dailyTimeBankMap.getOrDefault(employmentWithCtaDetailsDTO.getId(),new ArrayList<>());
            Object[] deltaTimebankAndCost =  calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, new HashSet<>(),employmentDailyTimeBankEntries , false);
            totalTimebank += (int)deltaTimebankAndCost[0];
            Object[] contractualAndCost = calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, new HashSet<>(), employmentDailyTimeBankEntries, true);
            totalContractual += (int)contractualAndCost[0];
            totalTimebankCost = totalTimebankCost.add((BigDecimal) deltaTimebankAndCost[1]);
            totalContractualCost = totalContractualCost.add((BigDecimal) contractualAndCost[1]);
        }
        return new Object[]{totalTimebank,totalContractual,totalTimebankCost,totalContractualCost};
    }

    private long getTotalTimeBankDetailsByInterval(long totalTimeBankBefore, String query, List<TimeTypeDTO> timeTypeDTOS, Interval interval, List<PayOutPerShift> payOutPerShifts, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, TimeBankIntervalDTO timeBankIntervalDTO, long timeBankOfInterval, Long approvePayOut, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS,Map<Long,List<EmploymentLinesDTO>> employmentWithCtaDetailsDTOMap) {
        Object[] calculatedTimebankValues = getSumOfTimebankValues(dailyTimeBankEntries,employmentWithCtaDetailsDTOMap);
        long plannedMinutesOfTimebank = (long)calculatedTimebankValues[1];
        long scheduledMinutesOfTimebank = (long)calculatedTimebankValues[2];
        BigDecimal plannedTimebankCost = (BigDecimal) calculatedTimebankValues[3];
        long timeBankOffMinutes = (Long) calculatedTimebankValues[4];
        Object[] calculatedPayoutValues = getSumOfPayoutValues(payOutPerShifts,employmentWithCtaDetailsDTOMap);
        long plannedMinutesOfPayout = (long)calculatedPayoutValues[0];
        long scheduledMinutesOfPayout = (long)calculatedPayoutValues[1];
        long protectedDaysOffMinutes = (long)calculatedTimebankValues[5];
        BigDecimal plannedPayoutCost = (BigDecimal) calculatedPayoutValues[2];
        totalTimeBankBefore = getTotalTimeBankBeforeAndUpdateTimebankInterval(query,totalTimeBankBefore, timeTypeDTOS, interval, shifts, dailyTimeBankEntries, timeBankIntervalDTO, timeBankOfInterval, approvePayOut, ctaRuleTemplateDTOS, plannedMinutesOfTimebank, scheduledMinutesOfTimebank, plannedTimebankCost, timeBankOffMinutes, plannedMinutesOfPayout, scheduledMinutesOfPayout, protectedDaysOffMinutes, plannedPayoutCost);
        return totalTimeBankBefore;
    }

    private long getTotalTimeBankBeforeAndUpdateTimebankInterval(String query,long totalTimeBankBefore, List<TimeTypeDTO> timeTypeDTOS, Interval interval, List<ShiftWithActivityDTO> shifts, List<DailyTimeBankEntry> dailyTimeBankEntries, TimeBankIntervalDTO timeBankIntervalDTO, long timeBankOfInterval, Long approvePayOut, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS, long plannedMinutesOfTimebank, long scheduledMinutesOfTimebank, BigDecimal plannedTimebankCost, long timeBankOffMinutes, long plannedMinutesOfPayout, long scheduledMinutesOfPayout, long protectedDaysOffMinutes, BigDecimal plannedPayoutCost) {
        timeBankIntervalDTO.setTitle(getTitle(query, interval,false));
        timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore);
        totalTimeBankBefore += timeBankOfInterval;
        timeBankIntervalDTO.setProtectedDaysOffMinutes(protectedDaysOffMinutes);
        timeBankIntervalDTO.setTotalPlannedMinutes(plannedMinutesOfTimebank + plannedMinutesOfPayout);
        timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore - approvePayOut);
        timeBankIntervalDTO.setTotalTimeBankMin(timeBankOfInterval - approvePayOut);
        timeBankIntervalDTO.setTotalTimeBankDiff(timeBankOfInterval - approvePayOut);
        timeBankIntervalDTO.setTotalScheduledMin(scheduledMinutesOfTimebank + scheduledMinutesOfPayout);
        timeBankIntervalDTO.setTotalPlannedCost(plannedTimebankCost.add(plannedPayoutCost).floatValue());
        List<TimeBankCTADistribution> timeBankDistributions = dailyTimeBankEntries.stream().filter(tb -> (interval.getStart().equals(DateUtils.toJodaDateTime(tb.getDate())) || interval.contains(asDate(tb.getDate()).getTime()))).flatMap(tb -> tb.getTimeBankCTADistributionList().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(TimeBankCTADistribution::getCtaName, Collectors.summingInt(TimeBankCTADistribution::getMinutes)));
        Map<String, Double> ctaCostDistributionMap = timeBankDistributions.stream().collect(Collectors.groupingBy(TimeBankCTADistribution::getCtaName, Collectors.summingDouble(TimeBankCTADistribution::getCost)));
        timeBankIntervalDTO.setTimeBankDistribution(getDistributionOfTimeBank(ctaDistributionMap, ctaRuleTemplateDTOS, plannedMinutesOfTimebank,ctaCostDistributionMap));
        timeBankIntervalDTO.setWorkingTimeType(isNotNull(timeTypeDTOS) ? timeBankAndPayOutCalculationService.getWorkingTimeType(interval, shifts, timeTypeDTOS) : null);
        return totalTimeBankBefore;
    }

    private void updateTimebankIntervalWithDefaultValue(long totalTimeBankBefore, String query, List<TimeTypeDTO> timeTypeDTOS, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS, Interval interval, List<ShiftWithActivityDTO> shifts, TimeBankIntervalDTO timeBankIntervalDTO, long timeBankOfInterval, Long approvePayOut) {
        timeBankIntervalDTO.setTotalTimeBankAfterCtaMin(totalTimeBankBefore - approvePayOut);
        timeBankIntervalDTO.setTotalTimeBankBeforeCtaMin(totalTimeBankBefore + timeBankOfInterval);
        timeBankIntervalDTO.setTotalTimeBankMin(timeBankOfInterval + approvePayOut);
        timeBankIntervalDTO.setTotalTimeBankDiff(timeBankOfInterval + approvePayOut);
        timeBankIntervalDTO.setTitle(getTitle(query, interval,false));
        timeBankIntervalDTO.setTimeBankDistribution(getDistributionOfTimeBank(new HashMap<>(), ctaRuleTemplateDTOS, 0,new HashMap<>()));
        timeBankIntervalDTO.setWorkingTimeType(isNotNull(timeTypeDTOS) ? timeBankAndPayOutCalculationService.getWorkingTimeType(interval, shifts, timeTypeDTOS) : null);
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
                return getTitle(query, interval,true);
            }
        }
        return null;
    }

    public TimeBankCTADistributionDTO getDistributionOfTimeBank(Map<String, Integer> ctaDistributionMap, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS, long plannedMinutesOfTimebank,Map<String, Double> ctaCostDistributionMap) {
        List<CTADistributionDTO> timeBankCTADistributionDTOS = new ArrayList<>();
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : ctaRuleTemplateDTOS) {
            if (ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT)) {
                if (newHashSet(BONUS_HOURS,FUNCTIONS,UNUSED_DAYOFF_LEAVES,CONDITIONAL_BONUS).contains(ctaRuleTemplate.getCalculationFor())) {
                    CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0),ctaCostDistributionMap.getOrDefault(ctaRuleTemplate.getName(), Double.valueOf(0.0)).intValue());
                    timeBankCTADistributionDTOS.add(ctaDistributionDTO);
                    ctaBonusMinutes += ctaDistributionDTO.getMinutes();
                } else if (ctaRuleTemplate.getCalculationFor().equals(SCHEDULED_HOURS)) {
                    scheduledCTADistributions.add(new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0),ctaCostDistributionMap.getOrDefault(ctaRuleTemplate.getName(), Double.valueOf(0.0)).intValue()));
                }
            }
        }
        return new TimeBankCTADistributionDTO(scheduledCTADistributions, new CTARuletemplateBonus(timeBankCTADistributionDTOS, ctaBonusMinutes), plannedMinutesOfTimebank);
    }

    private String getTitle(String query, Interval interval,boolean isHeader) {
        switch (query) {
            case DAILY:
                return interval.getStart().toLocalDate().toString();
            case WEEKLY:
                return StringUtils.capitalize(WEEKLY) + " " + interval.getStart().getWeekOfWeekyear()+(isHeader ? "" : " - "+interval.getStart().getYear());
            case MONTHLY:
                return interval.getStart().monthOfYear().getAsText()+(isHeader ? "" : " - "+interval.getStart().getYear());
            case ANNUALLY:
                return StringUtils.capitalize(AppConstants.YEAR) + " " + interval.getStart().getYear();
            case QUATERLY:
                return StringUtils.capitalize(AppConstants.QUARTER) + " " + getQuaterNumberByDate(interval.getStart()) + (isHeader ? "" : " - "+interval.getStart().getYear());
            default:
                break;

        }
        return "";
    }

    private int getQuaterNumberByDate(DateTime dateTime) {
        return (int) Math.ceil((double) dateTime.getMonthOfYear() / 3);
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
                    nextEndDay = startDateTime;
                    break;
            }
            intervals.add(new Interval(startDateTime, nextEndDay.isAfter(endDateTime) ? endDateTime.minusMillis(1) : nextEndDay.minusMillis(1)));
            startDateTime = nextEndDay;
        }
        if (!startDateTime.equals(endDateTime) && startDateTime.isBefore(endDateTime)) {
            intervals.add(new Interval(startDateTime, endDateTime.minusMillis(1)));
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

    public TreeMap<java.time.LocalDate, TimeBankIntervalDTO> getAccumulatedTimebankDTO(LocalDate firstRequestPhasePlanningPeriodEndDate, DateTimeInterval planningPeriodInterval, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, LocalDate startDate, LocalDate endDate, long actualTimebankMinutes, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS, ShiftDataHelper shiftDataHelper) {
        long expectedTimebankMinutes = actualTimebankMinutes;
        java.time.LocalDate employmentStartDate = employmentWithCtaDetailsDTO.getStartDate();
        Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(DailyTimeBankEntry::getDate, v -> v));
        TreeMap<java.time.LocalDate, TimeBankIntervalDTO> localDateTimeBankByDateDTOMap = new TreeMap<>();
        endDate = isNull(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : endDate.isBefore(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : employmentWithCtaDetailsDTO.getEndDate();
        Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap = shiftDataHelper.getDateAndPhaseDefaultName();
        Set<PhaseDefaultName> validPhaseForActualTimeBank = newHashSet(PUZZLE, CONSTRUCTION,REQUEST);
        Map<java.time.LocalDate, Boolean> publishPlanningPeriodDateMap = shiftDataHelper.getDateAndPublishPlanningPeriod();
        while (employmentStartDate.isBefore(endDate) || employmentStartDate.equals(endDate)) {
            int totalTimeBankMinutes;
            long publishedBalancesMinutes = 0;
            DailyTimeBankEntry dailyTimeBankEntry;
            Map<String, Integer> ctaRuletemplateNameAndMinutesMap = new HashMap<>();
            if (dateDailyTimeBankEntryMap.containsKey(employmentStartDate)) {
                dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                totalTimeBankMinutes = getDeltaTimebankByUserAccessRole(dailyTimeBankEntry);
                publishedBalancesMinutes = dailyTimeBankEntry.getPublishedBalances().values().stream().mapToLong(value -> value).sum();
                ctaRuletemplateNameAndMinutesMap = dailyTimeBankEntry.getTimeBankCTADistributionList().stream().collect(Collectors.groupingBy(TimeBankCTADistribution::getCtaName, Collectors.summingInt(TimeBankCTADistribution::getMinutes)));
            } else {
                totalTimeBankMinutes = -getContractualMinutesByDate(planningPeriodInterval, employmentStartDate, employmentWithCtaDetailsDTO.getEmploymentLines());
            }
            if (employmentStartDate.isAfter(LocalDate.now()) && (!employmentStartDate.isAfter(firstRequestPhasePlanningPeriodEndDate) && !publishPlanningPeriodDateMap.get(employmentStartDate) && validPhaseForActualTimeBank.contains(datePhaseDefaultNameMap.get(employmentStartDate)))) {
                expectedTimebankMinutes += totalTimeBankMinutes;
            }
            CTARuletemplateBonus ctaRuletemplateBonus = timeBankAndPayOutCalculationService.getCTABonusDistributions(ctaRuletemplateNameAndMinutesMap, ctaRuleTemplateDTOS);
            TimeBankCTADistributionDTO timeBankCTADistributionDTO = new TimeBankCTADistributionDTO(newArrayList(), ctaRuletemplateBonus, 0);
            if (employmentStartDate.isAfter(startDate) || startDate.equals(employmentStartDate)) {
                localDateTimeBankByDateDTOMap.put(employmentStartDate, new TimeBankIntervalDTO(totalTimeBankMinutes, 0, expectedTimebankMinutes, publishedBalancesMinutes, timeBankCTADistributionDTO));
            }
            employmentStartDate = employmentStartDate.plusDays(1);
        }
        return localDateTimeBankByDateDTOMap;
    }

    private int getDeltaTimebankByUserAccessRole(DailyTimeBankEntry dailyTimeBankEntry) {
        int deltaTimebankMinutes;
        if (UserContext.getUserDetails().isManagement() && isNotNull(dailyTimeBankEntry.getDraftDailyTimeBankEntry())) {
            deltaTimebankMinutes = dailyTimeBankEntry.getDraftDailyTimeBankEntry().getDeltaTimeBankMinutes();
        } else {
            deltaTimebankMinutes = dailyTimeBankEntry.getDeltaTimeBankMinutes();
        }
        return deltaTimebankMinutes;
    }

    public BigDecimal getHourlyCostByDate(List<EmploymentLinesDTO> employmentLines, java.time.LocalDate localDate) {
        BigDecimal hourlyCost = BigDecimal.valueOf(0);
        for (EmploymentLinesDTO employmentLine : employmentLines) {
            DateTimeInterval positionInterval = employmentLine.getInterval();
            if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(asDate(localDate)) || employmentLine.getEndDate().equals(localDate)))) {
                hourlyCost = employmentLine.getHourlyCost();
                break;
            }
        }
        return hourlyCost;
    }

    private Object[] getSumOfTimebankValues(List<DailyTimeBankEntry> dailyTimeBankEntries,Map<Long,List<EmploymentLinesDTO>> employmentWithCtaDetailsDTOMap) {
        long calculatedTimeBank = 0l;
        long plannedMinutesOfTimebank = 0l;
        long scheduledMinutes = 0l;
        BigDecimal plannedTimebankCost = BigDecimal.valueOf(0);
        long timeBankOffMinutes = 0l;
        long protectedDaysOffMinutes = 0l;
        for (DailyTimeBankEntry dailyTimeBankEntry : dailyTimeBankEntries) {
            calculatedTimeBank += dailyTimeBankEntry.getDeltaTimeBankMinutes();
            plannedMinutesOfTimebank += dailyTimeBankEntry.getPlannedMinutesOfTimebank();
            scheduledMinutes += dailyTimeBankEntry.getScheduledMinutesOfTimeBank();
            plannedTimebankCost = plannedTimebankCost.add(getCostByByMinutes(employmentWithCtaDetailsDTOMap.get(dailyTimeBankEntry.getEmploymentId()),dailyTimeBankEntry.getPlannedMinutesOfTimebank(),dailyTimeBankEntry.getDate()));
            for (TimeBankCTADistribution timeBankCTADistribution : dailyTimeBankEntry.getTimeBankCTADistributionList()) {
                timeBankCTADistribution.setCost(getCostByByMinutes(employmentWithCtaDetailsDTOMap.get(dailyTimeBankEntry.getEmploymentId()),timeBankCTADistribution.getMinutes(),dailyTimeBankEntry.getDate()).floatValue());
            }
            timeBankOffMinutes += dailyTimeBankEntry.getTimeBankOffMinutes();
            protectedDaysOffMinutes += dailyTimeBankEntry.getProtectedDaysOffMinutes();
        }
        return new Object[]{calculatedTimeBank, plannedMinutesOfTimebank, scheduledMinutes,plannedTimebankCost,timeBankOffMinutes,protectedDaysOffMinutes};

    }

    public BigDecimal getCostByByMinutes(List<EmploymentLinesDTO> employmentLinesDTOS, int minutes, java.time.LocalDate date){
        BigDecimal hourlyCost = getHourlyCostByDate(employmentLinesDTOS,date);
        BigDecimal oneMinuteCost = hourlyCost.divide(BigDecimal.valueOf(60),BigDecimal.ROUND_CEILING,6);
        return hourlyCost.multiply(BigDecimal.valueOf(getHourByMinutes(minutes))).add(oneMinuteCost.multiply(BigDecimal.valueOf(getHourMinutesByMinutes(minutes))));
    }
    private Object[] getSumOfPayoutValues(List<PayOutPerShift> payOutPerShifts,Map<Long,List<EmploymentLinesDTO>> employmentWithCtaDetailsDTOMap) {
        long plannedMinutesOfPayout = 0l;
        long scheduledMinutesOfPayout = 0l;
        BigDecimal plannedPayoutCost = BigDecimal.valueOf(0);
        if (isCollectionNotEmpty(payOutPerShifts)) {
            for (PayOutPerShift payOutPerShift : payOutPerShifts) {
                scheduledMinutesOfPayout += payOutPerShift.getScheduledMinutes();
                plannedMinutesOfPayout += payOutPerShift.getCtaBonusMinutesOfPayOut() + payOutPerShift.getScheduledMinutes();
                plannedPayoutCost = plannedPayoutCost.add(getCostByByMinutes(employmentWithCtaDetailsDTOMap.get(payOutPerShift.getEmploymentId()),(int)plannedMinutesOfPayout,payOutPerShift.getDate()));
            }
        }
        return new Object[]{plannedMinutesOfPayout, scheduledMinutesOfPayout,plannedPayoutCost};
    }

    public Map<java.time.LocalDate, PhaseDefaultName> getDatePhaseDefaultName(java.time.LocalDate startDate, java.time.LocalDate endDate, Long unitId) {
        LocalDateTime startDateTime = startDate.atTime(LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MIDNIGHT);
        Set<LocalDateTime> localDateTimes = new HashSet<>();
        while (startDateTime.isBefore(endDateTime) || startDateTime.equals(endDateTime)) {
            localDateTimes.add(startDateTime);
            startDateTime = startDateTime.plusDays(1);
        }
        Map<Date, Phase> phasesByDates = phaseService.getPhasesByDates(unitId, localDateTimes);
        if(isNull(phasesByDates)){
            exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PHASES);
        }
        return phasesByDates.entrySet().stream().collect(Collectors.toMap(k -> asLocalDate(k.getKey()), v -> v.getValue().getPhaseEnum()));
    }

    public Map<java.time.LocalDate, Boolean> getDateWisePublishPlanningPeriod(Long employmentTypeId, java.time.LocalDate startDate, java.time.LocalDate endDate, Long unitId) {
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


    public DailyTimeBankEntry updatePublishedBalances(DailyTimeBankEntry dailyTimeBankEntry, List<EmploymentLinesDTO> employmentLines, Long unitId) {
        DailyTimeBankEntry todayDailyTimeBankEntry = dailyTimeBankEntry.getDate().equals(LocalDate.now()) ? dailyTimeBankEntry : timeBankRepository.findByEmploymentAndDate(dailyTimeBankEntry.getEmploymentId(), java.time.LocalDate.now());
        if (isNull(todayDailyTimeBankEntry)) {
            DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
            int contractualMinutes = getContractualMinutesByDate(planningPeriodInterval, java.time.LocalDate.now(), employmentLines);
            todayDailyTimeBankEntry = new DailyTimeBankEntry(dailyTimeBankEntry.getEmploymentId(), dailyTimeBankEntry.getStaffId(), java.time.LocalDate.now());
            todayDailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(-contractualMinutes);
            todayDailyTimeBankEntry.setContractualMinutes(contractualMinutes);
            todayDailyTimeBankEntry.setDeltaTimeBankMinutes(-contractualMinutes);
        }
        todayDailyTimeBankEntry.getPublishedBalances().put(dailyTimeBankEntry.getDate(), dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes());
        return timeBankRepository.save(todayDailyTimeBankEntry);
    }

    public Long calculateActualTimebank(DateTimeInterval dateTimeInterval, List<DailyTimeBankEntry> dailyTimeBankEntries, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, java.time.LocalDate endDate, java.time.LocalDate employmentStartDate, ShiftDataHelper shiftDataHelper) {
        Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap = shiftDataHelper.getDatePhaseDefaultName();
        Map<java.time.LocalDate, Boolean> publishPlanningPeriodDateMap = shiftDataHelper.getDateAndPublishPlanningPeriod(employmentWithCtaDetailsDTO.getEmploymentTypeId());
        Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(DailyTimeBankEntry::getDate, v -> v));
        return getActualTimebank(dateTimeInterval, employmentWithCtaDetailsDTO, endDate, employmentStartDate, publishPlanningPeriodDateMap, dateDailyTimeBankEntryMap, datePhaseDefaultNameMap);
    }

    private long getActualTimebank(DateTimeInterval dateTimeInterval, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, java.time.LocalDate endDate, java.time.LocalDate employmentStartDate, Map<java.time.LocalDate, Boolean> publishPlanningPeriodDateMap, Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap, Map<java.time.LocalDate, PhaseDefaultName> datePhaseDefaultNameMap) {
        long actualTimebank = employmentWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
        endDate = isNull(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : endDate.isBefore(employmentWithCtaDetailsDTO.getEndDate()) ? endDate : employmentWithCtaDetailsDTO.getEndDate();
        Set<PhaseDefaultName> validPhaseForActualTimeBank = newHashSet(REALTIME, TIME_ATTENDANCE, PAYROLL);
        while (employmentStartDate.isBefore(endDate) || employmentStartDate.equals(endDate)) {
            int deltaTimeBankMinutes = (-getContractualMinutesByDate(dateTimeInterval, employmentStartDate, employmentWithCtaDetailsDTO.getEmploymentLines()));
            if (dateDailyTimeBankEntryMap.containsKey(employmentStartDate) && dateDailyTimeBankEntryMap.get(employmentStartDate).isPublishedSomeActivities()) {
                DailyTimeBankEntry dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                deltaTimeBankMinutes = dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes();
                actualTimebank += deltaTimeBankMinutes;
            } else if (validPhaseForActualTimeBank.contains(datePhaseDefaultNameMap.get(employmentStartDate)) || publishPlanningPeriodDateMap.get(employmentStartDate)) {
                actualTimebank += deltaTimeBankMinutes;
            }
            if(dateDailyTimeBankEntryMap.containsKey(employmentStartDate)){
                DailyTimeBankEntry dailyTimeBankEntry = dateDailyTimeBankEntryMap.get(employmentStartDate);
                actualTimebank -= dailyTimeBankEntry.getTimeBankOffMinutes();
                actualTimebank += dailyTimeBankEntry.getProtectedDaysOffMinutes();
            }
            employmentStartDate = employmentStartDate.plusDays(1);
        }
        return actualTimebank;
    }
}
