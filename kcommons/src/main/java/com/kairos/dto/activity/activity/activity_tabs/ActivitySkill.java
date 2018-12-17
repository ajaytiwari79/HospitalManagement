package com.kairos.dto.activity.activity.activity_tabs;

import java.io.Serializable;

/**
 * Created by pawanmandhan on 28/8/17.
 */
public class ActivitySkill implements Serializable {
    private  String name;
    private String level;
    private Long skillId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public ActivitySkill(){

    }


    public ActivitySkill(String name, String level, Long skillId) {
        this.name = name;
        this.level = level;
        this.skillId = skillId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
