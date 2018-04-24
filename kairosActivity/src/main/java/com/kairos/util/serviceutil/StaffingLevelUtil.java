package com.kairos.util.serviceutil;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevel;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelInterval;
import com.kairos.activity.response.dto.staffing_level.AbsenceStaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTimeSlotDTO;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StaffingLevelUtil {
    public static StaffingLevel buildPresenceStaffingLevels(StaffingLevelDto staffingLevelDTO, Long orgId){

        StaffingLevel staffingLevel=new StaffingLevel(staffingLevelDTO.getCurrentDate(),staffingLevelDTO.getWeekCount()
                ,orgId,staffingLevelDTO.getPhaseId(),staffingLevelDTO.getStaffingLevelSetting());

        Set<StaffingLevelInterval> staffingLevelTimeSlotsList=new LinkedHashSet<>();
        for(StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO :staffingLevelDTO.getPresenceStaffingLevelInterval()){
            StaffingLevelInterval staffingLevelTimeSlot=new StaffingLevelInterval(staffingLevelTimeSlotDTO.getSequence(),staffingLevelTimeSlotDTO.getMinNoOfStaff(),
                    staffingLevelTimeSlotDTO.getMaxNoOfStaff(),staffingLevelTimeSlotDTO.getStaffingLevelDuration()
            );
            staffingLevelTimeSlot.addStaffLevelActivity(staffingLevelTimeSlotDTO.getStaffingLevelActivities());
            staffingLevelTimeSlot.addStaffLevelSkill(staffingLevelTimeSlotDTO.getStaffingLevelSkills());
            staffingLevelTimeSlotsList.add(staffingLevelTimeSlot);
        }

        staffingLevel.addStaffingLevelTimeSlot(staffingLevelTimeSlotsList);
        return staffingLevel;

    }

    public static StaffingLevel buildAbsenceStaffingLevels(AbsenceStaffingLevelDto absenceStaffingLevelDto, Long unitId){

        StaffingLevel staffingLevel=new StaffingLevel(absenceStaffingLevelDto.getCurrentDate(),absenceStaffingLevelDto.getWeekCount()
                ,unitId,absenceStaffingLevelDto.getPhaseId());

        StaffingLevelDuration staffingLevelDuration = new StaffingLevelDuration(LocalTime.MIN, LocalTime.MAX);
        List<StaffingLevelInterval> absenceStaffingLevelIntervals = new ArrayList<StaffingLevelInterval>();
        StaffingLevelInterval absenceStaffingLevelInterval =  new StaffingLevelInterval(0, absenceStaffingLevelDto.getMinNoOfStaff(),
                absenceStaffingLevelDto.getMaxNoOfStaff(), staffingLevelDuration);
        absenceStaffingLevelInterval.setStaffingLevelActivities(absenceStaffingLevelDto.getStaffingLevelActivities());
        absenceStaffingLevelIntervals.add(absenceStaffingLevelInterval);
        staffingLevel.setAbsenceStaffingLevelInterval(absenceStaffingLevelIntervals);
        return staffingLevel;
    }
    public static StaffingLevel updateStaffingLevels(BigInteger staffingLevelId, StaffingLevelDto staffingLevelDTO,
                                                     Long unitId, StaffingLevel staffingLevel){

        BeanUtils.copyProperties(staffingLevelDTO,staffingLevel);
        staffingLevel.setUnitID(unitId);
        staffingLevel.setId(staffingLevelId);
        return staffingLevel;

    }
}
