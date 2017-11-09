package com.kairos.client.dto.organization;

/**
 * Created by prerna on 8/11/17.
 */
public class OrganizationEmploymentTypeDTO {
    private long employmentTypeId;
    private boolean allowedForContactPerson;

    public long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public boolean isAllowedForContactPerson() {
        return allowedForContactPerson;
    }

    public void setAllowedForContactPerson(boolean allowedForContactPerson) {
        this.allowedForContactPerson = allowedForContactPerson;
    }
}
