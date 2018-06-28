package com.kairos.planner.vrp.taskplanning.solver;

import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.kairos.planner.vrp.taskplanning.util.VrpPlanningUtil;
import com.thoughtworks.xstream.XStream;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
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
    public static String config = "src/main/resources/config/Kamstrup_Vrp_taskPlanning.solver.xml";
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    Solver<VrpTaskPlanningSolution> solver;
    SolverFactory<VrpTaskPlanningSolution> solverFactory;


    public VrpTaskPlanningSolver(){
        solverFactory = SolverFactory.createFromXmlFile(new File(config));
        solver = solverFactory.buildSolver();
    }


    public VrpTaskPlanningSolver(List<File> drlFileList){
        //solverFactory = SolverFactory.createFromXmlFile(new File("optaplanner-vrp-taskplanning/"+config));
        solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setScoreDrlFileList(drlFileList);
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
        printProblemInfo(problem);
        if(addBreaks)
        addBreaks(problem);
        AtomicInteger at=new AtomicInteger(0);
        problem.getTasks().forEach(t->{
            at.addAndGet(t.getDuration());
            t.setLocationsDistanceMatrix(problem.getLocationsDistanceMatrix());
        });
        //TODO ease efficiency for debugging
        //problem.getEmployees().forEach(e->e.setEfficiency(100));
        log.info("Number of tasks:"+problem.getTasks().size());
        LocationPair locationPair = new ArrayList<LocationPair>(problem.getLocationsDistanceMatrix().getTable().keySet()).get(2);
        LocationPairDifference locationsDifference = problem.getLocationsDistanceMatrix().getLocationsDifference(locationPair);
        LocationPairDifference locationsDifference2 = problem.getLocationsDistanceMatrix().getLocationsDifference(locationPair.getReversePair());

        VrpTaskPlanningSolution solution=null;
        try {
            solution = solver.solve(problem);

        }catch (Exception e){
            //e.printStackTrace();
            throw  e;
        }
        getxStream().toXML(solution,new FileWriter("src/main/resources/solution.xml"));
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
        Map<String,IntSummaryStatistics> map=problem.getShifts().stream().collect(Collectors.groupingBy(s->s.getEmployee().getName(),Collectors.summarizingInt(s->s.getChainDuration())));
        map.entrySet().forEach(e->{
            log.info(e.getKey()+"---"+e.getValue().getSum());
        });

        printBreaksInfo(solution.getShifts());

        DroolsScoreDirector<VrpTaskPlanningSolution> director=(DroolsScoreDirector<VrpTaskPlanningSolution>)solver.getScoreDirectorFactory().buildScoreDirector();

        director.setWorkingSolution(solution);
        Map<Task,Indictment> indictmentMap=(Map)director.getIndictmentMap();

        toDisplayString(new Object[]{solution,indictmentMap,director.getConstraintMatchTotals()});

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
        });
        VrpTaskPlanningSolution solution=null;
        try {
            solution = solver.solve(problem);
            DroolsScoreDirector<VrpTaskPlanningSolution> director=(DroolsScoreDirector<VrpTaskPlanningSolution>)solver.getScoreDirectorFactory().buildScoreDirector();

            director.setWorkingSolution(solution);
            Map<Task,Indictment> indictmentMap=(Map)director.getIndictmentMap();
            return new Object[]{solution,indictmentMap};
        }catch (Exception e){
            //e.printStackTrace();
            throw  e;
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


}
