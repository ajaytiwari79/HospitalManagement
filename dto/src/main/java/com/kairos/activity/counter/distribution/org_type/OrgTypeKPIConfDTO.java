package com.kairos.activity.counter.distribution.org_type;

import java.math.BigInteger;
import java.util.List;

public class OrgTypeKPIConfDTO {
    private List<Long> orgTypeIds;
    private List<BigInteger> kpiIds;

    public OrgTypeKPIConfDTO(){}

    public OrgTypeKPIConfDTO(List<Long> orgTypeIds, List<BigInteger> kpiIds){
        this.orgTypeIds = orgTypeIds;
        this.kpiIds = kpiIds;
    }

    public List<Long> getOrgTypeIds() {
        return orgTypeIds;
    }

    public void setOrgTypeIds(List<Long> orgTypeIds) {
        this.orgTypeIds = orgTypeIds;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
