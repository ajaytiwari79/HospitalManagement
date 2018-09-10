package com.kairos.planner.vrp.taskplanning.solver;

import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VrpTaskPlanningSolverTest {

    @Test
    public void solve() throws IOException {
        boolean readFromSolution=false;
        if(readFromSolution){
            //solution -3 drive.xml
            new VrpTaskPlanningSolver().solve("src/main/resources/solution.xml",false);
            //new VrpTaskPlanningSolver().solve("src/main/resources/best_solution_breaks_0h0M.xml",false);
        }else{
            new VrpTaskPlanningSolver().solve("src/main/resources/problem.xml",true);
        }

    }
    @Test
    public void solveWithDrls(){
        String drlsPath="../../planner/src/main/resources/droolsFile";
        List<File> files=  Arrays.stream(new File(drlsPath).listFiles()).filter(f->f.getName().endsWith(".drl")).collect(Collectors.toList());
        VrpTaskPlanningSolver vrpTaskPlanningSolver = new VrpTaskPlanningSolver(files,"src/main/resources/config/Kamstrup_Vrp_taskPlanning.solver.xml",5,4);
        VrpTaskPlanningSolution problem=(VrpTaskPlanningSolution) vrpTaskPlanningSolver.getxStream().fromXML(new File("src/main/resources/problem.xml"));
        vrpTaskPlanningSolver.solveProblemOnRequest(problem);

    }
}