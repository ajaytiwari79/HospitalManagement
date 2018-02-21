package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.RuleTemplateResponseDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */
public interface WTABaseRuleTemplateGraphRepository extends Neo4jBaseRepository<WTABaseRuleTemplate, Long> {


    @Query("MATCH (t:WTABaseRuleTemplate) where id(t)={0} with t " +
            "Match (t)<-[:" + HAS_RULE_TEMPLATES + "]-(r:RuleTemplateCategory) with t,r " +
            "Return id(t) as id ," +
            "t.timeLimit as timeLimit," +
            "t.balanceType as balanceType," +
            "t.checkAgainstTimeRules as checkAgainstTimeRules," +
            "t.minimumRest as minimumRest," +
            "t.daysWorked as daysWorked," +
            "t.name as name ," +
            "t.templateType as templateType," +
            "r as ruleTemplateCategory," +
            "t.disabled as disabled," +
            "t.description as description," +
            "t.daysLimit as daysLimit," +
            "t.creationDate as creationDate," +
            "t.lastModificationDate as lastModificationDate," +
            "t.nightsWorked as nightsWorked," +
            "t.intervalLength as intervalLength," +
            "t.intervalUnit as intervalUnit," +
            "t.validationStartDateMillis as validationStartDateMillis," +
            "t.balanceAdjustment as balanceAdjustment," +
            "t.useShiftTimes as useShiftTimes," +
            "t.maximumAvgTime as maximumAvgTime," +
            "t.maximumVetoPercentage as maximumVetoPercentage," +
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod," +
            "t.numberOfWeeks as numberOfWeeks," +
            "t.fromDayOfWeek as fromDayOfWeek," +
            "t.fromTime as fromTime," +
            "t.proportional as proportional," +
            "t.toTime as toTime," +
            "t.toDayOfWeek as toDayOfWeek," +
            "t.continuousDayRestHours as continuousDayRestHours," +
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts," +
            "t.continuousWeekRest as continuousWeekRest ," +
            "t.averageRest as averageRest," +
            "t.shiftAffiliation as shiftAffiliation," +
            "t.shiftsLimit as shiftsLimit," +
            "t.activityCode as activityCode," +
            "t.onlyCompositeShifts as onlyCompositeShifts")
    RuleTemplateCategoryDTO getRuleTemplateAndCategoryById(long templateId);

    @Query("Match (n:WTABaseRuleTemplate) where id(n) in {0} return n")
    List<RuleTemplate> getWtaBaseRuleTemplateByIds(List<Long> templateIds);

    @Query("MATCH (n:WTABaseRuleTemplate) where id(n) in {0}\n" +
            "Match (n)<-[r:" + HAS_RULE_TEMPLATES + "]-(category:RuleTemplateCategory) detach  delete r")
    void deleteOldCategories(List<Long> ruleTemplateIds);

    @Query("MATCH (n:WTABaseRuleTemplate) where id(n)={0}\n" +
            "Match (n)<-[r:" + HAS_RULE_TEMPLATES + "]-(category:RuleTemplateCategory) detach delete r with n \n" +
            "match  (catg:RuleTemplateCategory{deleted:false}) where  catg.ruleTemplateCategoryType='WTA' AND catg.name=~{2} \n" +
            "create (n)<-[:" + HAS_RULE_TEMPLATES + "]-(catg) ")
    void deleteCategoryFromTemplate(Long ruleTemplateId, Long previousRuleTemplateCategory, String newRuleTemplateCategory);

    @Query("match (rt:RuleTemplateCategory) where id(rt)={0}\n" +
            "match (r:WTABaseRuleTemplate)<-[:" + HAS_RULE_TEMPLATES + "]-(rt)\n" +
            "return id(r) as IDs")
    List<Long> findAllWTABelongsByTemplateCategoryId(long ruleTemplateCategoryId);

    @Query("match (rt:RuleTemplateCategory) where id(rt)={0}\n" +
            "optional match (r:WTABaseRuleTemplate) where id(r) IN {1}\n" +
            "optional match(rt)-[rel:" + HAS_RULE_TEMPLATES + "]->(r)\n" +
            "delete rel set rt.deleted=true")
    void deleteRelationOfRuleTemplateCategoryAndWTA(long ruleTemplateId, List<Long> WTAIds);

    @Query("match (rt:RuleTemplateCategory) where id(rt)={0}\n" +
            "match (r:WTABaseRuleTemplate) where id(r) IN {1}\n" +
            "create (rt)-[rq:" + HAS_RULE_TEMPLATES + "]->(r)")
    void setAllWTAWithCategoryNone(long ruleTemplateId, List<Long> WTAIds);


