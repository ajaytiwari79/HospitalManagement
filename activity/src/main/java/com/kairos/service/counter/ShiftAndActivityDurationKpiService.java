package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.getDateTimeintervalString;
import static com.kairos.commons.utils.KPIUtils.getDateTimeIntervals;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.BLANK_STRING;

@Service
public class ShiftAndActivityDurationKpiService implements CounterService {

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
//TODO not remove
//    private List<CommonKpiDataUnit> calculateDurationOfShiftAndActivity(List<ShiftWithActivityDTO> shiftWithActivityDTOS, LocalDate startDate, LocalDate endDate) {
//        List<CommonKpiDataUnit> clusteredBarChartKpiDataUnits = new ArrayList<>();
//        if (isCollectionNotEmpty(shiftWithActivityDTOS)) {
//            Map<String, String> activityNameAndColorCodeMap = new HashMap<>();
//            Map<LocalDate, List<ShiftWithActivityDTO>> dateAndShiftWithActivityMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(t -> asLocalDate(t.getStartDate()), Collectors.toList()));
//            while (startDateIsEqualsOrBeforeEndDate(startDate, endDate)) {
//                Integer shiftDurationMinutes = 0;
//                List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
//                Map<String, Integer> activityNameAndTotalDurationMinutesMap = new HashMap<>();
//                List<ShiftWithActivityDTO> shiftWithActivityDTO = dateAndShiftWithActivityMap.get(startDate);
//                if (CollectionUtils.isNotEmpty(shiftWithActivityDTO)) {
//                    for (ShiftWithActivityDTO shift : shiftWithActivityDTO) {
//                        shift.getActivities().forEach(activity -> {
//                            int activityDuration = activityNameAndTotalDurationMinutesMap.getOrDefault(activity.getActivityName(), 0);
//                            activityNameAndTotalDurationMinutesMap.put(activity.getActivityName(), activityDuration + activity.getDurationMinutes());
//                            activityNameAndColorCodeMap.putIfAbsent(activity.getActivityName(), (isNotNull(activity.getBackgroundColor()) && !BLANK_STRING.equals(activity.getBackgroundColor())) ? activity.getBackgroundColor() : AppConstants.KPI_DEFAULT_COLOR);
//                        });
//                        shiftDurationMinutes += shift.getDurationMinutes();
//                    }
//                }
//                subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(AppConstants.SHIFT, DateUtils.getHoursByMinutes(shiftDurationMinutes.doubleValue())));
//                activityNameAndTotalDurationMinutesMap.keySet().forEach(s -> subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(s, activityNameAndColorCodeMap.get(s), DateUtils.getHoursByMinutes(activityNameAndTotalDurationMinutesMap.get(s)))));
//
//            }
//        }
//        return clusteredBarChartKpiDataUnits;
//    }

