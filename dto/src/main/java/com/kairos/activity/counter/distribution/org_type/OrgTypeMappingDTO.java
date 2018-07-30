package com.kairos.activity.counter.distribution.org_type;

import java.math.BigInteger;

public class OrgTypeMappingDTO {
    private Long orgTypeId;
    private BigInteger kpiId;
    private BigInteger kpiAssignmentId;

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

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }
}
