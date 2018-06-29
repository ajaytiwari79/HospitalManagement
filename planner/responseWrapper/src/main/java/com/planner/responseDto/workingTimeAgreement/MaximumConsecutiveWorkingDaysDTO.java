package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE3
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumConsecutiveWorkingDaysDTO {

    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private long daysLimit;//no of days
    private int weight;
    private String level;
    private String templateType;

    public MaximumConsecutiveWorkingDaysDTO(long daysLimit, int weight, String level) {
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

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public boolean isCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public MaximumConsecutiveWorkingDaysDTO(List<String> balanceType, boolean checkAgainstTimeRules, long daysLimit) {
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.daysLimit = daysLimit;
    }

    public MaximumConsecutiveWorkingDaysDTO() {
    }


}
