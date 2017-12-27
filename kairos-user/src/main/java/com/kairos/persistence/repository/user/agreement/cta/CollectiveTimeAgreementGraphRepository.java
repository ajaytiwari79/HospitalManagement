package com.kairos.persistence.repository.user.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CTAListQueryResult;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateQueryResult;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectiveTimeAgreementGraphRepository extends Neo4jBaseRepository<CostTimeAgreement,Long> {


    /*@Query("MATCH (cta:CostTimeAgreement{deleted:false})-[:`BELONGS_TO`]-(country:Country) WHERE id(country)={0} "+
            "MATCH (cta)-[:HAS_RULE_TEMPLATE]-(p:`CTARuleTemplate`)-[:`HAS_RULE_TEMPLATES`]-(m0:`RuleTemplateCategory`) " +
            "WHERE NOT(p.`deleted` = true ) AND NOT(p.`disabled` = true )   "+
            " optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplateDayTypes:`CTARuleTemplateDayType`)"+
            " optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(dayType:`DayType`)"+
            " optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(countryHolidayCalender:`CountryHolidayCalender`)"+
            " optional  MATCH (p)-[:`HAS_ACCESS_GROUP`]-(accessGroup:`AccessGroup`) "+
            " optional  MATCH (p)-[:`HAS_EMPLOYMENT_TYPE`]-(employmentType:`EmploymentType`) "+
            " optional  MATCH (p)-[:`HAS_TIME_TYPES`]-(timeType:`TimeType`) "+
            " optional  MATCH (p)-[:`HAS_COMPENSATION_TABLE`]-(compensationTable:`CompensationTable`) "+
            " optional  MATCH (compensationTable)-[:`HAS_COMPENSATION_TABLE_INTERVAL`]-(compensationTableInterval:`CompensationTableInterval`) "+
            " optional  MATCH (p)-[:`BELONGS_TO`]-(calculateValueAgainst:`CalculateValueAgainst`) "+
            " optional  MATCH (calculateValueAgainst)-[:`BELONGS_TO`]-(fixedValue:`FixedValue`) "+
            " optional  MATCH (fixedValue)-[:`BELONGS_TO`]-(currency:`Currency`) "+
            " optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplatePhaseInfo:`CTARuleTemplatePhaseInfo`) "+
            " optional  MATCH (p)-[:`BELONGS_TO`]-(activityType:`ActivityType`) "+
            " optional  MATCH (p)-[:`BELONGS_TO`]-(plannedTimeWithFactor:`PlannedTimeWithFactor`) "+
            " with p,m0,cTARuleTemplateDayTypes,dayType,accessGroup,timeType,employmentType,countryHolidayCalender,"+
            "compensationTable,compensationTableInterval,calculateValueAgainst,fixedValue,currency,cTARuleTemplatePhaseInfo,activityType,plannedTimeWithFactor"+
            ", collect(distinct ID(countryHolidayCalender)) as holidaysIds"+
            " RETURN " +
            "p.name as name ,"+
            "p.description as description,"+
            "p.disabled as disabled,"+
            "ID(m0) as ruleTemplateCategory ,"+
            "p.ruleTemplateType as ruleTemplateType,"+
            "p.payrollType as payrollType ,"+
            "p.payrollSystem as payrollSystem ,"+
            "p.calculationUnit as calculationUnit ,"+
            "{id:ID(compensationTable),granularityLevel:compensationTable.granularityLevel,compensationMeasurementType:compensationTable.compensationMeasurementType, "+
            "compensationTableInterval:CASE WHEN compensationTableInterval IS NOT NULL THEN collect(distinct{id:ID(compensationTableInterval),to:compensationTableInterval.to,from:compensationTableInterval.from,value:compensationTableInterval.value}) ELSE [] END } as compensationTable ,"+
            "{id:ID(calculateValueAgainst),scale:calculateValueAgainst.scale,fixedValue:{id:ID(fixedValue),amount:fixedValue.amount,type:fixedValue.type,currencyId:ID(currency)}} as calculateValueAgainst ,"+
            "p.approvalWorkFlow as approvalWorkFlow ,"+
            "collect(distinct {dayType:ID(dayType),countryHolidayCalenders:holidaysIds}) as calculateOnDayTypes ,"+
            "collect(distinct cTARuleTemplatePhaseInfo) as phaseInfo ,"+
            "p.budgetType as budgetType ,"+
            "collect(distinct ID(accessGroup)) as calculateValueIfPlanned ,"+
            "collect(distinct ID(employmentType)) as employmentTypes ,"+
            "activityType as activityType ,"+
            "p.planningCategory as planningCategory ,"+
            "p.staffFunctions as staffFunctions ,"+
            "plannedTimeWithFactor as plannedTimeWithFactor ,"+
            "collect(distinct ID(timeType)) as timeTypes,"+
            "ID(p) as id")
    List<CTARuleTemplateQueryResult> findCTAByCountryId(Long countryId);*/

    @Query("MATCH (cta:CostTimeAgreement)-[:`BELONGS_TO`]-(country:Country) WHERE id(country)={0} " +
            "MATCH (cta)-[:HAS_RULE_TEMPLATE]-(p:`CTARuleTemplate`)  WHERE NOT(p.`deleted` = true ) AND NOT(p.`disabled` = true ) " +
            " optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplateDayTypes:`CTARuleTemplateDayType`) " +
            "optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(dayType:`DayType`) " +
            "optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(countryHolidayCalender:`CountryHolidayCalender`) " +
            "optional  MATCH (p)-[:`HAS_ACCESS_GROUP`]-(accessGroup:`AccessGroup`)  " +
            "optional  MATCH (p)-[:`HAS_EMPLOYMENT_TYPE`]-(employmentType:`EmploymentType`)  " +
            "optional  MATCH (p)-[:`HAS_TIME_TYPES`]-(timeType:`TimeType`)  " +
            "optional  MATCH (p)-[:`HAS_COMPENSATION_TABLE`]-(compensationTable:`CompensationTanameble`)  " +
            "optional  MATCH (compensationTable)-[:`HAS_COMPENSATION_TABLE_INTERVAL`]-(compensationTableInterval:`CompensationTableInterval`)  " +
            "optional  MATCH (p)-[:`BELONGS_TO`]-(calculateValueAgainst:`CalculateValueAgainst`)  " +
            "optional  MATCH (calculateValueAgainst)-[:`BELONGS_TO`]-(fixedValue:`FixedValue`)  " +
            "optional  MATCH (fixedValue)-[:`BELONGS_TO`]-(currency:`Currency`)  " +
            "optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplatePhaseInfo:`CTARuleTemplatePhaseInfo`)  " +
            "optional  MATCH (p)-[:`BELONGS_TO`]-(activityType:`ActivityType`)  " +
            "optional  MATCH (p)-[:`BELONGS_TO`]-(plannedTimeWithFactor:`PlannedTimeWithFactor`)  " +
            "with cta,p,cTARuleTemplateDayTypes,dayType,accessGroup,timeType,employmentType,countryHolidayCalender,compensationTable,compensationTableInterval,calculateValueAgainst,fixedValue,currency,cTARuleTemplatePhaseInfo,activityType,plannedTimeWithFactor, collect(distinct ID(countryHolidayCalender)) as holidaysIds, collect({id:id(p),name:p.name ,description:p.description,disabled:p.disabled ,ruleTemplateType:p.ruleTemplateType,payrollType:p.payrollType ,payrollSystem:p.payrollSystem ,calculationUnit:p.calculationUnit}) as ruleTemplates\n" +
            " RETURN id(cta) as id, cta.description as description,cta.name as name, ruleTemplates as ruleTemplates")
    List<CTAListQueryResult> findCTAByCountryId(Long countryId);


}