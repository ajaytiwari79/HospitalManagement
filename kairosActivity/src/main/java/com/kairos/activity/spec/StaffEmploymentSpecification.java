package com.kairos.activity.spec;

import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.activity.tabs.PhaseTemplateValue;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;

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
        List<PhaseTemplateValue> phaseTemplateValues = activity.getRulesActivityTab().getEligibleForSchedules();
        PhaseTemplateValue phaseTemplateValue1 = null;
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                phaseTemplateValue1 = phaseTemplateValue;
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
        List<PhaseTemplateValue> phaseTemplateValues = activity.getRulesActivityTab().getEligibleForSchedules();
        PhaseTemplateValue phaseTemplateValue1 = null;
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phase.getId().equals(phaseTemplateValue.getPhaseId())) {
                phaseTemplateValue1 = phaseTemplateValue;
            }
        }
        if (Optional.ofNullable(phaseTemplateValue1).isPresent()) {
            if (!phaseTemplateValue1.getEligibleEmploymentTypes().contains(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId())) {
                return Collections.singletonList("message.staff.employmentType.absent");
            }
        }
        return Collections.emptyList();
    }
}
