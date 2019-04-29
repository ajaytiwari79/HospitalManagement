package com.kairos.dto.activity.widget;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.widget.WidgetFilterType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * pradeep
 * 28/4/19
 */
@Setter
@Getter
public class DashboardWidgetDTO {
    private TimeSlotDTO nightTimeSlot;
    private List<ShiftDTO> shifts;
    private Map<Long, StaffAdditionalInfoDTO> staffIdAndstaffInfoMap;
    private Set<BigInteger> timeTypeIds;
    private Set<WidgetFilterType> widgetFilterTypes;

    public DashboardWidgetDTO() {
    }

    public DashboardWidgetDTO(TimeSlotDTO nightTimeSlot, List<ShiftDTO> shifts, Map<Long, StaffAdditionalInfoDTO> staffIdAndstaffInfoMap) {
        this.nightTimeSlot = nightTimeSlot;
        this.shifts = shifts;
        this.staffIdAndstaffInfoMap = staffIdAndstaffInfoMap;
    }
}
