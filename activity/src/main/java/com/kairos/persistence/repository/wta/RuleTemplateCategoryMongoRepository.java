package com.kairos.persistence.repository.wta;


import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.enums.RuleTemplateCategoryType;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryTagDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 2/8/17.
 */
@Repository

public interface RuleTemplateCategoryMongoRepository extends MongoBaseRepository<RuleTemplateCategory, BigInteger> {
    @Query("{}")
    List<RuleTemplateCategory> getRuleTemplateCategoryByCountry(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType);
    /*@Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]-(l:RuleTemplateCategory{deleted:false}) Where id(c)={0}\n" +
            "return l")*/
    /*@Query("match(c:Country{isEnabled:true})-[r:HAS_RULE_TEMPLATE_CATEGORY]-(l:RuleTemplateCategory{deleted:false}) with c,l\n" +
            "OPTIONAL MATCH (l)-[:HAS_TAG]-(t:Tag)<-[:COUNTRY_HAS_TAG]-(c) WHERE t.deleted=false AND t.masterDataType='RULE_TEMPLATE_CATEGORY' AND\n" +
            "id(c)=53 with l,t\n" +
            "RETURN id(l) as id, l.name as name, l.description as description, CASE when t IS NULL THEN [] ELSE collect({id:id(t),name:t.name,countryTag:t.countryTag})  END as tags")*/

    @Query("{countryId:?0,ruleTemplateCategoryType:?2,deleted:false}")
    RuleTemplateCategory checkDuplicateRuleTemplateCategory(Long countryId,RuleTemplateCategoryType type,String name);

    @Query("{countryId:?0,deleted:false}")
    List<RuleTemplateCategoryTagDTO> getAllRulesOfCountry(long countryId);

    @Query("{}")
    List<RuleTemplateCategoryTagDTO> getRuleTemplateCategoryByUnitId(Long unitId);


    @Query("{countryId:?0,name:?1,ruleTemplateCategoryType:?2,deleted:false}")
    RuleTemplateCategory findByName(Long countryId, String name, RuleTemplateCategoryType ruleTemplateCategoryType);

    @Query("{}")
    RuleTemplateCategory findByName(String name, RuleTemplateCategoryType ruleTemplateCategoryType);

    @Query("{}")
    void setRuleTemplateCategoryWithRuleTemplate(Long templateCategoryId, Long ruleTemplateId);

    @Query("{}")
    List<Long> findAllExistingRuleTemplateAddedToThiscategory(String ruleTemplateCategoryName, long countryId);

    @Query("{}")
    void updateCategoryOfRuleTemplate(List<Long> wtaBaseRuleTemplateId, String ruleTemplateCategoryName);

    // CTA PART
    @Query("{}")
    void updateCategoryOfCTARuleTemplate(List<Long> ctaRuleTemplateList, String ruleTemplateCategoryName);

    @Query("{}")
    List<Long> findAllExistingCTARuleTemplateByCategory(String ruleTemplateCategoryName, long countryId);


    RuleTemplateCategory findByNameAndRuleTemplateCategoryType(String name, RuleTemplateCategoryType ruleTemplateCategoryType);

    @Query("{}")
    void deleteRelationOfRuleTemplateCategoryAndCTA(BigInteger ruleTemplateId, List<Long> ctaRuleTemplateIds);

    @Query("{}")
    void setAllCTAWithCategoryNone(BigInteger ruleTemplateId, List<Long> ctaRuleTemplateIds);

    @Query("{}")
    void detachRuleTemplateCategoryFromCTARuleTemplate(long ctaRuleTemplateId, long ruleTemplateCategoryId);

    @Query("{}")
    void detachPreviousRuleTemplates(BigInteger wtaId);

    @Query("{}")
    boolean findByNameExcludingCurrent(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType, String name, BigInteger templateCategoryId);
}
