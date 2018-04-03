package com.kairos.service.country;

import com.kairos.client.dto.organization.OrganizationEmploymentTypeDTO;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.dto.EmploymentTypeDTO;

import com.kairos.persistence.model.user.country.dto.OrganizationMappingDTO;

import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;

import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;

import com.kairos.service.UserBaseService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.region.RegionService;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject
    private RegionService regionService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;

    public EmploymentType addEmploymentType(Long countryId, EmploymentTypeDTO employmentTypeDTO) {
        if(employmentTypeDTO.getName().trim().isEmpty()){
            throw new DataNotMatchedException("Name can't be blank");
        }
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }

        boolean isAlreadyExists=employmentTypeGraphRepository.findByNameExcludingCurrent(countryId,"(?i)"+employmentTypeDTO.getName().trim(),-1L);
        if(isAlreadyExists){
            throw new DuplicateDataException("EmploymentType already exists"+employmentTypeDTO.getName());
        }
        EmploymentType employmentTypeToCreate = employmentTypeDTO.generateEmploymentTypeFromEmploymentTypeDTO();
        country.addEmploymentType(employmentTypeToCreate);
        countryGraphRepository.save(country);

        return employmentTypeToCreate;
    }

    public EmploymentType updateEmploymentType(long countryId, long employmentTypeId, EmploymentTypeDTO employmentTypeDTO) {
        if(employmentTypeDTO.getName().trim().isEmpty()){
            throw new DataNotMatchedException("Name can't be blank");
        }
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        EmploymentType employmentTypeToUpdate = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToUpdate == null) {
            throw new DataNotFoundByIdException("Incorrect Employment Type id " + employmentTypeId);
        }
        if(!employmentTypeDTO.getName().trim().equalsIgnoreCase(employmentTypeToUpdate.getName())){
            boolean isAlreadyExists=employmentTypeGraphRepository.findByNameExcludingCurrent(countryId,"(?i)"+employmentTypeDTO.getName().trim(),employmentTypeId);
            if(isAlreadyExists){
                throw new DuplicateDataException("EmploymentType already exists"+employmentTypeDTO.getName());
            }
        }
        employmentTypeToUpdate.setName(employmentTypeDTO.getName());
        employmentTypeToUpdate.setDescription(employmentTypeDTO.getDescription());
        employmentTypeToUpdate.setAllowedForContactPerson(employmentTypeDTO.isAllowedForContactPerson());
        employmentTypeToUpdate.setAllowedForShiftPlan(employmentTypeDTO.isAllowedForShiftPlan());
        employmentTypeToUpdate.setAllowedForFlexPool(employmentTypeDTO.isAllowedForFlexPool());
        employmentTypeToUpdate.setPermanent(employmentTypeDTO.isPermanent());
        employmentTypeToUpdate.setTemporary(employmentTypeDTO.isTemporary());
        employmentTypeToUpdate.setGuest(employmentTypeDTO.isGuest());
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
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        return countryGraphRepository.getEmploymentTypeByCountry(countryId, isDeleted);
    }

    public List<Map<String, Object>> getEmploymentTypeOfOrganization(long unitId, boolean isDeleted) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }
        Organization parent = organizationService.fetchParentOrganization(unitId);
        return organizationGraphRepository.getEmploymentTypeByOrganization(parent.getId(), isDeleted);
    }

    public OrganizationEmploymentTypeDTO setEmploymentTypeSettingsOfOrganization(Long unitId, Long employmentTypeId, OrganizationEmploymentTypeDTO organizationEmploymentTypeDTO) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(employmentTypeId, 0);
//        boolean employmentTypeExistInOrganization = employmentTypeGraphRepository.isEmploymentTypeExistInOrganization(unitId, organizationEmploymentTypeDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            throw new DataNotFoundByIdException("Invalid Employment Type Id : " + employmentTypeId);
        }

        Boolean settingUpdated = employmentTypeGraphRepository.setEmploymentTypeSettingsForOrganization(unitId, employmentTypeId,
                organizationEmploymentTypeDTO.isAllowedForContactPerson(),
                organizationEmploymentTypeDTO.isAllowedForShiftPlan(),
                organizationEmploymentTypeDTO.isAllowedForFlexPool(), DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
        if (settingUpdated) {
            return organizationEmploymentTypeDTO;
        } else {
            logger.error("Employment type settings could not be updated in organization " + unitId);
            throw new InternalError("Employment type settings could not be updated");
        }
    }

    public List<EmploymentTypeDTO> getEmploymentTypeSettingsOfOrganization(Long unitId) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);

        // Fetch all mapped settings with employment Type
        List<EmploymentTypeDTO> employmentSettingForOrganization = employmentTypeGraphRepository.getCustomizedEmploymentTypeSettingsForOrganization(countryId, unitId, false);
        List<Long> listOfConfiguredEmploymentTypeIds = new ArrayList<>();
        for ( EmploymentTypeDTO employmentTypeDTO: employmentSettingForOrganization) {
            listOfConfiguredEmploymentTypeIds.add(employmentTypeDTO.getId());
        }

        // Fetch employment type setting which are not customized yet
        List<EmploymentTypeDTO> employmentSettingForParentOrganization = employmentTypeGraphRepository.getEmploymentTypeSettingsForOrganization(countryId, parent.getId(), false, listOfConfiguredEmploymentTypeIds);
        employmentSettingForOrganization.addAll(employmentSettingForParentOrganization);

        return employmentSettingForOrganization;
    }

    public OrganizationMappingDTO getOrganizationMappingDetails(Long countryId) {
        OrganizationMappingDTO organizationMappingDTO = new OrganizationMappingDTO();
        // Set employment type
        organizationMappingDTO.setEmploymentTypes(getEmploymentTypeList(countryId, false));
        // set Expertise
        organizationMappingDTO.setExpertise(expertiseGraphRepository.getAllExpertiseByCountry(countryId));
        //set levels
        organizationMappingDTO.setLevels(countryGraphRepository.getLevelsByCountry(countryId));
        // set regions
        organizationMappingDTO.setRegions(regionService.getRegionByCountryId(countryId));
        //set organization Hierarchy
        organizationMappingDTO.setOrganizationTypeHierarchy(organizationTypeGraphRepository.getAllOrganizationTypeWithSubTypeByCountryId(countryId));
        return organizationMappingDTO;
    }

}
