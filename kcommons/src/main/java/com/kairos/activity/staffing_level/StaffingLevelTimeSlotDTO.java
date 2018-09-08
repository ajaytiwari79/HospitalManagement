package com.kairos.activity.staffing_level;

import java.util.HashSet;
import java.util.Set;
@Deprecated
/**
 * Instead use {@link StaffingLevelInterval}
 */
public class StaffingLevelTimeSlotDTO {
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private Set<StaffingLevelActivity> staffingLevelActivities=new HashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();
    private Duration staffingLevelDuration;

    public StaffingLevelTimeSlotDTO() {
        //default constrictor
    }

    public StaffingLevelTimeSlotDTO(int sequence,int minNoOfStaff, int maxNoOfStaff,
                                    Duration staffingLevelDuration) {
        this.sequence=sequence;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDuration = staffingLevelDuration;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getMinNoOfStaff() {
        return minNoOfStaff;
    }

    public void setMinNoOfStaff(Integer minNoOfStaff) {
        this.minNoOfStaff = minNoOfStaff;
    }

    public int getMaxNoOfStaff() {
        return maxNoOfStaff;
    }

    public void setMaxNoOfStaff(Integer maxNoOfStaff) {
        this.maxNoOfStaff = maxNoOfStaff;
    }

    public Set<StaffingLevelActivity> getStaffingLevelActivities() {
        return staffingLevelActivities;
    }

    public void setStaffingLevelActivities(Set<StaffingLevelActivity> staffingLevelActivities) {
        this.staffingLevelActivities = staffingLevelActivities;
    }

    public int getAvailableNoOfStaff() {
        return availableNoOfStaff;
    }

    public void setAvailableNoOfStaff(Integer availableNoOfStaff) {
        this.availableNoOfStaff = availableNoOfStaff;
    }

    public Set<StaffingLevelSkill> getStaffingLevelSkills() {
        return staffingLevelSkills;
    }

    public void setStaffingLevelSkills(Set<StaffingLevelSkill> staffingLevelSkills) {
        this.staffingLevelSkills = staffingLevelSkills;
    }

    public Duration getStaffingLevelDuration() {
        return staffingLevelDuration;
    }

    public void setStaffingLevelDuration(Duration staffingLevelDuration) {
        this.staffingLevelDuration = staffingLevelDuration;
    }
}
