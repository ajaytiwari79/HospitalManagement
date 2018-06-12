package com.kairos.activity.persistence.model.attendence_setting;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.staffing_level.Duration;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AttendanceSetting extends MongoBaseEntity{
    private Long staffId;
    private Long unitId;
    private LocalDate currentDate;
    private List<Duration> clockInClockOutDuration=new ArrayList<>();
    public Long getStaffId() {
        return staffId;
    }

    public AttendanceSetting(Long unitId, Long staffId,LocalDate currentDate) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.currentDate=currentDate;

    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public List<Duration> getClockInClockOutDuration() {
        return clockInClockOutDuration;
    }

    public void setClockInClockOutDuration(List<Duration> clockInClockOutDuration) {
        this.clockInClockOutDuration = clockInClockOutDuration;
    }
}
