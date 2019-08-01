package com.kairos.dto.activity.payroll_setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.payroll_setting.PayrollFrequency;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public UnitPayrollSettingDTO() {
    }

    public UnitPayrollSettingDTO(Set<Integer> years) {
        this.years = years;
    }

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
        if (isCollectionNotEmpty(payrollPeriods)) {
            payrollPeriods.sort(Comparator.comparing(PayrollPeriodDTO::getStartDate));
        }

        this.payrollPeriods = payrollPeriods;
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


    public PayrollFrequency getPayrollFrequency() {
        return payrollFrequency;
    }

    public void setPayrollFrequency(PayrollFrequency payrollFrequency) {
        this.payrollFrequency = payrollFrequency;
    }

    public Set<Integer> getYears() {
        return years;
    }

    public void setYears(Set<Integer> years) {
        this.years = years;
    }


}
