package com.kairos.service.cta_compensation_settings;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta_compensation_setting.CTACompensationConfiguration;
import com.kairos.dto.activity.cta_compensation_setting.CTACompensationSettingDTO;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.cta.CompensationType;
import com.kairos.persistence.cta_compensation_setting.CTACompensationSetting;
import com.kairos.persistence.repository.cta_compensation_settings.CTACompensationSettingMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class CTACompensationSettingService {

    @Inject
    private CTACompensationSettingMongoRepository ctaCompensationSettingMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;

    public CTACompensationSettingDTO updateCTACompensationSetting(Long countryId, Long expertiseId, CTACompensationSettingDTO ctaCompensationSettingDTO) {
        CTACompensationSetting ctaCompensationSetting = ctaCompensationSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(countryId, expertiseId);
        if(isNotNull(ctaCompensationSetting)){
            ctaCompensationSettingDTO.setId(ctaCompensationSetting.getId());
        }
        ctaCompensationSetting = ObjectMapperUtils.copyPropertiesByMapper(ctaCompensationSettingDTO,CTACompensationSetting.class);
        ctaCompensationSetting.setCountryId(countryId);
        ctaCompensationSetting.setExpertiseId(expertiseId);
        ctaCompensationSettingMongoRepository.save(ctaCompensationSetting);
        return ctaCompensationSettingDTO;
    }

    public CTACompensationSettingDTO updateCTACompensationSettingByUnit(Long unitId, Long expertiseId, CTACompensationSettingDTO ctaCompensationSettingDTO) {
        CTACompensationSetting ctaCompensationSetting = ctaCompensationSettingMongoRepository.findByDeletedFalseAndUnitIdAndExpertiseIdAndPrimaryTrue(unitId, expertiseId);
        if(isNotNull(ctaCompensationSetting)){
            ctaCompensationSettingDTO.setId(ctaCompensationSetting.getId());
        }
        ctaCompensationSetting = ObjectMapperUtils.copyPropertiesByMapper(ctaCompensationSettingDTO,CTACompensationSetting.class);
        ctaCompensationSetting.setUnitId(unitId);
        ctaCompensationSetting.setExpertiseId(expertiseId);
        ctaCompensationSettingMongoRepository.save(ctaCompensationSetting);
        return ctaCompensationSettingDTO;
    }

    public CTACompensationSettingDTO getCTACompensationSetting(Long countryId, Long expertiseId) {
        Expertise expertise = userIntegrationService.getExpertise(countryId, expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            exceptionService.duplicateDataException(ERROR_EXPERTISE_NOTFOUND);
        }
        CTACompensationSetting ctaCompensationSetting = ctaCompensationSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(countryId, expertiseId);
        if(isNull(ctaCompensationSetting)){
            ctaCompensationSetting = getDefaultCTACompensationSetting(countryId,expertiseId, null);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(ctaCompensationSetting,CTACompensationSettingDTO.class);
    }

    public CTACompensationSettingDTO getCTACompensationSettingByUnit(Long unitId, Long expertiseId) {
        Expertise expertise = userIntegrationService.getExpertise(unitId, expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            exceptionService.duplicateDataException(ERROR_EXPERTISE_NOTFOUND);
        }
        CTACompensationSetting ctaCompensationSetting = ctaCompensationSettingMongoRepository.findByDeletedFalseAndUnitIdAndExpertiseIdAndPrimaryTrue(unitId, expertiseId);
        if(isNull(ctaCompensationSetting)){
            Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
            ctaCompensationSetting = ctaCompensationSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(countryId, expertiseId);
            if(isNull(ctaCompensationSetting)){
                ctaCompensationSetting = getDefaultCTACompensationSetting(null,expertiseId,unitId);
            }
        }
        return ObjectMapperUtils.copyPropertiesByMapper(ctaCompensationSetting,CTACompensationSettingDTO.class);
    }

    private CTACompensationSetting getDefaultCTACompensationSetting(Long countryId, Long expertiseId, Long unitId){
        List<CTACompensationConfiguration> configurations = new ArrayList<>();
        configurations.add(new CTACompensationConfiguration(0,0, DurationType.HOURS, CompensationType.HOURS,0));
        configurations.add(new CTACompensationConfiguration(0,0, DurationType.HOURS, CompensationType.HOURS,0));
        configurations.add(new CTACompensationConfiguration(0,0, DurationType.HOURS, CompensationType.HOURS,0));
        return new CTACompensationSetting(configurations,countryId,expertiseId,unitId);
    }

}