package com.kairos.persistence.repository.data_inventory.processing_activity;

import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityRiskResponseDTO;
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
    public List<ProcessingActivityResponseDTO> getAllProcessingActivityAndMetaDataAndSubProcessingActivities(Long organizationId) {


        String groupOperation = "{'$group':{'_id':'$_id','subProcessingActivities':{'$addToSet':'$subProcessingActivities'}," +
                "'processingPurposes':{'$first':'$processingPurposes'}," +
                "'transferMethods':{'$first':'$transferMethods'}," +
                "'accessorParties':{'$first':'$accessorParties'}," +
                "'dataSources':{'$first':'$dataSources'}," +
                "'responsibilityType':{ '$first':'$responsibilityType'}," +
                "'processingLegalBasis':{'$first':'$processingLegalBasis'}," +
                "'createdAt':{'$first':'$createdAt'}," +
                "'name':{'$first':'$name'}," +
                "'active':{'$first':'$active'}," +
                "'suggested':{'$first':'$suggested'}," +
                "'description':{'$first':'$description'}," +
                "'risks':{'$first':'$risks'}," +
                "'managingDepartment':{'$first':'$managingDepartment'}," +
                "'processOwner':{'$first':'$processOwner'}}}";


        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("processingPurpose", "processingPurposes", "_id", "processingPurposes"),
                lookup("transferMethod", "transferMethods", "_id", "transferMethods"),
                lookup("accessorParty", "accessorParties", "_id", "accessorParties"),
                lookup("dataSource", "dataSources", "_id", "dataSources"),
                lookup("responsibilityType", "responsibilityType", "_id", "responsibilityType"),
                lookup("processingLegalBasis", "processingLegalBasis", "_id", "processingLegalBasis"),
                lookup("risk", "risks", "_id", "risks"),
                lookup("processingActivity", "subProcessingActivities", "_id", "subProcessingActivities"),
                unwind("subProcessingActivities", true),
                lookup("risk", "subProcessingActivities.risks", "_id", "subProcessingActivities.risks"),
                lookup("processingPurpose", "subProcessingActivities.processingPurposes", "_id", "subProcessingActivities.processingPurposes"),
                lookup("transferMethod", "subProcessingActivities.transferMethods", "_id", "subProcessingActivities.transferMethods"),
                lookup("accessorParty", "subProcessingActivities.accessorParties", "_id", "subProcessingActivities.accessorParties"),
                lookup("dataSource", "subProcessingActivities.dataSources", "_id", "subProcessingActivities.dataSources"),
                lookup("responsibilityType", "subProcessingActivities.responsibilityType", "_id", "subProcessingActivities.responsibilityType"),
                lookup("processingLegalBasis", "subProcessingActivities.processingLegalBasis", "_id", "subProcessingActivities.processingLegalBasis"),
                new CustomAggregationOperation(Document.parse(groupOperation)),
                sort(Sort.Direction.DESC, "createdAt")
        );

        AggregationResults<ProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityResponseDTO.class);
        return result.getMappedResults();

    }


    @Override
    public List<ProcessingActivityBasicResponseDTO> getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(Long unitId, Set<BigInteger> processingActivityIds) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false).and("_id").in(processingActivityIds)),
                lookup("processingActivity", "subProcessingActivities", "_id", "subProcessingActivities"),
                new CustomAggregationOperation(addNonDeletedSubProcessingActivityOperation)
        );
        AggregationResults<ProcessingActivityBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityBasicResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<ProcessingActivityBasicResponseDTO> getAllProcessingActivityBasicDetailWithSubProcessingActivities(Long unitId) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("processingActivity", "subProcessingActivities", "_id", "subProcessingActivities"),
                new CustomAggregationOperation(addNonDeletedSubProcessingActivityOperation),
                sort(Sort.Direction.DESC, "createdAt")

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
                lookup("dataCategory", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("dataElement", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
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

    @Override
    public ProcessingActivityResponseDTO getProcessingActivityAndMetaDataById(Long unitId, BigInteger processingActivityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false).and("_id").is(processingActivityId)),
                lookup("processingPurpose", "processingPurposes", "_id", "processingPurposes"),
                lookup("transferMethod", "transferMethods", "_id", "transferMethods"),
                lookup("accessorParty", "accessorParties", "_id", "accessorParties"),
                lookup("dataSource", "dataSources", "_id", "dataSources"),
                lookup("responsibilityType", "responsibilityType", "_id", "responsibilityType"),
                lookup("processingLegalBasis", "processingLegalBasis", "_id", "processingLegalBasis"),
                lookup("asset", "assetId", "_id", "asset"),
                lookup("risk","risks","_id","risks")
        );

        AggregationResults<ProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<AssetBasicResponseDTO> getAllAssetLinkedWithProcessingActivityById(Long unitId, BigInteger processingActivityId) {

        String replaceRoot = "{ '$replaceRoot': { 'newRoot': '$linkedAssets' } }";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false).and("_id").is(processingActivityId)),
                lookup("asset", "linkedAssets", "_id", "linkedAssets"),
                unwind("linkedAssets"),
                new CustomAggregationOperation(Document.parse(replaceRoot)),
                match(Criteria.where(DELETED).is(false))
        );
        AggregationResults<AssetBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, AssetBasicResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<ProcessingActivityRiskResponseDTO> getAllProcessingActivityAndSubProcessWithRisksByUnitId(Long unitId) {

        String groupSubProcessingActivity = "{'$group':{_id:'$_id','processingActivities':{'$addToSet':'$processingActivities'},'risks':{'$first':'$risks'},'name':{'$first':'$name'}}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("risk", "risks", "_id", "risks"),
                lookup("processingActivity", "subProcessingActivities", "_id", "processingActivities"),
                unwind("processingActivities", true),
                lookup("risk", "processingActivities.risks", "_id", "processingActivities.risks"),
                new CustomAggregationOperation(Document.parse(groupSubProcessingActivity))

        );
        AggregationResults<ProcessingActivityRiskResponseDTO> result = mongoTemplate.aggregate(aggregation, ProcessingActivity.class, ProcessingActivityRiskResponseDTO.class);
        return result.getMappedResults();

    }
}
