package com.kairos.service.access_permisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.access_page.OrgCategoryTabAccessDTO;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.position.AccessPermissionAccessPageRelation;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageLanguageRelationShipRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentPageGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.UserMessagesConstants.*;


/**
 * Created by prabjot on 3/1/17.
 */
@Transactional
@Service
public class AccessPageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPageService.class);

    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private AccessPermissionGraphRepository accessPermissionGraphRepository;
    @Inject
    private EmploymentPageGraphRepository employmentPageGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
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
                LOGGER.error("Parent access page not found::id " + accessPageDTO.getParentTabId());
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,PARENTACCESSPAGE,accessPageDTO.getParentTabId());
            }
            List<AccessPage> childTabs = parentTab.getSubPages();
            childTabs.add(accessPage);
            parentTab.setSubPages(childTabs);
            accessPageRepository.save(parentTab);
        } else {
            accessPageRepository.save(accessPage);
        }
        return accessPage;
    }

    public AccessPage updateAccessPage(Long accessPageId,AccessPageDTO accessPageDTO){
        AccessPage accessPage = (Optional.ofNullable(accessPageId).isPresent())?accessPageRepository.
                updateAccessTab(accessPageId,accessPageDTO.getName()): null;
        if(!Optional.ofNullable(accessPage).isPresent()){
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,TAB,accessPageId);

        }
        return accessPage;
    }

    public List<AccessPageDTO> getMainTabs(){
        return accessPageRepository.getMainTabs();
    }

    public List<AccessPageDTO> getMainTabsForUnit(Long unitId){
        return accessPageRepository.getMainTabsForUnit(unitId);
    }

    public List<AccessPageDTO> getChildTabs(Long tabId){
        if( !Optional.ofNullable(tabId).isPresent() ){
            return Collections.emptyList();
        }
        return accessPageRepository.getChildTabs(tabId);
    }

    public Boolean updateStatus(boolean active,Long tabId){
        return (Optional.ofNullable(tabId).isPresent())?accessPageRepository.updateStatusOfAccessTabs(tabId,active):false;
    }

    public Boolean updateAccessForOrganizationCategory(Long tabId, OrgCategoryTabAccessDTO orgCategoryTabAccessDTO){
        if( !Optional.ofNullable(tabId).isPresent() ){
            return false;
        }

        Boolean isKairosHub = OrganizationCategory.HUB.equals(orgCategoryTabAccessDTO.getOrganizationCategory());
        Boolean isUnion = OrganizationCategory.UNION.equals(orgCategoryTabAccessDTO.getOrganizationCategory());

        if(orgCategoryTabAccessDTO.isAccessStatus()){
            accessGroupRepository.addAccessPageRelationshipForCountryAccessGroups(tabId,orgCategoryTabAccessDTO.getOrganizationCategory().toString() );
            accessGroupRepository.addAccessPageRelationshipForOrganizationAccessGroups(tabId, isKairosHub, isUnion);
        } else {
            accessGroupRepository.removeAccessPageRelationshipForCountryAccessGroup(tabId,orgCategoryTabAccessDTO.getOrganizationCategory().toString() );
            accessGroupRepository.removeAccessPageRelationshipForOrganizationAccessGroup(tabId, isKairosHub, isUnion);
        }
        return accessPageRepository.updateAccessStatusOfCountryByCategory(tabId, orgCategoryTabAccessDTO.getOrganizationCategory().toString(), orgCategoryTabAccessDTO.isAccessStatus());

    }

    public void createAccessPageByXml(Tab tab){

        List<AccessPage> accessPages = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Tab child : tab.getSubPages()){
            AccessPage accessPage = objectMapper.convertValue(child,AccessPage.class);
            accessPages.add(accessPage);
        }
        accessPageRepository.saveAll(accessPages);
    }

    public void setPermissionToAccessPage(){
        List<AccessPermission> accessPermissions = accessPermissionGraphRepository.findAll();
        List<AccessPage> accessPages = (List<AccessPage> )accessPageRepository.findAll();

        List<AccessPermissionAccessPageRelation> accessPermissionAccessPageRelations = new ArrayList<>(accessPages.size());
        for(AccessPermission accessPermission : accessPermissions){
            for (AccessPage accessPage : accessPages) {
                AccessPermissionAccessPageRelation accessPermissionAccessPageRelation = new AccessPermissionAccessPageRelation(accessPermission, accessPage);
                accessPermissionAccessPageRelation.setRead(true);
                accessPermissionAccessPageRelation.setWrite(false);
                accessPermissionAccessPageRelations.add(accessPermissionAccessPageRelation);
            }
        }
        employmentPageGraphRepository.saveAll(accessPermissionAccessPageRelations);
    }

    public void setPagePermissionToStaff(AccessPermission accessPermission,long accessGroupId) {
        List<AccessPage> accessPages = accessGroupRepository.getAccessPageByGroup(accessGroupId);
        List<AccessPermissionAccessPageRelation> accessPermissionAccessPageRelations = new ArrayList<>(accessPages.size());
        for (AccessPage accessPage : accessPages) {
            AccessPermissionAccessPageRelation accessPermissionAccessPageRelation = new AccessPermissionAccessPageRelation(accessPermission, accessPage);
            accessPermissionAccessPageRelation.setRead(true);
            accessPermissionAccessPageRelation.setWrite(true);
            accessPermissionAccessPageRelations.add(accessPermissionAccessPageRelation);
        }
        employmentPageGraphRepository.saveAll(accessPermissionAccessPageRelations);
    }

    public void setPagePermissionToAdmin(AccessPermission accessPermission) {
        List<AccessPage> accessPages =(List<AccessPage>) accessPageRepository.findAll();
        List<AccessPermissionAccessPageRelation> accessPermissionAccessPageRelations = new ArrayList<>(accessPages.size());
        for (AccessPage accessPage : accessPages) {
            AccessPermissionAccessPageRelation accessPermissionAccessPageRelation = new AccessPermissionAccessPageRelation(accessPermission, accessPage);
            accessPermissionAccessPageRelation.setRead(true);
            accessPermissionAccessPageRelation.setWrite(true);
            accessPermissionAccessPageRelations.add(accessPermissionAccessPageRelation);
        }
        employmentPageGraphRepository.saveAll(accessPermissionAccessPageRelations);
    }


    public AccessPage findByModuleId(String moduleId) {
        return accessPageRepository.findByModuleId(moduleId);

    }

    private synchronized String getTabId(Boolean isModule){

        Integer lastTabIdNumber = accessPageRepository.getLastTabOrModuleIdOfAccessPage(isModule);
        return (isModule ? AppConstants.MODULE_ID_PRFIX : AppConstants.TAB_ID_PRFIX)+(Optional.ofNullable(lastTabIdNumber).isPresent() ? String.valueOf(lastTabIdNumber+1) : "1");
    }

    public boolean isHubMember(Long userId){
        return accessPageRepository.isHubMember(userId);
    }


    public List<KPIAccessPageDTO> getKPIAccessPageListForCountry(Long countryId){
        List<KPIAccessPageQueryResult> accessPages = accessPageRepository.getKPITabsListForCountry(countryId);
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(accessPages, KPIAccessPageDTO.class);
    }

    public List<KPIAccessPageDTO> getKPIAccessPageListForUnit(Long unitId){
        Long userId=UserContext.getUserDetails().getId();
        if(accessPageRepository.isHubMember(userId)){
            Organization parentHub = accessPageRepository.fetchParentHub(userId);
            unitId=parentHub.getId();
        }
        List<KPIAccessPageQueryResult> accessPages = accessPageRepository.getKPITabsListForUnit(unitId,userId);
        List<KPIAccessPageDTO> kpiTabs = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(accessPages, KPIAccessPageDTO.class);
        for (KPIAccessPageDTO accessPage : kpiTabs) {
            for (KPIAccessPageDTO kpiAccessPageDTO : accessPage.getChild()) {
                kpiAccessPageDTO.setActive(kpiAccessPageDTO.isRead()||kpiAccessPageDTO.isWrite());
            }
            accessPage.setActive(accessPage.isRead()||accessPage.isWrite());
        }
        return kpiTabs;
    }

    public List<KPIAccessPageDTO> getKPIAccessPageList(String moduleId){
        List<AccessPage> accessPages = accessPageRepository.getKPITabsList(moduleId);
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(accessPages, KPIAccessPageDTO.class);
    }

    public AccessPageLanguageDTO assignLanguageToAccessPage(String moduleId, AccessPageLanguageDTO accessPageLanguageDTO){
        if(Optional.ofNullable(accessPageLanguageDTO.getId()).isPresent()){
            Optional<AccessPageLanguageRelationShip> accessPageLanguageRelationShip= accessPageLanguageRelationShipRepository.findById(accessPageLanguageDTO.getId());
            if(!accessPageLanguageRelationShip.isPresent()){
                exceptionService.dataNotFoundByIdException(ACCESS_PAGE_LANG_DESCRIPTION_ABSENT,accessPageLanguageDTO.getLanguageId());
            }
            accessPageLanguageRelationShip.get().setDescription(accessPageLanguageDTO.getDescription());
            accessPageLanguageRelationShipRepository.save(accessPageLanguageRelationShip.get());
            return accessPageLanguageDTO;
        }

        AccessPage accessPage=accessPageRepository.findByModuleId(moduleId);
        if(!Optional.ofNullable(accessPage).isPresent()){
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,"Access Page",moduleId);
        }
        SystemLanguage systemLanguage=systemLanguageGraphRepository.findSystemLanguageById(accessPageLanguageDTO.getLanguageId());
        if(!Optional.ofNullable(systemLanguage).isPresent()){
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,"SystemLanguage", accessPageLanguageDTO.getLanguageId());
        }

        AccessPageLanguageRelationShip accessPageLanguageRelationShip=new AccessPageLanguageRelationShip(accessPageLanguageDTO.getId(),accessPage,systemLanguage, accessPageLanguageDTO.getDescription());
        accessPageLanguageRelationShipRepository.save(accessPageLanguageRelationShip);
        accessPageLanguageDTO.setId(accessPageLanguageRelationShip.getId());
        return accessPageLanguageDTO;

    }

    public AccessPageLanguageDTO getLanguageDataByModuleId(String moduleId, Long languageId){
        return accessPageRepository.findLanguageSpecificDataByModuleIdAndLanguageId(moduleId,languageId);
    }

    public List<StaffAccessGroupQueryResult> getAccessPermission(Long userId, Set<Long> organizationIds){
       return accessPageRepository.getAccessPermission(userId,  organizationIds);
    }
}
