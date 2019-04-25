package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.PermissionField;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

public interface PermissionFieldRepository extends Neo4jBaseRepository<PermissionField,Long> {

    @Query(value = "MATCH (permissionModel:PermissionModel)-[:HAS_FIELD]-(permissionField:PermissionField) WHERE id(permissionModel)={0} AND id(permissionField)={1} return  permissionField")
    PermissionField getPermissionFieldByIdAndPermissionModelId(Long permissionModelId, Long permissionActionId);
}