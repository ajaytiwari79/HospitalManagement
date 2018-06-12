package com.kairos.service.system_setting;


import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
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
    ExceptionService exceptionService;
    @Inject
    SystemLanguageGraphRepository systemLanguageGraphRepository;

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
            exceptionService.dataNotFoundByIdException("message.system.language.notFoundd",systemLanguageId);
        }

        if( ! ( systemLanguage.getName().equalsIgnoreCase(systemLanguageDTO.getName()) ) && systemLanguageGraphRepository.isSystemLanguageExistsWithSameName(systemLanguageDTO.getName()) ){
            exceptionService.duplicateDataException("message.system.language.name.alreadyExist",systemLanguageDTO.getName() );
        }

        // if defaultLanguage is false then ask to set default
        if(!systemLanguageDTO.isDefaultLanguage() && !systemLanguageGraphRepository.isDefaultSystemLanguageExistsExceptId(systemLanguageId)){
            exceptionService.invalidRequestException("message.system.language.must.default");
        }

        // TODO to set inactive status, check if System Language is linked with Country
        systemLanguage.setCode(systemLanguageDTO.getCode());
        systemLanguage.setName(systemLanguageDTO.getName());
        save(systemLanguage);
        return systemLanguageDTO;
    }


    public Boolean deleteSystemLanguage(Long systemLanguageId){

        SystemLanguage  systemLanguage = systemLanguageGraphRepository.findOne(systemLanguageId);
        if(!Optional.ofNullable(systemLanguage).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.system.language.notFoundd",systemLanguageId);
        }

        systemLanguage.setDeleted(true);
        save(systemLanguage);
        return true;
    }

    public List<SystemLanguage> getListOfSystemLanguage(){
        return systemLanguageGraphRepository.getListOfSystemLanguage();
    }

}
