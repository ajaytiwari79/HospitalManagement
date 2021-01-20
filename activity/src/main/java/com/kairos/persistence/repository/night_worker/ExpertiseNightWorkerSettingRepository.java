package com.kairos.persistence.repository.night_worker;

import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface ExpertiseNightWorkerSettingRepository extends MongoBaseRepository<ExpertiseNightWorkerSetting, BigInteger> {

    @Query(value = "{ expertiseId:?0, countryId:?1,deleted:false}")
    ExpertiseNightWorkerSetting findByExpertiseIdAndCountryId(Long expertiseId, Long countryId);

    @Query(value = "{ expertiseId:{$in:?0},countryId:{$exists:true}, deleted:false}")
    List<ExpertiseNightWorkerSetting> findAllByCountryAndExpertiseIds(List<Long> expertiseIds);

    @Cacheable(value = "findByExpertiseIdAndUnitId", key = "{#expertiseId, #unitId}", cacheManager = "cacheManager")
    @Query(value = "{ expertiseId:?0, unitId:?1,deleted:false}")
    ExpertiseNightWorkerSetting findByExpertiseIdAndUnitId(Long expertiseId, Long unitId);

    @Query(value = "{ expertiseId:?0, countryId:{$exists:true} ,deleted:false}")
    ExpertiseNightWorkerSetting findByExpertiseIdAndDeletedFalseAndCountryIdExistsTrue(Long expertiseId);

    @Query(value = "{ expertiseId:{$in:?0},unitId:{$exists:true},deleted:false}")
    List<ExpertiseNightWorkerSetting> findAllByExpertiseIdsOfUnit(Collection<Long> expertiseIds);

    @Query(value = "{ expertiseId:{$in:?0},countryId:{$exists:true},deleted:false}")
    List<ExpertiseNightWorkerSetting> findAllByExpertiseIdsOfCountry(Collection<Long> expertiseIds);

    @Query(value = "{ expertiseId:?0,deleted:false}")
    ExpertiseNightWorkerSetting findOneByExpertiseId(Long expertiseId);
}
