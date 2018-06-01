package com.kairos.activity.service.kpi;

//import com.kairos.activity.constants.KpiStore;
//import com.kairos.activity.service.kpi.KpiService;
import com.kairos.activity.persistence.enums.kpi.KpiType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.kpi.KPI;
import com.kairos.activity.persistence.model.kpi.ModuleWiseKpi;
import com.kairos.activity.persistence.model.kpi.UnitRoleWiseKpi;
import com.kairos.activity.persistence.repository.kpi.KpiRepository;
import com.kairos.activity.response.dto.kpi.ManipulatableKpiIdsDTO;
import com.kairos.activity.response.dto.kpi.ModulewiseKpiGroupingDTO;
import com.kairos.activity.response.dto.kpi.RolewiseKpiDTO;
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
public class KpiManagementService extends MongoBaseService{
    //@Inject private KpiStore KpiStore;
    @Inject private KpiRepository kpiRepository;

    private final static Logger logger = LoggerFactory.getLogger(KpiManagementService.class);

    private void overrideOldObject(MongoBaseEntity newEntity, MongoBaseEntity oldEntity){
        BeanUtils.copyProperties(oldEntity, newEntity);
    }

    //storeKpi -List
    public void storeKpis(List<KPI> kpiDefs){
        for(KPI KpiDef: kpiDefs){
            KPI def = kpiRepository.getKpiByType(KpiDef.getType());
            if(def != null)
                overrideOldObject(KpiDef, def);
            save(KpiDef);
        }
    }

    //storeKpi - Object
    public void storeKpi(KPI kpiDef){
        KPI def = kpiRepository.getKpiByType(kpiDef.getType());
        if(def != null)
            overrideOldObject(kpiDef, def);
        save(kpiDef);
    }

    //vairous methods for accessing Kpitypes
    //to locate the Kpis in moduleWiseKpiMapping will be supplied to UI for ref.
    public List getAllKpis(){
        return kpiRepository.getEntityItemList(KPI.class);
    }

    public Map getKpiTypeAndIdMapping(List<KPI> kpis){
        Map<KpiType, BigInteger> kpiTypeIdMap = new HashMap<KpiType, BigInteger>();
        for(KPI definition : kpis){
            kpiTypeIdMap.put(definition.getType(), definition.getId());
        }
        return kpiTypeIdMap;
    }

    public Map getKpiIdAndTypeMapping(List<KPI> kpis){
        Map<BigInteger, KpiType> kpiIdTypeMap = new HashMap<>();
        for(KPI definition : kpis){
            kpiIdTypeMap.put(definition.getId(), definition.getType());
        }
        return kpiIdTypeMap;
    }

    //to identify Kpi definition for by id.
    public Map getKpiDefinitionByIdMap(List<KPI> kpis){
        Map<BigInteger, KPI> kpiTypeIdMap = new HashMap<BigInteger, KPI>();
        for(KPI definition : kpis){
            kpiTypeIdMap.put(definition.getId(), definition);
        }
        return kpiTypeIdMap;
    }

    //configuration for modulewise Kpis cofigurable at country level

    public List<ModulewiseKpiGroupingDTO> getModulewiseKpisForCountry(BigInteger countryId){
        return kpiRepository.getModulewiseKpiDTOsForCountry(countryId);
    }

    public List<ModuleWiseKpi> getModuleAndModuleKpiMappingForCountry(BigInteger countryId){
        return kpiRepository.getModulewiseKpisForCountry(countryId);
    }

    public void storeModuleWiseKpis(List<ModulewiseKpiGroupingDTO> modulesKpiMapping, BigInteger countryId){
        Map<String, Map<BigInteger, BigInteger>> moduleLevelKpisIdMap = getModuleLevelKpisIdMap(countryId);
        for(ModulewiseKpiGroupingDTO moduleKpisData : modulesKpiMapping){
            Map<BigInteger, BigInteger> kpiIdMap = moduleLevelKpisIdMap.get(moduleKpisData.getModuleId());
            ManipulatableKpiIdsDTO kpiIdsToModify = getKpiIdsToAddAndUpdate(kpiIdMap, moduleKpisData.getKpiIds());
            removeModulewiseKpis(kpiIdsToModify.getKpiIdsToRemove(), moduleKpisData.getModuleId());
            storeModulewiseKpis(countryId, moduleKpisData.getModuleId(), kpiIdsToModify.getKpiIdsToAdd());
        }
    }

    private void storeModulewiseKpis(BigInteger countryId, String moduleId, List<BigInteger> kpiIds){
        List<ModuleWiseKpi> moduleWiseKpiList = new ArrayList<>();
        for(BigInteger KpiId: kpiIds){
            ModuleWiseKpi moduleWiseKpi = new ModuleWiseKpi(moduleId, KpiId, countryId);
            moduleWiseKpiList.add(moduleWiseKpi);
        }
        save(moduleWiseKpiList);
    }

