package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.organization.Organization;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class AccessGroupStaffQueryResult {
    private Organization organization;
    private Long staffId;
    private List<AccessGroupDayTypesQueryResult> dayTypesByAccessGroup;
    private List<DayType> dayTypes;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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

    public List<AccessGroupDayTypesQueryResult> getDayTypesByAccessGroup() {
        return dayTypesByAccessGroup;
    }

    public void setDayTypesByAccessGroup(List<AccessGroupDayTypesQueryResult> dayTypesByAccessGroup) {
        this.dayTypesByAccessGroup = dayTypesByAccessGroup;
    }
}


