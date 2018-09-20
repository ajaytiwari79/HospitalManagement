package com.planner.repository.staff;

import com.planner.domain.staff.Staff;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends MongoBaseRepository<Staff,String> {
}
