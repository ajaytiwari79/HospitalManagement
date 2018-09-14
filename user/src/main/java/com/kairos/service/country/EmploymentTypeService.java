package com.kairos.service.country;

import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.user.organization.OrganizationEmploymentTypeDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.persistence.model.country.default_data.OrganizationMappingDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.region.RegionService;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.utils.DateUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by prerna on 2/11/17.
 */
@Service
@Transactional
public class EmploymentTypeService {

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
    @Inject
    private ExceptionService exceptionService;
    @Inject private DayTypeGraphRepository dayTypeGraphRepository;

    public EmploymentType addEmploymentType(Long countryId, EmploymentTypeDTO employmentTypeDTO) {
        if (employmentTypeDTO.getName().trim().isEmpty()) {
            exceptionService.dataNotMatchedException("error.employmentType.name.notEmpty");

        }
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }

        boolean isAlreadyExists = employmentTypeGraphRepository.findByNameExcludingCurrent(countryId, "(?i)" + employmentTypeDTO.getName().trim(), -1L);
        if (isAlreadyExists) {
            exceptionService.duplicateDataException("message.employmentType.name.alreadyExist",employmentTypeDTO.getName().trim());

        }
        EmploymentType employmentTypeToCreate = new EmploymentType(employmentTypeDTO.getName(), employmentTypeDTO.getDescription(), employmentTypeDTO.isAllowedForContactPerson(),
                employmentTypeDTO.isAllowedForShiftPlan(), employmentTypeDTO.isAllowedForFlexPool(), employmentTypeDTO.getEmploymentCategories(), employmentTypeDTO.getPaymentFrequency());
        country.addEmploymentType(employmentTypeToCreate);
        countryGraphRepository.save(country);

        return employmentTypeToCreate;
    }

    public EmploymentType updateEmploymentType(long countryId, long employmentTypeId, EmploymentTypeDTO employmentTypeDTO) {
        if (employmentTypeDTO.getName().trim().isEmpty()) {
            exceptionService.dataNotMatchedException("error.employmentType.name.notEmpty");

        }
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        EmploymentType employmentTypeToUpdate = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToUpdate == null) {
            exceptionService.dataNotFoundByIdException("message.employmentType.id.notFound",employmentTypeId);

        }
        if (!employmentTypeDTO.getName().trim().equalsIgnoreCase(employmentTypeToUpdate.getName())) {
            boolean isAlreadyExists = employmentTypeGraphRepository.findByNameExcludingCurrent(countryId, "(?i)" + employmentTypeDTO.getName().trim(), employmentTypeId);
            if (isAlreadyExists) {
                exceptionService.duplicateDataException("message.employmentType.name.alreadyExist",employmentTypeDTO.getName().trim());

            }
        }
        employmentTypeToUpdate.setName(employmentTypeDTO.getName());
        employmentTypeToUpdate.setDescription(employmentTypeDTO.getDescription());
        employmentTypeToUpdate.setAllowedForContactPerson(employmentTypeDTO.isAllowedForContactPerson());
        employmentTypeToUpdate.setAllowedForShiftPlan(employmentTypeDTO.isAllowedForShiftPlan());
        employmentTypeToUpdate.setAllowedForFlexPool(employmentTypeDTO.isAllowedForFlexPool());
        employmentTypeToUpdate.setEmploymentCategories(employmentTypeDTO.getEmploymentCategories());
        employmentTypeToUpdate.setPaymentFrequency(employmentTypeDTO.getPaymentFrequency());
        return employmentTypeGraphRepository.save(employmentTypeToUpdate);
    }

    public boolean deleteEmploymentType(long countryId, long employmentTypeId) {
        EmploymentType employmentTypeToDelete = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToDelete == null) {
//            logger.debug("Finding level by id::" + levelId);
            exceptionService.dataNotFoundByIdException("message.employmentType.id.notFound",employmentTypeId);

        }

        employmentTypeToDelete.setDeleted(true);
        employmentTypeGraphRepository.save(employmentTypeToDelete);
        return true;
    }

    public List<EmploymentType> getEmploymentTypeList(long countryId, boolean isDeleted) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        return countryGraphRepository.getEmploymentTypeByCountry(countryId, isDeleted);
    }

    public List<Map<String, Object>> getEmploymentTypeOfOrganization(long unitId, boolean isDeleted) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        Organization parent = organizationService.fetchParentOrganization(unitId);
        return organizationGraphRepository.getEmploymentTypeByOrganization(parent.getId(), isDeleted);
    }

    public OrganizationEmploymentTypeDTO setEmploymentTypeSettingsOfOrganization(Long unitId, Long employmentTypeId, OrganizationEmploymentTypeDTO organizationEmploymentTypeDTO) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(employmentTypeId, 0);
