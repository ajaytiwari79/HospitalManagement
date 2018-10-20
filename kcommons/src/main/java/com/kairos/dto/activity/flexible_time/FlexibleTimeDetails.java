package com.kairos.dto.activity.flexible_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

public class FlexibleTimeDetails {
    private Short before; // storing in minutes
    private Short after;  // storing in minutes

    public FlexibleTimeDetails() {
        //Default Constructor
    }

    public FlexibleTimeDetails(Short before, Short after) {
        this.before = before;
        this.after = after;
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
}
