package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.repository.common.CustomAggregationOperation;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
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

        String projection = "{$project:{'id':0,'staffQuestionnairesIds':'$staffQuestionnairesId'}}";
        String projection1 = "{$project:{'id':'$staffQuestionnaire.id','questionAnswerPair':'$staffQuestionnaire.questionAnswerPair'}}";
        String projection2 = "{$project:{'id':'$id','questionAnswerPair.answer':'$questionAnswerPair.answer','questionAnswerPair.question':'$questionAnswerPair.question.question'}}";
        String groupString = "{$group:{'id':'$_id', 'questionAnswerPair': { '$addToSet': '$questionAnswerPair' }}}";

        Document projectionObject =Document.parse(projection);
        Document projectionObject1 =Document.parse(projection1);
        Document projectionObject2 =Document.parse(projection2);
        Document groupObject =Document.parse(groupString);


//        ProjectionOperation projectionOperation = Aggregation.project().
//                and("id").as("id").
//                andInclude("questionAnswerPair");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(staffId)),
                project().andExclude("_id").and("staffQuestionnairesId").as("staffQuestionnairesIds"),
                unwind("staffQuestionnairesIds", true),
                lookup("staffQuestionnaire", "staffQuestionnairesIds", "_id", "staffQuestionnaire"),
                unwind("staffQuestionnaire", true),
                project().and("staffQuestionnaire._id").as("_id").and("staffQuestionnaire.questionAnswerPair").as("questionAnswerPair"),
                unwind("questionAnswerPair", true),
                lookup("nightWorkerQuestion", "questionAnswerPair.questionId", "_id", "questionAnswerPair.question"),
                unwind("questionAnswerPair.question", true),
                project().and("questionAnswerPair.answer").as("questionAnswerPair.answer").and("questionAnswerPair.question.question").as("questionAnswerPair.question"),

                new CustomAggregationOperation(groupObject)
        );

        AggregationResults<QuestionnaireAnswerResponseDTO> result = mongoTemplate.aggregate (aggregation, NightWorker.class, QuestionnaireAnswerResponseDTO.class);
        return result.getMappedResults();
    }
}
