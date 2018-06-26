package com.kairos.activity.service.counter;

import com.kairos.activity.client.counter.*;
import com.kairos.activity.component.counter.CounterServiceMapping;
import com.kairos.activity.enums.CounterType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.counter.*;
import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.service.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class CounterManagementService extends MongoBaseService{
    @Inject private CounterServiceMapping counterServiceMapping;
    @Inject private CounterRepository counterRepository;

    private final static Logger logger = LoggerFactory.getLogger(CounterManagementService.class);

    private void overrideOldObject(MongoBaseEntity newEntity, MongoBaseEntity oldEntity){
        BeanUtils.copyProperties(oldEntity, newEntity);
    }

    //storeCounter -List
    public void storeCounters(List<Counter> counterDefs){
        for(Counter counterDef: counterDefs){
            Counter def = counterRepository.getCounterByType(counterDef.getType());
            if(def != null)
                overrideOldObject(counterDef, def);
            save(counterDef);
        }
    }

    //storeCounter - Object
    public void storeCounter(Counter counterDef){
        Counter def = counterRepository.getCounterByType(counterDef.getType());
        if(def != null)
            overrideOldObject(counterDef, def);
        save(counterDef);
    }

    //vairous methods for accessing countertypes
    //to locate the counters in moduleWiseCounterMapping will be supplied to UI for ref.
    public List getAllCounters(){
        return counterRepository.getEntityItemList(Counter.class);
    }

    public Map getCounterTypeAndIdMapping(List<Counter> counters){
        Map<CounterType, BigInteger> counterTypeIdMap = new HashMap<CounterType, BigInteger>();
        for(Counter definition : counters){
            counterTypeIdMap.put(definition.getType(), definition.getId());
        }
        return counterTypeIdMap;
    }

    public Map getCounterIdAndTypeMapping(List<Counter> counters){
        Map<BigInteger, CounterType> counterIdTypeMap = new HashMap<>();
        for(Counter definition : counters){
            counterIdTypeMap.put(definition.getId(), definition.getType());
        }
        return counterIdTypeMap;
    }

    //to identify counter definition for by id.
    public Map getCounterDefinitionByIdMap(List<Counter> counters){
        Map<BigInteger, Counter> counterTypeIdMap = new HashMap<BigInteger, Counter>();
        for(Counter definition : counters){
            counterTypeIdMap.put(definition.getId(), definition);
        }
        return counterTypeIdMap;
    }

    //configuration for modulewise counters cofigurable at country level

    public List<ModuleCounterGroupingDTO> getModuleCountersForCountry(BigInteger countryId){
        return counterRepository.getModuleCounterDTOsForCountry(countryId);
    }

    public List<ModuleCounter> getModuleAndModuleCounterMappingForCountry(BigInteger countryId){
        return counterRepository.getModuleCountersForCountry(countryId);
    }

    public void storeModuleCounters(List<ModuleCounterGroupingDTO> modulesCounterMapping, BigInteger countryId){
        Map<String, Map<BigInteger, BigInteger>> moduleLevelCountersIdMap = getModuleLevelCountersIdMap(countryId);
        for(ModuleCounterGroupingDTO moduleCountersData : modulesCounterMapping){
            Map<BigInteger, BigInteger> counterIdMap = moduleLevelCountersIdMap.get(moduleCountersData.getModuleId());
            ManipulatableCounterIdsDTO counterIdsToModify = getCounterIdsToAddAndUpdate(counterIdMap, moduleCountersData.getCounterIds());
            removeModuleCounters(counterIdsToModify.getCounterIdsToRemove(), moduleCountersData.getModuleId(), countryId);
            storeModuleCounters(countryId, moduleCountersData.getModuleId(), counterIdsToModify.getCounterIdsToAdd());
        }
    }

    private void storeModuleCounters(BigInteger countryId, String moduleId, List<BigInteger> counterIds){
        List<ModuleCounter> moduleCounterList = new ArrayList<>();
        for(BigInteger counterId: counterIds){
            ModuleCounter moduleCounter = new ModuleCounter(countryId, moduleId, counterId);
            moduleCounterList.add(moduleCounter);
        }
        save(moduleCounterList);
    }

    private  ManipulatableCounterIdsDTO getCounterIdsToAddAndUpdate(Map<BigInteger, BigInteger> idMap, List<BigInteger> currentRefCounterIds){
        ManipulatableCounterIdsDTO manipulatableIds = new ManipulatableCounterIdsDTO();
        for(BigInteger counterId : currentRefCounterIds){
            if(idMap.remove(counterId) == null)
                manipulatableIds.getCounterIdsToAdd().add(counterId);
        }
        for(Map.Entry<BigInteger, BigInteger> entry: idMap.entrySet()){
            manipulatableIds.getCounterIdsToRemove().add(entry.getValue());
        }
        return manipulatableIds;
    }

    private Map<String, Map<BigInteger, BigInteger>> getModuleLevelCountersIdMap(BigInteger countryId){
        List<ModuleCounterGroupingDTO> moduleCounterGroupingDTOList = counterRepository.getModuleCounterDTOsForCountry(countryId);
        Map<String, Map<BigInteger, BigInteger>> moduleLevelCountersIdMap = new HashMap<>();
        for(ModuleCounterGroupingDTO moduleCounterGroupingDTO : moduleCounterGroupingDTOList){
            Map<BigInteger, BigInteger> idMap = new HashMap<>();
            for(BigInteger counterId : moduleCounterGroupingDTO.getCounterIds()){
                idMap.put(counterId, counterId);
            }
            moduleLevelCountersIdMap.put(moduleCounterGroupingDTO.getModuleId(), idMap);
        }
        return moduleLevelCountersIdMap;
    }

    private void removeModuleCounters(List<BigInteger> refCounterIds, String moduleId, BigInteger countryId){
        //TODO:
        List<BigInteger> moduleCountersIds = counterRepository.getModuleCountersIds(refCounterIds, moduleId, countryId);
        counterRepository.removeAll("refCounterId", moduleCountersIds, UnitRoleCounter.class);
        counterRepository.removeAll("_id", moduleCountersIds, ModuleCounter.class);
    }

    ////////////////////////////////////////////////////////

    //get role wise moduleCounterId mapping
    public List<RoleCounterDTO> getRoleCounterMapping(BigInteger unitId){
        return counterRepository.getRoleAndModuleCounterIdMapping(unitId);
    }

    private Map<BigInteger, Map<BigInteger, BigInteger>> getRoleCounterIdMapping(BigInteger unitId){
        List<RoleCounterDTO> roleCounterDTOS = getRoleCounterMapping(unitId);
        Map<BigInteger, Map<BigInteger, BigInteger>> roleLevelCountersIdMap = new HashMap<>();
        for(RoleCounterDTO roleCounterDTO : roleCounterDTOS){
            Map<BigInteger, BigInteger> idMap = new HashMap<>();
            for(BigInteger moduleCounterId : roleCounterDTO.getModuleCounterIds()){
                idMap.put(moduleCounterId, moduleCounterId);
            }
            roleLevelCountersIdMap.put(roleCounterDTO.getRoleId(), idMap);
        }
        return roleLevelCountersIdMap;
    }

    public void storeRoleCountersForUnit(List<RoleCounterDTO> roleCounterDTOS, BigInteger unitId){
        Map<BigInteger, Map<BigInteger, BigInteger>> roleLevelCountersIdMap = getRoleCounterIdMapping(unitId);
//        Map<BigInteger, BigInteger> baseCounterIdMapping = *//
        for(RoleCounterDTO roleCounterDTO : roleCounterDTOS){
            Map<BigInteger, BigInteger> refCountersIdMap = roleLevelCountersIdMap.get(roleCounterDTO.getRoleId());
            ManipulatableCounterIdsDTO counterRefsToModify = getCounterIdsToAddAndUpdate(refCountersIdMap, roleCounterDTO.getModuleCounterIds());
            removeRoleCounterForUnit(roleCounterDTO.getRoleId(), counterRefsToModify.getCounterIdsToRemove());
            addRoleCounterRefsForUnit(unitId, roleCounterDTO.getRoleId(), counterRefsToModify.getCounterIdsToAdd());
        }
    }

    public void removeRoleCounterForUnit(BigInteger roleId, List<BigInteger> counterRefsToRemove){
        //TODO: removal of counters
        counterRepository.removeRoleCounters(roleId, counterRefsToRemove);
    }

    public void addRoleCounterRefsForUnit(BigInteger unitId, BigInteger roleId, List<BigInteger> countersRefToAdd){
        List<UnitRoleCounter> roleCountersToAdd = new ArrayList<>();
        for(BigInteger counterIdRef : countersRefToAdd){
            UnitRoleCounter unitRoleCounter = new UnitRoleCounter(unitId, roleId, counterIdRef);
            roleCountersToAdd.add(unitRoleCounter);
        }
        save(roleCountersToAdd);
    }

    ///////////////////////////////////////////////////////
    // setting default counteres
    ///////////////////////////////////////////////////

    public void setupInitialConfigurationForUnit(BigInteger countryId, BigInteger unitId){
        List<CounterOrderDTO> orders = counterRepository.getOrderedCountersListForCountry(countryId, null);
        List<UnitCounterOrder> unitCounterOrders = new ArrayList<>();
        for(CounterOrderDTO counterOrderDTO : orders){
            UnitCounterOrder unitCounterOrder = new UnitCounterOrder(unitId,
                    counterOrderDTO.getModuleId(),
                    counterOrderDTO.getTabId(),
                    counterOrderDTO.getOrderedCounterIds());
        }
        save(unitCounterOrders);
    }

    public void setupInitialConfigurationForStaff(){
        //get mapping of rolewisecounterIds to role:modulewiseCounterIds.
    }

    public InitialCountersDetailsDTO getInitialCounterDataForCountry(String moduleId, BigInteger countryId){
        InitialCountersDetailsDTO initialCountersDetailsDTO = new InitialCountersDetailsDTO();
        initialCountersDetailsDTO.setCounterDefs(counterRepository.getModuleCounterDetails(moduleId, countryId));
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForCountry(countryId, moduleId));
        return initialCountersDetailsDTO;
    }

    public InitialCountersDetailsDTO getInitialCounterDataForUnit(String moduleId, BigInteger unitId, BigInteger countryId){
        InitialCountersDetailsDTO initialCountersDetailsDTO = new InitialCountersDetailsDTO();
        initialCountersDetailsDTO.setCounterDefs(counterRepository.getModuleCounterDetails(moduleId, countryId));
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForUnit(unitId, moduleId));
        if(initialCountersDetailsDTO.getOrderedList() == null || initialCountersDetailsDTO.getOrderedList().isEmpty())
            setupInitialConfigurationForUnit(countryId, unitId);
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForUnit(unitId, moduleId));
        return initialCountersDetailsDTO;
    }

    public InitialCountersDetailsDTO getInitialCounterDataForStaff(String moduleId, BigInteger staffId, BigInteger unitId, BigInteger roleId){
        InitialCountersDetailsDTO initialCountersDetailsDTO = new InitialCountersDetailsDTO();
        initialCountersDetailsDTO.setCounterDefs(counterRepository.getRoleCounterTypeDetails(roleId, unitId, moduleId));
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForUser(unitId, staffId, moduleId));
        if(initialCountersDetailsDTO.getOrderedList() == null || initialCountersDetailsDTO.getOrderedList().isEmpty())
            setupInitialConfigurationForStaff(); //TODO
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForUser(unitId, staffId, moduleId));
        return initialCountersDetailsDTO;
    }

    private DefaultCounterOrder updateExistingOrder(DefaultCounterOrder oldOrder, CounterOrderDTO latestOrder){
        oldOrder.setModuleId(latestOrder.getModuleId());
        oldOrder.setTabId(latestOrder.getTabId());
        oldOrder.setOrderedCounterIds(latestOrder.getOrderedCounterIds());
        return oldOrder;
    }

    public void updateCountersOrderForCountry(CounterOrderDTO counterOrderDTO, BigInteger countryId){
        DefaultCounterOrder order = (DefaultCounterOrder) counterRepository.getItemById(counterOrderDTO.getId(), DefaultCounterOrder.class);
        if(counterOrderDTO.getId() == null){
            //DefaultCounterOrder
        }
    }

    public void updateCountersOrderForUnit(CounterOrderDTO counterOrderDTO){

    }

    public void updateCountersOrderForStaff(CounterOrderDTO counterOrderDTO){

    }

    private CounterService getCounterService(CounterType counterType){
        return counterServiceMapping.getService(counterType);
    }

    //getCounterDefinitionList
    public long getCounterDefinitionCount(){
        return counterRepository.getEntityItemList(Counter.class).size();
    }

    //getCountOfCounterModuleLinks
    public long getCounterModuleLinksCount(){
        return counterRepository.getEntityItemList(ModuleCounter.class).size();
    }
}
