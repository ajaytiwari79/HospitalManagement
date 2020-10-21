package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeRequiredState;
import com.kairos.enums.shift.*;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.*;
import com.kairos.service.activity.ActivityUtil;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.time_bank.CalculatePlannedHoursAndScheduledHours;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeTypeEnum.PRESENCE;
import static com.kairos.enums.TimeTypes.NON_WORKING_TYPE;
import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static com.kairos.enums.shift.ShiftOperationType.CREATE;
import static com.kairos.enums.wta.WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;
import static com.kairos.enums.wta.WTATemplateType.NUMBER_OF_PARTOFDAY;
import static com.kairos.utils.CPRUtil.getAgeByCPRNumberAndStartDate;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;

/**
 * @author pradeep
 * @date - 10/5/18
 */
@Service
public class ShiftValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftValidatorService.class);

    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private StaffWTACounterRepository wtaCounterRepository;
    @Inject
    private
    WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;

    @Inject
    private NightWorkerMongoRepository nightWorkerMongoRepository;

    private static ExceptionService exceptionService;

    @Inject
    private TimeBankCalculationService timeBankCalculationService;

    @Inject
    private WorkTimeAgreementService workTimeAgreementService;

    @Inject
    private BlockSettingService blockSettingService;
    @Inject
    private ShiftSickService shiftSickService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;

    @Autowired
    public void setExceptionService(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    public static void throwException(String exception, Object... param) {
        exceptionService.invalidRequestException(exception, param);
    }

    public static String convertMessage(String exception, Object... param) {
        return exceptionService.convertMessage(exception, param);
    }

    public boolean validateGracePeriod(Date shiftDate, Boolean validatedByStaff, Long unitId, Phase phase) {
        boolean valid = true;
        if (TIME_AND_ATTENDANCE.equals(phase.getName())) {
            String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
            DateTimeInterval graceInterval = getGracePeriodInterval(phase, shiftDate, validatedByStaff);
            valid = graceInterval.contains(DateUtils.getDateFromTimeZone(timeZone));
        }

        return valid;
    }

    public ShiftWithViolatedInfoDTO validateRuleCheck(Shift shift, boolean ruleCheckRequired, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, boolean valid, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shiftWithActivityDTO, Shift oldStateShift) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;
        if (ruleCheckRequired) {
            boolean isValidateStaffingLevel = false;
            if (!valid && oldStateShift.getShiftType().equals(shift.getShiftType())) {
                isValidateStaffingLevel = staffingLevelService.validateStaffingLevel(shift, activityWrapperMap, phase, oldStateShift);
            }
            shiftWithViolatedInfoDTO = validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, oldStateShift, activityWrapperMap, true, false, !isValidateStaffingLevel);
        }
        if (isNull(shiftWithViolatedInfoDTO)) {
            shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO(new ViolatedRulesDTO());
        }
        return shiftWithViolatedInfoDTO;
    }

    public DateTimeInterval getGracePeriodInterval(Phase phase, Date date, boolean forStaff) {
        ZonedDateTime startDate = DateUtils.asZonedDateTime(date);
        ZonedDateTime endDate;
        if (UserContext.getUserDetails().isStaff()) {
            endDate = startDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(phase.getGracePeriodByStaff()).plusDays(1);
        } else {
            endDate = startDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(phase.getGracePeriodByStaff() + phase.getGracePeriodByManagement()).plusDays(1);
        }
        return new DateTimeInterval(startDate, endDate);
    }

    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift oldShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean byUpdate, boolean byTandAPhase, boolean isValidateStaffingLevel) {
        if (wtaQueryResultDTO.getEndDate() != null && wtaQueryResultDTO.getEndDate().isBefore(asLocalDate(shift.getStartDate()))) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_EXPIRED_UNIT);
        }
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), asLocalDate(shift.getStartDate()));
        if (planningPeriod == null) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PLANNING_PERIOD_EXITS, shift.getStartDate());
        }
        if (staffAdditionalInfoDTO.getUserAccessRoleDTO().isStaff() && !staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaffId().equals(shift.getStaffId())) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PERMISSION);
        }
        Shift mainShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shift.setPhaseId(phase.getId());
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shift, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap, CREATE);
        List<ActivityRuleViolation> activityRuleViolations = validateTimingOfActivity(shift, new ArrayList<>(activityWrapperMap.keySet()), activityWrapperMap);
        ruleTemplateSpecificInfo.getViolatedRules().getActivities().addAll(activityRuleViolations);
        if (isValidateStaffingLevel) {
            List<ShiftActivity>[] shiftActivities = mainShift.getShiftActivitiesForValidatingStaffingLevel(oldShift);

            for (ShiftActivity shiftActivity : shiftActivities[1]) {
                Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = new HashMap<>();
                validateStaffingLevel(phase, mainShift, activityWrapperMap, true, shiftActivity, ruleTemplateSpecificInfo,  staffingLevelActivityWithDurationMap);
                verifyStaffingLevel(ruleTemplateSpecificInfo,staffingLevelActivityWithDurationMap);
            }
            for (ShiftActivity shiftActivity : shiftActivities[0]) {
                Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = new HashMap<>();
                validateStaffingLevel(phase, mainShift, activityWrapperMap, false, shiftActivity, ruleTemplateSpecificInfo, staffingLevelActivityWithDurationMap);
                verifyStaffingLevel(ruleTemplateSpecificInfo,staffingLevelActivityWithDurationMap);
            }

        }
        validateAbsenceReasonCodeRule(activityWrapperMap, shift, ruleTemplateSpecificInfo);
        updateScheduledAndDurationMinutesInShift(shift, staffAdditionalInfoDTO, activityWrapperMap);
        boolean gracePeriodValid = validateGracePeriod(shift.getStartDate(), UserContext.getUserDetails().isStaff(), shift.getUnitId(), phase);
        if (!gracePeriodValid) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        CalculatePlannedHoursAndScheduledHours calculatePlannedHoursAndScheduledHours = new CalculatePlannedHoursAndScheduledHours(staffAdditionalInfoDTO, dateTimeInterval, newArrayList(shift), false, false, dayTypeDTOMap, timeBankCalculationService).calculate();
        shift.setPlannedMinutesOfTimebank(calculatePlannedHoursAndScheduledHours.getTotalDailyPlannedMinutes());
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkillLevelDTOS(), ruleTemplateSpecificInfo, exceptionService);
        Specification<ShiftWithActivityDTO> tagSpecification = new TagSpecification(staffAdditionalInfoDTO.getTags(), ruleTemplateSpecificInfo, exceptionService);
        Set<BigInteger> allActivities = getAllActivitiesOfTeam(shift);
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getEmployment().getExpertise(), ruleTemplateSpecificInfo);
        Specification<ShiftWithActivityDTO> staffActivitySpecification = new StaffActivitySpecification(ruleTemplateSpecificInfo, allActivities);
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        Specification<ShiftWithActivityDTO> activitySpecification = activityExpertiseSpecification.and(activitySkillSpec).and(wtaRulesSpecification).and(tagSpecification).and(staffActivitySpecification);

        if (byUpdate) {
            Specification<ShiftWithActivityDTO> activityPhaseSettingSpecification = new ActivityPhaseSettingSpecification(staffAdditionalInfoDTO, phase, activityWrapperMap.values(), oldShift);
            activitySpecification = activitySpecification.and(activityPhaseSettingSpecification);
        } else {
            Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, staffAdditionalInfoDTO);
            activitySpecification = activitySpecification.and(staffEmploymentSpecification);
            Specification<ShiftWithActivityDTO> blockSettingSpecification = new BlockSettingSpecification(blockSettingService.getBlockSetting(shift.getUnitId(), asLocalDate(shift.getStartDate())));
            activitySpecification = activitySpecification.and(blockSettingSpecification);
        }
        if (!byTandAPhase) {
            Specification<ShiftWithActivityDTO> shiftTimeLessThan = new ShiftStartTimeLessThan();
            activitySpecification = activitySpecification.and(shiftTimeLessThan);
        }
        List<Long> dayTypeIds = shift.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getActivity().getActivityRulesSettings().getDayTypes().stream()).collect(Collectors.toList());
        if (isCollectionNotEmpty(dayTypeIds)) {
            Set<DayOfWeek> validDays = getValidDays(dayTypeDTOMap, dayTypeIds, asLocalDate(shift.getStartDate()));
            Specification<ShiftWithActivityDTO> activityDayTypeSpec = new DayTypeSpecification(validDays, shift.getStartDate());
            activitySpecification = activitySpecification.and(activityDayTypeSpec);
        }
        shift.setTimeType(activityWrapperMap.get(shift.getActivities().get(0).getActivityId()).getTimeType());
        activitySpecification.validateRules(shift);
        return new ShiftWithViolatedInfoDTO(ruleTemplateSpecificInfo.getViolatedRules());
    }

    public Set<BigInteger> getAllActivitiesOfTeam(ShiftWithActivityDTO shift) {
        Set<BigInteger> activities = userIntegrationService.getTeamActivitiesOfStaff(shift.getUnitId(), shift.getStaffId());
        List<StaffActivitySetting> activitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(shift.getStaffId(), ActivityUtil.getAllActivities(shift));
        Set<BigInteger> allActivities = activitySettings.stream().map(StaffActivitySetting::getActivityId).collect(Collectors.toSet());
        allActivities.addAll(activities);
        return allActivities;
    }

    public ViolatedRulesDTO validateRuleOnShiftDelete(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), null);
        boolean gracePeriodValid = validateGracePeriod(shift.getStartDate(), UserContext.getUserDetails().isStaff(), shift.getUnitId(), phase);
        if (!gracePeriodValid) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
        Specification<Shift> shiftSpecification = new ShiftAllowedToDelete(activityWrapperMap, phase.getId());
        shiftSpecification.validateRules(shift);
        ViolatedRulesDTO violatedRulesDTO = new ViolatedRulesDTO();
        WTAQueryResultDTO wtaQueryResultDTO = workTimeAgreementService.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shift.getActivities().get(0).getStartDate()));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTO.getRuleTemplates().stream().filter(this::isValidWTARuleForDelete).collect(Collectors.toList());
        if (isCollectionNotEmpty(wtaBaseRuleTemplates)) {
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class), activityWrapperMap, null);
            RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shiftWithActivityDTO, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap, ShiftOperationType.DELETE);
            List<ShiftActivity>[] shiftActivities = shift.getShiftActivitiesForValidatingStaffingLevel(shift);
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = new HashMap<>();
            for (ShiftActivity shiftActivity : shiftActivities[0]) {
                validateStaffingLevel(phase, shift, activityWrapperMap, false, shiftActivity, ruleTemplateSpecificInfo, staffingLevelActivityWithDurationMap);
            }
            verifyStaffingLevel(ruleTemplateSpecificInfo,staffingLevelActivityWithDurationMap);
            Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaBaseRuleTemplates);
            wtaRulesSpecification.validateRules(shiftWithActivityDTO);
            violatedRulesDTO = ruleTemplateSpecificInfo.getViolatedRules();
        }
        return violatedRulesDTO;
    }

    private boolean isValidWTARuleForDelete(WTABaseRuleTemplate wtaRule) {
        if (newHashSet(NUMBER_OF_PARTOFDAY, CONSECUTIVE_WORKING_PARTOFDAY).contains(wtaRule.getWtaTemplateType())) {
            return true;
        }
        return false;
    }

    private void updateScheduledAndDurationMinutesInShift(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            ShiftActivity shiftActivity = ObjectMapperUtils.copyPropertiesByMapper(shiftActivityDTO, ShiftActivity.class);
            timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), staffAdditionalInfoDTO.getEmployment(), false);
            shiftActivityDTO.setScheduledMinutes(shiftActivity.getScheduledMinutes());
            shiftActivityDTO.setDurationMinutes(shiftActivity.getDurationMinutes());
            scheduledMinutes += shiftActivity.getScheduledMinutes();
            durationMinutes += shiftActivity.getDurationMinutes();
        }
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
    }

    private void validateAbsenceReasonCodeRule(Map<BigInteger, ActivityWrapper> activityWrapperMap, ShiftWithActivityDTO shift, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
            validateActivityReasonCode(activityWrapperMap, ruleTemplateSpecificInfo, shiftActivity);
            for (ShiftActivityDTO childActivity : shiftActivity.getChildActivities()) {
                validateActivityReasonCode(activityWrapperMap, ruleTemplateSpecificInfo, childActivity);
            }
        }
    }

    private void validateActivityReasonCode(Map<BigInteger, ActivityWrapper> activityWrapperMap, RuleTemplateSpecificInfo ruleTemplateSpecificInfo, ShiftActivityDTO childActivity) {
        Activity activity = activityWrapperMap.get(childActivity.getActivityId()).getActivity();
        ActivityRuleViolation activityRuleViolation;
        if (ReasonCodeRequiredState.MANDATORY.equals(activity.getActivityRulesSettings().getReasonCodeRequiredState()) && !Optional.ofNullable(childActivity.getAbsenceReasonCodeId()).isPresent()) {
            activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getId())).findAny().orElse(null);
            if (activityRuleViolation == null) {
                activityRuleViolation = new ActivityRuleViolation(activity.getId(), activity.getName(), 0, newHashSet(exceptionService.
                        convertMessage(MESSAGE_SHIFT_REASONCODE_REQUIRED, activity.getId())));
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
            } else {
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getId())).findAny().get().getErrorMessages().add(exceptionService.
                        convertMessage(MESSAGE_SHIFT_REASONCODE_REQUIRED, activity.getId()));
            }
        }
    }

    public void validateShiftViolatedRules(Shift shift, boolean shiftOverlappedWithNonWorkingType, ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO, ShiftActionType actionType) {
        ShiftViolatedRules shiftViolatedRules = isNull(shift.getDraftShift()) ? shift.getShiftViolatedRules() : shift.getDraftShift().getShiftViolatedRules();
        if (!(ShiftActionType.SAVE.equals(actionType) || ShiftActionType.CANCEL.equals(actionType))) {
            if (isNull(shiftViolatedRules)) {
                shiftViolatedRules = new ShiftViolatedRules(shift.getId());
                shiftViolatedRules.setDraft(isNotNull(shift.getDraftShift()));
            }
            if (shiftOverlappedWithNonWorkingType) {
                shiftViolatedRules.setEscalationReasons(newHashSet(ShiftEscalationReason.SHIFT_OVERLAPPING));
                shiftViolatedRules.setEscalationResolved(false);
            }
            if (shift.getBreakActivities().stream().anyMatch(ShiftActivity::isBreakNotHeld)) {
                shiftViolatedRules.getEscalationReasons().add(ShiftEscalationReason.BREAK_NOT_HELD);
                shiftViolatedRules.setEscalationResolved(false);
            }
            shiftViolatedRules.setActivities(shiftWithViolatedInfoDTO.getViolatedRules().getActivities());
            shiftViolatedRules.setWorkTimeAgreements(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
        }
        if (isNotNull(shiftViolatedRules)) {
            shiftViolatedRules.setEscalationCausedBy(UserContext.getUserDetails().isManagement() ? AccessGroupRole.MANAGEMENT : AccessGroupRole.STAFF);
            Shift shiftForViolatedRules = isNull(shift.getDraftShift()) ? shift : shift.getDraftShift();
            shiftForViolatedRules.setShiftViolatedRules(shiftViolatedRules);
        }
    }

    public void updateWTACounter(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftWithViolatedInfoDTO shiftWithViolatedInfo, Shift shift) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getEmployment().getId(), asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()), UserContext.getUserDetails().isStaff());
        Map<BigInteger, StaffWTACounter> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, v -> v));
        List<StaffWTACounter> updatedStaffCounters = new ArrayList<>();
        List<BigInteger> wtaRuleTemplateIds = shiftWithViolatedInfo.getViolatedRules().getWorkTimeAgreements().stream().map(WorkTimeAgreementRuleViolation::getRuleTemplateId).collect(Collectors.toList());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.findAllByIdInAndDeletedFalse(wtaRuleTemplateIds);
        Map<BigInteger, WTABaseRuleTemplate> wtaBaseRuleTemplateMap = wtaBaseRuleTemplates.stream().collect(Collectors.toMap(MongoBaseEntity::getId, v -> v));
        shiftWithViolatedInfo.getViolatedRules().getWorkTimeAgreements().forEach(workTimeAgreementRuleViolation -> {
            if (isNotNull(workTimeAgreementRuleViolation.getCounter())) {
                int count = workTimeAgreementRuleViolation.getCounter() - 1;
                if (count < MINIMUM_WTA_RULE_TEMPLATE_COUNTER) {
                    exceptionService.actionNotPermittedException("message.ruleTemplate.counter.exhausted");
                }
                StaffWTACounter staffWTACounter = staffWTACounterMap.getOrDefault(workTimeAgreementRuleViolation.getRuleTemplateId(), new StaffWTACounter(planningPeriod.getStartDate(), planningPeriod.getEndDate(), workTimeAgreementRuleViolation.getRuleTemplateId(), wtaBaseRuleTemplateMap.get(workTimeAgreementRuleViolation.getRuleTemplateId()).getName(), staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getUnitId(), UserContext.getUserDetails().isStaff()));
                staffWTACounter.setUserHasStaffRole(UserContext.getUserDetails().isStaff());
                staffWTACounter.setCount(count);
                updatedStaffCounters.add(staffWTACounter);
            }
        });
        if (!updatedStaffCounters.isEmpty()) {
            wtaCounterRepository.saveEntities(updatedStaffCounters);
        }
    }

    private List<ActivityRuleViolation> validateTimingOfActivity(ShiftWithActivityDTO shiftDTO, List<BigInteger> activityIds, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(shiftDTO.getStaffId(), activityIds);
        Map<BigInteger, StaffActivitySetting> staffActivitySettingMap = new HashMap<>();
        staffActivitySettings.forEach(staffActivitySetting -> staffActivitySettingMap.put(staffActivitySetting.getActivityId(), staffActivitySetting));
        Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap = new HashMap<>();
        shiftDTO.getActivities().forEach(shiftActivity -> shiftTimeDetailsMap.put(shiftActivity.getActivityId(), prepareShiftTimeDetails(shiftActivity, shiftTimeDetailsMap)));
        List<ActivityRuleViolation> activityRuleViolations = new ArrayList<>();
        shiftTimeDetailsMap.forEach((activityId, shiftTimeDetails) -> {
            List<String> errorMessages = new ArrayList<>();
            Short shortestTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getActivityRulesSettings().getShortestTime() : staffActivitySettingMap.get(activityId).getShortestTime();
            Short longestTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getActivityRulesSettings().getLongestTime() : staffActivitySettingMap.get(activityId).getLongestTime();
            LocalTime earliestStartTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getActivityRulesSettings().getEarliestStartTime() : staffActivitySettingMap.get(activityId).getEarliestStartTime();
            LocalTime latestStartTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getActivityRulesSettings().getLatestStartTime() : staffActivitySettingMap.get(activityId).getLatestStartTime();
            getErrorMessages(shiftTimeDetails, errorMessages, shortestTime, longestTime, earliestStartTime, latestStartTime);
            if (!errorMessages.isEmpty()) {
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                activityRuleViolations.add(new ActivityRuleViolation(activityId, activity.getName(), 0, new HashSet<>(errorMessages)));
            }

        });
        return activityRuleViolations;
    }

    private void getErrorMessages(ShiftTimeDetails shiftTimeDetails, List<String> errorMessages, Short shortestTime, Short longestTime, LocalTime earliestStartTime, LocalTime latestStartTime) {
        if (shortestTime != null && shiftTimeDetails.getTotalTime() < shortestTime) {
            errorMessages.add(exceptionService.convertMessage(ERROR_SHIFT_DURATION_LESS_THAN_SHORTEST_TIME, getHoursStringByMinutes(shortestTime)));
        }
        if (longestTime != null && shiftTimeDetails.getTotalTime() > longestTime) {
            errorMessages.add(exceptionService.convertMessage(ERROR_SHIFT_DURATION_EXCEEDS_LONGEST_TIME, getHoursStringByMinutes(longestTime)));
        }
        if (earliestStartTime != null && earliestStartTime.isAfter(shiftTimeDetails.getActivityStartTime())) {
            errorMessages.add(exceptionService.convertMessage(ERROR_START_TIME_GREATER_THAN_EARLIEST_TIME, earliestStartTime));
        }
        if (latestStartTime != null && !shiftTimeDetails.isOverNightActivity() && latestStartTime.isBefore(shiftTimeDetails.getActivityStartTime())) {
            errorMessages.add(exceptionService.convertMessage(ERROR_START_TIME_LESS_THAN_LATEST_TIME, latestStartTime));
        }
    }

    private ShiftTimeDetails prepareShiftTimeDetails(ShiftActivityDTO shiftActivity, Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap) {
        Long shiftDurationInMinute = new DateTimeInterval(shiftActivity.getStartDate(), shiftActivity.getEndDate()).getMinutes();
        ShiftTimeDetails shiftTimeDetails = shiftTimeDetailsMap.get(shiftActivity.getActivityId());
        if (shiftTimeDetails == null) {
            shiftTimeDetails = new ShiftTimeDetails(shiftActivity.getActivityId(), DateUtils.asLocalTime(shiftActivity.getStartDate()), shiftDurationInMinute.shortValue(), DateUtils.asLocalTime(shiftActivity.getStartDate()).isAfter(DateUtils.asLocalTime(shiftActivity.getEndDate())));
        } else {
            shiftTimeDetails.setTotalTime((short) (shiftDurationInMinute.shortValue() + shiftTimeDetails.getTotalTime()));
        }
        shiftTimeDetailsMap.put(shiftActivity.getActivityId(), shiftTimeDetails);
        return shiftTimeDetails;
    }

    public RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(Phase phase, ShiftWithActivityDTO shift, WTAQueryResultDTO wtaQueryResultDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, ShiftOperationType shiftOperationType) {
        logger.info("Current phase is {} for date {}", phase.getName(), new DateTime(shift.getStartDate()));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), asLocalDate(shift.getStartDate()));
        if (planningPeriod == null) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PLANNING_PERIOD_EXITS, shift.getStartDate());
        }
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), UserContext.getUserDetails().isStaff());
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(staffAdditionalInfoDTO.getUnitId());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates(), activityWrapperMap, lastPlanningPeriod.getEndDate());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(null, shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()), false);
        if (isNotNull(shift.getId())) {
            BigInteger shiftId = shift.getId();
            shifts = shifts.stream().filter(shiftWithActivityDTO -> !shiftWithActivityDTO.getId().equals(shiftId)).collect(Collectors.toList());
        }
        shifts = updateFullDayAndFullWeekActivityShifts(shifts);
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, StaffWTACounter::getCount));
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = staffAdditionalInfoDTO.getTimeSlotSets().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        shift = updateFullDayAndFullWeekActivityShifts(newArrayList(shift)).get(0);
        shift.setTimeType(activityWrapperMap.get(shift.getActivities().get(0).getActivityId()).getTimeType());
        wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shifts);
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndUnitId(staffAdditionalInfoDTO.getEmployment().getExpertise().getId(), shift.getUnitId());
        if (expertiseNightWorkerSetting == null) {
            expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndDeletedFalseAndCountryIdExistsTrue(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
        }
        long expectedTimebank = timeBankService.getExpectedTimebankByDate(shift, staffAdditionalInfoDTO);
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(shift.getStaffId());
        staffAdditionalInfoDTO.setStaffAge(getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), asLocalDate(shift.getStartDate())));
        List<Integer> staffChildAges = getChildAges(shift.getStartDate(), staffAdditionalInfoDTO);
        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotWrapperMap, phase.getId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, expectedTimebank, activityWrapperMap, staffAdditionalInfoDTO.getStaffAge(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate(), expertiseNightWorkerSetting, isNotNull(nightWorker) ? nightWorker.isNightWorker() : false, phase.getPhaseEnum(), staffChildAges, shiftOperationType);
    }

    public List<Integer> getChildAges(Date shiftStartDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<Integer> staffChildAges = new ArrayList<>();
        for (StaffChildDetailDTO staffChildDetailDTO : staffAdditionalInfoDTO.getStaffChildDetails()) {
            staffChildAges.add(getAgeByCPRNumberAndStartDate(staffChildDetailDTO.getCprNumber(), asLocalDate(shiftStartDate)));
        }
        return staffChildAges;
    }

    private RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(PlanningPeriodDTO planningPeriod, ShiftWithActivityDTO shift, WTAQueryResultDTO wtaQueryResultDTO, StaffEmploymentDetails staffEmploymentDetails, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffEmploymentUnitDataWrapper dataWrapper, List<ShiftWithActivityDTO> newCreatedShiftWithActivityDTOs) {
        logger.info("Current phase is {}", planningPeriod.getCurrentPhase() + " for date {}", new DateTime(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffEmploymentDetails.getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), UserContext.getUserDetails().isStaff());
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(dataWrapper.getUnitId());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates(), activityWrapperMap, lastPlanningPeriod.getEndDate());
        List<ShiftWithActivityDTO> shifts;
        if (newCreatedShiftWithActivityDTOs.isEmpty()) {
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(null, shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()), false);
            newCreatedShiftWithActivityDTOs.addAll(shifts);
        } else {
            shifts = newCreatedShiftWithActivityDTOs;
        }
        shifts = updateFullDayAndFullWeekActivityShifts(shifts);
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, StaffWTACounter::getCount));
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = dataWrapper.getTimeSlotWrappers().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        shift = updateFullDayAndFullWeekActivityShifts(newArrayList(shift)).get(0);
        wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shifts);
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndUnitId(staffEmploymentDetails.getExpertise().getId(), shift.getUnitId());
        if (expertiseNightWorkerSetting == null) {
            expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndDeletedFalseAndCountryIdExistsTrue(staffEmploymentDetails.getExpertise().getId());
        }
        long expectedTimebank = timeBankService.getExpectedTimebankByDate(shift, new StaffAdditionalInfoDTO(staffEmploymentDetails, dataWrapper.getDayTypes()));
        dataWrapper.setStaffAge(getAgeByCPRNumberAndStartDate(dataWrapper.getCprNumber(), asLocalDate(shift.getStartDate())));
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(shift.getStaffId());
        Phase phase = phaseMongoRepository.findOne(planningPeriod.getCurrentPhaseId());
        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotWrapperMap, planningPeriod.getCurrentPhaseId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, expectedTimebank, activityWrapperMap, dataWrapper.getStaffAge(), dataWrapper.getSeniorAndChildCareDays().getChildCareDays(), dataWrapper.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate(), expertiseNightWorkerSetting, nightWorker.isNightWorker(), phase.getPhaseEnum());
    }

    public List<String> validateShiftWhileCopy(StaffEmploymentUnitDataWrapper dataWrapper, ShiftWithActivityDTO shiftWithActivityDTO, StaffEmploymentDetails staffEmploymentDetails, List<WTAQueryResultDTO> wtaQueryResultDTOS, PlanningPeriodDTO planningPeriod, Map<BigInteger, ActivityWrapper> activityWrapperMap, List<ShiftWithActivityDTO> newCreatedShiftWithActivityDTOs) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffEmploymentDetails);
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(planningPeriod, shiftWithActivityDTO, wtaQueryResultDTOS.get(0), staffEmploymentDetails, activityWrapperMap, dataWrapper, newCreatedShiftWithActivityDTOs);
        List<ActivityRuleViolation> activityRuleViolations = validateTimingOfActivity(ruleTemplateSpecificInfo.getShift(), new ArrayList<>(activityWrapperMap.keySet()), activityWrapperMap);
        ruleTemplateSpecificInfo.getViolatedRules().getActivities().addAll(activityRuleViolations);
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(ruleTemplateSpecificInfo.getShift().getStaffId(), new ArrayList<>(activityWrapperMap.keySet()));
        Map<BigInteger, List<Long>> activityWiseDayType = staffActivitySettings.stream().collect(Collectors.toMap(k -> k.getActivityId(), v -> v.getDayTypeIds()));
        List<Long> dayTypeIds = ruleTemplateSpecificInfo.getShift().getActivities().stream().flatMap(shiftActivityDTO -> activityWiseDayType.containsKey(shiftActivityDTO.getActivity().getId()) ? activityWiseDayType.get(shiftActivityDTO.getActivity().getId()).stream() : shiftActivityDTO.getActivity().getActivityRulesSettings().getDayTypes().stream()).collect(Collectors.toList());
        Set<DayOfWeek> validDays = isCollectionNotEmpty(dayTypeIds) ? getValidDays(dayTypeDTOMap, dayTypeIds, asLocalDate(ruleTemplateSpecificInfo.getShift().getStartDate())) : new HashSet<>();
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftWithActivityDTO.getUnitId(), ruleTemplateSpecificInfo.getShift().getActivities().get(0).getStartDate(), ruleTemplateSpecificInfo.getShift().getActivities().get(0).getEndDate());
        Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, Shift.class);
        Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = new HashMap<>();
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            validateStaffingLevel(phase, shift, activityWrapperMap, true, shiftActivity, ruleTemplateSpecificInfo, staffingLevelActivityWithDurationMap);
        }
        verifyStaffingLevel(ruleTemplateSpecificInfo,staffingLevelActivityWithDurationMap);
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTOS.get(0).getRuleTemplates());
        Set<BigInteger> allActivities = getAllActivitiesOfTeam(shiftWithActivityDTO);
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffEmploymentDetails.getExpertise(), ruleTemplateSpecificInfo);
        Specification<ShiftWithActivityDTO> staffActivitySpecification = new StaffActivitySpecification(ruleTemplateSpecificInfo, allActivities);
        Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, staffAdditionalInfoDTO);
        Specification<ShiftWithActivityDTO> activityDayTypeSpecification = new DayTypeSpecification(validDays, ruleTemplateSpecificInfo.getShift().getStartDate());
        Specification<ShiftWithActivityDTO> activitySpecification = activityExpertiseSpecification.and(wtaRulesSpecification).and(staffEmploymentSpecification).and(activityDayTypeSpecification).and(staffActivitySpecification);
        activitySpecification.validateRules(shiftWithActivityDTO);
        return filterVoilatedRules(ruleTemplateSpecificInfo.getViolatedRules());
    }


    public void validateStatusOfShiftActivity(Shift shift) {
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            boolean notValid = shiftActivity.getStatus().contains(ShiftStatus.FIX);
            if (notValid) {
                exceptionService.actionNotPermittedException(MESSAGE_SHIFT_STATE_UPDATE, shiftActivity.getStatus());
            }
        }
    }

    public void updateStatusOfShiftActvity(Shift oldStateOfShift, ShiftDTO shiftDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase) {
        boolean valid = false;

        Map<String, ShiftActivityDTO> activityIdAndShiftActivityDTOMap = shiftDTO.getActivities().stream().collect(Collectors.toMap(shiftActivityDTO -> shiftActivityDTO.getActivityId() + "" + shiftActivityDTO.getStartDate(), v -> v));
        for (ShiftActivity shiftActivity : oldStateOfShift.getActivities()) {
            boolean isApprovalRequired = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().getApprovalAllowedPhaseIds().contains(phase.getId());
            String key = shiftActivity.getActivityId() + "" + shiftActivity.getStartDate();
            if (activityIdAndShiftActivityDTOMap.containsKey(key) && (!shiftActivity.getStartDate().equals(activityIdAndShiftActivityDTOMap.get(key).getStartDate()) || !shiftActivity.getEndDate().equals(activityIdAndShiftActivityDTOMap.get(key).getEndDate()))) {
                if (shiftActivity.getStatus().contains(ShiftStatus.FIX)) {
                    valid = true;
                } else if (shiftActivity.getStatus().contains(ShiftStatus.PUBLISH) || shiftActivity.getStatus().contains(ShiftStatus.APPROVE)) {
                    ShiftActivityDTO shiftActivityDTO = activityIdAndShiftActivityDTOMap.get(shiftActivity.getActivityId() + "" + shiftActivity.getStartDate());
                    shiftActivityDTO.getStatus().add(ShiftStatus.MOVED);
                }
            }
            if (isApprovalRequired && !activityIdAndShiftActivityDTOMap.containsKey(key) && (shiftActivity.getStatus().contains(ShiftStatus.APPROVE) && shiftActivity.getStatus().contains(ShiftStatus.PUBLISH))) {
                for (ShiftActivityDTO shiftActivityDTO : shiftDTO.getActivities()) {
                    shiftActivity.getStatus().remove(ShiftStatus.APPROVE);
                    shiftActivityDTO.setStatus(shiftActivity.getStatus());
                    shiftActivityDTO.getStatus().add(ShiftStatus.MOVED);
                }
            }
            if (valid) {
                exceptionService.actionNotPermittedException("message.shift.state.update", shiftActivity.getStatus());
            }

        }
    }

    public static List<ShiftWithActivityDTO> filterShiftsByPlannedTypeAndTimeTypeIds(List<ShiftWithActivityDTO> shifts, Set<BigInteger> timeTypeIds, Set<BigInteger> plannedTimeIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds())) && (CollectionUtils.isNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shift.getActivitiesPlannedTimeIds()));
            if (isValidShift) {
                shiftQueryResultWithActivities.add(shift);
            }

        });
        return shiftQueryResultWithActivities;
    }

    private List<String> filterVoilatedRules(ViolatedRulesDTO violatedRulesDTO) {
        List<String> messages = new ArrayList<>();
        if (violatedRulesDTO != null) {
            violatedRulesDTO.getActivities().forEach(activityRuleViolation -> messages.addAll(activityRuleViolation.getErrorMessages()));
            violatedRulesDTO.getWorkTimeAgreements().forEach(workTimeAgreementRuleViolation -> messages.add(workTimeAgreementRuleViolation.getName() + IS_BROKEN));
        }
        return messages;
    }


    public boolean validateStaffingLevel(Phase phase, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean checkOverStaffing, ShiftActivity shiftActivity, RuleTemplateSpecificInfo ruleTemplateSpecificInfo, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        Date shiftStartDate = shiftActivity.getStartDate();
        Date shiftEndDate = shiftActivity.getEndDate();
        PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        if (!Optional.ofNullable(phaseSettings).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_PHASESETTINGS_ABSENT);
        }
        boolean isStaffingLevelVerify = isVerificationRequired(checkOverStaffing, phaseSettings);
        if (isStaffingLevelVerify) {
            Date startDate = DateUtils.getDateByZoneDateTime(DateUtils.asZonedDateTime(shiftStartDate).truncatedTo(ChronoUnit.DAYS));
            Date endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZonedDateTime(shiftEndDate).truncatedTo(ChronoUnit.DAYS));
            List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(shift.getUnitId(), startDate, endDate);
            List<Shift> shifts = checkOverStaffing ? shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalseAndIdNotEqualTo(shiftStartDate, shiftEndDate, shift.getUnitId(), shift.getId()) : shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(shiftStartDate, shiftEndDate, newArrayList(shift.getUnitId()));
            if(checkOverStaffing){
               shifts.add(shift);
            }
            List<ShiftActivity> shiftActivities = shifts.stream().flatMap(curShift -> curShift.getActivities().stream()).collect(Collectors.toList());
            validateUnderAndOverStaffing(shifts, activityWrapperMap, checkOverStaffing, staffingLevels, shiftActivities, shiftActivity, ruleTemplateSpecificInfo,  staffingLevelActivityWithDurationMap,shift);
            staffingLevelMongoRepository.saveEntities(staffingLevels);
        }
        return isStaffingLevelVerify;
    }

    private void validateUnderAndOverStaffing(List<Shift> shifts, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean checkOverStaffing, List<StaffingLevel> staffingLevels, List<ShiftActivity> shiftActivities, ShiftActivity shiftActivity, RuleTemplateSpecificInfo ruleTemplateSpecificInfo, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, Shift currentShift) {
        ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
        if (activityWrapper.getActivity().getActivityRulesSettings().isEligibleForStaffingLevel()) {
            if (CollectionUtils.isEmpty(staffingLevels)) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ABSENT);
            }
            StaffingLevel staffingLevel = staffingLevels.get(0);
            int lowerLimit;
            int upperLimit;
            if (PRESENCE.equals(activityWrapper.getActivity().getActivityBalanceSettings().getTimeType())) {
                List<StaffingLevelInterval> applicableIntervals = staffingLevel.getPresenceStaffingLevelInterval();
                if (!DateUtils.getLocalDateFromDate(shiftActivity.getStartDate()).equals(DateUtils.getLocalDateFromDate(shiftActivity.getEndDate()))) {
                    lowerLimit = staffingLevelService.getLowerIndex(shiftActivity.getStartDate());
                    upperLimit = 95;
                    checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shifts, checkOverStaffing, shiftActivity, ruleTemplateSpecificInfo, staffingLevelActivityWithDurationMap,currentShift);
                    lowerLimit = 0;
                    upperLimit = staffingLevelService.getUpperIndex(shiftActivity.getEndDate());
                    if (staffingLevels.size() < 2) {
                        exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ABSENT);
                    }
                    staffingLevel = staffingLevels.get(1);
                    applicableIntervals = staffingLevel.getPresenceStaffingLevelInterval();

                    checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shifts, checkOverStaffing, shiftActivity, ruleTemplateSpecificInfo,  staffingLevelActivityWithDurationMap, currentShift);

                } else {
                    lowerLimit = staffingLevelService.getLowerIndex(shiftActivity.getStartDate());
                    upperLimit = staffingLevelService.getUpperIndex(shiftActivity.getEndDate());
                    checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shifts, checkOverStaffing, shiftActivity, ruleTemplateSpecificInfo, staffingLevelActivityWithDurationMap, currentShift);
                }
            } else {
                validateStaffingLevelForAbsenceTypeOfShift(staffingLevel, shiftActivity, checkOverStaffing, shiftActivities, ruleTemplateSpecificInfo);
            }
        }

    }


    public boolean isVerificationRequired(boolean checkOverStaffing, PhaseSettings phaseSettings) {
        boolean result = false;
        if (UserContext.getUserDetails().isStaff()) {
            result = checkOverStaffing ? !phaseSettings.isStaffEligibleForOverStaffing() : !phaseSettings.isStaffEligibleForUnderStaffing();
        } else if (UserContext.getUserDetails().isManagement()) {
            result = checkOverStaffing ? !phaseSettings.isManagementEligibleForOverStaffing() : !phaseSettings.isManagementEligibleForUnderStaffing();
        }
        return result;
    }

    private void checkStaffingLevelInterval(int lowerLimit, int upperLimit, List<StaffingLevelInterval> applicableIntervals, StaffingLevel staffingLevel,
                                            List<Shift> shifts, boolean checkOverStaffing, ShiftActivity shiftActivity, RuleTemplateSpecificInfo ruleTemplateSpecificInfo, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, Shift currentShift) {
        Activity parentActivity = activityMongoRepository.findByChildActivityId(shiftActivity.getActivityId());
        ActivityDTO activity = null;
        if (isNull(parentActivity)) {
            activity = activityMongoRepository.findByIdAndChildActivityEligibleForStaffingLevelTrue(shiftActivity.getActivityId());
        }
        outerLoop:
        for (int currentIndex = lowerLimit; currentIndex <= upperLimit && currentIndex < applicableIntervals.size(); currentIndex++) {
            int shiftsCount = 0;
            Optional<StaffingLevelActivity> staffingLevelActivity = applicableIntervals.get(currentIndex).getStaffingLevelActivities().stream()
                    .filter(sa -> sa.getActivityId().equals(shiftActivity.getActivityId()))
                    .findFirst();
            if (staffingLevelActivity.isPresent()) {
                ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelDuration().getFrom());
                ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelDuration().getTo());
                DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
                for(Shift shift:shifts) {
                    for (ShiftActivity shiftActivityDB : shift.getActivities()) {
                        boolean breakValid = shift.getBreakActivities().stream().anyMatch(shiftActivity1 -> !shiftActivity1.isBreakNotHeld() && interval.overlaps(shiftActivity1.getInterval()));
                        if (!breakValid && shiftActivityDB.getActivityId().equals(shiftActivity.getActivityId()) && interval.overlaps(shiftActivityDB.getInterval())) {
                            shiftsCount++;
                        } else if (!breakValid && activity != null && activity.getChildActivityIds().contains(shiftActivityDB.getActivityId())) {
                            shiftsCount++;
                        }
                    }
                }
                int totalCount = shiftsCount - (checkOverStaffing ? staffingLevelActivity.get().getMaxNoOfStaff() : staffingLevelActivity.get().getMinNoOfStaff());
                boolean breakValid = currentShift.getBreakActivities().stream().anyMatch(shiftActivity1 -> !shiftActivity1.isBreakNotHeld() && interval.overlaps(shiftActivity1.getInterval()));
                if (checkOverStaffing) {
                    StaffingLevelActivityWithDuration staffingLevelActivityWithDuration = staffingLevelActivityWithDurationMap.getOrDefault(shiftActivity.getActivityId(), new StaffingLevelActivityWithDuration(shiftActivity.getActivityId()));
                    if (totalCount > 0  && !breakValid ) {
                        staffingLevelActivityWithDuration.setOverStaffingDurationInMinutes((short) (staffingLevelActivityWithDuration.getOverStaffingDurationInMinutes() + 15));
                    } else if(!breakValid) {
                        staffingLevelActivityWithDuration.setResolvingUnderOrOverStaffingDurationInMinutes((short) (staffingLevelActivityWithDuration.getResolvingUnderOrOverStaffingDurationInMinutes() + 15));
                    }
                    staffingLevelActivityWithDurationMap.put(shiftActivity.getActivityId(), staffingLevelActivityWithDuration);
                }
                if (!checkOverStaffing) {

                    StaffingLevelActivityWithDuration staffingLevelActivityWithDuration = staffingLevelActivityWithDurationMap.getOrDefault(shiftActivity.getActivityId(), new StaffingLevelActivityWithDuration(shiftActivity.getActivityId()));
                    if (totalCount <= 0 && !breakValid) {
                        staffingLevelActivityWithDuration.setUnderStaffingDurationInMinutes((short) (staffingLevelActivityWithDuration.getUnderStaffingDurationInMinutes() + 15));
                    } else if(!breakValid) {
                        staffingLevelActivityWithDuration.setResolvingUnderOrOverStaffingDurationInMinutes((short) (staffingLevelActivityWithDuration.getResolvingUnderOrOverStaffingDurationInMinutes() + 15));
                    }
                    staffingLevelActivityWithDurationMap.put(shiftActivity.getActivityId(), staffingLevelActivityWithDuration);
                }
                if (isNotNull(parentActivity)) {
                    for (StaffingLevelActivity staffingLevelActivityObj : applicableIntervals.get(currentIndex).getStaffingLevelActivities()) {
                        if (checkOverStaffing && staffingLevelActivityObj.getActivityId().equals(parentActivity.getId()) && staffingLevelActivityObj.getAvailableNoOfStaff() >= staffingLevelActivityObj.getMaxNoOfStaff()) {
                            ActivityRuleViolation activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivity.getActivityId())).findAny().orElse(null);
                            if (activityRuleViolation == null) {
                                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, newHashSet(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING, shiftActivity.getActivityName()))));
                            } else {
                                activityRuleViolation.getErrorMessages().add(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING, shiftActivity.getActivityName()));
                            }
                            break outerLoop;
                        } else if (!checkOverStaffing && staffingLevelActivityObj.getActivityId().equals(parentActivity.getId()) && staffingLevelActivityObj.getAvailableNoOfStaff() <= staffingLevelActivityObj.getMinNoOfStaff()) {
                            ActivityRuleViolation activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivity.getActivityId())).findAny().orElse(null);
                            if (activityRuleViolation == null) {
                                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, newHashSet(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING, shiftActivity.getActivityName()))));
                            } else {
                                activityRuleViolation.getErrorMessages().add(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING, shiftActivity.getActivityName()));
                            }
                            break outerLoop;
                        }

                    }
                }
            } else {
                ActivityRuleViolation activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivity.getActivityId())).findAny().orElse(null);
                if (activityRuleViolation == null) {
                    ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, newHashSet(exceptionService.convertMessage(MESSAGE_STAFFINGLEVEL_ACTIVITY, shiftActivity.getActivityName()))));
                } else {
                    activityRuleViolation.getErrorMessages().add(exceptionService.convertMessage(MESSAGE_STAFFINGLEVEL_ACTIVITY, shiftActivity.getActivityName()));
                }

                break;
            }
        }

    }

    public void validateShifts(List<ShiftDTO> shiftDTOS) {
        Date shiftsStartDate = shiftDTOS.get(0).getActivities().get(0).getStartDate();
        Date shiftsEndDate = shiftDTOS.get(shiftDTOS.size() - 1).getActivities().get(shiftDTOS.get(shiftDTOS.size() - 1).getActivities().size() - 1).getEndDate();
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByEmploymentId(shiftDTOS.get(0).getEmploymentId(), shiftsStartDate, shiftsEndDate);
        if (shifts.stream().filter(shift -> !shift.getId().equals(shiftDTOS.get(0).getId())).findAny().isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_SHIFT_DATE_STARTANDEND, shiftsStartDate, shiftsEndDate);
        }
    }

    public boolean validateAccessGroup(ActivityShiftStatusSettings activityShiftStatusSettings, StaffAccessGroupDTO
            staffAccessGroupDTO) {
        return activityShiftStatusSettings != null && staffAccessGroupDTO != null && CollectionUtils.containsAny(activityShiftStatusSettings.getAccessGroupIds(), staffAccessGroupDTO.getAccessGroupIds());
    }


    public List<ShiftActivityDTO> findShiftActivityToValidateStaffingLevel
            (List<ShiftActivity> existingShiftActivities, List<ShiftActivityDTO> arrivedShiftActivities) {
        List<ShiftActivityDTO> shiftActivities = new ArrayList<>();
        try {
            for (int i = 0; i < arrivedShiftActivities.size(); i++) {
                ShiftActivityDTO currentShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(arrivedShiftActivities.get(i), ShiftActivityDTO.class);
                ShiftActivityDTO existingShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(existingShiftActivities.get(i), ShiftActivityDTO.class);
                if (!currentShiftActivity.getActivityId().equals(existingShiftActivity.getActivityId())) {
                    currentShiftActivity.setStartDate(existingShiftActivity.getStartDate());
                    shiftActivities.add(existingShiftActivity);
                    shiftActivities.add(currentShiftActivity);
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //Intentionally left blank to avoid ArrayIndexOutOfBoundsException
        }
        return shiftActivities;
    }

    public List<ShiftWithViolatedInfoDTO> validateShift(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId) {
        BigInteger shiftStateId = shiftDTO.getId();
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        boolean validate = validateGracePeriod(shiftDTO.getStartDate(), validatedByStaff, unitId, actualPhases);
        if (!validate) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = shiftService.updateShift(shiftDTO, true, !validatedByStaff, null);
        shiftDTO.setId(shiftStateId);
        for (ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO : shiftWithViolatedInfoDTOS) {
            shiftDTO = validateShiftStateAfterValidatingWtaRule(shiftDTO, validatedByStaff, actualPhases);
            shiftDTO.setEscalationReasons(shiftWithViolatedInfoDTO.getShifts().get(0).getEscalationReasons());
            shiftDTO.setRestingMinutes(shiftWithViolatedInfoDTO.getShifts().get(0).getRestingMinutes());
            shiftWithViolatedInfoDTO.setShifts(newArrayList(shiftDTO));
        }
        return shiftWithViolatedInfoDTOS;
    }

    public ShiftDTO validateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, Boolean
            validatedByStaff, Phase actualPhases) {
        BigInteger shiftStateId = shiftDTO.getId();
        ShiftState shiftState = shiftStateMongoRepository.findOne(shiftStateId);
        if (shiftState == null) {
            Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
            if (shift != null) {
                shiftDTO.setId(null);
            }
        }
        if (shiftState != null) {
            shiftDTO.setId(shiftStateId);
            shiftDTO.setAccessGroupRole(shiftState.getAccessGroupRole());
            shiftDTO.setValidated(shiftState.getValidated());
            shiftDTO.setShiftStatePhaseId(shiftState.getShiftStatePhaseId());
            shiftDTO.setShiftId(shiftState.getShiftId());
        }
        shiftState = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
        List<BigInteger> activityIds = shiftState.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftState.setShiftId(shiftDTO.getShiftId());
        shiftState.setStartDate(shiftState.getActivities().get(0).getStartDate());
        shiftState.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        shiftState.setValidated(LocalDate.now());
        shiftState.setShiftStatePhaseId(actualPhases.getId());
        shiftState.getActivities().forEach(activity -> {
            activity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            activity.setActivityName(activityWrapperMap.get(activity.getActivityId()).getActivity().getName());
        });
        shiftStateMongoRepository.save(shiftState);
        if (validatedByStaff) {
            shiftState.setAccessGroupRole(AccessGroupRole.MANAGEMENT);
            shiftState.setShiftStatePhaseId(actualPhases.getId());
            shiftState.setId(null);
            shiftState.setValidated(null);
            shiftState.getActivities().forEach(activity -> activity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
            shiftStateMongoRepository.save(shiftState);
        }
        shiftMongoRepository.updateValidateDetailsOfShift(shiftState.getShiftId(), shiftState.getAccessGroupRole(), shiftState.getValidated());
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        if (validatedByStaff) {
            shiftDTO.setEditable(true);
        }
        shiftDTO.setDurationMinutes((int) shiftDTO.getInterval().getMinutes());

        return shiftDTO;

    }

    public void validateRealTimeShift(Long unitId, ShiftDTO shiftDTO, Map<String, Phase> phaseMap) {
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        ShiftState shiftState = shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        Map<BigInteger, ShiftActivity> activityMap = shiftState.getActivities().stream().filter(distinctByKey(ShiftActivity::getId)).collect(Collectors.toMap(ShiftActivity::getId, v -> v));
        boolean realtime = phaseService.shiftEditableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        if (realtime) {
            shiftDTO.getActivities().forEach(shiftActivity -> {
                ShiftActivity shiftActivity1 = activityMap.get(shiftActivity.getId());
                if (shiftActivity1 != null &&
                        ((!shiftActivity.getStartDate().equals(shiftActivity1.getStartDate()) && shiftActivity.getStartDate().before(asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone)))))
                                || (!shiftActivity.getEndDate().equals(shiftActivity1.getEndDate()) && shiftActivity.getEndDate().before(asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone))))))) {
                    exceptionService.actionNotPermittedException(ERROR_ACTIVITY_STARTDATE, shiftActivity.getActivityName());
                }
            });
        }
    }

    //As discussed with Arvind we remove the Check of cross organization overlapping functionality
    public Object[] validateStaffDetailsAndShiftOverlapping(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO, ActivityWrapper activityWrapper, boolean byTandAPhase) {
        Activity activity = activityWrapper.getActivity();
        boolean shiftOverlappedWithNonWorkingType = false;
        validateStaffAndShift(staffAdditionalInfoDTO, shiftDTO, activity);
        DateTimeInterval shiftInterval;
        Date startDate = asDate(shiftDTO.getShiftDate());
        Date endDate;
        if (CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
            endDate = asDate(shiftDTO.getShiftDate().plusDays(6).atTime(LocalTime.MAX));
            shiftInterval = new DateTimeInterval(startDate, endDate);
        } else if (CommonConstants.FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
            endDate = asDate(shiftDTO.getShiftDate().atTime(LocalTime.MAX));
            shiftInterval = new DateTimeInterval(startDate, endDate);
        } else {
            shiftInterval = new DateTimeInterval(shiftDTO.getStartDate(), shiftDTO.getEndDate());
            startDate = getStartOfDay(shiftDTO.getStartDate());
            endDate = getEndOfDay(shiftDTO.getEndDate());
        }
        List<ShiftWithActivityDTO> overlappedShifts = shiftMongoRepository.findOverlappedShiftsByEmploymentId(byTandAPhase ? shiftDTO.getShiftId() : shiftDTO.getId(), staffAdditionalInfoDTO.getId(), startDate, endDate);
        if (isCollectionNotEmpty(overlappedShifts)) {
            if (ShiftType.ABSENCE.equals(overlappedShifts.get(0).getShiftType())) {
                overlappedShifts.get(0).setEndDate(asDateEndOfDay(asLocalDate(overlappedShifts.get(0).getStartDate())));
            }
        }
        Object[] shiftOverlapInfo = getShiftOverlapInfo(overlappedShifts, shiftInterval);
        boolean isShiftOverlapped = (boolean) shiftOverlapInfo[0];
        if (isShiftOverlapped && !CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime()) && isNull(activityWrapper.getActivity().getActivityRulesSettings().getSicknessSetting()) && WORKING_TYPE.name().equals(activityWrapper.getTimeType()) && staffAdditionalInfoDTO.getUserAccessRoleDTO().isManagement()) {
            shiftOverlappedWithNonWorkingType = true;
        }
        return new Object[]{shiftOverlappedWithNonWorkingType, shiftOverlapInfo[1]};
    }

    private void validateStaffAndShift(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO, Activity activity) {
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_ACTIVITY_ID, shiftDTO.getActivities().get(0).getActivityId());
        }
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_NOTFOUND);
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT);
        }
        if (!staffAdditionalInfoDTO.getEmployment().isPublished()) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_NOT_PUBLISHED);
        }
        if (!staffAdditionalInfoDTO.getEmployment().isPublished()) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_NOT_PUBLISHED);
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_UNIT, shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
    }

    private void validateStaffingLevelForAbsenceTypeOfShift(StaffingLevel staffingLevel, ShiftActivity
            shiftActivity, boolean checkOverStaffing, List<ShiftActivity> shiftActivities, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        if (CollectionUtils.isEmpty(staffingLevel.getAbsenceStaffingLevelInterval())) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ABSENT);
        }
        int shiftsCount = 0;
        Optional<StaffingLevelActivity> staffingLevelActivity = staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities().stream().filter(sa -> sa.getActivityId().equals(shiftActivity.getActivityId())).findFirst();
        if (!staffingLevelActivity.isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ACTIVITY, shiftActivity.getActivityName());
        }
        for (ShiftActivity currentShiftActivity : shiftActivities) {
            if (currentShiftActivity.getActivityId().equals(shiftActivity.getActivityId())) {
                shiftsCount++;
            }
        }
        int totalCount = shiftsCount - (checkOverStaffing ? staffingLevelActivity.get().getMaxNoOfStaff() : staffingLevelActivity.get().getMinNoOfStaff());
        if ((checkOverStaffing && totalCount >= 0)) {
            ActivityRuleViolation activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivity.getActivityId())).findAny().orElse(null);
            if (activityRuleViolation == null) {
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, newHashSet(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING))));
            } else {
                activityRuleViolation.getErrorMessages().add(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING));
            }
        } else if (!checkOverStaffing && totalCount <= 0) {

            ActivityRuleViolation activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(shiftActivity.getActivityId())).findAny().orElse(null);
            if (activityRuleViolation == null) {
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, newHashSet(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING))));
            } else {
                activityRuleViolation.getErrorMessages().add(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING));
            }
        }
    }


    //This method is being used to check overlapping shift with full day and full week activity
    public void checkAbsenceTypeShift(ShiftDTO shiftDTO) {
        Date startDate;
        Date endDate;
        if (shiftDTO.getStartDate() != null && asLocalDate(shiftDTO.getEndDate()).isAfter(asLocalDate(shiftDTO.getStartDate()))) {
            startDate = DateUtils.getStartOfDay(shiftDTO.getStartDate());
            endDate = DateUtils.getEndOfDay(shiftDTO.getEndDate());
        } else {
            startDate = asDateStartOfDay(shiftDTO.getShiftDate());
            endDate = asDateEndOfDay(shiftDTO.getShiftDate());
        }
        boolean absenceShiftExists = shiftMongoRepository.absenceShiftExistsByDate(shiftDTO.getUnitId(), startDate, endDate, shiftDTO.getStaffId());
        if (absenceShiftExists) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_OVERLAP_WITH_FULL_DAY);
        }
    }

    public ShiftDTO escalationCorrectionInShift(ShiftDTO shiftDTO, Date oldShiftStartDate, Date oldShiftEndDate, Shift shift) {
        ActivityWrapper activityWrapper = activityMongoRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
        boolean workingTypeShift = WORKING_TYPE.toString().equals(activityWrapper.getTimeType());
        List<Shift> overLappedShifts = shiftMongoRepository.findShiftBetweenDurationByEmploymentId(shift.getEmploymentId(), workingTypeShift ? shiftDTO.getActivities().get(0).getStartDate() : oldShiftStartDate, workingTypeShift ? shiftDTO.getActivities().get(0).getEndDate() : oldShiftEndDate);
        overLappedShifts.forEach(overLappedShift -> {
            if (!shiftOverLappedWithOther(overLappedShift) && isNotNull(overLappedShift.getShiftViolatedRules()) && isCollectionEmpty(overLappedShift.getShiftViolatedRules().getWorkTimeAgreements())) {
                if (getEsclationResolved(shift, overLappedShift)) {
                    shiftDTO.getEscalationFreeShiftIds().add(overLappedShift.getId());
                    overLappedShift.getShiftViolatedRules().setEscalationResolved(true);
                }
            }
            getEsclationResolved(shift, overLappedShift);
        });
        shiftMongoRepository.saveAll(overLappedShifts);
        return shiftDTO;
    }

    private boolean getEsclationResolved(Shift shift, Shift overLappedShift) {
        boolean isResolved = true;
        if (shift.getId().equals(overLappedShift.getId()) && isNotNull(overLappedShift.getShiftViolatedRules())) {
            if (shift.getBreakActivities().stream().anyMatch(ShiftActivity::isBreakNotHeld)) {
                overLappedShift.getShiftViolatedRules().setEscalationResolved(false);
                isResolved = false;
            } else {
                overLappedShift.getShiftViolatedRules().setEscalationResolved(true);
                isResolved = true;
            }

        }
        return isResolved;
    }

    private boolean shiftOverLappedWithOther(Shift shift) {
        return shiftMongoRepository.shiftOverLapped(shift.getEmploymentId(), shift.getStartDate(), shift.getEndDate(), shift.getId());
    }

    public List<ShiftWithActivityDTO> updateFullDayAndFullWeekActivityShifts(List<ShiftWithActivityDTO> shifts) {
        for (ShiftWithActivityDTO shift : shifts) {
            if (shift.getActivities().get(0).getActivity().isFullDayOrFullWeekActivity()) {
                Date startDate = getStartOfDay(shift.getStartDate());
                Date endDate = shift.isOverNightShift() ? shift.getEndDate() : getMidNightOfDay(shift.getEndDate());
                shift.getActivities().get(0).setStartDate(startDate);
                shift.getActivities().get(0).setEndDate(endDate);
                shift.setStartDate(startDate);
                shift.setEndDate(endDate);
            }
        }
        return shifts;
    }

    private Object[] getShiftOverlapInfo(List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval shiftInterval) {
        boolean shiftOverlap = false;
        BigInteger shiftOverlapWithShiftId = null;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            DateTimeInterval existingShiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
            for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                if ((WORKING_TYPE.toString().equals(activity.getTimeType())) && shiftInterval.overlaps(existingShiftInterval)) {
                    shiftOverlapWithShiftId = shiftWithActivityDTO.getId();
                } else if (CommonConstants.FULL_WEEK.equals(activity.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime()) || FULL_DAY.equals(activity.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
                    existingShiftInterval = new DateTimeInterval(getStartOfDay(shiftWithActivityDTO.getStartDate()), getEndOfDay(shiftWithActivityDTO.getEndDate()));
                    if (shiftInterval.overlaps(existingShiftInterval)) {
                        shiftOverlapWithShiftId = shiftWithActivityDTO.getId();
                    }
                }
                if ((NON_WORKING_TYPE.toString().equals(activity.getTimeType()) && shiftInterval.overlaps(existingShiftInterval))) {
                    TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivity().getActivityBalanceSettings().getTimeTypeId());
                    if (isNotNull(timeType) && timeType.isAllowedConflicts()) {
                        shiftOverlap = true;
                    }
                }
            }
        }
        return new Object[]{shiftOverlap, shiftOverlapWithShiftId};
    }


    public ViolatedRulesDTO validateRule(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        validateStatusOfShiftActivity(shift);
        Set<BigInteger> activityIds = new HashSet<>();
        for (ShiftActivity activity : shift.getActivities()) {
            activityIds.add(activity.getActivityId());
            activityIds.addAll(activity.getChildActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet()));
        }
        if (isNotNull(shift.getDraftShift())) {
            for (ShiftActivity draftActivity : shift.getDraftShift().getActivities()) {
                activityIds.add(draftActivity.getActivityId());
                activityIds.addAll(draftActivity.getChildActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet()));
            }
        }
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
        if (isNull(ctaResponseDTO)) {
            exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND, asLocalDate(shift.getStartDate()));
        }
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);

        return validateRuleOnShiftDelete(activityWrapperMap, shift, staffAdditionalInfoDTO);
    }

    public void verifyStaffingLevel(RuleTemplateSpecificInfo ruleTemplateSpecificInfo, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        int totalStaffingLevelResolve = staffingLevelActivityWithDurationMap.values().stream().mapToInt(StaffingLevelActivityWithDuration::getResolvingUnderOrOverStaffingDurationInMinutes).sum();
        int totalUnderStaffingCreated = staffingLevelActivityWithDurationMap.values().stream().mapToInt(StaffingLevelActivityWithDuration::getUnderStaffingDurationInMinutes).sum();
        int totalOverStaffingCreated = staffingLevelActivityWithDurationMap.values().stream().mapToInt(StaffingLevelActivityWithDuration::getOverStaffingDurationInMinutes).sum();
        if (totalOverStaffingCreated > totalStaffingLevelResolve || totalUnderStaffingCreated > totalStaffingLevelResolve) {
            ActivityRuleViolation activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(staffingLevelActivityWithDurationMap.values().iterator().next().getActivityId())).findAny().orElse(null);
            if (activityRuleViolation == null) {
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(staffingLevelActivityWithDurationMap.values().iterator().next().getActivityId(), staffingLevelActivityWithDurationMap.values().iterator().next().getName(), 0, newHashSet(exceptionService.convertMessage(totalOverStaffingCreated > totalStaffingLevelResolve ? MESSAGE_SHIFT_OVERSTAFFING : MESSAGE_SHIFT_UNDERSTAFFING))));
            } else {
                activityRuleViolation.getErrorMessages().add(exceptionService.convertMessage(totalOverStaffingCreated > totalStaffingLevelResolve ? MESSAGE_SHIFT_OVERSTAFFING : MESSAGE_SHIFT_UNDERSTAFFING));
            }
        }


    }

}