package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.EmployeeLimit;
import com.kairos.persistence.model.country.default_data.EmployeeLimitDTO;
import com.kairos.persistence.model.country.default_data.KairosStatus;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmployeeLimitGraphRepository;
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
public class EmployeeLimitService {

    @Inject
    private EmployeeLimitGraphRepository employeeLimitGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public EmployeeLimitDTO createEmployeeLimit(long countryId, EmployeeLimitDTO employeeLimitDTO){
        Country country = countryGraphRepository.findOne(countryId);
        EmployeeLimit employeeLimit = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        } else {
            Boolean employeeLimitExistInCountryByName = employeeLimitGraphRepository.employeeLimitExistInCountryByName(countryId, "(?i)" + employeeLimitDTO.getName(), -1L);
            if (employeeLimitExistInCountryByName) {
                exceptionService.duplicateDataException("error.EmployeeLimit.name.exist");
            }
            employeeLimit = new EmployeeLimit(employeeLimitDTO.getName(), employeeLimitDTO.getDescription(), employeeLimitDTO.getMinimum(), employeeLimitDTO.getMaximum());
            employeeLimit.setCountry(country);
            employeeLimitGraphRepository.save(employeeLimit);
        }
        employeeLimitDTO.setId(employeeLimit.getId());
        return employeeLimitDTO;
    }

    public List<EmployeeLimitDTO> getEmployeeLimitByCountryId(long countryId){
        List<EmployeeLimit> employeeLimits =employeeLimitGraphRepository.findEmployeeLimitByCountry(countryId);
        List<EmployeeLimitDTO> employeeLimitDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(employeeLimits,EmployeeLimitDTO.class);
        for(EmployeeLimitDTO employeeLimitDTO :employeeLimitDTOS){
            employeeLimitDTO.setCountryId(countryId);
            employeeLimitDTO.setTranslations(TranslationUtil.getTranslatedData(employeeLimitDTO.getTranslatedNames(),employeeLimitDTO.getTranslatedDescriptions()));
        }
        return employeeLimitDTOS;
    }

    public EmployeeLimitDTO updateEmployeeLimit(long countryId, EmployeeLimitDTO employeeLimitDTO){
        Boolean employeeLimitExistInCountryByName = employeeLimitGraphRepository.employeeLimitExistInCountryByName(countryId, "(?i)" + employeeLimitDTO.getName(), employeeLimitDTO.getId());
        if (employeeLimitExistInCountryByName) {
            exceptionService.duplicateDataException("error.EmployeeLimit.name.exist");
        }
        EmployeeLimit currentEmployeeLimit = employeeLimitGraphRepository.findOne(employeeLimitDTO.getId());
        if (currentEmployeeLimit != null) {
            currentEmployeeLimit.setName(employeeLimitDTO.getName());
            currentEmployeeLimit.setDescription(employeeLimitDTO.getDescription());
            currentEmployeeLimit.setMinimum(employeeLimitDTO.getMinimum());
            currentEmployeeLimit.setMaximum(employeeLimitDTO.getMaximum());
            employeeLimitGraphRepository.save(currentEmployeeLimit);
        }
        return employeeLimitDTO;
    }

    public boolean deleteEmployeeLimit(long contractTypeId){
        EmployeeLimit employeeLimit = employeeLimitGraphRepository.findOne(contractTypeId);
        if (employeeLimit !=null){
            employeeLimit.setEnabled(false);
            employeeLimitGraphRepository.save(employeeLimit);
        } else {
            exceptionService.dataNotFoundByIdException("error.EmployeeLimit.notfound");
        }
        return true;
    }

    public Map<String, TranslationInfo> updateTranslation(Long employeeLimitId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptions = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptions);
        EmployeeLimit employeeLimit =employeeLimitGraphRepository.findOne(employeeLimitId);
        employeeLimit.setTranslatedNames(translatedNames);
        employeeLimit.setTranslatedDescriptions(translatedDescriptions);
        employeeLimitGraphRepository.save(employeeLimit);
        return employeeLimit.getTranslatedData();
    }
}
