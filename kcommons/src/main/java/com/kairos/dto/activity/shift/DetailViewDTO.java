package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.attendance.AttendanceTimeSlot;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 28/9/18
 */

public class DetailViewDTO {

    private ShiftDetailViewDTO shifts;
    List<AttendanceTimeSlot> attendanceTimeSlot;
    private List<ReasonCodeDTO> reasonCodes;


    public DetailViewDTO() {
    }

    public DetailViewDTO(ShiftDetailViewDTO shifts, List<ReasonCodeDTO> reasonCodes,List<AttendanceTimeSlot> attendanceTimeSlot) {
        this.shifts = shifts;
        this.reasonCodes = reasonCodes;
        this.attendanceTimeSlot=attendanceTimeSlot;
    }

    public ShiftDetailViewDTO getShifts() {
        return shifts;
    }

    public void setShifts(ShiftDetailViewDTO shifts) {
        this.shifts = shifts;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public List<AttendanceTimeSlot> getAttendanceTimeSlot() {
        return attendanceTimeSlot;
    }

    public void setAttendanceTimeSlot(List<AttendanceTimeSlot> attendanceTimeSlot) {
        this.attendanceTimeSlot = attendanceTimeSlot;
    }
}
