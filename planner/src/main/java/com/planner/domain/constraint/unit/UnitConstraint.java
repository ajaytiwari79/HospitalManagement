package com.planner.domain.constraint.unit;

import com.planner.domain.constraint.common.Constraint;

import java.math.BigInteger;

public class UnitConstraint extends Constraint{

    //~
    private BigInteger parentUnitConstraintId;//copiedFrom
    private Long unitId;


    //====================================================

    public BigInteger getParentUnitConstraintId() {
        return parentUnitConstraintId;
    }

    public void setParentUnitConstraintId(BigInteger parentUnitConstraintId) {
        this.parentUnitConstraintId = parentUnitConstraintId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
