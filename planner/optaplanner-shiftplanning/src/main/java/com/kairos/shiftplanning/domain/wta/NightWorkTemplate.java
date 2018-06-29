package com.kairos.shiftplanning.domain.wta;

import com.kairos.shiftplanning.domain.Shift;
import com.kairos.shiftplanning.domain.TimeInterval;

public interface NightWorkTemplate {
    default boolean isNightShift(Shift shift) {
        return getNightTimeInterval().contains(shift.getStart().getMinuteOfDay());
    }
     TimeInterval getNightTimeInterval();
}
