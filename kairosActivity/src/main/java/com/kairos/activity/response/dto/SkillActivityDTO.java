package com.kairos.activity.response.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.activity.tabs.ActivitySkill;
import com.kairos.activity.persistence.model.activity.tabs.SkillActivityTab;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 25/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class SkillActivityDTO {
    private BigInteger activityId;
    private List<ActivitySkill> activitySkills;

    public SkillActivityTab buildSkillActivityTab(){
        SkillActivityTab skillActivityTab =new SkillActivityTab(activitySkills);
        return skillActivityTab;
    }
    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public List<ActivitySkill> getActivitySkills() {
        return activitySkills;
    }

    public void setActivitySkills(List<ActivitySkill> activitySkills) {
        this.activitySkills = activitySkills;
    }
}
