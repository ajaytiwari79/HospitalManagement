package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.PlannedTimeType;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
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
import com.kairos.service.shift.ShiftFilterService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.utils.counter.KPIUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.Interval;
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
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesByMapper;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOfListByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.CALCULATION_TYPE_NOT_VALID;
import static com.kairos.constants.ActivityMessagesConstants.EXCEPTION_INVALIDREQUEST;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.*;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.kpi.CalculationType.TOTAL_MINUTES;
import static com.kairos.enums.kpi.KPIRepresentation.REPRESENT_PER_STAFF;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;
import static com.kairos.utils.counter.KPIUtils.*;
import static java.util.Map.Entry.comparingByKey;


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
    private UnavailabilityCalculationKPIService unavailabilityCalculationKPIService;
    @Inject private ActivityMongoRepository activityMongoRepository;


    public Double getTotalByCalculationBased(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo,YAxisConfig yAxisConfig) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_BASED_ON))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        double total = 0;
        switch (yAxisConfig) {
            case PLANNED_TIME:
                total = getTotalByPlannedTime(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
                break;
            default:
                total = getActivityAndTimeTypeTotalByCalulationType(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
                break;
        }
        return total;
    }

    private double getTotalByPlannedTime(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = (CalculationType) copyPropertiesOfListByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE), CalculationType.class).get(0);
        if (!calculationType.equals(TOTAL_MINUTES)) {
            exceptionService.illegalArgumentException(CALCULATION_TYPE_NOT_VALID);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        Set<BigInteger> plannedTimeIds = shiftActivityCriteria.getPlannedTimeIds();
        int valuesSumInMinutes = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
        double total = getHoursByMinutes(valuesSumInMinutes);
        XAxisConfig calculationUnit = (XAxisConfig) copyPropertiesOfListByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_UNIT), XAxisConfig.class).get(0);
        if (PERCENTAGE.equals(calculationUnit)) {
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
            total = sumOfShifts > 0 ? (valuesSumInMinutes / sumOfShifts) * 100 : valuesSumInMinutes;
        } else if (COUNT.equals(calculationUnit)) {
            total = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).count();
        }
        return total;
    }

    public ShiftActivityCriteria getShiftActivityCriteria(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if(kpiCalculationRelatedInfo.getKpi().isMultiDimensional()){
            return kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria();
        }
        ShiftActivityCriteria currentShiftActivityCriteria = kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria();
        Set<BigInteger> timeTypeIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(TIME_TYPE) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TIME_TYPE)) : new HashSet<>();
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
            case PLANNED_HOURS_PAYOUT:
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
            case DELTA_TIMEBANK:
                return getTotalTimeBank(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            case UNAVAILABILITY:
                return unavailabilityCalculationKPIService.getUnavailabilityCalculationData(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            default:
                break;
        }
        return getTotalValueByByType(staffId, dateTimeInterval, kpiCalculationRelatedInfo, methodParam);
    }


    private double getTotalTimeBank(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        double totalTimeBank = 0;
        for (StaffKpiFilterDTO staffKpiFilterDTO : kpiCalculationRelatedInfo.getStaffKPIFilterDTO(staffId)) {
            for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                Collection<DailyTimeBankEntry> dailyTimeBankEntries = kpiCalculationRelatedInfo.getDailyTimeBankEntrysByEmploymentIdAndInterval(employmentWithCtaDetailsDTO.getId(), dateTimeInterval);
                int timeBankOfInterval = (int) timeBankCalculationService.calculateDeltaTimeBankForInterval(kpiCalculationRelatedInfo.getPlanningPeriodInterval(), new Interval(DateUtils.getLongFromLocalDate(dateTimeInterval.getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeInterval.getEndLocalDate())), employmentWithCtaDetailsDTO, new HashSet<>(), (List)dailyTimeBankEntries, false)[0];
                totalTimeBank += timeBankOfInterval;
            }
        }
        return getHoursByMinutes(totalTimeBank);
    }

    private double getTotalValueByByType(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, Function<ShiftActivityDTO, Integer> methodParam) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_UNIT))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        List<ShiftActivityDTO> shiftActivityDTOS = filterShiftActivity.getShiftActivityDTOS();
        int valuesSumInMinutes = shiftActivityDTOS.stream().mapToInt(methodParam::apply).sum();
        double total = getHoursByMinutes(valuesSumInMinutes);
        if (PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            int sumOfShifts = shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).mapToInt(methodParam::apply).sum();
            total = sumOfShifts > 0 ? (valuesSumInMinutes * 100 / sumOfShifts) : valuesSumInMinutes;
        } else if (COUNT.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            total = shiftActivityDTOS.size();
        }else if(VARIABLE_COST.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
            total = costCalculationKPIService.calculateTotalCostOfStaff(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
        }
        return total;
    }

    private List<CommonKpiDataUnit> getTotalHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi,ApplicableKPI applicableKPI) {
        double multiplicationFactor = 1;
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria, organizationId, applicableKPI,kpi);
        List<CommonKpiDataUnit> kpiDataUnits;
        if(kpi.isMultiDimensional()){
            Map<Object, List<ClusteredBarChartKpiDataUnit>> staffTotalHours = calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
            kpiDataUnits = getKpiDataUnits(staffTotalHours,applicableKPI,kpiCalculationRelatedInfo.staffKpiFilterDTOS);
        }else {
            Map<Object, Double> staffTotalHours = calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
            kpiDataUnits = getKpiDataUnits(multiplicationFactor, staffTotalHours, applicableKPI, kpiCalculationRelatedInfo.staffKpiFilterDTOS);
        }
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    private List<CommonKpiDataUnit> getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffTotalHours, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        for (Map.Entry<Object, Double> entry : staffTotalHours.entrySet()) {
            if (applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF)) {
                Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue() * multiplicationFactor))));
            } else {
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(getKpiDateFormatByIntervalUnit(entry.getKey().toString(),applicableKPI.getFrequencyType(),applicableKPI.getKpiRepresentation()),entry.getKey().toString(), Arrays.asList(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue() * multiplicationFactor))));
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
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue()));
            }
        }
        return kpiDataUnits;
    }


    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi,null);
        XAxisConfig XAxisConfig = (XAxisConfig) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(XAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi,applicableKPI);
        XAxisConfig XAxisConfig = (XAxisConfig) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(XAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }


    public Map<Long, Integer> getStaffAndWithTotalHour(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi,ApplicableKPI applicableKPI) {
        Map<Object, Double> totalHoursMap = getTotalHoursMap(filterBasedCriteria, organizationId,kpi, applicableKPI);
        return totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().intValue()));
    }

    private Map<Object, Double> getTotalHoursMap(Map<FilterType, List> filterBasedCriteria, Long organizationId,KPI kpi, ApplicableKPI applicableKPI) {
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria, organizationId, applicableKPI,kpi);
        return calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
    }


    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS,KPI kpi, ApplicableKPI applicableKPI) {
        Map<Long, Integer> staffAndTotalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, kpi,applicableKPI);
        return getFibonacciCalculation(staffAndTotalHoursMap, sortingOrder);
    }


    private <T,E> Map<T, E> calculateDataByKpiRepresentation(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<T, E> staffTotalHours;
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

    private <T, E> Map<T, E> getStaffTotalByRepresentTotalData(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Double totalHours = 0d;
        Map<T, E> staffTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : kpiCalculationRelatedInfo.getDateTimeIntervals()) {
            for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
                if(!kpiCalculationRelatedInfo.getKpi().isMultiDimensional()){
                    totalHours += getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getYAxisConfigs().get(0));
                }
            }
        }
        DateTimeInterval totalDataInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        T key = (T)getDateTimeintervalString(totalDataInterval);
        staffTotalHours.put(key, kpiCalculationRelatedInfo.getKpi().isMultiDimensional() ? (E)getClusteredBarChartDetails(null,totalDataInterval,kpiCalculationRelatedInfo) : (E)totalHours);
        return staffTotalHours;
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentPerStaff(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<T, E> staffTotalHours = new HashMap<>();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
            if(kpiCalculationRelatedInfo.getKpi().isMultiDimensional()){
                staffTotalHours.put((T)staffId, (E)getClusteredBarChartDetails(staffId,dateTimeInterval,kpiCalculationRelatedInfo));
            }else {
                staffTotalHours.put((T)staffId, (E)getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getYAxisConfigs().get(0)));
            }
        }
        return staffTotalHours;
    }

    private List<ClusteredBarChartKpiDataUnit> getClusteredBarChartDetails(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (YAxisConfig yAxisConfig : kpiCalculationRelatedInfo.getYAxisConfigs()) {
            kpiCalculationRelatedInfo.setCurrentCalculationType(null);
            switch (yAxisConfig){
                case ACTIVITY:
                    subClusteredBarValue.addAll(getActivitySubClusteredValue(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig));
                    break;
                case TIME_TYPE:
                    subClusteredBarValue.addAll(getTimeTypeSubClusteredValue(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig));
                    break;
                case PLANNED_TIME:
                    subClusteredBarValue.addAll(getPlannedTimeSubClusteredValue(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig));
                    break;
                case DELTA_TIMEBANK:
                case UNAVAILABILITY:
                case TOTAL_PLANNED_HOURS:
                    CalculationType currentCalculationType = copyPropertiesByMapper(yAxisConfig, CalculationType.class);
                    kpiCalculationRelatedInfo.setCurrentCalculationType(currentCalculationType);
                    Double value = getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(yAxisConfig.value,value));
                    break;
                    default:
                        break;

            }
        }
        return subClusteredBarValue;
    }

    private List<ClusteredBarChartKpiDataUnit> getActivitySubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo,YAxisConfig yAxisConfig){
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (Map.Entry<BigInteger, Activity> activityEntry : kpiCalculationRelatedInfo.getActivityMap().entrySet()) {
            kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setActivityIds(newHashSet(activityEntry.getKey()));
            Double value = getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(activityEntry.getValue().getName(), activityEntry.getValue().getGeneralActivityTab().getBackgroundColor(), value));
        }
        return subClusteredBarValue;
    }

    private List<ClusteredBarChartKpiDataUnit> getTimeTypeSubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo,YAxisConfig yAxisConfig){
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (Map.Entry<BigInteger, TimeTypeDTO> timeTypeEntry : kpiCalculationRelatedInfo.getTimeTypeMap().entrySet()) {
            kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setTimeTypeIds(newHashSet(timeTypeEntry.getKey()));
            Double value = getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(timeTypeEntry.getValue().getLabel(), timeTypeEntry.getValue().getBackgroundColor(), value));
        }
        return subClusteredBarValue;
    }



    private List<ClusteredBarChartKpiDataUnit> getPlannedTimeSubClusteredValue(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo,YAxisConfig yAxisConfig){
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (Map.Entry<BigInteger, PlannedTimeType> plannedtimeTypeEntry : kpiCalculationRelatedInfo.getPlannedTimeMap().entrySet()) {
            kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setPlannedTimeIds(newHashSet(plannedtimeTypeEntry.getKey()));
            Double value = getTotalByCalculationBased(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(plannedtimeTypeEntry.getValue().getName(), value));
        }
        return subClusteredBarValue;
    }

    private <T, E> Map<T, E> getStaffTotalByRepresentPerInterval(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Map<T, E> staffTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : kpiCalculationRelatedInfo.getDateTimeIntervals()) {
            Double totalHours = 0d;
            for (Long staffId : kpiCalculationRelatedInfo.getStaffIds()) {
                if(!kpiCalculationRelatedInfo.getKpi().isMultiDimensional()) {
                    totalHours += getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getYAxisConfigs().get(0));
                }
            }
            List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = getClusteredBarChartDetails(null,dateTimeInterval,kpiCalculationRelatedInfo);
            String key = getKeyByStaffRepresentation(kpiCalculationRelatedInfo, dateTimeInterval);
            staffTotalHours.put((T)key,kpiCalculationRelatedInfo.getKpi().isMultiDimensional() ? (E)subClusteredBarValue : (E)totalHours);
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
            Map<LocalDateTime, Double> staffKpiCostDataMap = getTotalHoursMap(filterBasedCriteria, organizationId, kpi,applicableKPI).entrySet().stream().collect(Collectors.toMap(k -> getLocaDateTimebyString(k.getKey().toString()), v -> v.getValue().doubleValue()));
            Map<LocalDateTime, Double> sortedStaffKpiCostDataMap = staffKpiCostDataMap.entrySet().stream()
                    .sorted(comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            kpiResponseDTO.setKpiValue(sortedStaffKpiCostDataMap);
        } else {
            Map<Long, Integer> totalHoursMap = getStaffAndWithTotalHour(filterBasedCriteria, organizationId, kpi,applicableKPI);
            Map<Long, Double> staffKpiDataMap = totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().doubleValue()));
            kpiResponseDTO.setStaffKPIValue(staffKpiDataMap);
        }
        kpiResponseDTO.setKpiName(kpi.getTitle());
        kpiResponseDTO.setKpiId(kpi.getId());
        return kpiResponseDTO;
    }

    @Getter
    @Setter
    class FilterShiftActivity {
        private List<ShiftWithActivityDTO> shifts;
        private List<ShiftActivityDTO> shiftActivityDTOS;
        private ShiftActivityCriteria shiftActivityCriteria;
        private boolean excludeBreak;

        public FilterShiftActivity(List<ShiftWithActivityDTO> shifts, ShiftActivityCriteria shiftActivityCriteria,boolean excludeBreak) {
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
                    shiftActivitys = timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().getShiftActivityByBreak(shift.getActivities(), shift.getBreakActivities());
                }
                shiftActivityDTOS.addAll(shiftActivitys.stream().filter(shiftActivityDTO -> isShiftActivityValid(shiftActivityDTO)).collect(Collectors.toList()));
            }
            return this;
        }

        private boolean isShiftActivityValid(ShiftActivityDTO shiftActivityDTO) {
            boolean validTimeType = isCollectionEmpty(this.shiftActivityCriteria.getTimeTypeIds()) || this.shiftActivityCriteria.getTimeTypeIds().contains(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId());
            boolean validActivity = isCollectionEmpty(this.shiftActivityCriteria.getActivityIds()) || this.shiftActivityCriteria.getActivityIds().contains(shiftActivityDTO.getActivityId());
            boolean validReasonCode = isCollectionEmpty(this.shiftActivityCriteria.getReasonCodeIds()) || this.shiftActivityCriteria.getReasonCodeIds().contains(shiftActivityDTO.getAbsenceReasonCodeId());
            boolean validPlannedTime = isCollectionEmpty(this.shiftActivityCriteria.getPlannedTimeIds()) || CollectionUtils.containsAny(this.shiftActivityCriteria.getPlannedTimeIds(), shiftActivityDTO.getPlannedTimes().stream().map(PlannedTime::getPlannedTimeId).collect(Collectors.toSet()));
            boolean validStatus = isCollectionEmpty(this.shiftActivityCriteria.getShiftStatuses()) || CollectionUtils.containsAny(this.shiftActivityCriteria.getShiftStatuses(), shiftActivityDTO.getStatus());
            boolean validTeamActivity = isCollectionEmpty(this.shiftActivityCriteria.getTeamActivityIds()) || this.shiftActivityCriteria.getTeamActivityIds().contains(shiftActivityDTO.getActivityId());
            return validActivity && validTimeType && validPlannedTime && validStatus && validTeamActivity && validReasonCode;
        }
    }

    @Getter
    @Setter
     class KPICalculationRelatedInfo {
        private Map<FilterType, List> filterBasedCriteria;
        private List<ShiftWithActivityDTO> shifts;
        private List<Long> staffIds;
        private List<DateTimeInterval> dateTimeIntervals;
        private List<StaffKpiFilterDTO> staffKpiFilterDTOS;
        private Long unitId;
        private ApplicableKPI applicableKPI;
        private KPI kpi;
        private Map<DateTimeInterval, List<ShiftWithActivityDTO>> intervalShiftsMap;
        private Map<Long, StaffKpiFilterDTO> staffIdAndStaffKpiFilterMap;
        private Map<Long, List<ShiftWithActivityDTO>> staffIdAndShiftsMap;
        private Set<Long> employmentIds;
        private Date startDate;
        private Date endDate;
        private Set<DayOfWeek> daysOfWeeks;
        private Map<Long, Collection<DailyTimeBankEntry>> employmentIdAndDailyTimebankEntryMap;
        private Map<Long, Collection<DailyTimeBankEntry>> staffIdAndDailyTimebankEntryMap;
        private Collection<DailyTimeBankEntry> dailyTimeBankEntries;
        private List<Long> employmentTypeIds;
        private Map<BigInteger, Activity> activityMap = new HashMap<>();
        private Map<BigInteger, TimeTypeDTO> timeTypeMap = new HashMap<>();
        private Map<BigInteger, PlannedTimeType> plannedTimeMap = new HashMap<>();
        private ShiftActivityCriteria currentShiftActivityCriteria;
        private List<YAxisConfig> yAxisConfigs;
        private List<XAxisConfig> xAxisConfigs;
        private CalculationType calculationType;
        private CalculationType currentCalculationType;
        private DateTimeInterval planningPeriodInterval;

        public KPICalculationRelatedInfo(Map<FilterType, List> filterBasedCriteria, Long unitId, ApplicableKPI applicableKPI, KPI kpi) {
            this.filterBasedCriteria = filterBasedCriteria;
            this.unitId = unitId;
            this.applicableKPI = applicableKPI;
            this.kpi = kpi;
            yAxisConfigs = copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_BASED_ON), YAxisConfig.class);
            xAxisConfigs = copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class);
            loadKpiCalculationRelatedInfo(filterBasedCriteria, unitId, applicableKPI);
            updateIntervalShiftsMap(applicableKPI);
            staffIdAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, v -> v));
            updateStaffAndShiftMap();
            employmentIds = staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toSet());
            startDate = dateTimeIntervals.get(0).getStartDate();
            endDate = dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate();
            getDailyTimeBankEntryByDate();
            updateActivityAndTimeTypeAndPlannedTimeMap();
            planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
            calculationType = (CalculationType) copyPropertiesOfListByMapper(filterBasedCriteria.get(CALCULATION_TYPE), CalculationType.class).get(0);
        }

        public CalculationType getCalculationType(){
            return isNotNull(currentCalculationType) ? currentCalculationType : calculationType;
        }

        private void updateStaffAndShiftMap() {
            staffIdAndShiftsMap = shifts.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
            staffKpiFilterDTOS.forEach(staffKpiFilterDTO -> {
                if(!staffIdAndShiftsMap.containsKey(staffKpiFilterDTO.getId())){
                    staffIdAndShiftsMap.put(staffKpiFilterDTO.getId(),new ArrayList<>());
                }
            });
        }

        private void updateActivityAndTimeTypeAndPlannedTimeMap() {
            for (YAxisConfig yAxisConfig : yAxisConfigs) {
                switch (yAxisConfig) {
                    case ACTIVITY:
                        List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(filterBasedCriteria.containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ACTIVITY_IDS)) : new HashSet<>());
                        activityMap = activities.stream().collect(Collectors.toMap(Activity::getId, v -> v));
                        break;
                    case TIME_TYPE:
                        timeTypeMap = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), filterBasedCriteria.containsKey(TIME_TYPE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(TIME_TYPE)) : new ArrayList<>());
                        break;
                    case PLANNED_TIME:
                        Collection<PlannedTimeType> plannedTimeTypes = plannedTimeTypeService.getAllPlannedTimeByIds(filterBasedCriteria.containsKey(PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(PLANNED_TIME_TYPE)) : new ArrayList<>());
                        plannedTimeMap = plannedTimeTypes.stream().collect(Collectors.toMap(PlannedTimeType::getId, v->v));
                        break;
                    default:break;
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
            Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
            staffIds = (List<Long>) filterCriteria[0];
            List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
            List<Long> unitIds = (List<Long>) filterCriteria[2];
            employmentTypeIds = (List<Long>) filterCriteria[3];
            Object[] kpiData = counterHelperService.getKPIdata(applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId);
            List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(organizationId);
            dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
            staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
            staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
            List<Integer> dayOfWeeksNo = new ArrayList<>();
            daysOfWeeks = (Set<DayOfWeek>) filterCriteria[4];
            daysOfWeeks.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
            shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
            StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, timeSlotDTOS, organizationId);
            shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO);
            currentShiftActivityCriteria = getDefaultShiftActivityCriteria();
        }

        private ShiftActivityCriteria getDefaultShiftActivityCriteria() {
            Set<BigInteger> teamActivityIds = new HashSet<>();
            if (filterBasedCriteria.containsKey(TEAM) && isCollectionNotEmpty(filterBasedCriteria.get(TEAM))) {
                Set<String> teamIds = getStringByList(new HashSet<>(filterBasedCriteria.get(TEAM)));
                ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(UserContext.getUserDetails().getLastSelectedOrganizationId(), teamIds));
                teamActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
            }
            Set<Long> reasonCodeIds = filterBasedCriteria.containsKey(REASON_CODE) ? KPIUtils.getLongValueSet(filterBasedCriteria.get(REASON_CODE)) : new HashSet<>();
            Set<ShiftStatus> shiftStatuses = filterBasedCriteria.containsKey(ACTIVITY_STATUS) ? (Set<ShiftStatus>)filterBasedCriteria.get(ACTIVITY_STATUS).stream().map(o -> ShiftStatus.valueOf(o.toString())).collect(Collectors.toSet()) : new HashSet<>();
            return ShiftActivityCriteria.builder().reasonCodeIds(reasonCodeIds).shiftStatuses(shiftStatuses).teamActivityIds(teamActivityIds).build();
        }

        private StaffFilterDTO getStaffFilterDto(Map<FilterType, List> filterBasedCriteria, List<TimeSlotDTO> timeSlotDTOS, Long organizationId) {
          StaffFilterDTO staffFilterDTO = new StaffFilterDTO();
            List<FilterSelectionDTO> filterData = new ArrayList<>();
            filterBasedCriteria.entrySet().forEach(filterTypeListEntry -> {
                getTimeSoltFilter(filterTypeListEntry, timeSlotDTOS, filterData);
                if (!newHashSet(PHASE, TEAM).contains(filterTypeListEntry.getKey())) {
                    filterData.add(new FilterSelectionDTO(filterTypeListEntry.getKey(), new HashSet<String>(filterTypeListEntry.getValue())));
                }
            });
            if (filterBasedCriteria.containsKey(PHASE)) {
                List<PhaseDTO> phases = phaseService.getPhasesByUnit(organizationId);
                Set<PhaseDefaultName> phaseDefaultNames = (Set<PhaseDefaultName>) filterBasedCriteria.get(FilterType.PHASE).stream().map(value -> PhaseDefaultName.valueOf(value.toString())).collect(Collectors.toSet());
                Set<String> phaseIds = phases.stream().filter(phaseDTO -> phaseDefaultNames.contains(phaseDTO.getPhaseEnum())).map(phaseDTO -> phaseDTO.getId().toString()).collect(Collectors.toSet());
                filterData.add(new FilterSelectionDTO(PHASE, phaseIds));
            }
            if (filterBasedCriteria.containsKey(TEAM)) {
                filterData.add(new FilterSelectionDTO(TEAM, new HashSet<>((List<String>) filterBasedCriteria.get(TEAM))));
            }
            staffFilterDTO.setFiltersData(filterData);
            return staffFilterDTO;
        }

        public void getTimeSoltFilter(Map.Entry<FilterType, List> filterTypeListEntry, List<TimeSlotDTO> timeSlotDTOS, List<FilterSelectionDTO> filterData) {
            if (filterTypeListEntry.getKey().equals(TIME_SLOT)) {
                Set<String> timeSlotName = new HashSet<>();
                for (Object timeSlotId : filterTypeListEntry.getValue()) {
                    for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                        if (timeSlotDTO.getId().equals(((Integer) timeSlotId).longValue())) {
                            timeSlotName.add(timeSlotDTO.getName());
                        }
                    }
                }
                filterData.add(new FilterSelectionDTO(filterTypeListEntry.getKey(), timeSlotName));
            }
        }

        public List<ShiftWithActivityDTO> getShiftsByStaffIdAndInterval(Long staffId, DateTimeInterval dateTimeInterval) {
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = isNull(staffId) ? shifts :staffIdAndShiftsMap.getOrDefault(staffId, new ArrayList<>());
            if (isNotNull(dateTimeInterval)) {
                shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate())) : dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList());
            }
            return shiftWithActivityDTOS;
        }

        private void getDailyTimeBankEntryByDate() {
            dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByIdsAndBetweenDates(employmentIds, startDate, endDate);
            if (isCollectionNotEmpty(daysOfWeeks)) {
                dailyTimeBankEntries = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> daysOfWeeks.contains(dailyTimeBankEntry.getDate().getDayOfWeek())).collect(Collectors.toList());
            }
            employmentIdAndDailyTimebankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toCollection(ArrayList::new)));
            staffIdAndDailyTimebankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getStaffId, Collectors.toCollection(ArrayList::new)));
            for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
                for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                    if(!employmentIdAndDailyTimebankEntryMap.containsKey(employmentWithCtaDetailsDTO.getId())){
                        employmentIdAndDailyTimebankEntryMap.put(employmentWithCtaDetailsDTO.getId(),new ArrayList<>());
                    }
                }
                if(!staffIdAndDailyTimebankEntryMap.containsKey(staffKpiFilterDTO.getId())){
                    staffIdAndDailyTimebankEntryMap.put(staffKpiFilterDTO.getId(),new ArrayList<>());
                }
            }
        }

        public Collection<DailyTimeBankEntry> getDailyTimeBankEntrysByEmploymentIdAndInterval(Long employmentId, DateTimeInterval dateTimeInterval) {
            Collection<DailyTimeBankEntry> filteredDailyTimeBankEntries = employmentIdAndDailyTimebankEntryMap.getOrDefault(employmentId, dailyTimeBankEntries);
            if (isNotNull(dateTimeInterval)) {
                filteredDailyTimeBankEntries = filteredDailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dateTimeInterval.contains(dailyTimeBankEntry.getDate())).collect(Collectors.toList());
            }
            return filteredDailyTimeBankEntries;
        }

        public List<StaffKpiFilterDTO> getStaffKPIFilterDTO(Long staffId) {
            List<StaffKpiFilterDTO> filteredStaffKpiFilterDTOS = this.staffKpiFilterDTOS;
            if (isNotNull(staffId)) {
                filteredStaffKpiFilterDTOS = filteredStaffKpiFilterDTOS.stream().filter(staffKpiFilterDTO -> staffKpiFilterDTO.getId().equals(staffId)).collect(Collectors.toList());
            }
            return filteredStaffKpiFilterDTOS;
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
