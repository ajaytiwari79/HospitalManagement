package com.kairos.service.counter;


import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.counter.KPICategoryDTO;
import com.kairos.dto.activity.counter.KPIDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.dto.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.dto.activity.counter.distribution.category.InitialKPICategoryDistDataDTO;
import com.kairos.dto.activity.counter.distribution.category.StaffKPIGalleryDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class CounterDistService extends MongoBaseService {
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private GenericIntegrationService genericIntegrationService;

    private final static Logger logger = LoggerFactory.getLogger(CounterDistService.class);

    private void overrideOldObject(MongoBaseEntity newEntity, MongoBaseEntity oldEntity) {
        BeanUtils.copyProperties(oldEntity, newEntity);
    }

    public List<KPIAccessPageDTO> getKPIAccessPageListForUnit(Long refId,ConfLevel level){
        List<KPIAccessPageDTO> kpiAccessPageDTOSOfDashboard=counterRepository.getKPIAcceccPage(refId,level);
        List<KPIAccessPageDTO> kpiAccessPageDTOS=genericIntegrationService.getKPIEnabledTabsForModuleForUnit(refId);
        setKPIAccessPage(kpiAccessPageDTOSOfDashboard,kpiAccessPageDTOS);
        return kpiAccessPageDTOS;
    }

    public List<KPIAccessPageDTO> getKPIAccessPageListForCountry(Long refId,ConfLevel level){
        List<KPIAccessPageDTO> kpiAccessPageDTOSOfDashboard=counterRepository.getKPIAcceccPage(refId,level);
        List<KPIAccessPageDTO> kpiAccessPageDTOS=genericIntegrationService.getKPIEnabledTabsForModuleForCountry(refId);
        setKPIAccessPage(kpiAccessPageDTOSOfDashboard,kpiAccessPageDTOS);
        return kpiAccessPageDTOS;
    }

    public void setKPIAccessPage(List<KPIAccessPageDTO> kpiAccessPages,List<KPIAccessPageDTO> kpiAccessPageDTOS){
        if(kpiAccessPages.isEmpty()||kpiAccessPageDTOS.isEmpty()) return;
        Map<String,List<KPIAccessPageDTO>> accessPageMap=new HashMap<>();
        kpiAccessPages.stream().forEach(kpiAccessPageDTO -> {
            accessPageMap.put(kpiAccessPageDTO.getModuleId(),kpiAccessPageDTO.getChild());
        });
        kpiAccessPageDTOS.stream().forEach(kpiAccessPageDTO -> {
            if(accessPageMap.get(kpiAccessPageDTO.getModuleId())!=null){
                kpiAccessPageDTO.setChild(accessPageMap.get(kpiAccessPageDTO.getModuleId()));
            }
        });
    }

    public List<KPIDTO> getKPIsList(Long refId, ConfLevel level) {
        if(ConfLevel.STAFF.equals(level)){
            refId=genericIntegrationService.getStaffIdByUserId(refId);
        }
        List<KPIDTO> kpidtos=counterRepository.getCounterListForCountryOrUnitOrStaff(refId,level);
        if(kpidtos.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }
        return kpidtos;
    }


    public InitialKPICategoryDistDataDTO getInitialCategoryKPIDistData(Long refId, ConfLevel level) {
        List<KPICategoryDTO> categories = counterRepository.getKPICategory(null, level, refId);
        List<BigInteger> categoryIds = categories.stream().map(kpiCategoryDTO -> kpiCategoryDTO.getId()).collect(toList());
        List<CategoryKPIMappingDTO> categoryKPIMapping = counterRepository.getKPIsMappingForCategories(categoryIds);
        return new InitialKPICategoryDistDataDTO(categories, categoryKPIMapping);
    }

    public StaffKPIGalleryDTO getInitialCategoryKPIDistDataForStaff(Long refId) {
        List<BigInteger> kpiIds=null;
        List<KPIDTO> kpidtos=null;
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO =genericIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
        if(accessGroupPermissionCounterDTO.getCountryAdmin()){
             kpidtos=counterRepository.getCounterListForCountryOrUnitOrStaff(refId,ConfLevel.UNIT);
        }else{
            kpidtos = counterRepository.getAccessGroupKPIDto(accessGroupPermissionCounterDTO.getAccessGroupIds(),ConfLevel.UNIT,refId,accessGroupPermissionCounterDTO.getStaffId());
        }
        kpiIds=kpidtos.stream().map(kpidto ->kpidto.getId()).collect(Collectors.toList());
        //dont delete
       // counterRepository.removeApplicableKPI(Arrays.asList(accessGroupPermissionCounterDTO.getStaffId()),kpiIds,refId,ConfLevel.STAFF);
        List<CategoryKPIMappingDTO> categoryKPIMapping = counterRepository.getKPIsMappingForCategoriesForStaff(kpiIds,refId,ConfLevel.UNIT);
        return new StaffKPIGalleryDTO(categoryKPIMapping,kpidtos);
    }

    public void addCategoryKPIsDistribution(CategoryKPIsDTO categoryKPIsDetails, ConfLevel level, Long refId) {
        Long countryId = ConfLevel.COUNTRY.equals(level)? refId: null;
        Long unitId=ConfLevel.UNIT.equals(level)? refId: null;
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPI(categoryKPIsDetails.getKpiIds(), level, refId);
        if(applicableKPIS.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }
        List<KPICategoryDTO> kpiCategoryDTOS = counterRepository.getKPICategory(null, level, refId);
        List<BigInteger> categoryIds=kpiCategoryDTOS.stream().map(kpiCategoryDTO ->kpiCategoryDTO.getId()).collect(Collectors.toList());
        if(!categoryIds.contains(categoryKPIsDetails.getCategoryId())){
            exceptionService.dataNotFoundByIdException("error.kpi_category.availability");
        }
        List<CategoryKPIConf> categoryKPIConfs = counterRepository.getCategoryKPIConfs(categoryKPIsDetails.getKpiIds(),categoryIds);
        List<BigInteger>  availableCategoryIds=categoryKPIConfs.stream().map(categoryKPIConf -> categoryKPIConf.getCategoryId()).collect(toList());
        if(availableCategoryIds.contains(categoryKPIsDetails.getCategoryId())){
            exceptionService.invalidOperationException("error.dist.category_kpi.invalid_operation");
        }
        List<CategoryKPIConf> newCategoryKPIConfs = new ArrayList<>();
        applicableKPIS.parallelStream().forEach(applicableKPI -> newCategoryKPIConfs.add(new CategoryKPIConf(applicableKPI.getActiveKpiId(),categoryKPIsDetails.getCategoryId(),countryId,unitId,level)));
        if(!newCategoryKPIConfs.isEmpty())
        {
        save(newCategoryKPIConfs);
        counterRepository.removeCategoryKPIEntries(availableCategoryIds,categoryKPIsDetails.getKpiIds());
        }

    }

    //settings for KPI-Module configuration

    public List<BigInteger> getInitialTabKPIDataConf(String moduleId,Long refId, ConfLevel level) {
        List<TabKPIMappingDTO> tabKPIMappingDTOS=counterRepository.getTabKPIConfigurationByTabIds(Arrays.asList(moduleId),new ArrayList<>(),refId,level);
        if (tabKPIMappingDTOS == null || tabKPIMappingDTOS.isEmpty()) return new ArrayList<>();
        return tabKPIMappingDTOS.stream().map(tabKPIMappingDTO ->tabKPIMappingDTO.getKpiId()).collect(Collectors.toList());
    }

    public List<TabKPIDTO> getInitialTabKPIDataConfForStaff(String moduleId,Long unitId, ConfLevel level){
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO =genericIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
       // Long staffId=genericIntegrationService.getStaffIdByUserId(unitId);
        List<TabKPIDTO> tabKPIDTOS=counterRepository.getTabKPIForStaffByTabAndStaffId(Arrays.asList(moduleId),new ArrayList<>(),accessGroupPermissionCounterDTO.getStaffId(),unitId,level);
        return tabKPIDTOS;
    }

    public List<TabKPIDTO> addTabKPIEntriesOfStaff(List<TabKPIMappingDTO> tabKPIMappingDTOS,Long unitId,ConfLevel level){
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO =genericIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
      //  Long staffId=genericIntegrationService.getStaffIdByUserId(unitId);
        List<TabKPIConf> entriesToSave = new ArrayList<>();
        List<String> tabIds=tabKPIMappingDTOS.stream().map(tabKPIMappingDTO -> tabKPIMappingDTO.getTabId()).collect(toList());
        List<BigInteger> kpiIds=tabKPIMappingDTOS.stream().map(tabKPIMappingDTO -> tabKPIMappingDTO.getKpiId()).collect(toList());
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap=setTabKPIEntries(tabIds,kpiIds,entriesToSave,null,null,accessGroupPermissionCounterDTO.getStaffId(),level,accessGroupPermissionCounterDTO.getCountryAdmin());
        tabKPIMappingDTOS.stream().forEach(tabKPIMappingDTO -> {
            if(tabKpiMap.get(tabKPIMappingDTO.getTabId()).get(tabKPIMappingDTO.getKpiId())==null){
                entriesToSave.add(new TabKPIConf(tabKPIMappingDTO.getTabId(),tabKPIMappingDTO.getKpiId(),null,unitId,accessGroupPermissionCounterDTO.getStaffId(),level,tabKPIMappingDTO.getPosition()));
            }
        });
        if (entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException("error.kpi.invalidData");
        }
        save(entriesToSave);
        return counterRepository.getTabKPIForStaffByTabAndStaffId(tabIds,kpiIds,accessGroupPermissionCounterDTO.getStaffId(),unitId,level);
    }

    public void addTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries,Long countryId,Long unitId,Long staffId, ConfLevel level) {
        List<TabKPIConf> entriesToSave = new ArrayList<>();
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap=setTabKPIEntries(tabKPIEntries.getTabIds(),tabKPIEntries.getKpiIds(),entriesToSave,countryId,unitId,staffId,level,false);
        tabKPIEntries.getTabIds().forEach(tabId->{tabKPIEntries.getKpiIds().forEach(kpiId->{
            if(tabKpiMap.get(tabId).get(kpiId)==null){
                entriesToSave.add(new TabKPIConf(tabId,kpiId,countryId,unitId,staffId,level,null));
            }
        });});
        if (entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException("error.kpi.invalidData");
        }
        save(entriesToSave);
    }
    public Map<String, Map<BigInteger, BigInteger>> setTabKPIEntries(List<String> tabIds,List<BigInteger> kpiIds,List<TabKPIConf> entriesToSave,Long countryId,Long unitId,Long staffId, ConfLevel level,boolean isCountryAdmin){
        Long refId = ConfLevel.COUNTRY.equals(level) ? countryId : unitId;
        if(ConfLevel.STAFF.equals(level)){
            refId=staffId;
        }
        List<TabKPIMappingDTO> tabKPIMappingDTOS = counterRepository.getTabKPIConfigurationByTabIds(tabIds,kpiIds,refId,level);
        if(!isCountryAdmin) {
            List<ApplicableKPI> applicableKPIS = counterRepository.getKPIByKPIId(kpiIds, refId, level);
            if (kpiIds.size() != applicableKPIS.size()) {
                exceptionService.actionNotPermittedException("message.counter.kpi.notfound");
            }
        }
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap = new HashMap<>();
        tabIds.forEach(tabKpiId -> {
            tabKpiMap.put(tabKpiId, new HashMap<BigInteger, BigInteger>());
        });
        tabKPIMappingDTOS.forEach(tabKPIMappingDTO -> {
            tabKpiMap.get(tabKPIMappingDTO.getTabId()).put(tabKPIMappingDTO.getKpiId(), tabKPIMappingDTO.getKpiId());
        });
        return  tabKpiMap;
    }
    public void updateTabKPIEntries(List<TabKPIMappingDTO> tabKPIMappingDTOS,String tabId,Long unitId, ConfLevel level) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO =genericIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
