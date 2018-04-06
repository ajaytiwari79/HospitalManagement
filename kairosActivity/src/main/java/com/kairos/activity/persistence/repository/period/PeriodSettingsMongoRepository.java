package com.kairos.activity.persistence.repository.period;

import com.kairos.activity.persistence.model.period.PeriodSettings;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

/**
 * Created by prerna on 30/3/18.
 */
public interface PeriodSettingsMongoRepository extends MongoBaseRepository<PeriodSettings, BigInteger>, CustomPeriodSettingsMongoRepository {

    @Query(value = "{ unitId:?0 ,name:?1 ,deleted:false}")
    PeriodSettings findByUnit(Long unitId, boolean deleted);
}
