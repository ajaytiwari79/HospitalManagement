package com.kairos.service.country;

import com.kairos.client.dto.organization.OrganizationEmploymentTypeDTO;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationEmploymentTypeRelationship;
import com.kairos.persistence.model.organization.OrganizationTimeSlotRelationship;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.RelationType;
import com.kairos.persistence.model.user.country.TimeType;
import com.kairos.persistence.model.user.country.dto.EmploymentTypeDTO;
import com.kairos.persistence.model.user.position.Position;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.position.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.inject.Inject;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by prerna on 2/11/17.
 */
@Service
@Transactional
public class EmploymentTypeService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public EmploymentType addEmploymentType(Long countryId, EmploymentTypeDTO employmentTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        EmploymentType employmentTypeToCreate = employmentTypeDTO.generateEmploymentTypeFromEmploymentTypeDTO();
        country.addEmploymentType(employmentTypeToCreate);
        countryGraphRepository.save(country);

        return employmentTypeToCreate;
    }

    public EmploymentType updateEmploymentType(long countryId, long employmentTypeId, EmploymentTypeDTO employmentTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        EmploymentType employmentTypeToUpdate = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToUpdate == null) {
//            logger.debug("Finding Employment Type by id::" + levelId);
            throw new DataNotFoundByIdException("Incorrect Employment Type id " + employmentTypeId);
        }
        employmentTypeToUpdate.setName(employmentTypeDTO.getName());
        employmentTypeToUpdate.setDescription(employmentTypeDTO.getDescription());
        employmentTypeToUpdate.setAllowedForContactPerson(employmentTypeDTO.isAllowedForContactPerson());
        employmentTypeToUpdate.setAllowedForShiftPlan(employmentTypeDTO.isAllowedForShiftPlan());
        employmentTypeToUpdate.setAllowedForFlexPool(employmentTypeDTO.isAllowedForFlexPool());
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

    public List<EmploymentType> getEmploymentTypeList(long countryId, boolean isDeleted) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        return countryGraphRepository.getEmploymentTypeByCountry(countryId, isDeleted);
    }

    public List<Map<String, Object>> getEmploymentTypeOfOrganization(long unitId, boolean isDeleted){
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId,0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }
        return organizationGraphRepository.getEmploymentTypeByOrganization(unitId,isDeleted);
    }

    public OrganizationEmploymentTypeDTO setEmploymentTypeSettingsOfOrganization(Long unitId, Long employmentTypeId, OrganizationEmploymentTypeDTO organizationEmploymentTypeDTO) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId,0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(employmentTypeId,0);
//        boolean employmentTypeExistInOrganization = employmentTypeGraphRepository.isEmploymentTypeExistInOrganization(unitId, organizationEmploymentTypeDTO.getEmploymentTypeId(), false);
        if( employmentType == null) {
            throw new DataNotFoundByIdException("Invalid Employment Type Id : "+employmentTypeId);
        }

        Boolean settingUpdated = employmentTypeGraphRepository.setEmploymentTypeSettingsForOrganization(unitId,employmentTypeId,
                organizationEmploymentTypeDTO.isAllowedForContactPerson(),
                organizationEmploymentTypeDTO.isAllowedForShiftPlan(),
                organizationEmploymentTypeDTO.isAllowedForFlexPool(),new Date().getTime(),new Date().getTime());
        if(settingUpdated){
            return organizationEmploymentTypeDTO;
        } else {
            logger.error("Employment type settings could not be updated in organization " +unitId);
            throw new InternalError("Employment type settings could not be updated");
        }
    }

    public List<EmploymentTypeDTO> getEmploymentTypeSettingsOfOrganization(Long unitId) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId,0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }
        return employmentTypeGraphRepository.getEmploymentTypeSettingsForOrganization(unitId, false);
    }

}
