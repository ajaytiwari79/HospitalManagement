package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.BusinessTypeDTO;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean businessTypeExistInCountryByName = businessTypeGraphRepository.businessTypeExistInCountryByName(countryId, "(?i)" + businessTypeDTO.getName(), -1L);
            if (businessTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.BusinessType.name.exist");
            }
            businessType = new BusinessType(businessTypeDTO.getName(), businessTypeDTO.getDescription());
            businessType.setCountry(country);
            businessTypeGraphRepository.save(businessType);
        }
        businessTypeDTO.setId(businessType.getId());
        return businessTypeDTO;
    }

    public List<BusinessTypeDTO> getBusinessTypeByCountryId(long countryId) {
        return businessTypeGraphRepository.findBusinessTypeByCountry(countryId);
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
}
