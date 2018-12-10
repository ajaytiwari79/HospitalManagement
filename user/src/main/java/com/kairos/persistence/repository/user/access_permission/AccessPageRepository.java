package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.auth.StaffPermissionQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.AppConstants.HAS_ACCESS_OF_TABS;
import static com.kairos.constants.AppConstants.ACCESS_PAGE_HAS_LANGUAGE;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by arvind on 24/10/16.
 */

@Repository
public interface AccessPageRepository extends Neo4jBaseRepository<AccessPage, Long> {
    @Query("Match (org:Organization) where id(org)={0}\n" +
            "match (org)-[:" + HAS_SUB_ORGANIZATION + "*]->(n)\n" +
            "Match (n)-[:HAS_GROUP]->(group:Group)-[:" + HAS_TEAM + "]->(team:Team)-[:" + TEAM_HAS_MEMBER + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={1} with staff\n" +
            "Match (staff)-[:" + STAFF_HAS_ACCESS_GROUP + "]->(accessGroup:AccessGroup)-[r:" + ACCESS_GROUP_HAS_ACCESS_TO_PAGE + "{read:false}]->(accessPage:AccessPage{isModule:true}) return distinct accessPage")
    List<AccessPage> getAccessModulesForUnits(long parentOrganizationId, long userId);

    @Query("Match (n:UnitPermission)-[:" + HAS_ACCESS_PERMISSION + "]->(accessPermission:AccessPermission)-[r:" + HAS_ACCESS_PAGE_PERMISSION + "]->(p:AccessPage) where id(n)={0} AND id(p)={1} SET r.isRead={2} return r")
    void modifyAccessPagePermission(long unitEmploymentId, long accessPageId, boolean value);

    @Query("MATCH path=(accessPage:AccessPage{active:true})-[:" + SUB_PAGE + "*]->(subPage:AccessPage{active:true}) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with ps\n" +
            "match (accessGroup:AccessGroup) where id(accessGroup)={0} with accessGroup,ps\n" +
            "optional match (parent:AccessPage)<-[r2:" + HAS_ACCESS_OF_TABS + "]-(accessGroup)\n" +
            "where id(parent)=id(ps.p) with r2,ps,accessGroup\n" +
            "optional match (child:AccessPage)<-[r:" + HAS_ACCESS_OF_TABS + "]-(accessGroup)\n" +
            "where id(child)=id(ps.c) with r,r2,ps,accessGroup\n" +
            "return {name:ps.p.name,id:id(ps.p),selected:case when r2.isEnabled then true else false end,module:ps.p.isModule,children:collect({name:ps.c.name,id:id(ps.c),selected:case when r.isEnabled then true else false end})} as data\n" +
            "UNION\n" +
            "Match (accessPage:AccessPage{isModule:true,active:true}) where not (accessPage)-[:" + SUB_PAGE + "]->() with accessPage\n" +
            "match (accessGroup:AccessGroup) where id(accessGroup)={0} with accessGroup,accessPage\n" +
            "optional match (accessPage:AccessPage)<-[r:" + HAS_ACCESS_OF_TABS + "]-(accessGroup)\n" +
            "return {name:accessPage.name,id:id(accessPage),selected:case when r.isEnabled then true else false end,module:accessPage.isModule,children:[]} as data")
    List<Map<String, Object>> getAccessPageHierarchy(long accessGroupId);


    // Fetch access page hierarchy show only selected access page
    @Query("match (ag:AccessGroup) where id(ag)={0} WITH ag \n" +
            "MATCH path=(accessPage:AccessPage{active:true})-[:SUB_PAGE*]->(subPage:AccessPage{active:true})-[:HAS_ACCESS_OF_TABS]-(ag) \n" +
            "WITH NODES(path) AS np,ag WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-3, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs,ag UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps,ag with ps,ag\n" +
            "optional match (parent:AccessPage)<-[r2:HAS_ACCESS_OF_TABS]-(ag)\n" +
            "where id(parent)=id(ps.p) with r2,ps,ag\n" +
            "optional match (child:AccessPage)<-[r:HAS_ACCESS_OF_TABS]-(ag)\n" +
            "where id(child)=id(ps.c) with r,r2,ps,ag\n" +
            "return {name:ps.p.name,id:id(ps.p),selected:case when r2.isEnabled then true else false end, read:r2.read, write:r2.write,module:ps.p.isModule,children:collect({name:ps.c.name,id:id(ps.c),read:r.read, write:r.write,selected:case when r.isEnabled then true else false end})} as data\n" +
            "UNION\n" +
            // Fetch modules which does not have child
            "match (ag:AccessGroup) where id(ag)={0} WITH ag \n" +
            "Match (accessPage:AccessPage{isModule:true,active:true}) where not (accessPage)-[:SUB_PAGE]->() with accessPage, ag\n" +
            "Match (accessPage)<-[r:HAS_ACCESS_OF_TABS]-(ag) with accessPage, ag,r\n" +
            "return {name:accessPage.name,id:id(accessPage),read:r.read, write:r.write,selected:case when r.isEnabled then true else false end,module:accessPage.isModule,children:[]} as data")
    List<Map<String,Object>> getSelectedAccessPageHierarchy(Long accessGroupId);


