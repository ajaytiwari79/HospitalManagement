package com.kairos.dto.activity.counter.distribution.org_type;

import java.util.List;

public class OrgTypeDTO {
    private List<Long> orgTypeIds;
    private Long unitId;

    public List<Long> getOrgTypeIds() {
        return orgTypeIds;
    }

    public void setOrgTypeIds(List<Long> orgTypeIds) {
        this.orgTypeIds = orgTypeIds;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
