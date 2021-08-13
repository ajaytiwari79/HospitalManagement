package com.kairos.service.granularity_setting;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.granularity_setting.GranularitySettingDTO;
import com.kairos.persistence.model.granularity_setting.GranularitySetting;
import com.kairos.persistence.repository.granularity_setting.GranularitySettingMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class GranularitySettingService {

    @Inject private GranularitySettingMongoRepository granularitySettingMongoRepository;


    public GranularitySettingDTO createGranularitySettingForCountry(GranularitySettingDTO granularitySettingDTO) {
        if(isNotNull(granularitySettingDTO.getId())){

        }
        GranularitySetting granularitySetting = granularitySettingMongoRepository.findByCountryIdAndOrganisationTypeIdAndDeletedFalse(granularitySettingDTO.getCountryId(),granularitySettingDTO.getOrganisationTypeId());
        if(isNotNull(granularitySetting)){
            granularitySetting.setGranularityInMinute(granularitySettingDTO.getGranularityInMinute());
        } else {
            granularitySetting = ObjectMapperUtils.copyPropertiesByMapper(granularitySettingDTO, GranularitySetting.class);
        }
        granularitySettingMongoRepository.save(granularitySetting);
        granularitySettingDTO.setId(granularitySetting.getId());
        return granularitySettingDTO;
    }

    public List<GranularitySettingDTO> updateGranularitySettingsForCountry(List<GranularitySettingDTO> generalSettingDTOS) {
        List<GranularitySetting> granularitySettings = ObjectMapperUtils.copyCollectionPropertiesByMapper(generalSettingDTOS, GranularitySetting.class);
        granularitySettingMongoRepository.saveEntities(granularitySettings);
        return generalSettingDTOS;
    }

    public List<GranularitySettingDTO> getGranularitySettingsForCountry(Long countryId) {
        return granularitySettingMongoRepository.findAllByCountryIdAndDeletedFalse(countryId);
    }

    public Boolean deleteGranularitySettingsForCountry(Long countryId, Long organisationTypeId) {
        GranularitySetting granularitySetting = granularitySettingMongoRepository.findByCountryIdAndOrganisationTypeIdAndDeletedFalse(countryId, organisationTypeId);
        if(isNull(granularitySetting)){

        }
        granularitySetting.setDeleted(true);
        granularitySettingMongoRepository.save(granularitySetting);
        return true;
    }
}
