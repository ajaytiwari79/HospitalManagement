package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.BasicChartKpiDateUnit;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDateTimeintervalString;
import static com.kairos.commons.utils.KPIUtils.getDateTimeIntervals;
import static com.kairos.commons.utils.ObjectUtils.distinctByKey;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    private double getPlannedHoursOfStaff(List<Shift> shifts){
        long plannedHours=0l;
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                plannedHours += shiftActivity.getTimeBankCtaBonusMinutes() + shiftActivity.getScheduledMinutes();
            }
        }
        return DateUtils.getHoursByMinutes(plannedHours);
    }

    public Map<Object, Double> calculatePlannedHours(List<Long> staffIds, List<LocalDate> filterDates, ApplicableKPI applicableKPI, List<DateTimeInterval> dateTimeIntervals,List<Shift> shifts) {
        Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap = new HashMap<>();
        staffIds=new ArrayList<>();
        staffIds.add(920l);
        staffIds.add(975l);
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        return calculateDataByKpiRepresentation(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI.getKpiRepresentation(), shifts);
    }


    private List<CommonKpiDataUnit> getPlannedHoursKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, boolean kpi,ApplicableKPI applicableKPI) {
        double multiplicationFactor = 1;
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Set<BigInteger> timeTypeIds = new HashSet<>();
        List<Long> staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates=new ArrayList<>();
        if(isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))){
            filterDates= KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL));
        }
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        List<Long> employmentType = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        if (filterBasedCriteria.containsKey(FilterType.TIME_TYPE) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_TYPE))) {
            if (filterBasedCriteria.get(FilterType.TIME_TYPE).get(0) instanceof String) {
                timeTypeIds = timeTypeMongoRepository.findTimeTypeIdssByTimeTypeEnum(filterBasedCriteria.get(FilterType.TIME_TYPE));
            } else {
                timeTypeIds = timeTypeMongoRepository.findAllTimeTypeIdsByTimeTypeIds(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
                timeTypeIds.addAll(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
            }
        }
        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(),applicableKPI.getValue(),applicableKPI.getFrequencyType(),filterDates);
        dateTimeIntervals.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentType, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate().toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters(staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), shiftActivityStatus, timeTypeIds, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<Object, Double> staffPlannedHours = calculatePlannedHours(staffIds,filterDates,applicableKPI,dateTimeIntervals,shifts);
        getKpiDataUnits(multiplicationFactor, staffPlannedHours, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        return kpiDataUnits;
    }



    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, true,null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, false,applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long, Number> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
     //   return getPlannedHoursKpiData(organizationId, filterBasedCriteria, true).stream().collect(Collectors.toMap(k->k.getRefId().longValue(),v->((ClusteredBarChartKpiDataUnit)v).getNumberValue()));
        return null;
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

    private Map<Object, Double> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, KPIRepresentation kpiRepresentation, List<Shift> shifts) {
    Map<Object, Double> staffRestingHours = new HashMap<>();
    Double plannedHours = 0d;
    switch (kpiRepresentation) {
        case REPRESENT_PER_STAFF:
            Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));
            for (Long staffId : staffIds) {
                plannedHours = getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
                staffRestingHours.put(staffId, plannedHours);
            }
            break;
        case REPRESENT_TOTAL_DATA:
            staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));
            for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                for (Long staffId : staffIds) {
                    plannedHours += getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
                }
            }
            staffRestingHours.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), plannedHours);
            break;
        case REPRESENT_PER_INTERVAL:
            Map<DateTimeInterval, Map<Long, List<Shift>>> dateTimeIntervalListMap1 = new HashedMap();
            dateTimeIntervalListMap.keySet().stream().forEach(dateTimeInterval -> dateTimeIntervalListMap1.put(dateTimeInterval, dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()))));
            for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                plannedHours = 0d;
                staffShiftMapping = dateTimeIntervalListMap1.get(dateTimeInterval);
                for (Long staffId : staffIds) {
                    plannedHours += getPlannedHoursOfStaff(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
                }
                staffRestingHours.put(getDateTimeintervalString(dateTimeInterval), plannedHours);
            }
            break;
        default:
            break;
    }
    return staffRestingHours;
}

}
