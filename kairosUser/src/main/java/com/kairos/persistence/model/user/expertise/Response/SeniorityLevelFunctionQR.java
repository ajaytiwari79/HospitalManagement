package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class SeniorityLevelFunctionQR {
    private Long seniorityLevelId;
    private List<FunctionQR> functions;


    public SeniorityLevelFunctionQR() {
        // dc
    }

    public Long getSeniorityLevelId() {
        return seniorityLevelId;
    }

    public void setSeniorityLevelId(Long seniorityLevelId) {
        this.seniorityLevelId = seniorityLevelId;
    }

    public List<FunctionQR> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionQR> functions) {
        this.functions = functions;
    }
}
