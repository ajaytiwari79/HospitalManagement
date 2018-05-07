package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.enums.FilterEntityType;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.enums.StaffStatusEnum;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.filter.*;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.user.staff.StaffFilterDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffFavouriteFilterGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.user_filter.FilterGroupGraphRepository;
import com.kairos.response.dto.web.filter.FilterDetailDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by prerna on 1/5/18.
 */
@Transactional
@Service
public class StaffFilterService extends UserBaseService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private FilterGroupGraphRepository filterGroupGraphRepository;
    @Inject
    EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    OrganizationService organizationService;
    @Inject
    EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    StaffFavouriteFilterGraphRepository staffFavouriteFilterGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    ExpertiseGraphRepository expertiseGraphRepository;


    public FiltersAndFavouriteFiltersDTO getAllAndFavouriteFilters(String moduleId, Long organizationId){
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);

        FiltersAndFavouriteFiltersDTO filtersAndFavouriteFiltersDTO = new FiltersAndFavouriteFiltersDTO(
                getAllFilters(moduleId, organizationService.getCountryIdOfOrganization(organizationId), organizationId),
                getFavouriteFilters(moduleId, staff.getId()));
        return filtersAndFavouriteFiltersDTO;
    }

    public List<FilterDetailQueryResult> dtoToQueryesultConverter(List<FilterDetailDTO> filterData, ObjectMapper objectMapper){
        List<FilterDetailQueryResult> queryResults = new ArrayList<>();

        filterData.forEach(filterDetailDTO -> {
            queryResults.add(objectMapper.convertValue(filterDetailDTO, FilterDetailQueryResult.class));
        });
        return queryResults;
    }

    public List<FilterDetailQueryResult> getFilterDetailsByFilterEntity(FilterEntityType filterEntityType, Long countryId, Long unitId){
        ObjectMapper objectMapper =  new ObjectMapper();
        switch (filterEntityType){
            case EMPLOYMENT_TYPE: {
                return getEmploymenTypeFiltersDataByCountry(countryId);
            }
            case GENDER: {
                return dtoToQueryesultConverter(Gender.getListOfGenderForFilters(), objectMapper);
            }
            case STAFF_STATUS: {
                return dtoToQueryesultConverter(StaffStatusEnum.getListOfStaffStatusForFilters(), objectMapper);
            }
            case ENGINEER_TYPE: {
                return engineerTypeGraphRepository.getEngineerTypeByCountryIdForFilters(countryId);
            }
            case EXPERTISE: {
                return expertiseGraphRepository.getExpertiseByCountryIdForFilters(unitId, countryId);
            }
            default: throw new InvalidRequestException(filterEntityType.value+" Entity not found");
        }
    }

    public FilterQueryResult getFilterDataByFilterEntity(FilterEntityType filterEntityType, Long countryId, Long unitId){
        FilterQueryResult tempFilterDTO = new FilterQueryResult();
        tempFilterDTO.setName(filterEntityType.name());
        tempFilterDTO.setTitle(filterEntityType.value);
        tempFilterDTO.setFilterData(getFilterDetailsByFilterEntity(filterEntityType, countryId, unitId));
        return tempFilterDTO;
    }

    public List<FilterQueryResult> getAllFilters(String moduleId, Long countryId, Long unitId) {
        FilterGroup filterGroup =  filterGroupGraphRepository.getFilterGroupByModuleId(moduleId);
        if(!Optional.ofNullable(filterGroup).isPresent()){
            throw new InvalidRequestException("Filter feature is not enabled for the module");
        }
        List<FilterQueryResult> filterDTOs = new ArrayList<>();
        // TODO refactor to fetch list by stream
        filterGroup.getFilterTypes().forEach(filterEntityType -> {
            filterDTOs.add(getFilterDataByFilterEntity(filterEntityType, countryId, unitId));
        });
        return filterDTOs;
    }

    public List<FavoriteFilterQueryResult> getFavouriteFilters(String moduleId, Long staffId) {
        return staffGraphRepository.getStaffFavouriteFiltersByStaffAndView(staffId, moduleId);
    }

    public StaffFilterDTO addFavouriteFilter(Long organizationId, StaffFilterDTO staffFilterDTO){
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);


        if(!Optional.ofNullable(staffFilterDTO.getName()).isPresent()){
            throw new InvalidRequestException("Name can not be empty");
        }
        if(staffFavouriteFilterGraphRepository.checkIfFavouriteFilterExistsWithName(staffFilterDTO.getModuleId(), staffFilterDTO.getName())){
            throw new InvalidRequestException("Filter already exists with name : "+staffFilterDTO.getName());
        }
        // Fetch filter group to which access page is linked
        FilterGroup filterGroup =  filterGroupGraphRepository.getFilterGroupByModuleId(staffFilterDTO.getModuleId());

        StaffFavouriteFilter staffFavouriteFilter = new StaffFavouriteFilter(staffFilterDTO.getName(),
                staffFilterDTO.getFiltersData(), filterGroup);
        save(staffFavouriteFilter);
        staff.addFavouriteFilters(staffFavouriteFilter);
        save(staff);
        staffFilterDTO.setId(staffFavouriteFilter.getId());
        return staffFilterDTO;
    }

    public StaffFilterDTO updateFavouriteFilter(Long filterId, Long organizationId, StaffFilterDTO favouriteFilterDTO){
        Long userId = UserContext.getUserDetails().getId();
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersOfStaffInOrganizationById(
                userId, organizationId, filterId);
        if(!Optional.ofNullable(staffFavouriteFilter).isPresent()){
            throw new InvalidRequestException("Invalid id of favourite filter : "+filterId);
        }
        if(!Optional.ofNullable(favouriteFilterDTO.getName()).isPresent()){
            throw new InvalidRequestException("Name can not be empty");
        }
        if(staffFavouriteFilterGraphRepository.checkIfFavouriteFilterExistsWithNameExceptId(favouriteFilterDTO.getModuleId(),
                favouriteFilterDTO.getName(), staffFavouriteFilter.getId())){
            throw new InvalidRequestException("Filter already exists with name : "+favouriteFilterDTO.getName());
        }
        staffGraphRepository.detachStaffFavouriteFilterDetails(staffFavouriteFilter.getId());
        List<FilterDetail> filters =  favouriteFilterDTO.getFiltersData();
        filters.forEach(filterDetail -> {filterDetail.setId(null);});
        staffFavouriteFilter.setFiltersData(filters);
        staffFavouriteFilter.setName(favouriteFilterDTO.getName());
        save(staffFavouriteFilter);
        return favouriteFilterDTO;
    }

    public Boolean deleteFavouriteFilter(Long filterId, Long organizationId){
        Long userId = UserContext.getUserDetails().getId();
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersOfStaffInOrganizationById(
                userId, organizationId, filterId);
        if(!Optional.ofNullable(staffFavouriteFilter).isPresent()){
            throw new InvalidRequestException("Invalid id of favourite filter : "+filterId);
        }
        staffFavouriteFilter.setDeleted(true);
        save(staffFavouriteFilter);
        return true;
    }

    public List<FilterDetailQueryResult> getEmploymenTypeFiltersDataByCountry(Long countryId){
        return employmentTypeGraphRepository.getEmploymentTypeByCountryIdForFilters(countryId);
    }

    public Map<FilterEntityType, List<String>> getMapOfFiltersToBeAppliedWithValue(String moduleId, List<FilterDetail> filters){
        Map<FilterEntityType,List<String>> mapOfFilters = new HashMap<>();
        // Fetch filter group to which access page is linked
        FilterGroup filterGroup =  filterGroupGraphRepository.getFilterGroupByModuleId(moduleId);
        filters.forEach(filterDetail -> {
            if(!filterDetail.getValue().isEmpty() && filterGroup.getFilterTypes().contains(
                    FilterEntityType.valueOf(filterDetail.getName()) ) ){
                mapOfFilters.put(FilterEntityType.valueOf(filterDetail.getName()) , filterDetail.getValue() );

            }
        });
        return mapOfFilters;
    }

    public List<Map> getAllStaffByUnitId(Long unitId, Boolean allStaffRequired, StaffFilterDTO staffFilterDTO) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            throw new DataNotFoundByIdException("unit  not found  Unit ID: " + unitId);
        }
        if (!Optional.ofNullable(staffFilterDTO.getModuleId()).isPresent() &&
                !filterGroupGraphRepository.checkIfFilterGroupExistsForModuleId(staffFilterDTO.getModuleId())) {
            throw new DataNotFoundByIdException("Invalid module Id or Filter settings are not set");
        }

        Organization organization = organizationService.fetchParentOrganization(unitId);
        return organizationGraphRepository.getStaffWithFilters(unitId, organization.getId(), !allStaffRequired,
                getMapOfFiltersToBeAppliedWithValue(staffFilterDTO.getModuleId(), staffFilterDTO.getFiltersData()), staffFilterDTO.getSearchText(),
                envConfig.getServerHost() + AppConstants.FORWARD_SLASH + envConfig.getImagesPath());

    }


}
