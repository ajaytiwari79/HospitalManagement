package com.kairos.rule_validator.activity;


import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.shift.ShiftValidatorService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_MANAGEMENT_AUTHORITY_PHASE;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFF_EMPLOYMENTTYPE_ABSENT;

public class StaffEmploymentSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Phase phase;
    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;

    public StaffEmploymentSpecification(Phase phase, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        this.phase = phase;
        this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        if (Optional.ofNullable(staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()).isPresent() && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()) {
            return true;
        }
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            List<PhaseTemplateValue> phaseTemplateValues = shiftActivityDTO.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues();
            PhaseTemplateValue phaseTemplateValue1 = null;
            for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
                if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                    phaseTemplateValue1 = phaseTemplateValue;
                    break;
                }
            }
            if (Optional.ofNullable(phaseTemplateValue1).isPresent()) {
                if (!phaseTemplateValue1.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId())){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            for (ShiftActivityDTO childActivity : shiftActivityDTO.getChildActivities()) {
                validateStaffEmployment(childActivity);
            }
            validateStaffEmployment(shiftActivityDTO);
        }
    }

    private void validateStaffEmployment(ShiftActivityDTO shiftActivityDTO) {
        List<PhaseTemplateValue> phaseTemplateValues = shiftActivityDTO.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues();
        PhaseTemplateValue phaseTemplateValue1 = null;
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                phaseTemplateValue1 = phaseTemplateValue;
                break;
            }
        }
        if (Optional.ofNullable(phaseTemplateValue1).isPresent()) {
            if (staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement() && !phaseTemplateValue1.isEligibleForManagement()) {
                ShiftValidatorService.throwException(MESSAGE_MANAGEMENT_AUTHORITY_PHASE);
            }
            if (staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff() && !phaseTemplateValue1.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId())) {
                ShiftValidatorService.throwException(MESSAGE_STAFF_EMPLOYMENTTYPE_ABSENT);
            }
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return Collections.emptyList();
    }
}
