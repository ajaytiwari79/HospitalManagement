package com.kairos.user.country.agreement.cta.cta_response;

/**
 * Created by prerna on 15/2/18.
 */
public class CTAAndWTASettingsActivityTabDTO {
    private  boolean eligibleForCostCalculation;

    public CTAAndWTASettingsActivityTabDTO() {
        //
    }

    public boolean isEligibleForCostCalculation() {
        return eligibleForCostCalculation;
    }

    public void setEligibleForCostCalculation(boolean eligibleForCostCalculation) {
        this.eligibleForCostCalculation = eligibleForCostCalculation;
    }


}
