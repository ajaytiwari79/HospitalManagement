package com.planner.service.staffinglevel;

import com.kairos.dto.activity.staffing_level.StaffingLevelPlanningDTO;
import com.kairos.commons.utils.DateUtils;
import com.planner.domain.staffinglevel.StaffingLevel;
import com.planner.repository.staffinglevel.StaffingLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaffingLevelService {
    /*@Autowired
    private StaffingLevelRepository staffingLevelRepository;
    public void createStaffingLevel(Long unitId,  StaffingLevelPlanningDTO staffingLevelPlanningDto) {
        StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId), staffingLevelPlanningDto.getPhaseId(),DateUtils.getLocalDateFromDate(staffingLevelPlanningDto.getCurrentDate())
                , staffingLevelPlanningDto.getWeekCount(), staffingLevelPlanningDto.getStaffingLevelSetting(), staffingLevelPlanningDto.getPresenceStaffingLevelInterval(), staffingLevelPlanningDto.getAbsenceStaffingLevelInterval(),
                staffingLevelPlanningDto.getId());
        staffingLevelRepository.save(sl);
    }
    public void updateStaffingLevel(BigInteger id, Long unitId,  StaffingLevelPlanningDTO staffingLevelPlanningDto) {
        StaffingLevel sl = staffingLevelRepository.findByKairosId(id).get();
        sl.setPresenceStaffingLevelInterval(staffingLevelPlanningDto.getPresenceStaffingLevelInterval());
        sl.setAbsenceStaffingLevelInterval(staffingLevelPlanningDto.getAbsenceStaffingLevelInterval());
        sl.setStaffingLevelSetting(staffingLevelPlanningDto.getStaffingLevelSetting());
        staffingLevelRepository.save(sl);
    }

    public void createStaffingLevels(Long unitId, List<StaffingLevelPlanningDTO> staffingLevelPlanningDtos) {
        List<StaffingLevel> staffingLevels= new ArrayList<>();
        for (StaffingLevelPlanningDTO staffingLevelPlanningDto : staffingLevelPlanningDtos){
            StaffingLevel sl = new StaffingLevel(BigInteger.valueOf(unitId), staffingLevelPlanningDto.getPhaseId(),DateUtils.getLocalDateFromDate(staffingLevelPlanningDto.getCurrentDate())
                    , staffingLevelPlanningDto.getWeekCount(), staffingLevelPlanningDto.getStaffingLevelSetting(), staffingLevelPlanningDto.getPresenceStaffingLevelInterval(), staffingLevelPlanningDto.getAbsenceStaffingLevelInterval(),
                    staffingLevelPlanningDto.getId());
            staffingLevels.add(sl);
        }
        staffingLevelRepository.saveAll(staffingLevels);
    }*/
}
