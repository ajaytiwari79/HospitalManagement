package com.kairos.activity.persistence.repository.solver_config;

import com.kairos.activity.persistence.model.solver_config.SolverConfig;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Repository
public interface SolverConfigRepository extends MongoBaseRepository<SolverConfig,BigInteger>, CustomSolverConfigRepository{

    @Query("{unitId:?0,deleted:false}")
    List<SolverConfigDTO> getAllByUnitId(Long unitId);


}
