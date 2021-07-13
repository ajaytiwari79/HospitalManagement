package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.*;
import com.kairos.persistence.repository.counter.ShiftMongoRepository;
import com.kairos.persistence.repository.counter.TimeTypeMongoRepository;
import com.kairos.utils.counter.FibonacciCalculationUtil;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.enums.kpi.KPIRepresentation.REPRESENT_PER_STAFF;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;


    public double getPlannedHoursOfStaff(List<Shift> shifts) {
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
        List<Long> staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>) filterCriteria[3];
        Set<BigInteger> timeTypeIds = new HashSet<>();
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        if (filterBasedCriteria.containsKey(FilterType.TIME_TYPE) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_TYPE))) {
            if (filterBasedCriteria.get(FilterType.TIME_TYPE).get(0) instanceof String) {
                timeTypeIds = timeTypeMongoRepository.findTimeTypeIdssByTimeTypeEnum(filterBasedCriteria.get(FilterType.TIME_TYPE));
            } else {
                timeTypeIds = timeTypeMongoRepository.findAllTimeTypeIdsByTimeTypeIds(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
                timeTypeIds.addAll(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
            }
        }
        Object[] kpiData = counterHelperService.getKPIdata(new HashMap(),applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
        staffIds = (List<Long>) kpiData[2];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters(staffIds, ObjectUtils.isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), shiftActivityStatus, timeTypeIds, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<Object, Double> staffPlannedHours = calculatePlannedHours(staffIds, applicableKPI, dateTimeIntervals, shifts);
        getKpiDataUnits(multiplicationFactor, staffPlannedHours, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        KPIUtils.sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    private void getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffRestingHours, List<CommonKpiDataUnit> kpiDataUnits, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        for (Map.Entry<Object, Double> entry : staffRestingHours.entrySet()) {
            if (REPRESENT_PER_STAFF.equals(applicableKPI.getKpiRepresentation())){
                Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue() * multiplicationFactor))));
            }else{
                kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(KPIUtils.getKpiDateFormatByIntervalUnit(entry.getKey().toString(),applicableKPI.getFrequencyType(),applicableKPI.getKpiRepresentation()),entry.getKey().toString(), Arrays.asList(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue() * multiplicationFactor))));
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
        return KPIUtils.verifyKPIResponseData(staffplannedHours) ? staffplannedHours : new HashMap<>();
    }

    public Map<Object, Double> getStaffPlannedHoursByRepresentPerInterval(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, DurationType frequencyType) {
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
            staffplannedHours.put(DurationType.DAYS.equals(frequencyType) ? DateUtils.getStartDateTimeintervalString(dateTimeInterval) : DateUtils.getDateTimeintervalString(dateTimeInterval), plannedHours);
        }
        return staffplannedHours;
    }

    public Map<Object, Double> getStaffPlannedHoursByRepresentTotalData(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<Shift> shifts, Double plannedHours) {
        Map<Object, Double> staffplannedHours = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping;
        staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            plannedHours += getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
        }
        staffplannedHours.put(DateUtils.getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), plannedHours);
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

    public Map<Object, Double> getStaffWithPlannedHour(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = new ArrayList<>();
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        Object[] kpiData = counterHelperService.getKPIdata(new HashMap(),applicableKPI, filterDates, staffIds, ObjectUtils.newArrayList(), ObjectUtils.newArrayList(organizationId), organizationId);
        staffIds = (List<Long>) kpiData[2];
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters(staffIds, ObjectUtils.newArrayList(organizationId), ObjectUtils.newArrayList(), ObjectUtils.newHashSet(), dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        return getStaffPlannedHoursByRepresentPerStaff(staffIds, shifts);
    }


    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, KPI kpi,ApplicableKPI applicableKPI) {
        Map<Object, Double> plannedHoursMap = getStaffWithPlannedHour(filterBasedCriteria, organizationId, applicableKPI);
        Map<Long, Integer> staffAndPlannedHoursMap = plannedHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().intValue()));
        return FibonacciCalculationUtil.getFibonacciCalculation(staffAndPlannedHoursMap, sortingOrder);
    }


    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPISetResponseDTO kpiSetResponseDTO = new KPISetResponseDTO();
        Map<Object, Double> plannedHoursMap = getStaffWithPlannedHour(filterBasedCriteria, organizationId, applicableKPI);
        Map<Long, Double> staffAndPlanningHoursMap = plannedHoursMap.entrySet().stream().collect(Collectors.toMap(k -> (Long) k.getKey(), v -> v.getValue().doubleValue()));
        kpiSetResponseDTO.setKpiId(kpi.getId());
        kpiSetResponseDTO.setKpiName(kpi.getTitle());
        kpiSetResponseDTO.setStaffKPIValue(staffAndPlanningHoursMap);
        return kpiSetResponseDTO;
    }

}
