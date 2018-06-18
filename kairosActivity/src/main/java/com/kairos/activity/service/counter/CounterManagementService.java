package com.kairos.activity.service.counter;

import com.kairos.activity.constants.CounterStore;
import com.kairos.activity.enums.counter.CounterType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.counter.*;
import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.response.dto.counter.*;
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

@Service
public class CounterManagementService extends MongoBaseService{
    @Inject private CounterStore counterStore;
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

    public List<ModulewiseCounterGroupingDTO> getModulewiseCountersForCountry(BigInteger countryId){
        return counterRepository.getModulewiseCounterDTOsForCountry(countryId);
    }

    public List<ModuleWiseCounter> getModuleAndModuleCounterMappingForCountry(BigInteger countryId){
        return counterRepository.getModulewiseCountersForCountry(countryId);
    }

    public void storeModuleWiseCounters(List<ModulewiseCounterGroupingDTO> modulesCounterMapping, BigInteger countryId){
        Map<String, Map<BigInteger, BigInteger>> moduleLevelCountersIdMap = getModuleLevelCountersIdMap(countryId);
        for(ModulewiseCounterGroupingDTO moduleCountersData : modulesCounterMapping){
            Map<BigInteger, BigInteger> counterIdMap = moduleLevelCountersIdMap.get(moduleCountersData.getModuleId());
            ManipulatableCounterIdsDTO counterIdsToModify = getCounterIdsToAddAndUpdate(counterIdMap, moduleCountersData.getCounterIds());
            removeModulewiseCounters(counterIdsToModify.getCounterIdsToRemove(), moduleCountersData.getModuleId(), countryId);
            storeModulewiseCounters(countryId, moduleCountersData.getModuleId(), counterIdsToModify.getCounterIdsToAdd());
        }
    }

    private void storeModulewiseCounters(BigInteger countryId, String moduleId, List<BigInteger> counterIds){
        List<ModuleWiseCounter> moduleWiseCounterList = new ArrayList<>();
        for(BigInteger counterId: counterIds){
            ModuleWiseCounter moduleWiseCounter = new ModuleWiseCounter(countryId, moduleId, counterId);
            moduleWiseCounterList.add(moduleWiseCounter);
        }
        save(moduleWiseCounterList);
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
        List<ModulewiseCounterGroupingDTO> modulewiseCounterGroupingDTOList = counterRepository.getModulewiseCounterDTOsForCountry(countryId);
        Map<String, Map<BigInteger, BigInteger>> moduleLevelCountersIdMap = new HashMap<>();
        for(ModulewiseCounterGroupingDTO modulewiseCounterGroupingDTO : modulewiseCounterGroupingDTOList){
            Map<BigInteger, BigInteger> idMap = new HashMap<>();
            for(BigInteger counterId : modulewiseCounterGroupingDTO.getCounterIds()){
                idMap.put(counterId, counterId);
            }
            moduleLevelCountersIdMap.put(modulewiseCounterGroupingDTO.getModuleId(), idMap);
        }
        return moduleLevelCountersIdMap;
    }

    private void removeModulewiseCounters(List<BigInteger> refCounterIds, String moduleId, BigInteger countryId){
        //TODO:
        List<BigInteger> moduleWiseCountersIds = counterRepository.getModuleWiseCountersIds(refCounterIds, moduleId, countryId);
        counterRepository.removeAll("refCounterId", moduleWiseCountersIds, UnitRoleWiseCounter.class);
        counterRepository.removeAll("_id", moduleWiseCountersIds, ModuleWiseCounter.class);
    }

    ////////////////////////////////////////////////////////

    //get role wise moduleCounterId mapping
    public List<RolewiseCounterDTO> getRolewiseCounterMapping(BigInteger unitId){
        return counterRepository.getRoleAndModuleCounterIdMapping(unitId);
    }

    private Map<BigInteger, Map<BigInteger, BigInteger>> getRolewiseCounterIdMapping(BigInteger unitId){
        List<RolewiseCounterDTO> rolewiseCounterDTOs = getRolewiseCounterMapping(unitId);
        Map<BigInteger, Map<BigInteger, BigInteger>> roleLevelCountersIdMap = new HashMap<>();
        for(RolewiseCounterDTO rolewiseCounterDTO: rolewiseCounterDTOs){
            Map<BigInteger, BigInteger> idMap = new HashMap<>();
            for(BigInteger modulewiseCounterId : rolewiseCounterDTO.getModulewiseCounterIds()){
                idMap.put(modulewiseCounterId, modulewiseCounterId);
            }
            roleLevelCountersIdMap.put(rolewiseCounterDTO.getRoleId(), idMap);
        }
        return roleLevelCountersIdMap;
    }

