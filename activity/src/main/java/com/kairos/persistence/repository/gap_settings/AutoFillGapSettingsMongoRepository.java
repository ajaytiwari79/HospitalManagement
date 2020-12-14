package com.kairos.persistence.repository.gap_settings;

import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AutoFillGapSettingsMongoRepository extends MongoBaseRepository<AutoFillGapSettings, BigInteger> {

    @Query("{'deleted' : false,'countryId':?0}")
    List<AutoFillGapSettings> getAllByCountryId(Long countryId);

    @Query("{'deleted' : false,'countryId':?0}")
    List<AutoFillGapSettings> getAllByUnitId(Long unitId);

    AutoFillGapSettings findByUnitIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndAutoGapFillingScenario(Long unitId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario);

    AutoFillGapSettings findByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndAutoGapFillingScenario(Long countryId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario);
}
