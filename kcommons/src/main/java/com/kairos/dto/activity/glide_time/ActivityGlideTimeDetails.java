package com.kairos.dto.activity.glide_time;
/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.enums.LocationEnum;

public class ActivityGlideTimeDetails {
    private LocationEnum location;
    private short before; // storing in minutes
    private short after;  // storing in minutes
    private boolean eligible;

    public ActivityGlideTimeDetails() {
        //Default Constructor
    }

    public ActivityGlideTimeDetails(LocationEnum location, short before, short after) {
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
