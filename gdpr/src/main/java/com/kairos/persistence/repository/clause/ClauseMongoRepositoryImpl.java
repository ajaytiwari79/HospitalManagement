package com.kairos.persistence.repository.clause;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.enums.FilterType;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.ID;
import static com.kairos.constants.AppConstant.DELETED;


public class ClauseMongoRepositoryImpl implements CustomClauseRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    Document addNonDeletedTemplateTypeOperation = Document.parse(CustomAggregationQuery.addNonDeletedTemplateTyeField());


    @Override
    public Clause findByTitle(Long countryId, Long organizationId, String title) {

        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("title").is(title).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Clause.class);
    }


    @Override
    public List<Clause> findClausesByTitle(Long countryId, Long orgId, List<String> clauseTitles) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("title").in(clauseTitles).and(ORGANIZATION_ID).is(orgId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, Clause.class);
    }


    @Override
    public List<ClauseResponseDTO> findAllClauseWithTemplateType(Long countryId, Long organizationId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false)),
                lookup("template_type", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation)

        );


        AggregationResults<ClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public ClauseResponseDTO findClauseWithTemplateTypeById(Long countryId, Long organizationId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("_id").is(id)),
                lookup("template_type", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation)


        );
        AggregationResults<ClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseResponseDTO.class);
        return result.getUniqueMappedResult();
    }


    @Override
    public List<ClauseResponseDTO> getClauseDataWithFilterSelection(Long countryId, Long organizationId, FilterSelectionDTO filterSelectionDto) {

        Criteria criteria = Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId);
        List<Criteria> clauseCriteria = new ArrayList<>(filterSelectionDto.getFiltersData().size());
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size() != 0) {
                clauseCriteria.add(buildMatchCriteria(filterSelection, filterSelection.getName()));
            }
        });
        if (!clauseCriteria.isEmpty()) {
            criteria = criteria.andOperator(clauseCriteria.toArray(new Criteria[clauseCriteria.size()]));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("template_type", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation)

        );

        AggregationResults<ClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType) {

        switch (filterType) {
            case ACCOUNT_TYPES:
                List<BigInteger> ids = new ArrayList<>();
                for (Long id : filterSelection.getValue()) {
                    ids.add(BigInteger.valueOf(id));
                }
                return Criteria.where("accountTypes" + ID).in(ids);
            case ORGANIZATION_TYPES:
                return Criteria.where("organizationTypes" + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_TYPES:
                return Criteria.where("organizationSubTypes" + ID).in(filterSelection.getValue());
            case ORGANIZATION_SERVICES:
                return Criteria.where("organizationServices" + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_SERVICES:
                return Criteria.where("organizationSubServices" + ID).in(filterSelection.getValue());
            default:
                throw new InvalidRequestException("data not found for FilterType " + filterType);

        }


    }


}
