package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.KPermissionField;
import com.kairos.persistence.model.kpermissions.KPermissionFieldQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FIELD;

public interface PermissionFieldRepository extends Neo4jBaseRepository<KPermissionField,Long> {

    @Query(value = "MATCH (kPermissionModel:KPermissionModel)-[:"+HAS_FIELD+"]-(kPermissionField:KPermissionField) WHERE id(kPermissionModel)={0} AND id(kPermissionField)={1} RETURN  kPermissionField,kPermissionModel")
    KPermissionFieldQueryResult getPermissionFieldByIdAndPermissionModelId(Long permissionModelId, Long permissionActionId);
}