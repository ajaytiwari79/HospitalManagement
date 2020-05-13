package com.planner.repository.solver_config;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.domain.solverconfig.SolverConfig;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 12/11/18
 */

public interface CustomSolverConfigRepository {

    SolverConfigDTO getSolverConfigWithConstraints(BigInteger solverConfigId);

    List<SolverConfig> getAllSolverConfigWithConstraintsByCountryId( Long countryId);

    List<SolverConfig> getAllSolverConfigWithConstraintsByUnitId(Long unitId);

    SolverConfig getSolverConfigById(BigInteger solverConfigId);

    List<SolverConfig> getAllSolverConfigByParentId(BigInteger solverConfigId);
}
