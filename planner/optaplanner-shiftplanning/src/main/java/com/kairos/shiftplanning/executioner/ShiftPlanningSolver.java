package com.kairos.shiftplanning.executioner;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staffing_level.SkillLineInterval;
import com.kairos.shiftplanning.dto.ShiftDTO;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.LocalDateConverter;
import com.kairos.shiftplanning.utils.LocalTimeConverter;
import com.kairos.shiftplanning.utils.ZonedDateTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.drools.dynamic.DynamicServiceRegistrySupplier;
import org.kie.api.KieServices;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.internal.utils.ServiceRegistryImpl;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.commons.utils.DateUtils.LOGGER;
import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.constraint.ConstraintSubType.*;
import static org.kie.api.internal.utils.ServiceUtil.instanceFromNames;

public class ShiftPlanningSolver {
    public static final String BASE_SRC = "src/main/resources/data/";
    public static final String STR = "\n------------------------\n";
    public static final String INFO = "info {}";
    public static final String SOLVER_XML = "com/kairos/shiftplanning/configuration/ShiftPlanning_Request_ActivityLine.solver.xml";
    public static final String ERROR = "Error {}";
    public static final String CONFIG_BREAKS = "com/kairos/shiftplanning/configuration/BreakAndIndirectActivityPlanning.solver.xml";
    public static final String CONFIG_WITH_WTA = "com/kairos/shiftplanning/configuration/ShiftPlanningRequest_activityLine_Wta.xml";
    public static String DROOL_FILE_PATH = new File("src").getAbsolutePath().replace("optaplanner-shiftplanning/src", "src/main/resources/droolsFile/Shift_Planning");
    boolean readFromFile=false;
    boolean disablePrimarySolver=false;
    boolean readSecondaryFromFile=false;
    boolean enableSecondarySolver=false;
    public static final String BENCH_MARKER_CONFIG = "com/kairos/shiftplanning/configuration/ShiftPlanningBenchmark.solver.xml";
    private static Logger log= LoggerFactory.getLogger(ShiftPlanningSolver.class);
    Solver<ShiftRequestPhasePlanningSolution> solver;
    SolverFactory<ShiftRequestPhasePlanningSolution> solverFactory;
    Solver<BreaksIndirectAndActivityPlanningSolution> solverBreaks;
    SolverFactory<BreaksIndirectAndActivityPlanningSolution> solverFactoryBreaks;
    public static String serverAddress;
    public static final String FIX_ACTIVITY_SHOULD_NOT_CHANGE = "Fix Activity should not change";
    public static final String IF_THIS_ACTIVITY_IS_USED_ON_A_TUESDAY = "If this activity is used on a Tuesday";
    public static final String MAX_SHIFT_OF_STAFF = "Max Shift of Staff";
    public static final String PRESENCE_AND_ABSENCE_SHOULD_NOT_BE_AT_SAME_TIME = "Presence And Absence should not be at same time";
    public static final String ACTIVITY_REQUIRED_TAG = "Activity required Tag";
    public static final String PREFER_PERMAMENT_EMPLOYEE = "Prefer Permament Employee";
    public static final String MINIMIZE_NO_OF_SHIFT_ON_WEEKEND = "Minimize No of Shift on weekend";
    public static final String MAX_NUMBER_OF_ALLOCATIONS_PR_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF = "Max number of allocations pr. shift for this activity per staff";
    public static final String SHORTEST_DURATION_FOR_THIS_ACTIVITY_RELATIVE_TO_SHIFT_LENGTH = "Shortest duration for this activity, relative to shift length";
    public static final String ACTIVITY_MUST_CONTINOUS_FOR_NUMBER_OF_HOURS_RELATIVE_TO_SHIFT_LENGTH = "Activity must continous for number of Hours, relative to shift length";
    static{
        System.setProperty("user.timezone", "UTC");
    }

