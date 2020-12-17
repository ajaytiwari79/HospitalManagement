package com.kairos.persistence.repository.gap_settings;

import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AutoFillGapSettingsMongoRepository extends MongoBaseRepository<AutoFillGapSettings, BigInteger> {

    @Query("{'deleted' : false,'countryId':?0}")
    List<AutoFillGapSettings> getAllByCountryId(Long countryId);

    @Query("{'deleted' : false,'unitId':?0}")
    List<AutoFillGapSettings> getAllByUnitId(Long unitId);

    @Query("{'deleted' : false,'unitId':?0,'organizationTypeId':?1,'organizationSubTypeId':?2,'phaseId':?3}")
    AutoFillGapSettings getCurrentlyApplicableGapSettingsForUnit(Long unitId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId);

    @Query("{'deleted' : false,'countryId':?0,'organizationTypeId':?1,'organizationSubTypeId':?2,'phaseId':?3}")
    AutoFillGapSettings getCurrentlyApplicableGapSettingsForCountry(Long countryId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId);
}