//        Long staffId=genericIntegrationService.getStaffIdByUserId(unitId);
        List<BigInteger> kpiIds=tabKPIMappingDTOS.stream().map(tabKPIMappingDTO -> tabKPIMappingDTO.getKpiId()).collect(Collectors.toList());
        List<TabKPIConf> tabKPIConfs=counterRepository.findTabKPIConfigurationByTabIds(tabId,kpiIds,accessGroupPermissionCounterDTO.getStaffId(),level);
        Map<BigInteger,TabKPIMappingDTO> tabKPIMappingDTOMap=new HashMap<>();
        tabKPIMappingDTOS.stream().forEach(tabKPIMappingDTO -> {
            tabKPIMappingDTOMap.put(tabKPIMappingDTO.getId(),tabKPIMappingDTO);
        });
        tabKPIConfs.stream().forEach(tabKPIConf -> {
           TabKPIMappingDTO tabKPIMappingDTO=tabKPIMappingDTOMap.get(tabKPIConf.getId());
           tabKPIConf.setPosition(tabKPIMappingDTO.getPosition());
        });
        save(tabKPIConfs);
    }


    public void removeTabKPIEntries(TabKPIMappingDTO tabKPIMappingDTO,Long refId,ConfLevel level) {
        if(ConfLevel.STAFF.equals(level)){
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO =genericIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
            refId=accessGroupPermissionCounterDTO.getStaffId();
        }
       counterRepository.removeTabKPIConfiguration(tabKPIMappingDTO,refId,level);
    }

    //setting accessGroup-KPI configuration

    public List<BigInteger> getInitialAccessGroupKPIDataConf(Long accessGroupId,Long refId,ConfLevel level) {
        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(Arrays.asList(accessGroupId),new ArrayList<>(),level,refId);
        if (AccessGroupMappingDTOS == null || AccessGroupMappingDTOS.isEmpty()) return new ArrayList<>();
        return AccessGroupMappingDTOS.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
    }


    public void addAccessGroupKPIEntries(AccessGroupKPIConfDTO accessGroupKPIConf,Long refId,ConfLevel level) {
        Long countryId = ConfLevel.COUNTRY.equals(level)? refId: null;
        Long unitId=ConfLevel.UNIT.equals(level)? refId: null;
        List<AccessGroupKPIEntry> entriesToSave = new ArrayList<>();
        List<ApplicableKPI> applicableKPIS = counterRepository.getKPIByKPIId(accessGroupKPIConf.getKpiIds(),refId,level);
        if (accessGroupKPIConf.getKpiIds().size() != applicableKPIS.size()) {
            exceptionService.actionNotPermittedException("message.counter.kpi.notfound");
        }
        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(accessGroupKPIConf.getAccessGroupIds(),accessGroupKPIConf.getKpiIds(),level,refId);
        Map<Long, Map<BigInteger, BigInteger>> accessGroupKPIMap = new HashMap<>();
        accessGroupKPIConf.getAccessGroupIds().forEach(orgTypeId -> {
            accessGroupKPIMap.put(orgTypeId, new HashMap<BigInteger, BigInteger>());
        });
        AccessGroupMappingDTOS.forEach(AccessGroupMappingDTO -> {
            accessGroupKPIMap.get(AccessGroupMappingDTO.getAccessGroupId()).put(AccessGroupMappingDTO.getKpiId(), AccessGroupMappingDTO.getKpiId());
        });
        accessGroupKPIConf.getAccessGroupIds().forEach(accessGroupId->{accessGroupKPIConf.getKpiIds().forEach(kpiId->{
            if(accessGroupKPIMap.get(accessGroupId).get(kpiId)==null){
                entriesToSave.add(new AccessGroupKPIEntry(accessGroupId,kpiId,countryId,unitId,level));
            }
        });});
        if(entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException("error.kpi.invalidData");
        }
        save(entriesToSave);
        if(ConfLevel.UNIT.equals(level)){
        List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
        Map<Long,Map<BigInteger,BigInteger>> staffIdKpiMap=new HashMap<>();
        List<StaffIdsDTO> staffIdsDTOS = genericIntegrationService.getStaffIdsByunitAndAccessGroupId(refId,accessGroupKPIConf.getAccessGroupIds());
        List<Long> staffids=staffIdsDTOS.stream().flatMap(staffIdsDTO -> staffIdsDTO.getStaffIds().stream()).collect(toList());
        staffids.forEach(staffid->{
            staffIdKpiMap.put(staffid,new HashMap<BigInteger, BigInteger>());
        });
        List<ApplicableKPI> applicableKPISForStaff = counterRepository.getApplicableKPIByReferenceId(AccessGroupMappingDTOS.stream().map(orgTypeMappingDTO -> orgTypeMappingDTO.getKpiId()).collect(toList()),staffids, ConfLevel.STAFF);
        applicableKPISForStaff.forEach(applicableKPI ->{
            staffIdKpiMap.get(applicableKPI.getStaffId()).put(applicableKPI.getBaseKpiId(),applicableKPI.getBaseKpiId());
        });
        staffids.forEach(staffId ->{accessGroupKPIConf.getKpiIds().forEach(kpiId->{
            if(staffIdKpiMap.get(staffId).get(kpiId)==null){
                applicableKPISToSave.add(new ApplicableKPI(kpiId,kpiId,null, null, staffId, ConfLevel.STAFF));
                staffIdKpiMap.get(staffId).put(kpiId,kpiId);
            }
        });});
        if(!applicableKPISToSave.isEmpty()){
            save(applicableKPISToSave);
        }
        }
    }

    public void removeAccessGroupKPIEntries(AccessGroupMappingDTO accessGroupMappingDTO,Long refId,ConfLevel level) {
        if(ConfLevel.UNIT.equals(level)) {
            AccessGroupKPIEntry accessGroupKPIEntry = counterRepository.getAccessGroupKPIEntry(accessGroupMappingDTO,refId);
            if(!Optional.ofNullable(accessGroupKPIEntry).isPresent()){
                exceptionService.dataNotFoundByIdException("message.accessgroup.kpi.notfound");
            }
               List<StaffIdsDTO> staffIdsDTOS = genericIntegrationService.getStaffIdsByunitAndAccessGroupId(accessGroupKPIEntry.getUnitId(),Arrays.asList(accessGroupKPIEntry.getAccessGroupId()));
                List<Long> staffIds=staffIdsDTOS.stream().flatMap(staffIdsDTO -> staffIdsDTO.getStaffIds().stream()).collect(Collectors.toList());
            counterRepository.removeApplicableKPI(staffIds, Arrays.asList(accessGroupKPIEntry.getKpiId()),refId, ConfLevel.STAFF);
            counterRepository.removeTabKPIEntry(staffIds, accessGroupKPIEntry.getKpiId(), ConfLevel.STAFF);
            counterRepository.removeEntityById(accessGroupKPIEntry.getId(), AccessGroupKPIEntry.class);
        }else{
            counterRepository.removeAccessGroupKPIEntryForCountry(accessGroupMappingDTO,refId);
        }
    }

    //setting orgType-KPI configuration


    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId,Long countryId) {
        List<OrgTypeMappingDTO> orgTypeKPIEntries = counterRepository.getOrgTypeKPIEntryOrgTypeIds(Arrays.asList(orgTypeId),new ArrayList<>(),countryId);
        if (orgTypeKPIEntries == null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
        return orgTypeKPIEntries.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
    }

    public void addOrgTypeKPIEntries(OrgTypeKPIConfDTO orgTypeKPIConf, Long countryId) {
        List<ApplicableKPI> applicableKPIS = counterRepository.getKPIByKPIId(orgTypeKPIConf.getKpiIds(),countryId,ConfLevel.COUNTRY);
        if (orgTypeKPIConf.getKpiIds().size() != applicableKPIS.size()) {
            exceptionService.actionNotPermittedException("message.counter.kpi.notfound");
        }
        List<OrgTypeKPIEntry> entriesToSave = new ArrayList<>();
        Map<Long, Map<BigInteger, BigInteger>> orgTypeKPIsMap = new HashMap<>();
        orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId->{
            orgTypeKPIsMap.put(orgTypeId,new HashMap<BigInteger, BigInteger>());
        });
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(orgTypeKPIConf.getOrgTypeIds(),orgTypeKPIConf.getKpiIds(), countryId);
        orgTypeMappingDTOS.forEach(orgTypeMappingDTO -> {
            orgTypeKPIsMap.get(orgTypeMappingDTO.getOrgTypeId()).put(orgTypeMappingDTO.getKpiId(),orgTypeMappingDTO.getKpiId());
        });
          orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId->{orgTypeKPIConf.getKpiIds().forEach(kpiId->{
              if(orgTypeKPIsMap.get(orgTypeId).get(kpiId)==null){
                  entriesToSave.add(new OrgTypeKPIEntry(orgTypeId,kpiId,countryId));
              }
          }); });
        if(entriesToSave.isEmpty()){
            exceptionService.invalidRequestException("error.kpi.invalidData");
        }
        save(entriesToSave);
        List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
        Map<Long,Map<BigInteger,BigInteger>> unitIdKpiMap=new HashMap<>();
        List<OrgTypeDTO> orgTypeDTOS=genericIntegrationService.getOrganizationIdsBySubOrgId(orgTypeKPIConf.getOrgTypeIds());
        List<Long> unitIds=orgTypeDTOS.stream().flatMap(orgTypeDTO -> orgTypeDTO.getUnitIds().stream()).collect(toList());
        unitIds.forEach(unitId->{
            unitIdKpiMap.put(unitId,new HashMap<BigInteger, BigInteger>());
        });
        List<ApplicableKPI> applicableKPISForUnit = counterRepository.getApplicableKPIByReferenceId(orgTypeMappingDTOS.stream().map(orgTypeMappingDTO -> orgTypeMappingDTO.getKpiId()).collect(toList()),unitIds, ConfLevel.UNIT);
        applicableKPISForUnit.forEach(applicableKPI ->{
            unitIdKpiMap.get(applicableKPI.getUnitId()).put(applicableKPI.getBaseKpiId(),applicableKPI.getBaseKpiId());
        });
        unitIds.forEach(unitId ->{orgTypeKPIConf.getKpiIds().forEach(kpiId->{
            if(unitIdKpiMap.get(unitId).get(kpiId)==null){
                applicableKPISToSave.add(new ApplicableKPI(kpiId,kpiId,null, unitId, null, ConfLevel.UNIT));
                unitIdKpiMap.get(unitId).put(kpiId,kpiId);
            }
        });});
        if(!applicableKPISToSave.isEmpty()){
            save(applicableKPISToSave);
        }
    }

    public void removeOrgTypeKPIEntries(OrgTypeMappingDTO orgTypeMappingDTO,Long countryId) {
        OrgTypeKPIEntry orgTypeKPIEntry=counterRepository.getOrgTypeKPIEntry(orgTypeMappingDTO,countryId);
        if(!Optional.ofNullable(orgTypeKPIEntry).isPresent()){
            exceptionService.dataNotFoundByIdException("message.orgtype.kpi.notfound");
        }
        List<OrgTypeDTO> orgTypeDTOS=genericIntegrationService.getOrganizationIdsBySubOrgId(Arrays.asList(orgTypeKPIEntry.getOrgTypeId()));
        List<Long> unitIds=orgTypeDTOS.stream().flatMap(orgTypeDTO -> orgTypeDTO.getUnitIds().stream()).collect(toList());
        counterRepository.removeCategoryKPIEntry(unitIds,orgTypeKPIEntry.getKpiId());
        counterRepository.removeAccessGroupKPIEntry(unitIds,orgTypeKPIEntry.getKpiId());
        counterRepository.removeTabKPIEntry(unitIds,orgTypeKPIEntry.getKpiId(),ConfLevel.UNIT);
        counterRepository.removeApplicableKPI(unitIds,Arrays.asList(orgTypeKPIEntry.getKpiId()),null,ConfLevel.UNIT);
        counterRepository.removeEntityById(orgTypeKPIEntry.getId(),OrgTypeKPIEntry.class);
    }

    public void createDefaultStaffKPISetting(Long unitId, DefaultKPISettingDTO defaultKPISettingDTO) {
        List<ApplicableKPI> applicableKPISForUnit = counterRepository.getApplicableKPIByReferenceId(new ArrayList<>(),Arrays.asList(unitId), ConfLevel.UNIT);
        List<ApplicableKPI> applicableKPIS = new ArrayList<>();
        List<BigInteger> applicableKpiIds = applicableKPISForUnit.stream().map(applicableKPI -> applicableKPI.getBaseKpiId()).collect(Collectors.toList());
        if(!applicableKPISForUnit.isEmpty()){
            applicableKpiIds.forEach(kpiId->{defaultKPISettingDTO.getStaffIds().forEach(staffId->{
                applicableKPIS.add(new ApplicableKPI(kpiId,kpiId,null,unitId,staffId,ConfLevel.STAFF));
            });
            });
        }
        List<TabKPIConf> tabKPIConfKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConf = counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds, unitId, ConfLevel.UNIT);
        if(!tabKPIConf.isEmpty()){
            defaultKPISettingDTO.getStaffIds().forEach(staffId -> {
                tabKPIConf.stream().forEach(tabKPIConfKPI -> {
                    tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(), tabKPIConfKPI.getKpiId(), null, null, staffId, ConfLevel.STAFF,tabKPIConfKPI.getPosition()));
                });
            });
        }
       if(!applicableKpiIds.isEmpty()) save(applicableKPIS);
       if(!tabKPIConfKPIEntries.isEmpty()) save(tabKPIConfKPIEntries);
    }

    public void createDefaultKpiSetting(Long unitId, DefaultKPISettingDTO defaultKPISettingDTO) {
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(defaultKPISettingDTO.getOrgTypeIds(), new ArrayList<>(), unitId);
        if(orgTypeMappingDTOS.isEmpty()){
               return;
        }
        List<BigInteger> applicableKpiIds = orgTypeMappingDTOS.stream().map(orgTypeMappingDTO -> orgTypeMappingDTO.getKpiId()).collect(Collectors.toList());
        if(Optional.ofNullable(defaultKPISettingDTO.getParentUnitId()).isPresent()) {
            setDefaultSettingUnit(defaultKPISettingDTO,applicableKpiIds,unitId,ConfLevel.UNIT);
        }else{
            setDefaultSettingUnit(defaultKPISettingDTO,applicableKpiIds,unitId,ConfLevel.COUNTRY);
        }
    }

    public void setDefaultSettingUnit(DefaultKPISettingDTO defalutKPISettingDTO, List<BigInteger> kpiIds, Long unitId, ConfLevel level) {
        Long refId = ConfLevel.COUNTRY.equals(level)? defalutKPISettingDTO.getCountryId(): defalutKPISettingDTO.getParentUnitId();
        List<CategoryKPIConf> categoryKPIConfToSave=new ArrayList<>();
        List<AccessGroupKPIEntry> accessGroupKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConfKPIEntries=new ArrayList<>();
        List<ApplicableKPI> applicableKPIS=counterRepository.getApplicableKPIByReferenceId(kpiIds,Arrays.asList(refId),level);
        List<BigInteger> applicableKpiIds=applicableKPIS.stream().map(applicableKPI -> applicableKPI.getActiveKpiId()).collect(Collectors.toList());
        List<Long> countryAccessGroupIds = defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().keySet().stream().collect(Collectors.toList());
        List<AccessGroupMappingDTO> accessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(countryAccessGroupIds, applicableKpiIds, level, refId);
        accessGroupMappingDTOS.forEach(accessGroupMappingDTO -> {
            accessGroupKPIEntries.add(new AccessGroupKPIEntry(defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().get(accessGroupMappingDTO.getAccessGroupId()), accessGroupMappingDTO.getKpiId(), null, unitId, ConfLevel.UNIT));
        });
        List<TabKPIConf> tabKPIConf=counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds,refId,level);
        tabKPIConf.stream().forEach(tabKPIConfKPI->{
            tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(),tabKPIConfKPI.getKpiId(),null,unitId,null,ConfLevel.UNIT,null));
        });
          List<KPICategoryDTO> kpiCategoryDTOS = counterRepository.getKPICategory(null, level, refId);
           Map<String,BigInteger> categoriesNameMap=new HashMap<>();
           Map<BigInteger,BigInteger> categoriesOldAndNewIds=new HashMap<>();
           kpiCategoryDTOS.stream().forEach(kpiCategoryDTO -> {
               categoriesNameMap.put(kpiCategoryDTO.getName(),kpiCategoryDTO.getId());
           });
            List<KPICategory> kpiCategories = kpiCategoryDTOS.stream().map(category -> new KPICategory(category.getName(),null,unitId,ConfLevel.UNIT)).collect(Collectors.toList());
            if(!kpiCategories.isEmpty()){
                save(kpiCategories);
            }
            kpiCategories.stream().forEach(kpiCategory -> {
               categoriesOldAndNewIds.put(categoriesNameMap.get(kpiCategory.getName()),kpiCategory.getId());
        });
           List<BigInteger> oldCategoriesIds=kpiCategoryDTOS.stream().map(kpiCategoryDTO -> kpiCategoryDTO.getId()).collect(Collectors.toList());
        List<CategoryKPIConf> categoryKPIConfList = counterRepository.getCategoryKPIConfs(applicableKpiIds,oldCategoriesIds);
        categoryKPIConfList.stream().forEach(categoryKPIConf -> {
                    categoryKPIConfToSave.add(new CategoryKPIConf(categoryKPIConf.getKpiId(),categoriesOldAndNewIds.get(categoryKPIConf.getCategoryId()),null,unitId,ConfLevel.UNIT));
        });
        List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
        applicableKpiIds.forEach(kpiId -> {
            applicableKPISToSave.add(new ApplicableKPI(kpiId,kpiId,null, unitId, null, ConfLevel.UNIT));
        });
        //due to avoid exception and entity may be blank here so I using multiple conditional statements harish
        if(!applicableKPISToSave.isEmpty()){
            save(applicableKPISToSave);
        }
        if(!accessGroupKPIEntries.isEmpty()) {
            save(accessGroupKPIEntries);
        }
        if(!categoryKPIConfToSave.isEmpty()) {
            save(categoryKPIConfToSave);
        }
        if(!tabKPIConfKPIEntries.isEmpty()) {
            save(tabKPIConfKPIEntries);
        }
    }

}
