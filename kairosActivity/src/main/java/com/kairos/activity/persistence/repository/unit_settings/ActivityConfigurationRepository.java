package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.enums.unit_settings.TimeTypeEnum;
import com.kairos.response.dto.web.unit_settings.activity_configuration.ActivityConfigurationDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ActivityConfigurationRepository extends MongoBaseRepository<ActivityConfiguration, BigInteger> {

    List<ActivityConfigurationDTO> findByUnitIdAndDeletedFalseAndTimeTypeEqualsIgnoreCase(Long unitId, TimeTypeEnum timeTypeEnum);

}
