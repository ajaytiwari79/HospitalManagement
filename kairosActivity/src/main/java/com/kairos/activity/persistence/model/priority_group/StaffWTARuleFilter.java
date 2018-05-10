package com.kairos.activity.persistence.model.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffWTARuleFilter {
    private int minimumShiftHours;
    private int maximumShiftHours;

    public StaffWTARuleFilter() {
        //Default Constructor
    }

    public StaffWTARuleFilter(int minimumShiftHours, int maximumShiftHours) {
        this.minimumShiftHours = minimumShiftHours;
        this.maximumShiftHours = maximumShiftHours;
    }

    public int getMinimumShiftHours() {
        return minimumShiftHours;
    }

    public void setMinimumShiftHours(int minimumShiftHours) {
        this.minimumShiftHours = minimumShiftHours;
    }

    public int getMaximumShiftHours() {
        return maximumShiftHours;
    }

    public void setMaximumShiftHours(int maximumShiftHours) {
        this.maximumShiftHours = maximumShiftHours;
    }
}
