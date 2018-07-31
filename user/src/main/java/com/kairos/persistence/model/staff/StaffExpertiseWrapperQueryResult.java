package com.kairos.persistence.model.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Set;

@QueryResult
public class StaffExpertiseWrapperQueryResult {
    private Long staffId;
    private Set<Long> expertiseIds;
    private Long employmentTypeId;

    public StaffExpertiseWrapperQueryResult() {
        //Default Constructor
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Set<Long> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(Set<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }
}
