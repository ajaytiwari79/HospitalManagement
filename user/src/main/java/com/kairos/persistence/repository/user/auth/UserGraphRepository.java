package com.kairos.persistence.repository.user.auth;

import com.kairos.persistence.model.country.Country;
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

    @Query("MATCH (u:User) WHERE u.timeCareExternalId={0} OR u.userName={1} OR u.email={2}  RETURN u ")
    User findByTimeCareExternalIdOrUserNameOrEmail(String timeCareExternalId,String userName,String email);

    @Query("MATCH (n:User) WHERE n.cprNumber={0}  RETURN n ")
    User findUserByCprNumber(String cprNumber);



    List<User> findAll();

    User findByOtp(int otp);

    @Query("MATCH(u:User { accessToken: {0} })SET u.accessToken = NULL RETURN u")
    User findAndRemoveAccessToken(String accessToken);

    User findByAccessToken(String accessToken);


    @Query("Match (organization:Organization{isEnable:true})-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={0} with organization\n" +
            "return id(organization) as id,organization.name as name,organization.isKairosHub as isKairosHub")
    List<OrganizationWrapper> getOrganizations(long userId);

    @Query("Match (accessPage:AccessPage) where id(accessPage)={0}\n" +
            "Match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage) with subPage\n" +
            "Match (org:Organization),(user:User) where id(org)={1} AND id(user)={2} with org,user,subPage\n" +
            "Match (org)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff)-[:BELONGS_TO]->(user) with staff,subPage\n" +
            "Match (staff)-[:STAFF_HAS_ACCESS_GROUP]->(accessGroup:AccessGroup)-[r:ACCESS_GROUP_HAS_ACCESS_TO_PAGE]->(subPage) return {pageId:id(subPage),pageName:subPage.name,read:r.read,write:r.write} as result")
    List<Map<String, Object>> getPermissionForModuleInOrganization(long accessPageId, long orgId, long userId);

    @Query("MATCH (u:User) WHERE u.email=~{0}  RETURN u")
    User findByEmail(String email);

    @Query("Match (user:User) WHERE user.email=~{0} " +
            "MATCH (user)<-[:"+BELONGS_TO+"]-(:Staff)<-[:"+BELONGS_TO+"]-(:Employment)<-[:"+HAS_EMPLOYMENTS+"]-(organization:Organization{isEnable:true,boardingCompleted: true,deleted:false}) return user")
    User findUserByEmailInAnyOrganization(String email);

    User findByKmdExternalId(Long kmdExternalId);

    @Query("Match (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={0} with organization,employment\n" +
            "optional match (organization)-[:HAS_SUB_ORGANIZATION*]->(unit:Organization) with organization+[unit] as coll,employment\n" +
            "unwind coll as units with distinct units,employment\n" +
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]->(unitEmp:UnitPermission)-[:APPLICABLE_IN_UNIT]->(units) with unitEmp,units\n" +
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
            "MATCH (u)<-[:"+BELONGS_TO+"]-(s:Staff)<-[:"+BELONGS_TO+"]-(e:Employment)<-[:"+HAS_EMPLOYMENTS+"]-(organization:Organization)-[:"+COUNTRY+"]->(c:Country) return id(c)")
    Long  getCountryOfUser(Long userId);

    @Query("Match(user:User)-[:"+ SELECTED_LANGUAGE +"]->(userLanguage:SystemLanguage{deleted:false}) where id(user)={0} return id(userLanguage) LIMIT 1")
    Long getUserSelectedLanguageId(Long userId);


    // This is used to get the very first user of the organization
    @Query("Match (org:Organization) where id(org)={0}" +
            "Optional Match (emp:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) with org,unitPermission,emp\n" +
            "Optional Match (unitPermission)-[r1:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'})-[:"+HAS_PARENT_ACCESS_GROUP+"]-(parentAG:AccessGroup) with org,unitPermission,emp,r1,ag,parentAG\n" +
            "Match (emp)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "return  id(org) as organizationId ,id(user) as id, user.email as email,user.firstName as firstName,ag.name as accessGroupName,id(parentAG) as parentAccessGroupId,id(ag) as accessGroupId,user.lastName as lastName ,user.cprNumber as cprNumber,user.creationDate as creationDate ORDER BY user.creationDate DESC LIMIT 1" )
    StaffPersonalDetailDTO getUnitManagerOfOrganization(Long unitId);

    @Query("Match (org:Organization) where id(org)  = {1}" +
            "Optional Match (emp:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) with org,unitPermission,emp\n" +
            "Optional Match (unitPermission)-[r1:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'}) with org,unitPermission,emp,r1,ag\n" +
            "Match (emp)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "return  id(org) as organizationId ,user.email as email,id(user) as id,ag.name as accessGroupName,id(ag) as accessGroupId, user.firstName as firstName,user.lastName as lastName ,user.cprNumber as cprNumber,staff as staff,user.creationDate as creationDate " +
            "UNION " +
            "Match (org:Organization),(child:Organization) where id(org) = {1} and id(child) IN {0}\n" +
            " Optional Match (org)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission) with child,emp,unitPermission\n" +
            " MATCH (unitPermission)-[:APPLICABLE_IN_UNIT]->(child) with  unitPermission,emp,child\n" +
            "Optional Match (unitPermission)-[r1:HAS_ACCESS_GROUP]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'}) with child,emp,ag\n" +
            "Match (emp)-[:BELONGS_TO]-(staff:Staff)-[:BELONGS_TO]-(user:User) \n" +
            "return  id(child) as organizationId ,user.email as email,id(user) as id,ag.name as accessGroupName,id(ag) as accessGroupId, user.firstName as firstName,user.lastName as lastName,user.cprNumber as cprNumber,staff as staff,user.creationDate as creationDate " )

    List<StaffPersonalDetailDTO> getUnitManagerOfOrganization(List<Long> unitId,Long parentOrganizationId);

    @Query("Match (org:Organization) where id(org)={0}" +
            "Optional Match (emp:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) with emp"+
            " Match (emp)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]-(user:User) \n" +
            "return user LIMIT 1 " )
    User getUserOfOrganization(Long unitId);

    @Query("MATCH (user:User) WHERE user.cprNumber={0}  RETURN user ")
    User findByCprNumber(String cprNumber);

    @Query("MATCH (user:User) WHERE ( user.cprNumber={1} OR user.email=~{0} ) AND id(user)<>{2} RETURN count(user) ")
    byte validateUserEmailAndCPRExceptCurrentUser(String email, String cprNumber, Long userId);
}
