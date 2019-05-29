package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.filter.FilterDetailDTO;
import com.kairos.enums.Employment;
import com.kairos.enums.FilterType;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.staff.StaffFilterDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.filter.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffFavouriteFilterGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.user_filter.FilterGroupGraphRepository;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.staff.StaffEmploymentTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.distinctByKey;
import static com.kairos.commons.utils.ObjectUtils.isNotEmpty;
import static com.kairos.constants.UserMessagesConstants.*;

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
    private
    EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private
    OrganizationService organizationService;
    @Inject
    private
    EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private
    StaffFavouriteFilterGraphRepository staffFavouriteFilterGraphRepository;
    @Inject
    private
    UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private
    ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private AccessPageRepository accessPageRepository;

    public FiltersAndFavouriteFiltersDTO getAllAndFavouriteFilters(String moduleId, Long unitId) {

        return getAllAndFavouriteFiltersFromParent(moduleId, organizationService.fetchParentOrganization(unitId).getId());

    }

    public FiltersAndFavouriteFiltersDTO getAllAndFavouriteFiltersFromParent(String moduleId, Long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization organization;
        if (accessPageRepository.isHubMember(userId)) {
            organization = accessPageRepository.fetchParentHub(userId);
        } else {
            organization = organizationGraphRepository.findOne(organizationId);
            if (!Optional.ofNullable(organization).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, organizationId);
            }
        }
        Long countryId = UserContext.getUserDetails().getCountryId();
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTEXIST);
        }
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organization.getId());

        return new FiltersAndFavouriteFiltersDTO(
                getAllFilters(moduleId, countryId, organizationId),
                getFavouriteFilters(moduleId, staff.getId()));
    }

    private List<FilterSelectionQueryResult> dtoToQueryesultConverter(List<FilterDetailDTO> filterData, ObjectMapper objectMapper) {
        List<FilterSelectionQueryResult> queryResults = new ArrayList<>();

        filterData.forEach(filterDetailDTO -> queryResults.add(objectMapper.convertValue(filterDetailDTO, FilterSelectionQueryResult.class)));
        return queryResults;
    }

    private List<FilterSelectionQueryResult> getFilterDetailsByFilterType(FilterType filterType, Long countryId, Long unitId) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (filterType) {
            case EMPLOYMENT_TYPE: {
                return getEmploymenTypeFiltersDataByCountry(countryId);
            }
            case GENDER: {
                return dtoToQueryesultConverter(Gender.getListOfGenderForFilters(), objectMapper);
            }
            case STAFF_STATUS: {
                return dtoToQueryesultConverter(StaffStatusEnum.getListOfStaffStatusForFilters(), objectMapper);
            }
            case EXPERTISE: {
                return expertiseGraphRepository.getExpertiseByCountryIdForFilters(unitId, countryId);
            }
            case EMPLOYMENT: {
                return dtoToQueryesultConverter(Employment.getListOfEmploymentForFilters(), objectMapper);
            }

            default:
                exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_ENTITY_NOTFOUND, filterType.value);

        }
        return null;
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
        if (!Optional.ofNullable(filterGroup).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_FILTER_FEATURE_NOTENABLED);

        }
        List<FilterQueryResult> filterDTOs = new ArrayList<>();

        filterGroup.getFilterTypes().forEach(filterType -> {
            FilterQueryResult tempFilterQueryResult = getFilterDataByFilterType(filterType, countryId, unitId);
            if (tempFilterQueryResult.getFilterData().size() > 0) {
                filterDTOs.add(getFilterDataByFilterType(filterType, countryId, unitId));
            }
        });
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
                staffFilterDTO.getFiltersData(), filterGroup);
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
        List<FilterSelection> filters = favouriteFilterDTO.getFiltersData();
        filters.forEach(filterSelection -> filterSelection.setId(null));
        staffFavouriteFilter.setFiltersData(filters);
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

    private Map<FilterType, List<String>> getMapOfFiltersToBeAppliedWithValue(String moduleId, List<FilterSelection> filters) {
        Map<FilterType, List<String>> mapOfFilters = new HashMap<>();
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

    public StaffEmploymentTypeWrapper getAllStaffByUnitId(Long unitId, StaffFilterDTO staffFilterDTO, String moduleId) {
        Organization organization=UserContext.getUserDetails().getConfLevel().equals(ConfLevel.ORGANIZATION)?organizationGraphRepository.findByIdAndDeletedFalse(unitId):unitGraphRepository.getParentOfOrganization(unitId);

        if (!Optional.ofNullable(staffFilterDTO.getModuleId()).isPresent() &&
                !filterGroupGraphRepository.checkIfFilterGroupExistsForModuleId(staffFilterDTO.getModuleId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_FILTER_SETTING_NOTFOUND);

        }
        Long loggedInStaffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
        StaffEmploymentTypeWrapper staffEmploymentTypeWrapper = new StaffEmploymentTypeWrapper();
        staffEmploymentTypeWrapper.setEmploymentTypes(employmentTypeGraphRepository.getAllEmploymentTypeByOrganization(organization.getId(), false));
        staffEmploymentTypeWrapper.setStaffList(unitGraphRepository.getStaffWithFilters(unitId, organization.getId(), moduleId,
                getMapOfFiltersToBeAppliedWithValue(staffFilterDTO.getModuleId(), staffFilterDTO.getFiltersData()), staffFilterDTO.getSearchText(),
                envConfig.getServerHost() + AppConstants.FORWARD_SLASH + envConfig.getImagesPath()));
        staffEmploymentTypeWrapper.setLoggedInStaffId(loggedInStaffId);
        List<Map> staffs = filterStaffByRoles(staffEmploymentTypeWrapper.getStaffList(), unitId);
        staffs = staffs.stream().filter(distinctByKey(a -> a.get("id"))).collect(Collectors.toList());
        staffEmploymentTypeWrapper.setStaffList(staffs);
        return staffEmploymentTypeWrapper;

    }

    private List<Map> filterStaffByRoles(List<Map> staffList, Long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        List<Map> staffListByRole = new ArrayList<>();
        Staff staffAtHub = staffGraphRepository.getStaffByOrganizationHub(unitId, userId);
        if (staffAtHub != null) {
            staffListByRole = staffList;
        } else {
            AccessGroupStaffQueryResult accessGroupQueryResult = accessGroupRepository.getAccessGroupDayTypesAndUserId(unitId, userId);
            String STAFF_CURRENT_ROLE;
            if (accessGroupQueryResult != null) {
                STAFF_CURRENT_ROLE = staffRetrievalService.getStaffAccessRole(accessGroupQueryResult);
                if (AccessGroupRole.MANAGEMENT.name().equals(STAFF_CURRENT_ROLE)) {
                    staffListByRole = staffList;
                } else if (AccessGroupRole.STAFF.name().equals(STAFF_CURRENT_ROLE)) {
                    Map staff = staffList.stream().filter(s -> s.get("id").equals(accessGroupQueryResult.getStaffId())).findFirst().orElse(new HashMap());
                    if (isNotEmpty(staff)) {
                        staffListByRole.add(staff);
                    }
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
