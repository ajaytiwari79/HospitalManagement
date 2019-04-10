package com.planner.domain.query_results.shift_planning;

import com.planner.domain.query_results.staff.StaffQueryResult;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class ShiftPlanningNeo4jQueryResult {
    private List<StaffQueryResult> staffList;
    private Long[] allStaffEmploymentIds;

    public List<StaffQueryResult> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffQueryResult> staffList) {
        this.staffList = staffList;
    }

    public Long[] getAllStaffEmploymentIds() {
        return allStaffEmploymentIds;
    }

    public void setAllStaffEmploymentIds(Long[] allStaffEmploymentIds) {
        this.allStaffEmploymentIds = allStaffEmploymentIds;
    }
}
