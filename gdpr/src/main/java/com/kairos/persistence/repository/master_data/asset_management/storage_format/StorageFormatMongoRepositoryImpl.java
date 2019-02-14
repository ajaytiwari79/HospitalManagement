package com.kairos.persistence.repository.master_data.asset_management.storage_format;

class StorageFormatMongoRepositoryImpl  {


   /* @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<StorageFormatResponseDTO> getAllNotInheritedStorageFormatFromParentOrgAndUnitStorageFormat(Long countryId, Long parentOrganizationId, Long organizationId) {
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
                new CustomAggregationOperation(projectionOperation)
               ,new CustomAggregationOperation(replaceRootOperation)

        );

        AggregationResults<StorageFormatResponseDTO> results = mongoTemplate.aggregate(aggregation, StorageFormat.class, StorageFormatResponseDTO.class);
        return results.getMappedResults();
    }*/
}
