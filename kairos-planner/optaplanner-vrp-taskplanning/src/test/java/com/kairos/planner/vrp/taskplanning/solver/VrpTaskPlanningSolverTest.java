package com.kairos.planner.vrp.taskplanning.solver;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.planner.vrp.taskplanning.model.LocationsDistanceMatrix;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class VrpTaskPlanningSolverTest {

    @Test
    public void solve() throws IOException {
        boolean readFromSolution=true;
        if(readFromSolution){
            new VrpTaskPlanningSolver().solve("src/main/resources/solution.xml");
        }else{
            new VrpTaskPlanningSolver().solve("src/main/resources/problem.xml");
        }

    }
}