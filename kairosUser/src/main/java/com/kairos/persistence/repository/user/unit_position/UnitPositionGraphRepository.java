package com.kairos.persistence.repository.user.unit_position;

import com.kairos.persistence.model.constants.RelationshipConstants;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.staff.EmploymentUnitPositionQueryResult;
import com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
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
    @Query("MATCH (unitPosition:UnitPosition{deleted:false}) where id(unitPosition)={0}\n" +
            "match (unitPosition)-[:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType)\n" +
            "match (unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(e:Expertise)\n" +
            "optional match (unitPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement)\n" +
            "return e as expertise,cta as costTimeAgreement," +
            "unitPosition.totalWeeklyHours as totalWeeklyHours," +
            "unitPosition.startDateMillis as startDateMillis," +
            "unitPosition.endDateMillis as endDateMillis," +
            "unitPosition.salary as salary," +
            "unitPosition.workingDaysInWeek as workingDaysInWeek," +
            "et as employmentType," +
            "unitPosition.hourlyWages as hourlyWages," +
            "id(unitPosition)   as id," +
            "unitPosition.avgDailyWorkingHours as avgDailyWorkingHours," +
            "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis," +
            "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes," +
            "unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes")
    StaffUnitPositionDetails getUnitPositionById(long unitEmploymentId);


    @Query("Match (org:Organization) where id(org)={0} WITH org\n" +
            "Match (e:Expertise) where id(e)={1} WITH e,org\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement{deleted:false}) WITH cta,org,e\n" +
            "MATCH (cta)-[:" + HAS_EXPERTISE_IN + "]->(e)  " +
            "return collect(cta)")
    List<CostTimeAgreement> getCtaByExpertise(Long organizationId, Long expertiseId);

    @Query("Match (org:Organization) where id(org)={0} WITH org\n" +
            "Match (e:Expertise) where id(e)={1} WITH e,org\n" +
            "OPTIONAL Match (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{disabled:false}) WITH  wta,org,e\n" +
            "MATCH (wta)-[:" + HAS_EXPERTISE_IN + "]->(e) \n" +
            "return collect(wta)")
    List<WorkingTimeAgreement> getWtaByExpertise(Long organizationId, Long expertiseId);

    @Query("MATCH (uep:UnitPosition)-[:HAS_CTA]-(cta:CostTimeAgreement{deleted:false}) WHERE id(uep)={0}  WITH cta\n" +
            "optional match(cta)-[:HAS_EXPERTISE_IN]->(expertise:Expertise{deleted:false}) WITH cta,expertise\n" +
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

    @Query("match(s:Staff)-[:" + BELONGS_TO_STAFF + "]-(unitPosition:UnitPosition{deleted:false})-[:" + IN_UNIT + "]-(o:Organization) where id(o)={0} AND id(s)={1} \n" +
            "match(unitPosition)-[:HAS_EXPERTISE_IN]-(e:Expertise) where id(e)={2}\n" +
            "return unitPosition")
    List<UnitPosition> getAllUEPByExpertise(Long unitId, Long staffId, Long expertiseId);


    @Query("match(s:Staff)-[:" + BELONGS_TO_STAFF + "]-(unitPosition:UnitPosition{deleted:false})-[:" + IN_UNIT + "]-(o:Organization) where id(o)={0} AND id(s)={1}  AND Id(unitPosition)<>{3}\n" +
            "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]-(e:Expertise) where id(e)={2}\n" +
            "return unitPosition")
    List<UnitPosition> getAllUEPByExpertiseExcludingCurrent(Long unitId, Long staffId, Long expertiseId, Long currentUnitPositionId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e)\n" +
            "return wta LIMIT 1")
    WorkingTimeAgreement getOneDefaultWTA(Long organizationId, Long expertiseId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (e:Expertise) where id(e)={1}\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement{deleted:false})-[:" + HAS_EXPERTISE_IN + "]->(e) \n" +
            "return cta LIMIT 1")
    CostTimeAgreement getOneDefaultCTA(Long organizationId, Long expertiseId);


    // For test cases
    @Query("match (cta:CostTimeAgreement{deleted:false})<-[:" + HAS_CTA + "]-(unitPosition:UnitPosition{deleted:false})-[:" + IN_UNIT + "]-(o:Organization) where id(o)={0} RETURN unitPosition LIMIT 1")
    UnitPosition getDefaultUnitPositionByOrg(Long unitId);

    @Query("match (cta:CostTimeAgreement{deleted:false})-[:" + HAS_CTA + "]-(unitPosition:UnitPosition) where id(unitPosition)={0} RETURN cta")
    CostTimeAgreement getCTALinkedWithUnitPosition(Long unitPositionId);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) where id(user)={0} \n" +
                    "match(staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) \n" +
                    "match(org)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization) with org,subOrg,staff,employment \n" +
                    "optional match(subOrg)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff) with unitPosition,org,subOrg,staff,employment \n" +
                    "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
                    "match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
                    "match(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
                    "match(unitPosition)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade)" +
                    "match (unitPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement) \n" +
                    "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
                    "optional match (unitPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement) \n" +
                    "optional match (unitPosition)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
                    "optional match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function)" +
                    "with expertise ,org,subOrg,reasonCode,unitPosition,wta,cta,positionCode ,unionData ,seniorityLevel,employmentRel,employmentType,payGrade,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount }) END as functionData " +
                    "return expertise as expertise,wta as workingTimeAgreement,cta as costTimeAgreement,unionData as union, positionCode as positionCode, \n" +
                    " {id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
                    " freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData,payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, \n" +
                    "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDateMillis as startDateMillis, unitPosition.endDateMillis as endDateMillis, \n" +
                    "unitPosition.salary as salary,id(reasonCode) as reasonCodeId,unitPosition.workingDaysInWeek as workingDaysInWeek, \n" +
                    "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
                    "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id,unitPosition.avgDailyWorkingHours as avgDailyWorkingHours, \n" +
                    "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,id(org) as parentUnitId, id(subOrg) as unitId " +
                    "UNION " +
                    "MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) where id(user)={0}\n" +
                    "match(staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:HAS_EMPLOYMENTS]-(org:Organization) \n" +
                    "match(org)<-[:" + IN_UNIT + "]-(unitPosition:UnitPosition{deleted:false})<-[:" + BELONGS_TO_STAFF + "]-(staff)  \n" +
                    "match(unitPosition)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise) \n" +
                    "match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType) \n" +
                    "match(unitPosition)-[:" + HAS_POSITION_CODE + "]->(positionCode:PositionCode{deleted:false}) \n" +
                    "match(unitPosition)-[:" + HAS_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade)" +
                    "MATCH (unitPosition)-[:" + HAS_WTA + "]->(wta:WorkingTimeAgreement) \n" +
                    "OPTIONAL MATCH (unitPosition)-[:" + HAS_REASON_CODE + "]->(reasonCode:ReasonCode) \n" +
                    "optional match (unitPosition)-[:" + HAS_CTA + "]->(cta:CostTimeAgreement) \n" +
                    "optional match (unitPosition)-[:" + SUPPORTED_BY_UNION + "]->(unionData:Organization{isEnable:true,union:true}) \n" +
                    "optional match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function)" +
                    "with expertise ,org,reasonCode,unitPosition,wta,cta,positionCode ,unionData ,seniorityLevel,employmentRel,employmentType,payGrade,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount }) END as functionData " +
                    "return expertise as expertise,wta as workingTimeAgreement,cta as costTimeAgreement,unionData as union, positionCode as positionCode,\n" +
                    " {id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
                    " freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,moreThan:seniorityLevel.moreThan,functions:functionData,payGrade:{id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}} as seniorityLevel, \n" +
                    "unitPosition.totalWeeklyMinutes as totalWeeklyMinutes, unitPosition.startDateMillis as startDateMillis, unitPosition.endDateMillis as endDateMillis, unitPosition.salary as salary, id(reasonCode) as reasonCodeId,\n" +
                    "unitPosition.workingDaysInWeek as workingDaysInWeek, \n" +
                    "{employmentTypeCategory:employmentRel.employmentTypeCategory,name:employmentType.name,id:id(employmentType)} as employmentType, \n" +
                    "unitPosition.hourlyWages as hourlyWages, id(unitPosition) as id, \n" +
                    "unitPosition.avgDailyWorkingHours as avgDailyWorkingHours, \n" +
                    "unitPosition.lastWorkingDateMillis as lastWorkingDateMillis,unitPosition.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes, \n" +
                    "id(org) as parentUnitId,id(org) as unitId ")
    List<UnitPositionQueryResult> getAllUnitPositionsByUser(long userId);
