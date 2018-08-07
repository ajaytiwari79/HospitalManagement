package com.kairos.service.counter;


import com.kairos.activity.counter.DefalutKPISettingDTO;
import com.kairos.activity.counter.KPIDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.activity.counter.distribution.access_group.RoleCounterDTO;
import com.kairos.activity.counter.distribution.category.CategoryAssignmentDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.activity.counter.distribution.category.InitialKPICategoryDistDataDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
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
public class CounterManagementService extends MongoBaseService {
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private GenericIntegrationService genericIntegrationService;

    private final static Logger logger = LoggerFactory.getLogger(CounterManagementService.class);

    private void overrideOldObject(MongoBaseEntity newEntity, MongoBaseEntity oldEntity) {
        BeanUtils.copyProperties(oldEntity, newEntity);
    }

    //get role wise moduleCounterId mapping
    public List<RoleCounterDTO> getRoleCounterMapping(BigInteger unitId) {
        return counterRepository.getRoleAndModuleCounterIdMapping(unitId);
    }

    public List<KPIDTO> getKPIsList(Long refId, ConfLevel level) {
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPIByUnitOrCountryOrStaffId(refId,level);
        List<KPIDTO> kpiDTOs=counterRepository.getEntityItemList(applicableKPIS.stream().map(applicableKPI -> applicableKPI.getActiveKpiId()).collect(Collectors.toList()));
        if(kpiDTOs.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }
        return kpiDTOs;
    }

    public InitialKPICategoryDistDataDTO getInitialCategoryKPIDistData(Long refId, ConfLevel level) {
        List<CategoryAssignmentDTO> categories = counterRepository.getCategoryAssignments(null, level, refId);
        List<BigInteger> categoryAssignmentIds = categories.parallelStream().map(categoryAssignment -> categoryAssignment.getId()).collect(toList());
        List<CategoryKPIMappingDTO> categoryKPIMapping = counterRepository.getKPIsMappingForCategories(categoryAssignmentIds);
        return new InitialKPICategoryDistDataDTO(categories, categoryKPIMapping);
    }

    public void updateCategoryKPIsDistribution(CategoryKPIsDTO categoryKPIsDetails, ConfLevel level, Long refId) {
        CategoryAssignment categoryAssignment = counterRepository.getCategoryAssignment(categoryKPIsDetails.getCategoryId(), level, refId);
        List<ApplicableKPI> applicableKPIS = counterRepository.getKPIAssignments(categoryKPIsDetails.getKpiIds(), level, refId);
        List<CategoryKPIConf> categoryKPIConfs = counterRepository.getCategoryKPIConfs(categoryAssignment.getId());
        List<BigInteger> kpiAssignmentIds = categoryKPIConfs.stream().map(categoryKPIConf -> categoryKPIConf.getKpiId()).collect(toList());
        applicableKPIS = applicableKPIS.parallelStream().filter(kpiAssignment -> kpiAssignmentIds.contains(kpiAssignment.getId())).collect(toList());
        if (applicableKPIS.isEmpty())
            exceptionService.invalidOperationException("error.dist.category_kpi.invalid_operation");
        List<CategoryKPIConf> newCategoryKPIConfs = new ArrayList<>();
        applicableKPIS.parallelStream().forEach(kpiAssignment -> newCategoryKPIConfs.add(new CategoryKPIConf(kpiAssignment.getActiveKpiId(), categoryAssignment.getId())));
        save(newCategoryKPIConfs);
    }

