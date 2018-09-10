package com.kairos.rule_validator.activity;

import com.kairos.persistence.model.activity.tabs.PhaseTemplateValue;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;

import java.math.BigInteger;
import java.util.*;

public class ShiftAllowedToDelete extends AbstractSpecification<BigInteger> {

    List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
    private UserAccessRoleDTO userAccessRoleDTO;

    public ShiftAllowedToDelete(List<PhaseTemplateValue> phaseTemplateValues , UserAccessRoleDTO userAccessRoleDTO) {
        this.phaseTemplateValues = phaseTemplateValues;
        this.userAccessRoleDTO = userAccessRoleDTO;
    }

    @Override
    public boolean isSatisfied(BigInteger phaseId) {
        return false;
    }

    @Override
    public void validateRules(BigInteger bigInteger) {

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
            if ((Optional.ofNullable(userAccessRoleDTO.getManagement()).isPresent() && Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent())
                    && ((userAccessRoleDTO.getManagement())&&userAccessRoleDTO.getStaff()) &&(currentPhase.isManagementCanDelete() ||currentPhase.isStaffCanDelete())) {
                return Collections.emptyList();
            }
                if ((Optional.ofNullable(userAccessRoleDTO.getManagement()).isPresent() && userAccessRoleDTO.getManagement() && !currentPhase.isManagementCanDelete()) ||
                        (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getStaff() && !currentPhase.isStaffCanDelete())) {
                    errors  = Arrays.asList("message.phase.authority.absent");
                }
            }
        return errors;
    }
}
