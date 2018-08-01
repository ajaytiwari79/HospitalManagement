package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class ApplicableKPI extends MongoBaseEntity {
    private BigInteger ActiveKpiId;
    private BigInteger BaseKpiId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;

    public ApplicableKPI() {

    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level) {
        ActiveKpiId = activeKpiId;
        BaseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
    }

    public BigInteger getActiveKpiId() {
        return ActiveKpiId;
    }

    public void setActiveKpiId(BigInteger activeKpiId) {
        this.ActiveKpiId = activeKpiId;
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
        return BaseKpiId;
    }

    public void setBaseKpiId(BigInteger baseKpiId) {
        BaseKpiId = baseKpiId;
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
}
