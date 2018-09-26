package com.kairos.persistence.model.shift;

import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.ShiftTemplateActivity;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class IndividualShiftTemplate extends MongoBaseEntity {
    private String name;
    private String remarks;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<ShiftTemplateActivity> activities;
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

    public List<ShiftTemplateActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftTemplateActivity> activities) {
        this.activities = activities;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
