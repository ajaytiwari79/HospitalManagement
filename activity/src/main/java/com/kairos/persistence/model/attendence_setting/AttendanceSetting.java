package com.kairos.persistence.model.attendence_setting;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.response.dto.web.attendance.AttendanceDuration;


import java.time.LocalDate;


public class AttendanceSetting extends MongoBaseEntity{
    private Long staffId;
    private Long unitId;
    private Long userId;
    private AttendanceDuration attendanceDuration ;
    public AttendanceSetting() {
    }

    public AttendanceSetting(Long unitId, Long staffId,Long userId,AttendanceDuration attendanceDuration) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId=userId;
        this.attendanceDuration=attendanceDuration;
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
