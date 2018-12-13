package com.planner.service.shift_planning;


import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
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
    Map<LocalDate, List<StaffingLevelInterval>> getStaffingLevelTimeSlotByDate(List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList) {
        return shiftPlanningStaffingLevelDTOList.stream().collect(Collectors.toMap(k -> k.getCurrentDate(), v -> v.getPresenceStaffingLevelInterval()));
    }

/******************************************************************************/
    /**
     *
     * @param map
     * @return
     */
    Map<LocalDate,Set<StaffingLevelActivity>> getStaffingLevelActivityByDay(Map<LocalDate,List<StaffingLevelInterval>> map)
    {

        Map<LocalDate,Set<StaffingLevelActivity>> map1=new HashMap<>();
        for(LocalDate localDate:map.keySet()){
            for(StaffingLevelInterval staffingLevelInterval:map.get(localDate))
            {
                if(staffingLevelInterval.getStaffingLevelActivities().size()>0)
                map1.put(localDate,staffingLevelInterval.getStaffingLevelActivities());
            }

        }
        return map1;
    }
}
