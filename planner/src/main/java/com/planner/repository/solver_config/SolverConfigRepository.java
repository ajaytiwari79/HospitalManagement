package com.planner.repository.solver_config;

import com.planner.domain.constraint.country.CountryConstraint;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface SolverConfigRepository  extends MongoBaseRepository<SolverConfig,BigInteger>,CustomSolverConfigRepository {


}
