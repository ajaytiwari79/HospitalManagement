package com.kairos.dto.activity.time_bank;

import java.util.List;

public class TimeBankStaffAndUnitPositionDTO {
    private List<Long> staffIds;
    private List<Long> unitPositionIds;

    public TimeBankStaffAndUnitPositionDTO() {
    }

    public TimeBankStaffAndUnitPositionDTO(List<Long> staffIds, List<Long> unitPositionIds) {
        this.staffIds = staffIds;
        this.unitPositionIds = unitPositionIds;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }

    public List<Long> getUnitPositionIds() {
        return unitPositionIds;
    }

    public void setUnitPositionIds(List<Long> unitPositionIds) {
        this.unitPositionIds = unitPositionIds;
    }
}
