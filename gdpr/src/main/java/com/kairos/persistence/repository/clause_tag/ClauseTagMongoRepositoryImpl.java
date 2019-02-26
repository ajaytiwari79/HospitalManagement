package com.kairos.persistence.repository.clause_tag;

class ClauseTagMongoRepositoryImpl {


    /*@Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<ClauseTag> findByCountryIdAndTitles(Long countryId, Set<String> titles) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and("deleted").is(false).and("name").in(titles));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, ClauseTag.class);
    }

    @Override
    public List<ClauseTag> findByUnitIdAndTitles(Long unitId, Set<String> titles) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(unitId).and("deleted").is(false).and("name").in(titles));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, ClauseTag.class);
    }*/
}
