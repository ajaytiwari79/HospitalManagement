package com.kairos.enums.cta;

import java.io.Serializable;

/**
 * Created by prerna on 20/2/18.
 */
public enum ActivityTypeForCostCalculation implements Serializable {
    COST_CALCULATION_ACTIVITY("Cost Calculation Activity"),SELECTED_ACTIVITY_TYPE("Selected Activity Type"),TIME_TYPE_ACTIVITY("Time Type Activity");
    private String activityType;
    ActivityTypeForCostCalculation(String activityType){
        this.activityType=activityType;
    }

}
