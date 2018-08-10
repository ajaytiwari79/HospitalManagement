package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class AccessGroupKPIEntry extends MongoBaseEntity {
    private Long accessGroupId;
    private BigInteger kpiId;

    public AccessGroupKPIEntry() {
    }

    public AccessGroupKPIEntry(Long accessGroupId, BigInteger kpiId) {
        this.accessGroupId = accessGroupId;
        this.kpiId = kpiId;
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }
}
