package com.planner.responseDto.PlanningDto.shiftPlanningDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeTypeDTO {

    private BigInteger id;
    private String timeTypes;
    private String label;
    private String description;

    public TimeTypeDTO() {
    }

    public TimeTypeDTO(BigInteger id, String timeTypes, String label, String description) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(String timeTypes) {
        this.timeTypes = timeTypes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
