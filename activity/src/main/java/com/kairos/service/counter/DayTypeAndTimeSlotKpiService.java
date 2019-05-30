package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.Day;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.KPIUtils.getDateTimeIntervals;
import static com.kairos.commons.utils.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.commons.utils.ObjectUtils.*;


@Service
public class DayTypeAndTimeSlotKpiService implements CounterService {
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;


    // use for calculate hours of given days of daytype
    private Double getTotalHoursOfDayType(List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<Day> days) {
        Long totalMilliSeconds = 0L;
        //TODO  when remove Everyday from day enum then remove if statement and use dayOfWeek of java
        if (days.get(0).equals(Day.EVERYDAY)) {
            days.addAll(newHashSet(Day.values()));
        }
        LocalTime startTime = LocalTime.of(timeSlotDTO.getStartHour(), timeSlotDTO.getStartMinute());
        LocalTime endTime = LocalTime.of(timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute());
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            DateTimeInterval dateTimeInterval = new DateTimeInterval(getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(shiftWithActivityDTO.getStartDate()), startTime)), getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(shiftWithActivityDTO.getStartDate()), endTime)));
            if (AppConstants.NIGHT.equals(timeSlotDTO.getName())) {
                dateTimeInterval = new DateTimeInterval(getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(shiftWithActivityDTO.getStartDate()), startTime)), getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(shiftWithActivityDTO.getEndDate()), endTime)));
            }
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
            if (days.stream().anyMatch(day -> day.toString().equals(asLocalDate(shiftWithActivityDTO.getStartDate()).getDayOfWeek().toString()))) {
                totalMilliSeconds += (dateTimeInterval.overlap(shiftInterval)) != null ? dateTimeInterval.overlap(shiftInterval).getMilliSeconds() : 0;
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalMilliSeconds);
    }

    private Double getTotalHoursOfHolidayDayType(Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap, TimeSlotDTO timeSlotDTO, List<CountryHolidayCalenderDTO> countryHolidayCalenderDTOS) {
        Long totalMilliSeconds = 0L;
        LocalTime startTime = LocalTime.of(timeSlotDTO.getStartHour(), timeSlotDTO.getStartMinute());
        LocalTime endTime = LocalTime.of(timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute());
        if (isCollectionNotEmpty(countryHolidayCalenderDTOS)) {
            for (CountryHolidayCalenderDTO countryHolidayCalenderDTO : countryHolidayCalenderDTOS) {
                if (localDateShiftMap.containsKey(countryHolidayCalenderDTO.getHolidayDate())) {
                    for (ShiftWithActivityDTO shiftWithActivityDTO : localDateShiftMap.get(countryHolidayCalenderDTO.getHolidayDate())) {
                        DateTimeInterval dateTimeInterval;
                        if (AppConstants.NIGHT.equals(timeSlotDTO.getName())) {
                            LocalDate endDate = asLocalDate(shiftWithActivityDTO.getEndDate()).equals(asLocalDate(shiftWithActivityDTO.getStartDate())) ? asLocalDate(shiftWithActivityDTO.getEndDate()).plusDays(1) : asLocalDate(shiftWithActivityDTO.getEndDate());
                            dateTimeInterval = new DateTimeInterval(asDate(asLocalDate(shiftWithActivityDTO.getStartDate()), startTime), asDate(endDate, endTime));
                        } else {
                            dateTimeInterval = new DateTimeInterval(asDate(asLocalDate(shiftWithActivityDTO.getStartDate()), startTime), asDate(asLocalDate(shiftWithActivityDTO.getStartDate()), endTime));
                        }
                        Date startDateOfHolidayType = isNotNull(countryHolidayCalenderDTO.getStartTime()) ? asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getStartTime()) : asDate(countryHolidayCalenderDTO.getHolidayDate());
                        Date endDateOfHolidayType = isNotNull(countryHolidayCalenderDTO.getEndTime()) ? DateUtils.asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getEndTime()) : asDateEndOfDay(countryHolidayCalenderDTO.getHolidayDate());
                        endDateOfHolidayType = (startDateOfHolidayType.after(endDateOfHolidayType)) ? getStartOfDay(asDate(countryHolidayCalenderDTO.getHolidayDate().plusDays(1))) : endDateOfHolidayType;
                        DateTimeInterval dateTimeIntervalOfHolidayType = new DateTimeInterval(startDateOfHolidayType, endDateOfHolidayType).overlap(dateTimeInterval);
                        DateTimeInterval shiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
                        if (isNotNull(dateTimeIntervalOfHolidayType) && dateTimeIntervalOfHolidayType.overlaps(shiftInterval)) {
                            totalMilliSeconds += dateTimeIntervalOfHolidayType.overlap(shiftInterval).getMilliSeconds();
                        }
                    }
                }
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalMilliSeconds);
    }




    private List<CommonKpiDataUnit> getDayTypeAndTimeSlotHours(Long organizationId, Map<FilterType, List> filterBasedCriteria,ApplicableKPI applicableKPI) {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        List<Long> staffIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<LocalDate> filterDates = new ArrayList<>();
        if (isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        List<Long> unitIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.UNIT_IDS, new ArrayList()));
        List<Long> employmentTypes = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.EMPLOYMENT_TYPE, new ArrayList()));
        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(), applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiDefaultData(staffEmploymentTypeDTO);
        List<Long> dayTypeIds = filterBasedCriteria.containsKey(FilterType.DAY_TYPE) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAY_TYPE)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.DAY_TYPE)) : defaultKpiDataDTO.getDayTypeDTOS().stream().map(DayTypeDTO::getId).collect(Collectors.toList());
        List<Long> timeSlotIds = filterBasedCriteria.containsKey(FilterType.TIME_SLOT) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_SLOT)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.TIME_SLOT)) : Arrays.asList(defaultKpiDataDTO.getTimeSlotDTOS().get(0).getId());
        Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap = defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        //filter staffids base on kpi filter rest call
        staffIds = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        if (!ObjectUtils.isCollectionEmpty(dayTypeIds)) {
            dayTypeIds.forEach(daysTypeId -> daysTypeIdAndDayTypeMap.get(daysTypeId).getValidDays().forEach(day -> {
                //TODO if remove Everyday from day enum then remove if statement and use dayOfWeek of java
                if (day.equals(Day.EVERYDAY)) {
                    daysOfWeek.addAll(newHashSet(DayOfWeek.values()));
                } else {
                    daysOfWeek.add(DayOfWeek.valueOf(day.toString()));
                }
            }));
        }
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        // filter staffIds if given staff has shift

        staffIds = shifts.stream().map(ShiftDTO::getStaffId).distinct().collect(Collectors.toList());
        TimeSlotDTO timeSlotDTO = defaultKpiDataDTO.getTimeSlotDTOS().stream().filter(timeSlotDto -> timeSlotIds.contains(timeSlotDto.getId())).findFirst().get();
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours = calculateDataByKpiRepresentation(staffIds,dateTimeIntervalListMap, dateTimeIntervals, applicableKPI, timeSlotDTO, dayTypeIds, daysTypeIdAndDayTypeMap,shifts);
        getKpiDataUnits(staffIdAndTotalHours, kpiDataUnits, applicableKPI, defaultKpiDataDTO.getStaffKpiFilterDTOs());
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getDayTypeAndTimeSlotHours(organizationId, filterBasedCriteria,null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getDayTypeAndTimeSlotHours(organizationId, filterBasedCriteria ,applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long, Number> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return null;
    }

    //use for return hours of timeslot
    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlotByStaff(List<Long> staffIds, List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<Long> dayTypeIds, Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours = new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shiftWithActivityDTOS.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap = staffShiftMapping.get(staffId).stream().collect(Collectors.groupingBy(k -> asLocalDate(k.getStartDate()), Collectors.toList()));
            staffIdAndTotalHours.putIfAbsent(staffId, new ArrayList<>());
            for (Long dayTypeId : dayTypeIds) {
                if (daysTypeIdAndDayTypeMap.get(dayTypeId).isHolidayType()) {
                    staffIdAndTotalHours.get(staffId).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfHolidayDayType(localDateShiftMap, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getCountryHolidayCalenderData())));
                } else {
                    staffIdAndTotalHours.get(staffId).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(staffShiftMapping.get(staffId), timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
                }
            }
        }
        return staffIdAndTotalHours;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlotByInterval(TimeSlotDTO timeSlotDTO, List<Long> dayTypeIds, Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap,List<DateTimeInterval> dateTimeIntervals,Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap , DurationType frequencyType) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> intervalAndTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap = dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(k -> asLocalDate(k.getStartDate()), Collectors.toList()));
            intervalAndTotalHours.putIfAbsent(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), new ArrayList<>());
            for (Long dayTypeId : dayTypeIds) {
                if (daysTypeIdAndDayTypeMap.get(dayTypeId).isHolidayType()) {
                    intervalAndTotalHours.get(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval)).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfHolidayDayType(localDateShiftMap, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getCountryHolidayCalenderData())));
                } else {
                    intervalAndTotalHours.get(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval)).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(dateTimeIntervalListMap.getOrDefault(dateTimeInterval,new ArrayList<>()), timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
                }
            }
        }
        return intervalAndTotalHours;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlotByTotalData(List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<Long> dayTypeIds, Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap, List<DateTimeInterval> dateTimeIntervals) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> intervalAndTotalHours = new HashMap<>();
        String dateTimeInterval=getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getStartDate()));
        intervalAndTotalHours.putIfAbsent(dateTimeInterval, new ArrayList<>());
            Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(k -> asLocalDate(k.getStartDate()), Collectors.toList()));
            for (Long dayTypeId : dayTypeIds) {
                if (daysTypeIdAndDayTypeMap.get(dayTypeId).isHolidayType()) {
                    intervalAndTotalHours.get(dateTimeInterval).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfHolidayDayType(localDateShiftMap, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getCountryHolidayCalenderData())));
                } else {
                    intervalAndTotalHours.get(dateTimeInterval).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(shiftWithActivityDTOS, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
                }
            }
        return intervalAndTotalHours;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, TimeSlotDTO timeSlotDTO, List<Long> dayTypeIds, Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap,List<ShiftWithActivityDTO> shifts){
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                objectListMap= getTotalHoursOfTimeSlotByStaff(staffIds,shifts,timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap);
                break;
            case REPRESENT_TOTAL_DATA:
                objectListMap = getTotalHoursOfTimeSlotByTotalData(shifts,timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap,dateTimeIntervals);
                break;
            case REPRESENT_PER_INTERVAL:
                objectListMap = getTotalHoursOfTimeSlotByInterval(timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap,dateTimeIntervals,dateTimeIntervalListMap,applicableKPI.getFrequencyType());
                break;
            default:
                objectListMap = getTotalHoursOfTimeSlotByInterval(timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap,dateTimeIntervals,dateTimeIntervalListMap,applicableKPI.getFrequencyType());
                break;
        }
        return objectListMap;

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
}
