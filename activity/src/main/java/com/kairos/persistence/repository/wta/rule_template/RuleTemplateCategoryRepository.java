package com.kairos.persistence.repository.wta.rule_template;


import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 2/8/17.
 */
@Repository
public interface RuleTemplateCategoryRepository extends MongoBaseRepository<RuleTemplateCategory, BigInteger> ,CustomRuleTemplateCategoryRepository {
    @Query("{}")
    List<RuleTemplateCategory> getRuleTemplateCategoryByCountry(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType);

    @Query("{countryId:?0,ruleTemplateCategoryType:?2,deleted:false}")
    RuleTemplateCategory checkDuplicateRuleTemplateCategory(Long countryId,RuleTemplateCategoryType type,String name);

    @Query("{}")
    List<RuleTemplateCategoryTagDTO> getRuleTemplateCategoryByUnitId(Long unitId);


    @Query("{countryId:?0,name:?1,ruleTemplateCategoryType:?2,deleted:false}")
    RuleTemplateCategory findByName(Long countryId, String name, RuleTemplateCategoryType ruleTemplateCategoryType);

}
