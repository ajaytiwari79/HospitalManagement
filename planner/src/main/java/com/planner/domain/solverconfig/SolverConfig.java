package com.planner.domain.solverconfig;

import com.kairos.persistence.enums.WTATemplateType;
import com.planner.domain.MongoBaseEntity;

import java.util.List;

public class SolverConfig extends MongoBaseEntity {
    private List<WTATemplateType> templateTypes;
    private long terminationSeconds;
    private Long unitId;
    public SolverConfig(List<WTATemplateType> templateTypes, long terminationSeconds,Long unitId) {
        this.templateTypes = templateTypes;
        this.terminationSeconds = terminationSeconds;
        this.unitId=unitId;
    }

    public SolverConfig() {

    }
    public List<WTATemplateType> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<WTATemplateType> templateTypes) {
        this.templateTypes = templateTypes;
    }

    public long getTerminationSeconds() {
        return terminationSeconds;
    }

    public void setTerminationSeconds(long terminationSeconds) {
        this.terminationSeconds = terminationSeconds;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
