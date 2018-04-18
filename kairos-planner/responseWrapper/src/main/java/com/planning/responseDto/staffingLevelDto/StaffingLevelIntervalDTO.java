package com.planning.responseDto.staffingLevelDto;


import java.util.HashSet;
import java.util.Set;

public class StaffingLevelIntervalDTO {
    private int sequence;
    private int minNoOfStaff;
    private int maxNoOfStaff;
    private int availableNoOfStaff;
    private StaffingLevelDurationDTO staffingLevelDurationDTO;
    private Set<StaffingLevelActivityDTO> staffingLevelActivities=new HashSet<>();
    private Set<StaffingLevelSkillDTO> staffingLevelSkillDTOS =new HashSet<>();

    public StaffingLevelIntervalDTO() {
        // default constractor
    }

    public StaffingLevelIntervalDTO(int sequence, int minNoOfStaff, int maxNoOfStaff,
                                    StaffingLevelDurationDTO staffingLevelDurationDTO) {
        this.sequence=sequence;
        this.minNoOfStaff = minNoOfStaff;
        this.maxNoOfStaff = maxNoOfStaff;
        this.staffingLevelDurationDTO = staffingLevelDurationDTO;
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

    public int getAvailableNoOfStaff() {
        return availableNoOfStaff;
    }

    public void setAvailableNoOfStaff(int availableNoOfStaff) {
        this.availableNoOfStaff = availableNoOfStaff;
    }

    public StaffingLevelDurationDTO getStaffingLevelDurationDTO() {
        return staffingLevelDurationDTO;
    }

    public void setStaffingLevelDurationDTO(StaffingLevelDurationDTO staffingLevelDurationDTO) {
        this.staffingLevelDurationDTO = staffingLevelDurationDTO;
    }

    public Set<StaffingLevelActivityDTO> getStaffingLevelActivities() {
        return staffingLevelActivities;
    }

    public void setStaffingLevelActivities(Set<StaffingLevelActivityDTO> staffingLevelActivities) {
        this.staffingLevelActivities = staffingLevelActivities;
    }

    public Set<StaffingLevelSkillDTO> getStaffingLevelSkillDTOS() {
        return staffingLevelSkillDTOS;
    }

    public void setStaffingLevelSkillDTOS(Set<StaffingLevelSkillDTO> staffingLevelSkillDTOS) {
        this.staffingLevelSkillDTOS = staffingLevelSkillDTOS;
    }

    public void addStaffLevelActivity(StaffingLevelActivityDTO staffLevelActivity) {
        if (staffLevelActivity == null)
            throw new NullPointerException("Can't add null staffLevelActivity");

        this.getStaffingLevelActivities().add(staffLevelActivity);

    }

    public void addStaffLevelActivity(Set<StaffingLevelActivityDTO> staffLevelActivitys) {
        if (staffLevelActivitys == null)
            throw new NullPointerException("Can't add null staffLevelActivity");

        this.getStaffingLevelActivities().addAll(staffLevelActivitys);

    }


    public void addStaffLevelSkill(StaffingLevelSkillDTO staffLevelSkill) {

        if (staffLevelSkill == null)
            throw new NullPointerException("Can't add null staffLevelActivity");
        this.getStaffingLevelSkillDTOS().add(staffLevelSkill);

    }

    public void addStaffLevelSkill(Set<StaffingLevelSkillDTO> staffLevelSkills) {

        if (staffLevelSkills == null)
            throw new NullPointerException("Can't add null staffLevelActivity");
        this.getStaffingLevelSkillDTOS().addAll(staffLevelSkills);

    }


}
