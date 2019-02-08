package com.kairos.dto.user.staff.staff;

import com.kairos.dto.user.access_permission.AccessGroupRole;

import java.util.HashMap;

public class UnitWiseStaffPermissionsDTO {

    private Boolean hub;
    private AccessGroupRole role;
    private HashMap<String, Object> hubPermissions;
    private HashMap<Long, Object> organizationPermissions;

    public UnitWiseStaffPermissionsDTO(){
        // default constructor
    }

    public Boolean isHub() {
        return hub;
    }

    public void setHub(Boolean hub) {
        this.hub = hub;
    }

    public HashMap<String, Object> getHubPermissions() {
        return hubPermissions;
    }

    public void setHubPermissions(HashMap<String, Object> hubPermissions) {
        this.hubPermissions = hubPermissions;
    }

    public HashMap<Long, Object> getOrganizationPermissions() {
        return organizationPermissions;
    }

    public void setOrganizationPermissions(HashMap<Long, Object> organizationPermissions) {
        this.organizationPermissions = organizationPermissions;
    }

    public AccessGroupRole getRole() {
        return role;
    }

    public void setRole(AccessGroupRole role) {
        this.role = role;
    }
}
