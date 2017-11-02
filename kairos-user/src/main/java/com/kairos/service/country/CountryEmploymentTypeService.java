package com.kairos.service.country;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.RelationType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.inject.Inject;
import java.util.List;

/**
 * Created by prerna on 2/11/17.
 */
@Service
@Transactional
public class CountryEmploymentTypeService extends UserBaseService {

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public EmploymentType addEmploymentType(Long countryId, EmploymentType employmentType) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
//            logger.debug("Finding country by id::" + countryId);
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        country.addEmploymentType(employmentType);
        countryGraphRepository.save(country);
        return employmentType;
    }

    public EmploymentType updateEmploymentType(long countryId, long employmentTypeId, EmploymentType employmentType) {
        EmploymentType employmentTypeToUpdate = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToUpdate == null) {
//            logger.debug("Finding Employment Type by id::" + levelId);
            throw new DataNotFoundByIdException("Incorrect Employment Type id " + employmentTypeId);
        }
        employmentTypeToUpdate.setName(employmentType.getName());
        employmentTypeToUpdate.setDescription(employmentType.getDescription());
        return save(employmentTypeToUpdate);
    }

    public boolean deleteEmploymentType(long countryId, long employmentTypeId) {
        EmploymentType employmentTypeToDelete = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToDelete == null) {
//            logger.debug("Finding level by id::" + levelId);
            throw new DataNotFoundByIdException("Incorrect Employment Type id " + employmentTypeId);
        }

        employmentTypeToDelete.setDeleted(true);
        save(employmentTypeToDelete);
        return true;
    }

    public List<EmploymentType> getEmploymentTypeList(long countryId) {
        return countryGraphRepository.getEmploymentTypeByCountry(countryId, false);
    }

}
