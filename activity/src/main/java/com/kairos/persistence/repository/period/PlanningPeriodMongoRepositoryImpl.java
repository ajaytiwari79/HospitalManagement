package com.kairos.persistence.repository.period;

import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ShiftDataHelper;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.getBigIntegerString;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
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
    public static final String DATE_RANGE = "dateRange";
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
                andInclude(DATE_RANGE).
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
                andInclude(DATE_RANGE).
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
                andInclude(DATE_RANGE).
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
                project("phase._id", "phase.name","phase.phaseEnum","phase.accessGroupIds","phase.organizationId")
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

    @Override
    public ShiftPlanningProblemSubmitDTO findDataForAutoPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO){
        String breakActivityIdString = getBigIntegerString(shiftPlanningProblemSubmitDTO.getBreakSettingMap().values().stream().map(breakSettingsDTO -> breakSettingsDTO.getActivityId()).collect(Collectors.toSet()).iterator());
        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").is(shiftPlanningProblemSubmitDTO.getPlanningPeriodId())),
                getlookupOperationOfShiftsForPlanning(shiftPlanningProblemSubmitDTO.getStaffIds()),
                getStaffingLevelLookupForPlanning(),
                new CustomAggregationOperation(Document.parse("{$addFields: { activityIds:\"$staffingLevels.presenceStaffingLevelInterval.staffingLevelActivities.activityId\"}}")),
                getProjectionWithReduceForPlanning("[]"),
                getProjectionWithReduceForPlanning(breakActivityIdString),
                getActivitiesLookupForPlanning(),
                getActivityConfigurationLookupForPlanning(),
                new CustomAggregationOperation(Document.parse("{ $addFields: { activityConfiguration: { $mergeObjects: \"$activityConfiguration\" } } }")),
                getProjectionForPlanning()

        );
        List<ShiftPlanningProblemSubmitDTO> shiftPlanningProblemSubmitDTOS = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, ShiftPlanningProblemSubmitDTO.class).getMappedResults();
        if(isCollectionNotEmpty(shiftPlanningProblemSubmitDTOS)){
            shiftPlanningProblemSubmitDTO.setActivities(shiftPlanningProblemSubmitDTOS.get(0).getActivities());
            shiftPlanningProblemSubmitDTO.setActivityConfiguration(shiftPlanningProblemSubmitDTOS.get(0).getActivityConfiguration());
            shiftPlanningProblemSubmitDTO.setPlanningPeriod(shiftPlanningProblemSubmitDTOS.get(0).getPlanningPeriod());
            shiftPlanningProblemSubmitDTO.setStaffingLevels(shiftPlanningProblemSubmitDTOS.get(0).getStaffingLevels());
            shiftPlanningProblemSubmitDTO.setShifts(shiftPlanningProblemSubmitDTOS.get(0).getShifts());
        }
        return shiftPlanningProblemSubmitDTO;
    }

    @Override
    public ShiftDataHelper getDataForShiftOperation(Date startDate, Long unitId, Collection<Long> employmentIds, Collection<Long> expertiseIds, Collection<Long> staffIds, Long countryId, Collection<BigInteger> activityIds, BigInteger shiftId, boolean userAccessRole){
        LocalDate localDate = asLocalDate(startDate);
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and("startDate").lte(localDate).and("endDate").gte(localDate).and("deleted").is(false).and("active").is(true)),
                getCustomLookUpForLastPlanningPeriod(unitId),
                getCustomProjectionForShift(),
                getCustomWTAOperationForShift(employmentIds),
                getCustomCTAOperationForShift(employmentIds),
                getCustomTimeSlotOperationForShift(unitId),
                getCustomDayTypeOperationForShift(countryId),
                getCustomActivityConfigurationOperationForShift(unitId),
                getCustomOperationForActivities(activityIds),
                getCustomOperationForNightWorker(staffIds),
                getCustomOperationForStaffWTACounter(employmentIds,userAccessRole),
                getCustomOperationForShift(shiftId),
                getCustomOperationForExpertiseNightWorkSetting(expertiseIds,unitId),
                getCustomOperationForCountryExpertiseNightWorkSetting(expertiseIds,countryId),
                getCustomAggregationForPhases(unitId),
                getCustomAggregationForBreakActivities(unitId,expertiseIds)
        );
        return mongoTemplate.aggregate(aggregation,PlanningPeriod.class, ShiftDataHelper.class).getMappedResults().get(0);
    }

    private AggregationOperation getCustomAggregationForBreakActivities(Long unitId,Collection<Long> expertiseIds) {
        return new CustomAggregationOperation("{\n" +
                "      \"$lookup\": {\n" +
                "        \"from\": \"breakSettings\",\n" +
                "        \"let\": {          \n" +
                "          \"expertiseIds\": "+expertiseIds+"\n" +
                "        },\n" +
                "        \"pipeline\": [\n" +
                "          {\n" +
                "            \"$match\": {\n" +
                "              \"$expr\": {\n" +
                "                \"$and\": [\n" +
                "                  {\n" +
                "                    \"$in\": [\n" +
                "                      \"$expertiseId\",\n" +
                "                      \"$$expertiseIds\"\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"activities\",\n" +
                "    \"let\": {\n" +
                "      \"id\" : \"$activityId\",\n" +
                "        \"unitId\":"+unitId+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$countryParentId\",\n" +
                "                  \"$$id\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$unitId\",\n" +
                "                  \"$$unitId\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "     $lookup:\n" +
                "       {\n" +
                "         from: \"time_Type\",\n" +
                "         localField: \"activityBalanceSettings.timeTypeId\",\n" +
                "         foreignField: \"_id\",\n" +
                "         as: \"timeType\"\n" +
                "       }\n" +
                "  },{\n" +
                "    \"$unwind\": \"$timeType\"\n" +
                "  }\n" +
                "    ],\n" +
                "    \"as\": \"activity\"\n" +
                "  }\n" +
                "},\n" +
                "{\n" +
                "    \"$unwind\": {\n" +
                "        path: \"$activity\",\n" +
                "        preserveNullAndEmptyArrays: true\n" +
                "      }\n" +
                "  }\n" +
                "        ],\n" +
                "        \"as\": \"breakSettings\"\n" +
                "      }\n" +
                "    }");
    }

    private AggregationOperation getCustomAggregationForPhases(Long unitId) {
        return new CustomAggregationOperation("{\n" +
                "      \"$lookup\": {\n" +
                "        \"from\": \"phases\",\n" +
                "        \"let\": {          \n" +
                "          \"unitId\": "+unitId+"\n" +
                "        },\n" +
                "        \"pipeline\": [\n" +
                "          {\n" +
                "            \"$match\": {\n" +
                "              \"$expr\": {\n" +
                "                \"$and\": [\n" +
                "                  {\n" +
                "                    \"$eq\": [\n" +
                "                      \"$organizationId\",\n" +
                "                      \"$$unitId\"\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"as\": \"phases\"\n" +
                "      }\n" +
                "    }");
    }

    private AggregationOperation getCustomProjectionForShift() {
        return new CustomAggregationOperation("{\"$project\":{\n" +
                "    \"planningPeriod.lastPlanningPeriodEndDate\":{ $arrayElemAt: [ \"$lastPlanningPeriod.endDate\", 0 ] },    \n" +
                "    \"planningPeriod.startDate\" : \"$startDate\",\n" +
                "    \"planningPeriod.endDate\" : \"$endDate\",\n" +
                "    \"planningPeriod.currentPhaseId\" : \"$currentPhaseId\",\n" +
                "    \"planningPeriod.nextPhaseId\" : \"$nextPhaseId\",\n" +
                "    \"planningPeriod.duration\" : \"$duration\",\n" +
                "    \"planningPeriod.durationType\" : \"$durationType\",\n" +
                "    \"_id\":0\n" +
                "    }\n" +
                "    }");
    }

    private AggregationOperation getCustomOperationForCountryExpertiseNightWorkSetting(Collection<Long> expertiseIds, Long countryId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"expertiseNightWorkerSetting\",\n" +
                "    \"let\": {\n" +
                "      \"expertiseIds\" : "+expertiseIds+",\n" +
                "        \"countryId\":"+countryId+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$expertiseId\",\n" +
                "                  \"$$expertiseIds\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$countryId\",\n" +
                "                  \"$$countryId\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"countryExpertiseNightWorkerSettings\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomOperationForExpertiseNightWorkSetting(Collection<Long> expertiseIds,Long unitId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"expertiseNightWorkerSetting\",\n" +
                "    \"let\": {\n" +
                "      \"expertiseIds\" : "+expertiseIds+",\n" +
                "        \"unitId\":"+unitId+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$expertiseId\",\n" +
                "                  \"$$expertiseIds\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$unitId\",\n" +
                "                  \"$$unitId\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"expertiseNightWorkerSettings\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomOperationForShift(BigInteger shiftId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"shifts\",\n" +
                "    \"let\": {\n" +
                "      \"id\" : '"+shiftId+"',\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$_id\",\n" +
                "                  \"$$id\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"shift\"\n" +
                "  }\n" +
                "},\n" +
                "{\n" +
                "    \"$unwind\": {\n" +
                "        path: \"$shift\",\n" +
                "        preserveNullAndEmptyArrays: true\n" +
                "      }\n" +
                "  }");
    }

    private AggregationOperation getCustomOperationForStaffWTACounter(Collection<Long> employmentIds, boolean userAccessRole) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"staffWTACounter\",\n" +
                "    \"let\": {\n" +
                "      \"employmentIds\" : "+employmentIds+",\n" +
                "        \"startDate\":\"$planningPeriod.startDate\",\n" +
                "        \"endDate\":\"$planningPeriod.endDate\",\n" +
                "        \"userHasStaffRole\":"+userAccessRole+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$employmentId\",\n" +
                "                  \"$$employmentIds\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$startDate\",\n" +
                "                  \"$$startDate\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$endDate\",\n" +
                "                  \"$$endDate\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$userHasStaffRole\",\n" +
                "                  \"$$userHasStaffRole\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"staffWTACounters\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomOperationForNightWorker(Collection<Long> staffIds) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"nightWorker\",\n" +
                "    \"let\": {\n" +
                "      \"staffIds\" : "+staffIds+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$staffId\",\n" +
                "                  \"$$staffIds\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"nightWorkers\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomOperationForActivities(Collection<BigInteger> activityIds) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"activities\",\n" +
                "    \"let\": {\n" +
                "      \"ids\" : "+getBigIntegerString(activityIds.iterator())+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$or\": [\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$_id\",\n" +
                "                  \"$$ids\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "         {\n" +
                "     $lookup:\n" +
                "       {\n" +
                "         from: \"time_Type\",\n" +
                "         localField: \"activityBalanceSettings.timeTypeId\",\n" +
                "         foreignField: \"_id\",\n" +
                "         as: \"timeType\"\n" +
                "       }\n" +
                "  },\n" +
                "  {\n" +
                "     $lookup:\n" +
                "       {\n" +
                "         from: \"activityPriority\",\n" +
                "         localField: \"activityPriorityId\",\n" +
                "         foreignField: \"_id\",\n" +
                "         as: \"activityPriority\"\n" +
                "       }\n" +
                "  },{\n" +
                "    \"$unwind\": \"$timeType\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"$unwind\": \"$activityPriority\"\n" +
                "  }\n" +
                "    ],\n" +
                "    \"as\": \"activities\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomActivityConfigurationOperationForShift(Long unitId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"activityConfiguration\",\n" +
                "    \"let\": {\n" +
                "      \"unitId\" : "+unitId+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$unitId\",\n" +
                "                  \"$$unitId\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"activityConfigurations\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomDayTypeOperationForShift(Long countryId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"dayType\",\n" +
                "    \"let\": {\n" +
                "      \"countryId\" : "+countryId+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$countryId\",\n" +
                "                  \"$$countryId\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "     $lookup:\n" +
                "       {\n" +
                "         from: \"countryHolidayCalender\",\n" +
                "         localField: \"_id\",\n" +
                "         foreignField: \"dayTypeId\",\n" +
                "         as: \"countryHolidayCalenderData\"\n" +
                "       }\n" +
                "  }\n" +
                "    ],\n" +
                "    \"as\": \"dayTypes\"\n" +
                "  }\n" +
                "}");
    }

    private AggregationOperation getCustomTimeSlotOperationForShift(Long unitId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"timeSlotSet\",\n" +
                "    \"let\": {\n" +
                "      \"unitId\": "+unitId+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$unitId\",\n" +
                "                  \"$$unitId\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$timeSlotType\",\n" +
                "                  \"SHIFT_PLANNING\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"as\": \"timeSlot\"\n" +
                "  }\n" +
                "},\n" +
                "{\n" +
                "    \"$unwind\": {\n" +
                "        path: \"$timeSlot\",\n" +
                "        preserveNullAndEmptyArrays: true\n" +
                "      }\n" +
                "  }");
    }

    private AggregationOperation getCustomCTAOperationForShift(Collection<Long> employmentIds) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"costTimeAgreement\",\n" +
                "    \"let\": {\n" +
                "      \"employmentIds\": "+employmentIds+"\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$deleted\",\n" +
                "                  false\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$employmentId\",\n" +
                "                  \"$$employmentIds\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "     $lookup:\n" +
                "       {\n" +
                "         from: \"cTARuleTemplate\",\n" +
                "         localField: \"ruleTemplateIds\",\n" +
                "         foreignField: \"_id\",\n" +
                "         as: \"ruleTemplates\"\n" +
                "       }\n" +
                "  }\n" +
                "    ],\n" +
                "    \"as\": \"costTimeAgreements\"\n" +
                "  }\n" +
                "}");
    }

    private CustomAggregationOperation getCustomWTAOperationForShift(Collection<Long> employmentIds) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"workingTimeAgreement\",\n" +
                "    \"let\": {\n" +
                "      \"employmentIds\": "+employmentIds+",\n" +
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$deleted\",\n" +
                "                  false\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"$in\": [\n" +
                "                  \"$employmentId\",\n" +
                "                  \"$$employmentIds\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "     $lookup:\n" +
                "       {\n" +
                "         from: \"wtaBaseRuleTemplate\",\n" +
                "         localField: \"ruleTemplateIds\",\n" +
                "         foreignField: \"_id\",\n" +
                "         as: \"ruleTemplates\"\n" +
                "       }\n" +
                "  },\n" +
                "    ],\n" +
                "    \"as\": \"workingTimeAgreements\"\n" +
                "  }\n" +
                "}");
    }

    private CustomAggregationOperation getCustomLookUpForLastPlanningPeriod(Long unitId) {
        return new CustomAggregationOperation("{\n" +
                "  \"$lookup\": {\n" +
                "    \"from\": \"planningPeriod\",\n" +
                "    \"let\": {\n" +
                "      \"unitId\" : "+unitId+
                "    },\n" +
                "    \"pipeline\": [\n" +
                "      {\n" +
                "        \"$match\": {\n" +
                "          \"$expr\": {\n" +
                "            \"$and\": [\n" +
                "              {\n" +
                "                \"$eq\": [\n" +
                "                  \"$unitId\",\n" +
                "                  \"$$unitId\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "       $group:\n" +
                "         {\n" +
                "           _id: \"$unitId\",\n" +
                "           endDate: { $last: \"$endDate\" }\n" +
                "         }\n" +
                "     }\n" +
                "    ],\n" +
                "    \"as\": \"lastPlanningPeriod\"\n" +
                "  }\n" +
                "}");
    }



    private CustomAggregationOperation getProjectionForPlanning() {
        return new CustomAggregationOperation(Document.parse("{$project:{\n" +
                "          \"planningPeriod.startDate\":\"$startDate\",\n" +
                "          \"planningPeriod.endDate\":\"$endDate\",\n" +
                "          \"planningPeriod.id\":\"$_id\",\n" +
                "          \"planningPeriod.unitId\":\"$unitId\",\n" +
                "          \"planningPeriod.currentPhaseId\":\"$currentPhaseId\",\n" +
                "          \"shifts\":1,\n" +
                "          \"activityConfiguration\":1,\n" +
                "          \"staffingLevels\":1,\n" +
                "          \"activities\":1\n" +
                "          }}"));
    }

    private CustomAggregationOperation getActivityConfigurationLookupForPlanning() {
        return new CustomAggregationOperation(Document.parse("{\n" +
                "        $lookup:{\n" +
                "         from: \"activityConfiguration\",\n" +
                "            let: { unitId: \"$unitId\",phaseId:\"$currentPhaseId\" },\n" +
                "         pipeline: [\n" +
                "              { $match:\n" +
                "                 { $expr:\n" +
                "                    { $or:\n" +
                "                       [\n" +
                "                         {$eq:[\"$presencePlannedTime.phaseId\",\"$$phaseId\"]},\n" +
                "                         {$eq:[\"$absencePlannedTime.phaseId\",\"$$phaseId\"]},\n" +
                "                         {$eq:[\"$nonWorkingPlannedTime.phaseId\",\"$$phaseId\"]}\n" +
                "                       ]\n" +
                "                    }\n" +
                "                 }\n" +
                "              },{\n" +
                "                  $project:{\n" +
                "                      \"presencePlannedTime\":1,\n" +
                "                      \"absencePlannedTime\":1,\n" +
                "                      \"nonWorkingPlannedTime\":1,\n" +
                "                      \"_id\":0\n" +
                "                      }\n" +
                "                  }\n" +
                "           ],\n" +
                "            as: \"activityConfiguration\"\n" +
                "            }\n" +
                "        }"));
    }

    private CustomAggregationOperation getStaffingLevelLookupForPlanning() {
        return new CustomAggregationOperation(Document.parse("{\n" +
                "        $lookup:{\n" +
                "            \n" +
                "         from: \"staffing_level\",\n" +
                "            let: { startDate: \"$startDate\", endDate: \"$endDate\",unitId: \"$unitId\" },\n" +
                "         pipeline: [\n" +
                "              { $match:\n" +
                "                 { $expr:\n" +
                "                    { $and:\n" +
                "                       [\n" +
                "                         { $gte: [ \"$currentDate\",  \"$$startDate\" ] },\n" +
                "                         { $lte: [ \"$currentDate\", \"$$endDate\" ] },\n" +
                "                         {$eq:[\"$unitId\",\"$$unitId\"]}\n" +
                "{\n" +
                        "                  \"$eq\": [\n" +
                        "                    \"$deleted\",\n" +
                        "                    false\n" +
                        "                  ]\n" +
                        "                }"+
                "                       ]\n" +
                "                    }\n" +
                "                 }\n" +
                "              }\n" +
                "           ],\n" +
                "            as: \"staffingLevels\"\n" +
                "            }\n" +
                "        }"));
    }

    private CustomAggregationOperation getActivitiesLookupForPlanning() {
        return new CustomAggregationOperation(Document.parse("{\n" +
                "        $lookup:{\n" +
                "            \n" +
                "         from: \"activities\",\n" +
                "            let: { activityIds: \"$activityIds\",unitId:\"$unitId\" },\n" +
                "         pipeline: [\n" +
                "              { $match:{ $expr:\n" +
                "                    { $and:\n" +
                "                       [\n" +
                "                         { $in: [ \"$_id\",  \"$$activityIds\" ] },\n" +
                "                         \n" +
                "                       ]\n" +
                "                    }\n" +
                "                 }\n" +
                "           \n" +
                "              },{\n" +
                "                  \"$project\":{\"name\":1,\"activityBalanceSettings\":1,\"activityRulesSettings\":1,\"activityTimeCalculationSettings\":1,\"activitySkillSettings\":1,\"tags\":1,\"employmentTypes\":1}\n" +
                "                  }\n" +
                "           ],\n" +
                "            as: \"activities\"\n" +
                "            }\n" +
                "        }"));
    }

    private CustomAggregationOperation getProjectionWithReduceForPlanning(String breakActivityIds) {
        return new CustomAggregationOperation(Document.parse("{\n" +
                "      $project: {\n" +
                "          \"startDate\":1,\n" +
                "          \"endDate\":1,\n" +
                "          \"unitId\":1,\n" +
                "          \"currentPhaseId\":1,\n" +
                "          \"shifts\":1,\n" +
                "          \"staffingLevels\":1,\n" +
                "        \"activityIds\": {\n" +
                "          $reduce: {\n" +
                "            input: \"$activityIds\",\n" +
                "            initialValue:"+breakActivityIds+
                "            in: { $concatArrays: [ \"$$value\", \"$$this\" ] }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }"));
    }

    private CustomAggregationOperation getlookupOperationOfShiftsForPlanning(List<Long> staffIds) {
        return new CustomAggregationOperation(Document.parse("{" +
                "      \"$lookup\": {" +
                "        \"from\": \"shifts\",\n" +
                "        \"let\": {\n" +
                "          \"startDate\": \"$startDate\",\n" +
                "          \"endDate\": \"$endDate\",\n" +
                "          \"unitId\": \"$unitId\"\n" +
                "        },\n" +
                "        \"pipeline\": [\n" +
                "          {\n" +
                "            \"$match\": {\n" +
                "              \"$expr\": {\n" +
                "                \"$and\": [\n" +
                "                  {\n" +
                "                    \"$gte\": [\n" +
                "                      \"$startDate\",\n" +
                "                      \"$$startDate\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"$lte\": [\n" +
                "                      \"$endDate\",\n" +
                "                      \"$$endDate\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"$eq\": [\n" +
                "                      \"$unitId\",\n" +
                "                      \"$$unitId\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "\t\t{\n" +
                "                    \"$in\": [\n" +
                "                      \"$staffId\",\n" +
                                     staffIds.toString() +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"as\": \"shifts\"\n" +
                "      }\n" +
                "    }"));
    }

}