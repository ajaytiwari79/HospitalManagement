package com.kairos.persistence.repository.user.access_profile;


import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.staff.Staff;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 9/19/16.
 */
@Repository
public interface AccessGroupRepository extends GraphRepository<AccessGroup> {

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
            "Match (organization)-[:"+ORGANIZATION_HAS_ACCESS_GROUPS+"]->(accessGroup:AccessGroup{isEnabled:true}) return accessGroup")
    List<AccessGroup> getAccessGroups(long unitId);

    @Query("Match (root:Organization) where id(root)={0} with root\n" +
            "Match (root)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={1} with employment\n" +
            "Match (employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit:Organization) with unitEmployment\n" +
            "MATCH (unitEmployment)-[:"+HAS_ACCESS_PERMISSION+"]->(accessPermission:AccessPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup) with accessPermission\n" +
            "Match (accessPermission)-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]->(accessPage:AccessPage{moduleId:{2}}) return {readPermission:collect(r.isRead),writePermission:collect(r.isWrite)} as data")
    Map<String,Object> getAccessPermission(long organizationId, long userId, String pageId);

    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup)={0}\n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPagePermission(long accessGroupId);

    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup) IN {0}\n" +
            "create unique (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:false}]->(accessPage) return r")
    List<Map<String,Object>> setAccessPagePermission(List<Long> accessGroupIds);

    @Query("Match (accessGroup:AccessGroup),(accessPage:AccessPage) where id(accessGroup)={0} and id(accessPage) IN {1}\n" +
            "MERGE (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"]->(accessPage)\n" +
            "ON CREATE SET r.isEnabled={2},r.creationDate={3},r.lastModificationDate={4}\n" +
            "ON MATCH SET r.isEnabled={2},r.lastModificationDate={4} return true")
    List<Map<String,Object>> updateAccessPagePermission(long accessGroupId,List<Long> pageIds,boolean isSelected,long creationDate,long lastModificationDate);

    @Query("Match (n:Organization),(staff:Staff),(accessPage:AccessPage) where id(n)={0} AND id(staff)={1} AND id(accessPage)={4} with n,staff,accessPage\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage\n" +
            "Match (emp)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmp:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) where id(unit)={2} with unitEmp,accessPage\n" +
            "Match (unitEmp)-[:HAS_ACCESS_PERMISSION]->(ap:AccessPermission)-[:HAS_ACCESS_GROUP]->(g:AccessGroup) where id(g)={3} with ap,accessPage\n" +
            "Merge (ap)-[r:HAS_ACCESS_PAGE_PERMISSION]->(accessPage)\n" +
            "ON CREATE SET r.isRead={5},r.isWrite={6}\n" +
            "ON MATCH SET  r.isRead={5},r.isWrite={6}")
    void setPermissionForTab(long organizationId,long staffId,long unitId,long accessGroupId,long accessPageId,boolean isRead,boolean isWrite);

    @Query("MATCH (org:Organization),(unit:Organization) where id(org)={0} AND id(unit)={1} with org,unit\n" +
            "MATCH (org)-[:"+HAS_EMPLOYMENTS+"]->(emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={2} with emp,org,unit\n" +
            "Match (emp)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmp:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit) with unitEmp\n" +
            "Match (unitEmp)-[:"+HAS_ACCESS_PERMISSION+"]->(ap:AccessPermission) with ap\n" +
            "Match (ap)-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]->(tab:AccessPage{moduleId:{3}}) with collect(r.isRead) as permission\n" +
            "return true in permission")
    boolean hasReadPermission(long orgId,long unitId,long userId,String tabId);

    @Query("MATCH (org:Organization),(unit:Organization) where id(org)={0} AND id(unit)={1} with org,unit\n" +
            "MATCH (org)-[:"+HAS_EMPLOYMENTS+"]->(emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={2} with emp,org,unit\n" +
            "Match (emp)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmp:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit) with unitEmp\n" +
            "Match (unitEmp)-[:"+HAS_ACCESS_PERMISSION+"]->(ap:AccessPermission) with ap\n" +
            "Match (ap)-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]->(tab:AccessPage{moduleId:{3}}) with collect(r.isWrite) as permission\n" +
            "return true in permission")
    boolean hasWritePermission(long orgId,long unitId,long userId,String tabId);
}
