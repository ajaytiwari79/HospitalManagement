package com.kairos.service.country;

import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.country.CountryGeneralSettings;
import com.kairos.persistence.repository.country.CountryGeneralSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
@Transactional
public class CountryGeneralSettingsService {

    @Inject  private CountryGeneralSettingsRepository countryGeneralSettingsRepository;

    public CountryGeneralSettings updateCountryGeneralSettings(Long countryId,CountryGeneralSettings countryGeneralSettings){
        CountryGeneralSettings existingCountryGeneralSettings = countryGeneralSettingsRepository.findByDeletedFalseAndCountryId(countryId);
        if(isNotNull(existingCountryGeneralSettings)){
            countryGeneralSettings.setId(existingCountryGeneralSettings.getId());
        }
        countryGeneralSettingsRepository.save(countryGeneralSettings);
        return countryGeneralSettings;
    }


    public CountryGeneralSettings getCountryGeneralSettings(Long countryId) {
        CountryGeneralSettings existingCountryGeneralSettings = countryGeneralSettingsRepository.findByDeletedFalseAndCountryId(countryId);
        if(isNull(existingCountryGeneralSettings)){
            existingCountryGeneralSettings = new CountryGeneralSettings(null,countryId,false,false);
        }
        return existingCountryGeneralSettings;
    }

    public CountryGeneralSettings updateCountryGeneralSettingsForUnit(Long unitId,CountryGeneralSettings countryGeneralSettings){
        CountryGeneralSettings existingCountryGeneralSettings = countryGeneralSettingsRepository.findByDeletedFalseAndUnitId(unitId);
        if(isNotNull(existingCountryGeneralSettings)){
            countryGeneralSettings.setId(existingCountryGeneralSettings.getId());
        }
        countryGeneralSettingsRepository.save(countryGeneralSettings);
        return countryGeneralSettings;
    }

    public CountryGeneralSettings getCountryGeneralSettingsForUnit(Long unitId) {
        CountryGeneralSettings existingCountryGeneralSettings = countryGeneralSettingsRepository.findByDeletedFalseAndUnitId(unitId);
        if(isNull(existingCountryGeneralSettings)){
            existingCountryGeneralSettings = countryGeneralSettingsRepository.findByDeletedFalseAndCountryId(UserContext.getUserDetails().getCountryId());
        }
        return existingCountryGeneralSettings;
    }
}
