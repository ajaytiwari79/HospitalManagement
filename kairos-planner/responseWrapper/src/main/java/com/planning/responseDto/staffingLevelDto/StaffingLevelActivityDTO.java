package com.planning.responseDto.staffingLevelDto;

public class StaffingLevelActivityDTO {

    private Long activityId;
    private int noOfStaff;
    private boolean includeInMin;

    public StaffingLevelActivityDTO() {
        //default constructor
    }

    public StaffingLevelActivityDTO(Long activityId, Integer noOfStaff) {
        this.activityId = activityId;
        this.noOfStaff = noOfStaff;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public int getNoOfStaff() {
        return noOfStaff;
    }

    public void setNoOfStaff(int noOfStaff) {
        this.noOfStaff = noOfStaff;
    }

    public boolean isIncludeInMin() {
        return includeInMin;
    }

    public void setIncludeInMin(boolean includeInMin) {
        this.includeInMin = includeInMin;
    }



}
