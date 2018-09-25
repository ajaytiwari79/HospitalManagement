package com.planner.domain.query_results.unit_position;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class UnitPositionQueryResult {
    public Long[] getUnitPostionsId() {
        return unitPostionsId;
    }

    public void setUnitPostionsId(Long[] unitPostionsId) {
        this.unitPostionsId = unitPostionsId;
    }

    private Long[] unitPostionsId;

}
