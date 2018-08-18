package com.kairos.activity.counter.distribution.access_group;

import java.math.BigInteger;

public class AccessGroupMappingDTO {
    private Long accessGroupId;
    private BigInteger kpiId;

    public AccessGroupMappingDTO() {
    }

    public AccessGroupMappingDTO(Long accessGroupId, BigInteger kpiId) {
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
