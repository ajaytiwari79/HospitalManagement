package com.kairos.dto.activity.wta.templates;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class BreakTemplateValue {
    private int shiftDuration;
    private int breaksAllowed;
    private int breakDuration;
    private int earliestDurationMinutes;
    private int latestDurationMinutes;
    private List<Long> activities=new ArrayList<>();

    public BreakTemplateValue() {
        //Default Constructor
    }

    public BreakTemplateValue(int shiftDuration, int breaksAllowed, int breakDuration, int earliestDurationMinutes, int latestDurationMinutes, List<Long> activities) {
        this.shiftDuration = shiftDuration;
        this.breaksAllowed = breaksAllowed;
        this.breakDuration = breakDuration;
        this.earliestDurationMinutes = earliestDurationMinutes;
        this.latestDurationMinutes = latestDurationMinutes;
        this.activities = activities;
    }

    public int getShiftDuration() {
        return shiftDuration;
    }

    public void setShiftDuration(int shiftDuration) {
        this.shiftDuration = shiftDuration;
    }

    public int getBreaksAllowed() {
        return breaksAllowed;
    }

    public void setBreaksAllowed(int breaksAllowed) {
        this.breaksAllowed = breaksAllowed;
    }

    public int getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(int breakDuration) {
        this.breakDuration = breakDuration;
    }

    public int getEarliestDurationMinutes() {
        return earliestDurationMinutes;
    }

    public void setEarliestDurationMinutes(int earliestDurationMinutes) {
        this.earliestDurationMinutes = earliestDurationMinutes;
    }

    public int getLatestDurationMinutes() {
        return latestDurationMinutes;
    }

    public void setLatestDurationMinutes(int latestDurationMinutes) {
        this.latestDurationMinutes = latestDurationMinutes;
    }

    public List<Long> getActivities() {
        return activities;
    }

    public void setActivities(List<Long> activities) {
        this.activities = activities;
    }
}
