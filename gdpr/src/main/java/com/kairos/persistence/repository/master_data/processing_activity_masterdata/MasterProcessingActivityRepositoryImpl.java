package com.kairos.persistence.repository.master_data.processing_activity_masterdata;


public class MasterProcessingActivityRepositoryImpl {


    /*@Inject
    private MongoTemplate mongoTemplate;


    Document projectionOperation = Document.parse(CustomAggregationQuery.processingActivityWithSubProcessingNonDeletedData());


    @Override
    public MasterProcessingActivity findByName(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name).and("subProcess").is(false));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, MasterProcessingActivity.class);


    }

    @Override
    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId, BigInteger id) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("masterProcessingActivity", "subProcessingActivityIds", "_id", "subProcessingActivities")
                , new CustomAggregationOperation(projectionOperation)
        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("masterProcessingActivity", "subProcessingActivityIds", "_id", "subProcessingActivities"),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(projectionOperation)
        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityWithFilterSelection(Long countryId, FilterSelectionDTO filterSelectionDto) {


        Criteria criteria = Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false);
        List<Criteria> processingActivityCriteriaList = new ArrayList<>(filterSelectionDto.getFiltersData().size());
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size() != 0) {
                processingActivityCriteriaList.add(buildMatchCriteria(filterSelection, filterSelection.getName()));
            }
        });

        if (!processingActivityCriteriaList.isEmpty()) {
            criteria = criteria.andOperator(processingActivityCriteriaList.toArray(new Criteria[processingActivityCriteriaList.size()]));

        }
        Aggregation aggregation = Aggregation.newAggregation(

                match(criteria),
                lookup("masterProcessingActivity", "subProcessingActivityIds", "_id", "subProcessingActivities"),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(projectionOperation)


        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getMappedResults();


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
                throw new InvalidRequestException("data not found for FilterType " + filterType);


        }
    }


    @Override
    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityByOrgTypeSubTypeCategoryAndSubCategory(Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("organizationTypes._id").in(organizationMetaDataDTO.getOrganizationTypeId())
                        .and("organizationSubTypes._id").in(organizationMetaDataDTO.getOrganizationSubTypeIds()).and(("organizationServices._id")).in(organizationMetaDataDTO.getServiceCategoryIds())
                        .and("organizationSubServices._id").in(organizationMetaDataDTO.getSubServiceCategoryIds())),
                lookup("masterProcessingActivity", "subProcessingActivityIds", "_id", "subProcessingActivities")


                );

        return mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class).getMappedResults();
    }


    @Override
    public List<MasterProcessingActivityRiskResponseDTO> getAllProcessingActivityWithLinkedRisksAndSubProcessingActivitiesByCountryId(Long countryId) {

        String addNonDeletedRisks = "{'$addFields':{'risks':{'$filter':{ 'input':'$risks', 'as':'risk','cond':{'$eq':['$$risk.deleted',false]} }} }}";
        String groupSubProcessingActivities = "{'$group':{'_id':'$_id','processingActivities':{'$addToSet':'$subProcessingActivities'},'name':{'$first':'$name'},'risks':{'$first':'$risks'}}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("risk", "risks", "_id", "risks"),
                lookup("masterProcessingActivity", "subProcessingActivityIds", "_id", "subProcessingActivities"),
                unwind("subProcessingActivities", true),
                lookup("risk", "subProcessingActivities.risks", "_id", "subProcessingActivities.risks"),
                new CustomAggregationOperation(Document.parse(groupSubProcessingActivities)),
                sort(Sort.Direction.DESC, "id"),
                new CustomAggregationOperation(Document.parse(addNonDeletedRisks))
        );
        AggregationResults<MasterProcessingActivityRiskResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityRiskResponseDTO.class);
        return result.getMappedResults();
    }
*/

}
