package com.kairos.service.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.flexible_time.GlideTimeSettingsDTO;
import com.kairos.persistence.model.flexible_time.GlideTimeSettings;
import com.kairos.persistence.repository.flexible_time.GlideTimeSettingsRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class GlideTimeSettingsService extends MongoBaseService {


    @Inject
    private GlideTimeSettingsRepository glideTimeSettingsRepository;
    @Inject
    private ExceptionService exceptionService;

    public GlideTimeSettingsDTO saveGlideTimeSettings(Long countryId, GlideTimeSettingsDTO glideTimeSettingsDTO){
        GlideTimeSettings glideTimeSettings = glideTimeSettingsRepository.getGlideTimeSettingsByIdAndDeletedFalse(glideTimeSettingsDTO.getId());
        if(glideTimeSettings ==null){
            exceptionService.dataNotFoundException("message.dataNotFound","Flexi Time Settings", glideTimeSettingsDTO.getId());
        }
        glideTimeSettings =new GlideTimeSettings(glideTimeSettingsDTO.getId(), glideTimeSettingsDTO.getGlideTimeForCheckIn(), glideTimeSettingsDTO.getGlideTimeForCheckOut(), glideTimeSettingsDTO.getTimeLimit());
        glideTimeSettings.setCountryId(countryId);
        save(glideTimeSettings);
        glideTimeSettingsDTO.setId(glideTimeSettings.getId());
        return glideTimeSettingsDTO;
    }

    public GlideTimeSettingsDTO getGlideTimeSettings(Long countryId){
        return glideTimeSettingsRepository.getGlideTimeSettingsByCountryIdAndDeletedFalse(countryId);
    }
}
