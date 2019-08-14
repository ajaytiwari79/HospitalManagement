package com.kairos.persistence.repository.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ActivityConfigurationRepository extends MongoBaseRepository<ActivityConfiguration, BigInteger> ,CustomActivityConfigurationRepository{

   boolean existsByUnitIdAndDeletedFalse(Long unitId);

   List<ActivityConfiguration> findAllByUnitIdAndDeletedFalse(Long unitId);

  /* @Query("{unitId:?0,presencePlannedTime:{$exists:false}}")
   List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId);

   @Query("{unitId:?0,absencePlannedTime:{$exists:false}}")
   List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId);

   @Query("{'unitId':?0,'absencePlannedTime.phaseId':?1}")
   List<ActivityConfiguration> findAllAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

   @Query("{'unitId':?0,'presencePlannedTime.phaseId':?1}")
   ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

   List<ActivityConfigurationDTO> findPresenceConfigurationByCountryId(Long countryId);

   List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryId(Long unitId);*/


}
