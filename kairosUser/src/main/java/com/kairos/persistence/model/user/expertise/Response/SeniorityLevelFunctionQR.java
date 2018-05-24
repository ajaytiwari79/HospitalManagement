package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class SeniorityLevelFunctionQR {
    private Long seniorityLevelId;

    public SeniorityLevelFunctionQR() {
        // dc
    }

    public Long getSeniorityLevelId() {
        return seniorityLevelId;
    }

    public void setSeniorityLevelId(Long seniorityLevelId) {
        this.seniorityLevelId = seniorityLevelId;
    }
}
