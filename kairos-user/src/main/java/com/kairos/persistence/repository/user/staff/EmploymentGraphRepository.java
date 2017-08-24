package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.user.staff.Employment;
import com.kairos.persistence.model.user.staff.UnitEmployment;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 3/12/16.
 */
@Repository
public interface EmploymentGraphRepository extends GraphRepository<Employment> {

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1}\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[BELONGS_TO]->(staff) return employment")
    Employment findEmployment(long organizationId, long staffId);

    @Query("Match (organization:Organization),(accessGroup:AccessGroup),(staff:Staff) where id(organization)={1} AND id(accessGroup)={2} AND id(staff) ={0} with organization,accessGroup,staff\n" +
            "Match (staff)<-[:"+BELONGS_TO+"]-(employment:Employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment{employmentStatus:'PENDING'}) with unitEmployment,organization,accessGroup\n" +
            "Match (organization)<-[:"+PROVIDED_BY+"]-(unitEmployment)-[:"+HAS_ACCESS_PERMISSION+"{isEnabled:true}]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup) with distinct unitEmployment,organization\n" +
            "return {id:id(unitEmployment),startDate:unitEmployment.startDate,endDate:unitEmployment.endDate,organizationId:id(organization),status:unitEmployment.employmentStatus} as data")
    Map<String,Object> getEmploymentOfParticularRole(long staffId, long organizationId, long accessGroupId);

    @Query("Match (organization:Organization),(staff:Staff),(unit:Organization) where id(organization)={0} AND id(staff) IN {1} AND id(unit)={2}\n" +
            "create (organization)-[r:"+HAS_EMPLOYMENTS+"]->(employment:Employment) with employment,r,staff,organization,unit\n" +
            "create (employment)-[r2:BELONGS_TO]->(staff) create (employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment{employmentStatus:'PENDING'})-[:PROVIDED_BY]->(unit) return r")
    void createEmployments(long organizationId, List<Long> staffId, long unitId);

    @Query("Match (organization:Organization),(staff:Staff),(unit:Organization) where id(organization)={0} AND id(staff)={2} AND id(unit)={1}\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff) with employment,staff,organization,unit match (employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment)-[:PROVIDED_BY]->(unit)\n" +
            "return unitEmployment limit 1")
    UnitEmployment getUnitEmployment(long organizationId, long unitId, long staffId);

    @Query("Match (organization:Organization),(staff:Staff),(unit:Organization) where id(organization)={0} AND id(staff)={2} AND id(unit)={1}\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff) with employment,staff,organization,unit create (employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment{employmentStatus:'PENDING'})-[:PROVIDED_BY]->(unit)\n" +
            "return unitEmployment limit 1")
    UnitEmployment createUnitEmployment(long organizationId, long unitId, long staffId);
}
