package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.access_group.CountryAccessGroupDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.AccessGroupDTO;
import com.kairos.dto.user.organization.OrganizationCategoryDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.CountryAccessGroupRelationship;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.country.default_data.account_type.AccountTypeAccessGroupCountQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.access_permission.AccessGroupsByCategoryDTO;
import com.kairos.persistence.model.user.counter.StaffIdsQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.*;
import com.kairos.persistence.repository.user.country.*;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.SUPER_ADMIN;
import static com.kairos.constants.UserMessagesConstants.*;


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
    @Inject
    private StaffService staffService;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;


    public AccessGroupDTO createAccessGroup(long organizationId, AccessGroupDTO accessGroupDTO) {
        validateDayTypes(accessGroupDTO.isAllowedDayTypes(), accessGroupDTO.getDayTypeIds());
        if (accessGroupDTO.getEndDate() != null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
        }
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isOrganizationAccessGroupExistWithName(organizationId, accessGroupDTO.getName().trim());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, ACCESS_GROUP, accessGroupDTO.getName());

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
        List<DayType> dayTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accessGroupDTO.getDayTypeIds())) {
            dayTypes = dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        }
        AccessGroup accessGroup = ObjectMapperUtils.copyPropertiesByMapper(accessGroupDTO, AccessGroup.class);
        accessGroup.setDayTypes(dayTypes);

        if (parent == null) {
            organization.getAccessGroups().add(accessGroup);
            organizationGraphRepository.save(organization, 2);

            //set default permission of access page while creating access group
            Long countryId = organizationService.getCountryIdOfOrganization(organization.getId());
            setAccessPageRelationshipWithAccessGroupByOrgCategory(countryId, accessGroup.getId(), organizationService.getOrganizationCategory(organization.getCompanyType()));
            accessGroupDTO.setId(accessGroup.getId());
            return accessGroupDTO;
        } else {
            exceptionService.actionNotPermittedException(MESSAGE_PERMITTED, ACCESS_GROUP);

        }
        return null;
    }

    public AccessGroupDTO updateAccessGroup(long accessGroupId, Long unitId, AccessGroupDTO accessGroupDTO) {
        validateDayTypes(accessGroupDTO.isAllowedDayTypes(), accessGroupDTO.getDayTypeIds());

        if (accessGroupDTO.getEndDate() != null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
        }
        AccessGroup accessGrpToUpdate = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGrpToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, accessGroupId);

        }
        if (accessGroupRepository.isOrganizationAccessGroupExistWithNameExceptId(unitId, accessGroupDTO.getName(), accessGroupId)) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, ACCESS_GROUP, accessGroupDTO.getName());

        }
        List<DayType> dayTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accessGroupDTO.getDayTypeIds())) {
            dayTypes = dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        }
        accessGrpToUpdate.setName(accessGroupDTO.getName());
        accessGrpToUpdate.setRole(accessGroupDTO.getRole());
        accessGrpToUpdate.setDescription(accessGroupDTO.getDescription());
        accessGrpToUpdate.setEnabled(accessGroupDTO.isEnabled());
        accessGrpToUpdate.setStartDate(accessGroupDTO.getStartDate());
        accessGrpToUpdate.setEndDate(accessGroupDTO.getEndDate());
        accessGrpToUpdate.setDayTypes(dayTypes);
        accessGrpToUpdate.setAllowedDayTypes(accessGroupDTO.isAllowedDayTypes());
        accessGroupRepository.save(accessGrpToUpdate);
        accessGroupDTO.setId(accessGrpToUpdate.getId());
        return accessGroupDTO;
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
            List<AccessGroupQueryResult> countryAccessGroups = accessGroupRepository.getCountryAccessGroupByCategory(countryId, organizationService.getOrganizationCategory(organization.getCompanyType()).toString());
            accessGroupList = new ArrayList<>(countryAccessGroups.size());
            for (AccessGroupQueryResult countryAccessGroup : countryAccessGroups) {
                AccessGroup accessGroup = new AccessGroup(countryAccessGroup.getName(), countryAccessGroup.getDescription(), countryAccessGroup.getRole(), countryAccessGroup.getDayTypes(), countryAccessGroup.getStartDate(), countryAccessGroup.getEndDate());
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
                if (accessGroup.getName().equals(SUPER_ADMIN)) {
                    accessGroups.remove(accessGroup);
                }
            }
            organization.setAccessGroups(accessGroups);
        }
        organizationGraphRepository.save(organization);
        return countryAndOrgAccessGroupIdsMap;
    }

    public void removeDefaultCopiedAccessGroup(List<Long> organizationIds) {
        accessGroupRepository.removeDefaultCopiedAccessGroup(organizationIds);
    }

    public void createDefaultAccessGroups(Organization parentOrg, List<Organization> organizations) {
        List<AccessGroupQueryResult> accessGroupList = accountTypeGraphRepository.getAccessGroupsByAccountTypeId(parentOrg.getAccountType().getId());
        createDefaultAccessGroupsInOrganization(parentOrg, accessGroupList, true);
        organizations.forEach(org -> {
            createDefaultAccessGroupsInOrganization(org, accessGroupList, false);
        });
    }

    /**
     * @param organization,accountTypeId
     * @author vipul
     * this method will create accessgroup to the organization
     * @Extra Need to optimize
     */
    public Map<Long, Long> createDefaultAccessGroupsInOrganization(Organization organization, List<AccessGroupQueryResult> accessGroupList, boolean company) {


        Map<Long, Long> countryAndOrgAccessGroupIdsMap = new LinkedHashMap<>();

        List<AccessGroup> newAccessGroupList = new ArrayList<>(accessGroupList.size());
        for (AccessGroupQueryResult currentAccessGroup : accessGroupList) {
            AccessGroup parent = new AccessGroup(currentAccessGroup.getName(), currentAccessGroup.getDescription(), currentAccessGroup.getRole(), currentAccessGroup.getDayTypes(), company ? DateUtils.getCurrentLocalDate() : currentAccessGroup.getStartDate(), currentAccessGroup.getEndDate());
            parent.setId(currentAccessGroup.getId());
            AccessGroup accessGroup = new AccessGroup(currentAccessGroup.getName(), currentAccessGroup.getDescription(), currentAccessGroup.getRole(), currentAccessGroup.getDayTypes(), company ? DateUtils.getCurrentLocalDate() : currentAccessGroup.getStartDate(), currentAccessGroup.getEndDate());
            accessGroup.setParentAccessGroup(parent);
            accessGroup.setLastModificationDate(accessGroup.getCreationDate());
            countryAndOrgAccessGroupIdsMap.put(currentAccessGroup.getId(), null);
            newAccessGroupList.add(accessGroup);
        }
        accessGroupRepository.saveAll(newAccessGroupList);
        AtomicInteger counter = new AtomicInteger(0);
        countryAndOrgAccessGroupIdsMap.forEach((k, v) -> {
            countryAndOrgAccessGroupIdsMap.put(k, newAccessGroupList.get(counter.get()).getId());
            // TODO PAVAN vipul remove this looped and use below when parent id is set to acccess group
            accessGroupRepository.setAccessPagePermissionForAccessGroup(k, newAccessGroupList.get(counter.get()).getId());
            // increment counter
            counter.addAndGet(1);
        });
        // DONT remove
        //List<Long> organizationAccessGroupIds = newAccessGroupList.stream().map(AccessGroup::getId).collect(Collectors.toList());
        //List<Long> countryAccessGroupIds = newAccessGroupList.stream().map(AccessGroup::getId).collect(Collectors.toList());
        //accessGroupRepository.setAccessPagePermissionForAccessGroup(countryAccessGroupIds, organizationAccessGroupIds);
        organization.setAccessGroups(newAccessGroupList);
        organizationGraphRepository.save(organization);
        return countryAndOrgAccessGroupIdsMap;
    }

    public List<AccessGroupQueryResult> getAccessGroupsForUnit(long organizationId) {
        Organization unit = organizationGraphRepository.findOne(organizationId, 0);
        if (!unit.isParentOrganization()) {
            unit = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        return accessGroupRepository.getAccessGroupsForUnit(unit.getId());
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

    public List<AccessPageQueryResult> getAccessPageHierarchy(long accessGroupId, Long countryId) {
        // Check if access group is of country
        if (Optional.ofNullable(countryId).isPresent()) {
            AccessGroup accessGroup = accessGroupRepository.findCountryAccessGroupById(accessGroupId, countryId);
            if (Optional.ofNullable(accessGroup).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, accessGroupId);

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
                exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, accessGroupId);

            }
        }
        long creationDate = DateUtils.getCurrentDate().getTime();
        long lastModificationDate = DateUtils.getCurrentDate().getTime();
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
            exceptionService.internalServerError(ERROR_UNIT_NOTNULL);
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
        }

        Long parentTabId = accessPageRepository.getParentTab(accessPermissionDTO.getPageId());
        if (!Optional.ofNullable(parentTabId).isPresent()) {
            return;
        } else {
            updateReadWritePermissionOfParentTab(accessGroupId, read, write, accessPermissionDTO.getPageId(),
                    (!Optional.ofNullable(parent).isPresent() ? unit.getId() : parent.getId()), unit.getId(), accessPermissionDTO.getStaffId());
        }
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


    public CountryAccessGroupDTO createCountryAccessGroup(long countryId, CountryAccessGroupDTO accessGroupDTO) {
        validateDayTypes(accessGroupDTO.isAllowedDayTypes(), accessGroupDTO.getDayTypeIds());
        if (accessGroupDTO.getEndDate() != null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
        }
        if (OrganizationCategory.ORGANIZATION.equals(accessGroupDTO.getOrganizationCategory()) && accessGroupDTO.getAccountTypeIds().isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_ACCOUNTTYPE_SELECT);
        }
        List<AccountType> accountType = accountTypeGraphRepository.getAllAccountTypeByIds(accessGroupDTO.getAccountTypeIds());
        if (accountType.size() != accessGroupDTO.getAccountTypeIds().size()) {
            exceptionService.dataNotMatchedException(MESSAGE_ACCOUNTTYPE_NOTFOUND);
        }
        List<DayType> dayTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accessGroupDTO.getDayTypeIds())) {
            dayTypes = dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        }

        Country country = countryGraphRepository.findOne(countryId);

        Boolean isAccessGroupExistWithSameName;
        if ("Organization".equals(accessGroupDTO.getOrganizationCategory().value)) {
            isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithName(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString(), accessGroupDTO.getAccountTypeIds());

        } else {
            isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithName(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString());
        }

        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, ACCESS_GROUP, accessGroupDTO.getName());

        }

        AccessGroup accessGroup = OrganizationCategory.ORGANIZATION.equals(accessGroupDTO.getOrganizationCategory()) ? new AccessGroup(accessGroupDTO.getName().trim(), accessGroupDTO.getDescription(), accessGroupDTO.getRole(), accountType, dayTypes, accessGroupDTO.getStartDate(), accessGroupDTO.getEndDate()) : new AccessGroup(accessGroupDTO.getName().trim(), accessGroupDTO.getDescription(), accessGroupDTO.getRole(), dayTypes, accessGroupDTO.getStartDate(), accessGroupDTO.getEndDate());

        CountryAccessGroupRelationship accessGroupRelationship = new CountryAccessGroupRelationship(country, accessGroup, accessGroupDTO.getOrganizationCategory());
        countryAccessGroupRelationshipRepository.save(accessGroupRelationship);
        countryGraphRepository.save(country);
        //set default permission of access page while creating access group
        setAccessPageRelationshipWithAccessGroupByOrgCategory(countryId, accessGroup.getId(), accessGroupDTO.getOrganizationCategory());
        accessGroupDTO.setId(accessGroup.getId());
        accessGroupDTO.setEnabled(true);
        return accessGroupDTO;
    }

    public AccessGroup updateCountryAccessGroup(long countryId, Long accessGroupId, CountryAccessGroupDTO accessGroupDTO) {
        validateDayTypes(accessGroupDTO.isAllowedDayTypes(), accessGroupDTO.getDayTypeIds());
        if (accessGroupDTO.getEndDate() != null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
        }
        Optional<AccessGroup> accessGrpToUpdate = accessGroupRepository.findById(accessGroupId);
        if (!accessGrpToUpdate.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, accessGroupId);

        }
        Boolean isAccessGroupExistWithSameName;
        if (OrganizationCategory.ORGANIZATION.equals(accessGroupDTO.getOrganizationCategory())) {
            isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithNameExceptId(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString(), accessGroupId, accessGroupDTO.getAccountTypeIds());
        } else {
            isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithNameExceptId(countryId, accessGroupDTO.getName(), accessGroupDTO.getOrganizationCategory().toString(), accessGroupId);
        }
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, ACCESS_GROUP, accessGroupDTO.getName());
        }
        List<DayType> dayTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accessGroupDTO.getDayTypeIds())) {
            dayTypes = dayTypeGraphRepository.getDayTypes(accessGroupDTO.getDayTypeIds());
        }
        accessGrpToUpdate.get().setName(accessGroupDTO.getName());
        accessGrpToUpdate.get().setDescription(accessGroupDTO.getDescription());
        accessGrpToUpdate.get().setRole(accessGroupDTO.getRole());
        accessGrpToUpdate.get().setEnabled(accessGroupDTO.isEnabled());
        accessGrpToUpdate.get().setStartDate(accessGroupDTO.getStartDate());
        accessGrpToUpdate.get().setEndDate(accessGroupDTO.getEndDate());
        accessGrpToUpdate.get().setDayTypes(dayTypes);
        accessGrpToUpdate.get().setAllowedDayTypes(accessGroupDTO.isAllowedDayTypes());
        accessGroupRepository.save(accessGrpToUpdate.get());
        return accessGrpToUpdate.get();
    }

    public boolean deleteCountryAccessGroup(long accessGroupId) {
        AccessGroup accessGroupToDelete = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroupToDelete).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, accessGroupId);

        }
        accessGroupToDelete.setDeleted(true);
        accessGroupRepository.save(accessGroupToDelete);
        return true;
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
    public List<AccessGroupQueryResult> getCountryAccessGroupByAccountTypeId(Long countryId, Long accountTypeId, String accessGroupRole) {
        List<String> accessGroupRoles = isNotNull(accessGroupRole) ? Arrays.asList(accessGroupRole) : Arrays.asList(AccessGroupRole.MANAGEMENT.toString(), AccessGroupRole.STAFF.toString());
        return accessGroupRepository.getCountryAccessGroupByAccountTypeId(countryId, accountTypeId, accessGroupRoles);
    }

    public List<AccessGroupQueryResult> getCountryAccessGroups(Long countryId, OrganizationCategory organizationCategory) {

        return accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, organizationCategory.toString());
    }

    public List<AccessGroupsByCategoryDTO> getCountryAccessGroupsOfAllCategories(Long countryId) {

        List<AccessGroupsByCategoryDTO> accessGroupsData = new ArrayList<>();
        accessGroupsData.add(new AccessGroupsByCategoryDTO(OrganizationCategory.HUB,
                accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, OrganizationCategory.HUB.toString())));

        accessGroupsData.add(new AccessGroupsByCategoryDTO(OrganizationCategory.ORGANIZATION,
                accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, OrganizationCategory.ORGANIZATION.toString())));

        accessGroupsData.add(new AccessGroupsByCategoryDTO(OrganizationCategory.UNION,
                accessGroupRepository.getCountryAccessGroupByOrgCategory(countryId, OrganizationCategory.UNION.toString())));
        return accessGroupsData;
    }

    /***** Access group - COUNTRY LEVEL - ENDS HERE ******************/

    public AccessGroupDTO copyUnitAccessGroup(long organizationId, AccessGroupDTO accessGroupDTO) {
        validateDayTypes(accessGroupDTO.isAllowedDayTypes(), accessGroupDTO.getDayTypeIds());
        if (accessGroupDTO.getEndDate() != null && accessGroupDTO.getEndDate().isBefore(accessGroupDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
        }
        Optional<Organization> organization = organizationGraphRepository.findById(organizationId);
        if (!organization.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, organizationId);

        }
        Organization parent;
        if (organization.get().getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.get().getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(organization.get().getId());
        }
        if (Optional.ofNullable(parent).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_ACCESSGROUP_COPIED);

        }
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isOrganizationAccessGroupExistWithName(organizationId, accessGroupDTO.getName().trim());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, ACCESS_GROUP, accessGroupDTO.getName().trim());

        }
        AccessGroupQueryResult currentAccessGroup = accessGroupRepository.findByAccessGroupId(organizationId, accessGroupDTO.getId());
        if (currentAccessGroup == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, accessGroupDTO.getId());

        }
        AccessGroup accessGroup = new AccessGroup(accessGroupDTO.getName().trim(), accessGroupDTO.getDescription(), accessGroupDTO.getRole(), currentAccessGroup.getDayTypes(), currentAccessGroup.getStartDate(), currentAccessGroup.getEndDate());
        accessGroupRepository.save(accessGroup);
        organization.get().getAccessGroups().add(accessGroup);
        organizationGraphRepository.save(organization.get());
        accessPageRepository.copyAccessGroupPageRelationShips(accessGroupDTO.getId(), accessGroup.getId());
        accessGroupDTO.setId(accessGroup.getId());
        return accessGroupDTO;

    }

    public CountryAccessGroupDTO copyCountryAccessGroup(long countryId, CountryAccessGroupDTO countryAccessGroupDTO) {
        validateDayTypes(countryAccessGroupDTO.isAllowedDayTypes(), countryAccessGroupDTO.getDayTypeIds());
        if (countryAccessGroupDTO.getEndDate() != null && countryAccessGroupDTO.getEndDate().isBefore(countryAccessGroupDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
        }
        Optional<Country> country = countryGraphRepository.findById(countryId);
        if (!country.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        Boolean isAccessGroupExistWithSameName = accessGroupRepository.isCountryAccessGroupExistWithName(countryId, countryAccessGroupDTO.getName().trim(), countryAccessGroupDTO.getOrganizationCategory().toString());
        if (isAccessGroupExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, ACCESS_GROUP, countryAccessGroupDTO.getName().trim());

        }
        Optional<AccessGroup> currentAccessGroup = accessGroupRepository.findById(countryAccessGroupDTO.getId());
        if (!currentAccessGroup.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ACESSGROUPID_INCORRECT, countryAccessGroupDTO.getId());

        }
        AccessGroup accessGroup = new AccessGroup(countryAccessGroupDTO.getName().trim(), countryAccessGroupDTO.getDescription(), currentAccessGroup.get().getRole(), currentAccessGroup.get().getAccountType(), currentAccessGroup.get().getDayTypes(), currentAccessGroup.get().getStartDate(), currentAccessGroup.get().getEndDate());

        CountryAccessGroupRelationship accessGroupRelationship = new CountryAccessGroupRelationship(country.get(), accessGroup, countryAccessGroupDTO.getOrganizationCategory());
        countryAccessGroupRelationshipRepository.save(accessGroupRelationship);
        countryGraphRepository.save(country.get());
        accessPageRepository.copyAccessGroupPageRelationShips(countryAccessGroupDTO.getId(), accessGroup.getId());
        countryAccessGroupDTO.setId(accessGroup.getId());
        return countryAccessGroupDTO;
    }

    // Method to fetch list of access group by Organization category ( Hub, Organization and Union)
    //TODO all three db calls can be combined in one
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

    // Method to fetch list of Management access group of Organization
    public List<AccessGroupQueryResult> getOrganizationManagementAccessGroups(Long organizationId, AccessGroupRole role) {
        return accessGroupRepository.getOrganizationAccessGroupByRole(organizationId, role.toString());
    }


    public UserAccessRoleDTO findUserAccessRole(Long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        //Todo Yatharth please check and verify our code
        Staff staffAtHub = staffGraphRepository.getStaffByOrganizationHub(unitId, userId);
        UserAccessRoleDTO userAccessRoleDTO;
        if (staffAtHub != null) {
            userAccessRoleDTO = new UserAccessRoleDTO(userId, unitId, false, true);
        } else {
            AccessGroupStaffQueryResult accessGroupQueryResult = accessGroupRepository.getAccessGroupDayTypesAndUserId(unitId, userId);
            if (accessGroupQueryResult == null) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFF_INVALID_UNIT);
            }
            String staffRole = staffRetrievalService.getStaffAccessRole(accessGroupQueryResult);
            boolean staff = AccessGroupRole.STAFF.name().equals(staffRole);
            boolean management = AccessGroupRole.MANAGEMENT.name().equals(staffRole);
            userAccessRoleDTO = new UserAccessRoleDTO(userId, unitId, staff, management);
            userAccessRoleDTO.setStaffId(accessGroupQueryResult.getStaffId());
        }
        //Todo till here
        return userAccessRoleDTO;
    }

    public UserAccessRoleDTO findStaffAccessRole(Long unitId, Long staffId) {
        AccessGroupStaffQueryResult accessGroupQueryResult = accessGroupRepository.getAccessGroupDayTypesAndStaffId(unitId, staffId);
        if (accessGroupQueryResult == null) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_INVALID_UNIT);
        }
        String staffRole = staffRetrievalService.getStaffAccessRole(accessGroupQueryResult);
        boolean staff = AccessGroupRole.STAFF.name().equals(staffRole);
        boolean management = AccessGroupRole.MANAGEMENT.name().equals(staffRole);
        return new UserAccessRoleDTO(unitId, staff, management, staffId);
    }

    public ReasonCodeWrapper getAbsenceReasonCodesAndAccessRole(Long unitId) {
        UserAccessRoleDTO userAccessRoleDTO = findUserAccessRole(unitId);
        List<ReasonCodeDTO> reasonCodes = ObjectMapperUtils.copyPropertiesOfListByMapper(reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(unitId, ReasonCodeType.TIME_TYPE), ReasonCodeDTO.class);

        return new ReasonCodeWrapper(reasonCodes, userAccessRoleDTO);
    }


    public List<StaffIdsQueryResult> getStaffIdsByUnitIdAndAccessGroupId(Long unitId, List<Long> accessGroupId) {
        return accessGroupRepository.getStaffIdsByUnitIdAndAccessGroupId(unitId, accessGroupId);
    }


    public List<StaffAccessGroupDTO> getStaffAndAccessGroupsByUnitId(Long unitId, List<Long> accessGroupId) {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(accessGroupRepository.getStaffIdsAndAccessGroupsByUnitId(unitId, accessGroupId), StaffAccessGroupDTO.class);
    }

    public StaffAccessGroupQueryResult getAccessGroupIdsByStaffIdAndUnitId(Long unitId) {
        Long staffId = staffRetrievalService.getStaffIdOfLoggedInUser(unitId);
        return accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, unitId);

    }

    public Map<Long, Long> getAccessGroupUsingParentId(Long unitId, Set<Long> accessGroupIds) {
        List<AccessPageQueryResult> accessPageQueryResults = accessGroupRepository.findAllAccessGroupWithParentIds(unitId, accessGroupIds);
        return convertToMap(accessPageQueryResults);
    }

    public Map<Long, Long> findAllAccessGroupWithParentOfOrganization(Long organizationId) {
        List<AccessPageQueryResult> accessPageQueryResults = accessGroupRepository.findAllAccessGroupWithParentOfOrganization(organizationId);
        return convertToMap(accessPageQueryResults);
    }

    private Map<Long, Long> convertToMap(List<AccessPageQueryResult> accessPageQueryResults) {
        Map<Long, Long> response = new HashMap<>();
        accessPageQueryResults.forEach(accessPageQueryResult -> {
            response.put(accessPageQueryResult.getParentId(), accessPageQueryResult.getId());
        });
        return response;
    }

    private void validateDayTypes(boolean allowedDayTypes, Set<Long> dayTypeIds) {
        if ((allowedDayTypes && CollectionUtils.isEmpty(dayTypeIds))) {
            exceptionService.actionNotPermittedException(ERROR_DAY_TYPE_ABSENT);
        } else if ((!allowedDayTypes && CollectionUtils.isNotEmpty(dayTypeIds))) {
            exceptionService.actionNotPermittedException(ERROR_ALLOWED_DAY_TYPE_ABSENT);
        }


    }

    public void linkParentOrganizationAccessGroup(Organization unit, Long parentOrganizationId) {
        List<AccessGroupQueryResult> accessGroupQueryResults = getOrganizationAccessGroups(parentOrganizationId);
        List<AccessGroup> accessGroupList = ObjectMapperUtils.copyPropertiesOfListByMapper(accessGroupQueryResults, AccessGroup.class);
        unit.setAccessGroups(accessGroupList);
        accessGroupRepository.saveAll(accessGroupList);

    }

    private List<AccessGroupQueryResult> getOrganizationAccessGroups(Long parentOrganizationId) {
        return accessGroupRepository.getAccessGroupsForUnit(parentOrganizationId);
    }
}
