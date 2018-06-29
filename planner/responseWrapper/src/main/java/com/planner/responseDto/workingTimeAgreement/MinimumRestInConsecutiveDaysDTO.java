package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE4
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumRestInConsecutiveDaysDTO {

    private long minimumRest;//hh:mm
    private long daysWorked;
    private int weight;
    private String level;
    private String templateType;

    public MinimumRestInConsecutiveDaysDTO(long minimumRest, int weight, String level) {
        this.minimumRest = minimumRest;
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

    public long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }


    public MinimumRestInConsecutiveDaysDTO(long minimumRest, long daysWorked) {
        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;
    }

    public MinimumRestInConsecutiveDaysDTO() {
    }


}
