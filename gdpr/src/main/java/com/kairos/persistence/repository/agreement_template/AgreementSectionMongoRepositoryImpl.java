package com.kairos.persistence.repository.agreement_template;

class AgreementSectionMongoRepositoryImpl {


    /*@Inject
    private MongoTemplate mongoTemplate;


    @Override
    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(id).and(DELETED).is(false).and(COUNTRY_ID).is(countryId)),
                lookup("clause", "clauseIds", "_id", "clauses")
        );
        AggregationResults<AgreementSectionResponseDTO> response = mongoTemplate.aggregate(aggregation, AgreementSection.class, AgreementSectionResponseDTO.class);
        return response.getUniqueMappedResult();

    }*/

/*
    @Override
    public Set<BigInteger> getClauseIdListPresentInAgreementSectionAndSubSectionsByCountryIdAndClauseIds(Long countryId, Set<BigInteger> clauseIds) {

        String projectionOperation = "{'$project':{'_id':0 ,'clauseIdOrderedIndex':1}}";
        String groupOperation = "{ '$group' : { '_id' : '$_id' , 'clauseIdOrderedIndex':{ '$addToSet' : '$clauseIdOrderedIndex'}}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(COUNTRY_ID).is(countryId).and("clauseIdOrderedIndex").in(clauseIds))
                , new CustomAggregationOperation(Document.parse(projectionOperation)),
                unwind("clauseIdOrderedIndex"),
                new CustomAggregationOperation(Document.parse(groupOperation))
        );
        AggregationResults<Map> response = mongoTemplate.aggregate(aggregation, AgreementSection.class, Map.class);
        Set<BigInteger> clauseIdLIst = new HashSet<>();
        Set<BigInteger> clauseIdListPresentInSection = new HashSet<>((ArrayList<BigInteger>) response.getUniqueMappedResult().get("clauseIdOrderedIndex"));
        clauseIds.forEach(clauseId -> {
            if (clauseIdListPresentInSection.contains(clauseId)) clauseIdLIst.add(clauseId);
        });
        return clauseIdLIst;
    }*/
}
