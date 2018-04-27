package com.kairos.activity.persistence.repository.period;

import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 6/4/18.
 */
public class PlanningPeriodMongoRepositoryImpl implements CustomPlanningPeriodMongoRepository{

    @Inject
    private MongoTemplate mongoTemplate;

    public PlanningPeriod getPlanningPeriodContainsDate(Long unitId, Date dateLiesInPeriod){
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).
                and("startDate").lte(dateLiesInPeriod).and("endDate").gte(dateLiesInPeriod));
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, Date startDate, Date endDate){
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).
                and("startDate").gte(startDate).and("endDate").lte(endDate));
        Update update = new Update();
        update.set("deleted", true);
        return mongoTemplate.updateMulti(query, update, PlanningPeriod.class);
    }

    public PlanningPeriod getFirstPlanningPeriod(Long unitId){
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC,"startDate"));
        query.limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public PlanningPeriod getLastPlanningPeriod(Long unitId){
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.DESC,"startDate"));
        query.limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public List<PlanningPeriodDTO> findAllPeriodsOfUnit(Long unitId) {

        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude("startDate").
                andInclude("endDate").
                andInclude("phaseFlippingDate").
                and("current_phase_data.name").as("currentPhase").
                and("next_phase_data.name").as("nextPhase");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId)),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                lookup("phases", "nextPhaseId", "_id", "next_phase_data"),
                sort(Sort.Direction.ASC,"startDate"),
                projectionOperation

        );

        AggregationResults<PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return result.getMappedResults();
    }

    public  List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, Date startDate, Date endDate) {

        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude("startDate").
                andInclude("endDate").
                andInclude("phaseFlippingDate").
                and("current_phase_data.name").as("currentPhase").
                and("next_phase_data.name").as("nextPhase");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("startDate").gte(startDate).and("endDate").lte(endDate)),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                lookup("phases", "nextPhaseId", "_id", "next_phase_data"),
                sort(Sort.Direction.ASC,"startDate"),
                projectionOperation

        );

        AggregationResults<com.kairos.response.dto.web.period.PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, com.kairos.response.dto.web.period.PlanningPeriodDTO.class);
        return result.getMappedResults();
    }

    public List<PlanningPeriod> getPlanningPeriodToFlipPhases(Long unitId, Date date){

        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).
                and("phaseFlippingDate.flippingDate").lte(date));
//        query.addCriteria(Criteria.where("this.nextPhaseId").is("this.phaseFlippingDate.phaseId"));
        query.with(Sort.by(Sort.Direction.ASC,"startDate"));
        return mongoTemplate.find(query, PlanningPeriod.class);
    }
}
