package com.kairos.activity.break_settings;/*
 *Created By Pavan on 27/8/18
 *
 */

import com.kairos.activity.activity.ActivityDTO;

import java.math.BigInteger;
import java.util.List;

public class BreakActivitiesDTO {
    private String timeType;
    private List<ActivityDTO> activities;

    public BreakActivitiesDTO() {
        //Default Constructor
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
