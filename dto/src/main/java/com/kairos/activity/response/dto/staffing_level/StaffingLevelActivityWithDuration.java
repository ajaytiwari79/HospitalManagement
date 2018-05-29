package com.kairos.activity.response.dto.staffing_level;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;

public class StaffingLevelActivityWithDuration {

    private String name;
    private Long activityId;
    // private int noOfStaff;
    private boolean includeInMin;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private StaffingLevelDuration staffingLevelDuration;

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

    public StaffingLevelDuration getStaffingLevelDuration() {
        return staffingLevelDuration;
    }

    public void setStaffingLevelDuration(StaffingLevelDuration staffingLevelDuration) {
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public StaffingLevelActivityWithDuration(Long activityId, int minNoOfStaff, int maxNoOfStaff, StaffingLevelDuration staffingLevelDuration) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }
}
