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
            exceptionService.actionNotPermittedException("error.glide_time.checkin.after.exceeds.limit");
        }
        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckIn().getBefore()) {
            exceptionService.actionNotPermittedException("error.glide_time.checkin.before.exceeds.limit");
        }
        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckOut().getAfter()) {
            exceptionService.actionNotPermittedException("error.glide_time.checkout.after.exceeds.limit");
        }
        if (glideTimeSettingsDTO.getTimeLimit()!=null && glideTimeSettingsDTO.getTimeLimit() < glideTimeSettingsDTO.getGlideTimeForCheckOut().getBefore()) {
            exceptionService.actionNotPermittedException("error.glide_time.checkout.before.exceeds.limit");
        }
    }
}

