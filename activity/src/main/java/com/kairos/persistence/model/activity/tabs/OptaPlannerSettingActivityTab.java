package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class OptaPlannerSettingActivityTab implements Serializable {

    private int maxThisActivityPerShift;
    private int minLength;
    private boolean eligibleForMove;

    public OptaPlannerSettingActivityTab() {
    }

    public int getMaxThisActivityPerShift() {
        return maxThisActivityPerShift;
    }

    public void setMaxThisActivityPerShift(int maxThisActivityPerShift) {
        this.maxThisActivityPerShift = maxThisActivityPerShift;
    }


    public boolean isEligibleForMove() {
        return eligibleForMove;
    }

    public void setEligibleForMove(boolean eligibleForMove) {
        this.eligibleForMove = eligibleForMove;
    }



    public OptaPlannerSettingActivityTab(int maxThisActivityPerShift, int minLength, boolean eligibleForMove) {
        this.maxThisActivityPerShift = maxThisActivityPerShift;
        this.minLength = minLength;
        this.eligibleForMove = eligibleForMove;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
}