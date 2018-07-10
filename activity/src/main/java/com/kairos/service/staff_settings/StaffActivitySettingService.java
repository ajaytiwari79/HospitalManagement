package com.kairos.service.staff_settings;

import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class StaffActivitySettingService extends MongoBaseService {

    @Inject private StaffActivitySettingRepository staffActivitySettingRepository;

    public StaffActivitySettingDTO createStaffActivitySetting(Long unitId,StaffActivitySettingDTO staffActivitySettingDTO){
        StaffActivitySetting staffActivitySetting=new StaffActivitySetting();
        ObjectMapperUtils.copyProperties(staffActivitySettingDTO,staffActivitySetting);
        staffActivitySetting.setUnitId(unitId);
        save(staffActivitySetting);
        staffActivitySettingDTO.setId(staffActivitySetting.getId());
        return staffActivitySettingDTO;
    }

    public List<StaffActivitySettingDTO> getStaffActivitySettings(Long unitId){
        return staffActivitySettingRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }
}
