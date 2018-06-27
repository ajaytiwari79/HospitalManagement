package com.kairos.persistence.repository.solver_config;

import com.kairos.planner.solverconfig.SolverConfigDTO;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public interface CustomSolverConfigRepository {

    SolverConfigDTO getoneById(BigInteger solverConfigId);
    List<SolverConfigDTO> getAllByUnitId(Long unitId);
    Boolean existsSolverConfigByNameAndUnitId(Long unitId, String name);
    Boolean existsSolverConfigByNameAndUnitIdAndSolverConfigId(Long unitId, String name, BigInteger solverConfigId);
}
