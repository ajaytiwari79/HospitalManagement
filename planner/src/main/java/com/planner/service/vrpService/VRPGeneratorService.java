package com.planner.service.vrpService;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.vrp.vrpPlanning.EmployeeDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.ShiftDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.planner.domain.common.solverconfig.SolverConfig;
import com.planner.domain.tomtomResponse.Matrix;
import com.planner.service.staffService.EmployeeService;
import com.planner.service.taskService.TaskService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.util.wta.FileIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class VRPGeneratorService {

    @Autowired private TaskService taskService;
    @Autowired private EmployeeService employeeService;
    @Autowired private TomTomService tomTomService;
    //@Autowired private SolverConfigService solverConfigService;

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
        Map<LocationPair,Boolean> onArriveSideMatrix=tomTomService.getOnArriveSideMatrix();
        LocationsRouteMatrix locationsRouteMatrix = new LocationsRouteMatrix(onArriveSideMatrix);
        solution.setLocationsRouteMatrix(locationsRouteMatrix);
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
            for (int i=4;i<=8;i++) {
                shifts.add(new Shift(e.getId()+i, e, LocalDate.of(2018, 6, i), null, null));
            }
        });
        return shifts;
    }

    public VrpTaskPlanningSolution getVRPProblemSolution(VrpTaskPlanningDTO vrpTaskPlanningDTO){
        VrpTaskPlanningSolution problem = new VrpTaskPlanningSolution();
        List<Matrix> matrix=tomTomService.getMatrix();
        LocationsDistanceMatrix locationsDistanceMatrix= new LocationsDistanceMatrix();
        matrix.forEach(m->{
            locationsDistanceMatrix.addLocationDistance(new LocationPair(m.getFirstLatitude(),m.getFirstLongitude(),m.getSecondLattitude(),m.getSecondLongitude()),
                    new LocationPairDifference(m.getResponse().getRouteSummary().getLengthInMeters(),m.getResponse().getRouteSummary().getTravelTimeInSeconds(),m.getResponse().getRouteSummary().getTrafficDelayInSeconds()));
        });
        List<Task> tasks = new ArrayList<>(vrpTaskPlanningDTO.getTasks().size());
        vrpTaskPlanningDTO.getTasks().forEach(t->{
            tasks.add(new Task(t.getId(),t.getInstallationNumber(),t.getLatitude(),t.getLongitude(),t.getSkills(),(int)t.getDuration(),t.getStreetName(),t.getHouseNo(),t.getBlock(),t.getFloorNo(),t.getPost(),t.getCity(),false));
        });
        Object[] objects= getEmployeesAndShifts(vrpTaskPlanningDTO.getShifts());
        List<Shift> shifts = (List<Shift>)objects[0];
        List<Employee> employees = (List<Employee>)objects[1];
        problem.setSolverConfigId(vrpTaskPlanningDTO.getSolverConfig().getId());

        SolverConfig solverConfig = null;//solverConfigService.getSolverConfigByDTO(vrpTaskPlanningDTO.getSolverConfig());
        //problem.setConstraint(solverConfig.getConstraint());
        problem.setTasks(tasks);
        Map<LocationPair,Boolean> onArriveSideMatrix=tomTomService.getOnArriveSideMatrix();
        LocationsRouteMatrix locationsRouteMatrix = new LocationsRouteMatrix(onArriveSideMatrix);
        problem.setLocationsRouteMatrix(locationsRouteMatrix);
        problem.setShifts(shifts);
        problem.setEmployees(employees);
        addBreaks(problem);
        problem.setLocationsDistanceMatrix(locationsDistanceMatrix);
        return problem;
    }


    private void addBreaks(VrpTaskPlanningSolution problem) {
        List<Task> breaks=new ArrayList<>();
        int maxBreaks=problem.getShifts().stream().filter(s->!s.isHalfWorkDay()).collect(Collectors.toList()).size();
        for (int i = 0; i <maxBreaks ; i++) {
            breaks.add(new Task(1000000000l+i,30,true));
        }
        problem.getTasks().addAll(breaks);
        Collections.shuffle(problem.getTasks());

    }

    private Object[] getEmployeesAndShifts(List<ShiftDTO> shiftDTOS){
        List<Shift> shifts = new ArrayList<>();
        List<EmployeeDTO> employeeDTOSet = new ArrayList<>(shiftDTOS.stream().map(shiftDTO -> shiftDTO.getEmployee()).collect(Collectors.toSet()));
        List<Employee> employees = ObjectMapperUtils.copyPropertiesOfListByMapper(employeeDTOSet,Employee.class);
        Map<String,Employee> employeeMap = employees.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        shiftDTOS.forEach(s->{
            //for (int i=4;i<=8;i++) {
                shifts.add(new Shift(s.getId(), employeeMap.get(s.getEmployee().getId()),s.getLocalDate(), DateUtils.dateToLocalDateTime(new Date(s.getStartTime())), DateUtils.dateToLocalDateTime(new Date(s.getEndTime()))));
            //}
        });
        return new Object[]{shifts,employeeDTOSet};
    }



}
