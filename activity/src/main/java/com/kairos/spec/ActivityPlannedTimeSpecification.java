package com.kairos.spec;

import com.kairos.util.DateUtils;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;

public class ActivityPlannedTimeSpecification extends AbstractActivitySpecification<ShiftWithActivityDTO> {

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {

//        LocalDateTime startDate = LocalDateTime.now();
//        LocalDateTime endDate = DateUtils.asLocalDateTime(shiftWithActivityDTO.getStartDate());
//        int numberOfHours = (int) Duration.between(LocalDateTime.now(), DateUtils.asLocalDateTime(shiftWithActivityDTO.getStartDate())).toHours();
//        int time=shiftWithActivityDTO.getActivity().getRulesActivityTab().getPlannedTimeInAdvance().getValue();
        if((int) Duration.between(LocalDateTime.now(), DateUtils.asLocalDateTime(shiftWithActivityDTO.getStartDate())).toHours()
                < shiftWithActivityDTO.getActivity().getRulesActivityTab().getPlannedTimeInAdvance().getValue()){
            return Collections.singletonList("message.shift.plannedTime.less");
        }

        return Collections.emptyList();
    }
}
