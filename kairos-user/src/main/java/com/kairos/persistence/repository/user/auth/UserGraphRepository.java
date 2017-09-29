package com.kairos.persistence.repository.user.auth;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.user.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.user.auth.TabPermission;
import com.kairos.persistence.model.user.auth.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Interface for CRUD operation on User
 */
@Repository
public interface UserGraphRepository extends GraphRepository<User> {


    User findByUserName(String userName);

    User findOne(Long id);

    User findByTimeCareExternalId(String timeCareExternalId);

    List<User> findAll();

    User findByOtp(int otp);

    @Query("MATCH(u:User { accessToken: {0} })SET u.accessToken = NULL RETURN u")
    User findAndRemoveAccessToken(String accessToken);

    User findByAccessToken(String accessToken);

    @Query("MATCH (u:User) WHERE id(u) = {0} SET org.isDeleted = true ")
    void safeDelete(Long aLong);

    @Query("Match (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={0} with organization\n" +
            "return id(organization) as id,organization.name as name,organization.isKairosHub as isKairosHub")
    List<OrganizationWrapper> getOrganizations(long userId);

    @Query("Match (accessPage:AccessPage) where id(accessPage)={0}\n" +
            "Match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage) with subPage\n" +
            "Match (org:Organization),(user:User) where id(org)={1} AND id(user)={2} with org,user,subPage\n" +
            "Match (org)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff)-[:BELONGS_TO]->(user) with staff,subPage\n" +
            "Match (staff)-[:STAFF_HAS_ACCESS_GROUP]->(accessGroup:AccessGroup)-[r:ACCESS_GROUP_HAS_ACCESS_TO_PAGE]->(subPage) return {pageId:id(subPage),pageName:subPage.name,read:r.read,write:r.write} as result")
    List<Map<String, Object>> getPermissionForModuleInOrganization(long accessPageId, long orgId, long userId);

    @Query("MATCH (u:User) where u.email= {0} return u")
    User findByEmail(String email);

    User findByKmdExternalId(Long kmdExternalId);

    @Query("Match (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={0} with organization,employment\n" +
            "optional match (organization)-[:HAS_SUB_ORGANIZATION*]->(unit:Organization) with organization+[unit] as coll,employment\n" +
            "unwind coll as units with distinct units,employment\n" +
            "MATCH (employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmp:UnitEmployment)-[:PROVIDED_BY]->(units) with unitEmp,units\n" +
            "Match (unitEmp)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission) with accessPermission,units\n" +
            "MATCH (accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,accessPermission,units\n" +
            "Match (accessPermission)-[modulePermission:HAS_ACCESS_PAGE_PERMISSION]->(accessPage:AccessPage) with accessPage,accessPermission,modulePermission,units\n" +
            "return id(accessPage) as id,accessPage.moduleId as tabId,modulePermission.isRead as read,modulePermission.isWrite as write,accessPage.isModule as isModule,id(units) as unitId")
    Set<TabPermission> getAccessPermissionsOfUser(Long userId);

    @Query("Match (emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={0} with emp\n" +
            "Match (emp:Employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmp:UnitEmployment)-[:"+PROVIDED_BY+"]->(org:Organization) with collect(org.isKairosHub) as hubList\n" +
            "return true in hubList")
    Boolean isHubMember(Long userId);
}
