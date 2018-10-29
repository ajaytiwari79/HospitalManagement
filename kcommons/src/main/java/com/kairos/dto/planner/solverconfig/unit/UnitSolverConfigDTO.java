package com.kairos.dto.planner.solverconfig.unit;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;

public class UnitSolverConfigDTO extends SolverConfigDTO {
    private Long unitId;
    private Long parentUnitSolverConfigId;

    //~ Getter/Setter
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentUnitSolverConfigId() {
        return parentUnitSolverConfigId;
    }

    public void setParentUnitSolverConfigId(Long parentUnitSolverConfigId) {
        this.parentUnitSolverConfigId = parentUnitSolverConfigId;
    }
}
