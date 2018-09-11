package com.planner.service.shift_planning;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.staffing_level.Duration;
import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.shiftplanning.domain.Activity;
import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.Employee;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import com.planner.domain.query_results.StaffQueryResult;
import com.planner.domain.shift_planning.Shift;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
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


    /**
     * ShiftRequestPhasePlanningSolution
     */
    public void initializeShiftPlanning(Long unitId, Date fromPlanningDate, Date toPlanningDate, Long[] staffIds) {
        //Prepare Data
        List<StaffQueryResult> staffWithSkillsAndUnitPostionIds = userNeo4jService.getStaffWithSkillsAndUnitPostionIds(unitId, staffIds);
        List<Long> unitPositionIds = staffWithSkillsAndUnitPostionIds.stream().map(s -> s.getStaffUnitPosition()).collect(Collectors.toList());

        //1.) Initialize Employees
        List<Employee> employeeList = getAllEmployee(staffIds, fromPlanningDate, toPlanningDate, staffWithSkillsAndUnitPostionIds, unitPositionIds);

        //2.)Initialize shifts
        List<ShiftRequestPhase> shiftRequestPhase = getShiftRequestPhase(unitPositionIds, fromPlanningDate, toPlanningDate, employeeList);

        //Prepare Data
        List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList = staffingLevelService.getShiftPlanningStaffingLevelDTOByUnitId(unitId, fromPlanningDate, toPlanningDate);
        Map<java.time.LocalDate, List<StaffingLevelTimeSlotDTO>> localDateStaffingLevelTimeSlotMap = staffingLevelService.getStaffingLevelTimeSlotByDate(shiftPlanningStaffingLevelDTOList);
        Map<java.time.LocalDate, Set<StaffingLevelActivity>> localDateStaffingLevelActivityMap = staffingLevelService.getStaffingLevelActivityByDay(localDateStaffingLevelTimeSlotMap);

        //3.)Initialize activities
        List<Activity> activityList = getActivities(localDateStaffingLevelActivityMap);

        //4.)Initialize ActivityLineInterval and activitiesPerDay
        Object[] activityLineIntervalsAndActivitiesPerDay = getActivityLineIntervalsAndActivitiesPerDay(activityList, localDateStaffingLevelTimeSlotMap, localDateStaffingLevelActivityMap);
        List<ActivityLineInterval> activityLineIntervalList = (List<ActivityLineInterval>) activityLineIntervalsAndActivitiesPerDay[0];
        Map<LocalDate, List<Activity>> activitiesPerDay = (Map<LocalDate, List<Activity>>) activityLineIntervalsAndActivitiesPerDay[1];
    }


    /***********************************************Employee List Initialization********************************************************************/
    /**
     * This method is used to get
     * All Staff/Employee with in {@param unitId}
     *
     * @param staffIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public List<Employee> getAllEmployee(Long[] staffIds, Date fromPlanningDate, Date toPlanningDate, List<StaffQueryResult> staffWithSkillsAndUnitPostionIds, List<Long> unitPositionIds) {
        List<Employee> employeeList = new ArrayList<>();
        //Prepare CTA data
        Map<Long, Map<java.time.LocalDate, CTAResponseDTO>> unitPositionIdWithLocalDateCTAMap = ctaService.getunitPositionIdWithLocalDateCTAMap(unitPositionIds, fromPlanningDate, toPlanningDate);

        //Initialize Employee
        for (StaffQueryResult staffQueryResult : staffWithSkillsAndUnitPostionIds) {
            Employee employee = new Employee();
            employee.setId(staffQueryResult.getStaffId().toString());
            employee.setName(staffQueryResult.getStaffName());
            employee.setUnitPositionId(staffQueryResult.getStaffUnitPosition());
            employee.setLocalDateCTAResponseDTOMap(ctaService.getLocalDateCTAMapByunitPositionId(unitPositionIdWithLocalDateCTAMap, staffQueryResult.getStaffUnitPosition()));
            employee.setSkillSet(skillService.setSkillsOfEmployee(staffQueryResult.getStaffSkills()));
            //employee.setLocalDateWTAMap();
            employeeList.add(employee);
        }
        return employeeList;
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
        Map<Long, Employee> unitPositionEmployeeMap = employeeList.stream().collect(Collectors.toMap(unitPositionId -> unitPositionId.getUnitPositionId(), emp -> emp));
        List<Shift> shifts = activityMongoService.getAllShiftsByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        List<ShiftRequestPhase> shiftRequestPhaseList = new ArrayList<>();
        //Initiaize Shifts
        for (Shift shift : shifts) {
            if (unitPositionEmployeeMap.containsKey(shift.getUnitPositionId())) {
                ShiftRequestPhase shiftRequestPhase = new ShiftRequestPhase();
                shiftRequestPhase.setStartDate(shift.getStartLocalDate());
                shiftRequestPhase.setEndDate(shift.getEndLocalDate());
                shiftRequestPhase.setEmployee(unitPositionEmployeeMap.get(shift.getUnitPositionId()));
                shiftRequestPhaseList.add(shiftRequestPhase);
            }
        }
        return shiftRequestPhaseList;
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

        return ObjectMapperUtils.copyProperties(activityMongoService.getActivities(activityIds), Activity.class);
    }
/****************************************************************************/
    /**
     * @param
     * @param activityList
     * @param localDateStaffingLevelTimeSlotMap
     * @param localDateStaffingLevelActivityMap
     * @return
     */
    public Object[] getActivityLineIntervalsAndActivitiesPerDay(List<Activity> activityList, Map<java.time.LocalDate, List<StaffingLevelTimeSlotDTO>> localDateStaffingLevelTimeSlotMap, Map<java.time.LocalDate, Set<StaffingLevelActivity>> localDateStaffingLevelActivityMap) {
        Object[] activityLineIntervalsAndActivitiesPerDay = new Object[2];
        List<ActivityLineInterval> activityLineIntervalList = new ArrayList<>();
        Map<LocalDate, List<Activity>> activitiesPerDay = new HashMap<>();
        Map<String, Activity> activityIdActivityMap = activityList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));

        for (Map.Entry<java.time.LocalDate, List<StaffingLevelTimeSlotDTO>> localDateListEntry : localDateStaffingLevelTimeSlotMap.entrySet()) {
            LocalDate jodaLocalDate = DateUtils.asJodaLocalDate(DateUtils.asDate(localDateListEntry.getKey()));
            List<Activity> activityListPerDay = new ArrayList<>();
            for (StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO : localDateListEntry.getValue()) {
                Duration duration = staffingLevelTimeSlotDTO.getStaffingLevelDuration();
                for (StaffingLevelActivity staffingLevelActivity : localDateStaffingLevelActivityMap.get(localDateListEntry.getKey())) {
                    for (int i = 0; i < staffingLevelActivity.getMaxNoOfStaff(); i++) {
                        activityListPerDay.add(activityIdActivityMap.get(staffingLevelActivity.getActivityId().toString()));
                        ActivityLineInterval activityLineInterval = new ActivityLineInterval();
                        activityLineInterval.setActivity(activityIdActivityMap.get(staffingLevelActivity.getActivityId().toString()));
                        activityLineInterval.setStart(new DateTime(DateUtils.getDateByLocalDateAndLocalTime(localDateListEntry.getKey(), duration.getFrom())));
                        activityLineInterval.setDuration(duration.getFrom().get(ChronoField.MINUTE_OF_DAY) - duration.getTo().get(ChronoField.MINUTE_OF_DAY));
                        if (i < staffingLevelActivity.getMinNoOfStaff()) activityLineInterval.setRequired(true);
                        activityLineIntervalList.add(activityLineInterval);
                    }
                }


            }

            activitiesPerDay.put(jodaLocalDate, activityListPerDay);
        }
        activityLineIntervalsAndActivitiesPerDay[0] = activityLineIntervalList;
        activityLineIntervalsAndActivitiesPerDay[1] = activitiesPerDay;
        return activityLineIntervalsAndActivitiesPerDay;
    }
}
