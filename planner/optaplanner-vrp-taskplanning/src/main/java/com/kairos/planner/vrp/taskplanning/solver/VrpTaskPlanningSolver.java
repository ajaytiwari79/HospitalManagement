package com.kairos.planner.vrp.taskplanning.solver;

import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.kairos.planner.vrp.taskplanning.util.VrpPlanningUtil;
import com.thoughtworks.xstream.XStream;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VrpTaskPlanningSolver {
    //public static String config = "src/main/resources/config/Kamstrup_Vrp_taskPlanning.solver.xml";
    public static String config = "src/main/resources/config/Kamstrup_Vrp_taskPlanning.solver.xml";
    public static String defaultDrl = "optaplanner-vrp-taskplanning/src/main/resources/drl/vrp_task_rules.drl";
    public static String config_on_request = "/opt/kairos/kairos-user/planner/optaplanner-vrp-taskplanning/src/main/resources/config/configuration_for_request.xml";
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    Solver<VrpTaskPlanningSolution> solver;
    SolverFactory<VrpTaskPlanningSolution> solverFactory;


    public VrpTaskPlanningSolver(){
        solverFactory = SolverFactory.createFromXmlFile(new File(config));
        solver = solverFactory.buildSolver();
    }


    //public VrpTaskPlanningSolver(List<File> drlFileList){
      //  solverFactory = SolverFactory.createFromXmlFile(new File(config_on_request));
        //solverFactory = SolverFactory.createFromXmlFile(new File(config));
        //solverFactory = SolverFactory.createFromXmlResource("config/Kamstrup_Vrp_taskPlanning.solver.xml");
    public VrpTaskPlanningSolver(List<File> drlFileList, String vrpXmlFilePath, int terminationTime, int numberOfThread){
        if(numberOfThread<=0 || numberOfThread>=40){
            throw new IllegalArgumentException("Invalid threads provided, please provide a sane number."+numberOfThread);
        }
        solverFactory = SolverFactory.createFromXmlFile(new File(vrpXmlFilePath));
        if(drlFileList!=null && !drlFileList.isEmpty()){
            log.info("no of drool files"+drlFileList.size());
            solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setScoreDrlFileList(drlFileList);
            solverFactory.getSolverConfig().setTerminationConfig(new TerminationConfig().withMinutesSpentLimit((long)terminationTime));
            //solverFactory.getSolverConfig().setMoveThreadCount(String.valueOf(numberOfThread));
        }
        solver = solverFactory.buildSolver();
    }

    public void solve(String problemXML,boolean addBreaks) throws IOException {
        XStream xstream = getxStream();
        VrpTaskPlanningSolution problem=(VrpTaskPlanningSolution) xstream.fromXML(new File(problemXML));
        solve(problem,addBreaks);
    }

    public XStream getxStream() {
        XStream xstream= new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.processAnnotations(LocationPair.class);
        xstream.processAnnotations(LocationPairDifference.class);
        xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
        return xstream;
    }

    public void solve(VrpTaskPlanningSolution problem,boolean addBreaks) throws IOException {
        problem.getTasks().stream().map(t->t.getCity()+"---"+t.getStreetName()).collect(Collectors.toSet()).forEach(b->{
            log.info("city---street:     "+b);
        });
        printProblemInfo(problem);
        if(addBreaks)
        addBreaks(problem);
        AtomicInteger at=new AtomicInteger(0);
        problem.getTasks().forEach(t->{
            at.addAndGet(t.getDuration());
            t.setLocationsDistanceMatrix(problem.getLocationsDistanceMatrix());
            t.setLocationsRouteMatrix(problem.getLocationsRouteMatrix());
        });
        //TODO ease efficiency for debugging
        //problem.getEmployees().forEach(e->e.setEfficiency(100));
        log.info("Number of tasks:"+problem.getTasks().size());
        LocationPair locationPair = new ArrayList<LocationPair>(problem.getLocationsDistanceMatrix().getTable().keySet()).get(2);
        LocationPairDifference locationsDifference = problem.getLocationsDistanceMatrix().getLocationsDifference(locationPair);
        LocationPairDifference locationsDifference2 = problem.getLocationsDistanceMatrix().getLocationsDifference(locationPair.getReversePair());


        //LocationPair locationPair2 = new LocationPair(56.462275d,10.034527d,56.46226953d,10.03495581d);
        LocationPair locationPair2 = new LocationPair(56.46219053d,10.03474252d,56.46226953d,10.03495581d);
        Boolean right= problem.getLocationsRouteMatrix().checkIfRightSideArrival(locationPair2);
        VrpTaskPlanningSolution solution=null;
        try {
            solution = solver.solve(problem);

        }catch (Exception e){
            //e.printStackTrace();
            throw  e;
        }
        getxStream().toXML(solution,new FileWriter("src/main/resources/solution.xml"));
        printSolutionInformation( solution);

    }

    private void printSolutionInformation(VrpTaskPlanningSolution solution) {

        log.info("---------------Printing SOlution Information.-----");
        int totalDrivingTime=0;
        StringBuilder sbs= new StringBuilder("Locs data:\n");
        StringBuilder sbTom= new StringBuilder("Locs data for tomtom:\n");
        StringBuilder shiftChainInfo= new StringBuilder("Shift chain data:\n");
        for(Shift shift: solution.getShifts()){
            StringBuffer sb= new StringBuffer(shift+":::"+(shift.getNumberOfTasks()<10?0+""+shift.getNumberOfTasks():shift.getNumberOfTasks())+">>>"+shift.getTaskChainString()+" ,lat long chain:"+shift.getLocationsString());
            log.info(sb.toString());
            sbs.append(shift.getId()+":"+getLocationList(shift).toString()+"\n");
            sbTom.append(shift.getId()+":"+getLocationListForTomTom(shift).toString()+"\n");
            shiftChainInfo.append(shift.getId()+":"+getShiftChainInfo(shift)+"\n");
            totalDrivingTime+=shift.getChainDrivingTime();
        }
        log.info(sbs.toString());
        log.info(sbTom.toString());
        log.info(shiftChainInfo.toString());
        log.info("total driving time:"+totalDrivingTime);

        log.info("per employee mins:");
        Map<String,IntSummaryStatistics> map=solution.getShifts().stream().collect(Collectors.groupingBy(s->s.getEmployee().getName(),Collectors.summarizingInt(s->s.getChainDuration())));
        map.entrySet().forEach(e->{
            log.info(e.getKey()+"---"+e.getValue().getSum());
        });

        printBreaksInfo(solution.getShifts());

        DroolsScoreDirector<VrpTaskPlanningSolution> director=(DroolsScoreDirector<VrpTaskPlanningSolution>)solver.getScoreDirectorFactory().buildScoreDirector();

        director.setWorkingSolution(solution);
        Map<Task,Indictment> indictmentMap=(Map)director.getIndictmentMap();

        toDisplayString(new Object[]{solution,indictmentMap,director.getConstraintMatchTotals()});
        log.info("---------------Printing Solution Information Completed-----");
    }

    private void printBreaksInfo(List<Shift> shifts) {
        log.info("Breaks info.");
        shifts.forEach(s->{
            log.info(s.getId()+"-"+s.getEmployee().getName()+"->"+s.getBreak()+(s.getBreak()==null?"":s.getBreak().getPlannedStartTime()));
        });
        log.info("-------------");
    }

    private void addBreaks(VrpTaskPlanningSolution problem) {
        List<Task> breaks=new ArrayList<>();
        int maxBreaks=problem.getEmployees().size()*4;
        for (int i = 0; i <maxBreaks ; i++) {
            breaks.add(new Task(1000000000l+i,30,true));
        }
        problem.getTasks().addAll(breaks);
        Collections.shuffle(problem.getTasks());

    }

    private void printProblemInfo(VrpTaskPlanningSolution problem) {
        log.info("Tasks details:");
        problem.getTasks().forEach(t->{
            log.info(t+"-----"+t.getSkills());
        });
        Map<String,List<Task>> map=problem.getTasks().stream().collect(Collectors.groupingBy(t->t.getSkills()==null?"break":t.getSkills().toString()));
        for(Map.Entry<String,List<Task>> e:map.entrySet()){
            log.info(e.getKey()+"----------"+e.getValue().stream().mapToInt(t->t.getDuration()).sum()+"------"+e.getValue());
        }
        log.info("Tasks details Done.");

    }

    public Object[] solveProblemOnRequest(VrpTaskPlanningSolution problem) {
        AtomicInteger at=new AtomicInteger(0);
        problem.getTasks().forEach(t->{
            t.setLocationsDistanceMatrix(problem.getLocationsDistanceMatrix());
            t.setLocationsRouteMatrix(problem.getLocationsRouteMatrix());
        });
        //removeBreaks(problem);
        VrpTaskPlanningSolution solution=null;
        try {
            //TODO put submiss id here
            log.info("Starting vrp solver on this thread with problem:"+problem.getSolverConfigId());
            solution = solver.solve(problem);
            DroolsScoreDirector<VrpTaskPlanningSolution> director=(DroolsScoreDirector<VrpTaskPlanningSolution>)solver.getScoreDirectorFactory().buildScoreDirector();

            director.setWorkingSolution(solution);
            Map<Object,Indictment> indictmentMap=(Map)director.getIndictmentMap();
            printSolutionInformation( solution);
            //log.info(solver.explainBestScore());
            //getxStream().toXML(solution,new FileWriter("src/main/resources/solution.xml"));
            return new Object[]{solution,indictmentMap,director.getConstraintMatchTotals()};
        }catch (Exception e){
            e.printStackTrace();
            //throw  e;
        }
        return null;
    }

    private void removeBreaks(VrpTaskPlanningSolution problem) {
        Iterator<Task> iterator = problem.getTasks().iterator();
        while (iterator.hasNext()){
            Task task =iterator.next();
            if(task.isShiftBreak()){
                iterator.remove();
            }
        }
    }

    private String getShiftChainInfo(Shift shift) {
        StringBuilder sb = new StringBuilder();
        int tasks=shift.getNumberOfTasks();
        int uniqueTasks=shift.getTaskList().stream().map(t->t.getLatitude()+"_"+t.getLongitude()).collect(Collectors.toSet()).size();
        if(tasks==uniqueTasks) return " all fine ";
        Map<String,List<Task>> groupedTasks= shift.getTaskList().stream().collect(Collectors.groupingBy(t->t.getLatitude()+"_"+t.getLongitude()));
        for(Map.Entry<String,List<Task>> e:groupedTasks.entrySet()){
                if(e.getValue().size()<2){
                    continue;
                }
                for (int i = 0; i < e.getValue().size(); i++) {
                    for (int j = i+1; j < e.getValue().size(); j++) {
                        Task t1=e.getValue().get(i);
                        Task t2=e.getValue().get(j);
                        if(!VrpPlanningUtil.isConsecutive(t1,t2)){
                            sb.append("Not consecutive{"+t1+t2+"},");
                        }
                    }
            }
        }

        return sb.toString().isEmpty()?" all fine ":sb.toString();
    }


    public List<Location> getLocationList(Shift shift){
        List<Location> list= new ArrayList<>();
        Task temp=shift.getNextTask();
        int i=0;
        while(temp!=null){
            Location location = new Location(temp.getLatitude(), temp.getLongitude(), ++i, temp.getInstallationNo());
            //if(!list.contains(location)){
            if(!temp.isShiftBreak())
                list.add(location);
            //}
            temp=temp.getNextTask();
        }
        return list;
    }
    public List<String> getLocationListForTomTom(Shift shift){
        List<String> list= new ArrayList<>();
        Task temp=shift.getNextTask();
        while(temp!=null){
            if(!temp.isShiftBreak())
                list.add("{lat:"+temp.getLatitude()+",lon:"+temp.getLongitude()+"}");
                temp=temp.getNextTask();
        }
        return list;
    }
    public String toDisplayString(Object[] array) {

        VrpTaskPlanningSolution solution = (VrpTaskPlanningSolution) array[0];
        //unassignTaskFromUnavailableEmployees(solution);
        Map<Task, Indictment> indictmentMap = (Map<Task, Indictment>) array[1];
        Collection<ConstraintMatchTotal> constraintMatchTotals = (Collection<ConstraintMatchTotal>) array[2];
        constraintMatchTotals.forEach(constraintMatchTotal -> {
            log.info(constraintMatchTotal.getConstraintName() + ":" + "Total:" + constraintMatchTotal.toString() + "==" + "Reason(entities):");
            constraintMatchTotal.getConstraintMatchSet().forEach(constraintMatch -> {
                constraintMatch.getJustificationList().forEach(o -> {

                    log.info(constraintMatch.getScore()+"---" + o+"---------"+(o instanceof Task?((Task)o).getShift()+""+((Task)o).getSkills():""));
                });
            });

        });
        return "";
    }


    public boolean terminateEarly() {
        return solver.terminateEarly();
    }
    public boolean isTerminateEarly() {
        return solver.isTerminateEarly();
    }
}
