package com.planner.service.shift_planning;
import com.kairos.commons.utils.DateUtils;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.shiftplanning.domain.Activity;
import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.Employee;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.wta.updated_wta.WorkingTimeAgreement;
import com.planner.domain.query_results.StaffQueryResult;
import com.planner.domain.shift_planning.Shift;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This service will interact with
 * {@link ActivityMongoService}
 * and
 * {@link UserNeo4jService}
 * to prepare {ShiftPlanningInitialization Data}
 *
 * @author mohit
 */
@Service
public class ShiftPlanningInitializationService {

    @Inject
    private ActivityMongoService activityMongoService;
    @Inject
    private UserNeo4jService userNeo4jService;
    @Inject
    private CTAService ctaService;
    @Inject
    private SkillService skillService;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private WTAService wtaService;


    /**
     * ShiftRequestPhasePlanningSolution(Opta-planner planning Solution)
     */
    public void initializeShiftPlanning(Long unitId, Date fromPlanningDate, Date toPlanningDate, Long[] staffIds) {
        //Prepare Data
        List<StaffQueryResult> staffWithSkillsAndUnitPostionIds = userNeo4jService.getStaffWithSkillsAndUnitPostionIds(unitId, staffIds);
        List<Long> unitPositionIds = staffWithSkillsAndUnitPostionIds.stream().map(s -> s.getStaffUnitPosition()).collect(Collectors.toList());

        //1.) Initialize Employees(Opta-planner fact)
        List<Employee> employeeList = getAllEmployee(fromPlanningDate, toPlanningDate, staffWithSkillsAndUnitPostionIds, unitPositionIds);

        //Prepare Data
        List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList = staffingLevelService.getShiftPlanningStaffingLevelDTOByUnitId(unitId, fromPlanningDate, toPlanningDate);
        Map<java.time.LocalDate, List<StaffingLevelTimeSlotDTO>> localDateStaffingLevelTimeSlotMap = staffingLevelService.getStaffingLevelTimeSlotByDate(shiftPlanningStaffingLevelDTOList);
        Map<java.time.LocalDate, Set<StaffingLevelActivity>> localDateStaffingLevelActivityMap = staffingLevelService.getStaffingLevelActivityByDay(localDateStaffingLevelTimeSlotMap);

        //2.)Initialize activities
        List<Activity> activityList = getActivities(localDateStaffingLevelActivityMap);

        //3.)Initialize ActivityLineInterval and activitiesPerDay
        Object[] activityLineIntervalsAndActivitiesPerDay = getActivityLineIntervalsAndActivitiesPerDay(activityList, localDateStaffingLevelTimeSlotMap);
        //(Opta-planner Planning Variable)
        List<ActivityLineInterval> activityLineIntervalList = (List<ActivityLineInterval>) activityLineIntervalsAndActivitiesPerDay[0];
        Map<LocalDate, List<Activity>> activitiesPerDay = (Map<LocalDate, List<Activity>>) activityLineIntervalsAndActivitiesPerDay[1];

        //4.)Initialize shifts(Opta-planner PlanningEntity(as fact))
        List<ShiftRequestPhase> shiftRequestPhase = getShiftRequestPhase(unitPositionIds, fromPlanningDate, toPlanningDate, employeeList);

    }


