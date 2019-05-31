package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDateTimeintervalString;
import static com.kairos.commons.utils.DateUtils.getStartDateTimeintervalString;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;
import static com.kairos.utils.counter.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.utils.counter.KPIUtils.verifyKPIResponseData;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(PlannedHoursCalculationService.class);

    private double getPlannedHoursOfStaff(List<Shift> shifts) {
        long plannedHours = 0l;
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                plannedHours += shiftActivity.getTimeBankCtaBonusMinutes() + shiftActivity.getScheduledMinutes();
            }
        }
        return DateUtils.getHoursByMinutes(plannedHours);
    }

    public Map<Object, Double> calculatePlannedHours(List<Long> staffIds, ApplicableKPI applicableKPI, List<DateTimeInterval> dateTimeIntervals, List<Shift> shifts) {
        Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        return calculateDataByKpiRepresentation(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI, shifts);
    }


    private List<CommonKpiDataUnit> getPlannedHoursKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, ApplicableKPI applicableKPI) {
        double multiplicationFactor = 1;
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
        List<Long> unitIds = (List<Long>)filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>)filterCriteria[3];
        Set<BigInteger> timeTypeIds = new HashSet<>();
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        if (filterBasedCriteria.containsKey(FilterType.TIME_TYPE) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_TYPE))) {
            if (filterBasedCriteria.get(FilterType.TIME_TYPE).get(0) instanceof String) {
                timeTypeIds = timeTypeMongoRepository.findTimeTypeIdssByTimeTypeEnum(filterBasedCriteria.get(FilterType.TIME_TYPE));
            } else {
                timeTypeIds = timeTypeMongoRepository.findAllTimeTypeIdsByTimeTypeIds(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
                timeTypeIds.addAll(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
            }
        }
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI,filterDates,staffIds,employmentTypeIds,unitIds,organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>)kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>)kpiData[0];
        staffIds = (List<Long>) kpiData[2];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), shiftActivityStatus, timeTypeIds, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<Object, Double> staffPlannedHours = calculatePlannedHours(staffIds, applicableKPI, dateTimeIntervals, shifts);
        getKpiDataUnits(multiplicationFactor, staffPlannedHours, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }




    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    private void getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffRestingHours, List<CommonKpiDataUnit> kpiDataUnits, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        for (Map.Entry<Object, Double> entry : staffRestingHours.entrySet()) {
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


    private Map<Object, Double> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Shift> shifts) {
        Map<Object, Double> staffplannedHours;
        Double plannedHours = 0d;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffplannedHours = getStaffPlannedHoursByRepresentPerStaff(staffIds, shifts);
                break;
            case REPRESENT_TOTAL_DATA:
                staffplannedHours = getStaffPlannedHoursByRepresentTotalData(staffIds, dateTimeIntervals, shifts, plannedHours);
                break;
            default:
                staffplannedHours = getStaffPlannedHoursByRepresentPerInterval(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI.getFrequencyType());
                break;
        }
        return verifyKPIResponseData(staffplannedHours) ? staffplannedHours : new HashMap<>();
    }

    private Map<Object, Double> getStaffPlannedHoursByRepresentPerInterval(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, DurationType frequencyType) {
        Map<Object, Double> staffplannedHours = new HashMap<>();
        Double plannedHours;
        Map<Long, List<Shift>> staffShiftMapping;
        Map<DateTimeInterval, Map<Long, List<Shift>>> dateTimeIntervalAndShiftMap = new HashedMap();
        dateTimeIntervalListMap.keySet().stream().forEach(dateTimeInterval -> dateTimeIntervalAndShiftMap.put(dateTimeInterval, dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()))));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            plannedHours = 0d;
            staffShiftMapping = dateTimeIntervalAndShiftMap.get(dateTimeInterval);
            for (Long staffId : staffIds) {
                plannedHours += getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
            }
            staffplannedHours.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), plannedHours);
        }
        return staffplannedHours;
    }

    private Map<Object, Double> getStaffPlannedHoursByRepresentTotalData(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<Shift> shifts, Double plannedHours) {
        Map<Object, Double> staffplannedHours = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping;
        staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            plannedHours += getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
        }
        staffplannedHours.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getStartDate())), plannedHours);
        return staffplannedHours;
    }

    public Map<Object, Double> getStaffPlannedHoursByRepresentPerStaff(List<Long> staffIds, List<Shift> shifts) {
        Double plannedHours;
        Map<Object, Double> staffplannedHours = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            plannedHours = getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
            staffplannedHours.put(staffId, plannedHours);
        }
        return staffplannedHours;
    }

    public   Map<Object, Double>  getStaffWithPlannedHour(Map<FilterType, List> filterBasedCriteria, Long organizationId,ApplicableKPI applicableKPI) {
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
      //  List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<Long> staffIds = new ArrayList<>();
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI,filterDates,staffIds,newArrayList(),newArrayList(organizationId),organizationId);
        staffIds=(List<Long>)kpiData[2];
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>)kpiData[1];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters(staffIds, newArrayList(organizationId), newArrayList(), newHashSet(), dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<Object, Double> plannedHoursMap = getStaffPlannedHoursByRepresentPerStaff(staffIds, shifts);
       // Map<Long, Integer> staffAndRestingHoursMap = restingHoursMap.entrySet().stream().collect(Collectors.toMap(k->(Long)k.getKey(),v->v.getValue().intValue()));
        return  plannedHoursMap;
    }





    @Override
    public TreeSet<FibonacciKPICalculation>  getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder,List<StaffKpiFilterDTO> staffKpiFilterDTOS,ApplicableKPI applicableKPI) {
        Map<Object, Double> plannedHoursMap = getStaffWithPlannedHour(filterBasedCriteria,organizationId,applicableKPI);
        Map<Long, Integer> staffAndRestingHoursMap = plannedHoursMap.entrySet().stream().collect(Collectors.toMap(k->(Long)k.getKey(),v->v.getValue().intValue()));
        return getFibonacciCalculation(staffAndRestingHoursMap,sortingOrder);
    }



    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI){
        KPISetResponseDTO kpiSetResponseDTO = new KPISetResponseDTO();;
        Map<Object, Double> plannedHoursMap = getStaffWithPlannedHour(filterBasedCriteria,organizationId,applicableKPI);
        Map<Long, Double> staffAndPlanningHoursMap = plannedHoursMap.entrySet().stream().collect(Collectors.toMap(k->(Long)k.getKey(),v->v.getValue().doubleValue()));
        kpiSetResponseDTO.setKpiId(kpi.getId());
        kpiSetResponseDTO.setKpiName(kpi.getTitle());
        kpiSetResponseDTO.setStaffKPIValue(staffAndPlanningHoursMap);
        return kpiSetResponseDTO;
    }

}
