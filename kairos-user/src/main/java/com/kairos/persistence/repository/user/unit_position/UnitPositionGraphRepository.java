package com.kairos.persistence.repository.user.unit_position;

import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails;
import org.springframework.data.neo4j.annotation.Query;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CTA;

import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;

import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Repository
public interface UnitPositionGraphRepository extends Neo4jBaseRepository<UnitPosition, Long> {
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
    StaffUnitPositionDetails getUnitPositionById(long unitEmploymentId);
/* Error while binding to WTA
* java.lang.IllegalArgumentException: Can not set com.kairos.persistence.model.user.agreement.wta.WTAResponseDTO field com.kairos.persistence.model.user.position_code.StaffUnitEmploymentDetails.workingTimeAgreement to java.util.Collections$UnmodifiableMap
* */
    @Query("match (uEmp:UnitEmployment)  where  Id(uEmp)={0} \n" +
            "match(uEmp)-[:" + HAS_UNIT_EMPLOYMENT_POSITION + "]->(unitEmpPosition:UnitEmploymentPosition{deleted:false})" +
            "match(unitEmpPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
            "match(unitEmpPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
            "match(unitEmpPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false})" +
            "optional match (unitEmpPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement)\n" +
            "optional match (unitEmpPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement)\n" +
            "optional match (unitEmpPosition)-[:" + STAFF_BELONGS_TO_UNION + "]->(unionData:Organization{isEnable:true,union:true})\n" +
            "return expertise as expertise,wta as workingTimeAgreement,cta as costTimeAgreement,unionData as union," +
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
    List<UnitPositionQueryResult> getAllUnitPositionsByStaff(Long unitEmploymentId, Long staffId);

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
    UnitPositionQueryResult getCtaByUnitEmploymentId(Long unitEmploymentId);


    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(u)={1}\n" +
            "match(uep)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={0}\n" +
            "return uep")
    List<UnitPosition> getAllUEPByExpertise(Long expertiseId, Long unitEmploymentId);

    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(uep)={0} return Id(u)")
    Long findEmploymentByUnitPosition(Long unitEmploymentPositionId);

    @Query("match(u:UnitEmployment)-[:HAS_UNIT_EMPLOYMENT_POSITION]-(uep:UnitEmploymentPosition{deleted:false}) where id(u)={1} AND Id(uep)<>{2}\n" +
            "match(uep)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={0}\n" +
            "return uep")
    List<UnitPosition> getAllUEPByExpertiseExcludingCurrent(Long expertiseId, Long unitEmploymentId, Long currentUnitPositionId);

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
