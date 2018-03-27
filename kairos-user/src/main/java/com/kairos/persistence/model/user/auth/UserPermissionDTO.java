package com.kairos.persistence.model.user.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by prerna on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPermissionDTO {
    private Set<HashMap<String, Object>> permissions;

    private Long lastSelectedChildOrgId;

    private Long lastSelectedParentOrgId;

    public UserPermissionDTO(){
        // default constructor
    }

    public UserPermissionDTO(Set<HashMap<String, Object>> permissions, Long lastSelectedChildOrgId, Long lastSelectedParentOrgId){
        this.permissions = permissions;
        this.lastSelectedChildOrgId = lastSelectedChildOrgId;
        this.lastSelectedParentOrgId = lastSelectedParentOrgId;
    }

    public Set<HashMap<String, Object>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<HashMap<String, Object>> permissions) {
        this.permissions = permissions;
    }

    public Long getLastSelectedChildOrgId() {
        return lastSelectedChildOrgId;
    }

    public void setLastSelectedChildOrgId(Long lastSelectedChildOrgId) {
        this.lastSelectedChildOrgId = lastSelectedChildOrgId;
    }

    public Long getLastSelectedParentOrgId() {
        return lastSelectedParentOrgId;
    }

    public void setLastSelectedParentOrgId(Long lastSelectedParentOrgId) {
        this.lastSelectedParentOrgId = lastSelectedParentOrgId;
    }
}
