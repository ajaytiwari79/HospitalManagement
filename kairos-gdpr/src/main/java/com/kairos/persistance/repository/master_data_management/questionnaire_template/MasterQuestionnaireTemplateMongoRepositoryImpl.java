package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;


import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

public class MasterQuestionnaireTemplateMongoRepositoryImpl implements CustomQuestionnaireTemplateRepository {

    @Inject
    private MongoTemplate mongoTemplate;


    final String addFieldSections = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedSections();
    final String addFieldQuestions = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedQuestions();
    final String addFieldAssetType = CustomAggregationQuery.questionnnaireTemplateAddNonDeletedAssetType();
    final String groupData = CustomAggregationQuery.questionnnaireTemplateGroupOperation();
    final String projection = CustomAggregationQuery.questionnnaireTemplateProjectionBeforeGroupOperationForAssetType();

    Document assetTypeAddFieldOperation = Document.parse(addFieldAssetType);
    Document questionsAddFieldOperation = Document.parse(addFieldQuestions);
    Document sectionsAddFieldOperation = Document.parse(addFieldSections);
    Document projectionOperation = Document.parse(projection);
    Document groupDataOperation = Document.parse(groupData);


    @Override
    public List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId) {


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


        AggregationResults<MasterQuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterQuestionnaireTemplate.class, MasterQuestionnaireTemplateResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, BigInteger id) {

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


        AggregationResults<MasterQuestionnaireTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterQuestionnaireTemplate.class, MasterQuestionnaireTemplateResponseDTO.class);
        return result.getUniqueMappedResult();
    }
}
