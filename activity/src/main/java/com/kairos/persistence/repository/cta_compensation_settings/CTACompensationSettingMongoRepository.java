package com.kairos.persistence.repository.cta_compensation_settings;

import com.kairos.persistence.cta_compensation_setting.CTACompensationSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Repository
public interface CTACompensationSettingMongoRepository extends MongoBaseRepository<CTACompensationSetting, BigInteger>{


    @Query("{deleted:false,expertiseId:{$in:?0}}")
    List<CTACompensationSetting> findAllByExpertiseIds(Collection<Long> expertiseIds);

    @Query("{deleted:false,expertiseId:?0}")
    CTACompensationSetting findAllByDeletedFalseAndExpertiseId(Long expertiseId);

    @Query("{countryId:?0,deleted:false,expertiseId:?1}")
    CTACompensationSetting findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(Long countryId, Long expertiseId);

    @Query("{countryId:?0,deleted:false}")
    List<CTACompensationSetting> findByDeletedFalseAndCountryId(Long countryId);

    @Query("{unitId:?0,deleted:false,expertiseId:?1}")
    CTACompensationSetting findByDeletedFalseAndUnitIdAndExpertiseIdAndPrimaryTrue(Long unitId, Long expertiseId);

}
