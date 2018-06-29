package com.kairos.planner.vrp.taskplanning.solver;

<<<<<<< HEAD:kairos-planner/optaplanner-vrp-taskplanning/src/test/java/com/kairos/planner/vrp/taskplanning/solver/VrpTaskPlanningSolverTest.java
import com.google.common.reflect.Reflection;
import com.kairos.activity.util.ObjectMapperUtils;
=======
import com.kairos.util.ObjectMapperUtils;
>>>>>>> KP-3748:planner/optaplanner-vrp-taskplanning/src/test/java/com/kairos/planner/vrp/taskplanning/solver/VrpTaskPlanningSolverTest.java
import com.kairos.planner.vrp.taskplanning.model.LocationsDistanceMatrix;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import org.junit.Test;
import org.mvel2.util.ReflectionUtil;
import org.reflections.ReflectionUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

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
}