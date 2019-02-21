package com.kairos.persistence.repository.data_inventory.Assessment;

class AssessmentMongoRepositoryImpl  {


    /*@Inject
    private MongoTemplate mongoTemplate;

    List<AssessmentStatus> assessmentStatusList = Arrays.asList(AssessmentStatus.NEW, AssessmentStatus.IN_PROGRESS);


    @Override
    public Assessment findAssessmentByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Assessment.class);
    }

    @Override
    public List<AssessmentBasicResponseDTO> getAllAssessmentByUnitIdAndStaffId(Long unitId, Long staffId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("assessmentStatus").in(assessmentStatusList).and("assigneeList._id").is(staffId)),
                sort(Sort.Direction.DESC,"createdAt")

        );
        AggregationResults<AssessmentBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentBasicResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<AssessmentResponseDTO> getAllAssessmentByUnitId(Long unitId) {

        String projectionOperation = "{ '$project':{'asset':{$arrayElemAt:['$asset',0]},'processingActivity':{'$arrayElemAt':['$processingActivity',0]}," +
                "'_id':1,'name':1,'endDate':1,'completedDate':1,'comment':1,'assigneeList':1,'approver':1,'createdAt':1,'assessmentStatus':1 ,assessmentLaunchedDate:1,startDate:1,assessmentSchedulingFrequency:1, 'risks':{'_id':1,'name':1}}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false)),
                lookup("asset", "assetId", "_id", "asset"),
                lookup("processingActivity", "processingActivityId", "_id", "processingActivity"),
                lookup("risk","riskIds","_id","risks"),
                new CustomAggregationOperation(Document.parse(projectionOperation)),
                sort(Sort.Direction.DESC,"createdAt")

        );
        AggregationResults<AssessmentResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public Assessment findPreviousLaunchedAssessmentByUnitIdAndAssetId(Long unitId, BigInteger assetId) {

        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and(DELETED).is(false)
                .and("assetId").is(assetId)
                .and("assessmentStatus").in(assessmentStatusList)
                .and("riskAssessment").is(false));
        return mongoTemplate.findOne(query, Assessment.class);
    }

    @Override
    public Assessment findPreviousLaunchedAssessmentByUnitIdAndProcessingActivityId(Long unitId, BigInteger processingActivityId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and(DELETED).is(false)
                .and("processingActivityId").is(processingActivityId)
                .and("assessmentStatus").in(assessmentStatusList)
                .and("riskAssessment").is(false));
        return mongoTemplate.findOne(query, Assessment.class);
    }


    @Override
    public List<Assessment> getAssessmentLinkedWithQuestionnaireTemplateByTemplateIdAndUnitId(Long unitId, BigInteger templateId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and(DELETED).is(false)
                .and("questionnaireTemplateId").is(templateId)
                .and("assessmentStatus").in(assessmentStatusList));
        return mongoTemplate.find(query, Assessment.class);
    }


    @Override
    public Assessment findPreviousLaunchedRiskAssessmentByUnitIdAndAssetId(Long unitId, BigInteger assetId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and(DELETED).is(false)
                .and("assetId").is(assetId)
                .and("assessmentStatus").in(assessmentStatusList)
                .and("riskAssessment").is(true));
        return mongoTemplate.findOne(query, Assessment.class);
    }

    @Override
    public Assessment findPreviousLaunchedRiskAssessmentByUnitIdAndProcessingActivityId(Long unitId, BigInteger processingActivityId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and(DELETED).is(false)
                .and("processingActivityId").is(processingActivityId)
                .and("assessmentStatus").in(assessmentStatusList)
                .and("riskAssessment").is(true));
        return mongoTemplate.findOne(query, Assessment.class);
    }


    @Override
    public List<AssessmentBasicResponseDTO> findAllAssessmentLaunchedForAssetByAssetIdAndUnitId(Long unitId, BigInteger assetId) {

        String projectionOperation = "{ '$project':{'_id':1,'name':1,'assessmentLaunchedDate':1,'assessmentSchedulingFrequency':1,'endDate':1, startDate:1,'completedDate':1,'comment':1,'assigneeList':1,'approver':1,'createdAt':1,'assessmentStatus':1 , 'risks':{'_id':1,'name':1}}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("assetId").is(assetId)),
                lookup("risk","riskIds","_id","risks"),
                new CustomAggregationOperation(Document.parse(projectionOperation)),
                sort(Sort.Direction.DESC,"createdAt")

        );
        AggregationResults<AssessmentBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentBasicResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<AssessmentBasicResponseDTO> findAllAssessmentLaunchedForProcessingActivityByActivityIdAndUnitId(Long unitId, BigInteger processingActivityId) {

        String projectionOperation = "{ '$project':{'_id':1,'name':1,'endDate':1,'completedDate':1,'comment':1,'assigneeList':1,startDate:1,'approver':1,'createdAt':1,'assessmentLaunchedDate' : 1, 'assessmentSchedulingFrequency' : 1,'assessmentStatus':1, 'risks':{'_id':1,'name':1}}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("processingActivityId").is(processingActivityId)),
                lookup("risk","riskIds","_id","risks"),
                new CustomAggregationOperation(Document.parse(projectionOperation)),
                sort(Sort.Direction.DESC,"createdAt")

        );
        AggregationResults<AssessmentBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, Assessment.class, AssessmentBasicResponseDTO.class);
        return result.getMappedResults();
    }*/
}
