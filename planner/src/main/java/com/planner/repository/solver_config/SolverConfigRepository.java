package com.planner.repository.config;

import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface SolverConfigRepository  extends MongoBaseRepository<SolverConfig,String> {

}
