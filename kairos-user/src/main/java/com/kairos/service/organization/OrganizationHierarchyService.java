package com.kairos.service.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.tree_structure.TreeStructureService;

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
            queryResult.setName(organization.getName());
            queryResult.setKairosHub(organization.isKairosHub());
            queryResult.setAccessable(true);
            queryResult.setType(ORGANIZATION_LABEL);
            queryResult.setPreKairos(organization.isPrekairos());
            queryResult.setEnabled(organization.isEnable());
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
}
