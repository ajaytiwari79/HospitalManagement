package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
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
    private MailService mailService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject private TodoRepository todoRepository;

    public ShiftAndActivtyStatusDTO updateStatusOfShifts(Long unitId, ShiftPublishDTO shiftPublishDTO) {
        Shift currentShift = shiftMongoRepository.findOne(shiftPublishDTO.getShifts().get(0).getShiftId());
        Activity activity = activityMongoRepository.findOne(currentShift.getActivities().get(0).getActivityId());
        if(CommonConstants.FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime())){
            List<Shift> shifts = shiftService.getFullWeekShiftsByDate(currentShift.getStartDate(),currentShift.getEmploymentId(), activity);
            shiftPublishDTO.getShifts().clear();
            shifts.forEach(shift -> shiftPublishDTO.getShifts().add(new ShiftActivitiesIdDTO(shift.getId(),shift.getActivities().stream().map(shiftActivityDTO -> shiftActivityDTO.getId()).collect(Collectors.toList()))));
        }
        Object[] objects = getActivitiesAndShiftIds(shiftPublishDTO.getShifts());
        Set<BigInteger> shiftActivitiyIds = ((Set<BigInteger>) objects[1]);
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>(shifts.size());
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shifts.size());
        Object[] activityDetails = getActivityDetailsMap(shifts);
        Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap = (Map<BigInteger, PhaseSettingsActivityTab>)activityDetails[0];
        Map<BigInteger, Activity> activityIdAndActivityMap = (Map<BigInteger, Activity>)activityDetails[1];
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", activityDetails[2].toString()));
        requestParam.add(new BasicNameValuePair("employmentIds", activityDetails[3].toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(shifts.get(0).getUnitId(), requestParam);
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap = staffAdditionalInfoDTOS.stream().filter(distinctByKey(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId())).collect(Collectors.toMap(s -> s.getEmployment().getId(), v -> v));
        if (isCollectionNotEmpty(shifts) && objects[1] != null) {
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
            StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(unitId);
            for (Shift shift : shifts) {
                List<ShiftActivity> oldActivity = new CopyOnWriteArrayList<>(shift.getActivities());
                for (ShiftActivity shiftActivity : oldActivity) {
                    updateStatusOfShiftActivity(shiftPublishDTO, shiftActivitiyIds, shiftActivityResponseDTOS, activityPhaseSettingMap, activityIdAndActivityMap, phaseListByDate, staffAccessGroupDTO, shift, shiftActivity,staffAdditionalInfoMap);
                    for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                        updateStatusOfShiftActivity(shiftPublishDTO, shiftActivitiyIds, shiftActivityResponseDTOS, activityPhaseSettingMap, activityIdAndActivityMap, phaseListByDate, staffAccessGroupDTO, shift, childActivity,staffAdditionalInfoMap);
                    }
                }
                if (shift.isDeleted()) {
                    shiftDTOS.addAll(shiftService.deleteAllLinkedShifts(shift.getId()).getShifts());
                } else {
                    shiftDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class));
                }

            }
            shiftMongoRepository.saveEntities(shifts);
            timeBankService.updateDailyTimeBankEntriesForStaffs(shifts,null);
        }
        wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS);
        return new ShiftAndActivtyStatusDTO(shiftDTOS, shiftActivityResponseDTOS);
    }

    private Object[] getActivityDetailsMap(List<Shift> shifts){
        Set<BigInteger> activityIds = new HashSet<>();
        Set<Long> staffIds = new HashSet<>();
        Set<Long> employmentIds = new HashSet<>();
        for (Shift shift : shifts) {
            activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            activityIds.addAll(shift.getActivities().stream().map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            staffIds.add(shift.getStaffId());
            employmentIds.add(shift.getEmploymentId());
        }
        List<Activity> activities = activityMongoRepository.findAllPhaseSettingsByActivityIds(activityIds);
        Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap = activities.stream().collect(Collectors.toMap(Activity::getId, Activity::getPhaseSettingsActivityTab));
        Map<BigInteger, Activity> activityIdAndActivityMap = activities.stream().collect(Collectors.toMap(Activity::getId, Function.identity()));
        return new Object[]{activityPhaseSettingMap,activityIdAndActivityMap,staffIds,employmentIds};
    }

    private void updateStatusOfShiftActivity(ShiftPublishDTO shiftPublishDTO, Set<BigInteger> shiftActivitiyIds, List<ShiftActivityResponseDTO> shiftActivityResponseDTOS, Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap, Map<BigInteger, Activity> activityIdAndActivityMap, Map<Date, Phase> phaseListByDate, StaffAccessGroupDTO staffAccessGroupDTO, Shift shift, ShiftActivity shiftActivity,Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap) {
       if (shiftActivitiyIds.contains(shiftActivity.getId())) {
            Phase phase = phaseListByDate.get(shift.getActivities().get(0).getStartDate());
            PhaseSettingsActivityTab phaseSettingsActivityTab = activityPhaseSettingMap.get(shiftActivity.getActivityId());
            PhaseTemplateValue phaseTemplateValue = phaseSettingsActivityTab.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
            ActivityShiftStatusSettings activityShiftStatusSettings = getActivityShiftStatusSettingByStatus(phaseTemplateValue, shiftPublishDTO.getStatus());
            ShiftActivityResponseDTO shiftActivityResponseDTO = getShiftActivityResponseDTO(shiftPublishDTO, activityIdAndActivityMap, staffAccessGroupDTO, shift, shiftActivity, activityShiftStatusSettings,staffAdditionalInfoMap);
            shiftActivityResponseDTOS.add(shiftActivityResponseDTO);
        }
    }

    private ShiftActivityResponseDTO getShiftActivityResponseDTO(ShiftPublishDTO shiftPublishDTO, Map<BigInteger, Activity> activityIdAndActivityMap, StaffAccessGroupDTO staffAccessGroupDTO, Shift shift, ShiftActivity shiftActivity, ActivityShiftStatusSettings activityShiftStatusSettings,Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap) {
        boolean validAccessGroup = shiftValidatorService.validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
        boolean validateShiftActivityStatus = validateShiftActivityStatus(shiftPublishDTO.getStatus(), shiftActivity, activityIdAndActivityMap.get(shiftActivity.getActivityId()));
        boolean draftShift=false;
        if(FIX.equals(shiftPublishDTO.getStatus())){
            draftShift=shift.isDraft();
            shift.setDraftShift(null);
        }
        if (validAccessGroup && validateShiftActivityStatus && !draftShift) {
            removeOppositeStatus(shift, shiftActivity, shiftPublishDTO.getStatus(),activityIdAndActivityMap,staffAdditionalInfoMap,shiftPublishDTO.getComment());
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage(MESSAGE_SHIFT_STATUS_ADDED), true, shiftActivity.getStatus()));
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
        if (isCollectionEmpty(activity.getRulesActivityTab().getApprovalAllowedPhaseIds()) && (shiftStatus.equals(ShiftStatus.FIX) || shiftStatus.equals(ShiftStatus.UNFIX) || shiftStatus.equals(ShiftStatus.PUBLISH))) {
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
        if(newHashSet(APPROVE,DISAPPROVE).contains(shiftStatus)){
            TodoStatus todoStatus = shiftStatus.equals(APPROVE) ? TodoStatus.APPROVE: TodoStatus.DISAPPROVE;
            todo = todoRepository.findAllByEntityIdAndSubEntityAndTypeAndStatus(shift.getId(), TodoType.APPROVAL_REQUIRED,newHashSet(TodoStatus.PENDING,TodoStatus.VIEWED,TodoStatus.REQUESTED),shiftActivity.getActivityId());
            if(isNotNull(todo)) {
                todo.setStatus(todoStatus);
                todo.setComment(comment);
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
                timeBankService.updateTimeBanOnApproveTimebankOFF(shiftActivity,shift.getEmploymentId(),activityIdAndActivityMap,staffAdditionalInfoMap.get(shift.getEmploymentId()));
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
                timeBankService.updateTimeBanOnApproveTimebankOFF(shiftActivity,shift.getEmploymentId(),activityIdAndActivityMap,staffAdditionalInfoMap.get(shift.getEmploymentId()));
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
            boolean removed = shift.getActivities().remove(shiftActivity);
            if(removed) {
                shift.setDeleted(true);
            }
            removeShiftAcivityFromChildActivities(shift,shiftActivity,removed);
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
                shiftActivity.getStatus().add(ShiftStatus.PUBLISH);
            } else if (isCollectionNotEmpty(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds()) && isCollectionEmpty(shiftActivity.getStatus())) {
                if (activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds().contains(phase.getId())) {
                    shiftActivity.getStatus().add(UserContext.getUserDetails().isManagement() ? ShiftStatus.APPROVE : ShiftStatus.REQUEST);
                }
            }
            if(shiftActivity.getStatus().contains(APPROVE)){
                ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
                Map<BigInteger,Activity> activityMap = new HashMap<>();
                activityMap.put(activityWrapper.getActivity().getId(),activityWrapper.getActivity());
                timeBankService.updateTimeBanOnApproveTimebankOFF(shiftActivity,mainShift.getEmploymentId(),activityMap,staffAdditionalInfoDTO);
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
        mailService.sendMailWithSendGrid(SHIFT_NOTIFICATION_EMAIL_TEMPLATE, templateParam, null, MAIL_SUBJECT, staffDTO.getEmail());
    }
}
