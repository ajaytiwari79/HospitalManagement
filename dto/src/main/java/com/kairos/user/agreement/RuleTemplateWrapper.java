package com.kairos.user.agreement;



import com.kairos.activity.wta.RuleTemplateCategoryTagDTO;
import com.kairos.activity.wta.WTABaseRuleTemplateDTO;

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
