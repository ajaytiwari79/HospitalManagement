package com.planner.domain.query_results;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@QueryResult
public class StaffQueryResult {

    private Long staffId ;
     private String staffName;
     private Long unitPositionsId;
     private HashSet<Map> staffSkills;
     private List<ExpertiseQueryResult> unitPositionExpertise;

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

    public Long getStaffUnitPosition() {
        return unitPositionsId;
    }

    public void setStaffUnitPosition(Long staffUnitPositions) {
        this.unitPositionsId = staffUnitPositions;
    }

    public HashSet<Map> getStaffSkills() {
        return staffSkills;
    }

    public void setStaffSkills(HashSet<Map> staffSkills) {
        this.staffSkills = staffSkills;
    }

    public List<ExpertiseQueryResult> getUnitPositionExpertise() {
        return unitPositionExpertise;
    }

    public void setUnitPositionExpertise(List<ExpertiseQueryResult> unitPositionExpertise) {
        this.unitPositionExpertise = unitPositionExpertise;
    }
}
