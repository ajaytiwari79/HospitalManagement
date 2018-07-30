package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class OrgTypeKPIEntry extends MongoBaseEntity {
    private Long orgTypeId;
    private BigInteger kpiAssignmentId;
    private Long countryId;
    public OrgTypeKPIEntry() {
    }

    public OrgTypeKPIEntry(Long orgTypeId, BigInteger kpiId) {
        this.orgTypeId = orgTypeId;
        this.kpiAssignmentId = kpiId;
    }

    public Long getOrgTypeId() {
        return orgTypeId;
    }

    public void setOrgTypeId(Long orgTypeId) {
        this.orgTypeId = orgTypeId;
    }

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
