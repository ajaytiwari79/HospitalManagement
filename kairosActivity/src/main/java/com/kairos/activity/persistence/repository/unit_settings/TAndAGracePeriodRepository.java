package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.TAndAGracePeriod;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface TAndAGracePeriodRepository extends MongoBaseRepository<TAndAGracePeriod ,BigInteger> {

    @Query(value ="{unitId:?0,deleted:false}")
    TAndAGracePeriod findByUnitId(Long unitId);

}
