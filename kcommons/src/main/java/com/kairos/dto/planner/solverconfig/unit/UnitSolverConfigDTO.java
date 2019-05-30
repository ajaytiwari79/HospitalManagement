package com.kairos.dto.planner.solverconfig.unit;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UnitSolverConfigDTO extends SolverConfigDTO {
    private Long unitId;
    private Long parentCountrySolverConfigId;
    private List<Long> organizationSubServiceIds;


}
