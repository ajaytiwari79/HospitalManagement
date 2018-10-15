package com.kairos.rule_validator.activity;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StaffEmploymentSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Phase phase;
    private ActivityDTO activity;
    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;

    public StaffEmploymentSpecification(Phase phase, ActivityDTO activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        this.phase = phase;
        this.activity = activity;
        this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        if(Optional.ofNullable(staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()).isPresent() && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()){
            return true;
        }
        List<PhaseTemplateValue> phaseTemplateValues = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues();
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
    public void validateRules(ShiftWithActivityDTO shift) {
        List<PhaseTemplateValue> phaseTemplateValues = activity.getPhaseSettingsActivityTab().getPhaseTemplateValues();
        PhaseTemplateValue phaseTemplateValue1 = null;
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                phaseTemplateValue1 = phaseTemplateValue;
                break;
            }
        }
        if (Optional.ofNullable(phaseTemplateValue1).isPresent()) {
            if(staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement() && !phaseTemplateValue1.isEligibleForManagement()){
                ShiftValidatorService.throwException("message.management.authority.phase");
            }
            if (staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff() && !phaseTemplateValue1.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId())) {
                ShiftValidatorService.throwException("message.staff.employmentType.absent");
            }
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {


        return Collections.emptyList();
    }
}
