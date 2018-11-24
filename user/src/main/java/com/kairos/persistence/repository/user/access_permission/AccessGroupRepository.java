package com.kairos.persistence.repository.user.access_permission;

import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.counter.StaffIdsQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.AppConstants.AG_COUNTRY_ADMIN;
import static com.kairos.constants.AppConstants.HAS_ACCESS_OF_TABS;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 9/19/16.
 */
@Repository
public interface AccessGroupRepository extends Neo4jBaseRepository<AccessGroup,Long> {

    @Query("Match(staff:Staff),(accessGroup:AccessGroup) where id(staff)={0} AND id(accessGroup) IN {1} CREATE UNIQUE (staff)-[:"+STAFF_HAS_ACCESS_GROUP+"]->(accessGroup) return staff")
    Staff assignGroupToStaff(long staffId, List<Long> accessGroupIds);

    @Query("Match (staff:Staff) where id(staff)={0} MATCH (staff)-[:"+STAFF_HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup)\n" +
            "MATCH (accessGroup)-[r:"+ACCESS_GROUP_HAS_ACCESS_TO_PAGE+"]->(accessPage:AccessPage) return {id:id(accessPage),name:accessPage.name,read:r.read,write:r.write} as data")
    List<Map<String, Object>> getAccessPermissions(long staffId);


    @Query("Match (child:Organization) where id(child)={0}\n" +
            "optional match (child)<-[:"+HAS_SUB_ORGANIZATION+"*]-(n{organizationLevel:'CITY'})\n" +
            "where not (n)<-[:"+HAS_SUB_ORGANIZATION+"]-()\n" +
            "optional Match (n)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(p:AccessGroup {name:{1}})\n" +
            "optional Match (child)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(c:AccessGroup {name:{1}})\n" +
            "RETURN\n" +
            "CASE\n" +
            "WHEN p IS NOT NULL\n" +
            "THEN p\n" +
            "ELSE c END as n")
    AccessGroup findAccessGroupByName(long organizationId, String name);

    @Query("Match (organization:Organization) where id(organization)={0}\n" +
            "Match (organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(accessGroup:AccessGroup{deleted:false,enabled:true}) WHERE NOT (accessGroup.name='"+AG_COUNTRY_ADMIN+"') return accessGroup")
    List<AccessGroup> getAccessGroups(long unitId);

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization) where id(user) IN {0} \n"+
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]-(up:UnitPermission) \n" +
            "MATCH (up)-[:HAS_ACCESS_GROUP]-(ag:AccessGroup) return DISTINCT\n" +
            "apoc.map.fromValues([id(staff), {role:ag.role,userId:id(user)}]) AS map")
    List<Map<String,Object>>  getUserAccessRoleByUserIds(List<Long> userIds);

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization) where id(user) IN {0} AND id(organization) ={1} \n"+
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]-(up:UnitPermission) \n" +
            "MATCH (up)-[:HAS_ACCESS_GROUP]-(ag:AccessGroup)  return DISTINCT \n" +
            "Case when ag.role={2} then true else false END as staff,Case when ag.role={3} then true else false END as management ,\n"+
            "id(user) as userId,id(staff) as staffId,id(organization) as unitId")
    List<UserAccessRoleQueryResult>  getUsersAccessRoleByUserIds(Long unitId,List<Long> userIds,String staffRole,String managementRoll);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0}\n" +
            "MATCH (organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(accessGroup:AccessGroup{deleted:false}) " +
            "OPTIONAL MATCH(accessGroup)-[:"+DAY_TYPES+"]-(dayType:DayType) WHERE NOT (accessGroup.name='"+AG_COUNTRY_ADMIN+"') " +
            "RETURN id(accessGroup) as id, accessGroup.name as name, accessGroup.description as description, accessGroup.typeOfTaskGiver as typeOfTaskGiver, accessGroup.deleted as deleted, accessGroup.role as role, accessGroup.enabled as enabled,accessGroup.startDate as startDate, accessGroup.endDate as endDate, collect(id(dayType)) as dayTypeIds,accessGroup.allowedDayTypes as allowedDayTypes ORDER BY accessGroup.name")
    List<AccessGroupQueryResult> getAccessGroupsForUnit(long unitId);


    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={1} WITH accessGroup\n" +
            "MATCH (c:Country)-[r:"+HAS_ACCESS_FOR_ORG_CATEGORY+"]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForHub=true WITH accessGroup,accessPage \n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:true, write:true}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPageForHubAccessGroup(Long countryId, Long accessGroupId);

    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={1} WITH accessGroup\n" +
            "MATCH (c:Country)-[r:"+HAS_ACCESS_FOR_ORG_CATEGORY+"]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForUnion=true WITH accessGroup,accessPage \n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:true, write:true}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPageForUnionAccessGroup(Long countryId, Long accessGroupId);

    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={1} WITH accessGroup\n" +
            "MATCH (c:Country)-[r:"+HAS_ACCESS_FOR_ORG_CATEGORY+"]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForOrganization=true WITH accessGroup,accessPage \n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:true, write:true}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPageForOrganizationAccessGroup(Long countryId, Long accessGroupId);


    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={0} WITH accessGroup\n" +
            "Match (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage)  WITH accessPage, r \n" +
            "Match (orgAccessGroup:AccessGroup) where id(orgAccessGroup)={1} WITH orgAccessGroup,accessPage,r \n" +
            "create unique (orgAccessGroup)-[orgAccessPageRel:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:r.read, write:r.write}]->(accessPage) return orgAccessPageRel")
    List<Map<String,Object>> setAccessPagePermissionForAccessGroup(Long countryAccessGroupId, Long orgAccessGroupId);

    /**
     *
     * @param countryAccessGroupId
     * @param orgAccessGroupId
     */
    @Query("Match (accessGroup:AccessGroup) where id(accessGroup) IN {0} \n" +
            "Match (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) \n" +
            "Match (orgAccessGroup:AccessGroup) where id(orgAccessGroup) IN {1} \n" +
            "create unique (orgAccessGroup)-[orgAccessPageRel:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:r.read, write:r.write}]->(accessPage) ")
    void setAccessPagePermissionForAccessGroup(List<Long> countryAccessGroupId, List<Long> orgAccessGroupId);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "MATCH (c:Country) WHERE id(c)={1} WITH c, listOfPage\n" +
            "UNWIND listOfPage as page\n" +
            "Match (c)-[r:"+HAS_ACCESS_GROUP+"]-(accessGroup:AccessGroup) WHERE  r.organizationCategory = {2} WITH accessGroup, page\n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:true, write:true}]->(page)")
    void addAccessPageRelationshipForCountryAccessGroups(Long accessPageId, Long countryId, String organizationCategory);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "MATCH (c:Country) WHERE id(c)={1} WITH c, listOfPage \n" +
            "UNWIND listOfPage as page \n" +
            "Match (c)-[r:"+HAS_ACCESS_GROUP+"]-(accessGroup:AccessGroup) WHERE  r.organizationCategory = {2} AND accessGroup.name<> '"+AG_COUNTRY_ADMIN+"' WITH accessGroup, page \n" +
            "MATCH (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(page) DELETE r")
    void removeAccessPageRelationshipForCountryAccessGroup(Long accessPageId, Long countryId, String organizationCategory);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "Match (org:Organization)-[:"+BELONGS_TO+"]-(c:Country) where id(c)={1} AND org.isKairosHub ={2} AND org.union={3} with org,listOfPage \n" +
            "OPTIONAL Match (org)-[:HAS_SUB_ORGANIZATION*]->(childOrg:Organization)  where childOrg.isKairosHub ={2} AND childOrg.union={3} with org+[childOrg] as allOrg,listOfPage \n" +
            "UNWIND listOfPage as page\n" +
            "UNWIND allOrg as org \n" +
            "Match (org)-[r:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(accessGroup:AccessGroup) WITH accessGroup, page \n"+
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true, read:true, write:true}]->(page)")
    void addAccessPageRelationshipForOrganizationAccessGroups(Long accessPageId, Long countryId, Boolean isKairosHub, Boolean isUnion);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "Match (org:Organization)-[:"+BELONGS_TO+"]-(c:Country) where id(c)={1} AND org.isKairosHub ={2} AND org.union={3} with org,listOfPage \n" +
            "OPTIONAL Match (org)-[:HAS_SUB_ORGANIZATION*]->(childOrg:Organization)  where childOrg.isKairosHub ={2} AND childOrg.union={3} with org+[childOrg] as allOrg,listOfPage \n" +
            "UNWIND listOfPage as page\n" +
            "UNWIND allOrg as org \n" +
            "Match (org)-[r:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(accessGroup:AccessGroup) WHERE accessGroup.name <> '"+ AG_COUNTRY_ADMIN +"' WITH accessGroup, page \n"+
            "MATCH (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(page) DELETE r")
    void removeAccessPageRelationshipForOrganizationAccessGroup(Long accessPageId, Long countryId, Boolean isKairosHub, Boolean isUnion);

    @Query("Match (n:AccessPage) where id(n)={0} with n\n" +
            "Optional Match (n)-[:" + SUB_PAGE + "*]->(subPage:AccessPage) with n+[subPage] as coll unwind coll as pages with distinct pages \n"+
            " Optional Match (pages)-[r:"+HAS_ACCESS_OF_TABS+"]-(ag:AccessGroup) WHERE id(ag) = {1} WITH r\n"+
            "set r.read={2}, r.write={3} return distinct true")
    Boolean updatePermissionsForAccessTabsAndChildrenOfAccessGroup(Long tabId, Long accessGroupId, Boolean read, Boolean write);

    @Query("Match (n:AccessPage) where id(n)={0} with n\n" +
           " Optional Match (n)-[r:"+HAS_ACCESS_OF_TABS+"]-(ag:AccessGroup) WHERE id(ag) = {1} WITH r\n"+
            "set r.read={2}, r.write={3} return distinct true")
    Boolean updatePermissionsForAccessTabOfAccessGroup(Long tabId, Long accessGroupId, Boolean read, Boolean write);

    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup)={0} and id(accessPage) IN {1}\n" +
            "MERGE (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(accessPage)\n" +
            "ON CREATE SET r.isEnabled={2},r.creationDate={3},r.lastModificationDate={4},r.read={5},r.write={6}\n" +
            "ON MATCH SET r.isEnabled={2},r.lastModificationDate={4},r.read={5},r.write={6} return true")
    List<Map<String,Object>> updateAccessPagePermission(long accessGroupId, List<Long> pageIds, boolean isSelected, long creationDate, long lastModificationDate, Boolean read, Boolean write);




    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
            "optional match (accessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPage, accessGroup\n" +
            "Match (n:Organization),(staff:Staff) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage,accessGroup\n" +
            "MERGE (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION{accessGroupId:{3}}]->(accessPage) \n" +
            "ON CREATE SET r.read={5},r.write={6}\n" +
            "ON MATCH SET r.read={5},r.write={6} return distinct true")
    void setCustomPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);

    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
            "optional match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with [subPage]+accessPage as coll,accessGroup as accessGroup\n" +
            "unwind coll as accessPage with distinct accessPage,accessGroup\n" +
            "Match (n:Organization),(staff:Staff) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage,accessGroup\n" +
            "MATCH (accessGroup)-[:"+HAS_ACCESS_OF_TABS+"]->(accessPage) with unitPermission,accessPage,accessGroup\n" +
            "MERGE (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION{accessGroupId:{3}}]->(accessPage)\n" +
            "ON CREATE SET r.read={5},r.write={6}\n" +
            "ON MATCH SET r.read={5},r.write={6} return distinct true")
    void setCustomPermissionForChildren(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);


    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
