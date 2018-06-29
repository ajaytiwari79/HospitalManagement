package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE9
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumNumberOfNightsDTO {

    private List<String> balanceType;//multiple check boxes
    private long nightsWorked;
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private int weight;
    private String level;
    private String templateType;

    public MaximumNumberOfNightsDTO(long nightsWorked, int weight, String level) {
        this.nightsWorked = nightsWorked;
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


    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
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

    public MaximumNumberOfNightsDTO(List<String> balanceType, long nightsWorked, long intervalLength, String intervalUnit, long validationStartDateMillis) {
        this.balanceType = balanceType;
        this.nightsWorked = nightsWorked;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public MaximumNumberOfNightsDTO() {
    }

}
