package com.kairos.persistence.repository.period;

import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 6/4/18.
 */
public class PlanningPeriodMongoRepositoryImpl implements CustomPlanningPeriodMongoRepository {

    public static final String ACTIVE = "active";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String UNIT_ID = "unitId";
    public static final String DELETED = "deleted";
    public static final String DURATION = "duration";
    public static final String DURATION_TYPE = "durationType";
    public static final String PHASE_FLIPPING_DATE = "phaseFlippingDate";
    public static final String PUBLISH_EMPLOYMENT_IDS = "publishEmploymentIds";
    public static final String CURRENT_PHASE_DATA_NAME = "current_phase_data.name";
    public static final String CURRENT_PHASE = "currentPhase";
    public static final String NEXT_PHASE_DATA_NAME = "next_phase_data.name";
    public static final String PHASES = "phases";
    public static final String CURRENT_PHASE_ID = "currentPhaseId";
    public static final String CURRENT_PHASE_DATA = "current_phase_data";
    public static final String NEXT_PHASE_ID = "nextPhaseId";
    public static final String NEXT_PHASE_DATA = "next_phase_data";
    public static final String PHASE = "phase";
    @Inject
    private MongoTemplate mongoTemplate;

    public PlanningPeriod getPlanningPeriodContainsDate(Long unitId, LocalDate localDateLiesInPeriod) {
        Date dateLiesInPeriod = DateUtils.getDateFromLocalDate(localDateLiesInPeriod);
        Query query = Query.query(Criteria.where(CommonConstants.UNIT_ID).is(unitId).and(CommonConstants.DELETED).is(false).and(ACTIVE).is(true).
                and(START_DATE).lte(dateLiesInPeriod).and(END_DATE).gte(dateLiesInPeriod));
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public PlanningPeriod getFirstPlanningPeriod(Long unitId) {
        Query query = Query.query(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).is(true));
        query.with(Sort.by(Sort.Direction.ASC, START_DATE));
        query.limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public PlanningPeriod getLastPlanningPeriod(Long unitId) {
        Query query = Query.query(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).is(true));
        query.with(Sort.by(Sort.Direction.DESC, START_DATE));
        query.limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public List<PlanningPeriodDTO> findAllPeriodsOfUnit(Long unitId) {

        ProjectionOperation projectionOperation = Aggregation.project(DURATION, DURATION_TYPE).
                and("id").as("id").
                andInclude("name").
                andInclude(START_DATE).
                andInclude(UNIT_ID).
                andInclude(END_DATE).
                andInclude(PHASE_FLIPPING_DATE).
                andInclude(PUBLISH_EMPLOYMENT_IDS).
                and(CURRENT_PHASE_DATA_NAME).as(CURRENT_PHASE).
                and("current_phase_data.color").as("color").
                and(NEXT_PHASE_DATA_NAME).as("nextPhase");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ACTIVE).is(true).and(UNIT_ID).is(unitId)),
                lookup(PHASES, CURRENT_PHASE_ID, "_id", CURRENT_PHASE_DATA),
                lookup(PHASES, NEXT_PHASE_ID, "_id", NEXT_PHASE_DATA),
                sort(Sort.Direction.ASC, START_DATE),
                projectionOperation

        );

        AggregationResults<PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return result.getMappedResults();
    }

    public List<PeriodDTO> findAllPeriodsByStartDateAndLastDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
        ProjectionOperation projectionOperation = Aggregation.project(DURATION, DURATION_TYPE).
                and("id").as("id").
                andInclude("name").
                andInclude(START_DATE).
                andInclude(END_DATE).
                andInclude(PUBLISH_EMPLOYMENT_IDS).
                and("current_phase_data._id").as("phaseId").
                and(CURRENT_PHASE_DATA_NAME).as("currentPhaseName").
                and("current_phase_data.color").as("phaseColor").
                and("current_phase_data.phaseEnum").as("phaseEnum").
                and(NEXT_PHASE_DATA_NAME).as("nextPhaseName");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).is(true).
                        and(START_DATE).lte(endDate).and(END_DATE).gte(startDate)),
                lookup(PHASES, CURRENT_PHASE_ID, "_id", CURRENT_PHASE_DATA),
                lookup(PHASES, NEXT_PHASE_ID, "_id", NEXT_PHASE_DATA),
                sort(Sort.Direction.ASC, START_DATE),
                projectionOperation

        );

        AggregationResults<PeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PeriodDTO.class);
        return result.getMappedResults();
    }



    public List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        ProjectionOperation projectionOperation = Aggregation.project(DURATION, DURATION_TYPE).
                and("id").as("id").
                andInclude("name").
                andInclude(START_DATE).
                andInclude(END_DATE).
                andInclude(PUBLISH_EMPLOYMENT_IDS).
                andInclude(PHASE_FLIPPING_DATE).
                and(CURRENT_PHASE_DATA_NAME).as(CURRENT_PHASE).
                and(NEXT_PHASE_DATA_NAME).as("nextPhase");
        Criteria criteria = Criteria.where(DELETED).is(false).and(ACTIVE).is(true).and(UNIT_ID).is(unitId).and(START_DATE).gte(startLocalDate);
        if (endLocalDate != null) {
            criteria = criteria.and(END_DATE).lte(endLocalDate);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(PHASES, CURRENT_PHASE_ID, "_id", CURRENT_PHASE_DATA),
                lookup(PHASES, NEXT_PHASE_ID, "_id", NEXT_PHASE_DATA),
                sort(Sort.Direction.ASC, START_DATE),
                projectionOperation

        );

        AggregationResults<PlanningPeriodDTO> result = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return result.getMappedResults();
    }

    public PlanningPeriod findCurrentDatePlanningPeriod(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate) {
        Date startDate = DateUtils.getDateFromLocalDate(startLocalDate);
        Date endDate = DateUtils.getDateFromLocalDate(endLocalDate);
        Query query = Query.query(
                Criteria.where(DELETED).is(false).and(ACTIVE).is(true).and(UNIT_ID).is(unitId)
                        .orOperator(
                                Criteria.where(START_DATE).gte(startDate).lte(endDate),
                                Criteria.where(END_DATE).gte(startDate).gte(endDate)
                        ));
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public List<PlanningPeriod> getPlanningPeriodToFlipPhases(Long unitId, Date date) {

        Query query = Query.query(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).
                and("phaseFlippingDate.flippingDate").lte(date));
        query.with(Sort.by(Sort.Direction.ASC, START_DATE));
        return mongoTemplate.find(query, PlanningPeriod.class);
    }

    public PlanningPeriod findLastPlaningPeriodEndDate(Long unitId) {
        Query query = Query.query(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).in(true));
        query.with(Sort.by(Sort.Direction.DESC, START_DATE)).limit(1);
        return mongoTemplate.findOne(query, PlanningPeriod.class);
    }

    public List<PlanningPeriod> findAllPeriodsOfUnitByRequestPhaseId(Long unitId, String requestPhaseName) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).and(ACTIVE).is(true)),
                lookup(PHASES, CURRENT_PHASE_ID, "_id", "phase_name"),
                match(Criteria.where("phase_name.name").is(requestPhaseName)),
                sort(Sort.Direction.ASC, START_DATE)
        );
        AggregationResults<PlanningPeriod> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriod.class);
        return results.getMappedResults();
    }

    @Override
    public Phase getCurrentPhaseByDateUsingPlanningPeriod(Long unitId, LocalDate dateLiesInPeriod) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).is(true).
                        and(START_DATE).lte(dateLiesInPeriod).and(END_DATE).gte(dateLiesInPeriod)),
                lookup(PHASES, CURRENT_PHASE_ID, "_id", PHASE),
                project().and(PHASE).arrayElementAt(0).as(PHASE),
                project("phase._id", "phase.name","phase.phaseEnum","phase.accessGroupIds")
        );
        AggregationResults<Phase> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, Phase.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }


    @Override
    public List<PlanningPeriod> findAllPeriodsByUnitIdAndDates(Long unitId, Set<LocalDate> localDates) {
        Criteria[] criteriaList = new Criteria[localDates.size()];
        int i = 0;
        for (LocalDate localDate : localDates) {
            criteriaList[i++] = Criteria.where(START_DATE).lte(localDate).and(END_DATE).gte(localDate);
        }
        Criteria criteria = Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).is(true).orOperator(criteriaList);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, PlanningPeriod.class);
    }

    @Override
    public List<PlanningPeriodDTO> findAllPlanningPeriodBetweenDatesAndUnitId(Long unitId, Date requestedStartDate, Date requestedEndDate) {
        ProjectionOperation projectionOperation = Aggregation.project().
                and("id").as("id").
                andInclude("name").
                andInclude(START_DATE).
                andInclude(END_DATE).
                andInclude(PUBLISH_EMPLOYMENT_IDS).
                and(CURRENT_PHASE_DATA_NAME).as(CURRENT_PHASE).
                and("current_phase_data._id").as(CURRENT_PHASE_ID);
        Aggregation aggregation = newAggregation(
                match(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).and(START_DATE).lte(requestedEndDate).and(END_DATE).gte(requestedStartDate)),
                lookup(PHASES, CURRENT_PHASE_ID, "_id", CURRENT_PHASE_DATA),
                sort(Sort.Direction.ASC, START_DATE),
                projectionOperation
        );
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return results.getMappedResults();
    }

    @Override
    public List<PlanningPeriod> findLastPlanningPeriodOfAllUnits() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(DELETED).is(false).and(ACTIVE).is(true)),
                sort(Sort.Direction.DESC, UNIT_ID, START_DATE),
                group(UNIT_ID).first("$$ROOT").as("data"),
                replaceRoot("data")
        );
        AggregationResults<PlanningPeriod> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriod.class);
        return results.getMappedResults();
    }

    @Override
    public PlanningPeriodDTO findStartDateAndEndDateOfPlanningPeriodByUnitId(Long unitId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                sort(Sort.Direction.ASC, START_DATE),
                group(UNIT_ID).first(START_DATE).as(START_DATE).last(END_DATE).as(END_DATE),
                project().and(START_DATE).as(START_DATE).and(END_DATE).as(END_DATE)
        );
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }

    @Override
    public PlanningPeriod findFirstRequestPhasePlanningPeriodByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(START_DATE).gte(LocalDate.now())),
                lookup(PHASES, CURRENT_PHASE_ID,"_id", PHASE),
                project(UNIT_ID, END_DATE).and(PHASE).arrayElementAt(0).as(PHASE),
                match(Criteria.where("phase.phaseEnum").is(PhaseDefaultName.REQUEST)),
                group(UNIT_ID).first(END_DATE).as(END_DATE)

        );
        AggregationResults<PlanningPeriod> results = mongoTemplate.aggregate(aggregation,PlanningPeriod.class,PlanningPeriod.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }

}