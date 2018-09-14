package com.planner.service.shift_planning;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.shiftplanning.domain.Activity;
import com.kairos.shiftplanning.domain.wta.updated_wta.WorkingTimeAgreement;
import com.planner.domain.shift_planning.Shift;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.ActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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
     * @param unitPositionIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Shift> getAllShiftsByUnitPositionIds(List<Long> unitPositionIds, Date fromDate, Date toDate) {
        return activityMongoRepository.getAllShiftsByUnitPositionIds(unitPositionIds, fromDate, toDate);
    }
/***********************************************CTAService******************************************************************/
    /**
     * Used in {@link CTAService}
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public  List<CTAResponseDTO>  getCTARuleTemplateByUnitPositionIds(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        return  activityMongoRepository.getCTARuleTemplateByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
    }
    /*************************************WTAService***********************************************/
    /**Used in {@link WTAService}
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public List<WorkingTimeAgreement> getWTARuleTemplateByUnitPositionIds(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        return activityMongoRepository.getWTARuleTemplateByUnitPositionIds(unitPositionIds,fromPlanningDate,toPlanningDate);
    }

    //TODO temporary use ObjectMapperUtils(currently not working)
    public List<Activity> getConvertedActivityList(List<ActivityDTO> activities){
        List<Activity> activityList=new ArrayList<>();
        for(ActivityDTO activityDTO:activities)
        {
            Activity activity=new Activity();
            activity.setId(activityDTO.getId().toString());
            activity.setName(activityDTO.getName());
            //activity.setActivityConstraints(activityDTO.);
            //activity.setSkills(activityDTO.get);
            activityList.add(activity);
        }
        return activityList;
    }
}
