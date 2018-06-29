package com.planner.responseDto.skillDto;


import com.planner.responseDto.commonDto.BaseDTO;

public class OptaSkillDTO extends BaseDTO{
    private String name;
    private String skillLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

}
