package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.TimeType;

import java.math.BigInteger;
import java.util.List;

public class ActivitiesPerTimeType {
    private BigInteger timeTypeId;
    private String timeTypeName;
    private List<Activity> selectedActivities;

    public ActivitiesPerTimeType() {
        //Default Constructor
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public String getTimeTypeName() {
        return timeTypeName;
    }

    public void setTimeTypeName(String timeTypeName) {
        this.timeTypeName = timeTypeName;
    }

    public List<Activity> getSelectedActivities() {
        return selectedActivities;
    }

    public void setSelectedActivities(List<Activity> selectedActivities) {
        this.selectedActivities = selectedActivities;
    }
}
