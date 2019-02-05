package com.kairos.persistence.repository.master_data.asset_management;


public class AssetTypeMongoRepositoryImpl  {


   /* @Inject
    private MongoTemplate mongoTemplate;


    private String nonDeletedSubAsset = CustomAggregationQuery.assetTypesAddNonDeletedSubAssetTypes();
    Document nonDeletedSubAssetOperation = Document.parse(nonDeletedSubAsset);
    private String groupOperationWithRisk = "{ '$group' : { '_id' : '$_id', 'subAssetTypes': { '$addToSet' : '$subAssetTypes' },'risks' : { '$first' : '$risks' }, 'hasSubAsset' : { '$first' : '$hasSubAsset' }, 'name' : { '$first':'$name' }," +
            "'subAssetType' : { '$first' :'$subAssetType'}, 'createdAt':{'$first' : '$createdAt' } } }";
    private String groupOperationWithOutRisk = "{ '$group' : { '_id' : '$_id', 'subAssetTypes': { '$addToSet' : '$subAssetTypes' },'hasSubAsset' : { '$first' : '$hasSubAsset' }, 'name' : { '$first':'$name' }," +
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
                lookup("assetType", "subAssetTypes", "_id", "subAssetTypes"),
                unwind("subAssetTypes", true),
                lookup("risk", "subAssetTypes.risks", "_id", "subAssetTypes.risks"),
                new CustomAggregationOperation(Document.parse(groupOperationWithRisk)),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );


        AggregationResults<AssetTypeRiskResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeRiskResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public AssetTypeResponseDTO getAssetTypeWithSubAssetTypesByIdAndCountryId(Long countryId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("subAssetType").is(false).and(DELETED).is(false).and("_id").is(id)),
                lookup("assetType", "subAssetTypes", "_id", "subAssetTypes"),
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
                lookup("assetType", "subAssetTypes", "_id", "subAssetTypes"),
                unwind("subAssetTypes", true),
                lookup("risk", "subAssetTypes.risks", "_id", "subAssetTypes.risks"),
                new CustomAggregationOperation(Document.parse(groupOperationWithRisk)),
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
                lookup("assetType", "subAssetTypes", "_id", "subAssetTypes"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );
        AggregationResults<AssetTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<AssetTypeResponseDTO> getAllAssetTypeWithSubAssetTypeByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and("subAssetType").is(false).and(DELETED).is(false)),
                lookup("risk", "risks", "_id", "risks"),
                lookup("assetType", "subAssetTypes", "_id", "subAssetTypes"),
                unwind("subAssetTypes", true),
                new CustomAggregationOperation(Document.parse(groupOperationWithOutRisk)),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(nonDeletedSubAssetOperation)
        );


        AggregationResults<AssetTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, AssetType.class, AssetTypeResponseDTO.class);
        return result.getMappedResults();
    }*/
}
