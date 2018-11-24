package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.organization.Organization;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class AccessGroupStaffQueryResult {
    private Organization currentOrganization;
    private Long staffId;
    private List<DayType> dayTypes;

    public Organization getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentOrganization(Organization currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }
}


