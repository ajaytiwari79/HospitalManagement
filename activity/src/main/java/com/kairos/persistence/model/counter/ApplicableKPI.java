package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

public class ApplicableKPI extends MongoBaseEntity {
    private BigInteger activeKpiId;
    private BigInteger baseKpiId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;
    private String title;
    private ApplicableFilter applicableFilter;
    private boolean fibonacciKPI;
    // use for country admin and unit manager if they create copy kpi from bottom instrument of kpi
    private boolean copy;

    public ApplicableKPI() {
        //Default Constructor
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level, ApplicableFilter applicableFilter,String title,boolean copy) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.applicableFilter=applicableFilter;
        this.copy=copy;
        this.title=title;
    }

    public BigInteger getActiveKpiId() {
        return activeKpiId;
    }

    public void setActiveKpiId(BigInteger activeKpiId) {
        this.activeKpiId = activeKpiId;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public BigInteger getBaseKpiId() {
        return baseKpiId;
    }

    public void setBaseKpiId(BigInteger baseKpiId) {
        this.baseKpiId = baseKpiId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public ApplicableFilter getApplicableFilter() {
        return applicableFilter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setApplicableFilter(ApplicableFilter applicableFilter) {
        this.applicableFilter = applicableFilter;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public boolean isFibonacciKPI() {
        return fibonacciKPI;
    }

    public void setFibonacciKPI(boolean fibonacciKPI) {
        this.fibonacciKPI = fibonacciKPI;
    }
}
