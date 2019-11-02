package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
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
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftFilterService;
import com.kairos.utils.counter.KPIUtils;
import com.kairos.utils.user_context.UserContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOfListByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.CALCULATION_TYPE_NOT_VALID;
import static com.kairos.constants.ActivityMessagesConstants.EXCEPTION_INVALIDREQUEST;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.kpi.CalculationType.TOTAL_MINUTES;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;
import static com.kairos.utils.counter.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.utils.counter.KPIUtils.verifyKPIResponseData;
import static java.util.Map.Entry.comparingByKey;


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
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CostCalculationKPIService costCalculationKPIService;
    @Inject
    private PhaseService phaseService;


    public double getTotalByCalculationBased(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_BASED_ON))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationBasedOn calculationBasedOn = (CalculationBasedOn) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_BASED_ON),CalculationBasedOn.class).get(0);

        double total = 0;
        switch (calculationBasedOn) {
            case ACTIVITY:
            case TIME_TYPE:
                total = getActivityAndTimeTypeTotalByCalulationType(kpiCalculationRelatedInfo);
                break;
            case PLANNED_TIME:
                total = getTotalByPlannedTime(shifts, filterBasedCriteria, shiftActivityDTOS, plannedTimeIds);
                break;
            case VARIABLE_COST:
                total = costCalculationKPIService.calculateTotalCostOfStaff(staffKpiFilterDTO, shiftActivityDTOS, dateTimeInterval);
                break;
            default:
                break;
        }
        return total;
    }

    private double getTotalByPlannedTime(List<ShiftWithActivityDTO> shifts, Map<FilterType, List> filterBasedCriteria, List<ShiftActivityDTO> shiftActivityDTOS, Set<BigInteger> plannedTimeIds) {
        if (isCollectionEmpty(filterBasedCriteria.get(CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = (CalculationType) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_TYPE), CalculationType.class).get(0);
        if (!calculationType.equals(TOTAL_MINUTES)) {
            exceptionService.illegalArgumentException(CALCULATION_TYPE_NOT_VALID);
        }
        int valuesSumInMinutes = shiftActivityDTOS.stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
        double total = valuesSumInMinutes;
        DisplayUnit calculationUnit = (DisplayUnit) filterBasedCriteria.get(CALCULATION_UNIT).get(0);
        if (DisplayUnit.PERCENTAGE.equals(calculationUnit)) {
            int sumOfShifts = shifts.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
            total = (valuesSumInMinutes / sumOfShifts) * 100;
        } else if (DisplayUnit.COUNT.equals(calculationUnit)) {
            total = shiftActivityDTOS.stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).count();
        }
        return total;
    }

    private double getActivityAndTimeTypeTotalByCalulationType(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isCollectionEmpty(filterBasedCriteria.get(CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = (CalculationType) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_TYPE), CalculationType.class).get(0);

        Function<ShiftActivityDTO, Integer> methodParam = ShiftActivityDTO::getScheduledMinutes;
        switch (calculationType) {
            case PLANNED_HOURS_TIMEBANK:
                methodParam = ShiftActivityDTO::getPlannedMinutesOfTimebank;
                break;
            case PLANNED_HOURS_PAYOUT:
                methodParam = ShiftActivityDTO::getPlannedMinutesOfPayout;
                break;
            case SCHEDULED_HOURS:
                methodParam = ShiftActivityDTO::getScheduledMinutes;
                break;
            case COLLECTIVE_TIME_BONUS_PAYOUT:
                methodParam = ShiftActivityDTO::getPayoutCtaBonusMinutes;
                break;
            case COLLECTIVE_TIME_BONUS_TIMEBANK:
                methodParam = ShiftActivityDTO::getTimeBankCtaBonusMinutes;
                break;
            case DURATION_HOURS:
                methodParam = ShiftActivityDTO::getDurationMinutes;
                break;
            case TOTAL_MINUTES:
                methodParam = ShiftActivityDTO::getMinutes;
                break;
            default:
                break;
        }
        return DateUtils.getHoursByMinutes(getTotalByType(filterBasedCriteria,shiftWithActivityDTOS,shiftActivityDTOS,methodParam));
    }

    private double getTotalValueByByType(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, Function<ShiftActivityDTO, Integer> methodParam) {
        if (isCollectionEmpty(filterBasedCriteria.get(CALCULATION_UNIT))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId,dateTimeInterval);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shifts, filterBasedCriteria).invoke();
        Set<BigInteger> plannedTimeIds = filterShiftActivity.getPlannedTimeIds();
        List<ShiftActivityDTO> shiftActivityDTOS = filterShiftActivity.getShiftActivityDTOS();
        int valuesSumInMinutes = shiftActivityDTOS.stream().mapToInt(shiftActivityDTO -> methodParam.apply(shiftActivityDTO)).sum();
        double total = valuesSumInMinutes;
        DisplayUnit calculationUnit = (DisplayUnit) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT), DisplayUnit.class).get(0);
        if (DisplayUnit.PERCENTAGE.equals(calculationUnit)) {
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).mapToInt(shiftActivityDTO -> methodParam.apply(shiftActivityDTO)).sum();
            total = sumOfShifts > 0 ? (valuesSumInMinutes / sumOfShifts) * 100 : sumOfShifts;
        } else if (DisplayUnit.COUNT.equals(calculationUnit)) {
            total = shiftActivityDTOS.size();
        }
        return total;
    }

    private List<CommonKpiDataUnit> getTotalHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        double multiplicationFactor = 1;
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria,organizationId,applicableKPI);
        Map<Object, Double> staffTotalHours =  calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        getKpiDataUnits(multiplicationFactor, staffTotalHours, kpiDataUnits, applicableKPI, kpiCalculationRelatedInfo.staffKpiFilterDTOS);
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

    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, null);
        DisplayUnit displayUnit = (DisplayUnit) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT), DisplayUnit.class).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), displayUnit, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(displayUnit.getDisplayValue(), AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, applicableKPI);
        DisplayUnit displayUnit = (DisplayUnit) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT), DisplayUnit.class).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), displayUnit, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(displayUnit.getDisplayValue(), AppConstants.VALUE_FIELD));
    }


    public Map<Long, Integer> getStaffAndWithTotalHour(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        Map<Object, Double> totalHoursMap = getTotalHoursMap(filterBasedCriteria, organizationId, applicableKPI);
        Map<Long, Integer> staffAndTotalHoursMap = totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().intValue()));
        return staffAndTotalHoursMap;
    }

    private Map<Object, Double> getTotalHoursMap(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria,organizationId,applicableKPI);
        return calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
    }


    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, ApplicableKPI applicableKPI) {
        Map<Long, Integer> staffAndTotalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, applicableKPI);
        return getFibonacciCalculation(staffAndTotalHoursMap, sortingOrder);
    }

    private Map<Object, Double> calculateDataByKpiRepresentation(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<Object, Double> staffTotalHours;
        switch (kpiCalculationRelatedInfo.getApplicableKPI().getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffTotalHours = getStaffTotalByRepresentPerStaff(kpiCalculationRelatedInfo);
                break;
            case REPRESENT_TOTAL_DATA:
                staffTotalHours = getStaffTotalByRepresentTotalData(kpiCalculationRelatedInfo);
                break;
            default:
                staffTotalHours = getStaffTotalByRepresentPerInterval(kpiCalculationRelatedInfo);
                break;
        }
        return verifyKPIResponseData(staffTotalHours) ? staffTotalHours : new HashMap<>();
    }

    private Map<Object, Double> getStaffTotalByRepresentTotalData(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        double totalHours = 0;
        Map<Object, Double> staffTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : kpiCalculationRelatedInfo.getDateTimeIntervals()) {
            for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId,dateTimeInterval);
                totalHours += getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
            }
        }
        staffTotalHours.put(getDateTimeintervalString(new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate())), totalHours);
        return staffTotalHours;
    }

    private Map<Object, Double> getStaffTotalByRepresentPerStaff(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Double totalHours;
        Map<Object, Double> staffTotalHours = new HashMap<>();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId,dateTimeInterval);
            totalHours = getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
            staffTotalHours.put(staffId, totalHours);
        }
        return staffTotalHours;
    }

    private Map<Object, Double> getStaffTotalByRepresentPerInterval(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<Object, Double> staffTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : kpiCalculationRelatedInfo.getDateTimeIntervals()) {
            double totalHours = 0d;
            for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId,dateTimeInterval);
                totalHours += getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
            }
            String key = getKeyByStaffRepresentation(kpiCalculationRelatedInfo, dateTimeInterval);
            staffTotalHours.put(key, totalHours);
        }
        return staffTotalHours;
    }

    private String getKeyByStaffRepresentation(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval) {
        String key;
        if(DurationType.HOURS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())){
            key = getLocalTimeByFormat(asLocalDateTime(dateTimeInterval.getStartDate()));
        }else if(DurationType.DAYS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())){
            key = getStartDateTimeintervalString(dateTimeInterval);
        }else {
            key = getDateTimeintervalString(dateTimeInterval);
        }
        return key;
    }

    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        Map<LocalDateTime, Double>  sortedStaffKpiCostDataMap=new HashMap<>();
        KPIResponseDTO kpiResponseDTO = new KPIResponseDTO();
        if(DurationType.HOURS.equals(applicableKPI.getFrequencyType())){
            Map<LocalDateTime, Double>  staffKpiCostDataMap=getTotalHoursMap(filterBasedCriteria, organizationId, applicableKPI).entrySet().stream().collect(Collectors.toMap(k -> getLocaDateTimebyString(k.getKey().toString()), v -> v.getValue().doubleValue()));;
            sortedStaffKpiCostDataMap= staffKpiCostDataMap.entrySet().stream()
                    .sorted(comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            kpiResponseDTO.setKpiValue(sortedStaffKpiCostDataMap);
        }else {
            Map<Long, Integer> totalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, applicableKPI);
            Map<Long, Double> staffKpiDataMap = totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().doubleValue()));
            kpiResponseDTO.setStaffKPIValue(staffKpiDataMap);
        }
        kpiResponseDTO.setKpiName(kpi.getTitle());
        kpiResponseDTO.setKpiId(kpi.getId());
        return kpiResponseDTO;
    }

    private class FilterShiftActivity {
        private List<ShiftWithActivityDTO> shifts;
        private Map<FilterType, List> filterBasedCriteria;
        private Set<BigInteger> plannedTimeIds;
        private List<ShiftActivityDTO> shiftActivityDTOS;

        public FilterShiftActivity(List<ShiftWithActivityDTO> shifts, Map<FilterType, List> filterBasedCriteria) {
            this.shifts = shifts;
            this.filterBasedCriteria = filterBasedCriteria;
        }

        public Set<BigInteger> getPlannedTimeIds() {
            return plannedTimeIds;
        }

        public List<ShiftActivityDTO> getShiftActivityDTOS() {
            return shiftActivityDTOS;
        }

        public FilterShiftActivity invoke() {
            Set<BigInteger> timeTypeIds = filterBasedCriteria.containsKey(TIME_TYPE) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(TIME_TYPE)) : new HashSet<>();
            plannedTimeIds = filterBasedCriteria.containsKey(PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(PLANNED_TIME_TYPE)) : new HashSet<>();
            Set<BigInteger> activityIds = filterBasedCriteria.containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ACTIVITY_IDS)) : new HashSet<>();
            if(filterBasedCriteria.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterBasedCriteria.get(ABSENCE_ACTIVITY))){
                activityIds.addAll(KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ABSENCE_ACTIVITY)));
            }
            if(filterBasedCriteria.containsKey(TEAM)){
                Set<String> teamIds = (Set<String>)filterBasedCriteria.get(TEAM);
                ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(UserContext.getUserDetails().getLastSelectedOrganizationId(),teamIds));
                activityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
            }
            shiftActivityDTOS = shifts.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).filter(shiftActivityDTO -> isShiftActivityValid(filterBasedCriteria, shiftActivityDTO, timeTypeIds, activityIds, plannedTimeIds)).collect(Collectors.toList());
            return this;
        }

        private boolean isShiftActivityValid(Map<FilterType, List> filterBasedCriteria, ShiftActivityDTO shiftActivityDTO, Set<BigInteger> timeTypeIds, Set<BigInteger> activityIds, Set<BigInteger> plannedTimeIds){
            boolean validTimeType = isCollectionEmpty(timeTypeIds) || timeTypeIds.contains(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId());
            boolean validActivity = isCollectionEmpty(activityIds) || activityIds.contains(shiftActivityDTO.getActivityId());
            boolean validPlannedTime = isCollectionEmpty(plannedTimeIds) || CollectionUtils.containsAny(plannedTimeIds,shiftActivityDTO.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
            return validActivity && validTimeType && validPlannedTime && CollectionUtils.containsAny(filterBasedCriteria.get(ACTIVITY_STATUS),shiftActivityDTO.getStatus());
        }
    }

    @Getter
    @Setter
    private class KPICalculationRelatedInfo{
        private Map<FilterType, List> filterBasedCriteria;
        private List<ShiftWithActivityDTO> shifts;
        private List<Long> staffIds;
        private List<DateTimeInterval> dateTimeIntervals;
        private List<StaffKpiFilterDTO> staffKpiFilterDTOS;
        private Long organizationId;
        private ApplicableKPI applicableKPI;
        private Map<DateTimeInterval, List<ShiftWithActivityDTO>> intervalShiftsMap;
        private Map<Long, StaffKpiFilterDTO> staffIdAndStaffKpiFilterMap;
        private Map<Long, List<ShiftWithActivityDTO>> staffIdAndShiftsMap;

        public KPICalculationRelatedInfo(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
            this.filterBasedCriteria = filterBasedCriteria;
            this.organizationId = organizationId;
            this.applicableKPI = applicableKPI;
            loadKpiCalculationRelatedInfo(filterBasedCriteria,organizationId,applicableKPI);
            updateIntervalShiftsMap(applicableKPI);
            staffIdAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            staffIdAndShiftsMap = shifts.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));

        }

        private void updateIntervalShiftsMap(ApplicableKPI applicableKPI) {
            intervalShiftsMap = new HashMap<>();
            for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                intervalShiftsMap.put(dateTimeInterval, shifts.stream().filter(shift -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate())) : dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
            }
        }


        private void loadKpiCalculationRelatedInfo(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
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
            Set<DayOfWeek> daysOfWeeks = (Set<DayOfWeek>) filterCriteria[4];
            daysOfWeeks.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
            shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
            StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, timeSlotDTOS, organizationId);
            shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO);
        }

        private StaffFilterDTO getStaffFilterDto(Map<FilterType, List> filterBasedCriteria, List<TimeSlotDTO> timeSlotDTOS, Long organizationId) {
            StaffFilterDTO staffFilterDTO = new StaffFilterDTO();
            List<FilterSelectionDTO> filterData = filterBasedCriteria.entrySet().stream().map(filterTypeListEntry -> {
                if (filterTypeListEntry.getKey().equals(TIME_SLOT)) {
                    Set<String> timeSlotName = new HashSet<>();
                    for (Object timeSlotId : filterTypeListEntry.getValue()) {
                        for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                            if (timeSlotDTO.getId().equals(((Integer) timeSlotId).longValue())) {
                                timeSlotName.add(timeSlotDTO.getName());
                            }
                        }
                    }
                    return new FilterSelectionDTO(filterTypeListEntry.getKey(), timeSlotName);
                } else {
                    return new FilterSelectionDTO(filterTypeListEntry.getKey(), new HashSet<String>(filterTypeListEntry.getValue()));
                }
            }).collect(Collectors.toList());
            if (filterBasedCriteria.containsKey(PHASE)) {
                List<PhaseDTO> phases = phaseService.getPhasesByUnit(organizationId);
                Set<PhaseDefaultName> phaseDefaultNames = (Set<PhaseDefaultName>) filterBasedCriteria.get(FilterType.PHASE).stream().map(value -> PhaseDefaultName.valueOf(value.toString())).collect(Collectors.toSet());
                Set<String> phaseIds = phases.stream().filter(phaseDTO -> phaseDefaultNames.contains(phaseDTO.getPhaseEnum())).map(phaseDTO -> phaseDTO.getId().toString()).collect(Collectors.toSet());
                filterData.add(new FilterSelectionDTO(PHASE, phaseIds));
            }
            if (filterBasedCriteria.containsKey(TEAM)) {
                filterData.add(new FilterSelectionDTO(TEAM, (Set<String>) filterBasedCriteria.get(TEAM)));
            }
            staffFilterDTO.setFiltersData(filterData);
            return staffFilterDTO;
        }

        public List<ShiftWithActivityDTO> getShiftsByStaffIdAndInterval(Long staffId,DateTimeInterval dateTimeInterval){
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = staffIdAndShiftsMap.getOrDefault(staffId, new ArrayList<>());
            return shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList());
        }
    }
}
