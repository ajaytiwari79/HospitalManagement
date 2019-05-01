package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.AccessGroupPermissionFieldRelationshipType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface AccessGroupPermissionFieldRelationshipGraphRepository extends Neo4jBaseRepository<AccessGroupPermissionFieldRelationshipType, Long> {


}
