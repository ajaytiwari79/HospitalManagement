package com.planner.repository.config;

import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolverConfigRepository   extends MongoBaseRepository<SolverConfig,String> {

}
