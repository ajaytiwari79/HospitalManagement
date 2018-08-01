package com.kairos.persistence.repository.wta.rule_template;

import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;

import java.util.List;

public interface CustomRuleTemplateCategoryRepository {

        List<RuleTemplateCategoryTagDTO> findAllUsingCountryId(Long countryId);
}
