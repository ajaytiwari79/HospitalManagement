package com.planner.service.shift_planning;

import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelTimeSlotDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffingLevelService {
    @Inject
    private ActivityMongoService activityMongoService;


    /**
     * @param unitId
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<ShiftPlanningStaffingLevelDTO> getShiftPlanningStaffingLevelDTOByUnitId(Long unitId, Date fromDate, Date toDate) {
        return activityMongoService.getShiftPlanningStaffingLevelDTOByUnitId(unitId, fromDate, toDate);
    }
/******************************************************************************/

    /**
     * @param shiftPlanningStaffingLevelDTOList
     * @return
     */
    Map<LocalDate, List<StaffingLevelTimeSlotDTO>> getStaffingLevelTimeSlotByDate(List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList) {
        return shiftPlanningStaffingLevelDTOList.stream().collect(Collectors.toMap(k -> k.getCurrentDate(), v -> v.getPresenceStaffingLevelInterval()));
    }

/******************************************************************************/
    /**
     *
     * @param map
     * @return
     */
    Map<LocalDate,Set<StaffingLevelActivity>> getStaffingLevelActivityByDay( Map<LocalDate,List<StaffingLevelTimeSlotDTO>> map)
    {

        Map<LocalDate,Set<StaffingLevelActivity>> map1=new HashMap<>();
        for(LocalDate localDate:map.keySet()){
            for(StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO:map.get(localDate))
            {
                if(staffingLevelTimeSlotDTO.getStaffingLevelActivities().size()>0)
                map1.put(localDate,staffingLevelTimeSlotDTO.getStaffingLevelActivities());
            }

        }
        return map1;
    }
}
