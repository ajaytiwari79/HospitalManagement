package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.organization.Unit;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class AccessGroupStaffQueryResult {
    private Unit unit;
    private Long staffId;
    private List<AccessGroupDayTypesQueryResult> dayTypesByAccessGroup;


    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }


    public List<AccessGroupDayTypesQueryResult> getDayTypesByAccessGroup() {
        return dayTypesByAccessGroup;
    }

    public void setDayTypesByAccessGroup(List<AccessGroupDayTypesQueryResult> dayTypesByAccessGroup) {
        this.dayTypesByAccessGroup = dayTypesByAccessGroup;
    }
}


