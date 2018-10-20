package com.kairos.service.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.flexible_time.FlexibleTimeSettingsDTO;
import com.kairos.persistence.model.flexible_time.FlexibleTimeSettings;
import com.kairos.persistence.repository.flexible_time.FlexibleTimeSettingsRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class FlexibleTimeSettingsService extends MongoBaseService {

    @Inject
    private FlexibleTimeSettingsRepository flexibleTimeSettingsRepository;

    public FlexibleTimeSettingsDTO saveFlexibleTimeSettings(Long countryId,FlexibleTimeSettingsDTO flexibleTimeSettingsDTO){
        FlexibleTimeSettings flexibleTimeSettings=ObjectMapperUtils.copyPropertiesByMapper(flexibleTimeSettingsDTO,FlexibleTimeSettings.class);
        flexibleTimeSettings.setCountryId(countryId);
        flexibleTimeSettings.setUnitId(null);
        flexibleTimeSettings.setActivityId(null);
        save(flexibleTimeSettings);
        return flexibleTimeSettingsDTO;
    }

    public FlexibleTimeSettingsDTO getFlexibleTimeSettings(Long countryId){
        return flexibleTimeSettingsRepository.getFlexibleTimeSettingsByCountryIdAndDeletedFalse(countryId);
    }

    public FlexibleTimeSettingsDTO saveFlexibleTimeSettingsForUnit(Long unitId,FlexibleTimeSettingsDTO flexibleTimeSettingsDTO){
        FlexibleTimeSettings flexibleTimeSettings=ObjectMapperUtils.copyPropertiesByMapper(flexibleTimeSettingsDTO,FlexibleTimeSettings.class);
        flexibleTimeSettings.setUnitId(unitId);
        flexibleTimeSettings.setCountryId(null);
        flexibleTimeSettings.setActivityId(null);
        save(flexibleTimeSettings);
        return flexibleTimeSettingsDTO;
    }

    public FlexibleTimeSettingsDTO getFlexibleTimeSettingsForUnit(Long unitId){
        return flexibleTimeSettingsRepository.getFlexibleTimeSettingsByUnitIdAndDeletedFalse(unitId);
    }



}
