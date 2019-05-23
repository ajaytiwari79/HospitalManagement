package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.BarLineChartKPiDateUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.BarLineChartKPIRepresentationData;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;
import static com.kairos.utils.counter.KPIUtils.sortKpiDataByDateTimeInterval;

@Service
public class ContractualAndPlannedHoursCalculationService implements CounterService {

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private PlannedHoursCalculationService plannedHoursCalculationService;
    @Inject
    private CounterHelperService counterHelperService;


    private Map<Long, Set<DateTimeInterval>> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate) {
        Map<Long, Set<DateTimeInterval>> unitAndDateTimeIntervalMap = new HashMap<>();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Map<Long, List<PlanningPeriod>> unitAndPlanningPeriodMap = planningPeriods.stream().collect(Collectors.groupingBy(PlanningPeriod::getUnitId, Collectors.toList()));
        unitIds.forEach(unitId -> {
            Set<DateTimeInterval>  dateTimeIntervals = unitAndPlanningPeriodMap.getOrDefault(unitId , new ArrayList<>()).stream().map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            unitAndDateTimeIntervalMap.put(unitId, dateTimeIntervals);
        });
        return unitAndDateTimeIntervalMap;
    }

    private List<CommonKpiDataUnit> getContractualAndPlannedHoursKpiDate(Long organizationId, Map<FilterType, List> filterBasedCriteria ,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits ;
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
        List<Long> unitIds = (List<Long>)filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>)filterCriteria[3];
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI,filterDates,staffIds,employmentTypeIds,unitIds,organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>)kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>)kpiData[0];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters((List<Long>) kpiData[2], isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), new HashSet<>(), dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<Object, Double> staffPlannedHours = plannedHoursCalculationService.calculatePlannedHours(staffIds, applicableKPI, dateTimeIntervals, shifts);
        Map<Object, Double> staffContractualAndPlannedHours = calculateDataByKpiRepresentation(dateTimeIntervals, applicableKPI,unitIds ,staffKpiFilterDTOS);
        kpiDataUnits = getKpiDataUnits(staffPlannedHours,staffContractualAndPlannedHours,applicableKPI,staffKpiFilterDTOS);
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }
    /**
     * @param unitIdAndPlanningPeriodIntervalMap
     * @param interval
     * @param staffKpiFilterDTOS
     * @return
     */
    public Map<Object, Double> calculateContractualHoursForStaff(Map<Long, Set<DateTimeInterval>> unitIdAndPlanningPeriodIntervalMap, Interval interval, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        Map<Object, Double> staffAndContractualHourMap = new HashMap<>();
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            Long contractualMinutes = 0l;
            contractualMinutes = getaContratualMinutes(unitIdAndPlanningPeriodIntervalMap, interval, staffKpiFilterDTO, contractualMinutes);
            staffAndContractualHourMap.put(staffKpiFilterDTO.getId(), DateUtils.getHoursByMinutes(contractualMinutes.doubleValue()));

        }
        return staffAndContractualHourMap;
    }

    private Long getaContratualMinutes(Map<Long, Set<DateTimeInterval>> unitIdAndPlanningPeriodIntervalMap, Interval interval, StaffKpiFilterDTO staffKpiFilterDTO, Long contractualMinutes) {
        for (EmploymentWithCtaDetailsDTO positionWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
            if (interval != null) {
                DateTime startDate = interval.getStart();
                while (startDate.isBefore(interval.getEnd())) {
                    contractualMinutes += timeBankCalculationService.getContractualMinutesByDate(unitIdAndPlanningPeriodIntervalMap.get(staffKpiFilterDTO.getUnitId()), DateUtils.asLocalDate(startDate), positionWithCtaDetailsDTO.getEmploymentLines());
                    startDate = startDate.plusDays(1);
                }
            }
        }
        return contractualMinutes;
    }

    public Map<Object, Double> calculateContractualHoursPerInterval(Map<Long, Set<DateTimeInterval>> unitIdAndPlanningPeriodIntervalMap, List<StaffKpiFilterDTO> staffKpiFilterDTOS ,List<DateTimeInterval> dateTimeIntervals, DurationType frequencyType) {
        Map<Object, Double> dateTimeIntervalAndContractualHourMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            Long contractualMinutes = 0l;
            Interval interval=new Interval(DateUtils.getLongFromLocalDate(dateTimeInterval.getStartLocalDate()),DateUtils.getLongFromLocalDate(dateTimeInterval.getEndLocalDate()));
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            contractualMinutes = getaContratualMinutes(unitIdAndPlanningPeriodIntervalMap, interval, staffKpiFilterDTO, contractualMinutes);
            }
            dateTimeIntervalAndContractualHourMap.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), DateUtils.getHoursByMinutes(contractualMinutes.doubleValue()));

        }
        return dateTimeIntervalAndContractualHourMap;
    }

    public Map<Object, Double> calculateContractualHoursTotalData(Map<Long, Set<DateTimeInterval>> unitIdAndPlanningPeriodIntervalMap, Interval interval, List<StaffKpiFilterDTO> staffKpiFilterDTOS ,List<DateTimeInterval> dateTimeIntervals) {
        Map<Object, Double> dateTimeIntervalAndContractualHourMap = new HashMap<>();
            Long contractualMinutes = 0l;
            for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
                contractualMinutes = getaContratualMinutes(unitIdAndPlanningPeriodIntervalMap, interval, staffKpiFilterDTO, contractualMinutes);
                }
            dateTimeIntervalAndContractualHourMap.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), DateUtils.getHoursByMinutes(contractualMinutes.doubleValue()));

        return dateTimeIntervalAndContractualHourMap;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getContractualAndPlannedHoursKpiDate(organizationId, filterBasedCriteria,null);
        return new BarLineChartKPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.CONTRACTUAL_HOURS, AppConstants.BAR_YAXIS), new KPIAxisData(AppConstants.PLANNED_HOURS, AppConstants.LINE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getContractualAndPlannedHoursKpiDate(organizationId, filterBasedCriteria ,applicableKPI);
        return new BarLineChartKPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.CONTRACTUAL_HOURS, AppConstants.BAR_YAXIS), new KPIAxisData(AppConstants.PLANNED_HOURS, AppConstants.LINE_FIELD));
    }

    @Override
    public TreeSet<FibonacciKPICalculation>  getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder,List<StaffKpiFilterDTO> staffKpiFilterDTOS,ApplicableKPI applicableKPI) {
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI,filterDates,staffIds,newArrayList(),newArrayList(organizationId),organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>)kpiData[1];
        Map<Object, Double> restingHoursMap = calculateDataByKpiRepresentation(dateTimeIntervals, applicableKPI, newArrayList(organizationId),staffKpiFilterDTOS);
        Map<Long, Integer> staffAndRestingHoursMap = restingHoursMap.entrySet().stream().collect(Collectors.toMap(k->(Long)k.getKey(),v->v.getValue().intValue()));
        return getFibonacciCalculation(staffAndRestingHoursMap,sortingOrder);
    }


    private Map<Object, Double> calculateDataByKpiRepresentation(List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Long> unitIds,List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Object, Double> staffContratualHours ;
        Map<Long, Set<DateTimeInterval>> planningPeriodIntervel = getPlanningPeriodIntervals(unitIds, dateTimeIntervals.get(0).getStartDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate());
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffContratualHours = calculateContractualHoursForStaff(planningPeriodIntervel,new Interval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate())),staffKpiFilterDTOS);
                break;
            case REPRESENT_TOTAL_DATA:
                staffContratualHours = calculateContractualHoursTotalData(planningPeriodIntervel,new Interval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate())),staffKpiFilterDTOS,dateTimeIntervals);
                break;
            default:
                staffContratualHours = calculateContractualHoursPerInterval(planningPeriodIntervel,staffKpiFilterDTOS,dateTimeIntervals, applicableKPI.getFrequencyType());
                break;
        }
        return staffContratualHours;
    }


    private List<CommonKpiDataUnit> getKpiDataUnits(Map<Object, Double> staffPlannedHours,Map<Object, Double> staffContractualAndPlannedHours, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<CommonKpiDataUnit> kpiDataUnits;
            switch (applicableKPI.getKpiRepresentation()) {
                case REPRESENT_PER_STAFF:
                    Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                    kpiDataUnits = staffContractualAndPlannedHours.entrySet().stream().map(entry -> new BarLineChartKPiDateUnit(staffIdAndNameMap.get(entry.getKey()), (Long)entry.getKey(), entry.getValue(), staffPlannedHours.get(entry.getKey()))).collect(Collectors.toList());
                    break;
                default:
                    kpiDataUnits = staffContractualAndPlannedHours.entrySet().stream().map(entry -> new BarLineChartKPiDateUnit(entry.getKey().toString(),  entry.getValue(), staffPlannedHours.get(entry.getKey()))).collect(Collectors.toList());
                    break;

        }
        return kpiDataUnits;
    }

    public KPISetResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI){
        Map<Object,Object> objectMap = new HashedMap();
        KPISetResponseDTO kpiSetResponseDTO = new KPISetResponseDTO();
        return kpiSetResponseDTO;

    }

}
