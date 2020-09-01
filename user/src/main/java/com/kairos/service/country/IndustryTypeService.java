package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.IndustryType;
import com.kairos.persistence.model.country.default_data.IndustryTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.IndustryTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class IndustryTypeService {
    @Inject
    private IndustryTypeGraphRepository industryTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public IndustryTypeDTO createIndustryType(long countryId, IndustryTypeDTO industryTypeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        IndustryType industryType = null;
        if ( country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        } else {
            Boolean industryTypeExistInCountryByName = industryTypeGraphRepository.industryTypeExistInCountryByName(countryId, "(?i)" + industryTypeDTO.getName(), -1L);
            if (industryTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.IndustryType.name.exist");
            }
            industryType = new IndustryType(industryTypeDTO.getName(),industryTypeDTO.getDescription());
            industryType.setCountry(country);
            industryTypeGraphRepository.save(industryType);
        }
        industryTypeDTO.setId(industryType.getId());
        return industryTypeDTO;
    }

    public List<IndustryTypeDTO> getIndustryTypeByCountryId(long countryId){
        List<IndustryType> industryTypes = industryTypeGraphRepository.findIndustryTypeByCountry(countryId);
        List<IndustryTypeDTO> industryTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(industryTypes,IndustryTypeDTO.class);
        for(IndustryTypeDTO industryTypeDTO :industryTypeDTOS){
            industryTypeDTO.setCountryId(countryId);
            industryTypeDTO.setTranslations(TranslationUtil.getTranslatedData(industryTypeDTO.getTranslatedNames(),industryTypeDTO.getTranslatedDescriptions()));
        }
        return industryTypeDTOS;
    }

    public IndustryTypeDTO updateIndustryType(long countryId, IndustryTypeDTO industryTypeDTO){
        Boolean industryTypeExistInCountryByName = industryTypeGraphRepository.industryTypeExistInCountryByName(countryId, "(?i)" + industryTypeDTO.getName(), industryTypeDTO.getId());
        if (industryTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.IndustryType.name.exist");
        }
        IndustryType currentIndustryType = industryTypeGraphRepository.findOne(industryTypeDTO.getId());
        if (currentIndustryType != null){
            currentIndustryType.setName(industryTypeDTO.getName());
            currentIndustryType.setDescription(industryTypeDTO.getDescription());
            industryTypeGraphRepository.save(currentIndustryType);
        }
        return industryTypeDTO;
    }

    public boolean deleteIndustryType(long industryTypeId){
        IndustryType currentIndustryType = industryTypeGraphRepository.findOne(industryTypeId);
        if (currentIndustryType!=null){
            currentIndustryType.setEnabled(false);
            industryTypeGraphRepository.save(currentIndustryType);
        } else {
            exceptionService.dataNotFoundByIdException("error.IndustryType.notfound");
        }
        return true;
    }

    public Map<String, TranslationInfo> updateTranslation(Long industryTypeId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptios);
        IndustryType industryType =industryTypeGraphRepository.findOne(industryTypeId);
        industryType.setTranslatedNames(translatedNames);
        industryType.setTranslatedDescriptions(translatedDescriptios);
        industryTypeGraphRepository.save(industryType);
        return industryType.getTranslatedData();
    }
}
