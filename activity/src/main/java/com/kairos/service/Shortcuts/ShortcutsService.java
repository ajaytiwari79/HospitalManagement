package com.kairos.service.Shortcuts;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.ShortCuts.ShortcutsDTO;
import com.kairos.dto.activity.counter.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.persistence.model.counter.TabKPIConf;
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
import java.util.stream.Collector;
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

    public ShortcutsDTO updateShortcut(ShortcutsDTO shortcutsDTO){
        Shortcuts shortcuts=shortcutsMongoRepository.findById(shortcutsDTO.getId()).orElse(null);
        if(ObjectUtils.isNull(shortcuts)){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(shortcutsDTO.getName(),shortcutsDTO.getStaffId(),shortcutsDTO.getUnitId(),shortcutsDTO.getId());
        if(existByName){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        shortcuts= ObjectMapperUtils.copyPropertiesByMapper(shortcutsDTO,Shortcuts.class);
        shortcutsMongoRepository.save(shortcuts);
        return shortcutsDTO;
    }

    public ShortcutsDTO getShortcutById(BigInteger shortcutId){
        ShortcutsDTO shortcutsDTO= shortcutsMongoRepository.findShortcutById(shortcutId);
        if(Objects.isNull(shortcutsDTO)){
            exceptionService.dataNotMatchedException("",shortcutsDTO.getId());
        }
        return shortcutsDTO;
    }

    public List<ShortcutsDTO> getAllShortcutByStaffIdAndUnitId(Long unitId,Long staffId){
        List<ShortcutsDTO> shortcutsDTOS=shortcutsMongoRepository.findShortcutByUnitIdAndStaffId(staffId,unitId);
        if(ObjectUtils.isCollectionEmpty(shortcutsDTOS)){
            exceptionService.dataNotMatchedException("",staffId);
        }
        return shortcutsDTOS;
    }


}
