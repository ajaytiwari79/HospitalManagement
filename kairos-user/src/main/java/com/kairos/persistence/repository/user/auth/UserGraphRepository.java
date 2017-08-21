package com.kairos.persistence.repository.user.auth;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.auth.User;


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

    @Query("Match (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={0} with employment,organization\n" +
            "Match (employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment) with unitEmployment,organization\n" +
            "MATCH (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission) with accessPermission,organization\n" +
            "Match (accessPermission)-[r:HAS_ACCESS_PAGE_PERMISSION]->(accessPage:AccessPage{isModule:true})\n" +
            "optional match (accessPage)-[:SUB_PAGE]->(subPage:AccessPage)<-[r2:HAS_ACCESS_PAGE_PERMISSION]-(accessPermission) with distinct {id:id(accessPage),name:accessPage.name,read:r.isRead,write:r.isWrite,tabPermissions:case when subPage is Null then [] else collect(distinct {id:id(subPage),name:subPage.name,read:r2.isRead,write:r2.isWrite}) end}as accessPermissions,organization\n" +
            "return {id:id(organization),name:organization.name,accessPage:collect(accessPermissions)} as result")
    List<Map<String, Object>> getOrganizations(long userId);

    @Query("Match (accessPage:AccessPage) where id(accessPage)={0}\n" +
            "Match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage) with subPage\n" +
            "Match (org:Organization),(user:User) where id(org)={1} AND id(user)={2} with org,user,subPage\n" +
            "Match (org)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff)-[:BELONGS_TO]->(user) with staff,subPage\n" +
            "Match (staff)-[:STAFF_HAS_ACCESS_GROUP]->(accessGroup:AccessGroup)-[r:ACCESS_GROUP_HAS_ACCESS_TO_PAGE]->(subPage) return {pageId:id(subPage),pageName:subPage.name,read:r.read,write:r.write} as result")
    List<Map<String, Object>> getPermissionForModuleInOrganization(long accessPageId, long orgId, long userId);

    @Query("MATCH (u:User) where u.email= {0} return u")
    User findByEmail(String email);


    User findByCprNumber(String cprNumber);
}
