package com.kairos.persistence.repository.user.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplate;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CTARuleTemplateGraphRepository  extends GraphRepository<CTARuleTemplate> {

 @Query("MATCH (p:`CTARuleTemplate`)-[:`HAS_RULE_TEMPLATES`]-(m0:`RuleTemplateCategory`) " +
         "WHERE NOT(p.`deleted` = true ) AND NOT(p.`disabled` = true )  and ID(m0) IN {0} "+
         " optional  MATCH (p)-[:`BELONGS_TO`]-(cTARuleTemplateDayTypes:`CTARuleTemplateDayTypes`)"+
         " optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(dayType:`DayType`)"+
         " optional  MATCH (cTARuleTemplateDayTypes)-[:`BELONGS_TO`]-(countryHolidayCalender:`CountryHolidayCalender`)"+
         " optional  MATCH (p)-[:`HAS_ACCESS_GROUP`]-(accessGroup:`AccessGroup`) "+
         " optional  MATCH (p)-[:`HAS_EMPLOYMENT_TYPE`]-(employmentType:`EmploymentType`) "+
         " optional  MATCH (p)-[:`HAS_TIME_TYPES`]-(timeType:`TimeType`) "+
         " with p,m0,cTARuleTemplateDayTypes,dayType,accessGroup,timeType,employmentType,countryHolidayCalender,"+
         " collect(ID(countryHolidayCalender)) as holidaysIds"+
         " RETURN p.name as name ,"+
         "p.description as description,"+
         "p.disabled as disabled,"+
         "ID(m0) as ruleTemplateCategory ,"+
         "p.ruleTemplateType as ruleTemplateType,"+
         "p.payrollType as payrollType ,"+
         "p.payrollSystem as payrollSystem ,"+
         "p.calculationUnit as calculationUnit ,"+
         "p.compensationTable as compensationTable ,"+
         "p.calculateValueAgainst as calculateValueAgainst ,"+
         "p.approvalWorkFlow as approvalWorkFlow ,"+
         "collect({dayType:ID(dayType),countryHolidayCalenders:holidaysIds}) as calculateOnDayTypes ,"+
         "p.phaseInfo as phaseInfo ,"+
         "p.budgetType as budgetType ,"+
         "collect(ID(accessGroup)) as calculateValueIfPlanned ,"+
         "collect(ID(employmentType)) as employmentTypes ,"+
         "p.activityType as activityType ,"+
         "p.planningCategory as planningCategory ,"+
         "p.staffFunction as staffFunction ,"+
         "p.plannedTimeWithFactor as plannedTimeWithFactor ,"+
         "collect(ID(timeType)) as timeTypes,"+
         "ID(p) as id")
    List<CTARuleTemplateDTO>findByRuleTemplateCategoryIdInAndDeletedFalseAndDisabledFalse(List<Long> categoryList);

}
