package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
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
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

@Service
public class WeekDayAndWeekEndHoursKpiService implements CounterService {
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    private Double getTotalHoursOfDayType(List<ShiftWithActivityDTO> shiftWithActivityDTOS,LocalTime startTime, LocalTime endTime,List<Day> days){
        Long totalHours=0l;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            DateTimeInterval dateTimeInterval = new DateTimeInterval(DateUtils.getLongFromLocalDateimeTime(LocalDateTime.of(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()),startTime)),DateUtils.getLongFromLocalDateimeTime(LocalDateTime.of(DateUtils.asLocalDate(shiftWithActivityDTO.getEndDate()),endTime)));
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftWithActivityDTO.getStartDate(), shiftWithActivityDTO.getEndDate());
            if(days.stream().anyMatch(day -> day.toString().equals(DateUtils.asLocalDate(shiftWithActivityDTO.getStartDate()).getDayOfWeek().toString()))){
                totalHours+=dateTimeInterval.overlap(shiftInterval).getMilliSeconds();
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalHours);
    }

    private Map<Long,List<ClusteredBarChartKpiDataUnit>> getTotalHoursOfTimeSlot(List<Long> staffIds,List<ShiftWithActivityDTO> shiftWithActivityDTOS, LocalTime startDate, LocalTime endDate, List<Long> dayTypeIds, Map<Long,List<Day>> daysTypeIdAndDaysMap,Map<Long,String> dayTypeIdAndColorCode){
        List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits=new ArrayList<>();
        Map<Long,List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours=new HashMap<>();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shiftWithActivityDTOS.parallelStream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        staffIds.forEach(staffId->{
            dayTypeIds.forEach(dayTypeId->{
                if(staffIdAndTotalHours.containsKey(staffId)&&staffShiftMapping.containsKey(staffId)) {
                    staffIdAndTotalHours.get(staffId).add(new ClusteredBarChartKpiDataUnit(daysTypeIdAndDaysMap.get(dayTypeId).get(0).toString(),dayTypeIdAndColorCode.get(dayTypeId),getTotalHoursOfDayType(staffShiftMapping.get(staffId), startDate, endDate, daysTypeIdAndDaysMap.get(dayTypeId))));
                }else{
                    staffIdAndTotalHours.put(staffId,clusteredBarChartKpiDataUnits);
                }
            });
        });
        return staffIdAndTotalHours;
    }


    private List<CommonKpiDataUnit> getWeekDayAndWeekEndHours(Long organizationId, Map<FilterType, List> filterBasedCriteria) {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        List<Long> staffIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<Long> dayTypeIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.DAY_TYPE, new ArrayList<>()));
        List<LocalDate> filterDates = filterBasedCriteria.getOrDefault(FilterType.TIME_INTERVAL, Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek()));
        List<Long> unitIds =  KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.UNIT_IDS,new ArrayList()));
        List<Long> employmentTypes = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.EMPLOYMENT_TYPE, new ArrayList()));
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        DefaultKpiDataDTO defaultKpiDataDTO = genericIntegrationService.getKpiDefaultData(staffEmploymentTypeDTO);
        dayTypeIds.addAll( defaultKpiDataDTO.getDayTypeDTOS().stream().map(dayTypeDTO -> dayTypeDTO.getId()).collect(Collectors.toList()));
        Map<Long,List<Day>> daysTypeIdAndDaysMap=defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(k->k.getId(),v->v.getValidDays()));
        Map<Long,String> dayTypeIdAndColorCode=defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(k->k.getId(),v->v.getColorCode()));
        staffIds = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList());
        if(filterBasedCriteria.containsKey(FilterType.DAY_TYPE))
        {
            filterBasedCriteria.get(FilterType.DAY_TYPE).forEach(daysType -> {
                daysTypeIdAndDaysMap.get(daysType).forEach(day -> {
                    if (day.equals(Day.EVERYDAY)) {
                        daysOfWeek.addAll(newHashSet(DayOfWeek.values()));
                    } else {
                        daysOfWeek.add(DayOfWeek.valueOf(day.toString()));
                    }
                });
            });
        }else{
            daysOfWeek.addAll(newHashSet(DayOfWeek.values()));
        }
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, new ArrayList<>(), dayOfWeeksNo, DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
        Map<Long, String> staffIdAndNameMap = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        Map<Long,List<ClusteredBarChartKpiDataUnit>> staffIdAndTotalHours=getTotalHoursOfTimeSlot(staffIds,shiftWithActivityDTOS,LocalTime.of(defaultKpiDataDTO.getTimeSlotDTOS().get(0).getStartHour(),defaultKpiDataDTO.getTimeSlotDTOS().get(0).getStartMinute()),LocalTime.of(defaultKpiDataDTO.getTimeSlotDTOS().get(0).getEndHour(),defaultKpiDataDTO.getTimeSlotDTOS().get(0).getEndMinute()),dayTypeIds,daysTypeIdAndDaysMap,dayTypeIdAndColorCode);
        staffIdAndTotalHours.keySet().forEach(s->kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(s),0d,staffIdAndTotalHours.get(s))));
        return kpiDataUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getWeekDayAndWeekEndHours(organizationId, filterBasedCriteria);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS, AppConstants.YAXIS);
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getWeekDayAndWeekEndHours(organizationId, filterBasedCriteria);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS, AppConstants.YAXIS);
    }
}
