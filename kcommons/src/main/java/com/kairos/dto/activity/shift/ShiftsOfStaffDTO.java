package com.kairos.dto.activity.shift;

import java.util.List;

public class ShiftsOfStaffDTO {
    private Long staffId;
    private List<ShiftDTO> shifts;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public List<ShiftDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftDTO> shifts) {
        this.shifts = shifts;
    }
}
