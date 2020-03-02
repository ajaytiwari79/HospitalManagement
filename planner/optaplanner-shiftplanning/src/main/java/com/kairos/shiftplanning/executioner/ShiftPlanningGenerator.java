package com.kairos.shiftplanning.executioner;

import com.kairos.enums.Day;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.constraints.activityConstraint.*;
import com.kairos.shiftplanning.constraints.unitConstraint.PreferedEmployementType;
import com.kairos.shiftplanning.constraints.unitConstraint.ShiftOnWeekend;
import com.kairos.shiftplanning.constraints.unitConstraint.UnitConstraints;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.cta.CollectiveTimeAgreement;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanning.domain.staff.PrevShiftsInfo;
import com.kairos.shiftplanning.domain.staffing_level.*;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.domain.wta.*;
import com.kairos.shiftplanning.enums.SkillType;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.JodaLocalDateConverter;
import com.kairos.shiftplanning.utils.JodaLocalTimeConverter;
import com.kairos.shiftplanning.utils.JodaTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.enums.MasterDataTypeEnum.*;

public class ShiftPlanningGenerator {

    private static final String DATA_UNPLANNED_TASK3_XML = "data/unplannedTask3.xml";
    private static final String DATA_PLANNED_TASKS = "data/employee-staffinglevel.xml";
    private static final String FILE_TO_USE=DATA_UNPLANNED_TASK3_XML;
    public static final String UNSOLVED_PROBLEM_XML = "src/main/resources/data/staffingLevel_With_Shift.xml";
    public static final int FIRST_BREAK_THRESHOLD_MINUTES = 300;
    public static final int SECOND_BREAK_THRESHOLD_MINUTES = 540;
    public static final int THIRD_BREAK_THRESHOLD_MINUTES = 720;
    public static final int BREAK_DURATION_30 = 30;
    public static final int BREAK_DURATION_15 = 15;
    public static final String BLANK_ACTIVITY = "BLANK";
    public static Integer INTERVAL_MINS = 15;
    private static Logger log= LoggerFactory.getLogger(ShiftPlanningGenerator.class);

    public ShiftRequestPhasePlanningSolution loadUnsolvedSolution() {
        ShiftRequestPhasePlanningSolution unresolvedSolution = new ShiftRequestPhasePlanningSolution();
        Object[] objects = dailyStaffingLines();
        List<DailyStaffingLine> staffingLines = (List<DailyStaffingLine>)objects[1];
        List<Activity> activities = (List<Activity>)objects[0];
        List<Employee> employees= generateEmployeeList(activities);
        unresolvedSolution.setEmployees(employees);
        List<ActivityLineInterval> activityLineIntervals= getActivityLineIntervalsList(staffingLines);
        //TODO sort activityLineIntervals
        List<SkillLineInterval> skillLineIntervals=staffingLines.stream().map(dailyStaffingLine -> dailyStaffingLine.getDailySkillLine().getSkillLineIntervals()).collect(ArrayList::new, List::addAll, List::addAll);
        unresolvedSolution.setUnit(getUnit());
        unresolvedSolution.setActivities(activities);
        unresolvedSolution.setActivitiesPerDay((Map<LocalDate, List<Activity>>) objects[2]);
        unresolvedSolution.setActivityLineIntervals(activityLineIntervals);
        unresolvedSolution.setSkillLineIntervals(skillLineIntervals);
        unresolvedSolution.setShifts(generateShiftForAssignments( employees,activityLineIntervals));
        unresolvedSolution.setActivitiesIntervalsGroupedPerDay(groupActivityLineIntervals(unresolvedSolution.getActivityLineIntervals()));
        //unresolvedSolution.setBreaks(generateBreaks(generateShiftForAssignments(employees.get(0))));
        unresolvedSolution.setWeekDates(getPlanningDays());
        int[] activitiesRank=activities.stream().mapToInt(a->a.getRank()).toArray();
        unresolvedSolution.setStaffingLevelMatrix(new StaffingLevelMatrix(ShiftPlanningUtility.createStaffingLevelMatrix(unresolvedSolution.getWeekDates(),unresolvedSolution.getActivityLineIntervals(),INTERVAL_MINS,unresolvedSolution.getActivities()), activitiesRank));
        //writeObjectToXml(unresolvedSolution);
        return unresolvedSolution;
    }
    @Deprecated
    //use SHiftPLannignService
    private Map<String, List<ActivityLineInterval>> groupActivityLineIntervals(List<ActivityLineInterval> activityLineIntervals) {
        Map<String,List<ActivityLineInterval>> groupedAlis= new HashMap<>();

        for(ActivityLineInterval ali:activityLineIntervals){
            String key=ali.getStart().toLocalDate().toString("MM/dd/yyyy")+"_"+ali.getActivity().getId()+"_"+ali.getStaffNo();
            if(groupedAlis.containsKey(key)){
                groupedAlis.get(key).add(ali);
            }else{
                List<ActivityLineInterval> alis=new ArrayList<>();
                alis.add(ali);
                groupedAlis.put(key,alis);
            }
        }
        return groupedAlis;
    }

