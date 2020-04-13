package com.kairos.shiftplanning.executioner;

import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.Day;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.ShiftLengthAndAverageSetting;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.activityconstraint.*;
import com.kairos.shiftplanning.constraints.unitconstraint.DislikeNightShiftsForNonNightWorkers;
import com.kairos.shiftplanning.constraints.unitconstraint.MaxLengthOfShiftInNightTimeSlot;
import com.kairos.shiftplanning.constraints.unitconstraint.PreferedEmployementType;
import com.kairos.shiftplanning.constraints.unitconstraint.ShiftOnWeekend;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanning.domain.staffing_level.*;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.domain.unit.*;
import com.kairos.shiftplanning.domain.wta_ruletemplates.AverageScheduledTimeWTATemplate;
import com.kairos.shiftplanning.domain.wta_ruletemplates.WTABaseRuleTemplate;
import com.kairos.shiftplanning.enums.SkillType;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.LocalDateConverter;
import com.kairos.shiftplanning.utils.LocalTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.kairos.shiftplanning.utils.ZonedDateTimeConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.commons.utils.DateUtils.asZonedDateTime;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.constants.CommonConstants.*;
import static com.kairos.enums.MasterDataTypeEnum.*;
import static com.kairos.enums.constraint.ScoreLevel.HARD;
import static com.kairos.enums.constraint.ScoreLevel.SOFT;

public class ShiftPlanningGenerator {

