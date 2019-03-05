package com.kairos.persistence.repository.user.staff;


import com.kairos.enums.employment_type.EmploymentStatus;
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

    @Query("MATCH (organization:Organization),(staff:Staff) WHERE id(organization)={1} AND id(staff)={0} WITH organization,staff\n" +
            "MATCH (organization)-[:" + HAS_POSITIONS + "]->(position:Position)-[:" + BELONGS_TO + "]->(staff) WITH position\n" +
            "MATCH (position)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + HAS_ACCESS_PERMISSION + "{isEnabled:true}]->(accessPermission:AccessPermission) WITH unitPermission\n" +
            "OPTIONAL MATCH (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) WHERE id(unit)={2} WITH r,unitPermission\n" +
            "RETURN {id:id(unitPermission),startDate:unitPermission.startDate,endDate:unitPermission.endDate,weeklyHours:unitPermission.weeklyHours,fullTime:unitPermission.fullTime,employmentType:unitPermission.employmentType,employmentType:unitPermission.employmentType,employmentNo:unitPermission.employmentNumber,isHistory:case when r is NULL then false else true end,wages:case when wage is NULL then [] else collect({id:id(wage),startDate:wage.startDate,endDate:wage.endDate,salary:wage.salary}) end} as data")
    List<Map<String, Object>> getUnitPermissionsInAllUnits(long staffId, long parentOrganizationId, long childOrganizationId);


    @Query("MATCH (organization:Organization),(staff:Staff) WHERE id(organization)={0} AND id(staff)={1} WITH organization,staff\n" +
            "MATCH (organization)-[:" + HAS_POSITIONS + "]->(position:Position)-[:" + BELONGS_TO + "]->(staff) WITH position\n" +
            "MATCH (position)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission{employmentStatus:{3}}) WITH unitPermission\n" +
            "MATCH (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) WHERE id(unit)={2} RETURN unitPermission")
    UnitPermission getUnitPermissions(long parentOrganizationId, long staffId, long childOrganizationId, EmploymentStatus employmentStatus);


    @Query("MATCH (organization:Organization),(staff:Staff) WHERE id(organization)={0} AND id(staff)={2} WITH organization,staff\n" +
            "MATCH (organization)-[:" + HAS_POSITIONS + "]->(position:Position)-[:"+BELONGS_TO+"]->(staff) WITH position\n" +
            "MATCH (position)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission) WITH unitPermission\n" +
            "MATCH (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) WHERE id(unit)={1} WITH unitPermission\n" +
            "OPTIONAL MATCH (unitPermission)-[customPermission:" +HAS_CUSTOMIZED_PERMISSION+ "]-(ap:AccessPage) WHERE customPermission.accessGroupId={3} WITH unitPermission,customPermission " +
            "MATCH (unitPermission)-[r:"+ HAS_ACCESS_GROUP +"]->(accessGroup:AccessGroup) WHERE id(accessGroup)={3}  delete r,customPermission ")
    void updateUnitPermission(long organizationId, long unitId, long staffId, long accessGroupId, boolean isEnabled);

    
    @Query("MATCH (organization:Organization),(staff:Staff) WHERE id(organization)={0} AND id(staff)={2} WITH organization,staff \n" +
            "MATCH (organization)-[:"+ HAS_POSITIONS +"]->(position:Position)-[:"+BELONGS_TO+"]->(staff)  \n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)\n" +
            "MATCH(unitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(co:Organization) WHERE id(co)={1}\n" +
            "RETURN  unitPermission")
    UnitPermission checkUnitPermissionOfStaff(Long parentOrganizationId, Long organizationId, Long staffId, Long accessGroupId);

    @Query("OPTIONAL MATCH (up:UnitPermission)-[r:HAS_ACCESS_GROUP]-(ag:AccessGroup) WHERE id(up)={0} AND id(ag)={1} RETURN CASE WHEN r IS NULL THEN false ELSE true END")
    Boolean checkUnitPermissionLinkedWithAccessGroup(Long unitPermissionId, Long accessGroupId);

    // for parent organization
    @Query("MATCH (organization:Organization),(user:User) WHERE id(organization)={0} AND id(user) ={1}\n" +
            "MATCH(organization)<-[:"+HAS_SUB_ORGANIZATION+"]-(parent:Organization)\n" +
            " MATCH (parent)-[:"+ HAS_POSITIONS +"]->(position:Position)-[:"+BELONGS_TO+"]->(staff:Staff)-[:"+BELONGS_TO+"]->(user)  \n" +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(organization)\n" +
            "RETURN  unitPermission")
    UnitPermission checkUnitPermissionOfUser(Long organizationId, Long userId);

    @Query("MATCH (staff:Staff) WHERE  id(staff)={0} " +
            "MATCH (staff)<-[:"+BELONGS_TO+"]-(position:Position) " +
            "MATCH (position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission) " +
            "MATCH (unitPermission)-[r:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup) " +
            "RETURN count(r)")
    int getAccessGroupRelationShipCountOfStaff(Long staffId);



}
