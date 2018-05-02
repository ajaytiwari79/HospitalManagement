package com.planner.service.staffinglevel;

import com.kairos.activity.response.dto.staffing_level.PresenceStaffingLevelDto;
import com.planner.domain.staffinglevel.StaffingLevel;
import com.planner.repository.staffinglevel.StaffingLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaffingLevelService {
    @Autowired
    private StaffingLevelRepository staffingLevelRepository;
    public void createStaffingLevel(Long unitId,  PresenceStaffingLevelDto staffingLevelDto) {
        StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId),staffingLevelDto.getPhaseId(),staffingLevelDto.getCurrentDate(),staffingLevelDto.getWeekCount(),staffingLevelDto.getStaffingLevelSetting(),staffingLevelDto.getPresenceStaffingLevelInterval(),staffingLevelDto.getId());
        staffingLevelRepository.save(sl);
    }
    public void updateStaffingLevel(BigInteger id, Long unitId,  PresenceStaffingLevelDto staffingLevelDto) {
        StaffingLevel sl = staffingLevelRepository.findByKairosId(id).get();
        sl.setStaffingLevelInterval(staffingLevelDto.getPresenceStaffingLevelInterval());
        sl.setStaffingLevelSetting(staffingLevelDto.getStaffingLevelSetting());
        staffingLevelRepository.save(sl);
    }

    public void createStaffingLevels(Long unitId, List<PresenceStaffingLevelDto> staffingLevelDtos) {
        List<StaffingLevel> staffingLevels= new ArrayList<>();
        for (PresenceStaffingLevelDto staffingLevelDto:staffingLevelDtos){
            StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId),staffingLevelDto.getPhaseId(),staffingLevelDto.getCurrentDate(),staffingLevelDto.getWeekCount(),staffingLevelDto.getStaffingLevelSetting(),staffingLevelDto.getPresenceStaffingLevelInterval(),staffingLevelDto.getId());
            staffingLevels.add(sl);
        }
        staffingLevelRepository.saveAll(staffingLevels);
    }
}
