package com.kairos.dto.activity.payroll_setting;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PayrollPeriodDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    //pay roll deadline date
    private LocalDateTime deadlineDate;
    private List<PayrollAccessGroupsDTO> payrollAccessGroups;
}

