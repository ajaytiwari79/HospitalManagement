package com.kairos.persistence.model.payroll_setting;

import com.kairos.enums.DurationType;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

public class UnitPayrollSetting extends MongoBaseEntity {
    private boolean published;
    private Long unitId;
    private BigInteger parentPayrollId;
    private List<PayrollPeriod> payrollPeriods;
    private PayrollFrequency payrollFrequency;
    private List<PayrollAccessGroups> accessGroupsPriority;


    public UnitPayrollSetting() {
    }

    public UnitPayrollSetting(boolean published, Long unitId, List<PayrollPeriod> payrollPeriods, PayrollFrequency payrollFrequency) {
        this.published = published;
        this.unitId = unitId;
        this.payrollPeriods = payrollPeriods;
        this.payrollFrequency = payrollFrequency;
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

    public PayrollFrequency getPayrollFrequency() {
        return payrollFrequency;
    }

    public void setPayrollFrequency(PayrollFrequency payrollFrequency) {
        this.payrollFrequency = payrollFrequency;
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
