package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class StaffUnitWrapper {
    private List<Long> staffIds;
    private List<Long> unitIds;

    public StaffUnitWrapper() {
    }

    public StaffUnitWrapper(List<Long> staffIds, List<Long> unitIds) {
        this.staffIds = staffIds;
        this.unitIds = unitIds;
    }

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
