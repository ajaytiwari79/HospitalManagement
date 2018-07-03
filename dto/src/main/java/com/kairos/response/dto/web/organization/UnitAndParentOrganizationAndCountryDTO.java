package com.kairos.response.dto.web.organization;

public class UnitAndParentOrganizationAndCountryDTO {

    private Long unitId;
    private Long parentOrganizationId;
    private Long countryId;

    public UnitAndParentOrganizationAndCountryDTO(){
        // default constructor
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
