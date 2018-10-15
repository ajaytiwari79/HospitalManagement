package com.kairos.persistence.repository.wta.rule_template;

import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class RuleTemplateCategoryRepositoryImpl implements CustomRuleTemplateCategoryRepository {
    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public RuleTemplateCategory findByName(Long countryId, String name, RuleTemplateCategoryType ruleTemplateCategoryType) {
        Criteria criteria = Criteria.where("name").regex(Pattern.compile("^" + name.trim() + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false).and("countryId").is(countryId).and("ruleTemplateCategoryType").is(ruleTemplateCategoryType);
        return mongoTemplate.findOne(new Query(criteria),RuleTemplateCategory.class);
    }

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
