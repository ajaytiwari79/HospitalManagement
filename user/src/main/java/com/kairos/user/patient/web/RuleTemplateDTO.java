package com.kairos.user.patient.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.user.agreement.wta.templates.RuleTemplateCategory;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prabjot on 18/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude()
public class RuleTemplateDTO {

    private List<Long> ruleTemplateIds;
    @NotNull(message = "error.WtaRuleTemplateDTO.categoryName.notnull")
    private String categoryName;
    private String ruleTemplateCategoryType;
    private List<Long> tags;
    private  RuleTemplateCategory ruleTemplateCategory;

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

    public String getRuleTemplateCategoryType() {
        return ruleTemplateCategoryType;
    }

    public void setRuleTemplateCategoryType(String ruleTemplateCategoryType) {
        this.ruleTemplateCategoryType = ruleTemplateCategoryType;
    }
    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public RuleTemplateCategory getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
    }
}
