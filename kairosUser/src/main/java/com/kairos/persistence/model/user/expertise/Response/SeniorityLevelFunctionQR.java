package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class SeniorityLevelFunctionQR {
    private Long seniorityLevelId;
    private Integer from; // added these 2 fields just FE needs them
    private Integer to;

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

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }
}
