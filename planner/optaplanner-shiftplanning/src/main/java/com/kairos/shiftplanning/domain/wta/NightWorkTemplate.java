package com.kairos.shiftplanning.domain.wta;

import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;

public interface NightWorkTemplate {
    default boolean isNightShift(Shift shift) {
        return getNightTimeInterval().contains(shift.getStart().getMinuteOfDay());
    }
     TimeInterval getNightTimeInterval();
}
