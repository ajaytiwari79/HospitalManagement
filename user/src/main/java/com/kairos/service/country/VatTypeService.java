package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.VatType;
import com.kairos.persistence.model.country.default_data.VatTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.VatTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_COUNTRY_ID_NOTFOUND;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class VatTypeService {

    @Inject
    private VatTypeGraphRepository vatTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public VatTypeDTO createVatType(long countryId, VatTypeDTO vatTypeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        VatType vatType;
        if ( country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        } else {
            Boolean vatTypeExistInCountryByNameOrCode = vatTypeGraphRepository.vatTypeExistInCountryByNameOrCode(countryId, "(?i)" + vatTypeDTO.getName(), vatTypeDTO.getCode(), -1L);
            if (vatTypeExistInCountryByNameOrCode) {
                exceptionService.duplicateDataException("error.VatType.name.code.exist");
            }
            vatType = new VatType(vatTypeDTO.getName(), vatTypeDTO.getCode(), vatTypeDTO.getDescription(), vatTypeDTO.getPercentage());
            vatType.setCountry(country);
            vatTypeGraphRepository.save(vatType);
        }
        vatTypeDTO.setId(vatTypeDTO.getId());
        return vatTypeDTO;
   }

    public List<VatTypeDTO> getVatTypeByCountryId(long countryId){
        return vatTypeGraphRepository.findVatTypesByCountry(countryId);
    }

    public VatTypeDTO updateVatType(long countryId, VatTypeDTO vatTypeDTO){
        Boolean vatTypeExistInCountryByNameOrCode = vatTypeGraphRepository.vatTypeExistInCountryByNameOrCode(countryId, "(?i)" + vatTypeDTO.getName(), vatTypeDTO.getCode(), vatTypeDTO.getId());
        if (vatTypeExistInCountryByNameOrCode) {
            exceptionService.duplicateDataException("error.VatType.name.code.exist");
        }
        VatType currentVatType = vatTypeGraphRepository.findOne(vatTypeDTO.getId());
        if (currentVatType != null){
            currentVatType.setName(vatTypeDTO.getName());
            currentVatType.setDescription(vatTypeDTO.getDescription());
            currentVatType.setPercentage(vatTypeDTO.getPercentage());
            currentVatType.setCode(vatTypeDTO.getCode());
            vatTypeGraphRepository.save(currentVatType);
        }
        return vatTypeDTO;
    }

    public boolean deleteVatType(long contractTypeId){
        VatType vatType = vatTypeGraphRepository.findOne(contractTypeId);
        if (vatType != null){
            vatType.setEnabled(false);
            vatTypeGraphRepository.save(vatType);
        } else {
            exceptionService.duplicateDataException("error.VatType.notfound");
        }
        return true;
    }
}
