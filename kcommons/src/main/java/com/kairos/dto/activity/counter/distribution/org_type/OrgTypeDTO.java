package com.kairos.dto.activity.counter.distribution.org_type;

import java.util.List;

public class OrgTypeDTO {
    private Long orgTypeId;
    private List<Long> unitIds;

    public Long getOrgTypeId() {
        return orgTypeId;
    }

    public void setOrgTypeId(Long orgTypeId) {
        this.orgTypeId = orgTypeId;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }
}
