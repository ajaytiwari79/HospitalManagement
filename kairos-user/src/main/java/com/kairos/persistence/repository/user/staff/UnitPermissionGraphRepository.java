package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.enums.EmploymentStatus;
import com.kairos.persistence.model.user.staff.AccessPermission;
import com.kairos.persistence.model.user.staff.UnitPermission;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
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
            "return {id:id(unitPermission),startDate:unitPermission.startDate,endDate:unitPermission.endDate,weeklyHours:unitPermission.weeklyHours,fullTime:unitPermission.fullTime,employmentType:unitPermission.employmentType,employmentType:unitPermission.employmentType,employmentNo:unitPermission.employmentNumber,isEditable:case when r is NULL then false else true end,wages:case when wage is NULL then [] else collect({id:id(wage),startDate:wage.startDate,endDate:wage.endDate,salary:wage.salary}) end} as data")
    List<Map<String, Object>> getUnitPermissionsInAllUnits(long staffId, long parentOrganizationId, long childOrganizationId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:" + BELONGS_TO + "]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission) with unitPermission\n" +
            "match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={2} return unitPermission")
    UnitPermission getUnitPermissions(long parentOrganizationId, long staffId, long childOrganizationId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:" + BELONGS_TO + "]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission{employmentStatus:{3}}) with unitPermission\n" +
            "match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={2} return unitPermission")
    UnitPermission getUnitPermissions(long parentOrganizationId, long staffId, long childOrganizationId, EmploymentStatus employmentStatus);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:BELONGS_TO]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission) with unitPermission\n" +
            "match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={1} with unitPermission\n" +
            "Match (unitPermission)-[:" + HAS_ACCESS_PERMISSION + "]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) where id(accessGroup)={3} return accessPermission")
    AccessPermission getAccessPermission(long organizationId, long unitId, long staffId, long accessGroupId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff\n" +
            "Match (organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:BELONGS_TO]->(staff) with employment\n" +
            "Match (employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission) with unitPermission\n" +
            "match (unitPermission)-[r:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={1} with unitPermission\n" +
            "Match (unitPermission)-[r:" + HAS_ACCESS_PERMISSION + "]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) where id(accessGroup)={3} set r.isEnabled={4} return r.isEnabled;")
    boolean updateUnitPermission(long organizationId, long unitId, long staffId, long accessGroupId, boolean isEnabled);

    @Query("Match (unitPermission:unitPermission),(accessPermission:AccessPermission) where id(unitPermission)={0} AND id(accessPermission)={1}\n" +
            "Create (unitPermission)-[r:" + HAS_ACCESS_PERMISSION + "{isEnabled:true}]->(accessPermission) return count(r) as count")
    int linkUnitPermissionWithAccessPermission(long unitEmploymentId, long accessPermissionId);


    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff \n" +
            "Match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff)  \n" +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)\n" +
            "match(unitPermission)-[:APPLICABLE_IN_UNIT]-(co:Organization) where id(co)={1}\n" +
            "return  unitPermission")
    UnitPermission checkUnitPermissionOfStaff(Long parentOrganizationId, Long organizationId, Long staffId);

    // for parent organization
    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff \n" +
            "Match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff)  \n" +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]-(organization)\n" +
            "return  unitPermission")
    UnitPermission checkUnitPermissionOfStaff(Long parentOrganizationId, Long staffId);
}
