package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelActivityWithDuration {

    private String name;
    private BigInteger activityId;
    private boolean includeInMin;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int underStaffingOverStaffingCount;
    private Duration staffingLevelDuration;


    public StaffingLevelActivityWithDuration(BigInteger activityId, int minNoOfStaff, int maxNoOfStaff, Duration staffingLevelDuration) {
        this.activityId = activityId;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }
    public StaffingLevelActivityWithDuration(StaffingLevelActivityWithDuration staffingLevelActivityWithDuration) {
        this.activityId = staffingLevelActivityWithDuration.getActivityId();
        this.minNoOfStaff = staffingLevelActivityWithDuration.getMinNoOfStaff();
        this.maxNoOfStaff = staffingLevelActivityWithDuration.maxNoOfStaff;
        this.underStaffingOverStaffingCount = staffingLevelActivityWithDuration.underStaffingOverStaffingCount;
        this.name = staffingLevelActivityWithDuration.getName();
        this.staffingLevelDuration = new Duration(staffingLevelActivityWithDuration.getStaffingLevelDuration().getFrom(),null) ;
    }
}
