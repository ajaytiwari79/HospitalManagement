package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta_compensation_setting.CTACompensationConfiguration;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.TimeBankDistributionDTO;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.cta.AccountType;
import com.kairos.enums.cta.CompensationType;
import com.kairos.enums.cta.ConditionalCompensationType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.MINIMUM_VALUE;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.PAID_OUT;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;
import static com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService.getCutoffInterval;
import static java.util.stream.Collectors.*;

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
    private Map<BigInteger, DayTypeDTO> dayTypeDTOMap;
    private TimeBankCalculationService timeBankCalculationService;

    public CalculatePlannedHoursAndScheduledHours(StaffAdditionalInfoDTO staffAdditionalInfoDTO, DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, boolean validatedByPlanner, boolean anyShiftPublish, Map<BigInteger, DayTypeDTO> dayTypeDTOMap, TimeBankCalculationService timeBankCalculationService) {
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
        this.timeBankCalculationService = timeBankCalculationService;
    }

    public CalculatePlannedHoursAndScheduledHours(TimeBankCalculationService timeBankCalculationService) {
        this.timeBankCalculationService = timeBankCalculationService;
        this.ctaTimeBankMinMap = new HashMap<>();
    }

    public CalculatePlannedHoursAndScheduledHours calculate() {
        boolean ruleTemplateValid = false;
        for (CTARuleTemplateDTO ruleTemplate : staffAdditionalInfoDTO.getEmployment().getCtaRuleTemplates()) {
            for (ShiftWithActivityDTO shift : shifts) {
                if (shift.getStatuses().contains(ShiftStatus.PUBLISH) && CalculationFor.CONDITIONAL_BONUS.equals(ruleTemplate.getCalculationFor()) && (isTimeSlotChanged(shift) || isNull(shift.getId()))) {
                    calculateConditionalBonus(ruleTemplate,staffAdditionalInfoDTO.getEmployment(),shift, TIMEBANK_ACCOUNT);
                }else {
                    ruleTemplateValid = calculateBonusOrScheduledMinutesByShiftActivity(ruleTemplateValid, ruleTemplate, shift);
                }
            }
            if (ruleTemplate.getCalculationFor().equals(FUNCTIONS) && ruleTemplateValid) {
                int value = timeBankCalculationService.getFunctionalBonusCompensation(staffAdditionalInfoDTO.getEmployment(), ruleTemplate, dateTimeInterval);
                ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + value);
                totalDailyPlannedMinutes += value;
            }
        }
        return this;
    }

    public boolean isTimeSlotChanged(ShiftWithActivityDTO shift) {
        return !staffAdditionalInfoDTO.getTimeSlotByShiftStartTime(shift.getStartDate()).equals(shift.getOldShiftTimeSlot());
    }

    private boolean calculateBonusOrScheduledMinutesByShiftActivity(boolean ruleTemplateValid, CTARuleTemplateDTO ruleTemplate, ShiftWithActivityDTO shift) {
        List<ShiftActivityDTO> shiftActivities = getShiftActivityByBreak(shift.getActivities(), shift.getBreakActivities());
        for (ShiftActivityDTO shiftActivity : shiftActivities) {
            ShiftActivityDTO shiftActivityDTO = getShiftActivityDTO(shift, shiftActivity);
            if (isNotNull(shiftActivityDTO)) {
                ruleTemplateValid = timeBankCalculationService.validateCTARuleTemplate(ruleTemplate, staffAdditionalInfoDTO.getEmployment(), shift.getPhaseId(), newHashSet(shiftActivity.getActivity().getId()), newHashSet(shiftActivity.getActivity().getActivityBalanceSettings().getTimeTypeId()), shiftActivity.getPlannedTimes()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(TIMEBANK_ACCOUNT);
                LOGGER.debug("rule template : {} valid {}", ruleTemplate.getId(), ruleTemplateValid);
                if (ruleTemplateValid) {
                    updateScheduledAndBonusMinutes(ruleTemplate, shift, shiftActivity, shiftActivityDTO);
                }
            }
        }
        return ruleTemplateValid;
    }

    public double calculateConditionalBonus(CTARuleTemplateDTO ruleTemplate, StaffEmploymentDetails employment, ShiftWithActivityDTO shift, AccountType accountType) {
        boolean ruleTemplateValid = timeBankCalculationService.validateCTARuleTemplate(ruleTemplate, staffAdditionalInfoDTO.getEmployment(), shift.getPhaseId(), shift.getActivityIds(), shift.getActivitiesTimeTypeIds(), shift.getActivitiesPlannedTimes()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(accountType);
        double compensation = 0;
        if(ruleTemplateValid && ruleTemplate.getConditionalCompensation().getConditionalCompensationTypes().contains(ConditionalCompensationType.MANUAL_PLANNING) && CollectionUtils.containsAny(ruleTemplate.getCalculateValueIfPlanned(),staffAdditionalInfoDTO.getCalculateValueIfPlanneds())) {
            ZonedDateTime todayDate = asZonedDateTime(LocalDate.now()).with(LocalTime.MIN);
            for (CTACompensationConfiguration ctaCompensationConfiguration : ruleTemplate.getCalculateValueAgainst().getCtaCompensationConfigurations()) {
                ZonedDateTime startDate = getDateByIntervalType(ctaCompensationConfiguration.getIntervalType(), ctaCompensationConfiguration.getFrom(), todayDate);
                ZonedDateTime endDate = getDateByIntervalType(ctaCompensationConfiguration.getIntervalType(), ctaCompensationConfiguration.getTo(), todayDate).with(LocalTime.MAX);
                DateTimeInterval dateTimeInterval = new DateTimeInterval(ctaCompensationConfiguration.getFrom()==0 ? todayDate.minusDays(2) : startDate, ctaCompensationConfiguration.getTo()==0 ? asZonedDateTime(shift.getEndDate()) : endDate);
                if (dateTimeInterval.contains(shift.getStartDate())) {
                    if (CompensationType.HOURS.equals(ctaCompensationConfiguration.getCompensationType())) {
                        compensation = ctaCompensationConfiguration.getValue();
                        ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaCompensationConfiguration.getValue());
                        totalDailyPlannedMinutes += ctaCompensationConfiguration.getValue();
                    } else {
                        BigDecimal hourlyCostByDate = timeBankCalculationService.getHourlyCostByDate(employment.getEmploymentLines(),asLocalDate(shift.getStartDate()));
                        int value = !hourlyCostByDate.equals(BigDecimal.valueOf(0)) ? BigDecimal.valueOf(ctaCompensationConfiguration.getValue()).divide(employment.getHourlyCost(), BigDecimal.ROUND_CEILING,5).multiply(BigDecimal.valueOf(60)).intValue() : 0;
                        ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0) + value);
                        totalDailyPlannedMinutes += value;
                        compensation = value;
                    }
                }
            }
        }
        return compensation;
    }



    private void updateScheduledAndBonusMinutes(CTARuleTemplateDTO ruleTemplate, ShiftWithActivityDTO shift, ShiftActivityDTO shiftActivity, ShiftActivityDTO shiftActivityDTO) {
        int ctaBonusAndScheduledMinutes = 0;
        if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && dateTimeInterval.contains(shiftActivity.getStartDate().getTime()) && timeBankCalculationService.isDayTypeValid(shiftActivity.getStartDate(),ruleTemplate,dayTypeDTOMap)) {
            scheduledMinutesOfTimeBank += shiftActivity.getScheduledMinutes();
            ctaBonusAndScheduledMinutes = shiftActivity.getScheduledMinutes();
            shiftActivityDTO.setScheduledMinutesOfTimebank(shiftActivity.getScheduledMinutes() + shiftActivityDTO.getScheduledMinutesOfTimebank());
        } else if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
            ctaBonusAndScheduledMinutes = (int) Math.round(getAndUpdateCtaBonusMinutes(dateTimeInterval, ruleTemplate, shiftActivity, staffAdditionalInfoDTO.getEmployment(),dayTypeDTOMap));
            Optional<TimeBankDistributionDTO> optionalTimeBankDistributionDTO = shiftActivityDTO.getTimeBankCTADistributions().stream().filter(distributionDTO -> distributionDTO.getCtaRuleTemplateId().equals(ruleTemplate.getId())).findAny();
            if (optionalTimeBankDistributionDTO.isPresent()) {
                optionalTimeBankDistributionDTO.get().setMinutes(optionalTimeBankDistributionDTO.get().getMinutes() + ctaBonusAndScheduledMinutes);
            } else {
                TimeBankDistributionDTO timeBankDistributionDTO = new TimeBankDistributionDTO(ruleTemplate.getName(), ruleTemplate.getId(), DateUtils.asLocalDate(getDate()), ctaBonusAndScheduledMinutes);
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
    }


    public ShiftActivityDTO getShiftActivityDTO(ShiftWithActivityDTO shift, ShiftActivityDTO shiftActivity) {
        ShiftActivityDTO shiftActivityDTO;
        try {
            return shift.getActivities().stream().filter(shiftActivityDTO1 -> shiftActivityDTO1.getId().equals(shiftActivity.getId()) || (isCollectionNotEmpty(shift.getBreakActivities()) && shift.getBreakActivities().get(0).getId().equals(shiftActivityDTO1.getId()))).findAny().get();
        } catch (NullPointerException | NoSuchElementException e) {
            shiftActivityDTO = shiftActivity;
        }
        return shiftActivityDTO;
    }

    public List<ShiftActivityDTO> getShiftActivityByBreak(List<ShiftActivityDTO> shiftActivities, List<ShiftActivityDTO> breakActivities) {
        List<ShiftActivityDTO> updatedShiftActivities = new ArrayList<>();
        if(isCollectionNotEmpty(breakActivities)){
            for (ShiftActivityDTO shiftActivity : shiftActivities) {
                boolean scheduledHourAdded = false;
                boolean anybreakFallOnShiftActivity = breakActivities.stream().anyMatch(breakActivity -> shiftActivity.getInterval().overlaps(breakActivity.getInterval()) && shiftActivity.getInterval().overlap(breakActivity.getInterval()).getMinutes() == breakActivity.getInterval().getMinutes() && !breakActivity.isBreakNotHeld());
                if(anybreakFallOnShiftActivity){
                    for (ShiftActivityDTO breakActivity : breakActivities) {
                        scheduledHourAdded = getShiftActivityByBreakInterval(updatedShiftActivities, shiftActivity, scheduledHourAdded, breakActivity);
                    }
                }else {
                    updatedShiftActivities.add(shiftActivity);
                }
            }

        } else {
            updatedShiftActivities = shiftActivities;
        }
        Collections.sort(updatedShiftActivities);
        return updatedShiftActivities;
    }

    private boolean getShiftActivityByBreakInterval(List<ShiftActivityDTO> updatedShiftActivities, ShiftActivityDTO shiftActivity, boolean scheduledHourAdded, ShiftActivityDTO breakActivity) {
        List<ActivityDTO> activityDTOS = timeBankCalculationService.activityMongoRepository.findByDeletedFalseAndIdsIn(newArrayList(breakActivity.getActivityId()));
        List<DateTimeInterval> dateTimeIntervals = shiftActivity.getInterval().minusInterval(breakActivity.getInterval());
        scheduledHourAdded = updateShiftActivityByBreakInterval(updatedShiftActivities, shiftActivity, dateTimeIntervals, scheduledHourAdded);
        List<PlannedTime> plannedTimes = new ArrayList<>();
        for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
            if (breakActivity.getInterval().overlaps(plannedTime.getInterval())) {
                DateTimeInterval breakDateTimeInterval = breakActivity.getInterval().overlap(plannedTime.getInterval());
                PlannedTime breakPlannedTime = ObjectMapperUtils.copyPropertiesByMapper(plannedTime, PlannedTime.class);
                breakPlannedTime.setStartDate(breakDateTimeInterval.getStartDate());
                breakPlannedTime.setEndDate(breakDateTimeInterval.getEndDate());
                plannedTimes.add(breakPlannedTime);
            }
        }
        breakActivity.setActivity(activityDTOS.get(0));
        breakActivity.setPlannedTimes(plannedTimes);
        breakActivity.setStatus(shiftActivity.getStatus());
        updatedShiftActivities.add(breakActivity);
        return scheduledHourAdded;
    }

    private boolean updateShiftActivityByBreakInterval(List<ShiftActivityDTO> updatedShiftActivities, ShiftActivityDTO shiftActivity, List<DateTimeInterval> dateTimeIntervals,boolean scheduledHourAdded) {
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
            updatedShiftActivity.setStatus(shiftActivity.getStatus());
            if(scheduledHourAdded){
                updatedShiftActivity.setScheduledMinutes(0);
                updatedShiftActivity.setDurationMinutes(0);
            }else{
                scheduledHourAdded = true;
            }
            updatedShiftActivities.add(updatedShiftActivity);
        }
        return scheduledHourAdded;
    }

    public Double getAndUpdateCtaBonusMinutes(DateTimeInterval dateTimeInterval, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivity, StaffEmploymentDetails staffEmploymentDetails,Map<BigInteger,DayTypeDTO> dayTypeDTOMap) {
        Double ctaBonusAndScheduledMinutes = 0.0;
        for (PlannedTime plannedTime : shiftActivity.getPlannedTimes()) {
            if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId()) && timeBankCalculationService.isDayTypeValid(plannedTime.getStartDate(),ruleTemplate,dayTypeDTOMap)) {
                DateTimeInterval shiftInterval = dateTimeInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                ctaBonusAndScheduledMinutes += timeBankCalculationService.calculateBonusAndUpdateShiftActivity(dateTimeInterval, ruleTemplate, shiftInterval, staffEmploymentDetails);
            }
            if (ruleTemplate.getPlannedTimeIds().contains(plannedTime.getPlannedTimeId()) && dateTimeInterval.getStartLocalDate().isBefore(asLocalDate(plannedTime.getEndDate())) && timeBankCalculationService.isDayTypeValid(plannedTime.getEndDate(),ruleTemplate,dayTypeDTOMap)) {
                DateTimeInterval nextDayInterval = new DateTimeInterval(getStartOfDay(plannedTime.getEndDate()), getEndOfDay(plannedTime.getEndDate()));
                DateTimeInterval shiftInterval = nextDayInterval.overlap(new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()));
                ctaBonusAndScheduledMinutes += timeBankCalculationService.calculateBonusAndUpdateShiftActivity(nextDayInterval, ruleTemplate, shiftInterval, staffEmploymentDetails);
            }
        }
        return ctaBonusAndScheduledMinutes;
    }


    public boolean updateTimeBankAgainstProtectedDaysOffSetting() {
        List<DailyTimeBankEntry> dailyTimeBankEntriesToSave = new ArrayList<>();
        List<PayOutPerShift> payOutOfStaffs = new ArrayList<>();
        List<StaffEmploymentDetails> staffEmploymentDetails = timeBankCalculationService.userIntegrationService.getStaffsMainEmployment();
        Set<Long> unitIds = staffEmploymentDetails.stream().map(StaffEmploymentDetails::getUnitId).collect(Collectors.toSet());
        List<ProtectedDaysOffSettingDTO> protectedDaysOffSettingOfUnit = timeBankCalculationService.protectedDaysOffService.getAllProtectedDaysOffByUnitIds(new ArrayList<>(unitIds));
        Map<Long, ProtectedDaysOffSettingDTO> unitIdAndProtectedDaysOffSettingDTOMap = protectedDaysOffSettingOfUnit.stream().collect(Collectors.toMap(ProtectedDaysOffSettingDTO::getUnitId, v -> v));
        Map<Long, List<StaffEmploymentDetails>> unitAndStaffEmploymentDetailsMap = staffEmploymentDetails.stream().collect(groupingBy(StaffEmploymentDetails::getUnitId));
        Set<Long> employmentIds = staffEmploymentDetails.stream().map(StaffEmploymentDetails::getId).collect(toSet());
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankCalculationService.timeBankService.findAllByEmploymentIdsAndBetweenDate(employmentIds, getLocalDate(), getLocalDate());
        List<PayOutPerShift> payOutPerShifts=timeBankCalculationService.payOutRepository.findByEmploymentsAndDateShiftId(employmentIds,asDate(getLocalDate()),asDate(getLocalDate()),BigInteger.valueOf(-1l));
        Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(DailyTimeBankEntry::getEmploymentId, v -> v));
        Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap = payOutPerShifts.stream().collect(Collectors.toMap(PayOutPerShift::getEmploymentId, v -> v));
        List<Activity> activities = timeBankCalculationService.activityMongoRepository.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.PROTECTED_DAYS_OFF, unitIds);
        Set<BigInteger> activityIds = activities.stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
        Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(MongoBaseEntity::getId, v -> v));
        Map<Long, Activity> unitIdAndActivityMap = activities.stream().collect(Collectors.toMap(Activity::getUnitId, v -> v));
        Map[] activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap = getActivityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap(employmentIds, activityIds, activityMap);
        Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap = activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap[0];
        Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap = activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap[1];
        List<CTAResponseDTO> ctaResponseDTOS = timeBankCalculationService.costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(new ArrayList<>(employmentIds), getDate(), getDate());
        Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap = ctaResponseDTOS.stream().collect(Collectors.toMap(CTAResponseDTO::getEmploymentId, v -> v));
        for (Long unitId : unitIds) {
            getDailyTimeBankEntryAndPaidOutPerUnit(dailyTimeBankEntriesToSave, payOutOfStaffs, unitIdAndProtectedDaysOffSettingDTOMap, unitAndStaffEmploymentDetailsMap, employmentIdAndDailyTimeBankEntryMap,employmentIdAndPayOutPerShiftMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId);
        }
        if (isCollectionNotEmpty(dailyTimeBankEntriesToSave)) {
            timeBankCalculationService.timeBankRepository.saveEntities(dailyTimeBankEntriesToSave);
        }
        if (isCollectionNotEmpty(payOutOfStaffs)) {
            timeBankCalculationService.payOutService.savePayout(payOutOfStaffs);
        }
        return true;
    }

    private Object[] getDailyTimeBankEntryAndPaidOutPerUnit(List<DailyTimeBankEntry> dailyTimeBankEntriesToSave, List<PayOutPerShift> payOutOfStaffs, Map<Long, ProtectedDaysOffSettingDTO> unitIdAndProtectedDaysOffSettingDTOMap, Map<Long, List<StaffEmploymentDetails>> unitAndStaffEmploymentDetailsMap, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId) {
        Object[] dailyTimeBankAndPayoutByOnceInAYear = new Object[]{};
        try {
            ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = unitIdAndProtectedDaysOffSettingDTOMap.get(unitId);
            Activity activity = unitIdAndActivityMap.get(unitId);
            if (isNotNull(protectedDaysOffSettingDTO) && isNotNull(activity)) {
                List<StaffEmploymentDetails> staffEmploymentDetailsList = unitAndStaffEmploymentDetailsMap.get(unitId);
                for (StaffEmploymentDetails employmentDetails : staffEmploymentDetailsList) {
                    dailyTimeBankAndPayoutByOnceInAYear = setDailyTimeBankAndPayoutPerStaff(dailyTimeBankAndPayoutByOnceInAYear,employmentIdAndPayOutPerShiftMap,employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId, protectedDaysOffSettingDTO, employmentDetails);
                    if (dailyTimeBankAndPayoutByOnceInAYear.length > AppConstants.MINIMUM_VALUE && isNotNull(dailyTimeBankAndPayoutByOnceInAYear[0])) {
                        dailyTimeBankEntriesToSave.add((DailyTimeBankEntry) dailyTimeBankAndPayoutByOnceInAYear[0]);
                    }
                    if (dailyTimeBankAndPayoutByOnceInAYear.length > MINIMUM_VALUE && isNotNull(dailyTimeBankAndPayoutByOnceInAYear[1])) {
                        payOutOfStaffs.add((PayOutPerShift) dailyTimeBankAndPayoutByOnceInAYear[1]);
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("error while add protected days off time bank in unit  {} ,\n {}  ", unitId, e);
        }
        return dailyTimeBankAndPayoutByOnceInAYear;
    }

    private Object[] setDailyTimeBankAndPayoutPerStaff(Object[] dailyTimeBankAndPayoutByOnceInAYear, Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO, StaffEmploymentDetails employmentDetails) {
        List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings;
        try {
            protectedDaysOffSettings = employmentDetails.getProtectedDaysOffSettings();
            if(isNotNull(protectedDaysOffSettings)) {
                protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.getPublicHolidayDate().isAfter(employmentDetails.getStartDate()) || (isNull(employmentDetails.getEndDate()) || !protectedDaysOffSetting.getPublicHolidayDate().isAfter(employmentDetails.getEndDate()))).collect(Collectors.toList());
                protectedDaysOffSettings =protectedDaysOffSettings.stream().filter(distinctByKey(protectedDaysOffSetting -> protectedDaysOffSetting.getPublicHolidayDate())).collect(toList());
                employmentDetails.setProtectedDaysOffSettings(protectedDaysOffSettings);
                switch (protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings()) {
                    case UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR:
                        dailyTimeBankAndPayoutByOnceInAYear = getDailyTimeBankAndPayoutByUpdateInTimeBank(employmentIdAndDailyTimeBankEntryMap,employmentIdAndPayOutPerShiftMap, unitIdAndActivityMap, employmentIdAndCtaResponseDTOMap, unitId, employmentDetails, true);
                        break;
                    case ONCE_IN_A_YEAR:
                        dailyTimeBankAndPayoutByOnceInAYear = getDailyTimeBankAndPayoutByOnceInAYear(employmentIdAndDailyTimeBankEntryMap,employmentIdAndPayOutPerShiftMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId, employmentDetails);
                        break;
                    case ACTIVITY_CUT_OFF_INTERVAL:
                        dailyTimeBankAndPayoutByOnceInAYear = getDailyTimeBankEntryAndPayoutByCutOffInterval(employmentIdAndCtaResponseDTOMap,employmentIdAndPayOutPerShiftMap, employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, unitId, employmentDetails);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("error while add protected days off time bank in staff  {} ,\n {}  ", employmentDetails.getStaffId(), e);
        }
        return dailyTimeBankAndPayoutByOnceInAYear;
    }

    private Object[] getDailyTimeBankAndPayoutByUpdateInTimeBank(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap, Map<Long, Activity> unitIdAndActivityMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId, StaffEmploymentDetails employmentDetails, boolean addValueInProtectedDaysOff) {
        DailyTimeBankEntry dailyTimeBankEntry = null;
        PayOutPerShift payOutPerShift = null;
        Activity activity = (unitIdAndActivityMap.get(unitId));
        DateTimeInterval dateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(getLocalDate()), getLocalDate());
        if (dateTimeInterval.getStartLocalDate().isBefore(employmentDetails.getStartDate())) {
            dateTimeInterval = new DateTimeInterval(employmentDetails.getStartDate(), dateTimeInterval.getEndLocalDate());
        }
        if (dateTimeInterval.getStartLocalDate().equals(getLocalDate())) {
            DateTimeInterval cutOffDateTimeInterval = dateTimeInterval;
            int count = (int) employmentDetails.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && cutOffDateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).count();
            dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, dateTimeInterval, count,addValueInProtectedDaysOff);
            payOutPerShift = getPayoutData(employmentIdAndCtaResponseDTOMap,employmentIdAndPayOutPerShiftMap, employmentDetails, dateTimeInterval, count,addValueInProtectedDaysOff);
        }
        return new Object[]{dailyTimeBankEntry, payOutPerShift};
    }


    private Object[] getDailyTimeBankEntryAndPayoutByCutOffInterval(Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Long unitId, StaffEmploymentDetails employmentDetails) {
        int[] scheduledAndApproveActivityCount;
        Activity activity = unitIdAndActivityMap.get(unitId);
        DateTimeInterval activityDateTimeInterval = activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
        List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings = new ArrayList<>();
        for (ProtectedDaysOffSettingDTO protectedDaysOffSetting : employmentDetails.getProtectedDaysOffSettings()) {
            DateTimeInterval dateTimeInterval = getCutoffInterval(protectedDaysOffSetting.getPublicHolidayDate().plusDays(1), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(getLocalDate().minusDays(1)), protectedDaysOffSetting.getPublicHolidayDate().plusDays(1));
            if (isNotNull(dateTimeInterval) && dateTimeInterval.getEndLocalDate().isEqual(getLocalDate()) && protectedDaysOffSetting.isProtectedDaysOff() && protectedDaysOffSetting.getPublicHolidayDate().isBefore(getLocalDate())) {
                protectedDaysOffSettings.add(protectedDaysOffSetting);
            }
        }
        int count = protectedDaysOffSettings.size();
        protectedDaysOffSettings.sort((protectedDaysOffSetting, t1) -> protectedDaysOffSetting.getPublicHolidayDate().compareTo(t1.getPublicHolidayDate()));
        DateTimeInterval protectedDaysDateTimeInterval = new DateTimeInterval(protectedDaysOffSettings.get(0).getPublicHolidayDate(), getLocalDate());
        scheduledAndApproveActivityCount = timeBankCalculationService.workTimeAgreementBalancesCalculationService.getShiftsActivityCountByInterval(activityDateTimeInterval, isNotNull(employmentIdAndShiftMap.get(employmentDetails.getId())) ? employmentIdAndShiftMap.get(employmentDetails.getId()) : new ArrayList<>(), newHashSet(unitIdAndActivityMap.get(unitId).getId()));
        count = count - scheduledAndApproveActivityCount[0];
        DailyTimeBankEntry dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, protectedDaysDateTimeInterval, count,false);
        PayOutPerShift payOutPerShift = getPayoutData(employmentIdAndCtaResponseDTOMap,employmentIdAndPayOutPerShiftMap, employmentDetails, protectedDaysDateTimeInterval, count ,false);
        return new Object[]{dailyTimeBankEntry, payOutPerShift};
    }

    private Object[] getDailyTimeBankAndPayoutByOnceInAYear(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Long unitId, StaffEmploymentDetails employmentDetails) {
        int[] scheduledAndApproveActivityCount;
        DateTimeInterval activityDateTimeInterval = activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
        int count = (int) employmentDetails.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && activityDateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).count();
        scheduledAndApproveActivityCount = timeBankCalculationService.workTimeAgreementBalancesCalculationService.getShiftsActivityCountByInterval(activityDateTimeInterval, isNotNull(employmentIdAndShiftMap.get(employmentDetails.getId())) ? employmentIdAndShiftMap.get(employmentDetails.getId()) : new ArrayList<>(), newHashSet(unitIdAndActivityMap.get(unitId).getId()));
        count = count - scheduledAndApproveActivityCount[0];
        DailyTimeBankEntry dailyTimeBankEntry = null;
        PayOutPerShift payOutPerShift = null;
        if (activityDateTimeInterval.getStartLocalDate().equals(getLocalDate()) && count > MINIMUM_VALUE) {
            dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, activityDateTimeInterval, count ,false);
            payOutPerShift = getPayoutData(employmentIdAndCtaResponseDTOMap, employmentIdAndPayOutPerShiftMap, employmentDetails, activityDateTimeInterval, count,false);
        }
        return new Object[]{dailyTimeBankEntry, payOutPerShift};
    }

    private DailyTimeBankEntry getDailyTimeBankEntry(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, StaffEmploymentDetails employmentDetails, DateTimeInterval activityDateTimeInterval, int count,boolean addValueInProtectedDaysOff) {
        DailyTimeBankEntry dailyTimeBankEntry = employmentIdAndDailyTimeBankEntryMap.get(employmentDetails.getId());
        if (count > 0) {
            CTAResponseDTO ctaResponseDTO = employmentIdAndCtaResponseDTOMap.get(employmentDetails.getId());
            int contractualMinutes = timeBankCalculationService.getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
            int value = 0;
            for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
                if (UNUSED_DAYOFF_LEAVES.equals(ruleTemplate.getCalculationFor()) && TIMEBANK_ACCOUNT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                    if (isNull(dailyTimeBankEntry)) {
                        dailyTimeBankEntry = new DailyTimeBankEntry(employmentDetails.getId(), employmentDetails.getStaffId(), getLocalDate());
                        timeBankCalculationService.resetDailyTimebankEntry(dailyTimeBankEntry, contractualMinutes);
                    }
                    int bonusByRuletemplate = getBonusOfUnusedDaysOff(activityDateTimeInterval, employmentDetails, contractualMinutes, ruleTemplate);
                    value += (bonusByRuletemplate * count);
                    dailyTimeBankEntry.getTimeBankCTADistributionList().add(new TimeBankCTADistribution(ruleTemplate.getName(), value, ruleTemplate.getId()));
                }
            }
            updatedailyTimeBankEntryByProtectedDaysOff(dailyTimeBankEntry, value,addValueInProtectedDaysOff);
        }
        return dailyTimeBankEntry;
    }

    private void updatedailyTimeBankEntryByProtectedDaysOff(DailyTimeBankEntry dailyTimeBankEntry, int value , boolean addValueInProtectedDaysOff) {
        if (isNotNull(dailyTimeBankEntry)) {
            if(addValueInProtectedDaysOff){
                dailyTimeBankEntry.setProtectedDaysOffMinutes(dailyTimeBankEntry.getProtectedDaysOffMinutes() + value);
            }else {
                dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes() + value);
                if (dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes() > MINIMUM_VALUE) {
                    dailyTimeBankEntry.setPublishedSomeActivities(true);
                }
            }
//                dailyTimeBankEntry.setPlannedMinutesOfTimebank(dailyTimeBankEntry.getPlannedMinutesOfTimebank() + value);
//                dailyTimeBankEntry.setDeltaTimeBankMinutes(dailyTimeBankEntry.getDeltaTimeBankMinutes() + value);

        }
    }

    private PayOutPerShift getPayoutData(Map<Long, CTAResponseDTO> employmentIdAndCtaResponseDTOMap, Map<Long, PayOutPerShift> employmentIdAndPayOutPerShiftMap, StaffEmploymentDetails employmentDetails, DateTimeInterval activityDateTimeInterval, int count, boolean addValueInProtectedDaysOff) {
        PayOutPerShift payOutPerShift = employmentIdAndPayOutPerShiftMap.get(employmentDetails.getId());
        if (count > 0) {
            CTAResponseDTO ctaResponseDTO = employmentIdAndCtaResponseDTOMap.get(employmentDetails.getId());
            int contractualMinutes = timeBankCalculationService.getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
            int value = 0;
            for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
                if (UNUSED_DAYOFF_LEAVES.equals(ruleTemplate.getCalculationFor()) && PAID_OUT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                    if (isNull(payOutPerShift)) {
                        payOutPerShift = new PayOutPerShift(BigInteger.valueOf(-1l), employmentDetails.getId(), employmentDetails.getStaffId(), getLocalDate(), employmentDetails.getUnitId());
                    }
                    int bonusByRuletemplate = getBonusOfUnusedDaysOff(activityDateTimeInterval, employmentDetails, contractualMinutes, ruleTemplate);
                    value += (bonusByRuletemplate * count);
                    payOutPerShift.getPayOutPerShiftCTADistributions().add(new PayOutPerShiftCTADistribution(ruleTemplate.getName(), value, ruleTemplate.getId(), 0f));
                }
            }
            timeBankCalculationService.addBonusForProtectedDaysOff(addValueInProtectedDaysOff, payOutPerShift, value);
        }
        return payOutPerShift;
    }

    private Map[] getActivityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap(Set<Long> employmentIds, Set<BigInteger> activityIds, Map<BigInteger, Activity> activityWrapperMap) {
        Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap = new HashMap<>();
        for (Map.Entry<BigInteger, Activity> bigIntegerActivityEntry : activityWrapperMap.entrySet()) {
            Activity activityWrapper = bigIntegerActivityEntry.getValue();
            DateTimeInterval dateTimeInterval= getCutoffInterval(activityWrapper.getActivityRulesSettings().getCutOffStartFrom(), activityWrapper.getActivityRulesSettings().getCutOffIntervalUnit(), activityWrapper.getActivityRulesSettings().getCutOffdayValue(), asDate(getLocalDate().minusDays(1)), getLocalDate());
            if(isNotNull(dateTimeInterval)) {
                activityIdDateTimeIntervalMap.putIfAbsent(bigIntegerActivityEntry.getKey(),dateTimeInterval);
            }
        }
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>(activityIdDateTimeIntervalMap.values());
        dateTimeIntervals.sort((dateTimeInterval, t1) -> dateTimeInterval.getStartLocalDate().compareTo(t1.getStartLocalDate()));
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = timeBankCalculationService.shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIds, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), activityIds);
        Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap = shiftWithActivityDTOS.stream().collect(groupingBy(ShiftWithActivityDTO::getEmploymentId));
        return new Map[]{activityIdDateTimeIntervalMap, employmentIdAndShiftMap};
    }


    private int getBonusOfUnusedDaysOff(DateTimeInterval dateTimeInterval, StaffEmploymentDetails staffEmploymentDetails, int contractualMinutes, CTARuleTemplateDTO ruleTemplate) {
        int value = 0;
        if (UNUSED_DAYOFF_LEAVES.equals(ruleTemplate.getCalculationFor())) {
            if (CompensationMeasurementType.FIXED_VALUE.equals(ruleTemplate.getCompensationTable().getUnusedDaysOffType())) {
                BigDecimal hourlyCost = timeBankCalculationService.getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate());
                value += !hourlyCost.equals(BigDecimal.valueOf(0)) ? BigDecimal.valueOf( ruleTemplate.getCompensationTable().getUnusedDaysOffvalue()).divide(hourlyCost, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(60)).intValue() : 0;
            } else if (CompensationMeasurementType.PERCENT.equals(ruleTemplate.getCompensationTable().getUnusedDaysOffType())) {
                value += contractualMinutes * ruleTemplate.getCompensationTable().getUnusedDaysOffvalue() / 100;
            }
        }
        return value;
    }
}

