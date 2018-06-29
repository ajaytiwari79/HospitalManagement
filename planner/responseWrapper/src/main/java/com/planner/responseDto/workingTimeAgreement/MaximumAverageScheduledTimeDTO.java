package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumAverageScheduledTimeDTO {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private boolean balanceAdjustment;
    private boolean useShiftTimes;
    private long maximumAvgTime;
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



    public List<String> getBalanceType() {
        return balanceType;
    }


    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
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


    public boolean isBalanceAdjustment() {
        return balanceAdjustment;
    }

    public void setBalanceAdjustment(boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public boolean isUseShiftTimes() {
        return useShiftTimes;
    }

    public void setUseShiftTimes(boolean useShiftTimes) {
        this.useShiftTimes = useShiftTimes;
    }

    public MaximumAverageScheduledTimeDTO(List<String> balanceType, long intervalLength, String intervalUnit, long validationStartDateMillis, boolean balanceAdjustment, boolean useShiftTimes, long maximumAvgTime) {
        this.balanceType = balanceType;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
        this.balanceAdjustment = balanceAdjustment;
        this.useShiftTimes = useShiftTimes;
        this.maximumAvgTime = maximumAvgTime;
    }

    public MaximumAverageScheduledTimeDTO(long maximumAvgTime, int weight, String level){
        this.weight = weight;
        this.maximumAvgTime = maximumAvgTime;
        this.level = level;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(long maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public MaximumAverageScheduledTimeDTO() {
    }


}
