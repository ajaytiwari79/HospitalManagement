package com.planner.repository.constraint;

import com.kairos.enums.constraint.ConstraintSubType;
import com.planner.domain.constraint.activity.ActivityConstraint;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.repository.common.MongoBaseRepository;

import java.math.BigInteger;
import java.util.List;

public interface UnitConstraintRepository extends MongoBaseRepository<UnitConstraint,Long> {

    List<UnitConstraint> findAllByUnitIdAndDeletedFalse(Long unitId);



    UnitConstraint findByUnitIdAndConstraintSubTypeAndDeletedFalse(Long unitId, ConstraintSubType constraintSubType);


}
