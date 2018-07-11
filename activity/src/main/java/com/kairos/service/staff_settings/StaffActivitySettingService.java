package com.kairos.service.staff_settings;

import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.user.staff.staff_settings.StaffAndActivitySettingWrapper;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffActivitySettingService extends MongoBaseService {

    @Inject private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ActivityMongoRepository activityMongoRepository;

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

    public StaffActivitySettingDTO updateStaffActivitySettings(BigInteger staffActivitySettingId,Long unitId, StaffActivitySettingDTO staffActivitySettingDTO){
        StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByIdAndDeletedFalse(staffActivitySettingId);
        if(!Optional.ofNullable(staffActivitySetting).isPresent()){
            exceptionService.dataNotFoundException("message.staff.activity.settings.absent");
        }
        ObjectMapperUtils.copyProperties(staffActivitySettingDTO,staffActivitySetting);
        staffActivitySetting.setUnitId(unitId);
        save(staffActivitySetting);
        return staffActivitySettingDTO;
    }

    public boolean deleteStaffActivitySettings(BigInteger staffActivitySettingId){
        StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByIdAndDeletedFalse(staffActivitySettingId);
        if(!Optional.ofNullable(staffActivitySetting).isPresent()){
            exceptionService.dataNotFoundException("message.staff.activity.settings.absent");
        }
        staffActivitySetting.setDeleted(true);
        save(staffActivitySetting);
        return true;
    }

    public StaffActivitySettingDTO getDefaultStaffActivitySettings(Long unitId,BigInteger activityId){
        return activityMongoRepository.findStaffPersonalizedSettings(unitId,activityId);
    }

    public StaffActivitySettingDTO assignActivitySettingToStaffs(Long unitId,StaffAndActivitySettingWrapper staffAndActivitySettingWrapper){
        List<StaffActivitySetting> staffActivitySettings=new ArrayList<>();
        staffAndActivitySettingWrapper.getStaffIds().forEach(staff->{
            staffAndActivitySettingWrapper.getStaffActivitySettings().forEach(staffActivitySetting -> {
                StaffActivitySetting currentStaffActivitySetting=new StaffActivitySetting(staff,staffActivitySetting.getActivityId(),staffActivitySetting.getUnitPositionId(),
                        unitId,staffActivitySetting.getShortestTime(),staffActivitySetting.getLongestTime(),staffActivitySetting.getMinLength(),staffActivitySetting.getMaxThisActivityPerShift(),
                        staffActivitySetting.isEligibleForMove());
                staffActivitySettings.add(currentStaffActivitySetting);
            });});
        save(staffActivitySettings);
        return null;//intentionally returning null
    }

    public List<StaffActivitySettingDTO> getStaffSpecificActivitySettings(Long unitId,Long staffId){
        return staffActivitySettingRepository.findAllByUnitIdAndStaffIdAndDeletedFalse(unitId,staffId);
    }






}
