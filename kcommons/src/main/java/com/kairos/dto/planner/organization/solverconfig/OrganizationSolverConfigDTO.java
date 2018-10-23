package com.kairos.dto.planner.organization.solverconfig;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;

public class OrganizationSolverConfigDTO extends SolverConfigDTO {

    private Long parentOrganizationSolverConfigId;
    private Long unitId;

    //~ Getter/Setter
    public Long getParentOrganizationSolverConfigId() {
        return parentOrganizationSolverConfigId;
    }

    public void setParentOrganizationSolverConfigId(Long parentOrganizationSolverConfigId) {
        this.parentOrganizationSolverConfigId = parentOrganizationSolverConfigId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
