package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;
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

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class AssetTypeMongoRepositoryImpl implements CustomAssetTypeRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    private String nonDeletedSubAsset = CustomAggregationQuery.assetTypesAddNonDeletedSubAssetTypes();
    Document nonDeletedSubAssetOperation = Document.parse(nonDeletedSubAsset);
    private String groupOperation = "{ '$group' : { '_id' : '$_id', 'subAssetTypes': { '$addToSet' : '$subAssetTypes' },'risks' : { '$first' : '$risks' }, 'hasSubAsset' : { '$first' : '$hasSubAsset' }, 'name' : { '$first':'$name' }," +
            "'subAssetType' : { '$first' :'$subAssetType'}, 'createdAt':{'$first' : '$createdAt' } } }";


    @Override
    public AssetType findByNameAndCountryId(Long countryId, String name) {

        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and("deleted").is(false).and("name").is(name).and("subAssetType").is(false));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, AssetType.class);
    }

    @Override
    public List<AssetTypeRiskResponseDTO> getAllAssetTypeWithSubAssetTypeAndRiskByCountryId(Long countryId) {


        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("subAssetType").is(false).and(DELETED).is(false)),
                lookup("risk", "risks", "_id", "risks"),
                lookup("asset_type", "subAssetTypes", "_id", "subAssetTypes"),
                unwind("subAssetTypes", true),
                lookup("risk", "subAssetTypes.risks", "_id", "subAssetTypes.risks"),
                new CustomAggregationOperation(Document.parse(groupOperation)),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );


        AggregationResults<AssetTypeRiskResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeRiskResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public AssetTypeResponseDTO getAssetTypesWithSubAssetTypesByIdAndCountryId(Long countryId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("subAssetType").is(false).and(DELETED).is(false).and("_id").is(id)),
                lookup("asset_type", "subAssetTypes", "_id", "subAssetTypes"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );
        AggregationResults<AssetTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeResponseDTO.class);
        return result.getUniqueMappedResult();
    }


    @Override
    public AssetType findByNameAndUnitId(Long organizationId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(organizationId).and("deleted").is(false).and("name").is(name).and("subAssetType").is(false));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, AssetType.class);
    }


    @Override
    public List<AssetTypeRiskResponseDTO> getAllAssetTypeWithSubAssetTypeAndRiskByUnitId(Long unitId) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and("subAssetType").is(false).and(DELETED).is(false)),
                lookup("risk", "risks", "_id", "risks"),
                lookup("asset_type", "subAssetTypes", "_id", "subAssetTypes"),
                unwind("subAssetTypes", true),
                lookup("risk", "subAssetTypes.risks", "_id", "subAssetTypes.risks"),
                new CustomAggregationOperation(Document.parse(groupOperation)),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );


        AggregationResults<AssetTypeRiskResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeRiskResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public AssetTypeResponseDTO getAssetTypesWithSubAssetTypesByIdAndUnitId(Long organizationId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(organizationId).and("subAssetType").is(false).and(DELETED).is(false).and("_id").is(id)),
                lookup("asset_type", "subAssetTypes", "_id", "subAssetTypes"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );
        AggregationResults<AssetTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeResponseDTO.class);
        return result.getUniqueMappedResult();
    }
}
