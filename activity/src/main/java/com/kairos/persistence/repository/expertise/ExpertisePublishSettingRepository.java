package com.kairos.persistence.repository.expertise;

import com.kairos.persistence.model.expertise.ExpertisePublishSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface ExpertisePublishSettingRepository extends MongoBaseRepository<ExpertisePublishSetting, BigInteger>  {

    @Query(value = "{ expertiseId:?0, countryId:?1,deleted:false}")
    ExpertisePublishSetting findByExpertiseIdAndCountryId(Long expertiseId, Long countryId);

    @Query(value = "{ expertiseId:{$in:?0},countryId:{$exists:true}, deleted:false}")
    List<ExpertisePublishSetting> findAllByCountryAndExpertiseIds(List<Long> expertiseIds);

    @Query(value = "{ expertiseId:?0, unitId:?1,deleted:false}")
    ExpertisePublishSetting findByExpertiseIdAndUnitId(Long expertiseId, Long unitId);

    @Query(value = "{unitId:?1,deleted:false}")
    List<ExpertisePublishSetting> findByUnitId(Long unitId);
}
