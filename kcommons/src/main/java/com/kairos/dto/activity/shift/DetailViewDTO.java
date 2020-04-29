package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.attendance.AttendanceTimeSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 28/9/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailViewDTO {
    private Long staffId;
    private ShiftDetailViewDTO shifts;
    private List<AttendanceTimeSlotDTO> attendanceTimeSlot;
}
