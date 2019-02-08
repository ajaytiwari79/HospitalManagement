package com.kairos.persistence.repository.master_data.data_category_element;

public class DataCategoryMongoRepositoryImpl {

    /*@Inject
    private MongoTemplate mongoTemplate;


    @Override
    public DataCategory findByCountryIdName(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, DataCategory.class);

    }

    @Override
    public DataCategory findByUnitIdAndName(Long unitId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, DataCategory.class);
    }

    @Override
    public DataCategoryResponseDTO getDataCategoryWithDataElementById(Long countryId, BigInteger id) {

        String projection = CustomAggregationQuery.dataCategoryWithDataElementProjectionData();
        Document projectionOperation = Document.parse(projection);
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false)),
                lookup("dataElement", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation)
        );


        AggregationResults<DataCategoryResponseDTO> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<DataCategoryResponseDTO> getAllDataCategoryWithDataElement(Long countryId) {

        String projection = CustomAggregationQuery.dataCategoryWithDataElementProjectionData();
        Document projectionOperation = Document.parse(projection);

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("dataElement", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation),
                sort(Sort.Direction.DESC,"createdAt")

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
                lookup("dataElement", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation),
                sort(Sort.Direction.DESC,"createdAt")

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
                lookup("dataElement", "dataElements", "_id", "dataElements"),
                new CustomAggregationOperation(projectionOperation)
        );


        AggregationResults<DataCategoryResponseDTO> result = mongoTemplate.aggregate(aggregation, DataCategory.class, DataCategoryResponseDTO.class);
        return result.getUniqueMappedResult();
    }*/
}
