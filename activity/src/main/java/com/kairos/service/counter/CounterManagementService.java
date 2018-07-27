package com.kairos.service.counter;


import com.kairos.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.activity.counter.distribution.access_group.RoleCounterDTO;
import com.kairos.activity.counter.distribution.category.CategoryAssignmentDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.activity.counter.distribution.category.InitialKPICategoryDistDataDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
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

import static java.util.stream.Collectors.toList;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class CounterManagementService extends MongoBaseService{
    @Inject private CounterRepository counterRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private GenericIntegrationService genericIntegrationService;

    private final static Logger logger = LoggerFactory.getLogger(CounterManagementService.class);

    private void overrideOldObject(MongoBaseEntity newEntity, MongoBaseEntity oldEntity){
        BeanUtils.copyProperties(oldEntity, newEntity);
    }

    //get role wise moduleCounterId mapping
    public List<RoleCounterDTO> getRoleCounterMapping(BigInteger unitId){
        return counterRepository.getRoleAndModuleCounterIdMapping(unitId);
    }

    public List<KPI> getKPIsList(Long refId, ConfLevel confLevel){
        return counterRepository.getEntityItemList(KPI.class);
    }

    public InitialKPICategoryDistDataDTO getInitialCategoryKPIDistData(Long refId, ConfLevel level){
        List<CategoryAssignmentDTO> categories = counterRepository.getCategoryAssignments(null, level, refId);
        List<BigInteger> categoryAssignmentIds = categories.parallelStream().map(categoryAssignment -> categoryAssignment.getId()).collect(toList());
        List<CategoryKPIMappingDTO> categoryKPIMapping = counterRepository.getKPIsMappingForCategories(categoryAssignmentIds);
        return new InitialKPICategoryDistDataDTO(categories, categoryKPIMapping);
    }

    public void updateCategoryKPIsDistribution(CategoryKPIsDTO categoryKPIsDetails, ConfLevel level, Long refId){
        CategoryAssignment categoryAssignment = counterRepository.getCategoryAssignment(categoryKPIsDetails.getCategoryId(), level, refId);
        List<KPIAssignment> kpiAssignments = counterRepository.getKPIAssignments(categoryKPIsDetails.getKpiIds(), level, refId);
        List<CategoryKPIConf> categoryKPIConfs = counterRepository.getCategoryKPIConfs(categoryAssignment.getId());
        List<BigInteger> kpiAssignmentIds = categoryKPIConfs.stream().map(categoryKPIConf -> categoryKPIConf.getKpiAssignmentId()).collect(toList());
        kpiAssignments = kpiAssignments.parallelStream().filter(kpiAssignment -> kpiAssignmentIds.contains(kpiAssignment.getId())).collect(toList());
        if(kpiAssignments.isEmpty()) exceptionService.invalidOperationException("error.dist.category_kpi.invalid_operation");
        List<CategoryKPIConf> newCategoryKPIConfs = new ArrayList<>();
        kpiAssignments.parallelStream().forEach(kpiAssignment -> newCategoryKPIConfs.add(new CategoryKPIConf(kpiAssignment.getKpiId(), categoryAssignment.getId())));
        save(newCategoryKPIConfs);
    }

    //settings for KPI-Module configuration

    public InitialKPITabDistDataDTO getInitialTabKPIDataConf(String moduleId, Long refId, ConfLevel level){
        List<KPIAccessPageDTO> kpiTabs= genericIntegrationService.getKPIEnabledTabsForModule(moduleId, refId);
        Map<String, List<BigInteger>> tabKPIsMap = new HashMap<>();
        if(kpiTabs != null && kpiTabs.isEmpty()){
            exceptionService.dataNotFoundByIdException("error.dist.module_kpi_tabs.not_available");
        }
        List<TabKPIMappingDTO> mappingDTO = counterRepository.getTabKPIConfigurationByTabIds(kpiTabs.stream().map(kpiTab -> kpiTab.getModuleId()).collect(toList()), level, refId);
        return new InitialKPITabDistDataDTO(kpiTabs, mappingDTO);
    }

    public void addTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries, ConfLevel level, Long refId){
        List<TabKPIMappingDTO> tabKPIMappingList= counterRepository.getTabKPIConfigurationByTabIds(tabKPIEntries.getTabIds(), level, refId);
        Map<String, Map<BigInteger, BigInteger>> tabKPIsMap = new HashMap<>();
        List<TabKPIEntry> entriesToSave = new ArrayList<>();
        tabKPIEntries.getTabIds().forEach(tabId -> {
            tabKPIsMap.put(tabId, new HashMap<BigInteger, BigInteger>());
        });

        tabKPIMappingList.parallelStream().forEach(tabKPIEntry -> {
            HashMap<BigInteger, BigInteger> kpiMap = new HashMap<>();
            tabKPIEntry.getKpiIds().parallelStream().forEach(kpiId -> kpiMap.put(kpiId, kpiId));
            tabKPIsMap.put(tabKPIEntry.getTabId(), kpiMap);
        });

        tabKPIEntries.getTabIds().parallelStream().forEach(tabId ->{
            tabKPIEntries.getKpiIds().forEach(kpiId -> {
                if(tabKPIsMap.get(tabId).get(kpiId) == null){
                    entriesToSave.add(new TabKPIEntry(tabId, kpiId));
                }
            });
        });

        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public void removeTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries){
        //this is only for single document deletion but the document identification fields are combination of two list.
        tabKPIEntries.getTabIds().forEach(tabId -> {
            tabKPIEntries.getKpiIds().forEach(kpiId -> {
                counterRepository.removeTabKPIConfiguration(new TabKPIEntry(tabId, kpiId));
            });
        });
    }

    //setting accessGroup-KPI configuration
    public Map<Long, List<BigInteger>> getInitialAccessGroupKPIDataConf(List<Long> accessGroupIds){
        Map<Long, List<BigInteger>> accessGroupKPIMap = new HashMap<>();
        accessGroupIds.forEach(accessGroupId -> {
            accessGroupKPIMap.put(accessGroupId, new ArrayList<>());
        });

        List<AccessGroupKPIEntry> accessGroupKPIEntries = counterRepository.getAccessGroupKPIConfigurationByAccessGroupId(accessGroupIds);
        accessGroupKPIEntries.forEach(accessGroupKPIEntry -> {
            accessGroupKPIMap.get(accessGroupKPIEntry.getAccessGroupId()).add(accessGroupKPIEntry.getKpiId());
        });
        return accessGroupKPIMap;
    }

    public void addAccessGroupKPIEntries(AccessGroupKPIConfDTO accessGroupKPIConf){
        List<AccessGroupKPIEntry> entries = counterRepository.getAccessGroupKPIConfigurationByAccessGroupId(accessGroupKPIConf.getAccessGroupIds());
        Map<Long, Map<BigInteger, BigInteger>> tabKPIsMap = new HashMap<>();
        List<AccessGroupKPIEntry> entriesToSave = new ArrayList<>();
        accessGroupKPIConf.getAccessGroupIds().forEach(accessGroupId -> {
            tabKPIsMap.put(accessGroupId, new HashMap<BigInteger, BigInteger>());
        });

        entries.parallelStream().forEach(accessGroupKPIEntry -> {
            tabKPIsMap.get(accessGroupKPIEntry.getAccessGroupId()).put(accessGroupKPIEntry.getKpiId(), accessGroupKPIEntry.getKpiId());
        });

        accessGroupKPIConf.getAccessGroupIds().parallelStream().forEach(accessGroupId ->{
            accessGroupKPIConf.getKpiIds().forEach(kpiId -> {
                if(tabKPIsMap.get(accessGroupId).get(kpiId) == null){
                    entriesToSave.add(new AccessGroupKPIEntry(accessGroupId, kpiId));
                }
            });
        });
        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public void removeOrgTypeKPIEntries(AccessGroupKPIConfDTO accessGroupKPIConf){
        accessGroupKPIConf.getAccessGroupIds().forEach(accessGroupId -> {
            accessGroupKPIConf.getKpiIds().forEach(kpiId -> {
                counterRepository.removeAccessGroupKPIEntry(new AccessGroupKPIEntry(accessGroupId, kpiId));
            });
        });
    }

    //setting orgType-KPI configuration
    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId){
        Map<String, List<BigInteger>> accessGroupKPIsMap = new HashMap<>();
        List<OrgTypeKPIEntry> orgTypeKPIEntries = counterRepository.getOrgTypeKPIConfigurationByOrgTypeId(Arrays.asList(orgTypeId));
        if(orgTypeKPIEntries==null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
        return orgTypeKPIEntries.parallelStream().map(entry -> entry.getKpiId()).collect(Collectors.toList());
    }

    public void addOrgTypeKPIEntries(OrgTypeKPIConfDTO orgTypeKPIConf){
        List<OrgTypeKPIEntry> entries = counterRepository.getOrgTypeKPIConfigurationByOrgTypeId(orgTypeKPIConf.getOrgTypeIds());
        Map<Long, Map<BigInteger, BigInteger>> orgTypeKPIMap = new HashMap<>();
        List<OrgTypeKPIEntry> entriesToSave = new ArrayList<>();
        orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId -> {
            orgTypeKPIMap.put(orgTypeId, new HashMap<BigInteger, BigInteger>());
        });

        entries.parallelStream().forEach(orgTypeKPIEntry -> {
            orgTypeKPIMap.get(orgTypeKPIEntry.getOrgTypeId()).put(orgTypeKPIEntry.getKpiId(), orgTypeKPIEntry.getKpiId());
        });

        orgTypeKPIConf.getOrgTypeIds().parallelStream().forEach(orgTypeId ->{
            orgTypeKPIConf.getKpiIds().forEach(kpiId -> {
                if(orgTypeKPIMap.get(orgTypeId).get(kpiId) == null){
                    entriesToSave.add(new OrgTypeKPIEntry(orgTypeId, kpiId));
                }
            });
        });
        if(!entriesToSave.isEmpty())
            save(entriesToSave);
    }

    public void removeOrgTypeKPIEntries(OrgTypeKPIConfDTO orgTypeKPIConf){
        orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId -> {
            orgTypeKPIConf.getKpiIds().forEach(kpiId -> {
                counterRepository.removeOrgTypeKPIEntry(new OrgTypeKPIEntry(orgTypeId, kpiId));
            });
        });
    }

}
