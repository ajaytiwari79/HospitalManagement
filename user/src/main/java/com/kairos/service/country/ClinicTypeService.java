package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.ClinicType;
import com.kairos.persistence.model.country.default_data.ClinicTypeDTO;
import com.kairos.persistence.model.country.default_data.IndustryType;
import com.kairos.persistence.repository.user.country.ClinicTypeGraphRepository;
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
public class ClinicTypeService {
    @Inject
    private ClinicTypeGraphRepository clinicTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public ClinicTypeDTO createClinicType(long countryId, ClinicTypeDTO clinicTypeDTO){
        ClinicType clinicType = null;
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            Boolean clinicTypeExistInCountryByName = clinicTypeGraphRepository.clinicTypeExistInCountryByName(countryId, "(?i)" + clinicTypeDTO.getName(), -1L);
            if (clinicTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.ClinicType.name.exist");
            }
            clinicType = new ClinicType(clinicTypeDTO.getName(), clinicTypeDTO.getDescription());
            clinicType.setCountry(country);
            clinicTypeGraphRepository.save(clinicType);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        clinicTypeDTO.setId(clinicType.getId());
        return clinicTypeDTO;
    }

    public List<ClinicTypeDTO> getClinicTypeByCountryId(long countryId){
        List<ClinicType> clinicTypes = clinicTypeGraphRepository.findClinicByCountryId(countryId);
        List<ClinicTypeDTO> clinicTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(clinicTypes,ClinicTypeDTO.class);
        for(ClinicTypeDTO clinicTypeDTO :clinicTypeDTOS){
            clinicTypeDTO.setCountryId(countryId);
            clinicTypeDTO.setTranslations(TranslationUtil.getTranslatedData(clinicTypeDTO.getTranslatedNames(),clinicTypeDTO.getTranslatedDescriptions()));
        }
        return clinicTypeDTOS;
    }

    public ClinicTypeDTO updateClinicType(long countryId, ClinicTypeDTO clinicTypeDTO){
        Boolean clinicTypeExistInCountryByName = clinicTypeGraphRepository.clinicTypeExistInCountryByName(countryId, "(?i)" + clinicTypeDTO.getName(), clinicTypeDTO.getId());
        if (clinicTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.ClinicType.name.exist");
        }
        ClinicType currentClinicType = clinicTypeGraphRepository.findOne(clinicTypeDTO.getId());
        if (currentClinicType!=null){
            currentClinicType.setName(clinicTypeDTO.getName());
            currentClinicType.setDescription(clinicTypeDTO.getDescription());
            clinicTypeGraphRepository.save(currentClinicType);
        }
        return clinicTypeDTO;
    }

    public boolean deleteClinicType(long clinicTypeId){
        ClinicType currentClinicType = clinicTypeGraphRepository.findOne(clinicTypeId);
        if (currentClinicType!=null){
            currentClinicType.setEnabled(false);
            clinicTypeGraphRepository.save(currentClinicType);
        } else {
            exceptionService.dataNotFoundByIdException("error.ClinicType.notfound");
        }
        return true;
    }
}
