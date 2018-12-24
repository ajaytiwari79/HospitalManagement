package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;


import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftActivityIdsDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.utils.ShiftValidatorService.throwException;

/**
 * @author pradeep
 * @date - 16/10/18
 */

public class ActivityPhaseSettingSpecification extends AbstractSpecification<ShiftWithActivityDTO> {


    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;
    private Collection<ActivityWrapper> activities;
    private Phase phase;
    private Shift oldShift;


    public ActivityPhaseSettingSpecification(StaffAdditionalInfoDTO staffAdditionalInfoDTO,Phase phase,Collection<ActivityWrapper> activities,Shift oldShift) {
        this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
        this.phase = phase;
        this.activities = activities;
        this.oldShift = oldShift;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        validateRules(shift);
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        boolean staff=staffAdditionalInfoDTO.getRoles().contains(AccessGroupRole.STAFF);
        boolean management=staffAdditionalInfoDTO.getRoles().contains(AccessGroupRole.MANAGEMENT);
        ShiftActivityIdsDTO shiftActivityIdsDTO = getActivitiesToProcess(oldShift.getActivities(), shift.getActivities());
        Map<BigInteger,PhaseTemplateValue> activityPerPhaseMap=constructMapOfActivityAndPhaseTemplateValue(phase,activities);
        activityPerPhaseMap.forEach((k,v)->{
            if(shiftActivityIdsDTO.getActivitiesToAdd().contains(k)){
                if(( staff && !v.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId())) || (management && !v.isEligibleForManagement() )){
                    throwException("error.shift.not.authorised.phase");
                }
            }
            if(shiftActivityIdsDTO.getActivitiesToEdit().contains(k)){
                if(!org.springframework.util.CollectionUtils.containsAny(v.getAllowedSettings().getCanEdit(),staffAdditionalInfoDTO.getRoles())){
                    throwException("error.shift.not.editable.phase");
                }
            }
            if(shiftActivityIdsDTO.getActivitiesToDelete().contains(k)){
                if((management && !v.isManagementCanDelete()) || (staff && !v.isStaffCanDelete())){
                    throwException("error.shift.not.deletable.phase");
                }
            }

        });
    }

    private Map<BigInteger,PhaseTemplateValue>  constructMapOfActivityAndPhaseTemplateValue(Phase phase,Collection<ActivityWrapper> activities){
        Map<BigInteger,PhaseTemplateValue> phaseTemplateValueMap=new HashMap<>();
        for(ActivityWrapper activityWrapper:activities){
            for(PhaseTemplateValue phaseTemplateValue:activityWrapper.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues()){
                if(phaseTemplateValue.getPhaseId().equals(phase.getId())){
                    phaseTemplateValueMap.put(activityWrapper.getActivity().getId(),phaseTemplateValue);
                    break;
                }
            }
        }
        return phaseTemplateValueMap;

    }

    /**
     *
     * @param existingShiftActivities
     * @param arrivedShiftActivities
     * @return shifActivityDTO
     * @Auther PAVAN
     * @LastModifiedBy Pavan
     * @Desc used to filter the ShiftActivities for Add , Edit and Delete
     */
    public static ShiftActivityIdsDTO getActivitiesToProcess(List<ShiftActivity> existingShiftActivities, List<ShiftActivityDTO> arrivedShiftActivities) {
        Set<BigInteger> allExistingShiftActivities = existingShiftActivities.stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet());
        Set<BigInteger> allArrivedShiftActivities = arrivedShiftActivities.stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
        Map<BigInteger, ShiftActivity> existingShiftActivityMap = existingShiftActivities.stream().collect(Collectors.toMap(ShiftActivity::getActivityId, Function.identity(), (currentActivity, nextActivity) -> currentActivity));
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
        return new ShiftActivityIdsDTO(activitiesToAdd,activitiesToEdit,activitiesToDelete);
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shift) {
        return null;
    }
}
