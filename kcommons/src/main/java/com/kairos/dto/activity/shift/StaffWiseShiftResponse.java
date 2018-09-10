package com.kairos.dto.activity.shift;

import com.kairos.dto.user.staff.staff.Staff;

import java.util.List;

public class StaffWiseShiftResponse {
    private Staff staff;

    private List<ShiftResponse> shiftResponses;

    public StaffWiseShiftResponse() {
        //DC
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }


    public List<ShiftResponse> getShiftResponses() {
        return shiftResponses;
    }

    public void setShiftResponses(List<ShiftResponse> shiftResponses) {
        this.shiftResponses = shiftResponses;
    }

    public StaffWiseShiftResponse(Staff staff,  List<ShiftResponse> shiftResponses) {
        this.staff = staff;
        this.shiftResponses = shiftResponses;
    }
}
