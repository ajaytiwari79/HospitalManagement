package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftActivityIdsDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.rule_validator.AbstractSpecification;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.STAFF;
import static com.kairos.service.shift.ShiftValidatorService.throwException;

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
        ShiftActivityIdsDTO shiftActivityIdsDTO = getActivitiesToProcess(oldShift.getActivities(), shift.getActivities());
        Map<BigInteger,PhaseTemplateValue> activityPerPhaseMap=constructMapOfActivityAndPhaseTemplateValue(phase,activities);
        activityPerPhaseMap.forEach((k,v)->{
            if(shiftActivityIdsDTO.getActivitiesToAdd().contains(k)){
                if(( UserContext.getUserDetails().isStaff() && !v.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId())) || (UserContext.getUserDetails().isManagement() && !v.isEligibleForManagement() )){
                    throwException(ERROR_SHIFT_NOT_AUTHORISED_PHASE);
                }
            }
            if(shiftActivityIdsDTO.getActivitiesToEdit().contains(k)){
                if(v.getAllowedSettings().getCanEdit().size()<2){
                    if(v.getAllowedSettings().getCanEdit().contains(AccessGroupRole.STAFF) && !UserContext.getUserDetails().isStaff() ||
                            v.getAllowedSettings().getCanEdit().contains(AccessGroupRole.MANAGEMENT) && !UserContext.getUserDetails().isManagement()
                    ){
                        throwException(ERROR_SHIFT_NOT_EDITABLE_PHASE);
                    }
                }
            }
            if(shiftActivityIdsDTO.getActivitiesToDelete().contains(k)){
                if((UserContext.getUserDetails().isManagement() && !v.isManagementCanDelete()) || (UserContext.getUserDetails().isStaff() && !v.isStaffCanDelete())){
                    throwException(ERROR_SHIFT_NOT_DELETABLE_PHASE);
                }
            }
            if(!staffAdditionalInfoDTO.isCountryAdmin() && !CollectionUtils.containsAny(phase.getAccessGroupIds(),staffAdditionalInfoDTO.getUserAccessRoleDTO().getAccessGroupIds())){
                throwException(ERROR_SHIFT_NOT_DELETABLE_PHASE);
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
