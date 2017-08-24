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


    @Query("MATCH (position:Position{isEnabled:true})<-[:" + HAS_POSITION + "]-(u:UnitEmployment) where id(u)={0} return position;")
    List<Position> findAllPositions(long unitEmploymentId);

    @Query("match(organization:Organization) where id(organization)={0}\n" +
            "match (staff:Staff) where id(staff)={1}\n" +
            "match(organization)-[:" + HAS_EMPLOYMENTS + "]->(E:Employment)-[:" + BELONGS_TO + "]->(staff)\n" +
            "match (organization)<-[:" + PROVIDED_BY + "]-(uEmp:UnitEmployment) \n" +
            "match (uEmp)-[:HAS_POSITION]->(p:Position)-[:HAS_POSITION_NAME]->(pn:PositionName)\n" +
            "match (uEmp)-[:HAS_POSITION]->(p:Position)-[:HAS_EXPERTISE_IN]->(e:Expertise)\n" +
            "return e as expertise," +
            "pn as positionName," +
            "p.totalWeeklyHours as totalWeeklyHours," +
            "p.startDate as startDate,"+
            "p.endDate as endDate," +
            "p.salary as salary," +
            "p.employmentType as employmentType," +
            "p.isEnabled as isEnabled," +
            "p.hourlyWages as hourlyWages," +
            "id(p)   as id," +
            "p.avgMonthlyWorkingHours as avgMonthlyWorkingHours,"+
            "p.lastModificationDate as lastModificationDate")
    List<PositionQueryResult> getAllPositionByStaff(long organizationId, long staffId);

}
