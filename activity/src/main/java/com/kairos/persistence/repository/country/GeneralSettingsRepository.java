package com.kairos.persistence.repository.country;

import com.kairos.persistence.model.country.GeneralSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface GeneralSettingsRepository extends MongoBaseRepository<GeneralSettings, BigInteger> {

    @Query("{countryId:?0,deleted:false}")
    GeneralSettings findByDeletedFalseAndCountryId(Long countryId);

    @Query("{unitId:?0,deleted:false}")
    GeneralSettings findByDeletedFalseAndUnitId(Long unitId);
}
