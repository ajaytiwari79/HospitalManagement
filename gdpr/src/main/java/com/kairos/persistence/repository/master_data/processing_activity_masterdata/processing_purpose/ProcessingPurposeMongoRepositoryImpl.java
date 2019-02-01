package com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose;

public class ProcessingPurposeMongoRepositoryImpl  {


    /*@Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<ProcessingPurposeResponseDTO> getAllNotInheritedProcessingPurposesFromParentOrgAndUnitProcessingPurpose(Long countryId, Long parentOrganizationId, Long organizationId) {

            Document groupOperationForDuplicateDataOnInheritingFromParentOrg = Document.parse(CustomAggregationQuery.metaDataGroupInheritParentOrgMetaDataAndOrganizationMetadata());
            Document projectionForFilteringDuplicateDataOfOrgAndParentOrg = Document.parse(CustomAggregationQuery.metaDataProjectionForRemovingDuplicateInheritedMetaData(organizationId));
            Document projectionOperation = Document.parse(CustomAggregationQuery.metaDataProjectionForAddingFinalDataObject());
            Document replaceRootOperation = Document.parse(CustomAggregationQuery.metaDataReplaceRoot());


            List<Long> orgIdList = new ArrayList<>();
            orgIdList.add(organizationId);orgIdList.add(parentOrganizationId);

            Aggregation aggregation = Aggregation.newAggregation(
                    match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).in(orgIdList)),
                    new CustomAggregationOperation(groupOperationForDuplicateDataOnInheritingFromParentOrg),
                    new CustomAggregationOperation(projectionForFilteringDuplicateDataOfOrgAndParentOrg),
                    new CustomAggregationOperation(projectionOperation),
                    new CustomAggregationOperation(replaceRootOperation)
            );

            AggregationResults<ProcessingPurposeResponseDTO> results = mongoTemplate.aggregate(aggregation, ProcessingPurpose.class, ProcessingPurposeResponseDTO.class);
            return results.getMappedResults();

    }*/
}
