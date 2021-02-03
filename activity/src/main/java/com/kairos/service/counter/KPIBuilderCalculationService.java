package com.kairos.service.counter;

import com.kairos.commons.service.audit_logging.AuditLoggingService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.activity.activity_tabs.ApprovalCriteria;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.*;
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
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.PlannedTimeType;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.PQLSettings;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftBreakService;
import com.kairos.service.shift.ShiftFilterService;
import com.kairos.service.time_bank.CalculatePlannedHoursAndScheduledHours;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.todo.TodoService;
import com.kairos.utils.counter.KPIUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.CALCULATION_TYPE_NOT_VALID;
import static com.kairos.constants.ActivityMessagesConstants.EXCEPTION_INVALIDREQUEST;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.*;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.kpi.CalculationType.*;
import static com.kairos.enums.kpi.KPIRepresentation.INDIVIDUAL_STAFF;
import static com.kairos.enums.kpi.KPIRepresentation.REPRESENT_PER_STAFF;
import static com.kairos.enums.wta.WTATemplateType.PROTECTED_DAYS_OFF;
import static com.kairos.enums.wta.WTATemplateType.*;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;
import static com.kairos.utils.counter.KPIUtils.getBigIntegerSet;
import static com.kairos.utils.counter.KPIUtils.*;
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
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_BASED_ON))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        double total = 0;
        if (YAxisConfig.PLANNED_TIME.equals(yAxisConfig)) {
            total = getTotalByPlannedTime(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        } else {
            total = getActivityAndTimeTypeTotalByCalulationType(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        }
        return getValueWithDecimalFormat(total);
    }


    private double getNumberOfBreakInterrupt(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval,true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        long interruptShift = filterShiftActivity.shifts.stream().filter(k -> k.getBreakActivities().stream().anyMatch(ShiftActivityDTO::isBreakInterrupt)).count();
        if (PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return isCollectionNotEmpty(filterShiftActivity.shifts) ? getValueWithDecimalFormat((interruptShift * 100.0d) / filterShiftActivity.shifts.size()) : 0;
        } else
            return interruptShift;
    }


    private double getTotalByPlannedTime(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = ((List<CalculationType>) copyCollectionPropertiesByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE), CalculationType.class)).get(0);
        if (!calculationType.equals(TOTAL_MINUTES)) {
            exceptionService.illegalArgumentException(CALCULATION_TYPE_NOT_VALID);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        Set<BigInteger> plannedTimeIds = shiftActivityCriteria.getPlannedTimeIds();
        int valuesSumInMinutes = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
        double total = getHoursByMinutes(valuesSumInMinutes);
        XAxisConfig calculationUnit = (XAxisConfig) ((List) copyCollectionPropertiesByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_UNIT), XAxisConfig.class)).get(0);
        if (PERCENTAGE.equals(calculationUnit)) {
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
            total = sumOfShifts > 0 ? getValueWithDecimalFormat((valuesSumInMinutes * 100.0) / sumOfShifts) : valuesSumInMinutes;

        } else if (COUNT.equals(calculationUnit)) {
            total = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).count();
        }
        return total;
    }

    public ShiftActivityCriteria getShiftActivityCriteria(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
            return kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria();
        }
        ShiftActivityCriteria currentShiftActivityCriteria = kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria();
        Set<BigInteger> timeTypeIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(TIME_TYPE) ? kpiCalculationRelatedInfo.getTimeTypeMap().keySet() : new HashSet<>();
        Set<BigInteger> plannedTimeIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(PLANNED_TIME_TYPE)) : new HashSet<>();
        Set<BigInteger> activityIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) : new HashSet<>();
        if (kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ABSENCE_ACTIVITY))) {
            activityIds.addAll(KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ABSENCE_ACTIVITY)));
        }
        return ShiftActivityCriteria.builder().shiftStatuses(currentShiftActivityCriteria.shiftStatuses).activityIds(activityIds).teamActivityIds(currentShiftActivityCriteria.teamActivityIds).plannedTimeIds(plannedTimeIds).reasonCodeIds(currentShiftActivityCriteria.reasonCodeIds).timeTypeIds(timeTypeIds).build();
    }

    private double getActivityAndTimeTypeTotalByCalulationType(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        Function<ShiftActivityDTO, Integer> methodParam = ShiftActivityDTO::getScheduledMinutes;
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
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_UNIT))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        List<ShiftActivityDTO> shiftActivityDTOS = filterShiftActivity.getShiftActivityDTOS();
        int valuesSumInMinutes = shiftActivityDTOS.stream().mapToInt(methodParam::apply).sum();
        double total = getHoursByMinutes(valuesSumInMinutes);
        if (PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, false);
            shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
            filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
            shiftWithActivityDTOS = filterShiftActivity.getShifts();
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).mapToInt(methodParam::apply).sum();
            total = sumOfShifts > 0 ? (getValueWithDecimalFormat(valuesSumInMinutes * 100.0d)) / sumOfShifts : valuesSumInMinutes;
        } else if (COUNT.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            total = shiftActivityDTOS.size();
        } else if (VARIABLE_COST.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            total = costCalculationKPIService.calculateTotalCostOfStaff(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        } else if (AVERAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            Set<LocalDate> localDates = shiftActivityDTOS.stream().map(shiftActivityDTO -> asLocalDate(shiftActivityDTO.getStartDate())).collect(Collectors.toSet());
            if (DurationType.WEEKS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
                total = getValueWithDecimalFormat(((double) localDates.size() / kpiCalculationRelatedInfo.getApplicableKPI().getValue()));
            }
            if (DurationType.MONTHS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
                total = getValueWithDecimalFormat(((double) localDates.size() / (kpiCalculationRelatedInfo.getApplicableKPI().getValue() * 4)));
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
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    private List<CommonKpiDataUnit> getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffTotalHours, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        for (Map.Entry<Object, Double> entry : staffTotalHours.entrySet()) {
            if (REPRESENT_PER_STAFF.equals(applicableKPI.getKpiRepresentation()) || INDIVIDUAL_STAFF.equals(applicableKPI.getKpiRepresentation())) {
                Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue() * multiplicationFactor))));
            } else {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(getKpiDateFormatByIntervalUnit(entry.getKey().toString(), applicableKPI.getFrequencyType(), applicableKPI.getKpiRepresentation()), entry.getKey().toString(), Arrays.asList(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue() * multiplicationFactor))));
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
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(getKpiDateFormatByIntervalUnit(entry.getKey().toString(), applicableKPI.getFrequencyType(), applicableKPI.getKpiRepresentation()), entry.getKey().toString(), entry.getValue()));
            }
        }
        return kpiDataUnits;
    }


    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi, null);
        XAxisConfig xAxisConfig = (XAxisConfig) ((List) copyCollectionPropertiesByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class)).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), xAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(xAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi, applicableKPI);
        XAxisConfig xAxisConfig = (XAxisConfig) ((List) copyCollectionPropertiesByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class)).get(0);
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
        return getFibonacciCalculation(staffAndTotalHoursMap, sortingOrder);
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
        return verifyKPIResponseData(staffTotalHours) ? staffTotalHours : new HashMap<>();
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentTotalData(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Double totalHours = 0d;
        Map<T, E> staffTotalHours = new HashMap<>();
        DateTimeInterval totalDataInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        if (!kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
            totalHours += getTotalByCalculationBased(null, totalDataInterval, kpiCalculationRelatedInfo, kpiCalculationRelatedInfo.getYAxisConfigs().get(0));
        }
        T key = (T) getDateTimeintervalString(totalDataInterval);
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
                    Double count = counterServiceMapping.getKpiServiceMap(copyPropertiesByMapper(yAxisConfig, CalculationType.class)).get(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
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
            if (newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.getCurrentCalculationType())) {
                if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(calculationType.value, value));
                } else {
                    for (Map.Entry<BigInteger, Activity> activityEntry : kpiCalculationRelatedInfo.getActivityMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setActivityIds(newHashSet(activityEntry.getKey()));
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
        if (isNotNull(staffId)) {
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
                subClusteredBarValue = newArrayList();
            }
        }
        return activitySubClusteredBarValue;
    }


    private List<ClusteredBarChartKpiDataUnit> getPQlOfTodo(Activity activity, List<TodoDTO> todoDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits = new ArrayList<>();
        PQLSettings pqlSettings = activity.getActivityRulesSettings().getPqlSettings();
        List<ApprovalCriteria> approvalCriterias = newArrayList(pqlSettings.getAppreciable(), pqlSettings.getAcceptable(), pqlSettings.getCritical());
        if (isNotNull(pqlSettings)) {
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
        if (isNotNull(approvalTime)) {
            for (TodoDTO todoDTO : todoDTOS) {
                if (TodoStatus.APPROVE.equals(todoDTO.getStatus()) || TodoStatus.DISAPPROVE.equals(todoDTO.getStatus()))
                    localDate = getApproveOrDisApproveDateFromTODO(localDate, todoDTO);
                if (isNotNull(localDate)) {
                    LocalDate endDate = add(asLocalDate(todoDTO.getRequestedOn()), approvalTime, kpiCalculationRelatedInfo);
                    Boolean isApproveExist = new DateTimeInterval(asLocalDate(todoDTO.getRequestedOn()), endDate).containsAndEqualsEndDate(asDate(localDate));
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
        if (isNull(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(DAYS_OF_WEEK))) {
            return date.plusDays(workdays);
        }
        LocalDate requestedDate = date;
        LocalDate result = date;
        List<DayOfWeek> addDay = new ArrayList<>();
        if (isCollectionNotEmpty(dayOfWeeks)) {
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
                localDate = asLocalDate(asDate(todoDTO.getApprovedOn()));
                break;
            case DISAPPROVE:
                localDate = asLocalDate(asDate(todoDTO.getDisApproveOn()));
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
            if (newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.getCurrentCalculationType())) {
                if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TIME_TYPE))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(yAxisConfig.value, value));
                } else {
                    for (Map.Entry<BigInteger, TimeTypeDTO> timeTypeEntry : kpiCalculationRelatedInfo.getTimeTypeMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setTimeTypeIds(newHashSet(timeTypeEntry.getKey()));
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
            if (newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.getCurrentCalculationType())) {
                if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(PLANNED_TIME_TYPE))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(yAxisConfig.value, value));
                } else {
                    for (Map.Entry<BigInteger, PlannedTimeType> plannedtimeTypeEntry : kpiCalculationRelatedInfo.getPlannedTimeMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setPlannedTimeIds(newHashSet(plannedtimeTypeEntry.getKey()));
                        Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                        subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(plannedtimeTypeEntry.getValue().getName(), value));
                    }
                }
            }
        }
        return subClusteredBarValue;
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentPerInterval(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (newHashSet(PRESENCE_UNDER_STAFFING, PRESENCE_OVER_STAFFING).contains(kpiCalculationRelatedInfo.getCalculationType()) && DurationType.HOURS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType()) && asLocalDate(kpiCalculationRelatedInfo.getStartDate()).plusDays(1).equals(asLocalDate(kpiCalculationRelatedInfo.getEndDate()))) {
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
            key = getLocalTimeByFormat(asLocalDateTime(dateTimeInterval.getStartDate()));
        } else if (DurationType.DAYS.equals(kpiCalculationRelatedInfo.getApplicableKPI().getFrequencyType())) {
            key = getStartDateTimeintervalString(dateTimeInterval);
        } else {
            key = getDateTimeintervalString(dateTimeInterval);
        }
        return key;
    }

    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPIResponseDTO kpiResponseDTO = new KPIResponseDTO();
        if (DurationType.HOURS.equals(applicableKPI.getFrequencyType())) {
            Map<LocalDateTime, Double> staffKpiCostDataMap = getTotalHoursMap(filterBasedCriteria, organizationId, kpi, applicableKPI).entrySet().stream().collect(Collectors.toMap(k -> getLocaDateTimebyString(k.getKey().toString()), v -> v.getValue().doubleValue()));
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
            boolean validTimeType = isCollectionEmpty(this.shiftActivityCriteria.getTimeTypeIds()) || this.shiftActivityCriteria.getTimeTypeIds().contains(shiftActivityDTO.getActivity().getActivityBalanceSettings().getTimeTypeId());
            boolean validActivity = isCollectionEmpty(this.shiftActivityCriteria.getActivityIds()) || this.shiftActivityCriteria.getActivityIds().contains(shiftActivityDTO.getActivityId());
            boolean validReasonCode = isCollectionEmpty(this.shiftActivityCriteria.getReasonCodeIds()) || this.shiftActivityCriteria.getReasonCodeIds().contains(shiftActivityDTO.getAbsenceReasonCodeId());
            boolean validPlannedTime = isCollectionEmpty(this.shiftActivityCriteria.getPlannedTimeIds()) || CollectionUtils.containsAny(this.shiftActivityCriteria.getPlannedTimeIds(), shiftActivityDTO.getPlannedTimes().stream().map(PlannedTime::getPlannedTimeId).collect(Collectors.toSet()));
            boolean validStatus = isCollectionEmpty(this.shiftActivityCriteria.getShiftStatuses()) || CollectionUtils.containsAny(this.shiftActivityCriteria.getShiftStatuses(), shiftActivityDTO.getStatus());
            boolean validTeamActivity = isCollectionEmpty(this.shiftActivityCriteria.getTeamActivityIds()) || this.shiftActivityCriteria.getTeamActivityIds().contains(shiftActivityDTO.getActivityId());
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

