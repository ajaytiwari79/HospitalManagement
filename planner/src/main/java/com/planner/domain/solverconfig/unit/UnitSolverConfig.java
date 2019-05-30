package com.planner.domain.solverconfig.unit;

import com.planner.domain.solverconfig.common.SolverConfig;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class UnitSolverConfig extends SolverConfig{

    private Long unitId;
    private BigInteger parentCountrySolverConfigId;//copiedFrom
    private List<Long> organizationSubServiceIds;

}
