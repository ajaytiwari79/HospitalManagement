package com.kairos.dto.planner.solverconfig;

import com.kairos.enums.wta.WTATemplateType;

import java.math.BigInteger;
import java.util.List;

public class SolverConfigWTADTO {
    private BigInteger id;
    private List<WTATemplateType> templateTypes;

    public SolverConfigWTADTO() {
    }

    private long terminationSeconds;

    public SolverConfigWTADTO(BigInteger id, List<WTATemplateType> templateTypes, long terminationSeconds) {
        this.id = id;
        this.templateTypes = templateTypes;
        this.terminationSeconds = terminationSeconds;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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
}
