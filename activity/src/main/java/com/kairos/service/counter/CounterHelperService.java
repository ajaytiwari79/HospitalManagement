package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.Day;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.counter.KPIUtils.getDateTimeIntervals;

/**
 * pradeep
 * 20/5/19
 */
@Service
public class CounterHelperService {

    @Inject
    private UserIntegrationService userIntegrationService;

    public Object[] getKPIdata(ApplicableKPI applicableKPI, List<LocalDate> filterDates, List<Long> staffIds, List<Long> employmentTypeIds, List<Long> unitIds, Long organizationId){
        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(), isNull(applicableKPI) ? 0 : applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,null);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        return new Object[]{staffKpiFilterDTOS,dateTimeIntervals,staffIds};
    }



    public Object[] getDataByFilterCriteria(Map<FilterType, List> filterBasedCriteria){
        List staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null)&& isCollectionNotEmpty(filterBasedCriteria.get(FilterType.STAFF_IDS)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = new ArrayList<>();
        if (isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentTypeIds = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        Set<DayOfWeek> daysOfWeeks = filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) ? KPIUtils.getDaysOfWeeksfromString(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) : newHashSet(DayOfWeek.values());
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        return new Object[]{staffIds,filterDates,unitIds,employmentTypeIds,daysOfWeeks,shiftActivityStatus};
    }
    public Set<DayOfWeek> getDayOfWeek(List<Long> dayTypeIds,Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap)
    {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();

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
        return daysOfWeek;
    }



}
