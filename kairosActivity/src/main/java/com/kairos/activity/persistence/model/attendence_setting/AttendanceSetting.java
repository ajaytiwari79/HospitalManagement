package com.kairos.activity.persistence.model.attendence_setting;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.staffing_level.Duration;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AttendanceSetting extends MongoBaseEntity{
    private Long userId;
    private Long unitId;
    private LocalDate currentDate;
    private List<Duration> attendanceDuration =new ArrayList<>();

    public AttendanceSetting() {
    }

    public AttendanceSetting(Long unitId, Long userId, LocalDate currentDate) {
        this.userId = userId;
        this.unitId = unitId;
        this.currentDate=currentDate;

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public List<Duration> getAttendanceDuration() {
        return attendanceDuration;
    }

    public void setAttendanceDuration(List<Duration> attendanceDuration) {
        this.attendanceDuration = attendanceDuration;
    }
}
