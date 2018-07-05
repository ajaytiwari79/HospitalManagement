package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.asset_management.AssetType;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.AssetTypeResponseDto;
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

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class AssetTypeMongoRepositoryImpl implements CustomAssetTypeRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    final String nonDeletedSubAsset = CustomAggregationQuery.assetTypesAddNonDeletedSubAssetTypes();
    Document nonDeletedSubAssetOperation = Document.parse(nonDeletedSubAsset);

    @Override
    public AssetType findByName(Long countryId, Long organizationId, String name) {

        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and("deleted").is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId).and("isSubAsset").is(true));
        ;
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, AssetType.class);
    }

    @Override
    public List<AssetTypeResponseDto> getAllAssetTypesWithSubAssetTypes(Long countryId, Long organizationId) {


        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(organizationId).and("isSubAsset").is(true).and(DELETED).is(false)),
                lookup("asset_type", "subAssetTypes", "_id", "subAssetTypes"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );


        AggregationResults<AssetTypeResponseDto> result = mongoTemplate.aggregate(aggregation,AssetType.class,AssetTypeResponseDto.class);
        return result.getMappedResults();
    }

    @Override
    public AssetTypeResponseDto getAssetTypesWithSubAssetTypes(Long countryId, Long organizationId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(organizationId).and("isSubAsset").is(true).and(DELETED).is(false).and("_id").is(id)),
                lookup("asset_type", "subAssetTypes", "_id", "subAssetTypes"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );


        AggregationResults<AssetTypeResponseDto> result = mongoTemplate.aggregate(aggregation,AssetType.class,AssetTypeResponseDto.class);
        return result.getUniqueMappedResult();
    }
}
