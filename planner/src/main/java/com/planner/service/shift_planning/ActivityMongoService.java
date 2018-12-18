package com.planner.service.shift_planning;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;

import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;

import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;

import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.shiftplanning.domain.Activity;
import com.kairos.shiftplanning.domain.TimeType;
import com.kairos.shiftplanning.domain.activityConstraint.*;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.wta.updated_wta.WorkingTimeAgreement;
import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.domain.shift_planning.Shift;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

/**
 * This service is used to interact with or have logic to collect data
 * from kairos mongodb
 *
 * @author mohit
 */
@Service
public class ActivityMongoService {

    @Inject
    private ActivityMongoRepository activityMongoRepository;

/**************************************************************************************/
    /**
     *
     * @param activitiesIds
     * @return
     */
    public List<ActivityDTO> getActivities(Set<String> activitiesIds) {

        return getActivitiesByIds(activitiesIds);
    }

/************************************************************************************/
    /**
     * @param organizationId
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<ShiftPlanningStaffingLevelDTO> getShiftPlanningStaffingLevelDTOByUnitId(Long organizationId, Date fromDate, Date toDate) {
        return activityMongoRepository.getShiftPlanningStaffingLevelDTOByUnitId(organizationId, fromDate, toDate);
    }
/************************************************************************************/
    /**
     * Here we return activitiesIds as String
     *
     * @param localDateStaffingLevelActivityMap
     * @return
     */
    public Set<String> getAllActivitiesIds(Map<LocalDate,Set<StaffingLevelActivity>> localDateStaffingLevelActivityMap) {
      Set<String> activityIds=new HashSet<>();
       for(LocalDate localDate:localDateStaffingLevelActivityMap.keySet())
       {
           for(StaffingLevelActivity staffingLevelActivity:localDateStaffingLevelActivityMap.get(localDate))
           {
               activityIds.add(staffingLevelActivity.getActivityId().toString());
           }

       }
        return activityIds;

        /*List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlotDTO = shiftPlanningStaffingLevelDTOList.stream().flatMap(s -> s.getPresenceStaffingLevelInterval().stream()).collect(Collectors.toList());
        Set<StaffingLevelActivity> staffingLevelActivitySet = staffingLevelTimeSlotDTO.stream().flatMap(s -> s.getStaffingLevelActivities().stream()).collect(Collectors.toSet());
        return staffingLevelActivitySet.stream().map(s -> s.getActivityId().toString()).collect(Collectors.toSet());*/
    }
/************************************************************************************/


    /**
     * @param acivitiesIds
     * @return
     */
    public List<ActivityDTO> getActivitiesByIds(Set<String> acivitiesIds) {
        return activityMongoRepository.getActivitiesById(acivitiesIds);
    }



/******************************************************************************************************/
    /**
     * @param organizationPositionIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Shift> getAllShiftsByUnitPositionIds(List<Long> organizationPositionIds, Date fromDate, Date toDate) {
        return activityMongoRepository.getAllShiftsByUnitPositionIds(organizationPositionIds, fromDate, toDate);
    }
/***********************************************CTAService******************************************************************/
    /**
     * Used in {@link CTAService}
     * @param organizationPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public  List<CTAResponseDTO>  getCTARuleTemplateByUnitPositionIds(List<Long> organizationPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        return  activityMongoRepository.getCTARuleTemplateByUnitPositionIds(organizationPositionIds, fromPlanningDate, toPlanningDate);
    }
    /*************************************WTAService***********************************************/
    /**Used in {@link WTAService}
     * @param organizationPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public List<WorkingTimeAgreement> getWTARuleTemplateByUnitPositionIds(List<Long> organizationPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        return activityMongoRepository.getWTARuleTemplateByUnitPositionIds(organizationPositionIds,fromPlanningDate,toPlanningDate);
    }

    //TODO temporary use ObjectMapperUtils(currently not working)
    public List<Activity> getConvertedActivityList(List<ActivityDTO> activities){
        List<Activity> activityList=new ArrayList<>();
        int order=0;
        int rank=0;
        for(ActivityDTO activityDTO:activities)
        {
            Activity activity=new Activity();
            activity.setId(activityDTO.getId().toString());
            activity.setName(activityDTO.getName());
            activity.setOrder(++order);
            activity.setActivityConstraints(getActivityContraints());
            TimeType timeType=ObjectMapperUtils.copyPropertiesByMapper(activityDTO.getTimeType(), TimeType.class);
            timeType.setName(activityDTO.getTimeType().getSecondLevelType().name().toLowerCase());
            activity.setTimeType(timeType);
            activity.setRank(++rank);
            //activity.setActivityConstraints(activityDTO.);
            //activity.setSkills(activityDTO.get);
            activityList.add(activity);
        }
        return activityList;
    }

    public ActivityConstraints getActivityContraints(){
        LongestDuration longestDuration = new LongestDuration(80, ScoreLevel.SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(60,ScoreLevel.HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(3,ScoreLevel.MEDIUM,-1);//3
        //ContinousActivityPerShift continousActivityPerShift = new ContinousActivityPerShift(3,ScoreLevel.SOFT,-4);
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3,ScoreLevel.MEDIUM,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(60,ScoreLevel.MEDIUM,-1);//5
        ActivityConstraints activityConstraints = new ActivityConstraints(longestDuration,shortestDuration,maxAllocationPerShift,maxDiffrentActivity,minimumLengthofActivity,null);
        return activityConstraints;
    }

    //

    public PlanningProblemDTO getPlanningPeriod(BigInteger planningPeriodId,Long unitId){
        return activityMongoRepository.getPlanningPeriod(planningPeriodId,unitId);
    }
}
