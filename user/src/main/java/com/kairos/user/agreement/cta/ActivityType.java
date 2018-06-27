package com.kairos.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;
@NodeEntity
public class ActivityType extends UserBaseEntity{
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
