package com.kairos.activity.break_settings;/*
 *Created By Pavan on 27/8/18
 *
 */

import com.kairos.activity.activity.ActivityDTO;

import java.math.BigInteger;
import java.util.List;

public class PaidAndUnPaidActivities {
    private String id;
    private List<ActivityDTO> activities;

    public PaidAndUnPaidActivities() {
        //Default Constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