//            "optional match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPage+[subPage] as coll,accessGroup as accessGroup\n" +
//            "unwind coll as accessPage with distinct accessPage,accessGroup\n" +
            "Match (n:Organization),(staff:Staff) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage,accessGroup\n" +
            "MATCH (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE r.accessGroupId ={3}\n" +
            "DELETE r")
    void deleteCustomPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId);




    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
            "optional match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPage+[subPage] as coll,accessGroup as accessGroup\n" +
            "unwind coll as accessPage with distinct accessPage,accessGroup\n" +
            "Match (n:Organization),(staff:Staff) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage,accessGroup\n" +
            "MATCH (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE r.accessGroupId={3} \n" +
            "DELETE r")
    void deleteCustomPermissionForChildren(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId);

    @Query("Match (accessGroup:AccessGroup)-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) with accessPage where id(accessGroup)={0} return accessPage")
    List<AccessPage> getAccessPageByGroup(long accessGroupId);

    @Query("Match (n:Organization)-[:ORGANIZATION_HAS_ACCESS_GROUPS]->(ag:AccessGroup{typeOfTaskGiver:true}) where id(n)={0} return ag")
    AccessGroup findTaskGiverAccessGroup(Long organizationId);

    List<AccessGroup> findAll();

    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND id(a)={1} AND r.organizationCategory={2} return a ")
    AccessGroup findCountryAccessGroupByIdAndCategory(Long countryId, Long accessGroupId, String orgCategory);

    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND id(a)={1} return a ")
    AccessGroup findCountryAccessGroupById(Long countryId, Long accessGroupId);

    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND LOWER(a.name) = LOWER({1}) AND r.organizationCategory={2} return a ")
    AccessGroup findCountryAccessGroupByNameAndCategory(Long countryId, String accessGroupName, String orgCategory);

    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND LOWER(a.name) = LOWER({1}) AND r.organizationCategory={2} return COUNT(a)>0 ")
    Boolean isCountryAccessGroupExistWithName(Long countryId, String name, String orgCategory);

    @Query("MATCH (o:Organization)-[r:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(a:AccessGroup{deleted:false}) WHERE id(o)={0} AND LOWER(a.name) = LOWER({1}) return COUNT(a)>0 ")
    Boolean isOrganizationAccessGroupExistWithName(Long orgId, String name);

    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND LOWER(a.name) = LOWER({1}) AND r.organizationCategory={2} AND NOT(id(a) = {3}) return COUNT(a)>0 ")
    Boolean isCountryAccessGroupExistWithNameExceptId(Long countryId, String name, String orgCategory, Long accessGroupId);

    @Query("MATCH (o:Organization)-[r:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(a:AccessGroup{deleted:false}) WHERE id(o)={0} AND LOWER(a.name) = LOWER({1}) AND NOT(id(a) = {2}) return COUNT(a)>0 ")
    Boolean isOrganizationAccessGroupExistWithNameExceptId(Long orgId, String name, Long accessGroupId);

    @Query("MATCH (c:Country) WHERE id(c)={0}\n" +
            "OPTIONAL MATCH (c)-[r:"+HAS_ACCESS_GROUP+"{organizationCategory:'HUB'}]->(a:AccessGroup{deleted:false})  WITH COUNT(r) as hubCount\n" +
            "OPTIONAL MATCH (c)-[r:"+HAS_ACCESS_GROUP+"{organizationCategory:'UNION'}]->(a:AccessGroup{deleted:false})  WITH COUNT(r) as unionCount, hubCount\n" +
            "RETURN hubCount, unionCount")
    AccessGroupCountQueryResult getListOfOrgCategoryWithCountryAccessGroupCount(Long countryId);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false}) WHERE id(c)={0} AND r.organizationCategory={1} " +
            "OPTIONAL MATCH(ag)-[:"+DAY_TYPES+"]-(dayType:DayType{isEnabled:true})  \n" +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.deleted as deleted, ag.role as role, ag.enabled as enabled,ag.startDate as startDate, ag.endDate as endDate, collect(id(dayType)) as dayTypeIds,ag.allowedDayTypes as allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByOrgCategory(Long countryId, String orgCategory);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false,enabled:true})  WHERE id(c)={0} AND r.organizationCategory={1} " +
            "OPTIONAL MATCH(ag)-[:"+DAY_TYPES+"]-(dayType:DayType{isEnabled:true}) WHERE  (ag.endDate IS NULL OR date(ag.endDate) >= date()) " +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.deleted as deleted, ag.role as role, ag.enabled as enabled,ag.startDate as startDate, ag.endDate as endDate, collect(dayType) as dayTypes,ag.allowedDayTypes as allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByCategory(Long countryId, String organizationCategory);

    // For Test cases
    @Query("Match (accessGroup:AccessGroup)-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) with accessPage where id(accessGroup)={0} return accessPage")
    List<Long> getAccessPageIdsByAccessGroup(long accessGroupId);

    @Query("Match (accessGroup:AccessGroup)-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) with accessPage where id(accessGroup)={0} return accessPage LIMIT 1")
    Long getAccessPageIdByAccessGroup(long accessGroupId);


    @Query("Match(employment:Employment)-[:" + HAS_UNIT_PERMISSIONS + "]-(unitPermission:UnitPermission) where id(employment) in {0} \n"+
            "Match(unitPermission)-[rel_has_access_group:" + HAS_ACCESS_GROUP + " ]-(ag:AccessGroup) optional Match(unitPermission)-[rel_has_customized_permission:" + HAS_CUSTOMIZED_PERMISSION + "]-" +
            "(accesspage:AccessPage) delete rel_has_access_group, rel_has_customized_permission")
    void deleteAccessGroupRelationAndCustomizedPermissionRelation(List<Long> empIds );


    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false,enabled:true}) WHERE id(c)={0} AND r.organizationCategory={1} AND id(ag)={2} \n" +
            "RETURN COUNT(ag)>0")
    boolean isCountryAccessGroupExistsByOrgCategory(Long countryId, String orgCategory, Long accessGroupId);


    @Query("Match(org:Organization) where id(org) in {0} Match(ag:AccessGroup) where id(ag)={1} \n" +
     "merge(org)-[:ORGANIZATION_HAS_ACCESS_GROUPS]-(ag)")
    void createAccessGroupUnitRelation(List<Long> orgIds, Long accessGroupId);

    @Query("MATCH (org:Organization) WHERE id(org)={0} WITH org\n" +
            "MATCH (org)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff)-[:BELONGS_TO]->(user:User) Where id(user)={3} WITH employment\n" +
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]-(up:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) WHERE id(unit)={1} \n" +
            "MATCH (up)-[:HAS_ACCESS_GROUP]-(ag:AccessGroup) WHERE ag.role={2} return count(ag) > 0")
    Boolean checkIfUserHasAccessByRoleInUnit(Long parentOrgId, Long unitId, String role,Long userId);

    @Query("MATCH (org:Organization) WHERE id(org)={0} WITH org\n" +
            "MATCH (org)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff) where id(staff)={3} WITH employment\n" +
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]-(up:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) WHERE id(unit)={1} \n" +
            "MATCH (up)-[:HAS_ACCESS_GROUP]-(ag:AccessGroup) WHERE ag.role={2} return count(ag) > 0")
    Boolean getStaffAccessRoles(Long parentOrgId, Long unitId, String role,Long staffId);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false, enabled:true}) WHERE id(c)={0} AND r.organizationCategory={1} AND ag.role={2} AND (ag.endDate IS NULL OR date(ag.endDate) >= date()) \n" +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.deleted as deleted, ag.role as role,ag.allowedDayTypes as allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByOrgCategoryAndRole(Long countryId, String orgCategory, String role);

    @Query("MATCH (org:Organization)-[r:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(ag:AccessGroup{deleted:false}) WHERE id(org)={0} AND ag.role={1}\n" +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.deleted as deleted, ag.role as role")
    List<AccessGroupQueryResult> getOrganizationAccessGroupByRole(Long organizationId,  String role);

    @Query("MATCH (org:Organization)-[r:ORGANIZATION_HAS_ACCESS_GROUPS]->(ag:AccessGroup{deleted:false}) WHERE id(org)={0} AND ag.name={1} AND ag.role={2}\n" +
            "RETURN ag")
    AccessGroup getOrganizationAccessGroupByName(Long organizationId, String name, String role);

    @Query("MATCH (org:Organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(ag:AccessGroup) where id(org)={0} and id(ag) IN {1} \n" +
            "with ag,org  match(ag)-[:"+HAS_ACCESS_GROUP+"]-(up:UnitPermission) with up,ag match(up)-[:"+HAS_UNIT_PERMISSIONS+"]-(emp:Employment) with ag,emp\n" +
            "match (emp)-[:"+BELONGS_TO+"]-(s:Staff) RETURN  id(ag) as accessGroupId,collect(id(s)) as staffIds ")
    List<StaffIdsQueryResult> getStaffIdsByUnitIdAndAccessGroupId(Long unitId, List<Long> accessGroupId);

    @Query("MATCH (org:Organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(ag:AccessGroup) where id(org)={0} and id(ag) IN {1} \n" +
            "WITH ag,org  match(ag)-[:"+HAS_ACCESS_GROUP+"]-(up:UnitPermission) with up,ag match(up)-[:"+HAS_UNIT_PERMISSIONS+"]-(emp:Employment) with ag,emp\n" +
            "MATCH (emp)-[:"+BELONGS_TO+"]-(s:Staff)\n" +
            "MATCH (s)-[:"+BELONGS_TO+"]-(em:Employment)  MATCH (em)-[:"+HAS_UNIT_PERMISSIONS+"]-(unitP:UnitPermission)\n" +
            "MATCH (unitP)-[:"+HAS_ACCESS_GROUP+"]-(agp:AccessGroup) RETURN id(s) as staffId, Collect(DISTINCT id(agp)) as accessGroupIds")
    List<StaffAccessGroupQueryResult> getStaffIdsAndAccessGroupsByUnitId(Long unitId, List<Long> accessGroupId);

    //for test cases
    @Query("MATCH(emp:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]-(up:UnitPermission)-[:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup) where id(emp)=8767 return id(ag)")
    Long findAccessGroupByEmploymentId(Long employmentId);


    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]->(ag:AccessGroup{deleted:false})-[:"+HAS_ACCOUNT_TYPE+"]->(accountType:AccountType) WHERE id(c)={0} AND id(accountType)={1} " +
            "OPTIONAL MATCH (ag)-[:"+DAY_TYPES+"]-(dayType:DayType) \n" +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.role as role, ag.enabled as enabled , ag.startDate as startDate, ag.endDate as endDate, collect(id(dayType)) as dayTypeIds,ag.allowedDayTypes as allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByAccountTypeId(Long countryId, Long accountTypeId);


    @Query("MATCH (staff:Staff),(org:Organization) where id(staff)={0} AND id(org)={1} with org,staff " +
            "match(org)<-[:HAS_SUB_ORGANIZATION*]-(parentOrganization:Organization) with org,parentOrganization,staff  "+
            "match(parentOrganization)-[:" + BELONGS_TO +"] -> (country:Country) with org,staff,country "+
            "match (staff)-[:"+BELONGS_TO+"]-(emp:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]-(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(org) with up,country  " +
            "MATCH (up)-[:HAS_ACCESS_GROUP]-(ag) RETURN Collect(DISTINCT id(ag)) as accessGroupIds ,id(country) as countryId")
    StaffAccessGroupQueryResult getAccessGroupIdsByStaffIdAndUnitId(Long staffId, Long unitId);


    @Query("MATCH (organization:Organization) WHERE id(organization)={0}\n" +
            "MATCH (organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(accessGroup:AccessGroup{deleted:false}) WHERE id(accessGroup)={1}" +
            "OPTIONAL MATCH (accessGroup)-[:"+DAY_TYPES+"]-(dayType:DayType)   " +
            "RETURN id(accessGroup) as id, accessGroup.name as name, accessGroup.description as description, accessGroup.typeOfTaskGiver as typeOfTaskGiver, accessGroup.deleted as deleted, accessGroup.role as role, accessGroup.enabled as enabled,accessGroup.startDate as startDate, accessGroup.endDate as endDate, collect(dayType) as dayTypes,accessGroup.allowedDayTypes as allowedDayTypes")
   AccessGroupQueryResult findByAccessGroupId(long unitId,long accessGroupId);

   @Query("MATCH(countryAccessGroup:AccessGroup{deleted:false})<-[:"+HAS_PARENT_ACCESS_GROUP+"]-(unitAccessGroup:AccessGroup{deleted:false})-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(org:Organization) where id(org) ={0} AND id(countryAccessGroup) ={1} " +
           "RETURN unitAccessGroup")
   AccessGroup getAccessGroupByParentId(Long unitId,Long parentId);

   @Query("MATCH (organization:Organization) where id(organization) IN {0}\n" +
           "MATCH (organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(accessGroup:AccessGroup{deleted:false}) detach delete accessGroup")
    void removeDefaultCopiedAccessGroup(List<Long> organizationId);

   @Query("MATCH (organization:Organization) where id(organization) = {0} \n" +
            "MATCH(organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(ag:AccessGroup)-[:"+HAS_PARENT_ACCESS_GROUP+"]-(pag:AccessGroup) return id(ag) as id,id(pag) as parentId")
    List<AccessPageQueryResult> findAllAccessGroupWithParentOfOrganization(Long organizationId);

    @Query("MATCH (organization:Organization) where id(organization) = {0} \n" +
            "MATCH(organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(ag:AccessGroup)-[:"+HAS_PARENT_ACCESS_GROUP+"]-(pag:AccessGroup) WHERE ID(pag) IN {1} return id(ag) as id,id(pag) as parentId")
    List<AccessPageQueryResult> findAllAccessGroupWithParentIds(Long organizationId,Set<Long> parentAccessGroupsIds);

    @Query("MATCH(user:User)<-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO_STAFF+"]-(unitPosition:UnitPosition)-[:"+IN_UNIT+"]-(currentOrganization:Organization) WHERE id(currentOrganization)={0} AND id(user)={1}\n" +
            "OPTIONAL MATCH(currentOrganization)<-[:"+HAS_SUB_ORGANIZATION+"]-(parentOrganizationOptional:Organization)\n" +
            "WITH currentOrganization,parentOrganizationOptional,staff,\n" +
            "CASE WHEN currentOrganization.isParentOrganization=true THEN currentOrganization  ELSE parentOrganizationOptional END AS parentOrganization\n" +
            "MATCH(parentOrganization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff)\n" +
            "WITH currentOrganization,parentOrganization,staff,employment\n" +
            " MATCH(employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup{role:\"MANAGEMENT\"})<-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]-(parentOrganization)\n" +
            "WITH currentOrganization,staff, accessGroup\n" +
            "OPTIONAL MATCH(accessGroup)-[:"+DAY_TYPES+"]->(dayTypes:DayType)\n" +
            "RETURN \n" +
            "currentOrganization,id(staff) AS staffId,COLLECT(DISTINCT dayTypes) AS dayTypes")
    AccessGroupStaffQueryResult getManagementRoleDayTypesAndStaffId(Long unitId, Long userId);
}