    private  ManipulatableKpiIdsDTO getKpiIdsToAddAndUpdate(Map<BigInteger, BigInteger> idMap, List<BigInteger> currentKpiIds){
        ManipulatableKpiIdsDTO manipulatableIds = new ManipulatableKpiIdsDTO();
        for(BigInteger kpiId : currentKpiIds){
            if(idMap.remove(kpiId) == null)
                manipulatableIds.getKpiIdsToAdd().add(kpiId);
        }
        for(Map.Entry<BigInteger, BigInteger> entry: idMap.entrySet()){
            manipulatableIds.getKpiIdsToRemove().add(entry.getValue());
        }
        return manipulatableIds;
    }

    private Map<String, Map<BigInteger, BigInteger>> getModuleLevelKpisIdMap(BigInteger countryId){
        List<ModulewiseKpiGroupingDTO> modulewiseKpiGroupingDTOList = kpiRepository.getModulewiseKpiDTOsForCountry(countryId);
        Map<String, Map<BigInteger, BigInteger>> moduleLevelKpisIdMap = new HashMap<>();
        for(ModulewiseKpiGroupingDTO modulewiseKpiGroupingDTO : modulewiseKpiGroupingDTOList){
            Map<BigInteger, BigInteger> idMap = new HashMap<>();
            for(BigInteger kpiId : modulewiseKpiGroupingDTO.getKpiIds()){
                idMap.put(kpiId, kpiId);
            }
            moduleLevelKpisIdMap.put(modulewiseKpiGroupingDTO.getModuleId(), idMap);
        }
        return moduleLevelKpisIdMap;
    }

    private void removeModulewiseKpis(List<BigInteger> modulewiseKpiIds, String moduleId){
        //TODO:
        //identify unitlevel ids
        //identify individual level ids
        //identify individual level default views

    }

    ////////////////////////////////////////////////////////

    //get role wise moduleKpiId mapping
    public List<RolewiseKpiDTO> getRolewiseKpiMapping(BigInteger unitId){
        return kpiRepository.getRoleAndModuleKpiIdMapping(unitId);
    }

    private Map<BigInteger, Map<BigInteger, BigInteger>> getRolewiseKpiIdMapping(BigInteger unitId){
        List<RolewiseKpiDTO> rolewiseKpiDTOs = getRolewiseKpiMapping(unitId);
        Map<BigInteger, Map<BigInteger, BigInteger>> roleLevelKpisIdMap = new HashMap<>();
        for(RolewiseKpiDTO rolewiseKpiDTO: rolewiseKpiDTOs){
            Map<BigInteger, BigInteger> idMap = new HashMap<>();
            for(BigInteger modulewiseKpiId : rolewiseKpiDTO.getModulewiseKpiIds()){
                idMap.put(modulewiseKpiId, modulewiseKpiId);
            }
            roleLevelKpisIdMap.put(rolewiseKpiDTO.getRoleId(), idMap);
        }
        return roleLevelKpisIdMap;
    }

    public void storeRolewiseKpisForUnit(List<RolewiseKpiDTO> rolewiseKpiDTOs, BigInteger unitId){
        Map<BigInteger, Map<BigInteger, BigInteger>> roleLevelKpisIdMap = getRolewiseKpiIdMapping(unitId);
        for(RolewiseKpiDTO rolewiseKpiDTO : rolewiseKpiDTOs){
            Map<BigInteger, BigInteger> kpisIdMap = roleLevelKpisIdMap.get(rolewiseKpiDTO.getRoleId());
            ManipulatableKpiIdsDTO kpiRefsToModify = getKpiIdsToAddAndUpdate(kpisIdMap, rolewiseKpiDTO.getModulewiseKpiIds());

        }
    }

    public void removeRolewiseKpiForUnit(BigInteger roleId, List<BigInteger> KpiRefsToRemove){
        //TODO: removal of Kpis
    }

    public void addRolewiseKpiRefsForUnit(BigInteger unitId, BigInteger roleId, List<BigInteger> KpisRefToAdd){
        List<UnitRoleWiseKpi> roleWiseKpisToAdd = new ArrayList<>();
        for(BigInteger KpiIdRef : KpisRefToAdd){
            UnitRoleWiseKpi unitRoleWiseKpi = new UnitRoleWiseKpi(unitId, roleId, KpiIdRef);
            roleWiseKpisToAdd.add(unitRoleWiseKpi);
        }
        save(roleWiseKpisToAdd);
    }

    ///////////////////////////////////////////////////////

//    private KpiService getKpiService(KpiType KpiType){
//        return KpiStore.getService(KpiType);
//    }

//    public void printData(){
//        logger.debug("\n\n\nKpi data: "+getKpiService(KpiType.RESTING_HOURS_PER_PRESENCE_DAY).getData());
//    }

    //getKpiDefinitionList
    public long getKpiDefinitionCount(){
        return kpiRepository.getEntityItemList(KPI.class).size();
    }

    //getCountOfKpiModuleLinks
    public long getKpiModuleLinksCount(){
        return kpiRepository.getEntityItemList(ModuleWiseKpi.class).size();
    }
}
