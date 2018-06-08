package com.kairos.planner.vrp.taskplanning.solver;

import com.kairos.planner.vrp.taskplanning.model.Shift;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
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
    public void solve(VrpTaskPlanningSolution problem){
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
