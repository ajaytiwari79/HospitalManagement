package com.kairos.persistence.repository.master_data.asset_management;

public class MasterAssetMongoRepositoryImpl {


   /* @Inject
    private MongoTemplate mongoTemplate;


    private Document masterAssetProjectionOperation = Document.parse(CustomAggregationQuery.masterAssetProjectionWithAssetType());


    @Override
    public MasterAsset findByName(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").is(name));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, MasterAsset.class);

    }


    @Override
    public List<MasterAssetResponseDTO> getAllMasterAssetWithAssetTypeAndSubAssetType(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(masterAssetProjectionOperation)


        );

        AggregationResults<MasterAssetResponseDTO> results = mongoTemplate.aggregate(aggregation, MasterAsset.class, MasterAssetResponseDTO.class);
        return results.getMappedResults();
    }


    @Override
    public MasterAssetResponseDTO getMasterAssetWithAssetTypeAndSubAssetTypeById(Long countryId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id)),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                new CustomAggregationOperation(masterAssetProjectionOperation)


        );

        AggregationResults<MasterAssetResponseDTO> results = mongoTemplate.aggregate(aggregation, MasterAsset.class, MasterAssetResponseDTO.class);
        return results.getUniqueMappedResult();
    }


    @Override
    public List<MasterAssetResponseDTO> getMasterAssetDataWithFilterSelection(Long countryId, FilterSelectionDTO filterSelectionDto) {

        Criteria criteria = Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false);

        List<Criteria> clauseCriteria = new ArrayList<>(filterSelectionDto.getFiltersData().size());
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size() != 0) {
                clauseCriteria.add(buildMatchCriteria(filterSelection, filterSelection.getName()));
            }
        });
        if (!clauseCriteria.isEmpty()) {
            criteria = criteria.andOperator(clauseCriteria.toArray(new Criteria[clauseCriteria.size()]));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(masterAssetProjectionOperation)


        );
        AggregationResults<MasterAssetResponseDTO> results = mongoTemplate.aggregate(aggregation, MasterAsset.class, MasterAssetResponseDTO.class);
        return results.getMappedResults();
    }


    @Override
    public Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType) {

        switch (filterType) {
            case ACCOUNT_TYPES:
                return Criteria.where("accountTypes" + ID).in(filterSelection.getValue());
            case ORGANIZATION_TYPES:
                return Criteria.where("organizationTypes" + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_TYPES:
                return Criteria.where("organizationSubTypes" + ID).in(filterSelection.getValue());
            case ORGANIZATION_SERVICES:
                return Criteria.where("organizationServices" + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_SERVICES:
                return Criteria.where("organizationSubServices" + ID).in(filterSelection.getValue());
            default:
                throw new InvalidRequestException("data not found for Filter Type " + filterType);


        }


    }

    @Override
    public List<MasterAssetResponseDTO> getMasterAssetByOrgTypeSubTypeCategoryAndSubCategory(Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("organizationTypes._id").in(organizationMetaDataDTO.getOrganizationTypeId())
                        .and("organizationSubTypes._id").in(organizationMetaDataDTO.getOrganizationSubTypeIds()).and(("organizationServices._id")).in(organizationMetaDataDTO.getServiceCategoryIds())
                        .and("organizationSubServices._id").in(organizationMetaDataDTO.getSubServiceCategoryIds())),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                new CustomAggregationOperation(masterAssetProjectionOperation)

        );
        return mongoTemplate.aggregate(aggregation, MasterAsset.class, MasterAssetResponseDTO.class).getMappedResults();

    }*/
}
