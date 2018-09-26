package com.kairos.persistence.repository.unit_settings;

import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ActivityConfigurationRepository extends MongoBaseRepository<ActivityConfiguration, BigInteger> ,CustomActivityConfigurationRepository{

   boolean existsByUnitIdAndDeletedFalse(Long unitId);


}
