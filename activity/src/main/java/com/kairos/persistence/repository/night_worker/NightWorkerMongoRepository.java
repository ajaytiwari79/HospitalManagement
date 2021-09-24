package com.kairos.persistence.repository.night_worker;

import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public interface NightWorkerMongoRepository extends MongoBaseRepository<NightWorker, BigInteger>, CustomNightWorkerMongoRepository{

    @Query(value = "{ staffId:?0, deleted:false }")
    NightWorker findByStaffId(Long staffId);

    @Query(value = "{ staffId:{$in:?0}, deleted:false }")
    List<NightWorker> findByStaffIds(Collection<Long> staffIds);

    @Query(value = "{ staffId:?0, unitId:?1, deleted:false }")
    NightWorker findByStaffAndUnitId(Long staffId, Long unitId);

    @Query(value = "{ 'unitId':{ '$in' : ?0 } ,deleted:false}")
    List<NightWorker> findByUnitIds(List<Long> unitIds);
}
