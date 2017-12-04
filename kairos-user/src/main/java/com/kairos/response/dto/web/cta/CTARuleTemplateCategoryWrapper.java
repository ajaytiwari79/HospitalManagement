package com.kairos.response.dto.web.cta;

import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateCategoryWrapper {
    private List<RuleTemplateCategory> ruleTemplateCategories=new ArrayList<>();
    private List<RuleTemplate> ruleTemplates=new ArrayList<>();

    public List<RuleTemplateCategory> getRuleTemplateCategories() {
        return ruleTemplateCategories;
    }

    public void setRuleTemplateCategories(List<RuleTemplateCategory> ruleTemplateCategories) {
        this.ruleTemplateCategories = ruleTemplateCategories;
    }

    public List<RuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("ruleTemplateCategories", ruleTemplateCategories)
                .append("ruleTemplates", ruleTemplates)
                .toString();
    }
}
