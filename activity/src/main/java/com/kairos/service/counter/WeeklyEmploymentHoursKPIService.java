package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.dto.activity.counter.enums.XAxisConfig.HOURS;

@Service
public class WeeklyEmploymentHoursKPIService {

    public Double getWeeklyHoursOfEmployment(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, LocalDate startDate, LocalDate endDate) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(startDate,endDate);
        Double weeklyHours = 0.0d;
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            if (staffKpiFilterDTO.getId().equals(staffId)) {
                for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                    if (ObjectUtils.isNotNull(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getTotalWeeklyHours())) {
                        if (employmentWithCtaDetailsDTO.getStartDate().isBefore(startDate) && ObjectUtils.isNull(employmentWithCtaDetailsDTO.getEndDate())) {
                            weeklyHours += Double.valueOf(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getTotalWeeklyHours());
                        }

                        if (ObjectUtils.isNotNull(employmentWithCtaDetailsDTO.getEndDate())) {
                            if ((employmentWithCtaDetailsDTO.getStartDate().isBefore(startDate) && employmentWithCtaDetailsDTO.getEndDate().isAfter(startDate)) || dateTimeInterval.containsAndEqualsEndDate(DateUtils.asDate(employmentWithCtaDetailsDTO.getStartDate())) || dateTimeInterval.containsAndEqualsEndDate(DateUtils.asDate(employmentWithCtaDetailsDTO.getEndDate()))) {
                                weeklyHours += Double.valueOf(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getTotalWeeklyHours());
                            }
                        }
                    }

                }

            }

        }


            return weeklyHours;
    }
}