    @Query("Match (accessGroup:AccessGroup),(accessPermission:AccessPermission) where id(accessPermission)={0} AND id(accessGroup)={1}\n" +
            "Match (accessGroup)-[r:" + HAS_ACCESS_OF_TABS + "{isEnabled:true}]->(accessPage:AccessPage) with accessPage,r,accessPermission\n" +
            "Create unique (accessPermission)-[:" + HAS_ACCESS_PAGE_PERMISSION + "{isEnabled:r.isEnabled,isRead:true,isWrite:false}]->(accessPage) return accessPermission")
    List<AccessPage> setDefaultPermission(long accessPermissionId, long accessGroupId);

    @Query("MATCH path=(accessPage:AccessPage)-[:" + SUB_PAGE + "*]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with ps\n" +
            "Match (accessPermission:AccessPermission) where id(accessPermission)={0} with accessPermission,ps\n" +
            "optional match (parent:AccessPage)<-[r2:" + HAS_ACCESS_PAGE_PERMISSION + "]-(accessPermission)\n" +
            "where id(parent)=id(ps.p) with r2,ps,accessPermission\n" +
            "optional match (child:AccessPage)<-[r:" + HAS_ACCESS_PAGE_PERMISSION + "]-(accessPermission)\n" +
            "where id(child)=id(ps.c) with r,r2,ps\n" +
            "return {name:ps.p.name,id:id(ps.p),selected:r2.isEnabled,module:ps.p.isModule,children:collect({name:ps.c.name,id:id(ps.c),selected:r.isEnabled})} as data")
    List<Map<String, Object>> getStaffPermission(long accessPermissionId);

    @Query("MATCH (n:Organization)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(n)={0} AND  id(staff)={2}\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={1}\n" +
            "Match (unitPermission)-[:HAS_ACCESS_PERMISSION{isEnabled:true}]->(ap:AccessPermission)-[:HAS_ACCESS_GROUP]->(g:AccessGroup) where id(g)={3} with ap,g\n" +
            "Match (g)-[:HAS_ACCESS_OF_TABS{isEnabled:true}]->(accessPage:AccessPage{isModule:true}) with accessPage,ap,g\n" +
            "MATCH path=(accessPage)-[:SUB_PAGE*]->() WITH NODES(path) AS np,g as g,ap as ap with REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1],g:g,ap:ap}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with distinct ps.g as g,ps as ps,ps.ap as ap\n" +
            "match (child:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g)\n" +
            "where id(child)=id(ps.c) with ps,child,g,ap\n" +
            "match (parent:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g)\n" +
            "where id(parent)=id(ps.p) with ps,parent,child,ap\n" +
            "optional Match (ap)-[r2:HAS_ACCESS_PAGE_PERMISSION]->(child) with ps,parent,r2,ap\n" +
            "optional Match (ap)-[r:HAS_ACCESS_PAGE_PERMISSION]->(parent) with ps,parent,r2,r\n" +
            "return {name:ps.p.name,id:id(ps.p),read:r.isRead,write:r.isWrite,module:ps.p.isModule,moduleId:ps.p.moduleId,children:collect( distinct {name:ps.c.name,id:id(ps.c),moduleId:ps.c.moduleId,read:r2.isRead,write:r2.isWrite})} as data")
    List<Map<String, Object>> getAccessPageByAccessGroup(long orgId, long unitId, long staffId, long accessGroupId);


    @Query("Match (accessGroup:AccessGroup{deleted:false})-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessPage:AccessPage) where id(accessPage)={1} AND id(accessGroup)={0} return r.read as read, r.write as write")
    AccessPageQueryResult getAccessPermissionForAccessPage(Long accessGroupId, Long accessPageId);


    @Query("Match (accessPage:AccessPage) where id(accessPage)={3} with accessPage\n" +
            "Match (n:Organization),(staff:Staff) where id(n)={0} AND id(staff)={1} with n,staff,accessPage\n" +
            "MATCH (n)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff)-[:BELONGS_TO]->(user:User) with user,emp,accessPage\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitPermission,accessPage\n" +
            "Match (unitPermission)-[r:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE r.accessGroupId={4}\n" +
            "RETURN r.read as read, r.write as write")
    AccessPageQueryResult getCustomPermissionOfTab(long organizationId, long staffId, long unitId, long accessPageId, long accessGroupId);



