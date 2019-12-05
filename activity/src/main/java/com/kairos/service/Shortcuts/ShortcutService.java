package com.kairos.service.Shortcuts;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.dto.activity.counter.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.persistence.model.shortcuts.Shortcut;
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

import static com.kairos.constants.ActivityMessagesConstants.SHORTCUT_ALREADY_EXISTS_NAME;
import static com.kairos.constants.ActivityMessagesConstants.SHORTCUT_NOT_FOUND;

@Service
public class ShortcutService {

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
        for (String tabId : tabIds) {
            tabKPIDTOS.add(new TabKPIDTO(unitId,tabId,tabIdAndTabKPIDtoMap.containsKey(tabId) ? tabIdAndTabKPIDtoMap.get(tabId).stream().map(tabKPIMappingDTO -> tabKPIMappingDTO.getKpiId()).collect(Collectors.toList()): new ArrayList<>()));
        }
        return tabKPIDTOS;
    }

    public ShortcutDTO saveShortcut(ShortcutDTO shortcutDTO){
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(shortcutDTO.getName(), shortcutDTO.getStaffId(), shortcutDTO.getUnitId(),BigInteger.valueOf(-1));
        if(existByName){
            exceptionService.duplicateDataException(SHORTCUT_ALREADY_EXISTS_NAME, shortcutDTO.getName());
        }
        shortcutDTO.setTabKPIs(getTabKPIs(shortcutDTO.getTabKPIs().stream().map(tabKPIDTO -> tabKPIDTO.getTabId()).collect(Collectors.toList()), new ArrayList<>(), shortcutDTO.getStaffId(), shortcutDTO.getUnitId()));
        Shortcut shortcut = shortcutsMongoRepository.save(ObjectMapperUtils.copyPropertiesByMapper(shortcutDTO, Shortcut.class));
        return ObjectMapperUtils.copyPropertiesByMapper(shortcut, ShortcutDTO.class);
    }

    public ShortcutDTO updateShortcut(BigInteger shortcutId , String name , ShortcutDTO shortcutDTO){
        Shortcut shortcut=shortcutsMongoRepository.findById(shortcutId).orElse(null);
        if(ObjectUtils.isNull(shortcut)){
           exceptionService.dataNotMatchedException(SHORTCUT_NOT_FOUND);
        }
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(ObjectUtils.isNotNull(name)?name:shortcutDTO.getName(),shortcut.getStaffId(),shortcut.getUnitId(),shortcut.getId());
        if(existByName){
          exceptionService.duplicateDataException(SHORTCUT_ALREADY_EXISTS_NAME,name);
        }
        if(ObjectUtils.isNotNull(name)){
            shortcut.setName(name);
        }else {
            shortcut=ObjectMapperUtils.copyPropertiesByMapper(shortcutDTO, Shortcut.class);
        }
        shortcutsMongoRepository.save(shortcut);
        return shortcutDTO;
    }

    public ShortcutDTO getShortcutById(BigInteger shortcutId){
        ShortcutDTO shortcutDTO = shortcutsMongoRepository.findShortcutById(shortcutId);
        if(Objects.isNull(shortcutDTO)){
            exceptionService.dataNotMatchedException(SHORTCUT_NOT_FOUND);
        }
        return shortcutDTO;
    }


    public boolean deleteShortcutById(BigInteger shortcutId){
        ShortcutDTO shortcutDTO = shortcutsMongoRepository.findShortcutById(shortcutId);
        if(Objects.isNull(shortcutDTO)){
           exceptionService.dataNotMatchedException(SHORTCUT_NOT_FOUND);
        }
        shortcutsMongoRepository.deleteById(shortcutId);
        return true;
    }


    public List<ShortcutDTO> getAllShortcutByStaffIdAndUnitId(Long unitId, Long staffId){
        return shortcutsMongoRepository.findShortcutByUnitIdAndStaffId(staffId,unitId);
    }

    public ShortcutDTO createCopyOfShortcut(BigInteger shortcutId, String name){
        Shortcut shortcut=shortcutsMongoRepository.findById(shortcutId).orElse(null);
        if(ObjectUtils.isNull(shortcut)){
            exceptionService.dataNotMatchedException(SHORTCUT_NOT_FOUND);
        }
        boolean existByName = shortcutsMongoRepository.existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(name,shortcut.getStaffId(),shortcut.getUnitId(),shortcut.getId());
        if(existByName){
            exceptionService.duplicateDataException(SHORTCUT_ALREADY_EXISTS_NAME,name);
        }
        shortcut.setName(name);
        shortcut.setId(null);
        shortcutsMongoRepository.save(shortcut);
        return ObjectMapperUtils.copyPropertiesByMapper(shortcut, ShortcutDTO.class);
    }

}
