package com.kairos.dto.activity.staffing_level;

import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDetailsByTimeSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyStaffingLevelDetailsDTO {

    private int totalUnderStaffing;
    private int totalOverStaffing;
    private int totalUnderStaffingMinutes;
    private int totalOverStaffingMinutes;
    private List<StaffingLevelDetailsByTimeSlotDTO> timeSlotsWiseData;
    int totalMinNoOfStaff;
    int totalMaxNoOfStaff;
    int totalMinimumMinutes;
    int totalMaximumMinutes;

}
