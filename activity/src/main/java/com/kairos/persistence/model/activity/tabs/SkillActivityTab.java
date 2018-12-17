package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.activity.activity_tabs.ActivitySkill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SkillActivityTab implements Serializable {

    private List<ActivitySkill> activitySkills = new ArrayList<>();

    public SkillActivityTab() {
    }

    public SkillActivityTab(List<ActivitySkill> activitySkills) {
        this.activitySkills = activitySkills;
    }

    public List<ActivitySkill> getActivitySkills() {
        return activitySkills;
    }

    public void setActivitySkills(List<ActivitySkill> activitySkills) {
        this.activitySkills = activitySkills;
    }
    public List<Long> getActivitySkillIds() {
        List<Long> skillIds=new ArrayList<>();
        for (ActivitySkill activitySkill:activitySkills){
            skillIds.add(activitySkill.getSkillId());
        }
        return skillIds;
    }
}