package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.default_data.EngineerTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_COUNTRY_ID_NOTFOUND;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class EngineerTypeService{

    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public EngineerTypeDTO createEngineerType(long countryId, EngineerTypeDTO engineerTypeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        EngineerType engineerType = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        } else {
            Boolean engineerTypeExistInCountryByName = engineerTypeGraphRepository.engineerTypeExistInCountryByName(countryId, "(?i)" + engineerTypeDTO.getName(), -1L);
            if (engineerTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.EngineerType.name.exist");
            }
            engineerType = new EngineerType(engineerTypeDTO.getName(), engineerTypeDTO.getDescription());
            engineerType.setCountry(country);
            engineerTypeGraphRepository.save(engineerType);
        }
        engineerTypeDTO.setId(engineerType.getId());
        return engineerTypeDTO;
    }

    public List<EngineerTypeDTO> getEngineerTypeByCountryId(long countryId){
        List<EngineerType> engineerTypes = engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
        List<EngineerTypeDTO> engineerTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(engineerTypes,EngineerTypeDTO.class);
        engineerTypeDTOS.forEach(engineerTypeDTO -> {
            engineerTypeDTO.setCountryId(countryId);
            engineerTypeDTO.setTranslations(TranslationUtil.getTranslatedData(engineerTypeDTO.getTranslatedNames(),engineerTypeDTO.getTranslatedDescriptions()));
        });
        return engineerTypeDTOS;
    }

    public EngineerTypeDTO updateEngineerType(long countryId, EngineerTypeDTO engineerTypeDTO){
        Boolean engineerTypeExistInCountryByName = engineerTypeGraphRepository.engineerTypeExistInCountryByName(countryId, "(?i)" + engineerTypeDTO.getName(), engineerTypeDTO.getId());
        if (engineerTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.EngineerType.name.exist");
        }
        EngineerType currentEngineerType = engineerTypeGraphRepository.findOne(engineerTypeDTO.getId());
        if (currentEngineerType != null) {
            currentEngineerType.setName(engineerTypeDTO.getName());
            currentEngineerType.setDescription(engineerTypeDTO.getDescription());
            engineerTypeGraphRepository.save(currentEngineerType);
        }
        return engineerTypeDTO;
    }

    public boolean deleteEngineerType(long engineerTypeId){
        EngineerType engineerType = engineerTypeGraphRepository.findOne(engineerTypeId);
        if (engineerType!=null){
            engineerType.setEnabled(false);
            engineerTypeGraphRepository.save(engineerType);
            return true;
        } else {
            exceptionService.dataNotFoundByIdException("error.EngineerType.notfound");
        }
        return false;
    }

    public Map<String, TranslationInfo> updateTranslation(Long engineerTypeId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptions = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptions);
        EngineerType engineerType =engineerTypeGraphRepository.findOne(engineerTypeId);
        engineerType.setTranslatedNames(translatedNames);
        engineerType.setTranslatedDescriptions(translatedDescriptions);
        engineerTypeGraphRepository.save(engineerType);
        return engineerType.getTranslatedData();
    }
}
