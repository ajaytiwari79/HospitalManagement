package com.kairos.user.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by prerna on 26/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationSelectionDTO {

    private Long lastSelectedParentOrgId;
    private Long lastSelectedChildOrgId;

    public Long getLastSelectedParentOrgId() {
        return lastSelectedParentOrgId;
    }

    public void setLastSelectedParentOrgId(Long lastSelectedParentOrgId) {
        this.lastSelectedParentOrgId = lastSelectedParentOrgId;
    }

    public Long getLastSelectedChildOrgId() {
        return lastSelectedChildOrgId;
    }

    public void setLastSelectedChildOrgId(Long lastSelectedChildOrgId) {
        this.lastSelectedChildOrgId = lastSelectedChildOrgId;
    }
}
