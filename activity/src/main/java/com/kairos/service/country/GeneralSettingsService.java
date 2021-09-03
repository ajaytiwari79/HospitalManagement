package com.kairos.service.country;

import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.country.GeneralSettings;
import com.kairos.persistence.repository.country.GeneralSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.ArrayList;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
@Transactional
public class GeneralSettingsService {

    @Inject  private GeneralSettingsRepository generalSettingsRepository;

    public GeneralSettings updateCountryGeneralSettings(Long countryId, GeneralSettings generalSettings){
        GeneralSettings existingGeneralSettings = generalSettingsRepository.findByDeletedFalseAndCountryId(countryId);
        if(isNotNull(existingGeneralSettings)){
            generalSettings.setId(existingGeneralSettings.getId());
        }
        generalSettingsRepository.save(generalSettings);
        return generalSettings;
    }


    public GeneralSettings getCountryGeneralSettings(Long countryId) {
        GeneralSettings existingGeneralSettings = generalSettingsRepository.findByDeletedFalseAndCountryId(countryId);
        if(isNull(existingGeneralSettings)){
            existingGeneralSettings = new GeneralSettings(null,countryId,false,false,false, new ArrayList<>(),0);
        }
        return existingGeneralSettings;
    }

    public GeneralSettings updateUnitGeneralSettingsForUnit(Long unitId, GeneralSettings generalSettings){
        GeneralSettings existingGeneralSettings = generalSettingsRepository.findByDeletedFalseAndUnitId(unitId);
        if(isNotNull(existingGeneralSettings)){
            generalSettings.setId(existingGeneralSettings.getId());
        }
        generalSettingsRepository.save(generalSettings);
        return generalSettings;
    }

    public GeneralSettings getGeneralSettingsByUnit(Long unitId) {
        GeneralSettings existingGeneralSettings = generalSettingsRepository.findByDeletedFalseAndUnitId(unitId);
        if(isNull(existingGeneralSettings)){
            existingGeneralSettings = generalSettingsRepository.findByDeletedFalseAndCountryId(UserContext.getUserDetails().getCountryId());
        }
        if(isNull(existingGeneralSettings)){
            existingGeneralSettings = new GeneralSettings(unitId,null,false,false,false, new ArrayList<>(),0);
        }
        return existingGeneralSettings;
    }
}
