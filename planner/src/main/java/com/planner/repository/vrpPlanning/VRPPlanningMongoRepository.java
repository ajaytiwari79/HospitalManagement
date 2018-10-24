package com.planner.repository.vrpPlanning;

import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Repository
public interface VRPPlanningMongoRepository extends MongoBaseRepository<VRPPlanningSolution,String> {


    @Query("{solverConfigId:?0}")
    VRPPlanningSolution getSolutionBySolverConfigId(BigInteger solverConfigId);

}
