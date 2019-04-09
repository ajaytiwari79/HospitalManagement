package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.skill.Skill;

import java.util.Set;

public class StaffingLevelActivityType {
    private String activityTypeId;
    private Set<Skill> skillSet;
    private int minimumStaffRequired;
    private int maximumStaffRequired;
    public StaffingLevelActivityType( Set<Skill> skillSet,int minimumStaffRequired, int maximumStaffRequired,String activityTypeId) {
        this.minimumStaffRequired = minimumStaffRequired;
        this.skillSet = skillSet;
        this.maximumStaffRequired = maximumStaffRequired;
        this.activityTypeId=activityTypeId;
    }
    public StaffingLevelActivityType(){}
    public Set<Skill> getSkillSet() {
        return skillSet;
    }
    public void setSkillSet(Set<Skill> skillSet) {
        this.skillSet = skillSet;
    }

    public int getMinimumStaffRequired() {
        return minimumStaffRequired;
    }

    public void setMinimumStaffRequired(int minimumStaffRequired) {
        this.minimumStaffRequired = minimumStaffRequired;
    }

    public int getMaximumStaffRequired() {
        return maximumStaffRequired;
    }

    public void setMaximumStaffRequired(int maximumStaffRequired) {
        this.maximumStaffRequired = maximumStaffRequired;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(String activityTypeId) {
        this.activityTypeId = activityTypeId;
    }
}