    @Query("MATCH (n:Organization)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(n)={0} AND  id(staff)={2}\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={1}\n" +
            "Match (unitPermission)-[:HAS_ACCESS_GROUP]->(g:AccessGroup) where id(g)={3} with g,unitPermission\n" +
            "Match (g)-[:HAS_ACCESS_OF_TABS]->(accessPage:AccessPage{isModule:true}) with accessPage,g,unitPermission\n" +
            "MATCH path=(accessPage)-[:SUB_PAGE*]->(accessPage1:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g) \n" +
            "WITH NODES(path) AS np,g as g,unitPermission as unitPermission with REDUCE(s=[], i IN RANGE(0, LENGTH(np)-3, 1) | s + {p:np[i], c:np[i+1],g:g,unitPermission:unitPermission}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with distinct ps.g as g,ps as ps,ps.unitPermission as unitPermission\n" +
            "optional match (parent:AccessPage)<-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g) where id(parent)=id(ps.p) \n" +
            "optional match (unitPermission)-[parentCustomRel:HAS_CUSTOMIZED_PERMISSION]->(parent:AccessPage) WHERE parentCustomRel.accessGroupId={3}\n" +
            " AND id(parent)=id(ps.p) with r,parentCustomRel,ps,unitPermission,g\n" +
            "optional match (child:AccessPage)<-[r2:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g) where id(child)=id(ps.c)\n" +
            "optional match (unitPermission)-[childCustomRel:HAS_CUSTOMIZED_PERMISSION]->(child:AccessPage) WHERE childCustomRel.accessGroupId={3}\n" +
            "AND id(child)=id(ps.c) with r,r2,parentCustomRel,childCustomRel,ps\n" +
            "return {name:ps.p.name,id:id(ps.p),\n" +
            "read:CASE WHEN parentCustomRel IS NULL THEN r.read ELSE parentCustomRel.read END ,\n" +
            "write:CASE WHEN parentCustomRel IS NULL THEN r.write ELSE parentCustomRel.write END,module:ps.p.isModule,moduleId:ps.p.moduleId,\n" +
            "children:collect( distinct {name:ps.c.name,id:id(ps.c),moduleId:ps.c.moduleId,\n" +
            "read:CASE WHEN childCustomRel IS NULL THEN r2.read ELSE childCustomRel.read END,\n" +
            "write:CASE WHEN childCustomRel IS NULL THEN r2.write ELSE childCustomRel.write END})} as data\n"+
            " UNION\n" +
            "MATCH (n:Organization)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(n)={0} AND  id(staff)={2}\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={1}\n" +
            "Match (unitPermission)-[:HAS_ACCESS_GROUP]->(g:AccessGroup) where id(g)={3} with g,unitPermission\n" +
            "Match (accessPage:AccessPage{isModule:true,active:true}) where not (accessPage)-[:SUB_PAGE]->() with accessPage, g, unitPermission\n" +
            "Match (accessPage)<-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g) with accessPage, g,r, unitPermission\n" +
            "optional match (unitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE customRel.accessGroupId={3}\n" +
            "return {name:accessPage.name,id:id(accessPage),\n" +
            "read:CASE WHEN customRel IS NULL THEN r.read ELSE customRel.read END, write:CASE WHEN customRel IS NULL THEN r.write ELSE customRel.write END,\n" +
            "selected:case when r.isEnabled then true else false end,module:accessPage.isModule,children:[]} as data")
    List<Map<String, Object>> getAccessPagePermissionOfStaff(long orgId, long unitId, long staffId, long accessGroupId);

    @Query("MATCH (org:Organization) where id(org)={0} with org\n" +
            "MATCH (org)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={1}  with org,emp\n" +
            "Match (emp)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org) with org,unitPermission\n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup{deleted:false,enabled:true}) with accessGroup,org,unitPermission\n" +
            "Match (accessPage:AccessPage{isModule:true})<-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with unitPermission, accessPage,r\n" +
            "optional match (unitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(accessPage) \n"+
            "return id(accessPage) as id,accessPage.name as name," +
            "CASE WHEN customRel IS NULL THEN r.read ELSE customRel.read END as read,CASE WHEN customRel IS NULL THEN r.write ELSE customRel.write END  as write," +
            "accessPage.moduleId as moduleId,accessPage.active as active")
    List<AccessPageQueryResult> getPermissionOfMainModule(long orgId, long userId);

    @Query("MATCH (org:Organization),(pOrg:Organization) where id(org) IN {0} AND id(pOrg)={2} \n" +
            "MATCH (pOrg)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={1}  with org,emp\n" +
            "Match (emp)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org) with org,unitPermission\n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup{deleted:false,enabled:true}) with accessGroup,org,unitPermission\n" +
            "Match (accessPage:AccessPage{isModule:true})<-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with unitPermission, accessPage,r\n" +
            "optional match (unitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(accessPage)\n"+
            "return id(accessPage) as id,accessPage.name as name," +
            "CASE WHEN customRel IS NULL THEN r.read ELSE customRel.read END as read,CASE WHEN customRel IS NULL THEN r.write ELSE customRel.write END  as write," +
            "accessPage.moduleId as moduleId,accessPage.active as active")
    List<AccessPageQueryResult> getPermissionOfMainModule(List<Long> orgIds, long userId, Long parentOrganizationId);


