package com.kairos.activity.cta;

import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 30/7/18
 */

public class CTARuleTemplateCategoryWrapper {

    private List<RuleTemplateCategoryDTO> ruleTemplateCategories=new ArrayList<>();
    private List<CTARuleTemplateDTO> ruleTemplates=new ArrayList<>();


    public CTARuleTemplateCategoryWrapper(List<RuleTemplateCategoryDTO> ruleTemplateCategories, List<CTARuleTemplateDTO> ruleTemplates) {
        this.ruleTemplateCategories = ruleTemplateCategories;
        this.ruleTemplates = ruleTemplates;
    }

    public CTARuleTemplateCategoryWrapper() {
    }

    public List<RuleTemplateCategoryDTO> getRuleTemplateCategories() {
        return ruleTemplateCategories;
    }

    public void setRuleTemplateCategories(List<RuleTemplateCategoryDTO> ruleTemplateCategories) {
        this.ruleTemplateCategories = ruleTemplateCategories;
    }

    public List<CTARuleTemplateDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<CTARuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }
}
