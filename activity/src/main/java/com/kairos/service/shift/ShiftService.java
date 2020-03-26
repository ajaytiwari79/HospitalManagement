package com.kairos.service.shift;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.attendance.AttendanceTimeSlotDTO;
import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.shift.*;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.SicknessSetting;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.ActivityPriorityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.todo.TodoService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.convertMessage;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.dto.user.access_permission.AccessGroupRole.MANAGEMENT;
import static com.kairos.enums.TimeTypeEnum.GAP;
import static com.kairos.enums.TimeTypeEnum.PLANNED_SICK_ON_FREE_DAYS;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValidDays;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;

/**
 * Created by vipul on 30/8/17.
 */
@Service
public class ShiftService extends MongoBaseService {
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private TimeAndAttendanceRepository timeAndAttendanceRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OpenShiftMongoRepository openShiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private StaffWTACounterRepository wtaCounterRepository;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftReminderService shiftReminderService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private ShiftStateService shiftStateService;
    @Inject
    private ShiftStatusService shiftStatusService;
    @Inject
    private AbsenceShiftService absenceShiftService;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
    @Inject
    private ShiftDetailsService shiftDetailsService;
    @Inject
    private TodoService todoService;
    @Inject
    private TodoRepository todoRepository;
    @Inject
    private ShiftFilterService shiftFilterService;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private ActivityPriorityMongoRepository activityPriorityMongoRepository;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private ShiftSickService shiftSickService;
    @Inject
    private ActivitySchedulerJobService activitySchedulerJobService;
    @Inject
    private ActivityService activityService;


    public List<ShiftWithViolatedInfoDTO> createShifts(Long unitId, List<ShiftDTO> shiftDTOS, ShiftActionType shiftActionType) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>(shiftDTOS.size());
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftWithViolatedInfoDTOS.addAll(createShift(unitId, shiftDTO, shiftActionType));
        }
        return shiftWithViolatedInfoDTOS;
    }

    public List<ShiftWithViolatedInfoDTO> createShift(Long unitId, ShiftDTO shiftDTO, ShiftActionType shiftActionType) {
        shiftDTO.setUnitId(unitId);
        Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(shiftDTO.getShiftDate(), shiftDTO.getStaffId(), shiftDTO.getEmploymentId(), reasonCodeIds);
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        shiftValidatorService.checkAbsenceTypeShift(shiftDTO, activityWrapper);
        updateCTADetailsOfEmployement(shiftDTO.getShiftDate(), staffAdditionalInfoDTO);
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS;
        if (activityWrapper.getActivity().getRulesActivityTab().isSicknessSettingValid()) {
            shiftWithViolatedInfoDTOS = shiftSickService.createSicknessShiftsOfStaff(shiftDTO, staffAdditionalInfoDTO, activityWrapper);
        } else {
            shiftWithViolatedInfoDTOS = validateAndCreateShift(shiftDTO, shiftActionType, staffAdditionalInfoDTO, activityWrapper);
        }
        return shiftWithViolatedInfoDTOS;
    }

    private List<ShiftWithViolatedInfoDTO> validateAndCreateShift(ShiftDTO shiftDTO, ShiftActionType shiftActionType, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ActivityWrapper activityWrapper) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        if ((CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            shiftDTO.setStartDate(asDate(shiftDTO.getShiftDate()));
            boolean shiftOverlappedWithNonWorkingType = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapper, false);
            shiftWithViolatedInfoDTOS = absenceShiftService.createAbsenceTypeShift(activityWrapper, shiftDTO, staffAdditionalInfoDTO, shiftOverlappedWithNonWorkingType, shiftActionType);
        } else {
            boolean shiftOverlappedWithNonWorkingType = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapper, false);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(), null);
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = saveShift(staffAdditionalInfoDTO, shiftDTO, phase, shiftOverlappedWithNonWorkingType, shiftActionType);
            addReasonCode(shiftWithViolatedInfoDTO.getShifts(), staffAdditionalInfoDTO.getReasonCodes());
            shiftWithViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);
        }
        return shiftWithViolatedInfoDTOS;
    }

    public void addReasonCode(List<ShiftDTO> shiftDTOS, List<ReasonCodeDTO> reasonCodes) {
        Map<Long, ReasonCodeDTO> reasonCodeDTOMap = reasonCodes.stream().collect(Collectors.toMap(ReasonCodeDTO::getId, v -> v));
        for (ShiftDTO shift : shiftDTOS) {
            Set<BigInteger> multipleActivityCount = new HashSet<>();
            for (ShiftActivityDTO activity : shift.getActivities()) {
                activity.setReasonCode(reasonCodeDTOMap.get(activity.getAbsenceReasonCodeId()));
                if (!activity.isBreakShift()) {
                    multipleActivityCount.add(activity.getActivityId());
                }
            }
            shift.setMultipleActivity(multipleActivityCount.size() > CommonConstants.MULTIPLE_ACTIVITY);
        }
    }

    public ShiftWithViolatedInfoDTO saveShift(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO, Phase phase, boolean shiftOverlappedWithNonWorkingType, ShiftActionType shiftActionType) {
        Long functionId = shiftDTO.getFunctionId();
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shiftDTO.getUnitId(), DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()));
        if (isNull(planningPeriod)) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIODSETTING_NOTFOUND);
        }
        Shift mainShift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shiftDTO.getActivities().get(0).getStartDate()));
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
        }
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityService.getActivityWrapperMap(newArrayList(), shiftDTO);
        mainShift.setPlanningPeriodId(planningPeriod.getId());
        mainShift.setPhaseId(phase.getId());

        List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(shiftDTO.getId() != null, mainShift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets(), mainShift);
        mainShift.setBreakActivities(breakActivities);


        activityConfigurationService.addPlannedTimeInShift(mainShift, activityWrapperMap, staffAdditionalInfoDTO, false);
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(mainShift, ShiftDTO.class);
        shiftDTO.setShiftType(updateShiftType(activityWrapperMap,mainShift));
        ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap, null);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false);
        if ((PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) || shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            mainShift = saveShiftWithActivity(activityWrapperMap, mainShift, staffAdditionalInfoDTO, false, functionId, phase, shiftActionType);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(isNotNull(mainShift.getDraftShift()) ? mainShift.getDraftShift() : mainShift, ShiftDTO.class);
            shiftDTO.setId(mainShift.getId());
            shiftValidatorService.validateShiftViolatedRules(mainShift, shiftOverlappedWithNonWorkingType, shiftWithViolatedInfoDTO, PhaseDefaultName.DRAFT.equals(phase.getPhaseEnum()) ? ShiftActionType.SAVE_AS_DRAFT : null);
            shiftDTO = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(Arrays.asList(shiftDTO)).get(0);
        }
        shiftWithViolatedInfoDTO.setShifts(newArrayList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }

    public Shift saveShiftWithActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean updateShift, Long functionId, Phase phase, ShiftActionType shiftAction) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(shift.getPlanningPeriodId());
        updateScheduledAndDurationHours(activityWrapperMap, shift, staffAdditionalInfoDTO);
        if(!ShiftActionType.SAVE_AS_DRAFT.equals(shiftAction)) {
            shiftStatusService.updateStatusOfShiftIfPhaseValid(planningPeriod, phase, shift, activityWrapperMap, staffAdditionalInfoDTO);
        }
        //As discuss with Arvind Presence and Absence type of activity cann't be perform in a Shift
        shift.setShiftType(updateShiftType(activityWrapperMap, shift));
        updateAppliedFunctionDetail(activityWrapperMap, shift, functionId);
        if (shift.isSickShift() && updateShift && isNotNull(shiftAction) && !shift.getActivities().stream().anyMatch(shiftActivity -> !shiftActivity.getStatus().contains(ShiftStatus.PUBLISH)) && newHashSet(PhaseDefaultName.CONSTRUCTION, PhaseDefaultName.DRAFT, PhaseDefaultName.TENTATIVE).contains(phase.getPhaseEnum())) {
            shift = updateShiftAfterPublish(shift, shiftAction);
        }
        if (shift.isSickShift() && isValidForDraftShiftFunctionality(staffAdditionalInfoDTO, updateShift, phase, shiftAction, planningPeriod)) {
            Shift draftShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
            draftShift.setShiftType(updateShiftType(activityWrapperMap, draftShift));
            draftShift.setDraft(true);
            shift.setDraftShift(draftShift.getDraftShift());
            shift.setDraft(true);
        }
        shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
        shift.setId(isNull(shift.getId()) ? shiftMongoRepository.nextSequence(Shift.class.getSimpleName()) : shift.getId());
        todoService.updateStatusOfShiftActivityIfApprovalRequired(activityWrapperMap, shift, updateShift);
