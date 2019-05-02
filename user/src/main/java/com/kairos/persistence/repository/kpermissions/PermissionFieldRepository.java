package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.KPermissionField;
import com.kairos.persistence.model.kpermissions.KPermissionFieldQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

public interface PermissionFieldRepository extends Neo4jBaseRepository<KPermissionField,Long> {

    @Query(value = "MATCH (permissionModel:KPermissionModel)-[:HAS_FIELD]-(permissionField:KPermissionField) WHERE id(permissionModel)={0} AND id(permissionField)={1} RETURN  permissionField,permissionModel")
    KPermissionFieldQueryResult getPermissionFieldByIdAndPermissionModelId(Long permissionModelId, Long permissionActionId);
}