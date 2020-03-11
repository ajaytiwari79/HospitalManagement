package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.common.StaffFilterDataDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.filter.FilterDetailDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.*;
import com.kairos.enums.cta.AccountType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.filter.*;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffFavouriteFilterGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.user_filter.FilterGroupGraphRepository;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.country.FunctionService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.GroupService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.UnitService;
import com.kairos.service.skill.SkillService;
import com.kairos.wrapper.staff.StaffEmploymentTypeWrapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.CommonConstants.FULL_WEEK;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.enums.FilterType.PAY_GRADE_LEVEL;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.shift.ShiftStatus.*;

/**
 * Created by prerna on 1/5/18.
 */
@Transactional
@Service
public class StaffFilterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private FilterGroupGraphRepository filterGroupGraphRepository;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private
    EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private StaffFavouriteFilterGraphRepository staffFavouriteFilterGraphRepository;
    @Inject
    private
    UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private FunctionService functionService;
    @Inject
    private SkillService skillService;
    @Inject
    private TagService tagService;
    @Inject
    private GroupService groupService;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private UnitService unitService;


    public FiltersAndFavouriteFiltersDTO getAllAndFavouriteFilters(String moduleId, Long unitId) {

        return getAllAndFavouriteFiltersFromParent(moduleId, unitId);

    }

    public FiltersAndFavouriteFiltersDTO getAllAndFavouriteFiltersFromParent(String moduleId, Long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization organization;
        Long organizationId = organizationService.fetchParentOrganization(unitId).getId();
        if (accessPageRepository.isHubMember(userId)) {
            organization = accessPageRepository.fetchParentHub(userId);
        } else {
            organization = organizationGraphRepository.findOne(organizationId);
            if (!Optional.ofNullable(organization).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, organizationId);
            }
        }
        Long countryId = UserContext.getUserDetails().getCountryId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organization.getId());
        if(isNull(staff)){
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNITID_NOTFOUND);
        }
        return new FiltersAndFavouriteFiltersDTO(
                getAllFilters(moduleId, countryId, unitId),
                getFavouriteFilters(moduleId, staff.getId()));
    }

    private List<FilterSelectionQueryResult> dtoToQueryesultConverter(List<FilterDetailDTO> filterData, ObjectMapper objectMapper) {
        List<FilterSelectionQueryResult> queryResults = new ArrayList<>();

        filterData.forEach(filterDetailDTO -> queryResults.add(objectMapper.convertValue(filterDetailDTO, FilterSelectionQueryResult.class)));
        return queryResults;
    }

    //todo send single call for get activity data
    private List<FilterSelectionQueryResult> getFilterDetailsByFilterType(FilterType filterType, Long countryId, Long unitId) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (filterType) {
            case EMPLOYMENT_TYPE:
                return getEmploymenTypeFiltersDataByCountry(countryId);
            case GENDER:
                return dtoToQueryesultConverter(Gender.getListOfGenderForFilters(), objectMapper);
            case STAFF_STATUS:
                return dtoToQueryesultConverter(StaffStatusEnum.getListOfStaffStatusForFilters(), objectMapper);
            case EXPERTISE:
                List<Long> allUnitIds = organizationBaseRepository.fetchAllUnitIds(unitId);
                OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(allUnitIds);
                if(ObjectUtils.isNotNull(servicesAndLevel)) {
                    return expertiseGraphRepository.getExpertiseByCountryIdForFilters(countryId, servicesAndLevel.getServicesId());
                }
                break;
            case EMPLOYMENT:
                return dtoToQueryesultConverter(Employment.getListOfEmploymentForFilters(), objectMapper);
            case ACTIVITY_TIMECALCULATION_TYPE:
                return newArrayList(new FilterSelectionQueryResult(FULL_DAY_CALCULATION,StringUtils.capitalize(FULL_DAY_CALCULATION.toLowerCase().replace("_"," "))),new FilterSelectionQueryResult(FULL_WEEK,StringUtils.capitalize(FULL_WEEK.toLowerCase().replace("_"," "))));
            case TIME_TYPE:
                return getAllTimeType(countryId);
            case ACTIVITY_STATUS:
                return getStatusFilter();
            case REAL_TIME_STATUS:
                return dtoToQueryesultConverter(RealTimeStatus.getListOfRealtimeStatusForFilters(), objectMapper);
            case TIME_SLOT:
                return getTimeSlots();
            case ABSENCE_ACTIVITY:
                return getAnsenceActivity(unitId);
            case  PLANNED_TIME_TYPE:
                return getPlannedTimeType(countryId);
            case FUNCTIONS:
                return getAllFunctions(countryId);
            case SKILLS:
                return getAllSkills(unitId);
            case VALIDATED_BY:
                return getTAStatus();
            case TAGS:
                return getTags(unitId);
            case GROUPS:
                return getGroups(unitId);
            case NIGHT_WORKERS:
                return dtoToQueryesultConverter(StaffWorkingType.getListOfStaffWorkingTypeForFilters(), objectMapper);
            case ESCALATION_CAUSED_BY:
                return dtoToQueryesultConverter(AccessGroupRole.getListOfAccessGroupRoleForFilters(), objectMapper);
            case TEAM:
            case MAIN_TEAM:
                return teamGraphRepository.getTeamsByUnitIdForFilters(unitId);
            case SKILL_LEVEL:
                return dtoToQueryesultConverter(SkillLevel.getListOfSkillLevelForFilters(), objectMapper);
            case ACCESS_GROUPS:
                return unitService.getAllAccessGroupByUnitIdForFilter(unitId);
            case CTA_ACCOUNT_TYPE:
                return getCTAAccounts();
            default:
                break;
        }
        return new ArrayList<>();
    }

    private List<FilterSelectionQueryResult> getTags(Long orgId) {
        List<TagDTO> tags = tagService.getTagsByOrganizationIdAndMasterDataType(orgId, MasterDataTypeEnum.STAFF);
        return tags.stream().map(tag  -> new FilterSelectionQueryResult(tag.getId().toString(),tag.getName())).collect(Collectors.toList());
    }

    private List<FilterSelectionQueryResult> getGroups(Long unitId) {
        List<GroupDTO> groups = groupService.getAllGroupsOfUnit(unitId);
        return groups.stream().map(group  -> new FilterSelectionQueryResult(group.getId().toString(),group.getName())).collect(Collectors.toList());
    }

    private List<FilterSelectionQueryResult> getCTAAccounts() {
        return Arrays.stream(AccountType.values()).map(accountType -> new FilterSelectionQueryResult(accountType.name(),accountType.toString())).collect(Collectors.toList());
    }

    private List<FilterSelectionQueryResult> getTAStatus(){
        return Arrays.stream(AccessGroupRole.values()).map(accessGroupRole -> new FilterSelectionQueryResult(accessGroupRole.name(),accessGroupRole.toString())).collect(Collectors.toList());
    }

    private  List<FilterSelectionQueryResult> getAllFunctions(Long countryId){
        List<FunctionDTO> functionDTOS=functionService.getFunctionsByCountry(countryId);
        return functionDTOS.stream().map(functionDTO  -> new FilterSelectionQueryResult(functionDTO.getId().toString(),functionDTO.getName())).collect(Collectors.toList());
    }

    private  List<FilterSelectionQueryResult> getAllSkills(Long unitId){
        List<Map<String, Object>> skillsMaps = skillService.getSkillsOfOrganization(unitId);
        List<FilterSelectionQueryResult> filterSkillData = new ArrayList<>();
        for (Map<String, Object> map : skillsMaps) {
            for (Map<String, Object> skill : ((List<Map<String, Object>>)((Map<String, Object>) map.get("data")).get("skills"))) {
                filterSkillData.add(new FilterSelectionQueryResult(skill.get("id").toString(), skill.get("name").toString()));
            }
        }
        return filterSkillData;
    }

    private List<FilterSelectionQueryResult> getPlannedTimeType(Long countryId){
        List<PresenceTypeDTO>  presenceTypeDTOS = activityIntegrationService.getAllPlannedTimeType(countryId);
        return presenceTypeDTOS.stream().map(presenceTypeDTO -> new FilterSelectionQueryResult(presenceTypeDTO.getId().toString(),presenceTypeDTO.getName())).collect(Collectors.toList());
    }


    private List<FilterSelectionQueryResult> getAnsenceActivity(Long unitId){
        List<ActivityDTO> activityDTOS = activityIntegrationService.getAllAbsenceActivity(unitId);
        return activityDTOS.stream().map(activityDTO -> new FilterSelectionQueryResult(activityDTO.getId().toString(),activityDTO.getName())).collect(Collectors.toList());
    }


    private List<FilterSelectionQueryResult> getTimeSlots(){
        List<FilterSelectionQueryResult> filterSelectionQueryResults = new ArrayList<>();
        filterSelectionQueryResults.add(new FilterSelectionQueryResult(AppConstants.DAY,AppConstants.DAY));
        filterSelectionQueryResults.add(new FilterSelectionQueryResult(AppConstants.EVENING,AppConstants.EVENING));
        filterSelectionQueryResults.add(new FilterSelectionQueryResult(AppConstants.NIGHT,AppConstants.NIGHT));
        return filterSelectionQueryResults;
    }

    private List<FilterSelectionQueryResult> getStatusFilter(){
        Set<ShiftStatus> shiftStatuses = newHashSet(UNPUBLISH,UNLOCK,UNFIX,DISAPPROVE,LOCK,REJECT);
        return Arrays.stream(ShiftStatus.values()).filter(shiftStatus -> !shiftStatuses.contains(shiftStatus)).map(shiftStatus -> new FilterSelectionQueryResult(shiftStatus.toString(),StringUtils.capitalize(shiftStatus.name().toLowerCase()))).collect(Collectors.toList());
    }

    private List<FilterSelectionQueryResult> getAllTimeType(Long countryId){
        List<TimeTypeDTO> timeTypeDTOS = activityIntegrationService.getAllTimeType(countryId);
        List<FilterSelectionQueryResult> filterSelectionQueryResults = new ArrayList<>();
        convertTimeTypeDTOSToFilterSelectionQueryResult(timeTypeDTOS.get(0).getChildren(), filterSelectionQueryResults);
        convertTimeTypeDTOSToFilterSelectionQueryResult(timeTypeDTOS.get(1).getChildren(), filterSelectionQueryResults);
        return filterSelectionQueryResults;
        //return timeTypeDTOS.stream().flatMap(timeTypeDTO -> timeTypeDTO.getChildren().stream()).map(timeTypeDTO -> new FilterSelectionQueryResult(timeTypeDTO.getSecondLevelType().toString(),timeTypeDTO.getLabel())).collect(Collectors.toList());
    }

    private void convertTimeTypeDTOSToFilterSelectionQueryResult(List<TimeTypeDTO> timeTypeDTOS, List<FilterSelectionQueryResult> filterSelectionQueryResults){
        for(TimeTypeDTO timeTypeDTO : timeTypeDTOS) {
            filterSelectionQueryResults.add(new FilterSelectionQueryResult(timeTypeDTO.getId().toString(),timeTypeDTO.getLabel()));
            if(isCollectionNotEmpty(timeTypeDTO.getChildren())){
                convertTimeTypeDTOSToFilterSelectionQueryResult(timeTypeDTO.getChildren(),filterSelectionQueryResults);
            }
        }
    }

    private FilterQueryResult getFilterDataByFilterType(FilterType filterType, Long countryId, Long unitId) {
        FilterQueryResult tempFilterDTO = new FilterQueryResult();
        tempFilterDTO.setName(filterType.name());
        tempFilterDTO.setTitle(filterType.value);
        tempFilterDTO.setFilterData(getFilterDetailsByFilterType(filterType, countryId, unitId));
        return tempFilterDTO;
    }

    private List<FilterQueryResult> getAllFilters(String moduleId, Long countryId, Long unitId) {
        FilterGroup filterGroup = filterGroupGraphRepository.getFilterGroupByModuleId(moduleId);
        List<FilterQueryResult> filterDTOs = new ArrayList<>();
        if (Optional.ofNullable(filterGroup).isPresent()) {
            filterGroup.getFilterTypes().forEach(filterType -> {
                FilterQueryResult tempFilterQueryResult = getFilterDataByFilterType(filterType, countryId, unitId);
                if (isCollectionNotEmpty(tempFilterQueryResult.getFilterData())) {
                    filterDTOs.add(tempFilterQueryResult);
                }
            });
        }
        return filterDTOs;
    }

    private List<FavoriteFilterQueryResult> getFavouriteFilters(String moduleId, Long staffId) {
        return staffGraphRepository.getStaffFavouriteFiltersByStaffAndView(staffId, moduleId);
    }

    public StaffFilterDTO addFavouriteFilter(Long unitId, StaffFilterDTO staffFilterDTO) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = accessPageRepository.isHubMember(userId) ? accessPageRepository.fetchParentHub(userId) : organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(userId, parent.getId());
        if (!Optional.ofNullable(staffFilterDTO.getName()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_NAME_EMPTY);
        }
        if (staffFilterDTO.getFiltersData().isEmpty()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_SELECT);
        }
        if (staffFavouriteFilterGraphRepository.checkIfFavouriteFilterExistsWithName(staffFilterDTO.getModuleId(), staffFilterDTO.getName())) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_NAME_ALREADYEXIST, staffFilterDTO.getName());
        }
        // Fetch filter group to which access page is linked
        FilterGroup filterGroup = filterGroupGraphRepository.getFilterGroupByModuleId(staffFilterDTO.getModuleId());
        StaffFavouriteFilter staffFavouriteFilter = new StaffFavouriteFilter(staffFilterDTO.getName(),
                ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffFilterDTO.getFiltersData(), FilterSelection.class), filterGroup);
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        staff.addFavouriteFilters(staffFavouriteFilter);
        staffGraphRepository.save(staff);
        staffFilterDTO.setId(staffFavouriteFilter.getId());
        return staffFilterDTO;
    }

    public StaffFilterDTO updateFavouriteFilter(Long filterId, Long organizationId, StaffFilterDTO favouriteFilterDTO) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = accessPageRepository.isHubMember(userId) ? accessPageRepository.fetchParentHub(userId) : organizationService.fetchParentOrganization(organizationId);
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersOfStaffInOrganizationById(
                userId, parent.getId(), filterId);
        if (!Optional.ofNullable(staffFavouriteFilter).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_FAVOURITEFILTERID_INVALID, filterId);
        }
        if (!Optional.ofNullable(favouriteFilterDTO.getName()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_NAME_EMPTY);
        }
        if (favouriteFilterDTO.getFiltersData().isEmpty()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_SELECT);
        }
        if (staffFavouriteFilterGraphRepository.checkIfFavouriteFilterExistsWithNameExceptId(favouriteFilterDTO.getModuleId(),
                favouriteFilterDTO.getName(), staffFavouriteFilter.getId())) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_NAME_ALREADYEXIST, favouriteFilterDTO.getName());
        }
        staffGraphRepository.detachStaffFavouriteFilterDetails(staffFavouriteFilter.getId());
        List<FilterSelectionDTO> filters = favouriteFilterDTO.getFiltersData();
        filters.forEach(filterSelection -> filterSelection.setId(null));
        staffFavouriteFilter.setFiltersData(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(filters, FilterSelection.class));
        staffFavouriteFilter.setName(favouriteFilterDTO.getName());
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        return favouriteFilterDTO;
    }

    public Boolean deleteFavouriteFilter(Long filterId, Long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = accessPageRepository.isHubMember(userId) ? accessPageRepository.fetchParentHub(userId) : organizationService.fetchParentOrganization(unitId);
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersOfStaffInOrganizationById(
                userId, parent.getId(), filterId);
        if (!Optional.ofNullable(staffFavouriteFilter).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_FAVOURITEFILTERID_INVALID, filterId);
        }
        staffFavouriteFilter.setDeleted(true);
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        return true;
    }

    private List<FilterSelectionQueryResult> getEmploymenTypeFiltersDataByCountry(Long countryId) {
        return employmentTypeGraphRepository.getEmploymentTypeByCountryIdForFilters(countryId);
    }

    public <T> Map<FilterType, Set<T>> getMapOfFiltersToBeAppliedWithValue(String moduleId, List<FilterSelectionDTO> filters) {
        Map<FilterType, Set<T>> mapOfFilters = new HashMap<>();
        // Fetch filter group to which access page is linked
        FilterGroup filterGroup = filterGroupGraphRepository.getFilterGroupByModuleId(moduleId);
        filters.forEach(filterSelection -> {
            if (!filterSelection.getValue().isEmpty() && filterGroup.getFilterTypes().contains(
                    filterSelection.getName())) {
                mapOfFilters.put(filterSelection.getName(), filterSelection.getValue());
            }
        });
        return mapOfFilters;
    }

    public <T> StaffEmploymentTypeWrapper getAllStaffByUnitId(Long unitId, StaffFilterDTO staffFilterDTO, String moduleId, LocalDate startDate, LocalDate endDate , boolean showAllStaffs,LocalDate selectedDate) {
        boolean unit=unitGraphRepository.existsById(unitId);
        Organization organization=organizationService.fetchParentOrganization(unitId);
        if (!Optional.ofNullable(staffFilterDTO.getModuleId()).isPresent() &&
                !filterGroupGraphRepository.checkIfFilterGroupExistsForModuleId(staffFilterDTO.getModuleId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_FILTER_SETTING_NOTFOUND);
        }
        Long loggedInStaffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
        StaffEmploymentTypeWrapper staffEmploymentTypeWrapper = new StaffEmploymentTypeWrapper();
        staffEmploymentTypeWrapper.setEmploymentTypes(employmentTypeGraphRepository.getAllEmploymentTypeByOrganization(organization.getId(), false));
        List<Long> allOrgIds=unit?Arrays.asList(organization.getId()):organizationGraphRepository.findAllOrganizationIdsInHierarchy(organization.getId());
        Map<FilterType, Set<T>> filterTypeSetMap = getMapOfFiltersToBeAppliedWithValue(staffFilterDTO.getModuleId(), staffFilterDTO.getFiltersData());
        List<Map> staffListMap=staffGraphRepository.getStaffWithFilters(unitId, allOrgIds, moduleId,
                filterTypeSetMap, staffFilterDTO.getSearchText(),
                envConfig.getServerHost() + AppConstants.FORWARD_SLASH + envConfig.getImagesPath(),null,selectedDate);

        staffListMap = filterStaffList(staffListMap, filterTypeSetMap);
        staffEmploymentTypeWrapper.setStaffList(staffListMap);
        staffEmploymentTypeWrapper.setLoggedInStaffId(loggedInStaffId);
        List<Map> staffs = filterStaffByRoles(staffEmploymentTypeWrapper.getStaffList(), unitId , moduleId , showAllStaffs);
        staffs = staffs.stream().filter(distinctByKey(a -> a.get(ID))).collect(Collectors.toList());
        staffEmploymentTypeWrapper.setStaffList(staffs);
        Map<Long,List<Long>> mapOfStaffAndEmploymentIds = getMapOfStaffAndEmploymentIds(staffs);
        staffFilterDTO.setMapOfStaffAndEmploymentIds(mapOfStaffAndEmploymentIds);
        staffFilterDTO.setIncludeWorkTimeAgreement(ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId));
        staffFilterDTO = activityIntegrationService.getNightWorkerDetails(staffFilterDTO, unitId, startDate, endDate);
        List<Map> staffList = new ArrayList<>();
        for (Map staffAndModifiable : staffs) {
            if(staffFilterDTO.getNightWorkerDetails().containsKey(staffAndModifiable.get(ID))) {
                Map<String, Object> staff = ObjectMapperUtils.copyPropertiesByMapper(staffAndModifiable, HashedMap.class);
                staff.put(NIGHT_WORKER, staffFilterDTO.getNightWorkerDetails().get(((Integer) ((Map) staff).get(ID)).longValue()));
                staffList.add(staff);
                if(staffFilterDTO.isIncludeWorkTimeAgreement()){
                    for (Map employment : ((Collection<Map>) staff.get(EMPLOYMENTS))) {
                        if (isNotNull(employment.get(ID))) {
                            Long employmentId = ((Integer) employment.get(ID)).longValue();
                            employment.put(WORK_TIME_AGREEMENTS, staffFilterDTO.getEmploymentIdAndWtaResponseMap().getOrDefault(employmentId, newArrayList()));
                        }
                    }
                }
            }
        }
        if(loggedInStaffId!=null && staffList.stream().noneMatch(k->k.containsKey(loggedInStaffId)) && ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)){
            List<Map> loggedInStaffDetails=staffGraphRepository.getStaffWithFilters(unitId, allOrgIds, moduleId,
                    new HashMap<>(), null,
                    envConfig.getServerHost() + AppConstants.FORWARD_SLASH + envConfig.getImagesPath(),loggedInStaffId,selectedDate);
            staffList.addAll(loggedInStaffDetails);
        }
        staffEmploymentTypeWrapper.setStaffList(staffList);
        return staffEmploymentTypeWrapper;
    }

    private <T> List<Map> filterStaffList(List<Map> staffListMap, Map<FilterType, Set<T>> filterData) {
        Set<FilterType> appliedFilters = newHashSet(AGE, ORGANIZATION_EXPERIENCE, BIRTHDAY, SENIORITY, EMPLOYED_SINCE, PAY_GRADE_LEVEL);
        for (Map.Entry<FilterType, Set<T>> filterTypeSetEntry : filterData.entrySet()) {
            if(isNotNull(filterTypeSetEntry.getKey()) && appliedFilters.contains(filterTypeSetEntry.getKey())) {
                staffListMap = getFilteredStaffs(staffListMap, filterTypeSetEntry.getKey(), filterData);
            }
        }
        return staffListMap;
    }

    private <T> List<Map> getFilteredStaffs(List<Map> staffListMap, FilterType filterType, Map<FilterType, Set<T>> filterData){
        Map ageRangeMap = (Map) filterData.get(filterType).iterator().next();
        AgeRangeDTO ageRange = new AgeRangeDTO(Integer.parseInt(ageRangeMap.get(FROM.toLowerCase()).toString()), isNotNull(ageRangeMap.get(TO.toLowerCase())) ? Integer.parseInt(ageRangeMap.get(TO.toLowerCase()).toString()) : null, isNull(ageRangeMap.get(DURATION_TYPE)) ? DurationType.DAYS : DurationType.valueOf(ageRangeMap.get(DURATION_TYPE).toString()));
        switch (filterType){
            case AGE:
                staffListMap =  staffListMap.stream().filter(map -> isNotNull(map.get(DATE_OF_BIRTH)) && validate(asLocalDate(map.get(DATE_OF_BIRTH).toString()), getCurrentLocalDate(), ageRange)).collect(Collectors.toList());
                break;
            case ORGANIZATION_EXPERIENCE:
                staffListMap =   staffListMap.stream().filter(map -> isNotNull(map.get(JOINING_BIRTH)) && validate(asLocalDate(map.get(JOINING_BIRTH).toString()), getCurrentLocalDate(), ageRange)).collect(Collectors.toList());
                break;
            case BIRTHDAY:
                ageRange.setTo(ageRange.getFrom());
                ageRange.setFrom(0);
                staffListMap =   staffListMap.stream().filter(map -> isNotNull(map.get(DATE_OF_BIRTH)) && validate(getCurrentLocalDate(), asLocalDate(getCurrentLocalDate().toString().substring(0,4) + map.get(DATE_OF_BIRTH).toString().substring(4)), ageRange)).collect(Collectors.toList());
                break;
            case SENIORITY:
                staffListMap =   staffListMap.stream().filter(map -> validateSeniority((List<Map>) map.get(EXPERTISE_LIST), ageRange)).collect(Collectors.toList());
                break;
            case EMPLOYED_SINCE:
                staffListMap =   staffListMap.stream().filter(map -> validateEmployment((List<Map>) map.get(EMPLOYMENTS), ageRange)).collect(Collectors.toList());
                break;
            case PAY_GRADE_LEVEL:
                staffListMap =   staffListMap.stream().filter(map -> validatePayGrade((List<Map>) map.get(EMPLOYMENTS), ageRange)).collect(Collectors.toList());
                break;

            default:
                break;
        }
        return staffListMap;
    }

    private boolean validateEmployment(List<Map> employments, AgeRangeDTO employmentRange) {
        for (Map employment : employments) {
            if(validate(asLocalDate(employment.get(START_DATE).toString()), getCurrentLocalDate(), employmentRange)){
                return true;
            }
        }
        return false;
    }

    private boolean validatePayGrade(List<Map> employments, AgeRangeDTO payGradeRange){
        long from = getDataInDays(payGradeRange.getFrom(), payGradeRange.getDurationType());
        long to = isNotNull(payGradeRange.getTo()) ? getDataInDays(payGradeRange.getTo(), payGradeRange.getDurationType()) : MAX_LONG_VALUE;
        for (Map employment : employments) {
            for (Map employmentLines : (List<Map>) employment.get(EMPLOYMENT_LINES)) {
                for (Map payGrades : (List<Map>) employmentLines.get(PAY_GRADES)) {
                    if(payGrades.containsKey(AppConstants.PAY_GRADE_LEVEL)) {
                        long payGradeLevel = Long.valueOf(payGrades.get(AppConstants.PAY_GRADE_LEVEL).toString());
                        if (from <= payGradeLevel && to >= payGradeLevel) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean validateSeniority(List<Map> expertiseList, AgeRangeDTO expertiseRange) {
        for (Map map : expertiseList) {
            if(map.containsKey(EXPERTISE_START_DATE_IN_MILLIS) && isNotNull(map.get(EXPERTISE_START_DATE_IN_MILLIS))) {
                Date expertiseStartDate = new Date(Long.valueOf(map.get(EXPERTISE_START_DATE_IN_MILLIS).toString()));
                if (validate(asLocalDate(expertiseStartDate), getCurrentLocalDate(), expertiseRange)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validate(LocalDate fromDate, LocalDate toDate, AgeRangeDTO dateRange){
        long inDays = ChronoUnit.DAYS.between(fromDate, toDate);
        long from = getDataInDays(dateRange.getFrom(), dateRange.getDurationType());
        long to = isNotNull(dateRange.getTo()) ? getDataInDays(dateRange.getTo(), dateRange.getDurationType()) : MAX_LONG_VALUE;
        return from <= inDays && to >= inDays;
    }

    private long getDataInDays(long value, DurationType durationType){
        switch (durationType){
            case YEAR :
                return Math.round(value *  DAYS_IN_ONE_YEAR);
            case MONTHS:
                return Math.round(value *  DAYS_IN_ONE_MONTH);
            default:
                return value;
        }
    }

    private Map<Long,List<Long>> getMapOfStaffAndEmploymentIds(List<Map> staffs){
        Map<Long,List<Long>> mapOfStaffAndEmploymentIds = new HashMap<>();
        for (Map staff : staffs) {
                mapOfStaffAndEmploymentIds.put((Long) staff.get(ID),((Collection<Map>)staff.get(EMPLOYMENTS)).stream().map(employment -> (Long)employment.get(ID)).collect(Collectors.toList()));
        }
        return mapOfStaffAndEmploymentIds;
    }

    private List<Map> filterStaffByRoles(List<Map> staffList, Long unitId ,String moduleId , boolean showAllStaffs) {
        Long userId = UserContext.getUserDetails().getId();
        List<Map> staffListByRole = new ArrayList<>();
        Organization organization=organizationService.fetchParentOrganization(unitId);
        Staff staffAtHub = staffGraphRepository.getStaffByOrganizationHub(organization.getId(), userId);
        if (staffAtHub != null) {
            staffListByRole = staffList;
        } else {
            AccessGroupStaffQueryResult accessGroupQueryResult = accessGroupRepository.getAccessGroupDayTypesAndUserId(unitId, userId);
            String STAFF_CURRENT_ROLE;
            if (accessGroupQueryResult != null) {
                STAFF_CURRENT_ROLE = staffRetrievalService.getStaffAccessRole(accessGroupQueryResult);
              if ((!showAllStaffs || !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) && AccessGroupRole.STAFF.name().equals(STAFF_CURRENT_ROLE)) {
                    Map staff = staffList.stream().filter(s -> s.get(ID).equals(accessGroupQueryResult.getStaffId())).findFirst().orElse(new HashMap());
                    if (isNotEmpty(staff)) {
                        staffListByRole.add(staff);
                    }
              } else {
                  staffListByRole = staffList;
              }
            }
        }
        return staffListByRole;
    }

    public StaffFilterDTO addStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long unitId) {
        StaffFavouriteFilter staffFavouriteFilter = new StaffFavouriteFilter();
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(userId, parent.getId());
        AccessPage accessPage = accessPageService.findByModuleId(staffFilterDTO.getModuleId());
        staffFavouriteFilter.setName(staffFilterDTO.getName());
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        staff.addFavouriteFilters(staffFavouriteFilter);
        staffGraphRepository.save(staff);
        staffFilterDTO.setModuleId(accessPage.getModuleId());
        staffFilterDTO.setName(staffFavouriteFilter.getName());
        staffFilterDTO.setId(staffFavouriteFilter.getId());
        return staffFilterDTO;
    }
}
