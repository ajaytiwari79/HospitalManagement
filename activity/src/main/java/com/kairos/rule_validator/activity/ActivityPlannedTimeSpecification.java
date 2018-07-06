package com.kairos.rule_validator.activity;

import com.kairos.rule_validator.activity.AbstractActivitySpecification;
import com.kairos.util.DateUtils;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public class ActivityPlannedTimeSpecification extends AbstractActivitySpecification<ShiftWithActivityDTO> {

    private ZoneId zoneId;

    public ActivityPlannedTimeSpecification(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        if((int) Duration.between(DateUtils.getLocalDateTimeFromZoneId(zoneId), DateUtils.asLocalDateTime(shiftWithActivityDTO.getStartDate())).toHours()
                < shiftWithActivityDTO.getActivity().getRulesActivityTab().getPlannedTimeInAdvance().getValue()){
            return Collections.singletonList("message.shift.plannedTime.less");
        }
        return Collections.emptyList();
    }
}
