package com.kairos.dto.activity.glide_time;
/*
 *Created By Pavan on 20/10/18
 *
 */

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class GlideTimeDetails {
    private short before; // storing in minutes
    private short after;  // storing in minutes

    public GlideTimeDetails() {
        //Default Constructor
    }

    public GlideTimeDetails(short before, short after) {
        this.before = before;
        this.after = after;
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

}
