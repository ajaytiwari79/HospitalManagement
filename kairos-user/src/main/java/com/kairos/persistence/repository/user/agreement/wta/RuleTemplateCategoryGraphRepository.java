package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by vipul on 2/8/17.
 */
@Repository
public interface RuleTemplateCategoryGraphRepository extends GraphRepository<RuleTemplateCategory> {
    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]-(l:RuleTemplateCategory{deleted:false}) Where id(c)={0}\n" +
            "return l")
    List<RuleTemplateCategory> getAllRulesOfCountry(long countryId);

    @Query("match(l:RuleTemplateCategory) Where id(l)={0} \n" +
            "set l.deleted=true\n" +
            "return l;")
    void softDelete(long ruleTemplateCategoryId);


    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]->(l:RuleTemplateCategory{deleted:false}) Where id(c)={0} and l.name=~{1} return l")
    RuleTemplateCategory findByName(Long countryId, String name);

    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"]->(l:RuleTemplateCategory{deleted:false}) Where id(c)={0} and l.name IN {1} return l")
    List<RuleTemplateCategory> findByNames(Long countryId, ArrayList<String> categoryNames );

    @Query("match(rc:RuleTemplateCategory) where id(rc)={0}\n" +
            "match (w:WTABaseRuleTemplate) where id(w)={1}\n" +
            "create (rc)-[:"+HAS_RULE_TEMPLATES+"]->(w)" )
    void setRuleTemplateCategoryWithRuleTemplate( Long templateCategoryId,Long ruleTemplateId );

    @Query("MATCH (n:RuleTemplateCategory)-[:"+HAS_RULE_TEMPLATES+"]->(w:WTABaseRuleTemplate)<-[:"+HAS_RULE_TEMPLATE+"]-(c:Country) where n.name={0} AND Id(c)={1} return Id(w)")
    List<Long> findAllExistingRuleTemplateAddedToThiscategory(String ruleTemplateCategoryName, long countryId);

    @Query("MATCH (n:RuleTemplateCategory)\n" +
            "match(he:RuleTemplateCategory) where he.name={1} \n" +
            "Match(w:WTABaseRuleTemplate)<-[r:HAS_RULE_TEMPLATES]-(n)  where Id(w) IN {0}\n" +
            "delete r\n" +
            "create(w)<-[:HAS_RULE_TEMPLATES]-(he)")
    void updateCategoryOfRuleTemplate(List<Long> wtaBaseRuleTemplateId,String ruleTemplateCategoryName);
}
