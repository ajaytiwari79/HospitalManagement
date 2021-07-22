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
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.DailyTimeBankEntry;
import com.kairos.persistence.model.PlannedTimeType;
import com.kairos.persistence.model.Shift;
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

    @Inject private MongoTemplate mongoTemplate;

    public List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(COUNTRY_ID).in(countryId).and(DELETED).is(false)),
                Aggregation.lookup(COUNTRY_HOLIDAY_CALENDER,ID1,"dayTypeId","countryHolidayCalenderData")
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
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PlanningPeriodDTO.class, PlanningPeriodDTO.class);
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
        AggregationResults<PhaseDTO> results = mongoTemplate.aggregate(aggregation, PLANNING_PERIOD, PhaseDTO.class);
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
        AggregationResults<PlanningPeriodDTO> results = mongoTemplate.aggregate(aggregation, PLANNING_PERIOD, PlanningPeriodDTO.class);
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
        AggregationResults<TimeTypeDTO> results = mongoTemplate.aggregate(aggregation, TIME_TYPE,TimeTypeDTO.class);
        return results.getMappedResults().stream().map(s-> s.getId()).collect(Collectors.toSet());
    }

    public Set<BigInteger> findAllTimeTypeIdsByTimeTypeIds(List<BigInteger> timeTypeIds) {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where(ID2).in(timeTypeIds)),
                Aggregation.graphLookup(TIME_TYPE).startWith(ID2).connectFrom(ID1).connectTo(UPPER_LEVEL_TIME_TYPE_ID).as("children"),
                Aggregation.project("children._id"),
                Aggregation.unwind(ID1))
                ;
        AggregationResults<TimeTypeDTO> results = mongoTemplate.aggregate(aggregation, TIME_TYPE,TimeTypeDTO.class);
        return results.getMappedResults().stream().map(s-> s.getId()).collect(Collectors.toSet());
    }

    public List<TimeTypeDTO> getAllTimeTypesByCountryId(Long countryId) {
        return mongoTemplate.find(new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),TimeTypeDTO.class, TIME_TYPE);
    }

    public List<ReasonCodeDTO> getReasonCodesByUnitId(Long refId, ReasonCodeType forceplan) {
        return mongoTemplate.find(new Query(Criteria.where(UNIT_ID).is(refId).and(DELETED).is(false).and("reasonCodeType").is(forceplan)),ReasonCodeDTO.class,"reasonCode");
    }
    public List<ShiftDTO> findAllByIdInAndDeletedFalseOrderByStartDateAsc(List<BigInteger> shiftIds) {
        return mongoTemplate.find(new Query(Criteria.where(ID1).is(shiftIds).and(DELETED).is(false)),ShiftDTO.class,"shifts");
    }
    public List<ShiftDTO> findAllByShiftIdsByAccessgroupRole(Set<BigInteger> shiftIds, Set<String> accessRole) {
        return mongoTemplate.find(new Query(Criteria.where("shiftId").is(shiftIds).and(DELETED).is(false).and("accessGroupRole").in(accessRole)),ShiftDTO.class,"shiftState");
    }

    public List<Shift> findShiftsByKpiFilters(List<Long> kpiDatum, List<Long> longs, List<String> objects, Set<BigInteger> objects1, Date startDate, Date endDate) {
        return null;
    }

    public List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> longs, List<BigInteger> objects, List<Integer> dayOfWeeksNo, Date startDate, Date endDate, Boolean b) {
        return null;
    }
    public List<Shift> findShiftBetweenDurationAndUnitIdAndDeletedFalse(Date startDate, Date endDate, List<Long> longs) {
        return null;
    }
    public List<Shift> findAllShiftsByStaffIdsAndDate(List<Long> staffIds, LocalDateTime localDateTimeFromLocalDate, LocalDateTime localDateTimeFromLocalDate1) {
        return null;
    }
    public Collection<DailyTimeBankEntry> findAllByEmploymentIdsAndBeforDate(ArrayList<Long> longs, Date endDate) {
        return null;
    }
    public List<WTAResponseDTO> getWTAByEmploymentIdAndDates(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(endDate).and(END_DATE).gte(startDate),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, START_DATE, END_DATE, RULE_TEMPLATES, EMPLOYMENT_ID)
        );
        AggregationResults<WTAResponseDTO> result = mongoTemplate.aggregate(aggregation, "workingTimeAgreement", WTAResponseDTO.class);
        return result.getMappedResults();
    }
}