    @Query("MATCH (o:Organization)-[:" + BELONGS_TO + "]-(c:Country{isEnabled:true})-[:HAS_RULE_TEMPLATE]-(t:WTABaseRuleTemplate) where id(o)={0} " +
            "Match (t)<-[:" + HAS_RULE_TEMPLATES + "]-(r:RuleTemplateCategory)  " +
            "Optional Match (t)-[: " + HAS_TEMPLATE_MATRIX + "]->(tempValue:PhaseTemplateValue)\n" +
            "with tempValue order by tempValue.phaseId ,t,c,r "+
            "with t,c,r, CASE WHEN tempValue IS NOT NULL THEN collect (tempValue)  else [] END as phaseTemplateValues \n" +
            "Return id(t) as id ," +
            "t.timeLimit as timeLimit," +
            "t.balanceType as balanceType," +
            "t.checkAgainstTimeRules as checkAgainstTimeRules," +
            "t.minimumRest as minimumRest," +
            "t.daysWorked as daysWorked," +
            "t.name as name ," +
            "t.templateType as templateType," +
            "r as ruleTemplateCategory," +
            "t.disabled as disabled," +
            "t.description as description," +
            "t.daysLimit as daysLimit," +
            "t.creationDate as creationDate," +
            "t.lastModificationDate as lastModificationDate," +
            "t.nightsWorked as nightsWorked," +
            "t.intervalLength as intervalLength," +
            "t.intervalUnit as intervalUnit," +
            "t.validationStartDateMillis as validationStartDateMillis," +
            "t.balanceAdjustment as balanceAdjustment," +
            "t.useShiftTimes as useShiftTimes," +
            "t.maximumAvgTime as maximumAvgTime," +
            "t.maximumVetoPercentage as maximumVetoPercentage," +
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod," +
            "t.numberOfWeeks as numberOfWeeks," +
            "t.fromDayOfWeek as fromDayOfWeek," +
            "t.fromTime as fromTime," +
            "t.proportional as proportional," +
            "t.toTime as toTime," +
            "t.toDayOfWeek as toDayOfWeek," +
            "t.continuousDayRestHours as continuousDayRestHours," +
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts," +
            "t.continuousWeekRest as continuousWeekRest ," +
            "t.averageRest as averageRest," +
            "t.shiftAffiliation as shiftAffiliation," +
            "t.shiftsLimit as shiftsLimit," +
            "t.activityCode as activityCode," +
            "t.onlyCompositeShifts as onlyCompositeShifts," +
            "t.recommendedValue as recommendedValue," +
            "t.lastUpdatedBy as lastUpdatedBy," +
            "t.frequency as frequency," +
            "t.yellowZone as yellowZone," +
            "t.forbid as forbid," +
            "t.allowExtraActivity as allowExtraActivity," +
            "phaseTemplateValues as phaseTemplateValues")
    List<RuleTemplateResponseDTO> getWTABaseRuleTemplateByUnitId(Long unitId);

    @Query("MATCH (c:Country{isEnabled:true})-[:" + HAS_RULE_TEMPLATE + "]-(t:WTABaseRuleTemplate) where id(c)={0} " +
            "Match (t)<-[:" + HAS_RULE_TEMPLATES + "]-(r:RuleTemplateCategory{ruleTemplateCategoryType:'WTA'})  " +
            "Optional Match (t)-[:" + HAS_TEMPLATE_MATRIX + "]->(tempValue:PhaseTemplateValue)\n" +
            "with tempValue order by tempValue.phaseId ,t,c,r "+
            "with t,c,r, CASE WHEN tempValue IS NOT NULL THEN collect (tempValue)  else [] END as phaseTemplateValues \n" +
            "Return id(t) as id ," +
            "t.timeLimit as timeLimit," +
            "t.balanceType as balanceType," +
            "t.checkAgainstTimeRules as checkAgainstTimeRules," +
            "t.minimumRest as minimumRest," +
            "t.daysWorked as daysWorked," +
            "t.name as name ," +
            "t.templateType as templateType," +
            "r as ruleTemplateCategory," +
            "t.disabled as disabled," +
            "t.description as description," +
            "t.daysLimit as daysLimit," +
            "t.creationDate as creationDate," +
            "t.lastModificationDate as lastModificationDate," +
            "t.nightsWorked as nightsWorked," +
            "t.intervalLength as intervalLength," +
            "t.intervalUnit as intervalUnit," +
            "t.validationStartDateMillis as validationStartDateMillis," +
            "t.balanceAdjustment as balanceAdjustment," +
            "t.useShiftTimes as useShiftTimes," +
            "t.maximumAvgTime as maximumAvgTime," +
            "t.maximumVetoPercentage as maximumVetoPercentage," +
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod," +
            "t.numberOfWeeks as numberOfWeeks," +
            "t.fromDayOfWeek as fromDayOfWeek," +
            "t.fromTime as fromTime," +
            "t.proportional as proportional," +
            "t.toTime as toTime," +
            "t.toDayOfWeek as toDayOfWeek," +
            "t.continuousDayRestHours as continuousDayRestHours," +
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts," +
            "t.continuousWeekRest as continuousWeekRest ," +
            "t.averageRest as averageRest," +
            "t.shiftAffiliation as shiftAffiliation," +
            "t.shiftsLimit as shiftsLimit," +
            "t.activityCode as activityCode," +
            "t.onlyCompositeShifts as onlyCompositeShifts," +
            "t.recommendedValue as recommendedValue," +
            "t.lastUpdatedBy as lastUpdatedBy," +
            "t.frequency as frequency," +
            "t.yellowZone as yellowZone," +
            "t.forbid as forbid," +
            "t.allowExtraActivity as allowExtraActivity," +
            "phaseTemplateValues as phaseTemplateValues"
    )
    List<RuleTemplateResponseDTO> getWTABaseRuleTemplateByCountryId(Long countryId);

}
