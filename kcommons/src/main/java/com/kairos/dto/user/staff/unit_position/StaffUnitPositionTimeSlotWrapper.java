package com.kairos.dto.user.staff.unit_position;

import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;

import java.util.List;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
public class StaffUnitPositionTimeSlotWrapper {
    private List<StaffUnitPositionDetails> staffUnitPositionDetails;
    private List<TimeSlotWrapper> timeSlotWrappers;

    public StaffUnitPositionTimeSlotWrapper() {
        // DC
    }

    public StaffUnitPositionTimeSlotWrapper(List<StaffUnitPositionDetails> staffUnitPositionDetails) {
        this.staffUnitPositionDetails = staffUnitPositionDetails;
    }

    public List<StaffUnitPositionDetails> getStaffUnitPositionDetails() {
        return staffUnitPositionDetails;
    }

    public void setStaffUnitPositionDetails(List<StaffUnitPositionDetails> staffUnitPositionDetails) {
        this.staffUnitPositionDetails = staffUnitPositionDetails;
    }

    public List<TimeSlotWrapper> getTimeSlotWrappers() {
        return timeSlotWrappers;
    }

    public void setTimeSlotWrappers(List<TimeSlotWrapper> timeSlotWrappers) {
        this.timeSlotWrappers = timeSlotWrappers;
    }
}
