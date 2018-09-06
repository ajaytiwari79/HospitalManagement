package com.planner.domain.query_results;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Set;

@QueryResult
public class StaffQueryResult {

    private Long staffId ;
     private String staffName;
     private Long[] unitPositionsId;
     private Set<SkillQueryResult> staffSkills;
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

    public Long[] getStaffUnitPositions() {
        return unitPositionsId;
    }

    public void setStaffUnitPositions(Long[] staffUnitPositions) {
        this.unitPositionsId = staffUnitPositions;
    }

    public Set<SkillQueryResult> getStaffSkills() {
        return staffSkills;
    }

    public void setStaffSkills(Set<SkillQueryResult> staffSkills) {
        this.staffSkills = staffSkills;
    }

    public List<ExpertiseQueryResult> getUnitPositionExpertise() {
        return unitPositionExpertise;
    }

    public void setUnitPositionExpertise(List<ExpertiseQueryResult> unitPositionExpertise) {
        this.unitPositionExpertise = unitPositionExpertise;
    }
}
