package com.kairos.persistence.model.user.agreement.wta.templates;


import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RULE_TEMPLATE_CATEGORY;

/**
 * Created by pawanmandhan on 10/8/17.
 */
@RelationshipEntity(type=HAS_RULE_TEMPLATE_CATEGORY)
public class TemplateCategoryRelation extends UserBaseEntity {


    @StartNode
    private WTABaseRuleTemplate baseRuleTemplate;
    @EndNode
    private RuleTemplateCategory ruleTemplateCategory;


    public TemplateCategoryRelation(){

    }

    public TemplateCategoryRelation(WTABaseRuleTemplate baseRuleTemplate, RuleTemplateCategory ruleTemplateCategory) {
        this.baseRuleTemplate = baseRuleTemplate;
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

    public WTABaseRuleTemplate getBaseRuleTemplate() {
        return baseRuleTemplate;
    }

    public void setBaseRuleTemplate(WTABaseRuleTemplate baseRuleTemplate) {
        this.baseRuleTemplate = baseRuleTemplate;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }
}
