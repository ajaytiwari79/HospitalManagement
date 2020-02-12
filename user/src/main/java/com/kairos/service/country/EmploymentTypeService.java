package com.kairos.service.country;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.*;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.basic_details.CountryHolidayCalender;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.organization.OrganizationEmploymentTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.persistence.model.country.default_data.OrganizationMappingDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.response.ExpertiseDTO;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.region.RegionService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;


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
    private ReasonCodeService reasonCodeService;
    @Inject
    private ExceptionService exceptionService;
    @Inject private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject private TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject private StaffRetrievalService  staffRetrievalService;
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private SkillService skillService;
    @Inject CountryHolidayCalenderService countryHolidayCalenderService;


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
        return unitGraphRepository.getEmploymentTypeByOrganization(organization.getId(), isDeleted);
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
                organizationEmploymentTypeDTO.isAllowedForFlexPool(), organizationEmploymentTypeDTO.getPaymentFrequency(), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
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
        List<ExpertiseDTO> expertise=expertiseGraphRepository.getAllExpertiseByCountryAndDate(countryId);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<ExpertiseResponseDTO> expertiseResponseDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertise,ExpertiseResponseDTO.class);
        return new PriorityGroupDefaultData(employmentTypeDTOS,expertiseResponseDTOS);
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(long unitId, boolean isDeleted) {
        Long countryId=countryGraphRepository.getCountryIdByUnitId(unitId);
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<ExpertiseDTO> expertises=expertiseGraphRepository.getAllExpertiseByCountryAndDate(countryId);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<ExpertiseResponseDTO> expertiseResponseDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertises,ExpertiseResponseDTO.class);
        return new PriorityGroupDefaultData(employmentTypeDTOS,expertiseResponseDTOS);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypes(Long countryId, boolean isDeleted) {
        List<EmploymentTypeDTO> employmentTypes=countryGraphRepository.getEmploymentTypes(countryId,isDeleted);
        List<com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO> employmentTypeDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(employmentTypes, com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO.class);
        List<DayType>  dayTypes = dayTypeGraphRepository.findByCountryId(countryId);

        List<com.kairos.dto.user.country.day_type.DayType> dayTypesDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypes, com.kairos.dto.user.country.day_type.DayType.class);
        return new DayTypeEmploymentTypeWrapper(dayTypesDTOS,employmentTypeDTOS);

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
        Long countryId=countryGraphRepository.getCountryIdByUnitId(staffEmploymentTypeDTO.getOrganizationId());
        List<DayTypeDTO> dayTypeDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypeGraphRepository.findByCountryId(countryId),DayTypeDTO.class);
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(staffEmploymentTypeDTO.getOrganizationId());
        List<StaffKpiFilterQueryResult> staffKpiFilterQueryResult=staffGraphRepository.getStaffsByFilter(staffEmploymentTypeDTO.getOrganizationId(), staffEmploymentTypeDTO.getUnitIds(), staffEmploymentTypeDTO.getEmploymentTypeIds(), staffEmploymentTypeDTO.getStartDate(), staffEmploymentTypeDTO.getEndDate(), staffEmploymentTypeDTO.getStaffIds(), organizationBaseEntity instanceof Organization);
        List<EmploymentLinesQueryResult> hourlyCostPerLine=employmentGraphRepository.findFunctionalHourlyCost(staffKpiFilterQueryResult.stream().flatMap(staffKpiFilterQueryResult1 -> staffKpiFilterQueryResult1.getEmployment().stream().map(EmploymentQueryResult::getId)).collect(Collectors.toList()));
       // List<EmploymentLinesQueryResult> payTableAmount=employmentGraphRepository.findSum(staffKpiFilterQueryResult.stream().flatMap(staffKpiFilterQueryResult1 -> staffKpiFilterQueryResult1.getEmployment().stream().map(EmploymentQueryResult::getId)).collect(Collectors.toList()),staffEmploymentTypeDTO.getStartDate());
