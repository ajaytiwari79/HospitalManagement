package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.enums.IntervalType;

//Domain name can be chaged
public class OpenShiftInterval {
    private int from;
    private int to;
    private IntervalType intervalType;

    public OpenShiftInterval() {
        //Default Constructor
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(IntervalType intervalType) {
        this.intervalType = intervalType;
    }
}
