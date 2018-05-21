package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorkerUnitSettings;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface NightWorkerUnitSettingsMongoRepository extends MongoBaseRepository<NightWorkerUnitSettings, BigInteger> {

    @Query(value = "{ unitId:?0 ,deleted:false}")
    NightWorkerUnitSettings findByUnit(Long unitId);
}
