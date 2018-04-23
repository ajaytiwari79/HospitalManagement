package com.planner.service.staffinglevel;

import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.planner.domain.staffinglevel.StaffingLevel;
import org.springframework.beans.factory.annotation.Autowired;


public class StaffingLevelService {
    @Autowired
    //CassandraTemplate cassandraTemplate;
    public void createStaffingLevel(Long unitId,  StaffingLevelDto staffingLevelDto) {
        StaffingLevel sl = new StaffingLevel(staffingLevelDto.getPhaseId(),staffingLevelDto.getCurrentDate(),staffingLevelDto.getWeekCount(),staffingLevelDto.getStaffingLevelSetting(),staffingLevelDto.getStaffingLevelInterval());
      //  cassandraTemplate.insert(sl);
    }
}