//        Map<Long,Long> longIntegerMap=payTableAmount.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getPayTableAmount));
        Map<Long, BigDecimal> hourlyCostMap = hourlyCostPerLine.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
        List<Long> expertiseIds=staffKpiFilterQueryResult.stream().flatMap(staffKpiFilterQueryResult1 -> staffKpiFilterQueryResult1.getEmployment().stream().map(employmentQueryResult -> employmentQueryResult.getExpertise().getId())).collect(Collectors.toList());
        Map<Long, SeniorAndChildCareDaysDTO> expertiseIdsAndSeniorAndChildCareDaysMap=expertiseService.getSeniorAndChildCareDaysMapByExpertiseIds(expertiseIds);
        for (StaffKpiFilterQueryResult kpiFilterQueryResult : staffKpiFilterQueryResult) {
            kpiFilterQueryResult.setDayTypeDTOS(dayTypeDTOS);
//            if(ObjectUtils.isNotNull(longIntegerMap.get(kpiFilterQueryResult.getId()))) {
//                kpiFilterQueryResult.setPayTableAmount(longIntegerMap.get(kpiFilterQueryResult.getId()));
//            }
            for (EmploymentQueryResult employmentQueryResult : kpiFilterQueryResult.getEmployment()) {
                employmentQueryResult.setUnitId(staffEmploymentTypeDTO.getOrganizationId());
                employmentQueryResult.setSeniorAndChildCareDays(expertiseIdsAndSeniorAndChildCareDaysMap.get(employmentQueryResult.getExpertise().getId()));
                for (EmploymentLinesQueryResult employmentLine : employmentQueryResult.getEmploymentLines()) {
                    if(hourlyCostMap.containsKey(employmentLine.getId())) {
                        BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                        employmentLine.setHourlyCost(hourlyCost);
                    }
                }
            }
        }
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffKpiFilterQueryResult, StaffKpiFilterDTO.class);
    }


    public DefaultKpiDataDTO getKpiDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(staffEmploymentTypeDTO.getOrganizationId());
        Long countryId = countryGraphRepository.getCountryIdByUnitId(staffEmploymentTypeDTO.getOrganizationId());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffGraphRepository.getStaffsByFilter(staffEmploymentTypeDTO.getOrganizationId(), staffEmploymentTypeDTO.getUnitIds(), staffEmploymentTypeDTO.getEmploymentTypeIds(), staffEmploymentTypeDTO.getStartDate(), staffEmploymentTypeDTO.getEndDate(), staffEmploymentTypeDTO.getStaffIds(), organizationBaseEntity instanceof Organization), StaffKpiFilterDTO.class);
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get("dayTypeId") != null).collect(Collectors.groupingBy(k -> ((Long) k.get("dayTypeId")), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS = dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), ObjectMapperUtils.copyPropertiesOfCollectionByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings(),dayType.getColorCode())
        ).collect(Collectors.toList());
//        List<DayTypeDTO> dayTypeDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypeGraphRepository.findByCountryId(countryId), DayTypeDTO.class);
        List<Long> unitIds = ObjectUtils.isCollectionNotEmpty(staffEmploymentTypeDTO.getUnitIds()) ? staffEmploymentTypeDTO.getUnitIds() : Arrays.asList(staffEmploymentTypeDTO.getOrganizationId());
        List<TimeSlotDTO> timeSlotSetDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(unitIds, TimeSlotType.SHIFT_PLANNING), TimeSlotDTO.class);
        return new DefaultKpiDataDTO(staffKpiFilterDTOS, dayTypeDTOS, timeSlotSetDTOS);
    }

    public DefaultKpiDataDTO getKpiFilterDefaultData(Long unitId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        Long staffId =staffRetrievalService.getStaffIdOfLoggedInUser(unitId);
        List<OrganizationCommonDTO>  organizationCommonDTO=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(unitGraphRepository.getAllOrganizaionByStaffid(staffId),OrganizationCommonDTO.class);
        List<Long> unitIds=organizationCommonDTO.stream().map(organizationCommonDto -> organizationCommonDto.getId()).collect(Collectors.toList());
        List<DayTypeDTO> dayTypeDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypeGraphRepository.findByCountryId(countryId), DayTypeDTO.class);
        List<TimeSlotDTO> timeSlotSetDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(isCollectionNotEmpty(unitIds)?unitIds:Arrays.asList(unitId), TimeSlotType.SHIFT_PLANNING), TimeSlotDTO.class);
        List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(countryGraphRepository.getEmploymentTypes(countryId,false),EmploymentTypeKpiDTO.class);
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffGraphRepository.getAllStaffIdAndNameByUnitId(isNull(staffId)?Arrays.asList(unitId):unitIds), StaffKpiFilterDTO.class);
        List<ReasonCodeDTO> reasonCodeDTOS=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(reasonCodeService.getReasonCodesByUnitIds(isCollectionNotEmpty(unitIds)?unitIds:Arrays.asList(unitId), ReasonCodeType.FORCEPLAN),ReasonCodeDTO.class);
        return new DefaultKpiDataDTO(countryId,staffKpiFilterDTOS, dayTypeDTOS, timeSlotSetDTOS,organizationCommonDTO,employmentTypeKpiDTOS,reasonCodeDTOS);
    }

    public DefaultKpiDataDTO getKpiAllDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = getStaffByKpiFilter(staffEmploymentTypeDTO);
        List<TimeSlotDTO> timeSlotDTOS = timeSlotService.getUnitTimeSlot(staffEmploymentTypeDTO.getOrganizationId());
        List<Long> staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        LocalDate startDate = asLocalDate(staffEmploymentTypeDTO.getStartDate());
        LocalDate endDate = asLocalDate(staffEmploymentTypeDTO.getEndDate());
        Map<String, List<StaffPersonalDetail>> skills = skillService.getStaffSkillAndLevelByStaffIds(staffIds, startDate, endDate);
        List<CountryHolidayCalenderDTO> holidayCalenders = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(countryHolidayCalenderService.getCountryHolidayCalenders(UserContext.getUserDetails().getCountryId(), startDate, endDate), CountryHolidayCalenderDTO.class);
        return new DefaultKpiDataDTO(staffKpiFilterDTOS, timeSlotDTOS, skills, holidayCalenders);
    }
}