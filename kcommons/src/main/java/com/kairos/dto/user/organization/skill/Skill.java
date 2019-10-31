package com.kairos.dto.user.organization.skill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * Skill Domain extends UserBaseEntity
 * SKill has relationship with com.kairos.enums.SkillLevel Domain
 */
@Getter
@Setter
@NoArgsConstructor
public class Skill {

    private Long id;
    @NotBlank(message = "error.Skill.name.notEmpty")
    private String name;
    private String description;
    private boolean isEnabled = true;
    private String shortName;
    private SkillStatus skillStatus;
    private SkillCategory skillCategory;


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
