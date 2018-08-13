package com.kairos.service.system_setting;


import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.organization.OrganizationService;
import com.kairos.user.country.system_setting.SystemLanguageDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class SystemLanguageService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationService organizationService;

    public SystemLanguageDTO addSystemLanguage(SystemLanguageDTO systemLanguageDTO) {

        logger.info("featureDTO : "+systemLanguageDTO.getName());
        if( systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName().trim()) ){
            exceptionService.duplicateDataException("message.system.language.name.alreadyExist",systemLanguageDTO.getName());
        }

        if(systemLanguageDTO.isDefaultLanguage() && !systemLanguageDTO.isActive()){
            exceptionService.invalidRequestException("message.system.language.default.must.active");
        } else if(systemLanguageDTO.isDefaultLanguage() && systemLanguageDTO.isActive()){
            // Set default status of other lanuages as false
            systemLanguageGraphRepository.setDefaultStatusForAllLangugae(false);
        } else if( !systemLanguageGraphRepository.isDefaultSystemLanguageExists()){
            exceptionService.invalidRequestException("message.system.language.must.default");
        }

        SystemLanguage systemLanguage = new SystemLanguage(systemLanguageDTO.getName(), systemLanguageDTO.getCode(), systemLanguageDTO.isDefaultLanguage(), systemLanguageDTO.isActive());
        save(systemLanguage);
        systemLanguageDTO.setId(systemLanguage.getId());
        return systemLanguageDTO;
    }

    public SystemLanguageDTO updateSystemLanguage(Long systemLanguageId, SystemLanguageDTO systemLanguageDTO) {

        logger.info("featureDTO : "+systemLanguageDTO.getName());
        SystemLanguage  systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if(!Optional.ofNullable(systemLanguage).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.system.language.notFound",systemLanguageId);
        }

        if(systemLanguageDTO.isDefaultLanguage() && !systemLanguageDTO.isActive()){
            exceptionService.invalidRequestException("message.system.language.default.must.active");
        } else if(systemLanguageDTO.isDefaultLanguage() && systemLanguageDTO.isActive()){
            // Set default status of all lanuages as false
            systemLanguageGraphRepository.setDefaultStatusForAllLangugae(false);

        } else if( systemLanguage.isDefaultLanguage() && !systemLanguageGraphRepository.isDefaultSystemLanguageExistsExceptId(systemLanguageId)){
            // If no language exists as default
            exceptionService.invalidRequestException("message.system.language.make.other.default");
        }

        if( ! ( systemLanguage.getName().equalsIgnoreCase(systemLanguageDTO.getName()) ) && systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName()) ){
            exceptionService.duplicateDataException("message.system.language.name.alreadyExist",systemLanguageDTO.getName() );
        }

        // To set inactive status, check if System Language is linked with Country
        if(!systemLanguageDTO.isActive() && systemLanguageGraphRepository.isSystemLanguageSetInAnyCountry(systemLanguageId)){
            exceptionService.invalidRequestException("message.system.language.cannot.set.inactive", systemLanguageId);
        }

        systemLanguage.setCode(systemLanguageDTO.getCode());
        systemLanguage.setName(systemLanguageDTO.getName());
        systemLanguage.setDefaultLanguage(systemLanguageDTO.isDefaultLanguage());
        systemLanguage.setActive(systemLanguageDTO.isActive());
        save(systemLanguage);
        return systemLanguageDTO;
    }


    public Boolean deleteSystemLanguage(Long systemLanguageId){

        SystemLanguage  systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if(!Optional.ofNullable(systemLanguage).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.system.language.notFound",systemLanguageId);
        }
        if(systemLanguage.isDefaultLanguage()) {
            exceptionService.dataNotFoundByIdException("message.system.language.default.cannot.delete");
        }

        systemLanguage.setDeleted(true);
        save(systemLanguage);
        return true;
    }

    public List<SystemLanguageDTO> getListOfSystemLanguage(Boolean active){
        List<SystemLanguageDTO> systemLanguageDTOS = null;
        if(Optional.ofNullable(active).isPresent() && active){
            systemLanguageDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(systemLanguageGraphRepository.getListOfSystemLanguageByActiveStatus(active), SystemLanguageDTO.class);
        } else {
            systemLanguageDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(systemLanguageGraphRepository.getListOfSystemLanguage(), SystemLanguageDTO.class);
        }
        return systemLanguageDTOS;
    }

    public Boolean updateSystemLanguageOfCountry(Long countryId, Long systemLanguageId){
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }

        SystemLanguage  systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if(!Optional.ofNullable(systemLanguage).isPresent() || !systemLanguage.isActive()) {
            exceptionService.dataNotFoundByIdException("message.system.language.notFound",systemLanguageId);
        }

        country.setSystemLanguage(systemLanguage);
        save(country);
        return true;
    }

    public List<SystemLanguageDTO> getSystemLanguageOfCountry(Long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }

        List<SystemLanguageDTO> systemLanguageDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(systemLanguageGraphRepository.getListOfSystemLanguageByActiveStatus(true), SystemLanguageDTO.class);

        SystemLanguage  systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(countryId);
        systemLanguageDTOS.stream().forEach(systemLanguageDTO -> {
            if(Optional.ofNullable(systemLanguage).isPresent() && systemLanguageDTO.getId().equals(systemLanguage.getId())){
                systemLanguageDTO.setDefaultLanguage(true);
            } else {
                systemLanguageDTO.setDefaultLanguage(false);
            }
        });
        return systemLanguageDTOS;
    }

    public SystemLanguage getDefaultSystemLanguageForUnit(Long unitId){
       Long countryId = organizationService.getCountryIdOfOrganization(unitId);
       SystemLanguage systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(countryId);
       if(!Optional.ofNullable(systemLanguage).isPresent()){
           systemLanguage = systemLanguageGraphRepository.getDefaultSystemLangugae();
       }
       return systemLanguage;
    }


    // For test cases

    public SystemLanguage getSystemLanguageByName(String name){
        return systemLanguageGraphRepository.findSystemLanguageByName(name);
    }


}
