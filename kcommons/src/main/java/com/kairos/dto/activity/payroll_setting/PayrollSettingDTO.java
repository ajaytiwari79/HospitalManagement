package com.kairos.dto.activity.payroll_setting;

import com.kairos.enums.DurationType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class PayrollSettingDTO {
    private BigInteger id;
    // use when break table
    private BigInteger parentPayrollId;
    private boolean published;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PayrollPeriodDTO> payrollPeriods;
    private List<PayrollAccessGroupsDTO> accessGroupsPriority;
    private DurationType durationType;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<PayrollPeriodDTO> getPayrollPeriods() {
        return payrollPeriods;
    }

    public void setPayrollPeriods(List<PayrollPeriodDTO> payrollPeriods) {
        this.payrollPeriods = payrollPeriods;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<PayrollAccessGroupsDTO> getAccessGroupsPriority() {
        return accessGroupsPriority;
    }

    public void setAccessGroupsPriority(List<PayrollAccessGroupsDTO> accessGroupsPriority) {
        this.accessGroupsPriority = accessGroupsPriority;
    }

    public BigInteger getParentPayrollId() {
        return parentPayrollId;
    }

    public void setParentPayrollId(BigInteger parentPayrollId) {
        this.parentPayrollId = parentPayrollId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
