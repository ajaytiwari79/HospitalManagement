package com.kairos.persistence.model.user.counter;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class OrgTypeQueryResult {
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
