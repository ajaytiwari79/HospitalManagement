package com.kairos.dto.activity.staffing_level;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class StaffingLevelSkill {
    private Long skillId;
    private int noOfStaff;

    public StaffingLevelSkill() {
        //default constructor
    }

    public StaffingLevelSkill(Long skillId, int noOfStaff) {
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
        return new ToStringBuilder(this)
                .append("skillId", skillId)
                .append("noOfStaff", noOfStaff)
                .toString();
    }
}
