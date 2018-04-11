package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.user.staff.Employment;
import com.kairos.persistence.model.user.staff.ExpiredEmploymentsQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 3/12/16.
 */
@Repository
public interface EmploymentGraphRepository extends Neo4jBaseRepository<Employment,Long> {

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1}\n" +
            "Match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[BELONGS_TO]->(staff) return employment")
    Employment findEmployment(long organizationId, long staffId);

    @Query("Match (organization:Organization),(accessGroup:AccessGroup),(staff:Staff) where id(organization)={1} AND id(accessGroup)={2} AND id(staff) ={0} with organization,accessGroup,staff\n" +
            "Match (staff)<-[:"+BELONGS_TO+"]-(employment:Employment)-[:"+ HAS_UNIT_PERMISSIONS +"]->(unitPermission:UnitPermission) with unitPermission,organization,accessGroup\n" +
            "Match (organization)<-[:"+ APPLICABLE_IN_UNIT +"]-(unitPermission)-[:HAS_ACCESS_GROUP]->(accessGroup) with distinct unitPermission,organization\n" +
            "return {id:id(unitPermission),startDate:unitPermission.startDate,endDate:unitPermission.endDate,organizationId:id(organization),status:unitPermission.employmentStatus} as data")
    Map<String,Object> getEmploymentOfParticularRole(long staffId, long organizationId, long accessGroupId);

    @Query("Match (organization:Organization),(staff:Staff),(unit:Organization) where id(organization)={0} AND id(staff) IN {1} AND id(unit)={2}\n" +
            "create (organization)-[r:"+HAS_EMPLOYMENTS+"]->(employment:Employment) with employment,r,staff,organization,unit\n" +
            "create (employment)-[r2:BELONGS_TO]->(staff) create (employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit) return r")
    void createEmployments(long organizationId, List<Long> staffId, long unitId);

    @Query("Match(employment:Employment)-[r1:BELONGS_TO]->(staff:Staff)-[r2:BELONGS_TO_STAFF]->(up:UnitPosition{deleted:false}) where employment.endDateMillis >= {0} and <= {1} \n" +
            "Match(up)-[r3:IN_UNIT]->(org:Organization)-[r4:ORGANIZATION_HAS_ACCESS_GROUPS]->(ag:AccessGroup{isEmploymentExpired:true,deleted:false}) \n" +
            "Match(employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(org) return employment, \n" +
            "case when org IS NOT NULL then COLLECT( distinct ag) else[] end as accessGroups,case when org IS NOT NULL then COLLECT( distinct org) else[] end as organizations, \n" +
            "case when unitPermission is NOT null then COLLECT(distinct unitPermission) else[] end as unitPermissions")
    List<ExpiredEmploymentsQueryResult> findExpiredEmploymentsAccessGroupsAndOrganizationsByEndDate(Long curDateMillisStart, Long curDateMillisEnd);
}

