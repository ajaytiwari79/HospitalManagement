package com.kairos.dto.activity.flexible_time;/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.enums.LocationEnum;

public class ActivityFlexibleTimeDetails {
    private LocationEnum location;
    private Short before; // storing in minutes
    private Short after;  // storing in minutes
    private boolean eligible;

    public ActivityFlexibleTimeDetails() {
        //Default Constructor
    }

    public ActivityFlexibleTimeDetails(LocationEnum location, Short before, Short after) {
        this.location = location;
        this.before = before;
        this.after = after;
    }

    public LocationEnum getLocation() {
        return location;
    }

    public void setLocation(LocationEnum location) {
        this.location = location;
    }

    public Short getBefore() {
        return before;
    }

    public void setBefore(Short before) {
        this.before = before;
    }

    public Short getAfter() {
        return after;
    }

    public void setAfter(Short after) {
        this.after = after;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
