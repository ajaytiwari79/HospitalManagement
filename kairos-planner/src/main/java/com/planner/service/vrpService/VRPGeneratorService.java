package com.planner.service.vrpService;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;

import com.planner.domain.tomtomResponse.Matrix;
import com.planner.service.staffService.EmployeeService;
import com.planner.service.taskService.TaskService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.util.wta.FileIOUtil;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class VRPGeneratorService {

    @Autowired private TaskService taskService;
    @Autowired private EmployeeService employeeService;
    @Autowired private TomTomService tomTomService;

    public VrpTaskPlanningSolution writeToJson(){
        VrpTaskPlanningSolution solution = new VrpTaskPlanningSolution();
        List<Employee> employees = ObjectMapperUtils.copyPropertiesOfListByMapper(employeeService.getEmployees(),Employee.class);

        List<Matrix> matrix=tomTomService.getMatrix();
        LocationsDistanceMatrix locationsDistanceMatrix= new LocationsDistanceMatrix();
        matrix.forEach(m->{
            locationsDistanceMatrix.addLocationDistance(new LocationPair(m.getFirstLatitude(),m.getFirstLongitude(),m.getSecondLattitude(),m.getSecondLongitude()),
                    new LocationPairDifference(m.getResponse().getRouteSummary().getLengthInMeters(),m.getResponse().getRouteSummary().getTravelTimeInSeconds(),m.getResponse().getRouteSummary().getTrafficDelayInSeconds()));
        });
        List<com.kairos.planner.vrp.taskplanning.model.Task> tasks = taskService.getUniqueTask();
        List<Shift> shifts = getShifts(employees);
        solution.setTasks(tasks);
        solution.setShifts(shifts);
        solution.setEmployees(employees);
        solution.setLocationsDistanceMatrix(locationsDistanceMatrix);
        FileIOUtil.writeVrpPlanningXMLToFile(solution,System.getProperty("user.dir")+"/optaplanner-vrp-taskplanning/src/main/resources/problem");
        /*try {
        //String json = ObjectMapperUtils.objectToJsonString(solution);
            PrintWriter out = new PrintWriter(new File(System.getProperty("user.dir")+"/optaplanner-vrp-taskplanning/src/main/resources/problem.json").getAbsolutePath());
            out.write(json);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        return solution;
    }





    private List<Shift> getShifts(List<Employee> employeeList){
        List<Shift> shifts = new ArrayList<>();
        employeeList.forEach(e->{
            for (int i=4;i<=9;i++) {
                shifts.add(new Shift(e.getId(), e, LocalDate.of(2018, 6, i), null, null));
            }
        });
        return shifts;
    }



}
