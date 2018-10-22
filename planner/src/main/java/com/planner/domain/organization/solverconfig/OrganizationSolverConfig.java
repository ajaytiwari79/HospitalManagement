package com.planner.domain.organization.solverconfig;

import com.planner.domain.common.solverconfig.SolverConfig;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public class OrganizationSolverConfig extends SolverConfig{

    private BigInteger parentOrganizationSolverConfigId;//copiedFrom
    private Long unitId;

    //~ Getter/Setter
    public BigInteger getParentOrganizationSolverConfigId() {
        return parentOrganizationSolverConfigId;
    }

    public void setParentOrganizationSolverConfigId(BigInteger parentOrganizationSolverConfigId) {
        this.parentOrganizationSolverConfigId = parentOrganizationSolverConfigId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
