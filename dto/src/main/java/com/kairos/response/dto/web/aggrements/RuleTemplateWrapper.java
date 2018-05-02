package com.kairos.response.dto.web.aggrements;



import com.kairos.response.dto.web.wta.RuleTemplateCategoryTagDTO;
import com.kairos.response.dto.web.wta.WTABaseRuleTemplateDTO;

import java.util.List;

/**
 * Created by vipul on 3/1/18.
 */
public class RuleTemplateWrapper {
    private List<RuleTemplateCategoryTagDTO> categoryList;
    private List<WTABaseRuleTemplateDTO> templateList;

    public List<RuleTemplateCategoryTagDTO> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<RuleTemplateCategoryTagDTO> categoryList) {
        this.categoryList = categoryList;
    }

    public List<WTABaseRuleTemplateDTO> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<WTABaseRuleTemplateDTO> templateList) {
        this.templateList = templateList;
    }
}
