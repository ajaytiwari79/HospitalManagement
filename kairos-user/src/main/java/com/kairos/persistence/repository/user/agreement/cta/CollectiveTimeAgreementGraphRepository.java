package com.kairos.persistence.repository.user.agreement.cta;

import com.kairos.persistence.model.constants.RelationshipConstants;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CTAListQueryResult;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateQueryResult;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface CollectiveTimeAgreementGraphRepository extends Neo4jBaseRepository<CostTimeAgreement,Long> {


    /*@Query("MATCH (cta:CostTimeAgreement{deleted:false})-[:`BELONGS_TO`]-(country:Country) WHERE id(country)= {0} WITH cta\n" +
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
            ",CASE WHEN accessGroup IS NULL THEN [] ELSE collect(distinct ID(accessGroup)) END as calculateValueIfPlanned,ruleTemplCat \n" +
            "optional  MATCH (ruleTemp)-[:`HAS_EMPLOYMENT_TYPE`]-(employmentType:`EmploymentType`)  WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,CASE WHEN employmentType IS NULL THEN [] ELSE  collect(distinct ID(employmentType)) END as employmentTypes,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`HAS_TIME_TYPES`]-(timeType:`TimeType`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, CASE WHEN timeType IS NULL THEN [] ELSE collect(distinct ID(timeType)) END as timeTypes,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`HAS_COMPENSATION_TABLE`]-(compensationTable:`CompensationTable`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, timeTypes,compensationTable,ruleTemplCat\n" +
            "optional  MATCH (compensationTable)-[:`HAS_COMPENSATION_TABLE_INTERVAL`]-(compensationTableInterval:`CompensationTableInterval`)  \n" +
            "WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, timeTypes,CASE WHEN compensationTableInterval IS NOT NULL THEN collect(distinct{id:ID(compensationTableInterval),to:compensationTableInterval.to,from:compensationTableInterval.from,value:compensationTableInterval.value}) ELSE [] END as compensationTableInterval,\n" +
            "CASE WHEN compensationTable IS NULL THEN NULL ELSE {id:ID(compensationTable),granularityLevel:compensationTable.granularityLevel,compensationMeasurementType:compensationTable.compensationMeasurementType,compensationTableInterval:compensationTableInterval} END as compensationTable,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(calculateValueAgainst:`CalculateValueAgainst`)  \n" +
            "optional  MATCH (calculateValueAgainst)-[:`BELONGS_TO`]-(fixedValue:`FixedValue`)  \n" +
            "optional  MATCH (fixedValue)-[:`BELONGS_TO`]-(currency:`Currency`) WITH cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, timeTypes,compensationTableInterval,compensationTable,\n" +
            "CASE WHEN calculateValueAgainst IS NULL  THEN null ELSE {id:ID(calculateValueAgainst),scale:calculateValueAgainst.scale,fixedValue:{id:ID(fixedValue),amount:fixedValue.amount,type:fixedValue.type,currencyId:ID(currency)},\n" +
            "currency:{id:ID(currency), name:currency.name, description:currency.description}} END as calculateValueAgainst,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(cTARuleTemplatePhaseInfo:`CTARuleTemplatePhaseInfo`)  WITH \n" +
            "cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, timeTypes,compensationTableInterval,compensationTable,calculateValueAgainst, \n" +
            "CASE WHEN cTARuleTemplatePhaseInfo IS NULL THEN [] ELSE collect(distinct cTARuleTemplatePhaseInfo) END  as phaseInfo,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(activityType:`ActivityType`)  WITH \n" +
            "cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, timeTypes,compensationTableInterval,compensationTable,calculateValueAgainst,phaseInfo,activityType ,ruleTemplCat\n" +
            "optional  MATCH (ruleTemp)-[:`BELONGS_TO`]-(plannedTimeWithFactor:`PlannedTimeWithFactor`)  WITH \n" +
            "cta,expertise,orgType,orgSubType,ruleTemp,cTARuleTemplateDayTypes,calculateOnDayTypes,calculateValueIfPlanned,employmentTypes, timeTypes,compensationTableInterval,compensationTable,calculateValueAgainst,phaseInfo,activityType, plannedTimeWithFactor ,ruleTemplCat\n" +
            "RETURN id(cta) as id,cta.startDateMillis as startDateMillis, cta.endDateMillis as endDateMillis, id(expertise) as expertise, id(orgType) as organizationType, id(orgSubType) as organizationSubType, cta.description as description,cta.name as name,CASE WHEN ruleTemp IS NULL THEN [] ELSE collect({id:id(ruleTemp),ruleTemplateCategory:ruleTemplCat,name:ruleTemp.name,approvalWorkFlow:ruleTemp.approvalWorkFlow ,description:ruleTemp.description,disabled:ruleTemp.disabled ,budgetType : ruleTemp.budgetType,planningCategory:ruleTemp.planningCategory,staffFunctions:ruleTemp.staffFunctions,ruleTemplateType:ruleTemp.ruleTemplateType,payrollType:ruleTemp.payrollType ,payrollSystem:ruleTemp.payrollSystem,timeTypes:timeTypes,calculationUnit:ruleTemp.calculationUnit,compensationTable:compensationTable, calculateValueAgainst:calculateValueAgainst, calculateValueIfPlanned:calculateValueIfPlanned,employmentTypes:employmentTypes,phaseInfo:phaseInfo,activityType:{id:id(activityType),onlyForActivityThatPartOfCostCalculation:activityType.onlyForActivityThatPartOfCostCalculation,activityTypes:activityType.activityTypes },plannedTimeWithFactor:{id:id(plannedTimeWithFactor), scale:plannedTimeWithFactor.scale, add:plannedTimeWithFactor.add, accountType:plannedTimeWithFactor.accountType},calculateOnDayTypes:calculateOnDayTypes}) END as ruleTemplates")
    */
    @Query("MATCH (cta:CostTimeAgreement{deleted:false})-[:`BELONGS_TO`]-(country:Country) WHERE id(country)= {0}  WITH cta\n" +
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
    List<CTAListQueryResult> findCTAByCountryId(Long countryId);

    @Query("MATCH (cta:CostTimeAgreement{deleted:false})-[:`HAS_CTA`]-(unit:Organization) WHERE id(unit)={0}  WITH cta\n" +
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
    List<CTAListQueryResult> findCTAByUnitId(Long unitId);


    @Query("MATCH (cta:CostTimeAgreement)-[:`BELONGS_TO`]-(country:Country) WHERE id(country)= {0} AND id(cta) = {1} AND cta.deleted={2} return cta")
    CostTimeAgreement findCTAByCountryAndIdAndDeleted(Long countryId, Long ctaId, Boolean deleted);

    @Query("match(ost:OrganizationType) where  id(ost) in {0} \n" +
            "match(cta:CostTimeAgreement)-[:"+ BELONGS_TO_ORG_SUB_TYPE+"]->(ost) WHERE cta.deleted={1}\n" +
            "return cta")
    List<CostTimeAgreement> getAllCTAByOrganiationSubType(List<Long> organizationSubTypeIds, Boolean deleted);

    @Query("Match (organization:Organization) where id(organization)={0} with organization"+
            "Match (organization)-[r:HAS_CTA]->(cta:CostTimeAgreement)  WHERE id(cta) = {1} DELETE r")
    void detachCTAFromOrganization(Long orgId, Long ctaId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization"+
        "Match (organization)-[r:HAS_CTA]->(cta:CostTimeAgreement) DELETE r")
    void detachAllCTAFromOrganization(Long orgId);

    @Query("Match (cta:CostTimeAgreement)-[r:"+BELONGS_TO+"]-(country:Country) WHERE id(cta) = {0}\n" +
            "RETURN CASE WHEN r IS NULL THEN false ELSE true END")
    Boolean isCTALinkedWithCountry(Long ctaId);

    @Query("MATCH (cta:CostTimeAgreement)<-[r:"+HAS_PARENT_CTA+"]-(chiltCTA:CostTimeAgreement) WHERE id(cta) = {0} return chiltCTA")
    CostTimeAgreement fetchChildCTA(Long ctaId);

    @Query("MATCH (cta:CostTimeAgreement)<-[r:"+HAS_PARENT_CTA+"]-(chiltCTA:CostTimeAgreement) WHERE id(chiltCTA) = {0} DELETE r")
    void detachParentCTA(Long ctaId);

    @Query("Match (o:Organization{isEnable:true})-[:"+HAS_CTA+"]-(orgCta:CostTimeAgreement)-[:"+HAS_PARENT_COUNTRY_CTA+"]->(countryCta:CostTimeAgreement)\n"+
            "WHERE id(countryCta)={0} return orgCta")
    List<CostTimeAgreement> getListOfOrganizationCTAByParentCountryCTA(Long countryCTAId);

    @Query("MATCH (cta:CostTimeAgreement{deleted:false})-[:`BELONGS_TO`]-(country:Country) WHERE id(country)= {0} AND  lower(cta.name)=lower({1}) RETURN CASE WHEN COUNT(cta)>0 THEN true ELSE false END")
    Boolean isCTAExistWithSameNameInCountry(Long countryId, String name);

    @Query("MATCH (cta:CostTimeAgreement{deleted:false})-[:`BELONGS_TO`]-(country:Country) WHERE id(country)= {0} AND  lower(cta.name)=lower({1}) AND NOT id(cta)={2} RETURN CASE WHEN COUNT(cta)>0 THEN true ELSE false END")
    Boolean isCTAExistWithSameNameInCountry(Long countryId, String name, Long ctaId);

    @Query("MATCH (cta:CostTimeAgreement{deleted:false})<-[:`HAS_CTA`]-(org:Organization) WHERE id(org)= {0} AND  lower(cta.name)=lower({1}) AND NOT id(cta)={2} RETURN CASE WHEN COUNT(cta)>0 THEN true ELSE false END")
    Boolean isCTAExistWithSameNameInUnit(Long unitId, String name, Long ctaId);
}