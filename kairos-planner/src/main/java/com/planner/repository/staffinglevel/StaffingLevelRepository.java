package com.planner.repository.staffinglevel;

import com.planner.domain.staffinglevel.StaffingLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
public interface StaffingLevelRepository extends MongoRepository<StaffingLevel,BigInteger> {
}
