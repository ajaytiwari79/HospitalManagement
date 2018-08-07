package com.kairos.activity.counter.distribution.org_type;

import java.math.BigInteger;

public class OrgTypeMappingDTO {
    private Long orgTypeId;
    private BigInteger kpiId;

    public OrgTypeMappingDTO() {

    }

    public OrgTypeMappingDTO(Long orgTypeId, BigInteger kpiId) {
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
