package com.kairos.persistence.model.activity.tabs;

import java.io.Serializable;

/**
 * Created by vipul on 30/11/17.
 */
public class CTAAndWTASettingsActivityTab implements Serializable{
    private  boolean eligibleForCostCalculation;

    public CTAAndWTASettingsActivityTab() {
    }

    public CTAAndWTASettingsActivityTab(boolean eligibleForCostCalculation) {
        this.eligibleForCostCalculation = eligibleForCostCalculation;
    }

    public boolean isEligibleForCostCalculation() {
        return eligibleForCostCalculation;
    }

    public void setEligibleForCostCalculation(boolean eligibleForCostCalculation) {
        this.eligibleForCostCalculation = eligibleForCostCalculation;
    }
}
