package com.kairos.client.dto;


/**
 * Skill Level Domain
 */

public class SkillLevel {
    private Long id;

    private String name;

    public SkillLevel() {
    }

    public SkillLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
