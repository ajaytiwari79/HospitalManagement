package com.kairos.activity.response.dto.staffing_level;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevelActivity;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSkill;

import java.util.HashSet;
import java.util.Set;

public class StaffingLevelTimeSlotDTO {
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private Set<StaffingLevelActivity> staffingLevelActivities=new HashSet<>();
    private Set<StaffingLevelSkill> staffingLevelSkills=new HashSet<>();
    private StaffingLevelDuration staffingLevelDuration;

    public StaffingLevelTimeSlotDTO() {
        //default constrictor
    }

    public StaffingLevelTimeSlotDTO(int sequence,int minNoOfStaff, int maxNoOfStaff,
                                    StaffingLevelDuration staffingLevelDuration) {
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

    public StaffingLevelDuration getStaffingLevelDuration() {
        return staffingLevelDuration;
    }

    public void setStaffingLevelDuration(StaffingLevelDuration staffingLevelDuration) {
        this.staffingLevelDuration = staffingLevelDuration;
    }
}
