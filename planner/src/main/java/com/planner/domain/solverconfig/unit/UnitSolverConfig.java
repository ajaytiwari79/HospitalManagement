package com.planner.domain.solverconfig.unit;

import com.planner.domain.solverconfig.common.SolverConfig;

import java.math.BigInteger;


public class UnitSolverConfig extends SolverConfig{

    private Long unitId;
    private BigInteger parentCountrySolverConfigId;//copiedFrom

    //~ Getter/Setter

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getParentCountrySolverConfigId() {
        return parentCountrySolverConfigId;
    }

    public void setParentCountrySolverConfigId(BigInteger parentCountrySolverConfigId) {
        this.parentCountrySolverConfigId = parentCountrySolverConfigId;
    }
}
