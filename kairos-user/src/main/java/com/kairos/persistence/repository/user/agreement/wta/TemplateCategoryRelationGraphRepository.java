package com.kairos.persistence.repository.user.agreement.wta;

import com.kairos.persistence.model.user.agreement.wta.templates.TemplateCategoryRelation;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

/**
 * Created by pawanmandhan on 11/8/17.
 */
public interface TemplateCategoryRelationGraphRepository extends Neo4jBaseRepository<TemplateCategoryRelation,Long> {


    @Query("MATCH (template:WTABaseRuleTemplate) where id(template) = {0} with template " +
            "Match (template)-[relation:HAS_RULE_TEMPLATE_CATEGORY]->(category:RuleTemplateCategory) with relation,category " +
            "Return relation")
    TemplateCategoryRelation getTemplateCategoryRelationById(long templateId);





}
