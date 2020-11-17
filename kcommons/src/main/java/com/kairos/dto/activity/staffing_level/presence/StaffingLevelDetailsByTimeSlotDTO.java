package com.kairos.dto.activity.staffing_level.presence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffingLevelDetailsByTimeSlotDTO {
    private int totalUnderStaffing;
    private int totalOverStaffing;
    private int totalUnderStaffingMinutes;
    private int totalOverStaffingMinutes;
    private String timeSlot;
    int totalMinNoOfStaff;
    int totalMaxNoOfStaff;
    int totalMinimumMinutes;
    int totalMaximumMinutes;

}
