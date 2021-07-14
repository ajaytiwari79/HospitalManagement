package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.enums.kpi.YAxisConfig;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class WorkTimeAgreementBalancesCalculationService implements KPIService{
    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getLeaveCount(staffId, dateTimeInterval, kpiCalculationRelatedInfo, (YAxisConfig) t);
    }

    public int getLeaveCount(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
        int count = 0;
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        if (isCollectionNotEmpty(staffKpiFilterDTOS)) {
            for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
                for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                    WorkTimeAgreementBalance workTimeAgreementBalance = getWorkTimeAgreementBalance(kpiCalculationRelatedInfo.getUnitId(),employmentWithCtaDetailsDTO.getId(), dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate(),kpiCalculationRelatedInfo.getWtaTemplateTypes(yAxisConfig),null);
                    count += workTimeAgreementBalance.getWorkTimeAgreementRuleTemplateBalances().stream().flatMap(workTimeAgreementRuleTemplateBalancesDTO -> workTimeAgreementRuleTemplateBalancesDTO.getIntervalBalances().stream()).mapToInt(intervalBalance -> (int) intervalBalance.getAvailable()).sum();
                }
            }
        }
        return count;
    }
}
