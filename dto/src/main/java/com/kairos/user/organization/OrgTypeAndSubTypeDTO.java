package com.kairos.user.organization;

public class OrgTypeAndSubTypeDTO {
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private Long countryId;

    public OrgTypeAndSubTypeDTO() {
        //Default Constructor
    }

    public OrgTypeAndSubTypeDTO(Long organizationTypeId, Long organizationSubTypeId, Long countryId) {
        this.organizationTypeId = organizationTypeId;
        this.organizationSubTypeId = organizationSubTypeId;
        this.countryId = countryId;
    }

    public Long getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(Long organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public Long getOrganizationSubTypeId() {
        return organizationSubTypeId;
    }

    public void setOrganizationSubTypeId(Long organizationSubTypeId) {
        this.organizationSubTypeId = organizationSubTypeId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
