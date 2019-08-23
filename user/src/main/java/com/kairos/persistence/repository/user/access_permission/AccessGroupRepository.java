package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.counter.StaffIdsQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.kairos.constants.AppConstants.HAS_ACCESS_OF_TABS;
import static com.kairos.constants.AppConstants.SUPER_ADMIN;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 9/19/16.
 */
@Repository
public interface AccessGroupRepository extends Neo4jBaseRepository<AccessGroup, Long> {

    @Query("MATCH(staff:Staff),(accessGroup:AccessGroup) WHERE id(staff)={0} AND id(accessGroup) IN {1} CREATE UNIQUE (staff)-[:" + STAFF_HAS_ACCESS_GROUP + "]->(accessGroup) RETURN staff")
    Staff assignGroupToStaff(long staffId, List<Long> accessGroupIds);

    @Query("MATCH (staff:Staff) WHERE id(staff)={0} MATCH (staff)-[:" + STAFF_HAS_ACCESS_GROUP + "]->(accessGroup:AccessGroup)\n" +
            "MATCH (accessGroup)-[r:" + ACCESS_GROUP_HAS_ACCESS_TO_PAGE + "]->(accessPage:AccessPage) RETURN {id:id(accessPage),name:accessPage.name,read:r.read,write:r.write} AS data")
    List<Map<String, Object>> getAccessPermissions(long staffId);


    @Query("MATCH (child:Unit) WHERE id(child)={0}\n" +
            "OPTIONAL MATCH (child)<-[:" + HAS_SUB_ORGANIZATION + "*]-(n{organizationLevel:'CITY'})\n" +
            "WHERE NOT (n)<-[:" + HAS_SUB_ORGANIZATION + "]-()\n" +
            "OPTIONAL MATCH (n)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(p:AccessGroup {name:{1}})\n" +
            "OPTIONAL MATCH (child)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(c:AccessGroup {name:{1}})\n" +
            "RETURN\n" +
            "CASE\n" +
            "WHEN p IS NOT NULL\n" +
            "THEN p\n" +
            "ELSE c END as n")
    AccessGroup findAccessGroupByName(long organizationId, String name);

