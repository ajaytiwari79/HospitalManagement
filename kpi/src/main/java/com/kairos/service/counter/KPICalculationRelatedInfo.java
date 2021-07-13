package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.AuditShiftDTO;
import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.PlannedTimeType;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.utils.counter.KPIUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.HOURS;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.kpi.CalculationType.*;
import static com.kairos.enums.wta.WTATemplateType.PROTECTED_DAYS_OFF;
import static com.kairos.enums.wta.WTATemplateType.*;
import static com.kairos.utils.counter.KPIUtils.getBigIntegerSet;
import static com.kairos.utils.counter.KPIUtils.*;

@Getter
@Setter
@NoArgsConstructor
public class KPICalculationRelatedInfo {

    private KPIBuilderCalculationService kpiBuilderCalculationService;

    private Map<FilterType, List> filterBasedCriteria = new HashMap<>();
    private List<ShiftWithActivityDTO> shifts = new ArrayList<>();
    private List<Long> staffIds = new ArrayList<>();
    private List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
    private List<StaffKpiFilterDTO> staffKpiFilterDTOS = new ArrayList<>();
    private Long unitId;
    private ApplicableKPI applicableKPI;
    private KPI kpi;
    private Map<DateTimeInterval, List<ShiftWithActivityDTO>> intervalShiftsMap = new HashMap<>();
    private Map<Long, StaffKpiFilterDTO> staffIdAndStaffKpiFilterMap = new HashMap<>();
    private Map<Long, List<ShiftWithActivityDTO>> staffIdAndShiftsMap = new HashMap<>();
    private Map<Long, List<TodoDTO>> staffIdAndTodoMap = new HashMap<>();
    private Set<Long> employmentIds = new HashSet<>();
    private Date startDate;
    private Date endDate;
    private Set<DayOfWeek> daysOfWeeks = new HashSet<>();
    private Map<Long, Collection<DailyTimeBankEntry>> employmentIdAndDailyTimebankEntryMap = new HashMap<>();
    private Map<Long, Collection<DailyTimeBankEntry>> staffIdAndDailyTimebankEntryMap = new HashMap<>();
    private Collection<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
    private List<Long> employmentTypeIds = new ArrayList<>();
    private Map<BigInteger, Activity> activityMap = new HashMap<>();
    private Map<BigInteger, TimeTypeDTO> timeTypeMap = new HashMap<>();
    private Map<BigInteger, PlannedTimeType> plannedTimeMap = new HashMap<>();
    private KPIBuilderCalculationService.ShiftActivityCriteria currentShiftActivityCriteria;
    private List<TodoDTO> filterActivityTodoDto = new ArrayList<>();
    private List<YAxisConfig> yAxisConfigs = new ArrayList<>();
    private List<XAxisConfig> xAxisConfigs = new ArrayList<>();
    private List<CalculationType> calculationTypes = new ArrayList<>();
    private List<EmploymentSubType> employmentSubTypes = new ArrayList<>();
    private CalculationType currentCalculationType;
    private DateTimeInterval planningPeriodInterval;
    private List<TodoDTO> todoDTOS = new ArrayList<>();
    private Map<BigInteger, List<TodoDTO>> activityIdAndTodoListMap = new HashMap<>();
    private Set<BigInteger> activityIds = new HashSet<>();
    private Map<BigInteger,List<TodoDTO>> timeTypeTodoListMap =new HashMap<>();
    private Boolean isDraft;
    private List<CountryHolidayCalenderDTO> holidayCalenders = new ArrayList<>();
    private List<TimeSlotDTO> timeSlotDTOS = new ArrayList<>();
    private Map<Long, List<AuditShiftDTO>> staffAuditLog = new HashMap<>();
    private Set<Long> tagIds = new HashSet<>();
    private Map<Long,Map<BigInteger,List<TodoDTO>>> staffIdAndActivityTodoListMap = new HashMap<>();
    private Map<Long,Map<BigInteger,List<TodoDTO>>> staffIdAndTimeTypeTodoListMap = new HashMap<>();
    private Map<Long,Map<BigInteger,List<Shift>>> staffIdAndActivityIdAndShiftMap = new HashMap<>();
    private Map<Long,Map<BigInteger,List<Shift>>> staffIdAndTimeTypeIdAndShiftMap = new HashMap<>();
    private Map<BigInteger,List<Shift>> activityIdAndShiftListMap =new HashMap<>();
    private Map<BigInteger,List<Shift>> timeTypeIdAndShiftListMap =new HashMap();