    private List<CommonKpiDataUnit> getDurationOfShiftAndActivity(Long organizationId, Map<FilterType, List> filterBasedCriteria, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Set<DayOfWeek> daysOfWeek = filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) ? KPIUtils.getDaysOfWeeksfromString(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) : newHashSet(DayOfWeek.values());
        List<Long> staffIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<LocalDate> filterDates = new ArrayList<>();
        if (isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        List<BigInteger> activitiesIds = KPIUtils.getBigIntegerValue(filterBasedCriteria.getOrDefault(FilterType.ACTIVITY_IDS, new ArrayList<>()));
        List<Long> unitIds = filterBasedCriteria.containsKey(FilterType.UNIT_IDS) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentTypes = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.EMPLOYMENT_TYPE, new ArrayList()));
        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(), applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        staffIds = staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList());
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), activitiesIds, dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectDoubleMap = calculateDataByKpiRepresentation(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI.getKpiRepresentation(), shifts);
        getKpiDataUnits(objectDoubleMap, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        return kpiDataUnits;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getDurationOfShiftAndActivity(organizationId, filterBasedCriteria, null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getDurationOfShiftAndActivity(organizationId, filterBasedCriteria, applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long, Number> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return new HashMap<>();
    }

    private void getKpiDataUnits(Map<Object, List<ClusteredBarChartKpiDataUnit>> staffRestingHours, List<CommonKpiDataUnit> kpiDataUnits, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        for (Map.Entry<Object, List<ClusteredBarChartKpiDataUnit>> entry : staffRestingHours.entrySet()) {
            switch (applicableKPI.getKpiRepresentation()) {
                case REPRESENT_PER_STAFF:
                    Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                    kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue()));
                    break;
                default:
                    kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue()));
                    break;

            }
        }
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, KPIRepresentation kpiRepresentation, List<ShiftWithActivityDTO> shifts) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap = new HashedMap();
        Map<String, Integer> activityNameAndTotalDurationMinutesMap = new HashMap<>();
        Integer shiftDurationMinutes = 0;
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        Map<String, String> activityNameAndColorCodeMap = new HashMap<>();
        switch (kpiRepresentation) {
            case REPRESENT_PER_STAFF:
                objectListMap = getShiftAndActivityByRepresentPerStaff(staffIds, shifts, objectListMap, activityNameAndColorCodeMap);
                break;
            case REPRESENT_TOTAL_DATA:
                objectListMap = getShiftAndActivityByRepresentTotalData(dateTimeIntervals, shifts, objectListMap, activityNameAndTotalDurationMinutesMap, shiftDurationMinutes, subClusteredBarValue, activityNameAndColorCodeMap);
                break;
            case REPRESENT_PER_INTERVAL:
                objectListMap = getShiftAndActivityByRepresentPerInterval(dateTimeIntervalListMap, dateTimeIntervals, objectListMap, activityNameAndColorCodeMap);
                break;
            default:
                break;
        }
        return objectListMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getShiftAndActivityByRepresentPerInterval(Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap, Map<String, String> activityNameAndColorCodeMap) {
        Map<String, Integer> activityNameAndTotalDurationMinutesMap;
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue;
        Integer shiftDurationMinutes;
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            activityNameAndTotalDurationMinutesMap = new HashMap<>();
            subClusteredBarValue = new ArrayList<>();
            shiftDurationMinutes = 0;
            List<ShiftWithActivityDTO> shiftWithActivityDTO = dateTimeIntervalListMap.get(dateTimeInterval);
            if (CollectionUtils.isNotEmpty(shiftWithActivityDTO)) {
                subClusteredBarValue = getShiftAndActivityDurationMap(activityNameAndColorCodeMap, activityNameAndTotalDurationMinutesMap, subClusteredBarValue, shiftDurationMinutes,shiftWithActivityDTO);
            }
            objectListMap.put(getDateTimeintervalString(dateTimeInterval), subClusteredBarValue);
        }
        return objectListMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getShiftAndActivityByRepresentTotalData(List<DateTimeInterval> dateTimeIntervals, List<ShiftWithActivityDTO> shifts, Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap, Map<String, Integer> activityNameAndTotalDurationMinutesMap, Integer shiftDurationMinutes, List<ClusteredBarChartKpiDataUnit> subClusteredBarValue, Map<String, String> activityNameAndColorCodeMap) {
        objectListMap.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), getShiftAndActivityDurationMap(activityNameAndColorCodeMap, activityNameAndTotalDurationMinutesMap, subClusteredBarValue, shiftDurationMinutes,shifts));
        return objectListMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getShiftAndActivityByRepresentPerStaff(List<Long> staffIds, List<ShiftWithActivityDTO> shifts, Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap, Map<String, String> activityNameAndColorCodeMap) {
        Map<String, Integer> activityNameAndTotalDurationMinutesMap;
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue;
        Integer shiftDurationMinutes;
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));
        for (Long staffId : staffIds) {
            activityNameAndTotalDurationMinutesMap = new HashMap<>();
            subClusteredBarValue = new ArrayList<>();
            shiftDurationMinutes = 0;
            objectListMap.put(staffId, getShiftAndActivityDurationMap(activityNameAndColorCodeMap, activityNameAndTotalDurationMinutesMap, subClusteredBarValue, shiftDurationMinutes, staffShiftMapping.getOrDefault(staffId,new ArrayList<>())));
        }
        return objectListMap;
    }

    private List<ClusteredBarChartKpiDataUnit> getShiftAndActivityDurationMap(Map<String, String> activityNameAndColorCodeMap, Map<String, Integer> activityNameAndTotalDurationMinutesMap, List<ClusteredBarChartKpiDataUnit> subClusteredBarValue, Integer shiftDurationMinutes, List<ShiftWithActivityDTO> shifts) {
        for (ShiftWithActivityDTO shift : shifts) {
            for (ShiftActivityDTO activity : shift.getActivities()) {
                int activityDuration = activityNameAndTotalDurationMinutesMap.getOrDefault(activity.getActivityName(), 0);
                activityNameAndTotalDurationMinutesMap.put(activity.getActivityName(), activityDuration + activity.getDurationMinutes());
                activityNameAndColorCodeMap.putIfAbsent(activity.getActivityName(), (isNotNull(activity.getBackgroundColor()) && !BLANK_STRING.equals(activity.getBackgroundColor())) ? activity.getBackgroundColor() : AppConstants.KPI_DEFAULT_COLOR);
            }
            shiftDurationMinutes += shift.getDurationMinutes();
        }
        subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(AppConstants.SHIFT, DateUtils.getHoursByMinutes(shiftDurationMinutes.doubleValue())));
        for (String s : activityNameAndTotalDurationMinutesMap.keySet()) {
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(s, activityNameAndColorCodeMap.get(s), DateUtils.getHoursByMinutes(activityNameAndTotalDurationMinutesMap.get(s))));
        }
        return subClusteredBarValue;
    }

}
