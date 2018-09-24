package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.access_group.AccessGroupWrapper;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.OrganizationLevel;
import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.CountryAccessGroupRelationship;
import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.country.default_data.account_type.AccountTypeAccessGroupCountQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.access_permission.AccessGroupByCategoryWrapper;
import com.kairos.persistence.model.user.access_permission.AccessGroupsByCategoryDTO;
import com.kairos.persistence.model.user.counter.StaffIdsQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.country.CountryAccessGroupRelationshipRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.dto.user.access_group.CountryAccessGroupDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.access_permission.AccessPermissionDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.AccessGroupDTO;
import com.kairos.dto.user.organization.OrganizationCategoryDTO;
import com.kairos.utils.DateUtil;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.AG_COUNTRY_ADMIN;


/**
 * Created by prabjot on 9/19/16.
 */
@Transactional
@Service
public class AccessGroupService {
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
    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private AccountTypeGraphRepository accountTypeGraphRepository;
    @Inject private StaffService staffService;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;

    public AccessGroup createAccessGroup(long organizationId, AccessGroupDTO accessGroupDTO) {
        if(accessGroupDTO.getEndDate()!=null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())){
            exceptionService.actionNotPermittedException("start_date.less.from.end_date");
        }
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isOrganizationAccessGroupExistWithName(organizationId, accessGroupDTO.getName().trim());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException("message.duplicate", "access-group", accessGroupDTO.getName());

        }
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
        List<DayType> dayTypes=dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        AccessGroup accessGroup=ObjectMapperUtils.copyPropertiesByMapper(accessGroupDTO,AccessGroup.class);
        accessGroup.setDayTypes(dayTypes);

        if (parent == null) {
            organization.getAccessGroups().add(accessGroup);
            organizationGraphRepository.save(organization,2);

            //set default permission of access page while creating access group
            Long countryId = organizationService.getCountryIdOfOrganization(organization.getId());
            setAccessPageRelationshipWithAccessGroupByOrgCategory(countryId, accessGroup.getId(), organizationService.getOrganizationCategory(organization.getCompanyType()));
            return accessGroup;
        } else {
            exceptionService.actionNotPermittedException("message.permitted", "access-group");

        }
        return null;
    }

    public AccessGroup updateAccessGroup(long accessGroupId, Long unitId, AccessGroupDTO accessGroupDTO) {
        if(accessGroupDTO.getEndDate()!=null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())){
            exceptionService.actionNotPermittedException("start_date.less.from.end_date");
        }
        AccessGroup accessGrpToUpdate = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGrpToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", accessGroupId);

        }
