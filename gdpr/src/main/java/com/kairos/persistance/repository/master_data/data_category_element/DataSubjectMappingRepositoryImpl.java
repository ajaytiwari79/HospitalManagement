package com.kairos.persistance.repository.master_data.data_category_element;

import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDto;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import java.math.BigInteger;
import java.util.List;





public class DataSubjectMappingRepositoryImpl implements CustomDataSubjectMappingRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public DataSubjectMapping findByName(Long countryId, Long organizationId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, DataSubjectMapping.class);


    }

    @Override
    public DataSubjectMappingResponseDto getDataSubjectAndMappingWithDataCategory(Long countryId,Long organizationId, BigInteger id) {

        String addFields=CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter=Document.parse(addFields);
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup("data_category", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("data_element", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
                new CustomAggregationOperation(addToFieldOperationFilter),
                match(Criteria.where("dataCategories.deleted").is(false)),
                group("$id")
                        .first("organizationTypes").as("organizationTypes")
                        .first("organizationSubTypes").as("organizationSubTypes")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataCategories").as("dataCategories")

        );

        AggregationResults<DataSubjectMappingResponseDto> result = mongoTemplate.aggregate(aggregation, DataSubjectMapping.class, DataSubjectMappingResponseDto.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<DataSubjectMappingResponseDto>getAllDataSubjectAndMappingWithDataCategory(Long countryId,Long organizationId)
    {

        String addFields=CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter=Document.parse(addFields);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup("data_category", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("data_element", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
                new CustomAggregationOperation(addToFieldOperationFilter),
                match(Criteria.where("dataCategories.deleted").is(false)),
                group("$id")
                        .first("organizationTypes").as("organizationTypes")
                        .first("organizationSubTypes").as("organizationSubTypes")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataCategories").as("dataCategories")
        );
        AggregationResults<DataSubjectMappingResponseDto> result = mongoTemplate.aggregate(aggregation, DataSubjectMapping.class, DataSubjectMappingResponseDto.class);
        return result.getMappedResults();
    }
}