    public BreaksIndirectAndActivityPlanningSolution loadUnsolvedBreakAndIndirectActivityPlanningSolution(ShiftRequestPhasePlanningSolution solution){
        BreaksIndirectAndActivityPlanningSolution secondarySolution= new BreaksIndirectAndActivityPlanningSolution();
        secondarySolution.setActivityLineIntervals(solution.getActivityLineIntervals());
        secondarySolution.setEmployees(solution.getEmployees());
        secondarySolution.setShifts(solution.getShifts());
        secondarySolution.setIndirectActivities(generateIndirectActivities(solution.getEmployees()));
        secondarySolution.setShiftBreaks(generateBreaksForShifts(secondarySolution.getShifts()));
        secondarySolution.setPossibleStartDateTimes(solution.getWeekDates().stream()
                .flatMap(d->IntStream.rangeClosed(0,1440/INTERVAL_MINS-1).mapToObj(i->d.toDateTimeAtStartOfDay().plusMinutes(i*INTERVAL_MINS))).collect(Collectors.toList()));
        secondarySolution.setSkillLineIntervals(solution.getSkillLineIntervals());
        secondarySolution.setWeekDates(solution.getWeekDates());
        secondarySolution.setActivities(solution.getActivities());
        //secondarySolution.setStaffingLevelMatrix(new StaffingLevelMatrix(ShiftPlanningUtility.createStaffingLevelMatrix(secondarySolution.getWeekDates(),secondarySolution.getActivityLineIntervals(),INTERVAL_MINS,secondarySolution.getActivities())));
        secondarySolution.setStaffingLevelMatrix(solution.getStaffingLevelMatrix());
        return secondarySolution;
    }
    public BreaksIndirectAndActivityPlanningSolution loadUnsolvedBreakAndIndirectActivityPlanningSolution(String filePath){
        //XStream xstream = new XStream();
        XStream xstream = new XStream(new PureJavaReflectionProvider());
        xstream.processAnnotations(Employee.class);
        xstream.processAnnotations(StaffingLevelPlannerEntity.class);
        xstream.processAnnotations(StaffingLevelInterval.class);
        xstream.setMode(XStream.ID_REFERENCES);
        //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
        xstream.registerConverter(new JodaTimeConverter());
        xstream.registerConverter(new JodaLocalTimeConverter());
        xstream.registerConverter(new JodaLocalDateConverter());
        BreaksIndirectAndActivityPlanningSolution unresolvedSolution;
        try {
            //unresolvedSolution = (ShiftPlanningSolution) xstream.fromXML("");//unplannedTask.xml
            unresolvedSolution = (BreaksIndirectAndActivityPlanningSolution) xstream.fromXML(new File(filePath));
            unresolvedSolution.getEmployees().forEach(emp->{
                emp.setWorkingTimeConstraints(getWTA());
                //emp.setCollectiveTimeAgreement(getCTA(unresolvedSolution.getActivities()));
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return unresolvedSolution;
    }
    private List<ShiftBreak> generateBreaksForShifts(List<ShiftImp> shifts) {
        for(ShiftImp shift:shifts){
            if(shift.isAbsenceActivityApplied() || shift.getMinutes()<FIRST_BREAK_THRESHOLD_MINUTES){
                continue;
            }
            if(shift.getMinutes()>= FIRST_BREAK_THRESHOLD_MINUTES){
                shift.getBreaks().add(new ShiftBreak(UUID.randomUUID().toString(), 1, BREAK_DURATION_30,shift));
            }
            if(shift.getMinutes()>= SECOND_BREAK_THRESHOLD_MINUTES){
                shift.getBreaks().add(new ShiftBreak(UUID.randomUUID().toString(), 2, BREAK_DURATION_15,shift));
            }
            if(shift.getMinutes()>= THIRD_BREAK_THRESHOLD_MINUTES){
                shift.getBreaks().add(new ShiftBreak(UUID.randomUUID().toString(), 3, BREAK_DURATION_15,shift));
            }
        }
        return shifts.stream().flatMap(s->s.getBreaks().stream()).collect(Collectors.toList());
    }

    private ArrayList<ActivityLineInterval> getActivityLineIntervalsList(List<DailyStaffingLine> staffingLines) {
        return staffingLines.stream().map(dailyStaffingLine -> dailyStaffingLine.getDailyActivityLine().getActivityLineIntervals()).collect(ArrayList::new, List::addAll, List::addAll);
    }

    public ShiftRequestPhasePlanningSolution loadUnsolvedSolutionFromXML(String problemXml) {
        //XStream xstream = new XStream();
        XStream xstream = new XStream(new PureJavaReflectionProvider());
        xstream.processAnnotations(Employee.class);
        xstream.processAnnotations(StaffingLevelPlannerEntity.class);
        xstream.processAnnotations(StaffingLevelInterval.class);
        xstream.setMode(XStream.ID_REFERENCES);
        //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
        xstream.registerConverter(new JodaTimeConverter());
        xstream.registerConverter(new JodaLocalTimeConverter());
        xstream.registerConverter(new JodaLocalDateConverter());
        ShiftRequestPhasePlanningSolution unresolvedSolution;
        try {
            //unresolvedSolution = (ShiftPlanningSolution) xstream.fromXML("");//unplannedTask.xml
            unresolvedSolution = (ShiftRequestPhasePlanningSolution) xstream.fromXML(new File(problemXml));
            unresolvedSolution.getEmployees().forEach(emp->{
                emp.setWorkingTimeConstraints(getWTA());
                //emp.setCollectiveTimeAgreement(getCTA(unresolvedSolution.getActivities()));
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return unresolvedSolution;
    }

    private List<IndirectActivity> generateIndirectActivities(List<Employee> employees) {
        return Arrays.asList(new IndirectActivity(UUID.randomUUID(),20,false,new ArrayList<>(employees.subList(2,5)),"XYZ",false));
    }

    private void writeObjectToXml(ShiftRequestPhasePlanningSolution shiftPlanningSolution){
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.registerConverter(new JodaTimeConverter());
        String xml = xStream.toXML(shiftPlanningSolution);
        writeStringToFile(xml, UNSOLVED_PROBLEM_XML);
        //saveRecomendationPlanningProblem(shiftPlanningSolution.getStaffingLevels());

    }
/*
    private void saveRecomendationPlanningProblem(List<StaffingLevel> staffingLevels){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.registerModule(new JodaModule());
            mapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID , true);
            mapper.writeValue(new File("src/main/resources/data/recomendation problem_with staffinglevel.json"),staffingLevels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void writeStringToFile(String data,String path){
        try {
            PrintWriter out = new PrintWriter(new File(path));
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private List<ShiftImp> generateShifts(List<Employee> employees){
        LocalDate shiftStart=getPlanningWeekStart();
        List<ShiftImp> shifts= new ArrayList<>();
        //for (Employee employee : employees) {
        int start = 5;
        for(int i=0;i<6;i++){
            ShiftImp shift = new ShiftImp();
            shift.setStartTime(new DateTime(shiftStart.toDateTimeAtStartOfDay()).minusHours(start+5).toLocalTime());
            shift.setEndTime(new DateTime(shiftStart.toDateTimeAtStartOfDay()).minusHours(start).toLocalTime());
            start+=5;
            shift.setDate(shiftStart.minusDays(i+1));
            shift.setLocked(true);
            shift.setEmployee(employees.get(0));
            shift.setId(UUID.randomUUID());
            shifts.add(shift);
        }
        return shifts;
    }

    public List<ShiftImp> generateShiftForAssignments(List<Employee> employees, List<ActivityLineInterval> activityLineIntervals) {
        List<ShiftImp> shiftList = new ArrayList<>();
        int i = 0;
        for(Employee emp:employees){
            for(LocalDate date:getPlanningDays()) {
                /*List<ActivityLineInterval> activityLineIntervalList = activityLineIntervals.subList(i,i=i+5);
                activityLineIntervalList.sort(Comparator.comparing(ActivityLineInterval::getStart));
                ActivityLineInterval ali = null;*/
                ShiftImp sa = new ShiftImp();
                sa.setEmployee(emp);
                sa.setId(UUID.randomUUID());
                sa.setDate(date);
                /*sa.setActivityLineIntervals(activityLineIntervalList);
                sa.setStartTime(activityLineIntervalList.get(0).getStart().toLocalTime());
                sa.setEndTime(activityLineIntervalList.get(activityLineIntervalList.size()-1).getEnd().toLocalTime());
                int j= 0;
                for (ActivityLineInterval activityLineInterval : activityLineIntervalList) {
                        activityLineInterval.setPrevious(ali);
                        activityLineInterval.setShift(sa);
                    j=j+1;
                    if(j<activityLineIntervalList.size()) {
                        ali = activityLineIntervalList.get(j);

                    }else {
                        ali = null;
                    }
                    activityLineInterval.setNext(ali);
                }*/
                shiftList.add(sa);
            }
        }
        return shiftList;
    }


    public Map<ConstraintSubType, Constraint> getActivityContraints(){
        LongestDuration longestDuration = new LongestDuration(80, ScoreLevel.SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(60,ScoreLevel.HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(3,ScoreLevel.SOFT,-1);//3
        //ContinousActivityPerShift continousActivityPerShift = new ContinousActivityPerShift(3,ScoreLevel.SOFT,-4);
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3,ScoreLevel.SOFT,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(60,ScoreLevel.SOFT,-1);//5
        List<DayType> dayTypes = getDayTypes();
        ActivityDayType activityDayType = new ActivityDayType(dayTypes,ScoreLevel.SOFT,5);
        ActivityRequiredTag activityRequiredTag = new ActivityRequiredTag(requiredTagId(),ScoreLevel.HARD,1);
        Map<ConstraintSubType, Constraint> constraintMap = new HashMap<>();
        constraintMap.put(ConstraintSubType.ACTIVITY_LONGEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,longestDuration);
        constraintMap.put(ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,shortestDuration);
        constraintMap.put(ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,maxAllocationPerShift);
        constraintMap.put(ConstraintSubType.ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS,maxDiffrentActivity);
        constraintMap.put(ConstraintSubType.MINIMUM_LENGTH_OF_ACTIVITY,minimumLengthofActivity);
        constraintMap.put(ConstraintSubType.ACTIVITY_VALID_DAYTYPE,activityDayType);
        constraintMap.put(ConstraintSubType.ACTIVITY_REQUIRED_TAG,activityRequiredTag);
        return constraintMap;
    }
    public  Tag requiredTagId(){
        Tag tag = new Tag(new BigInteger("1"),"StaffTag", STAFF, false, 958);;
        return tag;
    }


    private List<DayType> getDayTypes(){
        List<DayType> dayTypes = new ArrayList<>();
        dayTypes.add(new DayType(5l,"Monday",Arrays.asList(Day.MONDAY),new ArrayList<>(),false,false));
        dayTypes.add(new DayType(6l,"Tuesday",Arrays.asList(Day.TUESDAY),new ArrayList<>(),false,false));
        dayTypes.add(new DayType(7l,"Everyday",Arrays.asList(Day.EVERYDAY),new ArrayList<>(),false,false));
        List<CountryHolidayCalender> countryHolidayCalenders = new ArrayList<>();
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2017,12,11),null,null));
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2017,12,25),null,null));
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2018,1,1),null,null));
        dayTypes.add(new DayType(7l,"Public Holiday",new ArrayList<>(),countryHolidayCalenders,true,false));
        countryHolidayCalenders = new ArrayList<>();
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2017,12,18), java.time.LocalTime.of(0,0),java.time.LocalTime.of(12,0)));
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2017,12,27),java.time.LocalTime.of(12,0),java.time.LocalTime.of(23,0)));
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2018,1,1),java.time.LocalTime.of(17,0),java.time.LocalTime.of(23,0)));
        dayTypes.add(new DayType(7l,"Half Public Holiday",new ArrayList<>(),countryHolidayCalenders,true,true));
        return dayTypes;

    }

    public List<Employee> generateEmployeeList(List<Activity> activities) {
        List<Employee> employees = new ArrayList<Employee>();
        Employee employee =new Employee("145","Sachin Verma",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,3l);
        //employee.setCollectiveTimeAgreement(getCTA(activities));
        employee.setBaseCost(new BigDecimal(1.5));
        employee.setWorkingTimeConstraints(getWTA());
        employee.setPrevShiftsInfo(getPreShiftsInfo());
        employee.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employee.setTags(createTags1());
        employee.setEmploymentTypeId(123l);
        employees.add(employee);
        //employees.add(new Employee(102l,"Jane Doe",new ArrayList<Skill>()));
        //employees.add(new Employee(103l,"Jean Doe",new ArrayList<Skill>()));
        Employee employee2 = new Employee("160","Pradeep Singh",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,5l);
        //employee2.setCollectiveTimeAgreement(getCTA(activities));
        employee2.setBaseCost(new BigDecimal(1.5));
        employee2.setWorkingTimeConstraints(getWTA());
        employee2.setPrevShiftsInfo(getPreShiftsInfo());
        employee2.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee2.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employee2.setEmploymentTypeId(126l);
        employee2.setTags(createTags2());
        employees.add(employee2);

        Employee employee3 = new Employee("170","Arvind Das",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        //employee3.setCollectiveTimeAgreement(getCTA(activities));
        employee3.setBaseCost(new BigDecimal(1.5));
        employee3.setWorkingTimeConstraints(getWTA());
        employee3.setPrevShiftsInfo(getPreShiftsInfo());
        employee3.setEmploymentTypeId(123l);
        employee3.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee3.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employee3.setTags(createTags2());
        employees.add(employee3);

        Employee employee4 =new Employee("180","Ulrik",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,7l);
        //employee4.setCollectiveTimeAgreement(getCTA(activities));
        employee4.setBaseCost(new BigDecimal(1.5));
        employee4.setWorkingTimeConstraints(getWTA());
        employee4.setPrevShiftsInfo(getPreShiftsInfo());
        employee4.setEmploymentTypeId(126l);
        employee4.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee4.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employee4.setTags(createTags3());
        employees.add(employee4);

        Employee employee5 = new Employee("190","Ramanuj",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        //employee5.setCollectiveTimeAgreement(getCTA(activities));
        employee5.setBaseCost(new BigDecimal(1.5));
        employee5.setWorkingTimeConstraints(getWTA());
        employee5.setPrevShiftsInfo(getPreShiftsInfo());
        employee5.setEmploymentTypeId(123l);
        employee5.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee5.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employee5.setTags(createTags4());
        employees.add(employee5);


        Employee employee6 = new Employee("195","Dravid",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        //employee6.setCollectiveTimeAgreement(getCTA(activities));
        employee6.setBaseCost(new BigDecimal(1.5));
        employee6.setWorkingTimeConstraints(getWTA());
        employee6.setPrevShiftsInfo(getPreShiftsInfo());
        employee6.setEmploymentTypeId(145l);
        employee6.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee6.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employee6.setTags(createTags4());
        employees.add(employee6);


        return employees;
    }

    public PrevShiftsInfo getPreShiftsInfo(){
        PrevShiftsInfo prevShiftsInfo = new PrevShiftsInfo();
        prevShiftsInfo.setMaximumNumberOfNightsInfo(3);
        prevShiftsInfo.setNumberOfWeekendShiftInPeriodInfo(5);
        prevShiftsInfo.setMaximumAverageScheduledTimeInfo(300);
        prevShiftsInfo.setMaximumShiftsInIntervalInfo(11);
        prevShiftsInfo.setShortestAndAverageDailyRestInfo(250);
        prevShiftsInfo.setPrevConsecutiveWorkingDay(2);
        prevShiftsInfo.setPrevConsecutiveNightShift(1);
        return prevShiftsInfo;
    }

    public WorkingTimeConstraints getWTA(){
        Interval interval = new Interval(getPlanningWeekStart().toDateTimeAtStartOfDay(),getPlanningWeekStart().plusDays(7).toDateTimeAtStartOfDay());
        long nightStarts = new DateTime().withTimeAtStartOfDay().plusHours(20).getMinuteOfDay();
        long nightEnds = new DateTime().plusDays(1).withTimeAtStartOfDay().plusHours(3).getMinuteOfDay();
        MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTime = new MaximumAverageScheduledTimeWTATemplate(300,4,-10,ScoreLevel.SOFT,getPlanningWeekStart());
        maximumAverageScheduledTime.setInterval(interval);
        MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDays = new MaximumConsecutiveWorkingDaysWTATemplate(3,-5,ScoreLevel.SOFT);
        MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate(2,-6,ScoreLevel.SOFT,nightStarts,nightEnds);
        MaximumNightShiftLengthWTATemplate maximumNightShiftLength = new MaximumNightShiftLengthWTATemplate(600,-8,ScoreLevel.SOFT,nightStarts,nightEnds);
        MaximumNumberOfNightsWTATemplate maximumNumberOfNights = new MaximumNumberOfNightsWTATemplate(4,-6,ScoreLevel.SOFT,nightStarts,nightEnds);
        MaximumShiftLengthWTATemplate maximumShiftLength = new MaximumShiftLengthWTATemplate(600,-4,ScoreLevel.SOFT);
        MaximumShiftsInIntervalWTATemplate maximumShiftsInInterval = new MaximumShiftsInIntervalWTATemplate(6,-5,ScoreLevel.SOFT);
        maximumShiftsInInterval.setInterval(interval);
        MinimumConsecutiveNightsWTATemplate minimumConsecutiveNights = new MinimumConsecutiveNightsWTATemplate(2,-1,ScoreLevel.SOFT,nightStarts,nightEnds);
        MinimumDailyRestingTimeWTATemplateTemplate minimumDailyRestingTime = new MinimumDailyRestingTimeWTATemplateTemplate(660,-1,ScoreLevel.SOFT);
        MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShift = new MinimumDurationBetweenShiftWTATemplate(60,-1,ScoreLevel.SOFT);
        MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNights = new MinimumRestConsecutiveNightsWTATemplate(400,3,-1,ScoreLevel.SOFT,nightStarts,nightEnds);
        MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDays = new MinimumRestInConsecutiveDaysWTATemplate(200,4,-1,ScoreLevel.SOFT);
        MinimumShiftLengthWTATemplate minimumShiftLength = new MinimumShiftLengthWTATemplate(120,-1,ScoreLevel.SOFT);
        MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriod = new MinimumWeeklyRestPeriodWTATemplate(1200,-1,ScoreLevel.SOFT);
        minimumWeeklyRestPeriod.setInterval(interval);
        //NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriod = new NumberOfWeekendShiftInPeriodWTATemplate(2,-5,ScoreLevel.SOFT);
        NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriod = new NumberOfWeekendShiftInPeriodWTATemplate(2, 4,new LocalTime(14,0),0,new LocalTime(7,15),false,-5,ScoreLevel.SOFT,getPlanningWeekStart());
        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRest = new ShortestAndAverageDailyRestWTATemplate(320,-5,ScoreLevel.SOFT);
        WorkingTimeConstraints workingTimeConstraints = new WorkingTimeConstraints();
        workingTimeConstraints.setMaximumAverageScheduledTime(maximumAverageScheduledTime);
        workingTimeConstraints.setMaximumConsecutiveWorkingDays(maximumConsecutiveWorkingDays);
        workingTimeConstraints.setMaximumConsecutiveWorkingNights(maximumConsecutiveWorkingNights);
        workingTimeConstraints.setMaximumNightShiftLength(maximumNightShiftLength);
        workingTimeConstraints.setMaximumNumberOfNights(maximumNumberOfNights);
        workingTimeConstraints.setMaximumShiftLength(maximumShiftLength);
        workingTimeConstraints.setMaximumShiftsInInterval(maximumShiftsInInterval);
        workingTimeConstraints.setMinimumConsecutiveNights(minimumConsecutiveNights);
        workingTimeConstraints.setMinimumDailyRestingTime(minimumDailyRestingTime);
        workingTimeConstraints.setMinimumDurationBetweenShift(minimumDurationBetweenShift);
        workingTimeConstraints.setMinimumRestConsecutiveNights(minimumRestConsecutiveNights);
        workingTimeConstraints.setMinimumRestInConsecutiveDays(minimumRestInConsecutiveDays);
        workingTimeConstraints.setMinimumShiftLength(minimumShiftLength);
        workingTimeConstraints.setMinimumWeeklyRestPeriod(minimumWeeklyRestPeriod);
        workingTimeConstraints.setNumberOfWeekendShiftInPeriod(numberOfWeekendShiftInPeriod);
        workingTimeConstraints.setShortestAndAverageDailyRest(shortestAndAverageDailyRest);
        return workingTimeConstraints;
    }

    public LocalDate getPlanningWeekStart(){
        return DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate("18/12/2019");
    }
   /* public List<LocalDate> getPlanningWeek(){
        LocalDate weekStart=getPlanningWeekStart();
        return IntStream.rangeClosed(0,6).mapToObj(i->weekStart.plusDays(i)).collect(Collectors.toList());
    }*/
    public List<LocalDate> getPlanningDays(){
        LocalDate weekStart=getPlanningWeekStart();
        //return IntStream.of(0,1,2,3,4,5,6).mapToObj(i->weekStart.plusDays(i)).collect(Collectors.toList());
        return IntStream.of(0).mapToObj(i->weekStart.plusDays(i)).collect(Collectors.toList());
    }
    /*@Deprecated
    public List<StaffingLevel> createStaffingLevels(){
        List<StaffingLevel> staffingLevels = new ArrayList<>();
        for(int j=0;j<7;j++) {
            StaffingLevel staffingLevel = new StaffingLevel();
            Integer intervalMins = 15;
            LocalDate dateTime = getPlanningWeekStart().plusDays(j);
            //staffingLevel.setDate(dateTime.toLocalDate());
            staffingLevel.setId(""+j);
            staffingLevel.setUnitId(100l);

            staffingLevel.setIntervalMinutes(intervalMins);
            List<StaffingLevelInterval> intervals = new ArrayList<>();
            StaffingLevelActivityType[] staffingLevelActivityTypes = getActivityTypes(intervalMins);
            StaffingLevelSkill[] staffingLevelSkills=getSkillsDemand(intervalMins);
            *//*IntStream.rangeClosed(0, (1440 / intervalMins) - 1).forEachOrdered(i -> {
                intervals.add(new StaffingLevelInterval(dateTime.withTimeAtStartOfDay().plusMinutes(i * intervalMins), dateTime.withTimeAtStartOfDay().plusMinutes((i + 1) * intervalMins),
                        0, 0, staffingLevelSkills[i]==null || !dateTime.equals(getPlanningWeekStart())?null:Arrays.asList(staffingLevelSkills[i]), staffingLevelActivityTypes[i] == null ? null : Arrays.asList(staffingLevelActivityTypes[i])));
            });*//*
            staffingLevel.setIntervals(intervals);
            printStaffingLevel(staffingLevel);
            staffingLevels.add(staffingLevel);
        }
        return  staffingLevels;
    }*/
    private Object[] dailyStaffingLines(){
        return generateActivityLine();
    }

    /**
     *
     * @return This return a dummy staffing level with 15 mins interval for a week and every day between 8AM-8PM we require 1 non-optional staff and 1 optional staff for 2 activities
     */
    private static int seq=10000;
    public static String getId(){
        return ""+ ++seq;
    }
    public static String getAbsenceId(){
        return ""+ ++seq*2;
    }
    public Object[] generateActivityLine(){
        List<DailyStaffingLine> dailyStaffingLines = new ArrayList<>();
        LocalDate weekStart = getPlanningWeekStart();
        //List<LocalDate> planningWeekDates= getPlanningWeek();
        List<LocalDate> planningWeekDates=getPlanningDays();
        int intervalMins=15;
        List<Activity> activityPlannerEntities =getActivities();
        Map<LocalDate,List<Activity>> activitiesPerDay= new HashMap<>();
        planningWeekDates.forEach(date->{
            DailyActivityLine dailyActivityLine= new DailyActivityLine(date,new ArrayList<>());
            activitiesPerDay.put(date,new ArrayList<>());
            for (Activity activity : activityPlannerEntities){
                activitiesPerDay.get(date).add(activity);
                if(activity.isBlankActivity()){
                    for(int staffNum=0;staffNum<3;staffNum++){//first staff is required
                        for(int j=0;j<96;j++){
                            ActivityLineInterval activityLineInterval= new ActivityLineInterval(getId(),date.toDateTimeAtStartOfDay().plusMinutes(j*intervalMins),intervalMins,false, activity,staffNum);
                            dailyActivityLine.getActivityLineIntervals().add(activityLineInterval);
                        }
                    }
                }
                else if(activity.isTypePresence()){
                    for(int staffNum=0;staffNum<5;staffNum++){//first staff is required
                        for(int j=46;j<52;j++){//32..80
                            ActivityLineInterval activityLineInterval= new ActivityLineInterval(getId(),date.toDateTimeAtStartOfDay().plusMinutes(j*intervalMins),intervalMins,staffNum<3, activity,staffNum);
                            dailyActivityLine.getActivityLineIntervals().add(activityLineInterval);
                        }
                    }
                }else if(activity.isTypeAbsence()){
                    for(int staffNum=0;staffNum<2;staffNum++){
                        ActivityLineInterval activityLineInterval= new ActivityLineInterval(getAbsenceId(),date.toDateTimeAtStartOfDay(),24*60,staffNum==0, activity,staffNum);
                        dailyActivityLine.getActivityLineIntervals().add(activityLineInterval);
                    }
                }

            }
            DailySkillLine dailySkillLine= new DailySkillLine();
            dailySkillLine.setSkillLineIntervals(new ArrayList<>());
            DailyStaffingLine dailyStaffingLine= new DailyStaffingLine(dailyActivityLine,dailySkillLine );
            dailyStaffingLines.add(dailyStaffingLine);
        });
        Object[] objects = {activityPlannerEntities,dailyStaffingLines,activitiesPerDay};
        return objects;
    }
    //@Deprecated
    /*public List<DailyStaffingLine> generateActivityLine(List<staffinglevel> staffingLevels){
        List<DailyStaffingLine> dailyStaffingLines = new ArrayList<>();
        staffingLevels.forEach(staffingLevel -> {
            DailyActivityLine dailyActivityLine= new DailyActivityLine(staffingLevel.getDate(),new ArrayList<>());
            DailySkillLine dailySkillLine= new DailySkillLine(staffingLevel.getDate(),new ArrayList<>());
            staffingLevel.getIntervals().forEach(staffingLevelInterval -> {
                if(staffingLevelInterval.getActivityTypeLevels()!=null){
                    staffingLevelInterval.getActivityTypeLevels().forEach(activityTypeLevel -> {
                        if(activityTypeLevel.getMaximumStaffRequired()==0)return;
                        IntStream.rangeClosed(1,activityTypeLevel.getMaximumStaffRequired()).forEachOrdered(i -> {
                            ActivityLineInterval activityLineInterval = new ActivityLineInterval(staffingLevelInterval.getStart(),15,
                                    i<=activityTypeLevel.getMinimumStaffRequired(),activityTypeLevel.getActivityTypeId());
                            dailyActivityLine.getActivityLineIntervalsList().add(activityLineInterval);
                        });
                    });
                }
                if(staffingLevelInterval.getSkillLevels()!=null){
                    staffingLevelInterval.getSkillLevels().forEach(skillLevel -> {
                        if(skillLevel.getStaffRequired()==0)return;
                        IntStream.rangeClosed(1,skillLevel.getStaffRequired()).forEachOrdered(i -> {
                            SkillLineInterval skillLineInterval= new SkillLineInterval(staffingLevelInterval.getStart(),staffingLevelInterval.getEnd(),true,skillLevel.getSkill());
                            dailySkillLine.getSkillLineIntervals().add(skillLineInterval);
                        });
                    });
                }
            });
            DailyStaffingLine dailyStaffingLine = new DailyStaffingLine(dailyActivityLine,dailySkillLine);
            dailyStaffingLines.add(dailyStaffingLine);
        });
        return dailyStaffingLines;
    }*/

   /* private void printStaffingLevel(StaffingLevel staffingLevel) {
        staffingLevel.getIntervals().forEach(interval->{
            if(interval.getActivityTypeLevels()==null) return;
            StringBuilder sb = new StringBuilder();
            *//*interval.getStaffingLevelActivityTypes().forEach(act->{
                sb.append(act.getMinimumStaffRequired()).append(",").append(act.getSkillSet().toArray()[0]);
            });*//*
            *//*log.info("start:{} end:{} min:{} max:{} Activity:{}",interval.getStart().toString("HH:mm"),interval.getEnd().toString("HH:mm"),interval.getMinimumStaffRequired(),
                    interval.getMaximumStaffRequired(),sb.toString());*//*
        });
    }*/

    private int[][] getMinMaxWorkHours(Integer intervalMins){

        int[][] minMaxHours= new int[1440/intervalMins][2];
        if(intervalMins==15){
            IntStream.rangeClosed(32,79).forEachOrdered(i->{
                minMaxHours[i][0]=1;
                minMaxHours[i][1]=2;
            });
        }
        log.info("min:{}, max:{}", Arrays.asList(minMaxHours).stream().mapToInt(i->i[0]).sum(), Arrays.asList(minMaxHours).stream().mapToInt(i->i[1]).sum());
        return minMaxHours;
    }
    private StaffingLevelActivityType[] getActivityTypes(Integer intervalMins){
        StaffingLevelActivityType[] staffingLevelActivityTypes = new StaffingLevelActivityType[1440/intervalMins];
        String activityTypeId=UUID.randomUUID().toString();
        if(intervalMins==15){
            IntStream.rangeClosed(32,87).forEachOrdered(i->{
                staffingLevelActivityTypes[i]= createStaffingLevelActivityType(activityTypeId);
            });
        }
        return staffingLevelActivityTypes;
    }
    private StaffingLevelSkill[] getSkillsDemand(Integer intervalMins){
        StaffingLevelSkill[] staffingLevelSkills = new StaffingLevelSkill[1440/intervalMins];
        String activityTypeId=UUID.randomUUID().toString();
        if(intervalMins==15){
            IntStream.rangeClosed(32,87).forEachOrdered(i->{
                staffingLevelSkills[i]= createStaffingLevelSkills(activityTypeId);
            });
        }
        return staffingLevelSkills;
    }
    private Set<Skill> createSkillSet(){
        Set<Skill> skillSet= new HashSet<>();
        skillSet.add(new Skill("101l","Cleaner", SkillType.BASIC));
        skillSet.add(new Skill("102l","Washer", SkillType.BASIC));
        return skillSet;
    }
    private Set<Skill> createSkillSet2(){
        Set<Skill> skillSet= new HashSet<>();
        skillSet.add(new Skill("111l","Car Cleaner", SkillType.BASIC));
        skillSet.add(new Skill("112l","Car Washer", SkillType.BASIC));
        return skillSet;
    }
    private Skill createDistinctSkill(){
        return new Skill("105l","Launder", SkillType.BASIC);
    }
    private StaffingLevelActivityType createStaffingLevelActivityType(String activityTypeId){
        return new StaffingLevelActivityType(createSkillSet(),2,3,activityTypeId);
    }
    private StaffingLevelSkill createStaffingLevelSkills(String activityTypeId){
        return new StaffingLevelSkill(new ArrayList<>(createSkillSet()).get(0),1);
    }
    public TimeType[] createTimeTypes(){
        TimeType[] timeTypes= new TimeType[4];
        timeTypes[0]= new TimeType(UUID.randomUUID().toString(),"presence", TimeTypeEnum.PRESENCE );
        timeTypes[1]= new TimeType(UUID.randomUUID().toString(),"absence",TimeTypeEnum.ABSENCE);
        return timeTypes;
    }
    private List<Activity> getActivities(){
        TimeType[] timeTypes= createTimeTypes();
        Set<Tag> tags1 = createTags1();
        Set<Tag> tags2 = createTags2();
        Set<Tag> tags3 = createTags3();
        Set<Tag> tags4 = createTags4();
        List<Activity> activityPlannerEntities = new ArrayList<>();
        Activity activity = new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet()),2,"Team A",timeTypes[0], 1,10, null,tags1);
        activity.setConstraintMap(getActivityContraints());
        Activity activity2 =new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet2()),2,"Team B",timeTypes[0], 2,9, null, tags2);
        activity2.setConstraintMap(getActivityContraints());
        Activity activity3 = new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet2()),2,"Day Off",timeTypes[1], 3,2, null,tags3 );
        activity3.setConstraintMap(getActivityContraints());
        Activity activity4 = new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet2()),2, BLANK_ACTIVITY,timeTypes[0], 4,1, null,tags4);
        activity4.setConstraintMap(getActivityContraints());
        activityPlannerEntities.add(activity);
        activityPlannerEntities.add(activity2);
        activityPlannerEntities.add(activity3);
        activityPlannerEntities.add(activity4);
        return activityPlannerEntities;
    }

    public Set<Tag> createTags1(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(new BigInteger("1"),"StaffTag", STAFF, false, 958);
        Tag tag2 = new Tag(new BigInteger("2"),"ActivityTag", ACTIVITY, true, 18712);
        Tag tag3 = new Tag(new BigInteger("3"),"SkillTag", SKILL, false, 958);
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }

    public Set<Tag> createTags2(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(new BigInteger("1"),"StaffTag",EXPERTISE , false, 958);
        Tag tag2 = new Tag(new BigInteger("2"),"ActivityTag", ACTIVITY, true, 18712);
        Tag tag3 = new Tag(new BigInteger("3"),"SkillTag", SKILL, false, 958);

        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }
    public Set<Tag> createTags3(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(new BigInteger("1"),"StaffTag", WTA, false, 958);
        Tag tag2 = new Tag(new BigInteger("2"),"ActivityTag", ACTIVITY, true, 18712);
        Tag tag3 = new Tag(new BigInteger("3"),"SkillTag", SKILL, false, 958);
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }
    public Set<Tag>  createTags4(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(new BigInteger("1"),"StaffTag", CTA, false, 958);
        Tag tag2 = new Tag(new BigInteger("2"),"ActivityTag", ACTIVITY, true, 18712);
        Tag tag3 = new Tag(new BigInteger("3"),"SkillTag", SKILL, false, 958);
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }


    /*private List<String> getActivityIds(){
        List<String> ids = new ArrayList<>(2);
        getActivities().forEach(e->{
            ids.add(e.getId());
        });
        return ids;
    }*/

    public Unit getUnit(){
        // Shift On Weekends
        ShiftOnWeekend shiftOnWeekend = new ShiftOnWeekend();
        shiftOnWeekend.setLevel(ScoreLevel.HARD);
        shiftOnWeekend.setWeight(3);
        UnitConstraints unitConstraints = new UnitConstraints();
        unitConstraints.setShiftOnWeekend(shiftOnWeekend);
        //Prefer Permanent Employee
        PreferedEmployementType preferedEmployementType = new PreferedEmployementType();
        preferedEmployementType.setPreferedEmploymentTypeIds(newHashSet(123l,145l));
        preferedEmployementType.setLevel(ScoreLevel.SOFT);
        preferedEmployementType.setWeight(3);
        unitConstraints.setPreferedEmployementType(preferedEmployementType);





        Unit unit =new Unit();
        unit.setUnitConstraints(unitConstraints);
        unit.setId("1");
        return unit;
    }


}