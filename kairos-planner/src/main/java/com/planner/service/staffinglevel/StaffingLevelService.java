package com.planner.service.staffinglevel;

import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.planner.domain.staffinglevel.StaffingLevel;
import com.planner.repository.staffinglevel.StaffingLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
@Service
public class StaffingLevelService {
    @Autowired
    private StaffingLevelRepository staffingLevelRepository;
    public void createStaffingLevel(Long unitId,  StaffingLevelDto staffingLevelDto) {
        StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId),staffingLevelDto.getPhaseId(),staffingLevelDto.getCurrentDate(),staffingLevelDto.getWeekCount(),staffingLevelDto.getStaffingLevelSetting(),staffingLevelDto.getStaffingLevelInterval(),staffingLevelDto.getId());
        staffingLevelRepository.save(sl);
    }
    public void updateStaffingLevel(BigInteger id, Long unitId,  StaffingLevelDto staffingLevelDto) {
        StaffingLevel sl = staffingLevelRepository.findByKairosId(id).get();
        sl.setStaffingLevelInterval(staffingLevelDto.getStaffingLevelInterval());
        sl.setStaffingLevelSetting(staffingLevelDto.getStaffingLevelSetting());
        staffingLevelRepository.save(sl);
    }
}
