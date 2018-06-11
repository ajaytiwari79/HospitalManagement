package com.kairos.response.dto.web.clock_setting;

import com.kairos.activity.persistence.model.staffing_level.Duration;

import java.time.LocalDate;


public class AttendanceSettingDTO {

    private Long staffId;
    private Long unitId;
    private LocalDate currentDate;
    private Duration clockINclockOutDuration;

    public AttendanceSettingDTO(Long staffId, Long unitId, LocalDate currentDate) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.currentDate = currentDate;
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

    public Duration getClockINclockOutDuration() {
        return clockINclockOutDuration;
    }

    public void setClockINclockOutDuration(Duration clockINclockOutDuration) {
        this.clockINclockOutDuration = clockINclockOutDuration;
    }
}
