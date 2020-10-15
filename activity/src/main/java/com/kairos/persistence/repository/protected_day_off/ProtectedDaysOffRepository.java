package com.kairos.persistence.repository.protected_day_off;

import com.kairos.persistence.model.protected_day_off.ProtectedDaysOffSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ProtectedDaysOffRepository extends MongoBaseRepository<ProtectedDaysOffSetting, BigInteger> {

}
