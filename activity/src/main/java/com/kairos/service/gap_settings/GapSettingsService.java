package com.kairos.service.gap_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.gap_settings.GapSettingsDTO;
import com.kairos.persistence.model.gap_settings.GapSettings;
import com.kairos.persistence.repository.gap_settings.GapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class GapSettingsService {
    @Inject
    private GapSettingsMongoRepository gapSettingsMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public GapSettingsDTO createGapSettings(GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        validateGapSetting(gapSettingsDTO, forCountry);
        GapSettings gapSettings = ObjectMapperUtils.copyPropertiesByMapper(gapSettingsDTO, GapSettings.class);
        gapSettingsMongoRepository.save(gapSettings);
        gapSettingsDTO.setId(gapSettings.getId());
        return gapSettingsDTO;
    }

    public GapSettingsDTO updateGapSettings(GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        GapSettings gapSettings = gapSettingsMongoRepository.findOne(gapSettingsDTO.getId());
        if(isNull(gapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        validateGapSetting(gapSettingsDTO, forCountry);
        gapSettings = ObjectMapperUtils.copyPropertiesByMapper(gapSettingsDTO, GapSettings.class);
        gapSettingsMongoRepository.save(gapSettings);
        return gapSettingsDTO;
    }

    private void validateGapSetting(GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        GapSettings gapSettings;
        if(forCountry) {
            gapSettings = gapSettingsMongoRepository.findByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(gapSettingsDTO.getCountryId(), gapSettingsDTO.getOrganizationTypeId(), gapSettingsDTO.getOrganizationSubTypeId(), gapSettingsDTO.getPhaseId(), gapSettingsDTO.getGapFillingScenario().toString());
        } else {
            gapSettings = gapSettingsMongoRepository.findByUnitIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(gapSettingsDTO.getUnitId(), gapSettingsDTO.getOrganizationTypeId(), gapSettingsDTO.getOrganizationSubTypeId(), gapSettingsDTO.getPhaseId(), gapSettingsDTO.getGapFillingScenario().toString());
        }
        if(isNotNull(gapSettings) && gapSettingsDTO.getId() != gapSettings.getId()){
            exceptionService.duplicateDataException("Duplicate configuration for gap setting");
        }
    }

    public List<GapSettingsDTO> getAllGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<GapSettings> gapSettingsList;
        if(forCountry){
            gapSettingsList = gapSettingsMongoRepository.getAllByCountryId(countryOrUnitId);
        } else {
            gapSettingsList = gapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(gapSettingsList, GapSettingsDTO.class);
    }
}
