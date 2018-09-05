package com.planner.domain.query_results;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class StaffQueryResult {

    private Long staffId ;
     private String staffName;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
