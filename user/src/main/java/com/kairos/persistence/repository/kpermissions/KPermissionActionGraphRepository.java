package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.KPermissionAction;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KPermissionActionGraphRepository extends Neo4jBaseRepository<KPermissionAction,Long> {
}
