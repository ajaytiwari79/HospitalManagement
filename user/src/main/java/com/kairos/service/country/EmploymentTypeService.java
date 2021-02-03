package com.kairos.service.country;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.EmploymentTypeKpiDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.organization.OrganizationEmploymentTypeDTO;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.persistence.model.country.default_data.OrganizationMappingDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.tag.TagQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.response.ExpertiseBasicDetails;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.region.RegionService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffRetrievalService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.enums.kpi.CalculationType.*;


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
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private RegionService regionService;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private CountryService countryService;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private StaffRetrievalService  staffRetrievalService;
    @Inject
    private ExpertiseService expertiseService;
    @Inject private TagGraphRepository tagGraphRepository;
    @Inject
    private SkillService skillService;
    @Inject
    private SkillGraphRepository skillGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;


    public EmploymentType addEmploymentType(Long countryId, EmploymentTypeDTO employmentTypeDTO) {
        validateEmploymentType(employmentTypeDTO);
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);

        }

        boolean isAlreadyExists = employmentTypeGraphRepository.findByNameExcludingCurrent(countryId, "(?i)" + employmentTypeDTO.getName().trim(), -1L);
        if (isAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_EMPLOYMENTTYPE_NAME_ALREADYEXIST,employmentTypeDTO.getName().trim());

        }
        EmploymentType employmentTypeToCreate = new EmploymentType(null,employmentTypeDTO.getName(), employmentTypeDTO.getDescription(), employmentTypeDTO.isAllowedForContactPerson(),
                employmentTypeDTO.isAllowedForShiftPlan(), employmentTypeDTO.isAllowedForFlexPool(), employmentTypeDTO.getEmploymentCategories(), employmentTypeDTO.getPaymentFrequency(),employmentTypeDTO.isEditableAtEmployment());
        employmentTypeToCreate.setWeeklyMinutes(employmentTypeDTO.getWeeklyMinutes());
        country.addEmploymentType(employmentTypeToCreate);
        countryGraphRepository.save(country);

        return employmentTypeToCreate;
    }

    public EmploymentType updateEmploymentType(long countryId, long employmentTypeId, EmploymentTypeDTO employmentTypeDTO) {
        validateEmploymentType(employmentTypeDTO);
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);

        }
        EmploymentType employmentTypeToUpdate = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToUpdate == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENTTYPE_ID_NOTFOUND,employmentTypeId);

        }
        if (!employmentTypeDTO.getName().trim().equalsIgnoreCase(employmentTypeToUpdate.getName())) {
            boolean isAlreadyExists = employmentTypeGraphRepository.findByNameExcludingCurrent(countryId, "(?i)" + employmentTypeDTO.getName().trim(), employmentTypeId);
            if (isAlreadyExists) {
                exceptionService.duplicateDataException(MESSAGE_EMPLOYMENTTYPE_NAME_ALREADYEXIST,employmentTypeDTO.getName().trim());

            }
        }
        EmploymentType employmentType=new EmploymentType(employmentTypeToUpdate.getId(),employmentTypeDTO.getName(),employmentTypeDTO.getDescription(),employmentTypeDTO.isAllowedForContactPerson(),
                employmentTypeDTO.isAllowedForShiftPlan(),employmentTypeDTO.isAllowedForFlexPool(),employmentTypeDTO.getEmploymentCategories(),employmentTypeDTO.getPaymentFrequency(),
                employmentTypeDTO.isEditableAtEmployment());
        employmentType.setWeeklyMinutes(employmentTypeDTO.getWeeklyMinutes());
        return employmentTypeGraphRepository.save(employmentType);
    }

    public boolean deleteEmploymentType(long countryId, long employmentTypeId) {
        EmploymentType employmentTypeToDelete = countryGraphRepository.getEmploymentTypeByCountryAndEmploymentType(countryId, employmentTypeId);
        if (employmentTypeToDelete == null) {
//            logger.debug("Finding level by id::" + levelId);
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENTTYPE_ID_NOTFOUND,employmentTypeId);

        }

        employmentTypeToDelete.setDeleted(true);
        employmentTypeGraphRepository.save(employmentTypeToDelete);
        return true;
    }

    public List<EmploymentType> getEmploymentTypeList(long countryId, boolean isDeleted) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);
        }
        return countryGraphRepository.getEmploymentTypeByCountry(countryId, isDeleted);
    }

    public List<Map<String, Object>> getEmploymentTypeOfOrganization(Long unitId, boolean isDeleted) {
        Organization organization=organizationService.fetchParentOrganization(unitId);
        List<Map<String, Object>> employmentTypes = unitGraphRepository.getEmploymentTypeByOrganization(organization.getId(), isDeleted);
        employmentTypes.forEach(employmentType->TranslationUtil.convertTranslationFromStringToMap(employmentType));
        return employmentTypes;
    }

    public List<EmploymentType> getEmploymentTypes(Long unitId){
        Organization organization=organizationService.fetchParentOrganization(unitId);
        return employmentTypeGraphRepository.getAllEmploymentTypeByOrganization(organization.getId(), false);
    }

    public OrganizationEmploymentTypeDTO setEmploymentTypeSettingsOfOrganization(Long unitId, Long employmentTypeId, OrganizationEmploymentTypeDTO organizationEmploymentTypeDTO) {
        Unit unit = (Optional.ofNullable(unitId).isPresent()) ? unitGraphRepository.findOne(unitId, 0) : null;
        if (!Optional.ofNullable(unit).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND,unitId);

        }
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(employmentTypeId, 0);
//        boolean employmentTypeExistInOrganization = employmentTypeGraphRepository.isEmploymentTypeExistInOrganization(unitId, organizationEmploymentTypeDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENTTYPE_ID_NOTFOUND,employmentTypeId);

        }

        Boolean settingUpdated = employmentTypeGraphRepository.setEmploymentTypeSettingsForOrganization(unitId, employmentTypeId,
                organizationEmploymentTypeDTO.isAllowedForContactPerson(),
                organizationEmploymentTypeDTO.isAllowedForShiftPlan(),
                organizationEmploymentTypeDTO.isAllowedForFlexPool(), organizationEmploymentTypeDTO.getPaymentFrequency(), DateUtils.getDate().getTime(), DateUtils.getDate().getTime());
        if (settingUpdated) {
            return organizationEmploymentTypeDTO;
        } else {
            logger.error("Employment type settings could not be updated in organization " + unitId);
            exceptionService.internalServerError(ERROR_EMPLOYMENTTYPE_NOTBEUPDATED);
        }
        return null;
    }

    public List<EmploymentTypeDTO> getEmploymentTypeSettingsOfOrganization(Long unitId) {
        Organization organization=organizationService.fetchParentOrganization(unitId);
        Long countryId=countryService.getCountryIdByUnitId(unitId);
        // Fetch all mapped settings with employment Type
        List<EmploymentTypeDTO> employmentSettingForOrganization = employmentTypeGraphRepository.getCustomizedEmploymentTypeSettingsForOrganization(countryId, unitId, false);
        List<Long> listOfConfiguredEmploymentTypeIds = new ArrayList<>();
        for (EmploymentTypeDTO employmentTypeDTO : employmentSettingForOrganization) {
            listOfConfiguredEmploymentTypeIds.add(employmentTypeDTO.getId());
        }

        // Fetch employment type setting which are not customized yet
        List<EmploymentTypeDTO> employmentSettingForParentOrganization = employmentTypeGraphRepository.getEmploymentTypeSettingsForOrganization(countryId, organization.getId(), false, listOfConfiguredEmploymentTypeIds);
        employmentSettingForOrganization.addAll(employmentSettingForParentOrganization);

        return employmentSettingForOrganization;
    }

    public OrganizationMappingDTO getOrganizationMappingDetails(Long countryId,String selectedDate) {
        OrganizationMappingDTO organizationMappingDTO = new OrganizationMappingDTO();
        // Set employment type
        organizationMappingDTO.setEmploymentTypes(countryGraphRepository.getEmploymentTypeByCountry(countryId,false));
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
        List<ExpertiseBasicDetails> expertise=expertiseGraphRepository.getAllExpertiseByCountryAndDate(countryId);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyCollectionPropertiesByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<ExpertiseResponseDTO> expertiseResponseDTOS=ObjectMapperUtils.copyCollectionPropertiesByMapper(expertise,ExpertiseResponseDTO.class);
        return new PriorityGroupDefaultData(employmentTypeDTOS,expertiseResponseDTOS);
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(long unitId, boolean isDeleted) {
        Long countryId=countryGraphRepository.getCountryIdByUnitId(unitId);
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<ExpertiseBasicDetails> expertises=expertiseGraphRepository.getAllExpertiseByCountryAndDate(countryId);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyCollectionPropertiesByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<ExpertiseResponseDTO> expertiseResponseDTOS=ObjectMapperUtils.copyCollectionPropertiesByMapper(expertises,ExpertiseResponseDTO.class);
        return new PriorityGroupDefaultData(employmentTypeDTOS,expertiseResponseDTOS);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypes(Long countryId, boolean isDeleted) {
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyCollectionPropertiesByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<DayTypeDTO>  dayTypes = activityIntegrationService.getDayTypesByCountryId(countryId);
        return new DayTypeEmploymentTypeWrapper(dayTypes,employmentTypeDTOS);

    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypesAtUnit(Long unitId, boolean isDeleted) {
        Long countryId=countryGraphRepository.getCountryIdByUnitId(unitId);
        return getDayTypesAndEmploymentTypes(countryId,isDeleted);
    }

    private void validateEmploymentType(EmploymentTypeDTO employmentTypeDTO){
        if (employmentTypeDTO.getName().trim().isEmpty()) {
            exceptionService.dataNotMatchedException(ERROR_EMPLOYMENTTYPE_NAME_NOTEMPTY);

        }
        if(employmentTypeDTO.getWeeklyMinutes()==null){
            exceptionService.actionNotPermittedException(ERROR_WEEKLY_MINUTES_ABSENT);
        }
        if(employmentTypeDTO.getWeeklyMinutes()>ONE_WEEK_MINUTES){
            exceptionService.actionNotPermittedException(ERROR_WEEKLY_MINUTES_EXCEEDS);
        }
    }

    public List<StaffKpiFilterDTO> getStaffByKpiFilter(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(staffEmploymentTypeDTO.getOrganizationId());
        List<StaffKpiFilterQueryResult> staffKpiFilterQueryResult=staffGraphRepository.getStaffsByFilter(staffEmploymentTypeDTO.getOrganizationId(), staffEmploymentTypeDTO.getUnitIds(), staffEmploymentTypeDTO.getEmploymentTypeIds(), staffEmploymentTypeDTO.getStartDate(), staffEmploymentTypeDTO.getEndDate(), staffEmploymentTypeDTO.getStaffIds(), organizationBaseEntity instanceof Organization,staffEmploymentTypeDTO.getTagIds());
        if(staffEmploymentTypeDTO.isIncludeDataForKPIs()){
            Set<String> filterValues = (Set<String>)staffEmploymentTypeDTO.getFilterBasedCriteria().values().stream().flatMap(list -> list.stream()).map(value->value.toString()).collect(Collectors.toSet());
            updateTagsDetails(filterValues,staffKpiFilterQueryResult);
            updateHourlyCostDetails(staffKpiFilterQueryResult,filterValues,staffEmploymentTypeDTO.getOrganizationId());
            updateSkillsDetails(filterValues,staffKpiFilterQueryResult);
            //updateDayTypeDetails(filterValues,staffEmploymentTypeDTO.getOrganizationId(),staffKpiFilterQueryResult);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffKpiFilterQueryResult, StaffKpiFilterDTO.class);
    }

    private Map<Long, BigDecimal> updateHourlyCostDetails(List<StaffKpiFilterQueryResult> staffKpiFilterQueryResult, Set<String> filterValues,Long organizationId) {
        Map<Long, BigDecimal> hourlyCostMap = new HashMap<>();
        if(filterValues.contains(XAxisConfig.VARIABLE_COST.toString())){
            List<EmploymentLinesQueryResult> hourlyCostPerLine=employmentGraphRepository.findFunctionalHourlyCost(staffKpiFilterQueryResult.stream().flatMap(staffKpiFilterQueryResult1 -> staffKpiFilterQueryResult1.getEmployment().stream().map(EmploymentQueryResult::getId)).collect(Collectors.toList()));
            hourlyCostMap = hourlyCostPerLine.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
            for (StaffKpiFilterQueryResult kpiFilterQueryResult : staffKpiFilterQueryResult) {
                for (EmploymentQueryResult employmentQueryResult : kpiFilterQueryResult.getEmployment()) {
                    employmentQueryResult.setUnitId(organizationId);
                    for (EmploymentLinesQueryResult employmentLine : employmentQueryResult.getEmploymentLines()) {
                        if(hourlyCostMap.containsKey(employmentLine.getId())) {
                            BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                            employmentLine.setHourlyCost(hourlyCost);
                        }
                    }
                }
            }
        }
        return hourlyCostMap;
    }

    private void updateTagsDetails(Set<String> filterValues, List<StaffKpiFilterQueryResult> staffKpiFilterQueryResult) {
        if(filterValues.contains(CARE_BUBBLE.toString())){
            List<Long> staffIds = staffKpiFilterQueryResult.stream().map(staffKpiFilterQueryResult1 -> staffKpiFilterQueryResult1.getId()).collect(Collectors.toList());
            List<StaffKpiFilterQueryResult> staffsWithTag = tagGraphRepository.getStaffsTagsByStaffIds(staffIds);
            Map<Long, List<TagQueryResult>> tagMap = staffsWithTag.stream().collect(Collectors.toMap(k->k.getId(), v->v.getTags()));
            for (StaffKpiFilterQueryResult kpiFilterQueryResult : staffKpiFilterQueryResult) {
                kpiFilterQueryResult.setTags(tagMap.getOrDefault(kpiFilterQueryResult.getId(),new ArrayList<>()));
            }
        }
    }

    private void updateSkillsDetails(Set<String> filterValues, List<StaffKpiFilterQueryResult> staffKpiFilterQueryResult) {
        if(CollectionUtils.containsAny(filterValues,newHashSet(STAFF_SKILLS_COUNT.toString(),PRESENCE_OVER_STAFFING.toString(),PRESENCE_UNDER_STAFFING.toString(),ABSENCE_OVER_STAFFING.toString(),ABSENCE_UNDER_STAFFING.toString()))) {
            List<Long> staffIds = staffKpiFilterQueryResult.stream().map(staffKpiFilterQueryResult1 -> staffKpiFilterQueryResult1.getId()).collect(Collectors.toList());
            Map<Long, List<SkillLevelDTO>> skillMap = skillGraphRepository.getAllStaffSkillAndLevelByStaffIds(staffIds).stream().collect(Collectors.toMap(staffQueryResult -> staffQueryResult.getId(), staffQueryResult -> staffQueryResult.getSkills()));
            for (StaffKpiFilterQueryResult kpiFilterQueryResult : staffKpiFilterQueryResult) {
                kpiFilterQueryResult.setSkills(skillMap.getOrDefault(kpiFilterQueryResult.getId(),new ArrayList<>()));
            }
        }
    }


    //TODO Integrated
//    private void updateDayTypeDetails(Set<String> filterValues, Long organizationId, List<StaffKpiFilterQueryResult> staffKpiFilterQueryResult) {
//        if(filterValues.contains(XAxisConfig.VARIABLE_COST.toString())) {
//            List<DayTypeDTO> dayTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(dayTypeGraphRepository.getDayTypeByOrganizationById(organizationId), DayTypeDTO.class);
//            for (StaffKpiFilterQueryResult kpiFilterQueryResult : staffKpiFilterQueryResult) {
//                kpiFilterQueryResult.setDayTypeDTOS(dayTypeDTOS);
//            }
//        }
//    }


    public DefaultKpiDataDTO getKpiDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(staffEmploymentTypeDTO.getOrganizationId());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(staffGraphRepository.getStaffsByFilter(staffEmploymentTypeDTO.getOrganizationId(), staffEmploymentTypeDTO.getUnitIds(), staffEmploymentTypeDTO.getEmploymentTypeIds(), staffEmploymentTypeDTO.getStartDate(), staffEmploymentTypeDTO.getEndDate(), staffEmploymentTypeDTO.getStaffIds(), organizationBaseEntity instanceof Organization,staffEmploymentTypeDTO.getTagIds()), StaffKpiFilterDTO.class);
        return new DefaultKpiDataDTO(staffKpiFilterDTOS);
    }

    public DefaultKpiDataDTO getKpiFilterDefaultData(Long unitId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        Long staffId =staffRetrievalService.getStaffIdOfLoggedInUser(unitId);
        List<TagQueryResult> tags = new ArrayList<>();
        if(isNotNull(unitId)){
            Long organizationId = organizationBaseRepository.findParentOrgId(unitId);
            tags = tagGraphRepository.getListOfOrganizationTagsByMasterDataType(organizationId, false, "", MasterDataTypeEnum.STAFF.toString());
        }
        List<com.kairos.dto.user.country.tag.TagDTO> tagDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(tags,com.kairos.dto.user.country.tag.TagDTO.class);
        List<OrganizationCommonDTO>  organizationCommonDTO=ObjectMapperUtils.copyCollectionPropertiesByMapper(unitGraphRepository.getAllOrganizaionByStaffid(staffId),OrganizationCommonDTO.class);
        List<Long> unitIds=organizationCommonDTO.stream().map(organizationCommonDto -> organizationCommonDto.getId()).collect(Collectors.toList());
        List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS=ObjectMapperUtils.copyCollectionPropertiesByMapper(countryGraphRepository.getEmploymentTypes(countryId,false),EmploymentTypeKpiDTO.class);
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(staffGraphRepository.getAllStaffIdAndNameByUnitId(isNull(staffId)?Arrays.asList(unitId):unitIds), StaffKpiFilterDTO.class);
        return new DefaultKpiDataDTO(countryId,staffKpiFilterDTOS, new ArrayList<>(),organizationCommonDTO,employmentTypeKpiDTOS,tagDTOS);
    }

    public DefaultKpiDataDTO getKpiAllDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = getStaffByKpiFilter(staffEmploymentTypeDTO);
        return DefaultKpiDataDTO.builder().staffKpiFilterDTOs(staffKpiFilterDTOS).build();
    }
}