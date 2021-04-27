package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
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

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class PayLevelKPIService implements KPIService{

    public double getPayLevelGradeOfMainEmploymentOfStaff(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if(isNotNull(kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation())){
            LocalDate startDate =kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
            return getPayLevelOfMainEmploymentOfStaff(staffId,kpiCalculationRelatedInfo,startDate);
        }else {
            return getPayLevelOfMainEmploymentOfStaff(staffId,kpiCalculationRelatedInfo,asLocalDate(kpiCalculationRelatedInfo.getStartDate()));
        }
    }

    public double getPayLevelOfMainEmploymentOfStaff(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo,LocalDate selectedDate){
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        double payLevel=0.0d;
        for(StaffKpiFilterDTO staffKpiFilterDTO :staffKpiFilterDTOS){
               for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()) {
                   EmploymentLinesDTO currentEmploymentLine = getCurrentlyActiveLine(selectedDate, employmentWithCtaDetailsDTO.getEmploymentLines());
                   if(kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.EMPLOYMENT_SUB_TYPE)) {
                       payLevel = getPayLevel(kpiCalculationRelatedInfo, payLevel, currentEmploymentLine);
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

    private double getPayLevel(KPICalculationRelatedInfo kpiCalculationRelatedInfo, double payLevel, EmploymentLinesDTO currentEmploymentLine) {
        if (kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).get(0).equals(EmploymentSubType.MAIN.name()) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).size()<2) {
            payLevel = getPayLevelByCurrentEmploymentline(payLevel, currentEmploymentLine);
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
        return payLevel;
    }

    private double getPayLevelByCurrentEmploymentline(double payLevel, EmploymentLinesDTO currentEmploymentLine) {
        if (ObjectUtils.isNotNull(currentEmploymentLine)) {
            if (ObjectUtils.isNotNull(currentEmploymentLine.getEmploymentSubType()) && EmploymentSubType.MAIN.equals(currentEmploymentLine.getEmploymentSubType())) {
                payLevel += currentEmploymentLine.getPayGradeLevel();
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

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getPayLevelGradeOfMainEmploymentOfStaff(staffId, kpiCalculationRelatedInfo);
    }
}
