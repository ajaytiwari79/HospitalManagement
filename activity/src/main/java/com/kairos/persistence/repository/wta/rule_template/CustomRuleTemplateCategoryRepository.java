package com.kairos.persistence.repository.wta.rule_template;

import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;

import java.util.List;

public interface CustomRuleTemplateCategoryRepository {

        RuleTemplateCategory findByName(Long countryId, String name, RuleTemplateCategoryType ruleTemplateCategoryType);
        List<RuleTemplateCategoryTagDTO> findAllUsingCountryId(Long countryId);
}
