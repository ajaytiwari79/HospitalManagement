package com.planner.repository.config;

import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolverConfigRepository   extends MongoBaseRepository<SolverConfig,String> {


}
