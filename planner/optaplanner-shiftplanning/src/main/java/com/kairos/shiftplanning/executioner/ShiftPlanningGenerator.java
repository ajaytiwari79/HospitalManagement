package com.kairos.shiftplanning.executioner;


import com.kairos.enums.Day;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.shiftplanning.domain.*;
import com.kairos.shiftplanning.domain.activityConstraint.*;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.cta.*;
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
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                emp.setCollectiveTimeAgreement(getCTA(unresolvedSolution.getActivities()));
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return unresolvedSolution;
    }
    private List<ShiftBreak> generateBreaksForShifts(List<ShiftRequestPhase> shifts) {
        for(ShiftRequestPhase shift:shifts){
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
                emp.setCollectiveTimeAgreement(getCTA(unresolvedSolution.getActivities()));
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return unresolvedSolution;
    }

    private List<Absence> generateAbsenceList(List<Employee> employees) {
        List<Absence> absences= new ArrayList<>();
        employees.forEach(employee -> {
            /*Absence absenceVeto1= new Absence(UUID.randomUUID(),employee,new DateTime(getPlanningWeekStart().plusDays(3)),new DateTime(getPlanningWeekStart().plusDays(4)),"VETO");
            Absence absenceStopBrick1= new Absence(UUID.randomUUID(),employee,new DateTime(getPlanningWeekStart().plusDays(5)),new DateTime(getPlanningWeekStart().plusDays(5)).plusHours(8),"SB");
            Absence absenceStopBrick2= new Absence(UUID.randomUUID(),employee,new DateTime(getPlanningWeekStart().plusDays(5)).plusHours(20),new DateTime(getPlanningWeekStart().plusDays(6)),"SB");
            absences.add(absenceVeto1);
            absences.add(absenceStopBrick1);
            absences.add(absenceStopBrick2);*/
        });
        return absences;
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
    private List<ShiftRequestPhase> generateShifts(List<Employee> employees){
        LocalDate shiftStart=getPlanningWeekStart();
        List<ShiftRequestPhase> shifts= new ArrayList<>();
        //for (Employee employee : employees) {
        int start = 5;
        for(int i=0;i<6;i++){
            ShiftRequestPhase shift = new ShiftRequestPhase();
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

    public List<ShiftRequestPhase> generateShiftForAssignments(List<Employee> employees,List<ActivityLineInterval> activityLineIntervals) {
        List<ShiftRequestPhase> shiftList = new ArrayList<>();
        int i = 0;
        for(Employee emp:employees){
            for(LocalDate date:getPlanningDays()) {
                /*List<ActivityLineInterval> activityLineIntervalList = activityLineIntervals.subList(i,i=i+5);
                activityLineIntervalList.sort(Comparator.comparing(ActivityLineInterval::getStart));
                ActivityLineInterval ali = null;*/
                ShiftRequestPhase sa = new ShiftRequestPhase();
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


    public ActivityConstraints getActivityContraints(){
        LongestDuration longestDuration = new LongestDuration(80, ScoreLevel.SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(60,ScoreLevel.HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(3,ScoreLevel.MEDIUM,-1);//3
        //ContinousActivityPerShift continousActivityPerShift = new ContinousActivityPerShift(3,ScoreLevel.SOFT,-4);
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3,ScoreLevel.MEDIUM,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(60,ScoreLevel.MEDIUM,-1);//5
        List<DayType> dayTypes = getDayTypes();
        ActivityDayType activityDayType = new ActivityDayType(dayTypes,ScoreLevel.SOFT,5);
        ActivityConstraints activityConstraints = new ActivityConstraints(longestDuration,shortestDuration,maxAllocationPerShift,maxDiffrentActivity,minimumLengthofActivity,activityDayType);
        return activityConstraints;
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
        Employee employee =new Employee("145","Sachin Verma",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        employee.setCollectiveTimeAgreement(getCTA(activities));
        employee.setBaseCost(new BigDecimal(1.5));
        employee.setWorkingTimeConstraints(getWTA());
        employee.setPrevShiftsInfo(getPreShiftsInfo());
        employee.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employees.add(employee);
        //employees.add(new Employee(102l,"Jane Doe",new ArrayList<Skill>()));
        //employees.add(new Employee(103l,"Jean Doe",new ArrayList<Skill>()));
        Employee employee2 = new Employee("160","Pradeep Singh",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        employee2.setCollectiveTimeAgreement(getCTA(activities));
        employee2.setBaseCost(new BigDecimal(1.5));
        employee2.setWorkingTimeConstraints(getWTA());
        employee2.setPrevShiftsInfo(getPreShiftsInfo());
        employee2.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee2.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employees.add(employee2);

        Employee employee3 = new Employee("170","Arvind Das",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        employee3.setCollectiveTimeAgreement(getCTA(activities));
        employee3.setBaseCost(new BigDecimal(1.5));
        employee3.setWorkingTimeConstraints(getWTA());
        employee3.setPrevShiftsInfo(getPreShiftsInfo());
        employee3.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee3.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employees.add(employee3);

        Employee employee4 =new Employee("180","Ulrik",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        employee4.setCollectiveTimeAgreement(getCTA(activities));
        employee4.setBaseCost(new BigDecimal(1.5));
        employee4.setWorkingTimeConstraints(getWTA());
        employee4.setPrevShiftsInfo(getPreShiftsInfo());
        employee4.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee4.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employees.add(employee4);

        Employee employee5 = new Employee("190","Ramanuj",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        employee5.setCollectiveTimeAgreement(getCTA(activities));
        employee5.setBaseCost(new BigDecimal(1.5));
        employee5.setWorkingTimeConstraints(getWTA());
        employee5.setPrevShiftsInfo(getPreShiftsInfo());
        employee5.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee5.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employees.add(employee5);


        Employee employee6 = new Employee("195","Dravid",createSkillSet(), null,0,0,PaidOutFrequencyEnum.HOURLY,null);
        employee6.setCollectiveTimeAgreement(getCTA(activities));
        employee6.setBaseCost(new BigDecimal(1.5));
        employee6.setWorkingTimeConstraints(getWTA());
        employee6.setPrevShiftsInfo(getPreShiftsInfo());
        employee6.setPrevShiftStart(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(20));
        employee6.setPrevShiftEnd(new DateTime().withDayOfWeek(1).minusDays(1).withTimeAtStartOfDay().minusHours(10));
        employees.add(employee6);


        return employees;
    }

   /* private float[][] getCTA(){
        //0-1,1-2,2-3,3-4,4-5,5-6,6-7,7-8,8-9,9-10,10-11,11-12,12-13,13-14,14-15,15-16,16-17,17-18,18-19,19-20,20-21,21-22,22-23,23-0
        float cta[][] =
                        {{1.4f, 1.4f,  1.4f,  1.2f,  1,  1,  1,  1,  1,  1,    1.1f,    1,    1,    1,    1,    1,    1 ,   1,   1,     1,    1,    1,    2,   2},//Monday
                        {1.4f, 1.4f,  1.4f,  1.2f,  1,  1,  1,  1,  1,  1,    1,    1,    1,    1,    1,    1,    1 ,   1,   1,     1,    1,    1,    2,   2},//Tuesday
                        {1.2f, 1.2f,  1.3f,  1.6f,  1.2f,  1.2f,  1,  1,  1,  1,    1,    1,    1,    1,    1,    1,    1 ,   1,   1,     1,    1,    1,    2,   2},//Wednesday
                        {1.2f, 1.2f,  1.2f,  2,  1,  1,  1,  1,  1,  1,    1,    1,    1,    1,    1,    1,    1 ,   1,   1,     1,    1,    1,    2,   2},//Thursday
                        {1.3f, 1.3f,  1.3f,  2,  1,  1,  1,  1,  1,  1,    1,    1,    1,    1,    1,    1,    1 ,   1,   1,     1,    1,    1,    2,   2},//Friday
                        {1.3f, 1.3f,  1.4f,  1.2f,  1,  1,  1,  1,  1,  1,    1.2f,    1.2f,    1.2f,    1.2f,    1.2f,    2,    2 ,   2,   2,     2,    2,    2,    3,   3},//Satur
                        {1.3f, 1.4f,  1.4f,  2,  1,  1,  1,  1,  1,  1,    2,    2,    2,    2,    2,    2,    2 ,   2,   2,     2,    2,    2,    3,   3}};//Sun
        return cta;
    }*/


   public CollectiveTimeAgreement getCTA(List<Activity> activities){
        CollectiveTimeAgreement collectiveTimeAgreement = new CollectiveTimeAgreement();
        /*List<CTARuleTemplate> ctaRuleTemplates = new ArrayList<CTARuleTemplate>();
        ctaRuleTemplates.add(getWorkingEveningShift(activities));
        ctaRuleTemplates.add(getWorkingNightShift(activities));
        ctaRuleTemplates.add(getWorkingOnPublicHoliday(activities));
        ctaRuleTemplates.add(getWorkingOnHalfPublicHoliday(activities));
        ctaRuleTemplates.add(getWorkingOnSaturday(activities));
        ctaRuleTemplates.add(getWorkingOnSunday(activities));
       *//* WorkingExtraTimeCtaRuleTemplate workingExtraTime = new WorkingExtraTimeCtaRuleTemplate();
        collectiveTimeAgreement.setWorkingExtraTime(workingExtraTime);*//*
        collectiveTimeAgreement.setCtaRuleTemplates(ctaRuleTemplates);*/
       return collectiveTimeAgreement;
   }

   /*private CTARuleTemplate getWorkingEveningShift(List<Activity> activities){
       CTARuleTemplate workingEveningShift = new CTARuleTemplate();
       workingEveningShift.setCtaIntervals(Arrays.asList(new CTAInterval(new TimeInterval(1020,1380),CompensationType.MINUTES,new BigDecimal(10.5))));
       workingEveningShift.setId(101l);
       workingEveningShift.setRuleName("working Evening Shift");
       workingEveningShift.setPriority(1);
       workingEveningShift.setGranularity(30);
       workingEveningShift.setActivities(activities);
       workingEveningShift.setDays(new boolean[]{true,true,true,true,true,true,true});
       return workingEveningShift;
   }

   private CTARuleTemplate getWorkingNightShift(List<Activity> activities){
       CTARuleTemplate workingNightShift = new CTARuleTemplate();
       workingNightShift.setCtaIntervals(Arrays.asList(new CTAInterval(new TimeInterval(1380,420),CompensationType.MINUTES,new BigDecimal(10.5))));
       workingNightShift.setId(102l);
       workingNightShift.setRuleName("working night shift");
       workingNightShift.setPriority(2);
       workingNightShift.setGranularity(45);
       workingNightShift.setActivities(activities);
       workingNightShift.setDays(new boolean[]{true,true,true,true,true,true,true});
       return workingNightShift;
   }

    private CTARuleTemplate getWorkingOnPublicHoliday(List<Activity> activities){
        CTARuleTemplate workingOnPublicHoliday = new CTARuleTemplate();
        //workingOnPublicHoliday.setStartFrom(1380);
        //workingOnPublicHoliday.setEndTo(420);
        workingOnPublicHoliday.setActivities(activities);
        //workingOnPublicHoliday.setDays(Arrays.asList(1,2,3,4,5,6,7));
        workingOnPublicHoliday.setCtaIntervals(Arrays.asList(new CTAInterval(new TimeInterval(0,1440),CompensationType.MINUTES,new BigDecimal(10.5))));
        workingOnPublicHoliday.setId(103l);
        workingOnPublicHoliday.setRuleName("working on public holiday");
        workingOnPublicHoliday.setPriority(3);
        workingOnPublicHoliday.setGranularity(10);
        workingOnPublicHoliday.setHolidayDates(getPublicHolidayDates());
        return workingOnPublicHoliday;
    }


    private List<LocalDate> getPublicHolidayDates(){
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(new LocalDate(2017,12,11));
        localDates.add(new LocalDate(2017,12,16));
        localDates.add(new LocalDate(2017,12,25));
        localDates.add(new LocalDate(2017,12,10));
        return localDates;
    }

   private CTARuleTemplate getWorkingOnSaturday(List<Activity> activities){
       CTARuleTemplate workingOnSaturday = new CTARuleTemplate();
      // workingOnSaturday.setStartFrom(1380);
      // workingOnSaturday.setEndTo(420);
       workingOnSaturday.setCtaIntervals(Arrays.asList(new CTAInterval(new TimeInterval(0,1440),CompensationType.FIXED,new BigDecimal(20.5))));
       workingOnSaturday.setActivities(activities);
       workingOnSaturday.setId(105l);
       workingOnSaturday.setRuleName("working on saturday");
       workingOnSaturday.setDays(new boolean[]{false,false,false,false,false,true,false});
       workingOnSaturday.setPriority(5);
       workingOnSaturday.setGranularity(1);
       return workingOnSaturday;
   }

    private CTARuleTemplate getWorkingOnSunday(List<Activity> activities){
        CTARuleTemplate workingOnSunday = new CTARuleTemplate();
        // workingOnSaturday.setStartFrom(1380);
        // workingOnSaturday.setEndTo(420);
        workingOnSunday.setCtaIntervals(Arrays.asList(new CTAInterval(new TimeInterval(0,1440),CompensationType.PERCENTAGE,new BigDecimal(20.5))));
        workingOnSunday.setActivities(activities);
        workingOnSunday.setPriority(6);
        workingOnSunday.setRuleName("working on sunday");
        workingOnSunday.setId(106l);
        workingOnSunday.setGranularity(60);
        workingOnSunday.setDays(new boolean[]{false,false,false,false,false,false,true});
        return workingOnSunday;
    }


    private CTARuleTemplate getWorkingOnHalfPublicHoliday(List<Activity> activities){
        CTARuleTemplate workingOnHalfPublicHoliday = new CTARuleTemplate();
        workingOnHalfPublicHoliday.setCtaIntervals(Arrays.asList(new CTAInterval(new TimeInterval(720,1440),CompensationType.PERCENTAGE,new BigDecimal(20.5))));
        workingOnHalfPublicHoliday.setId(104l);
        workingOnHalfPublicHoliday.setRuleName("working on half public holiday");
        workingOnHalfPublicHoliday.setPriority(4);
        workingOnHalfPublicHoliday.setGranularity(15);
        workingOnHalfPublicHoliday.setActivities(activities);
        //workingOnHalfPublicHoliday.setDays(Arrays.asList(7));
        workingOnHalfPublicHoliday.setHolidayDates(getPublicHolidayDates());
        return workingOnHalfPublicHoliday;
    }*/



    /*private BigDecimal[][] getCTAByDayAndTime(){
     BigDecimal[][] costOfEmpByTime = new BigDecimal[7][24];
     for (int i=0;i<costOfEmpByTime.length;i++){
         for (int j=0;j<costOfEmpByTime[i].length;j++){
             if (i==6 || i==7){
                 if(j==0 || j==1 || j==2 || j==22 || j==23){
                     costOfEmpByTime[i][j] = new BigDecimal(4.33);
                     continue;
                 }else {
                     costOfEmpByTime[i][j] = new BigDecimal(2.33);
                     continue;
                 }
             }
             if(j==0 || j==1 || j==2 || j==22 || j==23){
                costOfEmpByTime[i][j] = new BigDecimal(1.51);
                 continue;
             }
             costOfEmpByTime[i][j] = new BigDecimal(1.00);
         }
     }
     return costOfEmpByTime;
    }*/



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
        //CareDayCheckWTATemplate careDayCheck;
        //  MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriod = new MaximumDaysOffInPeriodWTATemplate(,1,ScoreLevel.MEDIUM);
        //  MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYear = new MaximumSeniorDaysInYearWTATemplate(,1,ScoreLevel.MEDIUM);
        //MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriod = new MaximumVetoPerPeriodWTATemplate(,1,ScoreLevel.MEDIUM);

        Interval interval = new Interval(getPlanningWeekStart().toDateTimeAtStartOfDay(),getPlanningWeekStart().plusDays(7).toDateTimeAtStartOfDay());
        long nightStarts = new DateTime().withTimeAtStartOfDay().plusHours(20).getMinuteOfDay();
        long nightEnds = new DateTime().plusDays(1).withTimeAtStartOfDay().plusHours(3).getMinuteOfDay();
        MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTime = new MaximumAverageScheduledTimeWTATemplate(300,4,-10,ScoreLevel.MEDIUM,getPlanningWeekStart());
        maximumAverageScheduledTime.setInterval(interval);
        MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDays = new MaximumConsecutiveWorkingDaysWTATemplate(3,-5,ScoreLevel.MEDIUM);
        MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate(2,-6,ScoreLevel.MEDIUM,nightStarts,nightEnds);
        MaximumNightShiftLengthWTATemplate maximumNightShiftLength = new MaximumNightShiftLengthWTATemplate(600,-8,ScoreLevel.MEDIUM,nightStarts,nightEnds);
        MaximumNumberOfNightsWTATemplate maximumNumberOfNights = new MaximumNumberOfNightsWTATemplate(4,-6,ScoreLevel.MEDIUM,nightStarts,nightEnds);
        MaximumShiftLengthWTATemplate maximumShiftLength = new MaximumShiftLengthWTATemplate(600,-4,ScoreLevel.MEDIUM);
        MaximumShiftsInIntervalWTATemplate maximumShiftsInInterval = new MaximumShiftsInIntervalWTATemplate(6,-5,ScoreLevel.MEDIUM);
        maximumShiftsInInterval.setInterval(interval);
        MinimumConsecutiveNightsWTATemplate minimumConsecutiveNights = new MinimumConsecutiveNightsWTATemplate(2,-1,ScoreLevel.MEDIUM,nightStarts,nightEnds);
        MinimumDailyRestingTimeWTATemplateTemplate minimumDailyRestingTime = new MinimumDailyRestingTimeWTATemplateTemplate(660,-1,ScoreLevel.MEDIUM);
        MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShift = new MinimumDurationBetweenShiftWTATemplate(60,-1,ScoreLevel.MEDIUM);
        MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNights = new MinimumRestConsecutiveNightsWTATemplate(400,3,-1,ScoreLevel.MEDIUM,nightStarts,nightEnds);
        MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDays = new MinimumRestInConsecutiveDaysWTATemplate(200,4,-1,ScoreLevel.MEDIUM);
        MinimumShiftLengthWTATemplate minimumShiftLength = new MinimumShiftLengthWTATemplate(120,-1,ScoreLevel.MEDIUM);
        MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriod = new MinimumWeeklyRestPeriodWTATemplate(1200,-1,ScoreLevel.MEDIUM);
        minimumWeeklyRestPeriod.setInterval(interval);
        //NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriod = new NumberOfWeekendShiftInPeriodWTATemplate(2,-5,ScoreLevel.SOFT);
        NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriod = new NumberOfWeekendShiftInPeriodWTATemplate(2, 4,new LocalTime(14,0),0,new LocalTime(7,15),false,-5,ScoreLevel.MEDIUM,getPlanningWeekStart());
        ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRest = new ShortestAndAverageDailyRestWTATemplate(320,-5,ScoreLevel.MEDIUM);
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
        return DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate("11/12/2017");
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
        timeTypes[0]= new TimeType(UUID.randomUUID().toString(),"presence" );
        timeTypes[1]= new TimeType(UUID.randomUUID().toString(),"absence");
        return timeTypes;
    }
    private List<Activity> getActivities(){
        TimeType[] timeTypes= createTimeTypes();
        List<Activity> activityPlannerEntities = new ArrayList<>();
        Activity activity = new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet()),2,"Team A",timeTypes[0], 1,10, null);
        activity.setActivityConstraints(getActivityContraints());
        Activity activity2 =new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet2()),2,"Team B",timeTypes[0], 2,9, null);
        activity2.setActivityConstraints(getActivityContraints());
        Activity activity3 = new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet2()),2,"Day Off",timeTypes[1], 3,2, null);
        activity3.setActivityConstraints(getActivityContraints());
        Activity activity4 = new Activity(UUID.randomUUID().toString(),new ArrayList<>(createSkillSet2()),2, BLANK_ACTIVITY,timeTypes[0], 4,1, null);
        activity4.setActivityConstraints(getActivityContraints());
        activityPlannerEntities.add(activity);
        activityPlannerEntities.add(activity2);
        activityPlannerEntities.add(activity3);
        activityPlannerEntities.add(activity4);
        return activityPlannerEntities;
    }

    /*private List<String> getActivityIds(){
        List<String> ids = new ArrayList<>(2);
        getActivities().forEach(e->{
            ids.add(e.getId());
        });
        return ids;
    }*/



}