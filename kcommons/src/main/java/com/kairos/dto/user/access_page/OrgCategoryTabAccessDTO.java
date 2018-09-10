package com.kairos.dto.user.access_page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.OrganizationCategory;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 28/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgCategoryTabAccessDTO {

    @NotNull(message = "error.org.category.notnull")
    private OrganizationCategory organizationCategory;
    @NotNull(message = "error.org.access.status.notnull")
    private Boolean accessStatus;

    public OrgCategoryTabAccessDTO(){
        // default constructor
    }

    public OrgCategoryTabAccessDTO(OrganizationCategory organizationCategory, Boolean accessStatus){
        this.organizationCategory = organizationCategory;
        this.accessStatus = accessStatus;
    }
    public OrganizationCategory getOrganizationCategory() {
        return organizationCategory;
    }

    public void setOrganizationCategory(OrganizationCategory organizationCategory) {
        this.organizationCategory = organizationCategory;
    }

    public Boolean isAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(Boolean accessStatus) {
        this.accessStatus = accessStatus;
    }
}
