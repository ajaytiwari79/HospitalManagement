package com.kairos.persistence.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by prerna on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserOrganizationsDTO {
    private List<OrganizationWrapper> organizations;

    private Long lastSelectedChildOrgId;

    private Long lastSelectedParentOrgId;

    private Long systemLangugaeId;

    public UserOrganizationsDTO(){
        // default constructor
    }

    public UserOrganizationsDTO(List<OrganizationWrapper> organizations, Long lastSelectedChildOrgId, Long lastSelectedParentOrgId, Long systemLangugaeId){
        this.organizations = organizations;
        this.lastSelectedChildOrgId = lastSelectedChildOrgId;
        this.lastSelectedParentOrgId = lastSelectedParentOrgId;
        this.systemLangugaeId = systemLangugaeId;
    }

    public List<OrganizationWrapper> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationWrapper> organizations) {
        this.organizations = organizations;
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

    public Long getSystemLangugaeId() {
        return systemLangugaeId;
    }

    public void setSystemLangugaeId(Long systemLangugaeId) {
        this.systemLangugaeId = systemLangugaeId;
    }
}
