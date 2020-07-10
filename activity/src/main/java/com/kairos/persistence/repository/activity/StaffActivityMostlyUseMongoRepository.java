package com.kairos.persistence.repository.activity;

import com.kairos.persistence.model.activity.StaffActivityMostlyUse;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StaffActivityMostlyUseMongoRepository extends MongoBaseRepository<StaffActivityMostlyUse, BigInteger> {


    List<StaffActivityMostlyUse> findByDeletedFalseAndStaffIdAndActivityIdIn(Long staffId, List<BigInteger> activityIds);

    List<StaffActivityMostlyUse> findByDeletedFalseAndStaffId(Long staffId);
}
