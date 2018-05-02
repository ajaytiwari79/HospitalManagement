package com.kairos.service.user_filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.enums.FilterEntityType;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.enums.StaffStatusEnum;
import com.kairos.persistence.model.user.filter.*;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.user.staff.StaffFilterDTO;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffFavouriteFilterGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.user_filter.FilterGroupGraphRepository;
//import com.kairos.persistence.model.user.filter.FiltersAndFavouriteFiltersDTO;
import com.kairos.response.dto.web.filter.FilterDetailDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by prerna on 1/5/18.
 */
@Transactional
@Service
public class UserFilterService extends UserBaseService{
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

    // TODO Get list of access page for which filters are being shared
    /*public List<AccessPage> getListOfAccessPageForFiltersByModuleId(String moduleId){
        return null;
    }
    public StaffFilterDTO addStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long organizationId) {
        StaffFavouriteFilter alreadyExistFilter = staffGraphRepository.getStaffFavouriteFiltersByStaffAndView(staffId, staffFilterDTO.getModuleId());
        if(Optional.ofNullable(alreadyExistFilter).isPresent()){
            throw new DuplicateDataException("StaffFavouriteFilter already exist !");
        }
        StaffFavouriteFilter staffFavouriteFilters = new StaffFavouriteFilter();
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        AccessPage accessPage = accessPageService.findByModuleId(staffFilterDTO.getModuleId());
        // TODO Set List of Access Pages
        staffFavouriteFilters.setAccessPage(accessPage);
        staffFavouriteFilters.setFilterJson(staffFilterDTO.getFilterJson());
        staffFavouriteFilters.setName(staffFilterDTO.getName());
        save(staffFavouriteFilters);
        staff.addFavouriteFilters(staffFavouriteFilters);
        save(staff);
        staffFilterDTO.setFilterJson(staffFavouriteFilters.getFilterJson());
        staffFilterDTO.setModuleId(accessPage.getModuleId());
        staffFilterDTO.setName(staffFavouriteFilters.getName());
        staffFilterDTO.setId(staffFavouriteFilters.getId());
        return staffFilterDTO;
    }
    public StaffFilterDTO updateStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        StaffFavouriteFilter staffFavouriteFilters = staffGraphRepository.getStaffFavouriteFiltersById(staff.getId(), staffFilterDTO.getId());
        if (!Optional.ofNullable(staffFavouriteFilters).isPresent()) {
            throw new DataNotFoundByIdException("StaffFavouriteFilter  not found  with ID: " + staffFilterDTO.getId());
        }
        AccessPage accessPage = accessPageService.findByModuleId(staffFilterDTO.getModuleId());
        staffFavouriteFilters.setAccessPage(accessPage);
        staffFavouriteFilters.setFilterJson(staffFilterDTO.getFilterJson());
        staffFavouriteFilters.setName(staffFilterDTO.getName());
        staffFavouriteFilters.setEnabled(true);
        save(staffFavouriteFilters);
        staffFilterDTO.setFilterJson(staffFavouriteFilters.getFilterJson());
        // staffFilterDTO.setModuleId(accessPage.getModuleId());
        return staffFilterDTO;
    }
    public boolean removeStaffFavouriteFilters(Long staffFavouriteFilterId, long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        StaffFavouriteFilter staffFavouriteFilters = staffGraphRepository.getStaffFavouriteFiltersById(staff.getId(), staffFavouriteFilterId);
        if (!Optional.ofNullable(staffFavouriteFilters).isPresent()) {
            throw new DataNotFoundByIdException("StaffFavouriteFilter  not found  with ID: " + staffFavouriteFilterId);
        }
        staffFavouriteFilters.setEnabled(false);
        save(staffFavouriteFilters);
        return true;
    }
    public List<StaffFavouriteFilter> getStaffFavouriteFilters(String moduleId, long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        return staffGraphRepository.getStaffFavouriteFiltersByStaffAndView(staff.getId(), moduleId);
    }*/
    // Filter Methods
    public FiltersAndFavouriteFiltersDTO getAllAndFavouriteFilters(String moduleId, Long organizationId){
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);

        FiltersAndFavouriteFiltersDTO filtersAndFavouriteFiltersDTO = new FiltersAndFavouriteFiltersDTO(
                getAllFilters(moduleId, organizationService.getCountryIdOfOrganization(organizationId), organizationId),
                getFavouriteFilters(moduleId, staff.getId()));
         return filtersAndFavouriteFiltersDTO;
    }

    /*public List<FilterDetailQueryResult> dtoToQueryesultConverter(List<Class<T> > filterData, ObjectMapper objectMapper,){
        List<FilterDetailQueryResult> queryResults = new ArrayList<>();

        filterData.forEach(filterDetailDTO -> {
            queryResults.add(objectMapper.convertValue(filterDetailDTO, FilterDetailQueryResult.class));
        });
        return queryResults;
    }*/

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
            /*case EXPERTISE: {
                // TODO Fetch Expertise for filters data
                return new ArrayList<>();
            }*/
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


}
