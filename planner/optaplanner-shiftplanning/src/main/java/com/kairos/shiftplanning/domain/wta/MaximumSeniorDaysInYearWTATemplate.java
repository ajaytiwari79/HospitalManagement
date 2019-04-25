package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE20
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumSeniorDaysInYearWTATemplate implements ConstraintHandler {

    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private long daysLimit;
    private String activityCode;
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public MaximumSeniorDaysInYearWTATemplate(long daysLimit, int weight, ScoreLevel level) {
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

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
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

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public MaximumSeniorDaysInYearWTATemplate(long intervalLength, String intervalUnit, long validationStartDateMillis, long daysLimit, String activityCode) {
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
        this.daysLimit = daysLimit;
        this.activityCode = activityCode;
    }

    public MaximumSeniorDaysInYearWTATemplate() {
    }


}
