package com.kairos.persistence.model.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

@QueryResult
public class UserPermissionQueryResult {

    private Long unitId;
    private boolean parentOrganization;
    private List<Long> accessibleTabs;  // in case of unit te accessible tabs needs to be filtered
    private List<AccessPageQueryResult> permission = new ArrayList<>();
    private List<AccessGroup> accessGroups;

    public UserPermissionQueryResult(){
        // default constructor
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<AccessPageQueryResult> getPermission() {
        return permission;
    }

    public void setPermission(List<AccessPageQueryResult> permission) {
        this.permission = permission;
    }

    public boolean isParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(boolean parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public List<Long> getAccessibleTabs() {
        return accessibleTabs;
    }

    public void setAccessibleTabs(List<Long> accessibleTabs) {
        this.accessibleTabs = accessibleTabs;
    }

    public List<AccessGroup> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<AccessGroup> accessGroups) {
        this.accessGroups = accessGroups;
    }
}
