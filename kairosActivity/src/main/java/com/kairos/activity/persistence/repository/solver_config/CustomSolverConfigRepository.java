package com.kairos.activity.persistence.repository.solver_config;

import com.kairos.activity.persistence.model.solver_config.SolverConfig;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import org.springframework.data.mongodb.repository.Query;

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
