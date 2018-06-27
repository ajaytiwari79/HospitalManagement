package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumVetoPerPeriodDTO {

    private double maximumVetoPercentage;
    private int weight;
    private String level;
    private String templateType;


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

    public double getMaximumVetoPercentage() {
        return maximumVetoPercentage;
    }

    public void setMaximumVetoPercentage(double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public MaximumVetoPerPeriodDTO(double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public MaximumVetoPerPeriodDTO() {
    }


}
