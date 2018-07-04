package com.kairos.persistence.model.shift;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Set;

public class IndividualShiftTemplate extends MongoBaseEntity {
    private String name;
    private String remarks;
    private BigInteger activityId;
    private Long unitId;
    private Long createdBy;// using userId here
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isMainShift;
    private Set<BigInteger> subShiftIds;

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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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
        return isMainShift;
    }

    public void setMainShift(boolean mainShift) {
        isMainShift = mainShift;
    }

    public Set<BigInteger> getSubShiftIds() {
        return subShiftIds;
    }

    public void setSubShiftIds(Set<BigInteger> subShiftIds) {
        this.subShiftIds = subShiftIds;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
