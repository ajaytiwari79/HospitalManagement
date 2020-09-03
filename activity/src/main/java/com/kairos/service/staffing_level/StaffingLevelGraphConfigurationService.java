package com.kairos.service.staffing_level;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.staffing_level.DailyGraphConfiguration;
import com.kairos.dto.activity.staffing_level.StaffingLevelGraphConfigurationDTO;
import com.kairos.dto.activity.staffing_level.WeeklyGraphConfiguration;
import com.kairos.persistence.model.staffing_level.StaffingLevelGraphConfiguration;
import com.kairos.persistence.repository.staffing_level.StaffingLevelGraphConfigurationMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class StaffingLevelGraphConfigurationService {

    @Inject private StaffingLevelGraphConfigurationMongoRepository staffingLevelGraphConfigurationMongoRepository;


    public StaffingLevelGraphConfigurationDTO updateOrCreateStaffingLevelConfiguration(Long unitId,Long userId,StaffingLevelGraphConfigurationDTO staffingLevelGraphConfigurationDTO){
        StaffingLevelGraphConfiguration staffingLevelGraphConfiguration = Optional.ofNullable(staffingLevelGraphConfigurationMongoRepository.findOneByUnitIdAndUserId(unitId,userId)).orElse(getDefaultConfiguration(unitId,userId));
        if(isNotNull(staffingLevelGraphConfigurationDTO.getDailyGraphConfiguration())){
            staffingLevelGraphConfiguration.setDailyGraphConfiguration(staffingLevelGraphConfigurationDTO.getDailyGraphConfiguration());
        }if (isNotNull(staffingLevelGraphConfigurationDTO.getWeeklyGraphConfiguration())){
            staffingLevelGraphConfiguration.setWeeklyGraphConfiguration(staffingLevelGraphConfigurationDTO.getWeeklyGraphConfiguration());
        }
        staffingLevelGraphConfigurationMongoRepository.save(staffingLevelGraphConfiguration);
        return ObjectMapperUtils.copyPropertiesByMapper(staffingLevelGraphConfiguration,StaffingLevelGraphConfigurationDTO.class);
    }

    private StaffingLevelGraphConfiguration getDefaultConfiguration(Long unitId,Long userId) {
        WeeklyGraphConfiguration weeklyGraphConfiguration = new WeeklyGraphConfiguration(new ArrayList<>(),new ArrayList<>(),false,false,false,1,1,new int[]{2,4,6},new int[]{2,4,6});
        DailyGraphConfiguration dailyGraphConfiguration = new DailyGraphConfiguration(true,false,false,true,true,false,false,false,10,10,0,24,new int[]{2,4,6},new int[]{2,4,6});
        return new StaffingLevelGraphConfiguration(weeklyGraphConfiguration,dailyGraphConfiguration,userId,unitId);
    }

    public StaffingLevelGraphConfigurationDTO getStaffingLevelGraphConfiguration(Long unitId,Long userId){
        StaffingLevelGraphConfiguration staffingLevelGraphConfiguration = Optional.ofNullable(staffingLevelGraphConfigurationMongoRepository.findOneByUnitIdAndUserId(unitId,userId)).orElse(getDefaultConfiguration(unitId,userId));
        return ObjectMapperUtils.copyPropertiesByMapper(staffingLevelGraphConfiguration,StaffingLevelGraphConfigurationDTO.class);
    }
}
