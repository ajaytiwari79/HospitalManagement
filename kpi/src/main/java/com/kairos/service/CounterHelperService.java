package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.constants.ApiConstants;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Day;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.persistence.model.ExceptionService;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.repository.counter.CounterHelperRepository;
import com.kairos.utils.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.KPIMessagesConstants.MESSAGE_ORGANIZATION_PHASES_ON_DATE;
import static com.kairos.constants.KPIMessagesConstants.MESSAGE_PHASESETTINGS_ABSENT;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.FilterType.PLANNED_BY;
import static com.kairos.enums.phase.PhaseType.ACTUAL;

/**
 * pradeep
 * 20/5/19
 */
@Service
public class CounterHelperService {

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CounterHelperRepository counterHelperRepository;
    @Inject private ExceptionService exceptionService;

    public Object[] getKPIdata(Map<FilterType, List> filterBasedCriteria,ApplicableKPI applicableKPI, List<LocalDate> filterDates, List<Long> staffIds, List<Long> employmentTypeIds, List<Long> unitIds, Long organizationId){
        List<DateTimeInterval> dateTimeIntervals = KPIUtils.getDateTimeIntervals(applicableKPI.getInterval(), ObjectUtils.isNull(applicableKPI) ? 0 : applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,applicableKPI.getDateForKPISetCalculation());
        List<Long> tagIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.TAGS,new ArrayList<>()));
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString(),tagIds,filterBasedCriteria,true);
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Set<String> filterValues = (Set<String>)staffEmploymentTypeDTO.getFilterBasedCriteria().values().stream().flatMap(list -> list.stream()).map(value->value.toString()).collect(Collectors.toSet());
        if(filterValues.contains(XAxisConfig.VARIABLE_COST.toString())) {
            if(filterValues.contains(XAxisConfig.VARIABLE_COST.toString())) {
                List<DayTypeDTO> dayTypeDTOS = counterHelperRepository.findAllByCountryIdAndDeletedFalse(UserContext.getUserDetails().getCountryId());
                for (StaffKpiFilterDTO kpiFilterQueryResult : staffKpiFilterDTOS) {
                    kpiFilterQueryResult.setDayTypeDTOS(dayTypeDTOS);
                }
            }
        }
        staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        return new Object[]{staffKpiFilterDTOS, dateTimeIntervals, staffIds};
    }


    public DefaultKpiDataDTO getKPIAllData(ApplicableKPI applicableKPI, List<LocalDate> filterDates, List<Long> staffIds, List<Long> employmentTypeIds, List<Long> unitIds, Long organizationId,List<Long> tagIds,Map<FilterType, List> filterBasedCriteria){
        List<DateTimeInterval> dateTimeIntervals = KPIUtils.getDateTimeIntervals(applicableKPI.getInterval(), ObjectUtils.isNull(applicableKPI) ? 0 : applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,applicableKPI.getDateForKPISetCalculation());
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString(),tagIds,filterBasedCriteria,true);
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiAllDefaultData(UserContext.getUserDetails().getCountryId(), staffEmploymentTypeDTO);
        defaultKpiDataDTO.setDateTimeIntervals(dateTimeIntervals);
        defaultKpiDataDTO.setHolidayCalenders(counterHelperRepository.getAllByCountryIdAndHolidayDateBetween(UserContext.getUserDetails().getCountryId(),LocalDate.parse(staffEmploymentTypeDTO.getStartDate()), LocalDate.parse(staffEmploymentTypeDTO.getEndDate())));
        defaultKpiDataDTO.setTimeSlotDTOS(counterHelperRepository.getUnitTimeSlot(staffEmploymentTypeDTO.getOrganizationId()));
        return defaultKpiDataDTO;
    }

    public Object[] getDataByFilterCriteria(Map<FilterType, List> filterBasedCriteria){
        List staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null)&& ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.STAFF_IDS)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = new ArrayList<>();
        if (ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentTypeIds = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        Set<DayOfWeek> daysOfWeeks = filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) ? KPIUtils.getDaysOfWeeksfromString(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) : ObjectUtils.newHashSet(DayOfWeek.values());
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        List<BigInteger> plannedTimeIds = (filterBasedCriteria.get(FilterType.PLANNED_TIME_TYPE) != null) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.PLANNED_TIME_TYPE) ): new ArrayList<>();
        return new Object[]{staffIds,filterDates,unitIds,employmentTypeIds,daysOfWeeks,shiftActivityStatus,plannedTimeIds};
    }

    public Set<DayOfWeek> getDayOfWeek(List<BigInteger> dayTypeIds,Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap)
    {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();

        if (!ObjectUtils.isCollectionEmpty(dayTypeIds)) {
            dayTypeIds.forEach(daysTypeId -> daysTypeIdAndDayTypeMap.get(daysTypeId).getValidDays().forEach(day -> {
                //TODO if remove Everyday from day enum then remove if statement and use dayOfWeek of java
                if (day.equals(Day.EVERYDAY)) {
                    daysOfWeek.addAll(ObjectUtils.newHashSet(DayOfWeek.values()));
                } else {
                    daysOfWeek.add(DayOfWeek.valueOf(day.toString()));
                }
            }));
        }
        return daysOfWeek;
    }

    public DefaultKpiDataDTO getDefaultDataForKPI(StaffEmploymentTypeDTO staffEmploymentTypeDTO){
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiAllDefaultData(staffEmploymentTypeDTO);
        defaultKpiDataDTO.setHolidayCalenders(counterHelperRepository.getAllByCountryIdAndHolidayDateBetween(UserContext.getUserDetails().getCountryId(),LocalDate.parse(staffEmploymentTypeDTO.getStartDate()), LocalDate.parse(staffEmploymentTypeDTO.getEndDate())));
        defaultKpiDataDTO.setTimeSlotDTOS(counterHelperRepository.getUnitTimeSlot(staffEmploymentTypeDTO.getOrganizationId()));
        List<DayTypeDTO> dayTypeDTOS = counterHelperRepository.findAllByCountryIdAndDeletedFalse(staffEmploymentTypeDTO.getOrganizationId());
        defaultKpiDataDTO.setDayTypeDTOS(dayTypeDTOS);
        return defaultKpiDataDTO;
    }


    public <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<BigInteger> shiftStateIds=new ArrayList<>();
        Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
        if (isNull(staffFilterDTO)) {
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }
        List<TimeSlotDTO> timeSlotDTOS = counterHelperRepository.getUnitTimeSlot(unitId);
        Map<FilterType, Set<T>> filterTypeMap = staffFilterDTO.getFiltersData().stream().filter(distinctByKey(filterSelectionDTO -> filterSelectionDTO.getName())).collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));
        ShiftFilter timeTypeFilter = getTimeTypeFilter(filterTypeMap);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter timeSlotFilter = new TimeSlotFilter(filterTypeMap,timeSlotDTOS);
        ShiftFilter activityFilter = getActivityFilter(unitId, filterTypeMap);
        ShiftFilter plannedTimeTypeFilter=new PlannedTimeTypeFilter(filterTypeMap);
        ShiftFilter timeAndAttendanceFilter = getValidatedFilter(shiftWithActivityDTOS, shiftStateIds, filterTypeMap);
        ShiftFilter functionsFilter = getFunctionFilter(unitId, filterTypeMap);
        //ShiftFilter realTimeStatusFilter = getSickTimeTypeFilter(unitId, filterTypeMap);
        ShiftFilter plannedByFilter = getPlannedByFilter(unitId,filterTypeMap);
        ShiftFilter phaseFilter = new PhaseFilter(filterTypeMap);
        ShiftFilter escalationFilter = getEscalationFilter(shiftWithActivityDTOS.stream().map(shift->shift.getId()).collect(Collectors.toList()), filterTypeMap);
        Set<Long> employmentIds = shiftWithActivityDTOS.stream().map(s->s.getEmploymentId()).collect(Collectors.toSet());
        ShiftFilter timeBankBalanceFilter = getTimeBankBalanceFilter(unitId, filterTypeMap, employmentIds);
        ShiftFilter employmentTypeFilter = getEmploymentTypeFilter(filterTypeMap,staffKpiFilterDTOS);
        ShiftFilter employmentSubTypeFilter = getEmploymentSubTypeFilter(filterTypeMap,staffKpiFilterDTOS);
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter, activityTimecalculationTypeFilter).and(activityStatusFilter).and(timeSlotFilter).and(activityFilter).and(plannedTimeTypeFilter).and(timeAndAttendanceFilter)
                .and(functionsFilter).and(phaseFilter).and(plannedByFilter).and(escalationFilter)
                .and(timeBankBalanceFilter).and(employmentTypeFilter).and(employmentSubTypeFilter);//.and(realTimeStatusFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
    }

    private <G> ShiftFilter getTimeBankBalanceFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap, Set<Long> employmentIds) {
        //Update loop in a single call
        Map<Long,Double> employmentIdAndActualTimeBankData = new HashMap<>();
        if(filterTypeMap.containsKey(TIME_BANK_BALANCE) && isCollectionNotEmpty(filterTypeMap.get(TIME_BANK_BALANCE))) {
            for (Long employmentId : employmentIds) {
                Double timeBank = 0.0d;//DateUtils.getHoursByMinutes(Double.valueOf(asyncTimeBankCalculationService.getAccumulatedTimebankAndDelta(employmentId, unitId, false,null,null).toString()));
                employmentIdAndActualTimeBankData.put(employmentId,timeBank);
            }
        }
        return new TimeBankBalanceFilter(filterTypeMap, employmentIdAndActualTimeBankData);
    }

    private <G> ShiftFilter getEscalationFilter(List<BigInteger> shiftIds, Map<FilterType, Set<G>> filterTypeMap){
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = new HashMap<>();
        if(filterTypeMap.containsKey(ESCALATION_CAUSED_BY) && isCollectionNotEmpty(filterTypeMap.get(ESCALATION_CAUSED_BY))) {
            List<ShiftDTO> shifts = counterHelperRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftIds);
            shiftViolatedRulesMap = shifts.stream().filter(s-> isNotNull(s.getShiftViolatedRules())).collect(Collectors.toMap(k -> k.getId(), v -> v.getShiftViolatedRules()));
        }
        return new EscalationFilter(shiftViolatedRulesMap, filterTypeMap);
    }

    /*private <G> ShiftFilter getSickTimeTypeFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        Set<BigInteger> sickTimeTypes = new HashSet<>();
        if(filterTypeMap.containsKey(REAL_TIME_STATUS) && isCollectionNotEmpty(filterTypeMap.get(REAL_TIME_STATUS))) {
            sickTimeTypes = userIntegrationService.getSickSettingsOfUnit(unitId);
        }
        return new RealTimeStatusFilter(filterTypeMap, sickTimeTypes);
    }*/

    private <G> ShiftFilter getFunctionFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        Set<LocalDate> functionDates = new HashSet<>();
        if(filterTypeMap.containsKey(FilterType.FUNCTIONS) && isCollectionNotEmpty(filterTypeMap.get(FUNCTIONS))) {
            List<Long> functionIds = filterTypeMap.get(FUNCTIONS).stream().map(s -> new Long(s.toString())).collect(Collectors.toList());
            functionDates = userIntegrationService.getAllDateByFunctionIds(unitId, functionIds);
        }
        return new FunctionsFilter(filterTypeMap, functionDates);
    }

    private <T extends ShiftDTO, G> ShiftFilter getValidatedFilter(List<T> shiftWithActivityDTOS, List<BigInteger> shiftStateIds, Map<FilterType, Set<G>> filterTypeMap) {
        if(filterTypeMap.containsKey(FilterType.VALIDATED_BY) && isCollectionNotEmpty(filterTypeMap.get(VALIDATED_BY))) {
            Set<BigInteger> shiftIds = shiftWithActivityDTOS.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
            List<ShiftDTO> shiftStates = counterHelperRepository.findAllByShiftIdsByAccessgroupRole(shiftIds, filterTypeMap.get(FilterType.VALIDATED_BY).stream().map(v->v.toString()).collect(Collectors.toSet()));
            shiftStateIds=shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toList());
        }
        return new TimeAndAttendanceFilter(filterTypeMap,shiftStateIds);
    }

    private <G> ShiftFilter getActivityFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        List<BigInteger> selectedActivityIds = new ArrayList<>();
        if(filterTypeMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterTypeMap.get(ABSENCE_ACTIVITY))) {
            selectedActivityIds.addAll(filterTypeMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s.toString())).collect(Collectors.toList()));
        }
        if(filterTypeMap.containsKey(TEAM) && isCollectionNotEmpty(filterTypeMap.get(TEAM))){
            Set<String> teamIds = getStringByList(filterTypeMap.get(TEAM));
            ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(unitId,teamIds));
            selectedActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
        }
        return new ActivityFilter(filterTypeMap, selectedActivityIds);
    }


    private <G> ShiftFilter getTimeTypeFilter(Map<FilterType, Set<G>> filterTypeMap) {
        Set<BigInteger> timeTypeIds = new HashSet<>();
        if(filterTypeMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterTypeMap.get(TIME_TYPE))) {
            Set<BigInteger> ids = new HashSet<>(getBigInteger(filterTypeMap.get(TIME_TYPE)));
            timeTypeIds = counterHelperRepository.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), ids).keySet();
        }
        return new TimeTypeFilter(filterTypeMap, timeTypeIds);
    }

    private <G> ShiftFilter getEmploymentTypeFilter(Map<FilterType, Set<G>> filterTypeMap,List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long,Long> employmentIdAndEmploymentTypeIdMap = new HashMap<>();
        if(filterTypeMap.containsKey(EMPLOYMENT_TYPE)&&isCollectionNotEmpty(filterTypeMap.get(EMPLOYMENT_TYPE))){
            employmentIdAndEmploymentTypeIdMap = getEmploymentIdAndEmploymentTypeIdMap(staffKpiFilterDTOS);
        }
        return new EmploymentTypeFilter(filterTypeMap,employmentIdAndEmploymentTypeIdMap,isCollectionNotEmpty(staffKpiFilterDTOS));
    }

    private <G> ShiftFilter getEmploymentSubTypeFilter(Map<FilterType, Set<G>> filterTypeMap,List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeIdMap = new HashMap<>();
        if(filterTypeMap.containsKey(EMPLOYMENT_SUB_TYPE)&&isCollectionNotEmpty(filterTypeMap.get(EMPLOYMENT_SUB_TYPE))) {

            employmentIdAndEmploymentSubTypeIdMap = getEmploymentIdAndEmploymentSubType(staffKpiFilterDTOS);

        }
        return new EmploymentSubTypeFilter(filterTypeMap,employmentIdAndEmploymentSubTypeIdMap);
    }

    private Map<Long, Long> getEmploymentIdAndEmploymentTypeIdMap(List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long,Long> employmentIdAndEmploymentTypeIdMap = new HashMap<>();
        for(StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS){
            for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()){
                employmentIdAndEmploymentTypeIdMap.put(employmentWithCtaDetailsDTO.getId(),employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentTypeId());
            }
        }
        return employmentIdAndEmploymentTypeIdMap;
    }

    private Map<Long, EmploymentSubType> getEmploymentIdAndEmploymentSubType(List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeIdMap = new HashMap<>();
        for(StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS){
            for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()){
                List<EmploymentLinesDTO> employmentLinesDTOS = employmentWithCtaDetailsDTO.getEmploymentLines();
                Collections.sort(employmentLinesDTOS);
                employmentIdAndEmploymentSubTypeIdMap.put(employmentWithCtaDetailsDTO.getId(),employmentLinesDTOS.get(employmentLinesDTOS.size()-1).getEmploymentSubType());
            }
        }
        return employmentIdAndEmploymentSubTypeIdMap;
    }



    private <G> ShiftFilter getPlannedByFilter(Long unitId,Map<FilterType, Set<G>> filterTypeMap) {
        Set<Long> staffUserIds = new HashSet<>();
        if(filterTypeMap.containsKey(PLANNED_BY) && isCollectionNotEmpty(filterTypeMap.get(PLANNED_BY))){
            List<StaffDTO> staffDTOS = userIntegrationService.getStaffByUnitId(unitId);
            Set<AccessGroupRole> accessGroups = filterTypeMap.get(PLANNED_BY).stream().map(s -> AccessGroupRole.valueOf(s.toString())).collect(Collectors.toSet());
            for (StaffDTO staffDTO : staffDTOS) {
                if(isNotNull(staffDTO.getRoles()) && CollectionUtils.containsAny(staffDTO.getRoles(),accessGroups)){
                    staffUserIds.add(staffDTO.getStaffUserId());
                }
            }
        }
        return new PlannedByFilter(staffUserIds,filterTypeMap);
    }


    public Map[] getPhasesByDates(Long unitId, LocalDate startDate,LocalDate endDate,ZoneId timeZone,Long employementTypeId) {
        timeZone = isNull(timeZone) ? ZoneId.of(userIntegrationService.getTimeZoneByUnitId(unitId)) : timeZone;
        List<PhaseDTO> phases = counterHelperRepository.getPhasesByUnit(unitId);
        List<PlanningPeriodDTO> planningPeriods = counterHelperRepository.findAllPlanningPeriodBetweenDatesAndUnitId(unitId,asDate(startDate),asDate(endDate));
        Map<Date,PhaseDTO> localDatePhaseStatusMap=new HashMap<>();
        Map[] phaseDetailsMap=getPhaseMap(phases);
        Map<LocalDate,Boolean> publishEmployementType = new HashMap<>();
        Map<BigInteger,PhaseDTO> phaseAndIdMap=(Map<BigInteger,PhaseDTO>)phaseDetailsMap[0];
        Map<String,PhaseDTO> phaseMap = (Map<String,PhaseDTO>)phaseDetailsMap[1];
        DayOfWeek tentativeDayOfWeek = phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay() == null ? DayOfWeek.MONDAY : phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay();
        LocalDateTime untilTentative = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativeDayOfWeek).atStartOfDay().minusSeconds(1);
        while (!startDate.isAfter(endDate)){
            PhaseDTO phase = null;
            LocalDateTime requestedDate = asLocalDateTime(startDate);
            PlanningPeriodDTO planningPeriodOptional = findElementPlanningPeriodByDate(planningPeriods,requestedDate.toLocalDate());//planningPeriods.stream().filter(planningPeriod -> planningPeriod.contains(requestedDate.toLocalDate())).findAny();
            if (requestedDate.isAfter(untilTentative)) {
                //if (planningPeriodOptional.isPresent()) {
                phase = phaseAndIdMap.get(planningPeriodOptional.getCurrentPhaseId());
                //}
            } else {
                phase = getActualPhaseApplicableForDate(requestedDate, phaseMap, untilTentative, timeZone);
            }
            if (isNull(phase)) {
                exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PHASES_ON_DATE, unitId, requestedDate);
            }
            publishEmployementType.put(startDate,planningPeriodOptional.getPublishEmploymentIds().contains(employementTypeId));
            localDatePhaseStatusMap.put(asDate(requestedDate), phase);
            startDate = startDate.plusDays(1);
        }
        return new Map[]{localDatePhaseStatusMap,publishEmployementType};
    }

    private PlanningPeriodDTO findElementPlanningPeriodByDate(List<PlanningPeriodDTO> planningPeriodDTOS,LocalDate localDate){
        int left = 0, right = planningPeriodDTOS.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            // Check if x is present at mid
            if (planningPeriodDTOS.get(mid).contains(localDate)) {
                return planningPeriodDTOS.get(mid);
            }
            // If x greater, ignore left half
            if (planningPeriodDTOS.get(mid).getEndDate().isBefore(localDate)) {
                left = mid + 1;
            }
            // If x is smaller, ignore right half
            else {
                right = mid - 1;
            }
        }
        exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PHASES_ON_DATE, UserContext.getUserDetails().getLastSelectedOrganizationId(), localDate);
        return null;
    }

    private Map[] getPhaseMap(List<PhaseDTO> phases){
        Map<BigInteger,PhaseDTO> phaseMap = new HashMap<>();
        Map<String,PhaseDTO> phaseEnumMap = new HashMap<>();
        for (PhaseDTO phase : phases) {
            phaseMap.put(phase.getId(),phase);
            phaseEnumMap.put(phase.getPhaseEnum().toString(),phase);
        }
        return new Map[]{phaseMap,phaseEnumMap};
    }

    public BigInteger getCurrentPhaseByUnitIdAndDate(Long unitId, Date startDate, Date endDate) {
        ZoneId timeZone= ZoneId.of(userIntegrationService.getTimeZoneByUnitId(unitId));
        PhaseDTO tentativePhase = counterHelperRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TENTATIVE.toString());
        LocalDateTime untilTentativeDate = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativePhase.getUntilNextDay()==null? DayOfWeek.MONDAY:tentativePhase.getUntilNextDay()).atStartOfDay().minusSeconds(1);
        LocalDateTime startDateTime=DateUtils.asLocalDateTime(startDate);
        LocalDateTime endDateTime = Optional.ofNullable(endDate).isPresent()? DateUtils.asLocalDateTime(endDate):null;
        PhaseDTO phase;
        if(startDateTime.isAfter(untilTentativeDate)){
            phase = counterHelperRepository.getCurrentPhaseByDateUsingPlanningPeriod(unitId,DateUtils.asLocalDate(startDate));
        }
        else {
            List<PhaseDTO> actualPhases = counterHelperRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalse(unitId, ACTUAL.toString());
            Map<String, PhaseDTO> phaseMap = actualPhases.stream().collect(Collectors.toMap(k->k.getPhaseEnum().toString(), Function.identity()));
            phase = getActualPhaseApplicableForDate(startDateTime,phaseMap,untilTentativeDate,timeZone);
        }
        if (isNull(phase)) {
            exceptionService.dataNotFoundException(MESSAGE_PHASESETTINGS_ABSENT);
        }
        return phase.getId();
    }

    private PhaseDTO getActualPhaseApplicableForDate(LocalDateTime startDateTime, Map<String,PhaseDTO> phaseMap, LocalDateTime untilTentativeDate,ZoneId timeZone){
        PhaseDTO phase=null;
        int minutesToCalculate=phaseMap.get(PhaseDefaultName.REALTIME.toString()).getRealtimeDuration();
        LocalDateTime realTimeStartDate=DateUtils.getLocalDateTimeFromZoneId(timeZone).minusMinutes(minutesToCalculate+1);
        if (startDateTime.isBefore(realTimeStartDate)) {
            return phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString());
        }
        LocalDateTime realTimeEndDate=DateUtils.getLocalDateTimeFromZoneId(timeZone).plusMinutes(minutesToCalculate+1);
        if(new DateTimeInterval(asDate(realTimeStartDate),asDate(realTimeEndDate)).contains(asDate(startDateTime))){
            return phaseMap.get(PhaseDefaultName.REALTIME.toString());
        }else if ((startDateTime).isBefore(untilTentativeDate) && startDateTime.isAfter(realTimeEndDate)) {
            return phaseMap.get(PhaseDefaultName.TENTATIVE.toString());
        }
        return phase;
    }

    public class PlannedTimeTypeFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;


        public PlannedTimeTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap) {
            this.filterCriteriaMap = filterCriteriaMap;

        }
        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(PLANNED_TIME_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(PLANNED_TIME_TYPE));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                List<BigInteger> plannedTimeTypeIds=filterCriteriaMap.get(PLANNED_TIME_TYPE).stream().map(s -> new BigInteger(s.toString())).collect(Collectors.toList());
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    List<BigInteger> shiftPlannedTimeTypeIds= shiftDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId())).collect(Collectors.toList());
                    if(org.springframework.util.CollectionUtils.containsAny(plannedTimeTypeIds,shiftPlannedTimeTypeIds))
                        filteredShifts.add((T)shiftDTO);
                }
            }
            return filteredShifts;
        }
    }

    public class ActivityStatusFilter <G> implements ShiftFilter {

        private Map<FilterType, Set<G>> filterCriteriaMap;

        public ActivityStatusFilter(Map<FilterType, Set<G>> filterCriteriaMap) {
            this.filterCriteriaMap = filterCriteriaMap;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(ACTIVITY_STATUS) && isCollectionNotEmpty(filterCriteriaMap.get(ACTIVITY_STATUS));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    Set<String> activityStatus = new HashSet<>();
                    shiftDTO.getActivities().forEach(shiftActivityDTO -> {
                        activityStatus.addAll(shiftActivityDTO.getStatus().stream().filter(shiftStatus -> isNotNull(shiftStatus)).map(shiftStatus -> shiftStatus.toString()).collect(Collectors.toSet()));
                        shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> activityStatus.addAll(childActivityDTO.getStatus().stream().filter(shiftStatus -> isNotNull(shiftStatus)).map(shiftStatus -> shiftStatus.toString()).collect(Collectors.toSet())));
                    });
                    if(org.springframework.util.CollectionUtils.containsAny(filterCriteriaMap.get(ACTIVITY_STATUS),activityStatus)){
                        filteredShifts.add((T)shiftDTO);
                    }
                }

            }
            return filteredShifts;
        }

    }

    public class PhaseFilter <G> implements ShiftFilter {

        private Map<FilterType, Set<G>> filterCriteriaMap;

        public PhaseFilter(Map<FilterType, Set<G>> filterCriteriaMap) {
            this.filterCriteriaMap = filterCriteriaMap;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(PHASE) && isCollectionNotEmpty(filterCriteriaMap.get(PHASE));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                Set<BigInteger> phaseIds = filterCriteriaMap.get(PHASE).stream().map(s -> new BigInteger(s.toString())).collect(Collectors.toSet());
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(phaseIds.contains(shiftDTO.getPhaseId())){
                        filteredShifts.add((T)shiftDTO);
                    }
                }

            }
            return filteredShifts;
        }

    }

    public class TimeSlotFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        private List<TimeSlotDTO> timeSlotDTOS;

        public TimeSlotFilter(Map<FilterType, Set<G>> filterCriteriaMap,List<TimeSlotDTO> timeSlotDTOS) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.timeSlotDTOS = timeSlotDTOS;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(TIME_SLOT) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_SLOT));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            List<TimeInterval> timeIntervals = new ArrayList<>();
            if(validFilter){
                filterCriteriaMap.get(TIME_SLOT).forEach(timeSlot->{
                    for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                        if(timeSlot.equals(timeSlotDTO.getName()) || timeSlot.equals(timeSlotDTO.getId().intValue())){
                            timeIntervals.add(new TimeInterval((timeSlotDTO.getStartHour()* AppConstants.ONE_HOUR_MINUTES)+timeSlotDTO.getStartMinute(),(timeSlotDTO.getEndHour()*AppConstants.ONE_HOUR_MINUTES)+timeSlotDTO.getEndMinute()-1));
                        }
                    }
                });
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    for (TimeInterval timeInterval : timeIntervals) {
                        if (timeInterval.contains(DateUtils.asZonedDateTime(shiftDTO.getStartDate()).get(ChronoField.MINUTE_OF_DAY))) {
                            filteredShifts.add((T) shiftDTO);
                            break;
                        }
                    }
                }
            }
            return filteredShifts;
        }
    }
    public class ActivityTimeCalculationTypeFilter <G> implements ShiftFilter {

        private Map<FilterType, Set<G>> filterCriteriaMap;

        public ActivityTimeCalculationTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap) {
            this.filterCriteriaMap = filterCriteriaMap;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(ACTIVITY_TIMECALCULATION_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(ACTIVITY_TIMECALCULATION_TYPE));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    Set<String> methodForCalulation = new HashSet<>();
                    shiftDTO.getActivities().forEach(shiftActivityDTO -> {
                        methodForCalulation.add(shiftActivityDTO.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime());
                        shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> {
                            if(isNotNull(childActivityDTO.getActivity())) {
                                methodForCalulation.add(childActivityDTO.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime());
                            }
                        });
                    });
                    if (org.springframework.util.CollectionUtils.containsAny(filterCriteriaMap.get(ACTIVITY_TIMECALCULATION_TYPE), methodForCalulation)) {
                        filteredShifts.add((T)shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }
    }

    public class AndShiftFilter implements ShiftFilter {

        private ShiftFilter firstCriteria;
        private ShiftFilter secondCriteria;

        public AndShiftFilter(ShiftFilter firstCriteria,ShiftFilter secondCriteria){
            this.firstCriteria = firstCriteria;
            this.secondCriteria = secondCriteria;
        }

        public AndShiftFilter and(ShiftFilter shiftFilter){
            return new AndShiftFilter(this,shiftFilter);
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            shiftDTOS = firstCriteria.meetCriteria(shiftDTOS);
            return secondCriteria.meetCriteria(shiftDTOS);
        }
    }

    public class PlannedByFilter <G> implements ShiftFilter{

        private Set<Long> plannedByUserIds;
        private Map<FilterType, Set<G>> filterCriteriaMap;

        public PlannedByFilter(Set<Long> plannedByUserIds, Map<FilterType, Set<G>> filterCriteriaMap) {
            this.plannedByUserIds = plannedByUserIds;
            this.filterCriteriaMap = filterCriteriaMap;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(PLANNED_BY) && isCollectionNotEmpty(filterCriteriaMap.get(PLANNED_BY));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(isNotNull(shiftDTO.getCreatedBy()) && plannedByUserIds.contains(shiftDTO.getCreatedBy().getId())){
                        filteredShifts.add((T)shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }
    }

    public class ActivityFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        private List<BigInteger> selectedActivityIds;

        public ActivityFilter(Map<FilterType, Set<G>> filterCriteriaMap, List<BigInteger> selectedActivityIds) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.selectedActivityIds = selectedActivityIds;
        }


        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = (filterCriteriaMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterCriteriaMap.get(ABSENCE_ACTIVITY))) || (filterCriteriaMap.containsKey(TEAM) && isCollectionNotEmpty(filterCriteriaMap.get(TEAM)));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(shiftDTO.getActivities().stream().anyMatch(shiftActivityDTO -> selectedActivityIds.contains(shiftActivityDTO.getActivityId())))
                        filteredShifts.add((T)shiftDTO);
                }
            }
            return filteredShifts;
        }
    }

    public class EscalationFilter <G> implements ShiftFilter  {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap;

        public EscalationFilter(Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap, Map<FilterType, Set<G>> filterCriteriaMap) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.shiftViolatedRulesMap = shiftViolatedRulesMap;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(ESCALATION_CAUSED_BY) && isCollectionNotEmpty(filterCriteriaMap.get(ESCALATION_CAUSED_BY));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(shiftViolatedRulesMap.containsKey(shiftDTO.getId()) && isCollectionNotEmpty(shiftViolatedRulesMap.get(shiftDTO.getId()).getEscalationReasons()) &&  !shiftViolatedRulesMap.get(shiftDTO.getId()).isEscalationResolved() && filterCriteriaMap.get(ESCALATION_CAUSED_BY).contains(shiftViolatedRulesMap.get(shiftDTO.getId()).getEscalationCausedBy().toString())){
                        filteredShifts.add((T)shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }
    }

    public class TimeBankBalanceFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        private Map<Long,Double> employmentIdAndActualTimeBankData;

        public TimeBankBalanceFilter(Map<FilterType, Set<G>> filterCriteriaMap, Map<Long,Double> employmentIdAndActualTimeBankData) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.employmentIdAndActualTimeBankData = employmentIdAndActualTimeBankData;
        }
        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(TIME_BANK_BALANCE) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_BANK_BALANCE));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                Map timeBankRangeMap = (Map) filterCriteriaMap.get(TIME_BANK_BALANCE).iterator().next();

                Long from = timeBankRangeMap.containsKey("from") && isNotNull(timeBankRangeMap.get("from"))  ? Long.parseLong(timeBankRangeMap.get("from").toString()) : null;
                Long to = timeBankRangeMap.containsKey("to") && isNotNull(timeBankRangeMap.get("to")) ? Long.parseLong(timeBankRangeMap.get("to").toString()) : null;

                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(isValidTimeBank(from, to, shiftDTO.getEmploymentId())) {
                        filteredShifts.add((T) shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }

        private boolean isValidTimeBank(Long from, Long to, Long employmentId){
            boolean isValid = false;
            if(employmentIdAndActualTimeBankData.containsKey(employmentId) && isNotNull(from) || isNotNull(to)) {
                if (isNull(from)) {
                    isValid = employmentIdAndActualTimeBankData.get(employmentId) <= to;
                } else if (isNull(to)) {
                    isValid = employmentIdAndActualTimeBankData.get(employmentId) >= from;
                } else {
                    isValid = from <= employmentIdAndActualTimeBankData.get(employmentId) && to >= employmentIdAndActualTimeBankData.get(employmentId);
                }
            }
            return isValid;
        }
    }

    public class FunctionsFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        private Set<LocalDate> functionDates;
        public FunctionsFilter(Map<FilterType, Set<G>> filterCriteriaMap, Set<LocalDate> functionDates) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.functionDates = functionDates;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(FUNCTIONS);
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(functionDates.contains(asLocalDate(shiftDTO.getStartDate()))) {
                        filteredShifts.add((T) shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }
    }

    public class TimeAndAttendanceFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        private List<BigInteger> shiftStateIds;

        public TimeAndAttendanceFilter(Map<FilterType, Set<G>> filterCriteriaMap,List<BigInteger> shiftStateIds) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.shiftStateIds=shiftStateIds;

        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(VALIDATED_BY) && isCollectionNotEmpty(filterCriteriaMap.get(VALIDATED_BY));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if (validFilter) {
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if (shiftStateIds.contains(shiftDTO.getId()) || filterCriteriaMap.get(VALIDATED_BY).contains(String.valueOf(shiftDTO.getAccessGroupRole()))) {
                        filteredShifts.add((T) shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }

    }

    public class TimeTypeFilter <G> implements ShiftFilter{
        private Set<BigInteger> selectedTimeTypes;
        private Map<FilterType, Set<G>> filterCriteriaMap;

        public TimeTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap, Set<BigInteger> selectedTimeTypes) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.selectedTimeTypes = selectedTimeTypes;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = filterCriteriaMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_TYPE));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    List<BigInteger> timeTypeIds = new ArrayList<>();
                    shiftDTO.getActivities().forEach(shiftActivityDTO -> {
                        timeTypeIds.add(shiftActivityDTO.getActivity().getActivityBalanceSettings().getTimeTypeId());
                        if(isCollectionNotEmpty(shiftActivityDTO.getChildActivities())){
                            shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> {
                                if(isNotNull(childActivityDTO.getActivity())) {
                                    timeTypeIds.add(childActivityDTO.getActivity().getActivityBalanceSettings().getTimeTypeId());
                                }
                            });
                        }});
                    if(org.springframework.util.CollectionUtils.containsAny(selectedTimeTypes,timeTypeIds)){
                        filteredShifts.add((T)shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }
    }

    public class EmploymentTypeFilter <G> implements ShiftFilter {

        private Map<FilterType, Set<G>> filterCriteriaMap;
        private Map<Long,Long> employmentIdAndEmploymentTypeIdMap;
        private boolean includeEmploymentTypeFilter;

        public EmploymentTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap, Map<Long,Long> selectedEmploymentTypeIds,boolean includeEmploymentTypeFilter) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.employmentIdAndEmploymentTypeIdMap = selectedEmploymentTypeIds;
            this.includeEmploymentTypeFilter =includeEmploymentTypeFilter;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = ((filterCriteriaMap.containsKey(EMPLOYMENT_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(EMPLOYMENT_TYPE)))&&includeEmploymentTypeFilter) ;
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(getLongValueSetBySetOfObjects(filterCriteriaMap.get(EMPLOYMENT_TYPE)).contains(employmentIdAndEmploymentTypeIdMap.getOrDefault(shiftDTO.getEmploymentId(), ApiConstants.DEFAULT_ID)))
                        filteredShifts.add((T)shiftDTO);
                }
            }
            return filteredShifts;
        }
    }

    public class EmploymentSubTypeFilter <G> implements ShiftFilter {
        private Map<FilterType, Set<G>> filterCriteriaMap;
        private Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeMap;

        public EmploymentSubTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap, Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeMap) {
            this.filterCriteriaMap = filterCriteriaMap;
            this.employmentIdAndEmploymentSubTypeMap = employmentIdAndEmploymentSubTypeMap;
        }

        @Override
        public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
            boolean validFilter = (filterCriteriaMap.containsKey(EMPLOYMENT_SUB_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(EMPLOYMENT_SUB_TYPE)));
            List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
            if(validFilter){
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if(isNotNull(employmentIdAndEmploymentSubTypeMap.get(shiftDTO.getEmploymentId()))) {
                        if (filterCriteriaMap.get(EMPLOYMENT_SUB_TYPE).contains(employmentIdAndEmploymentSubTypeMap.getOrDefault(shiftDTO.getEmploymentId(), EmploymentSubType.NONE).name()))
                            filteredShifts.add((T) shiftDTO);
                    }
                }
            }
            return filteredShifts;
        }
    }


}
