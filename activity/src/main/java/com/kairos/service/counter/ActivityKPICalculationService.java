package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.CalculationBasedOn;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftFilterService;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDateTimeintervalString;
import static com.kairos.commons.utils.DateUtils.getStartDateTimeintervalString;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOfListByMapper;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ActivityMessagesConstants.CALCULATION_TYPE_NOT_VALID;
import static com.kairos.constants.ActivityMessagesConstants.EXCEPTION_INVALIDREQUEST;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.kpi.CalculationType.TOTAL_MINUTES;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;
import static com.kairos.utils.counter.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.utils.counter.KPIUtils.verifyKPIResponseData;


@Service
public class ActivityKPICalculationService implements CounterService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;
    @Inject
    private ShiftFilterService shiftFilterService;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ExceptionService exceptionService;

    public double getTotal(List<ShiftWithActivityDTO> shifts,Map<FilterType, List> filterBasedCriteria) {
        if(isCollectionEmpty(filterBasedCriteria.get(CALCULATION_BASED_ON))){
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationBasedOn calculationBasedOn = (CalculationBasedOn) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_BASED_ON),CalculationBasedOn.class).get(0);
        Set<BigInteger> timeTypeIds = filterBasedCriteria.containsKey(TIME_TYPE) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(TIME_TYPE)) : new HashSet<>();
        Set<BigInteger> plannedTimeIds = filterBasedCriteria.containsKey(PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(PLANNED_TIME_TYPE)) : new HashSet<>();
        Set<BigInteger> activityIds = filterBasedCriteria.containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ACTIVITY_IDS)) : new HashSet<>();
        if(filterBasedCriteria.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterBasedCriteria.get(ABSENCE_ACTIVITY))){
            activityIds.addAll(KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ABSENCE_ACTIVITY)));
        }
        List<ShiftActivityDTO> shiftActivityDTOS = shifts.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).filter(shiftActivityDTO->isShiftActivityValid(shiftActivityDTO,timeTypeIds,activityIds,plannedTimeIds)).collect(Collectors.toList());
        double total = 0;
        switch (calculationBasedOn){
            case ACTIVITY:
            case TIME_TYPE:total = getActivityAndTimeTypeTotalByCalulationType(filterBasedCriteria,shifts,shiftActivityDTOS);
                break;
            case PLANNED_TIME: total = getTotalByPlannedTime(shifts,filterBasedCriteria,shiftActivityDTOS,plannedTimeIds);
                break;
            default:break;
        }
        return total;
    }

    private double getTotalByPlannedTime(List<ShiftWithActivityDTO> shifts,Map<FilterType, List> filterBasedCriteria,List<ShiftActivityDTO> shiftActivityDTOS,Set<BigInteger> plannedTimeIds){
        if(isCollectionEmpty(filterBasedCriteria.get(CALCULATION_TYPE))){
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = (CalculationType)copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_TYPE),CalculationType.class).get(0);
        if(!calculationType.equals(TOTAL_MINUTES)){
            exceptionService.illegalArgumentException(CALCULATION_TYPE_NOT_VALID);
        }
        int valuesSumInMinutes = shiftActivityDTOS.stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).mapToInt(plannedTime->(int)plannedTime.getInterval().getMinutes()).sum();
        double total = valuesSumInMinutes;
        DisplayUnit calculationUnit = (DisplayUnit)filterBasedCriteria.get(CALCULATION_UNIT).get(0);
        if(DisplayUnit.PERCENTAGE.equals(calculationUnit)){
            int sumOfShifts = shifts.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream())).mapToInt(plannedTime->(int)plannedTime.getInterval().getMinutes()).sum();
            total = (valuesSumInMinutes/sumOfShifts) * 100;
        }else if(DisplayUnit.COUNT.equals(calculationUnit)){
            total = shiftActivityDTOS.stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).count();
        }
        return total;
    }

    private double getActivityAndTimeTypeTotalByCalulationType(Map<FilterType, List> filterBasedCriteria,List<ShiftWithActivityDTO> shiftWithActivityDTOS,List<ShiftActivityDTO> shiftActivityDTOS){
        if(isCollectionEmpty(filterBasedCriteria.get(CALCULATION_TYPE))){
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = (CalculationType)copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_TYPE),CalculationType.class).get(0);
        Function<ShiftActivityDTO,Integer> methodParam = ShiftActivityDTO::getScheduledMinutes;
        switch (calculationType){
            case PLANNED_HOURS_TIMEBANK: methodParam = ShiftActivityDTO::getPlannedMinutesOfTimebank;
                break;
            case PLANNED_HOURS_PAYOUT:methodParam = ShiftActivityDTO::getPlannedMinutesOfPayout;
                break;
            case SCHEDULED_HOURS:methodParam = ShiftActivityDTO::getScheduledMinutes;
                break;
            case COLLECTIVE_TIME_BONUS_PAYOUT:methodParam = ShiftActivityDTO::getPayoutCtaBonusMinutes;
                break;
            case COLLECTIVE_TIME_BONUS_TIMEBANK:methodParam = ShiftActivityDTO::getTimeBankCtaBonusMinutes;
                break;
            case DURATION_HOURS:methodParam = ShiftActivityDTO::getDurationMinutes;
                break;
            case TOTAL_MINUTES:methodParam = ShiftActivityDTO::getMinutes;
                break;
                default:break;
        }
        return getTotalByType(filterBasedCriteria,shiftWithActivityDTOS,shiftActivityDTOS,methodParam);
    }

    private double getTotalByType(Map<FilterType, List> filterBasedCriteria,List<ShiftWithActivityDTO> shiftWithActivityDTOS,List<ShiftActivityDTO> shiftActivityDTOS,Function<ShiftActivityDTO,Integer> methodParam){
        if(isCollectionEmpty(filterBasedCriteria.get(CALCULATION_UNIT))){
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        int valuesSumInMinutes = shiftActivityDTOS.stream().mapToInt(shiftActivityDTO->methodParam.apply(shiftActivityDTO)).sum();
        double total = valuesSumInMinutes;
        DisplayUnit calculationUnit = (DisplayUnit)copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT),DisplayUnit.class).get(0);
        if(DisplayUnit.PERCENTAGE.equals(calculationUnit)){
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).mapToInt(shiftActivityDTO->methodParam.apply(shiftActivityDTO)).sum();
            total = sumOfShifts > 0 ? (valuesSumInMinutes/sumOfShifts) * 100 : sumOfShifts;
        }else if(DisplayUnit.COUNT.equals(calculationUnit)){
            total = shiftActivityDTOS.size();
        }
        return total;
    }

    private boolean isShiftActivityValid(ShiftActivityDTO shiftActivityDTO,Set<BigInteger> timeTypeIds,Set<BigInteger> activityIds,Set<BigInteger> plannedTimeIds){
        boolean validTimeType = isCollectionEmpty(timeTypeIds) || timeTypeIds.contains(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId());
        boolean validActivity = isCollectionEmpty(activityIds) || activityIds.contains(shiftActivityDTO.getActivityId());
        boolean validPlannedTime = isCollectionEmpty(plannedTimeIds) || CollectionUtils.containsAny(plannedTimeIds,shiftActivityDTO.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
        return validActivity && validTimeType && validPlannedTime;
    }

    public Map<Object, Double> calculateTotalHours(Map<FilterType, List> filterBasedCriteria,List<Long> staffIds, ApplicableKPI applicableKPI, List<DateTimeInterval> dateTimeIntervals, List<ShiftWithActivityDTO> shifts) {
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        return calculateDataByKpiRepresentation(filterBasedCriteria,staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI, shifts);
    }

    private List<CommonKpiDataUnit> getTotalHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        // TO BE USED FOR AVERAGE CALCULATION.
        double multiplicationFactor = 1;
        Object[] objects = getKpiData(filterBasedCriteria, organizationId, applicableKPI);
        List<ShiftWithActivityDTO> shifts = (List<ShiftWithActivityDTO>) objects[0];
        List<Long> staffIds = (List<Long>) objects[1];
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) objects[2];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) objects[3];
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Map<Object, Double> staffTotalHours = calculateTotalHours(filterBasedCriteria,staffIds, applicableKPI, dateTimeIntervals, shifts);
        getKpiDataUnits(multiplicationFactor, staffTotalHours, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    private void getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffTotalHours, List<CommonKpiDataUnit> kpiDataUnits, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        for (Map.Entry<Object, Double> entry : staffTotalHours.entrySet()) {
            switch (applicableKPI.getKpiRepresentation()) {
                case REPRESENT_PER_STAFF:
                    Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                    kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue() * multiplicationFactor))));
                    break;
                default:
                    kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), Arrays.asList(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue() * multiplicationFactor))));
                    break;

            }
        }
    }

    private Object[] getKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>) filterCriteria[3];
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId);
        List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
        staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        Set<DayOfWeek> daysOfWeeks = (Set<DayOfWeek>)filterCriteria[4];
        daysOfWeeks.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, timeSlotDTOS);
        shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO);
        return new Object[]{shifts, staffIds, dateTimeIntervals, staffKpiFilterDTOS};
    }

    private StaffFilterDTO getStaffFilterDto(Map<FilterType, List> filterBasedCriteria, List<TimeSlotDTO> timeSlotDTOS) {
        StaffFilterDTO staffFilterDTO = new StaffFilterDTO();
        staffFilterDTO.setFiltersData(filterBasedCriteria.entrySet().stream().map(filterTypeListEntry -> {
            if(filterTypeListEntry.getKey().equals(TIME_SLOT)){
                Set<String> timeSlotName = new HashSet<>();
                for (Object timeSlotId : filterTypeListEntry.getValue()) {
                    for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                        if(timeSlotDTO.getId().equals(((Integer)timeSlotId).longValue())){
                            timeSlotName.add(timeSlotDTO.getName());
                        }
                    }
                }
                return new FilterSelectionDTO(filterTypeListEntry.getKey(), timeSlotName);
            }else {
                return new FilterSelectionDTO(filterTypeListEntry.getKey(), new HashSet<String>(filterTypeListEntry.getValue()));
            }
        }).collect(Collectors.toList()));
        return staffFilterDTO;
    }


    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, null);
        DisplayUnit displayUnit = (DisplayUnit)copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT),DisplayUnit.class).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), displayUnit, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(displayUnit.getDisplayValue(), AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, applicableKPI);
        DisplayUnit displayUnit = (DisplayUnit)copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT),DisplayUnit.class).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), displayUnit, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(displayUnit.getDisplayValue(), AppConstants.VALUE_FIELD));
    }


    public Map<Long, Integer> getStaffAndWithTotalHour(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        Object[] objects = getKpiData(filterBasedCriteria, organizationId, applicableKPI);
        List<ShiftWithActivityDTO> shifts = (List<ShiftWithActivityDTO>) objects[0];
        List<Long> staffIds = (List<Long>) objects[1];
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) objects[2];
        Map<Object, Double> totalHoursMap = calculateDataByKpiRepresentation(filterBasedCriteria,staffIds, null, dateTimeIntervals, applicableKPI, shifts);
        Map<Long, Integer> staffAndTotalHoursMap = totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().intValue()));
        return staffAndTotalHoursMap;
    }


    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, ApplicableKPI applicableKPI) {
        Map<Long, Integer> staffAndTotalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, applicableKPI);
        return getFibonacciCalculation(staffAndTotalHoursMap, sortingOrder);
    }

    private Map<Object, Double> calculateDataByKpiRepresentation(Map<FilterType, List> filterBasedCriteria,List<Long> staffIds, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<ShiftWithActivityDTO> shifts) {
        Map<Object, Double> staffTotalHours;
        Double totalHours = 0d;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffTotalHours = getStaffTotalByRepresentPerStaff(filterBasedCriteria,staffIds, dateTimeIntervals, shifts);
                break;
            case REPRESENT_TOTAL_DATA:
                staffTotalHours = getStaffTotalByRepresentTotalData(filterBasedCriteria,staffIds, dateTimeIntervals, shifts, totalHours);
                break;
            default:
                staffTotalHours = getStaffTotalByRepresentPerInterval(filterBasedCriteria,staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI.getFrequencyType());
                break;
        }
        return verifyKPIResponseData(staffTotalHours) ? staffTotalHours : new HashMap<>();
    }

    private Map<Object, Double> getStaffTotalByRepresentTotalData(Map<FilterType, List> filterBasedCriteria,List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<ShiftWithActivityDTO> shifts, Double totalHours) {
        Map<Object, Double> staffTotalHours = new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            for (Long staffId : staffIds) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = staffShiftMapping.getOrDefault(staffId, new ArrayList<>());
                shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList());
                totalHours += getTotal(shiftWithActivityDTOS,filterBasedCriteria);
            }
        }
        staffTotalHours.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), totalHours);
        return staffTotalHours;
    }

    private Map<Object, Double> getStaffTotalByRepresentPerStaff(Map<FilterType, List> filterBasedCriteria,List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<ShiftWithActivityDTO> shifts) {
        Double totalHours;
        Map<Object, Double> staffTotalHours = new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        DateTimeInterval dateTimeInterval = new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(),dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        for (Long staffId : staffIds) {
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = staffShiftMapping.getOrDefault(staffId, new ArrayList<>());
            shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList());
            totalHours = getTotal(shiftWithActivityDTOS,filterBasedCriteria);
            staffTotalHours.put(staffId, totalHours);
        }
        return staffTotalHours;
    }

    private Map<Object, Double> getStaffTotalByRepresentPerInterval(Map<FilterType, List> filterBasedCriteria,List<Long> staffIds, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, DurationType frequencyType) {
        Double totalHours;
        Map<Object, Double> staffTotalHours = new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping;
        Map<DateTimeInterval, Map<Long, List<ShiftWithActivityDTO>>> dateTimeIntervalListMap1 = new HashedMap();
        dateTimeIntervalListMap.keySet().stream().forEach(dateTimeInterval -> dateTimeIntervalListMap1.put(dateTimeInterval, dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()))));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            totalHours = 0d;
            staffShiftMapping = dateTimeIntervalListMap1.get(dateTimeInterval);
            for (Long staffId : staffIds) {
                totalHours += getTotal(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()).stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList()),filterBasedCriteria);
            }
            staffTotalHours.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), totalHours);
        }
        return staffTotalHours;
    }

    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPIResponseDTO kpiResponseDTO = new KPIResponseDTO();
        Map<Long, Integer> totalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, applicableKPI);
        Map<Long, Double> staffAndTotalHoursMap = totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().doubleValue()));
        kpiResponseDTO.setKpiName(kpi.getTitle());
        kpiResponseDTO.setKpiId(kpi.getId());
        kpiResponseDTO.setStaffKPIValue(staffAndTotalHoursMap);
        return kpiResponseDTO;
    }
}
