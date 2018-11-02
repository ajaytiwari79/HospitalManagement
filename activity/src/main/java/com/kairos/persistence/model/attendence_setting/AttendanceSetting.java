package com.kairos.persistence.model.attendence_setting;


import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.dto.activity.attendance.AttendanceDuration;

import java.math.BigInteger;
import java.util.List;


public class AttendanceSetting extends MongoBaseEntity {
    private Long staffId;
    private Long unitId;
    private Long userId;
    private BigInteger shiftId;
    private Long reasonCodeId;
    private List<AttendanceDuration> attendanceDuration ;
    public AttendanceSetting() {
    }

    public AttendanceSetting(Long unitId, Long staffId,Long userId,Long reasonCodeId,List<AttendanceDuration> attendanceDuration) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId=userId;
        this.reasonCodeId=reasonCodeId;
        this.attendanceDuration=attendanceDuration;
    }

    public AttendanceSetting(BigInteger shiftId,Long unitId, Long staffId, Long userId, Long reasonCodeId, List<AttendanceDuration> attendanceDuration) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId = userId;
        this.shiftId = shiftId;
        this.reasonCodeId = reasonCodeId;
        this.attendanceDuration = attendanceDuration;
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

    public List<AttendanceDuration> getAttendanceDuration() {
        return attendanceDuration;
    }

    public void setAttendanceDuration(List<AttendanceDuration> attendanceDuration) {
        this.attendanceDuration = attendanceDuration;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }


}
