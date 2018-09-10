package com.kairos.dto.planner.solverconfig;

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

    private List<DefaultContraintsDTO> defaultConstraints;
    private List<SolverConfigDTO> solverConfigs;

    public SolverConfigConstraintWrapper() {
    }

    public SolverConfigConstraintWrapper(List<DefaultContraintsDTO> defaultConstraints, List<SolverConfigDTO> solverConfigs) {
        this.defaultConstraints = defaultConstraints;
        this.solverConfigs = solverConfigs;
    }

    public List<DefaultContraintsDTO> getDefaultConstraints() {
        return defaultConstraints;
    }

    public void setDefaultConstraints(List<DefaultContraintsDTO> defaultConstraints) {
        this.defaultConstraints = defaultConstraints;
    }

    public List<SolverConfigDTO> getSolverConfigs() {
        return solverConfigs;
    }

    public void setSolverConfigs(List<SolverConfigDTO> solverConfigs) {
        this.solverConfigs = solverConfigs;
    }
}
