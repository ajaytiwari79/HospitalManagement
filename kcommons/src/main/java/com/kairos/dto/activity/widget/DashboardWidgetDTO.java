package com.kairos.dto.activity.widget;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.widget.WidgetFilterType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

/**
 * pradeep
 * 28/4/19
 */
@Setter
@Getter
public class DashboardWidgetDTO {
    private TimeSlotDTO nightTimeSlot;
    private List<ShiftWithActivityDTO> shifts;
    private Map<Long, StaffAdditionalInfoDTO> staffIdAndstaffInfoMap;
    private int realtimeDurationInMinutes;
    private Set<BigInteger> timeTypeIds;
    private Set<WidgetFilterType> widgetFilterTypes;
    private List<TimeTypeDTO> timeTypes;

    public DashboardWidgetDTO() {
    }

    public DashboardWidgetDTO(TimeSlotDTO nightTimeSlot, List<ShiftWithActivityDTO> shifts, Map<Long, StaffAdditionalInfoDTO> staffIdAndstaffInfoMap,int realtimeDurationInMinutes,List<TimeTypeDTO> timeTypes) {
        this.nightTimeSlot = nightTimeSlot;
        this.shifts = shifts;
        this.staffIdAndstaffInfoMap = staffIdAndstaffInfoMap;
        this.realtimeDurationInMinutes = realtimeDurationInMinutes;
        this.timeTypes = timeTypes;
        this.widgetFilterTypes = new HashSet<>();
        this.timeTypeIds = new HashSet<>();
    }
}
