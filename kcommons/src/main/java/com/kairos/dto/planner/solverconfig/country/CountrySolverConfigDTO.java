package com.kairos.dto.planner.solverconfig.country;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CountrySolverConfigDTO extends SolverConfigDTO{


    private Long countryId;
    private List<Long> organizationSubServiceIds;
}
