package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class AccessGroupKPIEntry extends MongoBaseEntity {
    private Long accessGroupId;
    private BigInteger kpiAssignmentId;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;

    public AccessGroupKPIEntry() {
    }

    public AccessGroupKPIEntry(Long accessGroupId, BigInteger kpiAssignmentId, Long countryId, Long unitId, ConfLevel level) {
        this.accessGroupId = accessGroupId;
        this.kpiAssignmentId = kpiAssignmentId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.level = level;
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public BigInteger getKpiId() {
        return kpiAssignmentId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiAssignmentId = kpiId;
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

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }
}
