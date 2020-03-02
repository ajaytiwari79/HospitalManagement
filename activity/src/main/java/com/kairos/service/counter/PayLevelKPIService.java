package com.kairos.service.counter;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.DateUtils.startDateIsEqualsOrBeforeEndDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class PayLevelKPIService {

    public double getPayLevelOfMainEmploymentOfStaff(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,LocalDate selectedDate){
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        double payLevel=0.0d;
        for(StaffKpiFilterDTO staffKpiFilterDTO :staffKpiFilterDTOS){
               for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()) {
                   EmploymentLinesDTO currentEmploymentLine = getCurrentlyActiveLine(selectedDate, employmentWithCtaDetailsDTO.getEmploymentLines());
                   if(kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.EMPLOYMENT_SUB_TYPE)) {
                       if (kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).get(0).equals(EmploymentSubType.MAIN.name()) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).size()<2) {
                           if (ObjectUtils.isNotNull(currentEmploymentLine)) {
                               if (ObjectUtils.isNotNull(currentEmploymentLine.getEmploymentSubType()) && EmploymentSubType.MAIN.equals(currentEmploymentLine.getEmploymentSubType())) {
                                   payLevel += currentEmploymentLine.getPayGradeLevel();
                               }
                           }
                       } else if (kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).get(0).equals(EmploymentSubType.SECONDARY.name())&& kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).size()<2) {
                           if (ObjectUtils.isNotNull(currentEmploymentLine)) {
                               if (ObjectUtils.isNotNull(currentEmploymentLine.getEmploymentSubType()) && EmploymentSubType.SECONDARY.equals(currentEmploymentLine.getEmploymentSubType())) {
                                   payLevel += currentEmploymentLine.getPayGradeLevel();
                               }
                           }
                       }
                       else {
                           payLevel += currentEmploymentLine.getPayGradeLevel();
                       }
                   }
                       else {
                       if (ObjectUtils.isNotNull(currentEmploymentLine)) {
                               payLevel += currentEmploymentLine.getPayGradeLevel();
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
