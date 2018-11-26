package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.attendance.AttendanceTimeSlot;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 28/9/18
 */

public class DetailViewDTO {
    private Long staffId;
    private ShiftDetailViewDTO shifts;
    List<AttendanceTimeSlot> attendanceTimeSlot;
    public DetailViewDTO() {
    }

    public DetailViewDTO(Long staffId,ShiftDetailViewDTO shifts,List<AttendanceTimeSlot> attendanceTimeSlot) {
        this.staffId=staffId;
        this.shifts = shifts;
        this.attendanceTimeSlot=attendanceTimeSlot;
    }

    public ShiftDetailViewDTO getShifts() {
        return shifts;
    }

    public void setShifts(ShiftDetailViewDTO shifts) {
        this.shifts = shifts;
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
}
