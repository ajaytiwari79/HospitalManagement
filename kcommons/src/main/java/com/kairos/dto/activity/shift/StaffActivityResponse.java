package com.kairos.dto.activity.shift;

import java.math.BigInteger;

public class StaffActivityResponse {
    private Long staffId;
    private BigInteger activityId;
    private String message;

    public StaffActivityResponse() {
        //Default Constructor
    }

    public StaffActivityResponse(Long staffId, BigInteger activityId, String message) {
        this.staffId = staffId;
        this.activityId = activityId;
        this.message = message;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
