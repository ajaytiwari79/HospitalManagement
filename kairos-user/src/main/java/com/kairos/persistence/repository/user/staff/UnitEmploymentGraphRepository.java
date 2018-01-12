package com.kairos.persistence.repository.user.staff;
import com.kairos.persistence.model.enums.EmploymentStatus;
import com.kairos.persistence.model.user.staff.AccessPermission;
import com.kairos.persistence.model.user.staff.UnitEmployment;
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
public interface UnitEmploymentGraphRepository extends Neo4jBaseRepository<UnitEmployment,Long> {

    List<UnitEmployment> findAll();

    @Query ("Match (organization:Organization),(staff:Staff) where id(organization)={1} AND id(staff)={0} with organization,staff\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff) with employment\n" +
            "Match (employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment)-[:"+HAS_ACCESS_PERMISSION+"{isEnabled:true}]->(accessPermission:AccessPermission) with unitEmployment\n" +
            "optional match (unitEmployment)-[r:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={2} with r,unitEmployment\n" +
            "optional match (unitEmployment)-[:"+HAS_WAGES+"]->(wage:Wage) with wage,unitEmployment,r\n" +
            "return {id:id(unitEmployment),startDate:unitEmployment.startDate,endDate:unitEmployment.endDate,weeklyHours:unitEmployment.weeklyHours,fullTime:unitEmployment.fullTime,employmentType:unitEmployment.employmentType,employmentType:unitEmployment.employmentType,employmentNo:unitEmployment.employmentNumber,isEditable:case when r is NULL then false else true end,wages:case when wage is NULL then [] else collect({id:id(wage),startDate:wage.startDate,endDate:wage.endDate,salary:wage.salary}) end} as data")
    List<Map<String,Object>> getUnitEmploymentsInAllUnits(long staffId, long parentOrganizationId, long childOrganizationId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff) with employment\n" +
            "Match (employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment) with unitEmployment\n" +
            "match (unitEmployment)-[r:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={2} return unitEmployment")
    UnitEmployment getUnitEmployment(long parentOrganizationId, long staffId, long childOrganizationId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]->(staff) with employment\n" +
            "Match (employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment{employmentStatus:{3}}) with unitEmployment\n" +
            "match (unitEmployment)-[r:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={2} return unitEmployment")
    UnitEmployment getUnitEmployment(long parentOrganizationId, long staffId, long childOrganizationId, EmploymentStatus employmentStatus);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:BELONGS_TO]->(staff) with employment\n" +
            "Match (employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment) with unitEmployment\n" +
            "match (unitEmployment)-[r:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={1} with unitEmployment\n" +
            "Match (unitEmployment)-[:"+HAS_ACCESS_PERMISSION+"]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) where id(accessGroup)={3} return accessPermission")
    AccessPermission getAccessPermission(long organizationId, long unitId, long staffId, long accessGroupId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={2} with organization,staff\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:BELONGS_TO]->(staff) with employment\n" +
            "Match (employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment) with unitEmployment\n" +
            "match (unitEmployment)-[r:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={1} with unitEmployment\n" +
            "Match (unitEmployment)-[r:"+HAS_ACCESS_PERMISSION+"]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) where id(accessGroup)={3} set r.isEnabled={4} return r.isEnabled;")
    boolean updateUnitEmployment(long organizationId, long unitId, long staffId, long accessGroupId, boolean isEnabled);

    @Query("Match (unitEmployment:UnitEmployment),(accessPermission:AccessPermission) where id(unitEmployment)={0} AND id(accessPermission)={1}\n" +
            "Create (unitEmployment)-[r:"+HAS_ACCESS_PERMISSION+"{isEnabled:true}]->(accessPermission) return count(r) as count")

    int linkUnitEmploymentWithAccessPermission(long unitEmploymentId, long accessPermissionId);


}
