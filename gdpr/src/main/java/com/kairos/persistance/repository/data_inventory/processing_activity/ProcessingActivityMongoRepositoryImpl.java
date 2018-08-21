package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import javax.print.Doc;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstant.*;

public class ProcessingActivityMongoRepositoryImpl implements CustomProcessingActivityRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    private Document addNonDeletedSubProcessingActivityOperation = Document.parse(CustomAggregationQuery.addNonDeletedSubProcessingActivityToProcessingActivity());

    @Override
    public ProcessingActivity findByName(Long organizationId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("name").is(name).and("subProcess").is(false));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, ProcessingActivity.class);
    }


    @Override
    public List<ProcessingActivityResponseDTO> getAllProcessingActivityAndMetaData(Long organizationId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("processing_purpose", "processingPurposes", "_id", "processingPurposes"),
                lookup("transfer_method", "transferMethods", "_id", "transferMethods"),
                lookup("accessor_party", "accessorParties", "_id", "accessorParties"),
                lookup("dataSource", "dataSources", "_id", "dataSources"),
                lookup("responsibility_type", "responsibilityType", "_id", "responsibilityType"),
                lookup("processingLegalBasis", "processingLegalBasis", "_id", "processingLegalBasis"),
                lookup("asset", "assetId", "_id", "asset")
        );

        AggregationResults<ProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<ProcessingActivityResponseDTO> getAllSubProcessingActivitiesOfProcessingActivity(Long organizationId, BigInteger processingActivityId) {

        String replaceRoot = " { '$replaceRoot':{'newRoot':'$subProcessingActivities'} }";
        Document replaceRootOperation = Document.parse(replaceRoot);

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("_id").is(processingActivityId).and("subProcess").is(false)),
                lookup("processing_activity", "subProcessingActivities", "_id", "subProcessingActivities"),
                unwind("subProcessingActivities"),
                lookup("processing_purpose", "subProcessingActivities.processingPurposes", "_id", "subProcessingActivities.processingPurposes"),
                lookup("transfer_method", "subProcessingActivities.transferMethods", "_id", "subProcessingActivities.transferMethods"),
                lookup("accessor_party", "subProcessingActivities.accessorParties", "_id", "subProcessingActivities.accessorParties"),
                lookup("dataSource", "subProcessingActivities.dataSources", "_id", "subProcessingActivities.dataSources"),
                lookup("responsibility_type", "subProcessingActivities.responsibilityType", "_id", "subProcessingActivities.responsibilityType"),
                lookup("processingLegalBasis", "subProcessingActivities.processingLegalBasis", "_id", "subProcessingActivities.processingLegalBasis"),
                group("$id")
                        .addToSet("subProcessingActivities").as("subProcessingActivities"),
                unwind("subProcessingActivities"),
                new CustomAggregationOperation(replaceRootOperation)
               // sort(Sort.Direction.ASC, "name")
        );

        AggregationResults<ProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<ProcessingActivityBasicResponseDTO> getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(Long unitId, Set<BigInteger> processingActivityIds) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false).and("_id").in(processingActivityIds)),
                lookup("processing_activity", "subProcessingActivities", "_id", "subProcessingActivities"),
                new CustomAggregationOperation(addNonDeletedSubProcessingActivityOperation)
        );
        AggregationResults<ProcessingActivityBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityBasicResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<ProcessingActivityBasicResponseDTO> getAllProcessingActivityBasicDetailWithSubprocessingActivities(Long unitId) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("processing_activity", "subProcessingActivities", "_id", "subProcessingActivities"),
                new CustomAggregationOperation(addNonDeletedSubProcessingActivityOperation)
        );
        AggregationResults<ProcessingActivityBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityBasicResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<DataSubjectMappingResponseDTO> getAllMappedDataSubjectWithDataCategoryAndDataElement(Long unitId, List<BigInteger> dataSubjectIds) {

        String addNonDeletedDataElements = CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter = Document.parse(addNonDeletedDataElements);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(unitId).and("_id").in(dataSubjectIds)),
                lookup("data_category", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("data_element", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
                new CustomAggregationOperation(addToFieldOperationFilter),
                match(Criteria.where("dataCategories.deleted").is(false)),
                group("$id")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataCategories").as("dataCategories")
        );
        AggregationResults<DataSubjectMappingResponseDTO> result = mongoTemplate.aggregate(aggregation, DataSubjectMapping.class, DataSubjectMappingResponseDTO.class);
        return result.getMappedResults();


    }
}
