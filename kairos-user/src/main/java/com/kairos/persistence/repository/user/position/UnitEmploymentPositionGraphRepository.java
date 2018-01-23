package com.kairos.persistence.repository.user.position;

import org.springframework.data.neo4j.annotation.Query;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CTA;

import com.kairos.persistence.model.user.position.PositionCtaWtaQueryResult;

import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import com.kairos.persistence.model.user.position.UnitEmploymentPositionQueryResult;

import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Repository
public interface UnitEmploymentPositionGraphRepository extends Neo4jBaseRepository<UnitEmploymentPosition, Long> {
    @Query("MATCH (unitEmpPosition:UnitEmploymentPosition{deleted:false})<-[:" + HAS_UNIT_EMPLOYMENT_POSITION + "]-(u:UnitEmployment) where id(u)={0}\n" +
            "match (unitEmpPosition)-[:" + HAS_POSITION_CODE + "]->(pn:PositionCode{deleted:false})\n" +
            "match (unitEmpPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "match (unitEmpPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)\n" +
            "optional match (unitEmpPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement)\n" +
            "optional match (unitEmpPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement)\n" +
            "return e as expertise,wta as workingTimeAgreement,cta as costTimeAgreement," +
            "pn as positionCode," +
            "unitEmpPosition.totalWeeklyHours as totalWeeklyHours," +
            "unitEmpPosition.startDateMillis as startDateMillis," +
            "unitEmpPosition.endDateMillis as endDateMillis," +
            "unitEmpPosition.salary as salary," +
            "unitEmpPosition.workingDaysInWeek as workingDaysInWeek," +
            "et as employmentType," +
            "unitEmpPosition.isEnabled as isEnabled," +
            "unitEmpPosition.hourlyWages as hourlyWages," +
            "id(unitEmpPosition)   as id," +
            "unitEmpPosition.avgDailyWorkingHours as avgDailyWorkingHours," +
            "unitEmpPosition.lastModificationDate as lastModificationDate")
    List<UnitEmploymentPositionQueryResult> findAllUnitEmploymentPositions(long unitEmploymentId);

    @Query("match (uEmp:UnitEmployment)  where  Id(uEmp)={0} \n" +
            "match(uEmp)-[:" + HAS_UNIT_EMPLOYMENT_POSITION + "]->(unitEmpPosition:UnitEmploymentPosition{deleted:false})" +
            "match(unitEmpPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitEmpPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "match(unitEmpPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode)" +
            "optional match (unitEmpPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement)\n" +
            "optional match (unitEmpPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement)\n" +
            "return expertise as expertise,wta as workingTimeAgreement,cta as costTimeAgreement," +
            "positionCode as positionCode," +
            "unitEmpPosition.totalWeeklyMinutes as totalWeeklyMinutes," +
            "unitEmpPosition.startDateMillis as startDateMillis," +
            "unitEmpPosition.endDateMillis as endDateMillis," +
            "unitEmpPosition.salary as salary," +
            "unitEmpPosition.workingDaysInWeek as workingDaysInWeek," +
            "employmentType as employmentType," +
            "unitEmpPosition.isEnabled as isEnabled," +
            "unitEmpPosition.hourlyWages as hourlyWages," +
            "id(unitEmpPosition)   as id," +
            "unitEmpPosition.avgDailyWorkingHours as avgDailyWorkingHours," +
            "unitEmpPosition.lastModificationDate as lastModificationDate")
    List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositionByStaff(Long unitEmploymentId, Long staffId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "MATCH (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "Optional Match (org)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement{isEnabled:true})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "return collect(wta) as wta,collect(cta) as cta")
    PositionCtaWtaQueryResult getCtaAndWtaByExpertise(Long organizationId, Long expertiseId);


}
