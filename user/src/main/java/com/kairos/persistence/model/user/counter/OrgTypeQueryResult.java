package com.kairos.persistence.model.user.counter;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class OrgTypeQueryResult {
    private List<Long> orgTypeIds;
    private Long unitId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<Long> getOrgTypeIds() {

        return orgTypeIds;
    }

    public void setOrgTypeIds(List<Long> orgTypeIds) {
        this.orgTypeIds = orgTypeIds;
    }
}
