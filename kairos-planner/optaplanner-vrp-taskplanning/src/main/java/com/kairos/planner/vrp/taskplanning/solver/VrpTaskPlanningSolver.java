package com.kairos.planner.vrp.taskplanning.solver;

import com.kairos.planner.vrp.taskplanning.model.LocationPair;
import com.kairos.planner.vrp.taskplanning.model.LocationPairDifference;
import com.kairos.planner.vrp.taskplanning.model.Shift;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.thoughtworks.xstream.XStream;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VrpTaskPlanningSolver {
    public static String config = "src/main/resources/config/Kamstrup_Vrp_taskPlanning.solver.xml";
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    Solver<VrpTaskPlanningSolution> solver;
    SolverFactory<VrpTaskPlanningSolution> solverFactory;


    public VrpTaskPlanningSolver(){
        solverFactory = SolverFactory.createFromXmlFile(new File(config));
        solver = solverFactory.buildSolver();
    }
    public void solve(String problemXML){
        XStream xstream= new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.processAnnotations(LocationPair.class);
        xstream.processAnnotations(LocationPairDifference.class);
        xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
        VrpTaskPlanningSolution problem=(VrpTaskPlanningSolution) xstream.fromXML(new File(problemXML));
        solve(problem);

    }
    public void solve(VrpTaskPlanningSolution problem){
        problem.getTasks().forEach(t->{
            t.setLocationsDistanceMatrix(problem.getLocationsDistanceMatrix());
        });
        VrpTaskPlanningSolution solution=solver.solve(problem);

        for(Shift shift: solution.getShifts()){
            Task task=shift.getNextTask();
            StringBuffer sb= new StringBuffer("Shift"+shift+":::");
            while (task!=null){
                sb.append(task+"->");
                task=task.getNextTask();

            }
        }
        log.info(solution.toString());

    }
}
