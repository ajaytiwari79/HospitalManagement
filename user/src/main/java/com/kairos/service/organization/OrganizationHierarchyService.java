package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.filter.FilterAndFavouriteFilterDTO;
import com.kairos.dto.gdpr.filter.FilterAttributes;
import com.kairos.dto.gdpr.filter.FilterResponseDTO;
import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.tree_structure.TreeStructureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class OrganizationHierarchyService {

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private TreeStructureService treeStructureService;

    public QueryResult generateHierarchyMinimum(long parentOrganizationId) {
        List<Map<String, Object>> units = organizationGraphRepository.getParentOrganization(parentOrganizationId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<QueryResult> list = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        for (Map<String, Object> unit : units) {
            Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unit.get("data")).get("parent");
            long id = (long) parentUnit.get("id");
            //this parameter will show the permission for access page for particular organization
            // value will be true or false
            if (ids.contains(id)) {
                for (QueryResult queryResult : list) {
                    if (queryResult.getId() == id) {
                        List<QueryResult> childs = queryResult.getChildren();
                        QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
                        break;
                    }
                }
            } else {
                List<QueryResult> queryResults = new ArrayList<>();
                QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);

                queryResults.add(child);
                QueryResult queryResult = new QueryResult((String) parentUnit.get("name"), id, queryResults);
                list.add(queryResult);
            }
            ids.add(id);
        }
        return treeStructureService.getTreeStructure(list);
    }

    public QueryResult generateHierarchy(long parentOrganizationId) {
        List<Map<String, Object>> units = organizationGraphRepository.getOrganizationHierarchy(parentOrganizationId);

        if (units.isEmpty()) {
            Organization organization = organizationGraphRepository.findOne(parentOrganizationId);
            if (organization == null) {
                return null;
            }
            QueryResult queryResult = new QueryResult();
            queryResult.setId(organization.getId());
            queryResult.setUnion(organization.isUnion());
            queryResult.setName(organization.getName());
            queryResult.setKairosHub(organization.isKairosHub());
            queryResult.setAccessable(true);
            queryResult.setType(ORGANIZATION_LABEL);
            queryResult.setPreKairos(organization.isPrekairos());
            queryResult.setEnabled(organization.isEnable());
            queryResult.setParentOrganization(organization.isParentOrganization());
            queryResult.setTimeZone(organization.getTimeZone()!=null? organization.getTimeZone().getId():null);
            queryResult.setOrganizationLevel(organization.getOrganizationLevel());
            return queryResult;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        List<QueryResult> list = new ArrayList<>();

        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> unit : units) {
            Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unit.get("data")).get("parent");
            long id = (long) parentUnit.get("id");
            if (ids.contains(id)) {
                for (QueryResult queryResult : list) {
                    if (queryResult.getId() == id) {
                        List<QueryResult> childs = queryResult.getChildren();
                        QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
                        child.setAccessable(true);
                        childs.add(child);
                        break;
                    }
                }
            } else {
                List<QueryResult> queryResults = new ArrayList<>();
                QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
                child.setAccessable(true);
                queryResults.add(child);
                QueryResult queryResult = objectMapper.convertValue(parentUnit, QueryResult.class);
                queryResult.setChildren(queryResults);
                queryResult.setAccessable(true);
                list.add(queryResult);
            }
            ids.add(id);
        }
        return treeStructureService.getTreeStructure(list);
    }

    /**
     * @param parentOrganizationId
     * @param userId
     * @param accessPageId         //page id of modules like citizen,visitator
     * @return list of organizations as a tree structure format
     * @author prabjot
     * it will return child organization list based upon the permission for access page
     */
    public QueryResult getChildUnits(long parentOrganizationId, long userId, String accessPageId) {

        List<Map<String, Object>> units = organizationGraphRepository.getParentOrganization(parentOrganizationId);

        ObjectMapper objectMapper = new ObjectMapper();

        List<QueryResult> list = new ArrayList<>();

        List<Long> ids = new ArrayList<>();
        boolean isKairosHub = organizationGraphRepository.isThisKairosHub(parentOrganizationId);
        for (Map<String, Object> unit : units) {
            Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unit.get("data")).get("parent");
            long id = (long) parentUnit.get("id");

            //this parameter will show the permission for access page for particular organization
            // value will be true or false
            boolean isRead;
            if (ids.contains(id)) {
                for (QueryResult queryResult : list) {
                    if (queryResult.getId() == id) {
                        List<QueryResult> childs = queryResult.getChildren();
                        QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
                        if (isKairosHub) {
                            child.setAccessable(true);
                        } else {
                            isRead = organizationGraphRepository.validateAccessGroupInUnit(parentOrganizationId, userId, child.getId(), accessPageId);
                            child.setAccessable(isRead);
                        }
                        childs.add(child);
                        break;
                    }
                }
            } else {
                List<QueryResult> queryResults = new ArrayList<>();
                QueryResult child = objectMapper.convertValue(((Map<String, Object>) unit.get("data")).get("child"), QueryResult.class);
                if (isKairosHub) {
                    child.setAccessable(true);
                } else {
                    isRead = organizationGraphRepository.validateAccessGroupInUnit(parentOrganizationId, userId, child.getId(), accessPageId);
                    child.setAccessable(isRead);
                }
                queryResults.add(child);
                QueryResult queryResult = new QueryResult((String) parentUnit.get("name"), id, queryResults);
                if (isKairosHub) {
                    queryResult.setAccessable(true);
                } else {
                    isRead = organizationGraphRepository.validateAccessGroupInUnit(parentOrganizationId, userId, queryResult.getId(), accessPageId);
                    queryResult.setAccessable(isRead);
                }
                list.add(queryResult);
            }
            ids.add(id);
        }
        return treeStructureService.getTreeStructure(list);
    }

    /**
     *
     * @param parentOrganizationId
     * @param organizationHierarchyFilterDTO
     * @return
     */
    public QueryResult generateOrganizationHierarchyByFilter(long parentOrganizationId,OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO) {
        List<Map<String, Object>> units = organizationGraphRepository.getOrganizationHierarchyByFilters(parentOrganizationId,organizationHierarchyFilterDTO);

        if (units.isEmpty()) {
            Organization organization = organizationGraphRepository.findOne(parentOrganizationId);
            if (organization == null) {
                return null;
            }
            QueryResult queryResult = new QueryResult();
            queryResult.setId(organization.getId());
            queryResult.setUnion(organization.isUnion());
            queryResult.setName(organization.getName());
            queryResult.setKairosHub(organization.isKairosHub());
            queryResult.setAccessable(true);
            queryResult.setType(ORGANIZATION_LABEL);
            queryResult.setPreKairos(organization.isPrekairos());
            queryResult.setEnabled(organization.isEnable());
            queryResult.setParentOrganization(organization.isParentOrganization());
            queryResult.setTimeZone(organization.getTimeZone()!=null? organization.getTimeZone().getId():null);
            queryResult.setOrganizationLevel(organization.getOrganizationLevel());
            return queryResult;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        List<QueryResult> list = new ArrayList<>();

        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> unit : units) {
            Map<String, Object> parentUnit = (Map<String, Object>)unit.get("parent");
            long id = (long) parentUnit.get("id");
            if (ids.contains(id)) {
                for (QueryResult queryResult : list) {
                    if (queryResult.getId() == id) {
                        List<QueryResult> childs = queryResult.getChildren();
                        QueryResult child = objectMapper.convertValue((unit.get("child")), QueryResult.class);
                        child.setAccessable(true);
                        childs.add(child);
                        break;
                    }
                }
            } else {
                List<QueryResult> queryResults = new ArrayList<>();
                QueryResult child = objectMapper.convertValue((unit.get("child")), QueryResult.class);
                child.setAccessable(true);
                queryResults.add(child);
                QueryResult queryResult = objectMapper.convertValue(parentUnit, QueryResult.class);
                queryResult.setChildren(queryResults);
                queryResult.setAccessable(true);
                list.add(queryResult);
            }
            ids.add(id);
        }
        return treeStructureService.getTreeStructure(list);
    }


    /**
     *
     * @param parentOrganizationId
     * @return
     */
    public FilterAndFavouriteFilterDTO getOrganizationHierarchyFilters(long parentOrganizationId) {
        FilterAndFavouriteFilterDTO filterAndFavouriteFilter=new FilterAndFavouriteFilterDTO();
        Map<String,Object> stringObjectMap= organizationGraphRepository.getFiltersByParentOrganizationId(parentOrganizationId);
        List<FilterResponseDTO> filterResponseDTOList=new ArrayList<>();
        for(String s:stringObjectMap.keySet()){
            FilterResponseDTO filterResponseDTO=new FilterResponseDTO();
            switch(s)
            {
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
            List<FilterAttributes> filterAttributes = ObjectMapperUtils.copyPropertiesOfListByMapper((List<Map>)stringObjectMap.get(s), FilterAttributes.class);
            filterResponseDTO.setFilterData(filterAttributes);
            filterResponseDTOList.add(filterResponseDTO);
      }
        filterAndFavouriteFilter.setAllFilters(filterResponseDTOList);
       return filterAndFavouriteFilter;
    }
}
