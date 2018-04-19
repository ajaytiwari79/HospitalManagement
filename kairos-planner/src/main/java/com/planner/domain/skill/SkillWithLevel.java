package com.planner.domain.skill;

import com.planner.domain.common.BaseEntity;
import com.planner.enums.SkillLevel;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class SkillWithLevel extends BaseEntity {

    private String skillId;
    private SkillLevel skillLevel;

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }
}
