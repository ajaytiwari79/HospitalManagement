package com.kairos.persistence.model.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class AccessGroupCounterQueryResult {
    private Long staffId;
    private Long countryId;
    private Boolean isCountryAdmin;
    private List<Long> accessGroupIds;

    public AccessGroupCounterQueryResult() {

    }

    public AccessGroupCounterQueryResult(Long staffId, Long countryId, Boolean isCountryAdmin, List<Long> accessGroupIds) {
        this.staffId = staffId;
        this.countryId = countryId;
        this.isCountryAdmin = isCountryAdmin;
        this.accessGroupIds = accessGroupIds;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Boolean getCountryAdmin() {
        return isCountryAdmin;
    }

    public void setCountryAdmin(Boolean countryAdmin) {
        isCountryAdmin = countryAdmin;
    }

    public List<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(List<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }
}
