package com.kairos.dto.user.skill;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Skill Domain extends UserBaseEntity
 * SKill has relationship with SkillLevel Domain
 */
public class Skill {
    @NotEmpty(message = "error.Skill.name.notEmpty") @NotNull(message = "error.Skill.name.notnull")
    private String name;

    //@NotEmpty(message = "error.Skill.description.notEmpty") @NotNull(message = "error.Skill.description.notnull")
    private String description;

    private boolean isEnabled = true;

    private String shortName;

    private SkillStatus skillStatus;
    private SkillCategory skillCategory;
    /*User requestedBy;

    User approvedBy;*/

    public Skill(String name, SkillCategory skillCategory) {
        this.name = name;
        this.skillCategory = skillCategory;
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

    public enum SkillStatus {
        PENDING, APPROVED, REJECTED;
    }
}
