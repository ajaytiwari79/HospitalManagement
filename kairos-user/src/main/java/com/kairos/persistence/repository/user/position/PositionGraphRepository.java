package com.kairos.persistence.repository.user.position;

import com.kairos.persistence.model.user.position.Position;
import com.kairos.persistence.model.user.position.PositionCtaWtaQueryResult;
import com.kairos.persistence.model.user.position.PositionQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */
public interface PositionGraphRepository extends Neo4jBaseRepository<Position,Long> {


    @Query("MATCH (p:Position{isEnabled:true})<-[:" + HAS_POSITION + "]-(u:UnitEmployment) where id(u)={0}\n" +
            "match (p)-[:"+HAS_POSITION_NAME+"]->(pn:PositionName)\n" +
            "match (p)-[:"+HAS_EMPLOYMENT_TYPE+"]->(et:EmploymentType)\n" +
            "match (p)-[:"+HAS_EXPERTISE_IN+"]->(e:Expertise)\n" +
            "optional match (p)-[:"+HAS_WTA+"]->(wta:WorkingTimeAgreement)\n" +
            "optional match (p)-[:"+HAS_CTA+"]->(cta:CostTimeAgreement)\n" +
            "return e as expertise,wta as workingTimeAgreement,cta as costTimeAgreement," +
            "pn as positionName," +
            "p.totalWeeklyHours as totalWeeklyHours," +
            "p.startDate as startDate,"+
            "p.endDate as endDate," +
            "p.salary as salary," +
            "p.workingDaysInWeek as workingDaysInWeek,"+
            "et as employmentType," +
            "p.isEnabled as isEnabled," +
            "p.hourlyWages as hourlyWages," +
            "id(p)   as id," +
            "p.avgDailyWorkingHours as avgDailyWorkingHours,"+
            "p.lastModificationDate as lastModificationDate")
    List<PositionQueryResult> findAllPositions(long unitEmploymentId);

    @Query("match(organization:Organization)-[:"+HAS_EMPLOYMENTS+"]->(emp:Employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(uEmp:UnitEmployment)  where  Id(organization)={0} And Id(uEmp)={1}\n" +
            "match(uEmp)-[:"+HAS_POSITION+"]->(p:Position)<-[:"+BELONGS_TO_STAFF+"]-(s:Staff) where id(s)={2}\n" +
            "match(p)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) \n" +
            "match(p)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) \n" +
            "match(p)-[:"+HAS_POSITION_NAME+"]->(positionName:PositionName)"+
            "return expertise as expertise," +
            "positionName as positionName," +
            "p.totalWeeklyHours as totalWeeklyHours," +
            "p.startDate as startDate,"+
            "p.endDate as endDate," +
            "p.salary as salary," +
            "p.workingDaysInWeek as workingDaysInWeek,"+
            "employmentType as employmentType," +
            "p.isEnabled as isEnabled," +
            "p.hourlyWages as hourlyWages," +
            "id(p)   as id," +
            "p.avgDailyWorkingHours as avgDailyWorkingHours,"+
            "p.lastModificationDate as lastModificationDate")
    List<PositionQueryResult> getAllPositionByStaff(long unitId,long unitEmploymentId, long staffId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "MATCH (org)-[:HAS_WTA]->(wta:WorkingTimeAgreement{isEnabled:true})-[:HAS_EXPERTISE_IN]->(e)\n" +
            "Optional Match (org)-[:HAS_CTA]->(cta:CostTimeAgreement{isEnabled:true})-[:HAS_EXPERTISE_IN]->(e)\n" +
            "return collect(wta) as wta,collect(cta) as cta")
    PositionCtaWtaQueryResult getCtaAndWtaByExpertise(Long organizationId, Long expertiseId);





}