    public static void main(String[] args){
        SolverConfigDTO solverConfigDTO = getSolverConfigDTO();
        String droolFilePath = "droolsFile/Shift_Planning";//"/home/droolsFile/Shift_Planning";
        String configurationFile = "/home/droolsFile/ShiftPlanning_Request_ActivityLine.solver.xml";
        ShiftPlanningSolver shiftPlanningSolver = new ShiftPlanningSolver(solverConfigDTO,droolFilePath,configurationFile);
        shiftPlanningSolver.runSolver();
        /*
        if(args.length==0){
            throw new RuntimeException("Please give the active profile");
        }
        updateServerAddress(args);
        * */
    }

    public static SolverConfigDTO getSolverConfigDTO(){
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO(ACTIVITY_MUST_CONTINOUS_FOR_NUMBER_OF_HOURS_RELATIVE_TO_SHIFT_LENGTH, ACTIVITY_MUST_CONTINOUS_FOR_NUMBER_OF_HOURS_RELATIVE_TO_SHIFT_LENGTH, ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO(SHORTEST_DURATION_FOR_THIS_ACTIVITY_RELATIVE_TO_SHIFT_LENGTH,SHORTEST_DURATION_FOR_THIS_ACTIVITY_RELATIVE_TO_SHIFT_LENGTH, ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO(MAX_NUMBER_OF_ALLOCATIONS_PR_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, MAX_NUMBER_OF_ALLOCATIONS_PR_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,  ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(MINIMIZE_NO_OF_SHIFT_ON_WEEKEND, MINIMIZE_NO_OF_SHIFT_ON_WEEKEND,  ConstraintType.ACTIVITY, MINIMIZE_SHIFT_ON_WEEKENDS, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(PREFER_PERMAMENT_EMPLOYEE, PREFER_PERMAMENT_EMPLOYEE,ConstraintType.ACTIVITY,PREFER_PERMANENT_EMPLOYEE, ScoreLevel.HARD,2,5l));
        constraintDTOS.add(new ConstraintDTO(ACTIVITY_REQUIRED_TAG, ACTIVITY_REQUIRED_TAG,  ConstraintType.ACTIVITY, ConstraintSubType.ACTIVITY_REQUIRED_TAG, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(PRESENCE_AND_ABSENCE_SHOULD_NOT_BE_AT_SAME_TIME, PRESENCE_AND_ABSENCE_SHOULD_NOT_BE_AT_SAME_TIME,  ConstraintType.UNIT, PRESENCE_AND_ABSENCE_SAME_TIME, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(MAX_SHIFT_OF_STAFF, MAX_SHIFT_OF_STAFF,  ConstraintType.ACTIVITY, ConstraintSubType.MAX_SHIFT_OF_STAFF, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(AVERAGE_SHEDULED_TIME.name(), AVERAGE_SHEDULED_TIME.name(),  ConstraintType.WTA, AVERAGE_SHEDULED_TIME, ScoreLevel.MEDIUM, 5,5l));
/*
        constraintDTOS.add(new ConstraintDTO(FIX_ACTIVITY_SHOULD_NOT_CHANGE, FIX_ACTIVITY_SHOULD_NOT_CHANGE,  ConstraintType.ACTIVITY, ConstraintSubType.FIX_ACTIVITY_SHOULD_NOT_CHANGE, ConstraintLevel.HARD, 5,5l));
*/
        constraintDTOS.add(new ConstraintDTO(IF_THIS_ACTIVITY_IS_USED_ON_A_TUESDAY, IF_THIS_ACTIVITY_IS_USED_ON_A_TUESDAY,  ConstraintType.ACTIVITY, ACTIVITY_VALID_DAYTYPE, ScoreLevel.SOFT, 4,5l));
        return new SolverConfigDTO(constraintDTOS);
    }

    private static void updateServerAddress(String[] args) {
        switch (args[0]){
            case "development":serverAddress="http://dev.kairosplanning.com/";
                break;
            case "qa":serverAddress="http://qa.kairosplanning.com/";
                break;
            case "production":serverAddress="http://app.kairosplanning.com/";
                break;
            default:throw new RuntimeException("Invalid profile");
        }
    }

    public ShiftPlanningSolver(SolverConfigDTO solverConfig,String droolFilePath, String configurationFile){
        droolFilePath = isNull(droolFilePath) ? DROOL_FILE_PATH : droolFilePath;
        List<File> droolsFiles = getDroolFilesByConstraints(solverConfig,droolFilePath);
        File solverConfigFile = new File(configurationFile);
        LOGGER.info("drool file count {} and path {}",droolsFiles.size(),droolFilePath);
        LOGGER.info("solver file exists {}",solverConfigFile.exists());
        solverFactory = isNull(configurationFile) ? SolverFactory.createFromXmlResource(configurationFile) : SolverFactory.createFromXmlFile(solverConfigFile);
        solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setScoreDrlFileList(droolsFiles);
        solver = solverFactory.buildSolver();
    }

    public ShiftRequestPhasePlanningSolution solveProblem(ShiftRequestPhasePlanningSolution planningProblem){
        planningProblem = solver.solve(planningProblem);
        return planningProblem;
    }
    public ShiftPlanningSolver(File solverConfigXml){
        solverFactory = SolverFactory.createFromXmlFile(solverConfigXml);
        solver = solverFactory.buildSolver();
    }

    private List<File> getDroolFilesByConstraints(SolverConfigDTO solverConfig, String droolFilePath){
        File[] drlFiles = new File(droolFilePath).listFiles();
        Map<String,File> fileMap = Stream.of(drlFiles).collect(Collectors.toMap(k->k.getName(), v->v));
        List<File> droolsFiles = new ArrayList<>();
        droolsFiles.add(fileMap.get("SHIFTPLANNING_BASE.drl"));
        for (ConstraintDTO constraintDTO : solverConfig.getConstraints()) {
            if(fileMap.containsKey(constraintDTO.getConstraintSubType().toString()+".drl")) {
                droolsFiles.add(fileMap.get(constraintDTO.getConstraintSubType().toString()+".drl"));
            }
        }
        return droolsFiles;
    }

    public void buildBenchmarker(){
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverFactory(solverFactory);
        PlannerBenchmark plannerBenchmark=benchmarkFactory.buildPlannerBenchmark(getUnsolvedSolution(readFromFile));
        plannerBenchmark.benchmark();

    }
    public void runBenchmarker(){
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(BENCH_MARKER_CONFIG);
        PlannerBenchmark plannerBenchmark=benchmarkFactory.buildPlannerBenchmark(getUnsolvedSolution(readFromFile));
        plannerBenchmark.benchmark();

    }

    public ShiftRequestPhasePlanningSolution runSolver() {
            Object[] solvedSolution = getSolution(null);
            printSolvedSolution(solvedSolution);
            printIndictment((Map<Object,Indictment>)solvedSolution[1]);
            return (ShiftRequestPhasePlanningSolution)solvedSolution[0];
    }

    public Object[] getSolution(ShiftRequestPhasePlanningSolution unsolvedSolution){
        if(unsolvedSolution==null) {
            unsolvedSolution = getUnsolvedSolution(readFromFile);
        }
        if(!readFromFile)
            toXml(unsolvedSolution,"shift_problem");
        long start=System.currentTimeMillis();
        ShiftRequestPhasePlanningSolution solution=disablePrimarySolver?unsolvedSolution:solver.solve(unsolvedSolution);
        ShiftPlanningUtility.printStaffingLevelMatrix(ShiftPlanningUtility.reduceStaffingLevelMatrix
                (solution.getStaffingLevelMatrix().getStaffingLevelMatrix(),solution.getShifts(),null,null,15),null);
        log.info("Solver took: {}",(System.currentTimeMillis()-start)/1000);
        if(!readFromFile)
            toXml(solution,"shift_solution");
        ScoreDirector director=solver.getScoreDirectorFactory().buildScoreDirector();
        director.setWorkingSolution(solution);
        if(enableSecondarySolver){
            BreaksIndirectAndActivityPlanningSolution secondarySolution=runAndGetBreaksSolution(readSecondaryFromFile?null:solution);
            BreaksIndirectAndActivityPlanningSolution solvedBreaksSolution=solverBreaks.solve(secondarySolution);
            printBreaksAndIndirectActivities(solvedBreaksSolution);
            ShiftPlanningUtility.printStaffingLevelMatrix(
                    ShiftPlanningUtility.reduceStaffingLevelMatrix(solvedBreaksSolution.getStaffingLevelMatrix().getStaffingLevelMatrix(),
                    solvedBreaksSolution.getShifts(),solvedBreaksSolution.getShiftBreaks(),solvedBreaksSolution.getIndirectActivities(),15),
                    ShiftPlanningUtility.reduceStaffingLevelMatrix
                            (solvedBreaksSolution.getStaffingLevelMatrix().getStaffingLevelMatrix(),solvedBreaksSolution.getShifts(),solvedBreaksSolution.getShiftBreaks(),null,15));
            if(!readSecondaryFromFile)
                toXml(solvedBreaksSolution,"shift_solution_secondary");
        }
        return new Object[]{solution,director.getIndictmentMap()};//,director.getConstraintMatchTotals()
    }

    private void printBreaksAndIndirectActivities(BreaksIndirectAndActivityPlanningSolution solvedBreaksSolution) {
        StringBuilder sb = new StringBuilder();
        solvedBreaksSolution.getShifts().forEach(s->{
            sb.append("\n"+"shift:"+s.getId()+","+s.getInterval()+","+s.getEmployee().getName());
            sb.append(solvedBreaksSolution.getShiftBreaks().stream().filter(sbrk->sbrk.getShift().getId().equals(s.getId())).sorted(Comparator.comparing(ShiftBreak::getOrder)).
                    map(sbrk->ShiftPlanningUtility.getIntervalAsString(sbrk.getInterval())).collect(Collectors.toList()).toString());
        });
        sb.append("\n");
        solvedBreaksSolution.getIndirectActivities().
                forEach(ia-> sb.append("["+ia.getEmployees().stream().map(e->e.getName()).collect(Collectors.toList())+":"+ia.getStartTime()+"]"));
        log.info(INFO,sb);
    }

    private BreaksIndirectAndActivityPlanningSolution runAndGetBreaksSolution(ShiftRequestPhasePlanningSolution solution) {
        if(solution==null)
            return new ShiftPlanningGenerator().loadUnsolvedBreakAndIndirectActivityPlanningSolution(BASE_SRC+"shift_solution_secondary.xml");
        return new ShiftPlanningGenerator().loadUnsolvedBreakAndIndirectActivityPlanningSolution(solution);
    }

    public ShiftRequestPhasePlanningSolution runSolverOnRequest(ShiftRequestPhasePlanningSolution unSolvedsolution) {
        try {
            Object[] solvedSolution = getSolution(unSolvedsolution);
            printSolvedSolution(solvedSolution);
            printIndictment((Map<Object,Indictment>)solvedSolution[1]);
            sendSolutionToKairos((ShiftRequestPhasePlanningSolution)solvedSolution[0]);
            return (ShiftRequestPhasePlanningSolution)solvedSolution[0];
        } catch (Exception e) {
            log.error(ERROR,e.getMessage());
            return null;
        }
    }

    private void printIndictment(Map<Object,Indictment> indictmentMap) {
        log.info("*************Indictment**************");
        MutableInt unassignedIntervals=new MutableInt(0);
        indictmentMap.forEach((entity,indictment)->{
            if(entity instanceof ShiftImp && !((ShiftImp) entity).isLocked() && ((ShiftImp) entity).getInterval()!=null) {
                printInctmentMapOfShift((ShiftImp) entity, indictment);
            }else if(entity instanceof ActivityLineInterval ) {
                printInctmentMapOfActivityLineInterval(unassignedIntervals, entity, indictment);
            }else if(entity instanceof Activity) {
                printInctmentMapOfActivity(entity, indictment);
            }else if(entity instanceof Employee) {
                printInctmentMapOfEmployee(entity, indictment);
            }
        });
        log.info("unassignedIntervals: {}",unassignedIntervals);
        log.info("*************Indictment End**************");
    }

    private void printInctmentMapOfShift(ShiftImp entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);
        sb.append(getShiftPlanInfo(entity)+"\n");
        MutableBoolean any=new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                return;
            }
            any.setTrue();
            sb.append(constraintMatch.getConstraintName()+"--"+constraintMatch.getScore().toString()+"\n");
        });
        if(any.isTrue()) {
            log.info(INFO,sb);
        }
    }

    private void printInctmentMapOfActivityLineInterval(MutableInt unassignedIntervals, Object entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);
        if(((ActivityLineInterval)entity).getShift()==null){
            unassignedIntervals.increment();
            return;
        }

        sb.append(entity.toString()+"--"+((ActivityLineInterval)entity).getShift()+"\n");
        MutableBoolean any=new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                return;
            }
            any.setTrue();
            sb.append("-------------"+constraintMatch.getConstraintName()+"----------"+constraintMatch.getScore().toString()+"\n");
        });
        if(any.isTrue()) {
            log.info(INFO,sb);
        }
    }

    private void printInctmentMapOfEmployee(Object entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);
        sb.append(entity.toString()+"---\n");
        MutableBoolean any=new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                return;
            }
            any.setTrue();
            sb.append("------"+constraintMatch.getConstraintName()+"-------"+constraintMatch.getScore().toString()+"\n");
        });
        if(any.isTrue()) {
            log.info(INFO,sb);
        }
    }

    private void printInctmentMapOfActivity(Object entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);

        sb.append(entity.toString()+"---\n");
        MutableBoolean any=new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                return;
            }
            any.setTrue();
            sb.append("------"+constraintMatch.getConstraintName()+"--------"+constraintMatch.getScore().toString()+"\n");
        });
        if(any.isTrue()) {
            log.info(INFO,sb);
        }
    }

    private String getShiftPlanInfo(Shift shift){
        return ""+shift.getStart().format(DateTimeFormatter.ofPattern("dd/MM-HH:mm"))+"--"+shift.getEnd().format(DateTimeFormatter.ofPattern("dd/MM-HH:mm"));
    }

    private void printSolvedSolution(Object[] output) {
        ShiftRequestPhasePlanningSolution solution = (ShiftRequestPhasePlanningSolution) output[0];
		log.info("-------Printing solution:-------");
        log.info("total intervals: {}",solution.getActivityLineIntervals().stream().count());
        log.info("total assigned intervals: {}",solution.getActivityLineIntervals().stream().filter(i->i.getShift()!=null).count());
        solution.getEmployees().forEach(emp->
            solution.getShifts().forEach(shift -> {
                if(!emp.getId().equals(shift.getEmployee().getId())){
                    return;
                }
                log.info("Shift A--------"+shift.getId()+","+shift.getEmployee().getId()+","+shift.getStartDate()+":["+shift.getInterval()+"("+shift.getShiftActivities().size()+")"+"]:"+shift.getShiftActivities()+
                        "["+Optional.ofNullable(shift.getBreaks()).orElse(Collections.emptyList()).stream().collect(StringBuilder::new ,(b1,b2)-> b1.append(b2.toString()),(b1,b2)->b2.append(",").append(b1))+"]");
            })
        );
        Map<ShiftImp,List<SkillLineInterval>> shiftsAssignedToSkillIntervals= new HashMap<>();
        solution.getSkillLineIntervals().stream().forEach(skillLineInterval -> {
            if(skillLineInterval.getShift()==null) return;
            if(shiftsAssignedToSkillIntervals.containsKey(skillLineInterval.getShift())){
                shiftsAssignedToSkillIntervals.get(skillLineInterval.getShift()).add(skillLineInterval);
            }else{
                shiftsAssignedToSkillIntervals.put(skillLineInterval.getShift(),new ArrayList<>());
            }
        });
        log.info("-------Printing solution Finished:-------");
    }

	private ShiftRequestPhasePlanningSolution getUnsolvedSolution(boolean loadFromFile) {
        ShiftRequestPhasePlanningSolution unsolvedSolution=null;
        if(loadFromFile){
            unsolvedSolution=new ShiftPlanningGenerator().loadUnsolvedSolutionFromXML(BASE_SRC+"shift_solution.xml");
        }else{
            unsolvedSolution=new ShiftPlanningGenerator().loadUnsolvedSolution();
        }
        return unsolvedSolution;
    }



    public void sendSolutionToKairos(ShiftRequestPhasePlanningSolution solvedSolution){
        List<ShiftDTO> shiftDTOS = getShift(solvedSolution.getShifts());
        ShiftPlanningUtility.solvedShiftPlanningProblem(shiftDTOS,solvedSolution.getUnit().getId());
    }

    private List<ShiftDTO> getShift(List<ShiftImp> shiftImp){
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shiftImp.size());
        shiftImp.forEach(s->{
            ShiftDTO shiftDTO = new ShiftDTO(asDate(s.getStart()),asDate(s.getEnd()),BigInteger.valueOf(320l),95l,1005l);
            shiftDTO.setUnitEmploymentPositionId(12431l);
            if(s.getActivityLineIntervals().size()>1) {
                shiftDTO.setSubShifts(getSubShift(s));
            }
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    private List<ShiftDTO> getSubShift(ShiftImp shift){
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        shift.getActivityLineIntervals().sort(Comparator.comparing(ActivityLineInterval::getStart));
        List<ActivityLineInterval> alis = getMergedALIs(shift.getActivityLineIntervals());
        if(alis.size()==1) return new ArrayList<>();
        alis.forEach(a->{
            ShiftDTO shiftDTO = new ShiftDTO(asDate(a.getStart().minusHours(5).minusMinutes(30)),asDate(a.getEnd().minusHours(5).minusMinutes(30)),BigInteger.valueOf(375),95l,1005l);
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    private List<ActivityLineInterval> getMergedALIs(List<ActivityLineInterval> intervals){
        List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
        ActivityLineInterval activityLineInterval = intervals.get(0);
        for (ActivityLineInterval ali:intervals.subList(1,intervals.size()-1)) {
            if (activityLineInterval.getEnd().equals(ali.getStart()) && activityLineInterval.getActivity().getId().equals(ali.getActivity().getId())) {
                activityLineInterval.setDuration(activityLineInterval.getDuration()+ali.getDuration());
            }else{
                activityLineIntervals.add(activityLineInterval);
                activityLineInterval = ali;
            }
        }
        activityLineInterval.setDuration(activityLineInterval.getDuration()+15);
        activityLineIntervals.add(activityLineInterval);
        return activityLineIntervals;
    }
    public static void toXml(Object solution, String fileName) {
        try {
            XStream xstream = new XStream(new PureJavaReflectionProvider());
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.registerConverter(new ZonedDateTimeConverter());
            xstream.registerConverter(new LocalTimeConverter());
            xstream.registerConverter(new LocalDateConverter());
            xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
            String xmlString = xstream.toXML(solution);
            writeXml(xmlString, fileName);
        }catch(Exception e){
            log.error(ERROR,e.getMessage());
        }
    }
    public static  void writeXml(String xmlString,String fileName){
        try(PrintWriter out = new PrintWriter(new File("" +fileName+".xml"))){
            out.write(xmlString);
        } catch (FileNotFoundException e) {
            log.error(ERROR,e.getMessage());
        }
    }
}
