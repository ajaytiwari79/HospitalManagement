package com.kairos.persistence.repository.solver_config;

import com.kairos.persistence.model.solver_config.Constraint;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.enums.solver_config.PlanningType;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Repository
public interface ConstraintRepository extends MongoBaseRepository<Constraint,BigInteger>{

    @Query("{unitId:?0,planningType:?1}")
    List<ConstraintDTO> getAllVRPPlanningConstraints(Long unitId, PlanningType planningType);

}
