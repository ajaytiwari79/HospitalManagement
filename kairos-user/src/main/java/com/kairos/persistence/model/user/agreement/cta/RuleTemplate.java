package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;

public abstract class RuleTemplate extends UserBaseEntity {
    protected String name;
    protected String description;
    protected boolean disabled;
    protected RuleTemplateCategory ruleTemplateCategory;


    public RuleTemplate() {
        //default constractor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

}
