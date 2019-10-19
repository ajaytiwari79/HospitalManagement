package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SolverConfigConstraintWrapper {

    private List<DefaultContraintsDTO> defaultConstraints;
    private List<SolverConfigDTO> solverConfigs;
}