    //settings for KPI-Module configuration

//    public InitialKPITabDistDataDTO getInitialTabKPIDataConf(String moduleId, Long refId, ConfLevel level){
//        List<KPIAccessPageDTO> kpiTabs= genericIntegrationService.getKPIEnabledTabsForModule(moduleId, refId);
//        Map<String, List<BigInteger>> tabKPIsMap = new HashMap<>();
//        if(kpiTabs != null && kpiTabs.isEmpty()){
//            exceptionService.dataNotFoundByIdException("error.dist.module_kpi_tabs.not_available");
//        }
//        List<TabKPIMappingDTO> mappingDTO = counterRepository.getTabKPIConfigurationByTabIds(kpiTabs.stream().map(kpiTab -> kpiTab.getModuleId()).collect(toList()), level, refId);
//        return new InitialKPITabDistDataDTO(kpiTabs, mappingDTO);
//    }
    public List<BigInteger> getInitialTabKPIDataConf(String moduleId, Long refId, ConfLevel level) {
        List<TabKPIMappingDTO> tabKPIMappingDTOS=counterRepository.getTabKPIConfigurationByTabIds(Arrays.asList(moduleId),new ArrayList<>(),level,refId);
        if (tabKPIMappingDTOS == null || tabKPIMappingDTOS.isEmpty()) return new ArrayList<>();
        return tabKPIMappingDTOS.stream().map(tabKPIMappingDTO ->tabKPIMappingDTO.getKpiId()).collect(Collectors.toList());
    }

