package com.kairos.response.dto.web.aggrements;

import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategoryTagDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.RuleTemplateResponseDTO;

import java.util.List;

/**
 * Created by vipul on 3/1/18.
 */
public class RuleTemplateWrapper {
    private List<RuleTemplateCategoryTagDTO> categoryList;
    private List<RuleTemplateResponseDTO> templateList;

    public List<RuleTemplateCategoryTagDTO> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<RuleTemplateCategoryTagDTO> categoryList) {
        this.categoryList = categoryList;
    }

    public List<RuleTemplateResponseDTO> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<RuleTemplateResponseDTO> templateList) {
        this.templateList = templateList;
    }
}
