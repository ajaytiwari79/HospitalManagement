package com.kairos.persistence.repository.user.position;

import com.kairos.persistence.model.user.position.StaffUnitEmploymentDetails;
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

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "OPTIONAL Match (org)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "return collect(wta) as wta,collect(cta) as cta")
    PositionCtaWtaQueryResult getCtaAndWtaByExpertise(Long organizationId, Long expertiseId);

    @Query("MATCH (uep:UnitEmploymentPosition)-[:HAS_CTA]-(cta:CostTimeAgreement{deleted:false}) WHERE id(uep)={0}  WITH cta\n" +
            "optional match(cta)-[:HAS_EXPERTISE_IN]->(expertise:Expertise{isEnabled:true}) WITH cta,expertise\n" +
            "optional match (cta)-[:BELONGS_TO_ORG_TYPE]->(orgType:OrganizationType) WITH cta,expertise,orgType\n" +
            "optional match(cta)-[:BELONGS_TO_ORG_SUB_TYPE]->(orgSubType:OrganizationType) WITH cta,expertise,orgType,orgSubType\n" +
            "OPTIONAL MATCH (cta)-[:HAS_RULE_TEMPLATE]-(ruleTemp:`CTARuleTemplate`)  WHERE NOT(ruleTemp.`deleted` = true ) AND NOT(ruleTemp.`disabled` = true ) WITH cta,expertise,orgType,orgSubType,ruleTemp\n" +
            "optional  MATCH (ruleTemp)-[:`HAS_RULE_TEMPLATES`]-(ruleTemplCat:`RuleTemplateCategory`) WITH cta,expertise,orgType,orgSubType,ruleTemp,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(cTARuleTemplateDayTypes:`CTARuleTemplateDayType`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes, CASE WHEN ruleTemplCat IS NULL THEN null ELSE ID(ruleTemplCat) END as ruleTemplCat\n" +
            "optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(dayType:`DayType`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,dayType,ruleTemplCat\n" +
            "optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(countryHolidayCalender:`CountryHolidayCalender`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,dayType,CASE WHEN countryHolidayCalender IS NULL THEN [] ELSE collect(distinct ID(countryHolidayCalender)) END  as countryHolidayCalender,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`HAS_ACCESS_GROUP`]-(accessGroup:`AccessGroup`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,\n" +
            "CASE WHEN cTARuleTemplateDayTypes IS NULL THEN [] ELSE collect(distinct {dayType:ID(dayType),countryHolidayCalenders:countryHolidayCalender}) END as calculateOnDayTypes\n" +
            ",ruleTemplCat \n" +
            "optional  MATCH (ruleTemp)-[:`HAS_EMPLOYMENT_TYPE`]-(employmentType:`EmploymentType`)  WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,CASE WHEN employmentType IS NULL THEN [] ELSE  collect(distinct ID(employmentType)) END as employmentTypes,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`HAS_TIME_TYPES`]-(timeType:`TimeType`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, CASE WHEN timeType IS NULL THEN [] ELSE collect(distinct ID(timeType)) END as timeTypes,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`HAS_COMPENSATION_TABLE`]-(compensationTable:`CompensationTable`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, timeTypes,compensationTable,ruleTemplCat\n" +
            "optional  MATCH (compensationTable)-[:`HAS_COMPENSATION_TABLE_INTERVAL`]-(compensationTableInterval:`CompensationTableInterval`)  \n" +
            "WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, timeTypes,CASE WHEN compensationTableInterval IS NOT NULL THEN collect(distinct{id:ID(compensationTableInterval),to:compensationTableInterval.to,from:compensationTableInterval.from,value:compensationTableInterval.value}) ELSE [] END as compensationTableInterval,compensationTable,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(calculateValueAgainst:`CalculateValueAgainst`)  \n" +
            "optional  MATCH (calculateValueAgainst)-[:`BELONGS_TO`]-(fixedValue:`FixedValue`)  \n" +
            "optional  MATCH (fixedValue)-[:`BELONGS_TO`]-(currency:`Currency`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, timeTypes,\n" +
            "CASE WHEN compensationTable IS NULL THEN NULL ELSE {id:ID(compensationTable),granularityLevel:compensationTable.granularityLevel,compensationMeasurementType:compensationTable.compensationMeasurementType,compensationTableInterval:compensationTableInterval} END as compensationTable,\n" +
            "CASE WHEN calculateValueAgainst IS NULL  THEN null ELSE {id:ID(calculateValueAgainst),scale:calculateValueAgainst.scale,fixedValue:{id:ID(fixedValue),amount:fixedValue.amount,type:fixedValue.type,currencyId:ID(currency)},\n" +
            "currency:{id:ID(currency), name:currency.name, description:currency.description}} END as calculateValueAgainst,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(cTARuleTemplatePhaseInfo:`CTARuleTemplatePhaseInfo`)  WITH \n" +
            "cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, timeTypes,compensationTable,calculateValueAgainst, \n" +
            "CASE WHEN cTARuleTemplatePhaseInfo IS NULL THEN [] ELSE collect({phaseId:cTARuleTemplatePhaseInfo.phaseId,type:cTARuleTemplatePhaseInfo.type,beforeStart:cTARuleTemplatePhaseInfo.beforeStart}) END  as phaseInfo,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(activityType:`ActivityType`)  WITH \n" +
            "cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, timeTypes,compensationTable,calculateValueAgainst,phaseInfo,activityType ,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(plannedTimeWithFactor:`PlannedTimeWithFactor`)  WITH \n" +
            "cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,employmentTypes, timeTypes,compensationTable,calculateValueAgainst,phaseInfo,activityType, plannedTimeWithFactor ,ruleTemplCat\n" +
            "RETURN id(cta) as id,cta.startDateMillis as startDateMillis, cta.endDateMillis as endDateMillis, id(expertise) as expertise, id(orgType) as organizationType, id(orgSubType) as organizationSubType, cta.description as description,cta.name as name,CASE WHEN ruleTemp IS NULL THEN [] ELSE collect({id:id(ruleTemp),ruleTemplateCategory:ruleTemplCat,name:ruleTemp.name,approvalWorkFlow:ruleTemp.approvalWorkFlow ,description:ruleTemp.description,disabled:ruleTemp.disabled ,budgetType : ruleTemp.budgetType,planningCategory:ruleTemp.planningCategory,staffFunctions:ruleTemp.staffFunctions,ruleTemplateType:ruleTemp.ruleTemplateType,payrollType:ruleTemp.payrollType ,payrollSystem:ruleTemp.payrollSystem,timeTypes:timeTypes,calculationUnit:ruleTemp.calculationUnit,compensationTable:compensationTable, calculateValueAgainst:calculateValueAgainst, calculateValueIfPlanned:ruleTemp.calculateValueIfPlanned,employmentTypes:employmentTypes,phaseInfo:phaseInfo,activityType:{id:id(activityType),onlyForActivityThatPartOfCostCalculation:activityType.onlyForActivityThatPartOfCostCalculation,activityTypes:activityType.activityTypes },plannedTimeWithFactor:{id:id(plannedTimeWithFactor), scale:plannedTimeWithFactor.scale, add:plannedTimeWithFactor.add, accountType:plannedTimeWithFactor.accountType},calculateOnDayTypes:calculateOnDayTypes}) END as ruleTemplates ORDER BY id DESC")
    UnitEmploymentPositionQueryResult getCtaByUnitEmploymentId(Long unitEmploymentPositionId);


    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(u)={1}\n" +
            "match(uep)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={0}\n" +
            "return uep")
    List<UnitEmploymentPosition> getAllUEPByExpertise(Long expertiseId, Long unitEmploymentId);
}
