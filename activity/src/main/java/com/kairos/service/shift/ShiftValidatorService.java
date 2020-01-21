package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
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
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.getHoursStringByMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeTypes.NON_WORKING_TYPE;
import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static com.kairos.enums.shift.ShiftOperationType.CREATE;
import static com.kairos.enums.wta.WTATemplateType.*;
import static com.kairos.utils.CPRUtil.getAgeByCPRNumberAndStartDate;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getIntervalByRuleTemplates;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValidDays;
import static java.util.Collections.singletonList;

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
    private
    ShiftMongoRepository shiftMongoRepository;
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
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
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

    public boolean validateGracePeriod(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId, Phase phase) {
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        DateTimeInterval graceInterval = getGracePeriodInterval(phase, shiftDTO.getActivities().get(0).getStartDate(), validatedByStaff);
        return graceInterval.contains(DateUtils.getDateFromTimeZone(timeZone));
    }

    public DateTimeInterval getGracePeriodInterval(Phase phase, Date date, boolean forStaff) {
        ZonedDateTime startDate = DateUtils.asZoneDateTime(date);
        ZonedDateTime endDate;
        if (forStaff) {
            endDate = startDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(phase.getGracePeriodByStaff()).plusDays(1);
        } else {
            endDate = startDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(phase.getGracePeriodByStaff() + phase.getGracePeriodByManagement()).plusDays(1);
        }
        return new DateTimeInterval(startDate, endDate);
    }

    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift oldShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean byUpdate, boolean byTandAPhase) {
        if (wtaQueryResultDTO.getEndDate() != null && wtaQueryResultDTO.getEndDate().isBefore(asLocalDate(shift.getEndDate()))) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_EXPIRED_UNIT);
        }
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), asLocalDate(shift.getStartDate()));
        if (planningPeriod == null) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PLANNING_PERIOD_EXITS, shift.getStartDate());
        }
        if (staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff() && !staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaffId().equals(shift.getStaffId())) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PERMISSION);
        }
        Shift mainShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shift.setPhaseId(phase.getId());
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shift, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap, CREATE);
        List<ActivityRuleViolation> activityRuleViolations = validateTimingOfActivity(shift, new ArrayList<>(activityWrapperMap.keySet()), activityWrapperMap);
        ruleTemplateSpecificInfo.getViolatedRules().getActivities().addAll(activityRuleViolations);
        List<ShiftActivity>[] shiftActivities = mainShift.getShiftActivitiesForValidatingStaffingLevel(null);
        for (ShiftActivity shiftActivity : shiftActivities[0]) {
            validateStaffingLevel(phase, mainShift, activityWrapperMap, true, shiftActivity, ruleTemplateSpecificInfo);
        }
        validateAbsenceReasonCodeRule(activityWrapperMap, shift, ruleTemplateSpecificInfo);
        updateScheduledAndDurationMinutesInShift(shift, staffAdditionalInfoDTO, activityWrapperMap);
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        TimeBankCalculationService.CalculatePlannedHoursAndScheduledHours calculatePlannedHoursAndScheduledHours = timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours(staffAdditionalInfoDTO, dateTimeInterval, newArrayList(shift), false, false, dayTypeDTOMap).calculate();
        shift.setPlannedMinutesOfTimebank(calculatePlannedHoursAndScheduledHours.getTotalDailyPlannedMinutes());
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkillLevelDTOS(), ruleTemplateSpecificInfo, exceptionService);
        Specification<ShiftWithActivityDTO> tagSpecification = new TagSpecification(staffAdditionalInfoDTO.getTags(), ruleTemplateSpecificInfo, exceptionService);
        Set<BigInteger> allActivities = getAllActivitiesOfTeam(shift);
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getEmployment().getExpertise(), ruleTemplateSpecificInfo, allActivities);
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        Specification<ShiftWithActivityDTO> activitySpecification = activityExpertiseSpecification.and(activitySkillSpec).and(wtaRulesSpecification).and(tagSpecification);
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
        List<Long> dayTypeIds = shift.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getActivity().getRulesActivityTab().getDayTypes().stream()).collect(Collectors.toList());
        if (isCollectionNotEmpty(dayTypeIds)) {
            Set<DayOfWeek> validDays = getValidDays(dayTypeDTOMap, dayTypeIds);
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

        Specification<Shift> shiftSpecification = new ShiftAllowedToDelete(activityWrapperMap, phase.getId());
        shiftSpecification.validateRules(shift);
        ViolatedRulesDTO violatedRulesDTO = new ViolatedRulesDTO();
        WTAQueryResultDTO wtaQueryResultDTO = workTimeAgreementService.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shift.getActivities().get(0).getStartDate()));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTO.getRuleTemplates().stream().filter(this::isValidWTARuleForDelete).collect(Collectors.toList());
        if (isCollectionNotEmpty(wtaBaseRuleTemplates)) {
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class), activityWrapperMap);
            RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shiftWithActivityDTO, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap, ShiftOperationType.DELETE);
            List<ShiftActivity>[] shiftActivities = shift.getShiftActivitiesForValidatingStaffingLevel(shift);
            for (ShiftActivity shiftActivity : shiftActivities[1]) {
                validateStaffingLevel(phase, shift, activityWrapperMap, false, shiftActivity,  ruleTemplateSpecificInfo);
            }
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
        if (ReasonCodeRequiredState.MANDATORY.equals(activity.getRulesActivityTab().getReasonCodeRequiredState()) && !Optional.ofNullable(childActivity.getAbsenceReasonCodeId()).isPresent()) {
            activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getId())).findAny().orElse(null);
            if (activityRuleViolation == null) {
                activityRuleViolation = new ActivityRuleViolation(activity.getId(), activity.getName(), 0, singletonList(exceptionService.
                        convertMessage(MESSAGE_SHIFT_REASONCODE_REQUIRED, activity.getId())));
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
            } else {
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getId())).findAny().get().getErrorMessages().add(exceptionService.
                        convertMessage(MESSAGE_SHIFT_REASONCODE_REQUIRED, activity.getId()));
            }
        }
    }

    public void validateShiftViolatedRules(Shift shift, boolean shiftOverlappedWithNonWorkingType, ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO, ShiftActionType actionType) {
        ShiftViolatedRules shiftViolatedRules = shiftViolatedRulesMongoRepository.findOneViolatedRulesByShiftId(shift.getId(), isNotNull(shift.getDraftShift()));
        shiftViolatedRulesMongoRepository.deleteAllViolatedRulesByShiftIds(newArrayList(shift.getId()));
        if (ShiftActionType.SAVE.equals(actionType) || ShiftActionType.CANCEL.equals(actionType)) {
            shiftViolatedRules = updateOrDeleteShiftViolatedRule(shift, actionType, shiftViolatedRules);
        } else {
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
            shiftViolatedRulesMongoRepository.save(shiftViolatedRules);
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

    private ShiftViolatedRules updateOrDeleteShiftViolatedRule(Shift shift, ShiftActionType actionType, ShiftViolatedRules shiftViolatedRules) {
        ShiftViolatedRules draftShiftViolatedRules = shiftViolatedRulesMongoRepository.findOneViolatedRulesByShiftId(shift.getId(), true);
        if (isNotNull(draftShiftViolatedRules) && ShiftActionType.SAVE.equals(actionType)) {
            shiftViolatedRules = draftShiftViolatedRules;
        }
        if (isNotNull(draftShiftViolatedRules)) {
            shiftViolatedRulesMongoRepository.delete(draftShiftViolatedRules);
        }
        return shiftViolatedRules;
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
            Short shortestTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getShortestTime() : staffActivitySettingMap.get(activityId).getShortestTime();
            Short longestTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getLongestTime() : staffActivitySettingMap.get(activityId).getLongestTime();
            LocalTime earliestStartTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getEarliestStartTime() : staffActivitySettingMap.get(activityId).getEarliestStartTime();
            LocalTime latestStartTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getLatestStartTime() : staffActivitySettingMap.get(activityId).getLatestStartTime();
            getErrorMessages(shiftTimeDetails, errorMessages, shortestTime, longestTime, earliestStartTime, latestStartTime);
            if (!errorMessages.isEmpty()) {
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                activityRuleViolations.add(new ActivityRuleViolation(activityId, activity.getName(), 0, errorMessages));
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
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()), false);
        if (isNotNull(shift.getId())) {
            BigInteger shiftId = shift.getId();
            shifts = shifts.stream().filter(shiftWithActivityDTO -> !shiftWithActivityDTO.getId().equals(shiftId)).collect(Collectors.toList());
        }
        shifts = updateFullDayAndFullWeekActivityShift(shifts);
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, StaffWTACounter::getCount));
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = staffAdditionalInfoDTO.getTimeSlotSets().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        shift = updateFullDayAndFullWeekActivityShift(newArrayList(shift)).get(0);
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
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()), false);
            newCreatedShiftWithActivityDTOs.addAll(shifts);
        } else {
            shifts = newCreatedShiftWithActivityDTOs;
        }
        shifts = updateFullDayAndFullWeekActivityShift(shifts);
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, StaffWTACounter::getCount));
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = dataWrapper.getTimeSlotWrappers().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<Long, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        shift = updateFullDayAndFullWeekActivityShift(newArrayList(shift)).get(0);
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
        Map<Long, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(ruleTemplateSpecificInfo.getShift().getStaffId(), new ArrayList<>(activityWrapperMap.keySet()));
        Map<BigInteger, List<Long>> activityWiseDayType = staffActivitySettings.stream().collect(Collectors.toMap(k -> k.getActivityId(), v -> v.getDayTypeIds()));
        List<Long> dayTypeIds = ruleTemplateSpecificInfo.getShift().getActivities().stream().flatMap(shiftActivityDTO -> activityWiseDayType.containsKey(shiftActivityDTO.getActivity().getId()) ? activityWiseDayType.get(shiftActivityDTO.getActivity().getId()).stream() : shiftActivityDTO.getActivity().getRulesActivityTab().getDayTypes().stream()).collect(Collectors.toList());
        Set<DayOfWeek> validDays = isCollectionNotEmpty(dayTypeIds) ? getValidDays(dayTypeDTOMap, dayTypeIds) : new HashSet<>();
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftWithActivityDTO.getUnitId(), ruleTemplateSpecificInfo.getShift().getActivities().get(0).getStartDate(), ruleTemplateSpecificInfo.getShift().getActivities().get(0).getEndDate());
        Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, Shift.class);
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            validateStaffingLevel(phase, shift, activityWrapperMap, true, shiftActivity, ruleTemplateSpecificInfo);
        }
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTOS.get(0).getRuleTemplates());
        Set<BigInteger> allActivities = getAllActivitiesOfTeam(shiftWithActivityDTO);
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffEmploymentDetails.getExpertise(), ruleTemplateSpecificInfo, allActivities);
        Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, staffAdditionalInfoDTO);
        Specification<ShiftWithActivityDTO> activityDayTypeSpecification = new DayTypeSpecification(validDays, ruleTemplateSpecificInfo.getShift().getStartDate());
        Specification<ShiftWithActivityDTO> activitySpecification = activityExpertiseSpecification.and(wtaRulesSpecification).and(staffEmploymentSpecification).and(activityDayTypeSpecification);
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

    public void updateStatusOfShiftActvity(Shift oldStateOfShift, ShiftDTO shiftDTO) {
        boolean valid = false;
        Map<String, ShiftActivityDTO> activityIdAndShiftActivityDTOMap = shiftDTO.getActivities().stream().collect(Collectors.toMap(shiftActivityDTO -> shiftActivityDTO.getActivityId() + "" + shiftActivityDTO.getStartDate(), v -> v));
        for (ShiftActivity shiftActivity : oldStateOfShift.getActivities()) {
            String key = shiftActivity.getActivityId() + "" + shiftActivity.getStartDate();
            if (activityIdAndShiftActivityDTOMap.containsKey(key) && (!shiftActivity.getStartDate().equals(activityIdAndShiftActivityDTOMap.get(key).getStartDate()) || !shiftActivity.getEndDate().equals(activityIdAndShiftActivityDTOMap.get(key).getEndDate()))) {
                if (shiftActivity.getStatus().contains(ShiftStatus.FIX)) {
                    valid = true;
                } else if (shiftActivity.getStatus().contains(ShiftStatus.PUBLISH)) {
                    activityIdAndShiftActivityDTOMap.get(shiftActivity.getActivityId() + "" + shiftActivity.getStartDate()).getStatus().add(ShiftStatus.MOVED);
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


    public void validateStaffingLevel(Phase phase, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean checkOverStaffing, ShiftActivity shiftActivity, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        Date shiftStartDate = shiftActivity.getStartDate();
        Date shiftEndDate = shiftActivity.getEndDate();
        PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        if (!Optional.ofNullable(phaseSettings).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_PHASESETTINGS_ABSENT);
        }

        if (isVerificationRequired(checkOverStaffing, phaseSettings)) {
            Date startDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftStartDate).truncatedTo(ChronoUnit.DAYS));
            Date endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftEndDate).truncatedTo(ChronoUnit.DAYS));
            List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(shift.getUnitId(), startDate, endDate);
            List<Shift> shifts = checkOverStaffing ? shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalseAndIdNotEqualTo(shiftStartDate, shiftEndDate, shift.getUnitId(), shift.getId()) : shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(shiftStartDate, shiftEndDate, newArrayList(shift.getUnitId()));
            List<ShiftActivity> shiftActivities = shifts.stream().flatMap(curShift -> curShift.getActivities().stream()).collect(Collectors.toList());
            validateUnderAndOverStaffing(shift, activityWrapperMap, checkOverStaffing, staffingLevels, shiftActivities, shiftActivity, ruleTemplateSpecificInfo);
            staffingLevelMongoRepository.saveEntities(staffingLevels);

        }
    }

    private void validateUnderAndOverStaffing(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean checkOverStaffing, List<StaffingLevel> staffingLevels, List<ShiftActivity> shiftActivities, ShiftActivity shiftActivity,  RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
        if (activityWrapper.getActivity().getRulesActivityTab().isEligibleForStaffingLevel()) {
            if (CollectionUtils.isEmpty(staffingLevels)) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ABSENT);
            }
            StaffingLevel staffingLevel = staffingLevels.get(0);
            int lowerLimit = 0;
            int upperLimit = 0;
            if (ShiftType.PRESENCE.equals(shift.getShiftType())) {
                List<StaffingLevelInterval> applicableIntervals = staffingLevel.getPresenceStaffingLevelInterval();
                if (!DateUtils.getLocalDateFromDate(shiftActivity.getStartDate()).equals(DateUtils.getLocalDateFromDate(shiftActivity.getEndDate()))) {
                    lowerLimit = staffingLevelService.getLowerIndex(shiftActivity.getStartDate());
                    upperLimit = 95;
                    checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shiftActivities, checkOverStaffing, shiftActivity, ruleTemplateSpecificInfo);
                    lowerLimit = 0;
                    upperLimit = staffingLevelService.getUpperIndex(shiftActivity.getEndDate());
                    if (staffingLevels.size() < 2) {
                        exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ABSENT);
                    }
                    staffingLevel = staffingLevels.get(1);
                    applicableIntervals = staffingLevel.getPresenceStaffingLevelInterval();

                    checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shiftActivities, checkOverStaffing, shiftActivity, ruleTemplateSpecificInfo);

                } else {
                    lowerLimit = staffingLevelService.getLowerIndex(shiftActivity.getStartDate());
                    upperLimit = staffingLevelService.getUpperIndex(shiftActivity.getEndDate());
                    checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shiftActivities, checkOverStaffing, shiftActivity, ruleTemplateSpecificInfo);
                }
            } else {
                validateStaffingLevelForAbsenceTypeOfShift(staffingLevel, shiftActivity, checkOverStaffing, shiftActivities,ruleTemplateSpecificInfo);
            }
        }

    }


    private boolean isVerificationRequired(boolean checkOverStaffing, PhaseSettings phaseSettings) {
        boolean result = false;
        if (UserContext.getUserDetails().isStaff()) {
            result = checkOverStaffing ? !phaseSettings.isStaffEligibleForOverStaffing() : !phaseSettings.isStaffEligibleForUnderStaffing();
        } else if (UserContext.getUserDetails().isManagement()) {
            result = checkOverStaffing ? !phaseSettings.isManagementEligibleForOverStaffing() : !phaseSettings.isManagementEligibleForUnderStaffing();
        }
        return result;
    }

    private void checkStaffingLevelInterval(int lowerLimit, int upperLimit, List<StaffingLevelInterval> applicableIntervals, StaffingLevel staffingLevel,
                                            List<ShiftActivity> shiftActivities, boolean checkOverStaffing, ShiftActivity shiftActivity, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        Activity parentActivity = activityMongoRepository.findByChildActivityId(shiftActivity.getActivityId());
        ActivityDTO activity = null;
        if (isNull(parentActivity)) {
            activity = activityMongoRepository.findByIdAndChildActivityEligibleForStaffingLevelTrue(shiftActivity.getActivityId());
        }
        outerLoop:
        for (int currentIndex = lowerLimit; currentIndex <= upperLimit; currentIndex++) {
            int shiftsCount = 0;
            Optional<StaffingLevelActivity> staffingLevelActivity = applicableIntervals.get(currentIndex).getStaffingLevelActivities().stream()
                    .filter(sa -> sa.getActivityId().equals(shiftActivity.getActivityId()))
                    .findFirst();
            if (staffingLevelActivity.isPresent()) {
                ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelDuration().getFrom());
                ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelDuration().getTo());
                DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
                for (ShiftActivity shiftActivityDB : shiftActivities) {
                    if (shiftActivityDB.getActivityId().equals(shiftActivity.getActivityId()) && interval.overlaps(shiftActivityDB.getInterval())) {
                        shiftsCount++;
                    } else if (activity != null && activity.getChildActivityIds().contains(shiftActivityDB.getActivityId())) {
                        shiftsCount++;
                    }
                }
                int totalCount = shiftsCount - (checkOverStaffing ? staffingLevelActivity.get().getMaxNoOfStaff() : staffingLevelActivity.get().getMinNoOfStaff());
                if (checkOverStaffing && totalCount >= 0) {
                    ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING))));
                    break;
                }
                if (!checkOverStaffing && totalCount <= 0) {
                    ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING))));
                    break;
                }
                if (isNotNull(parentActivity)) {
                    for (StaffingLevelActivity staffingLevelActivityObj : applicableIntervals.get(currentIndex).getStaffingLevelActivities()) {
                        if (checkOverStaffing && staffingLevelActivityObj.getActivityId().equals(parentActivity.getId()) && staffingLevelActivityObj.getAvailableNoOfStaff() >= staffingLevelActivityObj.getMaxNoOfStaff()) {
                            ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING))));
                            break outerLoop;
                        } else if (!checkOverStaffing && staffingLevelActivityObj.getActivityId().equals(parentActivity.getId()) && staffingLevelActivityObj.getAvailableNoOfStaff() <= staffingLevelActivityObj.getMinNoOfStaff()) {
                            ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING))));
                            break outerLoop;
                        }

                    }
                }
            } else {
                ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_STAFFINGLEVEL_ACTIVITY, shiftActivity.getActivityName()))));
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

    public ShiftWithViolatedInfoDTO validateShift(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId) {
        BigInteger shiftStateId = shiftDTO.getId();
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        boolean validate = validateGracePeriod(shiftDTO, validatedByStaff, unitId, actualPhases);
        if (!validate) {
            exceptionService.invalidRequestException(MESSAGE_SHIFT_CANNOT_UPDATE);
        }
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftService.updateShift(shiftDTO, true, !validatedByStaff, null);
        shiftDTO.setId(shiftStateId);
        shiftDTO = validateShiftStateAfterValidatingWtaRule(shiftDTO, validatedByStaff, actualPhases);
        shiftDTO.setEscalationReasons(shiftWithViolatedInfoDTO.getShifts().get(0).getEscalationReasons());
        shiftDTO.setRestingMinutes(shiftWithViolatedInfoDTO.getShifts().get(0).getRestingMinutes());
        shiftWithViolatedInfoDTO.setShifts(newArrayList(shiftDTO));
        return shiftWithViolatedInfoDTO;
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

    public boolean validateStaffDetailsAndShiftOverlapping(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO
            shiftDTO, ActivityWrapper activityWrapper, boolean byTandAPhase) {
        Activity activity = activityWrapper.getActivity();
        boolean shiftOverlappedWithNonWorkingType = false;
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
        DateTimeInterval shiftInterval = null;
        Date startDate = asDate(shiftDTO.getShiftDate());
        Date endDate = null;
        if (CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {

            endDate = asDate(shiftDTO.getShiftDate().plusDays(6).atTime(LocalTime.MAX));
            shiftInterval = new DateTimeInterval(startDate, endDate);
        } else if (CommonConstants.FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
            endDate = asDate(shiftDTO.getShiftDate().atTime(LocalTime.MAX));
            shiftInterval = new DateTimeInterval(startDate, endDate);
        } else {
            shiftInterval = new DateTimeInterval(shiftDTO.getStartDate(), shiftDTO.getEndDate());
            startDate = getStartOfDay(shiftDTO.getStartDate());
            endDate = getEndOfDay(shiftDTO.getEndDate());


        }
        //As discussed with Arvind we remove the Check of cross organization overlapping functionality
        List<ShiftWithActivityDTO> overlappedShifts = shiftMongoRepository.findOverlappedShiftsByEmploymentId(byTandAPhase ?
                        shiftDTO.getShiftId() : shiftDTO.getId(), staffAdditionalInfoDTO.getId(), startDate,
                endDate);
        if (!CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) && isShiftOverlap(overlappedShifts, shiftInterval) && WORKING_TYPE.name().equals(activityWrapper.getTimeType()) && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()) {
            shiftOverlappedWithNonWorkingType = true;
        }
        return shiftOverlappedWithNonWorkingType;
    }


    public boolean deleteDuplicateEntryOfShiftViolatedInfo() {
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAll();
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = new HashMap<>();
        List<ShiftViolatedRules> violatedRules = new ArrayList<>();
        for (ShiftViolatedRules shiftViolatedRules1 : shiftViolatedRules) {
            if (shiftViolatedRulesMap.containsKey(shiftViolatedRules1.getShiftId())) {
                violatedRules.add(shiftViolatedRules1);
            } else {
                shiftViolatedRulesMap.put(shiftViolatedRules1.getShiftId(), shiftViolatedRules1);
            }
        }
        logger.info("Duplicate remove entry count is " + violatedRules.size());
        shiftViolatedRulesMongoRepository.deleteAll(violatedRules);
        return true;
    }

    private void validateStaffingLevelForAbsenceTypeOfShift(StaffingLevel staffingLevel, ShiftActivity
            shiftActivity, boolean checkOverStaffing, List<ShiftActivity> shiftActivities,RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
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
            ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_SHIFT_OVERSTAFFING))));
        }
        else if(!checkOverStaffing && totalCount <= 0) {
            ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(new ActivityRuleViolation(shiftActivity.getActivityId(), shiftActivity.getActivityName(), 0, Arrays.asList(exceptionService.convertMessage(MESSAGE_SHIFT_UNDERSTAFFING))));
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
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(overLappedShifts.stream().map(Shift::getId).collect(Collectors.toList()), false);
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = shiftViolatedRules.stream().collect(Collectors.toMap(ShiftViolatedRules::getShiftId, Function.identity()));
        overLappedShifts.forEach(overLappedShift -> {

            if (!shiftOverLappedWithOther(overLappedShift) && shiftViolatedRulesMap.containsKey(overLappedShift.getId()) && isCollectionEmpty(shiftViolatedRulesMap.get(overLappedShift.getId()).getWorkTimeAgreements())) {
                if (getEsclationResolved(shift, shiftViolatedRulesMap, overLappedShift)) {
                    shiftDTO.getEscalationFreeShiftIds().add(overLappedShift.getId());
                    shiftViolatedRulesMap.get(overLappedShift.getId()).setEscalationResolved(true);
                }
            }
            getEsclationResolved(shift, shiftViolatedRulesMap, overLappedShift);
        });

        shiftViolatedRulesMongoRepository.saveAll(shiftViolatedRulesMap.values());
        return shiftDTO;
    }

    private boolean getEsclationResolved(Shift shift, Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap, Shift overLappedShift) {
        boolean isResolved = true;
        if (shift.getId().equals(overLappedShift.getId())) {
            if (shift.getBreakActivities().stream().anyMatch(ShiftActivity::isBreakNotHeld)) {
                shiftViolatedRulesMap.get(overLappedShift.getId()).setEscalationResolved(false);
                isResolved = false;
            } else {
                shiftViolatedRulesMap.get(overLappedShift.getId()).setEscalationResolved(true);
                isResolved = true;
            }

        }
        return isResolved;
    }

    public Set<BigInteger> getEscalationFreeShifts(List<BigInteger> shiftIds) {
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds, false);
        List<ShiftViolatedRules> escalationFreeViolatedRules = shiftViolatedRules.stream().filter(ShiftViolatedRules::isEscalationResolved).collect(Collectors.toList());
        return escalationFreeViolatedRules.stream().map(ShiftViolatedRules::getShiftId).collect(Collectors.toSet());
    }

    private boolean shiftOverLappedWithOther(Shift shift) {
        return shiftMongoRepository.shiftOverLapped(shift.getEmploymentId(), shift.getStartDate(), shift.getEndDate(), shift.getId());
    }

    public List<ShiftWithActivityDTO> updateFullDayAndFullWeekActivityShift(List<ShiftWithActivityDTO> shifts) {
        for (ShiftWithActivityDTO shift : shifts) {
            if (isNotNull(shift) && isNotNull(shift.getActivities().get(0).getActivity()) && shift.getActivities().get(0).getActivity().isFullDayOrFullWeekActivity()) {
                Date startDate = getStartOfDay(shift.getStartDate());
                Date endDate = getMidNightOfDay(shift.getEndDate());
                shift.getActivities().get(0).setStartDate(startDate);
                shift.getActivities().get(0).setEndDate(endDate);
                shift.setStartDate(startDate);
                shift.setEndDate(endDate);
            }
        }
        return shifts;
    }

    private boolean isShiftOverlap(List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval shiftInterval) {
        boolean shiftOverlap = false;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            DateTimeInterval existingShiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
            for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                if ((WORKING_TYPE.toString().equals(activity.getTimeType())) && shiftInterval.overlaps(existingShiftInterval)) {
                    exceptionService.invalidRequestException("message.shift.date.startandend", shiftWithActivityDTO.getStartDate(),
                            shiftWithActivityDTO.getEndDate());
                } else if (CommonConstants.FULL_WEEK.equals(activity.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY.equals(activity.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
                    existingShiftInterval = new DateTimeInterval(getStartOfDay(shiftWithActivityDTO.getStartDate()), getEndOfDay(shiftWithActivityDTO.getEndDate()));
                    if (shiftInterval.overlaps(existingShiftInterval)) {
                        exceptionService.invalidRequestException("message.shift.date.startandend", shiftWithActivityDTO.getStartDate(),
                                shiftWithActivityDTO.getEndDate());
                    }
                }
                if ((NON_WORKING_TYPE.toString().equals(activity.getTimeType()) && shiftInterval.overlaps(existingShiftInterval))) {
                    TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId());
                    if (isNotNull(timeType) && timeType.isAllowedConflicts()) {
                        shiftOverlap = true;
                    }
                }
            }
        }
        return shiftOverlap;
    }

    public List<ShiftViolatedRules> findAllViolatedRulesByShiftIds(List<BigInteger> shiftIds, boolean draft) {
        return shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds, draft);
    }

}