package com.planner.service.staffService;

import com.planner.domain.staff.Employee;
import com.planner.repository.staffRepository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class EmployeeService {

    @Autowired private EmployeeRepository employeeRepository;

    public void saveEmployee(List<Employee> employeeList){
        employeeRepository.saveAll(employeeList);
    }

    public List<Employee> getEmployees(){
        return employeeRepository.findAll();
    }

}
