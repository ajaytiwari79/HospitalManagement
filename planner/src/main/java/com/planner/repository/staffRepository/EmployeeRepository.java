package com.planner.repository.staffRepository;

import com.planner.domain.staff.Employee;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Repository
public interface EmployeeRepository extends MongoBaseRepository<Employee,String>{
}
