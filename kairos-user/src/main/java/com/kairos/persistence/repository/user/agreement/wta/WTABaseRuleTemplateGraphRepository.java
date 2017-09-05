package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.WTARuleTemplateQueryResponse;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATES;

/**
 * Created by pawanmandhan on 5/8/17.
 */
public interface WTABaseRuleTemplateGraphRepository extends GraphRepository<WTABaseRuleTemplate> {



    @Query("MATCH (t:WTABaseRuleTemplate) where id(t)={0} with t " +
            "Match (t)<-[:"+HAS_RULE_TEMPLATES+"]-(r:RuleTemplateCategory) with t,r " +
            "Return id(t) as id ," +
            "t.timeLimit as timeLimit,"+
            "t.balanceType as balanceType,"+
            "t.checkAgainstTimeRules as checkAgainstTimeRules,"+
            "t.minimumRest as minimumRest,"+
            "t.daysWorked as daysWorked,"+
            "t.name as name ," +
            "t.templateType as templateType," +
            "r as ruleTemplateCategory," +
            "t.isActive as isActive,"+
            "t.description as description," +
            "t.daysLimit as daysLimit,"+
            "t.creationDate as creationDate,"+
            "t.lastModificationDate as lastModificationDate,"+
            "t.nightsWorked as nightsWorked,"+
            "t.intervalLength as intervalLength,"+
            "t.intervalUnit as intervalUnit,"+
            "t.validationStartDateMillis as validationStartDateMillis,"+
            "t.balanceAdjustment as balanceAdjustment,"+
            "t.useShiftTimes as useShiftTimes,"+
            "t.maximumAvgTime as maximumAvgTime,"+
            "t.maximumVetoPercentage as maximumVetoPercentage,"+
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod,"+
            "t.numberOfWeeks as numberOfWeeks,"+
            "t.fromDayOfWeek as fromDayOfWeek,"+
            "t.fromTime as fromTime,"+
            "t.proportional as proportional,"+
            "t.toTime as toTime,"+
            "t.toDayOfWeek as toDayOfWeek,"+
            "t.continuousDayRestHours as continuousDayRestHours,"+
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts,"+
            "t.continuousWeekRest as continuousWeekRest ,"+
            "t.averageRest as averageRest,"+
            "t.shiftAffiliation as shiftAffiliation,"+
            "t.shiftsLimit as shiftsLimit,"+
            "t.activityCode as activityCode,"+
            "t.onlyCompositeShifts as onlyCompositeShifts")
    WTARuleTemplateQueryResponse getRuleTemplateAndCategoryById(long templateId);

    @Query("Match (n:WTABaseRuleTemplate) where id(n) in {0} return n")
    List<WTABaseRuleTemplate> getWtaBaseRuleTemplateByIds(List<Long> templateIds);

    @Query("MATCH (n:WTABaseRuleTemplate) where id(n) in {0}\n" +
            "Match (n)<-[r:"+HAS_RULE_TEMPLATES+"]-(category:RuleTemplateCategory) delete r")
    void deleteOldCategories(List<Long> ruleTemplateIds);

    @Query("MATCH (n:WTABaseRuleTemplate) where id(n)={0}\n" +
            "Match (n)<-[r:"+HAS_RULE_TEMPLATES+"]-(category:RuleTemplateCategory) delete r")
    void deleteCategoryFromTemplate(Long ruleTemplateId);

}
