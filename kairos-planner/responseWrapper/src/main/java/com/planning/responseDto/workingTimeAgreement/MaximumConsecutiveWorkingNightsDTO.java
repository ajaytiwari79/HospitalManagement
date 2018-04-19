package com.planning.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE7
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class MaximumConsecutiveWorkingNightsDTO {


    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private long nightsWorked;//no of days
    private int weight;
    private String level;
    private String templateType;

    public MaximumConsecutiveWorkingNightsDTO(long nightsWorked, int weight, String level) {
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

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public MaximumConsecutiveWorkingNightsDTO(List<String> balanceType, boolean checkAgainstTimeRules, long nightsWorked) {
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.nightsWorked = nightsWorked;
    }

    public MaximumConsecutiveWorkingNightsDTO() {
    }



}
