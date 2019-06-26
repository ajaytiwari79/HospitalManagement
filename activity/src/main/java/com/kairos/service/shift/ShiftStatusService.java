package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.lang.WordUtils;
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
import static com.kairos.enums.phase.PhaseDefaultName.DRAFT;
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

    public ShiftAndActivtyStatusDTO updateStatusOfShifts(Long unitId, ShiftPublishDTO shiftPublishDTO) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        Object[] objects = getActivitiesAndShiftIds(shiftPublishDTO.getShifts());
        Set<BigInteger> shiftActivitiyIds = ((Set<BigInteger>) objects[1]);
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>(shifts.size());
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shifts.size());
        Set<BigInteger> allActivities = shifts.stream().flatMap(s -> s.getActivities().stream().map(ShiftActivity::getActivityId)).collect(Collectors.toSet());
        List<Activity> activities = activityMongoRepository.findAllPhaseSettingsByActivityIds(allActivities);
        Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap = activities.stream().collect(Collectors.toMap(Activity::getId, Activity::getPhaseSettingsActivityTab));
        Map<BigInteger, Activity> activityIdAndActivityMap = activities.stream().collect(Collectors.toMap(Activity::getId, Function.identity()));
        if (isCollectionNotEmpty(shifts) && objects[1] != null) {
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
            StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(unitId);
            for (Shift shift : shifts) {
                List<ShiftActivity> oldActivity = new CopyOnWriteArrayList<>(shift.getActivities());
                for (ShiftActivity shiftActivity : oldActivity) {
                    if (shiftActivitiyIds.contains(shiftActivity.getId())) {
                        Phase phase = phaseListByDate.get(shift.getActivities().get(0).getStartDate());
                        PhaseSettingsActivityTab phaseSettingsActivityTab = activityPhaseSettingMap.get(shiftActivity.getActivityId());
                        PhaseTemplateValue phaseTemplateValue = phaseSettingsActivityTab.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
                        ActivityShiftStatusSettings activityShiftStatusSettings = getActivityShiftStatusSettingByStatus(phaseTemplateValue, shiftPublishDTO.getStatus());
                        ShiftActivityResponseDTO shiftActivityResponseDTO = getShiftActivityResponseDTO(shiftPublishDTO, activityIdAndActivityMap, staffAccessGroupDTO, shift, shiftActivity, activityShiftStatusSettings);
                        shiftActivityResponseDTOS.add(shiftActivityResponseDTO);
                    }
                }
                if (shift.isDeleted()) {
                    shiftDTOS.add(shiftService.deleteShift(shift.getId()));
                } else {
                    shiftDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class));
                }

            }
            shiftMongoRepository.saveEntities(shifts);
            timeBankService.updateDailyTimeBankEntriesForStaffs(shifts);
        }
        wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS, userAccessRoleDTO);
        return new ShiftAndActivtyStatusDTO(shiftDTOS, shiftActivityResponseDTOS);
    }

    private ShiftActivityResponseDTO getShiftActivityResponseDTO(ShiftPublishDTO shiftPublishDTO, Map<BigInteger, Activity> activityIdAndActivityMap, StaffAccessGroupDTO staffAccessGroupDTO, Shift shift, ShiftActivity shiftActivity, ActivityShiftStatusSettings activityShiftStatusSettings) {
        boolean validAccessGroup = shiftValidatorService.validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
        boolean validateShiftActivityStatus = validateShiftActivityStatus(shiftPublishDTO.getStatus(), shiftActivity, activityIdAndActivityMap.get(shiftActivity.getActivityId()));
        if (validAccessGroup && validateShiftActivityStatus) {
            removeOppositeStatus(shift, shiftActivity, shiftPublishDTO.getStatus());
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage(MESSAGE_SHIFT_STATUS_ADDED), true, shiftActivity.getStatus()));
        } else if (validAccessGroup && !validateShiftActivityStatus) {
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage(ACTIVITY_STATUS_INVALID), false));
        } else {
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage(ACCESS_GROUP_NOT_MATCHED), false));
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

    private void removeOppositeStatus(Shift shift, ShiftActivity shiftActivity, ShiftStatus shiftStatus) {
        switch (shiftStatus) {
            case LOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(UNLOCK, REQUEST));
                shiftActivity.getStatus().add(LOCK);
                break;
            case FIX:
                shiftActivity.getStatus().add(FIX);
                sendMailToStaffWhenStatusChange(shift, shiftActivity, shiftStatus);
                break;
            case UNFIX:
                shiftActivity.getStatus().removeAll(Arrays.asList(FIX));
                sendMailToStaffWhenStatusChange(shift, shiftActivity, shiftStatus);
                break;
            case APPROVE:
                shiftActivity.getStatus().removeAll(Arrays.asList(PENDING, REQUEST));
                shiftActivity.getStatus().add(APPROVE);
                break;
            case DISAPPROVE:
                if (shift.getActivities().size() > 1) {
                    shift.getActivities().remove(shiftActivity);
                    shift.setStartDate(shift.getActivities().get(0).getStartDate());
                    shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
                } else {
                    shift.setDeleted(true);
                }
                sendMailToStaffWhenStatusChange(shift, shiftActivity, shiftStatus);
                break;
            case UNLOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(LOCK, REQUEST));
                break;
            case PUBLISH:
                shiftActivity.getStatus().add(PUBLISH);
                break;
            case VALIDATE:
                shiftActivity.getStatus().add(VALIDATE);
                break;
            case PENDING:
                shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST));
                shiftActivity.getStatus().add(PENDING);
                sendMailToStaffWhenStatusChange(shift, shiftActivity, shiftStatus);
                break;
            default:
                break;
        }
    }

    public int activityChangeStatus(Activity activityOld, Activity activityCurrent) {
        boolean isShiftOldForPresence = TimeTypeEnum.PRESENCE.equals(activityOld.getBalanceSettingsActivityTab().getTimeType());
        boolean isShiftCurrentForAbsence = TimeTypeEnum.ABSENCE.equals(activityCurrent.getBalanceSettingsActivityTab().getTimeType());
      //  boolean isShiftOldForPresence = !(activityOld.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityOld.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
      //  boolean isShiftCurrentForAbsence = (activityCurrent.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityCurrent.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        int activityChangeStatus = 0;
        if (isShiftOldForPresence && isShiftCurrentForAbsence) {
            activityChangeStatus = 1;
        } else if (!isShiftOldForPresence && !isShiftCurrentForAbsence) {
            activityChangeStatus = 2;
        }

        return activityChangeStatus;
    }

    private ActivityShiftStatusSettings getActivityShiftStatusSettingByStatus(PhaseTemplateValue phaseTemplateValue, ShiftStatus status) {
        ActivityShiftStatusSettings activityShiftStatusSettings = null;
        for (ActivityShiftStatusSettings statusSettings : phaseTemplateValue.getActivityShiftStatusSettings()) {
            if (status.equals(statusSettings.getShiftStatus())) {
                activityShiftStatusSettings = statusSettings;
                break;
            }
        }
        return activityShiftStatusSettings;
    }

    public void updateStatusOfShiftIfPhaseValid(Phase phase, Shift mainShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, UserAccessRoleDTO userAccessRoleDTO) {
        Set<PhaseDefaultName> validPhaseForPublishingShift = newHashSet(DRAFT, PhaseDefaultName.REALTIME, PhaseDefaultName.TENTATIVE);
        for (ShiftActivity shiftActivity : mainShift.getActivities()) {
            if (validPhaseForPublishingShift.contains(phase.getPhaseEnum())) {
                shiftActivity.getStatus().add(ShiftStatus.PUBLISH);
            } else if (isCollectionNotEmpty(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds()) && isCollectionEmpty(shiftActivity.getStatus())) {
                if (activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds().contains(phase.getId())) {
                    shiftActivity.getStatus().add(userAccessRoleDTO.getManagement() ? ShiftStatus.APPROVE : ShiftStatus.REQUEST);
                }
            }
        }
    }


    public void sendMailToStaffWhenStatusChange(Shift shift, ShiftActivity activity, ShiftStatus shiftStatus) {
        StaffDTO staffDTO = userIntegrationService.getStaff(shift.getUnitId(), shift.getStaffId());
        LocalDateTime shiftDate = DateUtils.asLocalDateTime(shift.getStartDate());
        String bodyPart1 = "The status of the ";
        String bodyPart2 = activity.getActivityName();
        String bodyPart3 = " activity which is planned on " + WordUtils.capitalizeFully(getEmailDateTimeWithFormat(shiftDate)) + " has been moved to ";
        String bodyPart4 = shiftStatus.toString();
        String bodyPart5 = " by " + UserContext.getUserDetails().getFullName() + ".\n";

        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("receiverName", EMAIL_GREETING + staffDTO.getFullName());
        templateParam.put("descriptionPart1", bodyPart1);
        templateParam.put("descriptionPart2", bodyPart2);
        templateParam.put("descriptionPart3", bodyPart3);
        templateParam.put("descriptionPart4", bodyPart4);
        templateParam.put("descriptionPart5", bodyPart5);
        mailService.sendMailWithSendGrid(SHIFT_NOTIFICATION_EMAIL_TEMPLATE, templateParam, null, MAIL_SUBJECT, staffDTO.getEmail());
    }
}
