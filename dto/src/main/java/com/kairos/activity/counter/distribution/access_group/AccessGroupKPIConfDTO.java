package com.kairos.activity.counter.distribution.access_group;

import java.math.BigInteger;
import java.util.List;

public class AccessGroupKPIConfDTO {
    private List<Long> accessGroupIds;
    private List<BigInteger> kpiIds;

    public AccessGroupKPIConfDTO(){}

    public AccessGroupKPIConfDTO(List<Long> accessGroupIds, List<BigInteger> kpiIds){
        this.accessGroupIds = accessGroupIds;
        this.kpiIds =kpiIds;
    }

    public List<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(List<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
