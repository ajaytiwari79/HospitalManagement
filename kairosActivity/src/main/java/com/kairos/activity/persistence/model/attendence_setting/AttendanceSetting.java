package com.kairos.activity.persistence.model.attendence_setting;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.persistence.model.staffing_level.Duration;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AttendanceSetting extends MongoBaseEntity{
    private Long userId;
    private LocalDate currentDate;
    private List<Duration> attendanceDuration =new ArrayList<>();

    public AttendanceSetting() {
    }

    public AttendanceSetting(Long userId, LocalDate currentDate) {
        this.userId = userId;
       this.currentDate=currentDate;

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
