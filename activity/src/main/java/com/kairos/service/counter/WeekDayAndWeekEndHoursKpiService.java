package com.kairos.service.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

@Service
public class WeekDayAndWeekEndHoursKpiService implements CounterService {
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;


    private List<CommonKpiDataUnit> getWeekDayAndWeekEndHours(Long organizationId, Map<FilterType, List> filterBasedCriteria) {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        List<Long> staffIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<LocalDate> filterDates = filterBasedCriteria.getOrDefault(FilterType.TIME_INTERVAL, Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek()));
        //List<BigInteger> activitiesIds = KPIUtils.getBigIntegerValue(filterBasedCriteria.getOrDefault(FilterType.ACTIVITY_IDS, new ArrayList<>()));
        List<Long> unitIds =  KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.UNIT_IDS,new ArrayList()));
        List<Long> employmentTypes = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.EMPLOYMENT_TYPE, new ArrayList()));
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypes, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        DefaultKpiDataDTO defaultKpiDataDTO = genericIntegrationService.getKpiDefaultData(staffEmploymentTypeDTO);
        staffIds = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList());
        Map<Long,List<Day>> daysTypeIdAndDaysMap=defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(k->k.getId(),v->v.getValidDays()));
        filterBasedCriteria.get(FilterType.DAY_TYPE).forEach(daysType->{
            daysTypeIdAndDaysMap.get(daysType).forEach(day -> {
                daysOfWeek.add(DayOfWeek.valueOf(day.toString()));
            });
        });
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, new ArrayList<>(), dayOfWeeksNo, DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
        return new ArrayList<>();
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
