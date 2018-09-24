package com.planner.domain.query_results;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class ShiftPlanningNeo4jQueryResult {
    private List<StaffQueryResult> staffList;
    private Long[] allStaffUnitPositionIds;

    public List<StaffQueryResult> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffQueryResult> staffList) {
        this.staffList = staffList;
    }

    public Long[] getAllStaffUnitPositionIds() {
        return allStaffUnitPositionIds;
    }

    public void setAllStaffUnitPositionIds(Long[] allStaffUnitPositionIds) {
        this.allStaffUnitPositionIds = allStaffUnitPositionIds;
    }
}
