package com.kairos.dto.activity.wta.rule_template_category;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 30/4/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleTemplateAndCategoryResponseDTO {
    private RuleTemplateCategoryRequestDTO category;
    private List<WTABaseRuleTemplateDTO> templateList;
}
