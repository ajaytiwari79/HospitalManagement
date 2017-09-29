package com.kairos.persistence.repository.user.auth;

import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.user.auth.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENTS;


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


    User findByCprNumber(String cprNumber);

    User findByKmdExternalId(Long kmdExternalId);
}
