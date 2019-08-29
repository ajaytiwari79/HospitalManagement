package com.kairos.service.organization;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.filter.FilterAndFavouriteFilterDTO;
import com.kairos.dto.gdpr.filter.FilterAttributes;
import com.kairos.dto.gdpr.filter.FilterResponseDTO;
import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static java.util.stream.Collectors.toList;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class OrganizationHierarchyService {

    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private TreeStructureService treeStructureService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private OrganizationService organizationService;

    public QueryResult generateHierarchy() {
        List<OrganizationWrapper> organizationWrappers = userGraphRepository.getOrganizations(UserContext.getUserDetails().getId());

        OrganizationBaseEntity hierarchy = organizationGraphRepository.generateHierarchy(organizationWrappers.stream().map(organizationWrapper -> organizationWrapper.getId()).collect(toList())).get(0);
        return setUnitPermission(hierarchy, accessPageService.isHubMember(UserContext.getUserDetails().getId()));
    }


    /**
     * @param parentOrganizationId
     * @param organizationHierarchyFilterDTO
     * @return
     */
    public OrganizationBaseEntity generateOrganizationHierarchyByFilter(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO) {
        OrganizationBaseEntity hierarchy = unitGraphRepository.getOrganizationHierarchyByFilters(parentOrganizationId, organizationHierarchyFilterDTO);
        return null;
//        if(hierarchy instanceof Organization){
//
//        }
//        if (units.isEmpty()) {
//            Unit unit = unitGraphRepository.findOne(parentOrganizationId);
//            if (unit == null) {
//                return null;
//            }
//            QueryResult queryResult = new QueryResult();
//            queryResult.setId(unit.getId());
//            queryResult.setUnion(unit.isUnion());
//            queryResult.setName(unit.getName());
//            queryResult.setKairosHub(unit.isKairosHub());
//            queryResult.setAccessable(true);
//            queryResult.setType(ORGANIZATION_LABEL);
//            queryResult.setPreKairos(unit.isPrekairos());
//            queryResult.setEnabled(unit.isEnable());
//            queryResult.setParentOrganization(unit.isParentOrganization());
//            queryResult.setTimeZone(unit.getTimeZone() != null ? unit.getTimeZone().getId() : null);
//            queryResult.setOrganizationLevel(unit.getOrganizationLevel());
//            return queryResult;
//        }
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        List<QueryResult> list = new ArrayList<>();
//
//        List<Long> ids = new ArrayList<>();
//        for (Map<String, Object> unit : units) {
//            Map<String, Object> parentUnit = (Map<String, Object>) unit.get("parent");
//            long id = (long) parentUnit.get("id");
//            if (ids.contains(id)) {
//                for (QueryResult queryResult : list) {
//                    if (queryResult.getId() == id) {
//                        List<QueryResult> childs = queryResult.getChildren();
//                        QueryResult child = objectMapper.convertValue((unit.get("child")), QueryResult.class);
//                        child.setAccessable(true);
//                        childs.add(child);
//                        break;
//                    }
//                }
//            } else {
//                List<QueryResult> queryResults = new ArrayList<>();
//                QueryResult child = objectMapper.convertValue((unit.get("child")), QueryResult.class);
//                child.setAccessable(true);
//                queryResults.add(child);
//                QueryResult queryResult = objectMapper.convertValue(parentUnit, QueryResult.class);
//                queryResult.setChildren(queryResults);
//                queryResult.setAccessable(true);
//                list.add(queryResult);
//            }
//            ids.add(id);
//        }
//        return treeStructureService.getTreeStructure(list);
    }


    /**
     * @param unitId
     * @return
     */
    public FilterAndFavouriteFilterDTO getOrganizationHierarchyFilters(long unitId) {
        Organization parent = organizationService.fetchParentOrganization(unitId);
        FilterAndFavouriteFilterDTO filterAndFavouriteFilter = new FilterAndFavouriteFilterDTO();
        Map<String, Object> filterTypeDataMap = unitGraphRepository.getFiltersByParentOrganizationId(parent.getId());
        List<FilterResponseDTO> filterResponseDTOList = new ArrayList<>();
        for (String filterType : filterTypeDataMap.keySet()) {
            FilterResponseDTO filterResponseDTO = new FilterResponseDTO();
            switch (filterType) {
                case ORGANIZATION_TYPES:
                    filterResponseDTO.setDisplayName(FilterType.ORGANIZATION_TYPES.value);
                    filterResponseDTO.setName(FilterType.ORGANIZATION_TYPES);
                    break;
                case ORGANIZATION_SUB_TYPES:
                    filterResponseDTO.setDisplayName(FilterType.ORGANIZATION_SUB_TYPES.value);
                    filterResponseDTO.setName(FilterType.ORGANIZATION_SUB_TYPES);
                    break;
                case ORGANIZATION_SERVICES:
                    filterResponseDTO.setDisplayName(FilterType.ORGANIZATION_SERVICES.value);
                    filterResponseDTO.setName(FilterType.ORGANIZATION_SERVICES);
                    break;
                case ORGANIZATION_SUB_SERVICES:
                    filterResponseDTO.setDisplayName(FilterType.ORGANIZATION_SUB_SERVICES.value);
                    filterResponseDTO.setName(FilterType.ORGANIZATION_SUB_SERVICES);
                    break;
                case ACCOUNT_TYPES:
                    filterResponseDTO.setDisplayName(FilterType.ACCOUNT_TYPES.value);
                    filterResponseDTO.setName(FilterType.ACCOUNT_TYPES);
                    break;
                default:
            }
            List<FilterAttributes> filterAttributes = ObjectMapperUtils.copyPropertiesOfListByMapper((List<Map>) filterTypeDataMap.get(filterType), FilterAttributes.class);
            filterResponseDTO.setFilterData(filterAttributes);
            filterResponseDTOList.add(filterResponseDTO);
        }
        filterAndFavouriteFilter.setAllFilters(filterResponseDTOList);
        return filterAndFavouriteFilter;
    }

    private QueryResult setUnitPermission(OrganizationBaseEntity organizationHierarchy, boolean countryAdmin) {
        Set<Long> organizationIds = new HashSet<>();
        List<StaffAccessGroupQueryResult> staffAccessGroupQueryResults = accessPageService.getAccessPermission(UserContext.getUserDetails().getId(), getAllUnitIds(organizationHierarchy, organizationIds));
        Map<Long, Boolean> unitPermissionMap = staffAccessGroupQueryResults.stream().collect(Collectors.toMap(StaffAccessGroupQueryResult::getUnitId, StaffAccessGroupQueryResult::isHasPermission));
        QueryResult data = ObjectMapperUtils.copyPropertiesByMapper(organizationHierarchy, QueryResult.class);
        setPermission(data, unitPermissionMap, countryAdmin);
//        List<QueryResult> units = null;
//        if (organizationHierarchy instanceof Organization){
//            units = addUnits(unitPermissionMap, countryAdmin, ((Organization) organizationHierarchy));
//        }
//
//        QueryResult hierarchy= new QueryResult(organizationHierarchy.getName(), organizationHierarchy.getId(), new ArrayList<>(), units, true, (organizationHierarchy instanceof Organization) ? ((Organization) organizationHierarchy).isKairosHub() : false, organizationHierarchy.isEnable(), false, organizationHierarchy.getTimeZone().toString(), false, 18743l, countryAdmin ? true : unitPermissionMap.get(organizationHierarchy.getId()));
//        setPermissionInChildren(organizationHierarchy, unitPermissionMap, countryAdmin, hierarchy);
        return data;
    }

//    private void setPermissionInChildren(OrganizationBaseEntity organizationHierarchy, Map<Long, Boolean> unitPermissionMap, boolean countryAdmin, QueryResult resultQueryResults) {
//        if (organizationHierarchy instanceof Organization) {
//            ((Organization) organizationHierarchy).getChildren().forEach(organization -> {
//                List<QueryResult> children = ObjectMapperUtils.copyPropertiesOfListByMapper(organization.getChildren(), QueryResult.class);
//                List<QueryResult> units = addUnits(unitPermissionMap, countryAdmin, organization);
//                resultQueryResults.getChildren().add(new QueryResult(organization.getName(), organization.getId(), children, units, true,
//                        organization.isKairosHub(), organization.isEnable(), organization.isParentOrganization(), organization.getTimeZone().toString(), organization.isUnion(), 18743l, countryAdmin ? true : unitPermissionMap.get(organization.getId())));
//                setPermissionInChildren(organization, unitPermissionMap, countryAdmin, resultQueryResults);
//            });
//        }
//    }

//    private List<QueryResult> addUnits(Map<Long, Boolean> unitPermissionMap, boolean countryAdmin, Organization organization) {
//        List<QueryResult> units = ObjectMapperUtils.copyPropertiesOfListByMapper(organization.getUnits(), QueryResult.class);
//        units.forEach(u -> {
//            u.setAccessable(true);
//            u.setKairosHub(false);
//            u.setParentOrganization(false);
//            u.setUnion(false);
//            u.setHubId(18743l);
//            u.setHasPermission(countryAdmin ? true : unitPermissionMap.get(u.getId()));
//            u.setEnabled(countryAdmin ? true : unitPermissionMap.get(u.getId()));
//
//        });
//        return units;
//    }

    private Set<Long> getAllUnitIds(OrganizationBaseEntity organizationHierarchy, Set<Long> organizationIds) {
        if (organizationHierarchy instanceof Organization) {
            ((Organization) organizationHierarchy).getChildren().forEach(organization -> {
                organizationIds.add(organization.getId());
                getAllUnitIds(organization, organizationIds);
            });

            ((Organization) organizationHierarchy).getUnits().forEach(unit -> {
                organizationIds.add(unit.getId());
                getAllUnitIds(unit, organizationIds);
            });
        }
        organizationIds.add(organizationHierarchy.getId());
        return organizationIds;
    }

    private void setPermission(QueryResult queryResult, Map<Long, Boolean> unitPermissionMap, boolean countryAdmin) {
        queryResult.setAccessable(true);
        int counter = 0;
        queryResult.setKairosHub((countryAdmin && ++counter == 1) ? true : false);
        queryResult.setUnion(queryResult.isUnion());
        queryResult.setHubId(18743l);
        queryResult.setHasPermission(countryAdmin ? true : unitPermissionMap.get(queryResult.getId()));
        queryResult.setEnabled(countryAdmin ? true : unitPermissionMap.get(queryResult.getId()));
        queryResult.getChildren().forEach(org -> {
            setPermission(org, unitPermissionMap, countryAdmin);
            queryResult.getUnits().forEach(unit -> {
                setPermission(unit, unitPermissionMap, countryAdmin);
            });
        });

    }

}
