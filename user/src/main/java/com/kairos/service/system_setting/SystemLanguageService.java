package com.kairos.service.system_setting;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.system_setting.SystemLanguageDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.system_setting.CountryLanguageSettingRelationship;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.system_setting.SystemLanguageQueryResult;
import com.kairos.persistence.repository.system_setting.CountryLanguageSettingRelationshipRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.UserMessagesConstants.*;

@Transactional
@Service
public class SystemLanguageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private CountryLanguageSettingRelationshipRepository countryLanguageSettingRelationshipRepository;

    public SystemLanguageDTO addSystemLanguage(SystemLanguageDTO systemLanguageDTO) {

        logger.info("featureDTO : " + systemLanguageDTO.getName());
        if (systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName().trim())) {
            exceptionService.duplicateDataException(MESSAGE_SYSTEM_LANGUAGE_NAME_ALREADYEXIST, systemLanguageDTO.getName());
        }

        if (systemLanguageDTO.isDefaultLanguage() && !systemLanguageDTO.isActive()) {
            exceptionService.invalidRequestException(MESSAGE_SYSTEM_LANGUAGE_DEFAULT_MUST_ACTIVE);
        } else if (systemLanguageDTO.isDefaultLanguage() && systemLanguageDTO.isActive()) {
            // Set default status of other lanuages as false
            systemLanguageGraphRepository.setDefaultStatusForAllLanguage(false);
        } else if (!systemLanguageGraphRepository.isDefaultSystemLanguageExists()) {
            exceptionService.invalidRequestException(MESSAGE_SYSTEM_LANGUAGE_MUST_DEFAULT);
        }

        SystemLanguage systemLanguage = new SystemLanguage(systemLanguageDTO.getName(), systemLanguageDTO.getCode(), systemLanguageDTO.isDefaultLanguage(), systemLanguageDTO.isActive());
        systemLanguageGraphRepository.save(systemLanguage);
        systemLanguageDTO.setId(systemLanguage.getId());
        return systemLanguageDTO;
    }

    public SystemLanguageDTO updateSystemLanguage(Long systemLanguageId, SystemLanguageDTO systemLanguageDTO) {

        logger.info("featureDTO : " + systemLanguageDTO.getName());
        SystemLanguage systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if (!Optional.ofNullable(systemLanguage).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SYSTEM_LANGUAGE_NOTFOUND, systemLanguageId);
        }

        if (systemLanguageDTO.isDefaultLanguage() && !systemLanguageDTO.isActive()) {
            exceptionService.invalidRequestException(MESSAGE_SYSTEM_LANGUAGE_DEFAULT_MUST_ACTIVE);
        } else if (systemLanguageDTO.isDefaultLanguage() && systemLanguageDTO.isActive()) {
            // Set default status of all lanuages as false
            systemLanguageGraphRepository.setDefaultStatusForAllLanguage(false);

        } else if (systemLanguage.isDefaultLanguage() && !systemLanguageGraphRepository.isDefaultSystemLanguageExistsExceptId(systemLanguageId)) {
            // If no language exists as default
            exceptionService.invalidRequestException(MESSAGE_SYSTEM_LANGUAGE_MAKE_OTHER_DEFAULT);
        }

        if (!(systemLanguage.getName().equalsIgnoreCase(systemLanguageDTO.getName())) && systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName())) {
            exceptionService.duplicateDataException(MESSAGE_SYSTEM_LANGUAGE_NAME_ALREADYEXIST, systemLanguageDTO.getName());
        }

        // To set inactive status, check if System Language is linked with Country
        if (!systemLanguageDTO.isActive() && systemLanguageGraphRepository.isSystemLanguageSetInAnyCountry(systemLanguageId)) {
            exceptionService.invalidRequestException(MESSAGE_SYSTEM_LANGUAGE_CANNOT_SET_INACTIVE, systemLanguageId);
        }

        systemLanguage.setCode(systemLanguageDTO.getCode());
        systemLanguage.setName(systemLanguageDTO.getName());
        systemLanguage.setDefaultLanguage(systemLanguageDTO.isDefaultLanguage());
        systemLanguage.setActive(systemLanguageDTO.isActive());
        systemLanguageGraphRepository.save(systemLanguage);
        return systemLanguageDTO;
    }


    public Boolean deleteSystemLanguage(Long systemLanguageId) {

        SystemLanguage systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if (!Optional.ofNullable(systemLanguage).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SYSTEM_LANGUAGE_NOTFOUND, systemLanguageId);
        }
        if (systemLanguage.isDefaultLanguage()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SYSTEM_LANGUAGE_DEFAULT_CANNOT_DELETE);
        }
        if (systemLanguageGraphRepository.isSystemLanguageSetInAnyCountry(systemLanguageId)) {
            exceptionService.invalidRequestException(MESSAGE_SYSTEM_LANGUAGE_CANNOT_SET_INACTIVE, systemLanguageId);
        }
        systemLanguage.setDeleted(true);
        systemLanguageGraphRepository.save(systemLanguage);
        return true;
    }

    public List<SystemLanguageDTO> getListOfSystemLanguage() {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(systemLanguageGraphRepository.getListOfSystemLanguage(), SystemLanguageDTO.class);

    }

    public Boolean updateSystemLanguageOfCountry(Long countryId, Long systemLanguageId, Boolean defaultLanguage, Boolean selected) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }
        SystemLanguage systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId, 0);
        if (!Optional.ofNullable(systemLanguage).isPresent() || !systemLanguage.isActive()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SYSTEM_LANGUAGE_NOTFOUND, systemLanguageId);
        }
        if (isNotNull(selected) && selected || isNotNull(defaultLanguage) && defaultLanguage) {
            createCountryAndSystemLanguageMapping(country, systemLanguage, defaultLanguage, selected);
        } else if (isNotNull(selected) && !selected) {
            deleteCountryAndSystemLanguageMapping(country.getId(), systemLanguage.getId());
        }

        return true;
    }

    private Boolean deleteCountryAndSystemLanguageMapping(Long countryId, Long systemLanguageId) {
        CountryLanguageSettingRelationship countryLanguageSettingRelationship = countryLanguageSettingRelationshipRepository.findByCountryIdAndSystemLanguageId(countryId, systemLanguageId);
        if (isNotNull(countryLanguageSettingRelationship) && countryLanguageSettingRelationship.isDefaultLanguage()) {
            exceptionService.actionNotPermittedException("message.system.language.default.cannot.delete");
        }
        countryLanguageSettingRelationshipRepository.delete(countryLanguageSettingRelationship);
        return true;
    }

    private Boolean createCountryAndSystemLanguageMapping(Country country, SystemLanguage systemLanguage, Boolean defaultSetting, Boolean selected) {
        List<Long> countryLanguageSettingRelationshipIds = countryLanguageSettingRelationshipRepository.findAllByCountryId(country.getId());
        List<CountryLanguageSettingRelationship> countryLanguageSettingRelationships = countryLanguageSettingRelationshipRepository.findAllById(countryLanguageSettingRelationshipIds);
        if (isCollectionNotEmpty(countryLanguageSettingRelationships) && isNotNull(defaultSetting) && defaultSetting) {

            countryLanguageSettingRelationships.forEach(countryLanguageSettingRelationship -> {
                if (countryLanguageSettingRelationship.getSystemLanguage().getId().equals(systemLanguage.getId())) {
                    countryLanguageSettingRelationship.setDefaultLanguage(defaultSetting);
                } else {
                    countryLanguageSettingRelationship.setDefaultLanguage(false);
                }
            });
        } else if (isNotNull(selected) && selected) {
            countryLanguageSettingRelationships.add(new CountryLanguageSettingRelationship(country, systemLanguage, false));
        }
        countryLanguageSettingRelationshipRepository.saveAll(countryLanguageSettingRelationships);
        return true;
    }

    public List<SystemLanguageDTO> getSystemLanguageOfCountry(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }
        List<SystemLanguageDTO> systemLanguageDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(systemLanguageGraphRepository.findSystemLanguagesByCountryId(countryId), SystemLanguageDTO.class);
        return systemLanguageDTOS;
    }

    public List<SystemLanguageDTO> getSystemLanguageAndCountryMapping(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }
        List<SystemLanguageDTO> systemLanguageDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(systemLanguageGraphRepository.getActiveSystemLanguages(), SystemLanguageDTO.class);
        List<SystemLanguageQueryResult> selectedLanguageOfCountry = systemLanguageGraphRepository.findSystemLanguagesByCountryId(countryId);
        systemLanguageDTOS.stream().forEach(systemLanguageDTO -> {
            selectedLanguageOfCountry.forEach(systemLanguageQueryResult -> {
                if (systemLanguageDTO.getId().equals(systemLanguageQueryResult.getId())) {
                    systemLanguageDTO.setSelected(true);
                }
            });
            //TODO remove when fixed code reivew point
            systemLanguageDTO.setDefaultLanguage(false);
        });
        return systemLanguageDTOS;
    }

    public SystemLanguage getDefaultSystemLanguageForUnit(Long unitId) {
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        SystemLanguage systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(countryId);
        if (!Optional.ofNullable(systemLanguage).isPresent()) {
            systemLanguage = systemLanguageGraphRepository.getDefaultSystemLangugae();
        }
        return systemLanguage;
    }


    // For test cases

    public SystemLanguage getSystemLanguageByName(String name) {
        return systemLanguageGraphRepository.findSystemLanguageByName(name);
    }


}
