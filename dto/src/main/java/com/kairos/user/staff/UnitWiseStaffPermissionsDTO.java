package com.kairos.user.staff;

import java.util.HashMap;
import java.util.Map;

public class UnitWiseStaffPermissionsDTO {

    private Boolean hub;
    private HashMap<String, Object> hubPermissions;
    private HashMap<Long, Object> organizationPermissions;

    public UnitWiseStaffPermissionsDTO(){
        // default constructor
    }

    public Boolean getHub() {
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
}
