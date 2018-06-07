package com.kairos.dto.solverconfig;

import com.kairos.activity.persistence.enums.WTATemplateType;

import java.math.BigInteger;
import java.util.List;

public class SolverConfigDTO {
    private BigInteger id;
    private List<WTATemplateType> templateTypes;

    public SolverConfigDTO() {
    }

    private long terminationSeconds;

    public SolverConfigDTO(BigInteger id, List<WTATemplateType> templateTypes, long terminationSeconds) {
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
