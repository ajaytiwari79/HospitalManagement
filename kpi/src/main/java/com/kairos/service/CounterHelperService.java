package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Day;
import com.kairos.enums.FilterType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.persistence.model.ExceptionService;
import com.kairos.persistence.repository.counter.CounterHelperRepository;
import com.kairos.utils.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDateTime;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.KPIMessagesConstants.MESSAGE_ORGANIZATION_PHASES_ON_DATE;
import static com.kairos.constants.KPIMessagesConstants.MESSAGE_PHASESETTINGS_ABSENT;
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

    public List<ReasonCodeDTO> getReasonCodesByUnitId(Long refId, ReasonCodeType forceplan) {
        return null;
    }
    public List<ShiftWithActivityDTO> getShiftsByFilters(List<ShiftWithActivityDTO> shifts, StaffFilterDTO staffFilterDTO, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        return null;
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
}