    public void storeRolewiseCountersForUnit(List<RolewiseCounterDTO> rolewiseCounterDTOs, BigInteger unitId){
        Map<BigInteger, Map<BigInteger, BigInteger>> roleLevelCountersIdMap = getRolewiseCounterIdMapping(unitId);
//        Map<BigInteger, BigInteger> baseCounterIdMapping = *//
        for(RolewiseCounterDTO rolewiseCounterDTO : rolewiseCounterDTOs){
            Map<BigInteger, BigInteger> refCountersIdMap = roleLevelCountersIdMap.get(rolewiseCounterDTO.getRoleId());
            ManipulatableCounterIdsDTO counterRefsToModify = getCounterIdsToAddAndUpdate(refCountersIdMap, rolewiseCounterDTO.getModulewiseCounterIds());
            removeRolewiseCounterForUnit(rolewiseCounterDTO.getRoleId(), counterRefsToModify.getCounterIdsToRemove());
            addRolewiseCounterRefsForUnit(unitId, rolewiseCounterDTO.getRoleId(), counterRefsToModify.getCounterIdsToAdd());
        }
    }

    public void removeRolewiseCounterForUnit(BigInteger roleId, List<BigInteger> counterRefsToRemove){
        //TODO: removal of counters
        counterRepository.removeRoleWiseCounters(roleId, counterRefsToRemove);
    }

    public void addRolewiseCounterRefsForUnit(BigInteger unitId, BigInteger roleId, List<BigInteger> countersRefToAdd){
        List<UnitRoleWiseCounter> roleWiseCountersToAdd = new ArrayList<>();
        for(BigInteger counterIdRef : countersRefToAdd){
            UnitRoleWiseCounter unitRoleWiseCounter = new UnitRoleWiseCounter(unitId, roleId, counterIdRef);
            roleWiseCountersToAdd.add(unitRoleWiseCounter);
        }
        save(roleWiseCountersToAdd);
    }

    ///////////////////////////////////////////////////////
    // setting default counteres
    ///////////////////////////////////////////////////

    public void setupInitialConfigurationForUnit(BigInteger countryId, BigInteger unitId){
        List<CounterOrderDTO> orders = counterRepository.getOrderedCountersListForCountry(countryId, null);
        List<UnitWiseCounterOrder> unitCounterOrders = new ArrayList<>();
        for(CounterOrderDTO counterOrderDTO : orders){
            UnitWiseCounterOrder unitCounterOrder = new UnitWiseCounterOrder(unitId,
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
        initialCountersDetailsDTO.setCounterDefs(counterRepository.getModuleWiseCounterDetails(moduleId, countryId));
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForCountry(countryId, moduleId));
        return initialCountersDetailsDTO;
    }

    public InitialCountersDetailsDTO getInitialCounterDataForUnit(String moduleId, BigInteger unitId, BigInteger countryId){
        InitialCountersDetailsDTO initialCountersDetailsDTO = new InitialCountersDetailsDTO();
        initialCountersDetailsDTO.setCounterDefs(counterRepository.getModuleWiseCounterDetails(moduleId, countryId));
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForUnit(unitId, moduleId));
        if(initialCountersDetailsDTO.getOrderedList() == null || initialCountersDetailsDTO.getOrderedList().isEmpty())
            setupInitialConfigurationForUnit(countryId, unitId);
        initialCountersDetailsDTO.setOrderedList(counterRepository.getOrderedCountersListForUnit(unitId, moduleId));
        return initialCountersDetailsDTO;
    }

    public InitialCountersDetailsDTO getInitialCounterDataForStaff(String moduleId, BigInteger staffId, BigInteger unitId, BigInteger roleId){
        InitialCountersDetailsDTO initialCountersDetailsDTO = new InitialCountersDetailsDTO();
        initialCountersDetailsDTO.setCounterDefs(counterRepository.getRolewiseCounterTypeDetails(roleId, unitId, moduleId));
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
        return counterStore.getService(counterType);
    }

    public void printData(){
        logger.debug("\n\n\ncounter data: "+getCounterService(CounterType.RESTING_HOURS_PER_PRESENCE_DAY).getData());
    }

    //getCounterDefinitionList
    public long getCounterDefinitionCount(){
        return counterRepository.getEntityItemList(Counter.class).size();
    }

    //getCountOfCounterModuleLinks
    public long getCounterModuleLinksCount(){
        return counterRepository.getEntityItemList(ModuleWiseCounter.class).size();
    }
}