    //TODO CHECK ERROR
    /*@Query("MATCH (org:Organization) where id(org)={0} with org\n" +
            "MATCH (org)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={1}  with org,emp\n" +
            "Match (emp)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org) with org,unitPermission\n" +
            "Match (unitPermission)-[:" + HAS_ACCESS_PERMISSION + "{isEnabled:true}]->(ap:AccessPermission) with ap,org\n" +
            "MATCH (ap)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,ap,org\n" +
            "Match (ap)-[r:" + HAS_ACCESS_PAGE_PERMISSION + "]->(accessPage:AccessPage{isModule:true})<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup)\n" +
            "return id(accessPage) as id,accessPage.name as name,r.isRead as read,r.isWrite as write,accessPage.moduleId as moduleId,accessPage.active as active")
    List<AccessPageQueryResult> getPermissionOfMainModule(long orgId, long userId);

    @Query("MATCH (org:Organization),(pOrg:Organization) where id(org) IN {0} AND id(pOrg)={2} \n" +
            "MATCH (pOrg)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={1}  with org,emp\n" +
            "Match (emp)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org) with org,unitPermission\n" +
            "Match (unitPermission)-[:" + HAS_ACCESS_PERMISSION + "{isEnabled:true}]->(ap:AccessPermission) with ap,org\n" +
            "MATCH (ap)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,ap,org\n" +
            "Match (ap)-[r:" + HAS_ACCESS_PAGE_PERMISSION + "]->(accessPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup)\n" +
            "return  id(accessPage) as id,accessPage.name as name,r.isRead as read,r.isWrite as write,accessPage.moduleId as moduleId,accessPage.active as active")
    List<AccessPageQueryResult> getPermissionOfMainModule(List<Long> orgIds, long userId, Long parentOrganizationId);
*/

    // TODO For HUB permission for module is not AccessGroup wise, we are giving access of all modules to every user of hub

    @Query("Match (accessPage:AccessPage{isModule:true})\n" +
            "return id(accessPage) as id,accessPage.name as name,true as read,true as write,accessPage.moduleId as moduleId,accessPage.active as active")
    List<AccessPageQueryResult> getPermissionOfMainModuleForHubMembers();



    @Query("MATCH (org:Organization),(parentOrganization:Organization) where id(org)={0} AND id(parentOrganization)={2} with org,parentOrganization\n" +
            "MATCH (emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={1} with org,emp\n" +
            "MATCH (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(org) with unitPermission,org\n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,org,unitPermission\n" +
            "Match (module:AccessPage{isModule:true})<-[modulePermission:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with module,modulePermission,unitPermission,accessGroup\n" +
            "optional match (unitPermission)-[moduleCustomRel:HAS_CUSTOMIZED_PERMISSION]->(module) with module,modulePermission,unitPermission,moduleCustomRel,accessGroup\n" +
            "Match (module)-[:SUB_PAGE*]->(subPage:AccessPage)<-[subPagePermission:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with modulePermission,subPagePermission,subPage,module,unitPermission,moduleCustomRel\n" +
            "optional match (unitPermission)-[subPageCustomRel:HAS_CUSTOMIZED_PERMISSION]->(subPage)\n" +
            "return module.name as name,id(module) as id,module.moduleId as moduleId,CASE WHEN moduleCustomRel IS NULL THEN modulePermission.read ELSE moduleCustomRel.read END as read,CASE WHEN moduleCustomRel IS NULL THEN modulePermission.write ELSE moduleCustomRel.write END as write,module.isModule as isModule,module.active as active,collect( distinct {name:subPage.name,id:id(subPage),moduleId:subPage.moduleId,read:CASE WHEN subPageCustomRel IS NULL THEN subPagePermission.read ELSE subPageCustomRel.read END,write:CASE WHEN subPageCustomRel IS NULL THEN subPagePermission.write ELSE subPageCustomRel.write END,isModule:subPage.isModule,active:subPage.active}) as children")
    List<AccessPageQueryResult> getTabPermissionForUnit(long unitId, long userId, Long parentOrganizationId);


    @Query("Match (accessPage:AccessPage{isModule:true}) with accessPage as module\n" +
            "Match (module)-[:SUB_PAGE*]->(subPage:AccessPage) with module,subPage\n" +
            "return module.name as name,id(module) as id,module.moduleId as moduleId,true as read,true as write,module.isModule as isModule," +
            "module.active as active,collect({name:subPage.name,id:id(subPage),moduleId:subPage.moduleId,read:true,write:true," +
            "isModule:subPage.isModule,active:subPage.active}) as children")
    List<AccessPageQueryResult> getTabsPermissionForHubMember();

    AccessPage findByModuleId(String moduleId);

    @Query("Match (accessPage:AccessPage{isModule:true}) WITH accessPage\n" +
            "OPTIONAL MATCH (country:Country)-[r:" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(accessPage) WHERE id(country)={0} " +
            "OPTIONAL MATCH(accessPage)-[subTabs:SUB_PAGE]-(sub:AccessPage) " +
            "WITH r.accessibleForHub as accessibleForHub, r.accessibleForUnion as accessibleForUnion, r.accessibleForOrganization as accessibleForOrganization,accessPage,subTabs \n" +
            "RETURN \n" +
            "id(accessPage) as id,accessPage.name as name,accessPage.moduleId as moduleId,accessPage.active as active,accessPage.editable as editable, " +
            "CASE WHEN count(subTabs)>0 THEN true ELSE false END as hasSubTabs,\n" +
            "CASE WHEN accessibleForHub is NULL THEN false ELSE accessibleForHub END as accessibleForHub,\n" +
            "CASE WHEN accessibleForUnion is NULL THEN false ELSE accessibleForUnion END as accessibleForUnion,\n" +
            "CASE WHEN accessibleForOrganization is NULL THEN false ELSE accessibleForOrganization END as accessibleForOrganization ORDER BY id(accessPage)")
    List<AccessPageDTO> getMainTabs(Long countryId);

