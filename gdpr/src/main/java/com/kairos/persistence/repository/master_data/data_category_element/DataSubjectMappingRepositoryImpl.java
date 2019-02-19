package com.kairos.persistence.repository.master_data.data_category_element;


class DataSubjectMappingRepositoryImpl  {

   /* @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public DataSubjectMapping findByName(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, DataSubjectMapping.class);


    }

    @Override
    public DataSubjectMapping findByNameAndUnitId(Long unitId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").is(name).and(ORGANIZATION_ID).is(unitId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, DataSubjectMapping.class);


    }


    @Override
    public List<DataSubjectMapping> findByNameListAndUnitId(Long unitId, Set<String> names) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").in(names).and(ORGANIZATION_ID).is(unitId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, DataSubjectMapping.class);

    }

    @Override
    public DataSubjectMappingResponseDTO getDataSubjectWithDataCategoryAndDataElementByCountryIdAndId(Long countryId, BigInteger dataSubjectId) {

        String addFields = CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter = Document.parse(addFields);
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(dataSubjectId).and(DELETED).is(false)),
                lookup("dataCategory", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("dataElement", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
                new CustomAggregationOperation(addToFieldOperationFilter),
                match(Criteria.where("dataCategories.deleted").is(false)),
                group("$id")
                        .first("organizationTypes").as("organizationTypes")
                        .first("organizationSubTypes").as("organizationSubTypes")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataCategories").as("dataCategories")

        );

        AggregationResults<DataSubjectMappingResponseDTO> result = mongoTemplate.aggregate(aggregation, DataSubjectMapping.class, DataSubjectMappingResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<DataSubjectMappingResponseDTO> getAllDataSubjectWithDataCategoryAndDataElementByCountryId(Long countryId) {

        String addFields = CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter = Document.parse(addFields);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("dataCategory", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("dataElement", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
                new CustomAggregationOperation(addToFieldOperationFilter),
                match(Criteria.where("dataCategories.deleted").is(false)),
                group("$id")
                        .first("organizationTypes").as("organizationTypes")
                        .first("organizationSubTypes").as("organizationSubTypes")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first("createdAt").as("createdAt")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataCategories").as("dataCategories"),
                sort(Sort.Direction.DESC, "createdAt")

        );
        AggregationResults<DataSubjectMappingResponseDTO> result = mongoTemplate.aggregate(aggregation, DataSubjectMapping.class, DataSubjectMappingResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<DataSubjectMappingResponseDTO> getAllDataSubjectWithDataCategoryAndDataElementByUnitId(Long unitId) {
        String addFields = CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter = Document.parse(addFields);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(unitId)),
                lookup("dataCategory", "dataCategories", "_id", "dataCategories"),
                unwind("dataCategories"),
                lookup("dataElement", "dataCategories.dataElements", "_id", "dataCategories.dataElements"),
                new CustomAggregationOperation(addToFieldOperationFilter),
                match(Criteria.where("dataCategories.deleted").is(false)),
                sort(Sort.Direction.DESC, "createdAt"),
                group("$id")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first("createdAt").as("createdAt")
                        .first(COUNTRY_ID).as(COUNTRY_ID)
                        .addToSet("dataCategories").as("dataCategories"),
                sort(Sort.Direction.DESC, "createdAt")

        );
        AggregationResults<DataSubjectMappingResponseDTO> result = mongoTemplate.aggregate(aggregation, DataSubjectMapping.class, DataSubjectMappingResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public DataSubjectMappingResponseDTO getDataSubjectWithDataCategoryAndDataElementByUnitIdAndId(Long unitId, BigInteger dataSubjectId) {
        String addFields = CustomAggregationQuery.dataSubjectAddNonDeletedDataElementAddFields();
        Document addToFieldOperationFilter = Document.parse(addFields);
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where("_id").is(dataSubjectId).and(DELETED).is(false).and(ORGANIZATION_ID).is(unitId)),
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
        return result.getUniqueMappedResult();
    }
*/

}
