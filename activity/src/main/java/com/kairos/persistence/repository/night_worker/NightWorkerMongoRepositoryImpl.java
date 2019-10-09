package com.kairos.persistence.repository.night_worker;

import com.kairos.dto.activity.night_worker.QuestionAnswerDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.night_worker.NightWorkerQuestion;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerMongoRepositoryImpl implements CustomNightWorkerMongoRepository{

    public static final String STAFF_QUESTIONNAIRES_IDS = "staffQuestionnairesIds";
    public static final String STAFF_QUESTIONNAIRE = "staffQuestionnaire";
    public static final String QUESTIONNAIRE_CREATED_DATE = "questionnaireCreatedDate";
    public static final String SUBMITTED = "submitted";
    public static final String SUBMITTED_ON = "submittedOn";
    public static final String QUESTION_ANSWER_PAIR_QUESTION = "questionAnswerPair.question";
    @Inject
    MongoTemplate mongoTemplate;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaireDetails(Long staffId) {

        String groupString = "{$group:{_id:'$_id', 'name':{'$first':'$name'}, 'submitted':{'$first':'$submitted'}, 'submittedOn':{'$first':'$submittedOn'} "+
                "'questionAnswerPair': { '$push':'$questionAnswerPair' }}}";
        String sortString = "{$sort:{questionnaireCreatedDate:-1, createdDate:1}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(staffId)),
                project().andExclude("_id").and("staffQuestionnairesId").as(STAFF_QUESTIONNAIRES_IDS),
                unwind(STAFF_QUESTIONNAIRES_IDS),
                lookup(STAFF_QUESTIONNAIRE, STAFF_QUESTIONNAIRES_IDS, "_id", STAFF_QUESTIONNAIRE),
                unwind(STAFF_QUESTIONNAIRE),

                project().and("staffQuestionnaire.name").as("name").
                        and("staffQuestionnaire.createdAt").as(QUESTIONNAIRE_CREATED_DATE).
                        and("staffQuestionnaire.submitted").as(SUBMITTED).
                        and("staffQuestionnaire.submittedOn").as(SUBMITTED_ON).
                        and("staffQuestionnaire._id").as("_id").
                        and("staffQuestionnaire.questionAnswerPair").as("questionAnswerPair").
                        and("staffQuestionnaire.name").as("name"),
                unwind("questionAnswerPair"),
                lookup("nightWorkerQuestion", "questionAnswerPair.questionId", "_id", QUESTION_ANSWER_PAIR_QUESTION),
                unwind(QUESTION_ANSWER_PAIR_QUESTION),

                project().and("questionAnswerPair.answer").as("questionAnswerPair.answer").
                        and("questionAnswerPair.question.question").as(QUESTION_ANSWER_PAIR_QUESTION).
                        and("questionAnswerPair.question._id").as( "questionAnswerPair.questionId").
                        and("name").as("name").
                        and(QUESTIONNAIRE_CREATED_DATE).as(QUESTIONNAIRE_CREATED_DATE).
                        and("questionAnswerPair.question.createdAt").as("createdDate").
                        and(SUBMITTED).as(SUBMITTED).
                        and(SUBMITTED_ON).as(SUBMITTED_ON),
                new CustomAggregationOperation(Document.parse(sortString)),
                new CustomAggregationOperation(Document.parse(groupString))
        );

        AggregationResults<QuestionnaireAnswerResponseDTO> result = mongoTemplate. aggregate (aggregation, NightWorker.class, QuestionnaireAnswerResponseDTO.class);
        return result.getMappedResults();
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
