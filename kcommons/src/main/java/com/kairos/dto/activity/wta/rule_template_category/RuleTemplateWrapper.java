package com.kairos.dto.activity.wta.rule_template_category;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by vipul on 3/1/18.
 */
@Getter
@Setter
public class RuleTemplateWrapper {
    private List<RuleTemplateCategoryTagDTO> categoryList;
    private List<WTABaseRuleTemplateDTO> templateList;
}
