package com.kairos.persistence.repository.night_worker;

import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.night_worker.NightWorkerQuestion;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.dto.activity.night_worker.QuestionAnswerDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;
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

    @Inject
    MongoTemplate mongoTemplate;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaireDetails(Long staffId) {

        String groupString = "{$group:{_id:'$_id', 'name':{'$first':'$name'}, 'submitted':{'$first':'$submitted'}, 'submittedOn':{'$first':'$submittedOn'} "+
                "'questionAnswerPair': { '$push':'$questionAnswerPair' }}}";
        String sortString = "{$sort:{questionnaireCreatedDate:-1, createdDate:1}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(staffId)),
                project().andExclude("_id").and("staffQuestionnairesId").as("staffQuestionnairesIds"),
                unwind("staffQuestionnairesIds"),
                lookup("staffQuestionnaire", "staffQuestionnairesIds", "_id", "staffQuestionnaire"),
                unwind("staffQuestionnaire"),

                project().and("staffQuestionnaire.name").as("name").
                        and("staffQuestionnaire.createdAt").as("questionnaireCreatedDate").
                        and("staffQuestionnaire.submitted").as("submitted").
                        and("staffQuestionnaire.submittedOn").as("submittedOn").
                        and("staffQuestionnaire._id").as("_id").
                        and("staffQuestionnaire.questionAnswerPair").as("questionAnswerPair").
                        and("staffQuestionnaire.name").as("name"),
                unwind("questionAnswerPair"),
                lookup("nightWorkerQuestion", "questionAnswerPair.questionId", "_id", "questionAnswerPair.question"),
                unwind("questionAnswerPair.question"),

                project().and("questionAnswerPair.answer").as("questionAnswerPair.answer").
                        and("questionAnswerPair.question.question").as("questionAnswerPair.question").
                        and("questionAnswerPair.question._id").as( "questionAnswerPair.questionId").
                        and("name").as("name").
                        and("questionnaireCreatedDate").as("questionnaireCreatedDate").
                        and("questionAnswerPair.question.createdAt").as("createdDate").
                        and("submitted").as("submitted").
                        and("submittedOn").as("submittedOn"),
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
