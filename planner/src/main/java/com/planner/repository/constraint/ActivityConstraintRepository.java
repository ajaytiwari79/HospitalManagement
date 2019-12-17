package com.planner.repository.constraint;

import com.kairos.enums.constraint.ConstraintSubType;
import com.planner.domain.constraint.activity.ActivityConstraint;
import com.planner.domain.constraint.common.Constraint;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ActivityConstraintRepository extends MongoBaseRepository<ActivityConstraint, BigInteger> {

    List<ActivityConstraint>  findAllByActivityIdAndDeletedFalse(BigInteger activityId);

    ActivityConstraint findByActivityIdAndDeletedFalse(BigInteger activityId);

    BigInteger deleteByActivityId(BigInteger activityId);


   ActivityConstraint  findByActivityIdAndConstraintSubTypeAndDeletedFalse(BigInteger activityId, ConstraintSubType constraintSubType);

}
