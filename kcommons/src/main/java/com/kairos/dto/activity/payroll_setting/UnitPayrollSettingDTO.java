package com.kairos.dto.activity.payroll_setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UnitPayrollSettingDTO {
    private BigInteger id;
    // use when break table
    private BigInteger parentPayrollId;
    private boolean published;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PayrollPeriodDTO> payrollPeriods;
    private List<PayrollAccessGroupsDTO> accessGroupsPriority;
    private PayrollFrequency payrollFrequency;
     // for use send default data
    private Set<Integer> years;


    public void setPayrollPeriods(List<PayrollPeriodDTO> payrollPeriods) {
        if (isCollectionNotEmpty(payrollPeriods)) {
            payrollPeriods.sort(Comparator.comparing(PayrollPeriodDTO::getStartDate));
        }

        this.payrollPeriods = payrollPeriods;
    }


}