    public void addTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries, ConfLevel level, Long refId) {
        Long countryId = ConfLevel.COUNTRY.equals(level) ? refId : null;
        Long unitId = ConfLevel.UNIT.equals(level) ? refId : null;
        Long staffId = ConfLevel.STAFF.equals(level) ? refId : null;
        List<TabKPIMappingDTO> tabKPIMappingDTOS = counterRepository.getTabKPIConfigurationByTabIds(tabKPIEntries.getTabIds(),tabKPIEntries.getKpiIds(),level, refId);
//        List<ApplicableKPI> applicableKPIS = counterRepository.getKPIAssignmentsByKPIId(tabKPIEntries.getKpiIds());
//        if (tabKPIMappingDTOS.getKpiIds().size() != tabKPIEntries.size()) {
//            exceptionService.actionNotPermittedException("message.counter.kpi.notfound");
//        }
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap = new HashMap<>();
        List<TabKPIConf> entriesToSave = new ArrayList<>();
        tabKPIEntries.getTabIds().forEach(tabKpiId -> {
            tabKpiMap.put(tabKpiId, new HashMap<BigInteger, BigInteger>());
        });
        tabKPIMappingDTOS.forEach(tabKPIMappingDTO -> {
            tabKpiMap.get(tabKPIMappingDTO.getTabId()).put(tabKPIMappingDTO.getKpiId(), tabKPIMappingDTO.getKpiId());
        });
      tabKPIEntries.getTabIds().forEach(tabId->{tabKPIEntries.getKpiIds().forEach(kpiId->{
          if(tabKpiMap.get(tabId).get(kpiId)==null){
              entriesToSave.add(new TabKPIConf(tabId,kpiId,countryId,unitId,staffId,level));
          }
      });});
        if (entriesToSave.isEmpty()) {
            exceptionService.duplicateDataException("message.tab.kpi.notfound");
        }
        save(entriesToSave);
    }

    public void removeTabKPIEntries(TabKPIMappingDTO tabKPIMappingDTO,Long refId,ConfLevel level) {
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
      //  List<ApplicableKPI> applicableKPIS = counterRepository.getKPIAssignmentsByKPIId(accessGroupKPIConf.getKpiIds());
//        if (accessGroupKPIConf.getKpiIds().size() != kpiAssignments.size()) {
//            exceptionService.actionNotPermittedException("message.counter.kpi.notfound");
//        }
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
            exceptionService.dataNotFoundByIdException("message.accessgroup.kpi.notfound");
        }
        save(entriesToSave);

    }

    public void removeAccessGroupKPIEntries(AccessGroupMappingDTO accessGroupMappingDTO,Long refId,ConfLevel level) {
        if(ConfLevel.UNIT.equals(level)) {
            AccessGroupKPIEntry accessGroupKPIEntry = counterRepository.getAccessGroupKPIEntry(accessGroupMappingDTO,refId);
            if(!Optional.ofNullable(accessGroupKPIEntry).isPresent()){
                exceptionService.dataNotFoundByIdException("message.accessgroup.kpi.notfound");
            }
            List<Long> staffIds = genericIntegrationService.getStaffIdsByunitAndAccessGroupId(accessGroupKPIEntry.getUnitId(), accessGroupKPIEntry.getAccessGroupId());
            counterRepository.removeApplicableKPI(staffIds, accessGroupKPIEntry.getKpiId(), ConfLevel.STAFF);
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
//      List<ApplicableKPI> applicableKPIS=counterRepository.getApplicableKPIByKPIId(orgTypeKPIConf.getKpiIds());
//        if (orgTypeKPIConf.getKpiIds().size() != applicableKPIS.size()) {
//            exceptionService.actionNotPermittedException("message.counter.kpi.notfound");
//        }
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
            exceptionService.dataNotFoundByIdException("Organization Type KPI not found");
        }
            save(entriesToSave);
    }

    public void removeOrgTypeKPIEntries(OrgTypeMappingDTO orgTypeMappingDTO,Long countryId) {
        OrgTypeKPIEntry orgTypeKPIEntry=counterRepository.getOrgTypeKPIEntry(orgTypeMappingDTO,countryId);
        if(!Optional.ofNullable(orgTypeKPIEntry).isPresent()){
            exceptionService.dataNotFoundByIdException("message.orgtype.kpi.notfound");
        }
         List<Long> unitIds=genericIntegrationService.getOrganizationIdsBySubOrgId(orgTypeKPIEntry.getOrgTypeId());
        counterRepository.removeAccessGroupKPIEntry(unitIds,orgTypeKPIEntry.getKpiId());
        counterRepository.removeTabKPIEntry(unitIds,orgTypeKPIEntry.getKpiId(),ConfLevel.UNIT);
        counterRepository.removeApplicableKPI(unitIds,orgTypeKPIEntry.getKpiId(),ConfLevel.UNIT);
        counterRepository.removeEntityById(orgTypeKPIEntry.getId(),OrgTypeKPIEntry.class);
    }

    public void createDefalutStaffKPISetting(Long unitId,DefalutKPISettingDTO defalutKPISettingDTO) {
        List<ApplicableKPI> applicableKPISForUnit = counterRepository.getApplicableKPIByUnitOrCountryOrStaffId(unitId, ConfLevel.UNIT);
        if(applicableKPISForUnit.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.applicable.kpi.notfound");
        }
        List<BigInteger> applicableKpiIds = applicableKPISForUnit.stream().map(applicableKPI -> applicableKPI.getBaseKpiId()).collect(Collectors.toList());
        List<TabKPIConf> tabKPIConfKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConf = counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds, unitId, ConfLevel.UNIT);
        if(tabKPIConf.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.tab.kpi.notfound");
        }
        defalutKPISettingDTO.getStaffIds().forEach(staffId -> {
            tabKPIConf.stream().forEach(tabKPIConfKPI -> {
                tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(), tabKPIConfKPI.getKpiId(), null, null, staffId, ConfLevel.STAFF));
            });
        });
        List<ApplicableKPI> applicableKPIS = new ArrayList<>();
       applicableKpiIds.forEach(kpiId->{defalutKPISettingDTO.getStaffIds().forEach(staffId->{
           applicableKPIS.add(new ApplicableKPI(kpiId,kpiId,null,null,staffId,ConfLevel.STAFF));
       });
       });
       save(applicableKPIS);
       save(tabKPIConfKPIEntries);
    }

    public void createDefaultKpiSetting(Long unitId, DefalutKPISettingDTO defalutKPISettingDTO) {
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(defalutKPISettingDTO.getOrgTypeIds(), new ArrayList<>(), unitId);
        if(orgTypeMappingDTOS.isEmpty()){
                exceptionService.dataNotFoundByIdException("message.orgtype.kpi.notfound");
        }
        List<BigInteger> kpiIds = orgTypeMappingDTOS.stream().map(orgTypeMappingDTO -> orgTypeMappingDTO.getKpiId()).collect(Collectors.toList());
        List<ApplicableKPI> applicableKPIS = new ArrayList<>();
        kpiIds.forEach(kpiId -> {
            applicableKPIS.add(new ApplicableKPI(kpiId,kpiId,null, unitId, null, ConfLevel.UNIT));
        });
        if(Optional.ofNullable(defalutKPISettingDTO.getParentUnitId()).isPresent()) {
            setDefaultDateFromParentUnitTounit(defalutKPISettingDTO,unitId);
        }else{
            setDefaultDateFromCountryToUnit(defalutKPISettingDTO,unitId);
        }
        save(applicableKPIS);
    }

    public void setDefaultDateFromCountryToUnit(DefalutKPISettingDTO defalutKPISettingDTO,Long unitId) {
        List<AccessGroupKPIEntry> accessGroupKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConfKPIEntries=new ArrayList<>();
        List<ApplicableKPI> applicableKPISForUnit=counterRepository.getApplicableKPIByUnitOrCountryOrStaffId(defalutKPISettingDTO.getCountryId(),ConfLevel.COUNTRY);
        if(applicableKPISForUnit.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.applicable.kpi.notfound");
        }
        List<BigInteger> applicableKpiIds=applicableKPISForUnit.stream().map(applicableKPI -> applicableKPI.getBaseKpiId()).collect(Collectors.toList());
        List<Long> countryAccessGroupIds = defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().keySet().stream().collect(Collectors.toList());
        List<AccessGroupMappingDTO> accessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(countryAccessGroupIds, new ArrayList<>(), ConfLevel.COUNTRY, defalutKPISettingDTO.getCountryId());
        if(accessGroupMappingDTOS.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.accessgroup.kpi.notfound");
        }
        accessGroupMappingDTOS.forEach(accessGroupMappingDTO -> {
            accessGroupKPIEntries.add(new AccessGroupKPIEntry(defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().get(accessGroupMappingDTO.getAccessGroupId()), accessGroupMappingDTO.getKpiId(), null, unitId, ConfLevel.UNIT));
        });
        List<TabKPIConf> tabKPIConf=counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds,defalutKPISettingDTO.getCountryId(),ConfLevel.COUNTRY);
        if(tabKPIConf.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.tab.kpi.notfound");
        }
        tabKPIConf.stream().forEach(tabKPIConfKPI->{
            tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(),tabKPIConfKPI.getKpiId(),null,unitId,null,ConfLevel.UNIT));
        });
        save(tabKPIConfKPIEntries);
        save(accessGroupKPIEntries);
    }
    public void setDefaultDateFromParentUnitTounit(DefalutKPISettingDTO defalutKPISettingDTO,Long unitId){
        List<AccessGroupKPIEntry> accessGroupKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConfKPIEntries=new ArrayList<>();
        List<ApplicableKPI> applicableKPISForUnit=counterRepository.getApplicableKPIByUnitOrCountryOrStaffId(defalutKPISettingDTO.getParentUnitId(),ConfLevel.UNIT);
        if(applicableKPISForUnit.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.applicable.kpi.notfound");
        }
        List<BigInteger> applicableKpiIds=applicableKPISForUnit.stream().map(applicableKPI -> applicableKPI.getBaseKpiId()).collect(Collectors.toList());
        List<AccessGroupKPIEntry> accessGroupMappingDTOSForunit=counterRepository.getAccessGroupKPIByUnitIdAndKpiIds(applicableKpiIds,defalutKPISettingDTO.getParentUnitId());
        if(accessGroupMappingDTOSForunit.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.accessgroup.kpi.notfound");
        }
        accessGroupMappingDTOSForunit.stream().forEach(accessGroupKPIEntry -> {
            accessGroupKPIEntries.add(new AccessGroupKPIEntry(accessGroupKPIEntry.getAccessGroupId(),accessGroupKPIEntry.getKpiId(),null,unitId,ConfLevel.UNIT));
        });
        List<TabKPIConf> tabKPIConf=counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds,defalutKPISettingDTO.getParentUnitId(),ConfLevel.UNIT);
        tabKPIConf.stream().forEach(tabKPIConfKPI->{
            tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(),tabKPIConfKPI.getKpiId(),null,unitId,null,ConfLevel.UNIT));
        });
        save(tabKPIConfKPIEntries);
        save(accessGroupKPIEntries);
    }

}


