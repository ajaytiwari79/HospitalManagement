package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.todo.TodoService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.DateUtils.getEmailDateTimeWithFormat;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.MAIL_SUBJECT;
import static com.kairos.constants.CommonConstants.EMAIL_GREETING;
import static com.kairos.constants.CommonConstants.SHIFT_NOTIFICATION_EMAIL_TEMPLATE;
import static com.kairos.enums.shift.ShiftStatus.*;

@Service
public class ShiftStatusService {


    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private LocaleService localeService;
    @Inject
    private SendGridMailService sendGridMailService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject private TodoRepository todoRepository;
    @Inject private TodoService todoService;

    public ShiftAndActivtyStatusDTO updateStatusOfShifts(Long unitId, ShiftPublishDTO shiftPublishDTO) {
        if (isCollectionEmpty(shiftPublishDTO.getShifts())) {
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_IDS);
        }
        Shift currentShift = shiftMongoRepository.findOne(shiftPublishDTO.getShifts().get(0).getShiftId());
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>();
        ShiftAndActivtyStatusDTO shiftAndActivtyStatusDTO=null;
        if(isNotNull(currentShift.getRequestAbsence())){
            return updateStatusOfRequestAbsence(unitId, shiftPublishDTO, currentShift);
        }
            Activity activity = activityMongoRepository.findOne(currentShift.getActivities().get(0).getActivityId());
            if (CommonConstants.FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
                List<Shift> shifts = shiftService.getFullWeekShiftsByDate(currentShift.getStartDate(), currentShift.getEmploymentId(), activity);
                shiftPublishDTO.getShifts().clear();
                shifts.forEach(shift -> shiftPublishDTO.getShifts().add(new ShiftActivitiesIdDTO(shift.getId(), shift.getActivities().stream().map(shiftActivityDTO -> shiftActivityDTO.getId()).collect(Collectors.toList()))));
            }
            Object[] objects = getActivitiesAndShiftIds(shiftPublishDTO.getShifts());
            Set<BigInteger> shiftActivitiyIds = ((Set<BigInteger>) objects[1]);
            List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
            if (isCollectionEmpty(shifts)) {
                exceptionService.dataNotFoundException(MESSAGE_SHIFT_IDS);
            }