//        updateStatusForShift(shift,shiftAction,updateShift,activityWrapperMap,staffAdditionalInfoDTO,planningPeriod);
        payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
        timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, false);
        shiftMongoRepository.save(shift);
        shiftStateService.createShiftStateByPhase(Arrays.asList(shift), phase);
        return shift;
    }

    public ActivityRuleViolation validateAndUpdateSicknessShift(Map<BigInteger, ActivityWrapper> activityWrapperMap, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ActivityWrapper activityWrapper = activityWrapperMap.get(shift.getActivities().get(0).getActivityId());
        List<String> errorMessages = new ArrayList<>();
        if (isNotNull(activityWrapper.getActivity().getRulesActivityTab().getSicknessSetting()) && !activityWrapper.getActivity().getRulesActivityTab().isAllowedAutoAbsence()) {
            List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), getStartOfDay(shift.getStartDate()), DateUtils.getEndOfDay(shift.getEndDate()));
            List<Activity> protectedDaysOffActivities = activityRepository.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.PROTECTED_DAYS_OFF, newHashSet(shift.getUnitId()));
            validateSicknessShift(shift, staffAdditionalInfoDTO, activityWrapper, shifts, protectedDaysOffActivities);
            if (isCollectionEmpty(errorMessages)) {
                for (Shift oldShift : shifts) {
                    oldShift.setDisabled(true);
                }
                shiftMongoRepository.saveEntities(shifts);
                shift.setSickShift(true);
            }
        }
        return new ActivityRuleViolation(activityWrapper.getActivity().getId(), activityWrapper.getActivity().getName(), 0, new HashSet<>(errorMessages));
    }

    public void validateSicknessShift(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ActivityWrapper activityWrapper, List<Shift> shifts, List<Activity> activities) {
        List<String> errorMessages = new ArrayList<>();
        SicknessSetting sicknessSetting = activityWrapper.getActivity().getRulesActivityTab().getSicknessSetting();
        validateSickSettings(staffAdditionalInfoDTO, activityWrapper, shifts, errorMessages, sicknessSetting);
        if (sicknessSetting.isUsedOnProtecedDaysOff()) {
            Set<BigInteger> activityIds = activities.stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
            for (Shift oldShift : shifts) {
                if (!activityIds.contains(oldShift.getActivities().stream().map(ShiftActivity::getActivityId))) {
                    errorMessages.add(exceptionService.convertMessage(MESSAGE_ACTIVITY_USEDON_PROTECTEDDAYSOFF));
                }
            }
        }
        if (!sicknessSetting.isCanNotUsedTopOfApprovedAbsences()) {
            for (Shift currentElement : shifts) {
                if (ShiftType.ABSENCE.equals(currentElement.getShiftType()) && !shift.getActivities().stream().anyMatch(shiftActivity -> shiftActivity.getStatus().contains(ShiftStatus.APPROVE))) {
                    errorMessages.add(exceptionService.convertMessage(MESSAGE_ACTIVITY_USEDON_APPROVEABSENCES));
                }
            }
        }
    }

    private void validateSickSettings(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ActivityWrapper activityWrapper, List<Shift> shifts, List<String> errorMessages, SicknessSetting sicknessSetting) {
        if (!(sicknessSetting.isCanOnlyUsedOnMainEmployment() && EmploymentSubType.MAIN.equals(staffAdditionalInfoDTO.getEmployment().getEmploymentSubType()))) {
            errorMessages.add(exceptionService.convertMessage(MESSAGE_STAFF_MAIN_EMPLOYMENT_NOT_FOUND));
        }
        if (isCollectionNotEmpty(activityWrapper.getActivity().getRulesActivityTab().getStaffTagIds())) {
            Set<BigInteger> tadIds = staffAdditionalInfoDTO.getTags().stream().map(TagDTO::getId).collect(Collectors.toSet());
            if (CollectionUtils.containsAny(tadIds, activityWrapper.getActivity().getRulesActivityTab().getStaffTagIds())) {
                errorMessages.add(exceptionService.convertMessage(STAFF_NOT_ALLOWED_ON_TAG));
            }

        }
        if (sicknessSetting.isValidForChildCare() && isCollectionEmpty(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays())) {
            errorMessages.add(exceptionService.convertMessage(MESSAGE_STAFF_CARE_DAYS_NOT_FOUND));
        }
        if (sicknessSetting.isUsedOnFreeDays() && isCollectionNotEmpty(shifts)) {
            errorMessages.add(exceptionService.convertMessage(MESSAGE_ACTIVITY_USEDON_FREEDAY));
        }
    }

    private void updateScheduledAndDurationHours(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            int[] scheduledAndDurationMinutes = updateActivityDetailsInShiftActivity(shiftActivity, activityWrapperMap, staffAdditionalInfoDTO);
            scheduledMinutes += scheduledAndDurationMinutes[0];
            durationMinutes += scheduledAndDurationMinutes[1];
            for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                updateActivityDetailsInShiftActivity(childActivity, activityWrapperMap, staffAdditionalInfoDTO);
            }
        }
        if (isCollectionNotEmpty(shift.getBreakActivities()) && activityWrapperMap.containsKey(shift.getBreakActivities().get(0).getActivityId()) && UNPAID_BREAK.equals(activityWrapperMap.get(shift.getBreakActivities().get(0).getActivityId()).getTimeType())) {
            scheduledMinutes -= shift.getBreakActivities().get(0).getInterval().getMinutes();
        }
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
    }

    private void updateAppliedFunctionDetail(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, Long functionId) {
        if (isNotNull(functionId)) {
            if (activityWrapperMap.values().stream().anyMatch(k -> TimeTypeEnum.PRESENCE.equals(k.getActivity().getBalanceSettingsActivityTab().getTimeType()))) {
                Map<LocalDate, Long> dateAndFunctionIdMap = new HashMap<>();
                dateAndFunctionIdMap.put(asLocalDate(shift.getStartDate()), functionId);
                userIntegrationService.applyFunction(shift.getUnitId(), shift.getEmploymentId(), dateAndFunctionIdMap, HttpMethod.POST, null);
            } else {
                exceptionService.actionNotPermittedException(ERROR_FUNCTION_CAN_NOT_APPLY_WITH_ABSENCE_ACTIVITY);
            }
        }
        //TODO pavan please verify it is required or not as I discussed with sakshi we can't remove function at the time of shift update
        /* else {
            BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", asLocalDate(shift.getStartDate()).toString());
            userIntegrationService.applyFunction(shift.getUnitId(), shift.getEmploymentId(), null, HttpMethod.DELETE, Arrays.asList(appliedDate));
        }*/
    }

    private boolean isValidForDraftShiftFunctionality(StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean updateShift, Phase phase, ShiftActionType shiftAction, PlanningPeriod planningPeriod) {
        boolean isValidActionType = (PhaseDefaultName.CONSTRUCTION.equals(phase.getPhaseEnum()) || !updateShift) && ShiftActionType.SAVE_AS_DRAFT.equals(shiftAction);
        boolean isValidPhase = (PhaseDefaultName.CONSTRUCTION.equals(phase.getPhaseEnum()) || (planningPeriod.getPublishEmploymentIds().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId())
                && newHashSet(PhaseDefaultName.DRAFT, PhaseDefaultName.TENTATIVE).contains(phase.getPhaseEnum())));
        return isValidActionType && isValidPhase;
    }

    public ShiftType updateShiftType(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        ShiftType shiftType = null;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
            TimeTypeEnum timeTypeEnum = activity.getBalanceSettingsActivityTab().getTimeType();
            if (PLANNED_SICK_ON_FREE_DAYS.equals(timeTypeEnum)) {
                shiftType = ShiftType.SICK;
            } else if (TimeTypeEnum.PRESENCE.equals(timeTypeEnum)) {
                shiftType = ShiftType.PRESENCE;
                break;
            } else if (TimeTypeEnum.ABSENCE.equals(timeTypeEnum)) {
                if (activity.getRulesActivityTab().isSicknessSettingValid()) {
                    shiftType = ShiftType.SICK;
                } else {
                    shiftType = ShiftType.ABSENCE;
                }
            } else if (isNull(shiftType)) {
                shiftType = ShiftType.NON_WORKING;
            }
        }
        return shiftType;
    }

    public int[] updateActivityDetailsInShiftActivity(ShiftActivity shiftActivity, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        if (shiftActivity.getId() == null) {
            shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
        }
        ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
        shiftActivity.setTimeType(activityWrapper.getTimeType());
        if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
            Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
            Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
            if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getEmployment(), false);
                scheduledMinutes = shiftActivity.getScheduledMinutes();
                durationMinutes = shiftActivity.getDurationMinutes();
            }
        }
        shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
        shiftActivity.setActivityName(activityWrapper.getActivity().getName());
        return new int[]{scheduledMinutes, durationMinutes};
    }

    public void saveShiftWithActivity(Map<Date, Phase> phaseListByDate, List<Shift> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityService.getActivityWrapperMap(shifts, null);
        for (Shift shift : shifts) {
            List<ShiftActivity>[] shiftActivities = shift.getShiftActivitiesForValidatingStaffingLevel(null);
            for (ShiftActivity shiftActivity : shiftActivities[1]) {
                shiftValidatorService.validateStaffingLevel(phaseListByDate.get(shift.getStartDate()), shift, activityWrapperMap, true, shiftActivity, null, new StaffingLevelHelper());
            }
            int scheduledMinutes = 0;
            int durationMinutes = 0;
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                int[] scheduledAndDurationMinutes = updateActivityDetailsInShiftActivity(shiftActivity, activityWrapperMap, staffAdditionalInfoDTO);
                scheduledMinutes += scheduledAndDurationMinutes[0];
                durationMinutes += scheduledAndDurationMinutes[1];
                for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                    updateActivityDetailsInShiftActivity(childActivity, activityWrapperMap, staffAdditionalInfoDTO);
                }
            }
            shift.setPhaseId(phaseListByDate.get(shift.getActivities().get(0).getStartDate()).getId());
            shift.setScheduledMinutes(scheduledMinutes);
            shift.setDurationMinutes(durationMinutes);
            shift.setStartDate(shift.getActivities().get(0).getStartDate());
            shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
            activityConfigurationService.addPlannedTimeInShift(shift, activityWrapperMap, staffAdditionalInfoDTO, false);
        }
        shifts.forEach(shift -> {
            timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, false);
            payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
        });
        shiftMongoRepository.saveEntities(shifts);
        todoService.createOrUpdateTodo(shifts.get(0), TodoType.APPROVAL_REQUIRED);
    }

    public List<ShiftWithViolatedInfoDTO> deleteShiftsAfterValidation(List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS1 = new ArrayList<>();
        for (ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO : shiftWithViolatedInfoDTOS) {
            shiftWithViolatedInfoDTOS1.add(deleteShiftAfterValidation(shiftWithViolatedInfoDTO));
        }
        return shiftWithViolatedInfoDTOS1;
    }

    public ShiftWithViolatedInfoDTO deleteShiftAfterValidation(ShiftWithViolatedInfoDTO shiftWithViolatedInfo) {
        List<ShiftDTO> responseShiftDTOS = new ArrayList<>();
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftWithViolatedInfo.getShifts().stream().map(s -> s.getId()).collect(Collectors.toList()));
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shifts.get(0).getActivities().get(0).getStartDate()), shifts.get(0).getStaffId(), shifts.get(0).getEmploymentId(), Collections.emptySet());
        boolean updateWTACounterFlag = true;
        for (Shift shift : shifts) {
            if (updateWTACounterFlag) {
                shiftValidatorService.updateWTACounter(staffAdditionalInfoDTO, shiftWithViolatedInfo, shift);
                updateWTACounterFlag = false;
            }
            shift.setDeleted(true);
            responseShiftDTOS.add(deleteShift(shift, staffAdditionalInfoDTO));
        }
        shiftWithViolatedInfo.setShifts(responseShiftDTOS);
        return shiftWithViolatedInfo;
    }

    public List<ShiftWithViolatedInfoDTO> saveShiftsAfterValidation(List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfos, Boolean validatedByStaff, boolean updateShiftState, Long unitId, ShiftActionType shiftActionType, TodoType todoType) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        for (ShiftWithViolatedInfoDTO shiftWithViolatedInfo : shiftWithViolatedInfos) {
            shiftWithViolatedInfoDTOS.add(saveShiftAfterValidation(shiftWithViolatedInfo, validatedByStaff, updateShiftState, unitId, shiftActionType, todoType));
        }
        return shiftWithViolatedInfoDTOS;
    }

    public ShiftWithViolatedInfoDTO saveShiftAfterValidation(ShiftWithViolatedInfoDTO shiftWithViolatedInfo, Boolean validatedByStaff, boolean updateShiftState, Long unitId, ShiftActionType shiftActionType, TodoType todoType) {
        boolean updateWTACounterFlag = true;
        List<ShiftDTO> responseShiftDTOS = new ArrayList<>();
        for (ShiftDTO shiftDTO : shiftWithViolatedInfo.getShifts()) {
            Long functionId = shiftDTO.getFunctionId();
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
            Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getActivities().get(0).getStartDate());
            //reason code will be sanem for all shifts.

            Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftStartDate), shiftDTO.getStaffId(), shift.getEmploymentId(), reasonCodeIds);
            Shift oldShift = null;
            if (isNotNull(shift.getId())) {
                oldShift = shiftMongoRepository.findOne(shift.getId());
            }
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activityService.getActivityWrapperMap(isNotNull(oldShift) ? newArrayList(oldShift) : newArrayList(), shiftDTO);
            updateCTADetailsOfEmployement(shiftDTO.getShiftDate(), staffAdditionalInfoDTO);
            boolean shiftOverLappedWithNonWorkingTime = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapperMap.get(shift.getActivities().get(0).getActivityId()), false);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
            shift.setPhaseId(phase.getId());
            WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shiftWithViolatedInfo.getShifts().get(0).getActivities().get(0).getStartDate()));
            if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
            }
            // replace id from shift id if request come form detailed view and compact view
            if (isNotNull(shiftDTO.getShiftId())) {
                shift.setId(shiftDTO.getShiftId());
            }
            ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap, null);
            PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shiftDTO.getUnitId(), shiftDTO.getShiftDate());
            List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(shift.isShiftUpdated(isNotNull(oldShift) ? oldShift : shift), shift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets(), isNotNull(oldShift) ? oldShift : shift);
            shift.setBreakActivities(breakActivities);

            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfo = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, oldShift, activityWrapperMap, isNotNull(shiftWithActivityDTO.getId()), isNull(shiftDTO.getShiftId()));
            List<ShiftDTO> shiftDTOS = newArrayList(shiftDTO);
            if (isIgnoredAllRuletemplate(shiftWithViolatedInfo, updatedShiftWithViolatedInfo)) {
                if (updateWTACounterFlag && !ShiftActionType.SAVE_AS_DRAFT.equals(shiftActionType)) {
                    shiftValidatorService.updateWTACounter(staffAdditionalInfoDTO, updatedShiftWithViolatedInfo, shift);
                    updateWTACounterFlag = false;
                }
                shift.setPlanningPeriodId(planningPeriod.getId());
                shift = saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, isNotNull(shift.getId()), functionId, phase, shiftActionType);
                if (isNotNull(todoType)) {
                    Todo todo = todoRepository.findByEntityIdAndType(shift.getId(), TodoType.REQUEST_ABSENCE);
                    todo.setStatus(TodoStatus.APPROVE);
                    todo.setApprovedOn(getDate());
                    todoRepository.save(todo);
                }
                //todoService.createOrUpdateTodo(shift, TodoType.APPROVAL_REQUIRED, isNotNull(shiftDTO.getId()),staffAdditionalInfoDTO);
                shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
                updateShiftViolatedOnIgnoreCounter(shift, shiftOverLappedWithNonWorkingTime, updatedShiftWithViolatedInfo);
                activitySchedulerJobService.updateJobForShiftReminder(activityWrapperMap, shift);
                if (updateShiftState) {
                    shiftDTO = shiftStateService.updateShiftStateAfterValidatingWtaRule(shiftDTO, shiftDTO.getId(), shiftDTO.getShiftStatePhaseId());
                } else if (isNotNull(validatedByStaff)) {
                    Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
                    shiftDTO = shiftValidatorService.validateShiftStateAfterValidatingWtaRule(shiftDTO, validatedByStaff, actualPhases);
                }
                shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(newArrayList(shiftDTO));
            } else {
                shiftWithViolatedInfo = updatedShiftWithViolatedInfo;
            }
            shiftDTO = shiftDTOS.get(0);
            if (isNotNull(shiftDTO.getDraftShift())) {
                shiftDTO.getDraftShift().setId(shift.getId());
            }
            responseShiftDTOS.addAll(Arrays.asList(isNotNull(shiftDTO.getDraftShift()) ? shiftDTO.getDraftShift() : shiftDTO));
        }
        shiftWithViolatedInfo.setShifts(responseShiftDTOS);
        return shiftWithViolatedInfo;
    }

    private void updateShiftViolatedOnIgnoreCounter(Shift shift, boolean shiftOverLappedWithNonWorkingTime, ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfo) {
        ShiftViolatedRules shiftViolatedRules = shiftViolatedRulesMongoRepository.findOneViolatedRulesByShiftId(shift.getId(), isNotNull(shift.getDraftShift()));
        if (isNull(shiftViolatedRules)) {
            shiftViolatedRules = new ShiftViolatedRules(shift.getId());
            shiftViolatedRules.setDraft(isNotNull(shift.getDraftShift()));
        }
        shiftViolatedRules.setEscalationReasons(shiftOverLappedWithNonWorkingTime ? newHashSet(ShiftEscalationReason.SHIFT_OVERLAPPING, ShiftEscalationReason.WORK_TIME_AGREEMENT) : newHashSet(ShiftEscalationReason.WORK_TIME_AGREEMENT));
        shiftViolatedRules.setEscalationResolved(false);
        shiftViolatedRules.setActivities(updatedShiftWithViolatedInfo.getViolatedRules().getActivities());
        shiftViolatedRules.setWorkTimeAgreements(updatedShiftWithViolatedInfo.getViolatedRules().getWorkTimeAgreements());
        shiftViolatedRules.setEscalationCausedBy(UserContext.getUserDetails().isManagement() ? MANAGEMENT : AccessGroupRole.STAFF);
        shiftViolatedRulesMongoRepository.save(shiftViolatedRules);
    }

    public Map<String, Object> saveAndCancelDraftShift(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, ViewType viewType, ShiftFilterParam shiftFilterParam, ShiftActionType shiftActionType, StaffFilterDTO staffFilterDTO) {
        List<PlanningPeriod> planningPeriods = new ArrayList<>();
        if (isNotNull(staffFilterDTO.getPlanningPeriodIds())) {
            planningPeriods = planningPeriodMongoRepository.findAllByUnitIdAndIds(unitId, staffFilterDTO.getPlanningPeriodIds());
            planningPeriods.sort(Comparator.comparing(PlanningPeriod::getStartDate));
        }
        List<Shift> draftShifts = getDraftShifts(unitId, staffId, staffFilterDTO.getPlanningPeriodIds(), startDate, endDate, employmentId);
        List<Shift> saveShifts;
        List<BigInteger> deletedShiftIds = new ArrayList<>();
        if (ShiftActionType.SAVE.equals(shiftActionType)) {
            saveShifts = updateShiftAndShiftViolatedRules(unitId, draftShifts);
            shiftStateService.createShiftState(saveShifts, true, unitId);
        } else {
            List[] saveShiftsDeletedShiftIds = deleteDraftShiftAndViolatedRules(draftShifts);
            deletedShiftIds = saveShiftsDeletedShiftIds[1];
        }
        Map<String, Object> response = new HashMap<>();
        response.put("shiftDetails", getAllShiftAndStates(unitId, staffId, isNull(startDate) ? planningPeriods.get(0).getStartDate() : startDate, isNull(endDate) ? planningPeriods.get(planningPeriods.size() - 1).getEndDate() : endDate, employmentId, viewType, shiftFilterParam, null, staffFilterDTO));
        response.put("deletedShiftIds", deletedShiftIds);
        return response;
    }

    private List<Shift> getDraftShifts(Long unitId, Long staffId, List<BigInteger> planningPeriodIds, LocalDate startDate, LocalDate endDate, Long employmentId) {
        List<Shift> draftShifts;
        if (isNotNull(staffId) && isNotNull(employmentId) && isCollectionNotEmpty(planningPeriodIds)) {
            draftShifts = shiftMongoRepository.getAllDraftShiftBetweenDuration(employmentId, staffId, planningPeriodIds, unitId);
        } else {
            draftShifts = shiftMongoRepository.findDraftShiftBetweenDurationAndUnitIdAndDeletedFalse(asDate(startDate), getEndOfDay(asDate(endDate)), unitId);
        }
        if (isCollectionEmpty(draftShifts)) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_DRAFT_NOTFOUND);
        }
        return draftShifts;
    }

    public List[] deleteDraftShiftAndViolatedRules(List<Shift> draftShifts) {
        List<Shift> saveShifts = new ArrayList<>();
        List<Shift> deleteShift = new ArrayList<>();
        List<BigInteger> deletedShiftIds = new ArrayList<>();
        for (Shift draftShift : draftShifts) {
            if (draftShift.isDraft()) {
                deleteShift.add(draftShift);
                deletedShiftIds.add(draftShift.getId());
            } else {
                draftShift.setDraftShift(null);
                saveShifts.add(draftShift);
            }
        }
        if (isCollectionNotEmpty(saveShifts)) {
            shiftMongoRepository.saveEntities(saveShifts);
        }
        if (isCollectionNotEmpty(deleteShift)) {
            shiftMongoRepository.deleteAll(deleteShift);
        }
        shiftViolatedRulesMongoRepository.deleteAllViolatedRulesByShiftIds(draftShifts.stream().map(MongoBaseEntity::getId).collect(Collectors.toList()), true);
        return new List[]{saveShifts, deletedShiftIds};
    }

    private List<Shift> updateShiftAndShiftViolatedRules(Long unitId, List<Shift> draftShifts) {
        List<Shift> saveShifts = new ArrayList<>();
        List<ShiftViolatedRules> saveShiftViolatedRules = new ArrayList<>();
        List<ShiftViolatedRules> deleteShiftViolatedRules = new ArrayList<>();
        for (Shift draftShift : draftShifts) {
            Shift shift = draftShift.getDraftShift();
            shift.setDraftShift(null);
            shift.setId(draftShift.getId());
            shift.setDraft(false);
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                shiftActivity.getStatus().add(ShiftStatus.PUBLISH);
            }
            saveShifts.add(shift);
        }
        shiftStateService.updateShiftDailyTimeBankAndPaidOut(saveShifts, saveShifts, unitId);
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(draftShifts.stream().map(MongoBaseEntity::getId).collect(Collectors.toList()), true);
        Map<BigInteger, ShiftViolatedRules> draftShiftViolatedRules = shiftViolatedRules.stream().filter(ShiftViolatedRules::isDraft).collect(Collectors.toMap(ShiftViolatedRules::getShiftId, Function.identity()));
        for (ShiftViolatedRules shiftViolatedRule : shiftViolatedRules) {
            if (isNotNull(draftShiftViolatedRules.get(shiftViolatedRule.getShiftId()))) {
                ShiftViolatedRules violatedRules = draftShiftViolatedRules.get(shiftViolatedRule.getShiftId());
                violatedRules.setEscalationCausedBy(UserContext.getUserDetails().isManagement() ? MANAGEMENT : AccessGroupRole.STAFF);
                saveShiftViolatedRules.add(violatedRules);
                deleteShiftViolatedRules.add(shiftViolatedRule);
            }
        }
        if (isCollectionNotEmpty(saveShifts)) {
            shiftMongoRepository.saveEntities(saveShifts);
        }
        shiftViolatedRulesMongoRepository.saveEntities(saveShiftViolatedRules);
        shiftViolatedRulesMongoRepository.deleteAll(deleteShiftViolatedRules);
        return saveShifts;
    }

    public List<ShiftWithViolatedInfoDTO> updateShifts(List<ShiftDTO> shiftDTOS, boolean byTAndAView, boolean validatedByPlanner, ShiftActionType shiftAction) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>(shiftDTOS.size());
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftWithViolatedInfoDTOS.addAll(updateShift(shiftDTO, byTAndAView, validatedByPlanner, shiftAction));
        }
        return shiftWithViolatedInfoDTOS;
    }

    public List<ShiftWithViolatedInfoDTO> updateShift(ShiftDTO shiftDTO, boolean byTAndAView, boolean validatedByPlanner, ShiftActionType shiftAction) {
        Long functionId = shiftDTO.getFunctionId();
        Shift shift = shiftMongoRepository.findByIdAndDeletedFalse(byTAndAView ? shiftDTO.getShiftId() : shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFT_ID, shiftDTO.getId());
        }
        if (shift.isDraft() && ShiftActionType.CANCEL.equals(shiftAction)) {
            shiftMongoRepository.delete(shift);
            return new ArrayList<>();
        }

        boolean ruleCheckRequired = shift.isShiftUpdated(ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class));
        Date currentShiftStartDate = shift.getStartDate();
        Date currentShiftEndDate = shift.getEndDate();
        Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(shiftDTO.getShiftDate(), shiftDTO.getStaffId(), shiftDTO.getEmploymentId(), reasonCodeIds);
        if (UserContext.getUserDetails().isManagement() && isNotNull(shift.getDraftShift()) && !byTAndAView) {
            shift = shift.getDraftShift();
        }
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityService.getActivityWrapperMap(newArrayList(shift), shiftDTO);
        boolean shiftOverlappedWithNonWorkingType = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapperMap.get(shiftDTO.getActivities().get(0).getActivityId()), byTAndAView);
        updateCTADetailsOfEmployement(shiftDTO.getShiftDate(), staffAdditionalInfoDTO);
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        ActivityWrapper absenceActivityWrapper = getAbsenceTypeOfActivityIfPresent(shiftDTO.getActivities(), activityWrapperMap);
        boolean updatingSameActivity = true;
        if (isNotNull(absenceActivityWrapper)) {
            updatingSameActivity = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getActivityId().equals(absenceActivityWrapper.getActivity().getId())).findAny().isPresent();
        }
        if (!updatingSameActivity) {
            shiftWithViolatedInfoDTOS = absenceShiftService.createAbsenceTypeShift(absenceActivityWrapper, shiftDTO, staffAdditionalInfoDTO, shiftOverlappedWithNonWorkingType, shiftAction);
        }else {
            if (isNull(staffAdditionalInfoDTO.getUnitId())) {
                exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNIT, shiftDTO.getStaffId(), shiftDTO.getUnitId());
            }
            WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shiftDTO.getActivities().get(0).getStartDate());
            if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
            }
            //copy old state of activity object
            Shift oldStateOfShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
            if (!byTAndAView) {
                shiftValidatorService.updateStatusOfShiftActvity(oldStateOfShift, shiftDTO);

            }
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());

            shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
            shift.setShiftType(updateShiftType(activityWrapperMap, shift));
            shift.setPhaseId(phase.getId());
            if (byTAndAView) {
                shift.setId(shiftDTO.getShiftId());
            }
            shift.setDraftShift(oldStateOfShift.getDraftShift());
            shift.setPlanningPeriodId(oldStateOfShift.getPlanningPeriodId());
            List<ShiftActivity> breakActivities = new ArrayList<>();
            List<Shift> shiftList = getShiftsOnTheBasisOfGapActivity(shift, activityWrapperMap);
            for (Shift currentShift : shiftList) {
                List<ShiftActivity> breakActivityList = shiftBreakService.updateBreakInShift(shift.isShiftUpdated(oldStateOfShift), currentShift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets(), oldStateOfShift);
                breakActivities.addAll(breakActivityList);
            }
            shift.setBreakActivities(breakActivities);

            activityConfigurationService.addPlannedTimeInShift(shift, activityWrapperMap, staffAdditionalInfoDTO, !oldStateOfShift.getShiftType().equals(shift.getShiftType()));
            ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(null, activityWrapperMap, shift);
            boolean valid = isNotNull(shiftAction) && !shiftAction.equals(ShiftActionType.CANCEL) && shift.getActivities().stream().anyMatch(activity -> !activity.getStatus().contains(ShiftStatus.PUBLISH)) && UserContext.getUserDetails().isManagement();

            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = validateRuleCheck(shift, ruleCheckRequired, staffAdditionalInfoDTO, activityWrapperMap, phase, valid, wtaQueryResultDTO, shiftWithActivityDTO, oldStateOfShift);
            List<ShiftDTO> shiftDTOS = newArrayList(shiftDTO);
            if (PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) || (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty())) {
                shift = saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, true, functionId, phase, shiftAction);
                wtaRuleTemplateCalculationService.updateWTACounter(shift, staffAdditionalInfoDTO);
                shiftDTO = UserContext.getUserDetails().isManagement() ? ObjectMapperUtils.copyPropertiesByMapper(isNotNull(shift.getDraftShift()) ? shift.getDraftShift() : shift, ShiftDTO.class) : ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
                timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner);
                if (ShiftActionType.SAVE.equals(shiftAction)) {
                    shiftStateService.createShiftState(Arrays.asList(shift), true, shift.getUnitId());
                }
                // TODO VIPUL WE WILL UNCOMMENTS AFTER FIX mailing servive
                //shiftReminderService.updateReminderTrigger(activityWrapperMap,shift);
                if (ruleCheckRequired) {
                    activityConfigurationService.addPlannedTimeInShift(shift, activityWrapperMap, staffAdditionalInfoDTO, !oldStateOfShift.getShiftType().equals(shift.getShiftType()));
                    shiftValidatorService.validateShiftViolatedRules(shift, shiftOverlappedWithNonWorkingType, shiftWithViolatedInfoDTO, shiftAction);
                }
                shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(newArrayList(shiftDTO));
            }
            shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
            shiftWithViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);

        }
        addReasonCode(shiftWithViolatedInfoDTOS.stream().flatMap(shiftWithViolatedInfoDTO -> shiftWithViolatedInfoDTO.getShifts().stream()).collect(Collectors.toList()), staffAdditionalInfoDTO.getReasonCodes());
        if (!shiftDTO.isDraft()) {
            if (byTAndAView) {
                shiftDTO.setId(shiftDTO.getShiftId());
            }
            shiftValidatorService.escalationCorrectionInShift(shiftDTO, currentShiftStartDate, currentShiftEndDate, shift);
        }
        return shiftWithViolatedInfoDTOS;
    }


    private ShiftWithViolatedInfoDTO validateRuleCheck(Shift shift, boolean ruleCheckRequired, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, boolean valid, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shiftWithActivityDTO, Shift oldStateShift) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;
        if (ruleCheckRequired) {
            if (!valid && oldStateShift.getShiftType().equals(shift.getShiftType())) {
                validateStaffingLevel(shift, activityWrapperMap, phase, oldStateShift);
            }
            shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, oldStateShift, activityWrapperMap, true, false);
        }
        if (isNull(shiftWithViolatedInfoDTO)) {
            shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO(new ViolatedRulesDTO());
        }
        return shiftWithViolatedInfoDTO;
    }

    private List<ShiftWithViolatedInfoDTO> updateAbsenceTypeShift(ShiftDTO shiftDTO, ShiftActionType shiftAction, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean shiftOverlappedWithNonWorkingType, ActivityWrapper absenceActivityWrapper) {
        boolean updatingSameActivity = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getActivityId().equals(absenceActivityWrapper.getActivity().getId())).findAny().isPresent();
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        if (!updatingSameActivity) {
            shiftWithViolatedInfoDTOS = absenceShiftService.createAbsenceTypeShift(absenceActivityWrapper, shiftDTO, staffAdditionalInfoDTO, shiftOverlappedWithNonWorkingType, shiftAction);
        } else {
            List<ShiftDTO> shiftDTOs;
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
            if (CommonConstants.FULL_WEEK.equals(absenceActivityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
                List<Shift> shifts = getFullWeekShiftsByDate(shift.getStartDate(), shift.getEmploymentId(), absenceActivityWrapper.getActivity());
                shifts.forEach(shift1 -> shift1.getActivities().forEach(shiftActivity -> shiftActivity.setAbsenceReasonCodeId(shiftDTO.getActivities().get(0).getAbsenceReasonCodeId())));
                shiftMongoRepository.saveEntities(shifts);
                shiftDTOs = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shifts, ShiftDTO.class);
            } else {
                shiftMongoRepository.save(shift);
                updateAbsenceReasonCode(shiftDTO, shift);
                shiftDTOs = newArrayList(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class));
            }
            for (ShiftDTO shiftDto1 : shiftDTOs) {
                shiftWithViolatedInfoDTOS.add(new ShiftWithViolatedInfoDTO(newArrayList(shiftDTO)));
            }
        }
        return shiftWithViolatedInfoDTOS;
    }

    private void updateAbsenceReasonCode(ShiftDTO shiftDTO, Shift shift) {
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            shiftActivity.setAbsenceReasonCodeId(shiftDTO.getActivities().get(0).getAbsenceReasonCodeId());
        }
    }

    public void updateCTADetailsOfEmployement(LocalDate shiftDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asDate(shiftDate));
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.cta.notFound", shiftDate);
        }
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
    }

    private Shift updateShiftAfterPublish(Shift shift, ShiftActionType shiftActionType) {
        Shift originalShift = shiftMongoRepository.findOne(shift.getId());
        boolean valid = shift.getActivities().stream().allMatch(activity -> activity.getStatus().contains(ShiftStatus.PUBLISH)) && UserContext.getUserDetails().isManagement();
        if (valid && ShiftActionType.SAVE_AS_DRAFT.equals(shiftActionType)) {
            Shift draftShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
            draftShift.setPlannedMinutesOfTimebank(originalShift.getPlannedMinutesOfTimebank());
            draftShift.setTimeBankCtaBonusMinutes(originalShift.getTimeBankCtaBonusMinutes());
            draftShift.setScheduledMinutes(originalShift.getScheduledMinutes());
            originalShift.setDraftShift(draftShift);
            originalShift.getDraftShift().setDraft(true);
        } else if (valid && ShiftActionType.SAVE.equals(shiftActionType)) {
            originalShift = shift;
            originalShift.setDraft(false);
        } else {
            originalShift.setDraft(false);
            originalShift.setDraftShift(null);
        }
        return originalShift;
    }

    private ShiftFunctionWrapper getShiftByStaffId(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, StaffFilterDTO staffFilterDTO) {
        if (staffId == null) {
            exceptionService.actionNotPermittedException(STAFF_ID_NULL);
        }
        Map<LocalDate, List<FunctionDTO>> functionDTOMap = new HashMap<>();
        List<ReasonCodeDTO> reasonCodeDTOS;
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = null;
        if (Optional.ofNullable(employmentId).isPresent()) {
            staffAdditionalInfoDTO = userIntegrationService.verifyEmploymentAndFindFunctionsAfterDate(staffId, employmentId);
            if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_BELONGS, staffId);
            }
            if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT, startDate.toString());
            }
            reasonCodeDTOS = staffAdditionalInfoDTO.getReasonCodes();
            List<FunctionDTO> appliedFunctionDTOs = null;
            if (Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
                appliedFunctionDTOs = staffAdditionalInfoDTO.getEmployment().getAppliedFunctions();
            }
            addFunction(functionDTOMap, staffAdditionalInfoDTO, appliedFunctionDTOs);
        } else {
            functionDTOMap = userIntegrationService.getFunctionsOfEmployment(unitId, startDate, endDate);
            List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.TIME_TYPE.toString()));
            reasonCodeDTOS = userIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        }
        Map<Long, ReasonCodeDTO> reasonCodeMap = reasonCodeDTOS.stream().collect(Collectors.toMap(ReasonCodeDTO::getId, v -> v));
        //When employmentID is not present then we are retreiving shifts for all staffs(NOT only for Employment).
        if (endDate == null) {
            endDate = DateUtils.getLocalDate();
        }
        List<ShiftDTO> shifts;
        if (Optional.ofNullable(employmentId).isPresent()) {
            shifts = shiftMongoRepository.findAllShiftsBetweenDuration(employmentId, staffId, asDate(startDate), asDate(endDate), unitId);
        } else {
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationOfUnitAndStaffId(staffId, asDate(startDate), asDate(endDate), unitId);
        }
        shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO, new ArrayList<>());
        addReasonCode(shifts, reasonCodeDTOS);
        for (ShiftDTO shift : shifts) {
            for (ShiftActivityDTO activity : shift.getActivities()) {
                activity.setReasonCode(reasonCodeMap.get(activity.getAbsenceReasonCodeId()));
            }
        }
        UserAccessRoleDTO userAccessRoleDTO;
        if (isNotNull(staffAdditionalInfoDTO)) {
            //shifts = timeBankService.updateTimebankDetailsInShiftDTO(shifts);
            userAccessRoleDTO = staffAdditionalInfoDTO.getUserAccessRoleDTO();
        } else {
            userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        }
        shifts = updateDraftShiftToShift(shifts, userAccessRoleDTO);
        shifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shifts);
        Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        shiftDetailsService.setLayerInShifts(shiftsMap);
        return new ShiftFunctionWrapper(shiftsMap, functionDTOMap);
    }


    public List<ShiftDTO> updateDraftShiftToShift(List<ShiftDTO> shifts, UserAccessRoleDTO userAccessRoleDTO) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        if (userAccessRoleDTO.getManagement()) {
            for (ShiftDTO shift : shifts) {
                if (isNotNull(shift.getDraftShift())) {
                    ShiftDTO shiftDTO = shift.getDraftShift();
                    shiftDTO.setDraft(true);
                    if (!shift.isDraft()) {
                        shiftDTO.setHasOriginalShift(true);
                    }
                    shiftDTO.setId(shift.getId());
                    shiftDTOS.add(shiftDTO);
                } else {
                    shiftDTOS.add(shift);
                }
            }
        } else {
            shiftDTOS = shifts.stream().filter(shiftDTO -> !shiftDTO.isDraft()).collect(Collectors.toList());
        }
        return shiftDTOS;
    }

    private void addFunction(Map<LocalDate, List<FunctionDTO>> functionDTOMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<FunctionDTO> appliedFunctionDTOs) {
        if (CollectionUtils.isNotEmpty(appliedFunctionDTOs)) {
            for (FunctionDTO appliedFunctionDTO : appliedFunctionDTOs) {
                if (CollectionUtils.isNotEmpty(appliedFunctionDTO.getAppliedDates())) {
                    FunctionDTO functionDTO = new FunctionDTO(appliedFunctionDTO.getId(), appliedFunctionDTO.getName(), appliedFunctionDTO.getIcon());
                    functionDTO.setCode(appliedFunctionDTO.getCode());
                    functionDTO.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
                    for (LocalDate date : appliedFunctionDTO.getAppliedDates()) {
                        functionDTOMap.put(date, Arrays.asList(functionDTO));
                    }
                }
            }
        }
    }


    public ShiftWithViolatedInfoDTO deleteAllLinkedShifts(BigInteger shiftId) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        Shift shift = shiftMongoRepository.findById(shiftId).orElseThrow(()->new DataNotFoundByIdException(convertMessage(MESSAGE_SHIFT_IDS)));
        Activity activity = activityRepository.findOne(shift.getActivities().get(0).getActivityId());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), shift.getEmploymentId(), Collections.emptySet());
        if (staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff() && !staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaffId().equals(shift.getStaffId())) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PERMISSION);
        }
        ViolatedRulesDTO violatedRulesDTO;
        if (CommonConstants.FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
            violatedRulesDTO = deleteFullWeekShifts(shiftDTOS, getFullWeekShiftsByDate(shift.getStartDate(), shift.getEmploymentId(), activity), staffAdditionalInfoDTO);
        } else {
            violatedRulesDTO = validateRule(shift, staffAdditionalInfoDTO);
            if(isCollectionEmpty(violatedRulesDTO.getWorkTimeAgreements()) && isCollectionEmpty(violatedRulesDTO.getActivities())) {
                shift.setDeleted(true);
                shiftDTOS.add(deleteShift(shift, staffAdditionalInfoDTO));
            } else {
                ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
                shiftDTO.setShiftDate(asLocalDate(shift.getStartDate()));
                shiftDTOS.add(shiftDTO);
            }
        }
        return new ShiftWithViolatedInfoDTO(shiftDTOS, violatedRulesDTO);
    }

    public ViolatedRulesDTO deleteFullWeekShifts(List<ShiftDTO> shiftDTOS, List<Shift> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ViolatedRulesDTO violatedRulesDTO = new ViolatedRulesDTO();
        for (Shift shift : shifts) {
            violatedRulesDTO = validateRule(shift, staffAdditionalInfoDTO);
            if (isCollectionNotEmpty(violatedRulesDTO.getWorkTimeAgreements())) {
                break;
            }
            shift.setDeleted(true);
            shiftMongoRepository.save(shift);
        }
        if (isCollectionNotEmpty(violatedRulesDTO.getWorkTimeAgreements())) {
            shifts.forEach(shift -> {
                shift.setDeleted(false);
                ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
                shiftDTO.setShiftDate(asLocalDate(shift.getStartDate()));
                shiftDTOS.add(shiftDTO);
            });
            shiftMongoRepository.saveAll(shifts);
        } else {
            shifts.forEach(shift -> shiftDTOS.add(deleteShift(shift, staffAdditionalInfoDTO)));
        }
        return violatedRulesDTO;
    }

    public List<Shift> getFullWeekShiftsByDate(Date shiftStartDate, Long employmentId, Activity activity) {
        ZonedDateTime startDate = asZoneDateTime(shiftStartDate).with(TemporalAdjusters.previousOrSame(activity.getTimeCalculationActivityTab().getFullWeekStart())).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = startDate.plusDays(7);
        return shiftMongoRepository.findShiftBetweenDurationByEmploymentId(employmentId, asDate(startDate), asDate(endDate));
    }

    public ViolatedRulesDTO validateRule(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        shiftValidatorService.validateStatusOfShiftActivity(shift);
        Set<BigInteger> activityIds = new HashSet<>();
        for (ShiftActivity activity : shift.getActivities()) {
            activityIds.add(activity.getActivityId());
            activityIds.addAll(activity.getChildActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet()));
        }
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);

        ViolatedRulesDTO violatedRulesDTO = shiftValidatorService.validateRuleOnShiftDelete(activityWrapperMap, shift, staffAdditionalInfoDTO);

        return violatedRulesDTO;
    }

    private ShiftDTO deleteShift(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftDTO shiftDTO = new ShiftDTO();
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByStaffId(shift.getStaffId(), DateUtils.getStartOfDay(shift.getStartDate()), DateUtils.getEndOfDay(shift.getEndDate()));
        if (shifts.size() == 1 && CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getEmployment().getAppliedFunctions()) && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_DAY_CALCULATION)
                && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
            Long functionId = userIntegrationService.removeFunctionFromEmploymentByDate(shift.getUnitId(), shift.getEmploymentId(), shift.getStartDate());
            shiftDTO.setFunctionDeleted(true);
            shift.setFunctionId(functionId);
        }
        shiftMongoRepository.save(shift);
        wtaRuleTemplateCalculationService.updateWTACounter(shift, staffAdditionalInfoDTO);
        shiftDTO.setId(shift.getId());
        shiftDTO.setStartDate(shift.getStartDate());
        shiftDTO.setEndDate(shift.getEndDate());
        shiftDTO.setUnitId(shift.getUnitId());
        shiftDTO.setDeleted(true);
        shiftDTO.setShiftDate(asLocalDate(shift.getStartDate()));
        shiftDTO.setActivities(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shift.getActivities(), ShiftActivityDTO.class));
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        todoService.deleteTodo(shift.getId(), null);
        timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, false);
        payOutService.deletePayOut(shift.getId());
        List<BigInteger> jobIds = shift.getActivities().stream().map(ShiftActivity::getId).collect(Collectors.toList());
        shiftReminderService.deleteReminderTrigger(jobIds, shift.getUnitId());
        return shiftDTO;
    }


    private void validateStaffingLevel(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, Shift oldStateShift) {
        ShiftType oldStateShiftType = oldStateShift.getShiftType();
        ShiftType shiftType = shift.getShiftType();
        boolean activityReplaced = activityReplaced(oldStateShift, shift);
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = new RuleTemplateSpecificInfo();
        StaffingLevelHelper staffingLevelHelper = new StaffingLevelHelper();
        if (activityReplaced) {
            for (int i = 0; i < oldStateShift.getActivities().size(); i++) {
                try {
                    if (activityWrapperMap.get(oldStateShift.getActivities().get(i).getActivityId()).getTimeTypeInfo().getPriorityFor().equals(activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getTimeTypeInfo().getPriorityFor())) {
                        shift.setShiftType(oldStateShiftType);
                        shiftValidatorService.validateStaffingLevel(phase, oldStateShift, activityWrapperMap, false, oldStateShift.getActivities().get(i), ruleTemplateSpecificInfo, staffingLevelHelper);
                        shift.setShiftType(shiftType);
                        shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, true, shift.getActivities().get(i), ruleTemplateSpecificInfo, staffingLevelHelper);
                        if (isNull(activityWrapperMap.get(oldStateShift.getActivities().get(i).getActivityId()).getActivityPriority()) || isNull(activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getActivityPriority())) {
                            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_PRIORITY_SEQUENCE);
                        }
                        int rankOfOld = activityWrapperMap.get(oldStateShift.getActivities().get(i).getActivityId()).getActivityPriority().getSequence();
                        int rankOfNew = activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getActivityPriority().getSequence();
                        long durationMinutesOfOld = oldStateShift.getActivities().get(i).getInterval().getMinutes();
                        long durationMinutesOfNew = shift.getActivities().get(i).getInterval().getMinutes();
                        boolean allowedForReplace = true;
                        String staffingLevelState = null;
                        if (UNDERSTAFFING.equals(staffingLevelHelper.getStaffingLevelForOld()) && OVERSTAFFING.equals(staffingLevelHelper.getStaffingLevelForNew())) {
                            exceptionService.actionNotPermittedException(SHIFT_CAN_NOT_MOVE, OVERSTAFFING);
                        }
                        if (BALANCED.equals(staffingLevelHelper.getStaffingLevelForNew()) && UNDERSTAFFING.equals(staffingLevelHelper.getStaffingLevelForOld())) {
                            if (!(rankOfNew < rankOfOld || (rankOfNew == rankOfOld && durationMinutesOfNew > durationMinutesOfOld))) {
                                allowedForReplace = false;
                                staffingLevelState = UNDERSTAFFING;
                            }
                        }
                        if (BALANCED.equals(staffingLevelHelper.getStaffingLevelForOld()) && OVERSTAFFING.equals(staffingLevelHelper.getStaffingLevelForNew())) {
                            if (!(rankOfNew < rankOfOld || (rankOfNew == rankOfOld && durationMinutesOfNew > durationMinutesOfOld))) {
                                allowedForReplace = false;
                                staffingLevelState = OVERSTAFFING;
                            }
                        }

                        if (!allowedForReplace) {
                            exceptionService.actionNotPermittedException(SHIFT_CAN_NOT_MOVE, staffingLevelState);
                        }
                    }
                    //else {
//                        shift.setShiftType(oldStateShiftType);
//                        shiftValidatorService.validateStaffingLevel(phase, oldStateShift, activityWrapperMap, false, oldStateShift.getActivities().get(i), ruleTemplateSpecificInfo,new StaffingLevelHelper());
//                        shift.setShiftType(shiftType);
//                        shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, true, shift.getActivities().get(i), ruleTemplateSpecificInfo,new StaffingLevelHelper());
//                    }
                } catch (IndexOutOfBoundsException e) {
                    //Intentionally left blank
                }
            }
        }
    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }

    private ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate, ViewType viewType, StaffFilterDTO staffFilterDTO) {
        if (endLocalDate == null) {
            exceptionService.actionNotPermittedException(ENDDATE_NULL);
        }
        Date startDate = asDate(startLocalDate);
        Date endDate = asDate(endLocalDate);
        List<ShiftDTO> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate);
        assignedShifts = shiftFilterService.getShiftsByFilters(assignedShifts, staffFilterDTO, new ArrayList<>());
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        assignedShifts = updateDraftShiftToShift(assignedShifts, userAccessRoleDTO);
        Map<Long, List<ShiftDTO>> employmentIdAndShiftsMap = assignedShifts.stream().collect(Collectors.groupingBy(ShiftDTO::getEmploymentId, Collectors.toList()));
        assignedShifts = new ArrayList<>(assignedShifts.size());
        for (Map.Entry<Long, List<ShiftDTO>> employmentIdAndShiftEntry : employmentIdAndShiftsMap.entrySet()) {
            List<ShiftDTO> shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(employmentIdAndShiftEntry.getValue());
            assignedShifts.addAll(shiftDTOS);
        }
        List<OpenShift> openShifts = userAccessRoleDTO.getManagement() ? openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate) :
                openShiftNotificationMongoRepository.findValidOpenShiftsForStaff(userAccessRoleDTO.getStaffId(), startDate, endDate);
        ButtonConfig buttonConfig = null;
        if (Optional.ofNullable(viewType).isPresent() && viewType.toString().equalsIgnoreCase(ViewType.WEEKLY.toString())) {
            buttonConfig = findButtonConfig(assignedShifts, userAccessRoleDTO.getManagement());
        }
        List<OpenShiftResponseDTO> openShiftResponseDTOS = new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift, openShiftResponseDTO, openShift.getStartDate().toString(), openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });
        List<AccessGroupRole> roles = new ArrayList<>();
        if (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getManagement()) {
            roles.add(MANAGEMENT);
        }
        if (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getStaff()) {
            roles.add(AccessGroupRole.STAFF);
        }
        StaffAccessRoleDTO staffAccessRoleDTO = new StaffAccessRoleDTO(userAccessRoleDTO.getStaffId(), roles);
        Map<LocalDate, List<FunctionDTO>> appliedFunctionDTOs = userIntegrationService.getFunctionsOfEmployment(unitId, startLocalDate, endLocalDate);
        Map<LocalDate, List<ShiftDTO>> shiftsMap = assignedShifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        shiftDetailsService.setLayerInShifts(shiftsMap);
        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO, buttonConfig, appliedFunctionDTOs);
    }

    public ButtonConfig findButtonConfig(List<ShiftDTO> shifts, boolean management) {
        ButtonConfig buttonConfig = new ButtonConfig();
        if (management && isCollectionNotEmpty(shifts)) {
            Set<BigInteger> shiftIds = shifts.stream().map(ShiftDTO::getId).collect(Collectors.toSet());
            List<ShiftState> shiftStates = shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds, MANAGEMENT);
            Set<BigInteger> shiftStateIds = shiftStates.stream().map(ShiftState::getShiftId).collect(Collectors.toSet());
            for (BigInteger shiftId : shiftIds) {
                if (!shiftStateIds.contains(shiftId)) {
                    buttonConfig.setSendToPayrollEnabled(false);
                    break;
                }
                buttonConfig.setSendToPayrollEnabled(true);
            }
        }
        return buttonConfig;
    }

    private List<ShiftDTO> getShiftOfStaffByExpertiseId(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long expertiseId, StaffFilterDTO staffFilterDTO) {
        if (staffId == null || endDate == null || expertiseId == null) {
            exceptionService.actionNotPermittedException(STAFF_ID_END_DATE_NULL);
        }
        Long employmentId = userIntegrationService.getEmploymentId(unitId, staffId, expertiseId);
        List<ShiftDTO> shiftDTOS = shiftMongoRepository.getAllShiftBetweenDuration(employmentId, staffId, asDate(startDate), asDate(endDate), unitId);
        shiftDTOS = shiftFilterService.getShiftsByFilters(shiftDTOS, staffFilterDTO, new ArrayList<>());
        return wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS);
    }

    public ShiftWithActivityDTO buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ShiftDTO shiftDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(isNull(shiftDTO) ? shift : shiftDTO, ShiftWithActivityDTO.class);
        updateActivityDetails(activityWrapperMap, shiftWithActivityDTO);
        if (isNotNull(shiftWithActivityDTO.getDraftShift())) {
            updateActivityDetails(activityWrapperMap, shiftWithActivityDTO.getDraftShift());
        }
        if (isNotNull(shiftDTO)) {
            shiftDTO.getActivities().forEach(shiftActivityDTO ->
                    shiftActivityDTO.setActivityName(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getName())
            );
            shiftWithActivityDTO.setStartDate(shiftDTO.getActivities().get(0).getStartDate());
            shiftWithActivityDTO.setEndDate(shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        }
        return shiftWithActivityDTO;
    }

    private void updateActivityDetails(Map<BigInteger, ActivityWrapper> activityWrapperMap, ShiftWithActivityDTO shiftWithActivityDTO) {
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
            shiftActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class));
            shiftActivityDTO.setTimeType(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getTimeType());
            shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> {
                childActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class));
                childActivityDTO.setTimeType(activityWrapperMap.get(childActivityDTO.getActivityId()).getTimeType());
            });
        });
    }

    public void deleteShiftsAndOpenShiftsOnEmploymentEnd(Long staffId, LocalDateTime employmentEndDate, Long unitId) {
        shiftMongoRepository.deleteShiftsAfterDate(staffId, employmentEndDate);
        List<OpenShift> openShifts = openShiftMongoRepository.findAllOpenShiftsByInterestedStaff(staffId, employmentEndDate);
        if (!openShifts.isEmpty()) {
            for (OpenShift openShift : openShifts) {
                openShift.getInterestedStaff().remove(staffId);
                openShift.getAssignedStaff().remove(staffId);
            }
            openShiftMongoRepository.saveEntities(openShifts);
        }
    }

    public void deleteShiftsAfterEmploymentEndDate(Long employmentId, LocalDate employmentEndDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<Shift> shiftList = shiftMongoRepository.findAllShiftsByEmploymentIdAfterDate(employmentId, asDate(employmentEndDate));
        timeBankService.updateDailyTimebankForShifts(null, employmentId, staffAdditionalInfoDTO, shiftList);
    }

    //TODO need to optimize this method
    public List<ShiftWithViolatedInfoDTO> updateShiftByTandA(Long unitId, ShiftDTO shiftDTO, Boolean updatedByStaff) {
        Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.REALTIME.toString());
        Map<String, Phase> phaseMap = new HashMap<String, Phase>() {{
            put(phase.getPhaseEnum().toString(), phase);
        }};
        if (shiftDTO.getShiftStatePhaseId().equals(phase.getId())) {
            shiftValidatorService.validateRealTimeShift(unitId, shiftDTO, phaseMap);
        }
        if (isNull(shiftDTO.getShiftId())) {
            shiftDTO.setShiftId(shiftDTO.getId());
        }
        BigInteger shiftStateId = shiftDTO.getId();
        BigInteger shiftStatePhaseId = shiftDTO.getShiftStatePhaseId();
        shiftDTO.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = updateShift(shiftDTO, true, false, null);
        for (ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO : shiftWithViolatedInfoDTOS) {
            ShiftDTO updatedShiftDto = shiftStateService.updateShiftStateAfterValidatingWtaRule(shiftWithViolatedInfoDTO.getShifts().get(0), shiftStateId, shiftStatePhaseId);
            List<ShiftDTO> shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(newArrayList(updatedShiftDto));
            shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
            shiftWithViolatedInfoDTO.getShifts().get(0).setEditable(true);
            shiftWithViolatedInfoDTO.getShifts().get(0).setDurationMinutes((int) shiftWithViolatedInfoDTO.getShifts().get(0).getInterval().getMinutes());
        }
        return shiftWithViolatedInfoDTOS;
    }

    private ShiftDetailViewDTO getShiftDetailsOfStaff(String timeZone, Map<String, Phase> phaseMap, List<Shift> shifts, List<ShiftState> shiftStatesList, StaffFilterDTO staffFilterDTO, Map<BigInteger, PhaseDefaultName> phaseIdAndDefaultNameMap) {
        List<ShiftDTO> plannedShifts = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        if (isCollectionEmpty(plannedShifts)) {
            shifts = shifts.stream().filter(shift -> !newHashSet(PhaseDefaultName.TIME_ATTENDANCE, PhaseDefaultName.REALTIME).contains(phaseIdAndDefaultNameMap.get(shift.getPhaseId()))).collect(Collectors.toList());
            plannedShifts = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shifts, ShiftDTO.class);
        }
        plannedShifts = shiftFilterService.getShiftsByFilters(plannedShifts, staffFilterDTO, new ArrayList<>());
        plannedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(plannedShifts);
        List<ShiftDTO> realTimeShift = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        realTimeShift = shiftFilterService.getShiftsByFilters(realTimeShift, staffFilterDTO, new ArrayList<>());
        List<ShiftDTO> shiftStateDTOs = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftStatesList, ShiftDTO.class);
        shiftStateDTOs = shiftFilterService.getShiftsByFilters(shiftStateDTOs, staffFilterDTO, new ArrayList<>());
        List<ShiftDTO> staffValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(AccessGroupRole.STAFF) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList());
        staffValidatedShifts = shiftFilterService.getShiftsByFilters(staffValidatedShifts, staffFilterDTO, new ArrayList<>());
        Map<String, ShiftDTO> staffAndShiftMap = staffValidatedShifts.stream().collect(Collectors.toMap(k -> k.getStaffId() + "" + k.getId(), v -> v));
        DateTimeInterval graceInterval;
        List<ShiftDTO> updateRealTime = new ArrayList<>();
        for (ShiftDTO shiftDTO : realTimeShift) {
            if (!Optional.ofNullable(staffAndShiftMap.get(shiftDTO.getStaffId() + "" + shiftDTO.getId())).isPresent() && shiftDTO.getValidated() == null && phaseService.shiftEditableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate())) {
                shiftDTO.setEditable(true);
            }
            updateRealTime.add(shiftDTO);
        }
        updateRealTime = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(updateRealTime);
        if (!staffValidatedShifts.isEmpty()) {
            Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(staffValidatedShifts.get(0).getUnitId(), PhaseDefaultName.TIME_ATTENDANCE.toString());
            graceInterval = shiftValidatorService.getGracePeriodInterval(phase, staffValidatedShifts.get(0).getStartDate(), false);
            for (ShiftDTO staffValidatedShift : staffValidatedShifts) {
                if (staffValidatedShift.getValidated() == null && graceInterval.contains(staffValidatedShift.getStartDate())) {
                    staffValidatedShift.setEditable(true);
                }
            }
        }
        staffValidatedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(staffValidatedShifts);
        List<ShiftDTO> plannerValidatedShifts = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(MANAGEMENT) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        plannerValidatedShifts = shiftFilterService.getShiftsByFilters(plannerValidatedShifts, staffFilterDTO, new ArrayList<>());
        //change id because id was same and issue on FE side and this is only for show FE side
        for (ShiftDTO shiftDTO : plannerValidatedShifts) {
            if (shiftDTO.getValidated() == null) {
                shiftDTO.setEditable(true);
            }
        }
        plannerValidatedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(plannerValidatedShifts);
        return new ShiftDetailViewDTO(plannedShifts, updateRealTime, staffValidatedShifts, plannerValidatedShifts);
    }

    public CompactViewDTO getDetailedAndCompactViewData(Long selectedStaffId, Long unitId, Date shiftStartDate, StaffFilterDTO staffFilterDTO) {
        List<Long> staffIds;
        if (isNull(selectedStaffId)) {
            List<StaffPersonalDetail> staffResponseDTOS = userIntegrationService.getStaffListByUnit();
            staffIds = staffResponseDTOS.stream().map(StaffPersonalDetail::getId).collect(Collectors.toList());
        } else {
            staffIds = Arrays.asList(selectedStaffId);
        }
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String, Phase> phaseMap = phases.stream().collect(Collectors.toMap(p -> p.getPhaseEnum().toString(), Function.identity()));
        Date endDate = asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1));
        List<TimeAndAttendanceDTO> timeAndAttendance = timeAndAttendanceRepository.findAllAttendanceByStaffIds(staffIds, unitId, asDate(DateUtils.asLocalDate(shiftStartDate).minusDays(1)), shiftStartDate);
        Map<Long, List<AttendanceTimeSlotDTO>> staffsTimeAndAttendance = (CollectionUtils.isNotEmpty(timeAndAttendance)) ? timeAndAttendance.stream().collect(Collectors.toMap(TimeAndAttendanceDTO::getStaffId, TimeAndAttendanceDTO::getAttendanceTimeSlot)) : new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findShiftByStaffIdsAndDate(staffIds, shiftStartDate, endDate);
        shifts.forEach(shift -> shift.setDurationMinutes((int) shift.getInterval().getMinutes()));
        shifts = shifts.stream().filter(shift -> !shift.isDraft()).collect(Collectors.toList());
        List<ShiftState> shiftStates = shiftStateMongoRepository.getAllByStaffsByIdsBetweenDate(staffIds, shiftStartDate, endDate);
        Map<BigInteger, PhaseDefaultName> phaseIdAndDefaultNameMap = phases.stream().collect(Collectors.toMap(MongoBaseEntity::getId, Phase::getPhaseEnum));
        List<ShiftState> realTimeShiftStatesList = shiftStateService.checkAndCreateRealtimeAndDraftState(shifts, phaseMap, phaseIdAndDefaultNameMap);
        shiftStates.addAll(realTimeShiftStatesList);
        Map<Long, List<Shift>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.TIME_TYPE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = userIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        Map<Long, List<ShiftState>> shiftStateMap = shiftStates.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        List<DetailViewDTO> shiftDetailViewDTOMap = staffIds.stream().map(staffId -> new DetailViewDTO(staffId, getShiftDetailsOfStaff(timeZone, phaseMap, shiftsMap.getOrDefault(staffId, new ArrayList<>()), shiftStateMap.getOrDefault(staffId, new ArrayList<>()), staffFilterDTO, phaseIdAndDefaultNameMap), staffsTimeAndAttendance.getOrDefault(staffId, new ArrayList<>()))).collect(Collectors.toList());
        Map<LocalDate, List<FunctionDTO>> functionDTOMap = userIntegrationService.getFunctionsOfEmployment(unitId, asLocalDate(shiftStartDate), asLocalDate(endDate));
        return new CompactViewDTO(shiftDetailViewDTOMap, reasonCodeDTOS, functionDTOMap);
    }

    //Description this method will fetch all shifts / open shifts and shift states based on the above request param
    public Object getAllShiftAndStates(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, ViewType viewType, ShiftFilterParam shiftFilterParam, Long expertiseId, StaffFilterDTO staffFilterDTO) {
        Object object = null;
        if (endDate != null) {
            endDate = endDate.plusDays(1);
        }
        switch (shiftFilterParam) {
            case INDIVIDUAL_VIEW:
                object = getShiftByStaffId(unitId, staffId, startDate, endDate, employmentId, staffFilterDTO);
                break;
            case OPEN_SHIFT:
                object = getAllShiftsOfSelectedDate(unitId, startDate, endDate, viewType, staffFilterDTO);
                break;
            case EXPERTISE:
                object = getShiftOfStaffByExpertiseId(unitId, staffId, startDate, endDate, expertiseId, staffFilterDTO);
                break;
            case SHIFT_STATE:
                object = getDetailedAndCompactViewData(staffId, unitId, asDate(startDate), staffFilterDTO);
                break;
            default:
                exceptionService.actionNotPermittedException(PLEASE_SELECT_VALID_CRITERIA);
        }
        return object;
    }

    private boolean activityReplaced(Shift dbShift, Shift shift) {
        boolean activityReplaced = false;
        if (shift.getActivities().size() == dbShift.getActivities().size()) {
            for (int i = 0; i < shift.getActivities().size(); i++) {
                if (!shift.getActivities().get(i).getActivityId().equals(dbShift.getActivities().get(i).getActivityId())) {
                    activityReplaced = true;
                    break;
                }
            }
        }
        return activityReplaced;
    }

    private ActivityWrapper getAbsenceTypeOfActivityIfPresent(List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ActivityWrapper activityWrapper = null;
        for (ShiftActivityDTO shiftActivityDTO : shiftActivityDTOS) {
            if (CommonConstants.FULL_WEEK.equals(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
                activityWrapper = activityWrapperMap.get(shiftActivityDTO.getActivityId());
            }
        }
        return activityWrapper;
    }

    public boolean updatePlanningPeriodInShifts() {
        List<Shift> shifts = shiftMongoRepository.findAllAbsenceShifts(ShiftType.ABSENCE.toString());
        for (Shift shift : shifts) {
            PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOneByUnitIdAndDate(shift.getUnitId(), getStartOfDay(shift.getStartDate()));
            shift.setPlanningPeriodId(isNotNull(planningPeriod) ? planningPeriod.getId() : null);
        }
        if (isCollectionNotEmpty(shifts)) {
            shiftMongoRepository.saveEntities(shifts);
        }
        return true;
    }

    private boolean isIgnoredAllRuletemplate(ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO, ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfoDTO) {
        Set<BigInteger> violatedRuleTemplateIds = shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().stream().map(WorkTimeAgreementRuleViolation::getRuleTemplateId).collect(Collectors.toSet());
        Set<BigInteger> updatedViolatedRuleTemplateIds = updatedShiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().stream().map(WorkTimeAgreementRuleViolation::getRuleTemplateId).collect(Collectors.toSet());
        return violatedRuleTemplateIds.equals(updatedViolatedRuleTemplateIds);
    }

    public Long getPublishShiftCount(Long employmentId) {
        List<Shift> shifts = shiftMongoRepository.findAllPublishShiftByEmploymentId(employmentId);
        return (long) shifts.size();
    }

    private List<Shift> getShiftsOnTheBasisOfGapActivity(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        List<Shift> shiftList = new ArrayList<>();
        Shift shift1 = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shift1.setActivities(new ArrayList<>());
        shift1.setBreakActivities(new ArrayList<>());
        Iterator iterator = shift.getActivities().iterator();
        while (iterator.hasNext()) {
            ShiftActivity shiftActivity = (ShiftActivity) iterator.next();
            if (GAP.equals(activityWrapperMap.get(shiftActivity.getActivityId()).getTimeTypeInfo().getSecondLevelType())) {
                shift1.setEndDate(shiftActivity.getStartDate());
                shiftBreakService.updateBreak(shift, shift1, shiftActivity);
                shiftList.add(shift1);
                shift1 = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
                shift1.setActivities(new ArrayList<>());
                shift1.setBreakActivities(new ArrayList<>());

                continue;
            }
            shift1.getActivities().add(shiftActivity);

            if (!iterator.hasNext()) {
                shift1.setStartDate(shift1.getActivities().get(0).getStartDate());
                shift1.setEndDate(shift1.getActivities().get(shift1.getActivities().size() - 1).getEndDate());
                shiftBreakService.updateBreak(shift, shift1, shiftActivity);
                shiftList.add(shift1);
            }

        }
        return shiftList;
    }

    @Getter
    public class ShiftHelper {
        private Map<Long, StaffAdditionalInfoDTO> employmentIdAndstaffAdditionalInfoMap = new HashMap<>();
        private Map<BigInteger, ActivityWrapper> activityWrapperMap = new HashMap<>();
        private Map<LocalDate, PlanningPeriod> planningPeriodMap = new HashMap<>();
        private Long unitId;
        private Map<String, WTAQueryResultDTO> workTimeAgreementMap = new HashMap<>();
        private Map<String, CTAResponseDTO> collectiveTimeAgreementMap = new HashMap<>();
        private Map<LocalDate, Phase> phaseMap = new HashMap<>();
        private Set<Long> absenceReasonCodeIds;
        private Map<LocalDate, StaffingLevel> staffingLevelMap = new HashMap<>();
        private ShiftActionType shiftActionType;

        public ShiftHelper(ShiftDTO shiftDTO, Shift oldShift, ShiftActionType shiftActionType) {
            LocalDate localDate = shiftDTO.getShiftDate();
            this.unitId = shiftDTO.getUnitId();
            this.shiftActionType = shiftActionType;
            updateAbsenceResonCodeIds(shiftDTO);
            updateActivityWrapperMap(newArrayList(shiftDTO), oldShift);
            updateShiftHelperByDetails(shiftDTO.getEmploymentId(), shiftDTO.getStaffId(), localDate);
            getStaffingLevel(localDate);
            if (isNotNull(oldShift) && !localDate.equals(asLocalDate(oldShift.getStartDate()))) {
                localDate = asLocalDate(oldShift.getStartDate());
                updateShiftHelperByDetails(oldShift.getEmploymentId(), oldShift.getStaffId(), localDate);
                getStaffingLevel(localDate);
            }

        }

        private void updateAbsenceResonCodeIds(ShiftDTO shiftDTO) {
            absenceReasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> isNotNull(shiftActivity.getAbsenceReasonCodeId())).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());
        }

        public void updateShiftHelperByDetails(Long employmentId, Long staffId, LocalDate localDate) {
            getPlanningPeriod(localDate);
            updateWorkTimeAgreement(employmentId, localDate);
            updateStaffAdditionalInfoDTO(employmentId, staffId, localDate);
        }

        public PlanningPeriod getPlanningPeriod(LocalDate localDate) {
            if (!this.planningPeriodMap.containsKey(localDate)) {
                PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(this.unitId, localDate);
                if (isNull(planningPeriod)) {
                    exceptionService.actionNotPermittedException(MESSAGE_PERIODSETTING_NOTFOUND);
                }
                this.planningPeriodMap.put(localDate, planningPeriod);
            }
            return this.planningPeriodMap.get(localDate);
        }

        public StaffingLevel getStaffingLevel(LocalDate localDate) {
            if (!this.staffingLevelMap.containsKey(localDate)) {
                List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(this.unitId, asDate(localDate), asDate(localDate));
                if (CollectionUtils.isEmpty(staffingLevels)) {
                    exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ABSENT);
                }
                this.staffingLevelMap.put(localDate, staffingLevels.get(0));
            }
            return this.staffingLevelMap.get(localDate);
        }

        public void updateStaffAdditionalInfoDTO(Long employmentId, Long staffId, LocalDate localDate) {
            if (!employmentIdAndstaffAdditionalInfoMap.containsKey(employmentId)) {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(localDate, staffId, employmentId, absenceReasonCodeIds);
                updateCTADetailsOfEmployement(localDate, employmentId);
                employmentIdAndstaffAdditionalInfoMap.put(employmentId, staffAdditionalInfoDTO);
            }
        }

        public void updateActivityWrapperMap(List<ShiftDTO> shifts, Shift oldShift) {
            Set<BigInteger> activityIds = new HashSet<>();
            for (ShiftDTO shift : shifts) {
                getActivityIdsByShiftDTO(activityIds, shift);
            }
            if (isNotNull(oldShift)) {
                getActivityIdsByShift(oldShift, activityIds);
                if (isNotNull(oldShift.getDraftShift())) {
                    getActivityIdsByShift(oldShift.getDraftShift(), activityIds);
                }
            }
            activityIds.removeIf(activityId -> this.activityWrapperMap.containsKey(activityId));
            List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
            activities.forEach(activityWrapper -> this.activityWrapperMap.put(activityWrapper.getActivity().getId(), activityWrapper));
        }

        public void getActivityIdsByShift(Shift oldShift, Set<BigInteger> activityIds) {
            activityIds.addAll(oldShift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(oldShift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
            if (isCollectionNotEmpty(oldShift.getBreakActivities())) {
                activityIds.addAll(oldShift.getBreakActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
            }
        }

        public void getActivityIdsByShiftDTO(Set<BigInteger> activityIds, ShiftDTO shiftDTO) {
            activityIds.addAll(shiftDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getChildActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            if (isCollectionNotEmpty(shiftDTO.getBreakActivities())) {
                activityIds.addAll(shiftDTO.getBreakActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
            }
        }

        public void updateWorkTimeAgreement(Long employmentId, LocalDate localDate) {
            if (!this.collectiveTimeAgreementMap.containsKey(employmentId + "-" + localDate)) {
                WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(employmentId, asDate(localDate));
                if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
                    exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
                }
                this.workTimeAgreementMap.put(employmentId + "-" + localDate, wtaQueryResultDTO);
            }
        }

        public void updateCTADetailsOfEmployement(LocalDate localDate, Long employmentId) {
            updateCollectiveTimeAgreement(localDate, employmentId);
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = employmentIdAndstaffAdditionalInfoMap.get(employmentId);
            staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(this.collectiveTimeAgreementMap.get(employmentId + "-" + localDate).getRuleTemplates());
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        }

        public void updateCollectiveTimeAgreement(LocalDate localDate, Long employmentId) {
            if (!this.collectiveTimeAgreementMap.containsKey(employmentId + "-" + localDate)) {
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(employmentId, asDate(localDate));
                if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
                    exceptionService.dataNotFoundByIdException("error.cta.notFound", localDate);
                }
                this.collectiveTimeAgreementMap.put(employmentId + "-" + localDate, ctaResponseDTO);
            }
        }
    }


//    public void updateStatusForShift(Shift shift, ShiftActionType shiftActionType, boolean shiftUpdated,Map<BigInteger, ActivityWrapper> activityWrapperMap,StaffAdditionalInfoDTO staffAdditionalInfoDTO,PlanningPeriod planningPeriod) {
//        Shift shift1 =isNotNull(shift.getDraftShift())?shift.getDraftShift():shift;
//        Set<ShiftStatus> shiftStatuses = new HashSet<>();
//        for (ShiftActivity shiftActivity : shift1.getActivities()) {
//            updatedStatus(shift, shiftActionType, shiftUpdated, shiftStatuses, shiftActivity,activityWrapperMap,staffAdditionalInfoDTO,planningPeriod);
//        }
//    }

//    private void updatedStatus(Shift shift, ShiftActionType shiftActionType, boolean shiftUpdated, Set<ShiftStatus> shiftStatuses, ShiftActivity shiftActivity, Map<BigInteger, ActivityWrapper> activityWrapperMap,StaffAdditionalInfoDTO staffAdditionalInfoDTO,PlanningPeriod planningPeriod) {
//        boolean isApprovalRequired =(isCollectionNotEmpty(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds()) && activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds().contains(shift.getPhaseId()));
//
//        boolean isHasAccessToApproved =UserContext.getUserDetails().isManagement();
//
//        boolean isPlanningPeriodPublished =planningPeriod.getPublishEmploymentIds().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId());
//
//        if (isPlanningPeriodPublished) {
//            if(isApprovalRequired){
//              shiftActivity.getStatus().clear();
//              shiftStatuses.add(ShiftStatus.PUBLISH);
//              shiftActivity.setStatus(shiftStatuses);
//            }else {
//                shiftActivity.getStatus().add(ShiftStatus.PUBLISH);
//            }
//        }
//        else if (ShiftActionType.SAVE_AS_DRAFT.equals(shiftActionType)) {
//            updateStatusOfAbsenceDraftShiftAndPresenceDraftShift(shiftActionType, shiftStatuses, shiftActivity,isApprovalRequired,isHasAccessToApproved,shiftUpdated);
//        }
//        else if (ShiftActionType.SAVE.equals(shiftActionType) && isApprovalRequired) {
//            updateStatusOfAbsenceShiftOfUpdateShiftAndNewShift(shiftUpdated, shiftStatuses, shiftActivity,shift,isHasAccessToApproved,isPlanningPeriodPublished);
//        }
//        else if(ShiftActionType.SAVE.equals((shiftActionType)) && !isApprovalRequired){
//            updateStatusOfPresenceShiftAfterTheUpdateofShift(shiftUpdated, shiftStatuses, shiftActivity,shift,isHasAccessToApproved);
//        }else{
//            if(isHasAccessToApproved) {
//                if (isApprovalRequired) {
//                    shiftStatuses.add(ShiftStatus.APPROVE);
//                    shiftStatuses.add(ShiftStatus.PUBLISH);
//                    shiftActivity.setStatus(shiftStatuses);
//                } else {
//                    shiftStatuses.add(ShiftStatus.PUBLISH);
//                    shiftActivity.setStatus(shiftStatuses);
//                }
//            }
//            else {
//                shiftStatuses.add(ShiftStatus.REQUEST);
//                shiftActivity.setStatus(shiftStatuses);
//            }
//        }
//    }
//
//    private void updateStatusOfAbsenceDraftShiftAndPresenceDraftShift(ShiftActionType shiftActionType, Set<ShiftStatus> shiftStatuses, ShiftActivity shiftActivity,boolean isApprovalrequired,boolean isHasAccessToApproved,boolean updatedShift) {
//        if(isHasAccessToApproved) {
//
//        }else{
//            shiftStatuses.add(ShiftStatus.REQUEST);
//            shiftActivity.setStatus(shiftStatuses);
//        }
//
//    }
//
//
//    private void updateStatusOfPresenceShiftAfterTheUpdateofShift(boolean shiftUpdated, Set<ShiftStatus> shiftStatuses, ShiftActivity shiftActivity,Shift shift,boolean isHasAccessToApproved) {
//        if(isHasAccessToApproved) {
//
//        }else{
//            shiftStatuses.add(ShiftStatus.REQUEST);
//            shiftActivity.setStatus(shiftStatuses);
//        }
//    }
//
//    private void updateStatusOfAbsenceShiftOfUpdateShiftAndNewShift(boolean shiftUpdated, Set<ShiftStatus> shiftStatuses, ShiftActivity shiftActivity,Shift shift,boolean isHasAccessToApproved,boolean isPlanningPeriodPublished) {
//        if(isHasAccessToApproved) {
//
//        }else {
//            shiftStatuses.add(ShiftStatus.REQUEST);
//            shiftActivity.setStatus(shiftStatuses);
//        }
//    }
//

}
