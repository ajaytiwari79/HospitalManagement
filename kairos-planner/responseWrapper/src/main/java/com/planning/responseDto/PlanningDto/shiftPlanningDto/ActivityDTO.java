package com.planning.responseDto.PlanningDto.shiftPlanningDto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDTO {

    private BigInteger id;
    private String name;
    private String description;
    private SkillActivityTab skillActivityTab;
    private TimeTypeDTO timeType;

    public ActivityDTO() {
    }

    public TimeTypeDTO getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeDTO timeType) {
        this.timeType = timeType;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SkillActivityTab getSkillActivityTab() {
        return skillActivityTab;
    }

    public void setSkillActivityTab(SkillActivityTab skillActivityTab) {
        this.skillActivityTab = skillActivityTab;
    }
}
