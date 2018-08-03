package com.kairos.service.counter;


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

    public List<KPI> getKPIsList(Long refId, ConfLevel confLevel) {
        return counterRepository.getEntityItemList(KPI.class);
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
        List<BigInteger> kpiAssignmentIds = categoryKPIConfs.stream().map(categoryKPIConf -> categoryKPIConf.getKpiAssignmentId()).collect(toList());
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
//            exceptionService.actionNotPermittedException("KPi id not valid");
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
        if (!entriesToSave.isEmpty()) {
            save(entriesToSave);
        }
    }

    public Boolean removeTabKPIEntries(TabKPIMappingDTO tabKPIMappingDTO,Long refId,ConfLevel level) {
       counterRepository.removeTabKPIConfiguration(tabKPIMappingDTO,refId,level);
        return true;
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
//            exceptionService.actionNotPermittedException("KPi id not valid");
//        }
        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(accessGroupKPIConf.getAccessGroupIds(),accessGroupKPIConf.getKpiIds(),level,countryId);
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
        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public boolean removeAccessGroupKPIEntries(AccessGroupMappingDTO accessGroupMappingDTO,Long refId,ConfLevel level) {
        if(ConfLevel.UNIT.equals(level)) {
            AccessGroupKPIEntry accessGroupKPIEntry = counterRepository.getAccessGroupKPIEntry(accessGroupMappingDTO,refId);
            List<Long> staffIds = genericIntegrationService.getStaffIdsByunitAndAccessGroupId(accessGroupKPIEntry.getUnitId(), accessGroupKPIEntry.getAccessGroupId());
            counterRepository.removeApplicableKPI(staffIds, accessGroupKPIEntry.getKpiId(), ConfLevel.STAFF);
            counterRepository.removeTabKPIEntry(staffIds, accessGroupKPIEntry.getKpiId(), ConfLevel.STAFF);
            counterRepository.removeEntityById(accessGroupKPIEntry.getId(), AccessGroupKPIEntry.class);
        }else{
            counterRepository.removeAccessGroupKPIEntryForCountry(accessGroupMappingDTO,refId);
        }
        return true;// counterRepository.removeAccessGroupKPIEntry(BigInteger.valueOf(accessGroupId));
    }

    //setting orgType-KPI configuration


    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId,Long countryId) {
        List<OrgTypeMappingDTO> orgTypeKPIEntries = counterRepository.getOrgTypeKPIEntryOrgTypeIds(Arrays.asList(orgTypeId),new ArrayList<>(),countryId);
        if (orgTypeKPIEntries == null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
        return orgTypeKPIEntries.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
    }

    public void addOrgTypeKPIEntries(OrgTypeKPIConfDTO orgTypeKPIConf, Long countryId) {
      List<ApplicableKPI> applicableKPIS=counterRepository.getApplicableKPIByKPIId(orgTypeKPIConf.getKpiIds());
        if (orgTypeKPIConf.getKpiIds().size() != applicableKPIS.size()) {
            exceptionService.actionNotPermittedException("KPi id not valid");
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
        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public boolean removeOrgTypeKPIEntries(OrgTypeMappingDTO orgTypeMappingDTO,Long countryId) {
        OrgTypeKPIEntry orgTypeKPIEntries=counterRepository.getOrgTypeKPIEntry(orgTypeMappingDTO,countryId);
         List<Long> unitIds=genericIntegrationService.getOrganizationIdsBySubOrgId(orgTypeKPIEntries.getOrgTypeId());
        counterRepository.removeAccessGroupKPIEntry(unitIds,orgTypeKPIEntries.getKpiId());
        counterRepository.removeTabKPIEntry(unitIds,orgTypeKPIEntries.getKpiId(),ConfLevel.UNIT);
        counterRepository.removeApplicableKPI(unitIds,orgTypeKPIEntries.getKpiId(),ConfLevel.UNIT);
        counterRepository.removeEntityById(orgTypeKPIEntries.getId(),OrgTypeKPIEntry.class);
       return true;
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