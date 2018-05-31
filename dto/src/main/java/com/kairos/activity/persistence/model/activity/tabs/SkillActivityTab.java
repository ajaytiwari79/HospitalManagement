package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 25/8/17.
 */
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
