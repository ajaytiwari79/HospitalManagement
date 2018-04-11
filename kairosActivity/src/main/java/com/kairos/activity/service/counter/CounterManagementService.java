package com.kairos.activity.service.counter;

import com.kairos.activity.constants.CounterStore;
import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.counter.CounterAccessiblity;
import com.kairos.activity.persistence.model.counter.CounterDefinition;
import com.kairos.activity.persistence.model.counter.CounterModuleLink;
import com.kairos.activity.persistence.model.counter.CustomCounterSettings;
import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.response.dto.counter.CounterAccessiblityDTO;
import com.kairos.activity.response.dto.counter.CounterAccessiblityUpdatorDTO;
import com.kairos.activity.response.dto.counter.CounterModuleLinkDTO;
import com.kairos.activity.response.dto.counter.CustomCounterSettingDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.userContext.CurrentUserDetails;
import com.kairos.activity.util.userContext.UserContext;
import io.jsonwebtoken.lang.Assert;
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
    @Inject
    CounterStore counterStore;
    @Inject
    CounterRepository counterRepository;

    private final static Logger logger = LoggerFactory.getLogger(CounterManagementService.class);

    private void overrideOldObject(MongoBaseEntity newEntity, MongoBaseEntity oldEntity){
        BeanUtils.copyProperties(oldEntity, newEntity);
    }

    //storeCounterDefinition -List
    public void storeCounterDefinitions(List<CounterDefinition> counterDefs){
        for(CounterDefinition counterDef: counterDefs){
            CounterDefinition def = counterRepository.getCounterByType(counterDef.getType());
            if(def != null)
                overrideOldObject(counterDef, def);
            save(counterDef);
        }
    }

    //storeCounterDefinition - Object
    public void storeCounterDefinition(CounterDefinition counterDef){
        CounterDefinition def = counterRepository.getCounterByType(counterDef.getType());
        if(def != null)
            overrideOldObject(counterDef, def);
        save(counterDef);
    }

    public Map getCounterTypeAndIdMapping(){
        List<CounterDefinition> counterDefinitions = counterRepository.getEntityItemList(CounterDefinition.class);
        Map<CounterType, BigInteger> counterTypeIdMap = new HashMap<CounterType, BigInteger>();
        for(CounterDefinition definition : counterDefinitions){
            counterTypeIdMap.put(definition.getType(), definition.getId());
        }
        return counterTypeIdMap;
    }

    public Map getCounterDefinitionByIdMap(){
        List<CounterDefinition> counterDefinitions = counterRepository.getEntityItemList(CounterDefinition.class);
        Map<BigInteger, CounterDefinition> counterTypeIdMap = new HashMap<BigInteger, CounterDefinition>();
        for(CounterDefinition definition : counterDefinitions){
            counterTypeIdMap.put(definition.getId(), definition);
        }
        return counterTypeIdMap;
    }

    //TODO: add a module verification functionality for moduleId.
    //storeCounterModuleRelation to globally configure all tabs with counters.
    //TODO: should accept list of types for each moduleId for batch processing.
    public void storeModuleCounterLink(String moduleId, CounterType type){
        CounterDefinition cDef = counterRepository.getCounterByType(type);
        Assert.notNull(cDef, "CounterDefinition Can't be null!");
        CounterModuleLink counterModuleLink = counterRepository.getCounterModuleLink(moduleId, cDef.getId());
        if(counterModuleLink == null) {
            counterModuleLink = new CounterModuleLink();
            counterModuleLink.setModuleId(moduleId);
            counterModuleLink.setCounterDefinitionId(cDef.getId());
            save(counterModuleLink);
        }
    }

    //deleteCounterModuleLink
    public void deleteCounterModuleLink(BigInteger moduleId, BigInteger counterDefinitionId){
        counterRepository.deleteCounterModuleLink(moduleId, counterDefinitionId);
    }

    //get counter module links list
    public List<CounterModuleLinkDTO> getCounterModuleLinks(String moduleId){
        Assert.notNull(moduleId, "Module Id should not be null!");
        return counterRepository.getCounterModuleLinks(moduleId);
    }

    //setCounterAccessLevelForUnit
    public void setCounterAccessLevelForUnit(CounterAccessiblityUpdatorDTO data){
        Map<BigInteger, BigInteger> details = new HashMap<BigInteger, BigInteger>();
        List<BigInteger> removableAccessiblities = new ArrayList<BigInteger>();
        List<CounterAccessiblity> newAccessiblities= new ArrayList<CounterAccessiblity>();
        for(CounterModuleLinkDTO dto : data.getCounterModuleLinkDTOs()){
            details.put(dto.getId(), dto.getId());
        }
        List<CounterAccessiblityDTO> dtos = counterRepository.getCounterAccessiblityList(data.getUnitId(), data.getCounterLevel());
        for(CounterAccessiblityDTO accessiblityDTO : dtos){
            if(details.get(accessiblityDTO.getCounterModule().getId())!=null)
                details.remove(accessiblityDTO.getCounterModule().getId());
            else
                removableAccessiblities.add(accessiblityDTO.getId());
        }
        for(Map.Entry<BigInteger, BigInteger> entry : details.entrySet()){
            CounterAccessiblity accessiblity = new CounterAccessiblity();
            accessiblity.setAccessLevel(data.getCounterLevel());
            accessiblity.setCounterModuleLinkId(entry.getValue());
            accessiblity.setUnitId(data.getUnitId());
            newAccessiblities.add(accessiblity);
        }

        counterRepository.removeAccessiblitiesById(removableAccessiblities);
        counterRepository.removeCustomCounterProfiles(removableAccessiblities);
        if(newAccessiblities.size()>0)
            save(newAccessiblities);
    }

    //setCounterConf
    public void setCounterConfiguration(CustomCounterSettingDTO customCounterSettingDTO, BigInteger userId){
        CustomCounterSettings customCounterSettings = (CustomCounterSettings) counterRepository.getItemById(customCounterSettingDTO.getId(), CustomCounterSettings.class);
        if(customCounterSettings == null ) customCounterSettings = new CustomCounterSettings();
        BeanUtils.copyProperties(customCounterSettingDTO, customCounterSettings);
        customCounterSettings.setCouterAccessibilityId(customCounterSettingDTO.getCounterAccessiblity().getId());
        customCounterSettings.setStaffId(userId);
        save(customCounterSettings);
    }

    //getListOfCountersConfigured
    public List getAvailableCountersList(BigInteger userId){
        CurrentUserDetails currentUserDetails= UserContext.getUserDetails();
        Long unitId = UserContext.getUnitId();
        List configuredCounters = counterRepository.getConfiguredCounters(userId);
        return configuredCounters;
    }

    //getListOfCountersDefault
    public List getAccessibleCountersList(Long moduleId){
        CurrentUserDetails currentUserDetails= UserContext.getUserDetails();
        Long unitId = UserContext.getUnitId();
        List accessibleCounters = counterRepository.getAccessableCountersList(moduleId);
        return accessibleCounters;
    }

    private CounterService getCounterService(CounterType counterType){
        return counterStore.getService(counterType);
    }

    //

//    public List getCountersList(String moduleId){
//        return counterAccessPageRepository.getCounters(moduleId);
//    }

    public void printData(){
        logger.debug("\n\n\ncounter data: "+getCounterService(CounterType.RESTING_HOURS_PER_PRESENCE_DAY).getData());
    }

    //getCounterDefinitionList
    public long getCounterDefinitionCount(){
        return counterRepository.getEntityItemList(CounterDefinition.class).size();
    }

    //getCountOfCounterModuleLinks
    public long getCounterModuleLinksCount(){
        return counterRepository.getEntityItemList(CounterModuleLink.class).size();
    }
}
