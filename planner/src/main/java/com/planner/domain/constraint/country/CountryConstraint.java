package com.planner.domain.constraint.country;

import com.planner.domain.constraint.common.Constraint;

import java.math.BigInteger;

public class CountryConstraint extends Constraint {
   //~
    private Long countryId;
    private Long organizationServiceId;
    private Long organizationSubServiceId;

    //======================================================

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getParentCountryConstraintId() {
        return parentCountryConstraintId;
    }

    public void setParentCountryConstraintId(BigInteger parentCountryConstraintId) {
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
