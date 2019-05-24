package com.kairos.persistence.repository.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermissions;
import com.kairos.persistence.model.kpermissions.AccessGroupPermissionModelRelationshipType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface AccessGroupPermissionModelRelationshipGraphRepository extends Neo4jBaseRepository<AccessGroupPermissionModelRelationshipType, Long> {

    @Query(value = "MATCH (kPermissionModel:KPermissionModel),(accessGroup:AccessGroup) where id(kPermissionModel)={0} AND id(accessGroup)={1} CREATE UNIQUE (kPermissionModel)-[r:"+HAS_PERMISSION+"]->(accessGroup) SET r.fieldLevelPermission={2}")
    void createAccessGroupPermissionModelRelationship(Long kpermissionModelId, Long accessGroupId, FieldLevelPermissions fieldLevelPermission);
}
