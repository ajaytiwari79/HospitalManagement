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
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.commons.utils.DateUtils.asLocalDate;

@Service
public class ShiftAndActivityDurationKpiService implements  CounterService {

    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    private List<CommonKpiDataUnit> calculateDurationOfShiftAndActivity(List<ShiftWithActivityDTO> shiftWithActivityDTOS,LocalDate startDate,LocalDate endDate) {
        List<CommonKpiDataUnit> clusteredBarChartKpiDataUnits = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(shiftWithActivityDTOS)) {
            Map<String,String> activityNameAndColorCodeMap=new HashMap<>();
            Map<LocalDate, List<ShiftWithActivityDTO>> dateAndShiftWithActivityMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(t -> asLocalDate(t.getStartDate()), Collectors.toList()));
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                Double shiftDurationMinutes=0.0;
                List<ClusteredBarChartKpiDataUnit> subClusteredBarValue=new ArrayList<>();
                Map<String,Integer> activityNameAndTotalDurationMinutesMap=new HashMap<>();
                List<ShiftWithActivityDTO> shiftWithActivityDTO = dateAndShiftWithActivityMap.get(startDate);
                if(CollectionUtils.isNotEmpty(shiftWithActivityDTO)) {
                    for (ShiftWithActivityDTO shift : shiftWithActivityDTO) {
                        shift.getActivities().forEach(activity -> {
                            int activityDuration = activityNameAndTotalDurationMinutesMap.getOrDefault(activity.getActivityName(),0);
                            activityNameAndTotalDurationMinutesMap.put(activity.getActivityName(), activityDuration + activity.getDurationMinutes());
                            activityNameAndColorCodeMap.putIfAbsent(activity.getActivityName(),activity.getBackgroundColor());
                        });
                        shiftDurationMinutes=shiftDurationMinutes+shift.getDurationMinutes();
                    }
                }
                activityNameAndTotalDurationMinutesMap.keySet().forEach(s -> subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(s,activityNameAndColorCodeMap.get(s),DateUtils.getHoursByMinutes(activityNameAndTotalDurationMinutesMap.get(s)))));
                clusteredBarChartKpiDataUnits.add(new ClusteredBarChartKpiDataUnit(startDate.toString(),DateUtils.getHoursByMinutes(shiftDurationMinutes),subClusteredBarValue));
                startDate = startDate.plusDays(1);
            }
        }
        return clusteredBarChartKpiDataUnits;
    }

    private List<CommonKpiDataUnit> getDurationOfShiftAndActivity(Long organizationId, Map<FilterType, List> filterBasedCriteria){
        List<DayOfWeek> daysOfWeek=filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK)?KPIUtils.getDaysOfWeekOfString(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)): Stream.of(DayOfWeek.values()).map(dayOfWeek -> DayOfWeek.valueOf(dayOfWeek.toString())).collect(Collectors.toList());
        List<Long> staffIds=filterBasedCriteria.containsKey(FilterType.STAFF_IDS)? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)):new ArrayList<>();
        List<LocalDate> filterDates = filterBasedCriteria.containsKey(FilterType.TIME_INTERVAL)? filterBasedCriteria.get(FilterType.TIME_INTERVAL): Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        List<BigInteger> activitiesIds = filterBasedCriteria.containsKey(FilterType.ACTIVITY_IDS)? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.ACTIVITY_IDS)):new ArrayList();
        List<Long> employmentTypes =filterBasedCriteria.containsKey(FilterType.EMPLOYMENT_TYPE)?KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)): new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(staffIds,new ArrayList<>(),employmentTypes,organizationId,filterDates.get(0).toString(),filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS=genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        staffIds=staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList());
        List<Integer> dayOfWeeksNo=new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> {
           dayOfWeeksNo.add((dayOfWeek.getValue()<7)?dayOfWeek.getValue()+1:1);
        });
        List<ShiftWithActivityDTO> shiftWithActivityDTOS=shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds,activitiesIds,dayOfWeeksNo,DateUtils.asDate(filterDates.get(0)),DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
        return calculateDurationOfShiftAndActivity(shiftWithActivityDTOS,filterDates.get(0),filterDates.get(1));
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList= getDurationOfShiftAndActivity(organizationId,filterBasedCriteria);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS,AppConstants.YAXIS);
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList= getDurationOfShiftAndActivity(organizationId,filterBasedCriteria);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS,AppConstants.YAXIS);
    }
}
