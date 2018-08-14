package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponsDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
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
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstant.*;

public class ProcessingActivityMongoRepositoryImpl implements CustomProcessingActivityRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    private Document addNonDeletedSubProcessingActivityOperation = Document.parse(CustomAggregationQuery.addNondeletedSubProcessingActivityToProcessingActivity());

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
                lookup("processingLegalBasis", "processingLegalBasis", "_id", "processingLegalBasis")

        );

        AggregationResults<ProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public ProcessingActivityResponseDTO getAllSubProcessingActivitiesOfProcessingActivity(Long organizationId, BigInteger processingActivityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("_id").is(processingActivityId).and("subProcess").is(true)),
                unwind("subProcessingActivities"),
                lookup("processing_purpose", "subProcessingActivities.processingPurposes", "_id", "subProcessingActivities.processingPurposes"),
                lookup("transfer_method", "subProcessingActivities.transferMethods", "_id", "subProcessingActivities.transferMethods"),
                lookup("accessor_party", "subProcessingActivities.accessorParties", "_id", "subProcessingActivities.accessorParties"),
                lookup("dataSource", "subProcessingActivities.dataSources", "_id", "subProcessingActivities.dataSources"),
                lookup("responsibility_type", "subProcessingActivities.responsibilityType", "_id", "subProcessingActivities.responsibilityType"),
                lookup("processingLegalBasis", "subProcessingActivities.processingLegalBasis", "_id", "subProcessingActivities.processingLegalBasis"),
                group("$id")
                        .addToSet("subProcessingActivities").as("subProcessingActivities"),
                project().andExclude("_id"),
                unwind("subProcessingActivities"),
                sort(Sort.Direction.ASC, "name")
        );

        AggregationResults<ProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityResponseDTO.class);
        return result.getUniqueMappedResult();
    }


    @Override
    public List<ProcessingActivityBasicResponsDTO> getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(Long unitId, Set<BigInteger> processingActivityIds) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false).and("_id").in(processingActivityIds)),
                lookup("processing_activity", "subProcessingActivities", "_id", "subProcessingActivities"),
                new CustomAggregationOperation(addNonDeletedSubProcessingActivityOperation)
        );
        AggregationResults<ProcessingActivityBasicResponsDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityBasicResponsDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<ProcessingActivityBasicResponsDTO> getAllProcessingActivityBasicDetailWithSubprocessingActivities(Long unitId) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("processing_activity", "subProcessingActivities", "_id", "subProcessingActivities"),
                new CustomAggregationOperation(addNonDeletedSubProcessingActivityOperation)
        );
        AggregationResults<ProcessingActivityBasicResponsDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityBasicResponsDTO.class);
        return result.getMappedResults();
    }
}