//        if (accessGroupRepository.isOrganizationAccessGroupExistWithNameExceptId(unitId, accessGroupDTO.getName(), accessGroupId)) {
//            exceptionService.duplicateDataException("message.duplicate", "access-group", accessGroupDTO.getName());
//
//        }
        List<DayType> dayTypes=dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        accessGrpToUpdate.setName(accessGroupDTO.getName());
        accessGrpToUpdate.setRole(accessGroupDTO.getRole());
        accessGrpToUpdate.setDescription(accessGroupDTO.getDescription());
        accessGrpToUpdate.setEnabled(accessGroupDTO.isEnabled());
        accessGrpToUpdate.setStartDate(accessGroupDTO.getStartDate());
        accessGrpToUpdate.setEndDate(accessGroupDTO.getEndDate());
        accessGrpToUpdate.setDayTypes(dayTypes);
        accessGroupRepository.save(accessGrpToUpdate);
        return accessGrpToUpdate;
    }

    public boolean deleteAccessGroup(long accessGroupId) {
        AccessGroup objectToDelete = accessGroupRepository.findOne(accessGroupId);
        if (objectToDelete == null) {
            return false;
        }
        objectToDelete.setDeleted(true);
        accessGroupRepository.save(objectToDelete);
        return true;
    }

    /**
     * @param organization
     * @author prabjot
     * this method will find the root organization, if root node exist then will return access group of root node
     * otherwise new access group will be created for organization
     */
    public Map<Long, Long> createDefaultAccessGroups(Organization organization) {

        //get root organization
        Organization parent;
        Map<Long, Long> countryAndOrgAccessGroupIdsMap = new HashMap<>();
        if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
        }
        Long countryId = organizationService.getCountryIdOfOrganization(organization.getId());
        List<AccessGroup> accessGroupList = null;
        if (parent == null) {
            List<AccessGroup> countryAccessGroups = accessGroupRepository.getCountryAccessGroupByCategory(countryId, organizationService.getOrganizationCategory(organization.getCompanyType()).toString());
            accessGroupList = new ArrayList<>(countryAccessGroups.size());
            for (AccessGroup countryAccessGroup : countryAccessGroups) {

                AccessGroup accessGroup = new AccessGroup(countryAccessGroup.getName(), countryAccessGroup.getDescription(), countryAccessGroup.getRole(),countryAccessGroup.getDayTypes());
                accessGroup.setCreationDate(DateUtil.getCurrentDate().getTime());
                accessGroup.setLastModificationDate(DateUtil.getCurrentDate().getTime());
                accessGroupRepository.save(accessGroup);
                countryAndOrgAccessGroupIdsMap.put(countryAccessGroup.getId(), accessGroup.getId());
                accessGroupRepository.setAccessPagePermissionForAccessGroup(countryAccessGroup.getId(), accessGroup.getId());
                accessGroupList.add(accessGroup);
            }

            organization.setAccessGroups(accessGroupList);
        } else {
            // Remove AG_COUNTRY_ADMIN access group to be copied
            List<AccessGroup> accessGroups = new ArrayList<>(parent.getAccessGroups());
            for (AccessGroup accessGroup : accessGroups) {
                if (accessGroup.getName().equals(AG_COUNTRY_ADMIN)) {
                    accessGroups.remove(accessGroup);
                }
            }
            organization.setAccessGroups(accessGroups);
        }
        organizationGraphRepository.save(organization);
        return countryAndOrgAccessGroupIdsMap;
    }

    public AccessGroupWrapper getAccessGroupsForUnit(long organizationId) {
        Long countryId=organizationGraphRepository.getCountryId(organizationId);
        List<DayType> dayTypes=dayTypeGraphRepository.findByCountryId(countryId);
        List<AccessGroup> accessGroups= accessGroupRepository.getAccessGroupsForUnit(organizationId);
        List<AccessGroupDTO> accessGroupDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(accessGroups,AccessGroupDTO.class);
        List<DayTypeDTO> dayTypeDTOS=  ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypes, DayTypeDTO.class);
        return new AccessGroupWrapper(accessGroupDTOS,dayTypeDTOS);
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
            accessPageRepository.save(accessPage);
            accessPageList.add(accessPage);
        }
        parentAccessPage.setModule(isModule);
        parentAccessPage.setSubPages(accessPageList);
        accessPageRepository.save(parentAccessPage);
        return parentAccessPage;
    }

    public List<AccessPage> getAccessModulesForUnits(long parentOrganizationId, long userId) {
        return accessPageRepository.getAccessModulesForUnits(parentOrganizationId, userId);
    }

    public void modifyAccessPagePermission(long unitEmploymentId, long accessPageId, boolean read) {
        accessPageRepository.modifyAccessPagePermission(unitEmploymentId, accessPageId, read);
    }


    public List<AccessPageQueryResult> getAccessPageHierarchy(long accessGroupId, Long countryId) {
        // Check if access group is of country
        if (Optional.ofNullable(countryId).isPresent()) {
            AccessGroup accessGroup = accessGroupRepository.findCountryAccessGroupById(accessGroupId, countryId);
            if (Optional.ofNullable(accessGroup).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", accessGroupId);

            }
        }

        List<Map<String, Object>> accessPages = accessPageRepository.getSelectedAccessPageHierarchy(accessGroupId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessPageQueryResult> queryResults = new ArrayList<>();
        for (Map<String, Object> accessPage : accessPages) {
            AccessPageQueryResult accessPageQueryResult = objectMapper.convertValue((Map<String, Object>) accessPage.get("data"), AccessPageQueryResult.class);
            queryResults.add(accessPageQueryResult);
        }
        List<AccessPageQueryResult> treeData = getAccessPageHierarchy(queryResults, queryResults);

        List<AccessPageQueryResult> modules = new ArrayList<>();
        for (AccessPageQueryResult accessPageQueryResult : treeData) {
            if (accessPageQueryResult.isModule()) {
                modules.add(accessPageQueryResult);
            }
        }
        return modules;
    }

    public List<AccessPageQueryResult> getAccessPageByAccessGroup(long accessGroupId, long unitId, long staffId) {
        Organization unit = organizationGraphRepository.findOne(unitId, 0);

        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        List<Map<String, Object>> accessPages;

        accessPages = accessPageRepository.getAccessPagePermissionOfStaff(
                (Optional.ofNullable(parent).isPresent() ? parent.getId() : unitId)
                , unitId, staffId, accessGroupId);

        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessPageQueryResult> queryResults = new ArrayList<>();
        for (Map<String, Object> accessPage : accessPages) {
            AccessPageQueryResult accessPageQueryResult = objectMapper.convertValue((Map<String, Object>) accessPage.get("data"), AccessPageQueryResult.class);
            queryResults.add(accessPageQueryResult);
        }
        List<AccessPageQueryResult> treeData = getAccessPageHierarchy(queryResults, queryResults);

        List<AccessPageQueryResult> modules = new ArrayList<>();
        for (AccessPageQueryResult accessPageQueryResult : treeData) {
            if (accessPageQueryResult.isModule()) {
                modules.add(accessPageQueryResult);
            }
        }
        return modules;
    }

    public Boolean setAccessPagePermissions(long accessGroupId, List<Long> accessPageIds, boolean isSelected, Long countryId) {
        // Check if access group is of country
        if (Optional.ofNullable(countryId).isPresent()) {
            AccessGroup accessGroup = accessGroupRepository.findCountryAccessGroupById(accessGroupId, countryId);
            if (Optional.ofNullable(accessGroup).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", accessGroupId);

            }
        }
        long creationDate = DateUtil.getCurrentDate().getTime();
        long lastModificationDate = DateUtil.getCurrentDate().getTime();
        Boolean read = isSelected;
        Boolean write = isSelected;


        accessGroupRepository.updateAccessPagePermission(accessGroupId, accessPageIds, isSelected, creationDate, lastModificationDate, read, write);

        // Update read/write permission of successive parent tabs
        updateReadWritePermissionOfSuccessiveParentTabForAccessGroup(accessGroupId, accessPageIds.get(0));
        // Remove customized permission for accessPageIds of accessGroupId
        accessPageRepository.removeCustomPermissionsForAccessGroup(accessGroupId, accessPageIds);
        return true;
    }

    public Map<String, Object> setPagePermissionToUser(long staffId, long unitId, long accessGroupId, long tabId, boolean read, boolean write) {

        return accessPermissionGraphRepository.setPagePermissionToUser(unitId, staffId, accessGroupId, tabId, read, write);

    }

    public List<AccessPageQueryResult> getAccessPageHierarchy(List<AccessPageQueryResult> allResults, List<AccessPageQueryResult> accessPageQueryResults) {

        for (AccessPageQueryResult accessPageQueryResult : accessPageQueryResults) {
            accessPageQueryResult.setChildren(getChilds(allResults, accessPageQueryResult));
            getAccessPageHierarchy(allResults, accessPageQueryResult.getChildren());
        }

        return allResults;
    }

    private List<AccessPageQueryResult> getChilds(List<AccessPageQueryResult> accessPageQueryResults, AccessPageQueryResult accessPageQueryResult) {


        AccessPageQueryResult result = null;
        for (AccessPageQueryResult accessPageQueryResult1 : accessPageQueryResults) {
            if (accessPageQueryResult1.getId() == accessPageQueryResult.getId() && accessPageQueryResult1.isWrite()) {
                result = accessPageQueryResult1;
                break;
            } else if (accessPageQueryResult1.getId() == accessPageQueryResult.getId()) {
                result = accessPageQueryResult1;
            }

        }

        if (result == null) {
            return new ArrayList<>();
        }
        return result.getChildren();
    }

    public List<Map<String, Object>> getAccessPermissions(long staffId) {

        return accessGroupRepository.getAccessPermissions(staffId);
    }

    public void updateReadWritePermissionOfSuccessiveParentTabForAccessGroup(Long tabId, Long accessGroupId) {
        // fetch parentTab Id
        Long parentTabId = accessPageRepository.getParentAccessPageIdForAccessGroup(tabId);

        if (!Optional.ofNullable(parentTabId).isPresent()) {
            return;
        }

        // Check read/write permission of child tabs and set parent tab permission accordingly
        List<AccessPageQueryResult> accesPageList = accessPageRepository.getChildAccessPagePermissionsForAccessGroup(accessGroupId, parentTabId);

        boolean parentTabRead = false, parentTabWrite = false;

        // Predicate to check if any of tab has read and write access
        parentTabRead = accesPageList.stream().anyMatch(accessPage -> accessPage.isRead());
        parentTabWrite = accesPageList.stream().anyMatch(accessPage -> accessPage.isWrite());

        accessPageRepository.updateAccessPagePermissionsForAccessGroup(accessGroupId, parentTabId, parentTabRead, parentTabWrite);
        updateReadWritePermissionOfSuccessiveParentTabForAccessGroup(parentTabId, accessGroupId);
    }

    public void updateReadWritePermissionOfParentTab(Long accessGroupId, Boolean read, Boolean write, Long tabId, Long orgId, Long unitId, Long staffId) {

        AccessPageQueryResult readAndWritePermissionForAccessGroup = accessPageRepository.getAccessPermissionForAccessPage(accessGroupId, tabId);

        // Check if new permissions are different then of Access Group
        /*if(Optional.ofNullable(readAndWritePermissionForAccessGroup).isPresent() && readAndWritePermissionForAccessGroup.isRead() == read  && readAndWritePermissionForAccessGroup.isWrite() == write){
            // CHECK if custom permission exist and then delete
            accessGroupRepository.deleteCustomPermissionForTab(orgId, staffId, unitId, accessGroupId, tabId);
        } else {
            accessGroupRepository.setCustomPermissionForTab(orgId, staffId, unitId, accessGroupId, tabId, read, write);
        }*/

        List<AccessPageQueryResult> accesPageList = accessPageRepository.getChildTabsAccessPermissionsByStaffAndOrg(orgId, unitId, staffId, tabId, accessGroupId);

        boolean parentTabRead = false, parentTabWrite = false;

        // Predicate to check if any of tab has read and write access
        parentTabRead = accesPageList.stream().anyMatch(accessPage -> accessPage.isRead());
        parentTabWrite = accesPageList.stream().anyMatch(accessPage -> accessPage.isWrite());

        // Check if new permissions are different then of Access Group
        if (Optional.ofNullable(readAndWritePermissionForAccessGroup).isPresent() &&
                readAndWritePermissionForAccessGroup.isRead() == parentTabRead && readAndWritePermissionForAccessGroup.isWrite() == parentTabWrite) {
            // CHECK if custom permission exist and then delete
            accessGroupRepository.deleteCustomPermissionForTab(orgId, staffId, unitId, accessGroupId, tabId);
        } else {
            accessGroupRepository.setCustomPermissionForTab(orgId, staffId, unitId, accessGroupId, tabId, read, write);
        }

        Long parentTabId = accessPageRepository.getParentTab(tabId);
        if (!Optional.ofNullable(parentTabId).isPresent()) {
            return;
        }
        updateReadWritePermissionOfParentTab(accessGroupId, parentTabRead, parentTabWrite, parentTabId, orgId, unitId, staffId);
    }

    public void assignPermission(long accessGroupId, AccessPermissionDTO accessPermissionDTO) {

        Organization unit = organizationGraphRepository.findOne(accessPermissionDTO.getUnitId(), 0);
        if (unit == null) {
            exceptionService.internalServerError("error.unit.notNull");
        }
        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
//        updateReadWritePermissionOfParentTab(accessGroupId, accessPermissionDTO.isRead(), accessPermissionDTO.isWrite(),
//                accessPermissionDTO.getPageId(), (!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), accessPermissionDTO.getUnitId(), accessPermissionDTO.getStaffId());


        AccessPageQueryResult readAndWritePermissionForAccessGroup = accessPageRepository.getAccessPermissionForAccessPage(accessGroupId, accessPermissionDTO.getPageId());

        AccessPageQueryResult customReadAndWritePermissionForAccessGroup = accessPageRepository.getCustomPermissionOfTab(
                (!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()),
                accessPermissionDTO.getStaffId(), unit.getId(), accessPermissionDTO.getPageId(), accessGroupId);
        Boolean savedReadCheck = readAndWritePermissionForAccessGroup.isRead();
        Boolean savedWriteCheck = readAndWritePermissionForAccessGroup.isWrite();
        if (Optional.ofNullable(customReadAndWritePermissionForAccessGroup).isPresent()) {
            savedReadCheck = customReadAndWritePermissionForAccessGroup.isRead();
            savedWriteCheck = customReadAndWritePermissionForAccessGroup.isWrite();
        }

        Boolean write = accessPermissionDTO.isWrite();
        Boolean read = accessPermissionDTO.isRead();

        // If change has been done in read and if it is false then set write as false too
        if (savedReadCheck != read && !read) {
            write = false;
        }
        // If change has been done in write and if it is true then set read as true too
        else if (savedWriteCheck != write && write) {
            read = true;
        }

        // Check if new permissions are different then of Access Group
        if (Optional.ofNullable(readAndWritePermissionForAccessGroup).isPresent() && readAndWritePermissionForAccessGroup.isRead() == read && readAndWritePermissionForAccessGroup.isWrite() == write) {
            // CHECK if custom permission exist and then delete
//            accessGroupRepository.deleteCustomPermissionForTab((!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()) ,accessPermissionDTO.getStaffId(), unit.getId(), accessGroupId,accessPermissionDTO.getPageId());
            accessGroupRepository.deleteCustomPermissionForChildren((!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), accessPermissionDTO.getStaffId(), unit.getId(), accessGroupId, accessPermissionDTO.getPageId());

        } else {
            accessGroupRepository.setCustomPermissionForChildren((!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), accessPermissionDTO.getStaffId(), unit.getId(), accessGroupId, accessPermissionDTO.getPageId(), read, write);

            /*if(updateChildren) {
                accessGroupRepository.setCustomPermissionForTabAndChildren((!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), accessPermissionDTO.getStaffId(), unit.getId(), accessGroupId, accessPermissionDTO.getPageId(), read, write);
            } else {
                accessGroupRepository.setCustomPermissionForTab((!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), accessPermissionDTO.getStaffId(), unit.getId(), accessGroupId, accessPermissionDTO.getPageId(), read, write);
            }*/
        }

        Long parentTabId = accessPageRepository.getParentTab(accessPermissionDTO.getPageId());
        if (!Optional.ofNullable(parentTabId).isPresent()) {
            return;
        } else {
            updateReadWritePermissionOfParentTab(accessGroupId, read, write, accessPermissionDTO.getPageId(),
                    (!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), unit.getId(), accessPermissionDTO.getStaffId());
        }
    }

    public List<AccessGroup> findAllAccessGroup() {
        return accessGroupRepository.findAll();
    }


    public Boolean updatePermissionsForAccessTabsOfAccessGroup(Long accessGroupId, Long accessPageId, AccessPermissionDTO accessPermissionDTO, Boolean updateChildren) {

        AccessPageQueryResult readAndWritePermissionOfAccessPage = accessPageRepository.getAccessPermissionForAccessPage(accessGroupId, accessPageId);

        Boolean write = accessPermissionDTO.isWrite();
        Boolean read = accessPermissionDTO.isRead();

        // If change has been done in read and if it is false then set write as false too
        if (readAndWritePermissionOfAccessPage.isRead() != read && !read) {
            write = false;
        }
        // If change has been done in write and if it is true then set read as true too
        else if (readAndWritePermissionOfAccessPage.isWrite() != write && write) {
            read = true;
        }
        if (updateChildren) {
            // Update read/write permission of tab and its children
            return accessGroupRepository.updatePermissionsForAccessTabsAndChildrenOfAccessGroup(accessPageId, accessGroupId, read, write);

        } else {
            // Update read/write permission of tab itself
            return accessGroupRepository.updatePermissionsForAccessTabOfAccessGroup(accessPageId, accessGroupId, accessPermissionDTO.isRead(), accessPermissionDTO.isWrite());
        }
    }

    /***** Access group - COUNTRY LEVEL - STARTS HERE ******************/

    private void setAccessPageRelationshipWithAccessGroupByOrgCategory(Long countryId, Long accessGroupId, OrganizationCategory organizationCategory) {
        switch (organizationCategory) {
            case HUB: {
                accessGroupRepository.setAccessPageForHubAccessGroup(countryId, accessGroupId);
                break;
            }
            case ORGANIZATION: {
                accessGroupRepository.setAccessPageForOrganizationAccessGroup(countryId, accessGroupId);
                break;
            }
            case UNION: {
                accessGroupRepository.setAccessPageForUnionAccessGroup(countryId, accessGroupId);
                break;
            }
        }
    }


    public AccessGroup createCountryAccessGroup(long countryId, CountryAccessGroupDTO accessGroupDTO) {

        if(accessGroupDTO.getEndDate()!=null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())){
            exceptionService.actionNotPermittedException("start_date.less.from.end_date");
        }
        if (OrganizationCategory.ORGANIZATION.equals(accessGroupDTO.getOrganizationCategory()) && accessGroupDTO.getAccountTypeIds().isEmpty()) {
            exceptionService.actionNotPermittedException("message.accountType.select");
        }
        List<AccountType> accountType = accountTypeGraphRepository.getAllAccountTypeByIds(accessGroupDTO.getAccountTypeIds());
        if (accountType.size() != accessGroupDTO.getAccountTypeIds().size()) {
            exceptionService.dataNotMatchedException("message.accountType.notFound");
        }
        List<DayType> dayTypes=dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        Country country = countryGraphRepository.findOne(countryId);

        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithName(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException("message.duplicate", "access-group", accessGroupDTO.getName());

        }

        AccessGroup accessGroup = OrganizationCategory.ORGANIZATION.equals(accessGroupDTO.getOrganizationCategory()) ? new AccessGroup(accessGroupDTO.getName().trim(), accessGroupDTO.getDescription(), accessGroupDTO.getRole(), accountType,dayTypes) : new AccessGroup(accessGroupDTO.getName().trim(), accessGroupDTO.getDescription(), accessGroupDTO.getRole(),dayTypes);

        CountryAccessGroupRelationship accessGroupRelationship = new CountryAccessGroupRelationship(country, accessGroup, accessGroupDTO.getOrganizationCategory());
        accessGroupRelationship.setCreationDate(DateUtil.getCurrentDate().getTime());
        accessGroupRelationship.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        countryAccessGroupRelationshipRepository.save(accessGroupRelationship);
        countryGraphRepository.save(country);
        //set default permission of access page while creating access group
        setAccessPageRelationshipWithAccessGroupByOrgCategory(countryId, accessGroup.getId(), accessGroupDTO.getOrganizationCategory());
        return accessGroup;
    }

    public AccessGroup updateCountryAccessGroup(long countryId, Long accessGroupId, CountryAccessGroupDTO accessGroupDTO) {

        if(accessGroupDTO.getEndDate()!=null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())){
            exceptionService.actionNotPermittedException("start_date.less.from.end_date");
        }
        Optional<AccessGroup> accessGrpToUpdate = accessGroupRepository.findById(accessGroupId);
        if (!accessGrpToUpdate.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", accessGroupId);

        }
        if (accessGroupRepository.isCountryAccessGroupExistWithNameExceptId(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString(), accessGroupId)) {
            exceptionService.duplicateDataException("message.duplicate", "access-group", accessGroupDTO.getName());

        }
        List<DayType> dayTypes=dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        accessGrpToUpdate.get().setName(accessGroupDTO.getName());
        accessGrpToUpdate.get().setDescription(accessGroupDTO.getDescription());
        accessGrpToUpdate.get().setLastModificationDate(DateUtil.getCurrentDate().getTime());
        accessGrpToUpdate.get().setRole(accessGroupDTO.getRole());
        accessGrpToUpdate.get().setEnabled(accessGroupDTO.isEnabled());
        accessGrpToUpdate.get().setStartDate(accessGroupDTO.getStartDate());
        accessGrpToUpdate.get().setEndDate(accessGroupDTO.getEndDate());
        accessGrpToUpdate.get().setDayTypes(dayTypes);
        accessGroupRepository.save(accessGrpToUpdate.get());
        return accessGrpToUpdate.get();
    }

    public boolean deleteCountryAccessGroup(long accessGroupId) {
        AccessGroup accessGroupToDelete = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroupToDelete).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", accessGroupId);

        }
        accessGroupToDelete.setDeleted(true);
        accessGroupToDelete.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        accessGroupRepository.save(accessGroupToDelete);
        return true;
    }


    AccessGroup getCountryAccessGroupByName(Long countryId, OrganizationCategory category, String name) {
        return accessGroupRepository.findCountryAccessGroupByNameAndCategory(countryId, name, category.toString());
    }

    public Map<String, Object> getListOfOrgCategoryWithCountryAccessGroupCount(Long countryId) {
        List<OrganizationCategoryDTO> organizationCategoryDTOS = OrganizationCategory.getListOfOrganizationCategory();
        AccessGroupCountQueryResult accessGroupCountData = accessGroupRepository.getListOfOrgCategoryWithCountryAccessGroupCount(countryId);
        List<AccountTypeAccessGroupCountQueryResult> accountTypes
                = accountTypeGraphRepository.getAllAccountTypeWithAccessGroupCountByCountryId(countryId);
        organizationCategoryDTOS.forEach(orgCategoryDTO -> {
            switch (OrganizationCategory.valueOf(orgCategoryDTO.getValue())) {
                case HUB: {
                    orgCategoryDTO.setCount(accessGroupCountData.getHubCount());
                    break;
                }
                case ORGANIZATION: {
                    orgCategoryDTO.setCount(accountTypes.size());
                    break;
                }
                case UNION: {
                    orgCategoryDTO.setCount(accessGroupCountData.getUnionCount());
                    break;
                }
            }
        });

        Map<String, Object> response = new HashMap<>(2);
        response.put("accountTypes", accountTypes);
        response.put("category", organizationCategoryDTOS);
        return response;
    }

    /**
     * @param accountTypeId
     * @return
     * @author vipul
     * @Desc This api is used to fetch all access group by account type id in country.
     */
    public List<AccessGroupQueryResult> getCountryAccessGroupByAccountTypeId(Long countryId, Long accountTypeId) {

        return accessGroupRepository.getCountryAccessGroupByAccountTypeId(countryId, accountTypeId);
    }

    public List<AccessGroupQueryResult> getCountryAccessGroups(Long countryId, OrganizationCategory organizationCategory) {

        return accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, organizationCategory.toString());
    }

    public AccessGroupByCategoryWrapper getCountryAccessGroupsOfAllCategories(Long countryId) {

        List<AccessGroupsByCategoryDTO> accessGroupsData = new ArrayList<>();
        accessGroupsData.add(new AccessGroupsByCategoryDTO(OrganizationCategory.HUB,
                accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, OrganizationCategory.HUB.toString())));

        accessGroupsData.add(new AccessGroupsByCategoryDTO(OrganizationCategory.ORGANIZATION,
                accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, OrganizationCategory.ORGANIZATION.toString())));

        accessGroupsData.add(new AccessGroupsByCategoryDTO(OrganizationCategory.UNION,
                accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, OrganizationCategory.UNION.toString())));

        List<DayType> dayTypes=dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypes,DayTypeDTO.class);

        return new AccessGroupByCategoryWrapper(accessGroupsData,dayTypeDTOS);
    }

    /***** Access group - COUNTRY LEVEL - ENDS HERE ******************/

    // For Test Cases
    List<Long> getAccessPageIdsByAccessGroup(Long accessGroupId) {
        return accessGroupRepository.getAccessPageIdsByAccessGroup(accessGroupId);
    }

    Long getAccessPageIdByAccessGroup(Long accessGroupId) {
        return accessGroupRepository.getAccessPageIdByAccessGroup(accessGroupId);
    }

    public AccessGroupDTO copyUnitAccessGroup(long organizationId, AccessGroupDTO accessGroupDTO) {

        if(accessGroupDTO.getEndDate()!=null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())){
            exceptionService.actionNotPermittedException("start_date.less.from.end_date");
        }
        Optional<Organization> organization = organizationGraphRepository.findById(organizationId);
        if (!organization.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", organizationId);

        }
        Organization parent;
        if (organization.get().getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.get().getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(organization.get().getId());
        }
        if (Optional.ofNullable(parent).isPresent()) {
            exceptionService.actionNotPermittedException("message.accessGroup.copied");

        }
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isOrganizationAccessGroupExistWithName(organizationId, accessGroupDTO.getName().trim());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException("message.duplicate", "access-group", accessGroupDTO.getName().trim());

        }
        Optional<AccessGroup> currentAccessGroup = accessGroupRepository.findById(accessGroupDTO.getId());
        if (!currentAccessGroup.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", accessGroupDTO.getId());

        }
        AccessGroup accessGroup=new AccessGroup(accessGroupDTO.getName().trim(),accessGroupDTO.getDescription(),accessGroupDTO.getRole(),currentAccessGroup.get().getDayTypes());
        accessGroupRepository.save(accessGroup);
        organization.get().getAccessGroups().add(accessGroup);
        organizationGraphRepository.save(organization.get());
        accessPageRepository.copyAccessGroupPageRelationShips(accessGroupDTO.getId(),accessGroup.getId());
        return new AccessGroupDTO(accessGroup.getId(),accessGroup.getName(),accessGroup.getDescription(),accessGroup.getRole());

    }

    public CountryAccessGroupDTO copyCountryAccessGroup(long countryId, CountryAccessGroupDTO countryAccessGroupDTO) {

        if(countryAccessGroupDTO.getEndDate()!=null && countryAccessGroupDTO.getEndDate().isBefore(countryAccessGroupDTO.getStartDate())){
            exceptionService.actionNotPermittedException("start_date.less.from.end_date");
        }
        Optional<Country> country = countryGraphRepository.findById(countryId);
        if (!country.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithName(countryId, countryAccessGroupDTO.getName().trim(), countryAccessGroupDTO.getOrganizationCategory().toString());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException("message.duplicate", "access-group", countryAccessGroupDTO.getName().trim());

        }
        Optional<AccessGroup> currentAccessGroup = accessGroupRepository.findById(countryAccessGroupDTO.getId());
        if (!currentAccessGroup.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.acessGroupId.incorrect", countryAccessGroupDTO.getId());

        }
        AccessGroup accessGroup = new AccessGroup(countryAccessGroupDTO.getName().trim(), countryAccessGroupDTO.getDescription(), countryAccessGroupDTO.getRole(),currentAccessGroup.get().getAccountType(),currentAccessGroup.get().getDayTypes());
        accessGroup.setCreationDate(DateUtil.getCurrentDate().getTime());
        accessGroup.setLastModificationDate(DateUtil.getCurrentDate().getTime());

        CountryAccessGroupRelationship accessGroupRelationship = new CountryAccessGroupRelationship(country.get(), accessGroup, countryAccessGroupDTO.getOrganizationCategory());
        accessGroupRelationship.setCreationDate(DateUtil.getCurrentDate().getTime());
        accessGroupRelationship.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        countryAccessGroupRelationshipRepository.save(accessGroupRelationship);
        countryGraphRepository.save(country.get());
        accessPageRepository.copyAccessGroupPageRelationShips(countryAccessGroupDTO.getId(), accessGroup.getId());
        countryAccessGroupDTO.setId(accessGroup.getId());
        return countryAccessGroupDTO;
    }

    // Method to fetch list of access group by Organization category ( Hub, Organization and Union)
    public Map<String, List<AccessGroupQueryResult>> getCountryAccessGroupsForOrganizationCreation(Long countryId) {
        Map<String, List<AccessGroupQueryResult>> accessGroupForParentOrganizationCreation = new HashMap<>();
        accessGroupForParentOrganizationCreation.put("hub",
                accessGroupRepository.getCountryAccessGroupByOrgCategoryAndRole(countryId, OrganizationCategory.HUB.toString(), AccessGroupRole.MANAGEMENT.toString()));
        accessGroupForParentOrganizationCreation.put("organization",
                accessGroupRepository.getCountryAccessGroupByOrgCategoryAndRole(countryId, OrganizationCategory.ORGANIZATION.toString(), AccessGroupRole.MANAGEMENT.toString()));
        accessGroupForParentOrganizationCreation.put("union",
                accessGroupRepository.getCountryAccessGroupByOrgCategoryAndRole(countryId, OrganizationCategory.UNION.toString(), AccessGroupRole.MANAGEMENT.toString()));
        return accessGroupForParentOrganizationCreation;
    }

    // Method to fetch list of access group by Organization category ( Hub, Organization and Union)
    public List<AccessGroupQueryResult> getOrganizationAccessGroupsForUnitCreation(Long organizationId) {
        return accessGroupRepository.getOrganizationAccessGroupByRole(organizationId, AccessGroupRole.MANAGEMENT.toString());
    }

    public UserAccessRoleDTO checkIfUserHasAccessByRoleInUnit(Long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parentOrganization = organizationService.fetchParentOrganization(unitId);
        UserAccessRoleDTO userAccessRoleDTO = new UserAccessRoleDTO(userId, unitId,
                accessGroupRepository.checkIfUserHasAccessByRoleInUnit(parentOrganization.getId(), unitId, AccessGroupRole.STAFF.toString()),
                accessGroupRepository.checkIfUserHasAccessByRoleInUnit(parentOrganization.getId(), unitId, AccessGroupRole.MANAGEMENT.toString())
        );
        return userAccessRoleDTO;
    }

    public UserAccessRoleDTO getStaffAccessRoles(Long unitId, Long staffId) {
        Organization parentOrganization = organizationService.fetchParentOrganization(unitId);
        UserAccessRoleDTO userAccessRoleDTO = new UserAccessRoleDTO(unitId,
                accessGroupRepository.getStaffAccessRoles(parentOrganization.getId(), unitId, AccessGroupRole.STAFF.toString(), staffId),
                accessGroupRepository.getStaffAccessRoles(parentOrganization.getId(), unitId, AccessGroupRole.MANAGEMENT.toString(), staffId), staffId
        );
        return userAccessRoleDTO;
    }
    public List<StaffIdsQueryResult> getStaffIdsByUnitIdAndAccessGroupId(Long unitId, List<Long> accessGroupId){
        return accessGroupRepository.getStaffIdsByUnitIdAndAccessGroupId(unitId,accessGroupId);
    }

    public StaffAccessGroupQueryResult getAccessGroupIdsByStaffIdAndUnitId(Long unitId){
        Long staffId=staffService.getStaffIdOfLoggedInUser(unitId);
        return accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId,unitId);
    }
}
