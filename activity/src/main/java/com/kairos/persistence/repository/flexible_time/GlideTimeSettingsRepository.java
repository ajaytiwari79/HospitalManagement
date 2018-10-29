package com.kairos.persistence.repository.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.GlideTimeSettingsDTO;
import com.kairos.persistence.model.flexible_time.GlideTimeSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

public interface GlideTimeSettingsRepository extends MongoBaseRepository<GlideTimeSettings,BigInteger> {

    GlideTimeSettingsDTO getGlideTimeSettingsByCountryIdAndDeletedFalse(Long countryId);
}