    @Query("match (org:Organization) where id(org)={0} with org\n" +
            "match(org)-[:" + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(accessgroup:AccessGroup{deleted: false}) with accessgroup\n" +
            "match(accessgroup)-[r:" + HAS_ACCESS_OF_TABS + "]->(accessPage:AccessPage{isModule:true}) with  r.accessibleForHub as accessibleForHub, r.accessibleForUnion as accessibleForUnion, r.accessibleForOrganization as accessibleForOrganization,accessPage\n" +
            "return distinct id(accessPage) as id,accessPage.name as name,accessPage.moduleId as moduleId,accessPage.active as active," +
            "CASE WHEN accessibleForHub is NULL THEN false ELSE accessibleForHub END as accessibleForHub, \n" +
            "CASE WHEN accessibleForUnion is NULL THEN false ELSE accessibleForUnion END as accessibleForUnion, \n" +
            "CASE WHEN accessibleForOrganization is NULL THEN false ELSE accessibleForOrganization END as accessibleForOrganization  ORDER BY id(accessPage)")
    List<AccessPageDTO> getMainTabsForUnit(Long unitId);


    @Query("Match (accessPage:AccessPage)-[:" + SUB_PAGE + "]->(subPage:AccessPage) where id(accessPage)={0} WITH subPage,accessPage\n" +
            "OPTIONAL MATCH (country:Country)-[r:" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(subPage) WHERE id(country)={1} WITH r,subPage,id(accessPage) as parentTabId,\n" +
            "r.accessibleForHub as accessibleForHub, r.accessibleForUnion as accessibleForUnion, r.accessibleForOrganization as accessibleForOrganization\n" +
            "return id(subPage) as id, subPage.name as name,subPage.moduleId as moduleId,subPage.active as active, parentTabId, " +
            "CASE WHEN count(subPage)>0 THEN true ELSE false END as hasSubTabs,\n" +
            "CASE WHEN accessibleForHub is NULL THEN false ELSE accessibleForHub END as accessibleForHub,\n" +
            "CASE WHEN accessibleForUnion is NULL THEN false ELSE accessibleForUnion END as accessibleForUnion,\n" +
            "CASE WHEN accessibleForOrganization is NULL THEN false ELSE accessibleForOrganization END as accessibleForOrganization "
    )
    List<AccessPageDTO> getChildTabs(Long tabId, Long countryId);

    @Query("Match (accessPage:AccessPage) where id(accessPage)={0} set accessPage.name={1} return accessPage")
    AccessPage updateAccessTab(Long id, String name);

    @Query("Match (n:AccessPage) where id(n)={0} with n\n" +
            "Optional Match (n)-[:" + SUB_PAGE + "*]->(subPage:AccessPage) with n+[subPage] as coll unwind coll as pages with distinct pages set pages.active={1} return distinct true")
    Boolean updateStatusOfAccessTabs(Long tabId, Boolean active);

    @Query("Match (n:AccessPage) where id(n)={0} with n \n" +
            "OPTIONAL Match (n)-[:SUB_PAGE*]->(subPage:AccessPage)  with collect(subPage)+collect(n) as coll unwind coll as pages with distinct pages with collect(pages) as listOfPage \n" +
            "MATCH (c:Country) WHERE id(c)={1} WITH c, listOfPage\n" +
            "UNWIND listOfPage as page\n" +
            "MERGE (c)-[r:HAS_ACCESS_FOR_ORG_CATEGORY]->(page)\n" +
            "ON CREATE SET r.accessibleForHub = (CASE WHEN {2}='HUB' THEN {3} ELSE false END), \n" +
            "r.accessibleForUnion = (CASE WHEN {2}='UNION' THEN {3} ELSE false END), \n" +
            "r.accessibleForOrganization= (CASE WHEN {2}='ORGANIZATION' THEN {3} ELSE false END)\n" +
            "ON MATCH SET r.accessibleForHub = (CASE WHEN {2}='HUB' THEN {3} ELSE r.accessibleForHub  END), \n" +
            "r.accessibleForUnion = (CASE WHEN {2}='UNION' THEN {3} ELSE r.accessibleForUnion  END),\n" +
            "r.accessibleForOrganization= (CASE WHEN {2}='ORGANIZATION' THEN {3} ELSE r.accessibleForOrganization END)\n" +
            " return distinct true")
    Boolean updateAccessStatusOfCountryByCategory(Long tabId, Long countryId, String organizationCategory, Boolean accessStatus);

