package com.kairos.service.system_setting;


import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.user.country.Country;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.system_setting.SystemLanguageDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
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

    public SystemLanguageDTO addSystemLanguage(SystemLanguageDTO systemLanguageDTO) {

        logger.info("featureDTO : "+systemLanguageDTO.getName());
        if( systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName()) ){
            exceptionService.duplicateDataException("message.system.language.name.alreadyExist",systemLanguageDTO.getName());
        }
        // if defaultLanguage is false then ask to set default
        if(!systemLanguageDTO.isDefaultLanguage() && !systemLanguageGraphRepository.isDefaultSystemLanguageExists()){
            exceptionService.invalidRequestException("message.system.language.must.default");
        }

        SystemLanguage systemLanguage = new SystemLanguage(systemLanguageDTO.getName(), systemLanguageDTO.getCode(), systemLanguageDTO.isDefaultLanguage());
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

        if( ! ( systemLanguage.getName().equalsIgnoreCase(systemLanguageDTO.getName()) ) && systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName()) ){
            exceptionService.duplicateDataException("message.system.language.name.alreadyExist",systemLanguageDTO.getName() );
        }

        // if defaultLanguage is false then ask to set default
        if( !systemLanguageDTO.isDefaultLanguage() ||  ((systemLanguageDTO.isDefaultLanguage()
                && systemLanguageDTO.isInactive()) && !systemLanguageGraphRepository.isDefaultSystemLanguageExistsExceptId(systemLanguageId)) ){
            exceptionService.invalidRequestException("message.system.language.must.default");
        }

        // To set inactive status, check if System Language is linked with Country
        if(systemLanguageGraphRepository.isSystemLanguageSetInAnyCountry(systemLanguageId)){
            exceptionService.invalidRequestException("message.system.language.cannot.set.inactive", systemLanguageId);
        }

        systemLanguage.setCode(systemLanguageDTO.getCode());
        systemLanguage.setName(systemLanguageDTO.getName());
        save(systemLanguage);
        return systemLanguageDTO;
    }


    public Boolean deleteSystemLanguage(Long systemLanguageId){

        SystemLanguage  systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if(!Optional.ofNullable(systemLanguage).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.system.language.notFound",systemLanguageId);
        }

        systemLanguage.setDeleted(true);
        save(systemLanguage);
        return true;
    }

    public List<SystemLanguage> getListOfSystemLanguage(){
        return systemLanguageGraphRepository.getListOfSystemLanguage();
    }

    public Boolean updateSystemLanguageOfCountry(Long countryId, Long systemLanguageId){
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }

        SystemLanguage  systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if(!Optional.ofNullable(systemLanguage).isPresent() || systemLanguage.isInactive()) {
            exceptionService.dataNotFoundByIdException("message.system.language.notFound",systemLanguageId);
        }

        country.setSystemLanguage(systemLanguage);
        save(country);
        return true;
    }


    // For test cases

    public SystemLanguage getSystemLanguageByName(String name){
        return systemLanguageGraphRepository.findSystemLanguageByName(name);
    }


}
