package com.planning.responseDto.staffingLevelDto;



public class StaffingLevelSkillDTO {
    private Long skillId;
    private int noOfStaff;

    public StaffingLevelSkillDTO() {
        //default constructor
    }

    public StaffingLevelSkillDTO(Long skillId, int noOfStaff) {
        this.skillId = skillId;
        this.noOfStaff = noOfStaff;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public int getNoOfStaff() {
        return noOfStaff;
    }

    public void setNoOfStaff(int noOfStaff) {
        this.noOfStaff = noOfStaff;
    }

    @Override
    public String toString() {
        return "StaffingLevelSkillDTO{" +
                "skillId=" + skillId +
                ", noOfStaff=" + noOfStaff +
                '}';
    }
}
