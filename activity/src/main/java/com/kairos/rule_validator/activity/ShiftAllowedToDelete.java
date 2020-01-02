package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.rule_validator.AbstractSpecification;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_PHASE_AUTHORITY_ABSENT;
import static com.kairos.service.shift.ShiftValidatorService.throwException;

public class ShiftAllowedToDelete extends AbstractSpecification<Shift> {

    private Map<BigInteger, ActivityWrapper> activityWrapperMap;
    private BigInteger phaseId;

    public ShiftAllowedToDelete(Map<BigInteger, ActivityWrapper> activityWrapperMap,BigInteger phaseId) {
        this.activityWrapperMap = activityWrapperMap;
        this.phaseId = phaseId;
    }

    @Override
    public boolean isSatisfied(Shift phaseId) {
        return false;
    }

    @Override
    public void validateRules(Shift shift) {
        PhaseTemplateValue currentPhase = null;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            for (PhaseTemplateValue phaseTemplateValue : activityWrapperMap.get(shiftActivity.getActivityId()).getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues()) {
                if (phaseId.equals(phaseTemplateValue.getPhaseId())) {
                    currentPhase = phaseTemplateValue;
                    break;
                }
            }
            if (Optional.ofNullable(currentPhase).isPresent()) {
                if (UserContext.getUserDetails().isManagement() && !currentPhase.isManagementCanDelete() ||
                        (UserContext.getUserDetails().isStaff() && !currentPhase.isStaffCanDelete())) {
                    throwException(MESSAGE_PHASE_AUTHORITY_ABSENT);
                }
            }
        }
    }

    @Override
    public List<String> isSatisfiedString(Shift shift) {
        return null;
    }
}
