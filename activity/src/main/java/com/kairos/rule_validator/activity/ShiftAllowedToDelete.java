package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.user_context.UserContext;
import com.kairos.rule_validator.AbstractSpecification;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_PHASE_AUTHORITY_ABSENT;

public class ShiftAllowedToDelete extends AbstractSpecification<BigInteger> {

    private List<PhaseTemplateValue> phaseTemplateValues;

    public ShiftAllowedToDelete(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }

    @Override
    public boolean isSatisfied(BigInteger phaseId) {
        return false;
    }

    @Override
    public void validateRules(BigInteger bigInteger) {
        //Not in use
    }

    @Override
    public List<String> isSatisfiedString(BigInteger phaseId) {
        List<String> errors = new ArrayList<>();
        PhaseTemplateValue currentPhase = null;
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phaseId.equals(phaseTemplateValue.getPhaseId())) {
                currentPhase = phaseTemplateValue;
                break;
            }
        }
        if (Optional.ofNullable(currentPhase).isPresent()) {
            if (currentPhase.isManagementCanDelete() && currentPhase.isStaffCanDelete()) {
                return Collections.emptyList();
            }
            if (UserContext.getUserDetails().isManagement() && !currentPhase.isManagementCanDelete() ||
                    (UserContext.getUserDetails().isStaff() && !currentPhase.isStaffCanDelete())) {
                errors = Arrays.asList(MESSAGE_PHASE_AUTHORITY_ABSENT);
            }
        }
        return errors;
    }
}
