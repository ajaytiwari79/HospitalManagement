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

   @Query("{unitId:?0,presencePlannedTime:{$exists:true}}")
   List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId);

   @Query("{unitId:?0,absencePlannedTime:{$exists:true}}")
   List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId);

   @Query("{'absencePlannedTime.phaseId':?1,unitId:?0}")
   List<ActivityConfiguration> findAllAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

   @Query("{'presencePlannedTime.phaseId':?1,unitId:?0}")
   ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId);

   @Query("{'presencePlannedTime.phaseId':?1,countryId:?0}")
   ActivityConfiguration findPresenceConfigurationByCountryIdAndPhaseId(Long countryId, BigInteger phaseId);

   @Query("{'absencePlannedTime.phaseId':?1,countryId:?0}")
   List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryIdAndPhaseId(Long countryId, BigInteger phaseId);

   @Query("{presencePlannedTime:{$exists:true},'countryId':?0}")
   List<ActivityConfigurationDTO> findPresenceConfigurationByCountryId(Long countryId);

   @Query("{absencePlannedTime:{$exists:true},'countryId':?0}")
   List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryId(Long countryId);

   @Query("{unitId:?0,nonWorkingPlannedTime:{$exists:true}}")
   List<ActivityConfigurationDTO> findNonWorkingConfigurationByUnitId(Long unitId);

   @Query("{nonWorkingPlannedTime:{$exists:true},'countryId':?0}")
   List<ActivityConfigurationDTO> findNonWorkingConfigurationByCountryId(Long countryId);
}
