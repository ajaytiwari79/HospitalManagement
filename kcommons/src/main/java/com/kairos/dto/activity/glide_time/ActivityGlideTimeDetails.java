package com.kairos.dto.activity.glide_time;
/*
 *Created By Pavan on 25/10/18
 *
 */

import com.kairos.enums.LocationEnum;

import java.io.Serializable;

public class ActivityGlideTimeDetails implements Serializable {
    private static final long serialVersionUID = -1047736179421816528L;
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

    public short getBefore() {
        return before;
    }

    public void setBefore(short before) {
        this.before = before;
    }

    public short getAfter() {
        return after;
    }

    public void setAfter(short after) {
        this.after = after;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
