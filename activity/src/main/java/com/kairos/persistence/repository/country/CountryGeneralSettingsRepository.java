package com.kairos.persistence.repository.country;

import com.kairos.persistence.cta_compensation_setting.CTACompensationSetting;
import com.kairos.persistence.model.country.CountryGeneralSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CountryGeneralSettingsRepository extends MongoBaseRepository<CountryGeneralSettings, BigInteger> {

    @Query("{countryId:?0,deleted:false}")
    CountryGeneralSettings findByDeletedFalseAndCountryId(Long countryId);

    @Query("{unitId:?0,deleted:false}")
    CountryGeneralSettings findByDeletedFalseAndUnitId(Long unitId);
}
