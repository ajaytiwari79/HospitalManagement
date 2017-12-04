package com.kairos.persistence.repository.user.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplate;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CTARuleTemplateGraphRepository  extends GraphRepository<CTARuleTemplate> {

 @Query("MATCH (n:`CTARuleTemplate`) WHERE NOT(n.`deleted` = true ) AND NOT(n.`disabled` = true )"+
    "MATCH (m0:`RuleTemplateCategory`) WHERE ID(m0) IN {0} "+
         "MATCH (n)-[:`HAS_RULE_TEMPLATES`]-(m0) WITH n MATCH p=(n)-[*0..1]-(m) RETURN p, ID(n)")
    List<CTARuleTemplate>findByRuleTemplateCategoryIdInAndDeletedFalseAndDisabledFalse(List<Long> categoryList);

}
