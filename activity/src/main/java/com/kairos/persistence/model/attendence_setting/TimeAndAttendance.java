package com.kairos.persistence.model.attendence_setting;


import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.dto.activity.attendance.AttendanceTimeSlot;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Document
public class TimeAndAttendance extends MongoBaseEntity {
    private Long userId;
    private Long staffid;
    private LocalDate date;
    private List<AttendanceTimeSlot> attendanceTimeSlot;
    public TimeAndAttendance() {
    }

    public TimeAndAttendance(Long staffId, Long userId, List<AttendanceTimeSlot> attendanceTimeSlot) {
        this.staffid=staffId;
        this.userId=userId;
        this.attendanceTimeSlot = attendanceTimeSlot;
    }

    public TimeAndAttendance(Long userId, List<AttendanceTimeSlot> attendanceTimeSlot) {
        this.userId = userId;
        this.attendanceTimeSlot = attendanceTimeSlot;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<AttendanceTimeSlot> getAttendanceTimeSlot() {
        return attendanceTimeSlot;
    }

    public void setAttendanceTimeSlot(List<AttendanceTimeSlot> attendanceTimeSlot) {
        this.attendanceTimeSlot = attendanceTimeSlot;
    }

    public Long getStaffid() {
        return staffid;
    }

    public void setStaffid(Long staffid) {
        this.staffid = staffid;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
