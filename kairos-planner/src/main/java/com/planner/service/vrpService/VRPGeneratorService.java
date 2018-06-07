package com.planner.service.vrpService;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.planner.vrp.taskplanning.model.Employee;
import com.kairos.planner.vrp.taskplanning.model.Shift;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;

import com.planner.domain.task.Task;
import com.planner.service.staffService.EmployeeService;
import com.planner.service.taskService.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public VrpTaskPlanningSolution getVrpTaskPlanningSolution(){
        VrpTaskPlanningSolution solution = new VrpTaskPlanningSolution();
        List<Employee> employees = ObjectMapperUtils.copyPropertiesOfListByMapper(employeeService.getEmployees(),Employee.class);
        List<com.kairos.planner.vrp.taskplanning.model.Task> tasks = taskService.getUniqueTask();
        List<Shift> shifts = getShifts(employees);
        solution.setTasks(tasks);
        solution.setShifts(shifts);
        solution.setEmployees(employees);
        return solution;
    }

    private List<Shift> getShifts(List<Employee> employeeList){
        List<Shift> shifts = new ArrayList<>();
        employeeList.forEach(e->{
            for (int i=0;i<7;i++) {
                shifts.add(new Shift(e.getId(), e, LocalDate.now().plusDays(i), null, null));
            }
        });
        return shifts;
    }



}
