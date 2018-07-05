package com.kairos.spec;

import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.util.List;

public class ActivityPlannedTimeSpecification extends AbstractActivitySpecification<ShiftWithActivityDTO> {

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
           shiftWithActivityDTO.getStartDate()
    }
}
