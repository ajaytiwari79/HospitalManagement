package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface CommonRepository extends Neo4jBaseRepository<Staff,Long> {
}
