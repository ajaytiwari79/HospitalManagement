package com.kairos.shiftplanning.executioner;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.shiftplanning.domain.*;
import com.kairos.shiftplanning.dto.ShiftDTO;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.JodaLocalDateConverter;
import com.kairos.shiftplanning.utils.JodaLocalTimeConverter;
import com.kairos.shiftplanning.utils.JodaTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftPlanningSolver {
    public static final String BASE_SRC = "src/main/resources/data/";
    public static String config2 = "com/kairos/shiftplanning/configuration/ShiftPlanning_Request_ActivityLine.solver.xml";
    public static String config_breaks = "com/kairos/shiftplanning/configuration/BreakAndIndirectActivityPlanning.solver.xml";
    public static String configWithWTA = "com/kairos/shiftplanning/configuration/ShiftPlanningRequest_activityLine_Wta.xml";
    boolean readFromFile=false;
    boolean disablePrimarySolver=false;
    boolean readSecondaryFromFile=false;
    boolean enableSecondarySolver=false;
    public static String benchMarkerConfig = "com/kairos/shiftplanning/configuration/ShiftPlanningBenchmark.solver.xml";
    private static Logger log= LoggerFactory.getLogger(ShiftPlanningSolver.class);
    Solver<ShiftRequestPhasePlanningSolution> solver;
    SolverFactory<ShiftRequestPhasePlanningSolution> solverFactory;
    Solver<BreaksIndirectAndActivityPlanningSolution> solverBreaks;
    SolverFactory<BreaksIndirectAndActivityPlanningSolution> solverFactoryBreaks;

    static{
        System.setProperty("user.timezone", "UTC");
    }
    public ShiftPlanningSolver(SolverConfigDTO solverConfig){
        List<File> droolsFiles = getDroolFilesByConstraints(solverConfig);
        solverFactory = SolverFactory.createFromXmlResource(config2);
        solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setScoreDrlFileList(droolsFiles);
       // solverFactory.getSolverConfig().setEnvironmentMode(EnvironmentMode.FULL_ASSERT);
        solver = solverFactory.buildSolver();
        //solverFactoryBreaks = SolverFactory.createFromXmlResource(config_breaks);
        //solverBreaks = solverFactoryBreaks.buildSolver();
    }
    public ShiftPlanningSolver(File solverConfigXml){
        solverFactory = SolverFactory.createFromXmlFile(solverConfigXml);
        solver = solverFactory.buildSolver();
    }

    private List<File> getDroolFilesByConstraints(SolverConfigDTO solverConfig){
        File[] drlFiles = new File("/media/pradeep/bak/kairos/kairos-user/planner/src/main/resources/droolsFile/Shift_Planning").listFiles();
        Map<String,File> fileMap = Arrays.asList(drlFiles).stream().collect(Collectors.toMap(k->k.getName(),v->v));
        List<File> droolsFiles = new ArrayList<>();
        droolsFiles.add(fileMap.get("SHIFTPLANNING_BASE.drl"));
        for (ConstraintDTO constraintDTO : solverConfig.getConstraints()) {
            if(fileMap.containsKey(constraintDTO.getConstraintSubType().toString()+".drl")) {
                droolsFiles.add(fileMap.get(constraintDTO.getConstraintSubType().toString()+".drl"));
            }
        }
        return droolsFiles;
    }

    public ShiftPlanningSolver(){
        solverFactory = SolverFactory.createFromXmlResource(config2);
        solverFactory.getSolverConfig().setMoveThreadCount(String.valueOf(4));
        solver = solverFactory.buildSolver();
        solverFactoryBreaks = SolverFactory.createFromXmlResource(config_breaks);
        solverBreaks = solverFactoryBreaks.buildSolver();
    }
    public void buildBenchmarker(){
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverFactory(solverFactory);
        PlannerBenchmark plannerBenchmark=benchmarkFactory.buildPlannerBenchmark(getUnsolvedSolution(readFromFile));
        plannerBenchmark.benchmark();

    }
    public void runBenchmarker(){
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(benchMarkerConfig);
        PlannerBenchmark plannerBenchmark=benchmarkFactory.buildPlannerBenchmark(getUnsolvedSolution(readFromFile));
        plannerBenchmark.benchmark();

    }

    public static void main(String[] s ){
        new ShiftPlanningSolver().runSolver();
    }


    public ShiftRequestPhasePlanningSolution runSolver() {
        try {
            Object[] solvedSolution = getSolution(null);
            printSolvedSolution(solvedSolution);
            printIndictment((Map<Object,Indictment>)solvedSolution[1]);
            return (ShiftRequestPhasePlanningSolution)solvedSolution[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Object[] getSolution(ShiftRequestPhasePlanningSolution unsolvedSolution) throws Exception{
        if(unsolvedSolution==null) {
            unsolvedSolution = getUnsolvedSolution(readFromFile);
        }
        //toXml(unsolvedSolution,"shift_problem");
        if(!readFromFile)
            toXml(unsolvedSolution,"shift_problem");
        long start=System.currentTimeMillis();
        ShiftRequestPhasePlanningSolution solution=disablePrimarySolver?unsolvedSolution:solver.solve(unsolvedSolution);
        ShiftPlanningUtility.printStaffingLevelMatrix(ShiftPlanningUtility.reduceStaffingLevelMatrix
                (solution.getStaffingLevelMatrix().getStaffingLevelMatrix(),solution.getShifts(),null,null,15),null);
        log.info("Solver took:"+(System.currentTimeMillis()-start)/1000);
        if(!readFromFile)
            toXml(solution,"shift_solution");
        ScoreDirector director=solver.getScoreDirectorFactory().buildScoreDirector();
        director.setWorkingSolution(solution);
        //sendSolutionToKairos(solution);
        //log.info("AFTER SOLVER:"+ ShiftPlanningUtility.SEQUENCE);
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
            sb.append("\n"+"shift:"+s.getPrettyId()+","+s.getInterval()+","+s.getEmployee().getName());
            sb.append(solvedBreaksSolution.getShiftBreaks().stream().filter(sbrk->sbrk.getShift().getId().equals(s.getId())).sorted(Comparator.comparing(ShiftBreak::getOrder)).
                    map(sbrk->ShiftPlanningUtility.getIntervalAsString(sbrk.getInterval())).collect(Collectors.toList()).toString());
        });
        sb.append("\n");
        solvedBreaksSolution.getIndirectActivities().
                forEach(ia-> sb.append("["+ia.getEmployees().stream().map(e->e.getName()).collect(Collectors.toList())+":"+ia.getStartTime()+"]"));
        log.info(sb.toString());
    }

    private void processShifts(ShiftRequestPhasePlanningSolution solution) {
        solution.getShifts().forEach(s->{
            ListIterator<ActivityLineInterval> it = s.getActivityLineIntervals().listIterator();
            while (it.hasNext()){
                ActivityLineInterval found=null;
                ActivityLineInterval temp=it.next();
                for(ActivityLineInterval ali: solution.getActivityLineIntervals()){
                    if(ali.getId().equals(temp.getId()) && ali.getShift()==null){
                        it.remove();
                        break;
                    }
                }
            }
        });
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
            e.printStackTrace();
            return null;
        }
    }

    private void printIndictment(Map<Object,Indictment> indictmentMap) {
        log.info("*************Indictment**************");
        MutableInt unassignedIntervals=new MutableInt(0);
        indictmentMap.forEach((entity,indictment)->{
            if(entity instanceof ShiftRequestPhase && !((ShiftRequestPhase) entity).isLocked() && ((ShiftRequestPhase) entity).getInterval()!=null) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n------------------------\n");
                sb.append(getShiftPlanInfo((ShiftRequestPhase) entity)+"\n");
                MutableBoolean any=new MutableBoolean(false);
                indictment.getConstraintMatchSet().forEach(constraintMatch -> {
                    if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                        return;
                    }
                    any.setTrue();
                    sb.append("------"+constraintMatch.getConstraintName()+"-------"+constraintMatch.getScore().toString()+"\n");
                });
                if(any.isTrue())
                    log.info(sb.toString());
            }else if(entity instanceof ActivityLineInterval ) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n------------------------\n");
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
                    sb.append("------"+constraintMatch.getConstraintName()+"-------"+constraintMatch.getScore().toString()+"\n");
                });
                if(any.isTrue())
                    log.info(sb.toString());
            }else if(entity instanceof Activity) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n------------------------\n");

                sb.append(entity.toString()+"---\n");
                MutableBoolean any=new MutableBoolean(false);
                indictment.getConstraintMatchSet().forEach(constraintMatch -> {
                    if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                        return;
                    }
                    any.setTrue();
                    sb.append("------"+constraintMatch.getConstraintName()+"-------"+constraintMatch.getScore().toString()+"\n");
                });
                if(any.isTrue())
                    log.info(sb.toString());
            }else if(entity instanceof Employee) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n------------------------\n");
                sb.append(entity.toString()+"---\n");
                MutableBoolean any=new MutableBoolean(false);
                indictment.getConstraintMatchSet().forEach(constraintMatch -> {
                    if(((HardMediumSoftLongScore)constraintMatch.getScore()).getHardScore()==0 &&((HardMediumSoftLongScore)constraintMatch.getScore()).getMediumScore()==0){
                        return;
                    }
                    any.setTrue();
                    sb.append("------"+constraintMatch.getConstraintName()+"-------"+constraintMatch.getScore().toString()+"\n");
                });
                if(any.isTrue())
                log.info(sb.toString());
            }else{
                //log.info("------------------------");
                //log.info(entity.toString()+"-----------"+indictment.toString());
            }
        });
        log.info("unassignedIntervals:"+unassignedIntervals);
        log.info("*************Indictment End**************");
    }
    private String getShiftPlanInfo(Shift shift){
        return ""+shift.getStart().toString("dd/MM-HH:mm")+"--"+shift.getEnd().toString("dd/MM-HH:mm");
    }

    private void printSolvedSolution(Object[] output) {
        ShiftRequestPhasePlanningSolution solution = (ShiftRequestPhasePlanningSolution) output[0];
		log.info("-------Printing solution:-------");
        log.info("total intervals:"+solution.getActivityLineIntervals().stream().count());
        log.info("total assigned intervals:"+solution.getActivityLineIntervals().stream().filter(i->i.getShift()!=null).count());
		/*Map<ShiftRequestPhase,List<ActivityLineInterval>> shiftsAssignedToActivityIntervals= new HashMap<>();
        solution.getActivityLineIntervalsList().stream().forEach(activityLineInterval -> {
            if(activityLineInterval.getShift()==null) return;
            if(shiftsAssignedToActivityIntervals.containsKey(activityLineInterval.getShift())){
                shiftsAssignedToActivityIntervals.get(activityLineInterval.getShift()).add(activityLineInterval);
            }else{
                shiftsAssignedToActivityIntervals.put(activityLineInterval.getShift(),new ArrayList<>());
            }
        });
        shiftsAssignedToActivityIntervals.forEach((shift,activityLineIntervals)->{
            log.info("Shift A--------"+shift.getId().toString()+":["+shift.getInterval()+"]:"+getMergedInterval(activityLineIntervals.stream().map(i->i.getInterval()).sorted((i1,i2)->i1.getStart().compareTo(i2.getStart())).collect(Collectors.toList())) );
        });*/
        solution.getEmployees().forEach(emp->{
            solution.getShifts().forEach(shift -> {
                if(!emp.getId().equals(shift.getEmployee().getId())){
                    return;
                }
                log.info("Shift A--------"+shift.getPrettyId().toString()+","+shift.getEmployee().getId()+","+shift.getDate()+":["+shift.getInterval()+"("+shift.getActivityLineIntervals().size()+")"+"]:"+ShiftPlanningUtility.getMergedInterval(solution.getActivityLineIntervals().stream().filter(ali->Objects.equals(ali.getShift(),shift)).collect(Collectors.toList()))+
                        "["+Optional.ofNullable(shift.getBreaks()).orElse(Collections.emptyList()).stream().collect(StringBuilder::new ,(b1,b2)-> b1.append(b2.toString()),(b1,b2)->b2.append(",").append(b1))+"]");
            });
        });

        //printStaffingLines(solution.getActivityLineIntervalsList());
        Map<ShiftRequestPhase,List<SkillLineInterval>> shiftsAssignedToSkillIntervals= new HashMap<>();
        solution.getSkillLineIntervals().stream().forEach(skillLineInterval -> {
            if(skillLineInterval.getShift()==null) return;
            if(shiftsAssignedToSkillIntervals.containsKey(skillLineInterval.getShift())){
                shiftsAssignedToSkillIntervals.get(skillLineInterval.getShift()).add(skillLineInterval);
            }else{
                shiftsAssignedToSkillIntervals.put(skillLineInterval.getShift(),new ArrayList<>());
            }
        });
        /*shiftsAssignedToSkillIntervals.forEach((shift,v)->{
            log.info("Shift S--------"+shift.getId().toString()+":["+shift.getInterval()+"]:"+getMergedInterval(v.stream().map(i->i.getInterval()).sorted((i1,i2)->i1.getStart().compareTo(i2.getStart())).collect(Collectors.toList())) );
        });*/
        log.info("-------Printing solution Finished:-------");
    }

	private void printStaffingLines(List<ActivityLineInterval> activityLineIntervals) {
        ///activityLineIntervals.sort(Comparator.comparing(a -> a.getStart()).thenComparing(ActivityLineInterval::getStart));
        activityLineIntervals.sort(Comparator.comparing(( ActivityLineInterval a) -> a.getStart().toLocalDate())
                .thenComparing(a -> a.getActivity().getName())
                .thenComparingInt(a -> a.getStaffNo())
                .thenComparing(a -> a.getStart().toLocalTime()));//This doesnt
        activityLineIntervals.sort(Comparator.comparing(a -> a.getActivity().getName())); //This does
        for(ActivityLineInterval activityLineInterval:activityLineIntervals){
            log.info(activityLineInterval.toString()+"-------"+activityLineInterval.getShift());
        }
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
    private void assignEmployeeToAvaiability(List<Employee> employeeList, boolean assign) {
		/*employeeList.forEach(employee->{
			employee.getAvailabilityList().forEach(avail->{
				avail.setEmployee(assign?employee:null);
			});
		});*/

    }



    public void sendSolutionToKairos(ShiftRequestPhasePlanningSolution solvedSolution){
        List<ShiftDTO> shiftDTOS = getShift(solvedSolution.getShifts());
        ShiftPlanningUtility.solvedShiftPlanningProblem(shiftDTOS,solvedSolution.getUnitId());
    }

    private List<ShiftDTO> getShift(List<ShiftRequestPhase> shiftRequestPhase){
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shiftRequestPhase.size());
        shiftRequestPhase.forEach(s->{
            //s.getActivityLineIntervals().get(0).getActivity().getId()
            ShiftDTO shiftDTO = new ShiftDTO(s.getStart().toDate(),s.getEnd().toDate(),new BigInteger("320"),95l,1005l);//Long.valueOf(s.getEmployee().getId()));
            shiftDTO.setUnitEmploymentPositionId(12431l);
            if(s.getActivityLineIntervals().size()>1) {
                shiftDTO.setSubShifts(getSubShift(s, 95l));
            }
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    private List<ShiftDTO> getSubShift(ShiftRequestPhase shift,Long unitId){
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        shift.getActivityLineIntervals().sort(Comparator.comparing(ActivityLineInterval::getStart));
       // int activityCount = getActivityCount(shift.getActivityLineIntervals());
       // if(activityCount==1) return null;
        List<ActivityLineInterval> alis = getMergedALIs(shift.getActivityLineIntervals());
        if(alis.size()==1) return null;
        alis.forEach(a->{
            //a.getActivity().getId()
            ShiftDTO shiftDTO = new ShiftDTO(a.getStart().minusHours(5).minusMinutes(30).toDate(),a.getEnd().minusHours(5).minusMinutes(30).toDate(),new BigInteger("375"),95l,1005l);//Long.valueOf(shift.getEmployee().getId()));
            //shiftDTO.setSubShifts(getSubShift(shift,95l));
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    private int getActivityCount(List<ActivityLineInterval> intervals){
        Set<Activity> activities = new HashSet<>();
        intervals.forEach(activityLineInterval -> {
            activities.add(activityLineInterval.getActivity());
        });
        return activities.size();
    }

    private List<ActivityLineInterval> getMergedALIs(List<ActivityLineInterval> intervals){
        //String activityId = intervals.get(0).getActivity().getId();
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
            //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.registerConverter(new JodaTimeConverter());
            xstream.registerConverter(new JodaLocalTimeConverter());
            xstream.registerConverter(new JodaLocalDateConverter());
            // xstream.registerConverter(new JodaTimeConverterNoTZ());
            xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
            String xmlString = xstream.toXML(solution);
            writeXml(xmlString, fileName);
        }catch(Throwable e){
            log.error("soe:",e);
            throw e;
        }
    }
    public static  void writeXml(String xmlString,String fileName){
        PrintWriter out = null;
        try {
            out = new PrintWriter(new File("" +fileName+".xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.write(xmlString);
        out.close();
    }
}
