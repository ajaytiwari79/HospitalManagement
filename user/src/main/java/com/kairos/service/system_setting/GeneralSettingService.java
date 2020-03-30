package com.kairos.service.system_setting;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.system_setting.GeneralSettingDTO;
import com.kairos.persistence.model.system_setting.GeneralSetting;
import com.kairos.persistence.repository.system_setting.GeneralSettingGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By G.P.Ranjan on 25/11/19
 **/
@Transactional
@Service
public class GeneralSettingService {
    @Inject
    private GeneralSettingGraphRepository generalSettingGraphRepository;

    public GeneralSettingDTO updateGeneralSetting(GeneralSettingDTO generalSettingDTO) {
        GeneralSetting generalSetting = ObjectMapperUtils.copyPropertiesOrCloneByMapper(generalSettingDTO, GeneralSetting.class);
        generalSettingGraphRepository.save(generalSetting);
        generalSettingDTO.setId(generalSetting.getId());
        return generalSettingDTO;
    }

    public List<GeneralSettingDTO> getGeneralSetting() {
        List<GeneralSettingDTO> generalSettingDTOS = new ArrayList<>();
        for(GeneralSetting generalSetting : generalSettingGraphRepository.findAll()){
            generalSettingDTOS.add(ObjectMapperUtils.copyPropertiesOrCloneByMapper(generalSetting, GeneralSettingDTO.class));
        }
        return generalSettingDTOS;
    }
}
