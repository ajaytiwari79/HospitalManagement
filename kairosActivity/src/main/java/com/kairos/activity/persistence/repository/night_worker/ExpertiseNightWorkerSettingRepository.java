package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface ExpertiseNightWorkerSettingRepository extends MongoBaseRepository<ExpertiseNightWorkerSetting, BigInteger> {

    @Query(value = "{ countryId:?0 ,expertiseId:?1, deleted:false}")
    ExpertiseNightWorkerSetting findByCountryAndExpertise(Long countryId, Long expertiseId);
}
