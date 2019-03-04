package com.kairos.dto.activity.payroll_setting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PayRollPeriodSettingDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    //pay roll deadline date
    private LocalDateTime deadlineDate;
    private List<PayRollAccessGroupSettingDTO> payrollAccessGroups;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public List<PayRollAccessGroupSettingDTO> getPayrollAccessGroups() {
        return payrollAccessGroups;
    }

    public void setPayrollAccessGroups(List<PayRollAccessGroupSettingDTO> payrollAccessGroups) {
        this.payrollAccessGroups = payrollAccessGroups;
    }
}

