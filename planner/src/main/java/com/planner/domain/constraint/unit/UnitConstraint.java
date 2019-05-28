package com.planner.domain.constraint.unit;

import com.kairos.enums.constraint.ConstraintLevel;
import com.planner.domain.constraint.common.Constraint;

import java.math.BigInteger;

public class UnitConstraint extends Constraint{

    private BigInteger parentCountryConstraintId;//copiedFrom
    private Long unitId;

    public UnitConstraint() {
    }

    public UnitConstraint(ConstraintLevel constraintLevel, int penalty, String name) {
        this.name = name;
        this.constraintLevel = constraintLevel;
        this.penalty = penalty;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getParentCountryConstraintId() {
        return parentCountryConstraintId;
    }

    public void setParentCountryConstraintId(BigInteger parentCountryConstraintId) {
        this.parentCountryConstraintId = parentCountryConstraintId;
    }
}
