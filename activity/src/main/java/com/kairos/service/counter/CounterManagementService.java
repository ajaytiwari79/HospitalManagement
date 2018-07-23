package com.kairos.service.counter;


import com.kairos.activity.counter.*;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.counter.CounterServiceMapping;
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
    @Inject private CounterServiceMapping counterServiceMapping;
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
        List<KPI> kpisList = getKPIsList(refId, level);
        List<KPICategory> categories = counterRepository.getEntityItemList(KPICategory.class);
        Map<BigInteger, List<BigInteger>> categoryKPIsMap = new HashMap<>();
        categories.forEach(category -> {
            categoryKPIsMap.put(category.getId(), new ArrayList<>());
        });
        kpisList.forEach(kpi -> {
            if(kpi.getCategoryId() != null){
                categoryKPIsMap.get(kpi.getCategoryId()).add(kpi.getId());
            }
        });

        return new InitialKPICategoryDistDataDTO(categories, categoryKPIsMap);
    }

    public void updateCategoryKPIsDistribution(CategoryKPIsDTO categoryKPIsDetails){
        List<KPI> kpis = getKPIsList();
        Map<BigInteger, List<KPI>> categoryKPIMap = kpis.stream().collect(Collectors.groupingBy(kpi-> (kpi.getCategoryId()!=null)?kpi.getCategoryId():BigInteger.valueOf(-1), Collectors.toList()));
        Map<BigInteger, Counter> kpisIdMap = new HashMap<>();
        kpis.parallelStream().forEach(kpi -> {
            kpisIdMap.put(kpi.getId(), kpi);
        });

        categoryKPIsDetails.getKpiIds().forEach(kpiId ->{
            if(categoryKPIsDetails.getCategoryId().equals(kpisIdMap.get(kpiId).getCategoryId()))
                exceptionService.invalidOperationException("error.dist.category_kpi.invalid_operation");
            kpisIdMap.get(kpiId).setCategoryId(categoryKPIsDetails.getCategoryId());
        });

        save(kpis);
    }

    //settings for KPI-Module configuration

    public InitialKPITabDistDataDTO getInitialTabKPIDataConf(String moduleId, Long countryId){
        List<KPIAccessPageDTO> kpiTabs= genericIntegrationService.getKPIEnabledTabsForModule(moduleId, countryId);
        Map<String, List<BigInteger>> tabKPIsMap = new HashMap<>();
        if(kpiTabs != null && kpiTabs.isEmpty()){
            exceptionService.dataNotFoundByIdException("error.dist.module_kpi_tabs.not_available");
        }
        kpiTabs.forEach(tabKPI -> {
            tabKPIsMap.put(tabKPI.getModuleId(), new ArrayList<>());
        });

        List<TabKPIEntry> tabKPIEntries = counterRepository.getTabKPIConfgiurationByTabId(kpiTabs.stream().map(kpiTab -> kpiTab.getModuleId()).collect(toList()));
        tabKPIEntries.forEach(tabKPI -> {
            tabKPIsMap.get(tabKPI.getTabId()).add(tabKPI.getKpiId());
        });
        return new InitialKPITabDistDataDTO(kpiTabs, tabKPIsMap);
    }

    public void addTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries){
        List<TabKPIEntry> entries = counterRepository.getTabKPIConfgiurationByTabId(tabKPIEntries.getTabIds());
        Map<String, Map<BigInteger, BigInteger>> tabKPIsMap = new HashMap<>();
        List<TabKPIEntry> entriesToSave = new ArrayList<>();
        tabKPIEntries.getTabIds().forEach(tabId -> {
            tabKPIsMap.put(tabId, new HashMap<BigInteger, BigInteger>());
        });

        entries.parallelStream().forEach(tabKPIEntry -> {
            tabKPIsMap.get(tabKPIEntry.getTabId()).put(tabKPIEntry.getKpiId(), tabKPIEntry.getKpiId());
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
