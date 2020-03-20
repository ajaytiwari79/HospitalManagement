package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class WeeklyEmploymentHoursKPIService {

    public double getWeeklyHoursOfEmployment(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if (isNotNull(kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation())) {
            LocalDate startDate = kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
            LocalDate endDate = kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
            return getWeeklyHoursOfEmployment(staffId, kpiCalculationRelatedInfo, startDate, endDate);
        } else {
            return getWeeklyHoursOfEmployment(staffId, kpiCalculationRelatedInfo, asLocalDate(kpiCalculationRelatedInfo.getStartDate()), asLocalDate(kpiCalculationRelatedInfo.getEndDate()));
        }
    }

    public Double getWeeklyHoursOfEmployment(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, LocalDate startDate, LocalDate endDate) {
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(startDate, endDate);
        Double weeklyHours = 0.0d;
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                weeklyHours = getWeeklyHours(kpiCalculationRelatedInfo, startDate, dateTimeInterval, weeklyHours, employmentWithCtaDetailsDTO);

            }
        }

         return weeklyHours;
        }

    private Double getWeeklyHours(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, LocalDate startDate, DateTimeInterval dateTimeInterval, Double weeklyHours, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        if (kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.EMPLOYMENT_SUB_TYPE)) {
            if(isNotNull(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentSubType())) {
                weeklyHours = getWeeklyHoursByEmploymentSubType(kpiCalculationRelatedInfo, startDate, dateTimeInterval, weeklyHours, employmentWithCtaDetailsDTO);
            }
    }
    else {
        weeklyHours = getWeeklyHours(startDate, dateTimeInterval, weeklyHours, employmentWithCtaDetailsDTO);
    }
        return weeklyHours;
    }

    private Double getWeeklyHoursByEmploymentSubType(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, LocalDate startDate, DateTimeInterval dateTimeInterval, Double weeklyHours, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        if (kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).get(0).equals(EmploymentSubType.MAIN.name()) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).size() < 2) {
            if (employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentSubType().equals(EmploymentSubType.MAIN)) {
                weeklyHours = getWeeklyHours(startDate, dateTimeInterval, weeklyHours, employmentWithCtaDetailsDTO);
            }
        } else if (kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).get(0).equals(EmploymentSubType.SECONDARY.name()) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).size() < 2) {
            if (employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentSubType().equals(EmploymentSubType.SECONDARY)) {
                weeklyHours = getWeeklyHours(startDate, dateTimeInterval, weeklyHours, employmentWithCtaDetailsDTO);
            }
        } else {
            weeklyHours = getWeeklyHours(startDate, dateTimeInterval, weeklyHours, employmentWithCtaDetailsDTO);
        }
        return weeklyHours;
    }

    private Double getWeeklyHours(LocalDate startDate, DateTimeInterval dateTimeInterval, Double weeklyHours, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        if (ObjectUtils.isNotNull(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getTotalWeeklyHours())) {
            if ((employmentWithCtaDetailsDTO.getStartDate().isBefore(startDate) && ObjectUtils.isNull(employmentWithCtaDetailsDTO.getEndDate())) || dateTimeInterval.contains(employmentWithCtaDetailsDTO.getStartDate())) {
                weeklyHours += Double.valueOf(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getTotalWeeklyHours());
            }
            if (ObjectUtils.isNotNull(employmentWithCtaDetailsDTO.getEndDate()) && ((employmentWithCtaDetailsDTO.getStartDate().isBefore(startDate) && employmentWithCtaDetailsDTO.getEndDate().isAfter(startDate)) || dateTimeInterval.containsAndEqualsEndDate(DateUtils.asDate(employmentWithCtaDetailsDTO.getStartDate())) || dateTimeInterval.containsAndEqualsEndDate(DateUtils.asDate(employmentWithCtaDetailsDTO.getEndDate())))) {
                weeklyHours += Double.valueOf(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getTotalWeeklyHours());
            }
        }
        return weeklyHours;
    }

}
