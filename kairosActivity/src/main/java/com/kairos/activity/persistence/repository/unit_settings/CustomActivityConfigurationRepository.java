package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.ActivityConfiguration;

import java.math.BigInteger;

public interface CustomActivityConfigurationRepository {
    ActivityConfiguration findAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);
    ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);
}
