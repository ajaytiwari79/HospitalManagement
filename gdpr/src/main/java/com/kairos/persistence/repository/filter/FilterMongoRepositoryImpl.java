package com.kairos.persistence.repository.filter;


class FilterMongoRepositoryImpl implements CustomFilterMongoRepository {


    /*@Inject
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, AggregationOperation> getDefaultFilters(Long countryId,  List<FilterType> filterTypes, FilterGroup filterGroup) {
        Map<String, AggregationOperation> aggregationOperations = new HashMap<>();
        if (filterGroup.getAccessModule().get(0).getModuleId().equals(MASTER_PROCESSING_ACTIVITY_MODULE_ID)) {
            aggregationOperations.put("match", match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false)));

        } else {
            aggregationOperations.put("match", match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)));
        }
        filterTypes.forEach(filterType -> buildAggregationQuery(filterType,aggregationOperations)

        );

        return aggregationOperations;
    }


    *//*accountTypes ,organizationServices ,organizationSubServices ,organizationSubTypes ,organizationTypes    are fields in domain (clause ,asset ,master processing activity)
      ACCOUNT_TYPES ,ORGANIZATION_SERVICES,ORGANIZATION_SUB_SERVICES ,ORGANIZATION_TYPES,ORGANIZATION_SUB_TYPES etc represent field name in domains(Clause,MasterAsset and Master Processing activity)
      @param filterType
     * @return
     *//*
    @Override
    public void buildAggregationQuery(FilterType filterType, Map<String, AggregationOperation> aggregationOperations ) {
        switch (filterType) {

            case ACCOUNT_TYPES:
                aggregationOperations.put("accountTypes",Aggregation.unwind("accountTypes"));
                break;
            case ORGANIZATION_SERVICES:
                aggregationOperations.put("organizationServices",Aggregation.unwind("organizationServices"));
                break;
            case ORGANIZATION_SUB_SERVICES:
                aggregationOperations.put("organizationSubServices",Aggregation.unwind("organizationSubServices"));
                break;
            case ORGANIZATION_TYPES:
                aggregationOperations.put("organizationTypes",Aggregation.unwind("organizationTypes"));
                break;
            case ORGANIZATION_SUB_TYPES:
                aggregationOperations.put("organizationSubTypes",Aggregation.unwind("organizationSubTypes"));
                break;
            default:
                throw new InvalidRequestException("invalid request");
        }


    }

    @Override
    public Aggregation createAggregationQueryForFilterCategory(Map<String, AggregationOperation> aggregationOperations) {
        GroupOperation groupOperation = group();
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(aggregationOperations.get("match"));
        for (Map.Entry<String, AggregationOperation> entry : aggregationOperations.entrySet())
            if (entry.getKey().equals("match")) {
                continue;
            } else {
                operations.add(entry.getValue());
                groupOperation = groupOperation.addToSet(entry.getKey()).as(entry.getKey());
            }
        operations.add(groupOperation);
        return Aggregation.newAggregation(operations);

    }

    @Override
    public AggregationResults<FilterCategoryResult> getFilterAggregationResult(Aggregation aggregation, FilterGroup filterGroup, String moduleId) {

        List<ModuleIdDTO> moduleIdDto = filterGroup.getAccessModule();
        String domainName = null;
        for (ModuleIdDTO moduleIdDto1 : moduleIdDto) {
            if (moduleIdDto1.getModuleId().equalsIgnoreCase(moduleId)) {
                domainName = moduleIdDto1.getName();
                break;
            }
        }
        if (StringUtils.isBlank(domainName)) {
            throw new InvalidRequestException("module name is null");
        }
        switch (domainName) {
            case CLAUSE_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation, Clause.class, FilterCategoryResult.class);
            case ASSET_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation, MasterAsset.class, FilterCategoryResult.class);
            case MASTER_PROCESSING_ACTIVITY_MODULE_NAME:
                return mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, FilterCategoryResult.class);
            default:
                throw new DataNotFoundByIdException("data not found by moduleId" + moduleId);

        }
    }*/
}
