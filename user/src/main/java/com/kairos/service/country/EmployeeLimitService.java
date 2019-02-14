package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.EmployeeLimit;
import com.kairos.persistence.model.country.default_data.EmployeeLimitDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmployeeLimitGraphRepository;
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
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
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
        return employeeLimitGraphRepository.findEmployeeLimitByCountry(countryId);
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
}
