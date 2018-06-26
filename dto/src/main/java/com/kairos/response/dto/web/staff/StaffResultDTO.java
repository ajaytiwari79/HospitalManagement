package com.kairos.response.dto.web.staff;

import java.util.List;


public class StaffResultDTO {
    private List<Long> staffIds;
    private List<Long> unitIds;

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }
}
