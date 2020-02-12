package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.DateUtils.startDateIsEqualsOrBeforeEndDate;

@Service
public class PayLevelKPIService {

    public double getPayLevelOfMainEmploymentOfStaff(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,LocalDate selectedDate){
        List<StaffKpiFilterDTO> staffKpiFilterDTOS =kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        double payLevel=0.0d;
        for(StaffKpiFilterDTO staffKpiFilterDTO :staffKpiFilterDTOS){
            if(staffId.equals(staffKpiFilterDTO.getId())) {
               for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()) {
                   EmploymentLinesDTO currentEmploymentLine = getCurrentlyActiveLine(selectedDate, employmentWithCtaDetailsDTO.getEmploymentLines());
                   if (ObjectUtils.isNotNull(currentEmploymentLine)) {
                       if (ObjectUtils.isNotNull(currentEmploymentLine.getEmploymentSubType()) && EmploymentSubType.MAIN.equals(currentEmploymentLine.getEmploymentSubType())) {
                           payLevel = currentEmploymentLine.getPayGradeLevel();
                       }
                   }
               }

            }
        }

        return payLevel;
    }

    public EmploymentLinesDTO getCurrentlyActiveLine(LocalDate selectedDate,List<EmploymentLinesDTO> employmentLines) {
        selectedDate = selectedDate == null ? getCurrentLocalDate() : selectedDate;
        EmploymentLinesDTO currentEmploymentLine = null;
        for (EmploymentLinesDTO employmentLinesDTO : employmentLines) {
            if (startDateIsEqualsOrBeforeEndDate(employmentLinesDTO.getStartDate(),selectedDate ) &&
                    (employmentLinesDTO.getEndDate() == null || startDateIsEqualsOrBeforeEndDate(selectedDate, employmentLinesDTO.getEndDate()))) {
                currentEmploymentLine = employmentLinesDTO;
                break;
            }
        }
        return currentEmploymentLine;
    }

}
