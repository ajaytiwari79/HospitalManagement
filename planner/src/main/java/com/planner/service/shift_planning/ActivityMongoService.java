package com.planner.service.shift_planning;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.constraints.activityconstraint.*;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.domain.wta.WorkingTimeAgreement;
import com.planner.domain.shift_planning.Shift;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.enums.MasterDataTypeEnum.STAFF;

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
    public List<Shift> getAllShiftsByEmploymentIds(List<Long> organizationPositionIds, Date fromDate, Date toDate) {
        return activityMongoRepository.getAllShiftsByEmploymentIds(organizationPositionIds, fromDate, toDate);
    }
/***********************************************CTAService******************************************************************/
    /**
     * Used in {@link CTAService}
     * @param organizationPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public  List<CTAResponseDTO> getCTARuleTemplateByEmploymentIds(List<Long> organizationPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        return  activityMongoRepository.getCTARuleTemplateByEmploymentIds(organizationPositionIds, fromPlanningDate, toPlanningDate);
    }
    /*************************************WTAService***********************************************/
    /**Used in {@link WTAService}
     * @param organizationPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public List<WorkingTimeAgreement> getWTARuleTemplateByEmploymentIds(List<Long> organizationPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        return activityMongoRepository.getWTARuleTemplateByEmploymentIds(organizationPositionIds,fromPlanningDate,toPlanningDate);
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
            activity.setConstraintMap(getActivityContraints());
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

    public Map<ConstraintSubType, Constraint> getActivityContraints(){
        LongestDuration longestDuration = new LongestDuration(80, ScoreLevel.SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(60,ScoreLevel.HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(3,ScoreLevel.MEDIUM,-1);//3
        //ContinousActivityPerShift continousActivityPerShift = new ContinousActivityPerShift(3,ScoreLevel.SOFT,-4);
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3,ScoreLevel.MEDIUM,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(60,ScoreLevel.MEDIUM,-1);//5
        ActivityRequiredTag activityRequiredTag = new ActivityRequiredTag(requiredTagId(),ScoreLevel.HARD,1);
        Map<ConstraintSubType, Constraint> constraintMap = new HashMap<>();
        constraintMap.put(ConstraintSubType.ACTIVITY_LONGEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,longestDuration);
        constraintMap.put(ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,shortestDuration);
        constraintMap.put(ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,maxAllocationPerShift);
        constraintMap.put(ConstraintSubType.ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS,maxDiffrentActivity);
        constraintMap.put(ConstraintSubType.MINIMUM_LENGTH_OF_ACTIVITY,minimumLengthofActivity);
        //constraintMap.put(ConstraintSubType.ACTIVITY_VALID_DAYTYPE,activityDayType);
        constraintMap.put(ConstraintSubType.ACTIVITY_REQUIRED_TAG,activityRequiredTag);
        return constraintMap;
    }

    public Tag requiredTagId(){
        Tag tag = new Tag(1l,"StaffTag", STAFF);;
        return tag;
    }
    //

    public PlanningProblemDTO getPlanningPeriod(BigInteger planningPeriodId,Long unitId){
        return activityMongoRepository.getPlanningPeriod(planningPeriodId,unitId);
    }
}
