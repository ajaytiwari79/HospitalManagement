package com.kairos.activity.counter.distribution.access_group;

import java.math.BigInteger;

public class AccessGroupMappingDTO {
    private Long accessGroupId;
    private BigInteger kpiId;
    private BigInteger kpiAssignmentId;

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

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }
}