    @Query("Match (organization:Unit) where id(organization)={0}\n" +
            "Match (organization)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(accessGroup:AccessGroup{deleted:false,enabled:true}) WHERE NOT (accessGroup.name='" + SUPER_ADMIN + "') return accessGroup")
    List<AccessGroup> getAccessGroups(long unitId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0}\n" +
            "MATCH (organization)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(accessGroup:AccessGroup{deleted:false})-["+HAS_PARENT_ACCESS_GROUP+"]->(pag:AccessGroup) " +
            "OPTIONAL MATCH(accessGroup)-[:" + DAY_TYPES + "]-(dayType:DayType) WHERE NOT (accessGroup.name='" + SUPER_ADMIN + "') " +
            "RETURN id(accessGroup) AS id, accessGroup.name AS name, accessGroup.description AS description, accessGroup.typeOfTaskGiver AS typeOfTaskGiver, accessGroup.deleted AS deleted, accessGroup.role AS role, accessGroup.enabled AS enabled,accessGroup.startDate AS startDate, accessGroup.endDate AS endDate, collect(id(dayType)) AS dayTypeIds,accessGroup.allowedDayTypes AS allowedDayTypes,pag as parentAccessGroup ORDER BY accessGroup.name")
    List<AccessGroupQueryResult> getAccessGroupsForUnit(Long refId);


    @Query("MATCH (accessGroup:AccessGroup) WHERE id(accessGroup)={1} WITH accessGroup\n" +
            "MATCH (c:Country)-[r:" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForHub=true WITH accessGroup,accessPage \n" +
            "CREATE UNIQUE (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true, read:true, write:true}]->(accessPage) RETURN r")
    List<Map<String, Object>> setAccessPageForHubAccessGroup(Long countryId, Long accessGroupId);

    @Query("MATCH (accessGroup:AccessGroup) WHERE id(accessGroup)={1} WITH accessGroup\n" +
            "MATCH (c:Country)-[r:" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForUnion=true WITH accessGroup,accessPage \n" +
            "CREATE UNIQUE (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true, read:true, write:true}]->(accessPage) RETURN r")
    List<Map<String, Object>> setAccessPageForUnionAccessGroup(Long countryId, Long accessGroupId);

    @Query("MATCH (accessGroup:AccessGroup) WHERE id(accessGroup)={1} WITH accessGroup\n" +
            "MATCH (c:Country)-[r:" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForOrganization=true WITH accessGroup,accessPage \n" +
            "CREATE UNIQUE (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true, read:true, write:true}]->(accessPage) RETURN r")
    List<Map<String, Object>> setAccessPageForOrganizationAccessGroup(Long countryId, Long accessGroupId);


    @Query("MATCH (accessGroup:AccessGroup) WHERE id(accessGroup)={0} WITH accessGroup\n" +
            "MATCH (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true}]->(accessPage:AccessPage)  WITH accessPage, r \n" +
            "MATCH (orgAccessGroup:AccessGroup) WHERE id(orgAccessGroup)={1} WITH orgAccessGroup,accessPage,r \n" +
            "CREATE UNIQUE (orgAccessGroup)-[orgAccessPageRel:" + HAS_ACCESS_OF_TABS + "{isEnabled:true, read:r.read, write:r.write}]->(accessPage) RETURN orgAccessPageRel")
    List<Map<String, Object>> setAccessPagePermissionForAccessGroup(Long countryAccessGroupId, Long orgAccessGroupId);

    @Query("MATCH (n:AccessPage) WHERE id(n)={0} WITH n \n" +
            "OPTIONAL MATCH (n)-[:SUB_PAGE*]->(subPage:AccessPage)  WITH collect(subPage)+collect(n) AS coll UNWIND coll AS pages WITH distinct pages WITH collect(pages) AS listOfPage \n" +
            "MATCH (c:Country)  WITH c, listOfPage\n" +
            "UNWIND listOfPage AS page\n" +
            "MATCH (c)-[r:" + HAS_ACCESS_GROUP + "]-(accessGroup:AccessGroup) WHERE  r.organizationCategory = {1} WITH accessGroup, page\n" +
            "CREATE UNIQUE (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true, read:true, write:true}]->(page)")
    void addAccessPageRelationshipForCountryAccessGroups(Long accessPageId, String organizationCategory);

    @Query("MATCH (n:AccessPage) WHERE id(n)={0} WITH n \n" +
            "OPTIONAL MATCH (n)-[:SUB_PAGE*]->(subPage:AccessPage)  WITH collect(subPage)+collect(n) AS coll UNWIND coll AS pages WITH distinct pages WITH collect(pages) AS listOfPage \n" +
            "MATCH (c:Country)  WITH c, listOfPage \n" +
            "UNWIND listOfPage AS page \n" +
            "MATCH (c)-[r:" + HAS_ACCESS_GROUP + "]-(accessGroup:AccessGroup) WHERE  r.organizationCategory = {1} AND accessGroup.name<> '" + SUPER_ADMIN + "' WITH accessGroup, page \n" +
            "MATCH (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "]->(page) DELETE r")
    void removeAccessPageRelationshipForCountryAccessGroup(Long accessPageId, String organizationCategory);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "Match (org:Unit)-[:" + BELONGS_TO + "]-(c:Country) where  org.isKairosHub ={1} AND org.union={2} with org,listOfPage \n" +
            "OPTIONAL Match (org)-[:HAS_SUB_ORGANIZATION*]->(childOrg:Unit)  where childOrg.isKairosHub ={1} AND childOrg.union={2} with org+[childOrg] as allOrg,listOfPage \n" +
            "UNWIND listOfPage as page\n" +
            "UNWIND allOrg as org \n" +
            "Match (org)-[r:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(accessGroup:AccessGroup) WITH accessGroup, page \n" +
            "create unique (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true, read:true, write:true}]->(page)")
    void addAccessPageRelationshipForOrganizationAccessGroups(Long accessPageId, Boolean isKairosHub, Boolean isUnion);

    @Query("MATCH (n:AccessPage) WHERE id(n)={0} WITH n \n" +
            "OPTIONAL MATCH (n)-[:SUB_PAGE*]->(subPage:AccessPage)  WITH collect(subPage)+collect(n) AS coll UNWIND coll AS pages WITH distinct pages WITH collect(pages) AS listOfPage \n" +
            "MATCH (org:Unit)-[:" + BELONGS_TO + "]-(c:Country) WHERE  org.isKairosHub ={1} AND org.union={2} WITH org,listOfPage \n" +
            "OPTIONAL MATCH (org)-[:HAS_SUB_ORGANIZATION*]->(childOrg:Unit)  WHERE childOrg.isKairosHub ={1} AND childOrg.union={2} WITH org+[childOrg] AS allOrg,listOfPage \n" +
            "UNWIND listOfPage AS page\n" +
            "UNWIND allOrg AS org \n" +
            "MATCH (org)-[r:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(accessGroup:AccessGroup) WHERE accessGroup.name <> '" + SUPER_ADMIN + "' WITH accessGroup, page \n" +
            "MATCH (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "]->(page) DELETE r")
    void removeAccessPageRelationshipForOrganizationAccessGroup(Long accessPageId, Boolean isKairosHub, Boolean isUnion);

    @Query("MATCH (n:AccessPage) WHERE id(n)={0} WITH n\n" +
            "OPTIONAL MATCH (n)-[:" + SUB_PAGE + "*]->(subPage:AccessPage) WITH n+[subPage] AS coll UNWIND coll AS pages WITH distinct pages \n" +
            " OPTIONAL MATCH (pages)-[r:" + HAS_ACCESS_OF_TABS + "]-(ag:AccessGroup) WHERE id(ag) = {1} WITH r\n" +
            "set r.read={2}, r.write={3} RETURN distinct true")
    Boolean updatePermissionsForAccessTabsAndChildrenOfAccessGroup(Long tabId, Long accessGroupId, Boolean read, Boolean write);

    @Query("MATCH (n:AccessPage) WHERE id(n)={0} WITH n\n" +
            " OPTIONAL MATCH (n)-[r:" + HAS_ACCESS_OF_TABS + "]-(ag:AccessGroup) WHERE id(ag) = {1} WITH r\n" +
            "set r.read={2}, r.write={3} RETURN distinct true")
    Boolean updatePermissionsForAccessTabOfAccessGroup(Long tabId, Long accessGroupId, Boolean read, Boolean write);

    @Query("MATCH (accessGroup:AccessGroup),(accessPage:AccessPage) WHERE id(accessGroup)={0} and id(accessPage) IN {1}\n" +
            "MERGE (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "]->(accessPage)\n" +
            "ON CREATE SET r.isEnabled={2},r.creationDate={3},r.lastModificationDate={4},r.read={5},r.write={6}\n" +
            "ON MATCH SET r.isEnabled={2},r.lastModificationDate={4},r.read={5},r.write={6} RETURN true")
    List<Map<String, Object>> updateAccessPagePermission(long accessGroupId, List<Long> pageIds, boolean isSelected, long creationDate, long lastModificationDate, Boolean read, Boolean write);


    @Query("MATCH (accessPage:AccessPage),(accessGroup:AccessGroup) WHERE id(accessPage)={4} AND id(accessGroup)={3} WITH accessPage,accessGroup\n" +
            "OPTIONAL MATCH (accessPage)<-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]-(accessGroup) WITH accessPage, accessGroup\n" +
            "MATCH (n:Unit),(staff:Staff) WHERE id(n)={0} AND id(staff)={1} WITH n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:"+HAS_POSITIONS+"]->(position:Position)-[:"+BELONGS_TO+"]->(staff)-[:"+BELONGS_TO+"]->(user:User) WITH user,position,accessPage,accessGroup\n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit:Unit) WHERE id(unit)={2} WITH unitPermission,accessPage,accessGroup\n" +
            "MERGE (unitPermission)-[r:"+HAS_CUSTOMIZED_PERMISSION+"{accessGroupId:{3}}]->(accessPage) \n" +
            "ON CREATE SET r.read={5},r.write={6}\n" +
            "ON MATCH SET r.read={5},r.write={6} RETURN distinct true")
    void setCustomPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);

    @Query("MATCH (accessPage:AccessPage),(accessGroup:AccessGroup) WHERE id(accessPage)={4} AND id(accessGroup)={3} WITH accessPage,accessGroup\n" +
            "OPTIONAL MATCH (accessPage)-[:"+SUB_PAGE+"*]->(subPage:AccessPage)<-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]-(accessGroup) WITH [subPage]+accessPage AS coll,accessGroup AS accessGroup\n" +
            "UNWIND coll AS accessPage WITH distinct accessPage,accessGroup\n" +
            "MATCH (n:Unit),(staff:Staff) WHERE id(n)={0} AND id(staff)={1} WITH n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:"+HAS_POSITIONS+"]->(position:Position)-[:"+BELONGS_TO+"]->(staff)-[:"+BELONGS_TO+"]->(user:User) WITH user,position,accessPage,accessGroup\n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit:Unit) WHERE id(unit)={2} WITH unitPermission,accessPage,accessGroup\n" +
            "MATCH (accessGroup)-[:" + HAS_ACCESS_OF_TABS + "]->(accessPage) WITH unitPermission,accessPage,accessGroup\n" +
            "MERGE (unitPermission)-[r:"+HAS_CUSTOMIZED_PERMISSION+"{accessGroupId:{3}}]->(accessPage)\n" +
            "ON CREATE SET r.read={5},r.write={6}\n" +
            "ON MATCH SET r.read={5},r.write={6} RETURN distinct true")
    void setCustomPermissionForChildren(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);


    @Query("MATCH (accessPage:AccessPage),(accessGroup:AccessGroup) WHERE id(accessPage)={4} AND id(accessGroup)={3} WITH accessPage,accessGroup\n" +
            "MATCH (n:Unit),(staff:Staff) WHERE id(n)={0} AND id(staff)={1} WITH n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:"+HAS_POSITIONS+"]->(position:Position)-[:"+BELONGS_TO+"]->(staff)-[:"+BELONGS_TO+"]->(user:User) WITH user,position,accessPage,accessGroup\n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit:Unit) WHERE id(unit)={2} WITH unitPermission,accessPage,accessGroup\n" +
            "MATCH (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE r.accessGroupId ={3}\n" +
            "DELETE r")
    void deleteCustomPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId);


    @Query("MATCH (accessPage:AccessPage),(accessGroup:AccessGroup) WHERE id(accessPage)={4} AND id(accessGroup)={3} WITH accessPage,accessGroup\n" +
            "OPTIONAL MATCH (accessPage)-[:"+SUB_PAGE+"*]->(subPage:AccessPage)<-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]-(accessGroup) WITH accessPage+[subPage] AS coll,accessGroup AS accessGroup\n" +
            "UNWIND coll AS accessPage WITH distinct accessPage,accessGroup\n" +
            "MATCH (n:Unit),(staff:Staff) WHERE id(n)={0} AND id(staff)={1} WITH n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:"+HAS_POSITIONS+"]->(position:Position)-[:"+BELONGS_TO+"]->(staff)-[:"+BELONGS_TO+"]->(user:User) WITH user,position,accessPage,accessGroup\n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit:Unit) WHERE id(unit)={2} WITH unitPermission,accessPage,accessGroup\n" +
            "MATCH (unitPermission)-[r:"+HAS_CUSTOMIZED_PERMISSION+"]->(accessPage) WHERE r.accessGroupId={3} \n" +
            "DELETE r")
    void deleteCustomPermissionForChildren(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId);

    @Query("MATCH (accessGroup:AccessGroup)-[:" + HAS_ACCESS_OF_TABS + "{isEnabled:true}]->(accessPage:AccessPage) WITH accessPage WHERE id(accessGroup)={0} RETURN accessPage")
    List<AccessPage> getAccessPageByGroup(long accessGroupId);

    @Query("MATCH (n:Unit)-[:ORGANIZATION_HAS_ACCESS_GROUPS]->(ag:AccessGroup{typeOfTaskGiver:true}) WHERE id(n)={0} RETURN ag")
    AccessGroup findTaskGiverAccessGroup(Long organizationId);

    List<AccessGroup> findAll();

    @Query("MATCH (c:Country)-[r:" + HAS_ACCESS_GROUP + "]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND id(a)={1} RETURN a ")
    AccessGroup findCountryAccessGroupById(Long countryId, Long accessGroupId);

    @Query("MATCH (c:Country)-[r:" + HAS_ACCESS_GROUP + "]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND LOWER(a.name) = LOWER({1}) AND r.organizationCategory={2} RETURN COUNT(a)>0 ")
    Boolean isCountryAccessGroupExistWithName(Long countryId, String name, String orgCategory);

    @Query("MATCH(accountType:AccountType)-[:" + IN_COUNTRY + "]-(country:Country)-[r:" + HAS_ACCESS_GROUP + "]-(accessGroup:AccessGroup{deleted:false}) WHERE id(country)={0} AND LOWER(accessGroup.name) = LOWER({1}) AND r.organizationCategory={2} \n" +
            "MATCH(accountType)-[:" + HAS_ACCOUNT_TYPE + "]-(accessGroup)\n" +
            "WHERE id(accountType) IN {3} \n" +
            "RETURN COUNT(accessGroup)>0 ")
    Boolean isCountryAccessGroupExistWithName(Long countryId, String name, String orgCategory, Set<Long> accountTypeId);

    @Query("MATCH (o:Unit)-[r:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(a:AccessGroup{deleted:false}) WHERE id(o)={0} AND LOWER(a.name) = LOWER({1}) RETURN COUNT(a)>0 ")
    Boolean isOrganizationAccessGroupExistWithName(Long orgId, String name);

    @Query("MATCH (country:Country)-[r:" + HAS_ACCESS_GROUP + "]-(accessGroup:AccessGroup{deleted:false}) WHERE id(country)={0} AND LOWER(accessGroup.name) = LOWER({1}) AND r.organizationCategory={2} AND NOT(id(accessGroup) = {3}) RETURN COUNT(accessGroup)>0 ")
    Boolean isCountryAccessGroupExistWithNameExceptId(Long countryId, String name, String orgCategory, Long accessGroupId);

    @Query("MATCH (country:Country)-[r:" + HAS_ACCESS_GROUP + "]-(accessGroup:AccessGroup{deleted:false}) WHERE id(country)={0} AND LOWER(accessGroup.name) = LOWER({1}) AND r.organizationCategory={2} AND NOT(id(accessGroup) = {3}) \n" +
            "MATCH(accountType)-[:" + HAS_ACCOUNT_TYPE + "]-(accessGroup)\n" +
            "WHERE id(accountType) IN {4} \n" +
            "RETURN COUNT(accessGroup)>0 ")
    Boolean isCountryAccessGroupExistWithNameExceptId(Long countryId, String name, String orgCategory, Long accessGroupId, Set<Long> accountTypeId);

    @Query("MATCH (o:Unit)-[r:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(a:AccessGroup{deleted:false}) WHERE id(o)={0} AND LOWER(a.name) = LOWER({1}) AND NOT(id(a) = {2}) RETURN COUNT(a)>0 ")
    Boolean isOrganizationAccessGroupExistWithNameExceptId(Long orgId, String name, Long accessGroupId);

    @Query("MATCH (c:Country) WHERE id(c)={0}\n" +
            "OPTIONAL MATCH (c)-[r:" + HAS_ACCESS_GROUP + "{organizationCategory:'HUB'}]->(a:AccessGroup{deleted:false})  WITH COUNT(r) AS hubCount\n" +
            "OPTIONAL MATCH (c)-[r:" + HAS_ACCESS_GROUP + "{organizationCategory:'UNION'}]->(a:AccessGroup{deleted:false})  WITH COUNT(r) AS unionCount, hubCount\n" +
            "RETURN hubCount, unionCount")
    AccessGroupCountQueryResult getListOfOrgCategoryWithCountryAccessGroupCount(Long countryId);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false}) WHERE id(c)={0} AND r.organizationCategory={1} " +
            "OPTIONAL MATCH(ag)-[:" + DAY_TYPES + "]-(dayType:DayType{isEnabled:true})  \n" +
            "RETURN id(ag) AS id, ag.name AS name, ag.description AS description, ag.typeOfTaskGiver AS typeOfTaskGiver, ag.deleted AS deleted, ag.role AS role, ag.enabled AS enabled,ag.startDate AS startDate, ag.endDate AS endDate, collect(id(dayType)) AS dayTypeIds,ag.allowedDayTypes AS allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByOrgCategory(Long countryId, String orgCategory);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false,enabled:true})  WHERE id(c)={0} AND r.organizationCategory={1} " +
            "OPTIONAL MATCH(ag)-[:" + DAY_TYPES + "]-(dayType:DayType{isEnabled:true}) WHERE  (ag.endDate IS NULL OR date(ag.endDate) >= date()) " +
            "RETURN id(ag) AS id, ag.name AS name, ag.description AS description, ag.typeOfTaskGiver AS typeOfTaskGiver, ag.deleted AS deleted, ag.role AS role, ag.enabled AS enabled,ag.startDate AS startDate, ag.endDate AS endDate, collect(dayType) AS dayTypes,ag.allowedDayTypes AS allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByCategory(Long countryId, String organizationCategory);

    @Query("MATCH(position:Position)-[:" + HAS_UNIT_PERMISSIONS + "]-(unitPermission:UnitPermission) WHERE id(position) IN {0} \n" +
            "MATCH(unitPermission)-[rel_has_access_group:" + HAS_ACCESS_GROUP + " ]-(ag:AccessGroup) OPTIONAL MATCH(unitPermission)-[rel_has_customized_permission:" + HAS_CUSTOMIZED_PERMISSION + "]-" +
            "(accesspage:AccessPage) delete rel_has_access_group, rel_has_customized_permission")
    void deleteAccessGroupRelationAndCustomizedPermissionRelation(List<Long> positionIds);

    @Query("MATCH(org:Unit) WHERE id(org) IN {0} MATCH(ag:AccessGroup) WHERE id(ag)={1} \n" +
            "MERGE(org)-[:ORGANIZATION_HAS_ACCESS_GROUPS]-(ag)")
    void createAccessGroupUnitRelation(List<Long> orgIds, Long accessGroupId);

    @Query("MATCH (org:Unit) WHERE id(org)={0} WITH org\n" +
            "MATCH (org)-[:"+HAS_POSITIONS+"]-(position:Position)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) WHERE id(user)={3} WITH position\n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]-(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(unit:Unit) WHERE id(unit)={1} \n" +
            "MATCH (up)-[:"+HAS_ACCESS_GROUP+"]-(ag:AccessGroup) WHERE ag.role={2} RETURN count(ag) > 0")
    Boolean checkIfUserHasAccessByRoleInUnit(Long parentOrgId, Long unitId, String role, Long userId);


    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false, enabled:true}) WHERE id(c)={0} AND r.organizationCategory={1} AND ag.role={2} AND (ag.endDate IS NULL OR date(ag.endDate) >= date()) \n" +
            "RETURN id(ag) AS id, ag.name AS name, ag.description AS description, ag.typeOfTaskGiver AS typeOfTaskGiver, ag.deleted AS deleted, ag.role AS role,ag.allowedDayTypes AS allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByOrgCategoryAndRole(Long countryId, String orgCategory, String role);

    @Query("MATCH (org:Organization)-[r:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(ag:AccessGroup{deleted:false}) WHERE id(org)={0} AND ag.role={1}\n" +
            "RETURN id(ag) AS id, ag.name AS name, ag.description AS description, ag.typeOfTaskGiver AS typeOfTaskGiver, ag.deleted AS deleted, ag.role AS role")
    List<AccessGroupQueryResult> getOrganizationAccessGroupByRole(Long organizationId, String role);

    @Query("MATCH (org:Unit)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(ag:AccessGroup) WHERE id(org)={0} and id(ag) IN {1} \n" +
            "WITH ag,org  MATCH(ag)-[:" + HAS_ACCESS_GROUP + "]-(up:UnitPermission) WITH up,ag MATCH(up)-[:" + HAS_UNIT_PERMISSIONS + "]-(position:Position) WITH ag,position\n" +
            "MATCH (position)-[:" + BELONGS_TO + "]-(s:Staff) RETURN  id(ag) AS accessGroupId,collect(id(s)) AS staffIds ")
    List<StaffIdsQueryResult> getStaffIdsByUnitIdAndAccessGroupId(Long unitId, List<Long> accessGroupId);

    //TODO HARISH Please check this query. it can be optimized
    @Query("MATCH (org:Unit)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(ag:AccessGroup) WHERE id(org)={0} and id(ag) IN {1} \n" +
            "WITH ag,org  MATCH(ag)-[:" + HAS_ACCESS_GROUP + "]-(up:UnitPermission) WITH up,ag MATCH(up)-[:" + HAS_UNIT_PERMISSIONS + "]-(position:Position) WITH ag,position\n" +
            "MATCH (position)-[:" + BELONGS_TO + "]-(s:Staff)\n" +
            "MATCH (s)-[:" + BELONGS_TO + "]-(ps:Position)  MATCH (ps)-[:" + HAS_UNIT_PERMISSIONS + "]-(unitP:UnitPermission)\n" +
            "MATCH (unitP)-[:" + HAS_ACCESS_GROUP + "]-(agp:AccessGroup) RETURN id(s) AS staffId, Collect(DISTINCT id(agp)) AS accessGroupIds")
    List<StaffAccessGroupQueryResult> getStaffIdsAndAccessGroupsByUnitId(Long unitId, List<Long> accessGroupId);

    @Query("MATCH (c:Country)-[r:" + HAS_ACCESS_GROUP + "]->(ag:AccessGroup{deleted:false})-[:" + HAS_ACCOUNT_TYPE + "]->(accountType:AccountType) WHERE id(c)={0} AND id(accountType)={1} AND ag.role IN {2}" +
            "OPTIONAL MATCH (ag)-[:" + DAY_TYPES + "]-(dayType:DayType) \n" +
            "RETURN id(ag) AS id, ag.name AS name, ag.description AS description, ag.typeOfTaskGiver AS typeOfTaskGiver, ag.role AS role, ag.enabled AS enabled , ag.startDate AS startDate, ag.endDate AS endDate, collect(id(dayType)) AS dayTypeIds,ag.allowedDayTypes AS allowedDayTypes")
    List<AccessGroupQueryResult> getCountryAccessGroupByAccountTypeId(Long countryId, Long accountTypeId, List<String> role);


    @Query("MATCH (staff:Staff),(org:Unit) WHERE id(staff)={0} AND id(org)={1} WITH org,staff " +
            "MATCH(org)<-[:HAS_SUB_ORGANIZATION*]-(parentOrganization:Unit) WITH org,parentOrganization,staff  " +
            "MATCH(parentOrganization)-[:" + BELONGS_TO + "] -> (country:Country) WITH org,staff,country " +
            "MATCH (staff)-[:" + BELONGS_TO + "]-(position:Position)-[:" + HAS_UNIT_PERMISSIONS + "]-(up:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]-(org) WITH up,country  " +
            "MATCH (up)-[:HAS_ACCESS_GROUP]-(ag) RETURN Collect(DISTINCT id(ag)) AS accessGroupIds ,id(country) AS countryId")
    StaffAccessGroupQueryResult getAccessGroupIdsByStaffIdAndUnitId(Long staffId, Long unitId);


    @Query("MATCH (organization:Unit) WHERE id(organization)={0}\n" +
            "MATCH (organization)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(accessGroup:AccessGroup{deleted:false}) WHERE id(accessGroup)={1}" +
            "OPTIONAL MATCH (accessGroup)-[:" + DAY_TYPES + "]-(dayType:DayType)   " +
            "RETURN id(accessGroup) AS id, accessGroup.name AS name, accessGroup.description AS description, accessGroup.typeOfTaskGiver AS typeOfTaskGiver, accessGroup.deleted AS deleted, accessGroup.role AS role, accessGroup.enabled AS enabled,accessGroup.startDate AS startDate, accessGroup.endDate AS endDate, collect(dayType) AS dayTypes,accessGroup.allowedDayTypes AS allowedDayTypes")
    AccessGroupQueryResult findByAccessGroupId(long unitId, long accessGroupId);

    @Query("MATCH(countryAccessGroup:AccessGroup{deleted:false})<-[:" + HAS_PARENT_ACCESS_GROUP + "]-(unitAccessGroup:AccessGroup{deleted:false})-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(org:Organization) WHERE id(org) ={0} AND id(countryAccessGroup) ={1} " +
            "RETURN unitAccessGroup")
    AccessGroup getAccessGroupByParentAccessGroupId(Long unitId, Long parentId);

    @Query("MATCH(unitAccessGroup:AccessGroup{deleted:false})<-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(org:Unit) WHERE id(org) ={0} AND id(unitAccessGroup) ={1} " +
            "RETURN unitAccessGroup")
    AccessGroup getAccessGroupByParentId(Long unitId, Long parentId);

    @Query("MATCH (organization:Organization) WHERE id(organization) IN {0}\n" +
            "MATCH (organization)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]->(accessGroup:AccessGroup{deleted:false}) detach delete accessGroup")
    void removeDefaultCopiedAccessGroup(List<Long> organizationId);

    @Query("MATCH (organization) WHERE id(organization) = {0} \n" +
            "MATCH(organization)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(ag:AccessGroup)-[:" + HAS_PARENT_ACCESS_GROUP + "]-(pag:AccessGroup) RETURN id(ag) AS id,id(pag) AS parentId")
    List<AccessPageQueryResult> findAllAccessGroupWithParentOfOrganization(Long organizationId);

    @Query("MATCH (organization:Unit) WHERE id(organization) = {0} \n" +
            "MATCH(organization)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(ag:AccessGroup)-[:" + HAS_PARENT_ACCESS_GROUP + "]-(pag:AccessGroup) WHERE ID(pag) IN {1} RETURN id(ag) AS id,id(pag) AS parentId")
    List<AccessPageQueryResult> findAllAccessGroupWithParentIds(Long organizationId, Set<Long> parentAccessGroupsIds);


    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(position:Position)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]-(organization)\n" +
            "WHERE id(organization)={0} AND id(user)={1}\n" +
            "MATCH (unitPermission)-[:" + HAS_ACCESS_GROUP + "]->(accessGroup:AccessGroup)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(organization)\n" +
            "OPTIONAL MATCH(accessGroup)-[:" + DAY_TYPES + "]->(dayType:DayType)\n" +
            "OPTIONAL MATCH(dayType)-[:" + DAY_TYPE + "]-(chc:CountryHolidayCalender)\n" +
            "WITH organization,id(staff) AS staffId,accessGroup,dayType,\n" +
            "COLLECT(DISTINCT {id:id(chc),holidayDate:chc.holidayDate,holidayType:chc.holidayType,startTime:chc.startTime,endTime:chc.endTime}) AS countryHolidayCalender\n" +
            "WITH organization, staffId,accessGroup,countryHolidayCalender,\n" +
            "COLLECT(DISTINCT {id:id(dayType),holidayType:dayType.holidayType,validDays:dayType.validDays,name:dayType.name,allowTimeSettings:dayType.allowTimeSettings,countryHolidayCalenders:countryHolidayCalender}) AS dayType\n" +
            "RETURN\n" +
            "organization,staffId,COLLECT({accessGroup:{id:id(accessGroup),name:accessGroup.name,role:accessGroup.role,startDate:accessGroup.startDate,allowedDayTypes:accessGroup.allowedDayTypes},dayTypes:dayType}) AS dayTypesByAccessGroup")
    AccessGroupStaffQueryResult getAccessGroupDayTypesAndUserId(Long unitId, Long userId);


    @Query("MATCH(staff:Staff)<-[:" + BELONGS_TO + "]-(position:Position)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]-(organization:Unit)\n" +
            "WHERE id(organization)={0} AND id(staff)={1}\n" +
            "MATCH (unitPermission)-[:" + HAS_ACCESS_GROUP + "]->(accessGroup:AccessGroup)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(organization)\n" +
            "OPTIONAL MATCH(accessGroup)-[:" + DAY_TYPES + "]->(dayType:DayType)\n" +
            "OPTIONAL MATCH(dayType)-[:" + DAY_TYPE + "]-(chc:CountryHolidayCalender)\n" +
            "WITH organization,id(staff) AS staffId,accessGroup,dayType,\n" +
            "COLLECT(DISTINCT {id:id(chc),holidayDate:chc.holidayDate,holidayType:chc.holidayType,startTime:chc.startTime,endTime:chc.endTime}) AS countryHolidayCalender\n" +
            "WITH organization, staffId,accessGroup,countryHolidayCalender,\n" +
            "COLLECT(DISTINCT {id:id(dayType),holidayType:dayType.holidayType,validDays:dayType.validDays,name:dayType.name,allowTimeSettings:dayType.allowTimeSettings,countryHolidayCalenders:countryHolidayCalender}) AS dayType\n" +
            "RETURN\n" +
            "organization,staffId,COLLECT({accessGroup:{id:id(accessGroup),name:accessGroup.name,role:accessGroup.role,startDate:accessGroup.startDate,allowedDayTypes:accessGroup.allowedDayTypes},dayTypes:dayType}) AS dayTypesByAccessGroup")
    AccessGroupStaffQueryResult getAccessGroupDayTypesAndStaffId(Long unitId, Long staffId);

    @Query("MATCH(ag:AccessGroup)-[r:" + DAY_TYPES + "]->(dayType:DayType) WHERE id(ag)={0} DETACH DELETE r\n")
    void unlinkDayTypes(Long accessGroupId);
}


