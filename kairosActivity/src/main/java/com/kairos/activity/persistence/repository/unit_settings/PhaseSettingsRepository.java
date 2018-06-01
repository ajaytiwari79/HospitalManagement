package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.PhaseSettings;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.unit_settings.PhaseSettingsDTO;

import java.math.BigInteger;
import java.util.List;

public interface PhaseSettingsRepository extends MongoBaseRepository<PhaseSettings,BigInteger> {

    List<PhaseSettingsDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

}
