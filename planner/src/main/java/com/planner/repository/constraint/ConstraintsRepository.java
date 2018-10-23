package com.planner.repository.constraint;

import com.planner.domain.constraint.common.Constraint;
import com.planner.repository.common.MongoBaseRepository;

import java.math.BigInteger;

/**
 * Common for CountryConstraint and OrganizationConstraint
 */
public interface ConstraintsRepository extends MongoBaseRepository<Constraint,BigInteger>{
}
