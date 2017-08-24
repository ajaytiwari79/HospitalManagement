package com.kairos.persistence.repository.user.cta_wta.template;

import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplateDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATES;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATE_CATEGORY;

/**
 * Created by pawanmandhan on 5/8/17.
 */
public interface WTABaseRuleTemplateGraphRepository extends GraphRepository<WTABaseRuleTemplate>{


    @Query("Match (t:WTABaseRuleTemplate),(r:RuleTemplateCategory) where id(t)={0} AND id(r)={1}  with t,r " +
            "Merge (t)<-[:"+HAS_RULE_TEMPLATES+"]-(r) "+
            "Return id(t) as id ," + "t.name as name ," +"t.templateType as templateType," +"r as ruleTemplateCategory," + "t.isActive as isActive,"+"t.description as description," + "t.daysWorked as daysWorked,"+"t.numberOfDays as number,"+
            "t.creationDate as creationDate,"+ "t.lastModificationDate as lastModificationDate,"+ "t.time as time,"+ "t.days as days,"+"t.minimumRest as minimumRest,"+"t.checkAgainstTimeRules as checkAgainstTimeRules,"+
            "t.nightsWorked as nightsWorked,"+ "t.minimumDaysOff as minimumDaysOff,"+"t.balanceAdjustment as balanceAdjustment,"+  "t.calculatedShift as calculatedShift,"+ "t.maximumAvgTime as maximumAvgTime,"+ "t.maximumVeto as maximumVeto,"+
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod,"+ "t.numberOfWeeks as numberOfWeeks,"+ "t.fromDayOfWeek as fromDayOfWeek,"+ "t.fromTime as fromTime,"+ "t.proportional as proportional,"+
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts,"+"t.continuousWeekRest as continuousWeekRest ,"+ "t.continuousDayRestHours as continuousDayRestHours,"+"t.averageRest as averageRest,"+
            "t.shiftAffiliation as shiftAffiliation,"+"t.balanceType as balanceType,"+"t.onlyCompositeShifts as onlyCompositeShifts,"+"t.interval as interval,"+"t.intervalUnit as intervalUnit,"+"t.validationStartDate as validationStartDate,"+
            "t.activityCode as activityCode")
    WTABaseRuleTemplateDTO addCategoryInTemplate(long templateId, long categoryId);

    @Query("Match (template:WTABaseRuleTemplate),(category:RuleTemplateCategory) where id(template) IN {0} AND id(category)={1}  with template,category " +
            "Merge (template)-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]->(category) " )
    void addCategoryInAllTemplate(List<Long> ruleTemplateIds, Long id);

    @Query("MATCH (t:WTABaseRuleTemplate) where id(t)={0} with t " +
            "Match (t)<-[:"+HAS_RULE_TEMPLATES+"]-(r:RuleTemplateCategory) with t,r " +
            "Return id(t) as id ," + "t.name as name ," +"t.templateType as templateType," +"r as ruleTemplateCategory," + "t.isActive as isActive,"+"t.description as description," + "t.daysWorked as daysWorked,"+"t.numberOfDays as number,"+
            "t.creationDate as creationDate,"+ "t.lastModificationDate as lastModificationDate,"+ "t.time as time,"+ "t.days as days,"+"t.minimumRest as minimumRest,"+"t.checkAgainstTimeRules as checkAgainstTimeRules,"+
            "t.nightsWorked as nightsWorked,"+ "t.minimumDaysOff as minimumDaysOff,"+"t.balanceAdjustment as balanceAdjustment,"+  "t.calculatedShift as calculatedShift,"+ "t.maximumAvgTime as maximumAvgTime,"+ "t.maximumVeto as maximumVeto,"+
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod,"+ "t.numberOfWeeks as numberOfWeeks,"+ "t.fromDayOfWeek as fromDayOfWeek,"+ "t.fromTime as fromTime,"+ "t.proportional as proportional,"+
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts,"+"t.continuousWeekRest as continuousWeekRest ,"+ "t.continuousDayRestHours as continuousDayRestHours,"+"t.averageRest as averageRest,"+
            "t.shiftAffiliation as shiftAffiliation,"+"t.balanceType as balanceType,"+"t.onlyCompositeShifts as onlyCompositeShifts,"+"t.interval as interval,"+"t.intervalUnit as intervalUnit,"+"t.validationStartDate as validationStartDate,"+
            "t.activityCode as activityCode")
    WTABaseRuleTemplateDTO getRuleTemplateAndCategoryById(long templateId);

    @Query("Match (n:WTABaseRuleTemplate) where id(n) in {0} return n")
    List<WTABaseRuleTemplate> getWtaBaseRuleTemplateByIds(List<Long> templateIds);

    @Query("MATCH (n:WTABaseRuleTemplate) where id(n) in {0}\n" +
            "Match (n)<-[r:"+HAS_RULE_TEMPLATES+"]-(category:RuleTemplateCategory) delete r")
    void deleteOldCategories(List<Long> ruleTemplateIds);

    @Query("MATCH (n:WTABaseRuleTemplate) where id(n)={0}\n" +
            "Match (n)<-[r:"+HAS_RULE_TEMPLATES+"]-(category:RuleTemplateCategory) delete r")
    void deleteCategoryFromTemplate(Long ruleTemplateId);

}
