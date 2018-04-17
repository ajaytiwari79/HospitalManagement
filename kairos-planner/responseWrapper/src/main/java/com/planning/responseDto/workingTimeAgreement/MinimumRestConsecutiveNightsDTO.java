package com.planning.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE8
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumRestConsecutiveNightsDTO {


    private List<String> balanceType;//multiple check boxes
    private long minimumRest;
    private long nightsWorked;
    private int weight;
    private String level;
    private String templateType;


    public MinimumRestConsecutiveNightsDTO(long minimumRest, int weight, String level) {
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

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public MinimumRestConsecutiveNightsDTO(List<String> balanceType, long minimumRest, long nightsWorked) {
        this.balanceType = balanceType;
        this.minimumRest = minimumRest;
        this.nightsWorked = nightsWorked;
    }

    public MinimumRestConsecutiveNightsDTO() {
    }


}
