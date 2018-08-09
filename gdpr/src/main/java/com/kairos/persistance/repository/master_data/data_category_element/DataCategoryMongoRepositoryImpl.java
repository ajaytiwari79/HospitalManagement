package com.kairos.persistance.repository.master_data.data_category_element;

import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;

public class DataCategoryMongoRepositoryImpl implements CustomDataCategoryRepository {

    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public DataCategory findByName(Long countryId, Long organizationId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, DataCategory.class);

    }

    @Override
    public DataCategoryResponseDTO getDataCategoryWithDataElementById(Long countryId, Long organizationId, BigInteger id) {

        String projection = CustomAggregationQuery.dataCategoryWithDataElementProjectionData();
        Document projectionOperation = Document.parse(projection);
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup("data_element", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation)
        );


        AggregationResults<DataCategoryResponseDTO> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElement(Long countryId, Long organizationId) {

        String projection = CustomAggregationQuery.dataCategoryWithDataElementProjectionData();
        Document projectionOperation = Document.parse(projection);

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup("data_element", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation)
        );

        AggregationResults<DataCategoryResponseDTO> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<DataCategory> findByNamesAndUnitId(Long unitId, Set<String> names) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").in(names).and(ORGANIZATION_ID).is(unitId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, DataCategory.class);

    }


    @Override
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByUnitId(Long unitId) {
        String projection = CustomAggregationQuery.dataCategoryWithDataElementProjectionData();
        Document projectionOperation = Document.parse(projection);

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(unitId)),
                lookup("data_element", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation)
        );

        AggregationResults<DataCategoryResponseDTO> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public DataCategoryResponseDTO getDataCategoryWithDataElementByUnitIdAndId(Long unitId, BigInteger dataCategoryId) {

        String projection = CustomAggregationQuery.dataCategoryWithDataElementProjectionData();
        Document projectionOperation = Document.parse(projection);
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where("_id").is(dataCategoryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(unitId)),
                lookup("data_element", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation)
        );


        AggregationResults<DataCategoryResponseDTO> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDTO.class);
        return result.getUniqueMappedResult();
    }
}
