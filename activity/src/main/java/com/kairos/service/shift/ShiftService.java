package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.attendance.AttendanceTimeSlotDTO;
import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.shift.*;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.ShiftAllowedToDelete;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.todo.TodoService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
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

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.UNPAID_BREAK;
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


    public ShiftWithViolatedInfoDTO createShift(Long unitId, ShiftDTO shiftDTO, ShiftActionType shiftActionType) {
        shiftDTO.setUnitId(unitId);
        Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(shiftDTO.getShiftDate(), shiftDTO.getStaffId(), shiftDTO.getEmploymentId(), reasonCodeIds);
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        shiftValidatorService.checkAbsenceTypeShift(shiftDTO);
        updateCTADetailsOfEmployement(shiftDTO.getShiftDate(), staffAdditionalInfoDTO);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        if ((CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            shiftDTO.setStartDate(asDate(shiftDTO.getShiftDate()));
            boolean shiftOverlappedWithNonWorkingType = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapper, false);
            shiftWithViolatedInfoDTO = absenceShiftService.createAbsenceTypeShift(activityWrapper, shiftDTO, staffAdditionalInfoDTO, shiftOverlappedWithNonWorkingType, shiftActionType);
        } else {
            boolean shiftOverlappedWithNonWorkingType = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapper, false);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, shiftDTO.getActivities().get(0).getStartDate(), null);
            shiftDTO.setShiftType(ShiftType.PRESENCE);
            shiftWithViolatedInfoDTO = saveShift(staffAdditionalInfoDTO, shiftDTO, phase, shiftOverlappedWithNonWorkingType, shiftActionType);

        }
        addReasonCode(shiftWithViolatedInfoDTO.getShifts(), staffAdditionalInfoDTO.getReasonCodes());
        return shiftWithViolatedInfoDTO;
    }

    private void addReasonCode(List<ShiftDTO> shiftDTOS, List<ReasonCodeDTO> reasonCodes) {
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
        Map<BigInteger, ActivityWrapper> activityWrapperMap = getActivityWrapperMap(newArrayList(), shiftDTO);
        mainShift.setPlanningPeriodId(planningPeriod.getId());
        mainShift.setPhaseId(planningPeriod.getCurrentPhaseId());
        List<ShiftActivity>[] shiftActivities = mainShift.getShiftActivitiesForValidatingStaffingLevel(null);
        for (ShiftActivity shiftActivity : shiftActivities[0]) {
            shiftValidatorService.validateStaffingLevel(phase, mainShift, activityWrapperMap, true, staffAdditionalInfoDTO, shiftActivity);
        }
        //shiftValidatorService.validateStaffingLevel(phase, mainShift, activityWrapperMap, true, staffAdditionalInfoDTO);
        List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(false,mainShift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets());
        mainShift.setBreakActivities(breakActivities);
        activityConfigurationService.addPlannedTimeInShift(mainShift, activityWrapperMap, staffAdditionalInfoDTO);
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(mainShift, ShiftDTO.class);
        ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false, shiftActionType);
        if (PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) || shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            mainShift = saveShiftWithActivity(activityWrapperMap, mainShift, staffAdditionalInfoDTO, false, functionId, phase, shiftActionType);
            todoService.createOrUpdateTodo(mainShift, TodoType.APPROVAL_REQUIRED, staffAdditionalInfoDTO.getUserAccessRoleDTO(), isNotNull(shiftDTO.getId()));
            payOutService.updatePayOut(staffAdditionalInfoDTO, mainShift, activityWrapperMap);
            //shiftReminderService.setReminderTrigger(activityWrapperMap, mainShift);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(isNotNull(mainShift.getDraftShift()) ? mainShift.getDraftShift() : mainShift, ShiftDTO.class);
            shiftDTO.setId(mainShift.getId());
            shiftDTO = timeBankService.updateTimebankDetailsInShiftDTO(newArrayList(shiftDTO)).get(0);
            shiftValidatorService.validateShiftViolatedRules(mainShift, shiftOverlappedWithNonWorkingType, shiftWithViolatedInfoDTO, PhaseDefaultName.DRAFT.equals(phase.getPhaseEnum()) ? ShiftActionType.SAVE_AS_DRAFT : null);
            shiftDTO = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(Arrays.asList(shiftDTO), staffAdditionalInfoDTO.getUserAccessRoleDTO()).get(0);
        }
        shiftWithViolatedInfoDTO.setShifts(newArrayList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }

    public Shift saveShiftWithActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift,
                                       StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean updateShift, Long functionId, Phase phase, ShiftActionType shiftAction) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(shift.getPlanningPeriodId());
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
        if (isCollectionNotEmpty(shift.getBreakActivities()) && activityWrapperMap.containsKey(shift.getBreakActivities().get(0).getActivityId()) &&  UNPAID_BREAK.equals(activityWrapperMap.get(shift.getBreakActivities().get(0).getActivityId()).getTimeType())) {
            scheduledMinutes -= shift.getBreakActivities().get(0).getInterval().getMinutes();
        }



        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
        shiftStatusService.updateStatusOfShiftIfPhaseValid(phase, shift, activityWrapperMap, staffAdditionalInfoDTO.getUserAccessRoleDTO(), shiftAction);
        //As discuss with Arvind Presence and Absence type of activity cann't be perform in a Shift
        shift.setShiftType(updateShiftType(activityWrapperMap, shift));
        if (isNotNull(functionId)) {
            if (activityWrapperMap.values().stream().anyMatch(k -> TimeTypeEnum.PRESENCE.equals(k.getActivity().getBalanceSettingsActivityTab().getTimeType()))) {
                Map<LocalDate, Long> dateAndFunctionIdMap = new HashMap<>();
                dateAndFunctionIdMap.put(asLocalDate(shift.getStartDate()), functionId);
                userIntegrationService.applyFunction(shift.getUnitId(), shift.getEmploymentId(), dateAndFunctionIdMap, HttpMethod.POST, null);
            } else {
                exceptionService.actionNotPermittedException(ERROR_FUNCTION_CAN_NOT_APPLY_WITH_ABSENCE_ACTIVITY);
            }
        } else {
            BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", asLocalDate(shift.getStartDate()).toString());
            userIntegrationService.applyFunction(shift.getUnitId(), shift.getEmploymentId(), null, HttpMethod.DELETE, Arrays.asList(appliedDate));
        }
        if (updateShift && isNotNull(shiftAction) && !shift.getActivities().stream().anyMatch(shiftActivity -> !shiftActivity.getStatus().contains(ShiftStatus.PUBLISH))) {
            shift = updateShiftAfterPublish(shift, staffAdditionalInfoDTO.getUserAccessRoleDTO(), shiftAction);
        }
        if (!updateShift && planningPeriod.getPublishEmploymentIds().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId())
                && ShiftActionType.SAVE_AS_DRAFT.equals(shiftAction) && newHashSet(PhaseDefaultName.CONSTRUCTION, PhaseDefaultName.DRAFT, PhaseDefaultName.TENTATIVE).contains(phase.getPhaseEnum())) {
            Shift draftShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
            draftShift.setDraft(true);
            shift.setDraftShift(draftShift);
            shift.setDraft(true);

        }
        shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
        shiftMongoRepository.save(shift);
        timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, false);
        return shift;
    }

    public ShiftType updateShiftType(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        ShiftType shiftType = null;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            TimeTypeEnum timeTypeEnum = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getBalanceSettingsActivityTab().getTimeType();
            if (TimeTypeEnum.PRESENCE.equals(timeTypeEnum)) {
                shiftType = ShiftType.PRESENCE;
                break;
            } else if (TimeTypeEnum.ABSENCE.equals(timeTypeEnum)) {
                shiftType = ShiftType.ABSENCE;
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
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getEmployment());
                scheduledMinutes = shiftActivity.getScheduledMinutes();
                durationMinutes = shiftActivity.getDurationMinutes();
            }
        }
        shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
        shiftActivity.setActivityName(activityWrapper.getActivity().getName());
        return new int[]{scheduledMinutes, durationMinutes};
    }

    public void saveShiftWithActivity(Map<Date, Phase> phaseListByDate, List<Shift> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<BigInteger, ActivityWrapper> activityWrapperMap = getActivityWrapperMap(shifts, null);

        for (Shift shift : shifts) {
            List<ShiftActivity>[] shiftActivities = shift.getShiftActivitiesForValidatingStaffingLevel(null);
            for (ShiftActivity shiftActivity : shiftActivities[0]) {
                shiftValidatorService.validateStaffingLevel(phaseListByDate.get(shift.getStartDate()), shift, activityWrapperMap, true, staffAdditionalInfoDTO, shiftActivity);
            }
            //shiftValidatorService.validateStaffingLevel(phaseListByDate.get(shift.getStartDate()), shift, activityWrapperMap, true, staffAdditionalInfoDTO);
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
            activityConfigurationService.addPlannedTimeInShift(shift, activityWrapperMap, staffAdditionalInfoDTO);
        }
        shiftMongoRepository.saveEntities(shifts);
        shifts.forEach(shift -> timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, false));
        todoService.createOrUpdateTodo(shifts.get(0), TodoType.APPROVAL_REQUIRED, staffAdditionalInfoDTO.getUserAccessRoleDTO(), isNull(shifts.get(0).getId()));
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
            Map<BigInteger, ActivityWrapper> activityWrapperMap = getActivityWrapperMap(isNotNull(oldShift) ? newArrayList(oldShift) : newArrayList(), shiftDTO);
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
            ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap);
            PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shiftDTO.getUnitId(), shiftDTO.getShiftDate());
            List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(shift.isShiftUpdated(isNotNull(oldShift)?oldShift:shift),shift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets());
            shift.setBreakActivities(breakActivities);
            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfo = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, oldShift, activityWrapperMap, isNotNull(shiftWithActivityDTO.getId()), isNull(shiftDTO.getShiftId()), shiftActionType);
            List<ShiftDTO> shiftDTOS = newArrayList(shiftDTO);
            if (isIgnoredAllRuletemplate(shiftWithViolatedInfo, updatedShiftWithViolatedInfo)) {
                if(updateWTACounterFlag && !ShiftActionType.SAVE_AS_DRAFT.equals(shiftActionType)){
                    shiftValidatorService.updateWTACounter(staffAdditionalInfoDTO, updatedShiftWithViolatedInfo, shift);
                    updateWTACounterFlag = false;
                }
                shift.setPlanningPeriodId(planningPeriod.getId());
                shift = saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, isNotNull(shift.getId()), functionId, phase, shiftActionType);
                if (isNotNull(todoType)) {
                    Todo todo = todoRepository.findByEntityIdAndType(shift.getId(), TodoType.REQUEST_ABSENCE);
                    todo.setStatus(TodoStatus.APPROVE);
                    todoRepository.save(todo);
                }
                todoService.createOrUpdateTodo(shift, TodoType.APPROVAL_REQUIRED, staffAdditionalInfoDTO.getUserAccessRoleDTO(), isNotNull(shiftDTO.getId()));
                shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
                if (isNull(shiftActionType) || ShiftActionType.SAVE.equals(shiftActionType)) {
                    payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
                    shiftDTO = timeBankService.updateTimebankDetailsInShiftDTO(newArrayList(shiftDTO)).get(0);
                }
                updateShiftViolatedOnIgnoreCounter(shift, shiftOverLappedWithNonWorkingTime, updatedShiftWithViolatedInfo);
                shiftReminderService.setReminderTrigger(activityWrapperMap, shift);
                if (updateShiftState) {
                    shiftDTO = shiftStateService.updateShiftStateAfterValidatingWtaRule(shiftDTO, shiftDTO.getId(), shiftDTO.getShiftStatePhaseId());
                } else if (isNotNull(validatedByStaff)) {
                    Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
                    shiftDTO = shiftValidatorService.validateShiftStateAfterValidatingWtaRule(shiftDTO, validatedByStaff, actualPhases);
                }
                shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(newArrayList(shiftDTO), staffAdditionalInfoDTO.getUserAccessRoleDTO());
            } else {
                shiftWithViolatedInfo = updatedShiftWithViolatedInfo;
            }

            shiftDTO=shiftDTOS.get(0);
            if(isNotNull(shiftDTO.getDraftShift())){
                shiftDTO.getDraftShift().setId(shift.getId());
            }responseShiftDTOS.addAll(Arrays.asList(isNotNull(shiftDTO.getDraftShift()) ? shiftDTO.getDraftShift() : shiftDTO));
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
        shiftViolatedRulesMongoRepository.save(shiftViolatedRules);
    }

    public Map<String, Object> saveAndCancelDraftShift(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, ViewType viewType,
                                                       ShiftFilterParam shiftFilterParam, ShiftActionType shiftActionType, StaffFilterDTO staffFilterDTO) {

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
                saveShiftViolatedRules.add(draftShiftViolatedRules.get(shiftViolatedRule.getShiftId()));
                deleteShiftViolatedRules.add(shiftViolatedRule);
            }
        }
        shiftViolatedRulesMongoRepository.saveEntities(saveShiftViolatedRules);
        shiftViolatedRulesMongoRepository.deleteAll(deleteShiftViolatedRules);
        return saveShifts;
    }

    public Map<BigInteger, ActivityWrapper> getActivityWrapperMap(List<Shift> shifts, ShiftDTO shiftDTO) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (Shift shift : shifts) {
            activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            activityIds.addAll(shift.getActivities().stream().map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
        }
        if (isNotNull(shiftDTO)) {
            activityIds.addAll(shiftDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getChildActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            activityIds.addAll(shiftDTO.getActivities().stream().map(shiftActivityDTO -> shiftActivityDTO.getActivityId()).collect(Collectors.toList()));
        }
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        return activityWrapperMap;
    }

    public ShiftWithViolatedInfoDTO updateShift(ShiftDTO shiftDTO, boolean byTAndAView, boolean validatedByPlanner, ShiftActionType shiftAction) {
        Long functionId = shiftDTO.getFunctionId();
        Shift shift = shiftMongoRepository.findByIdAndDeletedFalse(byTAndAView ? shiftDTO.getShiftId() : shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFT_ID, shiftDTO.getId());
        }
        if (shift.isDraft() && ShiftActionType.CANCEL.equals(shiftAction)) {
            shiftMongoRepository.delete(shift);
            return new ShiftWithViolatedInfoDTO();
        }

        boolean ruleCheckRequired = shift.isShiftUpdated(ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class));
        Date currentShiftStartDate = shift.getStartDate();
        Date currentShiftEndDate = shift.getEndDate();
        Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(shiftDTO.getShiftDate(), shiftDTO.getStaffId(), shiftDTO.getEmploymentId(), reasonCodeIds);
        if (staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement() && isNotNull(shift.getDraftShift()) && !byTAndAView) {
            shift = shift.getDraftShift();
        }
        Map<BigInteger, ActivityWrapper> activityWrapperMap = getActivityWrapperMap(newArrayList(shift), shiftDTO);
        boolean shiftOverlappedWithNonWorkingType = shiftValidatorService.validateStaffDetailsAndShiftOverlapping(staffAdditionalInfoDTO, shiftDTO, activityWrapperMap.get(shiftDTO.getActivities().get(0).getActivityId()), byTAndAView);
        updateCTADetailsOfEmployement(shiftDTO.getShiftDate(), staffAdditionalInfoDTO);
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        List<ShiftActivityDTO> shiftActivities = shiftValidatorService.findShiftActivityToValidateStaffingLevel(shift.getActivities(), shiftDTO.getActivities());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;

        ActivityWrapper absenceActivityWrapper = getAbsenceTypeOfActivityIfPresent(shiftDTO.getActivities(), activityWrapperMap);
        if (isNotNull(absenceActivityWrapper)) {
            shiftWithViolatedInfoDTO = updateAbsenceTypeShift(shiftDTO, shiftAction, shift, staffAdditionalInfoDTO, shiftOverlappedWithNonWorkingType, absenceActivityWrapper);
        } else {
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
            shiftDTO.setShiftType(ShiftType.PRESENCE);
            shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
            shift.setPhaseId(phase.getId());
            if (byTAndAView) {
                shift.setId(shiftDTO.getShiftId());
            }
            shift.setDraftShift(oldStateOfShift.getDraftShift());
            shift.setPlanningPeriodId(oldStateOfShift.getPlanningPeriodId());
            List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(ruleCheckRequired,shift, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets());
            shift.setBreakActivities(breakActivities);
            activityConfigurationService.addPlannedTimeInShift(shift, activityWrapperMap, staffAdditionalInfoDTO);
            ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class), activityWrapperMap);
            boolean valid = isNotNull(shiftAction) && !shiftAction.equals(ShiftActionType.CANCEL) && shift.getActivities().stream().anyMatch(activity -> !activity.getStatus().contains(ShiftStatus.PUBLISH)) && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement();
            shiftWithViolatedInfoDTO = validateRuleCheck(shiftDTO, shift, ruleCheckRequired, staffAdditionalInfoDTO, activityWrapperMap, phase, shiftActivities, shiftWithViolatedInfoDTO, valid, wtaQueryResultDTO, shiftWithActivityDTO, shiftAction, oldStateOfShift);
            List<ShiftDTO> shiftDTOS = newArrayList(shiftDTO);
            if (PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) || shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
                shift = saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, true, functionId, phase, shiftAction);
                wtaRuleTemplateCalculationService.updateWTACounter(shift, staffAdditionalInfoDTO);
                todoService.createOrUpdateTodo(shift, TodoType.APPROVAL_REQUIRED, staffAdditionalInfoDTO.getUserAccessRoleDTO(), true);
                shiftDTO = staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement() ? ObjectMapperUtils.copyPropertiesByMapper(isNotNull(shift.getDraftShift()) ? shift.getDraftShift() : shift, ShiftDTO.class) : ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
                payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
                //timeBankService.updateTimeBank(staffAdditionalInfoDTO,oldStateOfShift,validatedByPlanner);
                timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner);
                shiftDTO = timeBankService.updateTimebankDetailsInShiftDTO(newArrayList(shiftDTO)).get(0);
                if (ShiftActionType.SAVE.equals(shiftAction)) {
                    shiftStateService.createShiftState(Arrays.asList(shift), true, shift.getUnitId());
                }
                // TODO VIPUL WE WILL UNCOMMENTS AFTER FIX mailing servive
                //shiftReminderService.updateReminderTrigger(activityWrapperMap,shift);
                if (ruleCheckRequired) {
                    activityConfigurationService.addPlannedTimeInShift(shift, activityWrapperMap, staffAdditionalInfoDTO);
                    shiftValidatorService.validateShiftViolatedRules(shift, shiftOverlappedWithNonWorkingType, shiftWithViolatedInfoDTO, shiftAction);
                }
                shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(newArrayList(shiftDTO), staffAdditionalInfoDTO.getUserAccessRoleDTO());
            }
            shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        }
        addReasonCode(shiftWithViolatedInfoDTO.getShifts(), staffAdditionalInfoDTO.getReasonCodes());
        if (!shiftDTO.isDraft()) {
            if (byTAndAView) {
                shiftDTO.setId(shiftDTO.getShiftId());
            }
            shiftValidatorService.escalationCorrectionInShift(shiftDTO, currentShiftStartDate, currentShiftEndDate, shift);
        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftWithViolatedInfoDTO validateRuleCheck(ShiftDTO shiftDTO, Shift shift, boolean ruleCheckRequired, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, List<ShiftActivityDTO> shiftActivities, ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO,
                                                       boolean valid, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shiftWithActivityDTO, ShiftActionType shiftActionType, Shift oldStateShift) {
        if (ruleCheckRequired) {
            if (!valid) {
                shiftValidatorService.verifyRankAndStaffingLevel(shiftActivities, shiftDTO.getUnitId(), activityWrapperMap.values(), phase, staffAdditionalInfoDTO.getUserAccessRoleDTO());
                validateStaffingLevel(shift, staffAdditionalInfoDTO, activityWrapperMap, phase, shiftDTO, oldStateShift);
            }
            shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, shift, activityWrapperMap, true, false, shiftActionType);
        }
        if (shiftWithViolatedInfoDTO == null) {
            shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO(new ViolatedRulesDTO());
        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftWithViolatedInfoDTO updateAbsenceTypeShift(ShiftDTO shiftDTO, ShiftActionType shiftAction, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean shiftOverlappedWithNonWorkingType, ActivityWrapper absenceActivityWrapper) {
        boolean updatingSameActivity = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getActivityId().equals(absenceActivityWrapper.getActivity().getId())).findAny().isPresent();
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        if (!updatingSameActivity) {
            shiftWithViolatedInfoDTO = absenceShiftService.createAbsenceTypeShift(absenceActivityWrapper, shiftDTO, staffAdditionalInfoDTO, shiftOverlappedWithNonWorkingType, shiftAction);
        } else {
            List<ShiftDTO> shiftDTOs;
            shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
            if (CommonConstants.FULL_WEEK.equals(absenceActivityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
                List<Shift> shifts = getFullWeekShiftsByDate(shift.getStartDate(), shift.getEmploymentId(), absenceActivityWrapper.getActivity());
                shifts.forEach(shift1 -> shift1.getActivities().forEach(shiftActivity -> shiftActivity.setAbsenceReasonCodeId(shiftDTO.getActivities().get(0).getAbsenceReasonCodeId())));
                shiftMongoRepository.saveEntities(shifts);
                shiftDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
            } else {
                updateAbsenceReasonCode(shiftDTO, shift);
                shiftDTOs = newArrayList(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class));
            }
            shiftWithViolatedInfoDTO.setShifts(shiftDTOs);
        }
        return shiftWithViolatedInfoDTO;
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


    private Shift updateShiftAfterPublish(Shift shift, UserAccessRoleDTO userAccessRoleDTO, ShiftActionType shiftActionType) {
        Shift originalShift = shiftMongoRepository.findOne(shift.getId());
        boolean valid = shift.getActivities().stream().allMatch(activity -> activity.getStatus().contains(ShiftStatus.PUBLISH)) && userAccessRoleDTO.getManagement();
        if (valid && ShiftActionType.SAVE_AS_DRAFT.equals(shiftActionType)) {
            Shift draftShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
            draftShift.setPlannedMinutesOfTimebank(originalShift.getPlannedMinutesOfTimebank());
            draftShift.setTimeBankCtaBonusMinutes(originalShift.getTimeBankCtaBonusMinutes());
            draftShift.setScheduledMinutes(originalShift.getScheduledMinutes());
            originalShift.setDraftShift(draftShift);
            originalShift.getDraftShift().setDraft(true);
        } else if (valid && ShiftActionType.SAVE.equals(shiftActionType)) {
            originalShift = shift;
        } else {
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
        shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO);
        addReasonCode(shifts, reasonCodeDTOS);
        for (ShiftDTO shift : shifts) {
            for (ShiftActivityDTO activity : shift.getActivities()) {
                activity.setReasonCode(reasonCodeMap.get(activity.getAbsenceReasonCodeId()));
            }

        }
        UserAccessRoleDTO userAccessRoleDTO;
        if (isNotNull(staffAdditionalInfoDTO)) {
            shifts = timeBankService.updateTimebankDetailsInShiftDTO(shifts);
            userAccessRoleDTO = staffAdditionalInfoDTO.getUserAccessRoleDTO();

        } else {
            userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        }
        shifts = updateDraftShiftToShift(shifts, userAccessRoleDTO);
        shifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shifts, userAccessRoleDTO);
        Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        return new ShiftFunctionWrapper(shiftsMap, functionDTOMap);
    }

    public List<ShiftDTO> updateDraftShiftToShift(List<ShiftDTO> shifts, UserAccessRoleDTO userAccessRoleDTO) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        if (userAccessRoleDTO.getManagement()) {
            for (ShiftDTO shift : shifts) {
                if (isNotNull(shift.getDraftShift())) {
                    ShiftDTO shiftDTO = shift.getDraftShift();
                    shiftDTO.setDraft(true);
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
                    functionDTO.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
                    for (LocalDate date : appliedFunctionDTO.getAppliedDates()) {
                        functionDTOMap.put(date, Arrays.asList(functionDTO));
                    }
                }
            }
        }
    }


    public List<ShiftDTO> deleteAllLinkedShifts(BigInteger shiftId) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        Shift currentShift = shiftMongoRepository.findOne(shiftId);
        Activity activity = activityRepository.findOne(currentShift.getActivities().get(0).getActivityId());
        if (CommonConstants.FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
            List<Shift> shifts = getFullWeekShiftsByDate(currentShift.getStartDate(), currentShift.getEmploymentId(), activity);
            shifts.forEach(shift -> {
                shiftDTOS.add(deleteShift(shift.getId()));
            });
        } else {
            shiftDTOS.add(deleteShift(shiftId));
        }
        return shiftDTOS;
    }

    public List<Shift> getFullWeekShiftsByDate(Date shiftStartDate, Long employmentId, Activity activity) {
        ZonedDateTime startDate = asZoneDateTime(shiftStartDate).with(TemporalAdjusters.previousOrSame(activity.getTimeCalculationActivityTab().getFullWeekStart())).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = startDate.plusDays(7);

        return shiftMongoRepository.findShiftBetweenDurationByEmploymentId(employmentId,asDate(startDate),asDate(endDate));

    }

    public ShiftDTO deleteShift(BigInteger shiftId) {
        ShiftDTO shiftDTO = new ShiftDTO();
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFT_ID, shiftId);
        }
        shiftValidatorService.validateStatusOfShiftActivity(shift);
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
        List<BigInteger> activityIds = shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), shift.getEmploymentId(), Collections.emptySet());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), null);
        List<ShiftActivity>[] shiftActivities = shift.getShiftActivitiesForValidatingStaffingLevel(shift);
        for (ShiftActivity shiftActivity : shiftActivities[1]) {
            shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, staffAdditionalInfoDTO, shiftActivity);
        }
        //shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, staffAdditionalInfoDTO);
        Specification<BigInteger> activitySpecification = new ShiftAllowedToDelete(activityWrapper.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues(), staffAdditionalInfoDTO.getUserAccessRoleDTO());
        List<String> messages = activitySpecification.isSatisfiedString(phase.getId());
        if (!messages.isEmpty()) {
            exceptionService.actionNotPermittedException(messages.get(0));
        }
        shift.setDeleted(true);
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByStaffId(shift.getStaffId(), DateUtils.getStartOfDay(shift.getStartDate()), DateUtils.getEndOfDay(shift.getEndDate()));
        if (shifts.size() == 1 && CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getEmployment().getAppliedFunctions()) && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_DAY_CALCULATION)
                && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) {
            Long functionId = userIntegrationService.removeFunctionFromEmploymentByDate(shift.getUnitId(), shift.getEmploymentId(), shift.getStartDate());
            shiftDTO.setFunctionDeleted(true);
            shift.setFunctionId(functionId);

        }
        shiftMongoRepository.save(shift);
        wtaRuleTemplateCalculationService.updateWTACounter(shift, staffAdditionalInfoDTO);
        shiftDTO.setId(shiftId);
        shiftDTO.setStartDate(shift.getStartDate());
        shiftDTO.setEndDate(shift.getEndDate());
        shiftDTO.setUnitId(shift.getUnitId());
        shiftDTO.setDeleted(true);
        shiftDTO.setActivities(ObjectMapperUtils.copyPropertiesOfListByMapper(shift.getActivities(), ShiftActivityDTO.class));
        //shiftValidatorService.escalationCorrectionInShift(shiftDTO, shift.getStartDate(), shift.getEndDate());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        todoService.deleteTodo(shiftId, null);
        timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift, false);
        payOutService.deletePayOut(shift.getId());
        List<BigInteger> jobIds = shift.getActivities().stream().map(ShiftActivity::getId).collect(Collectors.toList());
        shiftReminderService.deleteReminderTrigger(jobIds, shift.getUnitId());
        return shiftDTO;

    }

    private void validateStaffingLevel(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, ShiftDTO shiftDTO, Shift oldStateShift) {
        List<ShiftActivity>[] shiftActivities = oldStateShift.getShiftActivitiesForValidatingStaffingLevel(shift);
        for (ShiftActivity shiftActivity : shiftActivities[0]) {
            shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, true, staffAdditionalInfoDTO, shiftActivity);
        }
        for (ShiftActivity shiftActivity : shiftActivities[1]) {
            shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, staffAdditionalInfoDTO, shiftActivity);
        }
