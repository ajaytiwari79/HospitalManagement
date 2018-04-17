package com.kairos.shiftplanning.domain;

public class StaffingLevelSkill {
    private Skill skill;
    private int staffRequired;

    public StaffingLevelSkill(Skill skill, int staffRequired) {
        this.skill = skill;
        this.staffRequired = staffRequired;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public int getStaffRequired() {
        return staffRequired;
    }

    public void setStaffRequired(int staffRequired) {
        this.staffRequired = staffRequired;
    }
}
