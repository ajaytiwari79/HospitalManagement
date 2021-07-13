package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Day;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * pradeep
 * 20/5/19
 */
@Service
public class CounterHelperService {

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CountryHolidayCalenderService countryHolidayCalenderService;
    @Inject
    private CountryCalenderRepo countryCalenderRepo;
    @Inject
    private DayTypeRepository dayTypeRepository;
    @Inject
    private TimeSlotSetService timeSlotSetService;

    public Object[] getKPIdata(Map<FilterType, List> filterBasedCriteria,ApplicableKPI applicableKPI, List<LocalDate> filterDates, List<Long> staffIds, List<Long> employmentTypeIds, List<Long> unitIds, Long organizationId){
        List<DateTimeInterval> dateTimeIntervals = KPIUtils.getDateTimeIntervals(applicableKPI.getInterval(), ObjectUtils.isNull(applicableKPI) ? 0 : applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,applicableKPI.getDateForKPISetCalculation());
        List<Long> tagIds = KPIUtils.getLongValue(filterBasedCriteria.getOrDefault(FilterType.TAGS,new ArrayList<>()));
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString(),tagIds,filterBasedCriteria,true);
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Set<String> filterValues = (Set<String>)staffEmploymentTypeDTO.getFilterBasedCriteria().values().stream().flatMap(list -> list.stream()).map(value->value.toString()).collect(Collectors.toSet());
        if(filterValues.contains(XAxisConfig.VARIABLE_COST.toString())) {
            if(filterValues.contains(XAxisConfig.VARIABLE_COST.toString())) {
                List<DayTypeDTO> dayTypeDTOS = dayTypeRepository.findAllByCountryIdAndDeletedFalse(organizationId);
                for (StaffKpiFilterDTO kpiFilterQueryResult : staffKpiFilterDTOS) {
                    kpiFilterQueryResult.setDayTypeDTOS(dayTypeDTOS);
                }
            }
        }
        staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        return new Object[]{staffKpiFilterDTOS, dateTimeIntervals, staffIds};
    }


    public DefaultKpiDataDTO getKPIAllData(ApplicableKPI applicableKPI, List<LocalDate> filterDates, List<Long> staffIds, List<Long> employmentTypeIds, List<Long> unitIds, Long organizationId,List<Long> tagIds,Map<FilterType, List> filterBasedCriteria){
        List<DateTimeInterval> dateTimeIntervals = KPIUtils.getDateTimeIntervals(applicableKPI.getInterval(), ObjectUtils.isNull(applicableKPI) ? 0 : applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,applicableKPI.getDateForKPISetCalculation());
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString(),tagIds,filterBasedCriteria,true);
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiAllDefaultData(UserContext.getUserDetails().getCountryId(), staffEmploymentTypeDTO);
        defaultKpiDataDTO.setDateTimeIntervals(dateTimeIntervals);
        defaultKpiDataDTO.setHolidayCalenders(countryCalenderRepo.getAllByCountryIdAndHolidayDateBetween(UserContext.getUserDetails().getCountryId(),LocalDate.parse(staffEmploymentTypeDTO.getStartDate()), LocalDate.parse(staffEmploymentTypeDTO.getEndDate())));
        defaultKpiDataDTO.setTimeSlotDTOS(timeSlotSetService.getUnitTimeSlot(staffEmploymentTypeDTO.getOrganizationId()));
        return defaultKpiDataDTO;
    }

    public Object[] getDataByFilterCriteria(Map<FilterType, List> filterBasedCriteria){
        List staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null)&& ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.STAFF_IDS)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = new ArrayList<>();
        if (ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentTypeIds = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        Set<DayOfWeek> daysOfWeeks = filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK) && ObjectUtils.isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) ? KPIUtils.getDaysOfWeeksfromString(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) : ObjectUtils.newHashSet(DayOfWeek.values());
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        List<BigInteger> plannedTimeIds = (filterBasedCriteria.get(FilterType.PLANNED_TIME_TYPE) != null) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.PLANNED_TIME_TYPE) ): new ArrayList<>();
        return new Object[]{staffIds,filterDates,unitIds,employmentTypeIds,daysOfWeeks,shiftActivityStatus,plannedTimeIds};
    }

    public Set<DayOfWeek> getDayOfWeek(List<BigInteger> dayTypeIds,Map<BigInteger, DayTypeDTO> daysTypeIdAndDayTypeMap)
    {
        Set<DayOfWeek> daysOfWeek = new HashSet<>();

        if (!ObjectUtils.isCollectionEmpty(dayTypeIds)) {
            dayTypeIds.forEach(daysTypeId -> daysTypeIdAndDayTypeMap.get(daysTypeId).getValidDays().forEach(day -> {
                //TODO if remove Everyday from day enum then remove if statement and use dayOfWeek of java
                if (day.equals(Day.EVERYDAY)) {
                    daysOfWeek.addAll(ObjectUtils.newHashSet(DayOfWeek.values()));
                } else {
                    daysOfWeek.add(DayOfWeek.valueOf(day.toString()));
                }
            }));
        }
        return daysOfWeek;
    }

    public DefaultKpiDataDTO getDefaultDataForKPI(StaffEmploymentTypeDTO staffEmploymentTypeDTO){
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiAllDefaultData(staffEmploymentTypeDTO);
        defaultKpiDataDTO.setHolidayCalenders(countryCalenderRepo.getAllByCountryIdAndHolidayDateBetween(UserContext.getUserDetails().getCountryId(),LocalDate.parse(staffEmploymentTypeDTO.getStartDate()), LocalDate.parse(staffEmploymentTypeDTO.getEndDate())));
        defaultKpiDataDTO.setTimeSlotDTOS(timeSlotSetService.getUnitTimeSlot(staffEmploymentTypeDTO.getOrganizationId()));
        List<DayTypeDTO> dayTypeDTOS = dayTypeRepository.findAllByCountryIdAndDeletedFalse(staffEmploymentTypeDTO.getOrganizationId());
        defaultKpiDataDTO.setDayTypeDTOS(dayTypeDTOS);
        return defaultKpiDataDTO;
    }

}