    @Query("Match (emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={0}\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={1}\n" +
            "Match (unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup)\n" +
            "Match (module:AccessPage{isModule:true})-[:SUB_PAGE]->(subPage:AccessPage)\n" +
            "optional Match (accessGroup)-[r:HAS_ACCESS_OF_TABS]->(module)\n" +
            "optional Match (subPage)<-[r2:HAS_ACCESS_OF_TABS]-(accessGroup) with {name:subPage.name,active:subPage.active,moduleId:subPage.moduleId,read:case when r2.isRead then r2.isRead else false end,write:case when r2.isWrite then r2.isWrite else false end,id:id(subPage)} as data,accessGroup,module,r,r2\n" +
            "return id(accessGroup) as accessGroupId,id(module) as id,module.name as name,module.isModule as module,module.active as active,case when r.isRead then r.isRead else false end as read,case when r.isWrite then r.isWrite else false end as write,module.moduleId as moduleId, collect(data) as tabPermissions")
    List<StaffPermissionQueryResult> getAccessPermissionOfUserForUnit(Long userId, Long unitId);

    @Query("Match (accessPage:AccessPage{isModule:true}) with accessPage as module\n" +
            "Match (module)-[:SUB_PAGE*]->(subPage:AccessPage) with module,subPage\n" +
            "return module.name as name,id(module) as id,module.moduleId as moduleId,true as read,true as write,module.isModule as module," +
            "module.active as active,collect({name:subPage.name,id:id(subPage),moduleId:subPage.moduleId,read:true,write:true," +
            "active:subPage.active}) as tabPermissions")
    List<StaffPermissionQueryResult> getTabsPermissionForHubUserForUnit();

    @Query("Match (emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={0} with emp\n" +
            "Match (emp:Employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org:Organization) with collect(org.isKairosHub) as hubList\n" +
            "return true in hubList")
    Boolean isHubMember(Long userId);

    // For Test Cases
    @Query("Match (accessPage:AccessPage{isModule:true}) WITH accessPage return accessPage LIMIT 1")
    AccessPage getOneMainModule();

    @Query("Match (accessPage)-[:SUB_PAGE]->(childAP:AccessPage) WHERE id(childAP)={0}  RETURN id(accessPage)")
    Long getParentTab(Long accessPageId);

    @Query("MATCH (n:Organization)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(n)={0} AND  id(staff)={2}\n" +
            "Match (emp)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={1} \n" +
            "Match (unitPermission)-[:HAS_ACCESS_GROUP]->(g:AccessGroup) with g,unitPermission\n" +
            "OPTIONAL Match (g)-[:HAS_ACCESS_OF_TABS]->(accessPage:AccessPage) WITH g,unitPermission, accessPage\n" +
            "MATCH (accessPage)-[:SUB_PAGE]->(childrenAccessPages:AccessPage)<-[r:HAS_ACCESS_OF_TABS]-(g) WHERE id(accessPage)={3}\n" +
            "WITH childrenAccessPages,unitPermission,r,g\n" +
            "optional match (unitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(childrenAccessPages:AccessPage) WHERE customRel.accessGroupId={4}\n" +
            "WITH r,customRel,unitPermission,childrenAccessPages,g\n" +
            "RETURN childrenAccessPages.name as name,id(childrenAccessPages) as id, CASE WHEN customRel IS NULL THEN r.read ELSE customRel.read END as read ,\n" +
            "CASE WHEN customRel IS NULL THEN r.write ELSE customRel.write END as write ORDER BY id DESC")
    List<AccessPageQueryResult> getChildTabsAccessPermissionsByStaffAndOrg(long orgId, long unitId, Long staffId, Long tabId, Long accessGroupId);

    @Query("MATCH(ac:AccessGroup) where id(ac)={1} with ac " +
            "MATCH(accessGroup:AccessGroup)-[rel:" + HAS_ACCESS_OF_TABS + "]->(accessPage:AccessPage) where id(accessGroup)={0} with ac,accessGroup as accessGroup," +
            "accessPage as accessPage, rel.read as read, rel.write as write ,rel.isEnabled as isEnabled  " +
            "create unique (ac)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:isEnabled, read:read, write:write}]->(accessPage)")
    void copyAccessGroupPageRelationShips(Long oldAccessGroupId,Long newAccessGroupId);

    @Query("MATCH (a:AccessPage) WHERE a.isModule={0} WITH id(a) as id,a.name as name,a.moduleId as moduleId, "+
            "toInt(split(a.moduleId,\"_\")[1]) as tabIdNumber return tabIdNumber ORDER BY tabIdNumber DESC LIMIT 1")
    Integer getLastTabOrModuleIdOfAccessPage(boolean isModule);


    @Query(" MATCH (accessPage:AccessPage) WHERE id(accessPage)={0} \n" +
            "MATCH (parentAccessPage: AccessPage)-[:SUB_PAGE]->(accessPage) RETURN id(parentAccessPage)")
    Long getParentAccessPageIdForAccessGroup(Long accessPageId);


