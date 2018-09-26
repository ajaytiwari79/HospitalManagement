package com.kairos.persistence.model.attendence_setting;


import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.dto.activity.attendance.AttendanceDuration;


public class AttendanceSetting extends MongoBaseEntity {
    private Long staffId;
    private Long unitId;
    private Long userId;
    private Long reasonCodeId;
    private AttendanceDuration attendanceDuration ;
    public AttendanceSetting() {
    }

    public AttendanceSetting(Long unitId, Long staffId,Long userId,AttendanceDuration attendanceDuration) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId=userId;
        this.attendanceDuration=attendanceDuration;
    }
    public AttendanceSetting(Long unitId, Long staffId,Long userId,Long reasonCodeId,AttendanceDuration attendanceDuration) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId=userId;
        this.reasonCodeId=reasonCodeId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AttendanceDuration getAttendanceDuration() {
        return attendanceDuration;
    }

    public void setAttendanceDuration(AttendanceDuration attendanceDuration) {
        this.attendanceDuration = attendanceDuration;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }
}
