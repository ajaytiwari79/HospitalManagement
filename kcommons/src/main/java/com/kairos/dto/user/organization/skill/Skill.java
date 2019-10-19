package com.kairos.dto.user.organization.skill;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * Skill Domain extends UserBaseEntity
 * SKill has relationship with com.kairos.enums.SkillLevel Domain
 */
public class Skill {

    private Long id;
    @NotBlank(message = "error.Skill.name.notEmpty")
    private String name;
    private String description;
    private boolean isEnabled = true;
    private String shortName;
    private SkillStatus skillStatus;
    private SkillCategory skillCategory;

    public Skill() {
    }

    public Skill(String name, SkillCategory skillCategory) {
        this.name = name;
        this.skillCategory = skillCategory;
    }


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public SkillCategory getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String,Object> data = new HashMap<>();
        data.put("name",this.name);
        data.put("description",this.description);
        data.put("shortName",this.shortName);
        return data;
    }

    public enum SkillLevel {

        BASIC("Basic"), ADVANCE("Advance"), EXPERT("Expert");
        public String value;

        SkillLevel(String value) {
            this.value = value;
        }

        public static SkillLevel getByValue(String value) {
            for (SkillLevel skillLevel : SkillLevel.values()) {
                if (skillLevel.value.equals(value)) {
                    return skillLevel;
                }
            }
            return null;
        }
    }

    public SkillStatus getSkillStatus() {
        return skillStatus;
    }

    public void setSkillStatus(SkillStatus skillStatus) {
        this.skillStatus = skillStatus;
    }

    public enum SkillStatus {
        PENDING, APPROVED, REJECTED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
