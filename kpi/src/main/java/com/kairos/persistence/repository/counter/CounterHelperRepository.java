package com.kairos.persistence.repository.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.PlannedTimeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.AppConstants.TIME_SLOT_SET;
import static com.kairos.constants.CommonConstants.DELETED;
import static com.kairos.constants.KPIMessagesConstants.TIMESLOT_NOT_FOUND_FOR_UNIT;
import static com.kairos.enums.TimeSlotType.SHIFT_PLANNING;
import static com.kairos.enums.shift.TodoStatus.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class CounterHelperRepository {

    public static final String DELETED = "deleted";
    public static final String UNIT_ID = "unitId";
    public static final String SHIFT_DATE = "shiftDate";
    public static final String TODO = "todo";
    public static final String STATUS = "status";
    public static final String COUNTRY_HOLIDAY_CALENDER = "countryHolidayCalender";
    @Inject private MongoTemplate mongoTemplate;
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String SEQUENCE = "sequence";
    public static final String PHASE_TYPE = "phaseType";
    public static final String PHASES = "phases";
    public static final String COUNTRY_ID = "countryId";
    public static final String DURATION = "duration";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ACTIVE = "active";
    public static final String DURATION_TYPE = "durationType";
    public static final String PHASE_FLIPPING_DATE = "phaseFlippingDate";
    public static final String PUBLISH_EMPLOYMENT_IDS = "publishEmploymentIds";
    public static final String CURRENT_PHASE_DATA_NAME = "current_phase_data.name";
    public static final String CURRENT_PHASE = "currentPhase";
    public static final String NEXT_PHASE_DATA_NAME = "next_phase_data.name";
    public static final String CURRENT_PHASE_ID = "currentPhaseId";
    public static final String CURRENT_PHASE_DATA = "current_phase_data";
    public static final String NEXT_PHASE_ID = "nextPhaseId";
    public static final String NEXT_PHASE_DATA = "next_phase_data";
    public static final String PHASE = "phase";
    public static final String DATE_RANGE = "dateRange";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PHASE_ID = "phaseId";
    public static final String CURRENT_PHASE_NAME = "currentPhaseName";
    public static final String NEXT_PHASE_NAME = "nextPhaseName";
    public static final String ID1 = "_id";
    public static final String NEXT_PHASE = "nextPhase";
    public static final String DATA = "data";
    public static final String C_TA_RULE_TEMPLATE = "cTARuleTemplate";
    public static final String RULE_TEMPLATE_IDS = "ruleTemplateIds";
    public static final String RULE_TEMPLATES = "ruleTemplates";
    public static final String EMPLOYMENT_ID = "employmentId";
    public static final String PARENT_ID = "parentId";
    public static final String ORGANIZATION_PARENT_ID = "organizationParentId";
    public static final String DESCRIPTION = "description";
    public static final String STAFF_ID = "staffId";
    public static final String SHIFTS = "shifts";

    public List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(COUNTRY_ID).in(countryId).and(DELETED).is(false)),
                Aggregation.lookup(COUNTRY_HOLIDAY_CALENDER,"_id","dayTypeId","countryHolidayCalenderData")
        );
        return mongoTemplate.aggregate(aggregation, "dayType",DayTypeDTO.class).getMappedResults();
    }

    public List<CountryHolidayCalenderDTO> getAllByCountryIdAndHolidayDateBetween(Long countryId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where(COUNTRY_ID).in(countryId).and(DELETED).is(false).and("holidayDate").gte(startDate).lte(endDate);
        return mongoTemplate.find(new Query(criteria),CountryHolidayCalenderDTO.class, COUNTRY_HOLIDAY_CALENDER);
    }

    public List<TimeSlotDTO> getUnitTimeSlot(Long organizationId) {
        return findByUnitIdAndTimeSlotTypeOrderByStartDate(organizationId,SHIFT_PLANNING).getTimeSlots();
    }

    public TimeSlotSetDTO findByUnitIdAndTimeSlotTypeOrderByStartDate(Long refId, TimeSlotType shiftPlanning) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(AppConstants.UNIT_ID).is(refId)),
                Aggregation.lookup(TIME_SLOT_SET, AppConstants.UNIT_ID, AppConstants.UNIT_ID, TIME_SLOT_SET),
                Aggregation.unwind(TIME_SLOT_SET),
                Aggregation.replaceRoot(TIME_SLOT_SET),
                Aggregation.match(Criteria.where("timeSlotType").is(shiftPlanning.toString()))
        );
        List<TimeSlotSetDTO> timeSlotSetDTOS =  mongoTemplate.aggregate(aggregation, "unitSetting",TimeSlotSetDTO.class).getMappedResults();
        if(isCollectionEmpty(timeSlotSetDTOS)){
            throwException(TIMESLOT_NOT_FOUND_FOR_UNIT);
        }
        return timeSlotSetDTOS.get(0);
    }

    public List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitIds) {
        Query query = new Query(Criteria.where(UNIT_ID).in(unitIds).and(DELETED).is(false));
        query.fields().include("name").include("id");
        return mongoTemplate.find(query,ActivityDTO.class,"activities");
    }

    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        return mongoTemplate.find(new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),PresenceTypeDTO.class,"plannedTimeType");
    }

    public ShortcutDTO getShortcutById(BigInteger shortcutId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(shortcutId).and(DELETED).is(false)),ShortcutDTO.class,"shortcut");
    }

    public Map<BigInteger, TimeTypeDTO> getAllTimeTypeWithItsLowerLevel(Long countryId, Collection<BigInteger> timeTypeIds) {
        return null;
    }

    public List<ActivityDTO> findAllByUnitIdAndTimeTypeIds(Long unitId, Set<BigInteger> lowerLevelTimeTypeIds) {
        return mongoTemplate.find(new Query(Criteria.where(UNIT_ID).is(unitId).and("activityBalanceSettings.timeTypeId").in(lowerLevelTimeTypeIds).and(DELETED).is(false)),ActivityDTO.class,"activities");
    }

    public List<PlannedTimeType> getAllPlannedTimeByIds(List<BigInteger> plannedTimeTypeIds) {
        return mongoTemplate.find(new Query(Criteria.where("_id").in(plannedTimeTypeIds).and(DELETED).is(false)),PlannedTimeType.class,"plannedTimeType");
    }

    public List<ActivityDTO> findAllActivitiesByIds(Set<BigInteger> activityIds) {
        return mongoTemplate.find(new Query(Criteria.where("_id").in(activityIds).and(DELETED).is(false)),ActivityDTO.class,"activities");
    }

    public List<TodoDTO> getAllTodoByEntityIds(Long unitId,Date startDate, Date endDate) {
        Query query = new Query(Criteria.where(UNIT_ID).in(unitId).and(DELETED).is(false).and("requestedOn").gte(startDate).lte(endDate).and(STATUS).in(newArrayList(APPROVE,DISAPPROVE, REQUESTED,PENDING,VIEWED)));
        return mongoTemplate.find(query,TodoDTO.class, TODO);
    }

    public List<TodoDTO> getAllTodoByShiftDate(Long unitId,Date startDate, Date endDate) {
        Query query = new Query(Criteria.where(UNIT_ID).in(unitId).and(DELETED).is(false).and(SHIFT_DATE).gte(startDate).lte(endDate).and(STATUS).in(newArrayList(APPROVE,DISAPPROVE, REQUESTED,PENDING,VIEWED)));
        return mongoTemplate.find(query,TodoDTO.class, TODO);
    }

    public List<TodoDTO> getAllTodoByDateTimeIntervalAndTodoStatus(Long unitId,Date startDate, Date endDate, List<TodoStatus> todoStatuses) {
        Query query = new Query(Criteria.where(UNIT_ID).in(unitId).and(DELETED).is(false).and(SHIFT_DATE).gte(startDate).lte(endDate).and(STATUS).in(todoStatuses));
        return mongoTemplate.find(query,TodoDTO.class, TODO);
    }

    public List<PhaseDTO> getPhasesByUnit(Long organizationId) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false));
        query.with(Sort.by(Sort.Direction.ASC, SEQUENCE).and(Sort.by(Sort.Direction.DESC, PHASE_TYPE)));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }

    public List<StaffingLevelDTO> findByUnitIdAndDates(Long unitId, Date startDate, Date endDate) {
        Query query = new Query(Criteria.where(UNIT_ID).in(unitId).and(DELETED).and("currentDate").gte(startDate).lte(endDate));
        return mongoTemplate.find(query, StaffingLevelDTO.class, "staffing_level");
    }

    public PhaseDTO findByUnitIdAndPhaseEnum(Long unitId, String phaseName) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("phaseEnum").is(phaseName));
        return mongoTemplate.findOne(query, PhaseDTO.class, PHASES);
    }

    public DateTimeInterval getPlanningPeriodIntervalByUnitId(Long unitId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                sort(Sort.Direction.ASC, START_DATE),
                group(UNIT_ID).first(START_DATE).as(START_DATE).last(END_DATE).as(END_DATE),
                project().and(START_DATE).as(START_DATE).and(END_DATE).as(END_DATE)
        );
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriodDTO.class, PlanningPeriodDTO.class);
        PlanningPeriodDTO planningPeriodDTO = results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
        return new DateTimeInterval(planningPeriodDTO.getStartDate(),planningPeriodDTO.getEndDate());
    }

    public PhaseDTO getCurrentPhaseByDateUsingPlanningPeriod(Long unitId, LocalDate localDate) {
        return null;
    }

    public List<PhaseDTO> findByOrganizationIdAndPhaseTypeAndDeletedFalse(Long unitId, String phaseType) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("phaseType").is(phaseType));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }

    public List<PlanningPeriodDTO> findAllPlanningPeriodBetweenDatesAndUnitId(Long unitId, Date startDate, Date endDate) {
        ProjectionOperation projectionOperation = Aggregation.project().
                and(ID).as(ID).
                andInclude(NAME).
                andInclude(START_DATE).
                andInclude(END_DATE).
                andInclude(PUBLISH_EMPLOYMENT_IDS).
                and(CURRENT_PHASE_ID).as(CURRENT_PHASE_ID);
        Aggregation aggregation = newAggregation(
                match(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).and(START_DATE).lte(startDate).and(END_DATE).gte(endDate)),
                sort(Sort.Direction.ASC, START_DATE),
                projectionOperation
        );
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, "planningPeriod", PlanningPeriodDTO.class);
        return results.getMappedResults();
    }

    public CTAResponseDTO getCTAByEmploymentIdAndDate(Long employmentId, Date date) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId)
                .orOperator(Criteria.where(START_DATE).lte(date).and(END_DATE).gte(date),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("tag", "tags", "_id", "tags"),
                lookup(C_TA_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES)
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, "costTimeAgreement", CTAResponseDTO.class);
        return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
    }

    public List<TodoDTO> findAllByKpiFilter(Long unitId, Date startDate, Date endDate, List<Long> staffIds, Collection<String> todoStatus) {
        Criteria criteria=Criteria.where("unitId").is(unitId).and(STAFF_ID).in(staffIds).and("type").is(TodoType.APPROVAL_REQUIRED).and("shiftDate").gte(startDate).lt(endDate);
        if (ObjectUtils.isCollectionNotEmpty(todoStatus)) {
            criteria.and(STATUS).in(todoStatus);
        }
        if(todoStatus.contains(TodoStatus.DISAPPROVE.toString())){
            criteria.orOperator(Criteria.where("deleted").is("false"),criteria);
        }
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup(SHIFTS,"entityId","_id", SHIFTS),
                Aggregation.project("id", STATUS, STAFF_ID).and(SHIFTS).arrayElementAt(0).as("shift"),
                Aggregation.project("id", STATUS, STAFF_ID).and("shift.startDate").as("shiftDateTime")
        );
        AggregationResults<TodoDTO> result = mongoTemplate.aggregate(aggregation, TODO, TodoDTO.class);
        return result.getMappedResults();
    }
}
