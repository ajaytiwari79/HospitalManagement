package com.kairos.service.counter;


import com.kairos.activity.counter.DefalutKPISettingDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.activity.counter.distribution.access_group.RoleCounterDTO;
import com.kairos.activity.counter.distribution.category.CategoryAssignmentDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.activity.counter.distribution.category.InitialKPICategoryDistDataDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.activity.counter.distribution.tab.InitialKPITabDistDataDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.access_page.KPIAccessPageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
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
        List<KPIAssignment> kpiAssignments = counterRepository.getKPIAssignments(categoryKPIsDetails.getKpiIds(), level, refId);
        List<CategoryKPIConf> categoryKPIConfs = counterRepository.getCategoryKPIConfs(categoryAssignment.getId());
        List<BigInteger> kpiAssignmentIds = categoryKPIConfs.stream().map(categoryKPIConf -> categoryKPIConf.getKpiAssignmentId()).collect(toList());
        kpiAssignments = kpiAssignments.parallelStream().filter(kpiAssignment -> kpiAssignmentIds.contains(kpiAssignment.getId())).collect(toList());
        if (kpiAssignments.isEmpty())
            exceptionService.invalidOperationException("error.dist.category_kpi.invalid_operation");
        List<CategoryKPIConf> newCategoryKPIConfs = new ArrayList<>();
        kpiAssignments.parallelStream().forEach(kpiAssignment -> newCategoryKPIConfs.add(new CategoryKPIConf(kpiAssignment.getKpiId(), categoryAssignment.getId())));
        save(newCategoryKPIConfs);
    }

    //settings for KPI-Module configuration

    public List<BigInteger> getInitialTabKPIDataConf(String moduleId, Long refId, ConfLevel level) {
       List<TabKPIMappingDTO> tabKPIMappingDTOS=counterRepository.getTabKPIConfigurationByTabIds(Arrays.asList(moduleId),level,refId);
        if (tabKPIMappingDTOS == null || tabKPIMappingDTOS.isEmpty()) return new ArrayList<>();
       return tabKPIMappingDTOS.stream().map(tabKPIMappingDTO ->tabKPIMappingDTO.getKpiId()).collect(Collectors.toList());
        }

    public void addTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries, ConfLevel level, Long refId) {
        Long countryId = ConfLevel.COUNTRY.equals(level) ? refId : null;
        Long unitId = ConfLevel.UNIT.equals(level) ? refId : null;
        Long staffId = ConfLevel.STAFF.equals(level) ? refId : null;
        List<TabKPIMappingDTO> tabKPIMappingDTOS = counterRepository.getTabKPIConfigurationByTabIds(tabKPIEntries.getTabIds(), level, refId);
        List<KPIAssignment> kpiAssignments = counterRepository.getKPIAssignmentsByKPIId(tabKPIEntries.getKpiIds());
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap = new HashMap<>();
        List<TabKPIConf> entriesToSave = new ArrayList<>();
        tabKPIEntries.getTabIds().forEach(tabKpiId -> {
            tabKpiMap.put(tabKpiId, new HashMap<BigInteger, BigInteger>());
        });
        tabKPIMappingDTOS.forEach(tabKPIMappingDTO -> {
            tabKpiMap.get(tabKPIMappingDTO.getTabId()).put(tabKPIMappingDTO.getKpiAssignmentId(), tabKPIMappingDTO.getKpiAssignmentId());
        });
        kpiAssignments.forEach(kpiAssignment -> {
            tabKPIEntries.getTabIds().forEach(tabId -> {
                if (tabKpiMap.get(tabId).get(kpiAssignment.getId()) == null) {
                    entriesToSave.add(new TabKPIConf(tabId, kpiAssignment.getId(), countryId, unitId, staffId, level));
                }
            });
        });
        if (!entriesToSave.isEmpty()) {
            save(entriesToSave);
        }
    }

    public Boolean removeTabKPIEntries(Long tabKPIEntriyId) {
      return counterRepository.removeTabKPIEntry(BigInteger.valueOf(tabKPIEntriyId));
    }

    //setting accessGroup-KPI configuration
    public List<BigInteger> getInitialAccessGroupKPIDataConf(Long accessGroupId,Long refId,ConfLevel level) {
        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(Arrays.asList(accessGroupId),level,refId);
        if (AccessGroupMappingDTOS == null || AccessGroupMappingDTOS.isEmpty()) return new ArrayList<>();
        return AccessGroupMappingDTOS.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
    }

    public void addAccessGroupKPIEntries(AccessGroupKPIConfDTO accessGroupKPIConf,Long refId,ConfLevel level) {
        Long countryId = ConfLevel.COUNTRY.equals(level)? refId: null;
        Long unitId=ConfLevel.UNIT.equals(level)? refId: null;
        Map<Long, Map<BigInteger, BigInteger>> tabKPIsMap = new HashMap<>();
        List<AccessGroupKPIEntry> entriesToSave = new ArrayList<>();
        List<KPIAssignment> kpiAssignments = counterRepository.getKPIAssignmentsByKPIId(accessGroupKPIConf.getKpiIds());
//        if (accessGroupKPIConf.getKpiIds().size() != kpiAssignments.size()) {
//            exceptionService.actionNotPermittedException("KPi id not valid");
//        }
        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(accessGroupKPIConf.getAccessGroupIds(),level,countryId);
        Map<Long, Map<BigInteger, BigInteger>> orgTypeKPIMap = new HashMap<>();
        accessGroupKPIConf.getAccessGroupIds().forEach(orgTypeId -> {
            orgTypeKPIMap.put(orgTypeId, new HashMap<BigInteger, BigInteger>());
        });
        AccessGroupMappingDTOS.forEach(AccessGroupMappingDTO -> {
            orgTypeKPIMap.get(AccessGroupMappingDTO.getAccessGroupId()).put(AccessGroupMappingDTO.getKpiAssignmentId(), AccessGroupMappingDTO.getKpiAssignmentId());
        });
        kpiAssignments.forEach(kpiAssignment -> {accessGroupKPIConf.getAccessGroupIds().forEach(accessGroupId->{
            if(orgTypeKPIMap.get(accessGroupId).get(kpiAssignment.getId()) == null){
                entriesToSave.add(new AccessGroupKPIEntry(accessGroupId, kpiAssignment.getId(),countryId,unitId,level));
            } });
        });
        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public boolean removeAccessGroupKPIEntries(Long accessGroupId) {
        return counterRepository.removeAccessGroupKPIEntry(BigInteger.valueOf(accessGroupId));
    }

    //setting orgType-KPI configuration
    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId,Long countryId) {
        List<OrgTypeMappingDTO> orgTypeKPIEntries = counterRepository.getOrgTypeKPIEntryOrgTypeIds(Arrays.asList(orgTypeId),countryId);
        if (orgTypeKPIEntries == null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
        return orgTypeKPIEntries.stream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
    }

    public void addOrgTypeKPIEntries(OrgTypeKPIConfDTO orgTypeKPIConf, Long countryId) {
        List<OrgTypeKPIEntry> entriesToSave = new ArrayList<>();
        List<KPIAssignment> kpiAssignments = counterRepository.getKPIAssignmentsByKPIId(orgTypeKPIConf.getKpiIds());
//        if (orgTypeKPIConf.getKpiIds().size() != kpiAssignments.size()) {
//            exceptionService.actionNotPermittedException("KPi id not valid");
//        }
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(orgTypeKPIConf.getOrgTypeIds(), countryId);
        Map<Long, Map<BigInteger, BigInteger>> orgTypeKPIMap = new HashMap<>();
        orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId -> {
            orgTypeKPIMap.put(orgTypeId, new HashMap<BigInteger, BigInteger>());
        });
        orgTypeMappingDTOS.forEach(orgTypeMappingDTO -> {
            orgTypeKPIMap.get(orgTypeMappingDTO.getOrgTypeId()).put(orgTypeMappingDTO.getKpiAssignmentId(), orgTypeMappingDTO.getKpiAssignmentId());
        });
        kpiAssignments.forEach(kpiAssignment -> {orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId->{
            if(orgTypeKPIMap.get(orgTypeId).get(kpiAssignment.getId()) == null){
                entriesToSave.add(new OrgTypeKPIEntry(orgTypeId, kpiAssignment.getId()));
            } });
        });
        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public boolean removeOrgTypeKPIEntries(Long orgTypeId,Long countryId) {
        return counterRepository.removeOrgTypeKPIEntry(BigInteger.valueOf(orgTypeId));
    }

    public void createDefaultKpiSetting(Long unitId, DefalutKPISettingDTO defalutKPISettingDTO) {
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(defalutKPISettingDTO.getOrgTypeIds(), unitId);
        List<BigInteger> kpiIds = orgTypeMappingDTOS.stream().map(orgTypeMappingDTO -> orgTypeMappingDTO.getKpiId()).collect(Collectors.toList());
        List<KPIAssignment> kpiAssignments = new ArrayList<>();
        Map<BigInteger, BigInteger> KpiIdAndAssignmentKPIIds = new HashMap<>();
        kpiIds.forEach(kpiId -> {
            kpiAssignments.add(new KPIAssignment(kpiId, null, unitId, null, ConfLevel.UNIT));
        });
        save(kpiAssignments);
        kpiAssignments.forEach(kpiAssignment -> {
            KpiIdAndAssignmentKPIIds.put(kpiAssignment.getKpiId(), kpiAssignment.getId());
        });
        List<AccessGroupKPIEntry> accessGroupKPIEntrie = new ArrayList<>();
        List<Long> countryAccessGroupIds = defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().keySet().stream().collect(Collectors.toList());
        List<AccessGroupMappingDTO> accessGroupKPIEntries = counterRepository.getAccessGroupKPIEntryAccessGroupIds(countryAccessGroupIds, ConfLevel.COUNTRY, defalutKPISettingDTO.getCountryId());
        accessGroupKPIEntries.forEach(accessGroupMappingDTO -> {
                accessGroupKPIEntrie.add(new AccessGroupKPIEntry(defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().get(accessGroupMappingDTO.getAccessGroupId()), KpiIdAndAssignmentKPIIds.get(accessGroupMappingDTO.getKpiId()), null, unitId, ConfLevel.UNIT));
        });
        save(accessGroupKPIEntrie);

    }
}


