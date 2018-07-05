package com.kairos.spec;

import com.kairos.persistence.model.phase.Phase;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.Collections;
import java.util.List;

public class StaffAndManagementShiftDeleteSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private Phase phase;

    public StaffAndManagementShiftDeleteSpecification(Phase phase) {
        this.phase = phase;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        if(shiftWithActivityDTO.

                < shiftWithActivityDTO.getActivity().getRulesActivityTab().getPlannedTimeInAdvance().getValue()){
            return Collections.singletonList("message.shift.plannedTime.less");
        }

        return Collections.emptyList();
    }
}
