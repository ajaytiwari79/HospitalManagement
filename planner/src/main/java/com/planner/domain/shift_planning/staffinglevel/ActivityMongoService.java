package com.planner.domain.shift_planning.staffinglevel;

import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.ActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This service is used to interact with or have logic to collect data
 * from kairos mongodb
 * @author mohit
 *
 */
@Service
public class ActivityMongoService {

    @Inject
    private ActivityMongoRepository activityMongoRepository;

/************************************************************************************/
    /**
     * @param unitId
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<ShiftPlanningStaffingLevelDTO> getShiftPlanningStaffingLevelDTOByUnitId(Long unitId, Date fromDate, Date toDate) {
        return activityMongoRepository.getShiftPlanningStaffingLevelDTOByUnitId(unitId, fromDate, toDate);
    }
/************************************************************************************/
    /**
     * Here we return activitiesId as String
     *
     * @param shiftPlanningStaffingLevelDTOList
     * @return
     */
    public Set<String> getActivitiesIds(List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList) {
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlotDTO = shiftPlanningStaffingLevelDTOList.stream().flatMap(s -> s.getPresenceStaffingLevelInterval().stream()).collect(Collectors.toList());
        Set<StaffingLevelActivity> staffingLevelActivitySet = staffingLevelTimeSlotDTO.stream().flatMap(s -> s.getStaffingLevelActivities().stream()).collect(Collectors.toSet());
        return staffingLevelActivitySet.stream().map(s -> s.getActivityId().toString()).collect(Collectors.toSet());
    }
/************************************************************************************/
    /**
     * @param acivitiesIds
     * @return
     */
    public List<ActivityDTO> getActivitiesByIds(Set<String> acivitiesIds) {
        return activityMongoRepository.getActivitiesById(acivitiesIds);
    }
}
