package com.planner.service.shift_planning;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.query_results.staff.StaffQueryResult;
import com.planner.domain.shift_planning.Shift;
import com.planner.service.config.PathProvider;
import com.planner.service.planning_problem.PlanningProblemService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.getActivityIndex;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.getTimeIndex;

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
    @Inject
    private PathProvider pathProvider;
    @Inject
    private PlanningProblemService planningProblemService;

    /**
     * ShiftRequestPhasePlanningSolution(Opta-planner planning Solution)
     */
    public ShiftRequestPhasePlanningSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {

        Long unitId = shiftPlanningProblemSubmitDTO.getUnitId();
        Date fromPlanningDate;
        Date toPlanningDate;
        List<Long> staffIds = shiftPlanningProblemSubmitDTO.getStaffIds();
        BigInteger planningPeriodId = shiftPlanningProblemSubmitDTO.getPlanningPeriodId();
        if (planningPeriodId != null) {
            com.kairos.dto.planner.planninginfo.PlanningProblemDTO planningPeriodDTO = activityMongoService.getPlanningPeriod(planningPeriodId,unitId);
            fromPlanningDate = asDate(planningPeriodDTO.getStartDate());
            toPlanningDate = asDate(planningPeriodDTO.getEndDate());

        } else {
            fromPlanningDate = asDate(shiftPlanningProblemSubmitDTO.getStartDate());
            toPlanningDate = asDate(shiftPlanningProblemSubmitDTO.getEndDate());
        }


        List<StaffQueryResult> staffWithSkillsAndEmploymentIds = userNeo4jService.getStaffWithSkillsAndEmploymentIds(unitId, staffIds);
        List<Long> employmentIds = new LinkedList<>();
        for(StaffQueryResult staffQueryResult:staffWithSkillsAndEmploymentIds){
            employmentIds.addAll(staffQueryResult.getEmploymentIds());
        }

        List<Employee> employeeList = getAllEmployee(fromPlanningDate, toPlanningDate, staffWithSkillsAndEmploymentIds, employmentIds);
       //
        List<ShiftPlanningStaffingLevelDTO> shiftPlanningStaffingLevelDTOList = staffingLevelService.getShiftPlanningStaffingLevelDTOByUnitId(unitId, fromPlanningDate, toPlanningDate);
        Map<java.time.LocalDate, List<StaffingLevelInterval>> localDateStaffingLevelTimeSlotMap = staffingLevelService.getStaffingLevelTimeSlotByDate(shiftPlanningStaffingLevelDTOList);
        Map<java.time.LocalDate, Set<StaffingLevelActivity>> localDateStaffingLevelActivityMap = staffingLevelService.getStaffingLevelActivityByDay(localDateStaffingLevelTimeSlotMap);
       //
        List<Activity> activityList = getActivities(localDateStaffingLevelActivityMap);
        Object[] activityLineIntervalsAndActivitiesPerDay = getActivityLineIntervalsAndActivitiesPerDay(activityList, localDateStaffingLevelTimeSlotMap);
        List<ActivityLineInterval> activityLineIntervalList = (List<ActivityLineInterval>) activityLineIntervalsAndActivitiesPerDay[0];
        //
        Map<LocalDate, Set<Activity>> activitiesPerDay = (Map<LocalDate, Set<Activity>>) activityLineIntervalsAndActivitiesPerDay[1];
        Map<LocalDate, List<Activity>> activitiesPerDayList = new HashMap<>();
        for (LocalDate localDate : activitiesPerDay.keySet()) {
            List<Activity> activities = new ArrayList<>();
            activities.addAll(activitiesPerDay.get(localDate));
            activitiesPerDayList.put(localDate, activities);
        }
        Map<java.time.LocalDate, List<ActivityLineInterval>> dateWiseALIsList = (Map<java.time.LocalDate, List<ActivityLineInterval>>) activityLineIntervalsAndActivitiesPerDay[2];
        Map<String, List<ActivityLineInterval>> activityLineIntervalPerDayList = new HashMap<>();
        for (java.time.LocalDate localDate : dateWiseALIsList.keySet()) {
            List<ActivityLineInterval> activityLineInterval = new ArrayList<>();
            activityLineInterval.addAll(dateWiseALIsList.get(localDate));
            activityLineIntervalPerDayList.put(localDate.toString(), activityLineInterval);
        }
        //

        List<LocalDate> weekDates=new ArrayList<>();
        weekDates.addAll(activitiesPerDayList.keySet());

        List<ShiftImp> shiftImp = getShiftRequestPhase(employmentIds, fromPlanningDate, toPlanningDate, employeeList);
        Map<LocalDate,Object[]> staffingLevelMatrix=createStaffingLevelMatrix(activityLineIntervalList,15,activitiesPerDayList);
        int[] activitiesRank=activityList.stream().mapToInt(a->a.getRank()).toArray();
        //
        ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution = new ShiftRequestPhasePlanningSolution();
        shiftRequestPhasePlanningSolution.setEmployees(employeeList);
        shiftRequestPhasePlanningSolution.setShifts(shiftImp);
        shiftRequestPhasePlanningSolution.setActivities(activityList);
        shiftRequestPhasePlanningSolution.setActivityLineIntervals(activityLineIntervalList);
        shiftRequestPhasePlanningSolution.setActivitiesIntervalsGroupedPerDay(activityLineIntervalPerDayList);
        //shiftRequestPhasePlanningSolution.setActivitiesPerDay(activitiesPerDayList);
        shiftRequestPhasePlanningSolution.setUnitId(unitId);
        //shiftRequestPhasePlanningSolution.setStaffingLevelMatrix(new StaffingLevelMatrix(staffingLevelMatrix,activitiesRank));
        //shiftRequestPhasePlanningSolution.setWeekDates(weekDates);
        shiftRequestPhasePlanningSolution.setSkillLineIntervals(new ArrayList<>());//Temporary
        BigInteger problemId = planningProblemService.addProblemFileAndGetPlanningProblemID(shiftPlanningProblemSubmitDTO,fromPlanningDate,toPlanningDate,shiftRequestPhasePlanningSolution);
        ShiftRequestPhasePlanningSolution planningSolution=new ShiftPlanningSolver(getSolverConfigDTO()).solveProblem(shiftRequestPhasePlanningSolution);
        planningProblemService.addSolutionFile(planningSolution,problemId);
        return planningSolution;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO("Shortest duration for this activity, relative to shift length", "Shortest duration for this activity, relative to shift length", ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO("Max number of allocations pr. shift for this activity per staff", "Max number of allocations pr. shift for this activity per staff", ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5, 5l));
        return new SolverConfigDTO(constraintDTOS);
    }

    /***********************************************Employee List Initialization********************************************************************/
    /**
     * This method is used to get
     * All Staff/Employee with in {@param unitId}
     * between
     *
     * @param fromPlanningDate
     * @param toPlanningDate   Must have CTA associated with employmentId within planning range else Staff will be skipped in planning
     *                         Must have WTA associated with employmentId within planning range else Staff will be skipped in planning
     * @return
     */
    public List<Employee> getAllEmployee(Date fromPlanningDate, Date toPlanningDate, List<StaffQueryResult> staffWithSkillsAndEmploymentIds, List<Long> employmentIds) {
        List<Employee> employeeList = new ArrayList<>();
        if (staffWithSkillsAndEmploymentIds.size() > 0) {
            //Prepare CTA & WTA data
            Map<Long, Map<java.time.LocalDate, CTAResponseDTO>> employmentIdWithLocalDateCTAMap = ctaService.getEmploymentIdWithLocalDateCTAMap(employmentIds, fromPlanningDate, toPlanningDate);
            //Map<Long, Map<java.time.LocalDate, WorkingTimeAgreement>> dateWTAMap = wtaService.getEmploymentIdWithLocalDateWTAMap(employmentIds, fromPlanningDate, toPlanningDate);

            //Initialize Employee
            for (StaffQueryResult staffQueryResult : staffWithSkillsAndEmploymentIds) {
                for(Long employmentId : staffQueryResult.getEmploymentIds()) {
                    if (employmentId != null && employmentIdWithLocalDateCTAMap.containsKey(employmentId)) {
                        Employee employee = new Employee();
                        employee.setId(staffQueryResult.getStaffId());
                        employee.setName(staffQueryResult.getStaffName());
                        employee.setSkillSet(skillService.setSkillsOfEmployee(staffQueryResult.getStaffSkills()));
                        employeeList.add(employee);
                    }
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
    /**
     * This method creates all ActivityLineIntervals based on Demand(StaffingLevel)and DatewiseActivity Map
     *
     * @param
     * @param activityList
     * @param localDateStaffingLevelTimeSlotMap
     * @return
     */
    public Object[] getActivityLineIntervalsAndActivitiesPerDay(List<Activity> activityList, Map<java.time.LocalDate, List<StaffingLevelInterval>> localDateStaffingLevelTimeSlotMap) {
        Object[] activityLineIntervalsAndActivitiesPerDay = new Object[3];
        List<ActivityLineInterval> activityLineIntervalList = new ArrayList<>();//arr[0]
        Map<LocalDate, Set<Activity>> activitiesPerDay = new HashMap<>();//arr[1]
        Map<java.time.LocalDate, List<ActivityLineInterval>> dateWiseALIList = new HashMap<>();//arr[2]
        Map<BigInteger, Activity> activityIdActivityMap = activityList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        for (Map.Entry<java.time.LocalDate, List<StaffingLevelInterval>> localDateListEntry : localDateStaffingLevelTimeSlotMap.entrySet()) {
            LocalDate jodaLocalDate = DateUtils.asJodaLocalDate(asDate(localDateListEntry.getKey()));
            Set<Activity> activityListPerDay = new HashSet<>();
            List<ActivityLineInterval> perDayALIList = new ArrayList<>();
            updateActivityInterval(activityIdActivityMap, localDateListEntry, activityListPerDay, perDayALIList);
            if (!activityListPerDay.isEmpty()) activitiesPerDay.put(jodaLocalDate, activityListPerDay);
            if (!perDayALIList.isEmpty()) {
                dateWiseALIList.put(localDateListEntry.getKey(), perDayALIList);
                activityLineIntervalList.addAll(perDayALIList);
            }
        }
        activityLineIntervalsAndActivitiesPerDay[0] = activityLineIntervalList;
        activityLineIntervalsAndActivitiesPerDay[1] = activitiesPerDay;
        activityLineIntervalsAndActivitiesPerDay[2] = dateWiseALIList;
        return activityLineIntervalsAndActivitiesPerDay;
    }

    private void updateActivityInterval(Map<BigInteger, Activity> activityIdActivityMap, Map.Entry<java.time.LocalDate, List<StaffingLevelInterval>> localDateListEntry, Set<Activity> activityListPerDay, List<ActivityLineInterval> perDayALIList) {
        for (StaffingLevelInterval staffingLevelInterval : localDateListEntry.getValue()) {
            //Create ALI only if there exist at least 1 StaffingLevelActivity for current[TimeSlot/Interval]
            if (!staffingLevelInterval.getStaffingLevelActivities().isEmpty()) {
                Duration duration = staffingLevelInterval.getStaffingLevelDuration();
                for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                    //Prepare DateWise Required/Demanding activities for optaplanner
                    getInterval(activityIdActivityMap, localDateListEntry, activityListPerDay, perDayALIList, duration, staffingLevelActivity);
                }
            }
        }
    }

    private void getInterval(Map<BigInteger, Activity> activityIdActivityMap, Map.Entry<java.time.LocalDate, List<StaffingLevelInterval>> localDateListEntry, Set<Activity> activityListPerDay, List<ActivityLineInterval> perDayALIList, Duration duration, StaffingLevelActivity staffingLevelActivity) {
        if (activityIdActivityMap.containsKey(staffingLevelActivity.getActivityId().toString())) {
            activityListPerDay.add(activityIdActivityMap.get(staffingLevelActivity.getActivityId().toString()));
            //Create ALI's for all [activity Types]
            for (int i = 0; i < staffingLevelActivity.getMaxNoOfStaff(); i++) {
                //Create same ALI till - Max demand for particular [Interval/TimeSlot]
                ActivityLineInterval activityLineInterval = new ActivityLineInterval();
                BigInteger activityId = staffingLevelActivity.getActivityId();
                activityLineInterval.setActivity(activityIdActivityMap.get(activityId));
                //activityLineInterval.setStart(new DateTime(DateUtils.getDateByLocalDateAndLocalTime(localDateListEntry.getKey(), duration.getFrom())));
                activityLineInterval.setDuration(Math.abs(duration.getFrom().get(ChronoField.MINUTE_OF_DAY) - duration.getTo().get(ChronoField.MINUTE_OF_DAY)));
                if (i < staffingLevelActivity.getMinNoOfStaff()) {
                    activityLineInterval.setRequired(true);
                }
                perDayALIList.add(activityLineInterval);
            }
        }
    }
    /****************************Shift Initialization**********************************************************************************/
    /**
     * @param EmploymentIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @param employeeList
     * @return
     */
    public List<ShiftImp> getShiftRequestPhase(List<Long> EmploymentIds, Date fromPlanningDate, Date toPlanningDate, List<Employee> employeeList) {
        List<Shift> shifts = activityMongoService.getAllShiftsByEmploymentIds(EmploymentIds, fromPlanningDate, toPlanningDate);
        List<ShiftImp> shiftImpList = new ArrayList<>();
        if (shifts.size() > 0) {
            Map<Long, Employee> employmentEmployeeMap = new HashMap<>();//employeeList.stream().collect(Collectors.toMap(employmentId -> employmentId.getEmploymentId(), emp -> emp));
            //Initialize Shifts
            for (Shift shift : shifts) {
                if (employmentEmployeeMap.containsKey(shift.getEmploymentId())) {
                    ShiftImp shiftImp = new ShiftImp();
                    shiftImp.setStartDate(DateUtils.asLocalDate(shift.getStartDate()));
                    shiftImp.setEndDate(DateUtils.asLocalDate(shift.getEndDate()));
                    /*shiftImp.setId(UUID.randomUUID());//Temporary
                    shiftImp.setDate(DateUtils.asJodaLocalDate(shift.getStartDate()));
                    *///Set Appropriate Staff/Employee
                    shiftImp.setEmployee(employmentEmployeeMap.get(shift.getEmploymentId()));
                    //Set Matched ALI/s on the basis of its [Interval/Duration]
                    shiftImpList.add(shiftImp);
                }
            }
        }
        return shiftImpList;
    }

    public static Map<LocalDate, Object[]> createStaffingLevelMatrix( List<ActivityLineInterval> alis, int granularity, Map<LocalDate, List<Activity>> activitiesPerDayList){
        Map<LocalDate, Object[]> slMatrix=new HashMap<>();
        Set<LocalDate> localDates=activitiesPerDayList.keySet();
            for (LocalDate localDate : localDates) {
                List<Activity> activities=activitiesPerDayList.get(localDate);
                if(CollectionUtils.isNotEmpty(activities)) {
                    slMatrix.put(localDate, new int[1440 / granularity][activities.size() * 2]);
                }
            }
            for (ActivityLineInterval ali : alis) {
                if (ali.getActivity().isBlankActivity()) continue;
                if (ali.getActivity().isTypeAbsence()) {
                    IntStream.rangeClosed(0, 1440 / granularity - 1).forEach(i ->
                        ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[i][getActivityIndex(ali)]++
                    );
                } else {
                    ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[getTimeIndex(ali.getStart(), granularity)][getActivityIndex(ali)]++;
                }
            }
            //printStaffingLevelMatrix(slMatrix, null);
        return slMatrix;
    }
}
