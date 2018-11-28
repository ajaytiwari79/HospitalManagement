package com.kairos.persistence.model.attendence_setting;


import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.dto.activity.attendance.AttendanceTimeSlot;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document
public class TimeAndAttendance extends MongoBaseEntity {
    private Long userId;
    private Long staffId;
    private LocalDate date;
    private List<AttendanceTimeSlot> attendanceTimeSlot;
    public TimeAndAttendance() {
    }

    public TimeAndAttendance(Long staffId, Long userId, List<AttendanceTimeSlot> attendanceTimeSlot,LocalDate date) {
        this.staffId =staffId;
        this.userId=userId;
        this.attendanceTimeSlot = attendanceTimeSlot;
        this.date=date;
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

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
