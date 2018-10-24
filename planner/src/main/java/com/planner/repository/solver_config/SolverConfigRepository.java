package com.planner.repository.solver_config;

import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface SolverConfigRepository  extends MongoBaseRepository<SolverConfig,BigInteger> {

}
