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
            "optional match (unitEmpPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement)-[:HAS_RULE_TEMPLATE]-(ruleTemp:WTABaseRuleTemplate)\n" +
            "with cta,et,e,unitEmpPosition,wta,CASE  WHEN ruleTemp IS NOT NULL THEN collect({disabled:ruleTemp.disabled,daysLimit:ruleTemp.daysLimit,fromDayOfWeek:ruleTemp.fromDayOfWeek, \n" +
            "minimumDurationBetweenShifts:ruleTemp.minimumDurationBetweenShifts, fromTime:ruleTemp.fromTime,activityCode:ruleTemp.activityCode, \n" +
            "onlyCompositeShifts:ruleTemp.onlyCompositeShifts,shiftsLimit:ruleTemp.shiftsLimit,shiftAffiliation:ruleTemp.shiftAffiliation,averageRest:ruleTemp.averageRest, \n" +
            "continuousWeekRest:ruleTemp.continuousWeekRest,proportional:ruleTemp.proportional,toTime:ruleTemp.toTime,toDayOfWeek:ruleTemp.toDayOfWeek, \n" +
            "continuousDayRestHours:ruleTemp.continuousDayRestHours,name:ruleTemp.name,id:Id(ruleTemp),minimumRest:ruleTemp.minimumRest,timeLimit:ruleTemp.timeLimit,balanceType:ruleTemp.balanceType, \n" +
            "templateType:ruleTemp.templateType,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description, numberShiftsPerPeriod:ruleTemp.numberShiftsPerPeriod, \n" +
            "numberOfWeeks:ruleTemp.numberOfWeeks,maximumVetoPercentage:ruleTemp.maximumVetoPercentage,maximumAvgTime:ruleTemp.maximumAvgTime,useShiftTimes:ruleTemp.useShiftTimes, \n" +
            "balanceAdjustment:ruleTemp.balanceAdjustment,intervalLength:ruleTemp.intervalLength,intervalUnit:ruleTemp.intervalUnit,\n" +
            "validationStartDateMillis:ruleTemp.validationStartDateMillis,daysWorked:ruleTemp.daysWorked,nightsWorked:ruleTemp.nightsWorked,description:ruleTemp.description, \n" +
            "checkAgainstTimeRules:ruleTemp.checkAgainstTimeRules}) else [] END as ruleTemplates\n" +
            "with cta,et,e,unitEmpPosition,collect({name:wta.name,id:id(wta),endDateMillis:wta.endDateMillis ,startDateMillis:wta.startDateMillis ,ruleTemplates:ruleTemplates}) as wtas \n" +
            "return e as expertise,wtas as workingTimeAgreement,cta as costTimeAgreement," +
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

}
