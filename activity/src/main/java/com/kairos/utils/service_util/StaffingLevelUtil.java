package com.kairos.utils.service_util;

import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaffingLevelUtil {

    public static StaffingLevel buildPresenceStaffingLevels(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long orgId){
        presenceStaffingLevelDTO.getStaffingLevelSetting().setActivitiesRank(null);
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

        Duration staffingLevelDuration = new Duration(LocalTime.MIN, LocalTime.MAX);
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
        staffingLevel.setUnitId(unitId);
        staffingLevel.setId(staffingLevelId);
        return staffingLevel;

    }

    public static StaffingLevel updateAbsenceStaffingLevels(AbsenceStaffingLevelDto absenceStaffingLevelDto,
                                                     Long unitId, StaffingLevel staffingLevel){

        staffingLevel.setPhaseId(absenceStaffingLevelDto.getPhaseId());
        staffingLevel.setWeekCount(absenceStaffingLevelDto.getWeekCount());
        staffingLevel.setUnitId(unitId);

        Duration staffingLevelDuration = new Duration(LocalTime.MIN, LocalTime.MAX);
        List<StaffingLevelInterval> absenceStaffingLevelIntervals = new ArrayList<StaffingLevelInterval>();
        StaffingLevelInterval absenceStaffingLevelInterval =  new StaffingLevelInterval(0, absenceStaffingLevelDto.getMinNoOfStaff(),
                absenceStaffingLevelDto.getMaxNoOfStaff(), staffingLevelDuration);
        absenceStaffingLevelInterval.setStaffingLevelActivities(absenceStaffingLevelDto.getStaffingLevelActivities());
        absenceStaffingLevelIntervals.add(absenceStaffingLevelInterval);
        staffingLevel.setAbsenceStaffingLevelInterval(absenceStaffingLevelIntervals);

        return staffingLevel;

    }

    public static List<AbsenceStaffingLevelDto> buildAbsenceStaffingLevelDto(List<StaffingLevel> staffingLevels) {
        List<AbsenceStaffingLevelDto> absenceStaffingLevelDtos = new ArrayList<AbsenceStaffingLevelDto>();

        for(StaffingLevel staffingLevel : staffingLevels) {
            AbsenceStaffingLevelDto absenceStaffingLevelDto = new AbsenceStaffingLevelDto(staffingLevel.getId(),staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(),staffingLevel.getWeekCount());
            absenceStaffingLevelDto.setMinNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMinNoOfStaff());
            absenceStaffingLevelDto.setMaxNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMaxNoOfStaff());
            absenceStaffingLevelDto.setAbsentNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getAvailableNoOfStaff());
            absenceStaffingLevelDto.setStaffingLevelActivities(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
            absenceStaffingLevelDtos.add(absenceStaffingLevelDto);

        }

        return absenceStaffingLevelDtos;

    }

    public static void sortStaffingLevelActivities(PresenceStaffingLevelDto presenceStaffingLevelDto, Map<BigInteger,Integer> activitiesRankMap) {

        for (StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO:presenceStaffingLevelDto.getPresenceStaffingLevelInterval()){
            Map<BigInteger,StaffingLevelActivity> staffingLevelActivityMap=staffingLevelTimeSlotDTO.getStaffingLevelActivities().stream().collect(Collectors.toMap(StaffingLevelActivity::getActivityId,Function.identity()));
            StaffingLevelActivity staffingLevelActivities[]=new StaffingLevelActivity[staffingLevelTimeSlotDTO.getStaffingLevelActivities().size()];
            if(activitiesRankMap!=null) {
                activitiesRankMap.forEach((activityId, rank) -> {
                    staffingLevelActivities[rank - 1] = staffingLevelActivityMap.get(activityId);
                });
            }
            staffingLevelTimeSlotDTO.setStaffingLevelActivities(new LinkedHashSet<>(Arrays.asList(staffingLevelActivities)));

        }

    }
}
