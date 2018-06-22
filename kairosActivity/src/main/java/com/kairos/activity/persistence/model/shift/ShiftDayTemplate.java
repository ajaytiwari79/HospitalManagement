package com.kairos.activity.persistence.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Set;

public class ShiftDayTemplate extends MongoBaseEntity {
    private String name;
    private String remarks;
    private BigInteger activityId;
    private Long unitId;
    private Long createdBy;// using userId here
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isMainShift = true;
    private Set<BigInteger> subShifts;

    public ShiftDayTemplate() {
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

    public Set<BigInteger> getSubShifts() {
        return subShifts;
    }

    public void setSubShifts(Set<BigInteger> subShifts) {
        this.subShifts = subShifts;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
