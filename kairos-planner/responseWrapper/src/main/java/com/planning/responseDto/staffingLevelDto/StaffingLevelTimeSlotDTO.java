package com.planning.responseDto.staffingLevelDto;


import java.util.HashSet;
import java.util.Set;

public class StaffingLevelTimeSlotDTO {
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private Set<StaffingLevelActivityDTO> staffingLevelActivities=new HashSet<>();
    private Set<StaffingLevelSkillDTO> staffingLevelSkills =new HashSet<>();
    private StaffingLevelDurationDTO staffingLevelDuration;

    public StaffingLevelTimeSlotDTO() {
    }

    public StaffingLevelTimeSlotDTO(int sequence,int minNoOfStaff, int maxNoOfStaff,
                                    StaffingLevelDurationDTO staffingLevelDuration) {
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

    public Set<StaffingLevelActivityDTO> getStaffingLevelActivities() {
        return staffingLevelActivities;
    }

    public void setStaffingLevelActivities(Set<StaffingLevelActivityDTO> staffingLevelActivities) {
        this.staffingLevelActivities = staffingLevelActivities;
    }

    public int getAvailableNoOfStaff() {
        return availableNoOfStaff;
    }

    public void setAvailableNoOfStaff(Integer availableNoOfStaff) {
        this.availableNoOfStaff = availableNoOfStaff;
    }

    public Set<StaffingLevelSkillDTO> getStaffingLevelSkills() {
        return staffingLevelSkills;
    }

    public void setStaffingLevelSkills(Set<StaffingLevelSkillDTO> staffingLevelSkills) {
        this.staffingLevelSkills = staffingLevelSkills;
    }

    public StaffingLevelDurationDTO getStaffingLevelDuration() {
        return staffingLevelDuration;
    }

    public void setStaffingLevelDuration(StaffingLevelDurationDTO staffingLevelDuration) {
        this.staffingLevelDuration = staffingLevelDuration;
    }
}
