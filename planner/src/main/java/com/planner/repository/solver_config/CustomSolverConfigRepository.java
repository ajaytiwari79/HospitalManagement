package com.planner.repository.solver_config;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.domain.solverconfig.unit.UnitSolverConfig;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 12/11/18
 */

public interface CustomSolverConfigRepository {

    SolverConfigDTO getSolverConfigWithConstraints(BigInteger solverConfigId);

    List<SolverConfigDTO> getAllSolverConfigWithConstraints(boolean checkForCountry, Long countryOrUnitId);

    SolverConfig getSolverConfigById(BigInteger solverConfigId, boolean checkForCountry);

    List<UnitSolverConfig> getAllSolverConfigByParentId(BigInteger solverConfigId);
}
