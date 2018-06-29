package com.kairos.persistence.repository.night_worker;

import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface ExpertiseNightWorkerSettingRepository extends MongoBaseRepository<ExpertiseNightWorkerSetting, BigInteger> {

    @Query(value = "{ countryId:?0 ,expertiseId:?1, deleted:false}")
    ExpertiseNightWorkerSetting findByCountryAndExpertise(Long countryId, Long expertiseId);

    @Query(value = "{ expertiseId:{$in:?0}, deleted:false}")
    List<ExpertiseNightWorkerSetting> findAllByCountryAndExpertiseIds(List<Long> expertiseIds);
}
