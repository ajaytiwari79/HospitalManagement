package com.planner.service.taskPlanningService;

import com.planner.service.config.PathProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlannerLauncherService {

    @Autowired private PathProvider pathProvider;
    public  void instantiateNewSolver() throws Exception {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder =
                new ProcessBuilder(path, "-cp",
                        classpath,
                        PlannerLauncherService.class.getName(),pathProvider.getProblemXmlpath(),pathProvider.getSolverConfigXmlpath(),pathProvider.getSolutionXmlpath());
        Process process = processBuilder.start();
        process.waitFor();
    }
}
