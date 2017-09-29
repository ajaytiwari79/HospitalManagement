package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.access_permission.AccessPageQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.constants.AppConstants.HAS_ACCESS_OF_TABS;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by arvind on 24/10/16.
 */

@Repository
public interface AccessPageRepository extends GraphRepository<AccessPage> {

    @Override
    List<AccessPage> findAll();

    @Override
    List<AccessPage> findAll(Iterable<Long> accessPageIds);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "match (org)-[:"+HAS_SUB_ORGANIZATION+"*]->(n)\n" +
            "Match (n)-[:HAS_GROUP]->(group:Group)-[:"+HAS_TEAM+"]->(team:Team)-[:"+TEAM_HAS_MEMBER+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={1} with staff\n" +
            "Match (staff)-[:"+STAFF_HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup)-[r:"+ACCESS_GROUP_HAS_ACCESS_TO_PAGE+"{read:false}]->(accessPage:AccessPage{isModule:true}) return distinct accessPage")
    List<AccessPage> getAccessModulesForUnits(long parentOrganizationId, long userId);

    @Query("Match (n:UnitEmployment)-[:"+HAS_ACCESS_PERMISSION+"]->(accessPermission:AccessPermission)-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]->(p:AccessPage) where id(n)={0} AND id(p)={1} SET r.isRead={2} return r")
    void modifyAccessPagePermission(long unitEmploymentId, long accessPageId, boolean value);

    @Query("MATCH path=(accessPage:AccessPage)-[:"+SUB_PAGE+"*]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with ps\n" +
            "match (accessGroup:AccessGroup) where id(accessGroup)={0} with accessGroup,ps\n" +
            "optional match (parent:AccessPage)<-[r2:"+HAS_ACCESS_OF_TABS+"]-(accessGroup)\n" +
            "where id(parent)=id(ps.p) with r2,ps,accessGroup\n" +
            "optional match (child:AccessPage)<-[r:"+HAS_ACCESS_OF_TABS+"]-(accessGroup)\n" +
            "where id(child)=id(ps.c) with r,r2,ps,accessGroup\n" +
            "return {name:ps.p.name,id:id(ps.p),selected:case when r2.isEnabled then true else false end,module:ps.p.isModule,children:collect({name:ps.c.name,id:id(ps.c),selected:case when r.isEnabled then true else false end})} as data")
    List<Map<String,Object>> getAccessPageHierarchy(long accessGroupId);

    @Query("Match (accessGroup:AccessGroup),(accessPermission:AccessPermission) where id(accessPermission)={0} AND id(accessGroup)={1}\n" +
            "Match (accessGroup)-[r:"+HAS_ACCESS_OF_TABS+"{isEnabled:true}]->(accessPage:AccessPage) with accessPage,r,accessPermission\n" +
            "Create unique (accessPermission)-[:"+HAS_ACCESS_PAGE_PERMISSION+"{isEnabled:r.isEnabled,isRead:true,isWrite:false}]->(accessPage) return accessPermission")
    List<AccessPage> setDefaultPermission(long accessPermissionId, long accessGroupId);

    @Query("MATCH path=(accessPage:AccessPage)-[:"+SUB_PAGE+"*]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with ps\n" +
            "Match (accessPermission:AccessPermission) where id(accessPermission)={0} with accessPermission,ps\n" +
            "optional match (parent:AccessPage)<-[r2:"+HAS_ACCESS_PAGE_PERMISSION+"]-(accessPermission)\n" +
            "where id(parent)=id(ps.p) with r2,ps,accessPermission\n" +
            "optional match (child:AccessPage)<-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]-(accessPermission)\n" +
            "where id(child)=id(ps.c) with r,r2,ps\n" +
            "return {name:ps.p.name,id:id(ps.p),selected:r2.isEnabled,module:ps.p.isModule,children:collect({name:ps.c.name,id:id(ps.c),selected:r.isEnabled})} as data")
    List<Map<String,Object>> getStaffPermission(long accessPermissionId);

    @Query("MATCH (n:Organization)-[:HAS_EMPLOYMENTS]->(emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(n)={0} AND  id(staff)={2}\n" +
            "Match (emp)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmp:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) where id(unit)={1}\n" +
            "Match (unitEmp)-[:HAS_ACCESS_PERMISSION{isEnabled:true}]->(ap:AccessPermission)-[:HAS_ACCESS_GROUP]->(g:AccessGroup) where id(g)={3} with ap,g\n" +
            "Match (g)-[:HAS_ACCESS_OF_TABS{isEnabled:true}]->(accessPage:AccessPage{isModule:true}) with accessPage,ap,g\n" +
            "MATCH path=(accessPage)-[:SUB_PAGE*]->() WITH NODES(path) AS np,g as g,ap as ap with REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1],g:g,ap:ap}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps with distinct ps.g as g,ps as ps,ps.ap as ap\n" +
            "match (child:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g)\n" +
            "where id(child)=id(ps.c) with ps,child,g,ap\n" +
            "match (parent:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(g)\n" +
            "where id(parent)=id(ps.p) with ps,parent,child,ap\n" +
            "optional Match (ap)-[r2:HAS_ACCESS_PAGE_PERMISSION]->(child) with ps,parent,r2,ap\n" +
            "optional Match (ap)-[r:HAS_ACCESS_PAGE_PERMISSION]->(parent) with ps,parent,r2,r\n" +
            "return {name:ps.p.name,id:id(ps.p),read:r.isRead,write:r.isWrite,module:ps.p.isModule,moduleId:ps.p.moduleId,children:collect( distinct {name:ps.c.name,id:id(ps.c),moduleId:ps.c.moduleId,read:r2.isRead,write:r2.isWrite})} as data")
    List<Map<String,Object>> getAccessPageByAccessGroup(long orgId, long unitId, long staffId, long accessGroupId);

    @Query("MATCH (org:Organization) where id(org)={0} with org\n" +
            "MATCH (org)-[:"+HAS_EMPLOYMENTS+"]->(emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={1} with org,emp\n" +
            "Match (emp)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmp:UnitEmployment)-[:"+PROVIDED_BY+"]->(org) with org,unitEmp\n" +
            "Match (unitEmp)-[:"+HAS_ACCESS_PERMISSION+"{isEnabled:true}]->(ap:AccessPermission) with ap,org\n" +
            "MATCH (ap)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,ap,org\n"+
            "Match (ap)-[r:"+HAS_ACCESS_PAGE_PERMISSION+"]->(accessPage:AccessPage{isModule:true})<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup)\n" +
            "return id(accessPage) as id,accessPage.name as name,r.isRead as read,r.isWrite as write,accessPage.moduleId as moduleId")
    List<AccessPageQueryResult> getPermissionOfMainModule(long orgId, long userId);

    @Query("MATCH (org:Organization) where id(org)={0} with org\n" +
            "MATCH (emp:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={1} with org,emp\n" +
            "MATCH (emp)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmp:UnitEmployment)-[:PROVIDED_BY]->(org) with unitEmp,org\n" +
            "Match (unitEmp)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission) with accessPermission,org\n" +
            "MATCH (accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,accessPermission,org\n"+
            "Match (accessPermission)-[modulePermission:HAS_ACCESS_PAGE_PERMISSION]->(module:AccessPage{isModule:true})<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with module,accessPermission,modulePermission\n" +
            "Match (accessPage)-[:SUB_PAGE*]->(subPage:AccessPage)<-[:HAS_ACCESS_OF_TABS{isEnabled:true}]-(accessGroup) with accessPermission,modulePermission,subPage,module\n"+
            "Match (subPage:AccessPage)<-[subPagePermission:HAS_ACCESS_PAGE_PERMISSION]-(accessPermission) with module,subPage,modulePermission,subPagePermission\n" +
            "return module.name as name,id(module) as id,module.moduleId as moduleId,modulePermission.isRead as read,modulePermission.isWrite as write,module.isModule as isModule,collect({name:subPage.name,id:id(subPage),moduleId:subPage.moduleId,read:subPagePermission.isRead,write:subPagePermission.isWrite,isModule:subPage.isModule}) as children")
    List<AccessPageQueryResult> getTabPermissionForUnit(long unitId, long userId);

    @Query("Match (accessPage:AccessPage{isModule:true}) with accessPage as module\n" +
            "Match (module)-[:SUB_PAGE*]->(subPage:AccessPage) with module,subPage\n" +
            "return module.name as name,id(module) as id,module.moduleId as moduleId,true as read,true as write,module.isModule as isModule,collect({name:subPage.name,id:id(subPage),moduleId:subPage.moduleId,read:true,write:true,isModule:subPage.isModule}) as children")
    List<AccessPageQueryResult> getTabsPermissionForHubMember();
}
