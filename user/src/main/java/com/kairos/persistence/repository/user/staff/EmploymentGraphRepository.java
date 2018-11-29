package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.model.staff.employment.EmploymentReasonCodeQueryResult;
import com.kairos.persistence.model.staff.employment.ExpiredEmploymentsQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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


    @Query("Match(employment:Employment)-[r1:" + BELONGS_TO + "]->(staff:Staff) where id(employment) in {0} \n" +
            "Match(employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org:Organization) return employment, \n" +
            "case when org IS NOT NULL then COLLECT( distinct org) else[] end as organizations, \n" +
            "case when unitPermission is NOT null then COLLECT(distinct unitPermission) else[] end as unitPermissions")
    List<ExpiredEmploymentsQueryResult> findExpiredEmploymentsAccessGroupsAndOrganizationsByEndDate(List<Long> employmentIds);

    @Query("Match(staff:Staff)<-[:"+ BELONGS_TO +"]-(employment:Employment) where id(staff) = {0} optional Match(employment)-[:"+HAS_REASON_CODE+"]-(reasonCode:ReasonCode)  return employment, reasonCode")
    EmploymentReasonCodeQueryResult findEmploymentreasonCodeByStaff(Long staffId);

    @Query("Match(staff:Staff)<-[:"+ BELONGS_TO +"]-(emp:Employment) where id(staff) = {0} return emp")
    Employment findEmploymentByStaff(Long staffId);


    @Query("Match(staff:Staff)<-[:"+ BELONGS_TO +"]-(emp:Employment) where id(staff) = {0} set emp.startDateMillis = {1}")
    void updateEmploymentStartDate(Long staffId, Long endDateMillis);

    @Query("Match(staff:Staff)-[:" + BELONGS_TO + "]-(emp:Employment) Match(emp)-[r:"+ HAS_REASON_CODE +"]-(reasonCode:ReasonCode) where id(staff)={0} delete r")
    void deleteEmploymentReasonCodeRelation(Long staffId);

    // TODO By Harish  please don't delete
 //    @Query("match (staff:Staff)-[:" +BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition) where id(staff)={0} with staff\n" +
//            "match (staff)<-[:" + BELONGS_TO + "]-(employment:Employment) RETURN employment")
//    Employment findUnitPostionAndEmploymentByStaff(Long staffId);

    @Query("Match(staff:Staff)-[:"+BELONGS_TO+"]-(emp:Employment) where id(emp)={0} return id(staff)")
    Long findStaffByEmployment(Long employmentId);

}

