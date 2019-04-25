package com.kairos.persistence.repository.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;

import java.math.BigInteger;
import java.util.List;

public interface CustomActivityConfigurationRepository {

    ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

    List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId);

    List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId);

    List<ActivityConfiguration> findAllAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

    ActivityConfiguration findPresenceConfigurationByCountryIdAndPhaseId(Long countryId, BigInteger phaseId);

    List<ActivityConfigurationDTO> findPresenceConfigurationByCountryId(Long countryId);

    List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryId(Long unitId);


}
