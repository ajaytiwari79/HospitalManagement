package com.planner.service.shift_planning;

import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.ActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This service will interact with
 * {@link ActivityMongoService}
 * and
 * {}
 * to prepare {}ShiftPlanningInitialization Data}
 * @author mohit
 */
@Service
public class ShiftPlanningInitializationService {

    @Inject
    private ActivityMongoService activityMongoService;

    /**
     *Only Method Responsible to prepare shiftPlanningInitialization Data
     */
    public List<ActivityDTO> shiftPlanningInitialization(Long unitId, Date fromDate, Date toDate) {
        List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList = activityMongoService.getShiftPlanningStaffingLevelDTOByUnitId(unitId, fromDate, toDate);
        Set<String> activitiesIds = activityMongoService.getActivitiesIds(shiftPlanningStaffingLevelDTOList);
        return activityMongoService.getActivitiesByIds(activitiesIds);
    }
}
