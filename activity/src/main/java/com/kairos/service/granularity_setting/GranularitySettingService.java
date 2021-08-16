package com.kairos.service.granularity_setting;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.granularity_setting.GranularitySettingDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.persistence.model.granularity_setting.GranularitySetting;
import com.kairos.persistence.repository.granularity_setting.GranularitySettingMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class GranularitySettingService {

    @Inject private GranularitySettingMongoRepository granularitySettingMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private UserIntegrationService userIntegrationService;

    public GranularitySettingDTO createGranularitySettingForCountry(GranularitySettingDTO granularitySettingDTO) {
        GranularitySetting granularitySetting = granularitySettingMongoRepository.findByCountryIdAndOrganisationTypeIdAndDeletedFalse(granularitySettingDTO.getCountryId(),granularitySettingDTO.getOrganisationTypeId());
        if(isNotNull(granularitySetting) || isNotNull(granularitySettingDTO.getId())){
            exceptionService.duplicateDataException(ERROR_GRANULARITY_SETTING_DUPLICATE);
        }
        granularitySetting = ObjectMapperUtils.copyPropertiesByMapper(granularitySettingDTO, GranularitySetting.class);
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
            exceptionService.dataNotFoundException(ERROR_GRANULARITY_SETTING_NOT_CONFIGURED, organisationTypeId);
        }
        granularitySetting.setDeleted(true);
        granularitySettingMongoRepository.save(granularitySetting);
        return true;
    }

    public void createDefaultGranularitySettingForUnit(Long unitId, Long countryId, Long organisationTypeId){
        GranularitySetting countryGranularitySetting = granularitySettingMongoRepository.findByCountryIdAndOrganisationTypeIdAndDeletedFalse(countryId, organisationTypeId);
        GranularitySetting granularitySetting = new GranularitySetting(unitId, countryGranularitySetting.getGranularityInMinute(), DateUtils.getCurrentLocalDate(), null);
        granularitySettingMongoRepository.save(granularitySetting);
    }

    public GranularitySettingDTO updateGranularitySettingsForUnit(Long unitId, GranularitySettingDTO generalSettingDTO) {
        GranularitySetting granularitySetting = granularitySettingMongoRepository.findByUnitIdAndStartDate(unitId, generalSettingDTO.getStartDate());
        if(isNull(granularitySetting)){
            granularitySetting = granularitySettingMongoRepository.findByUnitIdDate(unitId, generalSettingDTO.getStartDate());
            GranularitySetting newGranularitySetting = new GranularitySetting(unitId, generalSettingDTO.getGranularityInMinute(), generalSettingDTO.getStartDate(), granularitySetting.getEndDate());
            granularitySetting.setEndDate(generalSettingDTO.getStartDate().minusDays(1));
            granularitySettingMongoRepository.saveEntities(newArrayList(granularitySetting, newGranularitySetting));
        } else {
            granularitySetting.setGranularityInMinute(generalSettingDTO.getGranularityInMinute());
            granularitySettingMongoRepository.save(granularitySetting);
        }
        return generalSettingDTO;
    }

    public GranularitySettingDTO getCurrentGranularitySettingForUnit(Long unitId){
        GranularitySetting granularitySetting = granularitySettingMongoRepository.findByUnitIdDate(unitId, DateUtils.getCurrentLocalDate());
        return ObjectMapperUtils.copyPropertiesByMapper(granularitySetting, GranularitySettingDTO.class);
    }

    public boolean createDefaultDataForCountry(Long countryId){
        List<OrganizationTypeDTO> orgTypeDTOS = userIntegrationService.getAllOrgTypeByCountryId(countryId);
        List<GranularitySetting> granularitySettings = new ArrayList<>();
        for (OrganizationTypeDTO orgTypeDTO : orgTypeDTOS) {
            GranularitySetting granularitySetting = granularitySettingMongoRepository.findByCountryIdAndOrganisationTypeIdAndDeletedFalse(countryId,orgTypeDTO.getId());
            if(isNull(granularitySetting)){
                granularitySettings.add(new GranularitySetting(countryId, 15, orgTypeDTO.getId()));
            }
        }
        List<Long> unitIds = userIntegrationService.getUnitIds(countryId);
        for (Long unitId : unitIds) {
            GranularitySetting granularitySetting = granularitySettingMongoRepository.findByUnitIdAndDeletedFalse(unitId);
            if(isNull(granularitySetting)){
                granularitySettings.add(new GranularitySetting(unitId, 15, DateUtils.getCurrentLocalDate(), null));
            }
        }
        if(isCollectionNotEmpty(granularitySettings)){
            granularitySettingMongoRepository.saveEntities(granularitySettings);
        }
        return true;
    }
}
