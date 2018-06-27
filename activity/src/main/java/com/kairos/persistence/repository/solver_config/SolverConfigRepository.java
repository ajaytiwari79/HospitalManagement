package com.kairos.persistence.repository.solver_config;

import com.kairos.persistence.model.solver_config.SolverConfig;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Repository
public interface SolverConfigRepository extends MongoBaseRepository<SolverConfig,BigInteger>, CustomSolverConfigRepository{




}
