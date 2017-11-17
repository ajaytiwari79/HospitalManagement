package com.kairos.response.dto.web;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prabjot on 18/8/17.
 */
public class WtaRuleTemplateDTO {

    private List<Long> ruleTemplateIds;
    @NotEmpty(message = "error.WtaRuleTemplateDTO.categoryName.notEmpty")     @NotNull(message = "error.WtaRuleTemplateDTO.categoryName.notnull")
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
