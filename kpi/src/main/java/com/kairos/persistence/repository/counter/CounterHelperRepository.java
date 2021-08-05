package com.kairos.persistence.repository.counter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.*;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.shift.ShiftType;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.AppConstants.DESCRIPTION;
import static com.kairos.constants.AppConstants.TIME_SLOT_SET;
import static com.kairos.constants.CommonConstants.DELETED;
import static com.kairos.constants.CommonConstants.DISABLED;
import static com.kairos.constants.KPIMessagesConstants.*;
import static com.kairos.enums.PriorityFor.NONE;
import static com.kairos.enums.TimeSlotType.SHIFT_PLANNING;
import static com.kairos.enums.shift.TodoStatus.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class CounterHelperRepository {

    public static final String DELETED = "deleted";
    public static final String UNIT_ID = "unitId";
    public static final String SHIFT_DATE = "shiftDate";
    public static final String TODO = "todo";
    public static final String STATUS = "status";
    public static final String COUNTRY_HOLIDAY_CALENDER = "countryHolidayCalender";
    public static final String ID2 = "id";
    public static final String UPPER_LEVEL_TIME_TYPE_ID = "upperLevelTimeTypeId";
    public static final String ACTIVITIES = "activities";
    public static final String PLANNING_PERIOD = "planningPeriod";
    public static final String DATE = "date";
    public static final String TIME_TYPE = "time_Type";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String SEQUENCE = "sequence";
    public static final String PHASE_TYPE = "phaseType";
    public static final String PHASES = "phases";
    public static final String COUNTRY_ID = "countryId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ACTIVE = "active";
    public static final String PUBLISH_EMPLOYMENT_IDS = "publishEmploymentIds";
    public static final String CURRENT_PHASE_ID = "currentPhaseId";
    public static final String PHASE = "phase";
    public static final String ID = ID2;
    public static final String NAME = "name";
    public static final String ID1 = "_id";
    public static final String C_TA_RULE_TEMPLATE = "cTARuleTemplate";
    public static final String RULE_TEMPLATE_IDS = "ruleTemplateIds";
    public static final String RULE_TEMPLATES = "ruleTemplates";
    public static final String EMPLOYMENT_ID = "employmentId";
    public static final String STAFF_ID = "staffId";
    public static final String SHIFTS = "shifts";
    public static final String WTA_BASE_RULE_TEMPLATE = "wtaBaseRuleTemplate";
    public static final String PROTECTED_DAYS_OFF_SETTING = "protectedDaysOffSetting";
    public static final String ACTIVITIES_ACTIVITY_ID = "activities.activityId";
    public static final String DRAFT ="draft";

    @Inject private MongoTemplate mongoTemplate;
    @Inject private ExceptionService exceptionService;

    public List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(COUNTRY_ID).in(countryId).and(DELETED).is(false)),
                Aggregation.lookup(COUNTRY_HOLIDAY_CALENDER,ID1,"dayTypeId","countryHolidayCalenderData")
        );
        return mongoTemplate.aggregate(aggregation, DayType.class,DayTypeDTO.class).getMappedResults();
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
        query.fields().include("name").include(ID2);
        return mongoTemplate.find(query,ActivityDTO.class, ACTIVITIES);
    }

    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        return mongoTemplate.find(new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),PresenceTypeDTO.class,"plannedTimeType");
    }

    public ShortcutDTO getShortcutById(BigInteger shortcutId) {
        return mongoTemplate.findOne(new Query(Criteria.where(ID1).is(shortcutId).and(DELETED).is(false)),ShortcutDTO.class,"shortcut");
    }

    public Map<BigInteger, TimeTypeDTO> getAllTimeTypeWithItsLowerLevel(Long countryId, Collection<BigInteger> timeTypeIds) {
        List<TimeTypeDTO> timeTypeDTOS =  getAllTimeType(null,countryId);
        Map<BigInteger,TimeTypeDTO> resultTimeTypeDTOS = new HashMap<>();
        updateTimeTypeList(resultTimeTypeDTOS,timeTypeDTOS.get(0).getChildren(), timeTypeIds,false);
        updateTimeTypeList(resultTimeTypeDTOS,timeTypeDTOS.get(1).getChildren(), timeTypeIds,false);
        return resultTimeTypeDTOS;
    }

    public List<TimeTypeDTO> getAllTimeType(BigInteger timeTypeId, Long countryId) {
        List<TimeTypeDTO> topLevelTimeTypes = mongoTemplate.find(new Query(Criteria.where(UPPER_LEVEL_TIME_TYPE_ID).exists(false).and(COUNTRY_ID).is(countryId).and(DELETED).is(false)),TimeTypeDTO.class, TIME_TYPE);
        List<TimeTypeDTO> timeTypeDTOS = new ArrayList<>(2);
        TimeTypeDTO workingTimeTypeDTO = new TimeTypeDTO(TimeTypes.WORKING_TYPE.toValue(), AppConstants.WORKING_TYPE_COLOR);
        TimeTypeDTO nonWorkingTimeTypeDTO = new TimeTypeDTO(TimeTypes.NON_WORKING_TYPE.toValue(), AppConstants.NON_WORKING_TYPE_COLOR);
        if (topLevelTimeTypes.isEmpty()) {
            timeTypeDTOS.add(workingTimeTypeDTO);
            timeTypeDTOS.add(nonWorkingTimeTypeDTO);
            return timeTypeDTOS;
        }
        List<TimeTypeDTO> timeTypes = mongoTemplate.find(new Query(Criteria.where(UPPER_LEVEL_TIME_TYPE_ID).exists(true).and(COUNTRY_ID).is(countryId).and(DELETED).is(false)),TimeTypeDTO.class, TIME_TYPE);
        List<TimeTypeDTO> parentOfWorkingTimeType = new ArrayList<>();
        List<TimeTypeDTO> parentOfNonWorkingTimeType = new ArrayList<>();
        for (TimeTypeDTO timeType : topLevelTimeTypes) {
            updateChildTimeTypeDetailsBeforeResponse(timeTypeId, timeTypes, parentOfWorkingTimeType, parentOfNonWorkingTimeType, timeType);
        }
        workingTimeTypeDTO.setChildren(parentOfWorkingTimeType);
        nonWorkingTimeTypeDTO.setChildren(parentOfNonWorkingTimeType);
        timeTypeDTOS.add(workingTimeTypeDTO);
        timeTypeDTOS.add(nonWorkingTimeTypeDTO);
        return timeTypeDTOS;
    }

    private void updateChildTimeTypeDetailsBeforeResponse(BigInteger timeTypeId, List<TimeTypeDTO> timeTypes, List<TimeTypeDTO> parentOfWorkingTimeType, List<TimeTypeDTO> parentOfNonWorkingTimeType, TimeTypeDTO timeType) {
        TimeTypeDTO timeTypeDTO = timeType;
        timeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
        if ( timeType.getId().equals(timeTypeId)) {
            timeTypeDTO.setSelected(true);
        }
        timeTypeDTO.setTimeTypes(timeType.getTimeTypes());
        timeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
        if (TimeTypes.WORKING_TYPE.toString().equals(timeType.getTimeTypes())) {
            parentOfWorkingTimeType.add(timeTypeDTO);
        } else {
            parentOfNonWorkingTimeType.add(timeTypeDTO);
        }
    }

    private List<TimeTypeDTO> getLowerLevelTimeTypeDTOs(BigInteger timeTypeId, BigInteger upperlevelTimeTypeId, List<TimeTypeDTO> timeTypes) {
        List<TimeTypeDTO> lowerLevelTimeTypeDTOS = new ArrayList<>();
        timeTypes.forEach(timeType -> {
            if (timeType.getUpperLevelTimeTypeId().equals(upperlevelTimeTypeId)) {
                TimeTypeDTO levelTwoTimeTypeDTO = timeType;
                if (timeTypeId != null && timeType.getId().equals(timeTypeId)) {
                    levelTwoTimeTypeDTO.setSelected(true);
                }
                levelTwoTimeTypeDTO.setSecondLevelType(timeType.getSecondLevelType());
                levelTwoTimeTypeDTO.setTimeTypes(timeType.getTimeTypes());
                levelTwoTimeTypeDTO.setChildren(getLowerLevelTimeTypeDTOs(timeTypeId, timeType.getId(), timeTypes));
                levelTwoTimeTypeDTO.setUpperLevelTimeTypeId(upperlevelTimeTypeId);
                lowerLevelTimeTypeDTOS.add(levelTwoTimeTypeDTO);
            }
        });
        return lowerLevelTimeTypeDTOS;
    }

    private void updateTimeTypeList(Map<BigInteger,TimeTypeDTO> resultTimeTypeDTOS, List<TimeTypeDTO> timeTypeDTOS, Collection<BigInteger> timeTypeIds, boolean addAllLowerLevelChildren){
        for(TimeTypeDTO timeTypeDTO : timeTypeDTOS) {
            if(timeTypeIds.contains(timeTypeDTO.getId())){
                resultTimeTypeDTOS.put(timeTypeDTO.getId(),timeTypeDTO);
                if(isCollectionNotEmpty(timeTypeDTO.getChildren())){
                    updateTimeTypeList(resultTimeTypeDTOS,timeTypeDTO.getChildren(), timeTypeIds,true);
                }
            }else if(addAllLowerLevelChildren){
                if(isCollectionNotEmpty(timeTypeDTO.getChildren())) {
                    updateTimeTypeList(resultTimeTypeDTOS, timeTypeDTO.getChildren(), timeTypeIds, true);
                }else{
                    resultTimeTypeDTOS.put(timeTypeDTO.getId(),timeTypeDTO);
                }
            }else{
                updateTimeTypeList(resultTimeTypeDTOS, timeTypeDTO.getChildren(), timeTypeIds, false);
            }
        }
    }

    public List<ActivityDTO> findAllByUnitIdAndTimeTypeIds(Long unitId, Set<BigInteger> lowerLevelTimeTypeIds) {
        return mongoTemplate.find(new Query(Criteria.where(UNIT_ID).is(unitId).and("activityBalanceSettings.timeTypeId").in(lowerLevelTimeTypeIds).and(DELETED).is(false)),ActivityDTO.class, ACTIVITIES);
    }

    public List<PlannedTimeType> getAllPlannedTimeByIds(List<BigInteger> plannedTimeTypeIds) {
        return mongoTemplate.find(new Query(Criteria.where(ID1).in(plannedTimeTypeIds).and(DELETED).is(false)),PlannedTimeType.class,"plannedTimeType");
    }

    public List<ActivityDTO> findAllActivitiesByIds(Set<BigInteger> activityIds) {
        return mongoTemplate.find(new Query(Criteria.where(ID1).in(activityIds).and(DELETED).is(false)),ActivityDTO.class, ACTIVITIES);
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
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        PlanningPeriodDTO planningPeriodDTO = results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
        return new DateTimeInterval(planningPeriodDTO.getStartDate(),planningPeriodDTO.getEndDate());
    }

    public PhaseDTO getCurrentPhaseByDateUsingPlanningPeriod(Long unitId, LocalDate localDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(ACTIVE).is(true).
                        and(START_DATE).lte(localDate).and(END_DATE).gte(localDate)),
                lookup(PHASES, CURRENT_PHASE_ID, ID1, PHASE),
                project().and(PHASE).arrayElementAt(0).as(PHASE),
                project("phase._id", "phase.name","phase.phaseEnum","phase.accessGroupIds","phase.organizationId")
        );
        AggregationResults<PhaseDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PhaseDTO.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
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
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriod.class, PlanningPeriodDTO.class);
        return results.getMappedResults();
    }

    public CTAResponseDTO getCTAByEmploymentIdAndDate(Long employmentId, Date date) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId)
                .orOperator(Criteria.where(START_DATE).lte(date).and(END_DATE).gte(date),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("tag", "tags", ID1, "tags"),
                lookup(C_TA_RULE_TEMPLATE, RULE_TEMPLATE_IDS, ID1, RULE_TEMPLATES)
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, "costTimeAgreement", CTAResponseDTO.class);
        return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
    }

    public List<TodoDTO> findAllByKpiFilter(Long unitId, Date startDate, Date endDate, List<Long> staffIds, Collection<String> todoStatus) {
        Criteria criteria=Criteria.where(UNIT_ID).is(unitId).and(STAFF_ID).in(staffIds).and("type").is(TodoType.APPROVAL_REQUIRED).and("shiftDate").gte(startDate).lt(endDate);
        if (ObjectUtils.isCollectionNotEmpty(todoStatus)) {
            criteria.and(STATUS).in(todoStatus);
        }
        if(todoStatus.contains(TodoStatus.DISAPPROVE.toString())){
            criteria.orOperator(Criteria.where(DELETED).is(false),criteria);
        }
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup(SHIFTS,"entityId",ID1, SHIFTS),
                Aggregation.project(ID2, STATUS, STAFF_ID).and(SHIFTS).arrayElementAt(0).as("shift"),
                Aggregation.project(ID2, STATUS, STAFF_ID).and("shift.startDate").as("shiftDateTime")
        );
        AggregationResults<TodoDTO> result = mongoTemplate.aggregate(aggregation, TODO, TodoDTO.class);
        return result.getMappedResults();
    }

    public List<DailyTimeBankEntry> findAllDailyTimeBankByIdsAndBetweenDates(Collection<Long> employmentIds, Date startDate, Date endDate) {
        return mongoTemplate.find(new Query(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and(DELETED).is(false).and(DATE).gte(startDate).lt(endDate)),DailyTimeBankEntry.class,"dailyTimeBankEntries");
    }
    public List<DailyTimeBankEntry> findAllDailyTimeBankByStaffIdsAndBetweenDates(List<Long> staffIds, LocalDate startDate, LocalDate endDate) {
        return mongoTemplate.find(new Query(Criteria.where(STAFF_ID).in(staffIds).and(DELETED).is(false).and(DATE).gte(startDate).lte(endDate)),DailyTimeBankEntry.class,"dailyTimeBankEntries");
    }

    public Set<BigInteger> findTimeTypeIdssByTimeTypeEnum(List<String> timeTypeEnums) {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timeTypes").in(timeTypeEnums)),
                Aggregation.project(ID2),
                Aggregation.unwind(ID2));
        AggregationResults<TimeTypeDTO> results = mongoTemplate.aggregate(aggregation, TimeType.class,TimeTypeDTO.class);
        return results.getMappedResults().stream().map(s-> s.getId()).collect(Collectors.toSet());
    }

    public Set<BigInteger> findAllTimeTypeIdsByTimeTypeIds(List<BigInteger> timeTypeIds) {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where(ID1).in(timeTypeIds)),
                Aggregation.graphLookup(TIME_TYPE).startWith(ID2).connectFrom(ID1).connectTo(UPPER_LEVEL_TIME_TYPE_ID).as("children"),
                Aggregation.project("children._id"),
                Aggregation.unwind(ID1));
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, TimeType.class,Map.class);
        return results.getMappedResults().stream().map(a->new BigInteger(a.get("_id").toString())).collect(Collectors.toSet());
    }

    public List<TimeTypeDTO> getAllTimeTypesByCountryId(Long countryId) {
        return mongoTemplate.find(new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),TimeTypeDTO.class, TIME_TYPE);
    }

    public List<ReasonCodeDTO> getReasonCodesByUnitId(Long refId, ReasonCodeType forceplan) {
        return mongoTemplate.find(new Query(Criteria.where(UNIT_ID).is(refId).and(DELETED).is(false).and("reasonCodeType").is(forceplan)),ReasonCodeDTO.class,"reasonCode");
    }
    public List<ShiftDTO> findAllByIdInAndDeletedFalseOrderByStartDateAsc(List<BigInteger> shiftIds) {
        return mongoTemplate.find(new Query(Criteria.where(ID1).is(shiftIds).and(DELETED).is(false)),ShiftDTO.class,SHIFTS);
    }
    public List<ShiftDTO> findAllByShiftIdsByAccessgroupRole(Set<BigInteger> shiftIds, Set<String> accessRole) {
        return mongoTemplate.find(new Query(Criteria.where("shiftId").is(shiftIds).and(DELETED).is(false).and("accessGroupRole").in(accessRole)),ShiftDTO.class,"shiftState");
    }

    public List<ShiftDTO> findShiftsByKpiFilters(List<Long> staffIds, List<Long> unitIds, List<String> shiftActivityStatus, Set<BigInteger> timeTypeIds, Date startDate, Date endDate){
        Criteria criteria = where(STAFF_ID).in(staffIds).and(UNIT_ID).in(unitIds).and(DELETED).is(false).and(DISABLED).is(false)
                .and(START_DATE).gte(startDate).lt(endDate);
        List<AggregationOperation> aggregationOperation = new ArrayList<>();
        aggregationOperation.add(new MatchOperation(criteria));
        aggregationOperation.add(unwind(ACTIVITIES));
        if (CollectionUtils.isNotEmpty(shiftActivityStatus)) {
            aggregationOperation.add(match(where("activities.status").in(shiftActivityStatus)));
        }
        if (CollectionUtils.isNotEmpty(timeTypeIds)) {
            aggregationOperation.add(lookup(ACTIVITIES, ACTIVITIES_ACTIVITY_ID, "_id", ACTIVITY));
            aggregationOperation.add(unwind(ACTIVITY));
            aggregationOperation.add(match(where("activity.activityBalanceSettings.timeTypeId").in(timeTypeIds)));
        }
        aggregationOperation.add(new CustomAggregationOperation(shiftWithActivityGroup()));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> unitIds, List<BigInteger> activitiesIds, List<Integer> dayOfWeeks, Date startDate, Date endDate, Boolean isDraft) {
        Criteria criteria = where(STAFF_ID).in(staffIds).and(UNIT_ID).in(unitIds).and(DELETED).is(false).and(DISABLED).is(false)
                .and(START_DATE).gte(startDate).lte(endDate).and(DRAFT).is(false);
        List<AggregationOperation> aggregationOperation = new ArrayList<>();
        aggregationOperation.add(new MatchOperation(criteria));
        aggregationOperation.add(unwind(ACTIVITIES));
        if (CollectionUtils.isNotEmpty(activitiesIds)) {
            aggregationOperation.add(match(where(ACTIVITIES_ACTIVITY_ID).in(activitiesIds)));
        }
        aggregationOperation.add(lookup(ACTIVITIES, ACTIVITIES_ACTIVITY_ID, ID1, ACTIVITY));
        aggregationOperation.add(new CustomAggregationOperation(shiftWithActivityKpiProjection()));
        if (CollectionUtils.isNotEmpty(dayOfWeeks)) {
            aggregationOperation.add(match(where("dayOfWeek").in(dayOfWeeks)));
        }
        aggregationOperation.add(new CustomAggregationOperation(Document.parse(groupByShiftAndActivity())));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        updateActivityInShift(result.getMappedResults());
        return result.getMappedResults();
    }

    private <T extends ShiftDTO> void updateActivityInShift(List<T> shiftWithActivityDTOS) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (T shift : shiftWithActivityDTOS) {
            activityIds.addAll(getActivityIdsByShift(shift));
            if(isNotNull(shift.getDraftShift())){
                activityIds.addAll(getActivityIdsByShift(shift.getDraftShift()));
            }
        }
        Map<BigInteger, ActivityDTO> activityDTOMap = getActivityDTOMap(activityIds);
        shiftWithActivityDTOS.forEach(shift -> {
            updateActivityInShiftActivities(activityDTOMap, shift.getActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
            updateActivityInShiftActivities(activityDTOMap, shift.getBreakActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
            if(isNotNull(shift.getDraftShift())){
                updateActivityInShiftActivities(activityDTOMap, shift.getDraftShift().getActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
                updateActivityInShiftActivities(activityDTOMap, shift.getDraftShift().getBreakActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
            }
        });
    }

    private <T extends ShiftActivityDTO> void updateActivityInShiftActivities(Map<BigInteger, ActivityDTO> activityDTOMap, List<T> shiftActivities, Long employmentId, BigInteger phaseId, ShiftType shiftType) {
        if(isCollectionNotEmpty(shiftActivities)) {
            shiftActivities.forEach(shiftActivityDTO -> {
                shiftActivityDTO.setEmploymentId(employmentId);
                shiftActivityDTO.setPhaseId(phaseId);
                shiftActivityDTO.setShiftType(shiftType);
                shiftActivityDTO.setActivity(activityDTOMap.get(shiftActivityDTO.getActivityId()));
                shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> childActivityDTO.setActivity(activityDTOMap.get(childActivityDTO.getActivityId())));
            });
        }
    }

    private <T extends ShiftDTO> Set<BigInteger> getActivityIdsByShift( T shift) {
        Set<BigInteger> activityIds = new HashSet<>();
        activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        activityIds.addAll(shift.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        if(isCollectionNotEmpty(shift.getBreakActivities())) {
            activityIds.addAll(shift.getBreakActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        }
        return activityIds;
    }

    private Map<BigInteger, ActivityDTO> getActivityDTOMap(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ID1).in(activityIds).and(DISABLED).is(false)),
                lookup(TIME_TYPE, "activityBalanceSettings.timeTypeId", ID1, TIME_TYPE)
                ,project(NAME,DESCRIPTION,COUNTRY_ID,UNIT_ID,"parentId","isParentActivity","activityGeneralSettings","activityBalanceSettings","activityRulesSettings","activityTimeCalculationSettings","activitySkillSettings")
                        .and(TIME_TYPE).arrayElementAt(0).as(TIME_TYPE));
        List<ActivityDTO> activityDTOS = mongoTemplate.aggregate(aggregation, ACTIVITIES, ActivityDTO.class).getMappedResults();
        return activityDTOS.stream().collect(Collectors.toMap(ActivityDTO::getId, v->v));
    }


    public List<ShiftDTO> findShiftBetweenDurationAndUnitIdAndDeletedFalse(Date startDate, Date endDate, List<Long> unitIds) {
        return mongoTemplate.find(new Query(Criteria.where(DELETED).is(false).and(UNIT_ID).in(unitIds).and(DISABLED).is(false).and(START_DATE).lte(endDate).and(END_DATE).gte(startDate)),ShiftDTO.class,SHIFTS);
    }

    public List<ShiftDTO> findAllShiftsByStaffIdsAndDate(List<Long> staffIds, LocalDateTime startDate, LocalDateTime endDate) {
        return mongoTemplate.find(new Query(Criteria.where(DELETED).is(false).and(STAFF_ID).in(staffIds).and(DISABLED).is(false).and(START_DATE).lte(endDate).and(END_DATE).gte(startDate)),ShiftDTO.class,SHIFTS);
    }

    public Collection<DailyTimeBankEntry> findAllByEmploymentIdsAndBeforDate(ArrayList<Long> employmentIds, Date endDate) {
        return mongoTemplate.find(new Query(Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and("date").lte(endDate)),DailyTimeBankEntry.class,"dailyTimeBankEntries");
    }

    public List<WTAResponseDTO> getWTAByEmploymentIdAndDates(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(endDate).and(END_DATE).gte(startDate),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, ID1, RULE_TEMPLATES),
                project(NAME, DESCRIPTION, DISABLED, START_DATE, END_DATE, RULE_TEMPLATES, EMPLOYMENT_ID)
        );
        AggregationResults<WTAResponseDTO> result = mongoTemplate.aggregate(aggregation, "workingTimeAgreement", WTAResponseDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftActivityDTO> findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(Long employmentId, Date startDate, Date endDate, Set<BigInteger> activityIds) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).lte(endDate).and(END_DATE).gte(startDate);
        } else {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).orOperator(Criteria.where(END_DATE).gte(startDate));
        }
        Aggregation aggregation = Aggregation.newAggregation(match(criteria.and(DRAFT).is(false)),
                unwind(ACTIVITIES),
                match(Criteria.where(ACTIVITIES_ACTIVITY_ID).in(activityIds)),
                new CustomAggregationOperation("{\n" +
                        "    \"$project\": {\n" +
                        "      \"status\": 1,\n" +
                        "      \"activityId\": 1,\n" +
                        "      \"startDate\": 1,\n" +
                        "      \"endDate\": 1,\n" +
                        "    }\n" +
                        "  }")
        );
        return mongoTemplate.aggregate(aggregation,Shift.class ,ShiftActivityDTO.class).getMappedResults();
    }

    public ProtectedDaysOffSettingDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOffSettingDTO protectedDaysOffSetting = mongoTemplate.findOne(new Query(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),ProtectedDaysOffSettingDTO.class, PROTECTED_DAYS_OFF_SETTING);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF,unitId);
        }
        return new ProtectedDaysOffSettingDTO(protectedDaysOffSetting.getId(), protectedDaysOffSetting.getUnitId(), protectedDaysOffSetting.getProtectedDaysOffUnitSettings());
    }
    public List<ProtectedDaysOffSettingDTO> getProtectedDaysOffByExpertiseId(Long expertiseId) {
        return mongoTemplate.find(new Query(Criteria.where("expertiseId").is(expertiseId).and(DELETED).is(false)),ProtectedDaysOffSettingDTO.class, PROTECTED_DAYS_OFF_SETTING);
    }

    private String groupByShiftAndActivity() {
        return "{'$group':{'_id':'$_id', 'durationMinutes':{'$first':'$durationMinutes'},\n" +
                "'staffId':{'$first':'$staffId'},'shiftType':{'$first':'$shiftType'},'startDate':{'$first':'$startDate'},'createdBy':{'$first':'$createdBy'},'endDate':{'$first':'$endDate'},'employmentId':{'$first':'$employmentId'},'phaseId':{'$first':'$phaseId'},'breakActivities':{'$first':'$breakActivities'},'activities':{'$addToSet':'$activities'}}}";
    }

    public static Document shiftWithActivityGroup() {
        String group = "{ '$group': {\n" +
                "    '_id': {\n" +
                "    '_id' : '$_id',\n" +
                "    'name' : '$name',\n" +
                "    'draft' : '$draft',\n" +
                "    'draftShift' : '$draftShift',\n" +
                "    'startDate' : '$startDate',\n" +
                "    'endDate' : '$endDate',\n" +
                "    'disabled' : '$disabled',\n" +
                "    'bid' :'$bid',\n" +
                "    'pId' :'$pId',\n" +
                "    'bonusTimeBank' : '$bonusTimeBank',\n" +
                "    'amount' : '$amount',\n" +
                "    'probability' : '$probability',\n" +
                "    'accumulatedTimeBankInMinutes' : '$accumulatedTimeBankInMinutes',\n" +
                "    'remarks' : '$remarks',\n" +
                "    'staffId' : '$staffId',\n" +
                "    'unitId' : '$unitId',\n" +
                "    'phaseId' : '$phaseId',\n" +
                "    'scheduledMinutes' : '$scheduledMinutes',\n" +
                "    'durationMinutes' :'$durationMinutes',\n" +
                "    'employmentId'  : '$employmentId' },\n" +
                "     'activities': { '$addToSet':'$activities'}\n" +
                "    }}";
        return Document.parse(group);
    }

    public static Document shiftWithActivityKpiProjection() {
        String project = "{  \n" +
                "      '$project':{  \n" +
                "         '_id' : 1,\n" +
                "         'breakActivities' : 1,\n" +
                "    'name' : 1,\n" +
                "    'durationMinutes' : 1,\n" +
                "        'scheduledMinutes':1,\n" +
                "        'timeBankCtaBonusMinutes':1,\n" +
                "        'scheduledMinutesOfTimebank':1,\n" +
                "        'scheduledMinutesOfPayout':1,\n" +
                "        'plannedMinutesOfPayout':1,\n" +
                "        'plannedMinutesOfTimebank':1,\n" +
                "        'payoutCtaBonusMinutes':1,\n" +
                "        'createdBy':1,\n" +
                "    'staffId' : 1,\n" +
                "    'shiftType' : 1,\n" +
                "    'startDate' : 1,\n" +
                "    'endDate' : 1,\n" +
                "    'employmentId' : 1,\n" +
                "    'phaseId' : 1,\n" +
                " 'dayOfWeek': { '$dayOfWeek': '$startDate' }\n" +
                getActivitiesProjection()+"   }}";

        return Document.parse(project);
    }

    private static String getActivitiesProjection() {
        return "\t'activities.id' : 1,\n" +
                "        'activities.activityId' : 1,\n" +
                "        'activities.durationMinutes' : 1,\n" +
                "        'activities._id' : 1,\n" +
                "        'activities.breakShift' : 1,\n" +
                "        'activities.allowedBreakDurationInMinute' : 1,\n" +
                "        'activities.scheduledMinutes':1,\n" +
                "        'activities.activityName':1,\n" +
                "        'activities.status':1,\n" +
                "        'activities.timeBankCtaBonusMinutes':1,\n" +
                "        'activities.scheduledMinutesOfTimebank':1,\n" +
                "        'activities.scheduledMinutesOfPayout':1,\n" +
                "        'activities.plannedMinutesOfPayout':1,\n" +
                "        'activities.plannedMinutesOfTimebank':1,\n" +
                "        'activities.absenceReasonCodeId' : 1,\n" +
                "        'activities.reasonCodeId' : 1,\n" +
                "        'activities.remarks' : 1,\n" +
                "        'activities.payoutCtaBonusMinutes':1,\n" +
                "        'activities.plannedTimes':1,\n" +
                "        'activities.startDate' : 1,\n" +
                "        'activities.endDate' : 1,\n" +
                "        'activities.description':{ '$arrayElemAt':['$activityObject.description',0] }\n" +
                "        'activities.backgroundColor':{'$arrayElemAt':[ '$activity.activityGeneralSettings.backgroundColor',0]}\n";

    }

    public class CustomAggregationOperation implements AggregationOperation {

        private Document operation;

        public CustomAggregationOperation(Document operation) {
            this.operation = operation;
        }

        public CustomAggregationOperation(String operation) {
            this.operation = Document.parse(operation);
        }

        @Override
        public Document toDocument(AggregationOperationContext context) {
            return context.getMappedObject(operation);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @NoArgsConstructor
    public class PlanningPeriod extends MongoBaseEntity {

        private LocalDate startDate;
        private LocalDate endDate;
        private String name;
        private String dateRange;
        @Indexed
        private Long unitId = -1L;
        private BigInteger currentPhaseId;
        private BigInteger nextPhaseId;
        private int duration;
        private DurationType durationType;
        private boolean active = true;
        private Set<Long> publishEmploymentIds = new HashSet<>();
    }

    @Getter
    @Setter
    public class Shift {
        protected Date startDate;
        protected Date endDate;
        protected Integer shiftStartTime;//In Second
        protected Integer shiftEndTime;//In Second
        protected boolean disabled = false;
        @NotNull(message = "error.ShiftDTO.staffId.notnull")
        protected Long staffId;
        protected BigInteger phaseId;
        protected BigInteger planningPeriodId;
        @Indexed
        protected Long unitId;
        protected int scheduledMinutes;
        protected int durationMinutes;
        @NotEmpty(message = "message.shift.activity.empty")
        protected List<ShiftActivity> activities;
    }

    @org.springframework.data.mongodb.core.mapping.Document(collection = "time_Type")
    @Getter
    @Setter
    @NoArgsConstructor
    public class TimeType extends MongoBaseEntity implements Serializable {

        private static final long serialVersionUID = 3265660403399363722L;
        private Long countryId;
        private TimeTypes timeTypes;
        private BigInteger upperLevelTimeTypeId;
        private String label;
        private boolean leafNode;
        private String description;
        private List<BigInteger> childTimeTypeIds = new ArrayList<>();
        private String backgroundColor;
        private TimeTypeEnum secondLevelType;
        private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;
        private boolean partOfTeam;
        private boolean allowChildActivities;
        private boolean allowedConflicts;
        private ActivityPhaseSettings activityPhaseSettings;
        private List<Long> expertises;
        private List<Long> organizationTypes;
        private List<Long> organizationSubTypes;
        private List<Long> regions;
        private List<Long> levels;
        private List<Long> employmentTypes;
        private boolean breakNotHeldValid;
        private PriorityFor priorityFor = NONE;
        private boolean sicknessSettingValid;
        private Map<String, BigInteger> upperLevelTimeTypeDetails;
        //this setting for unity graph
        private UnityActivitySetting unityActivitySetting;
    }

    @org.springframework.data.mongodb.core.mapping.Document
    @Getter
    @Setter
    public class DayType extends MongoBaseEntity {
        private static final long serialVersionUID = 5594442948746712580L;
        @NotBlank(message = "error.DayType.name.notEmpty")
        private String name;
        @NotNull
        int code;
        private String description;
        private String colorCode;
        private Long countryId;
        private List<Day> validDays = new ArrayList<>();
        private boolean holidayType;
        private boolean isEnabled = true;
        private boolean allowTimeSettings;
    }
}
