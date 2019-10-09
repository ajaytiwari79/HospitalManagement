package com.kairos.persistence.repository.wta.rule_template;

import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
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
    public static final String DELETED = "deleted";
    public static final String COUNTRY_ID = "countryId";
    public static final String RULE_TEMPLATE_CATEGORY_TYPE = "ruleTemplateCategoryType";
    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public RuleTemplateCategory findByName(Long countryId, String name, RuleTemplateCategoryType ruleTemplateCategoryType) {
        Criteria criteria = Criteria.where("name").regex(Pattern.compile("^" + name.trim() + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(COUNTRY_ID).is(countryId).and(RULE_TEMPLATE_CATEGORY_TYPE).is(ruleTemplateCategoryType);
        return mongoTemplate.findOne(new Query(criteria),RuleTemplateCategory.class);
    }

    public List<RuleTemplateCategoryTagDTO> findAllUsingCountryId(Long countryId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(COUNTRY_ID).is(countryId).and(RULE_TEMPLATE_CATEGORY_TYPE).is(RuleTemplateCategoryType.WTA)),
                lookup("tag", "tags", "_id", "tags"),
                project("name", "description", "tags")
        );
        AggregationResults<RuleTemplateCategoryTagDTO> result = mongoTemplate.aggregate(aggregation, RuleTemplateCategory.class, RuleTemplateCategoryTagDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<RuleTemplateCategoryDTO> findAllUsingCountryIdAndType(Long countryId, RuleTemplateCategoryType ruleTemplateCategoryType) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(COUNTRY_ID).is(countryId).and(RULE_TEMPLATE_CATEGORY_TYPE).is(ruleTemplateCategoryType)),
                lookup("tag", "tags", "_id", "tags")
        );
        AggregationResults<RuleTemplateCategoryDTO> result = mongoTemplate.aggregate(aggregation, RuleTemplateCategory.class, RuleTemplateCategoryDTO.class);
        return result.getMappedResults();
    }

}
