package com.kairos.service.counter;

import com.kairos.commons.service.audit_logging.AuditLoggingService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.constants.KPIMessagesConstants;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.activity.activity_tabs.ApprovalCriteria;
import com.kairos.dto.activity.activity.activity_tabs.PQLSettings;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.persistence.model.ExceptionService;
import com.kairos.persistence.model.FibonacciKPICalculation;
import com.kairos.persistence.model.KPI;
import com.kairos.utils.counter.FibonacciCalculationUtil;
import com.kairos.utils.counter.KPIUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.enums.kpi.CalculationType.*;
import static com.kairos.enums.kpi.CalculationType.TOTAL_PLANNED_HOURS;
import static com.kairos.enums.kpi.KPIRepresentation.*;
import static com.kairos.enums.kpi.YAxisConfig.*;
import static com.kairos.enums.shift.TodoStatus.*;
import static com.kairos.enums.shift.TodoStatus.DISAPPROVE;
import static java.util.Map.Entry.comparingByKey;

@Getter
@Service
public class KPIBuilderCalculationService implements CounterService {

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
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Autowired
    @Lazy
    private TodoService todoService;
    @Inject
    private AbsencePlanningKPIService absencePlanningKPIService;
    @Inject
    private StaffingLevelCalculationKPIService staffingLevelCalculationKPIService;
    @Inject
    private AuditLoggingService auditLoggingService;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject private ShiftEscalationService shiftEscalationService;
    @Inject private UnavailabilityCalculationKPIService unavailabilityCalculationKPIService;
    @Inject private TimeBankOffKPIService timeBankOffKPIService;
    @Inject
    private CounterServiceMapping counterServiceMapping;


