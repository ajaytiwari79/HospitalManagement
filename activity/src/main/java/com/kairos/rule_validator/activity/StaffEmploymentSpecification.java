package com.kairos.rule_validator.activity;


import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.PhaseTemplateValue;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StaffEmploymentSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Phase phase;
    private Activity activity;
    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;

    public StaffEmploymentSpecification(Phase phase, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        this.phase = phase;
        this.activity = activity;
        this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        if(Optional.ofNullable(staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()).isPresent() && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()){
            return true;
        }
        List<PhaseTemplateValue> phaseTemplateValues = activity.getRulesActivityTab().getEligibleForSchedules();
        PhaseTemplateValue phaseTemplateValue1 = null;
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                phaseTemplateValue1 = phaseTemplateValue;
                break;
            }
        }
        if (Optional.ofNullable(phaseTemplateValue1).isPresent()) {
            if (!phaseTemplateValue1.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        if (Optional.ofNullable(staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()).isPresent() && !staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()) {
            List<PhaseTemplateValue> phaseTemplateValues = activity.getRulesActivityTab().getEligibleForSchedules();
            PhaseTemplateValue phaseTemplateValue1 = null;
            for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
                if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                    phaseTemplateValue1 = phaseTemplateValue;
                    break;
                }
            }
            if (Optional.ofNullable(phaseTemplateValue1).isPresent()) {
                if (!phaseTemplateValue1.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId())) {
                    return Arrays.asList("message.staff.employmentType.absent");
                }
            }
            }
        return Collections.emptyList();
    }
}
