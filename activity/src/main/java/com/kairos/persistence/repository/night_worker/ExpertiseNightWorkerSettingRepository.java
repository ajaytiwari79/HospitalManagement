package com.kairos.persistence.repository.night_worker;

import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface ExpertiseNightWorkerSettingRepository extends MongoBaseRepository<ExpertiseNightWorkerSetting, BigInteger> {

    @Query(value = "{ expertiseId:?0, deleted:false}")
    ExpertiseNightWorkerSetting findByExpertiseId(Long expertiseId);

    @Query(value = "{ expertiseId:{$in:?0}, deleted:false}")
    List<ExpertiseNightWorkerSetting> findAllByCountryAndExpertiseIds(List<Long> expertiseIds);
}
