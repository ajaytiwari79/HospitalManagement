package com.kairos.activity.persistence.model.attendence_setting;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.response.dto.web.attendance.AttendanceDuration;


import java.time.LocalDate;


public class AttendanceSetting extends MongoBaseEntity{
    private Long staffId;
    private Long unitId;
    private AttendanceDuration attendanceDuration ;
    public AttendanceSetting() {
    }

    public AttendanceSetting(Long unitId, Long staffId) {
        this.staffId = staffId;
        this.unitId = unitId;


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


    public AttendanceDuration getAttendanceDuration() {
        return attendanceDuration;
    }

    public void setAttendanceDuration(AttendanceDuration attendanceDuration) {
        this.attendanceDuration = attendanceDuration;
    }
}