    /***********************************************Employee List Initialization********************************************************************/
    /**
     * This method is used to get
     * All Staff/Employee with in {@param unitId}
     * between
     * @param fromPlanningDate
     * @param toPlanningDate
     * Must have CTA associated with unitPositionId within planning range else Staff will be skipped in planning
     * Must have WTA associated with unitPositionId within planning range else Staff will be skipped in planning //TODO attach already created logic
     * @return
     */
    public List<Employee> getAllEmployee(Date fromPlanningDate, Date toPlanningDate, List<StaffQueryResult> staffWithSkillsAndUnitPostionIds, List<Long> unitPositionIds) {
        List<Employee> employeeList = new ArrayList<>();
        if (staffWithSkillsAndUnitPostionIds.size() > 0) {
            //Prepare CTA & WTA data
            Map<Long, Map<java.time.LocalDate, CTAResponseDTO>> unitPositionIdWithLocalDateCTAMap = ctaService.getunitPositionIdWithLocalDateCTAMap(unitPositionIds, fromPlanningDate, toPlanningDate);
            Map<Long, Map<java.time.LocalDate,WorkingTimeAgreement>> dateWTAMap=wtaService.getunitPositionIdWithLocalDateWTAMap(unitPositionIds, fromPlanningDate, toPlanningDate);

            //Initialize Employee
            for (StaffQueryResult staffQueryResult : staffWithSkillsAndUnitPostionIds) {
                if (staffQueryResult.getStaffUnitPosition() != null && unitPositionIdWithLocalDateCTAMap.containsKey(staffQueryResult.getStaffUnitPosition())) {
                    Employee employee = new Employee();
                    employee.setId(staffQueryResult.getStaffId().toString());
                    employee.setName(staffQueryResult.getStaffName());
                    employee.setUnitPositionId(staffQueryResult.getStaffUnitPosition());
                    employee.setLocalDateCTAResponseDTOMap(ctaService.getLocalDateCTAMapByunitPositionId(unitPositionIdWithLocalDateCTAMap, staffQueryResult.getStaffUnitPosition()));
                    employee.setSkillSet(skillService.setSkillsOfEmployee(staffQueryResult.getStaffSkills()));
                    employee.setLocalDateWTAMap(wtaService.getLocalDateWTAMapByunitPositionId(dateWTAMap,staffQueryResult.getStaffUnitPosition()));//TODO
                    employeeList.add(employee);
                }
            }
        }
        return employeeList;
    }



/************************************ActivityList initialization****************************************/
    /**
     * @param localDateStaffingLevelActivityMap
     * @return
     */
    public List<Activity> getActivities(Map<java.time.LocalDate, Set<StaffingLevelActivity>> localDateStaffingLevelActivityMap) {
        Set<String> activityIds = new HashSet<>();
        for (java.time.LocalDate localDate : localDateStaffingLevelActivityMap.keySet()) {
            for (StaffingLevelActivity staffingLevelActivity : localDateStaffingLevelActivityMap.get(localDate)) {
                activityIds.add(staffingLevelActivity.getActivityId().toString());
            }

        }
        return activityMongoService.getConvertedActivityList(activityMongoService.getActivities(activityIds));
    }
    /***************************************************************************************************************************************/
    /**This method creates all ActivityLineIntervals based on Demand(StaffingLevel)and DatewiseActivity Map
     * @param
     * @param activityList
     * @param localDateStaffingLevelTimeSlotMap
     * @return
     */
    public Object[] getActivityLineIntervalsAndActivitiesPerDay(List<Activity> activityList, Map<java.time.LocalDate, List<StaffingLevelTimeSlotDTO>> localDateStaffingLevelTimeSlotMap) {
        Object[] activityLineIntervalsAndActivitiesPerDay = new Object[2];
        List<ActivityLineInterval> activityLineIntervalList = new ArrayList<>();
        Map<LocalDate, List<Activity>> activitiesPerDay = new HashMap<>();
        Map<String, Activity> activityIdActivityMap = activityList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        for (Map.Entry<java.time.LocalDate, List<StaffingLevelTimeSlotDTO>> localDateListEntry : localDateStaffingLevelTimeSlotMap.entrySet()) {
            LocalDate jodaLocalDate = DateUtils.asJodaLocalDate(DateUtils.asDate(localDateListEntry.getKey()));
            List<Activity> activityListPerDay = new ArrayList<>();
            for (StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO : localDateListEntry.getValue()) {
                //Create ALI only if there exist at least 1 StaffingLevelActivity for current[TimeSlot/Interval]
                if(!staffingLevelTimeSlotDTO.getStaffingLevelActivities().isEmpty()) {
                    Duration duration = staffingLevelTimeSlotDTO.getStaffingLevelDuration();
                    for (StaffingLevelActivity staffingLevelActivity : staffingLevelTimeSlotDTO.getStaffingLevelActivities()) {
                        //Prepare DateWise Required/Demanding activities for optaplanner
                        activityListPerDay.add(activityIdActivityMap.get(staffingLevelActivity.getActivityId()));
                        //Create ALI's for all [activity Types]
                        for (int i = 0; i < staffingLevelActivity.getMaxNoOfStaff(); i++) {
                            //Create same ALI till - Max demand for particular [Interval/TimeSlot]
                            ActivityLineInterval activityLineInterval = new ActivityLineInterval();
                            BigInteger activityId=staffingLevelActivity.getActivityId();
                            activityLineInterval.setActivity(activityIdActivityMap.get(activityId.toString()));
                            activityLineInterval.setStart(new DateTime(DateUtils.getDateByLocalDateAndLocalTime(localDateListEntry.getKey(), duration.getFrom())));
                            activityLineInterval.setDuration(Math.abs(duration.getFrom().get(ChronoField.MINUTE_OF_DAY) - duration.getTo().get(ChronoField.MINUTE_OF_DAY)));
                            if (i < staffingLevelActivity.getMinNoOfStaff()) {
                                activityLineInterval.setRequired(true);
                            }
                            activityLineIntervalList.add(activityLineInterval);
                        }
                    }
                }
            }
           if(activityListPerDay.size()>0) activitiesPerDay.put(jodaLocalDate, activityListPerDay);
        }
        activityLineIntervalsAndActivitiesPerDay[0] = activityLineIntervalList;
        activityLineIntervalsAndActivitiesPerDay[1] = activitiesPerDay;
        return activityLineIntervalsAndActivitiesPerDay;
    }
    /****************************Shift Initialization**********************************************************************************/
    /**
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @param employeeList
     * @return
     */
    public List<ShiftRequestPhase> getShiftRequestPhase(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate, List<Employee> employeeList) {
        List<Shift> shifts = activityMongoService.getAllShiftsByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        List<ShiftRequestPhase> shiftRequestPhaseList = new ArrayList<>();
        if (shifts.size() > 0) {
            Map<Long, Employee> unitPositionEmployeeMap = employeeList.stream().collect(Collectors.toMap(unitPositionId -> unitPositionId.getUnitPositionId(), emp -> emp));
            //Initialize Shifts
            for (Shift shift : shifts) {
                if (unitPositionEmployeeMap.containsKey(shift.getUnitPositionId())) {
                    ShiftRequestPhase shiftRequestPhase = new ShiftRequestPhase();
                    shiftRequestPhase.setStartDate(shift.getStartLocalDate());
                    shiftRequestPhase.setEndDate(shift.getEndLocalDate());
                    shiftRequestPhase.setEmployee(unitPositionEmployeeMap.get(shift.getUnitPositionId()));
                    shiftRequestPhaseList.add(shiftRequestPhase);
                }
            }
        }
        return shiftRequestPhaseList;
    }
}
