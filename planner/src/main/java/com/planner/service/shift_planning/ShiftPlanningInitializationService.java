package com.planner.service.shift_planning;

import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.planner.domain.query_results.StaffQueryResult;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.ActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This service will interact with
 * {@link ActivityMongoService}
 * and
 * {@link UserNeo4jService}
 * to prepare {ShiftPlanningInitialization Data}
 * @author mohit
 */
@Service
public class ShiftPlanningInitializationService {

    @Inject
    private ActivityMongoService activityMongoService;

    @Inject
    private UserNeo4jService userNeo4jService;

    /**
     *
     */
    public List<ActivityDTO> getActivities(Long unitId, Date fromDate, Date toDate) {
        return activityMongoService.getActivities(unitId,fromDate,toDate);
    }

    /**
     * Mehod to get All staffList by ids
     * @param staffIds
     * @return
     */
    public List<StaffQueryResult> getStaffWithSkillsAndUnitPostionIds(Long unitId,Long[] staffIds){
       return userNeo4jService.getStaffWithSkillsAndUnitPostionIds(unitId,staffIds);
    }
}