    @Query( "MATCH(accessGroup:AccessGroup) where id(accessGroup)={0} \n" +
            "MATCH (accessPage:AccessPage) WHERE id(accessPage)={1} \n" +
            "MATCH (accessPage)-[:SUB_PAGE]->(childrenAccessPages:AccessPage)<-[r:HAS_ACCESS_OF_TABS]-(g) \n" +
            "RETURN childrenAccessPages.name as name,id(childrenAccessPages) as id, r.read as read ,\n" +
            "r.write as write ORDER BY id DESC")
    List<AccessPageQueryResult> getChildAccessPagePermissionsForAccessGroup(Long accessGroupId, Long accessPageId);

    @Query( "MATCH(accessGroup:AccessGroup) where id(accessGroup)={0} \n" +
            "MATCH (accessPage:AccessPage) WHERE id(accessPage)={1} \n" +
            "MATCH (accessPage)<-[r:HAS_ACCESS_OF_TABS]-(g) \n" +
            "SET r.read={2}, r.write={3} ")
    List<AccessPageQueryResult> updateAccessPagePermissionsForAccessGroup(Long accessGroupId, Long accessPageId, Boolean readPermission, Boolean writePermission);

    @Query("Match (unitPermission:UnitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(accessPages:AccessPage) " +
            "WHERE customRel.accessGroupId={0} AND id(accessPages) IN {1} DELETE customRel")
    void removeCustomPermissionsForAccessGroup(Long accessGroupId, List<Long> accessPageIds);


    @Query("Match (emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={0} with emp\n" +
            "Match (emp:Employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org:Organization{isKairosHub:true}) return org ORDER BY id(org) LIMIT 1 \n" )
    Organization fetchParentHub(Long userId);



    // fetch Permission of Hub Users

    @Query("MATCH (emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={0} with emp \n" +
            "MATCH (emp)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org:Organization{isEnable:true}) WHERE id(org)={1}  with unitPermission,org \n" +
            "MATCH (unitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup) WHERE (accessGroup.endDate IS NULL OR date(accessGroup.endDate) >= date()) with accessGroup,org,unitPermission \n" +
            "Match (module:AccessPage)<-[modulePermission:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with module,modulePermission,unitPermission,accessGroup\n" +
            "optional match (unitPermission)-[moduleCustomRel:HAS_CUSTOMIZED_PERMISSION]->(module) WHERE moduleCustomRel.accessGroupId=id(accessGroup) \n" +
            "with module,modulePermission,unitPermission,moduleCustomRel,accessGroup\n" +
            "return module.name as name,id(module) as id,module.moduleId as moduleId,CASE WHEN moduleCustomRel IS NULL THEN modulePermission.read ELSE moduleCustomRel.read END as read,CASE WHEN moduleCustomRel IS NULL THEN modulePermission.write ELSE moduleCustomRel.write END as write,module.isModule as module")
    List<AccessPageQueryResult> fetchHubUserPermissions(Long userId, Long organizationId);

    @Query("MATCH (u:User) WHERE id(u)={0} \n" +
            "MATCH (org:Organization{isEnable:true})-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(s:Staff)-[:"+BELONGS_TO+"]-(u) \n" +
            "OPTIONAL MATCH (org)-[:HAS_SUB_ORGANIZATION*]->(unit:Organization{isEnable:true,boardingCompleted:true}) with employment,org+[unit] as coll\n" +
            "unwind coll as units with  distinct units,employment \n" +
            "OPTIONAL MATCH  (o:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY'})-[r:HAS_SUB_ORGANIZATION*1..]->(units) \n" +
            "WITH o,employment, [o]+units as units  unwind units as org  WITH distinct org,o,employment\n" +
            "MATCH (employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(org) WITH org,unitPermission \n" +
            "MATCH (unitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup{deleted:false,enabled:true}) " +
            "MATCH(accessGroup)-[:"+DAY_TYPES+"]->(dayType:DayType) WHERE  id(dayType) IN   {1}  AND (accessGroup.endDate IS NULL OR date(accessGroup.endDate) >= date())  WITH org,accessGroup,unitPermission\n" +
            "MATCH (accessPage:AccessPage)<-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) \n" +
            "OPTIONAL MATCH (unitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE customRel.accessGroupId=id(accessGroup)\n" +
            "WITH org,collect( distinct {name:accessPage.name,id:id(accessPage),moduleId:accessPage.moduleId,read:CASE WHEN customRel IS NULL THEN r.read ELSE customRel.read END,write:CASE WHEN customRel IS NULL THEN r.write ELSE customRel.write END,module:accessPage.isModule}) as permissions\n" +
            "RETURN id(org) as unitId,org.isParentOrganization as parentOrganization, permissions as permission")
    List<UserPermissionQueryResult> fetchStaffPermissionsWithDayTypes(Long userId, Set<Long> dayTypeIds);



