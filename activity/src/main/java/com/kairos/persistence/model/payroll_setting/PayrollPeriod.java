package com.kairos.persistence.model.payroll_setting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PayrollPeriod {
    private LocalDate startDate;
    private LocalDate endDate;
    //pay roll deadline date and time
    private LocalDateTime deadlineDate;
    private List<PayrollAccessGroups> payrollAccessGroups;

    public PayrollPeriod() {
    }

    public PayrollPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public List<PayrollAccessGroups> getPayrollAccessGroups() {
        return payrollAccessGroups;
    }

    public void setPayrollAccessGroups(List<PayrollAccessGroups> payrollAccessGroups) {
        this.payrollAccessGroups = payrollAccessGroups;
    }
}
