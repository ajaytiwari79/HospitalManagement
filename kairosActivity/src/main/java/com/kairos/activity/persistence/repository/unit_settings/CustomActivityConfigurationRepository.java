package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.ActivityConfiguration;

import java.math.BigInteger;
import java.util.List;

public interface CustomActivityConfigurationRepository {
    ActivityConfiguration findAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

    ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

    List<ActivityConfiguration> findPresenceConfigurationByUnitIdAndPhaseId(Long unitId);

    List<ActivityConfiguration> findAbsenceConfigurationByUnitIdAndPhaseId(Long unitId);

}
