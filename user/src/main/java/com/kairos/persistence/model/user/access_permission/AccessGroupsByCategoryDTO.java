package com.kairos.persistence.model.user.access_permission;

import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.AccessGroupQueryResult;

import java.util.List;

/**
 * Created by prerna on 25/4/18.
 */
public class AccessGroupsByCategoryDTO {
    OrganizationCategory organizationCategory;
    List<AccessGroupQueryResult> accessGroups;

    public AccessGroupsByCategoryDTO(){
        // default constructor
    }

    public AccessGroupsByCategoryDTO(OrganizationCategory organizationCategory, List<AccessGroupQueryResult> accessGroups){
        this.organizationCategory = organizationCategory;
        this.accessGroups = accessGroups;
    }


    public OrganizationCategory getOrganizationCategory() {
        return organizationCategory;
    }

    public void setOrganizationCategory(OrganizationCategory organizationCategory) {
        this.organizationCategory = organizationCategory;
    }

    public List<AccessGroupQueryResult> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<AccessGroupQueryResult> accessGroups) {
        this.accessGroups = accessGroups;
    }
}
