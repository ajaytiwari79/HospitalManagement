package com.kairos.persistence.repository.period;

import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.persistence.model.phase.Phase;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 6/4/18.
 */
public class PlanningPeriodMongoRepositoryImpl implements CustomPlanningPeriodMongoRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public PlanningPeriod getPlanningPeriodContainsDate(Long unitId, LocalDate localDateLiesInPeriod) {
        Date dateLiesInPeriod = DateUtils.getDateFromLocalDate(localDateLiesInPeriod);
        Query query = Query.query(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true).
                and("startDate").lte(dateLiesInPeriod).and("endDate").gte(dateLiesInPeriod));
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
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
                and("current_phase_data.color").as("color").
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

    public List<PeriodDTO> findAllPeriodsByStartDateAndLastDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude("startDate").
                andInclude("endDate").
                and("current_phase_data._id").as("phaseId").
                and("current_phase_data.name").as("currentPhaseName").
                and("current_phase_data.color").as("phaseColor").
                and("current_phase_data.phaseEnum").as("phaseEnum").
                and("next_phase_data.name").as("nextPhaseName");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true).
                        and("startDate").lte(endDate).and("endDate").gte(startDate)),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                lookup("phases", "nextPhaseId", "_id", "next_phase_data"),
                sort(Sort.Direction.ASC, "startDate"),
                projectionOperation

        );

        AggregationResults<PeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PeriodDTO.class);
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

        AggregationResults<PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return result.getMappedResults();
    }*/

    public List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {

        // Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        // Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude("startDate").
                andInclude("endDate").
                andInclude("phaseFlippingDate").
                and("current_phase_data.name").as("currentPhase").
                and("next_phase_data.name").as("nextPhase");
        Criteria criteria = Criteria.where("deleted").is(false).and("active").is(true).and("unitId").is(unitId).and("startDate").gte(startLocalDate);
        if (endLocalDate != null) {
            criteria = criteria.and("endDate").lte(endLocalDate);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                lookup("phases", "nextPhaseId", "_id", "next_phase_data"),
                sort(Sort.Direction.ASC, "startDate"),
                projectionOperation

        );

        AggregationResults<PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return result.getMappedResults();
    }

    public PlanningPeriod findCurrentDatePlanningPeriod(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
        Query query = Query.query(
                Criteria.where("deleted").is(false).and("active").is(true).and("unitId").is(unitId)
                        .orOperator(
                                Criteria.where("startDate").gte(startDate).lte(endDate),
                                Criteria.where("endDate").gte(startDate).gte(endDate)
                        ));
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public boolean checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate, int sequence) {

        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
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

        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
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
                sort(Sort.Direction.ASC, "startDate")
        );
        AggregationResults<PlanningPeriod> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriod.class);
        return results.getMappedResults();
    }

    @Override
    public Phase getCurrentPhaseByDateUsingPlanningPeriod(Long unitId, LocalDate dateLiesInPeriod) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true).
                        and("startDate").lte(dateLiesInPeriod).and("endDate").gte(dateLiesInPeriod)),
                lookup("phases", "currentPhaseId", "_id", "phase"),
                project("phase._id", "phase.name")
        );
        AggregationResults<Phase> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, Phase.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }


    @Override
    public List<PlanningPeriod> findAllPeriodsByUnitIdAndDates(Long unitId, Set<LocalDate> localDates) {
        Criteria[] criteriaList = new Criteria[localDates.size()];
        int i = 0;
        for (LocalDate localDate : localDates) {
            criteriaList[i++] = Criteria.where("startDate").lte(localDate).and("endDate").gte(localDate);
        }
        Criteria criteria = Criteria.where("unitId").is(unitId).and("deleted").is(false).and("active").is(true).orOperator(criteriaList);
        Query query = new Query(criteria);
        List<PlanningPeriod> results = mongoTemplate.find(query, PlanningPeriod.class);
        return results;
    }

    @Override
    public List<PlanningPeriodDTO> findAllPlanningPeriodBetweenDatesAndUnitId(Long unitId, Date requestedStartDate, Date requestedEndDate) {
        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude("startDate").
                andInclude("endDate").
                and("current_phase_data.name").as("currentPhase").
                and("current_phase_data._id").as("currentPhaseId");
        Aggregation aggregation = newAggregation(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("startDate").lte(requestedEndDate).and("endDate").gte(requestedStartDate)),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"), projectionOperation
        );
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return results.getMappedResults();
    }

    @Override
    public PlanningPeriodDTO findStartDateAndEndDateOfPlanningPeriodByUnitId(Long unitId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                sort(Sort.Direction.ASC, "startDate"),
                group("unitId").first("startDate").as("startDate").last("endDate").as("endDate"),
                project().and("startDate").as("startDate").and("endDate").as("endDate")
        );
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }

}