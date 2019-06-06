package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.filter.FilterAndFavouriteFilterDTO;
import com.kairos.dto.gdpr.filter.FilterAttributes;
import com.kairos.dto.gdpr.filter.FilterResponseDTO;
import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.common.OrganizationBaseEntityQueryResult;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
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

    public List<QueryResult> generateHierarchy() {
        List<QueryResult> resultQueryResults = new ArrayList<>();
        List<OrganizationWrapper> organizationWrappers = userGraphRepository.getOrganizations(UserContext.getUserDetails().getId());

       OrganizationBaseEntity hierarchy= organizationGraphRepository.generateHierarchy(organizationWrappers.stream().map(organizationWrapper -> organizationWrapper.getId()).collect(toList())).get(0);
       setUnitPermission(hierarchy, accessPageService.isHubMember(UserContext.getUserDetails().getId()),resultQueryResults);

//        return unitGraphRepository.getOrganizationHierarchyByFilters(, organizationHierarchyFilterDTO);
//        List<Map<String, Object>> units = unitGraphRepository.getOrganizationHierarchy(organizationWrappers.stream().map(organizationWrapper -> organizationWrapper.getId()).collect(toList()));
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        List<QueryResult> list = new ArrayList<>();
//
//        List<Long> organizationIds = new ArrayList<>();
//        Long parentOrgId = null;
//        for (Map<String, Object> unit : units) {
//            Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unit.get("data")).get("parent");
//            parentOrgId = (long) parentUnit.get("id");
//            if (organizationIds.contains(parentOrgId)) {
//                for (QueryResult queryResult : list) {
//                    if (queryResult.getId() == parentOrgId) {
//                        List<QueryResult> childs = queryResult.getChildren();
//                        QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
//                        child.setAccessable(true);
//                        childs.add(child);
//                        break;
//                    }
//                }
//            } else {
//                List<QueryResult> queryResults = new ArrayList<>();
//                QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
//                if (child.getId() != 0) {
//                    child.setAccessable(true);
//                    queryResults.add(child);
//                    QueryResult queryResult = objectMapper.convertValue(parentUnit, QueryResult.class);
//                    queryResult.setChildren(queryResults);
//                    queryResult.setAccessable(true);
//                    list.add(queryResult);
//                } else {
//                    resultQueryResults.add(objectMapper.convertValue(parentUnit, QueryResult.class));
//                }
//
//            }
//            organizationIds.add(parentOrgId);
//        }
//
//        if (accessPageService.isHubMember(UserContext.getUserDetails().getId())) {
//            resultQueryResults.add(treeStructureService.getTreeStructure(list));
//            setUnitPermission(resultQueryResults, true);
//
//        } else {
//            for (QueryResult queryResult : list) {
//                resultQueryResults.add(treeStructureService.getTreeStructure(Arrays.asList(queryResult)));
//            }
//            setUnitPermission(resultQueryResults, false);
//        }
       return resultQueryResults;
    }


    /**
     * @param parentOrganizationId
     * @param organizationHierarchyFilterDTO
     * @return
     */
    public OrganizationBaseEntity generateOrganizationHierarchyByFilter(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO) {
        return null;//unitGraphRepository.getOrganizationHierarchyByFilters(parentOrganizationId, organizationHierarchyFilterDTO);
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

    private void setUnitPermission(OrganizationBaseEntity organizationHierarchy,boolean countryAdmin,List<QueryResult> resultQueryResults) {
        Set<Long> organizationIds=new HashSet<>();
        List<StaffAccessGroupQueryResult> staffAccessGroupQueryResults = accessPageService.getAccessPermission(UserContext.getUserDetails().getId(), getAllUnitIds(organizationHierarchy,organizationIds));
        Map<Long, Boolean> unitPermissionMap = staffAccessGroupQueryResults.stream().collect(Collectors.toMap(StaffAccessGroupQueryResult::getUnitId, StaffAccessGroupQueryResult::isHasPermission));
        resultQueryResults.add(new QueryResult(organizationHierarchy.getName(),organizationHierarchy.getId(),new ArrayList<>(),new ArrayList<>(),true,
                (organizationHierarchy instanceof Organization)?((Organization) organizationHierarchy).isKairosHub():false,organizationHierarchy.isEnable(),false,organizationHierarchy.getTimeZone().toString(),false,18743l,countryAdmin?true:unitPermissionMap.get(organizationHierarchy.getId())));
        setPermissionInChildren(organizationHierarchy, unitPermissionMap, countryAdmin,resultQueryResults);
    }

    private void setPermissionInChildren(OrganizationBaseEntity organizationHierarchy, Map<Long, Boolean> unitPermissionMap, boolean countryAdmin,List<QueryResult> resultQueryResults) {
        if(organizationHierarchy instanceof Organization){
            ((Organization) organizationHierarchy).getChildren().forEach(organization->{
                List<QueryResult> children=ObjectMapperUtils.copyPropertiesOfListByMapper(((Organization) organizationHierarchy).getChildren(),QueryResult.class);
                List<QueryResult> units=ObjectMapperUtils.copyPropertiesOfListByMapper(((Organization) organizationHierarchy).getUnits(),QueryResult.class);
                resultQueryResults.get(0).getChildren().add(new QueryResult(organization.getName(),organization.getId(),children,units,true,
                        organization.isKairosHub(),organization.isEnable(),organization.isParentOrganization(),organization.getTimeZone().toString(),organization.isUnion(),18743l,countryAdmin?true:unitPermissionMap.get(organization.getId())));
                organization.getUnits().forEach(unit->{
                    resultQueryResults.get(0).getUnits().add(new QueryResult(unit.getName(),unit.getId(),null,null,true,
                            false,unit.isEnable(),false,organization.getTimeZone().toString(),false,18743l,countryAdmin?true:unitPermissionMap.get(organization.getId())));
                });
                setPermissionInChildren(organization,unitPermissionMap,countryAdmin,resultQueryResults);
            });

        }


    }
    private Set<Long> getAllUnitIds(OrganizationBaseEntity organizationHierarchy,Set<Long> organizationIds){
        if(organizationHierarchy instanceof Organization){
            ((Organization) organizationHierarchy).getChildren().forEach(organization->{
                organizationIds.add(organization.getId());
                getAllUnitIds(organization,organizationIds);
            });

            ((Organization) organizationHierarchy).getUnits().forEach(unit->{
                organizationIds.add(unit.getId());
                getAllUnitIds(unit,organizationIds);
            });
        }
        organizationIds.add(organizationHierarchy.getId());
        return organizationIds;
    }

}
