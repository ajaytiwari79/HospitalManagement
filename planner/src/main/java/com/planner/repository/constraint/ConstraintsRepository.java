package com.planner.repository.constraint;

import com.planner.domain.constraint.common.Constraint;
import com.planner.domain.constraint.country.CountryConstraint;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

/**
 * Common for CountryConstraint and UnitConstraint
 */
public interface ConstraintsRepository extends MongoBaseRepository<Constraint,BigInteger>{

    @Query(value = "{ '_id':{$in:?0}, 'deleted':false}")
    List<CountryConstraint> findAllCountryConstraintByIds(List<BigInteger> ids);

    @Query(value = "{ '_id':{$in:?0}, 'deleted':false}")
    List<UnitConstraint> findAllUnitConstraintByIds(List<BigInteger> ids);
}
