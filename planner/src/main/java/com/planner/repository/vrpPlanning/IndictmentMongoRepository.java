package com.planner.repository.vrpPlanning;

import com.kairos.dto.planner.vrp.vrpPlanning.VRPIndictmentDTO;
import com.planner.domain.vrpPlanning.VRPIndictment;
import com.planner.repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 9/7/18
 */
@Repository
public interface IndictmentMongoRepository extends MongoBaseRepository<VRPIndictment,String> {

    @Query("{solverConfigId:?0}")
    VRPIndictmentDTO getIndictmentBySolverConfigId(BigInteger solverConfigId);

    @Query("{solverConfigId:{$in:?0}}")
    List<VRPIndictment> getAllIndictmentBySolverConfigId(BigInteger solverConfigId);

}
