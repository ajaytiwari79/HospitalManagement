package com.kairos.persistence.model.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class UnitTimeZoneQueryResult {
    private Long unitId;
    private String timezone;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