            List<ShiftDTO> shiftDTOS = new ArrayList<>(shifts.size());
            Object[] activityDetails = getActivityDetailsMap(shifts);
            Map<BigInteger, ActivityPhaseSettings> activityPhaseSettingMap = (Map<BigInteger, ActivityPhaseSettings>) activityDetails[0];
            Map<BigInteger, Activity> activityIdAndActivityMap = (Map<BigInteger, Activity>) activityDetails[1];
            List<NameValuePair> requestParam = new ArrayList<>();
            requestParam.add(new BasicNameValuePair("staffIds", activityDetails[2].toString()));
            requestParam.add(new BasicNameValuePair("employmentIds", activityDetails[3].toString()));
            List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(shifts.get(0).getUnitId(), requestParam);
            Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap = staffAdditionalInfoDTOS.stream().filter(distinctByKey(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId())).collect(Collectors.toMap(s -> s.getEmployment().getId(), v -> v));
            if (isCollectionNotEmpty(shifts) && objects[1] != null) {
                Set<LocalDateTime> dates = shifts.stream().flatMap(shift -> shift.getActivities().stream()).map(shiftActivity->DateUtils.asLocalDateTime(shiftActivity.getStartDate())).collect(Collectors.toSet());
                Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
                StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(unitId);
                for (Shift shift : shifts) {
                    List<ShiftActivity> oldActivity = new CopyOnWriteArrayList<>(shift.getActivities());
                    for (ShiftActivity shiftActivity : oldActivity) {
                        updateStatusOfShiftActivity(unitId, shiftPublishDTO, shiftActivitiyIds, shiftActivityResponseDTOS, activityPhaseSettingMap, activityIdAndActivityMap, phaseListByDate, staffAccessGroupDTO, shift, shiftActivity, staffAdditionalInfoMap);
                        for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                            updateStatusOfShiftActivity(unitId, shiftPublishDTO, shiftActivitiyIds, shiftActivityResponseDTOS, activityPhaseSettingMap, activityIdAndActivityMap, phaseListByDate, staffAccessGroupDTO, shift, childActivity, staffAdditionalInfoMap);
                        }
                    }
                    if (shift.isDeleted()) {
                        shiftDTOS.addAll(shiftService.deleteAllLinkedShifts(shift.getId()).getShifts());
                    } else {
                        shiftDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class));
                    }

                }
                shiftMongoRepository.saveEntities(shifts);
                timeBankService.updateDailyTimeBankEntriesForStaffs(shifts, null);

            wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS);
            if(isNotNull(shiftAndActivtyStatusDTO)){
                shiftDTOS.add(shiftAndActivtyStatusDTO.getShifts().get(0));
                shiftActivityResponseDTOS.add(shiftAndActivtyStatusDTO.getShiftActivityStatusResponse().get(0));
            }
        }

        return new ShiftAndActivtyStatusDTO(shiftDTOS, shiftActivityResponseDTOS);
    }

    private ShiftAndActivtyStatusDTO updateStatusOfRequestAbsence(Long unitId, ShiftPublishDTO shiftPublishDTO, Shift currentShift) {
        Activity activity = activityMongoRepository.findOne(currentShift.getRequestAbsence().getActivityId());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, currentShift.getStartDate(),currentShift.getEndDate());
        PhaseTemplateValue phaseTemplateValue = activity.getActivityPhaseSettings().getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
        StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(unitId);
        ActivityShiftStatusSettings activityShiftStatusSettings = getActivityShiftStatusSettingByStatus(phaseTemplateValue, shiftPublishDTO.getStatus());
        String staffAccessRole = UserContext.getUserDetails().getUnitWiseAccessRole().get(unitId.toString());
        boolean validAccessGroup = shiftValidatorService.validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
        Set<String> accessRoles =activityShiftStatusSettings==null?new HashSet<>(): userIntegrationService.getAccessRolesByAccessGroupIds(unitId,activityShiftStatusSettings.getAccessGroupIds());
        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(currentShift.getId());
        List<TodoStatus> todoStatuses = newArrayList(TodoStatus.REQUESTED,TodoStatus.PENDING);
        if(todoStatuses.contains(currentShift.getRequestAbsence().getTodoStatus()) && validAccessGroup && accessRoles.contains(staffAccessRole)){
            todoService.updateTodoStatus(null, currentShift.getRequestAbsence().getTodoStatus(),shiftPublishDTO.getShifts().get(0).getShiftId(),null);
            ShiftActivityDTO shiftActivityDTO = new ShiftActivityDTO(currentShift.getRequestAbsence().getActivityName(), null, localeService.getMessage(MESSAGE_SHIFT_STATUS_ADDED), true, newHashSet(shiftPublishDTO.getStatus()));
            shiftActivityDTO.setId(currentShift.getId());
            shiftActivityResponseDTO.getActivities().add(shiftActivityDTO);
        }else if(!accessRoles.contains(staffAccessRole) || !validAccessGroup){
            ShiftActivityDTO shiftActivityDTO = new ShiftActivityDTO(currentShift.getRequestAbsence().getActivityName(), currentShift.getStartDate(), currentShift.getEndDate(), currentShift.getId(), localeService.getMessage(ACCESS_GROUP_NOT_MATCHED)+" to " +shiftPublishDTO.getStatus()+" Request Absence" , false);
            shiftActivityDTO.setId(currentShift.getId());
            shiftActivityResponseDTO.getActivities().add(shiftActivityDTO);
        }else {
            ShiftActivityDTO shiftActivityDTO = new ShiftActivityDTO(currentShift.getRequestAbsence().getActivityName(), currentShift.getStartDate(), currentShift.getEndDate(), currentShift.getId(), localeService.getMessage(ACTIVITY_STATUS_INVALID) + " to " +shiftPublishDTO.getStatus()+" Request Absence", false);
            shiftActivityDTO.setId(currentShift.getId());
            shiftActivityResponseDTO.getActivities().add(shiftActivityDTO);
        }
        return new ShiftAndActivtyStatusDTO(newArrayList(ObjectMapperUtils.copyPropertiesByMapper(currentShift,ShiftDTO.class)), newArrayList(shiftActivityResponseDTO));
    }

    private Object[] getActivityDetailsMap(List<Shift> shifts){
        Set<BigInteger> activityIds = new HashSet<>();
        Set<Long> staffIds = new HashSet<>();
        Set<Long> employmentIds = new HashSet<>();
        for (Shift shift : shifts) {
            activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            activityIds.addAll(shift.getActivities().stream().map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            staffIds.add(shift.getStaffId());
            if(shift.getEmploymentId()!=null)
            employmentIds.add(shift.getEmploymentId());
            if(isNotNull(shift.getRequestAbsence())){
                activityIds.add(shift.getRequestAbsence().getActivityId());
            }
        }
        List<Activity> activities = activityMongoRepository.findAllPhaseSettingsByActivityIds(activityIds);
        Map<BigInteger, ActivityPhaseSettings> activityPhaseSettingMap = activities.stream().collect(Collectors.toMap(Activity::getId, Activity::getActivityPhaseSettings));
        Map<BigInteger, Activity> activityIdAndActivityMap = activities.stream().collect(Collectors.toMap(Activity::getId, Function.identity()));
        return new Object[]{activityPhaseSettingMap,activityIdAndActivityMap,staffIds,employmentIds};
    }

    private void updateStatusOfShiftActivity(Long unitId, ShiftPublishDTO shiftPublishDTO, Set<BigInteger> shiftActivitiyIds, List<ShiftActivityResponseDTO> shiftActivityResponseDTOS, Map<BigInteger, ActivityPhaseSettings> activityPhaseSettingMap, Map<BigInteger, Activity> activityIdAndActivityMap, Map<Date, Phase> phaseListByDate, StaffAccessGroupDTO staffAccessGroupDTO, Shift shift, ShiftActivity shiftActivity, Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap) {
       if (shiftActivitiyIds.contains(shiftActivity.getId())) {
            Phase phase = phaseListByDate.get(shift.getActivities().get(0).getStartDate());
            ActivityPhaseSettings activityPhaseSettings = activityPhaseSettingMap.get(shiftActivity.getActivityId());
            PhaseTemplateValue phaseTemplateValue = activityPhaseSettings.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
            ActivityShiftStatusSettings activityShiftStatusSettings = getActivityShiftStatusSettingByStatus(phaseTemplateValue, shiftPublishDTO.getStatus());
            ShiftActivityResponseDTO shiftActivityResponseDTO = getShiftActivityResponseDTO(unitId,shiftPublishDTO, activityIdAndActivityMap, staffAccessGroupDTO, shift, shiftActivity, activityShiftStatusSettings,staffAdditionalInfoMap);
            shiftActivityResponseDTOS.add(shiftActivityResponseDTO);
        }
    }

    private ShiftActivityResponseDTO getShiftActivityResponseDTO(Long unitId,ShiftPublishDTO shiftPublishDTO, Map<BigInteger, Activity> activityIdAndActivityMap, StaffAccessGroupDTO staffAccessGroupDTO, Shift shift, ShiftActivity shiftActivity, ActivityShiftStatusSettings activityShiftStatusSettings,Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap) {
        String staffAccessRole = UserContext.getUserDetails().getUnitWiseAccessRole().get(unitId.toString());
        boolean validAccessGroup = shiftValidatorService.validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
        Set<String> accessRoles =activityShiftStatusSettings==null?new HashSet<>(): userIntegrationService.getAccessRolesByAccessGroupIds(unitId,activityShiftStatusSettings.getAccessGroupIds());
        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
        boolean validateShiftActivityStatus = validateShiftActivityStatus(shiftPublishDTO.getStatus(), shiftActivity, activityIdAndActivityMap.get(shiftActivity.getActivityId()));
        boolean draftShift=false;
        if(FIX.equals(shiftPublishDTO.getStatus())){
            draftShift=shift.isDraft();
            shift.setDraftShift(null);
        }
        if (validAccessGroup && validateShiftActivityStatus && !draftShift && accessRoles.contains(staffAccessRole)) {
            removeOppositeStatus(shift, shiftActivity, shiftPublishDTO.getStatus(),activityIdAndActivityMap,staffAdditionalInfoMap,shiftPublishDTO.getComment());
            Set<ShiftStatus> shiftStatuses=new HashSet<>();
            shiftStatuses.addAll(shiftActivity.getStatus());
            shiftStatuses.add(shiftPublishDTO.getStatus());
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage(MESSAGE_SHIFT_STATUS_ADDED), true, shiftStatuses));
        } else if (validAccessGroup && !validateShiftActivityStatus) {
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(),shiftActivity.getStartDate(), shiftActivity.getEndDate(), shiftActivity.getId(), localeService.getMessage(ACTIVITY_STATUS_INVALID), false));
        } else {
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(),shiftActivity.getStartDate(), shiftActivity.getEndDate(), shiftActivity.getId(), localeService.getMessage(ACCESS_GROUP_NOT_MATCHED), false));
        }
        if(draftShift){
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(),shiftActivity.getStartDate(), shiftActivity.getEndDate(), shiftActivity.getId(), localeService.getMessage(STATUS_NOT_ALLOWED), false));
        }
        return shiftActivityResponseDTO;
    }

    private Object[] getActivitiesAndShiftIds(List<ShiftActivitiesIdDTO> shifts) {
        List<BigInteger> shiftIds = new ArrayList<>();
        Set<BigInteger> activitiesIds = new HashSet<>();
        shifts.forEach(s -> {
            shiftIds.add(s.getShiftId());
            activitiesIds.addAll(s.getActivityIds());
        });
        return new Object[]{shiftIds, activitiesIds};
    }

    private boolean validateShiftActivityStatus(ShiftStatus shiftStatus, ShiftActivity shiftActivity, Activity activity) {
        boolean valid;
        if (isCollectionEmpty(activity.getActivityRulesSettings().getApprovalAllowedPhaseIds()) && (shiftStatus.equals(ShiftStatus.FIX) || shiftStatus.equals(ShiftStatus.UNFIX) || shiftStatus.equals(ShiftStatus.PUBLISH))) {
            valid = true;
        } else {
            valid = activityStatusIsValid(shiftStatus, shiftActivity);
        }
        return valid;
    }

    private boolean activityStatusIsValid(ShiftStatus shiftStatus, ShiftActivity shiftActivity) {
        return getValidShiftStatus(shiftActivity.getStatus()).contains(shiftStatus);
    }

    private Set<ShiftStatus> getValidShiftStatus(Set<ShiftStatus> shiftStatusSet) {
        Set<ShiftStatus> shiftStatuses;
        if (shiftStatusSet.contains(ShiftStatus.REQUEST)) {
            shiftStatuses = newHashSet(ShiftStatus.PENDING, APPROVE, DISAPPROVE);
        } else if (shiftStatusSet.contains(ShiftStatus.PENDING)) {
            shiftStatuses = newHashSet(APPROVE, DISAPPROVE);
        } else if (shiftStatusSet.contains(ShiftStatus.APPROVE) || shiftStatusSet.contains(ShiftStatus.FIX)) {
            shiftStatuses = newHashSet(FIX, UNFIX, PUBLISH);
        } else {
            shiftStatuses = newHashSet(FIX, PUBLISH);
        }
        return shiftStatuses;
    }

    private void removeOppositeStatus(Shift shift, ShiftActivity shiftActivity, ShiftStatus shiftStatus,Map<BigInteger, Activity> activityIdAndActivityMap,Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap, String comment) {
        Todo todo = null;

        if(newHashSet(PENDING,APPROVE,DISAPPROVE).contains(shiftStatus)){
            TodoStatus todoStatus =null;
            if(shiftStatus.equals(PENDING)){
                todoStatus =TodoStatus.PENDING;
            }else {
                todoStatus = shiftStatus.equals(APPROVE) ? TodoStatus.APPROVE : TodoStatus.DISAPPROVE;
            }
            todo = todoRepository.findAllByEntityIdAndSubEntityAndTypeAndStatus(shift.getId(), TodoType.APPROVAL_REQUIRED,newHashSet(TodoStatus.PENDING,TodoStatus.VIEWED,TodoStatus.REQUESTED),shiftActivity.getActivityId());
            if(isNotNull(todo)) {
                todo.setStatus(todoStatus);
                todo.setComment(comment);
                if(TodoStatus.APPROVE.equals(todo.getStatus())){
                    todo.setApprovedOn(getDate());
                }else if(TodoStatus.DISAPPROVE.equals(todo.getStatus())){
                    todo.setDisApproveOn(getDate());
                }else if(TodoStatus.PENDING.equals(todo.getStatus())){
                    todo.setPendingOn(getDate());
                }
                todoRepository.save(todo);
            }
        }
        switch (shiftStatus) {
            case LOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(UNLOCK, REQUEST));
                shiftActivity.getStatus().add(LOCK);
                break;
            case FIX:
                shiftActivity.getStatus().add(FIX);
                sendMailToStaffWhenStatusChange(shift, shiftActivity.getActivityName(), shiftStatus, null);
                break;
            case UNFIX:
                shiftActivity.getStatus().removeAll(Arrays.asList(FIX));
                sendMailToStaffWhenStatusChange(shift, shiftActivity.getActivityName(), shiftStatus, null);
                break;
            case APPROVE:
                shiftActivity.getStatus().removeAll(Arrays.asList(PENDING, REQUEST));
                    shiftActivity.getStatus().add(APPROVE);
                //timeBankService.updateTimeBanOnApproveTimebankOFF(shiftActivity,shift.getEmploymentId(),activityIdAndActivityMap,staffAdditionalInfoMap.get(shift.getEmploymentId()));
                break;
            case DISAPPROVE:
                updateShiftOnDisapprove(shift, shiftActivity);
                sendMailToStaffWhenStatusChange(shift, shiftActivity.getActivityName(), shiftStatus, isNotNull(todo) ? todo.getComment() : null);
                break;
            case UNLOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(LOCK, REQUEST));
                break;
            case PUBLISH:
                shiftActivity.getStatus().add(PUBLISH);
                shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST));
               // timeBankService.updateTimeBanOnApproveTimebankOFF(shiftActivity,shift.getEmploymentId(),activityIdAndActivityMap,staffAdditionalInfoMap.get(shift.getEmploymentId()));
                break;
            case VALIDATE:
                shiftActivity.getStatus().add(VALIDATE);
                break;
            case PENDING:
                shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST));
                shiftActivity.getStatus().add(PENDING);
                sendMailToStaffWhenStatusChange(shift, shiftActivity.getActivityName(), shiftStatus, null);
                break;
            default:
                break;
        }
    }

    private void updateShiftOnDisapprove(Shift shift, ShiftActivity shiftActivity) {
        if (shift.getActivities().size() > 1) {
            int indexOfShiftActivity = shift.getActivities().indexOf(shiftActivity);
            if(indexOfShiftActivity==0 || indexOfShiftActivity==(shift.getActivities().size()-1)){
                boolean removed = shift.getActivities().remove(shiftActivity);
                removeShiftAcivityFromChildActivities(shift, shiftActivity,removed);
            }
            else {
                if(indexOfShiftActivity>0){
                    boolean removed = shift.getActivities().remove(shiftActivity);
                    if(removed){
                        ShiftActivity firstActivity = shift.getActivities().get(0);
                        if(firstActivity.getEndDate().equals(shiftActivity.getStartDate())){
                            firstActivity.setEndDate(shiftActivity.getEndDate());
                        }else {
                            shiftActivity.setActivityId(firstActivity.getActivityId());
                            shift.getActivities().add(shiftActivity);
                        }
                    }
                    removeShiftAcivityFromChildActivities(shift, shiftActivity,removed);
                }

            }
        }else {
            /*boolean removed = shift.getActivities().get(0).setStatus(newHashSet(DISAPPROVE));
            if(removed) {

            }*/
            shift.getActivities().get(0).setStatus(newHashSet(DISAPPROVE));
            shift.setDeleted(true);
            //removeShiftAcivityFromChildActivities(shift,shiftActivity,removed);
        }
    }

    private void removeShiftAcivityFromChildActivities(Shift shift, ShiftActivity shiftActivity,boolean removed) {
        if(!removed){
            for (ShiftActivity activity : shift.getActivities()) {
                removed = activity.getChildActivities().remove(shiftActivity);
                if(removed){
                    break;
                }
            }
        }
    }

    public ActivityShiftStatusSettings getActivityShiftStatusSettingByStatus(PhaseTemplateValue phaseTemplateValue, ShiftStatus status) {
        ActivityShiftStatusSettings activityShiftStatusSettings = null;
        for (ActivityShiftStatusSettings statusSettings : phaseTemplateValue.getActivityShiftStatusSettings()) {
            if (status.equals(statusSettings.getShiftStatus())) {
                activityShiftStatusSettings = statusSettings;
                break;
            }
        }
        return activityShiftStatusSettings;
    }

    public void updateStatusOfShiftIfPhaseValid(PlanningPeriod planningPeriod, Phase phase, Shift mainShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        for (ShiftActivity shiftActivity : mainShift.getActivities()) {
            if (planningPeriod.getPublishEmploymentIds().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId())) {
                if(!activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().getApprovalAllowedPhaseIds().contains(phase.getId())||(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().getApprovalAllowedPhaseIds().contains(phase.getId())&&UserContext.getUserDetails().isStaff())) {
                    shiftActivity.getStatus().add(PUBLISH);
                }else {
                    if(shiftActivity.getStatus().contains(REQUEST)) {
                        shiftActivity.setStatus(newHashSet(APPROVE,PUBLISH));
                    }
                }
            } else if (isCollectionNotEmpty(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().getApprovalAllowedPhaseIds()) && isCollectionEmpty(shiftActivity.getStatus())) {
                if (activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getActivityRulesSettings().getApprovalAllowedPhaseIds().contains(phase.getId())) {
                    shiftActivity.getStatus().add(UserContext.getUserDetails().isManagement() ? ShiftStatus.APPROVE : REQUEST);
                }
            }else if(shiftActivity.getStatus().contains(REQUEST)&&UserContext.getUserDetails().isManagement()){
                shiftActivity.setStatus(newHashSet(APPROVE));
            } else if(shiftActivity.getStatus().contains(APPROVE)){
                ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
                Map<BigInteger,Activity> activityMap = new HashMap<>();
                activityMap.put(activityWrapper.getActivity().getId(),activityWrapper.getActivity());
                //timeBankService.updateTimeBanOnApproveTimebankOFF(shiftActivity,mainShift.getEmploymentId(),activityMap,staffAdditionalInfoDTO);
            }
        }
    }


    public void sendMailToStaffWhenStatusChange(Shift shift, String activityName, ShiftStatus shiftStatus, String disapproveComments) {
        StaffDTO staffDTO = userIntegrationService.getStaff(shift.getUnitId(), shift.getStaffId());
        LocalDateTime shiftDate = DateUtils.asLocalDateTime(shift.getStartDate());
        String bodyPart1 = "The status of the ";
        String bodyPart2 = activityName;
        String bodyPart3 = " activity which is planned on " + WordUtils.capitalizeFully(getEmailDateTimeWithFormat(shiftDate)) + " has been moved to ";
        String bodyPart4 = shiftStatus.toString();
        String bodyPart5 = " by " + UserContext.getUserDetails().getFullName() + ".\n";
        String bodyPart6 = "[ ";
        String bodyPart7 = "Comments : ";
        String bodyPart8 = disapproveComments + ". ]\n";

        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("receiverName", EMAIL_GREETING + staffDTO.getFullName());
        templateParam.put("descriptionPart1", bodyPart1);
        templateParam.put("descriptionPart2", bodyPart2);
        templateParam.put("descriptionPart3", bodyPart3);
        templateParam.put("descriptionPart4", bodyPart4);
        templateParam.put("descriptionPart5", bodyPart5);
        if(StringUtils.isNotBlank(disapproveComments)){
            templateParam.put("descriptionPart6", bodyPart6);
            templateParam.put("descriptionPart7", bodyPart7);
            templateParam.put("descriptionPart8", bodyPart8);
        }
        sendGridMailService.sendMailWithSendGrid(SHIFT_NOTIFICATION_EMAIL_TEMPLATE, templateParam, null, MAIL_SUBJECT, staffDTO.getContactDetail().getPrivateEmail());
    }

    public ShiftAndActivtyStatusDTO updateShiftStatus(Long unitId, ShiftStatus shiftStatus, ShiftActivitiesIdDTO shiftActivitiesIdDTO) {
        if(isCollectionEmpty(shiftActivitiesIdDTO.getActivityIds())){
            Shift shift = shiftMongoRepository.findOne(shiftActivitiesIdDTO.getShiftId());
            List<BigInteger> activityIds;
            if(isNotNull(shift.getRequestAbsence())){
                activityIds = shift.getActivities().stream().map(ShiftActivity::getId).collect(Collectors.toList());
            } else {
                activityIds = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getStatus().contains(PENDING) || shiftActivity.getStatus().contains(REQUEST)).map(ShiftActivity::getId).collect(Collectors.toList());
            }
            if(isCollectionEmpty(activityIds)){
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_NOTFOUND);
            }
            shiftActivitiesIdDTO.setActivityIds(activityIds);
        }
        ShiftPublishDTO shiftPublishDTO = new ShiftPublishDTO(newArrayList(shiftActivitiesIdDTO), shiftStatus, null);
        return updateStatusOfShifts(unitId,shiftPublishDTO);
    }
}
