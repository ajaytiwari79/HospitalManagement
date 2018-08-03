package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class OrgTypeKPIEntry extends MongoBaseEntity {
    private Long orgTypeId;
    private BigInteger kpiId;

    public OrgTypeKPIEntry(){}

    public OrgTypeKPIEntry(Long orgTypeId, BigInteger kpiId){
        this.orgTypeId = orgTypeId;
        this.kpiId = kpiId;
    }

    public Long getOrgTypeId() {
        return orgTypeId;
    }

    public void setOrgTypeId(Long orgTypeId) {
        this.orgTypeId = orgTypeId;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }
}
