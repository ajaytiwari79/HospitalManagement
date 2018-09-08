package com.kairos.client.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by prabjot on 24/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTypeSkill {

    private long skillId;
    private Skill.SkillLevel skillLevel;

    public long getSkillId() {
        return skillId;
    }

    public Skill.SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillId(long skillId) {
        this.skillId = skillId;
    }

    public void setSkillLevel(Skill.SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }
}
