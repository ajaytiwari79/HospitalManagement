package com.kairos.service.counter;

import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayLevelKPIService {

    @Inject
    private UserIntegrationService userIntegrationService;

    public double getTotalSumOfPayLevelOfAllEmploymentOfStaff(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, LocalDate selectedDate){
        List<StaffKpiFilterDTO> staffKpiFilterDTOS =kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        double totalSumOfPayLevel=0.0d;
        for(StaffKpiFilterDTO staffKpiFilterDTO :staffKpiFilterDTOS){
            List<Long> employmentIds =staffKpiFilterDTO.getEmployment().stream().map(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getId()).collect(Collectors.toList());
            totalSumOfPayLevel =userIntegrationService.getTotalSumOfPayLevel(UserContext.getUserDetails().getCountryId(),employmentIds,selectedDate);

        }
        return totalSumOfPayLevel;
    }

}
