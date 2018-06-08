package com.kairos.persistance.repository.master_data_management.data_category_element;

import com.kairos.persistance.model.master_data_management.data_category_element.DataCategory;
import com.kairos.response.dto.master_data.DataCategoryResponseDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.AppConstant.COUNTRY_ID;
import static com.kairos.constant.AppConstant.DELETED;

public class DataCategoryMongoRepositoryImpl implements CustomDataCategoryRepository {

    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public DataCategoryResponseDto getDataCategoryWithDataElementById(Long countryId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false)),
                lookup("data_element", "dataElements", "_id", "dataElements"),
                unwind("dataElements"),
                match(Criteria.where("dataElements.deleted").is(false)),
                group("$id")
                        .first("name").as("name")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataElements").as("dataElements")


        );
        AggregationResults<DataCategoryResponseDto> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDto.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<DataCategoryResponseDto> getAllDataCategoryWithDataElement(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("data_element", "dataElements", "_id", "dataElements"),
                unwind("dataElements"),
                match(Criteria.where("dataElements.deleted").is(false)),
                group("$id")
                        .first("name").as("name")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataElements").as("dataElements")
        );

        AggregationResults<DataCategoryResponseDto> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDto.class);
        return result.getMappedResults();
    }
}
