package com.kairos.util.serviceutil;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevel;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelInterval;
import com.kairos.activity.response.dto.staffing_level.AbsenceStaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.PresenceStaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTimeSlotDTO;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StaffingLevelUtil {
    public static StaffingLevel buildPresenceStaffingLevels(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long orgId){

        StaffingLevel staffingLevel=new StaffingLevel(presenceStaffingLevelDTO.getCurrentDate(), presenceStaffingLevelDTO.getWeekCount()
                ,orgId, presenceStaffingLevelDTO.getPhaseId(), presenceStaffingLevelDTO.getStaffingLevelSetting());

        Set<StaffingLevelInterval> staffingLevelTimeSlotsList=new LinkedHashSet<>();
        for(StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval()){
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
    public static StaffingLevel updateStaffingLevels(BigInteger staffingLevelId, PresenceStaffingLevelDto presenceStaffingLevelDTO,
                                                     Long unitId, StaffingLevel staffingLevel){

        BeanUtils.copyProperties(presenceStaffingLevelDTO,staffingLevel);
        staffingLevel.setUnitID(unitId);
        staffingLevel.setId(staffingLevelId);
        return staffingLevel;

    }
    public static StaffingLevel updateAbsenceStaffingLevels(BigInteger staffingLevelId, AbsenceStaffingLevelDto absenceStaffingLevelDto,
                                                     Long unitId, StaffingLevel staffingLevel){

        staffingLevel.setPhaseId(absenceStaffingLevelDto.getPhaseId());
        staffingLevel.setWeekCount(absenceStaffingLevelDto.getWeekCount());
        staffingLevel.setUnitID(unitId);

        StaffingLevelDuration staffingLevelDuration = new StaffingLevelDuration(LocalTime.MIN, LocalTime.MAX);
        List<StaffingLevelInterval> absenceStaffingLevelIntervals = new ArrayList<StaffingLevelInterval>();
        StaffingLevelInterval absenceStaffingLevelInterval =  new StaffingLevelInterval(0, absenceStaffingLevelDto.getMinNoOfStaff(),
                absenceStaffingLevelDto.getMaxNoOfStaff(), staffingLevelDuration);
        absenceStaffingLevelInterval.setStaffingLevelActivities(absenceStaffingLevelDto.getStaffingLevelActivities());
        absenceStaffingLevelIntervals.add(absenceStaffingLevelInterval);
        staffingLevel.setAbsenceStaffingLevelInterval(absenceStaffingLevelIntervals);

        return staffingLevel;

    }
}
