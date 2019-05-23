package com.kairos.service.glide_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import com.kairos.dto.activity.glide_time.GlideTimeSettingsDTO;
import com.kairos.persistence.model.flexible_time.GlideTimeSettings;
import com.kairos.persistence.repository.glide_time.GlideTimeSettingsRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class GlideTimeSettingsService extends MongoBaseService {


    @Inject
    private GlideTimeSettingsRepository glideTimeSettingsRepository;
    @Inject
    private ExceptionService exceptionService;

    public GlideTimeSettingsDTO saveGlideTimeSettings(Long countryId, GlideTimeSettingsDTO glideTimeSettingsDTO) {
        validateGlideTime(glideTimeSettingsDTO);
        GlideTimeSettings glideTimeSettings = new GlideTimeSettings(glideTimeSettingsDTO.getId(), glideTimeSettingsDTO.getGlideTimeForCheckIn(), glideTimeSettingsDTO.getGlideTimeForCheckOut(), glideTimeSettingsDTO.getTimeLimit());
        glideTimeSettings.setCountryId(countryId);
        save(glideTimeSettings);
        glideTimeSettingsDTO.setId(glideTimeSettings.getId());
        return glideTimeSettingsDTO;
    }

    public GlideTimeSettingsDTO getGlideTimeSettings(Long countryId) {
        return glideTimeSettingsRepository.getGlideTimeSettingsByCountryIdAndDeletedFalse(countryId);
    }

    private void validateGlideTime(GlideTimeSettingsDTO glideTimeSettingsDTO) {

        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckIn().getAfter()) {
            exceptionService.actionNotPermittedException(ERROR_GLIDE_TIME_CHECKIN_AFTER_EXCEEDS_LIMIT);
        }
        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckIn().getBefore()) {
            exceptionService.actionNotPermittedException(ERROR_GLIDE_TIME_CHECKIN_BEFORE_EXCEEDS_LIMIT);
        }
        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckOut().getAfter()) {
            exceptionService.actionNotPermittedException(ERROR_GLIDE_TIME_CHECKOUT_AFTER_EXCEEDS_LIMIT);
        }
        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckOut().getBefore()) {
            exceptionService.actionNotPermittedException(ERROR_GLIDE_TIME_CHECKOUT_BEFORE_EXCEEDS_LIMIT);
        }
    }
}

