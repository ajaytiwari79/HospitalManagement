package com.kairos.service.auto_gap_fill_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.repository.gap_settings.AutoFillGapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class AutoFillGapSettingsService {
    @Inject
    private AutoFillGapSettingsMongoRepository autoFillGapSettingsMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public AutoFillGapSettingsDTO createAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        validateGapSetting(autoFillGapSettingsDTO, forCountry);
        AutoFillGapSettings autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        autoFillGapSettingsDTO.setId(autoFillGapSettings.getId());
        return autoFillGapSettingsDTO;
    }

    public AutoFillGapSettingsDTO updateAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsDTO.getId());
        if(isNull(autoFillGapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        validateGapSetting(autoFillGapSettingsDTO, forCountry);
        autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return autoFillGapSettingsDTO;
    }

    private void validateGapSetting(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings;
        if(forCountry) {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.findByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndAutoGapFillingScenario(autoFillGapSettingsDTO.getCountryId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString());
        } else {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.findByUnitIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndAutoGapFillingScenario(autoFillGapSettingsDTO.getUnitId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString());
        }
        if(isNotNull(autoFillGapSettings) && autoFillGapSettingsDTO.getId() != autoFillGapSettings.getId()){
            exceptionService.duplicateDataException("Duplicate configuration for gap setting");
        }
    }

    public List<AutoFillGapSettingsDTO> getAllAutoFillGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<AutoFillGapSettings> autoFillGapSettingsList;
        if(forCountry){
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByCountryId(countryOrUnitId);
        } else {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(autoFillGapSettingsList, AutoFillGapSettingsDTO.class);
    }
}
