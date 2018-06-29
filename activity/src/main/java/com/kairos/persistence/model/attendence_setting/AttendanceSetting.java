package com.kairos.persistence.model.attendence_setting;

import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.staffing_level.Duration;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AttendanceSetting extends MongoBaseEntity{
    private Long staffId;
    private Long unitId;
    private LocalDate currentDate;
    private List<Duration> attendanceDuration =new ArrayList<>();

    public AttendanceSetting() {
    }

    public AttendanceSetting(Long unitId, Long staffId, LocalDate currentDate) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.currentDate=currentDate;

    }
    public Long getStaffId() {
        return staffId;
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

    public List<Duration> getAttendanceDuration() {
        return attendanceDuration;
    }

    public void setAttendanceDuration(List<Duration> attendanceDuration) {
        this.attendanceDuration = attendanceDuration;
    }
}