//        shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, staffAdditionalInfoDTO);
//        shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, true, staffAdditionalInfoDTO);
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
        assignedShifts = shiftFilterService.getShiftsByFilters(assignedShifts, staffFilterDTO);
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        assignedShifts = updateDraftShiftToShift(assignedShifts, userAccessRoleDTO);
        Map<Long, List<ShiftDTO>> employmentIdAndShiftsMap = assignedShifts.stream().collect(Collectors.groupingBy(ShiftDTO::getEmploymentId, Collectors.toList()));
        assignedShifts = new ArrayList<>(assignedShifts.size());
        for (Map.Entry<Long, List<ShiftDTO>> employmentIdAndShiftEntry : employmentIdAndShiftsMap.entrySet()) {
            List<ShiftDTO> shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(employmentIdAndShiftEntry.getValue(), userAccessRoleDTO);
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
            roles.add(AccessGroupRole.MANAGEMENT);
        }
        if (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getStaff()) {
            roles.add(AccessGroupRole.STAFF);
        }
        StaffAccessRoleDTO staffAccessRoleDTO = new StaffAccessRoleDTO(userAccessRoleDTO.getStaffId(), roles);
        Map<LocalDate, List<FunctionDTO>> appliedFunctionDTOs = userIntegrationService.getFunctionsOfEmployment(unitId, startLocalDate, endLocalDate);

        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO, buttonConfig, appliedFunctionDTOs);
    }

    public ButtonConfig findButtonConfig(List<ShiftDTO> shifts, boolean management) {
        ButtonConfig buttonConfig = new ButtonConfig();
        if (management && isCollectionNotEmpty(shifts)) {
            Set<BigInteger> shiftIds = shifts.stream().map(ShiftDTO::getId).collect(Collectors.toSet());
            List<ShiftState> shiftStates = shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds, AccessGroupRole.MANAGEMENT);
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
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        List<ShiftDTO> shiftDTOS = shiftMongoRepository.getAllShiftBetweenDuration(employmentId, staffId, asDate(startDate), asDate(endDate), unitId);
        shiftDTOS = shiftFilterService.getShiftsByFilters(shiftDTOS, staffFilterDTO);
        return wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS, userAccessRoleDTO);
    }

    public ShiftWithActivityDTO buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ShiftDTO shiftDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftWithActivityDTO.class);
        shiftDTO.getActivities().forEach(shiftActivityDTO ->
                shiftActivityDTO.setActivityName(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getName())
        );
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
            shiftActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class));
            shiftActivityDTO.setTimeType(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getTimeType());
            shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> {
                childActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class));
                childActivityDTO.setTimeType(activityWrapperMap.get(childActivityDTO.getActivityId()).getTimeType());
            });
        });
        shiftWithActivityDTO.setStartDate(shiftDTO.getActivities().get(0).getStartDate());
        shiftWithActivityDTO.setEndDate(shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        return shiftWithActivityDTO;
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

    public void deleteShiftsAfterEmploymentEndDate(Long staffId, Long unitId, LocalDate employmentEndDate) {
        shiftMongoRepository.deleteShiftsAfterDate(staffId, employmentEndDate.atStartOfDay());
    }

    //TODO need to optimize this method
    public ShiftWithViolatedInfoDTO updateShiftByTandA(Long unitId, ShiftDTO shiftDTO,
                                                       Boolean updatedByStaff) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.REALTIME.toString());
        if (!userAccessRoleDTO.getStaff() && updatedByStaff && !shiftDTO.getShiftStatePhaseId().equals(phase.getId())) {
            exceptionService.actionNotPermittedException("message.shift.save.access");
        } else if (!userAccessRoleDTO.getManagement() && !updatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.save.access");
        }
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
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = updateShift(shiftDTO, true, false, null);
        shiftDTO = shiftStateService.updateShiftStateAfterValidatingWtaRule(shiftWithViolatedInfoDTO.getShifts().get(0), shiftStateId, shiftStatePhaseId);
        List<ShiftDTO> shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(newArrayList(shiftDTO), userAccessRoleDTO);
        shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        shiftWithViolatedInfoDTO.getShifts().get(0).setEditable(true);
        shiftWithViolatedInfoDTO.getShifts().get(0).setDurationMinutes((int) shiftWithViolatedInfoDTO.getShifts().get(0).getInterval().getMinutes());
        return shiftWithViolatedInfoDTO;
    }


    private ShiftDetailViewDTO getShiftDetailsOfStaff(UserAccessRoleDTO userAccessRoleDTO, String timeZone, Map<String, Phase> phaseMap, List<Shift> shifts, List<ShiftState> shiftStatesList, StaffFilterDTO staffFilterDTO) {
        List<ShiftDTO> plannedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        if (isCollectionEmpty(plannedShifts)) {
            plannedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
        }
        plannedShifts = shiftFilterService.getShiftsByFilters(plannedShifts, staffFilterDTO);
        plannedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(plannedShifts, userAccessRoleDTO);
        List<ShiftDTO> realTimeShift = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        realTimeShift = shiftFilterService.getShiftsByFilters(realTimeShift, staffFilterDTO);
        List<ShiftDTO> shiftStateDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList, ShiftDTO.class);
        shiftStateDTOs = shiftFilterService.getShiftsByFilters(shiftStateDTOs, staffFilterDTO);
        List<ShiftDTO> staffValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(AccessGroupRole.STAFF) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList());
        staffValidatedShifts = shiftFilterService.getShiftsByFilters(staffValidatedShifts, staffFilterDTO);
        Map<String, ShiftDTO> staffAndShiftMap = staffValidatedShifts.stream().collect(Collectors.toMap(k -> k.getStaffId() + "" + k.getId(), v -> v));
        DateTimeInterval graceInterval;
        List<ShiftDTO> updateRealTime = new ArrayList<>();
        for (ShiftDTO shiftDTO : realTimeShift) {
            if (!Optional.ofNullable(staffAndShiftMap.get(shiftDTO.getStaffId() + "" + shiftDTO.getId())).isPresent() && shiftDTO.getValidated() == null && phaseService.shiftEditableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate())) {
                shiftDTO.setEditable(true);
            }
            updateRealTime.add(shiftDTO);
        }
        updateRealTime = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(updateRealTime, userAccessRoleDTO);
        if (!staffValidatedShifts.isEmpty()) {
            Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(staffValidatedShifts.get(0).getUnitId(), PhaseDefaultName.TIME_ATTENDANCE.toString());
            graceInterval = shiftValidatorService.getGracePeriodInterval(phase, staffValidatedShifts.get(0).getStartDate(), false);
            for (ShiftDTO staffValidatedShift : staffValidatedShifts) {
                if (staffValidatedShift.getValidated() == null && graceInterval.contains(staffValidatedShift.getStartDate())) {
                    staffValidatedShift.setEditable(true);
                }
            }
        }
        staffValidatedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(staffValidatedShifts, userAccessRoleDTO);
        List<ShiftDTO> plannerValidatedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(AccessGroupRole.MANAGEMENT) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        plannerValidatedShifts = shiftFilterService.getShiftsByFilters(plannerValidatedShifts, staffFilterDTO);
        //change id because id was same and issue on FE side and this is only for show FE side
        for (ShiftDTO shiftDTO : plannerValidatedShifts) {
            if (shiftDTO.getValidated() == null) {
                shiftDTO.setEditable(true);
            }
        }
        plannerValidatedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(plannerValidatedShifts, userAccessRoleDTO);
        return new ShiftDetailViewDTO(plannedShifts, updateRealTime, staffValidatedShifts, plannerValidatedShifts);
    }

    public ShiftWithActivityDTO convertIntoShiftWithActivity(Shift sourceShift, Map<BigInteger, ActivityWrapper> activityMap) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
        shiftWithActivityDTO.getActivities().forEach(s -> {
            ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityMap.get(s.getActivityId()).getActivity(), ActivityDTO.class);
            s.setActivity(activityDTO);
        });
        return shiftWithActivityDTO;
    }

    public CompactViewDTO getDetailedAndCompactViewData(Long selectedStaffId, Long unitId, Date shiftStartDate, StaffFilterDTO staffFilterDTO) {
        List<Long> staffIds;
        if (isNull(selectedStaffId)) {
            List<StaffDTO> staffResponseDTOS = userIntegrationService.getStaffListByUnit();
            staffIds = staffResponseDTOS.stream().map(StaffDTO::getId).collect(Collectors.toList());
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
        List<ShiftState> realTimeShift = shiftStates.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList());
        List<ShiftState> realTimeShiftStatesList = shiftStateService.checkAndCreateRealtimeAndDraftState(shifts, realTimeShift, phaseMap);
        shiftStates.addAll(realTimeShiftStatesList);
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.TIME_TYPE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = userIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        Map<Long, List<Shift>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        Map<Long, List<ShiftState>> shiftStateMap = shiftStates.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        List<DetailViewDTO> shiftDetailViewDTOMap = staffIds.stream().map(staffId -> new DetailViewDTO(staffId, getShiftDetailsOfStaff(userAccessRoleDTO, timeZone, phaseMap, shiftsMap.getOrDefault(staffId, new ArrayList<>()), shiftStateMap.getOrDefault(staffId, new ArrayList<>()), staffFilterDTO), staffsTimeAndAttendance.getOrDefault(staffId, new ArrayList<>()))).collect(Collectors.toList());
        Map<LocalDate, List<FunctionDTO>>  functionDTOMap = userIntegrationService.getFunctionsOfEmployment(unitId, asLocalDate(shiftStartDate), asLocalDate(endDate));
        return new CompactViewDTO(shiftDetailViewDTOMap, reasonCodeDTOS,functionDTOMap);
    }

    /**
     * @Desc used to filter the ShiftActivities for Add , Edit and Delete
     */
    private ShiftActivityIdsDTO getActivitiesToProcess(List<ShiftActivity> existingShiftActivities, List<ShiftActivityDTO> arrivedShiftActivities) {
        Set<BigInteger> allExistingShiftActivities = existingShiftActivities.stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet());
        Set<BigInteger> allArrivedShiftActivities = arrivedShiftActivities.stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
        Map<BigInteger, ShiftActivity> existingShiftActivityMap = existingShiftActivities.stream().collect(Collectors.toMap(ShiftActivity::getActivityId, Function.identity(), (previous, current) -> previous));
        Set<BigInteger> activitiesToEdit = new HashSet<>();
        Set<BigInteger> activitiesToAdd = new HashSet<>();
        Set<BigInteger> activitiesToDelete = new HashSet<>();
        for (ShiftActivityDTO shiftActivity : arrivedShiftActivities) {
            if (allExistingShiftActivities.contains(shiftActivity.getActivityId())) {
                ShiftActivity existingActivity = existingShiftActivityMap.get(shiftActivity.getActivityId());
                if (!shiftActivity.getStartDate().equals(existingActivity.getStartDate()) || !shiftActivity.getEndDate().equals(existingActivity.getEndDate())) {
                    activitiesToEdit.add(shiftActivity.getActivityId());
                }
            } else {
                activitiesToAdd.add(shiftActivity.getActivityId());
            }
        }
        for (BigInteger current : allExistingShiftActivities) {
            if (!allArrivedShiftActivities.contains(current)) {
                activitiesToDelete.add(current);
            }
        }
        return new ShiftActivityIdsDTO(activitiesToAdd, activitiesToEdit, activitiesToDelete);
    }

    //Description this method will fetch all shifts / open shifts and shift states based on the above request param
    public Object getAllShiftAndStates(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, ViewType viewType,
                                       ShiftFilterParam shiftFilterParam, Long expertiseId, StaffFilterDTO staffFilterDTO) {
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
}
