package com.kairos.service.counter;

import com.kairos.commons.service.audit_logging.AuditLoggingService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.activity.activity_tabs.ApprovalCriteria;
import com.kairos.dto.activity.activity.activity_tabs.PQLSettings;
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
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftFilterService;
import com.kairos.service.shift.ShiftValidatorService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.todo.TodoService;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.utils.counter.KPIUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
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
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOrCloneByMapper;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOrCloneCollectionByMapper;
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
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private TodoService todoService;
    @Inject
    private WorkTimeAgreementService workTimeAgreementService;
    @Inject
    private WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private AbsencePlanningKPIService absencePlanningKPIService;
    @Inject
    private StaffingLevelCalculationKPIService staffingLevelCalculationKPIService;
    @Inject
    private SkillKPIService skillKPIService;
    @Inject
    private ActivityService activityService;
    @Inject
    private AuditLoggingService auditLoggingService;
    @Inject private AbsencePlanningKPIService absencePlanningKPIService;
    @Inject private StaffingLevelCalculationKPIService staffingLevelCalculationKPIService;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject private ShiftEscalationService shiftEscalationService;
    @Inject private KPICalculationHelperService kpiCalculationHelperService;
    @Inject private TimeBankOffKPI timeBankOffKPI;
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

    private double getTotalByPlannedTime(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE))) {
            exceptionService.dataNotFoundException(EXCEPTION_INVALIDREQUEST);
        }
        CalculationType calculationType = ((List<CalculationType>) copyPropertiesOrCloneCollectionByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_TYPE), CalculationType.class)).get(0);
        if (!calculationType.equals(TOTAL_MINUTES)) {
            exceptionService.illegalArgumentException(CALCULATION_TYPE_NOT_VALID);
        }
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        ShiftActivityCriteria shiftActivityCriteria = getShiftActivityCriteria(kpiCalculationRelatedInfo);
        FilterShiftActivity filterShiftActivity = new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        Set<BigInteger> plannedTimeIds = shiftActivityCriteria.getPlannedTimeIds();
        int valuesSumInMinutes = filterShiftActivity.getShiftActivityDTOS().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream()).filter(plannedTime -> plannedTimeIds.contains(plannedTime.getPlannedTimeId())).mapToInt(plannedTime -> (int) plannedTime.getInterval().getMinutes()).sum();
        double total = getHoursByMinutes(valuesSumInMinutes);
        XAxisConfig calculationUnit = (XAxisConfig) ((List) copyPropertiesOrCloneCollectionByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_UNIT), XAxisConfig.class)).get(0);
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
            case DELTA_TIMEBANK:
                return timeBankService.getTotalTimeBankOrContractual(staffId, dateTimeInterval, kpiCalculationRelatedInfo, false);
            case ACTUAL_TIMEBANK:
                return timeBankService.getActualTimeBank(staffId, kpiCalculationRelatedInfo);
            case STAFFING_LEVEL_CAPACITY:
                return timeBankService.getTotalTimeBankOrContractual(staffId, dateTimeInterval, kpiCalculationRelatedInfo, true);
            case UNAVAILABILITY:
                return unavailabilityCalculationKPIService.getUnavailabilityCalculationData(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            case PROTECTED_DAYS_OFF:
            case CARE_DAYS:
            case SENIORDAYS:
            case TOTAL_ABSENCE_DAYS:
            case CHILD_CARE_DAYS:
                return workTimeAgreementBalancesCalculationService.getLeaveCount(staffId, dateTimeInterval, kpiCalculationRelatedInfo, null);
            case BREAK_INTERRUPT:
                return shiftBreakService.getNumberOfBreakInterrupt(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            case ESCALATED_SHIFTS:
            case ESCALATION_RESOLVED_SHIFTS:
                return shiftEscalationService.getEscalatedShiftsOrResolvedShifts(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            case STAFF_AGE:
                return getStaffAgeData(staffId, kpiCalculationRelatedInfo);
            case SUM_OF_CHILDREN:
                return getChildrenCount(staffId, kpiCalculationRelatedInfo);
            case WORKED_ON_PUBLIC_HOLIDAY:
                return kpiCalculationHelperService.getWorkedOnPublicHolidayCount(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            case PRESENCE_OVER_STAFFING:
            case PRESENCE_UNDER_STAFFING:
            case ABSENCE_OVER_STAFFING:
            case ABSENCE_UNDER_STAFFING:
                return staffingLevelCalculationKPIService.getStaffingLevelCalculationData(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
            case CARE_BUBBLE:
                return kpiCalculationHelperService.calculateCareBubble(kpiCalculationRelatedInfo, dateTimeInterval, staffId);
            case TOTAL_WEEKLY_HOURS:
                return weeklyEmploymentHoursKPIService.getWeeklyHoursOfEmployment(staffId, kpiCalculationRelatedInfo);
            case STAFF_SKILLS_COUNT:
                return skillKPIService.getCountOfSkillOfStaffIdOnSelectedDate(staffId, asLocalDate(kpiCalculationRelatedInfo.getStartDate()), asLocalDate(kpiCalculationRelatedInfo.getEndDate()), kpiCalculationRelatedInfo);
            case PAY_LEVEL_GRADE:
                return payLevelKPIService.getPayLevelGradeOfMainEmploymentOfStaff(staffId, kpiCalculationRelatedInfo);
            case TODO_STATUS:
                return  timeBankOffKPI.getCountAndHoursAndPercentageOfTODOS(staffId,kpiCalculationRelatedInfo);
            case ABSENCE_REQUEST:
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
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria, organizationId, applicableKPI, kpi);
        List<CommonKpiDataUnit> kpiDataUnits;
        if (kpi.isMultiDimensional()) {
            Map<Object, List<ClusteredBarChartKpiDataUnit>> staffTotalHours = calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
            kpiDataUnits = getKpiDataUnits(staffTotalHours, applicableKPI, kpiCalculationRelatedInfo.staffKpiFilterDTOS);
        } else {
            Map<Object, Double> staffTotalHours = calculateDataByKpiRepresentation(kpiCalculationRelatedInfo);
            kpiDataUnits = getKpiDataUnits(multiplicationFactor, staffTotalHours, applicableKPI, kpiCalculationRelatedInfo.staffKpiFilterDTOS);
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
        XAxisConfig xAxisConfig = (XAxisConfig) ((List) copyPropertiesOrCloneCollectionByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class)).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), xAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(xAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTotalHoursKpiData(filterBasedCriteria, organizationId, kpi, applicableKPI);
        XAxisConfig xAxisConfig = (XAxisConfig) ((List) copyPropertiesOrCloneCollectionByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class)).get(0);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), xAxisConfig, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(xAxisConfig.getDisplayValue(), AppConstants.VALUE_FIELD));
    }


    public Map<Long, Double> getStaffAndWithTotalHour(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        Map<Object, Double> totalHoursMap = getTotalHoursMap(filterBasedCriteria, organizationId, kpi, applicableKPI);
        return totalHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue()));
    }

    private Map<Object, Double> getTotalHoursMap(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo(filterBasedCriteria, organizationId, applicableKPI, kpi);
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
                    List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits = absencePlanningKPIService.getActivityStatusCount(todoDTOList, kpiCalculationRelatedInfo.xAxisConfigs.get(0));
                    subClusteredBarValue.addAll(clusteredBarChartKpiDataUnits);
                    break;
                default:
                    Double count = counterServiceMapping.getKpiServiceMap(copyPropertiesOrCloneByMapper(yAxisConfig, CalculationType.class)).get(staffId,dateTimeInterval,kpiCalculationRelatedInfo,yAxisConfig);
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
            if (newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.currentCalculationType)) {
                if (isCollectionEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS))) {
                    Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                    subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(calculationType.value, value));
                } else {
                    for (Map.Entry<BigInteger, Activity> activityEntry : kpiCalculationRelatedInfo.getActivityMap().entrySet()) {
                        kpiCalculationRelatedInfo.getCurrentShiftActivityCriteria().setActivityIds(newHashSet(activityEntry.getKey()));
                        Double value = getTotalByCalculationBased(staffId, dateTimeInterval, kpiCalculationRelatedInfo, yAxisConfig);
                        subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(activityEntry.getValue().getName(), activityEntry.getValue().getGeneralActivityTab().getBackgroundColor(), value));
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
        for (BigInteger activityId : kpiCalculationRelatedInfo.activityIdAndTodoListMap.keySet()) {
            if (kpiCalculationRelatedInfo.getActivityMap().containsKey(activityId)) {
                Activity activity = kpiCalculationRelatedInfo.getActivityMap().get(activityId);
                List<TodoDTO> todoDTOS = new CopyOnWriteArrayList(kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.activityIdAndTodoListMap.get(activityId)));
                ClusteredBarChartKpiDataUnit clusteredBarChartKpiDataUnit = new ClusteredBarChartKpiDataUnit(activity.getName(), activity.getGeneralActivityTab().getBackgroundColor(), todoDTOS.size());
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
        PQLSettings pqlSettings = activity.getRulesActivityTab().getPqlSettings();
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
            if (newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.currentCalculationType)) {
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
            if (newHashSet(SCHEDULED_HOURS, PLANNED_HOURS_TIMEBANK, DURATION_HOURS, TOTAL_MINUTES, COLLECTIVE_TIME_BONUS_TIMEBANK, PAYOUT, COLLECTIVE_TIME_BONUS_PAYOUT, TOTAL_COLLECTIVE_BONUS, TOTAL_PLANNED_HOURS).contains(kpiCalculationRelatedInfo.currentCalculationType)) {
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
    @NoArgsConstructor
    public class KPICalculationRelatedInfo {
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
        private ShiftActivityCriteria currentShiftActivityCriteria;
        private List<TodoDTO> filterActivityTodoDto = new ArrayList<>();
        private List<YAxisConfig> yAxisConfigs = new ArrayList<>();
        private List<XAxisConfig> xAxisConfigs = new ArrayList<>();
        private List<CalculationType> calculationTypes = new ArrayList<>();
        private List<EmploymentSubType> employmentSubTypes = new ArrayList<>();
        private CalculationType currentCalculationType;
        private DateTimeInterval planningPeriodInterval;
<<<<<<< HEAD
        private List<TodoDTO> todoDTOS;
        private Map<BigInteger, List<TodoDTO>> activityIdAndTodoListMap;
        private Map<BigInteger,List<TodoDTO>> timeTypeTodoListMap;
        private Set<BigInteger> activityIds;
=======
        private List<TodoDTO> todoDTOS = new ArrayList<>();
        private Map<BigInteger, List<TodoDTO>> activityIdAndTodoListMap = new HashMap<>();
        private Set<BigInteger> activityIds = new HashSet<>();
>>>>>>> a05ac182729eb1e5ee696894cfd5afb729febe97
        private Boolean isDraft;
        private List<CountryHolidayCalenderDTO> holidayCalenders = new ArrayList<>();
        private List<TimeSlotDTO> timeSlotDTOS = new ArrayList<>();
        private Map<Long, List<AuditShiftDTO>> staffAuditLog = new HashMap<>();
        private Set<Long> tagIds = new HashSet<>();

        public KPICalculationRelatedInfo(Map<FilterType, List> filterBasedCriteria, Long unitId, ApplicableKPI applicableKPI, KPI kpi) {
            this.filterBasedCriteria = filterBasedCriteria;
            this.unitId = unitId;
            this.applicableKPI = applicableKPI;
            this.kpi = kpi;
            yAxisConfigs = ((List) copyPropertiesOrCloneCollectionByMapper(filterBasedCriteria.get(CALCULATION_BASED_ON), YAxisConfig.class));
            isNullOrEmptyThrowException(yAxisConfigs);
            xAxisConfigs = ((List) copyPropertiesOrCloneCollectionByMapper(filterBasedCriteria.get(CALCULATION_UNIT), XAxisConfig.class));
            isNullOrEmptyThrowException(xAxisConfigs);
            calculationTypes = ((List) copyPropertiesOrCloneCollectionByMapper(filterBasedCriteria.get(CALCULATION_TYPE), CalculationType.class));
            calculationTypes = isCollectionNotEmpty(calculationTypes) ? calculationTypes : ((List) copyPropertiesOrCloneCollectionByMapper(yAxisConfigs, CalculationType.class));
            employmentSubTypes = ((List) copyPropertiesOrCloneCollectionByMapper(filterBasedCriteria.get(EMPLOYMENT_SUB_TYPE), EmploymentSubType.class));
            loadKpiCalculationRelatedInfo(filterBasedCriteria, unitId, applicableKPI);
            employmentIds = staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toSet());
            getTodoDetails();
            getDailyTimeBankEntryByDate();
            getActivityTodoList();
            getTimeTypeTodoList();
            updateActivityAndTimeTypeAndPlannedTimeMap();
            planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
            getDailyTimeBankEntryByEmploymentId();
        }

        public void getActivityTodoList() {
            if (CollectionUtils.containsAny(yAxisConfigs,newHashSet(YAxisConfig.PLANNING_QUALITY_LEVEL,ABSENCE_REQUEST))) {
                todoDTOS = todoService.getAllTodoByEntityIds(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
                activityIdAndTodoListMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
            }
        }

        public void getTimeTypeTodoList(){
            Map<BigInteger,List<TodoDTO>> timeTypeTodoListMap = new HashMap<>();
            List<Activity> activities =activityMongoRepository.findAllActivitiesByIds(activityIds);
            Map<BigInteger,BigInteger> activityTimeTypeIdsMap =activities.stream().collect(Collectors.toMap(activity ->activity.getId(),activity -> activity.getBalanceSettingsActivityTab().getTimeTypeId()));
            for(Map.Entry<BigInteger,BigInteger> activityTimeTypeEntry :activityTimeTypeIdsMap.entrySet()){
                for(Map.Entry<BigInteger,List<TodoDTO>> entry :activityIdAndTodoListMap.entrySet()){
                    if(activityTimeTypeEntry.getKey().equals(entry.getKey())){
                        timeTypeTodoListMap.put(activityTimeTypeEntry.getValue(),entry.getValue());
                    }
                }
            }

        }

        public CalculationType getCalculationType() {
            return isNotNull(currentCalculationType) ? currentCalculationType : calculationTypes.get(0);
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
            for (YAxisConfig yAxisConfig : isNull(yAxis) ? yAxisConfigs : Arrays.asList(yAxis)) {
                switch (yAxisConfig) {
                    case CHILD_CARE_DAYS:
                        wtaTemplateTypes.add(CHILD_CARE_DAYS_CHECK);
                        break;
                    case SENIORDAYS:
                        wtaTemplateTypes.add(SENIOR_DAYS_PER_YEAR);
                        break;
                    case PROTECTED_DAYS_OFF:
                        wtaTemplateTypes.add(PROTECTED_DAYS_OFF);
                        break;
                    case CARE_DAYS:
                        wtaTemplateTypes.add(WTA_FOR_CARE_DAYS);
                        break;
                    case TOTAL_ABSENCE_DAYS:
                        wtaTemplateTypes.addAll(newHashSet(CHILD_CARE_DAYS_CHECK, SENIOR_DAYS_PER_YEAR, PROTECTED_DAYS_OFF, WTA_FOR_CARE_DAYS));
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
                    case ACTIVITY:
                    case PLANNING_QUALITY_LEVEL:
                        List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(filterBasedCriteria.containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ACTIVITY_IDS)) : new HashSet<>());
                        activityMap = activities.stream().collect(Collectors.toMap(Activity::getId, v -> v));
                        break;
                    case TIME_TYPE:
                        timeTypeMap = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), filterBasedCriteria.containsKey(TIME_TYPE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(TIME_TYPE)) : new ArrayList<>());
                        break;
                    case PLANNED_TIME:
                        Collection<PlannedTimeType> plannedTimeTypes = plannedTimeTypeService.getAllPlannedTimeByIds(filterBasedCriteria.containsKey(PLANNED_TIME_TYPE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(PLANNED_TIME_TYPE)) : new ArrayList<>());
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
            Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
            staffIds = (List<Long>) filterCriteria[0];
            List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
            List<Long> unitIds = (List<Long>) filterCriteria[2];
            employmentTypeIds = (List<Long>) filterCriteria[3];
            DefaultKpiDataDTO defaultKpiDataDTO = counterHelperService.getKPIAllData(applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId, getLongValue(filterBasedCriteria.getOrDefault(TAGS, new ArrayList())),filterBasedCriteria);
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
            staffIdAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().filter(distinctByKey(StaffKpiFilterDTO::getId)).collect(Collectors.toMap(StaffKpiFilterDTO::getId, v -> v));
            updateStaffAndShiftMap();
            updateAuditLogs();
        }

        private void getStaffsByTeamType(Map<FilterType, List> filterBasedCriteria) {
            List<StaffKpiFilterDTO> staffKpiFilterDTOList = new ArrayList<>();
            if (filterBasedCriteria.containsKey(TEAM_TYPE) && isCollectionNotEmpty(filterBasedCriteria.get(TEAM_TYPE))) {
                for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
                    for (TeamDTO teamDTO : staffKpiFilterDTO.getTeams()) {
                        if (filterBasedCriteria.get(TEAM_TYPE).contains(teamDTO.getTeamType().name())) {
                            staffKpiFilterDTOList.add(staffKpiFilterDTO);
                            break;
                        }
                    }
                }
                staffKpiFilterDTOS = staffKpiFilterDTOList;

            }

        }

        private void updateAuditLogs() {
            if (CollectionUtils.containsAny(newHashSet(CARE_BUBBLE), calculationTypes) && filterBasedCriteria.containsKey(TAGS) && isCollectionNotEmpty(filterBasedCriteria.get(TAGS))) {
                tagIds = getLongValueSet(filterBasedCriteria.get(TAGS));
                List<Long> validStaffIds = staffKpiFilterDTOS.stream().filter(staffKpiFilterDTO -> staffKpiFilterDTO.isTagValid(tagIds)).map(staffKpiFilterDTO -> staffKpiFilterDTO.getId()).collect(Collectors.toList());
                List<Map> shiftsLog = auditLoggingService.getAuditLogOfStaff(validStaffIds, startDate, endDate);
                List<AuditShiftDTO> auditShiftDTOS = ObjectMapperUtils.copyPropertiesOrCloneCollectionByMapper(shiftsLog, AuditShiftDTO.class);
                staffAuditLog = auditShiftDTOS.stream().collect(Collectors.groupingBy(auditShiftDTO -> auditShiftDTO.getStaffId()));
            }
        }

        private void updateShiftsDetailsForKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, List<Long> unitIds, List<TimeSlotDTO> timeSlotDTOS, List<Integer> dayOfWeeksNo) {
            if (!CollectionUtils.containsAny(newHashSet(DELTA_TIMEBANK, ACTUAL_TIMEBANK, STAFF_AGE, STAFFING_LEVEL_CAPACITY), calculationTypes)) {
                List<String> validKPIS = newArrayList(PRESENCE_UNDER_STAFFING.toString(), PRESENCE_OVER_STAFFING.toString(), ABSENCE_UNDER_STAFFING.toString(), ABSENCE_OVER_STAFFING.toString());
                if (filterBasedCriteria.containsKey(FilterType.CALCULATION_TYPE) && CollectionUtils.containsAny(validKPIS, filterBasedCriteria.get(FilterType.CALCULATION_TYPE))) {
                    List<Shift> shiftData = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), isCollectionNotEmpty(unitIds) ? unitIds : newArrayList(organizationId));
                    shifts = ObjectMapperUtils.copyPropertiesOrCloneCollectionByMapper(shiftData, ShiftWithActivityDTO.class);
                } else {
                    shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), false);
                    StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, timeSlotDTOS, organizationId);
                    shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO, staffKpiFilterDTOS);
                }
            } else {
                shifts = new ArrayList<>();
            }
        }

        public void getTodoDetails() {
            if (CollectionUtils.containsAny(yAxisConfigs,newHashSet(YAxisConfig.PLANNING_QUALITY_LEVEL,ABSENCE_REQUEST))) {
                todoDTOS = todoService.getAllTodoByEntityIds(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
                activityIds = filterBasedCriteria.containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(ACTIVITY_IDS)) : new HashSet<>();
                if (isCollectionNotEmpty(activityIds)) {
                    todoDTOS = todoDTOS.stream().filter(todoDTO -> activityIds.contains(todoDTO.getSubEntityId())).collect(Collectors.toList());
                }
                staffIdAndTodoMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getStaffId, Collectors.toList()));
                activityIdAndTodoListMap = todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
            }
        }

        public void updateTodoDtosByStaffId(Long staffId) {
            List<TodoDTO> todoDTOList = staffIdAndTodoMap.get(staffId);
            if (isNotNull(todoDTOList)) {
                activityIdAndTodoListMap = todoDTOList.stream().collect(Collectors.groupingBy(k -> k.getSubEntityId(), Collectors.toList()));
            } else {
                activityIdAndTodoListMap = new HashMap<>(0);
            }
        }
        public void updateTimeTodosDtosByStaffId(Long StaffId){
            List<TodoDTO> todoDTOList = staffIdAndTodoMap.get(StaffId);
            if (isNotNull(todoDTOList)) {
                activityIdAndTodoListMap = todoDTOList.stream().collect(Collectors.groupingBy(k -> k.getSubEntityId(), Collectors.toList()));
            }

        }

        public List<TodoDTO> getTodosByStaffIdAndInterval(Long staffId, DateTimeInterval dateTimeInterval) {
            List<TodoDTO> filterTodoDTOS = isNull(staffId) ? todoDTOS : staffIdAndTodoMap.getOrDefault(staffId, new ArrayList<>());
            if (isNotNull(dateTimeInterval)) {
                filterTodoDTOS = filterTodoDTOS.stream().filter(todoDTO -> dateTimeInterval.contains(todoDTO.getRequestedOn())).collect(Collectors.toList());
            }
            return filterTodoDTOS;
        }

        public List<TodoDTO> getTodosByInterval(DateTimeInterval dateTimeInterval, List<TodoDTO> todoDTOS) {
            return todoDTOS.stream().filter(todoDTO -> dateTimeInterval.containsAndEqualsEndDate(todoDTO.getRequestedOn())).collect(Collectors.toList());
        }

        private ShiftActivityCriteria getDefaultShiftActivityCriteria() {
            Set<BigInteger> teamActivityIds = new HashSet<>();
            if (filterBasedCriteria.containsKey(TEAM) && isCollectionNotEmpty(filterBasedCriteria.get(TEAM))) {
                Set<String> teamIds = getStringByList(new HashSet<>(filterBasedCriteria.get(TEAM)));
                ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(UserContext.getUserDetails().getLastSelectedOrganizationId(), teamIds));
                teamActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
            }
            Set<Long> reasonCodeIds = filterBasedCriteria.containsKey(REASON_CODE) ? KPIUtils.getLongValueSet(filterBasedCriteria.get(REASON_CODE)) : new HashSet<>();
            Set<ShiftStatus> shiftStatuses = filterBasedCriteria.containsKey(ACTIVITY_STATUS) ? (Set<ShiftStatus>) filterBasedCriteria.get(ACTIVITY_STATUS).stream().map(o -> ShiftStatus.valueOf(o.toString())).collect(Collectors.toSet()) : new HashSet<>();
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
                    if (isCollectionNotEmpty(timeSlotDTOS)) {
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
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = isNull(staffId) ? shifts : staffIdAndShiftsMap.getOrDefault(staffId, new ArrayList<>());
            if (isNotNull(dateTimeInterval)) {
                shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate())) : dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())).collect(Collectors.toList());
            }
            if (includeFilter) {
                StaffFilterDTO staffFilterDTO = getStaffFilterDto(filterBasedCriteria, this.timeSlotDTOS, this.unitId);
                shifts = shiftFilterService.getShiftsByFilters(shifts, staffFilterDTO, staffKpiFilterDTOS);
            }
            return shiftWithActivityDTOS;
        }

        public List<AuditShiftDTO> getShiftAuditByStaffIdAndInterval(Long staffId, DateTimeInterval dateTimeInterval) {
            List<AuditShiftDTO> shiftWithActivityDTOS = isNull(staffId) ? staffAuditLog.values().stream().flatMap(auditShiftDTOS -> auditShiftDTOS.stream()).collect(Collectors.toList()) : staffAuditLog.getOrDefault(staffId, new ArrayList<>());
            if (isNotNull(dateTimeInterval)) {
                shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> DurationType.HOURS.equals(applicableKPI.getFrequencyType()) ? dateTimeInterval.overlaps(new DateTimeInterval(shiftWithActivityDTO.getActivities().get(0).getStartDate(), shiftWithActivityDTO.getActivities().get(0).getEndDate())) : dateTimeInterval.contains(shiftWithActivityDTO.getActivities().get(0).getStartDate())).collect(Collectors.toList());
            }
            return shiftWithActivityDTOS;
        }

        private void getDailyTimeBankEntryByDate() {
            if(CollectionUtils.containsAny(newHashSet(DELTA_TIMEBANK, ACTUAL_TIMEBANK, STAFFING_LEVEL_CAPACITY), calculationTypes)) {
                dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByIdsAndBetweenDates(employmentIds, startDate, endDate);
            }
            if (isCollectionNotEmpty(daysOfWeeks)) {
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
            dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdsAndBeforDate(new ArrayList<>(employmentIds), planningPeriodInterval.getEndDate());
            employmentIdAndDailyTimebankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toCollection(ArrayList::new)));
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

