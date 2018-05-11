package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

/**
 * Created by prerna on 8/5/18.
 */
public interface NightWorkerMongoRepository extends MongoBaseRepository<NightWorker, BigInteger>, CustomNightWorkerMongoRepository{

    @Query(value = "{ staffId:?0, deleted:false }")
    NightWorker findByStaffId(Long staffId);
}
