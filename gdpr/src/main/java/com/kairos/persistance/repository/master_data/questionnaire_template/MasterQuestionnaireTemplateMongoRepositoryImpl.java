package com.kairos.persistance.repository.master_data.questionnaire_template;

import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;


import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MasterQuestionnaireTemplateMongoRepositoryImpl implements CustomQuestionnaireTemplateRepository {

    @Inject
    private MongoTemplate mongoTemplate;


    final String addFieldSections = CustomAggregationQuery.questionnaireTemplateAddNonDeletedSections();
    final String addFieldQuestions = CustomAggregationQuery.questionnaireTemplateAddNonDeletedQuestions();
    final String addFieldAssetType = CustomAggregationQuery.questionnaireTemplateAddNonDeletedAssetType();
    final String groupData = CustomAggregationQuery.questionnaireTemplateGroupOperation();
    final String projection = CustomAggregationQuery.questionnaireTemplateProjectionBeforeGroupOperationForAssetType();

    Document assetTypeAddFieldOperation = Document.parse(addFieldAssetType);
    Document questionsAddFieldOperation = Document.parse(addFieldQuestions);
    Document sectionsAddFieldOperation = Document.parse(addFieldSections);
    Document projectionOperation = Document.parse(projection);
    Document groupDataOperation = Document.parse(groupData);


    @Override
    public MasterQuestionnaireTemplate findByName(Long countryId, Long organizationId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, MasterQuestionnaireTemplate.class);

    }

    @Override
    public List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId,Long organizationId) {


        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)),
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
    public MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId,Long organizationId,BigInteger id) {

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id).and(ORGANIZATION_ID).is(organizationId)),
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


    @Override
    public BigInteger getMasterQuestionanaireTemplateIdListByTemplateType(Long countryId, Long unitId, QuestionnaireTemplateType templateType) {
        List<BigInteger> assetNames=new ArrayList<>();
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("templateType").is(QuestionnaireTemplateType.ASSET_TYPE));
        query.fields().include("_id");
        query.fields().exclude("name");
       /* QueryMapper mapper = new QueryMapper(mongoTemplate.getConverter());
        org.bson.Document mappedQuery = mapper.getMappedObject(query.getQueryObject(), Optional.empty());
      */
       return mongoTemplate.findOne(query,MasterQuestionnaireTemplate.class).getId();
    }
}
