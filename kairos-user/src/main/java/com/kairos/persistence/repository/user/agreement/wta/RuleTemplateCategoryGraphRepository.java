package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategoryTagDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by vipul on 2/8/17.
 */
@Repository

public interface RuleTemplateCategoryGraphRepository extends Neo4jBaseRepository<RuleTemplateCategory,Long> {
    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]-(l:RuleTemplateCategory{deleted:false}) Where id(c)={0} AND l.ruleTemplateCategoryType={1}\n" +
            "return l")
    List<RuleTemplateCategory> getRuleTemplateCategoryByCountry(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType);
    /*@Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]-(l:RuleTemplateCategory{deleted:false}) Where id(c)={0}\n" +
            "return l")*/
    /*@Query("match(c:Country{isEnabled:true})-[r:HAS_RULE_TEMPLATE_CATEGORY]-(l:RuleTemplateCategory{deleted:false}) with c,l\n" +
            "OPTIONAL MATCH (l)-[:HAS_TAG]-(t:Tag)<-[:COUNTRY_HAS_TAG]-(c) WHERE t.deleted=false AND t.masterDataType='RULE_TEMPLATE_CATEGORY' AND\n" +
            "id(c)=53 with l,t\n" +
            "RETURN id(l) as id, l.name as name, l.description as description, CASE when t IS NULL THEN [] ELSE collect({id:id(t),name:t.name,countryTag:t.countryTag})  END as tags")*/
     @Query("match(c:Country{isEnabled:true})-[r:HAS_RULE_TEMPLATE_CATEGORY]-(l:RuleTemplateCategory{deleted:false}) with c,l\n" +
             "OPTIONAL MATCH (l)-[:HAS_TAG]-(t:Tag)<-[:COUNTRY_HAS_TAG]-(c) WHERE t.deleted=false AND t.masterDataType='RULE_TEMPLATE_CATEGORY' AND\n" +
             "id(c)={0} with l,t\n" +
             "RETURN id(l) as id, l.name as name, l.description as description,CASE when t IS NULL THEN [] ELSE collect({id:id(t),name:t.name,countryTag:t.countryTag})  END as tags")
     List<RuleTemplateCategoryTagDTO> getAllRulesOfCountry(long countryId);


    @Query("match(l:RuleTemplateCategory) Where id(l)={0} \n" +
            "set l.deleted=true\n" +
            "return l;")
    void softDelete(long ruleTemplateCategoryId);


    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]-(l:RuleTemplateCategory{deleted:false}) Where id(c)={0} and l.name=~{1} and l.ruleTemplateCategoryType={2} return l")
    RuleTemplateCategory findByName(Long countryId, String name,RuleTemplateCategoryType ruleTemplateCategoryType);

    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]->(l:RuleTemplateCategory{deleted:false}) Where id(c)={0} and l.name IN {1} return l")
    List<RuleTemplateCategory> findByNames(Long countryId, ArrayList<String> categoryNames );

    @Query("match(rc:RuleTemplateCategory) where id(rc)={0}\n" +
            "match (w:WTABaseRuleTemplate) where id(w)={1}\n" +
            "create (rc)-[:"+HAS_RULE_TEMPLATES+"]->(w)" )
    void setRuleTemplateCategoryWithRuleTemplate( Long templateCategoryId,Long ruleTemplateId );

    @Query("MATCH (n:RuleTemplateCategory{deleted:false})-[:"+HAS_RULE_TEMPLATES+"]->(w:WTABaseRuleTemplate)<-[:"+HAS_RULE_TEMPLATE+"]-(c:Country) where n.name={0} AND Id(c)={1} return Id(w)")
    List<Long> findAllExistingRuleTemplateAddedToThiscategory(String ruleTemplateCategoryName, long countryId);

    @Query("MATCH (allRTC:RuleTemplateCategory{deleted:false})\n" +
            "match(newRTC:RuleTemplateCategory) where newRTC.name={1} \n" +
            "Match(WBRT:WTABaseRuleTemplate)<-[r:HAS_RULE_TEMPLATES]-(allRTC)  where Id(WBRT) IN {0}\n" +
            "delete r\n" +
            "MERGE(WBRT)<-[:HAS_RULE_TEMPLATES]-(newRTC)")
    void updateCategoryOfRuleTemplate(List<Long> wtaBaseRuleTemplateId,String ruleTemplateCategoryName);

    // CTA PART
    @Query("match(newRTC:RuleTemplateCategory{ruleTemplateCategoryType:'CTA',deleted:false}) where newRTC.name={1} \n" +
            "Match(ctaRuleTemplates:CTARuleTemplate)-[r:HAS_RULE_TEMPLATES]-(:RuleTemplateCategory{ruleTemplateCategoryType:'CTA',deleted:false})  where Id(ctaRuleTemplates) IN {0}\n" +
            "delete r\n" +
            "MERGE(ctaRuleTemplates)<-[:HAS_RULE_TEMPLATES]-(newRTC)")
    void updateCategoryOfCTARuleTemplate(List<Long> ctaRuleTemplateList,String ruleTemplateCategoryName);

    @Query("match (c:Country)-[:"+HAS_RULE_TEMPLATE_CATEGORY+"]-(n:RuleTemplateCategory{ruleTemplateCategoryType:'CTA',deleted:false})-[:"+HAS_RULE_TEMPLATES+"]-(ctaRuleTemplates:CTARuleTemplate)\n" +
            "where n.name={0} AND Id(c)={1} return Id(ctaRuleTemplates)")
    List<Long> findAllExistingCTARuleTemplateByCategory(String ruleTemplateCategoryName, long countryId);


    RuleTemplateCategory findByNameAndRuleTemplateCategoryType(String name, RuleTemplateCategoryType ruleTemplateCategoryType);

    @Query("match (rt:RuleTemplateCategory) where id(rt)={0}\n" +
            "match (r:CTARuleTemplate) where id(r) IN {1}\n" +
            "match(rt)-[rel:"+HAS_RULE_TEMPLATES+"]->(r)\n" +
            "delete rel set rt.deleted=true")
    void deleteRelationOfRuleTemplateCategoryAndCTA(long ruleTemplateId, List<Long> ctaRuleTemplateIds );

    @Query("match (rt:RuleTemplateCategory) where id(rt)={0}\n" +
            "match (r:CTARuleTemplate) where id(r) IN {1}\n" +
            "create (rt)-[rq:"+HAS_RULE_TEMPLATES+"]->(r)")
    void setAllCTAWithCategoryNone(long ruleTemplateId, List<Long> ctaRuleTemplateIds);
}
