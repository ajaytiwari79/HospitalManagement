package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class AssetMongoRepositoryImpl implements CustomAssetRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    private Document projectionOperation = Document.parse(CustomAggregationQuery.assetProjectionWithMetaData());


    @Override
    public Asset findByName(Long organizationId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Asset.class);

    }


    @Override
    public AssetResponseDTO getAssetWithRiskAndRelatedProcessingActivitiesById(Long organizationId, BigInteger id) {
        Aggregation aggregation= getAggregationOfAsset(organizationId,id);
        AggregationResults<AssetResponseDTO> results = mongoTemplate.aggregate(aggregation, Asset.class, AssetResponseDTO.class);
        return results.getUniqueMappedResult();

    }

    @Override
    public List<AssetResponseDTO> findAllByUnitId(Long organizationId) {
        Aggregation aggregation= getAggregationOfAsset(organizationId,null);
        AggregationResults<AssetResponseDTO> results = mongoTemplate.aggregate(aggregation, Asset.class, AssetResponseDTO.class);
        return results.getMappedResults();
    }

    private Aggregation getAggregationOfAsset(Long organizationId, BigInteger id){
        String addSelectedSubProcessingActivity = "{'$addFields':{" +
                "    'processingActivities.subProcessingActivities':{   " +
                "     '$filter': {" +
                "      'input': '$processingActivities.subProcessingActivities'," +
                "      'as': 'subProcess'," +
                "      'cond': { '$in': [ '$$subProcess._id', '$subProcessingActivityIds' ] }}" +
                "     }}}";
        String addAssetType = "{'$addFields':{'assetType':{'$arrayElemAt':['$assetType',0]}}}";
        String addSubAssetType = "{'$addFields':{'assetSubType':{'$arrayElemAt':['$assetSubType',0]}}}";
        String groupOperation = "{'$group':{" +
                "       '_id':'$_id','processingActivities':{'$addToSet':'$processingActivities'},'assetType':{'$first':'$assetType'},'technicalSecurityMeasures':{'$first':'$technicalSecurityMeasures'},\n" +
                "       'assetSubType':{'$first':'$assetSubType'},'description':{'$first':'$description'},'hostingLocation':{'$first':'$hostingLocation'},'assetAssessor':{'$first':'$assetAssessor'},\n" +
                "        'name':{'$first':'$name'},'storageFormats':{'$first':'$storageFormats'},'orgSecurityMeasures':{'$first':'$orgSecurityMeasures'},'active':{'$first':'$active'},\n" +
                "        'hostingType':{'$first':{'$arrayElemAt':['$hostingType',0]}}," +
                "        'dataDisposal':{'$first':{'$arrayElemAt':['$dataDisposal',0]}}," +
                "        'hostingProvider':{'$first':{'$arrayElemAt':['$hostingProvider',0]}}" +
                "   }}";

        Criteria criteria=(Optional.ofNullable(id).isPresent())?Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("_id").is(id):
                Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("storageFormat", "storageFormats", "_id", "storageFormats"),
                lookup("organizationalSecurityMeasure", "orgSecurityMeasures", "_id", "orgSecurityMeasures"),
                lookup("technicalSecurityMeasure", "technicalSecurityMeasures", "_id", "technicalSecurityMeasures"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("hostingProvider", "hostingProviderId", "_id", "hostingProvider"),
                lookup("hostingType", "hostingTypeId", "_id", "hostingType"),
                lookup("dataDisposal", "dataDisposalId", "_id", "dataDisposal"),
                new CustomAggregationOperation(Document.parse(addAssetType)),
                new CustomAggregationOperation(Document.parse(addSubAssetType)),
                lookup("risk", "assetType.risks", "_id", "assetType.riskList"),
                lookup("risk", "assetSubType.risks", "_id", "assetSubType.riskList"),
                lookup("processingActivity", "processingActivityIds", "_id", "processingActivities"),
                unwind("processingActivities", true),
                lookup("processingActivity", "processingActivities.subProcessingActivities", "_id", "processingActivities.subProcessingActivities"),
                new CustomAggregationOperation(Document.parse(addSelectedSubProcessingActivity)),
                new CustomAggregationOperation(Document.parse(groupOperation))
        );
        return  aggregation;
    }

}
