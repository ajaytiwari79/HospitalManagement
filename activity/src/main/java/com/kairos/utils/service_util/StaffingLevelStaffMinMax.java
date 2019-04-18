package com.kairos.utils.service_util;

import java.math.BigInteger;

public class StaffingLevelStaffMinMax {

    private int minNoOfStaffParentActivity;
    private int maxNoOfStaffParentActivity;
    private int minNoOfStaffChildActivities;
    private int maxNoOfStaffChildActivities;

    public int getMinNoOfStaffParentActivity() { return minNoOfStaffParentActivity; }

    public void setMinNoOfStaffParentActivity(int minNoOfStaffParentActivity) { this.minNoOfStaffParentActivity = minNoOfStaffParentActivity; }

    public int getMaxNoOfStaffParentActivity() { return maxNoOfStaffParentActivity; }

    public void setMaxNoOfStaffParentActivity(int maxNoOfStaffParentActivity) { this.maxNoOfStaffParentActivity = maxNoOfStaffParentActivity; }

    public int getMinNoOfStaffChildActivities() { return minNoOfStaffChildActivities; }

    public void setMinNoOfStaffChildActivities(int minNoOfStaffChildActivities) { this.minNoOfStaffChildActivities = minNoOfStaffChildActivities; }

    public int getMaxNoOfStaffChildActivities() { return maxNoOfStaffChildActivities; }

    public void setMaxNoOfStaffChildActivities(int maxNoOfStaffChildActivities) { this.maxNoOfStaffChildActivities = maxNoOfStaffChildActivities; }

    public StaffingLevelStaffMinMax(int minNoOfStaffChildActivities, int maxNoOfStaffChildActivities) {
        this.minNoOfStaffChildActivities = minNoOfStaffChildActivities;
        this.maxNoOfStaffChildActivities = maxNoOfStaffChildActivities;
    }


    public StaffingLevelStaffMinMax() {
    }
}
