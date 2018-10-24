package com.planner.domain.solverconfig.unit;

import com.planner.domain.solverconfig.common.SolverConfig;

import java.math.BigInteger;


public class UnitSolverConfig extends SolverConfig{

    private BigInteger parentUnitSolverConfigId;//copiedFrom
    private Long unitId;

    //~ Getter/Setter
    public BigInteger getParentUnitSolverConfigId() {
        return parentUnitSolverConfigId;
    }

    public void setParentUnitSolverConfigId(BigInteger parentUnitSolverConfigId) {
        this.parentUnitSolverConfigId = parentUnitSolverConfigId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
