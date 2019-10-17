package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
@Deprecated
/**
 * Instead use {@link StaffingLevelInterval}
 */
@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelTimeSlotDTO {
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private Set<StaffingLevelActivity> staffingLevelActivities=new LinkedHashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();
    private Duration staffingLevelDuration;

    public StaffingLevelTimeSlotDTO(int sequence,int minNoOfStaff, int maxNoOfStaff,
                                    Duration staffingLevelDuration) {
        this.sequence=sequence;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }

}
