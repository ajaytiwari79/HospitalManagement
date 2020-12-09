package com.kairos.persistence.repository.gap_settings;

import com.kairos.persistence.model.gap_settings.GapSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface GapSettingsMongoRepository extends MongoBaseRepository<GapSettings, BigInteger> {

    @Query("{'deleted' : false,'countryId':?0}")
    List<GapSettings> getAllByCountryId(Long countryId);

    @Query("{'deleted' : false,'countryId':?0}")
    List<GapSettings> getAllByUnitId(Long unitId);

    GapSettings findByUnitIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(Long unitId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario);

    GapSettings findByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(Long countryId, Long organizationTypeId, Long organizationSubTypeId, BigInteger phaseId, String gapFillingScenario);
}
