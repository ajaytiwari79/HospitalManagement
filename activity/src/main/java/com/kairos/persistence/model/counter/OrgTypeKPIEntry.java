package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class OrgTypeKPIEntry extends MongoBaseEntity {
    private Long orgTypeId;
    private BigInteger kpiId;
    private Long countryId;
    public OrgTypeKPIEntry() {
    }

    public OrgTypeKPIEntry(Long orgTypeId, BigInteger kpiId) {
        this.orgTypeId = orgTypeId;
        this.kpiId = kpiId;
    }

    public Long getOrgTypeId() {
        return orgTypeId;
    }

    public void setOrgTypeId(Long orgTypeId) {
        this.orgTypeId = orgTypeId;
    }

    public BigInteger getKpiAssignmentId() {
        return kpiId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiId = kpiAssignmentId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
