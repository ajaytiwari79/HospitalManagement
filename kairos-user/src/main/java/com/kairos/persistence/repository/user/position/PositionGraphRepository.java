package com.kairos.persistence.repository.user.position;

import com.kairos.persistence.model.user.position.Position;
import com.kairos.persistence.model.user.position.PositionQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */
public interface PositionGraphRepository extends GraphRepository<Position> {


    @Query("MATCH (p:Position{isEnabled:true})<-[:" + HAS_POSITION + "]-(u:UnitEmployment) where id(u)={0}\n" +
            "match (p)-[:"+HAS_POSITION_NAME+"]->(pn:PositionName)\n" +
            "match (p)-[:"+HAS_EMPLOYMENT_TYPE+"]->(et:EmploymentType)\n" +
            "match (p)-[:"+HAS_EXPERTISE_IN+"]->(e:Expertise)\n" +
            "match (p)-[:"+HAS_WTA+"]->(wta:WorkingTimeAgreement)\n" +
            "return e as expertise,wta as workingTimeAgreement," +
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


//
//
//    @Query("match(organization:Organization) where id(organization)={0}\n" +
//            "match (staff:Staff) where id(staff)={1}\n" +
//            "match(organization)-[:" + HAS_EMPLOYMENTS + "]->(E:Employment)-[:" + BELONGS_TO + "]->(staff)\n" +
//            "match (organization)<-[:" + PROVIDED_BY + "]-(uEmp:UnitEmployment) \n" +
//            "match (uEmp)-[:HAS_POSITION]->(p:Position)-[:HAS_POSITION_NAME]->(pn:PositionName)\n" +
//            "match (uEmp)-[:HAS_POSITION]->(p:Position)-[:HAS_EXPERTISE_IN]->(e:Expertise)\n" +
//            "return e as expertise," +
//            "pn as positionName," +
//            "p.totalWeeklyHours as totalWeeklyHours," +
//            "p.startDate as startDate,"+
//            "p.endDate as endDate," +
//            "p.salary as salary," +
//            "p.employmentType as employmentType," +
//            "p.isEnabled as isEnabled," +
//            "p.hourlyWages as hourlyWages," +
//            "id(p)   as id," +
//            "p.avgDailyWorkingHours as avgDailyWorkingHours,"+
//            "p.lastModificationDate as lastModificationDate")
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

}
