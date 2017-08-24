package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by prabjot on 18/8/17.
 */
public class WtaRuleTemplateDTO {

    private List<Long> ruleTemplateIds;
    private String categoryName;

    public List<Long> getRuleTemplateIds() {
        return ruleTemplateIds;
    }

    public void setRuleTemplateIds(List<Long> ruleTemplateIds) {
        this.ruleTemplateIds = ruleTemplateIds;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
