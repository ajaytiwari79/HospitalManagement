package com.kairos.dto.planner.solverconfig.unit;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;

public class UnitSolverConfigDTO extends SolverConfigDTO {
    private Long unitId;
    private Long parentCountrySolverConfigId;

    //~ Getter/Setter
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentCountrySolverConfigId() {
        return parentCountrySolverConfigId;
    }

    public void setParentCountrySolverConfigId(Long parentCountrySolverConfigId) {
        this.parentCountrySolverConfigId = parentCountrySolverConfigId;
    }
}
