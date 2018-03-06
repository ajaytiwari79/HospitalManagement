package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.client.dto.organization.OrganizationCategoryDTO;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.enums.OrganizationCategory;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.user.access_permission.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.CountryAccessGroupRelationship;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.country.CountryAccessGroupRelationshipRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.access_group.CountryAccessGroupDTO;
import com.kairos.response.dto.web.cta.AccessGroupDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.util.DateUtil;
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
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private CountryAccessGroupRelationshipRepository countryAccessGroupRelationshipRepository;

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

    public AccessGroup updateAccessGroup(long accessGroupId, AccessGroupDTO accessGroupDTO) {
        AccessGroup accessGrpToUpdate = accessGroupRepository.findOne(accessGroupId);
        if (Optional.ofNullable(accessGrpToUpdate).isPresent()) {
            throw new DataNotFoundByIdException("Incorrect Access Group id " + accessGroupId);
        }
        accessGrpToUpdate.setName(accessGrpToUpdate.getName());
        save(accessGrpToUpdate);
        return accessGrpToUpdate;
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
                AccessGroup accessGroup = new AccessGroup(name, null);
                accessGroup.setCreationDate(DateUtil.getCurrentDate().getTime());
                accessGroup.setLastModificationDate(DateUtil.getCurrentDate().getTime());
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

    public List<AccessGroup> getAccessGroups(long organizationId) {

       return accessGroupRepository.getAccessGroups(organizationId);
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
        long creationDate = DateUtil.getCurrentDate().getTime();
        long lastModificationDate = DateUtil.getCurrentDate().getTime();
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

    private void assignPermissionOnNewUnit(Long organizationId,Long unitId){

    }

    public List<AccessGroup> findAllAccessGroup(){
      return accessGroupRepository.findAll();
    }



    /***** Access group - COUNTRY LEVEL - STARTS HERE ******************/

    public AccessGroup createCountryAccessGroup(long countryId, CountryAccessGroupDTO accessGroupDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithName(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString());
        if ( isAccessGroupExistWithSameName ) {
            throw new DuplicateDataException("Access Group already exists with name " +accessGroupDTO.getName() );
        }
        AccessGroup accessGroup = new AccessGroup(accessGroupDTO.getName(), accessGroupDTO.getDescription());
        accessGroup.setCreationDate(DateUtil.getCurrentDate().getTime());
        accessGroup.setLastModificationDate(DateUtil.getCurrentDate().getTime());

        CountryAccessGroupRelationship accessGroupRelationship = new CountryAccessGroupRelationship(country, accessGroup, accessGroupDTO.getOrganizationCategory());
        accessGroupRelationship.setCreationDate(DateUtil.getCurrentDate().getTime());
        accessGroupRelationship.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        countryAccessGroupRelationshipRepository.save(accessGroupRelationship);
        save(country);

        //set default permission of access page while creating access group
        accessGroupRepository.setAccessPagePermission(accessGroup.getId());
        return accessGroup;
    }

    public AccessGroup updateCountryAccessGroup(long countryId, Long accessGroupId, CountryAccessGroupDTO accessGroupDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        AccessGroup accessGrpToUpdate = accessGroupRepository.findCountryAccessGroupByIdAndCategory(countryId, accessGroupId, accessGroupDTO.getOrganizationCategory().toString());
        if (! Optional.ofNullable(accessGrpToUpdate).isPresent()) {
            throw new DataNotFoundByIdException("Incorrect Access Group id " + accessGroupId);
        }
        if( accessGroupRepository.isCountryAccessGroupExistWithNameExceptId(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString(), accessGroupId) ){
            throw new DuplicateDataException("Access Group already exists with name " +accessGroupDTO.getName() );
        }

        accessGrpToUpdate.setName(accessGroupDTO.getName());
        accessGrpToUpdate.setDescription(accessGroupDTO.getDescription());
        accessGrpToUpdate.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        save(accessGrpToUpdate);
        return accessGrpToUpdate;

    }

    public boolean deleteCountryAccessGroup(long accessGroupId) {
        AccessGroup accessGroupToDelete = accessGroupRepository.findOne(accessGroupId);
        if (! Optional.ofNullable(accessGroupToDelete).isPresent()) {
            throw new DataNotFoundByIdException("Incorrect Access Group id " + accessGroupId);
        }
        accessGroupToDelete.setDeleted(true);
        accessGroupToDelete.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        save(accessGroupToDelete);
        return true;
    }


    AccessGroup getCountryAccessGroupByName(Long countryId, OrganizationCategory category, String name){
        return accessGroupRepository.findCountryAccessGroupByNameAndCategory(countryId, name, category.toString());
    }

    public List<OrganizationCategoryDTO> getListOfOrgCategoryWithCountryAccessGroupCount(Long countryId){
        List<OrganizationCategoryDTO> organizationCategoryDTOS = OrganizationCategory.getListOfOrganizationCategory();
        AccessGroupCountQueryResult accessGroupCountData = accessGroupRepository.getListOfOrgCategoryWithCountryAccessGroupCount(countryId);
        organizationCategoryDTOS.forEach(orgCategoryDTO ->{
            switch (OrganizationCategory.valueOf(orgCategoryDTO.getValue())){
                case HUB: {
                    orgCategoryDTO.setCount(accessGroupCountData.getHubCount());
                    break;
                }
                case ORGANIZATION: {
                    orgCategoryDTO.setCount(accessGroupCountData.getOrganizationCount());
                    break;
                }
                case UNION: {
                    orgCategoryDTO.setCount(accessGroupCountData.getUnionCount());
                    break;
                }
            }
        } );
        return organizationCategoryDTOS;
    }

    public List<AccessGroupQueryResult> getCountryAccessGroups(Long countryId, OrganizationCategory organizationCategory) {

        return accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, organizationCategory.toString());
    }

    /***** Access group - COUNTRY LEVEL - ENDS HERE ******************/

}
