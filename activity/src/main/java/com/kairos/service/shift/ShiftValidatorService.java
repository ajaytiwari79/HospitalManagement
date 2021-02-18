package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
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
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeRequiredState;
import com.kairos.enums.shift.*;
import com.kairos.enums.wta.WTATemplateType;
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
import com.kairos.persistence.model.shift.ShiftDataHelper;
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
import com.kairos.rule_validator.RuleExecutionType;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.*;
import com.kairos.service.activity.ActivityUtil;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.staffing_level.StaffingLevelValidatorService;
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
import static com.kairos.enums.wta.WTATemplateType.*;
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
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
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
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private WorkTimeAgreementService workTimeAgreementService;
    @Inject
    private BlockSettingService blockSettingService;
    @Inject
    private ShiftSickService shiftSickService;
    @Inject
    private StaffingLevelValidatorService staffingLevelValidatorService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    private static ExceptionService exceptionService;
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

    public boolean validateGracePeriod(Date shiftDate, Boolean validatedByStaff, Long unitId, Phase phase, String timeZone) {
        boolean valid = true;
        if (TIME_AND_ATTENDANCE.equals(phase.getName())) {
            timeZone = isNull(timeZone) ? userIntegrationService.getTimeZoneByUnitId(unitId) : timeZone;
            DateTimeInterval graceInterval = getGracePeriodInterval(phase, shiftDate, validatedByStaff);
            valid = graceInterval.contains(DateUtils.getDateFromTimeZone(timeZone));
        }
        return valid;
    }

    public ShiftWithViolatedInfoDTO validateRuleCheck(boolean ruleCheckRequired, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shiftWithActivityDTO, Shift oldStateShift, Boolean skipRules) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;
        if (ruleCheckRequired) {
            shiftWithViolatedInfoDTO = validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, oldStateShift, activityWrapperMap, true, false, skipRules);
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

    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift oldShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean byUpdate, boolean byTandAPhase, Boolean skipRules) {
        if (wtaQueryResultDTO.getEndDate() != null && wtaQueryResultDTO.getEndDate().isBefore(asLocalDate(shift.getStartDate()))) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_EXPIRED_UNIT);
        }
        if (staffAdditionalInfoDTO.getUserAccessRoleDTO().isStaff() && !staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaffId().equals(shift.getStaffId())) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PERMISSION);
        }
        Shift mainShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shift.setPhaseId(phase.getId());
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shift, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap, CREATE);
        List<ActivityRuleViolation> activityRuleViolations = validateTimingOfActivity(shift, new ArrayList<>(activityWrapperMap.keySet()), activityWrapperMap);
        ruleTemplateSpecificInfo.getViolatedRules().getActivities().addAll(activityRuleViolations);
        validateStaffingForShift(phase, oldShift, activityWrapperMap, skipRules, mainShift, staffAdditionalInfoDTO.getReplacedActivity());
        validateAbsenceReasonCodeRule(activityWrapperMap, shift, ruleTemplateSpecificInfo);
        updateScheduledAndDurationMinutesInShift(shift, staffAdditionalInfoDTO);
        boolean gracePeriodValid = validateGracePeriod(shift.getStartDate(), UserContext.getUserDetails().isStaff(), shift.getUnitId(), phase, null);
        if (!gracePeriodValid) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        CalculatePlannedHoursAndScheduledHours calculatePlannedHoursAndScheduledHours = new CalculatePlannedHoursAndScheduledHours(staffAdditionalInfoDTO, dateTimeInterval, newArrayList(shift), false, false, dayTypeDTOMap, timeBankCalculationService).calculate();
        shift.setPlannedMinutesOfTimebank(calculatePlannedHoursAndScheduledHours.getTotalDailyPlannedMinutes());
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkillLevelDTOS(), ruleTemplateSpecificInfo);
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
        activitySpecification = getDayTypeSpecification(shift, dayTypeDTOMap, activitySpecification);
        shift.setTimeType(activityWrapperMap.get(shift.getActivities().get(0).getActivityId()).getTimeType());
        activitySpecification.validateRules(shift, RuleExecutionType.SHIFT_CREATION);
        return new ShiftWithViolatedInfoDTO(ruleTemplateSpecificInfo.getViolatedRules());
    }

    private Specification<ShiftWithActivityDTO> getDayTypeSpecification(ShiftWithActivityDTO shift, Map<BigInteger, DayTypeDTO> dayTypeDTOMap, Specification<ShiftWithActivityDTO> activitySpecification) {
        List<BigInteger> dayTypeIds = shift.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getActivity().getActivityRulesSettings().getDayTypes().stream()).collect(Collectors.toList());
        if (isCollectionNotEmpty(dayTypeIds)) {
            Set<DayOfWeek> validDays = getValidDays(dayTypeDTOMap, dayTypeIds, asLocalDate(shift.getStartDate()));
            Specification<ShiftWithActivityDTO> activityDayTypeSpec = new DayTypeSpecification(validDays, shift.getStartDate());
            activitySpecification = activitySpecification.and(activityDayTypeSpec);
        }
        return activitySpecification;
    }

    private void validateStaffingForShift(Phase phase, Shift oldShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, Boolean skipRules, Shift mainShift, ShiftActivityDTO replacedActivity) {
        if (isNull(skipRules) || !skipRules) {
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(phase.getOrganizationId(), phase.getId());
            List<ShiftActivity>[] shiftActivities = mainShift.getShiftActivitiesForValidatingStaffingLevel(oldShift);
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMapForUnderStaffing = new HashMap<>();
            for (ShiftActivity shiftActivity : shiftActivities[0]) {
                Phase oldShiftPhase=phaseService.getCurrentPhaseByUnitIdAndDate(oldShift.getUnitId(),oldShift.getStartDate(),oldShift.getEndDate());
                staffingLevelValidatorService.validateStaffingLevel(oldShiftPhase, oldShift, activityWrapperMap, false, shiftActivity, staffingLevelActivityWithDurationMapForUnderStaffing, false);
            }
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMapForOverStaffing = new HashMap<>();
            for (ShiftActivity shiftActivity : shiftActivities[1]) {
                staffingLevelValidatorService.validateStaffingLevel(phase, mainShift, activityWrapperMap, true, shiftActivity, staffingLevelActivityWithDurationMapForOverStaffing, false);
            }
            staffingLevelValidatorService.verifyStaffingLevel(staffingLevelActivityWithDurationMapForUnderStaffing, staffingLevelActivityWithDurationMapForOverStaffing, oldShift != null ? phaseSettings.getMaxProblemAllowed() : null, oldShift, activityWrapperMap, skipRules != null, replacedActivity);
        }
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
        boolean gracePeriodValid = validateGracePeriod(shift.getStartDate(), UserContext.getUserDetails().isStaff(), shift.getUnitId(), phase, null);
        if (!gracePeriodValid) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
        Specification<Shift> shiftSpecification = new ShiftAllowedToDelete(activityWrapperMap, phase.getId());
        shiftSpecification.validateRules(shift, RuleExecutionType.SHIFT_DELETE);
        ViolatedRulesDTO violatedRulesDTO = new ViolatedRulesDTO();
        WTAQueryResultDTO wtaQueryResultDTO = workTimeAgreementService.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shift.getActivities().get(0).getStartDate()));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTO.getRuleTemplates().stream().filter(this::isValidWTARuleForDelete).collect(Collectors.toList());
        if (isCollectionNotEmpty(wtaBaseRuleTemplates)) {
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class), activityWrapperMap, null);
            RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shiftWithActivityDTO, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap, ShiftOperationType.DELETE);
            List<ShiftActivity>[] shiftActivities = shift.getShiftActivitiesForValidatingStaffingLevel(shift);
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMapForUnderStaffing = new HashMap<>();
            for (ShiftActivity shiftActivity : shiftActivities[0]) {
                staffingLevelValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, shiftActivity, staffingLevelActivityWithDurationMapForUnderStaffing, false);
            }
            staffingLevelValidatorService.verifyStaffingLevel(staffingLevelActivityWithDurationMapForUnderStaffing, new HashMap<>(), null, shift, activityWrapperMap, false, null);
            Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaBaseRuleTemplates);
            wtaRulesSpecification.validateRules(shiftWithActivityDTO, RuleExecutionType.SHIFT_DELETE);
            violatedRulesDTO = ruleTemplateSpecificInfo.getViolatedRules();
        }
        return violatedRulesDTO;
    }

    private boolean isValidWTARuleForDelete(WTABaseRuleTemplate wtaRule) {
        return newHashSet(NUMBER_OF_PARTOFDAY, CONSECUTIVE_WORKING_PARTOFDAY).contains(wtaRule.getWtaTemplateType());
    }

    private void updateScheduledAndDurationMinutesInShift(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivityDTO, null, staffAdditionalInfoDTO.getEmployment(), false);
            scheduledMinutes += shiftActivityDTO.getScheduledMinutes();
            durationMinutes += shiftActivityDTO.getDurationMinutes();
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
        LOGGER.info("Current phase is {} for date {}", phase.getName(), new DateTime(shift.getStartDate()));
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
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().filter(distinctByKey(staffWTACounter -> staffWTACounter.getRuleTemplateId())).collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, StaffWTACounter::getCount));
        Map<String, TimeSlot> timeSlotMap = staffAdditionalInfoDTO.getTimeSlotSets().stream().collect(Collectors.toMap(TimeSlotDTO::getName, v -> new TimeSlot(v)));
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
        if (!ShiftOperationType.DELETE.equals(shiftOperationType)) {
            shifts.add(shift);
        }
        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotMap, phase.getId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, expectedTimebank, activityWrapperMap, staffAdditionalInfoDTO.getStaffAge(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate(), expertiseNightWorkerSetting, isNotNull(nightWorker) ? nightWorker.isNightWorker() : false, phase.getPhaseEnum(), staffChildAges, shiftOperationType);
    }

    public RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(Phase phase, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDataHelper shiftDataHelper, ShiftOperationType shiftOperationType) {
        LOGGER.info("Current phase is {} for date {}", phase.getName(), new DateTime(shift.getStartDate()));
        WTAQueryResultDTO wtaQueryResultDTO = shiftDataHelper.getWtaByDate(asLocalDate(shift.getStartDate()), shift.getEmploymentId());
        Set<WTATemplateType> templateTypes = wtaQueryResultDTO.getWTATemplateTypes();
        PlanningPeriodDTO planningPeriod = shiftDataHelper.getPlanningPeriod();
        List<StaffWTACounter> staffWTACounters = shiftDataHelper.getStaffWTACounter(staffAdditionalInfoDTO.getEmployment().getId());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates(), new HashMap<>(), shiftDataHelper.getPlanningPeriod().getLastPlanningPeriodEndDate());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(null, shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()), false);
        if (isNotNull(shift.getId())) {
            BigInteger shiftId = shift.getId();
            shifts = shifts.stream().filter(shiftWithActivityDTO -> !shiftWithActivityDTO.getId().equals(shiftId)).collect(Collectors.toList());
        }

        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, StaffWTACounter::getCount));
        Map<String, TimeSlot> timeSlotMap = shiftDataHelper.getTimeSlot().getTimeSlots().stream().collect(Collectors.toMap(TimeSlot::getName, v -> v));
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = shiftDataHelper.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        if (templateTypes.contains(WTATemplateType.DURATION_BETWEEN_SHIFTS)) {
            shifts = updateFullDayAndFullWeekActivityShifts(shifts);
            shift = updateFullDayAndFullWeekActivityShifts(newArrayList(shift)).get(0);
        }
        shift.setTimeType(shiftDataHelper.getActivityById(shift.getActivities().get(0).getActivityId()).getTimeType().getTimeTypes());
        wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shifts, shiftDataHelper);

        long expectedTimebank = 0;
        if (templateTypes.contains(WTATemplateType.TIME_BANK)) {
            expectedTimebank = timeBankService.getExpectedTimebankByDate(shift, staffAdditionalInfoDTO, shiftDataHelper);
        }
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = null;
        NightWorker nightWorker = null;
        if (templateTypes.contains(WTATemplateType.DAYS_OFF_AFTER_A_SERIES)) {
            expertiseNightWorkerSetting = shiftDataHelper.getExpertiseNightWorkerSettingByExpertiseId(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
            nightWorker = shiftDataHelper.getNightWorkerByStaffId(shift.getStaffId());
        }
        List<Integer> staffChildAges = new ArrayList<>();
        if (templateTypes.contains(CHILD_CARE_DAYS_CHECK)) {
            staffChildAges = getChildAges(shift.getStartDate(), staffAdditionalInfoDTO);
        }
        staffAdditionalInfoDTO.setStaffAge(getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), asLocalDate(shift.getStartDate())));
        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotMap, phase.getId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, expectedTimebank, new HashMap<>(), staffAdditionalInfoDTO.getStaffAge(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), shiftDataHelper.getPlanningPeriod().getLastPlanningPeriodEndDate(), expertiseNightWorkerSetting, isNotNull(nightWorker) ? nightWorker.isNightWorker() : false, phase.getPhaseEnum(), staffChildAges, shiftOperationType);
    }

    public List<Integer> getChildAges(Date shiftStartDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<Integer> staffChildAges = new ArrayList<>();
        for (StaffChildDetailDTO staffChildDetailDTO : staffAdditionalInfoDTO.getStaffChildDetails()) {
            staffChildAges.add(getAgeByCPRNumberAndStartDate(staffChildDetailDTO.getCprNumber(), asLocalDate(shiftStartDate)));
        }
        return staffChildAges;
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

    public List<ShiftWithViolatedInfoDTO> validateShift(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId) {
        BigInteger shiftStateId = shiftDTO.getId();
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        boolean validate = validateGracePeriod(shiftDTO.getStartDate(), validatedByStaff, unitId, actualPhases, null);
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

    public ShiftDTO validateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, Boolean validatedByStaff, Phase actualPhases) {
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
        prepareShiftStateDataAndSave(shiftDTO, validatedByStaff, actualPhases, shiftState);
        shiftMongoRepository.updateValidateDetailsOfShift(shiftState.getShiftId(), shiftState.getAccessGroupRole(), shiftState.getValidated());
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        if (validatedByStaff) {
            shiftDTO.setEditable(true);
        }
        shiftDTO.setDurationMinutes((int) shiftDTO.getInterval().getMinutes());
        return shiftDTO;
    }

    private void prepareShiftStateDataAndSave(ShiftDTO shiftDTO, Boolean validatedByStaff, Phase actualPhases, ShiftState shiftState) {
        List<BigInteger> activityIds = shiftState.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftState.setShiftId(shiftDTO.getShiftId());
        shiftState.setStartDate(shiftState.getActivities().get(0).getStartDate());
        shiftState.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        shiftState.setValidated(LocalDate.now());
        shiftState.setShiftStatePhaseId(actualPhases.getId());
        shiftState.getActivities().forEach(shiftActivity -> {
            shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            Activity activity1 = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
            shiftActivity.setActivityName(activity1.getName());
            shiftActivity.setUltraShortName(activity1.getActivityGeneralSettings().getUltraShortName());
            shiftActivity.setShortName(activity1.getActivityGeneralSettings().getShortName());
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

    //This method is being used to check overlapping shift with full day and full week activity
    public void checkAbsenceTypeShift(ShiftDTO shiftDTO) {
        Date startDate;
        Date endDate;
        if (shiftDTO.getStartDate() != null && asLocalDate(shiftDTO.getEndDate()).isAfter(asLocalDate(shiftDTO.getStartDate()))) {
            startDate = DateUtils.getStartOfDay(shiftDTO.getStartDate());
            endDate = DateUtils.getEndOfDay(shiftDTO.getStartDate());
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
                } else if (CommonConstants.FULL_WEEK.equals(activity.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(activity.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
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

    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDataHelper shiftDataHelper) {
        WTAQueryResultDTO wtaQueryResultDTO = shiftDataHelper.getWtaByDate(asLocalDate(shift.getStartDate()), staffAdditionalInfoDTO.getEmployment().getId());
        if (wtaQueryResultDTO.getEndDate() != null && wtaQueryResultDTO.getEndDate().isBefore(asLocalDate(shift.getStartDate()))) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_EXPIRED_UNIT);
        }
        shift.setPhaseId(phase.getId());
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shift, staffAdditionalInfoDTO, shiftDataHelper, CREATE);
        updateScheduledAndDurationMinutesInShift(shift, staffAdditionalInfoDTO);
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        Map<BigInteger, DayTypeDTO> dayTypeDTOMap = shiftDataHelper.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        CalculatePlannedHoursAndScheduledHours calculatePlannedHoursAndScheduledHours = new CalculatePlannedHoursAndScheduledHours(staffAdditionalInfoDTO, dateTimeInterval, newArrayList(shift), false, false, dayTypeDTOMap, timeBankCalculationService).calculate();
        shift.setPlannedMinutesOfTimebank(calculatePlannedHoursAndScheduledHours.getTotalDailyPlannedMinutes());
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        shift.setTimeType(shiftDataHelper.getActivityById(shift.getActivities().get(0).getActivityId()).getTimeType().getTimeTypes());
        wtaRulesSpecification.validateRules(shift, RuleExecutionType.COVER_SHIFT);
        return new ShiftWithViolatedInfoDTO(newArrayList(shift), ruleTemplateSpecificInfo.getViolatedRules());
    }
}