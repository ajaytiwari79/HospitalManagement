package com.kairos.persistence.repository.user.auth;

import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.auth.TabPermission;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Interface for CRUD operation on User
 */
@Repository
public interface UserGraphRepository extends Neo4jBaseRepository<User,Long> {

    User findByUserNameIgnoreCase(String userName);

    User findOne(Long id);

    @Query("MATCH (n:User) WHERE n.cprNumber={0}  RETURN n ")
    User findUserByCprNumber(String cprNumber);

    List<User> findAll();

    @Query("MATCH(u:User { accessToken: {0} })SET u.accessToken = NULL RETURN u")
    User findAndRemoveAccessToken(String accessToken);

    User findByAccessToken(String accessToken);

    @Query("MATCH(u:User) WHERE u.forgotPasswordToken={0} RETURN u")
    User findByForgotPasswordToken(String forgotPasswordToken);

    @Query("MATCH (organization:Organization{isEnable:true})-[:"+ HAS_POSITIONS +"]->(:Position)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) WHERE id(user)={0} WITH organization\n" +
            "RETURN id(organization) AS id,organization.name AS name,organization.isKairosHub AS isKairosHub")
    List<OrganizationWrapper> getOrganizations(long userId);

    @Query("MATCH (accessPage:AccessPage) WHERE id(accessPage)={0}\n" +
            "MATCH (accessPage)-[:"+SUB_PAGE+"*]->(subPage:AccessPage) WITH subPage\n" +
            "MATCH (org:Organization),(user:User) WHERE id(org)={1} AND id(user)={2} WITH org,user,subPage\n" +
            "MATCH (org)-[:"+HAS_TEAMS+"]->(team:Team)-[:"+TEAM_HAS_MEMBER+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user) WITH staff,subPage\n" +
            "MATCH (staff)-[:"+STAFF_HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup)-[r:"+ACCESS_GROUP_HAS_ACCESS_TO_PAGE+"]->(subPage) RETURN {pageId:id(subPage),pageName:subPage.name,read:r.read,write:r.write} AS result")
    List<Map<String, Object>> getPermissionForModuleInOrganization(long accessPageId, long orgId, long userId);

    @Query("MATCH (u:User) WHERE u.email=~{0}  RETURN u")
    User findByEmail(String email);

    @Query("MATCH (user:User) WHERE user.email=~{0} " +
            "MATCH (user)<-[:"+BELONGS_TO+"]-(:Staff)<-[:"+BELONGS_TO+"]-(:Position)<-[:"+ HAS_POSITIONS +"]-(organization:Organization{isEnable:true,boardingCompleted: true,deleted:false}) RETURN user")
    User findUserByEmailInAnyOrganization(String email);

    User findByKmdExternalId(Long kmdExternalId);

    @Query("MATCH (staff:Staff)-[:"+BELONGS_TO+"]->(user:User) WHERE id(staff)={0} RETURN user")
    User getUserByStaffId(Long staffId);

    @Query("MATCH (u:User),(ag:AccessGroup) WHERE id(u) = {0} AND ag.name={1} WITH u,ag\n" +
            "MATCH (ag)<-[:"+HAS_ACCESS_GROUP+"]-(up:UnitPermission)<-[:"+HAS_UNIT_PERMISSIONS+"]-(:Position)-[:"+BELONGS_TO+"]->(s:Staff)-[r:"+BELONGS_TO+"]-(u)  RETURN COUNT(u)>0")
    Boolean checkIfUserIsCountryAdmin(Long userId, String accessGroupName);

    @Query("MATCH(user:User)-[:"+ SELECTED_LANGUAGE +"]->(userLanguage:SystemLanguage{deleted:false}) WHERE id(user)={0} RETURN id(userLanguage) LIMIT 1")
    Long getUserSelectedLanguageId(Long userId);

    // This is used to get the very first user of the organization
    @Query("MATCH (org:Organization) WHERE id(org)={0}" +
            "OPTIONAL MATCH (position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) WITH org,unitPermission,position\n" +
            "OPTIONAL MATCH (unitPermission)-[r1:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'})-[:"+HAS_PARENT_ACCESS_GROUP+"]-(parentAG:AccessGroup) WITH org,unitPermission,position,r1,ag,parentAG\n" +
            "MATCH (position)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "RETURN  id(org) AS organizationId ,id(user) AS id, user.email AS email,user.firstName AS firstName,ag.name AS accessGroupName,id(parentAG) AS parentAccessGroupId,id(ag) AS accessGroupId,user.lastName AS lastName ,user.cprNumber AS cprNumber,user.creationDate AS creationDate ORDER BY user.creationDate DESC LIMIT 1" )
    StaffPersonalDetailDTO getUnitManagerOfOrganization(Long unitId);

    @Query("MATCH (org:Organization) WHERE id(org)  = {1}" +
            "OPTIONAL MATCH (position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) WITH org,unitPermission,position\n" +
            "OPTIONAL MATCH (unitPermission)-[r1:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'}) WITH org,unitPermission,position,r1,ag\n" +
            "MATCH (position)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "RETURN  id(org) AS organizationId ,user.email AS email,id(user) AS id,ag.name AS accessGroupName,id(ag) AS accessGroupId, user.firstName AS firstName,user.lastName AS lastName ,user.cprNumber AS cprNumber,staff AS staff,user.creationDate AS creationDate " +
            "UNION " +
            "MATCH (org:Organization),(child:Organization) WHERE id(org) = {1} and id(child) IN {0}\n" +
            " OPTIONAL MATCH (org)-[:"+HAS_POSITIONS+"]->(position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission) WITH child,position,unitPermission\n" +
            " MATCH (unitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(child) WITH  unitPermission,position,child\n" +
            "OPTIONAL MATCH (unitPermission)-[r1:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'}) WITH child,position,ag\n" +
            "MATCH (position)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "RETURN  id(child) AS organizationId ,user.email AS email,id(user) AS id,ag.name AS accessGroupName,id(ag) AS accessGroupId, user.firstName AS firstName,user.lastName AS lastName,user.cprNumber AS cprNumber,staff AS staff,user.creationDate AS creationDate " )

    List<StaffPersonalDetailDTO> getUnitManagerOfOrganization(List<Long> unitId,Long parentOrganizationId);

    @Query("MATCH (org:Organization) WHERE id(org)={0}" +
            "OPTIONAL MATCH (position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) WITH position"+
            " MATCH (position)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "RETURN user LIMIT 1 " )
    User getUserOfOrganization(Long unitId);

    @Query("MATCH (user:User) WHERE ( user.cprNumber={1} OR user.email=~{0} ) AND id(user)<>{2} RETURN count(user) ")
    byte validateUserEmailAndCPRExceptCurrentUser(String email, String cprNumber, Long userId);

    @Query("MATCH (user:User) WHERE ( user.cprNumber={0} OR user.email=~{1} ) RETURN user")
    User findUserByCprNumberOrEmail(String cprNumber, String email);

    @Query("MATCH (user:User) WHERE user.userName=~{0}  RETURN user ")
    User findUserByUserName(String userName);

    @Query("MATCH (user:User) WHERE user.userName=~{0} " +
            "MATCH (user)<-[:"+BELONGS_TO+"]-(:Staff)<-[:"+BELONGS_TO+"]-(:Position)<-[:"+ HAS_POSITIONS +"]-(organization:Organization{isEnable:true,boardingCompleted: true,deleted:false}) RETURN user")
    User findUserByUserNameInAnyOrganization(String userName);
}
