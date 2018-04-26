package com.kairos.service.country;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.EmployeeLimit;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmployeeLimitGraphRepository;
import com.kairos.service.UserBaseService;
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
public class EmployeeLimitService extends UserBaseService {

    @Inject
    private EmployeeLimitGraphRepository employeeLimitGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createEmployeeLimit(long countryId, EmployeeLimit employeeLimit){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            employeeLimit.setCountry(country);
             save(employeeLimit);
            return employeeLimit.retrieveDetails();
        }
        return null;
    }

    public List<Object> getEmployeeLimitByCountryId(long countryId){
        List<Map<String,Object>>  data = employeeLimitGraphRepository.findContractTypeByCountry(countryId);
        List<Object> response = new ArrayList<>();;
        if (data!=null){
            for (Map<String,Object> map: data) {
                Object o =  map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    public Map<String, Object> updateEmployeeLimit(EmployeeLimit employeeLimit){
        EmployeeLimit currentEmployeeLimit = employeeLimitGraphRepository.findOne(employeeLimit.getId());
        if (currentEmployeeLimit!=null){
            currentEmployeeLimit.setName(employeeLimit.getName());
            currentEmployeeLimit.setDescription(employeeLimit.getDescription());
            currentEmployeeLimit.setMinimum(employeeLimit.getMinimum());
            currentEmployeeLimit.setMaximum(employeeLimit.getMaximum());

            employeeLimitGraphRepository.save(currentEmployeeLimit);
            return currentEmployeeLimit.retrieveDetails();
        }
        return null;
    }

    public boolean deleteEmployeeLimit(long contractTypeId){
        EmployeeLimit employeeLimit = employeeLimitGraphRepository.findOne(contractTypeId);
        if (employeeLimit !=null){
            employeeLimit.setEnabled(false);
            save(employeeLimit);
            return true;
        }
        return false;
    }
}
