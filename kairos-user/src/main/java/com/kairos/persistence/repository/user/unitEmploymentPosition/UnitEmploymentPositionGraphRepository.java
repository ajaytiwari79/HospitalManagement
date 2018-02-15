package com.kairos.persistence.repository.user.unitEmploymentPosition;

import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.unitEmploymentPosition.StaffUnitEmploymentDetails;
import org.springframework.data.neo4j.annotation.Query;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CTA;

import com.kairos.persistence.model.user.unitEmploymentPosition.PositionCtaWtaQueryResult;

import com.kairos.persistence.model.user.unitEmploymentPosition.UnitEmploymentPosition;
import com.kairos.persistence.model.user.unitEmploymentPosition.UnitEmploymentPositionQueryResult;

import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Repository
public interface UnitEmploymentPositionGraphRepository extends Neo4jBaseRepository<UnitEmploymentPosition, Long> {
    @Query("MATCH (unitEmpPosition:UnitEmploymentPosition{deleted:false}) where id(unitEmpPosition)={0}\n" +
            "match (unitEmpPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "match (unitEmpPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)\n" +
            "optional match (unitEmpPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement)\n" +
            "return e as expertise,cta as costTimeAgreement," +
            "unitEmpPosition.totalWeeklyHours as totalWeeklyHours," +
            "unitEmpPosition.startDateMillis as startDateMillis," +
            "unitEmpPosition.endDateMillis as endDateMillis," +
            "unitEmpPosition.salary as salary," +
            "unitEmpPosition.workingDaysInWeek as workingDaysInWeek," +
            "et as employmentType," +
            "unitEmpPosition.hourlyWages as hourlyWages," +
            "id(unitEmpPosition)   as id," +
            "unitEmpPosition.avgDailyWorkingHours as avgDailyWorkingHours," +
            "unitEmpPosition.lastModificationDate as lastModificationDate")
    StaffUnitEmploymentDetails getUnitEmploymentPositionById(long unitEmploymentId);
/* Error while binding to WTA
* java.lang.IllegalArgumentException: Can not set com.kairos.persistence.model.user.agreement.wta.WTAResponseDTO field com.kairos.persistence.model.user.position_code.StaffUnitEmploymentDetails.workingTimeAgreement to java.util.Collections$UnmodifiableMap
* */
    @Query("match (uEmp:UnitEmployment)  where  Id(uEmp)={0} \n" +
            "match(uEmp)-[:" + HAS_UNIT_EMPLOYMENT_POSITION + "]->(unitEmpPosition:UnitEmploymentPosition{deleted:false})" +
            "match(unitEmpPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitEmpPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "match(unitEmpPosition)-[:" + HAS_POSITION_CODE + "]->(position_code:PositionCode)" +
            "optional match (unitEmpPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement)\n" +
            "optional match (unitEmpPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement)\n" +
            "return expertise as expertise,wta as workingTimeAgreement,cta as costTimeAgreement," +
            "position_code as position_code," +
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

    @Query("Match (org:Organization) where id(org)={0} WITH org\n" +
            "Match (e:Expertise) where id(e)={1} WITH e,org\n" +
            "OPTIONAL MATCH (org)-[:"+HAS_CTA+"]->(cta:CostTimeAgreement{deleted:false}) WITH cta,org,e\n" +
            "MATCH (cta)-[:"+HAS_EXPERTISE_IN+"]->(e)  "+
            "return collect(cta)")
    List<CostTimeAgreement> getCtaByExpertise(Long organizationId, Long expertiseId);

    @Query("Match (org:Organization) where id(org)={0} WITH org\n" +
            "Match (e:Expertise) where id(e)={1} WITH e,org\n" +
            "OPTIONAL Match (org)-[:"+HAS_WTA+"]->(wta:WorkingTimeAgreement{disabled:false}) WITH  wta,org,e\n" +
            "MATCH (wta)-[:"+HAS_EXPERTISE_IN+"]->(e) \n" +
            "return collect(wta)")
    List<WorkingTimeAgreement> getWtaByExpertise(Long organizationId, Long expertiseId);


    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(u)={1}\n" +
            "match(uep)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={0}\n" +
            "return uep")
    List<UnitEmploymentPosition> getAllUEPByExpertise(Long expertiseId, Long unitEmploymentId);

    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(uep)={0} return Id(u)")
    Long findEmploymentByUnitEmploymentPosition(Long unitEmploymentPositionId);

    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(u)={1} AND Id(uep)<>{2}\n" +
            "match(uep)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={0}\n" +
            "return uep")
    List<UnitEmploymentPosition> getAllUEPByExpertiseExcludingCurrent(Long expertiseId, Long unitEmploymentId,Long currentUnitEmploymentPositionId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "return wta LIMIT 1")
    WorkingTimeAgreement getOneDefaultWTA(Long organizationId, Long expertiseId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "return cta LIMIT 1")
    CostTimeAgreement getOneDefaultCTA(Long organizationId, Long expertiseId);

}
