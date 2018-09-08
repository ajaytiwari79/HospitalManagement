package com.kairos.persistence.repository.wta.rule_template;

import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class RuleTemplateCategoryRepositoryImpl implements CustomRuleTemplateCategoryRepository {
    @Inject
    private MongoTemplate mongoTemplate;


    public List<RuleTemplateCategoryTagDTO> findAllUsingCountryId(Long countryId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId).and("ruleTemplateCategoryType").is(RuleTemplateCategoryType.WTA)),
                unwind("tags",true),
                lookup("tag", "tags", "_id", "tags"),
                project("name", "description", "tags")
        );
        AggregationResults<RuleTemplateCategoryTagDTO> result = mongoTemplate.aggregate(aggregation, RuleTemplateCategory.class, RuleTemplateCategoryTagDTO.class);
        return result.getMappedResults();
    }

}
