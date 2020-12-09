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
        gapSettingsMongoRepository.save(gapSettings);
        gapSettingsDTO.setId(gapSettings.getId());
        return gapSettingsDTO;
    }

    private void validateGapSetting(GapSettingsDTO gapSettingsDTO, boolean forCountry) {

    }

    public List<GapSettingsDTO> getGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<GapSettings> gapSettingsList = forCountry ? gapSettingsMongoRepository.getAllByCountryId(countryOrUnitId) : gapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(gapSettingsList, GapSettingsDTO.class);
    }
}
