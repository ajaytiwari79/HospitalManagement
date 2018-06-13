package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateQueryResult;
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
    public List<MasterQuestionnaireTemplateQueryResult> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId) {


        String addFieldQuestion=CustomAggregationQuery.questionnnaireTemplateAddNonDeletedQuestions();
        Document addFieldsQuestionOperation=Document.parse(addFieldQuestion);

        String addFieldAssetType=CustomAggregationQuery.questionnnaireTemplateAddNonDeletedAssetType();
        Document addFieldsAssetTypeOperation=Document.parse(addFieldQuestion);

        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("questionnaire_section","sections","_id","sections"),
                lookup("storage_type","assetType","_id","assetType"),
                unwind("sections",true),
                lookup("question","sections.questions","_id","questions"),
                new CustomAggregationOperation(addFieldsQuestionOperation),
                new CustomAggregationOperation(addFieldsAssetTypeOperation),
                unwind("questions",true),
                group("$id")
                .first("name").as("name")
                        .first("description").as("description")
                        .first("templateType").as("templateType")
                        .first("assetType").as("assetType")
                        .first("countryId").as("countryId")
                .addToSet("sections").as("sections")
                .addToSet("questions").as("questions")




        );
        AggregationResults<MasterQuestionnaireTemplateQueryResult> result=mongoTemplate.aggregate(aggregation,MasterQuestionnaireTemplate.class,MasterQuestionnaireTemplateQueryResult.class);
        return result.getMappedResults();
    }

    @Override
    public MasterQuestionnaireTemplateQueryResult getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, BigInteger id) {

        String addFieldQuestion=CustomAggregationQuery.questionnnaireTemplateAddNonDeletedQuestions();
        Document addFieldsQuestionOperation=Document.parse(addFieldQuestion);

        String addFieldAssetType=CustomAggregationQuery.questionnnaireTemplateAddNonDeletedAssetType();
        Document addFieldsAssetTypeOperation=Document.parse(addFieldQuestion);

        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id)),
                lookup("questionnaire_section","sections","_id","sections"),
                lookup("storage_type","assetType","_id","assetType"),
                unwind("sections",true),
                lookup("question","sections.questions","_id","questions"),
                new CustomAggregationOperation(addFieldsQuestionOperation),
                new CustomAggregationOperation(addFieldsAssetTypeOperation),
                unwind("questions",true),
                group("$id")
                        .first("name").as("name")
                        .first("description").as("description")
                        .first("templateType").as("templateType")
                        .first("assetType").as("assetType")
                        .first("countryId").as("countryId")
                        .addToSet("sections").as("sections")
                        .addToSet("questions").as("questions")




        );
        AggregationResults<MasterQuestionnaireTemplateQueryResult> result=mongoTemplate.aggregate(aggregation,MasterQuestionnaireTemplate.class,MasterQuestionnaireTemplateQueryResult.class);
        return result.getUniqueMappedResult();
    }
}
