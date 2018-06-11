package com.kairos.activity.persistence.model.clock_setting;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDate;
import java.time.LocalTime;


public class AttendanceSetting extends MongoBaseEntity{
    private Long staffId;
    private Long unitId;
    private LocalDate currentDate;
    private Duration clockInClockOutDuration;
    public Long getStaffId() {
        return staffId;
    }

    public AttendanceSetting(Long unitId, Long staffId,LocalDate currentDate,Duration clockInClockOutDuration) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.currentDate=currentDate;
        this.clockInClockOutDuration=clockInClockOutDuration;
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

    public Duration getClockInClockOutDuration() {
        return clockInClockOutDuration;
    }

    public void setClockInClockOutDuration(Duration clockInClockOutDuration) {
        this.clockInClockOutDuration = clockInClockOutDuration;
    }
}
