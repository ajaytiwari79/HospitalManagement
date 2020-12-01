package com.kairos.service.country;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.BusinessTypeDTO;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
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
public class BusinessTypeService {

    @Inject
    private BusinessTypeGraphRepository businessTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public BusinessTypeDTO createBusinessType(long countryId, BusinessTypeDTO businessTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        BusinessType businessType = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        } else {
            Boolean businessTypeExistInCountryByName = businessTypeGraphRepository.businessTypeExistInCountryByName(countryId, "(?i)" + businessTypeDTO.getName(), -1L);
            if (businessTypeExistInCountryByName) {
                throw new InvalidRequestException("Min should be less than max");
                //exceptionService.duplicateDataException("error.BusinessType.name.exist");
            }
            businessType = new BusinessType(businessTypeDTO.getName(), businessTypeDTO.getDescription());
            businessType.setCountry(country);
            businessTypeGraphRepository.save(businessType);
        }
        businessTypeDTO.setId(businessType.getId());
        return businessTypeDTO;
    }

    public List<BusinessTypeDTO> getBusinessTypeByCountryId(long countryId) {
        List<BusinessType> businessTypes = businessTypeGraphRepository.findBusinessTypeByCountry(countryId);
        List<BusinessTypeDTO> businessTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(businessTypes,BusinessTypeDTO.class);
        for(BusinessTypeDTO businessTypeDTO :businessTypeDTOS){
            businessTypeDTO.setCountryId(countryId);
            businessTypeDTO.setTranslations(TranslationUtil.getTranslatedData(businessTypeDTO.getTranslatedNames(),businessTypeDTO.getTranslatedDescriptions()));
        }
        return businessTypeDTOS;
    }

    public BusinessTypeDTO updateBusinessType(long countryId, BusinessTypeDTO businessTypeDTO) {
        Boolean businessTypeExistInCountryByName = businessTypeGraphRepository.businessTypeExistInCountryByName(countryId, "(?i)" + businessTypeDTO.getName(), businessTypeDTO.getId());
        if (businessTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.BusinessType.name.exist");
        }
        BusinessType currentBusinessType = businessTypeGraphRepository.findOne(businessTypeDTO.getId());
        if (currentBusinessType != null) {
            currentBusinessType.setName(businessTypeDTO.getName());
            currentBusinessType.setDescription(businessTypeDTO.getDescription());
            businessTypeGraphRepository.save(currentBusinessType);
        }
        return businessTypeDTO;
    }

    public boolean deleteBusinessType(long businessTypeId) {
        BusinessType businessType = businessTypeGraphRepository.findOne(businessTypeId);
        if (businessType != null) {
            businessType.setEnabled(false);
            businessTypeGraphRepository.save(businessType);
        } else {
            exceptionService.dataNotFoundByIdException("error.BusinessType.notfound");
        }
        return true;
    }

    public Map<String, TranslationInfo> updateTranslation(Long businessTypeId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptions = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptions);
        BusinessType buisnessType =businessTypeGraphRepository.findOne(businessTypeId);
        buisnessType.setTranslatedNames(translatedNames);
        buisnessType.setTranslatedDescriptions(translatedDescriptions);
        businessTypeGraphRepository.save(buisnessType);
        return buisnessType.getTranslatedData();
    }

}
