package com.kairos.persistence.repository.user.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplate;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CTA_RULE_TEMPLATE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENT_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_TYPES;

@Repository
public interface CTARuleTemplateGraphRepository  extends Neo4jBaseRepository<CTARuleTemplate,Long> {

   @Query("MATCH (ctaRT:CTARuleTemplate) -[:"+HAS_CTA_RULE_TEMPLATE+"]-(c:Country) WHERE id(c)={0} AND lower(ctaRT.name)=lower({1}) return count(ctaRT)>0")
   Boolean isCTARuleTemplateExistWithSameName(Long countryId, String name);

    @Query("MATCH (ctaRT:CTARuleTemplate) -[:"+HAS_CTA_RULE_TEMPLATE+"]-(c:Country) WHERE id(c)={0} AND lower(ctaRT.ruleTemplateType)=lower({1}) return count(ctaRT)>0")
    Boolean isCTARuleTemplateExistWithSameTemplateType(Long countryId, String ruleTemplateType);

   @Query("MATCH (p:`CTARuleTemplate`)-[:`HAS_RULE_TEMPLATES`]-(m0:`RuleTemplateCategory`) " +
           "WHERE NOT(p.`deleted` = true ) AND ID(m0) IN {0} "+
           "MATCH (p)-[:HAS_CTA_RULE_TEMPLATE]-(country:Country) WHERE id(country)={1} "+
           " optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplateDayTypes:`CTARuleTemplateDayType`)"+
           " optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(dayType:`DayType`)"+
           " optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(countryHolidayCalender:`CountryHolidayCalender`)"+
           " optional  MATCH (p)-[:`HAS_EMPLOYMENT_TYPE`]-(employmentType:`EmploymentType`) "+
           " optional  MATCH (p)-[:`HAS_TIME_TYPES`]-(timeType:`TimeType`) "+
           " optional  MATCH (p)-[:`HAS_COMPENSATION_TABLE`]-(compensationTable:`CompensationTable`) "+
           " optional  MATCH (compensationTable)-[:`HAS_COMPENSATION_TABLE_INTERVAL`]-(compensationTableInterval:`CompensationTableInterval`) "+
           " optional  MATCH (p)-[:`BELONGS_TO`]-(calculateValueAgainst:`CalculateValueAgainst`) "+
           " optional  MATCH (calculateValueAgainst)-[:`BELONGS_TO`]-(fixedValue:`FixedValue`) "+
           " optional  MATCH (fixedValue)-[:`BELONGS_TO`]-(currency:`Currency`) "+
           " optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplatePhaseInfo:`CTARuleTemplatePhaseInfo`) "+
           " optional  MATCH (p)-[:`BELONGS_TO`]-(plannedTimeWithFactor:`PlannedTimeWithFactor`) "+
           " with p,m0,cTARuleTemplateDayTypes,dayType,timeType,employmentType,countryHolidayCalender,"+
           "compensationTable,compensationTableInterval,calculateValueAgainst,fixedValue,currency,cTARuleTemplatePhaseInfo,plannedTimeWithFactor"+
           ", collect(distinct ID(countryHolidayCalender)) as holidaysIds"+
           " RETURN p.name as name ,"+
           "p.description as description,"+
           "p.disabled as disabled,"+
           "ID(m0) as ruleTemplateCategory ,"+
           "p.ruleTemplateType as ruleTemplateType,"+
           "p.payrollType as payrollType ,"+
           "p.payrollSystem as payrollSystem ,"+
           "p.calculationUnit as calculationUnit ,"+
           "{id:ID(compensationTable),granularityLevel:compensationTable.granularityLevel, "+
           "compensationTableInterval:CASE WHEN compensationTableInterval IS NOT NULL THEN collect(distinct{id:ID(compensationTableInterval),compensationMeasurementType:compensationTableInterval.compensationMeasurementType, to:compensationTableInterval.to,from:compensationTableInterval.from,value:compensationTableInterval.value}) ELSE [] END } as compensationTable ,"+
           "{id:ID(calculateValueAgainst),calculateValue:calculateValueAgainst.calculateValue,scale:round(calculateValueAgainst.scale *100)/100,fixedValue:{id:ID(fixedValue),amount:round(fixedValue.amount *100)/100,type:fixedValue.type,currencyId:ID(currency)}} as calculateValueAgainst ,"+
           "p.approvalWorkFlow as approvalWorkFlow ,"+
           "collect(distinct {dayType:ID(dayType),countryHolidayCalenders:holidaysIds}) as calculateOnDayTypes ,"+
           "collect(distinct cTARuleTemplatePhaseInfo) as phaseInfo ,"+
           "p.budgetType as budgetType ,"+
           "p.calculateValueIfPlanned as calculateValueIfPlanned ,"+
           "collect(distinct ID(employmentType)) as employmentTypes ,"+
           "p.activityTypeForCostCalculation as activityTypeForCostCalculation,"+
           "p.plannedTimeId as plannedTimeId, "+
           "p.timeTypeId as timeTypeId, "+
           "p.dayTypeIds as dayTypeIds, "+
           "p.activityIds as activityIds, "+
           "p.planningCategory as planningCategory ,"+
           "p.staffFunctions as staffFunctions ,p.calculateScheduledHours as calculateScheduledHours,"+
           "plannedTimeWithFactor as plannedTimeWithFactor ,"+
//           "collect(distinct ID(timeType)) as timeTypes,"+
           "ID(p) as id")
   List<CTARuleTemplateQueryResult>findByRuleTemplateCategoryIdInAndCountryAndDeletedFalse(List<Long> categoryList, Long countryId);


   @Query("MATCH (ctaRT:CTARuleTemplate) WHERE ctaRT.deleted = false RETURN CASE WHEN COUNT(ctaRT) > 0 THEN true ELSE false END")
    Boolean isDefaultCTARuleTemplateExists();

    @Query("MATCH (ctaRT:CTARuleTemplate)-[r:"+HAS_TIME_TYPES+"]-(t:TimeType) WHERE id(ctaRT)={0} DELETE r")
    void detachAllTimeTypesFromCTARuleTemplate(Long ctaRuleTemplateId);

    @Query("MATCH (ctaRT:CTARuleTemplate)-[r:"+HAS_EMPLOYMENT_TYPE+"]-(et:EmploymentType) WHERE id(ctaRT)={0} DELETE r ")
    void detachAllEmploymentTypesFromCTARuleTemplate(Long ctaRuleTemplateId);

    @Query("MATCH (ctaRT:CTARuleTemplate),(country:Country) WHERE id(country)={0} AND id(ctaRT)={1} CREATE UNIQUE (country)-[r:"+HAS_CTA_RULE_TEMPLATE+"]->(ctaRT) ")
    void addCTARuleTemplateInCountry(Long countryId, Long ctaRuleTemplateId);

}
