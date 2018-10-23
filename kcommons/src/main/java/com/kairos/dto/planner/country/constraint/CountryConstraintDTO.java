package com.kairos.dto.planner.country.constraint;

import com.kairos.dto.planner.constarints.ConstraintDTO;

public class CountryConstraintDTO extends ConstraintDTO {
    //~
    private Long countryId;
    private Long parentCountryConstraintId;
    private Long organizationServiceId;
    private Long organizationSubServiceId;

    //======================================================

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getParentCountryConstraintId() {
        return parentCountryConstraintId;
    }

    public void setParentCountryConstraintId(Long parentCountryConstraintId) {
        this.parentCountryConstraintId = parentCountryConstraintId;
    }

    public Long getOrganizationServiceId() {
        return organizationServiceId;
    }

    public void setOrganizationServiceId(Long organizationServiceId) {
        this.organizationServiceId = organizationServiceId;
    }

    public Long getOrganizationSubServiceId() {
        return organizationSubServiceId;
    }

    public void setOrganizationSubServiceId(Long organizationSubServiceId) {
        this.organizationSubServiceId = organizationSubServiceId;
    }
}
