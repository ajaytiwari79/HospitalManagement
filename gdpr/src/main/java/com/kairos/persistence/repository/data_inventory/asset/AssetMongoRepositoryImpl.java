package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class AssetMongoRepositoryImpl implements CustomAssetRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    private Document projectionOperation = Document.parse(CustomAggregationQuery.assetProjectionWithMetaData());


    @Override
    public Asset findByName( Long organizationId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Asset.class);

    }


    @Override
    public AssetResponseDTO findAssetWithMetaDataById( Long organizationId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("_id").is(id)),
                lookup("storageFormat", "storageFormats", "_id", "storageFormats"),
                lookup("organizationalSecurityMeasure", "orgSecurityMeasures", "_id", "orgSecurityMeasures"),
                lookup("technicalSecurityMeasure", "technicalSecurityMeasures", "_id", "technicalSecurityMeasures"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("hostingProvider", "hostingProviderId", "_id", "hostingProvider"),
                lookup("hostingType", "hostingTypeId", "_id", "hostingType"),
                lookup("dataDisposal", "dataDisposalId", "_id", "dataDisposal"),
                new CustomAggregationOperation(projectionOperation)
        );

        AggregationResults<AssetResponseDTO> results = mongoTemplate.aggregate(aggregation, Asset.class, AssetResponseDTO.class);
        return results.getUniqueMappedResult();

    }

    @Override
    public List<AssetResponseDTO> findAllAssetWithMetaData( Long organizationId) {


        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false)),
                lookup("storageFormat", "storageFormats", "_id", "storageFormats"),
                lookup("organizationalSecurityMeasure", "orgSecurityMeasures", "_id", "orgSecurityMeasures"),
                lookup("technicalSecurityMeasure", "technicalSecurityMeasures", "_id", "technicalSecurityMeasures"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("hostingProvider", "hostingProviderId", "_id", "hostingProvider"),
                lookup("hostingType", "hostingTypeId", "_id", "hostingType"),
                lookup("dataDisposal", "dataDisposalId", "_id", "dataDisposal"),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(projectionOperation)



                );
        AggregationResults<AssetResponseDTO> results = mongoTemplate.aggregate(aggregation, Asset.class, AssetResponseDTO.class);
        return results.getMappedResults();
    }

}
