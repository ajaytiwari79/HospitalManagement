package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class KPIAssignment extends MongoBaseEntity {
    private BigInteger kpiId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;

    public KPIAssignment() {

    }

    public KPIAssignment(BigInteger kpiId, Long countryId, Long unitId, Long staffId, ConfLevel level){
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.kpiId = kpiId;
        this.level = level;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
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
