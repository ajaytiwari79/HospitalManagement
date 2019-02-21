package com.kairos.persistence.repository.master_data.data_category_element;

class DataElementMongoRepositoryImpl {


   /* @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<DataElement> findByCountryIdAndNames(Long countryId, Set<String> names) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").in(names).and(COUNTRY_ID).is(countryId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, DataElement.class);
    }

    @Override
    public List<DataElement> findByUnitIdAndNames(Long unitId, Set<String> names) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").in(names).and(ORGANIZATION_ID).is(unitId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, DataElement.class);

    }*/
}
