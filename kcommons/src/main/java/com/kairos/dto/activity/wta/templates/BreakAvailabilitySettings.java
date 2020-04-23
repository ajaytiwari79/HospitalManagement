package com.kairos.dto.activity.wta.templates;/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.enums.wta.PartOfDay;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BreakAvailabilitySettings {
    private PartOfDay timeSlot;
    private short startAfterMinutes;
    private short endBeforeMinutes;
    private short shiftPercentage;

    public BreakAvailabilitySettings(PartOfDay timeSlot, short startAfterMinutes, short endBeforeMinutes) {
        this.timeSlot = timeSlot;
        this.startAfterMinutes = startAfterMinutes;
        this.endBeforeMinutes = endBeforeMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BreakAvailabilitySettings)) return false;
        BreakAvailabilitySettings that = (BreakAvailabilitySettings) o;
        return startAfterMinutes == that.startAfterMinutes &&
                endBeforeMinutes == that.endBeforeMinutes &&
                timeSlot == that.timeSlot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeSlot, startAfterMinutes, endBeforeMinutes);
    }
}
