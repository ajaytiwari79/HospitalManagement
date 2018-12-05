package com.planner.service.shift_planning;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.staffing_level.Duration;

import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;

import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintLevel;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.shiftplanning.domain.*;
import com.kairos.shiftplanning.domain.wta.updated_wta.WorkingTimeAgreement;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.query_results.staff.StaffQueryResult;
import com.planner.domain.shift_planning.Shift;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.getActivityIndex;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.getTimeIndex;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.printStaffingLevelMatrix;

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
    public ShiftRequestPhasePlanningSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        Long unitId = shiftPlanningProblemSubmitDTO.getUnitId();
        Date fromPlanningDate;
        Date toPlanningDate;
        List staffIds = shiftPlanningProblemSubmitDTO.getStaffIds();
        BigInteger planningPeriodId = shiftPlanningProblemSubmitDTO.getPlanningPeriodId();
        if (planningPeriodId != null) {
            com.kairos.dto.planner.planninginfo.PlanningProblemDTO planningPeriodDTO = activityMongoService.getPlanningPeriod(planningPeriodId,unitId);
            fromPlanningDate = DateUtils.asDate(planningPeriodDTO.getStartDate());
            toPlanningDate = DateUtils.asDate(planningPeriodDTO.getEndDate());
        } else {
            fromPlanningDate = DateUtils.asDate(shiftPlanningProblemSubmitDTO.getStartDate());
            toPlanningDate = DateUtils.asDate(shiftPlanningProblemSubmitDTO.getEndDate());
        }

        List<StaffQueryResult> staffWithSkillsAndUnitPostionIds = userNeo4jService.getStaffWithSkillsAndUnitPostionIds(unitId, staffIds);
        List<Long> unitPositionIds = staffWithSkillsAndUnitPostionIds.stream().map(s -> s.getStaffUnitPosition()).collect(Collectors.toList());
       //
        List<Employee> employeeList = getAllEmployee(fromPlanningDate, toPlanningDate, staffWithSkillsAndUnitPostionIds, unitPositionIds);
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

        List<ShiftRequestPhase> shiftRequestPhase = getShiftRequestPhase(unitPositionIds, fromPlanningDate, toPlanningDate, employeeList, dateWiseALIsList);
        Map<LocalDate,Object[]> staffingLevelMatrix=createStaffingLevelMatrix(activityLineIntervalList,15,activitiesPerDayList);
        int[] activitiesRank=activityList.stream().mapToInt(a->a.getRank()).toArray();
        //
        ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution = new ShiftRequestPhasePlanningSolution();//new ShiftPlanningSolver(getSolverConfigDTO()).solveProblem(problem);
        shiftRequestPhasePlanningSolution.setEmployees(employeeList);
        shiftRequestPhasePlanningSolution.setShifts(shiftRequestPhase);
        shiftRequestPhasePlanningSolution.setActivities(activityList);
        shiftRequestPhasePlanningSolution.setActivityLineIntervals(activityLineIntervalList);
        shiftRequestPhasePlanningSolution.setActivitiesIntervalsGroupedPerDay(activityLineIntervalPerDayList);
        shiftRequestPhasePlanningSolution.setActivitiesPerDay(activitiesPerDayList);
        shiftRequestPhasePlanningSolution.setUnitId(unitId);
        shiftRequestPhasePlanningSolution.setStaffingLevelMatrix(new StaffingLevelMatrix(staffingLevelMatrix,activitiesRank));
        shiftRequestPhasePlanningSolution.setWeekDates(weekDates);
        shiftRequestPhasePlanningSolution.setSkillLineIntervals(new ArrayList<>());//Temporary
        ShiftRequestPhasePlanningSolution planningSolution=new ShiftPlanningSolver(getSolverConfigDTO()).solveProblem(shiftRequestPhasePlanningSolution);

        return planningSolution;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        /*constraintDTOS.add(new ConstraintDTO(null, DURATION_BETWEEN_SHIFTS.toString(), commonDescription+"ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS", ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ConstraintLevel.HARD, penaltyHard, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));*/
        constraintDTOS.add(new ConstraintDTO("Shortest duration for this activity, relative to shift length", "Shortest duration for this activity, relative to shift length", ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ConstraintLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO("Max number of allocations pr. shift for this activity per staff", "Max number of allocations pr. shift for this activity per staff", ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ConstraintLevel.HARD, 5, 5l));
        return new SolverConfigDTO(constraintDTOS);
    }

    /***********************************************Employee List Initialization********************************************************************/
    /**
     * This method is used to get
     * All Staff/Employee with in {@param unitId}
     * between
     *
     * @param fromPlanningDate
     * @param toPlanningDate   Must have CTA associated with unitPositionId within planning range else Staff will be skipped in planning
     *                         Must have WTA associated with unitPositionId within planning range else Staff will be skipped in planning
     * @return
     */
    public List<Employee> getAllEmployee(Date fromPlanningDate, Date toPlanningDate, List<StaffQueryResult> staffWithSkillsAndUnitPostionIds, List<Long> unitPositionIds) {
        List<Employee> employeeList = new ArrayList<>();
        if (staffWithSkillsAndUnitPostionIds.size() > 0) {
            //Prepare CTA & WTA data
            Map<Long, Map<java.time.LocalDate, CTAResponseDTO>> unitPositionIdWithLocalDateCTAMap = ctaService.getunitPositionIdWithLocalDateCTAMap(unitPositionIds, fromPlanningDate, toPlanningDate);
            Map<Long, Map<java.time.LocalDate, WorkingTimeAgreement>> dateWTAMap = wtaService.getunitPositionIdWithLocalDateWTAMap(unitPositionIds, fromPlanningDate, toPlanningDate);

            //Initialize Employee
            for (StaffQueryResult staffQueryResult : staffWithSkillsAndUnitPostionIds) {
                if (staffQueryResult.getStaffUnitPosition() != null && unitPositionIdWithLocalDateCTAMap.containsKey(staffQueryResult.getStaffUnitPosition())) {
                    Employee employee = new Employee();
                    employee.setId(staffQueryResult.getStaffId().toString());
                    employee.setName(staffQueryResult.getStaffName());
                    employee.setUnitPositionId(staffQueryResult.getStaffUnitPosition());
                    employee.setLocalDateCTAResponseDTOMap(ctaService.getLocalDateCTAMapByunitPositionId(unitPositionIdWithLocalDateCTAMap, staffQueryResult.getStaffUnitPosition()));
                    employee.setSkillSet(skillService.setSkillsOfEmployee(staffQueryResult.getStaffSkills()));
                    employee.setLocalDateWTAMap(wtaService.getLocalDateWTAMapByunitPositionId(dateWTAMap, staffQueryResult.getStaffUnitPosition()));//TODO
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
        Map<String, Activity> activityIdActivityMap = activityList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        for (Map.Entry<java.time.LocalDate, List<StaffingLevelInterval>> localDateListEntry : localDateStaffingLevelTimeSlotMap.entrySet()) {
            LocalDate jodaLocalDate = DateUtils.asJodaLocalDate(DateUtils.asDate(localDateListEntry.getKey()));
            Set<Activity> activityListPerDay = new HashSet<>();
            List<ActivityLineInterval> perDayALIList = new ArrayList<>();
            for (StaffingLevelInterval staffingLevelInterval : localDateListEntry.getValue()) {
                //Create ALI only if there exist at least 1 StaffingLevelActivity for current[TimeSlot/Interval]
                if (!staffingLevelInterval.getStaffingLevelActivities().isEmpty()) {
                    Duration duration = staffingLevelInterval.getStaffingLevelDuration();
                    for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                        //Prepare DateWise Required/Demanding activities for optaplanner
                        if (activityIdActivityMap.containsKey(staffingLevelActivity.getActivityId().toString())) {
                            activityListPerDay.add(activityIdActivityMap.get(staffingLevelActivity.getActivityId().toString()));
                            //Create ALI's for all [activity Types]
                            for (int i = 0; i < staffingLevelActivity.getMaxNoOfStaff(); i++) {
                                //Create same ALI till - Max demand for particular [Interval/TimeSlot]
                                ActivityLineInterval activityLineInterval = new ActivityLineInterval();
                                BigInteger activityId = staffingLevelActivity.getActivityId();
                                activityLineInterval.setActivity(activityIdActivityMap.get(activityId.toString()));
                                activityLineInterval.setStart(new DateTime(DateUtils.getDateByLocalDateAndLocalTime(localDateListEntry.getKey(), duration.getFrom())));
                                activityLineInterval.setDuration(Math.abs(duration.getFrom().get(ChronoField.MINUTE_OF_DAY) - duration.getTo().get(ChronoField.MINUTE_OF_DAY)));
                                if (i < staffingLevelActivity.getMinNoOfStaff()) {
                                    activityLineInterval.setRequired(true);
                                }
                                perDayALIList.add(activityLineInterval);
                            }
                        }
                    }
                }
            }
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
    /****************************Shift Initialization**********************************************************************************/
    /**
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @param employeeList
     * @param dateWiseALIsList
     * @return
     */
    public List<ShiftRequestPhase> getShiftRequestPhase(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate, List<Employee> employeeList, Map<java.time.LocalDate, List<ActivityLineInterval>> dateWiseALIsList) {
        List<Shift> shifts = activityMongoService.getAllShiftsByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        List<ShiftRequestPhase> shiftRequestPhaseList = new ArrayList<>();
        if (shifts.size() > 0) {
            Map<Long, Employee> unitPositionEmployeeMap = employeeList.stream().collect(Collectors.toMap(unitPositionId -> unitPositionId.getUnitPositionId(), emp -> emp));
            //Initialize Shifts
            for (Shift shift : shifts) {
                if (unitPositionEmployeeMap.containsKey(shift.getUnitPositionId())) {
                    ShiftRequestPhase shiftRequestPhase = new ShiftRequestPhase();
                    shiftRequestPhase.setStartDate(DateUtils.asLocalDate(shift.getStartDate()));
                    shiftRequestPhase.setEndDate(DateUtils.asLocalDate(shift.getEndDate()));
                    shiftRequestPhase.setId(UUID.randomUUID());//Temporary
                    shiftRequestPhase.setDate(DateUtils.asJodaLocalDate(shift.getStartDate()));//TODO check
                    //Set Appropriate Staff/Employee
                    shiftRequestPhase.setEmployee(unitPositionEmployeeMap.get(shift.getUnitPositionId()));
                    //Set Matched ALI/s on the basis of its [Interval/Duration]
                    //shiftRequestPhase.setActivityLineIntervals(getApplicableALIs(shift, dateWiseALIsList));
                    shiftRequestPhaseList.add(shiftRequestPhase);
                }
            }
        }
        return shiftRequestPhaseList;
    }

    private List<ActivityLineInterval> getShiftActivityLineInterval() {
        List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
        return activityLineIntervals;
    }


    //Fixme (DO NOT REMOVE)
    /**
     * This method is used to give initial solution without considering any
     * Constraint
     * Might be used in future for optimizations

     * @return
     */
    /*public List<ActivityLineInterval> getApplicableALIs(Shift shift, Map<java.time.LocalDate, List<ActivityLineInterval>> dateWiseALIsList) {
        //Get all ALI's by current Shift Date
        List<ActivityLineInterval> currentShiftStartDateALIs = dateWiseALIsList.get(shift.getStartLocalDate());
        List<ShiftActivity> shiftActivities = shift.getActivities();
        if (!shift.getStartLocalDate().equals(shift.getEndLocalDate())) {
            //Two days ALI's List
            currentShiftStartDateALIs.addAll(dateWiseALIsList.get(shift.getEndLocalDate()));
        }
        Set<DateTime> currentShiftIntervalStartTime = new TreeSet<>();
        for (ShiftActivity shiftActivity : shiftActivities) {
            int numberOfIntervals = shiftActivity.getDurationMinutes() / 15;
            for (int i = 0; i < numberOfIntervals; i++) {
                currentShiftIntervalStartTime.add(new DateTime(shiftActivity.getStartDate()).plusMinutes(i*15));
            }
        }
        *//*match every startDate from the set and if found in {currentShiftStartDateALIs} then assign it
        and remove from the list{currentShiftStartDateALIs}*//*
        List<ActivityLineInterval> applicableCurrentShiftALIs=new ArrayList<>();
        for(DateTime requiredIntervalStartDate:currentShiftIntervalStartTime)
        {
            for(ActivityLineInterval activityLineInterval:currentShiftStartDateALIs){
                if(requiredIntervalStartDate.equals(activityLineInterval.getStart()))
                {
                    applicableCurrentShiftALIs.add(activityLineInterval);
                }
            }
        }
        return applicableCurrentShiftALIs;
    }*/

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
                    IntStream.rangeClosed(0, 1440 / granularity - 1).forEach(i -> {
                        ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[i][getActivityIndex(ali)]++;
                    });
                } else {
                    ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[getTimeIndex(ali.getStart(), granularity)][getActivityIndex(ali)]++;
                }
            }
            printStaffingLevelMatrix(slMatrix, null);
        return slMatrix;
    }
}
