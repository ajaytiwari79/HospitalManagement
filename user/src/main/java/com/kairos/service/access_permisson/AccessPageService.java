package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.config.security.CurrentUserDetails;
import com.kairos.constants.AppConstants;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.auth.StaffPermissionQueryResult;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.model.staff.employment.EmploymentAccessPageRelation;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitEmpAccessRelationship;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.*;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentPageGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmpAccessGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.persistence.model.access_permission.AccessPageLanguageDTO;
import com.kairos.user.access_page.KPIAccessPageDTO;
import com.kairos.user.access_page.OrgCategoryTabAccessDTO;
import com.kairos.user.staff.permission.StaffPermissionDTO;
import com.kairos.user.staff.permission.StaffTabPermission;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.user_context.UserContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by prabjot on 3/1/17.
 */
@Transactional
@Service
public class AccessPageService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    AccessPageRepository accessPageRepository;
    @Inject
    AccessPermissionGraphRepository accessPermissionGraphRepository;
    @Inject
    EmploymentPageGraphRepository employmentPageGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private TreeStructureService treeStructureService;
    @Inject
    private AccessPageCustomIdRepository accessPageCustomIdRepository;
    @Inject
    UnitEmpAccessGraphRepository unitEmpAccessGraphRepository;
    @Inject
    UserGraphRepository userGraphRepository;
    @Inject private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private AccessPageLanguageRelationShipRepository accessPageLanguageRelationShipRepository;

    public synchronized AccessPage createAccessPage(AccessPageDTO accessPageDTO){
        AccessPage accessPage = new AccessPage(accessPageDTO.getName(),accessPageDTO.isModule(),
                getTabId(accessPageDTO.isModule()));
        if(Optional.ofNullable(accessPageDTO.getParentTabId()).isPresent()){
            AccessPage parentTab = accessPageRepository.findOne(accessPageDTO.getParentTabId());
            if(!Optional.ofNullable(parentTab).isPresent()){
                logger.error("Parent access page not found::id " + accessPageDTO.getParentTabId());
                exceptionService.dataNotFoundByIdException("message.dataNotFound","parentAccessPage",accessPageDTO.getParentTabId());

            }
            List<AccessPage> childTabs = parentTab.getSubPages();
            childTabs.add(accessPage);
            parentTab.setSubPages(childTabs);
            save(parentTab);
        } else {
            save(accessPage);
        }
        return accessPage;
    }

    public AccessPage updateAccessPage(Long accessPageId,AccessPageDTO accessPageDTO){
        AccessPage accessPage = (Optional.ofNullable(accessPageId).isPresent())?accessPageRepository.
                updateAccessTab(accessPageId,accessPageDTO.getName()): null;
        if(!Optional.ofNullable(accessPage).isPresent()){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","tab",accessPageId);

        }
        return accessPage;
    }

    public List<AccessPageDTO> getMainTabs(Long countryId){
        return accessPageRepository.getMainTabs(countryId);
    }

    public List<AccessPageDTO> getMainTabsForUnit(Long unitId){
        return accessPageRepository.getMainTabsForUnit(unitId);
    }

    public List<AccessPageDTO> getChildTabs(Long tabId, Long countryId){
        if( !Optional.ofNullable(tabId).isPresent() ){
            return Collections.emptyList();
        }
        return accessPageRepository.getChildTabs(tabId, countryId);
    }

    public Boolean updateStatus(boolean active,Long tabId){
        return (Optional.ofNullable(tabId).isPresent())?accessPageRepository.updateStatusOfAccessTabs(tabId,active):false;
    }

    public Boolean updateAccessForOrganizationCategory(Long countryId, Long tabId, OrgCategoryTabAccessDTO orgCategoryTabAccessDTO){
        if( !Optional.ofNullable(tabId).isPresent() ){
            return false;
        }

        Boolean isKairosHub = orgCategoryTabAccessDTO.getOrganizationCategory().equals(OrganizationCategory.HUB) ? true : false;
        Boolean isUnion = orgCategoryTabAccessDTO.getOrganizationCategory().equals(OrganizationCategory.UNION) ? true : false;

        if(orgCategoryTabAccessDTO.isAccessStatus()){
            accessGroupRepository.addAccessPageRelationshipForCountryAccessGroups(tabId, countryId,orgCategoryTabAccessDTO.getOrganizationCategory().toString() );
            accessGroupRepository.addAccessPageRelationshipForOrganizationAccessGroups(tabId, countryId, isKairosHub, isUnion);
        } else {
            accessGroupRepository.removeAccessPageRelationshipForCountryAccessGroup(tabId, countryId,orgCategoryTabAccessDTO.getOrganizationCategory().toString() );
            accessGroupRepository.removeAccessPageRelationshipForOrganizationAccessGroup(tabId, countryId, isKairosHub, isUnion);
        }
        return accessPageRepository.updateAccessStatusOfCountryByCategory(tabId, countryId, orgCategoryTabAccessDTO.getOrganizationCategory().toString(), orgCategoryTabAccessDTO.isAccessStatus());

    }

    public void createAccessPageByXml(Tab tab){

        List<AccessPage> accessPages = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Tab child : tab.getSubPages()){
            AccessPage accessPage = objectMapper.convertValue(child,AccessPage.class);
            accessPages.add(accessPage);
        }
        accessPageRepository.saveAll(accessPages);
        //setPermissionToAccessPage();
    }

    public void setPermissionToAccessPage(){
        List<AccessPermission> accessPermissions = accessPermissionGraphRepository.findAll();
        List<AccessPage> accessPages = (List<AccessPage> )accessPageRepository.findAll();

        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>(accessPages.size());
        for(AccessPermission accessPermission : accessPermissions){
            for (AccessPage accessPage : accessPages) {
                EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
                employmentAccessPageRelation.setRead(true);
                employmentAccessPageRelation.setWrite(false);
                employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
                employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
                employmentAccessPageRelations.add(employmentAccessPageRelation);
            }
        }
        employmentPageGraphRepository.saveAll(employmentAccessPageRelations);
    }

    public void setPagePermissionToStaff(AccessPermission accessPermission,long accessGroupId) {
        List<AccessPage> accessPages = accessGroupRepository.getAccessPageByGroup(accessGroupId);
        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>(accessPages.size());
        for (AccessPage accessPage : accessPages) {
            EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
            employmentAccessPageRelation.setRead(true);
            employmentAccessPageRelation.setWrite(true);
            employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
            employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
            employmentAccessPageRelations.add(employmentAccessPageRelation);
        }
        employmentPageGraphRepository.saveAll(employmentAccessPageRelations);
    }

    public void setPagePermissionToAdmin(AccessPermission accessPermission) {
        List<AccessPage> accessPages =(List<AccessPage>) accessPageRepository.findAll();
        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>(accessPages.size());
        for (AccessPage accessPage : accessPages) {
            EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation(accessPermission, accessPage);
            employmentAccessPageRelation.setRead(true);
            employmentAccessPageRelation.setWrite(true);
            employmentAccessPageRelation.setCreationDate(new DateTime().getMillis());
            employmentAccessPageRelation.setLastModificationDate(new DateTime().getMillis());
            employmentAccessPageRelations.add(employmentAccessPageRelation);
        }
        employmentPageGraphRepository.saveAll(employmentAccessPageRelations);
    }


    public AccessPage findByModuleId(String moduleId) {
        return accessPageRepository.findByModuleId(moduleId);

    }

    private synchronized String getTabId(Boolean isModule){

        Integer lastTabIdNumber = accessPageRepository.getLastTabOrModuleIdOfAccessPage(isModule);
        return (isModule ? AppConstants.MODULE_ID_PRFIX : AppConstants.TAB_ID_PRFIX)+(Optional.ofNullable(lastTabIdNumber).isPresent() ? String.valueOf(lastTabIdNumber+1) : "1");
    }

    /*private synchronized String getTabId(Boolean isModule){

        AccessPageCustomId accessPageCustomId = accessPageCustomIdRepository.findFirst();
        if(!Optional.ofNullable(accessPageCustomId).isPresent()){
            logger.error("AccessPageCustomId collection is not present");
            exceptionService.internalServerError("error.accessPage.customId.notPresent");

        }
        String content[];
        String tabId = null;
        if(isModule){
            content = accessPageCustomId.getModuleId().split("_");
            if(content.length>0){
                int id = Integer.parseInt(content[1]);
                id+=1;
                tabId = "module_" + id;
                accessPageCustomId.setModuleId(tabId);
            }
        } else {
            content = accessPageCustomId.getTabId().split("_");
            if(content.length>0){
                int id = Integer.parseInt(content[1]);
                id+=1;
                tabId = "tab_" + id;
                accessPageCustomId.setTabId(tabId);
            }
        }
        if(!Optional.ofNullable(tabId).isPresent()){
            exceptionService.internalServerError("error.tabId.notPresent");

        }
        save(accessPageCustomId);
        return tabId;
    }*/

    // TODO Uncomment and integrate
    /*public List<StaffPermissionDTO> getPermissionOfUserInUnit(Long organizationId, Long userId){
        List<StaffPermissionQueryResult> staffPermissions = accessPageRepository.getAccessPermissionOfUserForUnit(userId,organizationId);
        Map<Long,List<StaffPermissionQueryResult>> permissionByAccessGroup = staffPermissions.stream().collect(Collectors.groupingBy(StaffPermissionQueryResult::getAccessGroupIdOnEmploymentEnd));
        Set<Map.Entry<Long,List<StaffPermissionQueryResult>>> entries = permissionByAccessGroup.entrySet();
        Iterator<Map.Entry<Long,List<StaffPermissionQueryResult>>> iterator = entries.iterator();
        List<StaffPermissionQueryResult> allPermissions = new ArrayList<>();
        while (iterator.hasNext()){
            allPermissions.addAll(iterator.next().getValue());
        }
        return preparePermissionList(allPermissions);
    }*/

    public List<StaffPermissionDTO> getPermissionOfUserInUnit(Long userId){
        Boolean isCountryAdmin = userGraphRepository.checkIfUserIsCountryAdmin(userId, AppConstants.AG_COUNTRY_ADMIN);
        return (isCountryAdmin) ? getPermissionForHubMember() : null;
    }

    public List<StaffPermissionDTO> getPermissionOfUserInUnit(Long parentOrganizationId,Organization newUnit,Long userId){
        Organization parentOrganization = organizationGraphRepository.findOne(parentOrganizationId);
        if(isHubMember(userId)){
            return getPermissionForHubMember();
        }
        List<StaffPermissionQueryResult> staffPermissions = accessPageRepository.getAccessPermissionOfUserForUnit(userId,parentOrganizationId);
        Map<Long,List<StaffPermissionQueryResult>> permissionByAccessGroup = staffPermissions.stream().collect(Collectors.groupingBy(StaffPermissionQueryResult::getAccessGroupId));
        Set<Map.Entry<Long,List<StaffPermissionQueryResult>>> entries = permissionByAccessGroup.entrySet();
        Iterator<Map.Entry<Long,List<StaffPermissionQueryResult>>> iterator = entries.iterator();
        List<StaffPermissionQueryResult> allPermissions = new ArrayList<>();
        while (iterator.hasNext()){
            allPermissions.addAll(iterator.next().getValue());
        }
//        createEmploymentWithNewOrganization(newUnit,userId,permissionByAccessGroup,parentOrganizationId);
        return preparePermissionList(allPermissions);
    }

    private List<StaffPermissionDTO> preparePermissionList(List<StaffPermissionQueryResult> permissionsOfAllRole){
        List<StaffPermissionDTO> permissions = new ArrayList<>();
        List<String> processedModuleIds = new ArrayList<>();
        for(StaffPermissionQueryResult staffPermission : permissionsOfAllRole){
            if(!processedModuleIds.contains(staffPermission.getModuleId())){
                List<StaffPermissionQueryResult> modules = permissionsOfAllRole.stream().filter(module->module.getModuleId().equals(
                        staffPermission.getModuleId())).collect(Collectors.toList());
                permissions.add(getUnionOfPermissions(modules));
                processedModuleIds.add(staffPermission.getModuleId());
            }
        }
        return permissions;
    }

    private StaffPermissionDTO getUnionOfPermissions(List<StaffPermissionQueryResult> modules){
        StaffPermissionDTO moduleToReturn = null;
        List<Map<String,Object>> tabPermissions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(StaffPermissionQueryResult staffPermission : modules){
            if(!Optional.ofNullable(moduleToReturn).isPresent()){
                moduleToReturn = objectMapper.convertValue(staffPermission,StaffPermissionDTO.class);
            } else if(staffPermission.isWrite() || (staffPermission.isRead() && !moduleToReturn.isRead())){
                moduleToReturn = objectMapper.convertValue(staffPermission,StaffPermissionDTO.class);;
            }
            tabPermissions.addAll(staffPermission.getTabPermissions());
        }
        moduleToReturn.setTabPermissions(getUnionOfTabPermission(tabPermissions));
        return moduleToReturn;
    }

    private List<StaffTabPermission> getUnionOfTabPermission(List<Map<String,Object>> staffTabPermissions){
        Map<String,StaffTabPermission> tabPermissionToProceed = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Map<String,Object> tabPermission : staffTabPermissions){
            StaffTabPermission staffTabPermission = objectMapper.convertValue(tabPermission,StaffTabPermission.class);
            if(!tabPermissionToProceed.containsKey(staffTabPermission.getModuleId())){
                tabPermissionToProceed.put(staffTabPermission.getModuleId(),staffTabPermission);
            } else if(staffTabPermission.isWrite() || (staffTabPermission.isRead() && !tabPermissionToProceed.get(staffTabPermission.getId()).isRead())){
                tabPermissionToProceed.put(staffTabPermission.getModuleId(),staffTabPermission);
            }
        }
        return tabPermissionToProceed.values().stream().collect(Collectors.toList());
    }

    private void createEmploymentWithNewOrganization(Organization organization,Long userId,
                                                     Map<Long,List<StaffPermissionQueryResult>> accessPermissionByGroup,Long parentOrganizationId){

        Staff staff;
        Employment employment;
        if(organization.isParentOrganization()){
            CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
            staff = new Staff();
            staff.setFirstName(currentUserDetails.getFirstName());
            staff.setLastName(currentUserDetails.getLastName());
            staff.setEmail(currentUserDetails.getEmail());
            employment = new Employment();
            employment.setStaff(staff);
            User user = userGraphRepository.findOne(userId);
            staff.setUser(user);
        } else {
            staff = staffGraphRepository.getStaffByUserId(userId,parentOrganizationId);
            employment = employmentGraphRepository.findEmployment(parentOrganizationId,staff.getId());
        }
        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(organization);
        employment.getUnitPermissions().add(unitPermission);

        Set<Map.Entry<Long,List<StaffPermissionQueryResult>>> entries = accessPermissionByGroup.entrySet();
        Iterator<Map.Entry<Long,List<StaffPermissionQueryResult>>> iterator = entries.iterator();
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmploymentAccessPageRelation> employmentAccessPageRelations = new ArrayList<>();
        List<UnitEmpAccessRelationship> unitEmpAccessRelationships = new ArrayList<>();
        while (iterator.hasNext()){
            Map.Entry<Long,List<StaffPermissionQueryResult>> permissionByAccessGroup = iterator.next();
            AccessGroup accessGroup = accessGroupRepository.findOne(permissionByAccessGroup.getKey());
            AccessPermission accessPermission = new AccessPermission(accessGroup);
            for(StaffPermissionQueryResult staffPermissionQueryResult : permissionByAccessGroup.getValue()){
                AccessPage accessPage = objectMapper.convertValue(staffPermissionQueryResult,AccessPage.class);
                accessPage.setModule(staffPermissionQueryResult.isModule());
                EmploymentAccessPageRelation employmentAccessPageRelation = new EmploymentAccessPageRelation
                        (accessPermission,accessPage,staffPermissionQueryResult.isRead(),staffPermissionQueryResult.isWrite());
                employmentAccessPageRelations.add(employmentAccessPageRelation);
                for(Map<String,Object> staffTabPermission : staffPermissionQueryResult.getTabPermissions()){
                    AccessPage subPage = objectMapper.convertValue(staffTabPermission,AccessPage.class);
                    subPage.setModule(false);
                    EmploymentAccessPageRelation employmentAccessSubPageRelation = new EmploymentAccessPageRelation(accessPermission,subPage,
                            staffPermissionQueryResult.isRead(),staffPermissionQueryResult.isWrite());
                    employmentAccessPageRelations.add(employmentAccessSubPageRelation);
                }
            }
            UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitPermission,accessPermission);
            unitEmpAccessRelationships.add(unitEmpAccessRelationship);
        }
        if(organization.isParentOrganization()){
            organization.getEmployments().add(employment);
            save(organization);
        } else {
            employment.getUnitPermissions().add(unitPermission);
            save(employment);
        }
        unitEmpAccessGraphRepository.saveAll(unitEmpAccessRelationships);
        employmentPageGraphRepository.saveAll(employmentAccessPageRelations);
    }

    private List<StaffPermissionDTO> getPermissionForHubMember(){
        List<StaffPermissionQueryResult> staffPermissionQueryResults = accessPageRepository.getTabsPermissionForHubUserForUnit();
        ObjectMapper objectMapper = new ObjectMapper();
        return staffPermissionQueryResults.parallelStream().map(staffPermissionQueryResult-> objectMapper.
                convertValue(staffPermissionQueryResult,StaffPermissionDTO.class)).collect(Collectors.toList());
    }

    public boolean isHubMember(Long userId){
        Boolean hubMember = accessPageRepository.isHubMember(userId);
        if(hubMember instanceof Boolean){
            return hubMember;
        }
        return false;
    }


    // For Test Cases
    public AccessPage getOneMainModule(){
        return accessPageRepository.getOneMainModule();
    }

    public List<KPIAccessPageDTO> getKPIAccessPageList(String moduleId){
        List<AccessPage> accessPages = accessPageRepository.getKPITabsList(moduleId);
        List<KPIAccessPageDTO> kpiTabs = ObjectMapperUtils.copyPropertiesOfListByMapper(accessPages, KPIAccessPageDTO.class);
        return kpiTabs;
    }

    public AccessPageLanguageDTO assignLanguageToAccessPage(String moduleId, AccessPageLanguageDTO accessPageLanguageDTO){
        if(Optional.ofNullable(accessPageLanguageDTO.getId()).isPresent()){
            Optional<AccessPageLanguageRelationShip> accessPageLanguageRelationShip= accessPageLanguageRelationShipRepository.findById(accessPageLanguageDTO.getId());
            if(!accessPageLanguageRelationShip.isPresent()){
                exceptionService.dataNotFoundByIdException("access_page.lang.description.absent",accessPageLanguageDTO.getLanguageId());
            }
            accessPageLanguageRelationShip.get().setDescription(accessPageLanguageDTO.getDescription());
            save(accessPageLanguageRelationShip.get());
            return accessPageLanguageDTO;
        }

        AccessPage accessPage=accessPageRepository.findByModuleId(moduleId);
        if(!Optional.ofNullable(accessPage).isPresent()){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","Access Page",moduleId);
        }
        SystemLanguage systemLanguage=systemLanguageGraphRepository.findSystemLanguageById(accessPageLanguageDTO.getLanguageId());
        if(!Optional.ofNullable(systemLanguage).isPresent()){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","SystemLanguage", accessPageLanguageDTO.getLanguageId());
        }

        AccessPageLanguageRelationShip accessPageLanguageRelationShip=new AccessPageLanguageRelationShip(accessPageLanguageDTO.getId(),accessPage,systemLanguage, accessPageLanguageDTO.getDescription());
        save(accessPageLanguageRelationShip);
        accessPageLanguageDTO.setId(accessPageLanguageRelationShip.getId());
        return accessPageLanguageDTO;

    }

    public AccessPageLanguageDTO getLanguageDataByModuleId(String moduleId, Long languageId){
        return accessPageRepository.findLanguageSpecificDataByModuleIdAndLanguageId(moduleId,languageId);
    }
}
