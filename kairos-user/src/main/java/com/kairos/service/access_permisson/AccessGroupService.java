package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.user.access_permission.AccessPermissionDTO;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.tree_structure.TreeStructureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.*;


/**
 * Created by prabjot on 9/19/16.
 */
@Transactional
@Service
public class AccessGroupService extends UserBaseService {
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private AccessPermissionGraphRepository accessPermissionGraphRepository;
    @Inject
    private TreeStructureService treeStructureService;

    public AccessGroup createAccessGroup(long organizationId, AccessGroup accessGroup) {
        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (organization == null) {
            return null;
        }
        Organization parent;
        if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
        }

        if (parent == null) {
            organization.getAccessGroups().add(accessGroup);
            save(organization);

            //set default permission of access page while creating access group
            accessGroupRepository.setAccessPagePermission(accessGroup.getId());
            return accessGroup;
        }
        return null;
    }

    public AccessGroup updateAccessGroup(long accessGroupId, AccessGroup accessGroup) {
        AccessGroup objectToUpdate = accessGroupRepository.findOne(accessGroupId);
        if (objectToUpdate == null) {
            return null;
        }
        objectToUpdate.setName(accessGroup.getName());
        save(objectToUpdate);
        return objectToUpdate;
    }

    public boolean deleteAccessGroup(long accessGroupId) {
        AccessGroup objectToDelete = accessGroupRepository.findOne(accessGroupId);
        if (objectToDelete == null) {
            return false;
        }
        objectToDelete.setEnabled(false);
        save(objectToDelete);
        return true;
    }


    /**
     * @param organization
     * @author prabjot
     * this method will find the root organization, if root node exist then will return access group of root node
     * otherwise new access group will be created for organization
     */
    public List<AccessGroup> createDefaultAccessGroups(Organization organization) {

        //get root organization
        Organization parent;
        if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
        }
        List<AccessGroup> accessGroupList = null;
        List<Long> accessGroupIds = new ArrayList<>();
        if (parent == null) {
            String accessGroupNames[] = new String[]{VISITATOR, PLANNER, TASK_GIVERS, COUNTRY_ADMIN, UNIT_MANAGER};
            accessGroupList = new ArrayList<>(accessGroupNames.length);
            for (String name : accessGroupNames) {
                AccessGroup accessGroup = new AccessGroup(name);
                accessGroup.setCreationDate(new Date().getTime());
                accessGroup.setLastModificationDate(new Date().getTime());
                if(TASK_GIVERS.equals(name)){
                    accessGroup.setTypeOfTaskGiver(true);
                }
                save(accessGroup);
                accessGroupIds.add(accessGroup.getId());
                accessGroupList.add(accessGroup);
            }
            organization.setAccessGroups(accessGroupList);
            accessGroupRepository.setAccessPagePermission(accessGroupIds);
        } else {
            organization.setAccessGroups(parent.getAccessGroups());
        }
        save(organization);
        return accessGroupList;
    }

    public List<Map<String, Object>> getAccessGroups(long organizationId) {

        List<Map<String, Object>> list = new ArrayList<>();
        for (AccessGroup accessGroup : accessGroupRepository.getAccessGroups(organizationId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", accessGroup.getId());
            map.put("name", accessGroup.getName());
            list.add(map);
        }
        return list;
    }

    public boolean assignAccessGroupToStaff(List<String> accessGroupIds, long staffId) {

        List<Long> accessGroupLongValue = new ArrayList<Long>(accessGroupIds.size());
        for (String accessGroupId : accessGroupIds) {
            accessGroupLongValue.add(Long.valueOf(accessGroupId));
        }

        Staff staff = accessGroupRepository.assignGroupToStaff(staffId, accessGroupLongValue);
        return staff != null;
    }

    public AccessPage createAccessPage(String name, List<Map<String, Object>> childPage, boolean isModule) {

        AccessPage parentAccessPage = new AccessPage(name);
        List<AccessPage> accessPageList = new ArrayList<>();
        for (Map<String, Object> childAccessPage : childPage) {
            AccessPage accessPage = new AccessPage((String) childAccessPage.get("name"));
            save(accessPage);
            accessPageList.add(accessPage);
        }
        parentAccessPage.setModule(isModule);
        parentAccessPage.setSubPages(accessPageList);
        save(parentAccessPage);
        return parentAccessPage;
    }

    public List<AccessPage> getAccessModulesForUnits(long parentOrganizationId, long userId) {
        return accessPageRepository.getAccessModulesForUnits(parentOrganizationId, userId);
    }

    public void modifyAccessPagePermission(long unitEmploymentId, long accessPageId, boolean read) {
        accessPageRepository.modifyAccessPagePermission(unitEmploymentId, accessPageId, read);
    }


    public List<AccessPageQueryResult> getAccessPageHierarchy(long accessGroupId) {
        List<Map<String, Object>> accessPages = accessPageRepository.getAccessPageHierarchy(accessGroupId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessPageQueryResult> queryResults = new ArrayList<>();
        for (Map<String, Object> accessPage : accessPages) {
            AccessPageQueryResult accessPageQueryResult = objectMapper.convertValue((Map<String, Object>) accessPage.get("data"), AccessPageQueryResult.class);
            queryResults.add(accessPageQueryResult);
        }
        List<AccessPageQueryResult> treeData = getAccessPageHierarchy(queryResults,queryResults);

        List<AccessPageQueryResult> modules = new ArrayList<>();
        for(AccessPageQueryResult accessPageQueryResult : treeData){
            if(accessPageQueryResult.isModule()){
                modules.add(accessPageQueryResult);
            }
        }
        return modules;
    }

    public List<AccessPageQueryResult> getAccessPageByAccessGroup(long accessGroupId,long unitId,long staffId) {
        Organization unit = organizationGraphRepository.findOne(unitId,0);

        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        List<Map<String, Object>> accessPages;
        if(parent == null){
            accessPages = accessPageRepository.getAccessPageByAccessGroup(unitId,unitId,staffId,accessGroupId);
        } else {
            accessPages = accessPageRepository.getAccessPageByAccessGroup(parent.getId(),unitId,staffId,accessGroupId);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessPageQueryResult> queryResults = new ArrayList<>();
        for (Map<String, Object> accessPage : accessPages) {
            AccessPageQueryResult accessPageQueryResult = objectMapper.convertValue((Map<String, Object>) accessPage.get("data"), AccessPageQueryResult.class);
            queryResults.add(accessPageQueryResult);
        }
        List<AccessPageQueryResult> treeData = getAccessPageHierarchy(queryResults,queryResults);

        List<AccessPageQueryResult> modules = new ArrayList<>();
        for(AccessPageQueryResult accessPageQueryResult : treeData){
            if(accessPageQueryResult.isModule()){
                modules.add(accessPageQueryResult);
            }
        }
        return modules;
    }

    public boolean setAccessPagePermissions(long accessGroupId, List<Long> accessGroupIds,boolean isSelected) {
        long creationDate = new Date().getTime();
        long lastModificationDate = new Date().getTime();
        accessGroupRepository.updateAccessPagePermission(accessGroupId,accessGroupIds,isSelected,creationDate,lastModificationDate);
        return true;
    }

    public Map<String, Object> setPagePermissionToUser(long staffId, long unitId, long accessGroupId, long tabId, boolean read, boolean write) {

        Map<String,Object> response = accessPermissionGraphRepository.setPagePermissionToUser(unitId, staffId, accessGroupId, tabId, read, write);
        return response;
    }

    public List<AccessPageQueryResult> getAccessPageHierarchy(List<AccessPageQueryResult> allResults,List<AccessPageQueryResult> accessPageQueryResults) {

        for(AccessPageQueryResult accessPageQueryResult : accessPageQueryResults){
            accessPageQueryResult.setChildren(getChilds(allResults,accessPageQueryResult));
            getAccessPageHierarchy(allResults,accessPageQueryResult.getChildren());
        }

        return allResults;
    }

    private List<AccessPageQueryResult> getChilds(List<AccessPageQueryResult> accessPageQueryResults,AccessPageQueryResult accessPageQueryResult){


        AccessPageQueryResult result = null;
        for(AccessPageQueryResult accessPageQueryResult1 : accessPageQueryResults){
            if(accessPageQueryResult1.getId() == accessPageQueryResult.getId() && accessPageQueryResult1.isWrite()){
                result = accessPageQueryResult1;
                break;
            } else if(accessPageQueryResult1.getId() == accessPageQueryResult.getId()){
                result = accessPageQueryResult1;
            }

        }

        if(result == null){
            return new ArrayList<>();
        }
        return result.getChildren();
    }

    public List<Map<String, Object>> getAccessPermissions(long staffId) {

        return accessGroupRepository.getAccessPermissions(staffId);
    }

    public void assignPermission(long accessGroupId,AccessPermissionDTO accessPermissionDTO){

        Organization unit = organizationGraphRepository.findOne(accessPermissionDTO.getUnitId(),0);
        if(unit == null){
            throw new InternalError("Unit can't be null");
        }
        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        if(parent == null){
            accessGroupRepository.setPermissionForTab(unit.getId(),accessPermissionDTO.getStaffId(),unit.getId(),accessGroupId,accessPermissionDTO.getPageId(),accessPermissionDTO.isRead(),accessPermissionDTO.isWrite());
        } else {
            accessGroupRepository.setPermissionForTab(parent.getId(),accessPermissionDTO.getStaffId(),unit.getId(),accessGroupId,accessPermissionDTO.getPageId(),accessPermissionDTO.isRead(),accessPermissionDTO.isWrite());
        }

    }

}
