package com.kairos.dto.activity.wta.templates;/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.enums.wta.PartOfDay;

import java.util.Objects;

public class BreakAvailabilitySettings {
    private PartOfDay timeSlot;
    private short startAfterMinutes;
    private short endBeforeMinutes;

    public BreakAvailabilitySettings() {
        //Default Constructor
    }
    public BreakAvailabilitySettings( short startAfterMinutes, short endBeforeMinutes) {

        this.startAfterMinutes = startAfterMinutes;
        this.endBeforeMinutes = endBeforeMinutes;
    }
    public BreakAvailabilitySettings(PartOfDay timeSlot, short startAfterMinutes, short endBeforeMinutes) {
        this.timeSlot = timeSlot;
        this.startAfterMinutes = startAfterMinutes;
        this.endBeforeMinutes = endBeforeMinutes;
    }

    public PartOfDay getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(PartOfDay timeSlot) {
        this.timeSlot = timeSlot;
    }

    public short getStartAfterMinutes() {
        return startAfterMinutes;
    }

    public void setStartAfterMinutes(short startAfterMinutes) {
        this.startAfterMinutes = startAfterMinutes;
    }

    public short getEndBeforeMinutes() {
        return endBeforeMinutes;
    }

    public void setEndBeforeMinutes(short endBeforeMinutes) {
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
