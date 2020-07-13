package com.kairos.persistence.repository.activity;

import com.kairos.persistence.model.activity.StaffActivityDetails;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StaffActivityDetailsMongoRepository extends MongoBaseRepository<StaffActivityDetails, BigInteger> {


    List<StaffActivityDetails> findByDeletedFalseAndStaffIdAndActivityIdIn(Long staffId, List<BigInteger> activityIds);

    List<StaffActivityDetails> findByDeletedFalseAndStaffId(Long staffId);
}
