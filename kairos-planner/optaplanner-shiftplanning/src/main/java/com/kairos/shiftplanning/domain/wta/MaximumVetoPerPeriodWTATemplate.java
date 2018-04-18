package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.domain.ShiftConstrutionPhase;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumVetoPerPeriodWTATemplate implements ConstraintHandler {

    private double maximumVetoPercentage;
    private int weight;
    private ScoreLevel level;
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

    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    public double getMaximumVetoPercentage() {
        return maximumVetoPercentage;
    }

    public void setMaximumVetoPercentage(double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public MaximumVetoPerPeriodWTATemplate(double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public MaximumVetoPerPeriodWTATemplate() {
    }

    public int checkConsTraints(List<ShiftConstrutionPhase> shifts, ShiftConstrutionPhase shift){
        //TODO we can't consider from now it will comes in absence planning @Sachin
        return 0;
    }
}
