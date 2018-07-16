package com.kairos.persistence.repository.user.auth;

import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.auth.TabPermission;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.COUNTRY;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENTS;


/**
 * Interface for CRUD operation on User
 */
@Repository
public interface UserGraphRepository extends Neo4jBaseRepository<User,Long> {


    User findByUserNameIgnoreCase(String userName);

    User findOne(Long id);

    @Query("MATCH (u:User) WHERE u.timeCareExternalId={0} OR u.userName={1} OR u.email={2}  RETURN u ")
    User findByTimeCareExternalIdOrUserNameOrEmail(String timeCareExternalId,String userName,String email);

    @Query("MATCH (n:User) WHERE n.cprNumber={0} AND Not n:Client RETURN n ")
    User findUserByCprNumber(String cprNumber);

    List<User> findAll();

    User findByOtp(int otp);

    @Query("MATCH(u:User { accessToken: {0} })SET u.accessToken = NULL RETURN u")
    User findAndRemoveAccessToken(String accessToken);

    User findByAccessToken(String accessToken);

    @Query("MATCH (u:User) WHERE id(u) = {0} SET org.isDeleted = true ")
    void safeDelete(Long aLong);

    @Query("Match (organization:Organization{isEnable:true})-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={0} with organization\n" +
            "return id(organization) as id,organization.name as name,organization.isKairosHub as isKairosHub")
    List<OrganizationWrapper> getOrganizations(long userId);

    @Query("Match (accessPage:AccessPage) where id(accessPage)={0}\n" +
            "Match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage) with subPage\n" +
            "Match (org:Organization),(user:User) where id(org)={1} AND id(user)={2} with org,user,subPage\n" +
            "Match (org)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff)-[:BELONGS_TO]->(user) with staff,subPage\n" +
            "Match (staff)-[:STAFF_HAS_ACCESS_GROUP]->(accessGroup:AccessGroup)-[r:ACCESS_GROUP_HAS_ACCESS_TO_PAGE]->(subPage) return {pageId:id(subPage),pageName:subPage.name,read:r.read,write:r.write} as result")
    List<Map<String, Object>> getPermissionForModuleInOrganization(long accessPageId, long orgId, long userId);

    @Query("MATCH (u:User) WHERE u.email={0} AND Not u:Client RETURN u")
    User findByEmail(String email);

    User findByKmdExternalId(Long kmdExternalId);

    @Query("Match (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={0} with organization,employment\n" +
            "optional match (organization)-[:HAS_SUB_ORGANIZATION*]->(unit:Organization) with organization+[unit] as coll,employment\n" +
            "unwind coll as units with distinct units,employment\n" +
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]->(unitEmp:UnitEmployment)-[:APPLICABLE_IN_UNIT]->(units) with unitEmp,units\n" +
            "Match (unitEmp)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission) with accessPermission,units\n" +
            "MATCH (accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,accessPermission,units\n" +
            "Match (accessPermission)-[modulePermission:HAS_ACCESS_PAGE_PERMISSION]->(accessPage:AccessPage) with accessPage,accessPermission,modulePermission,units\n" +
            "return id(accessPage) as id,accessPage.moduleId as tabId,modulePermission.isRead as read,modulePermission.isWrite as write,id(units) as unitId")
    Set<TabPermission> getAccessPermissionsOfUser(Long userId);

    @Query("Match (staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(staff)={0} return user")
    User getUserByStaffId(Long staffId);

    @Query("MATCH (u:User),(ag:AccessGroup) WHERE id(u) = {0} AND ag.name={1} WITH u,ag\n" +
            "MATCH (ag)<-[:HAS_ACCESS_GROUP]-(up:UnitPermission)<-[:HAS_UNIT_PERMISSIONS]-(e:Employment)-[:BELONGS_TO]->(s:Staff)-[r:BELONGS_TO]-(u)  RETURN COUNT(u)>0")
    Boolean checkIfUserIsCountryAdmin(Long userId, String accessGroupName);

    @Query("Match (u:User) WHERE id(u)={0} " +
            "MATCH (u)<-[:"+BELONGS_TO+"]-(s:Staff)<-[:"+BELONGS_TO+"]-(e:Employment)<-[:"+HAS_EMPLOYMENTS+"]-(organization:Organization)-[:"+COUNTRY+"]->(c:Country) return c")
    Country getCountryOfUser(Long userId);
}
