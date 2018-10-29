package com.planner.domain.constraint.country;

import com.kairos.enums.constraint.ConstraintLevel;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.planner.domain.constraint.common.Constraint;

import java.math.BigInteger;

public class CountryConstraint extends Constraint {
   //~
    private Long countryId;
    private Long organizationServiceId;
    private Long organizationSubServiceId;

    //======================================================
    //~ Constructors
    public CountryConstraint(){}

    public CountryConstraint(BigInteger id, String name, String description, ConstraintType constraintType, ConstraintSubType constraintSubType, ConstraintLevel constraintLevel, int penalty, BigInteger planningProblemId, BigInteger parentCountryConstraintId, Long countryId, Long organizationServiceId, Long organizationSubServiceId) {
        super(id, name, description, constraintType, constraintSubType, constraintLevel, penalty, planningProblemId, parentCountryConstraintId);
        this.countryId = countryId;
        this.organizationServiceId = organizationServiceId;
        this.organizationSubServiceId = organizationSubServiceId;
    }
    //=======================================================

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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
