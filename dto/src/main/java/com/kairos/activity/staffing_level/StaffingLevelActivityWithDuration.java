package com.kairos.activity.staffing_level;

public class StaffingLevelActivityWithDuration {

    private String name;
    private Long activityId;
    // private int noOfStaff;
    private boolean includeInMin;
    private int minNoOfStaff;
    private int maxNoOfStaff;

    public int getUnderStaffingOverStaffingCount() {
        return underStaffingOverStaffingCount;
    }

    public void setUnderStaffingOverStaffingCount(int underStaffingOverStaffingCount) {
        this.underStaffingOverStaffingCount = underStaffingOverStaffingCount;
    }

    private int underStaffingOverStaffingCount;
    private Duration staffingLevelDuration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public boolean isIncludeInMin() {
        return includeInMin;
    }

    public void setIncludeInMin(boolean includeInMin) {
        this.includeInMin = includeInMin;
    }

    public int getMinNoOfStaff() {
        return minNoOfStaff;
    }

    public void setMinNoOfStaff(int minNoOfStaff) {
        this.minNoOfStaff = minNoOfStaff;
    }

    public int getMaxNoOfStaff() {
        return maxNoOfStaff;
    }

    public void setMaxNoOfStaff(int maxNoOfStaff) {
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public Duration getStaffingLevelDuration() {
        return staffingLevelDuration;
    }

    public void setStaffingLevelDuration(Duration staffingLevelDuration) {
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public StaffingLevelActivityWithDuration(Long activityId, int minNoOfStaff, int maxNoOfStaff, Duration staffingLevelDuration) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }
}