    public KPICalculationRelatedInfo(Map<FilterType, List> filterBasedCriteria, Long unitId, ApplicableKPI applicableKPI, KPI kpi,KPIBuilderCalculationService kpiBuilderCalculationService) {
        this.kpiBuilderCalculationService = kpiBuilderCalculationService;
        this.filterBasedCriteria = filterBasedCriteria;
        this.unitId = unitId;
        this.applicableKPI = applicableKPI;
        this.kpi = kpi;
        yAxisConfigs = ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(filterBasedCriteria.get(FilterType.CALCULATION_BASED_ON), YAxisConfig.class));
        ObjectUtils.isNullOrEmptyThrowException(yAxisConfigs);
        xAxisConfigs = ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(filterBasedCriteria.get(FilterType.CALCULATION_UNIT), XAxisConfig.class));
        ObjectUtils.isNullOrEmptyThrowException(xAxisConfigs);
        calculationTypes = ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(filterBasedCriteria.get(FilterType.CALCULATION_TYPE), CalculationType.class));
        calculationTypes = ObjectUtils.isCollectionNotEmpty(calculationTypes) ? calculationTypes : ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(yAxisConfigs, CalculationType.class));
        employmentSubTypes = ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(filterBasedCriteria.get(FilterType.EMPLOYMENT_SUB_TYPE), EmploymentSubType.class));
        loadKpiCalculationRelatedInfo(filterBasedCriteria, unitId, applicableKPI);
        employmentIds = staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toSet());
        getTodoDetails();
        getDailyTimeBankEntryByDate();
        getActivityTodoList();
        getActivityIdMap();
        getTimeTypeTodoList();
        updateActivityAndTimeTypeAndPlannedTimeMap();
        planningPeriodInterval = kpiBuilderCalculationService.getPlanningPeriodService().getPlanningPeriodIntervalByUnitId(unitId);
        getDailyTimeBankEntryByEmploymentId();
    }

    public void getActivityTodoList() {
        if (CollectionUtils.containsAny(yAxisConfigs, ObjectUtils.newHashSet(YAxisConfig.PLANNING_QUALITY_LEVEL, CalculationType.ABSENCE_REQUEST))) {
            todoDTOS = kpiBuilderCalculationService.getTodoService().getAllTodoByEntityIds(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
            activityIdAndTodoListMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
        }
    }


    public void getTimeTypeTodoList(){
        if (CollectionUtils.containsAny(yAxisConfigs, ObjectUtils.newHashSet(YAxisConfig.TIME_TYPE))) {
            getUpdateTodoStatus();
            getUpdateTodoDTOSByDayOfWeek(todoDTOS);
            getTimeTodoListMap();
            staffIdAndTodoMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getStaffId, Collectors.toList()));
            staffIdAndTimeTypeTodoListMap = getStaffIdBigIntegerIdTodoListMap(staffIdAndTimeTypeTodoListMap,staffIdAndTodoMap);
            updateActivityIdShiftListMap(timeTypeTodoListMap,timeTypeIdAndShiftListMap);
            if(XAxisConfig.HOURS.equals(xAxisConfigs.get(0))){
                staffIdAndTimeTypeIdAndShiftMap = getStaffIdAndActivityIdAndShiftMap(staffIdAndTimeTypeTodoListMap);
            }

        }

    }

    public void updateActivityIdShiftListMap(Map<BigInteger,List<TodoDTO>> activityIdTodoListMap, Map<BigInteger,List<Shift>> ActivityIdShiftListMap ){
        if(CalculationType.TODO_STATUS.equals(calculationTypes.get(0))) {
            for (Map.Entry<BigInteger, List<TodoDTO>> entry : activityIdTodoListMap.entrySet()) {
                List<BigInteger> shiftIds = entry.getValue().stream().map(TodoDTO::getEntityId).collect(Collectors.toList());
                List<Shift> shiftList = kpiBuilderCalculationService.getShiftMongoRepository().findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftIds);
                ActivityIdShiftListMap.put(entry.getKey(), shiftList);

            }
        }
    }

    private void getUpdateTodoDTOSByDayOfWeek(List<TodoDTO> todoDTOList) {
        if (filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK)) {
            todoDTOS = todoDTOList.stream().filter(todoDTO -> daysOfWeeks.contains(todoDTO.getShiftDate().getDayOfWeek())).collect(Collectors.toList());
        }
    }

    public Map<Long,Map<BigInteger,List<TodoDTO>>> getStaffIdBigIntegerIdTodoListMap(Map<Long,Map<BigInteger,List<TodoDTO>>> staffIdAndBigIntegerTodoListMap,Map<Long,List<TodoDTO>> longTodoListMap) {
        for (Map.Entry<Long, List<TodoDTO>> entry : longTodoListMap.entrySet()) {
            Map<BigInteger,List<TodoDTO>> bigIntegerListMap = entry.getValue().stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
            staffIdAndBigIntegerTodoListMap.put(entry.getKey(),bigIntegerListMap);
        }
        return staffIdAndBigIntegerTodoListMap;
    }

    private Map<Long, Map<BigInteger, List<Shift>>> getStaffIdAndActivityIdAndShiftMap(Map<Long,Map<BigInteger,List<TodoDTO>>> staffIdActivityIdTodoListMap) {
        Map<Long,Map<BigInteger,List<Shift>>> staffIdAndActivityAndShiftMap = new HashMap<>();
        Map<BigInteger,List<Shift>> activityIdShiftListMap = new HashMap<>();
        for(Map.Entry<Long,Map<BigInteger,List<TodoDTO>>> entry :staffIdActivityIdTodoListMap.entrySet()){
            for(Map.Entry<BigInteger,List<TodoDTO>> bigIntegerListEntry :entry.getValue().entrySet()){
                List<BigInteger> shiftIds =bigIntegerListEntry.getValue().stream().map(TodoDTO::getEntityId).collect(Collectors.toList());
                List<Shift> shiftList =kpiBuilderCalculationService.getShiftMongoRepository().findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftIds);
                activityIdShiftListMap.put(bigIntegerListEntry.getKey(),shiftList);
            }
            staffIdAndActivityAndShiftMap.put(entry.getKey(),activityIdShiftListMap);
            activityIdShiftListMap =new HashMap<>();
        }
        return staffIdAndActivityAndShiftMap;
    }

    private void getTimeTodoListMap() {
        if(CalculationType.TODO_STATUS.equals(calculationTypes.get(0))) {
            Set<BigInteger> activityIdList = activityMap.keySet();
            if (ObjectUtils.isCollectionNotEmpty(todoDTOS)) {
                todoDTOS = todoDTOS.stream().filter(todoDTO -> activityIdList.contains(todoDTO.getSubEntityId())).collect(Collectors.toList());
                timeTypeTodoListMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
            }
        }
    }
    private void getActivityIdMap(){
        if(CalculationType.TODO_STATUS.equals(calculationTypes.get(0)) && CollectionUtils.containsAny(yAxisConfigs, ObjectUtils.newHashSet(YAxisConfig.TIME_TYPE))) {
            Set<BigInteger> timeTypeIds = ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_TYPE)) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(FilterType.TIME_TYPE)) : new HashSet<>();
            Set<BigInteger> lowerLevelTimeTypeIds = kpiBuilderCalculationService.getTimeTypeService().getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), timeTypeIds).keySet();
            List<Activity> activities = kpiBuilderCalculationService.getActivityMongoRepository().findAllByUnitIdAndTimeTypeIds(unitId, lowerLevelTimeTypeIds);
            activityMap = activities.stream().collect(Collectors.toMap(Activity::getId, v -> v));
        }
    }



    public CalculationType getCalculationType() {
        return ObjectUtils.isNotNull(currentCalculationType) ? currentCalculationType : calculationTypes.get(0);
    }

    private void updateStaffAndShiftMap() {
        staffIdAndShiftsMap = shifts.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        staffKpiFilterDTOS.forEach(staffKpiFilterDTO -> {
            if (!staffIdAndShiftsMap.containsKey(staffKpiFilterDTO.getId())) {
                staffIdAndShiftsMap.put(staffKpiFilterDTO.getId(), new ArrayList<>());
            }
        });
    }

    public Set<WTATemplateType> getWtaTemplateTypes(YAxisConfig yAxis) {
        Set<WTATemplateType> wtaTemplateTypes = new HashSet<>();
        for (YAxisConfig yAxisConfig : ObjectUtils.isNull(yAxis) ? yAxisConfigs : Arrays.asList(yAxis)) {
            switch (yAxisConfig) {
                case YAxisConfig.CHILD_CARE_DAYS:
                    wtaTemplateTypes.add(WTATemplateType.CHILD_CARE_DAYS_CHECK);
                    break;
                case YAxisConfig.SENIORDAYS:
                    wtaTemplateTypes.add(WTATemplateType.SENIOR_DAYS_PER_YEAR);
                    break;
                case YAxisConfig.PROTECTED_DAYS_OFF:
                    wtaTemplateTypes.add(WTATemplateType.PROTECTED_DAYS_OFF);
                    break;
                case YAxisConfig.CARE_DAYS:
                    wtaTemplateTypes.add(WTATemplateType.WTA_FOR_CARE_DAYS);
                    break;
                case YAxisConfig.TOTAL_ABSENCE_DAYS:
                    wtaTemplateTypes.addAll(ObjectUtils.newHashSet(WTATemplateType.CHILD_CARE_DAYS_CHECK, WTATemplateType.SENIOR_DAYS_PER_YEAR, WTATemplateType.PROTECTED_DAYS_OFF, WTATemplateType.WTA_FOR_CARE_DAYS));
                    break;
                default:
                    break;
            }
        }
        return wtaTemplateTypes;
    }

    private void updateActivityAndTimeTypeAndPlannedTimeMap() {
        for (YAxisConfig yAxisConfig : yAxisConfigs) {
            switch (yAxisConfig) {
                case YAxisConfig.ACTIVITY:
                case YAxisConfig.PLANNING_QUALITY_LEVEL:
                    List<Activity> activities = kpiBuilderCalculationService.getActivityMongoRepository().findAllActivitiesByIds(filterBasedCriteria.containsKey(FilterType.ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(FilterType.ACTIVITY_IDS)) : new HashSet<>());
                    activityMap = activities.stream().collect(Collectors.toMap(Activity::getId, v -> v));
                    break;
                case YAxisConfig.TIME_TYPE:
                    timeTypeMap = kpiBuilderCalculationService.getTimeTypeService().getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), filterBasedCriteria.containsKey(FilterType.TIME_TYPE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)) : new ArrayList<>());
                    break;
                case YAxisConfig.PLANNED_TIME:
                    Collection<PlannedTimeType> plannedTimeTypes = kpiBuilderCalculationService.getPlannedTimeTypeService().getAllPlannedTimeByIds(filterBasedCriteria.containsKey(FilterType.PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.PLANNED_TIME_TYPE)) : new ArrayList<>());
                    plannedTimeMap = plannedTimeTypes.stream().collect(Collectors.toMap(PlannedTimeType::getId, v -> v));
                    break;
                default:
                    break;
            }
        }
    }

    private void updateIntervalShiftsMap(ApplicableKPI applicableKPI) {
        intervalShiftsMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            intervalShiftsMap.put(dateTimeInterval, shifts.stream().filter(shift -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate())) : dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
    }

    private void loadKpiCalculationRelatedInfo(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        Object[] filterCriteria = kpiBuilderCalculationService.getCounterHelperService().getDataByFilterCriteria(filterBasedCriteria);
        staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        employmentTypeIds = (List<Long>) filterCriteria[3];
        DefaultKpiDataDTO defaultKpiDataDTO = kpiBuilderCalculationService.getCounterHelperService().getKPIAllData(applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId, KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.TAGS, new ArrayList())),filterBasedCriteria);
        staffKpiFilterDTOS = defaultKpiDataDTO.getStaffKpiFilterDTOs();
        getStaffsByTeamType(filterBasedCriteria);
        dateTimeIntervals = defaultKpiDataDTO.getDateTimeIntervals();
        List<TimeSlotDTO> timeSlotDTOList = defaultKpiDataDTO.getTimeSlotDTOS();
        holidayCalenders = defaultKpiDataDTO.getHolidayCalenders();
        startDate = dateTimeIntervals.get(0).getStartDate();
        endDate = dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate();
        staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeeks = (Set<DayOfWeek>) filterCriteria[4];
        daysOfWeeks.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        updateShiftsDetailsForKPI(filterBasedCriteria, organizationId, unitIds, timeSlotDTOList, dayOfWeeksNo);
        currentShiftActivityCriteria = getDefaultShiftActivityCriteria();
        updateIntervalShiftsMap(applicableKPI);
        staffIdAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().filter(ObjectUtils.distinctByKey(StaffKpiFilterDTO::getId)).collect(Collectors.toMap(StaffKpiFilterDTO::getId, v -> v));
        updateStaffAndShiftMap();
        updateAuditLogs();
    }

    private void getStaffsByTeamType(Map<FilterType, List> filterBasedCriteria) {
        List<StaffKpiFilterDTO> staffKpiFilterDTOList = new ArrayList<>();
        if (filterBasedCriteria.containsKey(FilterType.TEAM_TYPE) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TEAM_TYPE))) {
            for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
                for (TeamDTO teamDTO : staffKpiFilterDTO.getTeams()) {
                    if (filterBasedCriteria.get(FilterType.TEAM_TYPE).contains(teamDTO.getTeamType().name())) {
                        staffKpiFilterDTOList.add(staffKpiFilterDTO);
                        break;
                    }
                }
            }
            staffKpiFilterDTOS = staffKpiFilterDTOList;

        }

    }

    private void updateAuditLogs() {
        if (CollectionUtils.containsAny(ObjectUtils.newHashSet(CalculationType.CARE_BUBBLE), calculationTypes) && filterBasedCriteria.containsKey(FilterType.TAGS) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TAGS))) {
            tagIds = KPIUtils.getLongValueSet(filterBasedCriteria.get(FilterType.TAGS));
            List<Long> validStaffIds = staffKpiFilterDTOS.stream().filter(staffKpiFilterDTO -> staffKpiFilterDTO.isTagValid(tagIds)).map(staffKpiFilterDTO -> staffKpiFilterDTO.getId()).collect(Collectors.toList());
            List<Map> shiftsLog = kpiBuilderCalculationService.getAuditLoggingService().getAuditLogOfStaff(validStaffIds, startDate, endDate);
            List<AuditShiftDTO> auditShiftDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftsLog, AuditShiftDTO.class);
            staffAuditLog = auditShiftDTOS.stream().collect(Collectors.groupingBy(auditShiftDTO -> auditShiftDTO.getStaffId()));
        }
    }

    private void updateShiftsDetailsForKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, List<Long> unitIds, List<TimeSlotDTO> timeSlotDTOS, List<Integer> dayOfWeeksNo) {
        if (!CollectionUtils.containsAny(ObjectUtils.newHashSet(CalculationType.DELTA_TIMEBANK, CalculationType.ACTUAL_TIMEBANK, CalculationType.STAFF_AGE, CalculationType.STAFFING_LEVEL_CAPACITY), calculationTypes)) {
            List<String> validKPIS = ObjectUtils.newArrayList(CalculationType.PRESENCE_UNDER_STAFFING.toString(), CalculationType.PRESENCE_OVER_STAFFING.toString(), CalculationType.ABSENCE_UNDER_STAFFING.toString(), CalculationType.ABSENCE_OVER_STAFFING.toString());
            if (filterBasedCriteria.containsKey(FilterType.CALCULATION_TYPE) && CollectionUtils.containsAny(validKPIS, filterBasedCriteria.get(FilterType.CALCULATION_TYPE))) {
                List<Shift> shiftData = kpiBuilderCalculationService.getShiftMongoRepository().findShiftBetweenDurationAndUnitIdAndDeletedFalse(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), ObjectUtils.isCollectionNotEmpty(unitIds) ? unitIds : ObjectUtils.newArrayList(organizationId));
                shifts = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftData, ShiftWithActivityDTO.class);
            } else {
                shifts = kpiBuilderCalculationService.getShiftMongoRepository().findShiftsByShiftAndActvityKpiFilters(staffIds, ObjectUtils.isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), false);
                StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, timeSlotDTOS, organizationId);
                shifts = kpiBuilderCalculationService.getShiftFilterService().getShiftsByFilters(shifts, staffFilterDTO, staffKpiFilterDTOS);
            }
        } else {
            shifts = new ArrayList<>();
        }
    }

    public void getTodoDetails() {
        if (CollectionUtils.containsAny(yAxisConfigs, ObjectUtils.newHashSet(YAxisConfig.PLANNING_QUALITY_LEVEL, CalculationType.ABSENCE_REQUEST,YAxisConfig.ACTIVITY))) {
            getUpdateTodoStatus();
            getUpdateTodoDTOSByDayOfWeek(todoDTOS);
            activityIds = filterBasedCriteria.containsKey(FilterType.ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(FilterType.ACTIVITY_IDS)) : new HashSet<>();
            if (ObjectUtils.isCollectionNotEmpty(activityIds)) {
                todoDTOS = todoDTOS.stream().filter(todoDTO -> activityIds.contains(todoDTO.getSubEntityId())).collect(Collectors.toList());
            }
            staffIdAndTodoMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getStaffId, Collectors.toList()));
            activityIdAndTodoListMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
            staffIdAndActivityTodoListMap = getStaffIdBigIntegerIdTodoListMap(staffIdAndActivityTodoListMap,staffIdAndTodoMap);
            updateActivityIdShiftListMap(activityIdAndTodoListMap,activityIdAndShiftListMap);
            staffIdAndActivityIdAndShiftMap= getStaffIdAndActivityIdAndShiftMap(staffIdAndActivityTodoListMap);
        }
    }

    private void getUpdateTodoStatus() {
        if(filterBasedCriteria.containsKey(FilterType.ACTIVITY_STATUS)&& !XAxisConfig.PERCENTAGE.equals(xAxisConfigs.get(0))) {
            todoDTOS = kpiBuilderCalculationService.getTodoService().getAllTodoByDateTimeIntervalAndTodoStatus(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(),filterBasedCriteria.get(FilterType.ACTIVITY_STATUS));
        }else if(!filterBasedCriteria.containsKey(FilterType.ACTIVITY_STATUS)&& !XAxisConfig.PERCENTAGE.equals(xAxisConfigs.get(0))){
            todoDTOS =kpiBuilderCalculationService.getTodoService().getAllTodoByShiftDate(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        }
        else if(XAxisConfig.PERCENTAGE.equals(xAxisConfigs.get(0))&& CalculationType.TODO_STATUS.equals(calculationTypes.get(0))){
            todoDTOS =kpiBuilderCalculationService.getTodoService().getAllTodoByShiftDate(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        }
        else{
            todoDTOS=kpiBuilderCalculationService.getTodoService().getAllTodoByEntityIds(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        }

    }

    public void updateTodoDtosByStaffId(Long staffId) {
        List<TodoDTO> todoDTOList = staffIdAndTodoMap.get(staffId);
        if (ObjectUtils.isNotNull(todoDTOList)) {
            activityIdAndTodoListMap = todoDTOList.stream().collect(Collectors.groupingBy(k -> k.getSubEntityId(), Collectors.toList()));
        }else{
            activityIdAndTodoListMap =new HashMap<>();
        }
    }



    public List<TodoDTO> getTodosByStaffIdAndInterval(Long staffId, DateTimeInterval dateTimeInterval) {
        List<TodoDTO> filterTodoDTOS = ObjectUtils.isNull(staffId) ? todoDTOS : staffIdAndTodoMap.getOrDefault(staffId, new ArrayList<>());
        if (ObjectUtils.isNotNull(dateTimeInterval)) {
            filterTodoDTOS = filterTodoDTOS.stream().filter(todoDTO -> dateTimeInterval.contains(todoDTO.getRequestedOn())).collect(Collectors.toList());
        }
        return filterTodoDTOS;
    }

    public List<TodoDTO> getTodosByInterval(DateTimeInterval dateTimeInterval, List<TodoDTO> todoDTOS) {
        if(YAxisConfig.PLANNING_QUALITY_LEVEL.equals(yAxisConfigs.get(0))) {
            return todoDTOS.stream().filter(todoDTO -> dateTimeInterval.containsAndEqualsEndDate(todoDTO.getRequestedOn())).collect(Collectors.toList());
        }else {
            return todoDTOS.stream().filter(todoDTO -> dateTimeInterval.containsAndEqualsEndDate(DateUtils.asDate(todoDTO.getShiftDate()))).collect(Collectors.toList());
        }
    }

    public List<Shift> getShiftsByInterval(DateTimeInterval dateTimeInterval,List<Shift> shifts){
        List<Shift> shiftList = new ArrayList<>();
        if(ObjectUtils.isCollectionNotEmpty(shifts)) {
            shiftList = shifts.stream().filter(shift -> dateTimeInterval.containsAndEqualsEndDate(shift.getStartDate())).collect(Collectors.toList());
        }
        return shiftList;
    }

    private KPIBuilderCalculationService.ShiftActivityCriteria getDefaultShiftActivityCriteria() {
        Set<BigInteger> teamActivityIds = new HashSet<>();
        if (filterBasedCriteria.containsKey(FilterType.TEAM) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TEAM))) {
            Set<String> teamIds = KPIUtils.getStringByList(new HashSet<>(filterBasedCriteria.get(FilterType.TEAM)));
            ShiftFilterDefaultData shiftFilterDefaultData = kpiBuilderCalculationService.getUserIntegrationService().getShiftFilterDefaultData(new SelfRosteringFilterDTO(UserContext.getUserDetails().getLastSelectedOrganizationId(), teamIds));
            teamActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
        }
        Set<Long> reasonCodeIds = filterBasedCriteria.containsKey(FilterType.REASON_CODE) ? KPIUtils.getLongValueSet(filterBasedCriteria.get(FilterType.REASON_CODE)) : new HashSet<>();
        Set<ShiftStatus> shiftStatuses = filterBasedCriteria.containsKey(FilterType.ACTIVITY_STATUS) ? (Set<ShiftStatus>) filterBasedCriteria.get(FilterType.ACTIVITY_STATUS).stream().map(o -> ShiftStatus.valueOf(o.toString())).collect(Collectors.toSet()) : new HashSet<>();
        return KPIBuilderCalculationService.ShiftActivityCriteria.builder().reasonCodeIds(reasonCodeIds).shiftStatuses(shiftStatuses).teamActivityIds(teamActivityIds).build();
    }

    private StaffFilterDTO getStaffFilterDto(Map<FilterType, List> filterBasedCriteria, List<TimeSlotDTO> timeSlotDTOS, Long organizationId) {
        StaffFilterDTO staffFilterDTO = new StaffFilterDTO();
        List<FilterSelectionDTO> filterData = new ArrayList<>();
        filterBasedCriteria.entrySet().forEach(filterTypeListEntry -> {
            getTimeSoltFilter(filterTypeListEntry, timeSlotDTOS, filterData);
            if (!ObjectUtils.newHashSet(FilterType.PHASE, FilterType.TEAM).contains(filterTypeListEntry.getKey())) {
                filterData.add(new FilterSelectionDTO(filterTypeListEntry.getKey(), new HashSet<String>(filterTypeListEntry.getValue())));
            }
        });
        if (filterBasedCriteria.containsKey(FilterType.PHASE)) {
            List<PhaseDTO> phases = kpiBuilderCalculationService.getPhaseService().getPhasesByUnit(organizationId);
            Set<PhaseDefaultName> phaseDefaultNames = (Set<PhaseDefaultName>) filterBasedCriteria.get(FilterType.PHASE).stream().map(value -> PhaseDefaultName.valueOf(value.toString())).collect(Collectors.toSet());
            Set<String> phaseIds = phases.stream().filter(phaseDTO -> phaseDefaultNames.contains(phaseDTO.getPhaseEnum())).map(phaseDTO -> phaseDTO.getId().toString()).collect(Collectors.toSet());
            filterData.add(new FilterSelectionDTO(FilterType.PHASE, phaseIds));
        }
        if (filterBasedCriteria.containsKey(FilterType.TEAM)) {
            filterData.add(new FilterSelectionDTO(FilterType.TEAM, new HashSet<>((List<String>) filterBasedCriteria.get(FilterType.TEAM))));
        }

        staffFilterDTO.setFiltersData(filterData);
        return staffFilterDTO;
    }

    public void getTimeSoltFilter(Map.Entry<FilterType, List> filterTypeListEntry, List<TimeSlotDTO> timeSlotDTOS, List<FilterSelectionDTO> filterData) {
        if (filterTypeListEntry.getKey().equals(FilterType.TIME_SLOT)) {
            Set<String> timeSlotName = new HashSet<>();
            for (Object timeSlotId : filterTypeListEntry.getValue()) {
                if (ObjectUtils.isCollectionNotEmpty(timeSlotDTOS)) {
                    for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                        if (timeSlotDTO.getId().equals(((Integer) timeSlotId).longValue())) {
                            timeSlotName.add(timeSlotDTO.getName());
                        }
                    }
                }
            }
            filterData.add(new FilterSelectionDTO(filterTypeListEntry.getKey(), timeSlotName));
        }
    }

    public List<ShiftWithActivityDTO> getShiftsByStaffIdAndInterval(Long staffId, DateTimeInterval dateTimeInterval, boolean includeFilter) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = ObjectUtils.isNull(staffId) ? shifts : staffIdAndShiftsMap.getOrDefault(staffId, new ArrayList<>());
        if (ObjectUtils.isNotNull(dateTimeInterval)) {
            shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate())) : dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList());
        }
        if (includeFilter) {
            StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, this.timeSlotDTOS, this.unitId);
            shifts = kpiBuilderCalculationService.getShiftFilterService().getShiftsByFilters(shifts, staffFilterDTO, staffKpiFilterDTOS);
        }
        return shiftWithActivityDTOS;
    }

    public List<AuditShiftDTO> getShiftAuditByStaffIdAndInterval(Long staffId, DateTimeInterval dateTimeInterval) {
        List<AuditShiftDTO> shiftWithActivityDTOS = ObjectUtils.isNull(staffId) ? staffAuditLog.values().stream().flatMap(auditShiftDTOS -> auditShiftDTOS.stream()).collect(Collectors.toList()) : staffAuditLog.getOrDefault(staffId, new ArrayList<>());
        if (ObjectUtils.isNotNull(dateTimeInterval)) {
            shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shiftWithActivityDTO.getActivities().get(0).getStartDate(), shiftWithActivityDTO.getActivities().get(0).getEndDate())) : dateTimeInterval.contains(shiftWithActivityDTO.getActivities().get(0).getStartDate())).collect(Collectors.toList());
        }
        return shiftWithActivityDTOS;
    }

    private void getDailyTimeBankEntryByDate() {
        if(CollectionUtils.containsAny(ObjectUtils.newHashSet(CalculationType.DELTA_TIMEBANK, CalculationType.ACTUAL_TIMEBANK, CalculationType.STAFFING_LEVEL_CAPACITY), calculationTypes)) {
            dailyTimeBankEntries = kpiBuilderCalculationService.getTimeBankRepository().findAllDailyTimeBankByIdsAndBetweenDates(employmentIds, startDate, endDate);
        }
        if (ObjectUtils.isCollectionNotEmpty(daysOfWeeks)) {
            dailyTimeBankEntries = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> daysOfWeeks.contains(dailyTimeBankEntry.getDate().getDayOfWeek())).collect(Collectors.toList());
        }
        employmentIdAndDailyTimebankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toCollection(ArrayList::new)));
        staffIdAndDailyTimebankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getStaffId, Collectors.toCollection(ArrayList::new)));
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                if (!employmentIdAndDailyTimebankEntryMap.containsKey(employmentWithCtaDetailsDTO.getId())) {
                    employmentIdAndDailyTimebankEntryMap.put(employmentWithCtaDetailsDTO.getId(), new ArrayList<>());
                }
            }
            if (!staffIdAndDailyTimebankEntryMap.containsKey(staffKpiFilterDTO.getId())) {
                staffIdAndDailyTimebankEntryMap.put(staffKpiFilterDTO.getId(), new ArrayList<>());
            }
        }
    }

    private void getDailyTimeBankEntryByEmploymentId() {
        dailyTimeBankEntries = kpiBuilderCalculationService.getTimeBankRepository().findAllByEmploymentIdsAndBeforDate(new ArrayList<>(employmentIds), planningPeriodInterval.getEndDate());
        employmentIdAndDailyTimebankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toCollection(ArrayList::new)));
    }

    public Collection<DailyTimeBankEntry> getDailyTimeBankEntrysByEmploymentIdAndInterval(Long employmentId, DateTimeInterval dateTimeInterval) {
        Collection<DailyTimeBankEntry> filteredDailyTimeBankEntries = employmentIdAndDailyTimebankEntryMap.getOrDefault(employmentId, dailyTimeBankEntries);
        if (ObjectUtils.isNotNull(dateTimeInterval)) {
            filteredDailyTimeBankEntries = filteredDailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dateTimeInterval.contains(dailyTimeBankEntry.getDate())).collect(Collectors.toList());
        }
        return filteredDailyTimeBankEntries;
    }

    public List<StaffKpiFilterDTO> getStaffKPIFilterDTO(Long staffId) {
        List<StaffKpiFilterDTO> filteredStaffKpiFilterDTOS = this.staffKpiFilterDTOS;
        if (ObjectUtils.isNotNull(staffId)) {
            filteredStaffKpiFilterDTOS = filteredStaffKpiFilterDTOS.stream().filter(staffKpiFilterDTO -> staffKpiFilterDTO.getId().equals(staffId)).collect(Collectors.toList());
        }
        return filteredStaffKpiFilterDTOS;
    }
}