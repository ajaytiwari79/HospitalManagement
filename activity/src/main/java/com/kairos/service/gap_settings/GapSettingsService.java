package com.kairos.service.gap_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.gap_settings.GapSettingsDTO;
import com.kairos.persistence.model.gap_settings.GapSettings;
import com.kairos.persistence.repository.gap_settings.GapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class GapSettingsService {
    @Inject
    private GapSettingsMongoRepository gapSettingsMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public GapSettingsDTO createGapSettings(Long countryOrUnitId, GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        gapSettingsDTO.setCountryId(forCountry ? countryOrUnitId : null);
        gapSettingsDTO.setUnitId(forCountry ? null : countryOrUnitId);
        validateGapSetting(gapSettingsDTO, forCountry);
        GapSettings gapSettings = ObjectMapperUtils.copyPropertiesByMapper(gapSettingsDTO, GapSettings.class);
        gapSettingsMongoRepository.save(gapSettings);
        gapSettingsDTO.setId(gapSettings.getId());
        return gapSettingsDTO;
    }

    public GapSettingsDTO updateGapSettings(BigInteger gapSettingsId, GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        GapSettings gapSettings = gapSettingsMongoRepository.findOne(gapSettingsId);
        if(isNull(gapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        validateGapSetting(gapSettingsDTO, forCountry);
        setDataForUpdateGapSettings(gapSettings, gapSettingsDTO);
        gapSettingsMongoRepository.save(gapSettings);
        gapSettingsDTO.setId(gapSettings.getId());
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
            exceptionService.duplicateDataException("Duplicate");
        }
    }

    private void setDataForUpdateGapSettings(GapSettings gapSettings, GapSettingsDTO gapSettingsDTO) {
        gapSettings.setOrganizationTypeId(gapSettingsDTO.getOrganizationTypeId());
        gapSettings.setOrganizationSubTypeId(gapSettingsDTO.getOrganizationSubTypeId());
        gapSettings.setActionMadeBy(gapSettingsDTO.getActionMadeBy());
        gapSettings.setEndDate(gapSettingsDTO.getEndDate());
        gapSettings.setStartDate(gapSettingsDTO.getStartDate());
        gapSettings.setPhaseId(gapSettingsDTO.getPhaseId());
        gapSettings.setGapFillingScenario(gapSettingsDTO.getGapFillingScenario());
        gapSettings.setSelectedGapSettingsRules(gapSettingsDTO.getSelectedGapSettingsRules());
    }

    public List<GapSettingsDTO> getAllGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<GapSettings> gapSettingsList = forCountry ? gapSettingsMongoRepository.getAllByCountryId(countryOrUnitId) : gapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(gapSettingsList, GapSettingsDTO.class);
    }
}
