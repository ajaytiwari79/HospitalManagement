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
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
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
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.persistence.model.FibonacciKPICalculation;
import com.kairos.persistence.model.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DayTypeAndTimeSlotKpiService implements CounterService {
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private CounterHelperService counterHelperService;

    // use for calculate hours of given days of daytype
    private Double getTotalHoursOfDayType(List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<Day> days) {
        Long totalMilliSeconds = 0L;
        //TODO  when remove Everyday from day enum then remove if statement and use dayOfWeek of java
        if (days.get(0).equals(Day.EVERYDAY)) {
            days.addAll(ObjectUtils.newHashSet(Day.values()));
        }
        LocalTime startTime = LocalTime.of(timeSlotDTO.getStartHour(), timeSlotDTO.getStartMinute());
        LocalTime endTime = LocalTime.of(timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute());
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            DateTimeInterval dateTimeInterval = new DateTimeInterval(DateUtils.getLongFromLocalDateimeTime(LocalDateTime.of(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()), startTime)), DateUtils.getLongFromLocalDateimeTime(LocalDateTime.of(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()), endTime)));
            if (AppConstants.NIGHT.equals(timeSlotDTO.getName())) {
                dateTimeInterval = new DateTimeInterval(DateUtils.getLongFromLocalDateimeTime(LocalDateTime.of(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()), startTime)), DateUtils.getLongFromLocalDateimeTime(LocalDateTime.of(DateUtils.asLocalDate(shiftWithActivityDTO.getEndDate()), endTime)));
            }
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
            if (days.stream().anyMatch(day -> day.toString().equals(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()).getDayOfWeek().toString()))) {
                totalMilliSeconds += (dateTimeInterval.overlap(shiftInterval)) != null ? dateTimeInterval.overlap(shiftInterval).getMilliSeconds() : 0;
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalMilliSeconds);
    }

    private Double getTotalHoursOfHolidayDayType(Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap, TimeSlotDTO timeSlotDTO, List<CountryHolidayCalenderDTO> countryHolidayCalenderDTOS) {
        Long totalMilliSeconds = 0L;
        LocalTime startTime = LocalTime.of(timeSlotDTO.getStartHour(), timeSlotDTO.getStartMinute());
        LocalTime endTime = LocalTime.of(timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute());
        if (ObjectUtils.isCollectionNotEmpty(countryHolidayCalenderDTOS)) {
            for (CountryHolidayCalenderDTO countryHolidayCalenderDTO : countryHolidayCalenderDTOS) {
                if (localDateShiftMap.containsKey(countryHolidayCalenderDTO.getHolidayDate())) {
                    totalMilliSeconds = getTotalHours(localDateShiftMap, timeSlotDTO, totalMilliSeconds, startTime, endTime, countryHolidayCalenderDTO);
                }
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalMilliSeconds);
    }

    private Long getTotalHours(Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap, TimeSlotDTO timeSlotDTO, Long totalMilliSeconds, LocalTime startTime, LocalTime endTime, CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        for (ShiftWithActivityDTO shiftWithActivityDTO : localDateShiftMap.get(countryHolidayCalenderDTO.getHolidayDate())) {
            DateTimeInterval dateTimeInterval;
            dateTimeInterval = getDateTimeIntervalByTimeSlot(timeSlotDTO, startTime, endTime, shiftWithActivityDTO);
            Date startDateOfHolidayType = ObjectUtils.isNotNull(countryHolidayCalenderDTO.getStartTime()) ? DateUtils.asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getStartTime()) : DateUtils.asDate(countryHolidayCalenderDTO.getHolidayDate());
            Date endDateOfHolidayType = ObjectUtils.isNotNull(countryHolidayCalenderDTO.getEndTime()) ? DateUtils.asDate(countryHolidayCalenderDTO.getHolidayDate(), countryHolidayCalenderDTO.getEndTime()) : DateUtils.asDateEndOfDay(countryHolidayCalenderDTO.getHolidayDate());
            endDateOfHolidayType = (startDateOfHolidayType.after(endDateOfHolidayType)) ? DateUtils.getStartOfDay(DateUtils.asDate(countryHolidayCalenderDTO.getHolidayDate().plusDays(1))) : endDateOfHolidayType;
            DateTimeInterval dateTimeIntervalOfHolidayType = new DateTimeInterval(startDateOfHolidayType, endDateOfHolidayType).overlap(dateTimeInterval);
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
            if (ObjectUtils.isNotNull(dateTimeIntervalOfHolidayType) && dateTimeIntervalOfHolidayType.overlaps(shiftInterval)) {
                totalMilliSeconds += dateTimeIntervalOfHolidayType.overlap(shiftInterval).getMilliSeconds();
            }
        }
        return totalMilliSeconds;
    }

    private DateTimeInterval getDateTimeIntervalByTimeSlot(TimeSlotDTO timeSlotDTO, LocalTime startTime, LocalTime endTime, ShiftWithActivityDTO shiftWithActivityDTO) {
        DateTimeInterval dateTimeInterval;
        if (AppConstants.NIGHT.equals(timeSlotDTO.getName())) {
            LocalDate endDate = DateUtils.asLocalDate(shiftWithActivityDTO.getEndDate()).equals(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate())) ? DateUtils.asLocalDate(shiftWithActivityDTO.getEndDate()).plusDays(1) : DateUtils.asLocalDate(shiftWithActivityDTO.getEndDate());
            dateTimeInterval = new DateTimeInterval(DateUtils.asDate(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()), startTime), DateUtils.asDate(endDate, endTime));
        } else {
            dateTimeInterval = new DateTimeInterval(DateUtils.asDate(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()), startTime), DateUtils.asDate(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()), endTime));
        }
        return dateTimeInterval;
    }


    private List<CommonKpiDataUnit> getDayTypeAndTimeSlotHours(Long organizationId, Map<FilterType, List> filterBasedCriteria,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
        List<Long> unitIds = (List<Long>)filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>)filterCriteria[3];
        List<DateTimeInterval> dateTimeIntervals = KPIUtils.getDateTimeIntervals(applicableKPI.getInterval(), applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,null);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString(),new ArrayList<>(),filterBasedCriteria,true);
        DefaultKpiDataDTO defaultKpiDataDTO = counterHelperService.getDefaultDataForKPI(staffEmploymentTypeDTO);
        //filter staffids base on kpi filter rest call
        staffIds = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        List<BigInteger> dayTypeIds = filterBasedCriteria.containsKey(FilterType.DAY_TYPE) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAY_TYPE)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.DAY_TYPE)) : defaultKpiDataDTO.getDayTypeDTOS().stream().map(DayTypeDTO::getId).collect(Collectors.toList());
        Set<BigInteger> timeSlotIds = filterBasedCriteria.containsKey(FilterType.TIME_SLOT) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_SLOT)) ? KPIUtils.getBigIntegerSet(filterBasedCriteria.get(FilterType.TIME_SLOT)) : ObjectUtils.newHashSet(defaultKpiDataDTO.getTimeSlotDTOS().get(0).getId());
        Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap = defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        Set<DayOfWeek> daysOfWeek = counterHelperService.getDayOfWeek(dayTypeIds,daysTypeIdAndDayTypeMap);
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, ObjectUtils.isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), dayOfWeeksNo, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(),null);
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        // filter staffIds if given staff has shift
        staffIds = shifts.stream().map(ShiftDTO::getStaffId).distinct().collect(Collectors.toList());
        TimeSlotDTO timeSlotDTO = defaultKpiDataDTO.getTimeSlotDTOS().stream().filter(timeSlotDto -> timeSlotIds.contains(timeSlotDto.getId())).findFirst().get();
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours = calculateDataByKpiRepresentation(staffIds,dateTimeIntervalListMap, dateTimeIntervals, applicableKPI, timeSlotDTO, dayTypeIds, daysTypeIdAndDayTypeMap,shifts);
        KPIUtils.getKpiDataUnits(staffIdAndTotalHours, kpiDataUnits, applicableKPI, defaultKpiDataDTO.getStaffKpiFilterDTOs());
        KPIUtils.sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getDayTypeAndTimeSlotHours(organizationId, filterBasedCriteria,null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getDayTypeAndTimeSlotHours(organizationId, filterBasedCriteria ,applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    //use for return hours of timeslot
    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlotByStaff(List<Long> staffIds, List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<BigInteger> dayTypeIds, Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours = new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shiftWithActivityDTOS.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap = staffShiftMapping.get(staffId).stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
            staffIdAndTotalHours.putIfAbsent(staffId, new ArrayList<>());
            for (BigInteger dayTypeId : dayTypeIds) {
                if (daysTypeIdAndDayTypeMap.get(dayTypeId).isHolidayType()) {
                    staffIdAndTotalHours.get(staffId).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfHolidayDayType(localDateShiftMap, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getCountryHolidayCalenderData())));
                } else {
                    staffIdAndTotalHours.get(staffId).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(staffShiftMapping.get(staffId), timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
                }
            }
        }
        return staffIdAndTotalHours;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlotByInterval(TimeSlotDTO timeSlotDTO, List<BigInteger> dayTypeIds, Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap,List<DateTimeInterval> dateTimeIntervals,Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap , DurationType frequencyType) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> intervalAndTotalHours = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            getHoursByDayType(timeSlotDTO, dayTypeIds, daysTypeIdAndDayTypeMap, dateTimeIntervalListMap, frequencyType, intervalAndTotalHours, dateTimeInterval);
        }
        return intervalAndTotalHours;
    }

    private void getHoursByDayType(TimeSlotDTO timeSlotDTO, List<BigInteger> dayTypeIds, Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, DurationType frequencyType, Map<Object, List<ClusteredBarChartKpiDataUnit>> intervalAndTotalHours, DateTimeInterval dateTimeInterval) {
        Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap = dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        intervalAndTotalHours.putIfAbsent(DurationType.DAYS.equals(frequencyType) ? DateUtils.getStartDateTimeintervalString(dateTimeInterval) : DateUtils.getDateTimeintervalString(dateTimeInterval), new ArrayList<>());
        for (BigInteger dayTypeId : dayTypeIds) {
            if (daysTypeIdAndDayTypeMap.get(dayTypeId).isHolidayType()) {
                intervalAndTotalHours.get(DurationType.DAYS.equals(frequencyType) ? DateUtils.getStartDateTimeintervalString(dateTimeInterval) : DateUtils.getDateTimeintervalString(dateTimeInterval)).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfHolidayDayType(localDateShiftMap, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getCountryHolidayCalenderData())));
            } else {
                intervalAndTotalHours.get(DurationType.DAYS.equals(frequencyType) ? DateUtils.getStartDateTimeintervalString(dateTimeInterval) : DateUtils.getDateTimeintervalString(dateTimeInterval)).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(dateTimeIntervalListMap.getOrDefault(dateTimeInterval,new ArrayList<>()), timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
            }
        }
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlotByTotalData(List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<BigInteger> dayTypeIds, Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap, List<DateTimeInterval> dateTimeIntervals) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> intervalAndTotalHours = new HashMap<>();
        String dateTimeInterval= DateUtils.getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate()));
        intervalAndTotalHours.putIfAbsent(dateTimeInterval, new ArrayList<>());
            Map<LocalDate, List<ShiftWithActivityDTO>> localDateShiftMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
            for (BigInteger dayTypeId : dayTypeIds) {
                if (daysTypeIdAndDayTypeMap.get(dayTypeId).isHolidayType()) {
                    intervalAndTotalHours.get(dateTimeInterval).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfHolidayDayType(localDateShiftMap, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getCountryHolidayCalenderData())));
                } else {
                    intervalAndTotalHours.get(dateTimeInterval).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(shiftWithActivityDTOS, timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
                }
            }
        return intervalAndTotalHours;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, TimeSlotDTO timeSlotDTO, List<BigInteger> dayTypeIds, Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap,List<ShiftWithActivityDTO> shifts){
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectListMap;
        switch (applicableKPI.getKpiRepresentation()) {
            case KPIRepresentation.REPRESENT_PER_STAFF:
                objectListMap= getTotalHoursOfTimeSlotByStaff(staffIds,shifts,timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap);
                break;
            case KPIRepresentation.REPRESENT_TOTAL_DATA:
                objectListMap = getTotalHoursOfTimeSlotByTotalData(shifts,timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap,dateTimeIntervals);
                break;
            case KPIRepresentation.REPRESENT_PER_INTERVAL:
                objectListMap = getTotalHoursOfTimeSlotByInterval(timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap,dateTimeIntervals,dateTimeIntervalListMap,applicableKPI.getFrequencyType());
                break;
            default:
                objectListMap = getTotalHoursOfTimeSlotByInterval(timeSlotDTO,dayTypeIds,daysTypeIdAndDayTypeMap,dateTimeIntervals,dateTimeIntervalListMap,applicableKPI.getFrequencyType());
                break;
        }
        return KPIUtils.verifyKPIResponseListData(objectListMap) ? objectListMap : new HashMap<>();

    }


    public KPISetResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI){
         return  new KPISetResponseDTO();
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, KPI kpi,ApplicableKPI applicableKPI) {
        return new TreeSet<>();
    }

}