    public Double getTotalByCalculationBased(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
        if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_BASED_ON))) {
            exceptionService.dataNotFoundException(KPIMessagesConstants.EXCEPTION_INVALIDREQUEST);
        }
        double total = 0;
        if (PLANNED_TIME.equals(yAxisConfig)) {
            total = getTotalByPlannedTime(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        } else {
            total = getActivityAndTimeTypeTotalByCalulationType(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        }
        return KPIUtils.getValueWithDecimalFormat(total);
    }


    private double getNumberOfBreakInterrupt(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval,true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        long interruptShift = filterShiftActivity.shifts.stream().filter(k -> k.getBreakActivities().stream().anyMatch(ShiftActivityDTO::isBreakInterrupt)).count();
        if (XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return ObjectUtils.isCollectionNotEmpty(filterShiftActivity.shifts) ? KPIUtils.getValueWithDecimalFormat((interruptShift * 100.0d) / filterShiftActivity.shifts.size()) : 0;
        } else
            return interruptShift;
    }


    private double getTotalByPlannedTime(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(KPIMessagesConstants.EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = ((List<CalculationType>) ObjectMapperUtils.copyCollectionPropertiesByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_TYPE), CalculationType.class)).get(0);
        if (!calculationType.equals(TOTAL_MINUTES)) {
            exceptionService.illegalArgumentException(KPIMessagesConstants.CALCULATION_TYPE_NOT_VALID);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        Set<BigInteger> plannedTimeIds = shiftActivityCriteria.getPlannedTimeIds();
        int valuesSumInMinutes = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
        double total = DateUtils.getHoursByMinutes(valuesSumInMinutes);
        XAxisConfig calculationUnit = (XAxisConfig) ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_UNIT), XAxisConfig.class)).get(0);
        if (XAxisConfig.PERCENTAGE.equals(calculationUnit)) {
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
            total = sumOfShifts > 0 ? KPIUtils.getValueWithDecimalFormat((valuesSumInMinutes * 100.0) / sumOfShifts) : valuesSumInMinutes;

        } else if (XAxisConfig.COUNT.equals(calculationUnit)) {
            total = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).count();
        }
        return total;
    }

    public ShiftActivityCriteria getShiftActivityCriteria(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
            return kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria();
        }
        ShiftActivityCriteria currentShiftActivityCriteria = kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria();
        Set<BigInteger> timeTypeIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.TIME_TYPE) ? kpiCalculationRelatedInfo.getTimeTypeMap().keySet() : new HashSet<>();
        Set<BigInteger> plannedTimeIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.PLANNED_TIME_TYPE)) : new HashSet<>();
        Set<BigInteger> activityIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_IDS)) : new HashSet<>();
        if (kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.ABSENCE_ACTIVITY) && ObjectUtils.isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ABSENCE_ACTIVITY))) {
            activityIds.addAll(KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ABSENCE_ACTIVITY)));
        }
        return ShiftActivityCriteria.builder().shiftStatuses(currentShiftActivityCriteria.shiftStatuses).activityIds(activityIds).teamActivityIds(currentShiftActivityCriteria.teamActivityIds).plannedTimeIds(plannedTimeIds).reasonCodeIds(currentShiftActivityCriteria.reasonCodeIds).timeTypeIds(timeTypeIds).build();
    }

    private double getActivityAndTimeTypeTotalByCalulationType(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(KPIMessagesConstants.EXCEPTION_INVALIDREQUEST);
        }
        Function<ShiftActivityDTO, Integer> methodParam = null;
        switch (kpiCalculationRelatedInfo.getCalculationType()) {
            case PLANNED_HOURS_TIMEBANK:
                methodParam = ShiftActivityDTO::getPlannedMinutesOfTimebank;
                break;
            case PAYOUT:
                methodParam = ShiftActivityDTO::getPlannedMinutesOfPayout;
                break;
            case TOTAL_PLANNED_HOURS:
                methodParam = ShiftActivityDTO::getTotalPlannedMinutes;
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
            case TOTAL_COLLECTIVE_BONUS:
                methodParam = ShiftActivityDTO::getTotalCtaBonusMinutes;
                break;
            case DURATION_HOURS:
                methodParam = ShiftActivityDTO::getDurationMinutes;
                break;
            case TOTAL_MINUTES:
                methodParam = ShiftActivityDTO::getMinutes;
                break;
            default:
                return counterServiceMapping.getKpiServiceMap(kpiCalculationRelatedInfo.getCalculationType()).get(staffId,dateTimeInterval,kpiCalculationRelatedInfo,null);
        }
        return getTotalValueByByType(staffId, dateTimeInterval, kpiCalculationRelatedInfo, methodParam);
    }

    private double getTotalValueByByType(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, Function<ShiftActivityDTO, Integer> methodParam) {
        if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_UNIT))) {
            exceptionService.dataNotFoundException(KPIMessagesConstants.EXCEPTION_INVALIDREQUEST);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        List<ShiftActivityDTO> shiftActivityDTOS = filterShiftActivity.getShiftActivityDTOS();
        int valuesSumInMinutes = shiftActivityDTOS.stream().mapToInt(methodParam::apply).sum();
        double total = DateUtils.getHoursByMinutes(valuesSumInMinutes);
        if (XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, false);
            shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
            filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
            shiftWithActivityDTOS = filterShiftActivity.getShifts();
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).mapToInt(methodParam::apply).sum();
            total = sumOfShifts > 0 ? (KPIUtils.getValueWithDecimalFormat(valuesSumInMinutes * 100.0d)) / sumOfShifts : valuesSumInMinutes;
        } else if (XAxisConfig.COUNT.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            total = shiftActivityDTOS.size();
        } else if (XAxisConfig.VARIABLE_COST.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            total = costCalculationKPIService.calculateTotalCostOfStaff(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        } else if (XAxisConfig.AVERAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            Set<LocalDate> localDates = shiftActivityDTOS.stream().map(shiftActivityDTO -> DateUtils.asLocalDate(shiftActivityDTO.getStartDate())).collect(Collectors.toSet());
            if (DurationType.WEEKS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
                total = KPIUtils.getValueWithDecimalFormat(((double) localDates.size() / kpiCalculationRelatedInfo.getApplicableKPI().getValue()));
            }
            if (DurationType.MONTHS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
                total = KPIUtils.getValueWithDecimalFormat(((double) localDates.size() / (kpiCalculationRelatedInfo.getApplicableKPI().getValue() * 4)));
            }
        }
        return total;
    }

    private List<CommonKpiDataUnit> getTotalHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        double multiplicationFactor = 1;
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria, organizationId, applicableKPI, kpi,this);
        List<CommonKpiDataUnit> kpiDataUnits;
        if (kpi.isMultiDimensional()) {
            Map<Object, List<ClusteredBarChartKpiDataUnit>> staffTotalHours = calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
            kpiDataUnits = getKpiDataUnits(staffTotalHours, applicableKPI, kpiCalculationRelatedInfo.getStaffKpiFilterDTOS());
        } else {
            Map<Object, Double> staffTotalHours = calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
            kpiDataUnits = getKpiDataUnits(multiplicationFactor, staffTotalHours, applicableKPI, kpiCalculationRelatedInfo.getStaffKpiFilterDTOS());
        }
        KPIUtils.sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    private List<CommonKpiDataUnit> getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffTotalHours, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        for (Map.Entry<Object, Double> entry : staffTotalHours.entrySet()) {
            if (REPRESENT_PER_STAFF.equals(applicableKPI.getKpiRepresentation()) || INDIVIDUAL_STAFF.equals(applicableKPI.getKpiRepresentation())) {
                Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue() * multiplicationFactor))));
            } else {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(KPIUtils.getKpiDateFormatByIntervalUnit(entry.getKey().toString(), applicableKPI.getFrequencyType(), applicableKPI.getKpiRepresentation()), entry.getKey().toString(), Arrays.asList(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue() * multiplicationFactor))));
            }
        }
        return kpiDataUnits;
    }

    public static List<CommonKpiDataUnit> getKpiDataUnits(Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        for (Map.Entry<Object, List<ClusteredBarChartKpiDataUnit>> entry : objectListMap.entrySet()) {
            if (REPRESENT_PER_STAFF.equals(applicableKPI.getKpiRepresentation())) {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue()));
            } else {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(KPIUtils.getKpiDateFormatByIntervalUnit(entry.getKey().toString(), applicableKPI.getFrequencyType(), applicableKPI.getKpiRepresentation()), entry.getKey().toString(), entry.getValue()));
            }
        }
        return kpiDataUnits;
    }


    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi, null);
        XAxisConfig xAxisConfig = (XAxisConfig) ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(filterBasedCriteria.get(FilterType.CALCULATION_UNIT), XAxisConfig.class)).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), xAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(xAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi, applicableKPI);
        XAxisConfig xAxisConfig = (XAxisConfig) ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(filterBasedCriteria.get(FilterType.CALCULATION_UNIT), XAxisConfig.class)).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), xAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(xAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }


    public Map<Long, Double> getStaffAndWithTotalHour(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        Map<Object, Double> totalHoursMap = getTotalHoursMap(filterBasedCriteria, organizationId, kpi, applicableKPI);
        return totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue()));
    }

    private Map<Object, Double> getTotalHoursMap(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria, organizationId, applicableKPI, kpi,this);
        return calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
    }


    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, KPI kpi, ApplicableKPI applicableKPI) {
        Map<Long, Integer> staffAndTotalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, kpi, applicableKPI).entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().intValue()));
        return FibonacciCalculationUtil.getFibonacciCalculation(staffAndTotalHoursMap, sortingOrder);
    }


    private <T, E> Map<T, E> calculateDataByKpiRepresentation(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<T, E> staffTotalHours;
        switch (kpiCalculationRelatedInfo.getApplicableKPI().getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
            case INDIVIDUAL_STAFF:
                staffTotalHours = getStaffTotalByRepresentPerStaff(kpiCalculationRelatedInfo);
                break;
            case REPRESENT_TOTAL_DATA:
                staffTotalHours = getStaffTotalByRepresentTotalData(kpiCalculationRelatedInfo);
                break;
            default:
                staffTotalHours = getStaffTotalByRepresentPerInterval(kpiCalculationRelatedInfo);
                break;
        }
        return KPIUtils.verifyKPIResponseData(staffTotalHours) ? staffTotalHours : new HashMap<>();
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentTotalData(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Double totalHours = 0d;
        Map<T, E> staffTotalHours = new HashMap<>();
        DateTimeInterval totalDataInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        if (!kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
            totalHours += getTotalByCalculationBased(null, totalDataInterval, kpiCalculationRelatedInfo, kpiCalculationRelatedInfo.getYAxisConfigs().get(0));
        }
        T key = (T) DateUtils.getDateTimeintervalString(totalDataInterval);
        staffTotalHours.put(key, kpiCalculationRelatedInfo.getKpi().isMultiDimensional() ? (E) getClusteredBarChartDetails(null, totalDataInterval, kpiCalculationRelatedInfo) : (E) totalHours);
        return staffTotalHours;
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentPerStaff(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<T, E> staffTotalHours = new HashMap<>();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
            if (kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
                staffTotalHours.put((T) staffId, (E) getClusteredBarChartDetails(staffId, dateTimeInterval, kpiCalculationRelatedInfo));
            } else {
                staffTotalHours.put((T) staffId, (E) getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, kpiCalculationRelatedInfo.getYAxisConfigs().get(0)));
            }
        }
        return staffTotalHours;
    }

    private List<ClusteredBarChartKpiDataUnit> getClusteredBarChartDetails(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (YAxisConfig yAxisConfig : kpiCalculationRelatedInfo.getYAxisConfigs()) {
            kpiCalculationRelatedInfo.setCurrentCalculationType(null);
            switch (yAxisConfig) {
                case ACTIVITY:
                    subClusteredBarValue.addAll(getActivitySubClusteredValue(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig));
                    break;
                case TIME_TYPE:
                    subClusteredBarValue.addAll(getTimeTypeSubClusteredValue(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig));
                    break;
                case PLANNED_TIME:
                    subClusteredBarValue.addAll(getPlannedTimeSubClusteredValue(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig));
                    break;
                case PLANNING_QUALITY_LEVEL:
                    subClusteredBarValue.addAll(geTodoSubClusteredValue(staffId, dateTimeInterval, kpiCalculationRelatedInfo));
                    break;
                case ABSENCE_REQUEST:
                    List<TodoDTO> todoDTOList = kpiCalculationRelatedInfo.getTodosByStaffIdAndInterval(staffId, dateTimeInterval);
                    List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits = absencePlanningKPIService.getActivityStatusCount(todoDTOList, kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
                    subClusteredBarValue.addAll(clusteredBarChartKpiDataUnits);
                    break;
                default:
                    Double count = counterServiceMapping.getKpiServiceMap(ObjectMapperUtils.copyPropertiesByMapper(yAxisConfig, CalculationType.class)).get(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(yAxisConfig.value, count));
                    break;
            }
        }
        return subClusteredBarValue;
    }

    private List<ClusteredBarChartKpiDataUnit> getActivitySubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (CalculationType calculationType : kpiCalculationRelatedInfo.getCalculationTypes()) {
            kpiCalculationRelatedInfo.setCurrentCalculationType(calculationType);
            if (ObjectUtils.newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, CalculationType.PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.getCurrentCalculationType())) {
                if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_IDS))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(calculationType.value, value));
                } else {
                    for (Map.Entry<BigInteger, Activity> activityEntry : kpiCalculationRelatedInfo.getActivityMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setActivityIds(ObjectUtils.newHashSet(activityEntry.getKey()));
                        Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                        subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(activityEntry.getValue().getName(), activityEntry.getValue().getActivityGeneralSettings().getBackgroundColor(), value));
                    }
                }
            }
        }
        return subClusteredBarValue;
    }


    private List<ClusteredBarChartKpiDataUnit> geTodoSubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        List<ClusteredBarChartKpiDataUnit> activitySubClusteredBarValue = new ArrayList<>();
        if (ObjectUtils.isNotNull(staffId)) {
            kpiCalculationRelatedInfo.updateTodoDtosByStaffId(staffId);
        }
        for (BigInteger activityId : kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().keySet()) {
            if (kpiCalculationRelatedInfo.getActivityMap().containsKey(activityId)) {
                Activity activity = kpiCalculationRelatedInfo.getActivityMap().get(activityId);
                List<TodoDTO> todoDTOS = new CopyOnWriteArrayList(kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().get(activityId)));
                ClusteredBarChartKpiDataUnit clusteredBarChartKpiDataUnit = new ClusteredBarChartKpiDataUnit(activity.getName(), activity.getActivityGeneralSettings().getBackgroundColor(), todoDTOS.size());
                subClusteredBarValue.addAll(getPQlOfTodo(activity, todoDTOS, kpiCalculationRelatedInfo));
                clusteredBarChartKpiDataUnit.setSubValues(subClusteredBarValue);
                activitySubClusteredBarValue.add(clusteredBarChartKpiDataUnit);
                subClusteredBarValue = ObjectUtils.newArrayList();
            }
        }
        return activitySubClusteredBarValue;
    }


    private List<ClusteredBarChartKpiDataUnit> getPQlOfTodo(Activity activity, List<TodoDTO> todoDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits = new ArrayList<>();
        PQLSettings pqlSettings = activity.getActivityRulesSettings().getPqlSettings();
        List<ApprovalCriteria> approvalCriterias = ObjectUtils.newArrayList(pqlSettings.getAppreciable(), pqlSettings.getAcceptable(), pqlSettings.getCritical());
        if (ObjectUtils.isNotNull(pqlSettings)) {
            for (ApprovalCriteria approvalCriteria : approvalCriterias) {
                getDataByPQLSetting(todoDTOS, clusteredBarChartKpiDataUnits, approvalCriteria, approvalCriteria.getColor(), approvalCriteria.getColorName(), kpiCalculationRelatedInfo);
            }
        }
        return clusteredBarChartKpiDataUnits;
    }

    private void getDataByPQLSetting(List<TodoDTO> todoDTOS, List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits, ApprovalCriteria approvalCriteria, String color, String range, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Short approvalTime = approvalCriteria.getApprovalTime();
        LocalDate localDate = null;
        long count = 0;
        if (ObjectUtils.isNotNull(approvalTime)) {
            for (TodoDTO todoDTO : todoDTOS) {
                if (APPROVE.equals(todoDTO.getStatus()) || DISAPPROVE.equals(todoDTO.getStatus()))
                    localDate = getApproveOrDisApproveDateFromTODO(localDate, todoDTO);
                if (ObjectUtils.isNotNull(localDate)) {
                    LocalDate endDate = add(DateUtils.asLocalDate(todoDTO.getRequestedOn()), approvalTime, kpiCalculationRelatedInfo);
                    Boolean isApproveExist = new DateTimeInterval(DateUtils.asLocalDate(todoDTO.getRequestedOn()), endDate).containsAndEqualsEndDate(DateUtils.asDate(localDate));
                    if (Boolean.TRUE.equals(isApproveExist)) {
                        count++;
                        todoDTOS.remove(todoDTO);
                    }
                }
            }
        }
        clusteredBarChartKpiDataUnits.add(new ClusteredBarChartKpiDataUnit(range, color, count));
    }

    public LocalDate add(LocalDate date, int workdays, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Set<DayOfWeek> dayOfWeeks = kpiCalculationRelatedInfo.getDaysOfWeeks();
        if (workdays < 1) {
            return date;
        }
        if (ObjectUtils.isNull(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.DAYS_OF_WEEK))) {
            return date.plusDays(workdays);
        }
        LocalDate requestedDate = date;
        LocalDate result = date;
        List<DayOfWeek> addDay = new ArrayList<>();
        if (ObjectUtils.isCollectionNotEmpty(dayOfWeeks)) {
            while (requestedDate.isBefore(date.plusDays(workdays))) {
                if (dayOfWeeks.contains(requestedDate.getDayOfWeek())) {
                    addDay.add(requestedDate.getDayOfWeek());
                }
                requestedDate = requestedDate.plusDays(1);
            }

        }
        return result.plusDays(addDay.size());
    }

    private LocalDate getApproveOrDisApproveDateFromTODO(LocalDate localDate, TodoDTO todoDTO) {
        switch (todoDTO.getStatus()) {
            case APPROVE:
                localDate = DateUtils.asLocalDate(DateUtils.asDate(todoDTO.getApprovedOn()));
                break;
            case DISAPPROVE:
                localDate = DateUtils.asLocalDate(DateUtils.asDate(todoDTO.getDisApproveOn()));
                break;
            default:
                break;
        }
        return localDate;
    }

    private List<ClusteredBarChartKpiDataUnit> getTimeTypeSubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (CalculationType calculationType : kpiCalculationRelatedInfo.getCalculationTypes()) {
            kpiCalculationRelatedInfo.setCurrentCalculationType(calculationType);
            if (ObjectUtils.newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, CalculationType.PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.getCurrentCalculationType())) {
                if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.TIME_TYPE))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(yAxisConfig.value, value));
                } else {
                    for (Map.Entry<BigInteger, TimeTypeDTO> timeTypeEntry : kpiCalculationRelatedInfo.getTimeTypeMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setTimeTypeIds(ObjectUtils.newHashSet(timeTypeEntry.getKey()));
                        Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                        subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(timeTypeEntry.getValue().getLabel(), timeTypeEntry.getValue().getBackgroundColor(), value));
                    }
                }
            }
        }
        return subClusteredBarValue;
    }

    private List<ClusteredBarChartKpiDataUnit> getPlannedTimeSubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (CalculationType calculationType : kpiCalculationRelatedInfo.getCalculationTypes()) {
            kpiCalculationRelatedInfo.setCurrentCalculationType(calculationType);
            if (ObjectUtils.newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, CalculationType.PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.getCurrentCalculationType())) {
                if (ObjectUtils.isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.PLANNED_TIME_TYPE))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(yAxisConfig.value, value));
                } else {
                    for (Map.Entry<BigInteger, PlannedTimeType> plannedtimeTypeEntry : kpiCalculationRelatedInfo.getPlannedTimeMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setPlannedTimeIds(ObjectUtils.newHashSet(plannedtimeTypeEntry.getKey()));
                        Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                        subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(plannedtimeTypeEntry.getValue().getName(), value));
                    }
                }
            }
        }
        return subClusteredBarValue;
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentPerInterval(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (ObjectUtils.newHashSet(CalculationType.PRESENCE_UNDER_STAFFING, CalculationType.PRESENCE_OVER_STAFFING).contains(kpiCalculationRelatedInfo.getCalculationType()) && DurationType.HOURS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType()) && asLocalDate(kpiCalculationRelatedInfo.getStartDate()).plusDays(1).equals(asLocalDate(kpiCalculationRelatedInfo.getEndDate()))) {
            return staffingLevelCalculationKPIService.getPresenceStaffingLevelCalculationPerHour(kpiCalculationRelatedInfo);
        }
        Map<T, E> staffTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : kpiCalculationRelatedInfo.getDateTimeIntervals()) {
            Double totalHours = 0d;
            if (!kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
                totalHours += getTotalByCalculationBased(null, dateTimeInterval, kpiCalculationRelatedInfo, kpiCalculationRelatedInfo.getYAxisConfigs().get(0));
            }
            List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = getClusteredBarChartDetails(null, dateTimeInterval, kpiCalculationRelatedInfo);
            String key = getKeyByStaffRepresentation(kpiCalculationRelatedInfo, dateTimeInterval);
            staffTotalHours.put((T) key, kpiCalculationRelatedInfo.getKpi().isMultiDimensional() ? (E) subClusteredBarValue : (E) totalHours);
        }
        return staffTotalHours;
    }

    private String getKeyByStaffRepresentation(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval) {
        String key;
        if (DurationType.HOURS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
            key = DateUtils.getLocalTimeByFormat(DateUtils.asLocalDateTime(dateTimeInterval.getStartDate()));
        } else if (DurationType.DAYS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
            key = DateUtils.getStartDateTimeintervalString(dateTimeInterval);
        } else {
            key = DateUtils.getDateTimeintervalString(dateTimeInterval);
        }
        return key;
    }

    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPIResponseDTO kpiResponseDTO = new KPIResponseDTO();
        if (DurationType.HOURS.equals(applicableKPI.getFrequencyType())) {
            Map<LocalDateTime, Double> staffKpiCostDataMap = getTotalHoursMap(filterBasedCriteria, organizationId, kpi, applicableKPI).entrySet().stream().collect(Collectors.toMap(k -> DateUtils.getLocaDateTimebyString(k.getKey().toString()), v -> v.getValue().doubleValue()));
            Map<LocalDateTime, Double> sortedStaffKpiCostDataMap = staffKpiCostDataMap.entrySet().stream()
                    .sorted(comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            kpiResponseDTO.setKpiValue(sortedStaffKpiCostDataMap);
        } else {
            Map<Long, Double> staffKpiDataMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, kpi, applicableKPI);
            kpiResponseDTO.setStaffKPIValue(staffKpiDataMap);
        }
        kpiResponseDTO.setKpiName(kpi.getTitle());
        kpiResponseDTO.setKpiId(kpi.getId());
        return kpiResponseDTO;
    }


    @Getter
    @Setter
    public class FilterShiftActivity {
        private List<ShiftWithActivityDTO> shifts;
        private List<ShiftActivityDTO> shiftActivityDTOS;
        private ShiftActivityCriteria shiftActivityCriteria;
        private boolean excludeBreak;

        public FilterShiftActivity(List<ShiftWithActivityDTO> shifts, ShiftActivityCriteria shiftActivityCriteria, boolean excludeBreak) {
            this.shifts = shifts;
            this.shiftActivityCriteria = shiftActivityCriteria;
            this.excludeBreak = excludeBreak;
            this.shiftActivityDTOS = new ArrayList<>();
        }

        public List<ShiftActivityDTO> getShiftActivityDTOS() {
            return shiftActivityDTOS;
        }

        public FilterShiftActivity invoke() {
            for (ShiftWithActivityDTO shift : shifts) {
                List<ShiftActivityDTO> shiftActivitys = shift.getActivities();
                if (excludeBreak) {
                    shiftActivitys = new CalculatePlannedHoursAndScheduledHours(timeBankCalculationService,new HashMap<>(),null).getShiftActivityByBreak(shift.getActivities(), shift.getBreakActivities());
                }
                shiftActivityDTOS.addAll(shiftActivitys.stream().filter(shiftActivityDTO -> isShiftActivityValid(shiftActivityDTO)).collect(Collectors.toList()));
            }
            return this;
        }

        private boolean isShiftActivityValid(ShiftActivityDTO shiftActivityDTO) {
            boolean validTimeType = ObjectUtils.isCollectionEmpty(this.shiftActivityCriteria.getTimeTypeIds()) || this.shiftActivityCriteria.getTimeTypeIds().contains(shiftActivityDTO.getActivity().getActivityBalanceSettings().getTimeTypeId());
            boolean validActivity = ObjectUtils.isCollectionEmpty(this.shiftActivityCriteria.getActivityIds()) || this.shiftActivityCriteria.getActivityIds().contains(shiftActivityDTO.getActivityId());
            boolean validReasonCode = ObjectUtils.isCollectionEmpty(this.shiftActivityCriteria.getReasonCodeIds()) || this.shiftActivityCriteria.getReasonCodeIds().contains(shiftActivityDTO.getAbsenceReasonCodeId());
            boolean validPlannedTime = ObjectUtils.isCollectionEmpty(this.shiftActivityCriteria.getPlannedTimeIds()) || CollectionUtils.containsAny(this.shiftActivityCriteria.getPlannedTimeIds(), shiftActivityDTO.getPlannedTimes().stream().map(PlannedTime::getPlannedTimeId).collect(Collectors.toSet()));
            boolean validStatus = ObjectUtils.isCollectionEmpty(this.shiftActivityCriteria.getShiftStatuses()) || CollectionUtils.containsAny(this.shiftActivityCriteria.getShiftStatuses(), shiftActivityDTO.getStatus());
            boolean validTeamActivity = ObjectUtils.isCollectionEmpty(this.shiftActivityCriteria.getTeamActivityIds()) || this.shiftActivityCriteria.getTeamActivityIds().contains(shiftActivityDTO.getActivityId());
            return validActivity && validTimeType && validPlannedTime && validStatus && validTeamActivity && validReasonCode;
        }
    }


    @Setter
    @Getter
    @Builder
    public static class ShiftActivityCriteria {
        private Set<BigInteger> activityIds;
        private Set<BigInteger> timeTypeIds;
        private Set<BigInteger> plannedTimeIds;
        private Set<BigInteger> teamActivityIds;
        private Set<Long> reasonCodeIds;
        private Set<ShiftStatus> shiftStatuses;
    }
}

