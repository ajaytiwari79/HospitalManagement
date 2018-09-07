package com.kairos.persistence.model.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * CreatedBy vipulpandey on 6/9/18
 **/
@QueryResult
public class UnitModuleAccess {
    private Long unitId;
    private List<Long> accessibleModules;

    public UnitModuleAccess() {
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<Long> getAccessibleModules() {
        return accessibleModules;
    }

    public void setAccessibleModules(List<Long> accessibleModules) {
        this.accessibleModules = accessibleModules;
    }
}
