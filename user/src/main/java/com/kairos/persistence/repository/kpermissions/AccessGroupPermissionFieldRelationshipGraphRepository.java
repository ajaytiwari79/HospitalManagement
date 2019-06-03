package com.kairos.persistence.repository.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.kpermissions.AccessGroupPermissionFieldRelationshipType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;
/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface AccessGroupPermissionFieldRelationshipGraphRepository extends Neo4jBaseRepository<AccessGroupPermissionFieldRelationshipType, Long> {

    @Query(value = "MATCH (kPermissionField:KPermissionField),(accessGroup:AccessGroup) where id(kPermissionField)={0} AND id(accessGroup) IN{1} CREATE UNIQUE (kPermissionField)-[r:"+HAS_PERMISSION+"]->(accessGroup) SET r.fieldLevelPermission={2}")
    void createAccessGroupPermissionFieldRelationshipType(Long kpermissionFieldId, List<Long> accessGroupId, FieldLevelPermission fieldLevelPermission);

}
