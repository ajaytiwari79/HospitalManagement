package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.access_permission.AccessGroupCountQueryResult;
import com.kairos.persistence.model.user.access_permission.AccessGroupQueryResult;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.staff.Staff;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
            "Match (organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(accessGroup:AccessGroup{deleted:false}) WHERE NOT (accessGroup.name='"+AG_COUNTRY_ADMIN+"') return accessGroup")
    List<AccessGroup> getAccessGroups(long unitId);

    @Query("Match (root:Organization) where id(root)={0} with root\n" +
            "Match (root)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={1} with employment\n" +
            "Match (employment)-[:"+ HAS_UNIT_PERMISSIONS +"]->(unitEmployment:UnitEmployment)-[:"+ APPLICABLE_IN_UNIT +"]->(unit:Organization) with unitEmployment\n" +
            "MATCH (unitEmployment)-[:"+HAS_ACCESS_PERMISSION+"]->(accessPermission:AccessPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup) with accessPermission\n" +
            "Match (accessPermission)-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]->(accessPage:AccessPage{moduleId:{2}}) return {readPermission:collect(r.isRead),writePermission:collect(r.isWrite)} as data")
    Map<String,Object> getAccessPermission(long organizationId, long userId, String pageId);

    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup)={0}\n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPagePermission(long accessGroupId);

    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={1} WITH accessGroup\n" +
            "OPTIONAL Match (c:Country)-[r:"+HAS_ACCESS_FOR_ORG_CATEGORY+"]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForHub=true WITH accessGroup,accessPage \n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false, read:true, write:true}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPageForHubAccessGroup(Long countryId, Long accessGroupId);

    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={1} WITH accessGroup\n" +
            "OPTIONAL Match (c:Country)-[r:"+HAS_ACCESS_FOR_ORG_CATEGORY+"]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForUnion=true WITH accessGroup,accessPage \n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false, read:true, write:true}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPageForUnionAccessGroup(Long countryId, Long accessGroupId);

    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={1} WITH accessGroup\n" +
            "OPTIONAL Match (c:Country)-[r:"+HAS_ACCESS_FOR_ORG_CATEGORY+"]-(accessPage:AccessPage) WHERE id(c)={0} AND r.accessibleForOrganization=true WITH accessGroup,accessPage \n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false, read:true, write:true}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPageForOrganizationAccessGroup(Long countryId, Long accessGroupId);

    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup) IN {0}\n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPagePermission(List<Long> accessGroupIds);

    @Query("Match (accessGroup:AccessGroup) where id(accessGroup)={0} WITH accessGroup\n" +
            "Match (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage)  WITH accessPage, r \n" +
            "Match (orgAccessGroup:AccessGroup) where id(orgAccessGroup)={1} WITH orgAccessGroup,accessPage,r \n" +
            "create unique (orgAccessGroup)-[orgAccessPageRel:"+HAS_ACCESS_OF_TABS+"{isEnabled:false, read:r.read, write:r.write}]->(accessPage) return orgAccessPageRel")
    List<Map<String,Object>> setAccessPagePermissionForAccessGroup(Long countryAccessGroupId, Long orgAccessGroupId);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "MATCH (c:Country) WHERE id(c)={1} WITH c, listOfPage\n" +
            "UNWIND listOfPage as page\n" +
            "Match (c)-[r:"+HAS_ACCESS_GROUP+"]-(accessGroup:AccessGroup) WHERE  r.organizationCategory = {2} WITH accessGroup, page\n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false}]->(page)")
    void addAccessPageRelationshipForCountryAccessGroups(Long accessPageId, Long countryId, String organizationCategory);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "MATCH (c:Country) WHERE id(c)={1} WITH c, listOfPage \n" +
            "UNWIND listOfPage as page \n" +
            "Match (c)-[r:"+HAS_ACCESS_GROUP+"]-(accessGroup:AccessGroup) WHERE  r.organizationCategory = {2} WITH accessGroup, page \n" +
            "MATCH (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(page) DELETE r")
    void removeAccessPageRelationshipForCountryAccessGroup(Long accessPageId, Long countryId, String organizationCategory);

    @Query("Match (n:AccessPage) where id(n)=2801 with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "Match (org:Organization)-[:BELONGS_TO]-(c:Country) where id(c)=4 AND org.isKairosHub =true AND org.union=false with org,listOfPage \n" +
            "OPTIONAL Match (org)-[:HAS_SUB_ORGANIZATION*]->(childOrg:Organization)  with org+[childOrg] as allOrg,listOfPage \n" +
            "UNWIND listOfPage as page\n" +
            "UNWIND allOrg as org \n" +
            "Match (org)-[r:ORGANIZATION_HAS_ACCESS_GROUPS]-(accessGroup:AccessGroup) WITH accessGroup, page \n"+
            "create unique (accessGroup)-[r:HAS_ACCESS_OF_TABS{isEnabled:false}]->(page)")
    void addAccessPageRelationshipForOrganizationAccessGroups(Long accessPageId, Long countryId, Boolean isKairosHub, Boolean isUnion);

    @Query("Match (n:AccessPage) where id(n)=2801 with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "Match (org:Organization)-[:BELONGS_TO]-(c:Country) where id(c)=4 AND org.isKairosHub =true AND org.union=false with org,listOfPage \n" +
            "OPTIONAL Match (org)-[:HAS_SUB_ORGANIZATION*]->(childOrg:Organization)  with org+[childOrg] as allOrg,listOfPage \n" +
            "UNWIND listOfPage as page\n" +
            "UNWIND allOrg as org \n" +
            "Match (org)-[r:ORGANIZATION_HAS_ACCESS_GROUPS]-(accessGroup:AccessGroup) WITH accessGroup, page \n"+
            "MATCH (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(page) DELETE r")
    void removeAccessPageRelationshipForOrganizationAccessGroup(Long accessPageId, Long countryId, Boolean isKairosHub, Boolean isUnion);

    @Query("Match (n:AccessPage) where id(n)={0} with n\n" +
            "Optional Match (n)-[:" + SUB_PAGE + "*]->(subPage:AccessPage) with n+[subPage] as coll unwind coll as pages with distinct pages \n"+
            " Optional Match (pages)-[r:"+HAS_ACCESS_OF_TABS+"]-(ag:AccessGroup) WHERE id(ag) = {1} WITH r\n"+
            "set r.read={2}, r.write={3} return distinct true")
    Boolean updatePermissionsForAccessTabsOfAccessGroup(Long tabId, Long accessGroupId, Boolean read, Boolean write);


    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup)={0} and id(accessPage) IN {1}\n" +
            "MERGE (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(accessPage)\n" +
            "ON CREATE SET r.isEnabled={2},r.creationDate={3},r.lastModificationDate={4}\n" +
            "ON MATCH SET r.isEnabled={2},r.lastModificationDate={4} return true")
    List<Map<String,Object>> updateAccessPagePermission(long accessGroupId, List<Long> pageIds, boolean isSelected, long creationDate, long lastModificationDate);

    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
            "optional match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPage+[subPage] as coll,accessGroup as accessGroup\n" +
            "unwind coll as accessPage with distinct accessPage,accessGroup\n" +
            "Match (n:Organization),(staff:Staff),(accessPage:AccessPage) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitEmp:UnitEmployment)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitEmp,accessPage,accessGroup\n" +
            "Match (unitEmp)-[:HAS_ACCESS_PERMISSION]->(ap:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup) with ap,accessPage\n" +
            "Merge (ap)-[r:HAS_ACCESS_PAGE_PERMISSION]->(accessPage)\n" +
            "ON CREATE SET r.isRead={5},r.isWrite={6}\n" +
            "ON MATCH SET  r.isRead={5},r.isWrite={6} return distinct true")
    void setPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);

    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
            "optional match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPage+[subPage] as coll,accessGroup as accessGroup\n" +
            "unwind coll as accessPage with distinct accessPage,accessGroup\n" +
            "Match (n:Organization),(staff:Staff),(accessPage:AccessPage) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage,accessGroup\n" +
           "MERGE (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION]->(accessPage)\n" +
            "ON CREATE SET r.isRead={5},r.isWrite={6}\n" +
            "ON MATCH SET r.isRead={5},r.isWrite={6} return distinct true")
    void setCustomPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);


    @Query("Match (accessPage:AccessPage),(accessGroup:AccessGroup) where id(accessPage)={4} AND id(accessGroup)={3} with accessPage,accessGroup\n" +
            "optional match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPage+[subPage] as coll,accessGroup as accessGroup\n" +
            "unwind coll as accessPage with distinct accessPage,accessGroup\n" +
            "Match (n:Organization),(staff:Staff),(accessPage:AccessPage) where id(n)={0} AND id(staff)={1} with n,staff,accessPage,accessGroup\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage,accessGroup\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage,accessGroup\n" +
            "MATCH (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION]->(accessPage)\n" +
            "DELETE r")
    void deleteCustomPermissionForTab(long organizationId, long staffId, long unitId, long accessGroupId, long accessPageId, boolean isRead, boolean isWrite);


    @Query("Match (employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={1} with employment\n" +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitEmployment:UnitEmployment)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={0} with unitEmployment \n" +
            "Match (unitEmployment)-[:HAS_ACCESS_PERMISSION{isEnabled:true}]->(ap:AccessPermission) with ap\n" +
            "Match (ap)-[r:HAS_ACCESS_PAGE_PERMISSION]->(tab:AccessPage{moduleId:{2}}) with collect(r.isRead) as permission\n" +
            "return true in permission")
    boolean hasReadPermission(long unitId, long userId, String tabId);

    @Query("Match (employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={1} with employment\n" +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitEmployment:UnitEmployment)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={0} with unitEmployment\n" +
            "Match (unitEmployment)-[:HAS_ACCESS_PERMISSION{isEnabled:true}]->(ap:AccessPermission) with ap\n" +
            "Match (ap)-[r:HAS_ACCESS_PAGE_PERMISSION]->(tab:AccessPage{moduleId:{2}}) with collect(r.isWrite) as permission\n" +
            "return true in permission")
    boolean hasWritePermission(long unitId, long userId, String tabId);

    @Query("Match (accessGroup:AccessGroup)-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) with accessPage where id(accessGroup)={0} return accessPage")
    List<AccessPage> getAccessPageByGroup(long accessGroupId);

    @Query("MATCH (accessGroup:AccessGroup) WHERE id(accessGroup) IN {0} return accessGroup")
    List<AccessGroup> getAccessGroupById(List<Long> accessGrpIds);

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

    @Query("MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} AND LOWER(a.name) = LOWER({1}) AND r.organizationCategory={2} AND NOT(id(a) = {3}) return COUNT(a)>0 ")
    Boolean isCountryAccessGroupExistWithNameExceptId(Long countryId, String name, String orgCategory, Long accessGroupId);

    @Query("OPTIONAL MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"{organizationCategory:'HUB'}]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} WITH COUNT(r) as hubCount\n" +
            "OPTIONAL MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"{organizationCategory:'UNION'}]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} WITH COUNT(r) as unionCount, hubCount\n" +
            "OPTIONAL MATCH (c:Country)-[r:"+HAS_ACCESS_GROUP+"{organizationCategory:'ORGANIZATION'}]-(a:AccessGroup{deleted:false}) WHERE id(c)={0} RETURN  COUNT(r) as organizationCount, hubCount, unionCount")
    AccessGroupCountQueryResult getListOfOrgCategoryWithCountryAccessGroupCount(Long countryId);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false}) WHERE id(c)={0} AND r.organizationCategory={1} \n" +
            "RETURN id(ag) as id, ag.name as name, ag.description as description, ag.typeOfTaskGiver as typeOfTaskGiver, ag.deleted as deleted")
    List<AccessGroupQueryResult> getCountryAccessGroupByOrgCategory(Long countryId, String orgCategory);

    @Query("MATCH (c:Country)-[r:HAS_ACCESS_GROUP]->(ag:AccessGroup{deleted:false}) WHERE id(c)={0} AND r.organizationCategory={1} RETURN ag")
    List<AccessGroup> getCountryAccessGroupByCategory(Long countryId, String organizationCategory);

    // For Test cases
    @Query("Match (accessGroup:AccessGroup)-[:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) with accessPage where id(accessGroup)={0} return accessPage")
    List<Long> getAccessPageIdsByAccessGroup(long accessGroupId);

    @Query("Match (o:Organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(ag:AccessGroup {name:{1}}) WHERE id(o)={0} return ag")
    AccessGroup getAccessGroupOfOrganizationByName(long organizationId, String name);
}

