package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.AccessGroupPermissionModelRelationshipType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface AccessGroupPermissionModelRelationshipGraphRepository extends Neo4jBaseRepository<AccessGroupPermissionModelRelationshipType, Long> {


}
