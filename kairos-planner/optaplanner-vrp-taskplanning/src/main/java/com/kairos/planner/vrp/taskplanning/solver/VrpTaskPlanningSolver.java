package com.kairos.planner.vrp.taskplanning.solver;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.thoughtworks.xstream.XStream;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VrpTaskPlanningSolver {
    public static String config = "src/main/resources/config/Kamstrup_Vrp_taskPlanning.solver.xml";
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    Solver<VrpTaskPlanningSolution> solver;
    SolverFactory<VrpTaskPlanningSolution> solverFactory;


    public VrpTaskPlanningSolver(){
        solverFactory = SolverFactory.createFromXmlFile(new File(config));
        solver = solverFactory.buildSolver();
    }

    public void solve(String problemXML) throws IOException {
        XStream xstream = getxStream();
        VrpTaskPlanningSolution problem=(VrpTaskPlanningSolution) xstream.fromXML(new File(problemXML));
        solve(problem);
    }

    private XStream getxStream() {
        XStream xstream= new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.processAnnotations(LocationPair.class);
        xstream.processAnnotations(LocationPairDifference.class);
        xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
        return xstream;
    }

    public void solve(VrpTaskPlanningSolution problem) throws IOException {
        AtomicInteger at=new AtomicInteger(0);
        problem.getTasks().forEach(t->{
            at.addAndGet(t.getDuration());
            t.setLocationsDistanceMatrix(problem.getLocationsDistanceMatrix());
        });
        //TODO ease efficiency for debugging
        //problem.getEmployees().forEach(e->e.setEfficiency(100));
        VrpTaskPlanningSolution solution=null;
        try {
            solution = solver.solve(problem);

        }catch (Exception e){
            //e.printStackTrace();
            throw  e;
        }
        getxStream().toXML(solution,new FileWriter("src/main/resources/problem.xml"));
        int totalDrivingTime=0;
        for(Shift shift: solution.getShifts()){
            StringBuffer sb= new StringBuffer(shift+":::"+shift.getTotalPlannedMinutes()+":::"+shift.getNumberOfTasks()+">>>"+shift.getTaskChainString()+" ,lat long chain:"+shift.getLocationsString());
            log.info(sb.toString());
            log.info(getLocationList(shift).toString());
            totalDrivingTime+=shift.getChainDrivingTime();
        }
        log.info("total driving time:"+totalDrivingTime);

    }

    public List<Location> getLocationList(Shift shift){
        List<Location> list= new ArrayList<>();
        Task temp=shift.getNextTask();
        int i=0;
        while(temp!=null){
            Location location = new Location(temp.getLattitude(), temp.getLongitude(), ++i, temp.getIntallationNo());
            if(!list.contains(location)){
                list.add(location);
            }
            temp=temp.getNextTask();
        }
        return list;
    }
}
