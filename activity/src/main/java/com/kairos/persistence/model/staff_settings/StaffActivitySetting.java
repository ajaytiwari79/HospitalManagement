package com.kairos.persistence.model.staff_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;

public class StaffActivitySetting extends MongoBaseEntity {
    private Long staffId;
    private BigInteger activityId;
    private Long unitPositionId;
    private Long unitId;
    private Short shortestTime;
    private Short longestTime;
    private Integer minLength;
    private Integer maxThisActivityPerShift;
    private boolean eligibleForMove;
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private LocalTime maximumEndTime;
    private List<Long> dayTypeIds;

    public StaffActivitySetting() {
        //Default Constructor
    }

    public StaffActivitySetting(Long staffId, BigInteger activityId, Long unitPositionId, Long unitId,
                                Short shortestTime, Short longestTime, Integer minLength, Integer maxThisActivityPerShift,
                                boolean eligibleForMove,LocalTime earliestStartTime,LocalTime latestStartTime,LocalTime maximumEndTime,List<Long> dayTypeIds) {
        this.staffId = staffId;
        this.activityId = activityId;
        this.unitPositionId = unitPositionId;
        this.unitId = unitId;
        this.shortestTime = shortestTime;
        this.longestTime = longestTime;
        this.minLength = minLength;
        this.maxThisActivityPerShift = maxThisActivityPerShift;
        this.eligibleForMove = eligibleForMove;
        this.earliestStartTime=earliestStartTime;
        this.latestStartTime=latestStartTime;
        this.maximumEndTime=maximumEndTime;
        this.dayTypeIds=dayTypeIds;
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

    public Short getShortestTime() {
        return shortestTime;
    }

    public void setShortestTime(Short shortestTime) {
        this.shortestTime = shortestTime;
    }

    public Short getLongestTime() {
        return longestTime;
    }

    public void setLongestTime(Short longestTime) {
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

    public List<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = dayTypeIds;
    }
}
