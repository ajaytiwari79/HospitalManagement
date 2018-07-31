package com.kairos.persistence.repository.period;

import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.util.DateUtils;
import com.kairos.activity.period.PlanningPeriodDTO;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 6/4/18.
 */
public class PlanningPeriodMongoRepositoryImpl implements CustomPlanningPeriodMongoRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public PlanningPeriod getPlanningPeriodContainsDate(Long unitId, LocalDate localDateLiesInPeriod) {
        Date dateLiesInPeriod = DateUtils.asDate(localDateLiesInPeriod);
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true).
                and("startDate").lte(dateLiesInPeriod).and("endDate").gte(dateLiesInPeriod));
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        Date startDate = DateUtils.asDate(startLocalDate);
        Date endDate = DateUtils.asDate(endLocalDate);
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true).
                and("startDate").gte(startDate).and("endDate").lte(endDate));
        Update update = new Update();
        update.set("deleted", true);
        update.set("active", false);
        return mongoTemplate.updateMulti(query, update, PlanningPeriod.class);
    }

    public PlanningPeriod getFirstPlanningPeriod(Long unitId) {
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true));
        query.with(Sort.by(Sort.Direction.ASC, "startDate"));
        query.limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public PlanningPeriod getLastPlanningPeriod(Long unitId) {
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true));
        query.with(Sort.by(Sort.Direction.DESC, "startDate"));
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
                match(Criteria.where("deleted").is(false).and("active").is(true).and("unitId").is(unitId)),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                lookup("phases", "nextPhaseId", "_id", "next_phase_data"),
                sort(Sort.Direction.ASC, "startDate"),
                projectionOperation

        );

        AggregationResults<PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return result.getMappedResults();
    }


    /*public  List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, Date startDate, Date endDate) {

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

        AggregationResults<com.kairos.activity.period.PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, com.kairos.activity.period.PlanningPeriodDTO.class);
        return result.getMappedResults();
    }*/

    public List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {

        Date startDate = DateUtils.asDate(startLocalDate);
        Date endDate = DateUtils.asDate(endLocalDate);
        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude("startDate").
                andInclude("endDate").
                andInclude("phaseFlippingDate").
                and("current_phase_data.name").as("currentPhase").
                and("next_phase_data.name").as("nextPhase");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("active").is(true).and("unitId").is(unitId).and("startDate").gte(startDate).and("endDate").lte(endDate)),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                lookup("phases", "nextPhaseId", "_id", "next_phase_data"),
                sort(Sort.Direction.ASC, "startDate"),
                projectionOperation

        );

        AggregationResults<com.kairos.activity.period.PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, com.kairos.activity.period.PlanningPeriodDTO.class);
        return result.getMappedResults();
    }


    public boolean checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate, int sequence) {

        Date startDate = DateUtils.asDate(startLocalDate);
        Date endDate = DateUtils.asDate(endLocalDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("active").is(true).and("unitId").is(unitId)
                        .orOperator(
                                Criteria.where("startDate").gte(startDate).lte(endDate),
                                Criteria.where("endDate").gte(startDate).lte(endDate)
                        )),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                match(Criteria.where("current_phase_data.sequence").ne(sequence)), count().as("countOfPhasesWithOtherSequence")
        );

        AggregationResults<Map> result =
                mongoTemplate.aggregate(aggregation, "planningPeriod", Map.class);
        Map resultData = result.getUniqueMappedResult();
        if (Optional.ofNullable(resultData).isPresent()) {
            return (Integer) result.getUniqueMappedResult().get("countOfPhasesWithOtherSequence") > 0;
        } else {
            return false;
        }
    }

    public boolean checkIfPeriodsExistsOrOverlapWithStartAndEndDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {

        Date startDate = DateUtils.asDate(startLocalDate);
        Date endDate = DateUtils.asDate(endLocalDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("active").is(true).and("unitId").is(unitId)
                        .orOperator(
                                Criteria.where("startDate").gte(startDate).lte(endDate),
                                Criteria.where("endDate").gte(startDate).lte(endDate),
                                Criteria.where("startDate").gte(startDate).and("endDate").lte(endDate)
                        )), count().as("countOfPhases")
        );

        AggregationResults<Map> result =
                mongoTemplate.aggregate(aggregation, "planningPeriod", Map.class);
        Map resultData = result.getUniqueMappedResult();
        if (Optional.ofNullable(resultData).isPresent()) {
            return (Integer) result.getUniqueMappedResult().get("countOfPhases") > 0;
        } else {
            return false;
        }
    }

    public List<PlanningPeriod> getPlanningPeriodToFlipPhases(Long unitId, Date date) {

        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).
                and("phaseFlippingDate.flippingDate").lte(date));
//        query.addCriteria(Criteria.where("this.nextPhaseId").is("this.phaseFlippingDate.phaseId"));
        query.with(Sort.by(Sort.Direction.ASC, "startDate"));
        return mongoTemplate.find(query, PlanningPeriod.class);
    }

    public PlanningPeriod findLastPlaningPeriodEndDate(Long unitId) {
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").in(true));
        query.with(Sort.by(Sort.Direction.DESC, "startDate")).limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public List<PlanningPeriod> findAllPeriodsOfUnitByRequestPhaseId(Long unitId, String requestPhaseName) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("active").is(true)),
                lookup("phases", "currentPhaseId", "_id", "phase_name"),
                match(Criteria.where("phase_name.name").is(requestPhaseName)),
               sort(Sort.Direction.ASC,"startDate")
        );
        AggregationResults<PlanningPeriod> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriod.class);
        return results.getMappedResults();
    }
}