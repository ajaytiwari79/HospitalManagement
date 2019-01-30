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
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.Day;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

@Service
public class DayTypeAndTimeSlotKpiService implements CounterService {
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;


    // use for calculate hours of given days of daytype
    private Double getTotalHoursOfDayType(List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<Day> days) {
        Long totalMilliSeconds = 0l;
        //TODO  when remove Everyday from day enum then remove if statement and use dayOfWeek of java
        if (days.get(0).equals(Day.EVERYDAY)) {
            days.addAll(newHashSet(Day.values()));
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

    //use for return hours of timeslot
    private Map<Long, List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlot(List<Long> staffIds, List<ShiftWithActivityDTO> shiftWithActivityDTOS, TimeSlotDTO timeSlotDTO, List<Long> dayTypeIds, Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap) {
        Map<Long, List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours = new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shiftWithActivityDTOS.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        staffIds.forEach(staffId -> {
            staffIdAndTotalHours.put(staffId, new ArrayList<>());
            dayTypeIds.forEach(dayTypeId -> {
                staffIdAndTotalHours.get(staffId).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDayTypeMap.get(dayTypeId).getName(), daysTypeIdAndDayTypeMap.get(dayTypeId).getColorCode(), getTotalHoursOfDayType(staffShiftMapping.get(staffId), timeSlotDTO, daysTypeIdAndDayTypeMap.get(dayTypeId).getValidDays())));
            });
        });
        return staffIdAndTotalHours;
    }


    private List<CommonKpiDataUnit> getDayTypeAndTimeSlotHours(Long organizationId, Map<FilterType, List> filterBasedCriteria) {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        List<Long> staffIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<LocalDate> filterDates = filterBasedCriteria.getOrDefault(FilterType.TIME_INTERVAL, Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek()));
        List<Long> unitIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.UNIT_IDS, new ArrayList()));
        List<Long> employmentTypes = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.EMPLOYMENT_TYPE, new ArrayList()));
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        DefaultKpiDataDTO defaultKpiDataDTO = genericIntegrationService.getKpiDefaultData(staffEmploymentTypeDTO);
        List<Long> dayTypeIds = filterBasedCriteria.containsKey(FilterType.DAY_TYPE) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.DAY_TYPE)) : defaultKpiDataDTO.getDayTypeDTOS().stream().map(dayTypeDTO -> dayTypeDTO.getId()).collect(Collectors.toList());
        List<Long> timeSlotIds = filterBasedCriteria.containsKey(FilterType.TIME_SLOT) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.TIME_SLOT)) : Arrays.asList(defaultKpiDataDTO.getTimeSlotDTOS().get(0).getId());
        Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap = defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        //filter staffids base on kpi filter rest call
        staffIds = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList());
        if (!ObjectUtils.isCollectionEmpty(dayTypeIds)) {
            dayTypeIds.forEach(daysTypeId -> {
                daysTypeIdAndDayTypeMap.get(daysTypeId).getValidDays().forEach(day -> {
                    //TODO if remove Everyday from day enum then remove if statement and use dayOfWeek of java
                    if (day.equals(Day.EVERYDAY)) {
                        daysOfWeek.addAll(newHashSet(DayOfWeek.values()));
                    } else {
                        daysOfWeek.add(DayOfWeek.valueOf(day.toString()));
                    }
                });
            });
        }
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, new ArrayList<>(), dayOfWeeksNo, DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
        // filter staffIds if given staff has shift
        staffIds = shiftWithActivityDTOS.stream().map(shiftWithActivityDTO -> shiftWithActivityDTO.getStaffId()).distinct().collect(Collectors.toList());
        Map<Long, String> staffIdAndNameMap = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        //TODO change start time and end time when use filter currently we only take day time
        TimeSlotDTO timeSlotDTO = defaultKpiDataDTO.getTimeSlotDTOS().stream().filter(timeSlotDto -> timeSlotIds.contains(timeSlotDto.getId())).findFirst().get();
        Map<Long, List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours = getTotalHoursOfTimeSlot(staffIds, shiftWithActivityDTOS, timeSlotDTO, dayTypeIds, daysTypeIdAndDayTypeMap);
        staffIdAndTotalHours.keySet().forEach(s -> kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(s), 0d, staffIdAndTotalHours.get(s))));
        return kpiDataUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getDayTypeAndTimeSlotHours(organizationId, filterBasedCriteria);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUEFIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getDayTypeAndTimeSlotHours(organizationId, filterBasedCriteria);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList,new KPIAxisData(AppConstants.STAFF,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUEFIELD));
    }
}
