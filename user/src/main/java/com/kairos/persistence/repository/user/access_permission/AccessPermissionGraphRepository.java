package com.kairos.persistence.repository.user.access_permission;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 3/1/17.
 */
@Repository
public interface AccessPermissionGraphRepository extends Neo4jBaseRepository<AccessPermission,Long> {

    List<AccessPermission> findAll();

    @Query("Match (organization:Organization),(staff:Staff),(accessGroup:AccessGroup),(accessPage:AccessPage) where id(organization)={0} AND id(staff)={1} AND id(accessGroup)={2} AND id(accessPage)={3} with organization,staff,accessGroup,accessPage\n" +
            "Match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff)-[:HAS_UNIT_PERMISSIONS]->(unitEmployment:UnitPermission) with unitEmployment,accessGroup,accessPage\n" +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitEmployment:UnitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup) with accessPermission,accessPage\n" +
            "Match (accessPermission)-[r:HAS_ACCESS_PAGE_PERMISSION]->(accessPage) SET r.isRead={4},r.isWrite={5} return r")
    Map<String,Object> setPagePermissionToUser(long unitId, long staffId, long groupId, long accessPageId, boolean read, boolean write);
}
