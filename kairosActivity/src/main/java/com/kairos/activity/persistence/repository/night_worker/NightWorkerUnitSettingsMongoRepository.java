package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorkerUnitSettings;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface NightWorkerUnitSettingsMongoRepository extends MongoBaseRepository<NightWorkerUnitSettings, BigInteger> {

    @Query(value = "{ unitId:?0 ,deleted:false}")
    NightWorkerUnitSettings findByUnit(Long unitId);

    @Query(value = "{ 'unitId':{ '$in' : ?0 } ,deleted:false}")
    List<NightWorkerUnitSettings> findByUnitIds(List<Long> unitIds);

}
