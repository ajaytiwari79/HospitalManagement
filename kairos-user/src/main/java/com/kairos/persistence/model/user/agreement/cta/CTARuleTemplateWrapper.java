package com.kairos.persistence.model.user.agreement.cta;

import java.util.List;

/**
 * Created by vipul on 12/12/17.
 */
public class CTARuleTemplateWrapper {
    private String ruleTemplateCategory;
    private List<Long> ctaList;

    public CTARuleTemplateWrapper() {
    }

    public CTARuleTemplateWrapper(String ruleTemplateCategory, List<Long> ctaList) {
        this.ruleTemplateCategory = ruleTemplateCategory;
        this.ctaList = ctaList;
    }

    public String getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(String ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }

    public List<Long> getCtaList() {
        return ctaList;
    }

    public void setCtaList(List<Long> ctaList) {
        this.ctaList = ctaList;
    }
}
