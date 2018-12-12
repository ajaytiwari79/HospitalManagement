package com.kairos.persistence.repository.user.staff;


import com.kairos.enums.employment_type.EmploymentStatus;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 6/12/16.
 */
@Repository
public interface UnitPermissionGraphRepository extends Neo4jBaseRepository<UnitPermission, Long> {

    List<UnitPermission> findAll();

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={1} AND id(staff)={0} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:" + BELONGS_TO + "]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + HAS_ACCESS_PERMISSION + "{isEnabled:true}]->(accessPermission:AccessPermission) with unitPermission\n" +
            "optional match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={2} with r,unitPermission\n" +
            "optional match (unitPermission)-[:" + HAS_WAGES + "]->(wage:Wage) with wage,unitPermission,r\n" +
            "return {id:id(unitPermission),startDate:unitPermission.startDate,endDate:unitPermission.endDate,weeklyHours:unitPermission.weeklyHours,fullTime:unitPermission.fullTime,employmentType:unitPermission.employmentType,employmentType:unitPermission.employmentType,employmentNo:unitPermission.employmentNumber,isHistory:case when r is NULL then false else true end,wages:case when wage is NULL then [] else collect({id:id(wage),startDate:wage.startDate,endDate:wage.endDate,salary:wage.salary}) end} as data")
    List<Map<String, Object>> getUnitPermissionsInAllUnits(long staffId, long parentOrganizationId, long childOrganizationId);


    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:" + BELONGS_TO + "]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission{employmentStatus:{3}}) with unitPermission\n" +
            "match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={2} return unitPermission")
    UnitPermission getUnitPermissions(long parentOrganizationId, long staffId, long childOrganizationId, EmploymentStatus employmentStatus);


    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission) with unitPermission\n" +
            "match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={1} with unitPermission\n" +
            "OPTIONAL MATCH (unitPermission)-[customPermission:" +HAS_CUSTOMIZED_PERMISSION+ "]-(ap:AccessPage) WHERE customPermission.accessGroupId={3} with unitPermission,customPermission " +
            "Match (unitPermission)-[r:"+ HAS_ACCESS_GROUP +"]->(accessGroup:AccessGroup) where id(accessGroup)={3}  delete r,customPermission ")
    void updateUnitPermission(long organizationId, long unitId, long staffId, long accessGroupId, boolean isEnabled);

    
    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff \n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff)  \n" +
            "Match (employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)\n" +
            "match(unitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(co:Organization) where id(co)={1}\n" +
            "return  unitPermission")
    UnitPermission checkUnitPermissionOfStaff(Long parentOrganizationId, Long organizationId, Long staffId, Long accessGroupId);

    @Query("OPTIONAL Match (up:UnitPermission)-[r:HAS_ACCESS_GROUP]-(ag:AccessGroup) WHERE id(up)={0} AND id(ag)={1} RETURN CASE WHEN r IS NULL THEN false ELSE true END")
    Boolean checkUnitPermissionLinkedWithAccessGroup(Long unitPermissionId, Long accessGroupId);

    // for parent organization
    @Query("Match (organization:Organization),(user:User) where id(organization)={0} AND id(user) ={1}\n" +
            "MATCH(organization)<-[:HAS_SUB_ORGANIZATION]-(parent:Organization)\n" +
            " Match (parent)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user)  \n" +
            "Match (employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]-(organization)\n" +
            "return  unitPermission")
    UnitPermission checkUnitPermissionOfUser(Long organizationId, Long userId);

    @Query("Match (staff:Staff) where  id(staff)={0} " +
            "Match (staff)<-[:"+BELONGS_TO+"]-(employment:Employment) " +
            "Match (employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission) " +
            "Match (unitPermission)-[r:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup) " +
            "return count(r)")
    int getAccessGroupRelationShipCountOfStaff(Long staffId);



}