/*//TODO harish do not delete

//    public void createDefaultKpiSetting(Long unitId, DefalutKPISettingDTO defalutKPISettingDTO) {
//        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(defalutKPISettingDTO.getOrgTypeIds(), unitId);
//        List<BigInteger> kpiIds = orgTypeMappingDTOS.stream().map(orgTypeMappingDTO -> orgTypeMappingDTO.getKpiId()).collect(Collectors.toList());
//        List<ApplicableKPI> kpiAssignments = new ArrayList<>();
//        Map<BigInteger, BigInteger> KpiIdAndAssignmentKPIIds = new HashMap<>();
//        kpiIds.forEach(kpiId -> {
//            kpiAssignments.add(new ApplicableKPI(kpiId, null, unitId, null, ConfLevel.UNIT));
//        });
//        save(kpiAssignments);
//        kpiAssignments.forEach(kpTab
// iAssignment -> {
//            KpiIdAndAssignmentKPIIds.put(kpiAssignment.getActiveKpiId(), kpiAssignment.getId());
//        });
//        List<AccessGroupKPIEntry> accessGroupKPIEntrie = new ArrayList<>();
//        List<Long> countryAccessGroupIds = defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().keySet().stream().collect(Collectors.toList());
//        List<AccessGroupMappingDTO> accessGroupKPIEntries = counterRepository.getAccessGroupKPIEntryAccessGroupIds(countryAccessGroupIds, ConfLevel.COUNTRY, defalutKPISettingDTO.getCountryId());
//        accessGroupKPIEntries.forEach(accessGroupMappingDTO -> {
//                accessGroupKPIEntrie.add(new AccessGroupKPIEntry(defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().get(accessGroupMappingDTO.getAccessGroupId()), KpiIdAndAssignmentKPIIds.get(accessGroupMappingDTO.getKpiId()), null, unitId, ConfLevel.UNIT));
//        });
//        save(accessGroupKPIEntrie);
//
//    }
//    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId){
//        Map<String, List<BigInteger>> accessGroupKPIsMap = new HashMap<>();
//        List<OrgTypeKPIEntry> orgTypeKPIEntries = counterRepository.getOrgTypeKPIConfigurationByOrgTypeId(Arrays.asList(orgTypeId));
//        if(orgTypeKPIEntries==null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
//        return orgTypeKPIEntries.parallelStream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
//    }


    //    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId,Long countryId) {
//        List<OrgTypeMappingDTO> orgTypeKPIEntries = counterRepository.getOrgTypeKPIEntryOrgTypeIds(Arrays.asList(orgTypeId),countryId);
//        if (orgTypeKPIEntries == null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
//        return orgTypeKPIEntries.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
//    }



//    public List<BigInteger> getInitialTabKPIDataConf(String moduleId, Long refId, ConfLevel level) {
//        List<TabKPIMappingDTO> tabKPIMappingDTOS=counterRepository.getTabKPIConfigurationByTabIds(Arrays.asList(moduleId),level,refId);
//        if (tabKPIMappingDTOS == null || tabKPIMappingDTOS.isEmpty()) return new ArrayList<>();
//        return tabKPIMappingDTOS.stream().map(tabKPIMappingDTO ->tabKPIMappingDTO.getKpiId()).collect(Collectors.toList());
//    }

//    public Map<Long, List<BigInteger>> getInitialAccessGroupKPIDataConf(List<Long> accessGroupIds){
//        Map<Long, List<BigInteger>> accessGroupKPIMap = new HashMap<>();
//        accessGroupIds.forEach(accessGroupId -> {
//            accessGroupKPIMap.put(accessGroupId, new ArrayList<>());
//        });
//
//        List<AccessGroupKPIEntry> accessGroupKPIEntries = counterRepository.getAccessGroupKPIConfigurationByAccessGroupId(accessGroupIds);
//        accessGroupKPIEntries.forEach(accessGroupKPIEntry -> {
//            accessGroupKPIMap.get(accessGroupKPIEntry.getAccessGroupId()).add(accessGroupKPIEntry.getKpiId());
//        });
//        return accessGroupKPIMap;
//    }
//    public List<BigInteger> getInitialAccessGroupKPIDataConf(Long accessGroupId,Long refId,ConfLevel level) {
//        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(Arrays.asList(accessGroupId),level,refId);
//        if (AccessGroupMappingDTOS == null || AccessGroupMappingDTOS.isEmpty()) return new ArrayList<>();
//        return AccessGroupMappingDTOS.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
//    }
 */