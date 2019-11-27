package com.kairos.service.Shortcuts;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.ShortCuts.ShortcutsDTO;
import com.kairos.persistence.model.shortcuts.Shortcuts;
import com.kairos.persistence.repository.Shortcuts.ShortcutsMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Service
public class ShortcutsService {

    @Inject
    private ShortcutsMongoRepository  shortcutsMongoRepository;


    public ShortcutsDTO saveShortcut(ShortcutsDTO shortcutsDTO){
        ShortcutsDTO shortcut=shortcutsMongoRepository.findShortcutByUnitIdAndStaffIdAndName(shortcutsDTO.getUnitId(),shortcutsDTO.getStaffId(),shortcutsDTO.getName());
        if(ObjectUtils.isNotNull(shortcut)){

        }
        shortcutsMongoRepository.save(ObjectMapperUtils.copyPropertiesByMapper(shortcutsDTO,Shortcuts.class));
        return shortcutsDTO;
    }

    public ShortcutsDTO updateShortcut(ShortcutsDTO shortcutsDTO){
        Shortcuts shortcuts=shortcutsMongoRepository.findById(shortcutsDTO.getId()).orElse(null);
        if(ObjectUtils.isNull(shortcuts)){

        }
        shortcuts= ObjectMapperUtils.copyPropertiesByMapper(shortcutsDTO,Shortcuts.class);
        shortcutsMongoRepository.save(shortcuts);
        return shortcutsDTO;
    }

    public ShortcutsDTO getShortcutById(BigInteger shortcutId){
        return shortcutsMongoRepository.findShortcutById(shortcutId);
    }

    public List<ShortcutsDTO> getAllShortcutByStaffIdAndUnitId(Long unitId,Long staffId){
        return shortcutsMongoRepository.findShortcutByUnitIdAndStaffId(unitId,staffId);
    }


}
