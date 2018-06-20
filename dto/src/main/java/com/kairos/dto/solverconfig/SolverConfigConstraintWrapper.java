package com.kairos.dto.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolverConfigConstraintWrapper {

    private List<ConstraintDTO> defaultconstraints;
    private List<SolverConfigDTO> solverConfigs;

    public SolverConfigConstraintWrapper() {
    }

    public SolverConfigConstraintWrapper(List<ConstraintDTO> defaultconstraints, List<SolverConfigDTO> solverConfigs) {
        this.defaultconstraints = defaultconstraints;
        this.solverConfigs = solverConfigs;
    }

    public List<ConstraintDTO> getDefaultconstraints() {
        return defaultconstraints;
    }

    public void setDefaultconstraints(List<ConstraintDTO> defaultconstraints) {
        this.defaultconstraints = defaultconstraints;
    }

    public List<SolverConfigDTO> getSolverConfigs() {
        return solverConfigs;
    }

    public void setSolverConfigs(List<SolverConfigDTO> solverConfigs) {
        this.solverConfigs = solverConfigs;
    }
}