/*
   List<AccessGroupKPIEntry> entries = counterRepository.getAccessGroupKPIConfigurationByAccessGroupId(accessGroupKPIConf.getAccessGroupIds());
        Map<Long, Map<BigInteger, BigInteger>> tabKPIsMap = new HashMap<>();
        List<AccessGroupKPIEntry> entriesToSave = new ArrayList<>();
        accessGroupKPIConf.getAccessGroupIds().forEach(accessGroupId -> {
            tabKPIsMap.put(accessGroupId, new HashMap<BigInteger, BigInteger>());
        });

        entries.parallelStream().forEach(accessGroupKPIEntry -> {
            tabKPIsMap.get(accessGroupKPIEntry.getAccessGroupId()).put(accessGroupKPIEntry.getKpiId(), accessGroupKPIEntry.getKpiId());
        });

        accessGroupKPIConf.getAccessGroupIds().parallelStream().forEach(accessGroupId -> {
            accessGroupKPIConf.getKpiIds().forEach(kpiId -> {
                if (tabKPIsMap.get(accessGroupId).get(kpiId) == null) {
                    entriesToSave.add(new AccessGroupKPIEntry(accessGroupId, kpiId,countryId,unitId,level));
                }
            });
        });
        if (!entriesToSave.isEmpty())
            save(entriesToSave);


             List<TabKPIMappingDTO> tabKPIMappingList = counterRepository.getTabKPIConfigurationByTabIds(tabKPIEntries.getTabIds(), level, refId);
        Map<String, Map<BigInteger, BigInteger>> tabKPIsMap = new HashMap<>();
        List<TabKPIEntry> entriesToSave = new ArrayList<>();
        tabKPIEntries.getTabIds().forEach(tabId -> {
            tabKPIsMap.put(tabId, new HashMap<BigInteger, BigInteger>());
        });

        tabKPIMappingList.parallelStream().forEach(tabKPIEntry -> {
            HashMap<BigInteger, BigInteger> kpiMap = new HashMap<>();
            tabKPIEntry.getKpiAssignmentId().parallelStream().forEach(kpiId -> kpiMap.put(kpiId, kpiId));
            tabKPIsMap.put(tabKPIEntry.getTabId(), kpiMap);
        });

        tabKPIEntries.getTabIds().parallelStream().forEach(tabId -> {
            tabKPIEntries.getKpiIds().forEach(kpiId -> {
                if (tabKPIsMap.get(tabId).get(kpiId) == null) {
                    entriesToSave.add(new TabKPIEntry(tabId, kpiId));
                }
            });
        });

        if (!entriesToSave.isEmpty())
            save(entriesToSave);
 */