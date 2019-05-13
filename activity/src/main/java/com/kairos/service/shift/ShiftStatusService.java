package com.kairos.service.shift;


import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
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
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.AppConstants.FULL_WEEK;
import static com.kairos.enums.phase.PhaseDefaultName.DRAFT;
import static com.kairos.enums.shift.ShiftStatus.*;

@Service
public class ShiftStatusService {


    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private PhaseService phaseService;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private TimeBankService timeBankService;
    @Inject private LocaleService localeService;

    public ShiftAndActivtyStatusDTO updateStatusOfShifts(Long unitId, ShiftPublishDTO shiftPublishDTO) {
        Object[] objects = getActivitiesAndShiftIds(shiftPublishDTO.getShifts());
        Set<BigInteger> shiftActivitiyIds = ((Set<BigInteger>) objects[1]);
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>(shifts.size());
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shifts.size());
        Set<BigInteger> allActivities = shifts.stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toSet());
        List<Activity> activities = activityMongoRepository.findAllPhaseSettingsByActivityIds(allActivities);
        Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap = activities.stream().collect(Collectors.toMap(Activity::getId, Activity::getPhaseSettingsActivityTab));
        if (isCollectionNotEmpty(shifts) && objects[1] != null) {
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
            StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(unitId);
            for (Shift shift : shifts) {
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if (shiftActivitiyIds.contains(shiftActivity.getId())) {
                        Phase phase = phaseListByDate.get(shift.getActivities().get(0).getStartDate());
                        PhaseSettingsActivityTab phaseSettingsActivityTab = activityPhaseSettingMap.get(shiftActivity.getActivityId());
                        PhaseTemplateValue phaseTemplateValue = phaseSettingsActivityTab.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
                        ActivityShiftStatusSettings activityShiftStatusSettings = getActivityShiftStatusSettingByStatus(phaseTemplateValue, shiftPublishDTO.getStatus());
                        boolean validAccessGroup = shiftValidatorService.validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
                        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
                        if (validAccessGroup) {
                            removeOppositeStatus(shift , shiftActivity, shiftPublishDTO.getStatus());
                            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage("message.shift.status.added"), true, shiftActivity.getStatus()));
                        } else {
                            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage("access.group.not.matched"), false));
                        }
                        shiftActivityResponseDTOS.add(shiftActivityResponseDTO);
                    }
                }
                shiftDTOS.add(ObjectMapperUtils.copyPropertiesByMapper(shift,ShiftDTO.class));
            }
            shiftMongoRepository.saveEntities(shifts);
            timeBankService.updateDailyTimeBankEntriesForStaffs(shifts);
        }
        return new ShiftAndActivtyStatusDTO( new ArrayList<>() , shiftActivityResponseDTOS);
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


    private void removeOppositeStatus(Shift shift , ShiftActivity shiftActivity, ShiftStatus shiftStatus) {
        switch (shiftStatus) {
            case LOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(UNLOCK, UNPUBLISH, REQUEST));
                shiftActivity.getStatus().add(LOCK);
                break;
            case FIX:
                shiftActivity.getStatus().removeAll(Arrays.asList(UNFIX, REQUEST));
                shiftActivity.getStatus().add(FIX);
                break;
            case UNFIX:
                shiftActivity.getStatus().removeAll(Arrays.asList(FIX, REQUEST));
                break;
            case APPROVE:
                shiftActivity.getStatus().removeAll(Arrays.asList(REJECT, UNPUBLISH, REQUEST));
                shiftActivity.getStatus().add(APPROVE);
                break;
            case REJECT:
                if(shift.getActivities().size()>1){
                    shift.getActivities().remove(shiftActivity);
                    shift.setStartDate(shift.getActivities().get(0).getStartDate());
                    shift.setEndDate(shift.getActivities().get(shift.getActivities().size()-1).getEndDate());
                }else{
                    shift.setDeleted(true);
                }
                break;
            case UNLOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(LOCK, REQUEST));
                break;
            case PUBLISH:
                shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST, UNPUBLISH, REJECT));
                shiftActivity.getStatus().add(PUBLISH);
                break;
            case UNPUBLISH:
                shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST, PUBLISH));
                break;
            case VALIDATE:
                shiftActivity.getStatus().add(VALIDATE);
                break;
        }
    }

    public int activityChangeStatus(Activity activityOld, Activity activityCurrent) {
        boolean isShiftOldForPresence = !(activityOld.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityOld.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        boolean isShiftCurrentForAbsence = (activityCurrent.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityCurrent.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
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

    public void updateStatusOfShiftIfPhaseValid(Phase phase, Shift mainShift ,Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        for (ShiftActivity shiftActivity : mainShift.getActivities()) {
            if(isCollectionNotEmpty(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds())){
                if(activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getRulesActivityTab().getApprovalAllowedPhaseIds().contains(phase.getId())){
                    shiftActivity.getStatus().add(userAccessRoleDTO.getManagement() ? ShiftStatus.APPROVE : ShiftStatus.REQUEST);
                }
            }
        }
    }
}
