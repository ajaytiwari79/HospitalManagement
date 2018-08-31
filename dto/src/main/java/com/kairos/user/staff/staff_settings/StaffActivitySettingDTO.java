package com.kairos.user.staff.staff_settings;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Set;

public class StaffActivitySettingDTO {
    private BigInteger id;
    private Long staffId;
    private BigInteger activityId;
    private Long unitPositionId;
    private Long unitId;
    private int shortestTime;
    private int longestTime;
    private Integer minLength;
    private Integer maxThisActivityPerShift;
    private boolean eligibleForMove;
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private LocalTime maximumEndTime;


    public StaffActivitySettingDTO() {
        //Default Constructor
    }

    public StaffActivitySettingDTO(BigInteger activityId, Long unitPositionId, int shortestTime,
                                   int longestTime, Integer minLength, Integer maxThisActivityPerShift, boolean eligibleForMove) {
        this.activityId = activityId;
        this.unitPositionId = unitPositionId;
        this.shortestTime = shortestTime;
        this.longestTime = longestTime;
        this.minLength = minLength;
        this.maxThisActivityPerShift = maxThisActivityPerShift;
        this.eligibleForMove = eligibleForMove;
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

    public int getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(int shortestTime) {
        this.shortestTime = shortestTime;
    }

    public int getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(int longestTime) {
        this.longestTime = longestTime;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxThisActivityPerShift() {
        return maxThisActivityPerShift;
    }

    public void setMaxThisActivityPerShift(Integer maxThisActivityPerShift) {
        this.maxThisActivityPerShift = maxThisActivityPerShift;
    }

    public boolean isEligibleForMove() {
        return eligibleForMove;
    }

    public void setEligibleForMove(boolean eligibleForMove) {
        this.eligibleForMove = eligibleForMove;
    }

    public LocalTime getEarliestStartTime() {
        return earliestStartTime;
    }

    public void setEarliestStartTime(LocalTime earliestStartTime) {
        this.earliestStartTime = earliestStartTime;
    }

    public LocalTime getLatestStartTime() {
        return latestStartTime;
    }

    public void setLatestStartTime(LocalTime latestStartTime) {
        this.latestStartTime = latestStartTime;
    }

    public LocalTime getMaximumEndTime() {
        return maximumEndTime;
    }

    public void setMaximumEndTime(LocalTime maximumEndTime) {
        this.maximumEndTime = maximumEndTime;
    }
}
