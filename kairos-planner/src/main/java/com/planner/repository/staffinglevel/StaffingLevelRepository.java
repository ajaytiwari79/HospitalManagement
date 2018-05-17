package com.planner.repository.staffinglevel;

import com.planner.domain.staffinglevel.StaffingLevel;
import com.planner.repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface StaffingLevelRepository extends MongoBaseRepository<StaffingLevel,String> {
}
