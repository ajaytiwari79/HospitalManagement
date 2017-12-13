package com.kairos.persistence.model.user.agreement.cta;

import java.util.List;

/**
 * Created by vipul on 12/12/17.
 */
public class CTARuleTemplateWrapper {
    private String ruleTemplateCategory;
    private List<Long> ctaRuleTemplateList;

    public CTARuleTemplateWrapper() {
    }

    public CTARuleTemplateWrapper(String ruleTemplateCategory, List<Long> ctaRuleTemplateList) {
        this.ruleTemplateCategory = ruleTemplateCategory;
        this.ctaRuleTemplateList = ctaRuleTemplateList;
    }

    public String getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(String ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

    public List<Long> getCtaRuleTemplateList() {
        return ctaRuleTemplateList;
    }

    public void setCtaRuleTemplateList(List<Long> ctaRuleTemplateList) {
        this.ctaRuleTemplateList = ctaRuleTemplateList;
    }
}
