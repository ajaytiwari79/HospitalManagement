package com.kairos.persistence.repository.master_data.asset_management.data_disposal;

class DataDisposalMongoRepositoryImpl  {

    /*@Inject
    private MongoTemplate mongoTemplate;


    @Override
    public List<DataDisposalResponseDTO> getAllNotInheritedDataDisposalFromParentOrgAndUnitDataDisposal(Long countryId, Long parentOrganizationId, Long organizationId) {
        Document groupOperationForDuplicateDataOnInheritingFromParentOrg = Document.parse(CustomAggregationQuery.metaDataGroupInheritParentOrgMetaDataAndOrganizationMetadata());
        Document projectionForFilteringDuplicateDataOfOrgAndParentOrg = Document.parse(CustomAggregationQuery.metaDataProjectionForRemovingDuplicateInheritedMetaData(organizationId));
        Document projectionOperation = Document.parse(CustomAggregationQuery.metaDataProjectionForAddingFinalDataObject());
        Document replaceRootOperation = Document.parse(CustomAggregationQuery.metaDataReplaceRoot());


        List<Long> orgIdList = new ArrayList<>();
        orgIdList.add(organizationId);
        orgIdList.add(parentOrganizationId);

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).in(orgIdList)),
                new CustomAggregationOperation(groupOperationForDuplicateDataOnInheritingFromParentOrg),
                new CustomAggregationOperation(projectionForFilteringDuplicateDataOfOrgAndParentOrg),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(replaceRootOperation)


                );

        AggregationResults<DataDisposalResponseDTO> results = mongoTemplate.aggregate(aggregation,DataDisposal.class, DataDisposalResponseDTO.class);
        return results.getMappedResults();
    }*/
}
