package com.kairos.dto.activity.counter.distribution.access_group;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiFunction;

public class AccessGroupMappingDTO {
    private Long accessGroupId;
    private BigInteger kpiId;
    private List<BigInteger> kpiIds;

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

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
