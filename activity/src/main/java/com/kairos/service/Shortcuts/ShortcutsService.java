package com.kairos.service.Shortcuts;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.ShortCuts.ShortcutsDTO;
import com.kairos.dto.activity.counter.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.persistence.model.shortcuts.Shortcuts;
import com.kairos.persistence.repository.Shortcuts.ShortcutsMongoRepository;
import com.kairos.service.counter.CounterDistService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ShortcutsService {

    @Inject
    private ShortcutsMongoRepository  shortcutsMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CounterDistService counterDistService;


    public List<TabKPIDTO> getTabKPIs(List<String> tabIds, List<BigInteger> kpiIds, Long staffId,Long unitId){
         List<TabKPIDTO> tabKPIDTOS=new ArrayList<>();
        List<TabKPIMappingDTO> tabKPIMappingDTOS = counterDistService.getTabKPIByTabIdsAndKpiIds(tabIds, kpiIds, staffId);
        Map<String,List<TabKPIMappingDTO>> tabIdAndTabKPIDtoMap=tabKPIMappingDTOS.stream().collect(Collectors.groupingBy(k -> k.getTabId(),Collectors.toList()));
        for (String tabId : tabIdAndTabKPIDtoMap.keySet()) {
            tabKPIDTOS.add(new TabKPIDTO(unitId,tabId,tabIdAndTabKPIDtoMap.get(tabId).stream().map(tabKPIMappingDTO -> tabKPIMappingDTO.getKpiId()).collect(Collectors.toList())));
        }
        return tabKPIDTOS;
    }

    public ShortcutsDTO saveShortcut(ShortcutsDTO shortcutsDTO){
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(shortcutsDTO.getName(),shortcutsDTO.getStaffId(),shortcutsDTO.getUnitId(),BigInteger.valueOf(-1));
        if(existByName){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        shortcutsDTO.setTabKPIs(getTabKPIs(shortcutsDTO.getTabKPIs().stream().map(tabKPIDTO -> tabKPIDTO.getTabId()).collect(Collectors.toList()), new ArrayList<>(),shortcutsDTO.getStaffId(),shortcutsDTO.getUnitId()));
        Shortcuts shortcuts = shortcutsMongoRepository.save(ObjectMapperUtils.copyPropertiesByMapper(shortcutsDTO,Shortcuts.class));
        return ObjectMapperUtils.copyPropertiesByMapper(shortcuts,ShortcutsDTO.class);
    }

    public ShortcutsDTO updateShortcut(BigInteger shortcutId , String name , ShortcutsDTO shortcutsDTO){
        Shortcuts shortcut=shortcutsMongoRepository.findById(shortcutId).orElse(null);
        if(ObjectUtils.isNull(shortcut)){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(ObjectUtils.isNotNull(name)?name:shortcut.getName(),shortcut.getStaffId(),shortcut.getUnitId(),shortcut.getId());
        if(existByName){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        if(ObjectUtils.isNotNull(name)){
            shortcut.setName(name);
        }else {
            shortcut=ObjectMapperUtils.copyPropertiesByMapper(shortcutsDTO,Shortcuts.class);
            shortcut.setTabKPIs(getTabKPIs(shortcutsDTO.getTabKPIs().stream().map(tabKPIDTO -> tabKPIDTO.getTabId()).collect(Collectors.toList()), new ArrayList<>(),shortcutsDTO.getStaffId(),shortcutsDTO.getUnitId()));
        }
        shortcutsMongoRepository.save(shortcut);
        return shortcutsDTO;
    }

    public ShortcutsDTO getShortcutById(BigInteger shortcutId){
        ShortcutsDTO shortcutsDTO= shortcutsMongoRepository.findShortcutById(shortcutId);
        if(Objects.isNull(shortcutsDTO)){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        return shortcutsDTO;
    }


    public boolean deleteShortcutById(BigInteger shortcutId){
        ShortcutsDTO shortcutsDTO= shortcutsMongoRepository.findShortcutById(shortcutId);
        if(Objects.isNull(shortcutsDTO)){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        shortcutsMongoRepository.deleteById(shortcutId);
        return true;
    }


    public List<ShortcutsDTO> getAllShortcutByStaffIdAndUnitId(Long unitId,Long staffId){
        List<ShortcutsDTO> shortcutsDTOS=shortcutsMongoRepository.findShortcutByUnitIdAndStaffId(staffId,unitId);
        if(ObjectUtils.isCollectionEmpty(shortcutsDTOS)){
            exceptionService.dataNotMatchedException("",staffId);
        }
        return shortcutsDTOS;
    }

    public ShortcutsDTO createCopyOfShortcut(BigInteger shortcutId,String name){
        Shortcuts shortcut=shortcutsMongoRepository.findById(shortcutId).orElse(null);
        if(ObjectUtils.isNull(shortcut)){
            exceptionService.dataNotMatchedException("",shortcutId);
        }
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(name,shortcut.getStaffId(),shortcut.getUnitId(),BigInteger.valueOf(-1));
        if(existByName){
            exceptionService.dataNotMatchedException("",shortcutId);
        }
        shortcut.setName(name);
        shortcut.setId(null);
        shortcutsMongoRepository.save(shortcut);
        return ObjectMapperUtils.copyPropertiesByMapper(shortcut,ShortcutsDTO.class);
    }

}
