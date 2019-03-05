package com.kairos.persistence.model.payroll_setting;

import com.kairos.enums.DurationType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

public class PayrollSetting extends MongoBaseEntity {
    private boolean published;
    private Long unitId;
    private BigInteger parentPayrollId;
    private List<PayrollPeriod> payrollPeriods;
    private DurationType durationType;
    private List<PayrollAccessGroups> accessGroupsPriority;


    public PayrollSetting() {
    }

    public PayrollSetting(boolean published, Long unitId, List<PayrollPeriod> payrollPeriods, DurationType durationType) {
        this.published = published;
        this.unitId = unitId;
        this.payrollPeriods = payrollPeriods;
        this.durationType = durationType;
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

    public List<PayrollPeriod> getPayrollPeriods() {
        return payrollPeriods;
    }

    public void setPayrollPeriods(List<PayrollPeriod> payrollPeriods) {
        this.payrollPeriods = payrollPeriods;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public List<PayrollAccessGroups> getAccessGroupsPriority() {
        return accessGroupsPriority;
    }

    public void setAccessGroupsPriority(List<PayrollAccessGroups> accessGroupsPriority) {
        this.accessGroupsPriority = accessGroupsPriority;
    }

    public BigInteger getParentPayrollId() {
        return parentPayrollId;
    }

    public void setParentPayrollId(BigInteger parentPayrollId) {
        this.parentPayrollId = parentPayrollId;
    }
}