    public static final String UNSOLVED_PROBLEM_XML = "src/main/resources/data/staffingLevel_With_Shift.xml";
    public static final int FIRST_BREAK_THRESHOLD_MINUTES = 300;
    public static final int SECOND_BREAK_THRESHOLD_MINUTES = 540;
    public static final int THIRD_BREAK_THRESHOLD_MINUTES = 720;
    public static final int BREAK_DURATION_30 = 30;
    public static final int BREAK_DURATION_15 = 15;
    public static final String BLANK_ACTIVITY = "BLANK";
    public static final String STAFF_TAG = "StaffTag";
    public static final String ACTIVITY_TAG = "ActivityTag";
    public static final String SKILL_TAG = "SkillTag";
    public static final Integer INTERVAL_MINS = 15;
    public static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningGenerator.class);
    public static final String ERROR = "error {}";

    public ShiftRequestPhasePlanningSolution loadUnsolvedSolution() {
        ShiftRequestPhasePlanningSolution unresolvedSolution = new ShiftRequestPhasePlanningSolution();
        Object[] objects = dailyStaffingLines();
        List<DailyStaffingLine> staffingLines = (List<DailyStaffingLine>)objects[1];
        List<Activity> activities = (List<Activity>)objects[0];

        List<ActivityLineInterval> activityLineIntervals= getActivityLineIntervalsList(staffingLines);
        //TODO sort activityLineIntervals
        List<SkillLineInterval> skillLineIntervals=staffingLines.stream().map(dailyStaffingLine -> dailyStaffingLine.getDailySkillLine().getSkillLineIntervals()).collect(ArrayList::new, List::addAll, List::addAll);
        unresolvedSolution.setActivities(activities);
        unresolvedSolution.setActivitiesPerDay((Map<LocalDate, List<Activity>>) objects[2]);
        unresolvedSolution.setActivityLineIntervals(activityLineIntervals);
        unresolvedSolution.setSkillLineIntervals(skillLineIntervals);
        unresolvedSolution.setActivitiesIntervalsGroupedPerDay(groupActivityLineIntervals(unresolvedSolution.getActivityLineIntervals()));
        List<LocalDate> planningDays = getPlanningDays();
        unresolvedSolution.setWeekDates(planningDays);
        Unit unit = getUnit(planningDays);
        unresolvedSolution.setUnit(unit);
        List<Employee> employees= generateEmployeeList(planningDays,unit);
        unresolvedSolution.setEmployees(employees);
        unresolvedSolution.setShifts(generateShiftForAssignments( employees));
        int[] activitiesRank=activities.stream().mapToInt(a->a.getRank()).toArray();
        unresolvedSolution.setStaffingLevelMatrix(new StaffingLevelMatrix(ShiftPlanningUtility.createStaffingLevelMatrix(unresolvedSolution.getWeekDates(),unresolvedSolution.getActivityLineIntervals(),INTERVAL_MINS,unresolvedSolution.getActivities()), activitiesRank));
        return unresolvedSolution;
    }

    private Map<String, List<ActivityLineInterval>> groupActivityLineIntervals(List<ActivityLineInterval> activityLineIntervals) {
        Map<String,List<ActivityLineInterval>> groupedAlis= new HashMap<>();

        for(ActivityLineInterval ali:activityLineIntervals){
            String key=ali.getStart().toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))+"_"+ali.getActivity().getId()+"_"+ali.getStaffNo();
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
                .flatMap(d->IntStream.rangeClosed(0,1440/INTERVAL_MINS-1).mapToObj(i->asZonedDateTime(d).plusMinutes(i*(long)INTERVAL_MINS))).collect(Collectors.toList()));
        secondarySolution.setSkillLineIntervals(solution.getSkillLineIntervals());
        secondarySolution.setWeekDates(solution.getWeekDates());
        secondarySolution.setActivities(solution.getActivities());
        secondarySolution.setStaffingLevelMatrix(solution.getStaffingLevelMatrix());
        return secondarySolution;
    }
    public BreaksIndirectAndActivityPlanningSolution loadUnsolvedBreakAndIndirectActivityPlanningSolution(String filePath){
        XStream xstream = new XStream(new PureJavaReflectionProvider());
        xstream.processAnnotations(Employee.class);
        xstream.processAnnotations(StaffingLevelPlannerEntity.class);
        xstream.processAnnotations(StaffingLevelInterval.class);
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.registerConverter(new ZonedDateTimeConverter());
        xstream.registerConverter(new LocalTimeConverter());
        xstream.registerConverter(new LocalDateConverter());
        BreaksIndirectAndActivityPlanningSolution unresolvedSolution = null;
        try {
            unresolvedSolution = (BreaksIndirectAndActivityPlanningSolution) xstream.fromXML(new File(filePath));
        } catch (Exception e) {
            LOGGER.error(ERROR,e.getMessage());
        }
        return unresolvedSolution;
    }
    private List<ShiftBreak> generateBreaksForShifts(List<ShiftImp> shifts) {
        for(ShiftImp shift:shifts){
            if(shift.isAbsenceActivityApplied() || shift.getMinutes()<FIRST_BREAK_THRESHOLD_MINUTES){
                continue;
            }
            if(shift.getMinutes()>= FIRST_BREAK_THRESHOLD_MINUTES){
                shift.getBreaks().add(new ShiftBreak(BigInteger.valueOf(new Date().getTime()), 1, BREAK_DURATION_30,shift));
            }
            if(shift.getMinutes()>= SECOND_BREAK_THRESHOLD_MINUTES){
                shift.getBreaks().add(new ShiftBreak(BigInteger.valueOf(new Date().getTime()), 2, BREAK_DURATION_15,shift));
            }
            if(shift.getMinutes()>= THIRD_BREAK_THRESHOLD_MINUTES){
                shift.getBreaks().add(new ShiftBreak(BigInteger.valueOf(new Date().getTime()), 3, BREAK_DURATION_15,shift));
            }
        }
        return shifts.stream().flatMap(s->s.getBreaks().stream()).collect(Collectors.toList());
    }

    private ArrayList<ActivityLineInterval> getActivityLineIntervalsList(List<DailyStaffingLine> staffingLines) {
        return staffingLines.stream().map(dailyStaffingLine -> dailyStaffingLine.getDailyActivityLine().getActivityLineIntervals()).collect(ArrayList::new, List::addAll, List::addAll);
    }

    public ShiftRequestPhasePlanningSolution loadUnsolvedSolutionFromXML(String problemXml) {
        XStream xstream = new XStream(new PureJavaReflectionProvider());
        xstream.processAnnotations(Employee.class);
        xstream.processAnnotations(StaffingLevelPlannerEntity.class);
        xstream.processAnnotations(StaffingLevelInterval.class);
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.registerConverter(new ZonedDateTimeConverter());
        xstream.registerConverter(new LocalTimeConverter());
        xstream.registerConverter(new LocalDateConverter());
        ShiftRequestPhasePlanningSolution unresolvedSolution = null;
        try {
            unresolvedSolution = (ShiftRequestPhasePlanningSolution) xstream.fromXML(new File(problemXml));
         } catch (Exception e) {
            LOGGER.error(ERROR,e.getMessage());
        }
        return unresolvedSolution;
    }

    private List<IndirectActivity> generateIndirectActivities(List<Employee> employees) {
        return Arrays.asList(new IndirectActivity(UUID.randomUUID(),20,false,new ArrayList<>(employees.subList(2,5)),"XYZ",false));
    }

    public List<ShiftImp> generateShiftForAssignments(List<Employee> employees) {
        List<ShiftImp> shiftList = new ArrayList<>();
        for(Employee emp:employees){
            for(LocalDate date:getPlanningDays()) {
                ShiftImp sa = new ShiftImp();
                sa.setEmployee(emp);
                sa.setId(BigInteger.valueOf(new Date().getTime()));
                sa.setDate(date);
                shiftList.add(sa);
            }
        }
        return shiftList;
    }


    public Map<ConstraintSubType, Constraint> getActivityContraints(){
        LongestDuration longestDuration = new LongestDuration(80, SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(60, HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(3, SOFT,-1);//3
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3, SOFT,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(60, SOFT,-1);//5
        List<DayType> dayTypes = getDayTypes();
        ActivityDayType activityDayType = new ActivityDayType(dayTypes, SOFT,5);
        ActivityRequiredTag activityRequiredTag = new ActivityRequiredTag(requiredTagId(), HARD,1);
        Map<ConstraintSubType, Constraint> constraintMap = new HashMap<>();
        TimeSlot timeSlot = new TimeSlot(23,7);
        constraintMap.put(ConstraintSubType.ACTIVITY_LONGEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,longestDuration);
        constraintMap.put(ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,shortestDuration);
        constraintMap.put(ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,maxAllocationPerShift);
        constraintMap.put(ConstraintSubType.ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS,maxDiffrentActivity);
        constraintMap.put(ConstraintSubType.MINIMUM_LENGTH_OF_ACTIVITY,minimumLengthofActivity);
        constraintMap.put(ConstraintSubType.ACTIVITY_VALID_DAYTYPE,activityDayType);
        constraintMap.put(ConstraintSubType.ACTIVITY_REQUIRED_TAG,activityRequiredTag);
        constraintMap.put(ConstraintSubType.PRESENCE_AND_ABSENCE_SAME_TIME,new PresenceAndAbsenceAtSameTime(SOFT,-6));
        constraintMap.put(ConstraintSubType.MAX_SHIFT_OF_STAFF,new MaxShiftOfStaff(1, SOFT,-6));
        constraintMap.put(ConstraintSubType.PREFER_PERMANENT_EMPLOYEE,new PreferedEmployementType(newHashSet(123l), SOFT,-4));
        constraintMap.put(ConstraintSubType.MINIMIZE_SHIFT_ON_WEEKENDS,new ShiftOnWeekend(SOFT,-4,newHashSet(DayOfWeek.SATURDAY,DayOfWeek.SUNDAY)));
        constraintMap.put(ConstraintSubType.MAX_LENGTH_OF_SHIFT_IN_NIGHT_TIMESLOT,new MaxLengthOfShiftInNightTimeSlot(SOFT,-4,timeSlot,5));
        constraintMap.put(ConstraintSubType.DISLIKE_NIGHT_SHIFS_FOR_NON_NIGHT_WORKERS,new DislikeNightShiftsForNonNightWorkers(SOFT,-4,timeSlot));
        return constraintMap;
    }
    public  Tag requiredTagId(){
        return new Tag(1l, STAFF_TAG, STAFF);
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
        dayTypes.add(new DayType(8l,"Public Holiday",new ArrayList<>(),countryHolidayCalenders,true,false));
        countryHolidayCalenders = new ArrayList<>();
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2017,12,18), java.time.LocalTime.of(0,0),java.time.LocalTime.of(12,0)));
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2017,12,27),java.time.LocalTime.of(12,0),java.time.LocalTime.of(23,0)));
        countryHolidayCalenders.add(new CountryHolidayCalender(java.time.LocalDate.of(2018,1,1),java.time.LocalTime.of(17,0),java.time.LocalTime.of(23,0)));
        dayTypes.add(new DayType(9l,"Half Public Holiday",new ArrayList<>(),countryHolidayCalenders,true,true));
        return dayTypes;

    }

    public List<Employee> generateEmployeeList(List<LocalDate> planningDays, Unit unit) {
        List<Employee> employees = new ArrayList<>();
        Employee employee = getEmployee("145","Sachin Verma",123l,createTags1(),unit);
        Map<LocalDate,Map<ConstraintSubType, WTABaseRuleTemplate>> wtaTemplateMap = getWTAMap(planningDays);
        employee.setWtaRuleTemplateMap(wtaTemplateMap);
        employees.add(employee);
        Employee employee2 = getEmployee("160","Pradeep Singh",126l,createTags2(), unit);
        employee2.setWtaRuleTemplateMap(wtaTemplateMap);
        employee2.setNightWorker(true);
        employees.add(employee2);
        Employee employee3 = getEmployee("170", "Arvind Das", 123l, createTags2(), unit);
        employee3.setWtaRuleTemplateMap(wtaTemplateMap);
        employee3.setNightWorker(true);
        employees.add(employee3);
        Employee employee4 = getEmployee("180","Ulrik",126l,createTags3(), unit);
        employee4.setWtaRuleTemplateMap(wtaTemplateMap);
        employees.add(employee4);
        Employee employee5 = getEmployee("190", "Ramanuj", 123l, createTags4(), unit);
        employee5.setWtaRuleTemplateMap(wtaTemplateMap);
        employees.add(employee5);
        Employee employee6 = getEmployee("195", "Dravid", 145l, createTags4(), unit);
        employee6.setWtaRuleTemplateMap(wtaTemplateMap);
        employees.add(employee6);
        return employees;
    }

    private Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>> getWTAMap(List<LocalDate> planningDays) {
        Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>> wtaTemplateMap = new HashMap<>();
        PhaseTemplateValue phaseTemplateValue = PhaseTemplateValue.builder().managementValue((short)1180).phaseId(BigInteger.valueOf(13l)).build();
        for (LocalDate planningDay : planningDays) {
            Map<ConstraintSubType, WTABaseRuleTemplate> wtaBaseRuleTemplateMap = new HashMap<>();
            AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = AverageScheduledTimeWTATemplate.builder().intervalLength(1).intervalUnit(WEEKS).minMaxSetting(MinMaxSetting.MAXIMUM).shiftLengthAndAverageSetting(ShiftLengthAndAverageSetting.DIFFERENCE_BETWEEN_START_END_TIME).phaseTemplateValues(newArrayList(phaseTemplateValue)).build();
            wtaBaseRuleTemplateMap.put(ConstraintSubType.AVERAGE_SHEDULED_TIME, averageScheduledTimeWTATemplate);
            wtaTemplateMap.put(planningDay,wtaBaseRuleTemplateMap);
        }
        return wtaTemplateMap;
    }

    private Employee getEmployee(String s, String s2, long l, Set<Tag> tags2, Unit unit) {
        Employee employee = new Employee(Long.valueOf(s), s2, createSkillSet(), null, 0, 0, PaidOutFrequencyEnum.HOURLY, null);
        employee.setBaseCost(BigDecimal.valueOf(1.5));
        employee.setEmploymentTypeId(l);
        employee.setTags(tags2);
        employee.setUnit(unit);
        return employee;
    }

    public LocalDate getPlanningWeekStart(){
        return LocalDate.parse("18/12/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public List<LocalDate> getPlanningDays(){
        LocalDate weekStart=getPlanningWeekStart();
        return IntStream.of(0).mapToObj(i->weekStart.plusDays(i)).collect(Collectors.toList());
    }

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
        List<LocalDate> planningWeekDates=getPlanningDays();
        int intervalMins=15;
        List<Activity> activityPlannerEntities =getActivities();
        Map<LocalDate,List<Activity>> activitiesPerDay= new HashMap<>();
        planningWeekDates.forEach(date->{
            DailyActivityLine dailyActivityLine = getDailyActivityLine(intervalMins, activityPlannerEntities, activitiesPerDay, date);
            DailySkillLine dailySkillLine= new DailySkillLine();
            dailySkillLine.setSkillLineIntervals(new ArrayList<>());
            DailyStaffingLine dailyStaffingLine= new DailyStaffingLine(dailyActivityLine,dailySkillLine );
            dailyStaffingLines.add(dailyStaffingLine);
        });
        return new Object[]{activityPlannerEntities,dailyStaffingLines,activitiesPerDay};
    }

    private DailyActivityLine getDailyActivityLine(int intervalMins, List<Activity> activityPlannerEntities, Map<LocalDate, List<Activity>> activitiesPerDay, LocalDate date) {
        DailyActivityLine dailyActivityLine= new DailyActivityLine(date,new ArrayList<>());
        activitiesPerDay.put(date,new ArrayList<>());
        for (Activity activity : activityPlannerEntities){
            activitiesPerDay.get(date).add(activity);
            if(activity.isBlankActivity()){
                getDailyActivityLine(intervalMins, date, dailyActivityLine, activity);
            }
            else if(activity.isTypePresence()){
                getPresenceActivityLine(intervalMins, date, dailyActivityLine, activity);
            }else if(activity.isTypeAbsence()){
                getAbsenceActivityLine(date, dailyActivityLine, activity);
            }

        }
        return dailyActivityLine;
    }

    private void getAbsenceActivityLine(LocalDate date, DailyActivityLine dailyActivityLine, Activity activity) {
        for(int staffNum=0;staffNum<2;staffNum++){
            ActivityLineInterval activityLineInterval= new ActivityLineInterval(getAbsenceId(),asZonedDateTime(date),24*60,staffNum==0, activity,staffNum);
            dailyActivityLine.getActivityLineIntervals().add(activityLineInterval);
        }
    }

    private void getPresenceActivityLine(int intervalMins, LocalDate date, DailyActivityLine dailyActivityLine, Activity activity) {
        for(int staffNum=0;staffNum<5;staffNum++){//first staff is required
            for(int j=46;j<52;j++){//32..80
                ActivityLineInterval activityLineInterval= new ActivityLineInterval(getId(),asZonedDateTime(date).plusMinutes(j*intervalMins),intervalMins,staffNum<3, activity,staffNum);
                dailyActivityLine.getActivityLineIntervals().add(activityLineInterval);
            }
        }
    }

    private void getDailyActivityLine(int intervalMins, LocalDate date, DailyActivityLine dailyActivityLine, Activity activity) {
        for(int staffNum=0;staffNum<3;staffNum++){//first staff is required
            for(int j=0;j<96;j++){
                ActivityLineInterval activityLineInterval= new ActivityLineInterval(getId(),asZonedDateTime(date).plusMinutes(j*intervalMins),intervalMins,false, activity,staffNum);
                dailyActivityLine.getActivityLineIntervals().add(activityLineInterval);
            }
        }
    }

    private Set<Skill> createSkillSet(){
        Set<Skill> skillSet= new HashSet<>();
        skillSet.add(new Skill(101l,"Cleaner", SkillType.BASIC));
        skillSet.add(new Skill(102l,"Washer", SkillType.BASIC));
        return skillSet;
    }
    private Set<Skill> createSkillSet2(){
        Set<Skill> skillSet= new HashSet<>();
        skillSet.add(new Skill(111l,"Car Cleaner", SkillType.BASIC));
        skillSet.add(new Skill(112l,"Car Washer", SkillType.BASIC));
        return skillSet;
    }

    public TimeType[] createTimeTypes(){
        TimeType[] timeTypes= new TimeType[4];
        timeTypes[0]= new TimeType(BigInteger.valueOf(new Date().getTime()),"presence", TimeTypeEnum.PRESENCE ,true);
        timeTypes[1]= new TimeType(BigInteger.valueOf(new Date().getTime()),"absence",TimeTypeEnum.ABSENCE,true);
        return timeTypes;
    }
    private List<Activity> getActivities(){
        TimeType[] timeTypes= createTimeTypes();
        Set<Tag> tags1 = createTags1();
        Set<Tag> tags2 = createTags2();
        Set<Tag> tags3 = createTags3();
        Set<Tag> tags4 = createTags4();
        List<Activity> activityPlannerEntities = new ArrayList<>();
        Activity activity = new Activity(BigInteger.valueOf(new Date().getTime()),new ArrayList<>(createSkillSet()),2,"Team A",timeTypes[0], 1,10, null,tags1);
        activity.setConstraints(getActivityContraints());
        Activity activity2 =new Activity(BigInteger.valueOf(new Date().getTime()),new ArrayList<>(createSkillSet2()),2,"Team B",timeTypes[0], 2,9, null, tags2);
        activity2.setConstraints(getActivityContraints());
        Activity activity3 = new Activity(BigInteger.valueOf(new Date().getTime()),new ArrayList<>(createSkillSet2()),2,"Day Off",timeTypes[1], 3,2, null,tags3 );
        activity3.setConstraints(getActivityContraints());
        Activity activity4 = new Activity(BigInteger.valueOf(new Date().getTime()),new ArrayList<>(createSkillSet2()),2, BLANK_ACTIVITY,timeTypes[0], 4,1, null,tags4);
        activity4.setConstraints(getActivityContraints());
        activityPlannerEntities.add(activity);
        activityPlannerEntities.add(activity2);
        activityPlannerEntities.add(activity3);
        activityPlannerEntities.add(activity4);
        return activityPlannerEntities;
    }

    public Set<Tag> createTags1(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(1l, STAFF_TAG, STAFF);
        Tag tag2 = new Tag(2l, ACTIVITY_TAG, ACTIVITY);
        Tag tag3 = new Tag(3l, SKILL_TAG, SKILL);
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }

    public Set<Tag> createTags2(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(1l, STAFF_TAG,EXPERTISE);
        Tag tag2 = new Tag(2l, ACTIVITY_TAG, ACTIVITY);
        Tag tag3 = new Tag(3l, SKILL_TAG, SKILL);

        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }
    public Set<Tag> createTags3(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(1l, STAFF_TAG, WTA);
        Tag tag2 = new Tag(2l, ACTIVITY_TAG, ACTIVITY);
        Tag tag3 = new Tag(3l, SKILL_TAG, SKILL);
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }
    public Set<Tag>  createTags4(){
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag(1l, STAFF_TAG, CTA);
        Tag tag2 = new Tag(2l, ACTIVITY_TAG, ACTIVITY);
        Tag tag3 = new Tag(3l, SKILL_TAG, SKILL);
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);
        return tags;
    }

    public Unit getUnit(List<LocalDate> planningDays){
        ShiftOnWeekend shiftOnWeekend = new ShiftOnWeekend();
        shiftOnWeekend.setLevel(HARD);
        shiftOnWeekend.setWeight(3);
        Map<ConstraintSubType,Constraint> unitConstraints = new HashMap<>();
        PreferedEmployementType preferedEmployementType = new PreferedEmployementType();
        preferedEmployementType.setPreferedEmploymentTypeIds(newHashSet(123l,145l));
        preferedEmployementType.setLevel(SOFT);
        preferedEmployementType.setWeight(3);
        unitConstraints.put(ConstraintSubType.PREFER_PERMANENT_EMPLOYEE,preferedEmployementType);
        unitConstraints.put(ConstraintSubType.MINIMIZE_SHIFT_ON_WEEKENDS,shiftOnWeekend);
        Map<Long, DayType> dayTypeMap = getDayTypes().stream().collect(Collectors.toMap(k->k.getId(),v->v));
        Map<String,TimeSlot> timeSlotMap = new HashMap<>();
        timeSlotMap.put(NIGHT,TimeSlot.builder().startHour(23).endHour(7).build());
        timeSlotMap.put(DAY,TimeSlot.builder().startHour(7).endHour(15).build());
        timeSlotMap.put(EVENING,TimeSlot.builder().startHour(15).endHour(23).build());
        Unit unit = Unit.builder().phase(new Phase(BigInteger.valueOf(13l), PhaseDefaultName.CONSTRUCTION, PhaseType.PLANNING)).planningPeriod(new PlanningPeriod(BigInteger.valueOf(12l),planningDays.get(0),planningDays.get(planningDays.size()-1))).dayTypeMap(dayTypeMap).timeSlotMap(timeSlotMap).id(1l).constraints(unitConstraints).build();
        unit.setConstraints(unitConstraints);
        unit.setAbsencePlannedTime(new AbsencePlannedTime(unit.getPhase().getId(),newArrayList(BigInteger.valueOf(3l)),false));
        unit.setPresencePlannedTime(new PresencePlannedTime(unit.getPhase().getId(),newArrayList(BigInteger.valueOf(5l))));
        unit.setNonWorkingPlannedTime(new NonWorkingPlannedTime(unit.getPhase().getId(),newArrayList(BigInteger.valueOf(3l)),false));
        unit.setId(1l);
        return unit;
    }


}