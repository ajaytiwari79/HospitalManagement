package com.kairos.persistence.repository.questionnaire_template;

public class QuestionnaireTemplateMongoRepositoryImpl {

    /*@Inject
    private MongoTemplate mongoTemplate;


    final String addFieldSections = CustomAggregationQuery.questionnaireTemplateAddNonDeletedSections();
    final String addFieldQuestions = CustomAggregationQuery.questionnaireTemplateAddNonDeletedQuestions();
    final String groupData = CustomAggregationQuery.questionnaireTemplateGroupOperation();
    final String projection = CustomAggregationQuery.questionnaireTemplateProjectionBeforeGroupOperationForAssetType();

    Document questionsAddFieldOperation = Document.parse(addFieldQuestions);
    Document sectionsAddFieldOperation = Document.parse(addFieldSections);
    Document projectionOperation = Document.parse(projection);
    Document groupDataOperation = Document.parse(groupData);


    @Override
    public QuestionnaireTemplate findByNameAndCountryId(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);

    }

    @Override
    public QuestionnaireTemplate findByNameAndUnitId(Long unitId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }

    @Override
    public List<QuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(Long countryId) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("questionnaireSection", "sections", "_id", "sections"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation),
                sort(Sort.Direction.DESC, "createdAt")


                );
        AggregationResults<QuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, QuestionnaireTemplate.class, QuestionnaireTemplateResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public QuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, BigInteger id) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id)),
                lookup("questionnaireSection", "sections", "_id", "sections"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation)
        );


        AggregationResults<QuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, QuestionnaireTemplate.class, QuestionnaireTemplateResponseDTO.class);
        return result.getUniqueMappedResult();
    }


    @Override
    public QuestionnaireTemplate getQuestionnaireTemplateByTemplateTypeAndUnitId(Long unitId, QuestionnaireTemplateType templateType) {
        Query query = new Query(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(unitId).and("templateType").is(templateType));
        query.fields().include("id").include("name").include("templateStatus").include("templateType");
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }


    @Override
    public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByUnitId(Long unitId, BigInteger templateId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("_id").is(templateId)),
                lookup("questionnaireSection", "sections", "_id", "sections"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation)
        );
        AggregationResults<QuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, QuestionnaireTemplate.class, QuestionnaireTemplateResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<QuestionnaireTemplateResponseDTO> getAllQuestionnaireTemplateWithSectionsAndQuestionsByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false)),
                lookup("questionnaireSection", "sections", "_id", "sections"),
                lookup("assetType", "assetTypeId", "_id", "assetType"),
                lookup("assetType", "assetSubTypeId", "_id", "assetSubType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation),
                sort(Sort.Direction.DESC, "createdAt")

                );
        AggregationResults<QuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, QuestionnaireTemplate.class, QuestionnaireTemplateResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeId(Long countryId, BigInteger assetTypeId) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId)
                .and("templateType").is(QuestionnaireTemplateType.RISK)
                .and("riskAssociatedEntity").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and(DELETED).is(false)
                .and("assetTypeId").is(assetTypeId));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }


    @Override
    public QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId(Long countryId, BigInteger assetTypeId, BigInteger subAssetTypeId) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId)
                .and("templateType").is(QuestionnaireTemplateType.RISK)
                .and("riskAssociatedEntity").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and(DELETED).is(false)
                .and("assetTypeId").is(assetTypeId)
                .and("assetSubTypeId").is(subAssetTypeId));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }


    @Override
    public QuestionnaireTemplate findRiskTemplateByAssociatedProcessingActivityAndCountryId(Long countryId) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId)
                .and("templateType").is(QuestionnaireTemplateType.RISK)
                .and("riskAssociatedEntity").is(QuestionnaireTemplateType.PROCESSING_ACTIVITY)
                .and(DELETED).is(false));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }

    @Override
    public QuestionnaireTemplate getDefaultPublishedAssetQuestionnaireTemplateByUnitId(Long unitId) {

        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and("templateType").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and("defaultAssetTemplate").is(true)
                .and(DELETED).is(false)
                .and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }

    @Override
    public QuestionnaireTemplate findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(Long unitId, BigInteger assetTypeId, BigInteger subAssetTypeId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and("templateType").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and("assetTypeId").is(assetTypeId).and("assetSubTypeId").in(subAssetTypeId)
                .and(DELETED).is(false)
                .and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }

    @Override
    public QuestionnaireTemplate findQuestionnaireTemplateByUnitIdAssetTypeIdAndTemplateStatus(Long unitId, BigInteger assetTypeId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and("templateType").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and("assetTypeId").is(assetTypeId)
                .and("assetSubTypeId").exists(false)
                .and(DELETED).is(false)
                .and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }

    @Override
    public QuestionnaireTemplate findPublishedQuestionnaireTemplateByUnitIdAndTemplateType(Long unitId, QuestionnaireTemplateType templateType) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and("templateType").is(templateType)
                .and(DELETED).is(false)
                .and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }


    @Override
    public QuestionnaireTemplate findPublishedRiskTemplateByAssociatedProcessingActivityAndUnitId(Long unitId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and("templateType").is(QuestionnaireTemplateType.RISK)
                .and("riskAssociatedEntity").is(QuestionnaireTemplateType.PROCESSING_ACTIVITY)
                .and(DELETED).is(false).and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }


    @Override
    public QuestionnaireTemplate findPublishedRiskTemplateByUnitIdAndAssetTypeId(Long unitId, BigInteger assetTypeId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and("templateType").is(QuestionnaireTemplateType.RISK)
                .and("riskAssociatedEntity").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and(DELETED).is(false)
                .and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED)
                .and("assetTypeId").is(assetTypeId));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }

    @Override
    public QuestionnaireTemplate findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId(Long unitId, BigInteger assetTypeId, BigInteger assetSubTypeId) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId)
                .and("templateType").is(QuestionnaireTemplateType.RISK)
                .and("riskAssociatedEntity").is(QuestionnaireTemplateType.ASSET_TYPE)
                .and(DELETED).is(false)
                .and("templateStatus").is(QuestionnaireTemplateStatus.PUBLISHED)
                .and("assetTypeId").is(assetTypeId)
                .and("assetSubTypeId").is(assetSubTypeId));
        return mongoTemplate.findOne(query, QuestionnaireTemplate.class);
    }*/
}
