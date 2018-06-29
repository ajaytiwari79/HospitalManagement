package com.planner.service.staffinglevel;

import com.kairos.activity.staffing_level.StaffingLevelDTO;
import com.kairos.util.DateUtils;
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
    public void createStaffingLevel(Long unitId,  StaffingLevelDTO staffingLevelDto) {
        StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId),staffingLevelDto.getPhaseId(),DateUtils.getLocalDateFromDate(staffingLevelDto.getCurrentDate())
                ,staffingLevelDto.getWeekCount(),staffingLevelDto.getStaffingLevelSetting(),staffingLevelDto.getPresenceStaffingLevelInterval(),staffingLevelDto.getAbsenceStaffingLevelInterval(),
                staffingLevelDto.getId());
        staffingLevelRepository.save(sl);
    }
    public void updateStaffingLevel(BigInteger id, Long unitId,  StaffingLevelDTO staffingLevelDto) {
        StaffingLevel sl = staffingLevelRepository.findByKairosId(id).get();
        sl.setPresenceStaffingLevelInterval(staffingLevelDto.getPresenceStaffingLevelInterval());
        sl.setAbsenceStaffingLevelInterval(staffingLevelDto.getAbsenceStaffingLevelInterval());
        sl.setStaffingLevelSetting(staffingLevelDto.getStaffingLevelSetting());
        staffingLevelRepository.save(sl);
    }

    public void createStaffingLevels(Long unitId, List<StaffingLevelDTO> staffingLevelDtos) {
        List<StaffingLevel> staffingLevels= new ArrayList<>();
        for (StaffingLevelDTO staffingLevelDto:staffingLevelDtos){
            StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId),staffingLevelDto.getPhaseId(),DateUtils.getLocalDateFromDate(staffingLevelDto.getCurrentDate())
                    ,staffingLevelDto.getWeekCount(),staffingLevelDto.getStaffingLevelSetting(),staffingLevelDto.getPresenceStaffingLevelInterval(),staffingLevelDto.getAbsenceStaffingLevelInterval(),
                    staffingLevelDto.getId());
            staffingLevels.add(sl);
        }
        staffingLevelRepository.saveAll(staffingLevels);
    }
}
