package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE16
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumDurationBetweenShiftDTO {

    private List<String> balanceType;
    private long minimumDurationBetweenShifts;
    private int weight;
    private String level;
    private String templateType;


    public MinimumDurationBetweenShiftDTO(long minimumDurationBetweenShifts, int weight, String level) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
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

    public long getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(long minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public MinimumDurationBetweenShiftDTO(List<String> balanceType, long minimumDurationBetweenShifts) {
        this.balanceType = balanceType;
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public MinimumDurationBetweenShiftDTO() {
    }


}