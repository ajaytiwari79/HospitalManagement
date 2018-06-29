package com.kairos.activity.wta.rule_template_category;

import com.kairos.activity.wta.basic_details.WTABaseRuleTemplateDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 30/4/18
 */

public class RuleTemplateAndCategoryDTO {
    private RuleTemplateCategoryDTO category;
    private List<WTABaseRuleTemplateDTO> templateList;


    public RuleTemplateAndCategoryDTO() {
    }

    public RuleTemplateAndCategoryDTO(RuleTemplateCategoryDTO category, List<WTABaseRuleTemplateDTO> templateList) {
        this.category = category;
        this.templateList = templateList;
    }

    public RuleTemplateCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(RuleTemplateCategoryDTO category) {
        this.category = category;
    }

    public List<WTABaseRuleTemplateDTO> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<WTABaseRuleTemplateDTO> templateList) {
        this.templateList = templateList;
    }
}