    @Query("MATCH (u:User) WHERE id(u)={0} \n" +
            "MATCH (org:Organization{isEnable:true})-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(s:Staff)-[:"+BELONGS_TO+"]-(u) \n" +
            "OPTIONAL MATCH (org)-[:HAS_SUB_ORGANIZATION*]->(unit:Organization{isEnable:true,boardingCompleted:true}) with employment,org+[unit] as coll\n" +
            "unwind coll as units WITH  distinct units,employment \n" +
            "OPTIONAL MATCH  (o:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY'})-[r:HAS_SUB_ORGANIZATION*1..]->(units) \n" +
            "WITH o,employment, [o]+units as units  unwind units as org  WITH distinct org,o,employment\n" +
            "MATCH (employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) WITH org,unitPermission \n" +
            "MATCH (unitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup{deleted:false,enabled:true}) WHERE (accessGroup.endDate IS NULL OR date(accessGroup.endDate) >= date())  WITH org,accessGroup,unitPermission\n" +
            "MATCH (accessPage:AccessPage)<-[r:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) \n" +
            "OPTIONAL MATCH (unitPermission)-[customRel:HAS_CUSTOMIZED_PERMISSION]->(accessPage) WHERE customRel.accessGroupId=id(accessGroup)\n" +
            "WITH org,collect( distinct {name:accessPage.name,id:id(accessPage),moduleId:accessPage.moduleId,read:CASE WHEN customRel IS NULL THEN r.read ELSE customRel.read END,write:CASE WHEN customRel IS NULL THEN r.write ELSE customRel.write END,module:accessPage.isModule}) as permissions\n" +
            "RETURN id(org) as unitId,org.isParentOrganization as parentOrganization, permissions as permission")
    List<UserPermissionQueryResult> fetchStaffPermissions(Long userId);



    @Query("MATCH (accessPage:AccessPage{isModule:true}) WITH accessPage\n" +
            "MATCH (country:Country)-[:" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(accessPage) WHERE id(country)={0} with accessPage\n" +
            "OPTIONAL MATCH (accessPage) -[: " +SUB_PAGE + "]->(subPages:AccessPage{active:true,kpiEnabled:true}) RETURN accessPage.name as name,accessPage.moduleId as moduleId, collect(distinct subPages) as child ORDER BY accessPage.moduleId")
    List<KPIAccessPageQueryResult> getKPITabsListForCountry(Long countryId);

    @Query("MATCH (org:Organization) where id(org)={0} with org\n" +
            "MATCH(org)-[: " + ORGANIZATION_HAS_ACCESS_GROUPS + "]-(accessgroup:AccessGroup{deleted: false}) with accessgroup\n" +
            "MATCH(accessgroup)-[r:HAS_ACCESS_OF_TABS ]->(accessPage:AccessPage{isModule:true}) with accessPage\n" +
            "OPTIONAL MATCH (accessPage) -[: " + SUB_PAGE + "]->(subPages:AccessPage{active:true,kpiEnabled:true}) RETURN \n" +
            "accessPage.name as name,accessPage.moduleId as moduleId, collect(distinct subPages) as child ORDER BY accessPage.moduleId")
    List<KPIAccessPageQueryResult> getKPITabsListForUnit(Long unitId);


    @Query("MATCH (n:AccessPage) -[:SUB_PAGE *]->(subPages:AccessPage{active:true,kpiEnabled:true}) WHERE n.moduleId={0} RETURN subPages")
    List<AccessPage> getKPITabsList(String moduleId);

    @Query("MATCH(accessPage:AccessPage)-[rel:"+ ACCESS_PAGE_HAS_LANGUAGE +"]->(language:SystemLanguage{deleted:false}) WHERE accessPage.moduleId={0} AND id(language)={1} " +
            "RETURN rel.description as description, id(rel) as id, rel.languageId as languageId, rel.moduleId as moduleId order by rel.creationDate DESC limit 1")
    AccessPageLanguageDTO findLanguageSpecificDataByModuleIdAndLanguageId(String moduleId, Long languageId);

    @Query("MATCH (accessPage:AccessPage{isModule:true}) where id(accessPage) IN {0} return accessPage")
    List<AccessPage> findAllModulesByIds(Set<Long> moduleIds);

    @Query("MATCH (country:Country)-[" + HAS_ACCESS_FOR_ORG_CATEGORY + "]-(accessPage:AccessPage{isModule:true,active:true}) WHERE id(country)={0} " +
            "RETURN id(accessPage) as id,accessPage.name as name,accessPage.moduleId as moduleId,accessPage.active as active")
    List<AccessPageDTO> getMainActiveTabs(Long countryId);

    @Query("MATCH (u:User) WHERE id(u)={0} \n" +
            "MATCH (org:Organization{isEnable:true})-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(s:Staff)-[:"+BELONGS_TO+"]-(u) \n" +
            "OPTIONAL MATCH (org)-[:HAS_SUB_ORGANIZATION*]->(unit:Organization{isEnable:true}) WITH employment,org+[unit] as coll\n" +
            "unwind coll as units with  distinct units,employment \n" +
            "OPTIONAL MATCH  (o:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY'})-[r:HAS_SUB_ORGANIZATION*1..]->(units) \n" +
            "WITH o,employment, [o]+units as units  unwind units as org  WITH distinct org,o,employment\n" +
            "MATCH (employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(org) WITH org,unitPermission \n" +
            "MATCH (unitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup{deleted:false,enabled:true})  return accessGroup"
            )
    List<AccessGroup> fetchAccessGroupsOfStaffPermission(Long userId);


}