//id:id(seniorityLevel)," +

    @Query("MATCH(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(subOrg:Organization) where id(unitPosition)={0} " +
            "MATCH(unitPosition)<-[:" + BELONGS_TO_STAFF + "]-(staff:Staff) " +
            "MATCH(staff)<-[:" + BELONGS_TO + "]-(employment:Employment) " +
            "MATCH(employment)<-[:" + HAS_EMPLOYMENTS + "]-(org:Organization) " +
            "RETURN id(subOrg) as unitId,id(org) as parentUnitId")
    UnitPositionQueryResult getUnitIdAndParentUnitIdByUnitPositionId(Long unitPositionId);



    @Query("match(staff:Staff)-[r1:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff)={1} \n" +
            "Match(org:Organization)-[r2:" + HAS_SUB_ORGANIZATION + "]->(suborg:Organization)<-[r3:" + IN_UNIT + "]-(up) where id(org)={0}  return \n" +
            "up.totalWeeklyMinutes as totalWeeklyMinutes, \n" +
            "up.startDateMillis as startDateMillis, \n" +
            "up.endDateMillis as endDateMillis, \n" +
            "up.salary as salary, \n" +
            "up.workingDaysInWeek as workingDaysInWeek, \n" +
            "up.hourlyWages as hourlyWages, \n" +
            "id(up)   as id, \n" +
            "up.avgDailyWorkingHours as avgDailyWorkingHours, \n" +
            "up.lastWorkingDateMillis as lastWorkingDateMillis \n" +
            "UNION \n" +
            "Match(staff:Staff)-[r11:" + BELONGS_TO_STAFF + "]->(up:UnitPosition{deleted:false}) where id(staff) = {1} Match(org:Organization)<-[r22:" + IN_UNIT + "]-(up) where id(org)= {0} return  \n"+
            "up.totalWeeklyMinutes as totalWeeklyMinutes, \n" +
            "up.startDateMillis as startDateMillis, \n" +
            "up.endDateMillis as endDateMillis, \n" +
            "up.salary as salary, \n" +
            "up.workingDaysInWeek as workingDaysInWeek, \n" +
            "up.hourlyWages as hourlyWages, \n" +
            "id(up)   as id, \n" +
            "up.avgDailyWorkingHours as avgDailyWorkingHours, \n" +
            "up.lastWorkingDateMillis as lastWorkingDateMillis ")
    List<UnitPositionQueryResult> getAllUnitPositionsByStaffId(Long organizationId, Long staffId);

    @Query("match(unitPosition)-[employmentRel:" + HAS_EMPLOYMENT_TYPE + "]->(employmentType:EmploymentType)  where id(unitPosition)={0} return unitPosition,employmentRel,employmentType")
    UnitPositionEmploymentTypeRelationShip findEmploymentTypeByUnitPositionId(Long unitPositionId);

    @Query("match(unitPosition)-[rel:" + HAS_FUNCTION + "]->(functions:Function) where id(unitPosition)={0}  detach delete rel")
    void removeOlderFunctionsFromUnitPosition(Long unitPositionId);

    @Query("Match(staff:Staff)-[:"+BELONGS_TO_STAFF+"]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.endDateMillis > {1} or up.endDateMillis is null)  set up.endDateMillis = {1}")
    void updateUnitPositionEndDateFromEmployment(Long staffId, Long endDateMillis);

    @Query("Match(staff:Staff)-[:"+BELONGS_TO_STAFF+"]->(up:UnitPosition{deleted:false}) where id(staff)={0} and ( up.endDateMillis > {1} or up.endDateMillis is null)  return up")
    List<UnitPosition> getUnitPositionsFromEmploymentEndDate(Long staffId, Long endDateMillis);

    @Query("Match(staff:Staff)-[:"+ BELONGS_TO_STAFF +"]->(up:UnitPosition{deleted:false}) where id(staff)={0} Match(staff)-[:"+BELONGS_TO+"]->(emp:Employment) return min(up.startDateMillis) as unitPositionMinStartDate, emp.endDateMillis as employmentEndDate")
     EmploymentUnitPositionQueryResult getUnitPositionMinStartDateAndEmploymentByStaffId(Long staffId);

}
