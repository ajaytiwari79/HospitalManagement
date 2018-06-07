package com.planner.service.vrpService;

import com.planner.domain.task.Task;
import com.planner.service.staffService.EmployeeService;
import com.planner.service.taskService.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class VRPGeneratorService {

    @Autowired private TaskService taskService;
    @Autowired private EmployeeService employeeService;



}
