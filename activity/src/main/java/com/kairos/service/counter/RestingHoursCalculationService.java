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
import com.kairos.enums.kpi.Interval;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Object, Double> calculateRestingHours(List<Long> staffIds, LocalDateTime startDate, LocalDateTime endDate,ApplicableKPI applicableKPI) {
        Map<DateTimeInterval,List<Shift>> dateTimeIntervalListMap = new HashMap<>();
        List<DateTimeInterval> dateTimeIntervals=getDateTimeIntervals(applicableKPI);
        dateTimeIntervals.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        staffIds=new ArrayList<>();
        staffIds.add(920l);
        staffIds.add(975l);
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIdsAndDate(staffIds, DateUtils.getLocalDateTimeFromLocalDate(DateUtils.asLocalDate(dateTimeIntervals.get(0).getStartDate())), DateUtils.getLocalDateTimeFromLocalDate(DateUtils.asLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate())));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval,shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        return calculateDataByKpiRepresentation(staffIds,dateTimeIntervalListMap,dateTimeIntervals,applicableKPI.getKpiRepresentation(),shifts);
    }

    private List<CommonKpiDataUnit> getRestingHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, boolean averageDay, boolean kpi,ApplicableKPI applicableKPI) {
        // TO BE USED FOR AVERAGE CALCULATION.
        double multiplicationFactor = 1;
        //FIXME: fixed time interval TO BE REMOVED ONCE FILTERS IMPLEMENTED PROPERLY
        List staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL) != null && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) ? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentTypes = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Object, Double> staffRestingHours = calculateRestingHours(staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), DateUtils.getLocalDateTimeFromLocalDate(filterDates.get(0)), DateUtils.getLocalDateTimeFromLocalDate(filterDates.get(1)).plusDays(1),applicableKPI);
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        getKpiDataUnits(multiplicationFactor, staffRestingHours, kpiDataUnits,applicableKPI,staffKpiFilterDTOS);
        return kpiDataUnits;
    }

    private void getKpiDataUnits(double multiplicationFactor, Map<Object, Double> staffRestingHours, List<CommonKpiDataUnit> kpiDataUnits,ApplicableKPI  applicableKPI,List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        for (Map.Entry<Object, Double> entry : staffRestingHours.entrySet()) {
            switch (applicableKPI.getKpiRepresentation()){
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
        List<CommonKpiDataUnit> dataList = getRestingHoursKpiData(filterBasedCriteria, organizationId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), false,null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getRestingHoursKpiData(filterBasedCriteria, organizationId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), true,applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long, Number> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return new HashMap<>();//getRestingHoursKpiData(filterBasedCriteria, organizationId, true, false);
    }

    private Map<Object, Double> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval,List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals , KPIRepresentation kpiRepresentation,List<Shift> shifts){
        Map<Object, Double> staffRestingHours = new HashMap<>();
        Double restingHours=0d;
        switch (kpiRepresentation){
            case REPRESENT_PER_STAFF:
                Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));

                for (Long staffId : staffIds) {
                    restingHours = getTotalRestingHours(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()), DateUtils.asLocalDate(dateTimeIntervals.get(0).getStartDate()), DateUtils.asLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate()));
                    staffRestingHours.put(staffId, restingHours);
                }
                break;
            case REPRESENT_TOTAL_DATA:
                for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                        restingHours += getTotalRestingHours(dateTimeIntervalListMap.getOrDefault(dateTimeInterval, new ArrayList<>()),  DateUtils.asLocalDate(dateTimeInterval.getStartDate()), DateUtils.asLocalDate(dateTimeInterval.getEndDate()));
                }
                staffRestingHours.put(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate()), restingHours);
                break;
            case REPRESENT_PER_INTERVAL:
                Map<DateTimeInterval,Map<Long,List<Shift>>> dateTimeIntervalListMap1 = new HashedMap();
                dateTimeIntervalListMap.keySet().stream().forEach(dateTimeInterval -> dateTimeIntervalListMap1.put(dateTimeInterval,dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()))));
                for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
                    restingHours=0d;
                    staffShiftMapping=dateTimeIntervalListMap1.get(dateTimeInterval);
                    for (Long staffId : staffIds) {
                        restingHours += getTotalRestingHours(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()),  DateUtils.asLocalDate(dateTimeInterval.getStartDate()), DateUtils.asLocalDate(dateTimeInterval.getEndDate()));
                    }
                    staffRestingHours.put(dateTimeInterval, restingHours);
                }
                break;
            default:
                break;
        }

        return staffRestingHours;
    }

    private List<DateTimeInterval> getDateTimeIntervals(ApplicableKPI applicableKPI) {
        List<DateTimeInterval> dateTimeIntervals = new ArrayList<>();
        LocalDate currentDate = DateUtils.getCurrentLocalDate();
        switch (applicableKPI.getInterval()) {
            case LAST:
                for (int i = 0; i < applicableKPI.getValue(); i++) {
                    currentDate = getLastDateTimeIntervalByDate(currentDate, applicableKPI.getFrequencyType(), dateTimeIntervals);
                }
                break;
            case CURRENT:
                getCurrentDateTimeIntervalByDate(currentDate, applicableKPI.getFrequencyType(), dateTimeIntervals);
                break;
            case NEXT:
                for (int i = 0; i < applicableKPI.getValue(); i++) {
                    currentDate = getNextDateTimeIntervalByDate(currentDate, applicableKPI.getFrequencyType(), dateTimeIntervals);
                }
                break;
            default:
                break;
        }

        return dateTimeIntervals;
    }

    private LocalDate getNextDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = date;
        LocalDate nextDate = getNextLocaDateByDurationType(date,durationType);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    private LocalDate getCurrentDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = getFirstDayOfCurrentDurationType(date,durationType);
        LocalDate nextDate = getCurrentLocaDateByDurationType(date,durationType);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    private LocalDate getLastDateTimeIntervalByDate(LocalDate date, DurationType durationType, List<DateTimeInterval> dateTimeIntervals) {
        LocalDate currentDate = date;
        LocalDate nextDate = getPriviousLocaDateByDurationType(date,durationType);
        dateTimeIntervals.add(new DateTimeInterval(currentDate, nextDate));
        return nextDate;
    }

    private LocalDate getNextLocaDateByDurationType(LocalDate date, DurationType durationType){
        switch (durationType){
            case DAYS:
                date=date.plusDays(1);
                break;
            case MONTHS:
                date=date.plusMonths(1);
                break;
            case WEEKS:
                date=date.plusWeeks(1);
                break;
            case YEAR:
                date=date.plusYears(1);
                break;
            default:
                break;
        }
        return date;
    }

    private LocalDate getPriviousLocaDateByDurationType(LocalDate date, DurationType durationType){
        switch (durationType){
            case DAYS:
                date = date.minusDays(1);
                break;
            case MONTHS:
                date = date.minusMonths(1);
                break;
            case WEEKS:
                date = date.minusWeeks(1);
                break;
            case YEAR:
                date = date.minusYears(1);
                break;
            default:
                break;
        }
        return date;
    }

    private LocalDate getCurrentLocaDateByDurationType(LocalDate date, DurationType durationType){
        switch (durationType){
            case DAYS:
                date=date.plusDays(1);
                break;
            case MONTHS:
                date=date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case WEEKS:
                date=date.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
                break;
            case YEAR:
                date=date.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                break;
        }
        return date;
    }

    private LocalDate getFirstDayOfCurrentDurationType(LocalDate date, DurationType durationType){
        switch (durationType){
            case DAYS:
                break;
            case MONTHS:
                date=date.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case WEEKS:
                date=date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                break;
            case YEAR:
                date=date.with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                break;
        }
        return date;
    }
}


