package com.kairos.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeniorityLevelFunctionDTO {
    private Long seniorityLevelId;
    private Integer from; // added these 2 fields just FE needs them
    private Integer to;

    private List<FunctionsDTO> functions;



    public SeniorityLevelFunctionDTO() {
    }

    public Long getSeniorityLevelId() {
        return seniorityLevelId;
    }

    public void setSeniorityLevelId(Long seniorityLevelId) {
        this.seniorityLevelId = seniorityLevelId;
    }

    public List<FunctionsDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionsDTO> functions) {
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
