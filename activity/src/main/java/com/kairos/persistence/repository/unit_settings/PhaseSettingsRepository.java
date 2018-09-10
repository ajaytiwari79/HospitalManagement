package com.kairos.persistence.repository.unit_settings;

import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.activity.unit_settings.PhaseSettingsDTO;

import java.math.BigInteger;
import java.util.List;

public interface PhaseSettingsRepository extends MongoBaseRepository<PhaseSettings,BigInteger> {

    List<PhaseSettingsDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

    PhaseSettings getPhaseSettingsByUnitIdAndPhaseId(Long unitId,BigInteger phaseId);

}
