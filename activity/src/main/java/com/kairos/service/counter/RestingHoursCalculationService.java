package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.TimeTypeService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDateTimeintervalString;
import static com.kairos.commons.utils.DateUtils.getStartDateTimeintervalString;
import static com.kairos.commons.utils.KPIUtils.getDateTimeIntervals;
import static com.kairos.commons.utils.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@Service
public class RestingHoursCalculationService implements CounterService {
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityService activityService;
    @Inject
    private UserIntegrationService userIntegrationService;

    public double getTotalRestingHours(List<Shift> shifts, LocalDate startDate, LocalDate endDate) {
        //all shifts should be sorted on startDate
        DateTimeInterval dateTimeInterval = new DateTimeInterval(startDate, endDate);
        long totalrestingMinutes = dateTimeInterval.getMilliSeconds();
        for (Shift shift : shifts) {
            DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            if (dateTimeInterval.overlaps(shiftInterval)) {
                totalrestingMinutes -= dateTimeInterval.overlap(shiftInterval).getMilliSeconds();
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalrestingMinutes);
    }

    public Map<Object, Double> calculateRestingHours(List<Long> staffIds, ApplicableKPI applicableKPI, List<DateTimeInterval> dateTimeIntervals) {
        Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap = new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIdsAndDate(staffIds, DateUtils.getLocalDateTimeFromLocalDate(DateUtils.asLocalDate(dateTimeIntervals.get(0).getStartDate())), DateUtils.getLocalDateTimeFromLocalDate(DateUtils.asLocalDate(dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        return calculateDataByKpiRepresentation(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI, shifts);
    }

    private List<CommonKpiDataUnit> getRestingHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        // TO BE USED FOR AVERAGE CALCULATION.
        double multiplicationFactor = 1;
        List staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = new ArrayList<>();
        if (isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentTypes = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(), applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        staffIds=staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        Map<Object, Double> staffRestingHours = calculateRestingHours(staffIds, applicableKPI, dateTimeIntervals);
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        getKpiDataUnits(multiplicationFactor, staffRestingHours, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
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


    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getRestingHoursKpiData(filterBasedCriteria, organizationId, null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getRestingHoursKpiData(filterBasedCriteria, organizationId, applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList,  new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long, Number> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return new HashMap<>();
    }

    private Map<Object, Double> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Shift> shifts) {
        Map<Object, Double> staffRestingHours ;
        Double restingHours = 0d;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffRestingHours = getStaffRestingHoursByRepresentPerStaff(staffIds, dateTimeIntervals, shifts);
                break;
            case REPRESENT_TOTAL_DATA:
                staffRestingHours = getStaffRestingHoursByRepresentTotalData(staffIds, dateTimeIntervals, shifts, restingHours);
                break;
            case REPRESENT_PER_INTERVAL:
                staffRestingHours =  getStaffRestingHoursByRepresentPerInterval(staffIds, dateTimeIntervalListMap, dateTimeIntervals ,applicableKPI.getFrequencyType() );
                break;
            default:
                staffRestingHours = getStaffRestingHoursByRepresentPerInterval(staffIds, dateTimeIntervalListMap, dateTimeIntervals ,applicableKPI.getFrequencyType());
                break;
        }
        return staffRestingHours;
    }

    private Map<Object, Double> getStaffRestingHoursByRepresentTotalData(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<Shift> shifts, Double restingHours) {
        Map<Long, List<Shift>> staffShiftMapping;
        Map<Object, Double> staffRestingHours = new HashMap<>();
        staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            for (Long staffId : staffIds) {
                restingHours += getTotalRestingHours(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()), DateUtils.asLocalDate(dateTimeInterval.getStartDate()), dateTimeInterval.getEndLocalDate());
            }
        }
        staffRestingHours.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), restingHours);
        return staffRestingHours;
    }

    private Map<Object, Double> getStaffRestingHoursByRepresentPerStaff(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<Shift> shifts) {
        Double restingHours;
        Map<Object, Double> staffRestingHours=new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            restingHours = getTotalRestingHours(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()), DateUtils.asLocalDate(dateTimeIntervals.get(0).getStartDate()), DateUtils.asLocalDate(dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate()));
            staffRestingHours.put(staffId, restingHours);
        }
        return staffRestingHours;
    }

    private Map<Object, Double> getStaffRestingHoursByRepresentPerInterval(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals , DurationType frequencyType) {
        Double restingHours;
        Map<Object, Double> staffRestingHours = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping;
        Map<DateTimeInterval, Map<Long, List<Shift>>> dateTimeIntervalListMap1 = new HashedMap();
        dateTimeIntervalListMap.keySet().stream().forEach(dateTimeInterval -> dateTimeIntervalListMap1.put(dateTimeInterval, dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()))));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            restingHours = 0d;
            staffShiftMapping = dateTimeIntervalListMap1.get(dateTimeInterval);
            for (Long staffId : staffIds) {
                restingHours += getTotalRestingHours(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()), DateUtils.asLocalDate(dateTimeInterval.getStartDate()), DateUtils.asLocalDate(dateTimeInterval.getEndDate()));
            }
            staffRestingHours.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), restingHours);
        }
        return staffRestingHours;
    }


}


