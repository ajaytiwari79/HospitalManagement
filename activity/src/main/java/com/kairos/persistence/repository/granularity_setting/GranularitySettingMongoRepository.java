package com.kairos.persistence.repository.granularity_setting;

import com.kairos.dto.activity.granularity_setting.GranularitySettingDTO;
import com.kairos.persistence.model.granularity_setting.GranularitySetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface GranularitySettingMongoRepository extends MongoBaseRepository<GranularitySetting, BigInteger> {

    GranularitySetting findByCountryIdAndOrganisationTypeIdAndDeletedFalse(Long countryId, Long organisationTypeId);

    List<GranularitySettingDTO> findAllByCountryIdAndDeletedFalse(Long countryId);
}
