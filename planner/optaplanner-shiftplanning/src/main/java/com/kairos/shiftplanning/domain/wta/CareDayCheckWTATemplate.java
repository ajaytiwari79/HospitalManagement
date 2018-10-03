package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.domain.ShiftConstrutionPhase;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE14
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CareDayCheckWTATemplate implements ConstraintHandler {
    private long daysLimit;
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
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

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public CareDayCheckWTATemplate(long daysLimit, long intervalLength, String intervalUnit, long validationStartDateMillis,ScoreLevel level) {
        this.daysLimit = daysLimit;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.level=level;
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public CareDayCheckWTATemplate() {
    }

    public void checkLevelWithWeight(){

    }
    public boolean checkConsTraints(List<ShiftConstrutionPhase> shifts, ShiftConstrutionPhase shift){
        return false;
    }


}
