package com.kairos.persistence.repository.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.FlexibleTimeSettingsDTO;
import com.kairos.persistence.model.flexible_time.FlexibleTimeSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

public interface FlexibleTimeSettingsRepository extends MongoBaseRepository<FlexibleTimeSettings,BigInteger> {

    FlexibleTimeSettingsDTO getFlexibleTimeSettingsByCountryIdAndDeletedFalse(Long countryId);

    FlexibleTimeSettings getFlexibleTimeSettingsByIdAndDeletedFalse(BigInteger id);
}
