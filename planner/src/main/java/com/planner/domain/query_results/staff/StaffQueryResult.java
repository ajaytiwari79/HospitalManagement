package com.planner.domain.query_results.staff;

import com.planner.domain.query_results.expertise.ExpertiseQueryResult;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@QueryResult
public class StaffQueryResult {

    private Long staffId ;
     private String staffName;
     private Long employmentId;
     private HashSet<Map> staffSkills;
     private List<ExpertiseQueryResult> employmentExpertise;

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

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }


    public HashSet<Map> getStaffSkills() {
        return staffSkills;
    }

    public void setStaffSkills(HashSet<Map> staffSkills) {
        this.staffSkills = staffSkills;
    }

    public List<ExpertiseQueryResult> getEmploymentExpertise() {
        return employmentExpertise;
    }

    public void setEmploymentExpertise(List<ExpertiseQueryResult> employmentExpertise) {
        this.employmentExpertise = employmentExpertise;
    }
}
