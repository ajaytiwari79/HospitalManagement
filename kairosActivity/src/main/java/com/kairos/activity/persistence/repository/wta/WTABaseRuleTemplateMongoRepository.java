package com.kairos.activity.persistence.repository.wta;


import com.kairos.activity.persistence.model.wta.templates.RuleTemplateCategoryDTO;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.RuleTemplateResponseDTO;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by pawanmandhan on 5/8/17.
 */
public interface WTABaseRuleTemplateMongoRepository extends MongoBaseRepository<WTABaseRuleTemplate, BigInteger> {


    @Query("{}")
    RuleTemplateCategoryDTO getRuleTemplateAndCategoryById(BigInteger templateId);

    @Query("{}")
    List<WTABaseRuleTemplate> getWtaBaseRuleTemplateByIds(List<BigInteger> templateIds);

    @Query("{}")
    void deleteOldCategories(List<BigInteger> ruleTemplateIds);

    @Query("{}")
    void deleteCategoryFromTemplate(BigInteger ruleTemplateId, BigInteger previousRuleTemplateCategory, String newRuleTemplateCategory);

    @Query("{}")
    List<BigInteger> findAllWTABelongsByTemplateCategoryId(BigInteger ruleTemplateCategoryId);

    @Query("{}")
    void deleteRelationOfRuleTemplateCategoryAndWTA(BigInteger ruleTemplateId, List<BigInteger> WTAIds);

    @Query("{}")
    void setAllWTAWithCategoryNone(BigInteger ruleTemplateId, List<BigInteger> WTAIds);


    @Query("{}")
    List<RuleTemplateResponseDTO> getWTABaseRuleTemplateByUnitId(Long unitId);

    @Query("{}")
    List<RuleTemplateResponseDTO> getWTABaseRuleTemplateByCountryId(Long countryId);

    @Query("{}")
    WTABaseRuleTemplate existsByName(Long countryId, String name);

    @Query("{}")
    String getLastInsertedTemplateType(Long countryId, String templateType);

}
