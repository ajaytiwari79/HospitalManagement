package com.kairos.activity.wta.rule_template_category;

import com.kairos.activity.wta.basic_details.WTABaseRuleTemplateDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 30/4/18
 */

public class RuleTemplateAndCategoryResponseDTO {
    private RuleTemplateCategoryRequestDTO category;
    private List<WTABaseRuleTemplateDTO> templateList;


    public RuleTemplateAndCategoryResponseDTO() {
    }

    public RuleTemplateAndCategoryResponseDTO(RuleTemplateCategoryRequestDTO category, List<WTABaseRuleTemplateDTO> templateList) {
        this.category = category;
        this.templateList = templateList;
    }

    public RuleTemplateCategoryRequestDTO getCategory() {
        return category;
    }

    public void setCategory(RuleTemplateCategoryRequestDTO category) {
        this.category = category;
    }

    public List<WTABaseRuleTemplateDTO> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<WTABaseRuleTemplateDTO> templateList) {
        this.templateList = templateList;
    }
}
