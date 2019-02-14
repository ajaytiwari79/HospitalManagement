package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.default_data.EngineerTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

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
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
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
        return engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
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
        return true;
    }
}
