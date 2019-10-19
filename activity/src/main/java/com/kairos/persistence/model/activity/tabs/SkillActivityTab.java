package com.kairos.persistence.model.activity.tabs;

import com.kairos.dto.activity.activity.activity_tabs.ActivitySkill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillActivityTab implements Serializable {

    private List<ActivitySkill> activitySkills = new ArrayList<>();

    public List<Long> getActivitySkillIds() {
        List<Long> skillIds=new ArrayList<>();
        for (ActivitySkill activitySkill:activitySkills){
            skillIds.add(activitySkill.getSkillId());
        }
        return skillIds;
    }
}