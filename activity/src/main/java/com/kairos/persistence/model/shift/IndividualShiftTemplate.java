package com.kairos.persistence.model.shift;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Set;

public class IndividualShiftTemplate extends MongoBaseEntity {
    private String name;
    private String remarks;
    private BigInteger activityId;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean mainShift;
    private Set<BigInteger> subShiftIds;
    private int durationMinutes;

    public IndividualShiftTemplate() {
        //Default Constructor
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isMainShift() {
        return mainShift;
    }

    public void setMainShift(boolean mainShift) {
        this.mainShift = mainShift;
    }

    public Set<BigInteger> getSubShiftIds() {
        return subShiftIds;
    }

    public void setSubShiftIds(Set<BigInteger> subShiftIds) {
        this.subShiftIds = subShiftIds;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
