package com.kairos.persistence.repository.wta.rule_template;

import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;

import java.util.List;

public interface CustomRuleTemplateMongoRepository {

        List<RuleTemplateCategoryTagDTO> findAllByCountryId(Long countryId);
}
