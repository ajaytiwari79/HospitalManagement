package com.kairos.dto.activity.payroll_setting;

import com.kairos.enums.DurationType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class PayrollSettingDTO {
    private BigInteger id;
    private boolean published;
    private Long unitId;
    private LocalDate startDate;
    private List<PayRollPeriodSettingDTO> payrollPeriods;
    private List<PayRollAccessGroupSettingDTO> accessGroupsPriority;
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

    public List<PayRollPeriodSettingDTO> getPayrollPeriods() {
        return payrollPeriods;
    }

    public void setPayrollPeriods(List<PayRollPeriodSettingDTO> payrollPeriods) {
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

    public List<PayRollAccessGroupSettingDTO> getAccessGroupsPriority() {
        return accessGroupsPriority;
    }

    public void setAccessGroupsPriority(List<PayRollAccessGroupSettingDTO> accessGroupsPriority) {
        this.accessGroupsPriority = accessGroupsPriority;
    }
}