//        boolean employmentTypeExistInOrganization = employmentTypeGraphRepository.isEmploymentTypeExistInOrganization(unitId, organizationEmploymentTypeDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            exceptionService.dataNotFoundByIdException("message.employmentType.id.notFound",employmentTypeId);

        }

        Boolean settingUpdated = employmentTypeGraphRepository.setEmploymentTypeSettingsForOrganization(unitId, employmentTypeId,
                organizationEmploymentTypeDTO.isAllowedForContactPerson(),
                organizationEmploymentTypeDTO.isAllowedForShiftPlan(),
                organizationEmploymentTypeDTO.isAllowedForFlexPool(), organizationEmploymentTypeDTO.getPaymentFrequency(), DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
        if (settingUpdated) {
            return organizationEmploymentTypeDTO;
        } else {
            logger.error("Employment type settings could not be updated in organization " + unitId);
            exceptionService.internalServerError("error.employmentType.notBeupdated");
        }
        return null;
    }

    public List<EmploymentTypeDTO> getEmploymentTypeSettingsOfOrganization(Long unitId) {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);

        // Fetch all mapped settings with employment Type
        List<EmploymentTypeDTO> employmentSettingForOrganization = employmentTypeGraphRepository.getCustomizedEmploymentTypeSettingsForOrganization(countryId, unitId, false);
        List<Long> listOfConfiguredEmploymentTypeIds = new ArrayList<>();
        for (EmploymentTypeDTO employmentTypeDTO : employmentSettingForOrganization) {
            listOfConfiguredEmploymentTypeIds.add(employmentTypeDTO.getId());
        }

        // Fetch employment type setting which are not customized yet
        List<EmploymentTypeDTO> employmentSettingForParentOrganization = employmentTypeGraphRepository.getEmploymentTypeSettingsForOrganization(countryId, parent.getId(), false, listOfConfiguredEmploymentTypeIds);
        employmentSettingForOrganization.addAll(employmentSettingForParentOrganization);

        return employmentSettingForOrganization;
    }

    public OrganizationMappingDTO getOrganizationMappingDetails(Long countryId,String selectedDate) throws ParseException {
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
    public PriorityGroupDefaultData getExpertiseAndEmployment(long countryId, boolean isDeleted) {
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<ExpertiseDTO> expertise=expertiseGraphRepository.getAllExpertiseByCountryAndDate(countryId,DateUtil.getCurrentDateMillis());
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<ExpertiseResponseDTO> expertiseResponseDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(expertise,ExpertiseResponseDTO.class);
        return new PriorityGroupDefaultData(employmentTypeDTOS,expertiseResponseDTOS);
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(long unitId, boolean isDeleted) {
        Long countryId=countryGraphRepository.getCountryIdByUnitId(unitId);
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<ExpertiseDTO> expertises=expertiseGraphRepository.getAllExpertiseByCountryAndDate(countryId,DateUtil.getCurrentDateMillis());
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<ExpertiseResponseDTO> expertiseResponseDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(expertises,ExpertiseResponseDTO.class);
        return new PriorityGroupDefaultData(employmentTypeDTOS,expertiseResponseDTOS);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypes(Long countryId, boolean isDeleted) {
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<DayType>  dayTypes = dayTypeGraphRepository.findByCountryId(countryId);

        List<com.kairos.dto.user.country.day_type.DayType> dayTypesDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypes, com.kairos.dto.user.country.day_type.DayType.class);
        DayTypeEmploymentTypeWrapper dayTypeEmploymentTypeWrapper= new DayTypeEmploymentTypeWrapper(dayTypesDTOS,employmentTypeDTOS);
        return  dayTypeEmploymentTypeWrapper;

    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypesAtUnit(Long unitId, boolean isDeleted) {
        Long countryId=countryGraphRepository.getCountryIdByUnitId(unitId);
        return getDayTypesAndEmploymentTypes(countryId,isDeleted);
    }


}