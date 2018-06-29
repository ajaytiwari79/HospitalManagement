package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE6
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumConsecutiveNightsDTO {


    private long daysLimit;
    private int weight;
    private String level;
    private String templateType;

    public MinimumConsecutiveNightsDTO(long daysLimit, int weight, String level) {
        this.daysLimit = daysLimit;
        this.weight = weight;
        this.level = level;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public MinimumConsecutiveNightsDTO(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public MinimumConsecutiveNightsDTO() {
    }

}
