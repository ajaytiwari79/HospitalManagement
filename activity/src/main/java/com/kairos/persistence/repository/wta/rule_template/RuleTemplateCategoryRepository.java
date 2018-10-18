package com.kairos.persistence.repository.wta.rule_template;


import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by pradeep on 2/8/17.
 */
@Repository
public interface RuleTemplateCategoryRepository extends MongoBaseRepository<RuleTemplateCategory, BigInteger> ,CustomRuleTemplateCategoryRepository {
    @Query("{countryId:?0,ruleTemplateCategoryType:?1,deleted:false}")
    List<RuleTemplateCategory> getRuleTemplateCategoryByCountry(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType);



}
