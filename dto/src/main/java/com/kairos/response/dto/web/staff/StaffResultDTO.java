package com.kairos.response.dto.web.staff;

import com.kairos.response.dto.web.organization.OrganizationIdAndNameResult;

import java.util.List;
import java.util.Map;


public class StaffResultDTO {
    private Long staffId;
    private Long unitId;
    private String unitName;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
