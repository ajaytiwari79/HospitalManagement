package com.kairos.dto.activity.cta;

import java.util.ArrayList;
import java.util.List;


public class ActivityType {
    boolean onlyForActivityThatPartOfCostCalculation;
    private List<Long> activityTypes=new ArrayList<>();

    public ActivityType(){
        // default constructor
    }

    public boolean isOnlyForActivityThatPartOfCostCalculation() {
        return onlyForActivityThatPartOfCostCalculation;
    }

    public void setOnlyForActivityThatPartOfCostCalculation(boolean onlyForActivityThatPartOfCostCalculation) {
        this.onlyForActivityThatPartOfCostCalculation = onlyForActivityThatPartOfCostCalculation;
    }

    public List<Long> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<Long> activityTypes) {
        this.activityTypes = activityTypes;
    }


}
