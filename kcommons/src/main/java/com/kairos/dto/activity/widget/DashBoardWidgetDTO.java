package com.kairos.dto.activity.widget;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * pradeep
 * 28/4/19
 */
@Setter
@Getter
public class DashBoardWidgetDTO {
    private TimeSlotDTO nightTimeSlot;
    private List<ShiftDTO> shifts;
    private Map<Long, StaffAdditionalInfoDTO> staffIdAndstaffInfoMap;


    public DashBoardWidgetDTO(TimeSlotDTO nightTimeSlot, List<ShiftDTO> shifts, Map<Long, StaffAdditionalInfoDTO> staffIdAndstaffInfoMap) {
        this.nightTimeSlot = nightTimeSlot;
        this.shifts = shifts;
        this.staffIdAndstaffInfoMap = staffIdAndstaffInfoMap;
    }
}
