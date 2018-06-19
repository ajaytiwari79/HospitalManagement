package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateQueryResult;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDto;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constant.AppConstant.COUNTRY_ID;
import static com.kairos.constant.AppConstant.DELETED;


import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

public class MasterQuestionnaireTemplateMongoRepositoryImpl implements CustomQuestionnaireTemplateRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<MasterQuestionnaireTemplateResponseDto> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId) {

        String addFieldSections = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedSections();
        String addFieldQuestions = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedQuestions();
        String addFieldAssetType = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedAssetType();
        String groupData = CustomAggregationQuery.questionnnaireTemplateGroupOperation();
        String projection = CustomAggregationQuery.questionnnaireTemplateProjectionBeforeGroupOperationForAssetType();

        Document assetTypeAddFieldOperation = Document.parse(addFieldAssetType);
        Document questionsAddFieldOperation = Document.parse(addFieldQuestions);
        Document sectionsAddFieldOperation = Document.parse(addFieldSections);
        Document projectionOperation = Document.parse(projection);
        Document groupDataOperation = Document.parse(groupData);


        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("questionnaire_section", "sections", "_id", "sections"),
                lookup("asset_type", "assetType", "_id", "assetType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                new CustomAggregationOperation(assetTypeAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation)
        );


        AggregationResults<MasterQuestionnaireTemplateResponseDto> result = mongoTemplate.aggregate(aggregation, MasterQuestionnaireTemplate.class, MasterQuestionnaireTemplateResponseDto.class);
        return result.getMappedResults();
    }

    @Override
    public MasterQuestionnaireTemplateResponseDto getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, BigInteger id) {



        String addFieldSections = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedSections();
        String addFieldQuestions = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedQuestions();
        String addFieldAssetType = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedAssetType();
        String projection = CustomAggregationQuery.questionnnaireTemplateProjectionBeforeGroupOperationForAssetType();
        String groupData = CustomAggregationQuery.questionnnaireTemplateGroupOperation();

        Document assetTypeAddFieldOperation = Document.parse(addFieldAssetType);
        Document questionsAddFieldOperation = Document.parse(addFieldQuestions);
        Document sectionsAddFieldOperation = Document.parse(addFieldSections);
        Document projectionOperation = Document.parse(projection);
        Document groupDataOperation = Document.parse(groupData);


        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id)),
                lookup("questionnaire_section", "sections", "_id", "sections"),
                lookup("asset_type", "assetType", "_id", "assetType"),
                new CustomAggregationOperation(sectionsAddFieldOperation),
                new CustomAggregationOperation(assetTypeAddFieldOperation),
                unwind("sections", true),
                lookup("question", "sections.questions", "_id", "questions"),
                new CustomAggregationOperation(questionsAddFieldOperation),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(groupDataOperation)
        );


        AggregationResults<MasterQuestionnaireTemplateResponseDto> result = mongoTemplate.aggregate(aggregation, MasterQuestionnaireTemplate.class, MasterQuestionnaireTemplateResponseDto.class);
        return result.getUniqueMappedResult();
    }
}
