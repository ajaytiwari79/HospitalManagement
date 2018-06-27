package com.kairos.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

/**
 * Created by vipul on 30/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CTAAndWTASettingsActivityTabDTO {
    private BigInteger activityId;
    private  boolean eligibleForCostCalculation;

    public CTAAndWTASettingsActivityTabDTO(boolean eligibleForCostCalculation) {
        this.eligibleForCostCalculation = eligibleForCostCalculation;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isEligibleForCostCalculation() {
        return eligibleForCostCalculation;
    }

    public void setEligibleForCostCalculation(boolean eligibleForCostCalculation) {
        this.eligibleForCostCalculation = eligibleForCostCalculation;
    }

    public CTAAndWTASettingsActivityTabDTO() {
    }


}
