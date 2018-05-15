package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.model.night_worker.NightWorkerQuestion;
import com.kairos.activity.persistence.repository.common.CustomAggregationOperation;
import com.kairos.response.dto.web.night_worker.QuestionAnswerDTO;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerMongoRepositoryImpl implements CustomNightWorkerMongoRepository{

    @Inject
    MongoTemplate mongoTemplate;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaireDetails(Long staffId) {

//         String groupString = "{$group:{'id':'$_id', 'questionAnswerPair': { '$addToSet': '$questionAnswerPair' }}}";
        String groupString = "{$group:{_id:'$_id', 'name':{'$first':'$name'}, 'questionAnswerPair': { '$addToSet': '$questionAnswerPair' }}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(staffId)),
                project().andExclude("_id").and("staffQuestionnairesId").as("staffQuestionnairesIds"),
                unwind("staffQuestionnairesIds"),
                lookup("staffQuestionnaire", "staffQuestionnairesIds", "_id", "staffQuestionnaire"),
                unwind("staffQuestionnaire"),
                project().and("staffQuestionnaire.name").as("name").and("staffQuestionnaire._id").as("_id").
                        and("staffQuestionnaire.questionAnswerPair").as("questionAnswerPair").
                        and("staffQuestionnaire.name").as("name"),
                unwind("questionAnswerPair"),
                lookup("nightWorkerQuestion", "questionAnswerPair.questionId", "_id", "questionAnswerPair.question"),
                unwind("questionAnswerPair.question"),
                project().and("questionAnswerPair.answer").as("questionAnswerPair.answer").
                        and("questionAnswerPair.question.question").as("questionAnswerPair.question").
                        and("questionAnswerPair.question._id").as( "questionAnswerPair.questionId").
                        and("name").as("name"),

                new CustomAggregationOperation(Document.parse(groupString))
        );

        AggregationResults<QuestionnaireAnswerResponseDTO> result = mongoTemplate.aggregate (aggregation, NightWorker.class, QuestionnaireAnswerResponseDTO.class);
        return result.getMappedResults();
    }

    public boolean checkIfNightWorkerQuestionnaireFormIsEnabled(Long staffId, Date date) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(staffId)),
                project().andExclude("_id").and("staffQuestionnairesId").as("staffQuestionnairesIds"),
                unwind("staffQuestionnairesIds"),
                lookup("staffQuestionnaire", "staffQuestionnairesIds", "_id", "staffQuestionnaire"),
                unwind("staffQuestionnaire"),
                match(Criteria.where("staffQuestionnaire.deleted").is(false).and("staffQuestionnaire.createdAt").gt(date)),
                count().as("questionnaireCount")
        );

        AggregationResults<Map> result = mongoTemplate.aggregate (aggregation, NightWorker.class, Map.class);
        Map resultData = result.getUniqueMappedResult();
        if (Optional.ofNullable(resultData).isPresent()) {
            return ! ((Integer) result.getUniqueMappedResult().get("questionnaireCount") > 0);
        } else {
            return true;
        }
    }
    public List<QuestionAnswerDTO> getNightWorkerQuestions() {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false)),
                project().and("id").as("questionId").and("question").as("question")
        );

        AggregationResults<QuestionAnswerDTO> result = mongoTemplate.aggregate (aggregation, NightWorkerQuestion.class, QuestionAnswerDTO.class);
        return result.getMappedResults();
    }


}
