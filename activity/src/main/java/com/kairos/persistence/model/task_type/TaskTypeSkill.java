package com.kairos.persistence.model.task_type;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.skill.Skill;


/**
 * Created by prabjot on 24/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTypeSkill implements Cloneable{
    private long skillId;
    private Skill.SkillLevel skillLevel;
    private long visitourId;
    private String name;

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


    public long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(long visitourId) {
        this.visitourId = visitourId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected TaskTypeSkill clone() throws CloneNotSupportedException {
        return (TaskTypeSkill) super.clone();
    }

}
