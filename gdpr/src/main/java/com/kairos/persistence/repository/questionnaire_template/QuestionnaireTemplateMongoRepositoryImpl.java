package com.kairos.persistence.repository.questionnaire_template;

import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;


import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

public class QuestionnaireTemplateMongoRepositoryImpl implements CustomQuestionnaireTemplateRepository {

    @Inject
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
                lookup("assetType", "assetType", "_id", "assetType"),
                lookup("assetType","assetSubType","_id","assetSubType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation)

        );
        AggregationResults<QuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, QuestionnaireTemplate.class, QuestionnaireTemplateResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public QuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, BigInteger id) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id)),
                lookup("questionnaireSection", "sections", "_id", "sections"),
                lookup("assetType", "assetType", "_id", "assetType"),
                lookup("assetType","assetSubType","_id","assetSubType"),
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


    public QuestionnaireTemplate getQuestionnaireTemplateByTemplateTypeByUnitId(Long unitId, QuestionnaireTemplateType templateType) {
        Query query = new Query(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(unitId).and("templateType").is(templateType));
        query.fields().include("id").include("name");
        return mongoTemplate.findOne(query,QuestionnaireTemplate.class);
    }



    @Override
    public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByUnitId(Long unitId, BigInteger templateId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("_id").is(templateId)),
                lookup("questionnaireSection", "sections", "_id", "sections"),
                lookup("assetType", "assetType", "_id", "assetType"),
                lookup("assetType","assetSubType","_id","assetSubType"),
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
                lookup("assetType", "assetType", "_id", "assetType"),
                lookup("assetType","assetSubType","_id","assetSubType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                sort(Sort.Direction.DESC, "createdAt"),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation)

        );
        AggregationResults<QuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, QuestionnaireTemplate.class, QuestionnaireTemplateResponseDTO.class);
        return result.getMappedResults();
    }
}
