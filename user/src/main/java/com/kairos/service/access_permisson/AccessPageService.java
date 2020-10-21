package com.kairos.service.access_permisson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.access_page.OrgCategoryTabAccessDTO;
import com.kairos.dto.user_context.UserContext;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.MODULE_11;
import static com.kairos.constants.AppConstants.TAB_119;
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
        AccessPage accessPage = accessPageRepository.findOne(accessPageId);
        if(isNull(accessPage)){
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,TAB,accessPageId);
        }
        accessPage.setName(accessPageDTO.getName());
        accessPage.setTranslatedNames(accessPageDTO.getTranslatedNames());
        accessPageRepository.save(accessPage);
        return accessPage;
    }

    public List<AccessPageDTO> getMainTabs(){
        List<AccessPageQueryResult> accessPageQueryResults = accessPageRepository.getMainTabs();
        return prepareAccessPageDTOList(accessPageQueryResults);
    }

    public List<AccessPageDTO> getMainTabsForUnit(Long unitId){
        List<AccessPageQueryResult> accessPageQueryResults = accessPageRepository.getMainTabsForUnit(unitId);
        return prepareAccessPageDTOList(accessPageQueryResults);
    }

    public List<AccessPageDTO> getChildTabs(Long tabId){
        if( !Optional.ofNullable(tabId).isPresent() ){
            return Collections.emptyList();
        }
        List<AccessPageQueryResult> accessPageQueryResults = accessPageRepository.getChildTabs(tabId);
        return prepareAccessPageDTOList(accessPageQueryResults);
    }

    private List<AccessPageDTO> prepareAccessPageDTOList(List<AccessPageQueryResult> accessPageQueryResults){
        List<AccessPageDTO> accessPageDTOS = new ArrayList<>();
        for (AccessPageQueryResult accessPageQueryResult : accessPageQueryResults) {
            AccessPageDTO accessPageDTO = ObjectMapperUtils.copyPropertiesByMapper(accessPageQueryResult, AccessPageDTO.class);
            accessPageDTO.setTranslatedNames(accessPageQueryResult.getAccessPage().getTranslatedNames());
            accessPageDTOS.add(accessPageDTO);
        }
        return accessPageDTOS;
    }

    public Boolean updateStatus(boolean active,Long tabId){
        return (Optional.ofNullable(tabId).isPresent())?accessPageRepository.updateStatusOfAccessTabs(tabId,active):false;
    }

    public Boolean updateAccessForOrganizationCategory(Long tabId, OrgCategoryTabAccessDTO orgCategoryTabAccessDTO){
        if( !Optional.ofNullable(tabId).isPresent() ){
            return false;
        }
        AccessPage accessPage = accessPageRepository.findById(tabId).orElse(new AccessPage());
        Boolean isKairosHub = OrganizationCategory.HUB.equals(orgCategoryTabAccessDTO.getOrganizationCategory());
        Boolean isUnion = OrganizationCategory.UNION.equals(orgCategoryTabAccessDTO.getOrganizationCategory());
        if(isKairosHub && !orgCategoryTabAccessDTO.isAccessStatus() && (MODULE_11.equals(accessPage.getModuleId()) || TAB_119.equals(accessPage.getModuleId()))){
            exceptionService.actionNotPermittedException(ERROR_TAB_CAN_NOT_BE_HIDE_FOR_HUB);
        }
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
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(accessPages, KPIAccessPageDTO.class);
    }

    public List<KPIAccessPageDTO> getKPIAccessPageListForUnit(Long unitId){
        Long userId=UserContext.getUserDetails().getId();
        if(accessPageRepository.isHubMember(userId)){
            Organization parentHub = accessPageRepository.fetchParentHub(userId);
            unitId=parentHub.getId();
        }
        List<KPIAccessPageQueryResult> accessPages = accessPageRepository.getKPITabsListForUnit(unitId,userId);
        List<KPIAccessPageDTO> kpiTabs = ObjectMapperUtils.copyCollectionPropertiesByMapper(accessPages, KPIAccessPageDTO.class);
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
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(accessPages, KPIAccessPageDTO.class);
    }

    public AccessPageLanguageDTO assignLanguageToAccessPage(String moduleId, AccessPageLanguageDTO accessPageLanguageDTO){
            AccessPage accessPage=accessPageRepository.findByModuleId(moduleId);
            if(!Optional.ofNullable(accessPage).isPresent()){
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,"Access Page",moduleId);
            }
            SystemLanguage systemLanguage=systemLanguageGraphRepository.findSystemLanguageById(accessPageLanguageDTO.getLanguageId());
            if(!Optional.ofNullable(systemLanguage).isPresent()){
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,"SystemLanguage", accessPageLanguageDTO.getLanguageId());
            }
            AccessPageLanguageRelationShip accessPageLanguageRelationShip= accessPageLanguageRelationShipRepository.findByModuleIdAndLanguageId(accessPageLanguageDTO.getModuleId(),accessPageLanguageDTO.getLanguageId()).orElse(new AccessPageLanguageRelationShip());
            accessPageLanguageRelationShip.setDescription(accessPageLanguageDTO.getDescription());
            accessPageLanguageRelationShip.setAccessPage(accessPage);
            accessPageLanguageRelationShip.setSystemLanguage(systemLanguage);
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

    public Map<String, TranslationInfo> updateTranslation(String moduleId, Map<String,TranslationInfo> translationData) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translationData.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        AccessPage accessPage = accessPageRepository.findByModuleId(moduleId);
        accessPage.setTranslatedNames(translatedNames);
        accessPage.setTranslatedDescriptions(translatedDescriptios);
        accessPageRepository.save(accessPage);
        return accessPage.getTranslatedData();
    }

    public Map<String, TranslationInfo> getTranslatedData(Long accessPageId) {
        AccessPage accessPage = accessPageRepository.findOne(accessPageId);
        return accessPage.getTranslatedData();
    }

    public List<AccessPageDTO> getTabHierarchy(Long languageId) {
        List<AccessPageDTO> mainTabs = prepareAccessPageDTOList(accessPageRepository.getMainTabsWithHelperText(languageId));
        for (AccessPageDTO accessPageDTO : mainTabs) {
            setChildrenAccessPages(accessPageDTO, languageId);
        }
        return mainTabs;
    }

    private void setChildrenAccessPages(AccessPageDTO accessPageDTO, Long languageId){
        List<AccessPageDTO> childAccessPageDTOS = prepareAccessPageDTOList(accessPageRepository.getChildTabsWithHelperText(accessPageDTO.getId(), languageId));
        for (AccessPageDTO childAccessPageDTO : childAccessPageDTOS) {
            if(childAccessPageDTO.isHasSubTabs()){
                setChildrenAccessPages(childAccessPageDTO, languageId);
            }
        }
        accessPageDTO.setChildren(childAccessPageDTOS);
    }

    public boolean setUrlInAccessPages() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("accesspage/accessPageUrl.json");
        InputStream inputStream = resource.getInputStream();
        //File file = ResourceUtils.getFile("classpath:accesspage/accessPageUrl.json");
        Map<String, String> accessPageMap = mapper.readValue(inputStream, new TypeReference<Map<String, String>>() {
        });
        List<AccessPage> accessPages= (List<AccessPage>) accessPageRepository.findAll();
        accessPages.forEach(accessPage-> accessPage.setUrl(accessPageMap.get(accessPage.getModuleId())));
        accessPageRepository.saveAll(accessPages);
        return true;
    }
}
