package com.kairos.user.staff.staff_settings;

import java.math.BigInteger;

public class StaffActivitySettingDTO {
    private BigInteger id;
    private Long staffId;
    private BigInteger activityId;
    private Long unitPositionId;
    private Long unitId;
    private Integer shortestTime;
    private Integer longestTime;

    public StaffActivitySettingDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(Integer shortestTime) {
        this.shortestTime = shortestTime;
    }

    public Integer getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(Integer longestTime) {
        this.longestTime = longestTime;
    }
}
